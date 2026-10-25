import random
import statistics
import sys

BASE = {"SSS": 0.0003, "SS": 0.001, "S": 0.006, "A": 0.12}
SOFT = {"S": (80, 0.05), "SS": (400, 0.01), "SSS": (800, 0.005)}
MYTHIC = 0.01
ORDER = ["SSS", "SS", "S"]
RANK = {"B": 0, "A": 1, "S": 2, "SS": 3, "SSS": 4}


def rate(tier, pulls):
    start, step = SOFT[tier]
    return min(1.0, BASE[tier] + max(0, pulls - start) * step)


def run(n, seed=1):
    rnd = random.Random(seed)
    pity = {"S": 0, "SS": 0, "SSS": 0}
    counts = {k: 0 for k in RANK}
    mythic = 0
    gaps = {"S": [], "SS": [], "SSS": []}
    last = {"S": 0, "SS": 0, "SSS": 0}
    for i in range(1, n + 1):
        for t in pity:
            pity[t] += 1
        r = rnd.random()
        acc = 0.0
        got = None
        for t in ORDER:
            acc += rate(t, pity[t])
            if r < acc:
                got = t
                break
        if got is None:
            got = "A" if r < acc + BASE["A"] else "B"
        if got == "SSS" and rnd.random() < MYTHIC:
            mythic += 1
        counts[got] += 1
        for t in pity:
            if RANK[got] >= RANK[t]:
                gaps[t].append(i - last[t])
                last[t] = i
                pity[t] = 0
    return counts, gaps, mythic


def main():
    n = int(sys.argv[1]) if len(sys.argv) > 1 else 2_000_000
    counts, gaps, mythic = run(n)
    for k, v in counts.items():
        print(f"{k:>3}: {v / n * 100:.3f} %")
    print(f"mythic: one in {n / max(1, mythic):.0f} pulls")
    for t, g in gaps.items():
        print(f"{t:>3} or better: every {statistics.mean(g):.1f} pulls, worst {max(g)}")


if __name__ == "__main__":
    main()
