import json
import shutil
import subprocess
from pathlib import Path

import numpy as np

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "art/sounds/source"
ASSETS = ROOT / "src/main/resources/assets/simplywings"
OUT = ASSETS / "sounds"
WINGS = ROOT / "src/main/resources/data/simplywings/simplywings/wing"
FFMPEG = shutil.which("ffmpeg") or r"C:\Users\adamg\AppData\Local\Programs\Python\Python312\Scripts\ffmpeg"
SR = 44100

FEATHER = "oga_wings_flap_large/wings_flap_large.flac"
CAPE = "fs_711122_cape_wings.mp3"
CREATURE = "fs_759529_large_flying_creature.mp3"
UMBRELLA = "fs_593487_umbrella_bat_wings.mp3"
INSECT = "fs_482733_insect_wing.mp3"
HUGE = "fs_670509_huge_wing_flaps.mp3"
INTERFACE = "kenney_interface-sounds/Audio/"
IMPACT = "kenney_impact-sounds/Audio/"


FURY = "user/night_fury_dive.mp3"


CASINO = "kenney_casino-audio/Audio/"
UI_AUDIO = "kenney_ui-audio/Audio/"


def clip(source, start=0.0, end=None, peak=-3.0, fade_in=0.004, fade_out=0.25, echo=0.0, loop=0.0, tempo=1.0, pitch=1.0, lp=0):
    return {"source": source, "start": start, "end": end, "peak": peak, "fade_in": fade_in, "fade_out": fade_out, "echo": echo,
            "loop": loop, "tempo": tempo, "pitch": pitch, "lp": lp}


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


