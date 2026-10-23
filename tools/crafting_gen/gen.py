import math
import re
import sys
from collections import Counter, OrderedDict
from pathlib import Path

from data import C, D, V, SHARED
from pairs import PAIRS
from essence import ASM as ESS_ASM, add, cost as ess_cost, fmt

REPO = Path(__file__).resolve().parents[2]
OUT = REPO / "crafting"
OUT.mkdir(exist_ok=True)

LIMIT = {"Rare": 1, "Épique": 2, "Légendaire": 3, "Mythique": 5}
RAR_LABEL = {"Rare": "rare", "Épique": "épique", "Légendaire": "légendaire", "Mythique": "mythique"}

errors = []


def name(i):
    if i in C:
        return C[i]["name"]
    if i in D:
        return D[i]["name"]
    if i in V:
        return V[i][0]
    errors.append(f"unknown item {i}")
    return i


def rid(i):
    return f"minecraft:{i}" if i in V else f"simplywings:{i}"


def inputs(i):
    r = C[i]
    cnt = Counter(ch for row in r["rows"] for ch in row if ch not in "._")
    return OrderedDict((r["key"][k], cnt[k]) for k in r["key"] if cnt[k])


for i, r in C.items():
    rows = r["rows"]
    if len(rows) != 5 or any(len(x) != 5 for x in rows):
        errors.append(f"{i}: bad shape")
        continue
    for a, b in [(0, 0), (0, 4), (4, 0), (4, 4)]:
        if rows[a][b] != ".":
            errors.append(f"{i}: corner not empty")
    for row in rows:
        for j, ch in enumerate(row):
            if ch == "." and not ((row is rows[0] or row is rows[4]) and j in (0, 4)):
                errors.append(f"{i}: dot outside corner")
            if ch not in "._" and ch not in r["key"]:
                errors.append(f"{i}: letter {ch} not in key")
    used = {ch for row in rows for ch in row}
    for k in r["key"]:
        if k not in used:
            errors.append(f"{i}: key {k} unused")
        name(r["key"][k])

LEVEL = {}


def level(i):
    if i in LEVEL:
        return LEVEL[i]
    LEVEL[i] = 0 if i not in C else 1 + max(level(x) for x in inputs(i))
    return LEVEL[i]


def anchor(t):
    t = t.lower()
    t = re.sub(r"[^\w\- ]", "", t)
    return t.replace(" ", "-")


wings_md = (REPO / "WINGS.md").read_text(encoding="utf-8").splitlines()
heading_of = {}
cur = None
for line in wings_md:
    if line.startswith("## "):
        cur = line[3:].strip()
    m = re.match(r"`simplywings:(\w+)`", line)
    if m and cur:
        heading_of.setdefault(m.group(1), cur)
heading_of.setdefault("night", "Les mythiques : Night et Light")
heading_of.setdefault("light", "Les mythiques : Night et Light")


def fmt_inputs(i, num):
    parts = []
    for x, n in inputs(i).items():
        ref = f" → #{num[x]}" if x in num else ""
        parts.append(f"{name(x)} ×{n}{ref}")
    return ", ".join(parts)


def where(x):
    return "GRID"


summary = []
RATIO = {}
drop_users = {}
shared_users = {}


