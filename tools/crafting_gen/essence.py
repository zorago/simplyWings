from data import C, TANKS
from pairs import PAIRS

TIERS = ["Rare", "Épique", "Légendaire", "Mythique"]
KINDS = ["cloud", "steam", "ethereal", "intensified"]
LABEL = {"cloud": "Cloud", "steam": "Steam", "ethereal": "Ethereal", "intensified": "Intensified"}
SHORT = {"cloud": "nuage", "steam": "vapeur", "ethereal": "éthérée", "intensified": "intensifiée"}
ASM = {
    "Rare": {"cloud": 100},
    "Épique": {"cloud": 150, "steam": 150},
    "Légendaire": {"cloud": 250, "steam": 250, "ethereal": 300},
    "Mythique": {"cloud": 250, "steam": 250, "ethereal": 250, "intensified": 250},
}
FIXED_TIER = {i: 3 for i in TANKS}
_tier = {}


def _inputs(i):
    return [C[i]["key"][ch] for row in C[i]["rows"] for ch in row if ch not in "._"]


def _walk():
    for p in PAIRS:
        t = TIERS.index(p["rarity"])
        stack = [p["harness"], p["core"], p["left"], p["right"]]
        seen = set()
        while stack:
            i = stack.pop()
            if i in seen or i not in C:
                continue
            seen.add(i)
            _tier[i] = min(_tier.get(i, t), t)
            stack += _inputs(i)


_walk()


def tier(i):
    if i in FIXED_TIER:
        return FIXED_TIER[i]
    return _tier.get(i, 0)


def complexity(total):
    if total <= 20:
        return 1
    if total <= 50:
        return 2
    if total <= 90:
        return 3
    return 4


def split(total, t):
    if total <= 0:
        return {}
    n = min(complexity(total), t + 1)
    kinds = KINDS[t - n + 1:t + 1]
    base = (total // n) // 5 * 5
    out = {k: base for k in kinds}
    out[kinds[-1]] += total - base * n
    return {k: v for k, v in out.items() if v > 0}


def cost(i):
    return split(C[i]["cost"], tier(i))


def json_cost(c):
    if not c:
        return 0
    if list(c) == ["cloud"]:
        return c["cloud"]
    return {k: c[k] for k in KINDS if k in c}


def fmt(c):
    if not c:
        return "0"
    return " + ".join(f"{c[k]} {SHORT[k]}" for k in KINDS if k in c)


def add(a, b, n=1):
    out = dict(a)
    for k, v in b.items():
        out[k] = out.get(k, 0) + v * n
    return out
