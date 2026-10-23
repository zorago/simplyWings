import json
import re
import subprocess
import sys
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8")
root = Path(__file__).resolve().parents[2]
RECIPES = root / "src" / "main" / "resources" / "data" / "simplywings" / "recipe"
SEND = [sys.executable, "-I", str(root / "tools" / "studio" / "send.py")]
FORGE = (0, 140, 2)
TABLE = (4, 140, 2)
KINDS = ["cloud", "steam", "ethereal", "intensified"]
FLASK = 0
HARNESS, CORE, LEFT, RIGHT, OUTPUT = 1, 2, 3, 4, 5
GRID, RESULT = 2, 23
INV = {"forge": 24, "table": 6}


def flask(kind):
    return f'simplywings:cloud_flask[simplywings:essence={{type:"{kind}",amount:100}},max_stack_size=1]'


def send(*cmds):
    return subprocess.run(SEND + [";".join(cmds)], capture_output=True, text=True, encoding="utf-8").stdout.splitlines()


def menu_slot(bench, index):
    base = INV[bench]
    return base + 27 + index if index < 9 else base + index - 9


def inventory():
    for line in send("inv"):
        if line.startswith("inv"):
            return {int(i): (k, int(n)) for i, k, n in re.findall(r"(\d+)=([a-z_]+:[a-z_]+)x(\d+)", line)}
    return {}


def menu(limit=24):
    for line in send(f"menu {limit}"):
        if line.startswith("menu "):
            return line
    return ""


def cost(data):
    e = data.get("essence", 0)
    if isinstance(e, int):
        return {"cloud": e} if e else {}
    return {k: v for k, v in e.items() if v}


def gauges(bench):
    x, y, z = FORGE if bench == "forge" else TABLE
    out = {}
    for line in send(*[f"cmd data get block {x} {y} {z} Essence_{k}" for k in KINDS], "wait 3"):
        m = re.search(r"has the following block data: (\d+)", line)
        if m:
            out[KINDS[len(out)]] = int(m.group(1))
    return out


def item_id(key):
    if "item" in key:
        return key["item"]
    raise ValueError(f"unsupported ingredient {key}")


def cells(pattern):
    h, w = len(pattern), max(len(r) for r in pattern)
    oy, ox = (5 - h) // 2, (5 - w) // 2
    pattern = [" " * 5] * oy + [(" " * ox + r).ljust(5) for r in pattern] + [" " * 5] * (5 - h - oy)
    out = []
    cell = 0
    for y in range(5):
        for x in range(5):
            if (x in (0, 4)) and (y in (0, 4)):
                continue
            if pattern[y][x] != " ":
                out.append((GRID + cell, pattern[y][x]))
            cell += 1
    return out


def open_bench(bench):
    x, y, z = FORGE if bench == "forge" else TABLE
    block = "simplywings:forge_bench" if bench == "forge" else "simplywings:assembly_table"
    send("screen close", "cmd gamemode creative", f"cmd setblock {x} {y - 1} {z} minecraft:stone",
         f"cmd tp @s {x} {y} {z - 2} 0 0", "cmd clear @s", "select 8", "wait 5", f"useon {x} {y} {z}", "wait 8")
    if bench == "forge" and not menu().startswith("menu ForgeBench") or bench == "table" and not menu().startswith("menu AssemblyTable"):
        send(f"cmd setblock {x} {y} {z} {block}", "wait 3", f"useon {x} {y} {z}", "wait 8")


def reset(bench):
    x, y, z = FORGE if bench == "forge" else TABLE
    block = "simplywings:forge_bench" if bench == "forge" else "simplywings:assembly_table"
    send("screen close", f"cmd setblock {x} {y} {z} minecraft:air", f"cmd setblock {x} {y} {z} {block}", "wait 3")
    open_bench(bench)


def refill(bench, needed):
    level = gauges(bench)
    for kind, amount in needed.items():
        while level.get(kind, 0) < amount:
            send(f"cmd give @s {flask(kind)}", "wait 2")
            inv = inventory()
            idx = next(i for i, (k, n) in inv.items() if k == "simplywings:cloud_flask")
            send(f"slot {menu_slot(bench, idx)} 0 pickup", f"slot {FLASK} 0 pickup", "wait 6", f"slot {FLASK} 0 quick_move", "wait 2",
                 "cmd clear @s simplywings:cloud_flask")
            level = gauges(bench)


