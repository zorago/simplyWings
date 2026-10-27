import json
import re
import subprocess
import sys
from collections import Counter
from pathlib import Path

import numpy as np

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "tools/sounds"))
import build_sounds as b

ASSETS = ROOT / "src/main/resources/assets/simplywings"
JAVA = ROOT / "src/main/java/com/zorago/simplywings"
SOURCES_MD = ROOT / "art/sounds/SOURCES.md"
SHOW_SOUNDS = ASSETS / "gacha/show_sounds.json"
sys.stdout.reconfigure(encoding="utf-8")
TTY = sys.stdout.isatty()


def color(text, code):
    return f"\033[{code}m{text}\033[0m" if TTY else text


errors = []
warnings = []


def error(msg):
    errors.append(msg)
    print(color("  ✗ " + msg, 31))


def warn(msg):
    warnings.append(msg)
    print(color("  ! " + msg, 33))


def ok(msg):
    print(color("  ✓ " + msg, 32))


def section(title):
    print("\n" + title)


def decode(path):
    raw = subprocess.run([b.FFMPEG, "-v", "error", "-i", str(path), "-f", "f32le", "-"], capture_output=True, check=True).stdout
    info = subprocess.run([b.FFMPEG, "-i", str(path)], capture_output=True).stderr.decode(errors="replace")
    m = re.search(r"Audio: [^,]+, (\d+) Hz, ([^,\n]+)", info)
    return np.frombuffer(raw, dtype=np.float32), (int(m.group(1)), m.group(2).strip()) if m else (None, None)


def measure(path):
    x, (rate, channels) = decode(path)
    if channels != "mono":
        x = x.reshape(-1, 2).mean(axis=1) if channels == "stereo" else x
    peak = float(np.abs(x).max()) if len(x) else 0.0
    attack = int(np.argmax(np.abs(x) >= peak * 0.1)) / b.SR if peak > 0 else 0.0
    return {
        "rms": 20 * np.log10(max(1e-9, b.rms_max(x))),
        "tp": 20 * np.log10(max(1e-9, b.true_peak(x))),
        "attack_ms": attack * 1000,
        "seam": abs(float(x[-1]) - float(x[0])) if len(x) > 1 else 0.0,
        "rate": rate,
        "channels": channels,
    }


def normalize_event(event):
    return re.sub(r"[^a-z0-9]", "", event.split(":")[-1].lower())


