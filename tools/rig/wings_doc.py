import sys
from pathlib import Path

heading, appearance, motion = sys.argv[1], sys.argv[2], sys.argv[3]
path = Path(__file__).resolve().parents[2] / "WINGS.md"
s = path.read_text(encoding="utf-8")
start = s.index("## " + heading + "\n")
end = s.index("\n## ", start + 1)
block = s[start:end]
lines = block.split("\n")
i = next(n for n, l in enumerate(lines) if l.startswith("**Apparence**"))
j = i + 1
while j < len(lines) and lines[j].strip() and not lines[j].startswith("**"):
    j += 1
if j < len(lines) and lines[j].strip() == "" and j + 1 < len(lines) and lines[j + 1].startswith("**Mouvement signature**"):
    j += 2
lines[i:j] = ["**Apparence** : " + appearance, "", "**Mouvement signature** : " + motion]
path.write_text(s[:start] + "\n".join(lines) + s[end:], encoding="utf-8")
