import json
import sys
import shutil
import subprocess
from pathlib import Path

import numpy as np

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "art/sounds/source"
ASSETS = ROOT / "src/main/resources/assets/simplywings"
OUT = ASSETS / "sounds"
FFMPEG = shutil.which("ffmpeg") or r"C:\Users\adamg\AppData\Local\Programs\Python\Python312\Scripts\ffmpeg"
SR = 44100

CATALOG = ROOT / "tools/sounds/catalog.json"
LOUDNESS = ROOT / "tools/sounds/loudness.json"
CEILING_DB = -1.5


def saw(f, t, top=3500):
    return sum(np.sin(2 * np.pi * k * f * t) / k for k in range(1, int(top / f) + 1))


def spectral(x, cutoff, bump=0.0, bump_at=700.0):
    spectrum = np.fft.rfft(x)
    f = np.fft.rfftfreq(len(x), 1 / SR)
    curve = 1 / (1 + (f / cutoff) ** 4) * (1 + bump * np.exp(-((f - bump_at) / (bump_at * 0.35)) ** 2))
    curve[f < 35] = 0
    return np.fft.irfft(spectrum * curve, len(x)).astype(np.float32)


def synth(name):
    rng = np.random.default_rng(7)
    if name == "buzz_loop":
        t = np.arange(2 * SR) / SR
        tone = saw(92.0, t) + 0.8 * saw(92.5, t) + 0.5 * np.sign(np.sin(2 * np.pi * 46 * t))
        flutter = 1 - 0.5 * (0.5 + 0.5 * np.sin(2 * np.pi * 30 * t))
        air = spectral(rng.standard_normal(len(t)).astype(np.float32), 2500) * 0.6
        return spectral((tone * flutter + air * flutter).astype(np.float32), 1300, bump=1.2, bump_at=650)
    if name.startswith("burr_"):
        base = float(name[5:])
        n = int(0.3 * SR)
        t = np.arange(n) / SR
        freq = base * (1 - 0.35 * t / t[-1])
        phase = 2 * np.pi * np.cumsum(freq) / SR
        tone = sum(np.sin(k * phase) / k for k in range(1, 20))
        flutter = 1 - 0.6 * (0.5 + 0.5 * np.sin(2 * np.pi * 34 * t))
        env = np.minimum(1, t / 0.008) * np.exp(-t / 0.09)
        return spectral((tone * flutter * env).astype(np.float32), 1600, bump=0.8, bump_at=600)
    raise ValueError(name)


def load(path, tempo=1.0, pitch=1.0, lp=0):
    filters = []
    if pitch != 1.0:
        filters.append(f"asetrate={int(SR * pitch)},aresample={SR}")
    if lp:
        filters += [f"lowpass=f={lp}", f"lowpass=f={lp}"]
    while tempo > 2.0:
        filters.append("atempo=2.0")
        tempo /= 2.0
    if tempo != 1.0:
        filters.append(f"atempo={tempo}")
    af = ["-af", ",".join(filters)] if filters else []
    raw = subprocess.run([FFMPEG, "-v", "error", "-i", str(path), *af, "-ac", "1", "-ar", str(SR), "-f", "f32le", "-"],
                         capture_output=True, check=True).stdout
    return np.frombuffer(raw, dtype=np.float32).copy()


def lowpass(x, cutoff):
    a = np.exp(-2 * np.pi * cutoff / SR)
    y = np.empty_like(x)
    acc = 0.0
    for i, v in enumerate(x):
        acc = (1 - a) * v + a * acc
        y[i] = acc
    return y


def echo(x, tail, delays=(0.23, 0.37), feedback=0.68, wet=0.5):
    n = len(x) + int(tail * SR)
    dry = np.zeros(n, dtype=np.float32)
    dry[:len(x)] = x
    out = dry.copy()
    for k, delay in enumerate(delays):
        d = int(delay * SR)
        tap = dry.copy()
        gain = wet / len(delays)
        shift = 0
        while gain > 0.002 and shift + d < n:
            shift += d
            tap = lowpass(tap, 5200 - 700 * k) if shift == d else tap
            out[shift:] += gain * tap[:n - shift]
            gain *= feedback
    fade = int(tail * SR)
    out[-fade:] *= np.linspace(1, 0, fade) ** 1.3
    return out


