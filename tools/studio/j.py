import sys
from pathlib import Path
sys.stdout.reconfigure(encoding="utf-8")
root = Path(__file__).resolve().parents[2]
journal = root / "docs" / "test-runs" / "2026-10-29" / (sys.argv[1] + ".md")
with journal.open("a", encoding="utf-8") as f:
    for row in sys.argv[2:]:
        cells = row.split("|")
        while len(cells) < 4:
            cells.append("")
        f.write("| " + " | ".join(c.strip() for c in cells) + " |\n")
