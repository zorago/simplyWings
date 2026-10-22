import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
WINGS = ROOT / "src/main/resources/data/simplywings/simplywings/wing"
STATS = ["endurance", "acceleration", "speed", "handling", "stability", "glide", "lift"]
TIERS = {"common": "base", "rare": "base", "epic": "epic", "legendary": "legendary"}
SHARE = {"base": 0.5, "epic": 0.3, "legendary": 0.2}


def compute(wings):
    pairs = {k: w for k, w in wings.items() if w["rarity"] in TIERS}
    totals = {s: 100 - min(w["equipped"][s] for w in pairs.values()) for s in STATS}
    result = {}
    for tier, share in SHARE.items():
        members = [k for k, w in pairs.items() if TIERS[w["rarity"]] == tier]
        cols = {s: share * totals[s] for s in STATS}
        row = sum(cols.values()) / len(members)
        m = {}
        for k in members:
            eq = pairs[k]["equipped"]
            best = max(eq.values())
            m[k] = {s: (eq[s] / best) ** 4 + 0.01 for s in STATS}
        for _ in range(800):
            for k in members:
                f = row / sum(m[k].values())
                m[k] = {s: v * f for s, v in m[k].items()}
            for s in STATS:
                f = cols[s] / sum(m[k][s] for k in members)
                for k in members:
                    m[k][s] *= f
        result.update(m)
    return result, totals


def main():
    write = "--write" in sys.argv
    wings = {p.stem: json.loads(p.read_text(encoding="utf-8")) for p in sorted(WINGS.glob("*.json"))}
    result, totals = compute(wings)
    worst = 0.0
    for k, g in result.items():
        for s in STATS:
            worst = max(worst, abs(round(g[s], 2) - wings[k]["global"][s]))
    print("totals", totals)
    print(f"max difference with current JSON: {worst:.2f}")
    if write:
        for k, g in result.items():
            path = WINGS / f"{k}.json"
            data = json.loads(path.read_text(encoding="utf-8"))
            data["global"] = {s: round(g[s], 2) for s in STATS}
            path.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8", newline="\n")
        print("written")
    return result


if __name__ == "__main__":
    main()