def rms_max(x, window=0.1, hop=0.01):
    w = int(window * SR)
    if len(x) <= w:
        return float(np.sqrt(np.sum(x.astype(np.float64) ** 2) / w))
    c = np.concatenate([[0.0], np.cumsum(x.astype(np.float64) ** 2)])
    starts = np.arange(0, len(x) - w + 1, int(hop * SR))
    return float(np.sqrt((c[starts + w] - c[starts]).max() / w))


def true_peak(x):
    up = np.fft.irfft(np.fft.rfft(x), 4 * len(x)) * 4
    return float(max(np.abs(x).max(), np.abs(up).max()))


def normalize(x, spec):
    if spec.get("target") is not None:
        gain = 10 ** ((spec["target"] + spec.get("gain_db", 0.0)) / 20) / max(1e-6, rms_max(x))
        ceiling = 10 ** (CEILING_DB / 20) / max(1e-6, true_peak(x))
        return x * min(gain, ceiling)
    return x * (10 ** ((spec["peak"] + spec.get("gain_db", 0.0)) / 20) / max(1e-6, np.abs(x).max()))


def zero_crossing(x, around, reach):
    lo = max(1, around - reach)
    hi = min(len(x) - 1, around + reach)
    seg = x[lo - 1:hi]
    idx = np.where((seg[:-1] <= 0) & (seg[1:] > 0))[0]
    if len(idx) == 0:
        return around
    slope = np.abs(x[lo + idx] - x[lo + idx - 1])
    return int(lo + idx[np.argmin(slope + 1e-4 * np.abs(lo + idx - around))])


def encode(x, target):
    target.parent.mkdir(parents=True, exist_ok=True)
    subprocess.run([FFMPEG, "-v", "error", "-y", "-f", "f32le", "-ar", str(SR), "-ac", "1", "-i", "-",
                    "-c:a", "libvorbis", "-q:a", "5", str(target)], input=x.astype(np.float32).tobytes(), check=True)