FILES = {
    "wing/feather/flap": [clip(CAPE, 22.16, 22.75), clip(CAPE, 25.94, 26.5), clip(CAPE, 29.39, 30.0)],
    "wing/membrane/flap": [clip(CREATURE, s - 0.04, s + 0.75, -2.0) for s in (2.28, 3.39, 4.49, 5.54, 6.55)],
    "wing/leather/flap": [clip(UMBRELLA, s - 0.03, s + 0.32, -4.0, fade_out=0.15) for s in (1.81, 2.72, 3.59, 4.50)],
    "wing/insect/flap": [clip(f"synth:burr_{f}", 0, None, -8.0, fade_out=0.05) for f in (120, 140, 165)],
    "wing/strong/flap": [clip(HUGE, 2.42, 3.62, -1.5, fade_out=0.4), clip(HUGE, 3.72, 4.3, -1.5), clip(HUGE, 5.06, 5.6, -1.5)],
    "fury/dive": [clip(FURY, 0.0, 5.0, -1.0, fade_out=0.03, echo=3.0)],
    "fury/invisible": [clip(FURY, 0.0, None, -1.0, fade_out=0.05, echo=3.0)],
    "gacha/ignite": [clip(f"oga_magical_{n}.ogg", 0, None, -2.0, fade_out=0.3) for n in ("1_0", "2", "4", "5", "6_0", "7_0")],
    "gacha/offer": [clip("oga_magical_3.ogg", 0, None, -2.0, fade_out=0.4)],
    "gacha/rune_charge": [clip("fs_832552_crystal_ball.mp3", 1.0, 9.6, -3.0, fade_in=0.3, fade_out=2.5),
                          clip("fs_832552_crystal_ball.mp3", 9.2, 17.6, -3.0, fade_in=0.3, fade_out=2.5)],
    "gacha/beam": [clip("fs_859497_powerup_loop.mp3", 0, None, -2.0, fade_in=0.05, fade_out=0.8)],
    "gacha/shockwave": [clip("fs_814053_shockwave.mp3", 0, 6.95, -1.0, fade_out=2.0)],
    "gacha/blast": [clip("fs_814044_creation_magic_blast.mp3", 0, 6.6, -1.0, fade_out=2.0)],
    "gacha/implosion": [clip("fs_814047_fantasy_implosion.mp3", 0, 7.7, -1.0, fade_out=2.0)],
    "gacha/puff": [clip("fs_714257_puff_of_smoke.mp3", 0, None, -3.0, fade_out=0.15)],
    "gacha/bell": [clip(f"{IMPACT}impactBell_heavy_00{n}.ogg", 0, None, -2.0, fade_out=0.3) for n in range(5)],
    "gacha/chime": [clip(f"{IMPACT}impactGlass_medium_00{n}.ogg", 0, None, -4.0, fade_out=0.2) for n in range(5)],
    "gacha/twinkle": [clip(f"{IMPACT}impactGlass_light_00{n}.ogg", 0, None, -6.0, fade_out=0.1) for n in range(5)],
    "ui/card_deal": [clip(f"{CASINO}card-slide-{n}.ogg", 0, None, -8.0, fade_out=0.1, lp=6000) for n in range(1, 9)],
    "ui/card_flip": [clip(f"{CASINO}card-shove-{n}.ogg", 0, None, -6.0, fade_out=0.1, lp=6000) for n in range(1, 5)]
                    + [clip(f"{CASINO}card-fan-1.ogg", 0, None, -6.0, fade_out=0.15, lp=6000)],
    "ui/card_shuffle": [clip(f"{CASINO}card-shuffle.ogg", 0, None, -7.0, fade_out=0.3, lp=5000)],
    "ui/card_place": [clip(f"{CASINO}card-place-{n}.ogg", 0, None, -6.0, fade_out=0.1, lp=4000, pitch=0.85) for n in range(1, 5)],
    "ui/confirm": [clip(f"{INTERFACE}bong_001.ogg", 0, None, -9.0, fade_out=0.06, pitch=0.7, lp=1800),
                   clip(f"{UI_AUDIO}switch3.ogg", 0, None, -9.0, fade_out=0.04, pitch=0.7, lp=1800)],
    "ui/equip": [clip("fs_561287.mp3", 0.2, 4.4, -3.0, fade_out=1.0)],
    "ui/open": [clip(f"{INTERFACE}open_00{n}.ogg", 0, None, -10.0, fade_out=0.05, pitch=0.75, lp=2000) for n in (2, 3, 4)],
    "fury/night_charge": [clip("fs_814061.mp3", 0.0, 2.6, -2.0, fade_in=0.4, fade_out=0.4)],
    "fury/crackle": [clip("fs_582631.mp3", 0.08, 0.9, -4.0, fade_out=0.2)],
    "fury/light_charge": [clip("fs_407052.mp3", 0.4, 20.0, -2.0, fade_in=0.2, fade_out=0.3, tempo=8.0)],
    "flight/wind": [clip("fs_454072.mp3", 30.0, 46.0, -3.0, loop=1.5)],
    "flight/landing_hard": [clip("fs_504626.mp3", 0.05, 1.4, -2.0, fade_out=0.4)],
    "flight/landing_soft": [clip(f"{IMPACT}impactSoft_medium_00{n}.ogg", 0, None, -8.0, fade_out=0.1) for n in range(4)],
    "wing/buzz/loop": [clip("synth:buzz_loop", 0, None, -8.0, loop=-1)],
    "block/forge_bench/hammer": [clip("fs_434339.mp3", s - 0.03, s + 0.9, -3.0, fade_out=0.4) for s in (0.23, 4.17, 5.74, 12.16, 13.78, 18.69)],
    "block/forge_bench/quench": [clip("fs_234782.mp3", 0.05, None, -4.0, fade_out=0.5)],
    "block/cloud_compressor/loop": [clip("fs_487748.mp3", 0.0, 7.6, -4.0, loop=0.8)],
    "essence/pour": [clip("fs_875132.mp3", 1.6, 2.6, -5.0, fade_out=0.2), clip("fs_875132.mp3", 2.9, 3.95, -5.0, fade_out=0.2)],
    "essence/full": [clip("fs_511485.mp3", 0, None, -6.0, fade_out=0.3)],
    "ui/denied": [clip(f"{INTERFACE}back_00{n}.ogg", 0, None, -9.0, fade_out=0.04, pitch=0.6, lp=1500) for n in (2, 3)],
}

FAMILIES = {
    "feather": ("wing.feather.flap", 1.0, 1.0),
    "feather_matter": ("wing.feather.flap", 0.6, 1.1),
    "element": ("wing.feather.flap", 0.35, 1.15),
    "membrane": ("wing.membrane.flap", 1.0, 1.0),
    "leather": ("wing.leather.flap", 1.0, 1.0),
    "glider": ("wing.leather.flap", 0.5, 0.85),
    "buzz": ("wing.insect.flap", 0.8, 1.2),
    "soft": ("wing.feather.flap", 0.3, 1.3),
    "mechanism": ("minecraft:block.wooden_trapdoor.open", 0.6, 0.8),
}

