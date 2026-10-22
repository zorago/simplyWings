import gzip
import json
import math
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "src/main/resources/data/simplywings/structure/wing_forge.nbt"
LOOT = ROOT / "src/main/resources/data/simplywings/loot_table/chests/forge_supplies.json"
DATA_VERSION = 3955
SIZE = (15, 8, 15)


class Int(int):
    pass


class Byte(int):
    pass


def tag_id(v):
    if isinstance(v, Byte):
        return 1
    if isinstance(v, (Int, int)):
        return 3
    if isinstance(v, str):
        return 8
    if isinstance(v, list):
        return 9
    if isinstance(v, dict):
        return 10
    raise TypeError(v)


def payload(v):
    t = tag_id(v)
    if t == 1:
        return struct.pack(">b", v)
    if t == 3:
        return struct.pack(">i", v)
    if t == 8:
        b = v.encode("utf-8")
        return struct.pack(">H", len(b)) + b
    if t == 9:
        inner = tag_id(v[0]) if v else 0
        return struct.pack(">bi", inner, len(v)) + b"".join(payload(x) for x in v)
    out = b""
    for k, x in v.items():
        kb = k.encode("utf-8")
        out += struct.pack(">bH", tag_id(x), len(kb)) + kb + payload(x)
    return out + b"\x00"


def build():
    blocks = {}

    def put(x, y, z, name, props=None, nbt=None):
        blocks[(x, y, z)] = (name, tuple(sorted((props or {}).items())), nbt)

    c = 7
    for x in range(15):
        for z in range(15):
            d = math.hypot(x - c, z - c)
            if d <= 7.2:
                put(x, 0, z, "minecraft:purpur_block" if d > 6.2 else "minecraft:end_stone_bricks")
                if d <= 5.5:
                    put(x, 1, z, "minecraft:purpur_block" if (x + z) % 2 == 0 else "minecraft:end_stone_bricks")
    for x in range(4, 11):
        for z in range(4, 11):
            put(x, 1, z, "minecraft:polished_blackstone_bricks" if abs(x - c) + abs(z - c) > 2 else "minecraft:obsidian")
    for px, pz in ((3, 3), (3, 11), (11, 3), (11, 11)):
        for yy in range(2, 7):
            put(px, yy, pz, "minecraft:purpur_pillar", {"axis": "y"})
        put(px, 7, pz, "minecraft:end_rod", {"facing": "up"})
    for i in range(3, 12):
        for px, pz in ((i, 3), (i, 11), (3, i), (11, i)):
            if (px, 6, pz) not in blocks:
                put(px, 6, pz, "minecraft:purpur_slab", {"type": "top", "waterlogged": "false"})
    for px, pz in ((5, 5), (5, 9), (9, 5), (9, 9)):
        put(px, 5, pz, "minecraft:end_rod", {"facing": "down"})
    put(c, 2, c, "simplywings:assembly_table", {"facing": "south"})
    barrel = {"id": "minecraft:barrel", "LootTable": "simplywings:chests/forge_supplies"}
    for bx, bz in ((4, 7), (10, 7), (7, 4), (7, 10), (5, 5), (9, 9)):
        put(bx, 2, bz, "minecraft:barrel", {"facing": "up", "open": "false"}, dict(barrel))
    palette, index = [], {}
    entries = []
    for (x, yy, z), (name, props, nbt) in sorted(blocks.items()):
        key = (name, props)
        if key not in index:
            index[key] = len(palette)
            state = {"Name": name}
            if props:
                state["Properties"] = {k: v for k, v in props}
            palette.append(state)
        entry = {"pos": [Int(x), Int(yy), Int(z)], "state": Int(index[key])}
        if nbt:
            entry["nbt"] = nbt
        entries.append(entry)
    return {
        "DataVersion": Int(DATA_VERSION),
        "size": [Int(s) for s in SIZE],
        "palette": palette,
        "blocks": entries,
        "entities": [],
    }


def write():
    root = build()
    data = struct.pack(">bH", 10, 0) + payload(root)
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_bytes(gzip.compress(data))
    LOOT.parent.mkdir(parents=True, exist_ok=True)
    LOOT.write_text(json.dumps({
        "type": "minecraft:chest",
        "pools": [{
            "rolls": 3,
            "entries": [
                {"type": "minecraft:item", "name": "minecraft:feather", "weight": 3,
                 "functions": [{"function": "minecraft:set_count", "count": {"min": 6, "max": 16}}]},
                {"type": "minecraft:item", "name": "minecraft:leather", "weight": 2,
                 "functions": [{"function": "minecraft:set_count", "count": {"min": 3, "max": 8}}]},
                {"type": "minecraft:item", "name": "minecraft:string", "weight": 1,
                 "functions": [{"function": "minecraft:set_count", "count": {"min": 2, "max": 6}}]},
            ],
        }],
    }, indent=2) + "\n", encoding="utf-8", newline="\n")
    return len(root["blocks"])


if __name__ == "__main__":
    print(write(), "blocks")