def place(bench, items):
    need = {}
    for _, item in items:
        need[item] = need.get(item, 0) + 1
    send(*[f"cmd give @s {item} {n}" for item, n in need.items()], "wait 3")
    inv = inventory()
    where = {}
    for i, (k, n) in inv.items():
        where.setdefault(k, i)
    clicks = []
    for slot, item in items:
        src = menu_slot(bench, where[item])
        clicks += [f"slot {src} 0 pickup", f"slot {slot} 1 pickup", f"slot {src} 0 pickup"]
    send(*clicks, "wait 3")


def spent(before, after):
    return {k: before.get(k, 0) - after.get(k, 0) for k in KINDS if before.get(k, 0) != after.get(k, 0)}


def clear_grid():
    send(*[f"slot {GRID + i} 0 quick_move" for i in range(21)], f"slot {RESULT} 0 quick_move", "wait 3", "cmd clear @s")


def craft_component(name, shift=False):
    path = RECIPES / "component" / f"{name}.json"
    if not path.exists():
        path = RECIPES / f"{name}.json"
    data = json.loads(path.read_text(encoding="utf-8"))
    send("button 0", "wait 3")
    place("forge", [(slot, item_id(data["key"][ch])) for slot, ch in cells(data["pattern"])])
    before = menu()
    send(f"slot {RESULT} 0 {'quick_move' if shift else 'pickup'}", "wait 4")
    if not shift:
        inv = inventory()
        free = next(i for i in range(9, 36) if i not in inv)
        send(f"slot {menu_slot('forge', free)} 0 pickup", "wait 2")
    return data, before, menu()


def craft_assembly(name):
    path = RECIPES / "assembly" / f"{name}.json"
    data = json.loads((path if path.exists() else RECIPES / f"{name}.json").read_text(encoding="utf-8"))
    place("table", [(HARNESS, item_id(data["harness"])), (CORE, item_id(data["core"])),
                    (LEFT, item_id(data["left_wing"])), (RIGHT, item_id(data["right_wing"]))])
    send("button 0", "wait 100")
    out = menu()
    send(f"slot {OUTPUT} 0 quick_move", "wait 3")
    return data, out


def sweep():
    bad = []
    reset("forge")
    for path in sorted((RECIPES / "component").glob("*.json")) + [RECIPES / "cloud_flask.json"]:
        name = path.stem
        data = json.loads(path.read_text(encoding="utf-8"))
        clear_grid()
        refill("forge", cost(data))
        g0 = gauges("forge")
        _, before, after = craft_component(name)
        rid, count = data["result"]["id"], data["result"].get("count", 1)
        got = sum(n for k, n in inventory().values() if k == rid)
        used = spent(g0, gauges("forge"))
        ok = f"{RESULT}={rid}x{count}" in before and got == count and used == cost(data)
        line = f"{'OK ' if ok else 'BAD'} component {name}: result {got}/{count}, spent {used} (cost {cost(data)})"
        print(line, flush=True)
        if not ok:
            bad.append(line + " | " + before[:300])
    reset("table")
    for path in sorted((RECIPES / "assembly").glob("*.json")) + [RECIPES / "common_wings.json"]:
        data = json.loads(path.read_text(encoding="utf-8"))
        send("cmd clear @s")
        refill("table", cost(data))
        g0 = gauges("table")
        _, out = craft_assembly(path.stem)
        rid = data["result"]["id"]
        got = sum(n for k, n in inventory().values() if k == rid)
        used = spent(g0, gauges("table"))
        ok = got == 1 and used == cost(data)
        line = f"{'OK ' if ok else 'BAD'} assembly {path.stem}: result {got}, spent {used} (cost {cost(data)})"
        print(line, flush=True)
        if not ok:
            bad.append(line + " | " + out[:300])
    print(f"{len(bad)} failures")
    for b in bad:
        print(b)


if __name__ == "__main__":
    mode = sys.argv[1]
    if mode == "all":
        sweep()
    elif mode == "one":
        reset("forge")
        data = json.loads((RECIPES / "component" / f"{sys.argv[2]}.json").read_text(encoding="utf-8"))
        refill("forge", cost(data))
        g0 = gauges("forge")
        data, before, after = craft_component(sys.argv[2])
        print("before:", before)
        print("after:", after, "spent:", spent(g0, gauges("forge")))
    elif mode == "assembly":
        reset("table")
        for name in sys.argv[2:]:
            data = json.loads((RECIPES / "assembly" / f"{name}.json").read_text(encoding="utf-8"))
            refill("table", cost(data))
            data, out = craft_assembly(name)
            print(name, "| out:", out, "| gauges:", gauges("table"))
    print("inventory:", inventory())