def shape(spec):
    if spec["source"].startswith("synth:"):
        x = synth(spec["source"][6:])
    else:
        x = load(SRC / spec["source"], spec["tempo"], spec["pitch"], spec["lp"])
    a = int(spec["start"] / spec["tempo"] * SR)
    b = len(x) if spec["end"] is None else min(len(x), int(spec["end"] / spec["tempo"] * SR))
    x = x[a:b] - np.mean(x[a:b])
    if spec["loop"] != 0:
        if spec["loop"] < 0:
            body = x.copy()
        else:
            xf = int(spec["loop"] * SR)
            start = zero_crossing(x, 0 + int(0.01 * SR), int(0.01 * SR))
            cut = zero_crossing(x, len(x) - xf, int(0.01 * SR))
            body = x[start:cut].copy()
            tail = x[cut:cut + xf]
            n = min(xf, len(tail), len(body))
            t = np.linspace(0, np.pi / 2, n)
            body[:n] = body[:n] * np.sin(t) + tail[:n] * np.cos(t)
        return body
    n_in = max(1, int(spec["fade_in"] * SR))
    n_out = min(len(x) // 2, max(1, int(spec["fade_out"] * SR)))
    x[:n_in] *= np.linspace(0, 1, n_in)
    x[-n_out:] *= np.linspace(1, 0, n_out) ** 2
    if spec["echo"] > 0:
        x = echo(x, spec["echo"])
    return x


def render_entry(entry, goal):
    variants = entry.get("variants", [])
    shaped = [shape(spec(v, goal)) for v in variants]
    if goal is None:
        return [(v, normalize(x, spec(v))) for v, x in zip(variants, shaped)]
    level = goal
    for v, x in zip(variants, shaped):
        reach = 20 * np.log10(10 ** (CEILING_DB / 20) / max(1e-6, true_peak(x)) * max(1e-9, rms_max(x)))
        level = min(level, reach - v.get("gain_db", 0.0))
    return [(v, normalize(x, spec(v, level))) for v, x in zip(variants, shaped)]


SPEC = ("source", "start", "end", "peak", "fade_in", "fade_out", "echo", "loop", "tempo", "pitch", "lp")


def catalog():
    return json.loads(CATALOG.read_text(encoding="utf-8"))["sounds"]


def spec(variant, target=None):
    out = {k: variant.get(k) for k in SPEC}
    out["peak"] = out["peak"] if out["peak"] is not None else -3.0
    out["gain_db"] = variant.get("gain_db", 0.0)
    out["target"] = target
    return out


def targets():
    return json.loads(LOUDNESS.read_text(encoding="utf-8")) if LOUDNESS.exists() else {}


def event(target, volume=1.0, pitch=1.0):
    entry = {"name": target, "type": "event"}
    if volume != 1.0:
        entry["volume"] = round(volume, 3)
    if pitch != 1.0:
        entry["pitch"] = round(pitch, 3)
    return entry


def sound_entries(e, by_id):
    status = e["status"]
    if status == "alias":
        return [event(e["alias"]["target"], e["alias"].get("volume", 1.0), e["alias"].get("pitch", 1.0))]
    if status == "vanilla":
        return [event(e["vanilla"]["event"], e["vanilla"].get("volume", 1.0), e["vanilla"].get("pitch", 1.0))]
    owner = by_id[e["variants_from"]] if e.get("variants_from") else e
    out = []
    for v in owner["variants"]:
        s = {"name": "simplywings:" + v["file"]}
        if e.get("volume", 1.0) != 1.0:
            s["volume"] = e["volume"]
        if e.get("pitch", 1.0) != 1.0:
            s["pitch"] = e["pitch"]
        if e.get("attenuation"):
            s["attenuation_distance"] = e["attenuation"]
        if e.get("preload"):
            s["preload"] = True
        if e.get("stream"):
            s["stream"] = True
        out.append(s)
    return out


def declared(e):
    return e.get("declared", True) and e["status"] != "silent" and (e["status"] != "todo" or e.get("variants"))


def build(only=None):
    entries = catalog()
    by_id = {e["id"]: e for e in entries}
    loudness = targets()
    if only is None and OUT.exists():
        shutil.rmtree(OUT)
    count = 0
    for e in entries:
        if only is not None and e["id"] not in only:
            continue
        goal = loudness.get(e.get("loudness_target") or "", {}).get("rms_max_db")
        for v, x in render_entry(e, goal):
            encode(x, OUT / f"{v['file']}.ogg")
            count += 1

    sounds = {}
    lang_keys = {}
    for e in entries:
        if not declared(e):
            continue
        body = {}
        if e.get("subtitle_key"):
            body["subtitle"] = f"subtitles.simplywings.{e['subtitle_key']}"
        if e.get("subtitle"):
            lang_keys[f"subtitles.simplywings.{e['subtitle_key']}"] = e["subtitle"]
        body["sounds"] = sound_entries(e, by_id)
        sounds[e["id"]] = body
    sounds_file = ASSETS / "sounds.json"
    sounds_file.write_text(json.dumps(sounds, indent=2) + "\n", encoding="utf-8", newline="\n")

    lang_file = ASSETS / "lang/en_us.json"
    lang = json.loads(lang_file.read_text(encoding="utf-8"))
    lang.update(lang_keys)
    lang_file.write_text(json.dumps(lang, indent=2, ensure_ascii=False) + "\n", encoding="utf-8", newline="\n")
    total = sum(f.stat().st_size for f in OUT.rglob("*.ogg"))
    print(f"{count} files, {total / 1024:.0f} KiB, {len(sounds)} sound events")


if __name__ == "__main__":
    build(set(sys.argv[1:]) or None)
