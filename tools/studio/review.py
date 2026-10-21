import subprocess
import sys
import time
from pathlib import Path

root = Path(__file__).resolve().parents[2]
studio = root / "run" / "studio"
wing = sys.argv[1]
only = sys.argv[2] if len(sys.argv) > 2 else "all"

poses = [
    ("ground_back", "state ground", "cam 25 12 3.4"),
    ("ground_side", "state ground", "cam 100 5 3.4"),
    ("rouse", "state ground\nrouse 0.5", "cam 25 12 3.4"),
    ("charge", "state charge 1", "cam 20 10 3.8"),
    ("air", "state air", "cam 30 10 3.8"),
    ("landing", "state landing", "cam 60 5 3.8"),
    ("cruise_back", "state cruise", "cam 0 35 4"),
    ("cruise_front", "state cruise", "cam 180 -15 4"),
    ("cruise_side", "state cruise", "cam 90 5 4"),
    ("dive", "state dive 0.6", "cam 30 25 4"),
    ("tuck", "state tuck", "cam 30 25 4"),
    ("brake", "state brake", "cam 60 5 4"),
    ("stall", "state cruise 1 stall", "cam 40 10 4"),
    ("hover_0", "state hover\nhover 0", "cam 20 10 4"),
    ("hover_50", "state hover\nhover 0.5", "cam 20 10 4"),
]
beats = [0.0, 0.12, 0.25, 0.37, 0.5, 0.58, 0.66, 0.75, 0.88]

lines = [f"wing {wing}", "time 100"]
names = []
if only in ("all", "poses"):
    for name, state, cam in poses:
        lines += ["live", "time 100", state, cam, f"shot {wing}_{name}"]
        names.append(f"{wing}_{name}")
beat_names = []
if only in ("all", "beat"):
    for view, cam in (("back", "cam 0 30 4"), ("side", "cam 90 0 4")):
        for t in beats:
            name = f"{wing}_beat_{view}_{int(t * 100):02d}"
            lines += ["live", "time 100", "state cruise", cam, f"beat {t}", f"shot {name}"]
            beat_names.append(name)
lines += ["live", "beat off"]
mark = f"m{time.time_ns()}"
tmp = studio / "commands.tmp"
tmp.write_text("\n".join(lines) + f"\nmark {mark}\n", encoding="utf-8")
tmp.replace(studio / "commands.txt")
log = studio / "log.txt"
deadline = time.time() + 600
while f"mark {mark}" not in (log.read_text(encoding="utf-8") if log.exists() else ""):
    if time.time() > deadline:
        sys.exit("studio timeout")
    time.sleep(0.5)
errors = studio / "errors.txt"
if errors.exists():
    print(errors.read_text(encoding="utf-8"))
    errors.unlink()
sheets = studio / "sheets"
sheet = [sys.executable, "-I", str(root / "tools" / "studio" / "sheet.py"), str(studio / "shots")]
if names:
    subprocess.run(sheet + [str(sheets / f"{wing}_poses.png")] + names, check=True)
if beat_names:
    subprocess.run(sheet + [str(sheets / f"{wing}_beat_back.png")] + beat_names[:9], check=True)
    subprocess.run(sheet + [str(sheets / f"{wing}_beat_side.png")] + beat_names[9:], check=True)
