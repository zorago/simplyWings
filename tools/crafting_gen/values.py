import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DATA = ROOT / "src/main/resources/data/simplywings"
OUT = DATA / "simplywings_offering/values.json"
MATERIALS = ROOT / "src/main/java/com/zorago/simplywings/item/MaterialIds.java"

TIER_VALUE = {"B": 0.01, "A": 0.1, "S": 1.0, "SS": 10.0, "SSS": 100.0, "MYTHIC": 100.0}
WING_TIER = {"rare": "S", "epic": "SS", "legendary": "SSS", "mythic": "MYTHIC"}
RATES = {"A": 0.12, "S": 0.01345, "SS": 0.00224, "SSS": 0.00137}
MYTHIC_SHARE = 0.01


def materials(name):
    text = MATERIALS.read_text(encoding="utf-8")
    block = re.search(name + r" = List\.of\((.*?)\);", text, re.S).group(1)
    return re.findall(r'"([a-z0-9_]+)"', block)


def main():
    out = {}
    for item in materials("DROPS"):
        out[item] = TIER_VALUE["B"]
    for item in materials("INTERMEDIATES") + materials("PARTS"):
        out[item] = TIER_VALUE["A"]
    for f in sorted((DATA / "simplywings/wing").glob("*.json")):
        rarity = json.loads(f.read_text(encoding="utf-8"))["rarity"]
        if rarity in WING_TIER:
            out[f.stem + "_wings"] = TIER_VALUE[WING_TIER[rarity]]
    b = 1 - sum(RATES.values())
    sss = RATES["SSS"] * ((1 - MYTHIC_SHARE) * TIER_VALUE["SSS"] + MYTHIC_SHARE * TIER_VALUE["MYTHIC"])
    ev = b * TIER_VALUE["B"] + RATES["A"] * TIER_VALUE["A"] + RATES["S"] * TIER_VALUE["S"] + RATES["SS"] * TIER_VALUE["SS"] + sss
    OUT.parent.mkdir(parents=True, exist_ok=True)
    data = {"simplywings:" + k: v for k, v in sorted(out.items())}
    OUT.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8", newline="\n")
    counts = {}
    for v in out.values():
        counts[v] = counts.get(v, 0) + 1
    print(f"{len(data)} values written to {OUT.relative_to(ROOT)}: " + ", ".join(f"{n} at {v:g}" for v, n in sorted(counts.items())))
    print(f"expected offering value of one pull, with the guarantees: {ev:.3f} summons")


if __name__ == "__main__":
    main()
