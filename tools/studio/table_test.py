import json
import re
import subprocess
import sys
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8")
root = Path(__file__).resolve().parents[2]
RECIPES = root / "src" / "main" / "resources" / "data" / "simplywings" / "recipe"
SEND = [sys.executable, "-I", str(root / "tools" / "studio" / "send.py")]
TABLE = (0, 140, 2)
FLASK, HARNESS, CORE, LEFT, RIGHT, OUTPUT, ENERGY, GRID, RESULT, INV = 0, 1, 2, 3, 4, 5, 6, 7, 28, 29
FULL = "simplywings:cloud_flask[simplywings:cloud_essence=100,max_stack_size=1]"


def send(*cmds):
    return subprocess.run(SEND + [";".join(cmds)], capture_output=True, text=True, encoding="utf-8").stdout.splitlines()


def menu_slot(index):
    return INV + 27 + index if index < 9 else INV + index - 9


def inventory():
    for line in send("inv"):
        if line.startswith("inv"):
            return {int(i): (k, int(n)) for i, k, n in re.findall(r"(\d+)=([a-z_]+:[a-z_]+)x(\d+)", line)}
    return {}


def menu(limit=29):
    for line in send(f"menu {limit}"):
        if line.startswith("menu "):
            return line
    return ""


def tank():
    x, y, z = TABLE
    for line in send(f"cmd data get block {x} {y} {z} Tank", "wait 3"):
        m = re.search(r"has the following block data: (\d+)", line)
        if m:
            return int(m.group(1))
    return None


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
            corner = (x in (0, 4)) and (y in (0, 4))
            if corner:
                continue
            ch = pattern[y][x]
            if ch != " ":
                out.append((GRID + cell, ch))
            cell += 1
    return out


def open_table(essence_flasks=0):
    x, y, z = TABLE
    cmds = ["screen close", "cmd gamemode creative", f"cmd setblock {x} {y - 1} {z} minecraft:stone",
            f"cmd setblock {x} {y} {z} minecraft:air", f"cmd setblock {x} {y} {z} simplywings:assembly_table",
            f"cmd tp @s {x} {y} {z - 2} 0 0", "cmd clear @s", "select 8", "wait 5", f"useon {x} {y} {z}", "wait 8"]
    send(*cmds)
    for _ in range(essence_flasks):
        send(f"cmd give @s {FULL}", "wait 3")
        inv = inventory()
        idx = next(i for i, (k, n) in inv.items() if k == "simplywings:cloud_flask")
        send(f"slot {menu_slot(idx)} 0 pickup", f"slot {FLASK} 0 pickup", "wait 10", f"slot {FLASK} 0 quick_move", "wait 3")
    send("cmd clear @s simplywings:cloud_flask")


def place(items):
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
        src = menu_slot(where[item])
        clicks += [f"slot {src} 0 pickup", f"slot {slot} 1 pickup", f"slot {src} 0 pickup"]
    send(*clicks, "wait 3")


def refill(needed):
    level = tank() or 0
    while level < needed:
        send(f"cmd give @s {FULL}", "wait 2")
        inv = inventory()
        idx = next(i for i, (k, n) in inv.items() if k == "simplywings:cloud_flask")
        send(f"slot {menu_slot(idx)} 0 pickup", f"slot {FLASK} 0 pickup", "wait 6", f"slot {FLASK} 0 quick_move", "wait 2",
             "cmd clear @s simplywings:cloud_flask")
        level = tank() or 0


def sweep():
    bad = []
    names = sorted(p.stem for p in (RECIPES / "component").glob("*.json"))
    for name in names:
        data = json.loads((RECIPES / "component" / f"{name}.json").read_text(encoding="utf-8"))
        clear_grid()
        refill(data["essence"])
        t0 = tank()
        _, before, after = craft_component(name)
        rid, count = data["result"]["id"], data["result"].get("count", 1)
        inv = inventory()
        got = sum(n for k, n in inv.values() if k == rid)
        t1 = tank()
        ok = f"28={rid}x{count}" in before and got == count and (t0 - t1) == data["essence"] and "| 7=" not in after.replace("| 28", "")
        line = f"{'OK ' if ok else 'BAD'} component {name}: result {got}/{count}, essence {t0}->{t1} (cost {data['essence']})"
        print(line, flush=True)
        if not ok:
            bad.append(line + " | " + before[:300])
    for p in sorted((RECIPES / "assembly").glob("*.json")):
        data = json.loads(p.read_text(encoding="utf-8"))
        send("cmd clear @s")
        refill(data["essence"])
        t0 = tank()
        _, out = craft_assembly(p.stem)
        rid = data["result"]["id"]
        inv = inventory()
        got = sum(n for k, n in inv.values() if k == rid)
        t1 = tank()
        ok = got == 1 and (t0 - t1) == data["essence"]
        line = f"{'OK ' if ok else 'BAD'} assembly {p.stem}: result {got}, essence {t0}->{t1} (cost {data['essence']})"
        print(line, flush=True)
        if not ok:
            bad.append(line + " | " + out[:300])
    print(f"{len(bad)} failures")
    for b in bad:
        print(b)


def clear_grid():
    send(*[f"slot {GRID + i} 0 quick_move" for i in range(21)], f"slot {RESULT} 0 quick_move", "wait 3", "cmd clear @s")


def craft_component(name, shift=False):
    path = RECIPES / "component" / f"{name}.json"
    if not path.exists():
        path = RECIPES / f"{name}.json"
    data = json.loads(path.read_text(encoding="utf-8"))
    send("button 1", "wait 3")
    items = [(slot, item_id(data["key"][ch])) for slot, ch in cells(data["pattern"])]
    place(items)
    before = menu()
    send(f"slot {RESULT} 0 {'quick_move' if shift else 'pickup'}", "wait 4")
    if not shift:
        inv = inventory()
        free = next(i for i in range(9, 36) if i not in inv)
        send(f"slot {menu_slot(free)} 0 pickup", "wait 2")
    after = menu()
    return data, before, after


def craft_assembly(name):
    data = json.loads((RECIPES / "assembly" / f"{name}.json").read_text(encoding="utf-8"))
    send("button 0", "wait 3")
    place([(HARNESS, item_id(data["harness"])), (CORE, item_id(data["core"])),
           (LEFT, item_id(data["left_wing"])), (RIGHT, item_id(data["right_wing"]))])
    send("button 2", "wait 100")
    out = menu()
    send(f"slot {OUTPUT} 0 quick_move", "wait 3")
    return data, out


if __name__ == "__main__":
    mode = sys.argv[1]
    if mode == "open":
        open_table(int(sys.argv[2]) if len(sys.argv) > 2 else 0)
        print("tank:", tank())
    elif mode == "one":
        clear_grid()
        data = json.loads((RECIPES / "component" / f"{sys.argv[2]}.json").read_text(encoding="utf-8"))
        refill(data["essence"])
        data, before, after = craft_component(sys.argv[2])
        print("before:", before)
        print("after:", after, "tank:", tank())
    elif mode == "all":
        open_table()
        sweep()
    elif mode == "component":
        for name in sys.argv[2:]:
            data, before, after = craft_component(name)
            print(name, "| before:", before, "| after:", after, "| tank:", tank())
    elif mode == "assembly":
        for name in sys.argv[2:]:
            data, out = craft_assembly(name)
            print(name, "| out:", out, "| tank:", tank())
    print("inventory:", inventory())