def build(p):
    rar = p["rarity"]
    parts = [p["harness"], p["core"], p["left"], p["right"]]
    for x in set(parts):
        if x not in C:
            errors.append(f"{p['id']}: part {x} not craft")
        elif level(x) > LIMIT[rar]:
            errors.append(f"{p['id']}: {x} level {level(x)} > {LIMIT[rar]}")
    demand = Counter(parts)
    order = []

    def visit(i):
        if i in order or i not in C:
            return
        for x in inputs(i):
            visit(x)
        order.append(i)

    for x in parts:
        visit(x)
    order.sort(key=lambda i: level(i))
    crafts = {}
    raw = Counter()
    for i in sorted(order, key=lambda i: -level(i)):
        n = math.ceil(demand[i] / C[i]["out"])
        crafts[i] = n
        for x, q in inputs(i).items():
            if x in C:
                demand[x] += q * n
            else:
                raw[x] += q * n
    num = {}
    rows = []
    cum = {}
    for k, i in enumerate(order, 1):
        num[i] = k
        n = crafts[i]
        cost = ess_cost(i)
        cum = add(cum, cost, n)
        made = n * C[i]["out"]
        ess = f"{n} × ({fmt(cost)})" if n > 1 and cost else fmt(cost)
        rows.append(f"| {k} | {name(i)} `{rid(i)}` ×{made} | `GRID` | {level(i)} | {n} | {fmt_inputs(i, num)} | {ess} | {fmt(cum)} |")
    pieces = cum
    asm = ESS_ASM[rar]
    cum = add(cum, asm)
    pair_item = f"simplywings:{p['id']}_wings"
    asm_in = (f"Harnais : {name(p['harness'])} → #{num[p['harness']]} · Cœur : {name(p['core'])} → #{num[p['core']]} · "
              f"Aile G : {name(p['left'])} → #{num[p['left']]} · Aile D : {name(p['right'])} → #{num[p['right']]}")
    rows.append(f"| {len(order) + 1} | {p['name']} `{pair_item}` | `ASM` | — | 1 | {asm_in} | {fmt(asm)} | **{fmt(cum)}** |")

    for x in raw:
        if x in D:
            drop_users.setdefault(x, []).append(p["id"])
    for x in order:
        if x in SHARED:
            shared_users.setdefault(x, []).append(p["id"])

    depth = max(level(x) for x in parts)
    head = heading_of.get(p["id"])
    link = f"[`WINGS.md`](../WINGS.md#{anchor(head)})" if head else "`WINGS.md`"
    ncraft = sum(crafts.values())
    drops = [x for x in raw if x in D]

    out = []
    out.append(f"# Route de craft · {p['name']}")
    out.append("")
    out.append(f"`simplywings:{p['id']}` · **{rar}** · item `{p['id']}_wings` · fiche : {link}")
    out.append("")
    out.append(p["theme"])
    out.append("")
    out.append("Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.")
    out.append("")
    out.append("## En bref")
    out.append("")
    out.append("| | |")
    out.append("| --- | --- |")
    out.append(f"| Profondeur | {depth} niveau{'x' if depth > 1 else ''} de craft avant les pièces (max {LIMIT[rar]} en {RAR_LABEL[rar]}) |")
    out.append(f"| Crafts | {ncraft} en `GRID` + 1 assemblage |")
    out.append(f"| Essences | {fmt(pieces)} (pièces) + {fmt(asm)} (assemblage) = **{fmt(cum)}** |")
    nv = sum(1 for x in raw if x not in D)
    out.append(f"| Matières premières | {len(drops)} types du mod ({sum(raw[x] for x in drops)} objets) · {nv} types vanilla ({sum(q for x, q in raw.items() if x not in D)} objets) |")
    if drops:
        out.append("| Drops du mod | " + ", ".join(f"{name(x)} ({D[x]['src']} · {D[x]['what']})" for x in drops) + " |")
    else:
        out.append("| Drops du mod | Aucun : uniquement des ressources vanilla |")
    out.append("")
    out.append("## Matières premières")
    out.append("")
    out.append("Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.")
    out.append("")
    out.append("| Objet | Qté | Où | Source | Condition | Chance |")
    out.append("| --- | --- | --- | --- | --- | --- |")
    for x in sorted(raw, key=lambda x: (x not in D, name(x))):
        if x in D:
            d = D[x]
            out.append(f"| {name(x)} `{rid(x)}` | {raw[x]} | `{d['src']}` | {d['what']} | {d['cond']} | {d['chance']} |")
        else:
            out.append(f"| {name(x)} `{rid(x)}` | {raw[x]} | `VANILLA` | {V[x][1]} | — | — |")
    out.append("")
    out.append("## Route")
    out.append("")
    out.append("Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.")
    out.append("")
    out.append("| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |")
    out.append("| --- | --- | --- | --- | --- | --- | --- | --- |")
    out.extend(rows)
    out.append("")
    out.append("## Recettes")
    out.append("")
    out.append("Grille 5×5 sans les coins : `.` = coin absent, `_` = case vide.")
    for i in order:
        r = C[i]
        out.append("")
        tag = " · partagé" if i in SHARED else ""
        out.append(f"### #{num[i]} · {r['name']} · `{rid(i)}`{tag}")
        out.append("")
        out.append(f"`GRID` · niveau {level(i)} · essence : {fmt(ess_cost(i))} · donne ×{r['out']}")
        out.append("")
        out.append("```")
        for row in r["rows"]:
            out.append(" ".join(row))
        out.append("```")
        out.append("")
        out.append("| Lettre | Ingrédient | Qté | Niv. |")
        out.append("| --- | --- | --- | --- |")
        cnt = Counter(ch for row in r["rows"] for ch in row if ch not in "._")
        for k, x in r["key"].items():
            out.append(f"| `{k}` | {name(x)} `{rid(x)}` | {cnt[k]} | {level(x)} |")
    out.append("")
    out.append("## Pourquoi ces ingrédients")
    out.append("")
    for w in p["why"]:
        out.append(f"- {w}")
    out.append("")
    (OUT / f"{p['id']}.md").write_text("\n".join(out), encoding="utf-8", newline="\n")
    RATIO[p['id']] = (sum(1 for x in raw if x in D), sum(1 for x in raw if x not in D), sum(q for x, q in raw.items() if x in D), sum(q for x, q in raw.items() if x not in D), sorted(x for x in raw if x not in D))
    summary.append((p, depth, ncraft, pieces, asm, cum))


