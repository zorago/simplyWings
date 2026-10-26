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


def clip(source, start=0.0, end=None, peak=-3.0, fade_in=0.004, fade_out=0.25, echo=0.0):
    return {"source": source, "start": start, "end": end, "peak": peak, "fade_in": fade_in, "fade_out": fade_out, "echo": echo}


FILES = {
    "wing/feather/flap": [clip(FEATHER, 0.0, 0.62), clip(FEATHER, 1.03, 1.66), clip(FEATHER, 2.08, 2.63),
                          clip(CAPE, 22.16, 22.75), clip(CAPE, 25.94, 26.5), clip(CAPE, 29.39, 30.0)],
    "wing/membrane/flap": [clip(CREATURE, s - 0.04, s + 0.75, -2.0) for s in (2.28, 3.39, 4.49, 5.54, 6.55)],
    "wing/leather/flap": [clip(UMBRELLA, s - 0.03, s + 0.32, -4.0, fade_out=0.15) for s in (1.81, 2.72, 3.59, 4.50)],
    "wing/insect/flap": [clip(INSECT, 0.07, 0.45, -6.0, fade_out=0.15), clip(INSECT, 0.94, 1.28, -6.0, fade_out=0.15),
                         clip(INSECT, 4.09, 4.55, -6.0, fade_out=0.15)],
    "wing/strong/flap": [clip(HUGE, 2.42, 3.62, -1.5, fade_out=0.4), clip(HUGE, 3.72, 4.3, -1.5), clip(HUGE, 5.06, 5.6, -1.5),
                         clip(HUGE, 5.87, 6.98, -1.5, fade_out=0.4), clip(HUGE, 9.27, 10.4, -1.5, fade_out=0.4)],
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
    "ui/card_deal": [clip(f"{INTERFACE}drop_00{n}.ogg", 0, None, -6.0, fade_out=0.05) for n in range(1, 5)],
    "ui/card_flip": [clip(f"{INTERFACE}maximize_00{n}.ogg", 0, None, -5.0, fade_out=0.08) for n in range(1, 5)],
    "ui/confirm": [clip(f"{INTERFACE}confirmation_00{n}.ogg", 0, None, -5.0, fade_out=0.08) for n in (1, 2)],
    "ui/denied": [clip(f"{INTERFACE}error_00{n}.ogg", 0, None, -6.0, fade_out=0.05) for n in (4, 8)],
}

FAMILIES = {
    "feather": ("wing.feather.flap", 1.0, 1.0),
    "feather_matter": ("wing.feather.flap", 0.6, 1.1),
    "element": ("wing.feather.flap", 0.35, 1.15),
    "membrane": ("wing.membrane.flap", 1.0, 1.0),
    "leather": ("wing.leather.flap", 1.0, 1.0),
    "glider": ("wing.leather.flap", 0.5, 0.85),
    "buzz": ("wing.insect.flap", 0.8, 1.2),
    "soft": ("wing.insect.flap", 0.45, 0.75),
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
    "ui.confirm": "Card revealed",
    "ui.denied": "Not enough feathers",
}


def load(path):
    raw = subprocess.run([FFMPEG, "-v", "error", "-i", str(path), "-ac", "1", "-ar", str(SR), "-f", "f32le", "-"],
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
    x = load(SRC / spec["source"])
    a = int(spec["start"] * SR)
    b = len(x) if spec["end"] is None else min(len(x), int(spec["end"] * SR))
    x = x[a:b] - np.mean(x[a:b])
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
    for fury, pitch in (("night_fury", 0.97), ("light_fury", 1.03)):
        name = "Night" if fury == "night_fury" else "Light"
        sounds[f"{fury}.dive"] = {"subtitle": subtitle(f"{fury}.dive"), "sounds": files("fury/dive")}
        sounds[f"{fury}.invisible"] = {"subtitle": subtitle(f"{fury}.invisible"),
                                       "sounds": [dict(files("fury/invisible")[0], pitch=pitch)]}
        SUBTITLES[f"{fury}.invisible"] = f"{name} Fury vanishes"
    for family in ("feather", "membrane", "leather", "insect", "strong"):
        sounds[f"wing.{family}.flap"] = {"sounds": files(f"wing/{family}/flap")}
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