PAIRS = {
    "common": ("feather", 1.0), "crow": ("feather", 0.95), "owl": ("feather", 0.9, 0.25), "gull": ("feather", 1.1),
    "eagle": ("feather", 0.85), "swift": ("feather", 1.25), "pigeon": ("feather", 1.15), "parrot": ("feather", 1.2),
    "peacock": ("feather", 0.95), "patchwork": ("feather", 1.05), "angel": ("feather", 0.9), "seraph": ("feather", 0.85),
    "yin_yang": ("feather", 1.0),
    "amethyst": ("feather_matter", 1.0), "copper": ("feather_matter", 0.95), "earth": ("feather_matter", 0.8),
    "fire": ("feather_matter", 1.0), "storm": ("feather_matter", 1.05), "shadow": ("feather_matter", 0.85),
    "nebula": ("feather_matter", 1.0),
    "water": ("element", 0.9), "air": ("element", 1.2), "aurora": ("element", 1.1), "forest": ("element", 0.95),
    "demon": ("membrane", 0.8), "ptero": ("membrane", 0.9), "phantom": ("membrane", 1.05), "night": ("membrane", 0.9),
    "light": ("membrane", 1.05), "bat": ("leather", 1.1),
    "squirrel": ("glider", 1.2), "flying_fish": ("glider", 1.1), "kite": ("glider", 0.9),
    "hummingbird": ("buzz", 1.6), "bee": ("buzz", 1.4), "beetle": ("buzz", 0.9), "ancient": ("buzz", 0.7),
    "moth": ("soft", 1.0), "psyche": ("soft", 1.15),
    "ornithopter": ("mechanism", 1.0), "daedalus": ("mechanism", 0.7),
}

STRONG_PITCH = {"membrane": 0.85, "leather": 1.2, "glider": 1.2, "buzz": 1.5, "soft": 1.4, "mechanism": 1.1}
STRONG_VOLUME = {"element": 0.5, "glider": 0.5, "buzz": 0.35, "soft": 0.3, "mechanism": 0.4}

SUBTITLES = {
    "wing.flap": "Wings flap",
    "wing.flap_strong": "Wings beat",
    "flight.takeoff_launch": "Wings launch",
    "gacha.offer": "Offering burns",
    "gacha.ignite": "Altar flame ignites",
    "gacha.rune_charge": "Altar hums",
    "gacha.beam": "Pillar of light rises",
    "gacha.shockwave": "Sky shakes",
    "gacha.blast": "Star bursts",
    "gacha.implosion": "Magic collapses",
    "gacha.puff": "Cloud puffs",
    "gacha.bell": "Star chimes",
    "gacha.chime": "Crystal rings",
    "gacha.twinkle": "Star twinkles",
    "ui.card_deal": "Card dealt",
    "ui.card_flip": "Card turns",
    "ui.card_shuffle": "Cards shuffle",
    "ui.card_place": "Card laid down",
    "ui.confirm": "Card revealed",
    "ui.denied": "Not enough feathers",
    "ui.equip": "Wings unfold",
    "ui.open": "Menu opens",
    "wing.buzz": "Wings buzz",
    "flight.wind": "Wind rushes",
    "flight.landing_hard": "Heavy landing",
    "flight.landing_soft": "Landing",
    "block.forge_bench.hammer": "Hammer strikes",
    "block.forge_bench.quench": "Metal hisses",
    "block.cloud_compressor.loop": "Compressor churns",
    "essence.pour": "Essence pours",
    "essence.full": "Container fills up",
}


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