for p in PAIRS:
    build(p)

if errors:
    print("\n".join(errors))
    sys.exit(1)

rec = ["| Paire | Id | Rareté | Profondeur | Crafts `GRID` | Pièces | Assemblage | Total | Route |", "| --- | --- | --- | --- | --- | --- | --- | --- | --- |",
       "| Common Wings | `common` | Commune | 0 | 0 | 0 | 0 | **0** | ci-dessous |"]
for p, depth, nc, pieces, asm, cum in summary:
    rec.append(f"| {p['name']} | `{p['id']}` | {p['rarity']} | {depth} / {LIMIT[p['rarity']]} | {nc} | {fmt(pieces)} | {fmt(asm)} | **{fmt(cum)}** | [`crafting/{p['id']}.md`](crafting/{p['id']}.md) |")
(Path(__file__).parent / "_recap.txt").write_text("\n".join(rec), encoding="utf-8")

reg = ["| Drop | Id | Où | Source | Condition | Chance | Paires |", "| --- | --- | --- | --- | --- | --- | --- |"]
for x, d in D.items():
    users = ", ".join(f"`{u}`" for u in drop_users.get(x, []))
    if not users:
        print("unused drop", x)
    reg.append(f"| {d['name']} | `simplywings:{x}` | `{d['src']}` | {d['what']} | {d['cond']} | {d['chance']} | {users} |")
(Path(__file__).parent / "_registry.txt").write_text("\n".join(reg), encoding="utf-8")

sh = ["| Objet | Id | Niv. | Entrées | Essence | Donne | Paires |", "| --- | --- | --- | --- | --- | --- | --- |"]
for x in sorted(SHARED, key=lambda x: (level(x), C[x]["name"])):
    ins = ", ".join(f"{name(y)} ×{n}" for y, n in inputs(x).items())
    users = ", ".join(f"`{u}`" for u in shared_users.get(x, []))
    sh.append(f"| {C[x]['name']} | `simplywings:{x}` | {level(x)} | {ins} | {fmt(ess_cost(x))} | ×{C[x]['out']} | {users} |")
(Path(__file__).parent / "_shared.txt").write_text("\n".join(sh), encoding="utf-8")
for p, depth, nc, pieces, asm, cum in summary:
    c, v, cq, vq, vl = RATIO[p["id"]]
    print(f"{p['id']:12} d{depth} crafts {nc:2} ess {sum(cum.values()):5} types custom {c:2} / vanilla {v:2}  qty {cq:3}/{vq:3}  {' '.join(vl)}")