def main():
    cat = json.loads(b.CATALOG.read_text(encoding="utf-8"))["sounds"]
    by_id = {e["id"]: e for e in cat}
    loudness = b.targets()
    sounds = json.loads((ASSETS / "sounds.json").read_text(encoding="utf-8"))

    section("1. Catalogue, sounds.json et fichiers")
    declared = {e["id"] for e in cat if b.declared(e)}
    for k in sorted(set(sounds) - declared):
        error(f"{k} est dans sounds.json mais pas déclaré au catalogue")
    for k in sorted(declared - set(sounds)):
        error(f"{k} est au catalogue mais absent de sounds.json (construire)")
    referenced = set()
    for k, v in sounds.items():
        for s in v["sounds"]:
            name = s["name"] if isinstance(s, dict) else s
            if s.get("type") == "event":
                ns, path = name.split(":")
                if ns == "simplywings" and path not in sounds:
                    error(f"{k} pointe vers {name}, inconnu")
                continue
            path = name.split(":")[1]
            referenced.add(path)
            if not (ASSETS / "sounds" / f"{path}.ogg").exists():
                error(f"{k} : fichier {path}.ogg manquant")
    present = {str(p.relative_to(ASSETS / "sounds")).replace("\\", "/")[:-4] for p in (ASSETS / "sounds").rglob("*.ogg")}
    for orphan in sorted(present - referenced):
        error(f"OGG orphelin : {orphan}.ogg")
    if not errors:
        ok(f"{len(sounds)} événements, {len(present)} fichiers, tout est relié")

    section("2. Sons joués par le code")
    before = len(errors)
    java = {p: p.read_text(encoding="utf-8") for p in JAVA.rglob("*.java")}
    mod_ids = set()
    for text in java.values():
        mod_ids |= set(re.findall(r'sound\("([a-z0-9_.]+)"\)', text))
    for sid in sorted(mod_ids):
        if sid not in by_id:
            error(f"ModSounds enregistre {sid}, absent du catalogue")
    vanilla_known = set()
    for e in cat:
        if e.get("vanilla"):
            vanilla_known.add(normalize_event(e["vanilla"]["event"]))
        for l in e.get("layers") or []:
            if isinstance(l, dict):
                vanilla_known.add(normalize_event(l["event"]))
    used = Counter()
    for p, text in java.items():
        for const in re.findall(r"SoundEvents\.([A-Z0-9_]+)", text):
            used[(const, p.name)] += 1
    for (const, file), _ in sorted(used.items()):
        key = const.lower().replace("_", "")
        if not any(v.endswith(key) for v in vanilla_known):
            error(f"SoundEvents.{const} ({file}) n'a pas d'entrée vanilla au catalogue")
    if SHOW_SOUNDS.exists():
        show = json.loads(SHOW_SOUNDS.read_text(encoding="utf-8"))
        ids = set(re.findall(r'"((?:minecraft|simplywings):[a-z0-9_./]+)"', json.dumps(show)))
        for sid in sorted(ids):
            ns, path = sid.split(":")
            if ns == "simplywings" and path not in sounds:
                error(f"show_sounds.json joue {sid}, absent de sounds.json")
            if ns == "minecraft" and normalize_event(sid) not in vanilla_known:
                error(f"show_sounds.json joue {sid}, sans entrée vanilla au catalogue")
    if len(errors) == before:
        ok("chaque son joué par le code ou par show_sounds.json est au catalogue")

    section("3. Mesures par fichier (cible ± 2 dB, pic vrai ≤ −1 dBTP, attaque < 10 ms, mono 44,1 kHz, raccord < 0,005)")
    before = len(errors)
    spreads = {}
    for e in cat:
        if not e.get("variants"):
            continue
        goal = loudness.get(e.get("loudness_target") or "", {}).get("rms_max_db")
        levels = []
        measured = []
        for v in e["variants"]:
            path = ASSETS / "sounds" / f"{v['file']}.ogg"
            if not path.exists():
                continue
            m = measure(path)
            levels.append(m["rms"])
            measured.append((v, m))
        limited = any(m["tp"] > b.CEILING_DB - 1.0 for _, m in measured)
        for v, m in measured:
            tag = f"{v['file']} ({e['id']})"
            aim = None if goal is None else goal + v.get("gain_db", 0.0)
            if aim is not None and m["rms"] - aim > 2:
                error(f"{tag} : RMS max {m['rms']:.1f} dB, cible {aim:g} dB")
            elif aim is not None and aim - m["rms"] > 2:
                (warn if limited else error)(f"{tag} : RMS max {m['rms']:.1f} dB, cible {aim:g} dB" + (" (limité par le plafond de pic du groupe)" if limited else ""))
            if m["tp"] > -1.0:
                error(f"{tag} : pic vrai {m['tp']:.2f} dBTP > −1")
            if not e.get("slow_attack") and not e.get("loop") and m["attack_ms"] > 10:
                error(f"{tag} : attaque {m['attack_ms']:.0f} ms")
            if m["rate"] != 44100 or m["channels"] != "mono":
                error(f"{tag} : {m['rate']} Hz {m['channels']}, attendu 44100 Hz mono")
            if e.get("loop") and m["seam"] > 0.005:
                error(f"{tag} : raccord de boucle {m['seam']:.3f}")
        if len(levels) > 1:
            spreads[e["id"]] = max(levels) - min(levels)
    if len(errors) == before:
        ok("tous les fichiers sont dans leur cible")

    section("4. Écart entre variations d'un même id (≤ 1,5 dB)")
    before = len(errors)
    for sid, spread in sorted(spreads.items()):
        if spread > 1.5:
            error(f"{sid} : écart de {spread:.1f} dB entre variations")
    if len(errors) == before:
        ok(f"{len(spreads)} groupes de variations alignés")

    section("5. Aucun son lié aux particules")
    effects = java.get(JAVA / "client/WingEffects.java", "")
    if re.search(r"SoundEvents|playLocalSound|playSound", effects):
        error("WingEffects joue encore des sons")
    else:
        ok("WingEffects ne joue aucun son")

    section("6. Sources et licences")
    before = len(errors)
    md = SOURCES_MD.read_text(encoding="utf-8") if SOURCES_MD.exists() else ""
    sources = sorted({v["source"] for e in cat for v in e.get("variants", []) if not v["source"].startswith("synth:")})
    for src in sources:
        root = src.split("/")[0]
        family = re.sub(r"\d[\d_]*\.\w+$", "", src)
        if f"`{src}`" not in md and ("/" not in src or f"`{root}/`" not in md) and f"`{family}*`" not in md:
            error(f"source {src} sans ligne dans art/sounds/SOURCES.md")
    blockers = sorted({e["id"] for e in cat if e.get("license_blocker") and b.declared(e)})
    for sid in blockers:
        print(color(f"  ✗ {sid} : source non libre, bloquant pour la publication", 31))
    if len(errors) == before:
        ok(f"{len(sources)} sources référencées dans SOURCES.md")

    section("7. Compteurs")
    status = Counter(e["status"] for e in cat)
    verdict = Counter((e.get("verdict") or {}).get("state", "todo") for e in cat)
    print("  statuts : " + ", ".join(f"{k} {status[k]}" for k in ("custom", "vanilla", "alias", "silent", "todo")))
    print("  verdicts : " + ", ".join(f"{k} {verdict[k]}" for k in ("todo", "keep", "reject", "revise", "validated", "in_game")))
    files = sum(len(e.get("variants", [])) for e in cat)
    print(f"  fichiers custom : {files}")

    print()
    if errors:
        print(color(f"{len(errors)} erreur(s)", 31) + (f", {len(blockers)} son(s) bloquant(s) pour la publication" if blockers else ""))
        sys.exit(1)
    print(color("Tout est au vert", 32) + (f" ; {len(blockers)} son(s) bloquant(s) pour la publication" if blockers else ""))


if __name__ == "__main__":
    main()
