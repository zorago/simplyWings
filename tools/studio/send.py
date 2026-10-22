import sys
sys.stdout.reconfigure(encoding="utf-8")
import time
from pathlib import Path

root = Path(__file__).resolve().parents[2]
args = sys.argv[1:]
if args and args[0].startswith("--dir="):
    studio = Path(args[0][6:]) / "studio"
    args = args[1:]
else:
    studio = root / "run" / "studio"
lines = []
for arg in args:
    if arg.startswith("@"):
        lines += Path(arg[1:]).read_text(encoding="utf-8").splitlines()
    else:
        lines += arg.split(";")
log = studio / "log.txt"
start = len(log.read_text(encoding="utf-8").splitlines()) if log.exists() else 0
mark = f"m{time.time_ns()}"
tmp = studio / "commands.tmp"
tmp.write_text("\n".join(l.strip() for l in lines) + f"\nmark {mark}\n", encoding="utf-8")
tmp.replace(studio / "commands.txt")
deadline = time.time() + 900
while True:
    text = log.read_text(encoding="utf-8").splitlines() if log.exists() else []
    if f"mark {mark}" in text:
        break
    if time.time() > deadline:
        sys.exit("studio timeout")
    time.sleep(0.25)
for line in text[start:]:
    if line != f"mark {mark}":
        print(line)
errors = studio / "errors.txt"
if errors.exists():
    print("ERRORS:", errors.read_text(encoding="utf-8"))
    errors.unlink()