def render(spec, target):
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
            body = x[:len(x) - xf].copy()
            t = np.linspace(0, np.pi / 2, xf)
            body[:xf] = body[:xf] * np.sin(t) + x[len(x) - xf:] * np.cos(t)
        body *= 10 ** (spec["peak"] / 20) / max(1e-6, np.abs(body).max())
        target.parent.mkdir(parents=True, exist_ok=True)
        subprocess.run([FFMPEG, "-v", "error", "-y", "-f", "f32le", "-ar", str(SR), "-ac", "1", "-i", "-",
                        "-c:a", "libvorbis", "-q:a", "5", str(target)], input=body.astype(np.float32).tobytes(), check=True)
        return
    n_in = max(1, int(spec["fade_in"] * SR))
    n_out = min(len(x) // 2, max(1, int(spec["fade_out"] * SR)))
    x[:n_in] *= np.linspace(0, 1, n_in)
    x[-n_out:] *= np.linspace(1, 0, n_out) ** 2
    if spec["echo"] > 0:
        x = echo(x, spec["echo"])
    x *= 10 ** (spec["peak"] / 20) / max(1e-6, np.abs(x).max())
    target.parent.mkdir(parents=True, exist_ok=True)
    subprocess.run([FFMPEG, "-v", "error", "-y", "-f", "f32le", "-ar", str(SR), "-ac", "1", "-i", "-",
                    "-c:a", "libvorbis", "-q:a", "5", str(target)], input=x.astype(np.float32).tobytes(), check=True)


def files(base):
    return [{"name": f"simplywings:{base}_{i + 1}"} for i in range(len(FILES[base]))]


def subtitle(key):
    return f"subtitles.simplywings.{key}"


def event(target, volume=1.0, pitch=1.0):
    entry = {"name": target, "type": "event"}
    if volume != 1.0:
        entry["volume"] = round(volume, 3)
    if pitch != 1.0:
        entry["pitch"] = round(pitch, 3)
    return entry


def main():
    if OUT.exists():
        shutil.rmtree(OUT)
    for base, specs in FILES.items():
        for i, spec in enumerate(specs):
            render(spec, OUT / f"{base}_{i + 1}.ogg")

    sounds_file = ASSETS / "sounds.json"
    sounds = {k: v for k, v in json.loads(sounds_file.read_text(encoding="utf-8")).items()
              if k in ("light_fury.charge", "light_fury.blast")}
    sounds.pop("light_fury.charge", None)
    sounds["night_fury.charge"] = {"subtitle": subtitle("night_fury.charge"), "sounds": files("fury/night_charge")}
    sounds["night_fury.crackle"] = {"subtitle": subtitle("night_fury.charge"), "sounds": files("fury/crackle")}
    sounds["light_fury.charge"] = {"subtitle": subtitle("light_fury.charge"), "sounds": files("fury/light_charge")}
    SUBTITLES["night_fury.charge"] = "Night Fury crackles"
    for fury, pitch in (("night_fury", 0.97), ("light_fury", 1.03)):
        name = "Night" if fury == "night_fury" else "Light"
        sounds[f"{fury}.dive"] = {"subtitle": subtitle(f"{fury}.dive"), "sounds": files("fury/dive")}
        sounds[f"{fury}.invisible"] = {"subtitle": subtitle(f"{fury}.invisible"),
                                       "sounds": [dict(files("fury/invisible")[0], pitch=pitch)]}
        SUBTITLES[f"{fury}.invisible"] = f"{name} Fury vanishes"
    for family in ("feather", "membrane", "leather", "insect", "strong"):
        sounds[f"wing.{family}.flap"] = {"sounds": files(f"wing/{family}/flap")}
    sounds["wing.buzz.loop"] = {"sounds": files("wing/buzz/loop")}
    for base in FILES:
        if base.startswith(("gacha/", "ui/", "block/", "essence/", "flight/")):
            key = base.replace("/", ".")
            sounds[key] = {"subtitle": subtitle(key), "sounds": files(base)}
    sounds["flight.takeoff_launch"] = {"subtitle": subtitle("flight.takeoff_launch"),
                                       "sounds": [event("simplywings:wing.strong.flap", 1.0, 1.1)]}

    pairs = sorted(p.stem for p in WINGS.glob("*.json"))
    missing = [p for p in pairs if p not in PAIRS]
    if missing:
        raise SystemExit(f"no sound family for {missing}")
    for pair in pairs:
        family, pitch, *rest = PAIRS[pair]
        volume = rest[0] if rest else 1.0
        target, family_volume, family_pitch = FAMILIES[family]
        if ":" not in target:
            target = "simplywings:" + target
        sounds[f"wing.{pair}.flap"] = {"subtitle": subtitle("wing.flap"),
                                       "sounds": [event(target, volume * family_volume, pitch * family_pitch)]}
        if family == "buzz" or pair == "daedalus":
            target_loop = "simplywings:block.cloud_compressor.loop" if pair == "daedalus" else "simplywings:wing.buzz.loop"
            sounds[f"wing.{pair}.buzz"] = {"subtitle": subtitle("wing.buzz"), "sounds": [event(target_loop, 1.0, pitch * (1.4 if pair == "daedalus" else 1.0))]}
        sounds[f"wing.{pair}.flap_strong"] = {"subtitle": subtitle("wing.flap_strong"),
                                              "sounds": [event("simplywings:wing.strong.flap", volume * STRONG_VOLUME.get(family, 1.0),
                                                               pitch * STRONG_PITCH.get(family, 1.0))]}
    sounds_file.write_text(json.dumps(sounds, indent=2) + "\n", encoding="utf-8", newline="\n")

    lang_file = ASSETS / "lang/en_us.json"
    lang = json.loads(lang_file.read_text(encoding="utf-8"))
    for key, text in SUBTITLES.items():
        lang[subtitle(key)] = text
    lang_file.write_text(json.dumps(lang, indent=2, ensure_ascii=False) + "\n", encoding="utf-8", newline="\n")
    total = sum(f.stat().st_size for f in OUT.rglob("*.ogg"))
    print(f"{sum(len(v) for v in FILES.values())} files, {total / 1024:.0f} KiB, {len(sounds)} sound events")


if __name__ == "__main__":
    main()
