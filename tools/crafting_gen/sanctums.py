import gzip
import json
import math
import random
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
STRUCTURES = ROOT / "src/main/resources/data/simplywings/structure"
LOOT = ROOT / "src/main/resources/data/simplywings/loot_table/chests/forge_supplies.json"
DATA_VERSION = 3955
W = 57
C = W // 2
TOP = 18
H = 52


class Int(int):
    pass


def tag_id(v):
    if isinstance(v, int):
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


class Noise:
    def __init__(self, rnd, cell):
        self.cell = cell
        self.grid = {}
        self.rnd = rnd

    def at(self, i, j):
        if (i, j) not in self.grid:
            self.grid[(i, j)] = self.rnd.uniform(-1, 1)
        return self.grid[(i, j)]

    def __call__(self, x, z):
        fx, fz = x / self.cell, z / self.cell
        i, j = math.floor(fx), math.floor(fz)
        tx, tz = fx - i, fz - j
        tx, tz = tx * tx * (3 - 2 * tx), tz * tz * (3 - 2 * tz)
        a = self.at(i, j) + (self.at(i + 1, j) - self.at(i, j)) * tx
        b = self.at(i, j + 1) + (self.at(i + 1, j + 1) - self.at(i, j + 1)) * tx
        return a + (b - a) * tz


class Build:
    def __init__(self, seed, size=(W, H, W)):
        self.rnd = random.Random(seed)
        self.blocks = {}
        self.surface = {}
        self.size = size

    def put(self, x, y, z, name, props=None, nbt=None, replace=True):
        sx, sy, sz = self.size
        if not (0 <= x < sx and 0 <= y < sy and 0 <= z < sz):
            return
        if not replace and (x, y, z) in self.blocks:
            return
        if name is None:
            self.blocks.pop((x, y, z), None)
            return
        self.blocks[(x, y, z)] = (name, tuple(sorted((props or {}).items())), nbt)

    def get(self, x, y, z):
        b = self.blocks.get((x, y, z))
        return b[0] if b else None

    def top(self, x, z):
        return self.surface.get((x, z))

    def island(self, surface, soil, core, accents, arm_len=(21, 26)):
        rnd = self.rnd
        edge = Noise(rnd, 6)
        fine = Noise(rnd, 2.5)
        arms = [rnd.randint(*arm_len) for _ in range(4)]
        widths = [rnd.uniform(6.5, 9.0) for _ in range(4)]
        mask = set()
        for x in range(W):
            for z in range(W):
                dx, dz = x - C, z - C
                n = edge(x, z) * 2.2 + fine(x, z) * 0.8
                inside = math.hypot(dx, dz) <= 11.5 + n
                for k, (ax, az) in enumerate(((1, 0), (-1, 0), (0, 1), (0, -1))):
                    along = dx * ax + dz * az
                    across = abs(dx * az - dz * ax)
                    if 0 <= along <= arms[k] + n:
                        hw = widths[k] * (1 - 0.6 * along / arms[k]) + n * 0.8
                        if across <= hw:
                            inside = True
                if inside:
                    mask.add((x, z))
        dist = {}
        frontier = [p for p in mask if any((p[0] + a, p[1] + b) not in mask for a, b in ((1, 0), (-1, 0), (0, 1), (0, -1)))]
        for p in frontier:
            dist[p] = 1
        while frontier:
            nxt = []
            for (x, z) in frontier:
                for a, b in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    q = (x + a, z + b)
                    if q in mask and q not in dist:
                        dist[q] = dist[(x, z)] + 1
                        nxt.append(q)
            frontier = nxt
        under = Noise(rnd, 3)
        for (x, z) in mask:
            d = dist[(x, z)]
            hill = max(0.0, edge(x * 1.7 + 40, z * 1.7) * 1.4) if d > 3 else 0
            top_y = TOP + int(round(hill))
            depth = min(TOP - 1, int(1 + d * 1.25 + under(x, z) * 2.5 + (rnd.random() < 0.06) * rnd.randint(2, 5)))
            for y in range(top_y - depth, top_y + 1):
                if y == top_y:
                    name = surface(x, z, d)
                elif y >= top_y - 2:
                    name = soil(x, z, d)
                else:
                    name = core if rnd.random() > 0.12 else rnd.choice(accents)
                self.put(x, y, z, name)
            self.surface[(x, z)] = top_y

    def scatter(self, picks, chance, avoid=None):
        for (x, z), y in list(self.surface.items()):
            if avoid and avoid(x, z):
                continue
            if self.get(x, y + 1, z) is None and self.rnd.random() < chance:
                name, props = self.rnd.choice(picks)
                if props == "double":
                    self.put(x, y + 1, z, name, {"half": "lower"})
                    self.put(x, y + 2, z, name, {"half": "upper"})
                else:
                    self.put(x, y + 1, z, name, props)

    def tree(self, x, z, log, leaves, height, radius, extra_leaves=None):
        y0 = self.top(x, z)
        if y0 is None:
            return
        for y in range(y0 + 1, y0 + height + 1):
            self.put(x, y, z, log, {"axis": "y"})
        cy = y0 + height
        for dx in range(-radius, radius + 1):
            for dz in range(-radius, radius + 1):
                for dy in range(-1, radius):
                    r = math.sqrt(dx * dx + dz * dz + (dy * 1.4) ** 2)
                    if r <= radius + self.rnd.uniform(-0.6, 0.3) and self.get(x + dx, cy + dy, z + dz) is None:
                        leaf = extra_leaves if extra_leaves and self.rnd.random() < 0.35 else leaves
                        self.put(x + dx, cy + dy, z + dz, leaf, {"persistent": "true", "distance": "1", "waterlogged": "false"})

    def hanging(self, name, chance, length=(1, 4), props=None):
        bottoms = {}
        for (x, y, z) in self.blocks:
            if (x, z) not in bottoms or y < bottoms[(x, z)]:
                bottoms[(x, z)] = y
        for (x, z), y in bottoms.items():
            if self.rnd.random() < chance:
                for k in range(1, self.rnd.randint(*length) + 1):
                    self.put(x, y - k, z, name, props, replace=False)

    def ruin(self, keep, chance_at, debris):
        removed = []
        for pos, (name, props, nbt) in list(self.blocks.items()):
            if keep(pos, name):
                continue
            if self.rnd.random() < chance_at(pos):
                del self.blocks[pos]
                removed.append(pos)
        for (x, y, z) in removed:
            if self.rnd.random() < 0.08:
                gx, gz = x + self.rnd.randint(-2, 2), z + self.rnd.randint(-2, 2)
                gy = self.top(gx, gz)
                if gy is not None and self.get(gx, gy + 1, gz) is None:
                    name, props = self.rnd.choice(debris)
                    self.put(gx, gy + 1, gz, name, props)

    def nbt(self):
        palette, index, entries = [], {}, []
        for (x, y, z), (name, props, nbt) in sorted(self.blocks.items()):
            key = (name, props)
            if key not in index:
                index[key] = len(palette)
                state = {"Name": name}
                if props:
                    state["Properties"] = {k: v for k, v in props}
                palette.append(state)
            entry = {"pos": [Int(x), Int(y), Int(z)], "state": Int(index[key])}
            if nbt:
                entry["nbt"] = nbt
            entries.append(entry)
        sx, sy, sz = self.size
        return {"DataVersion": Int(DATA_VERSION), "size": [Int(sx), Int(sy), Int(sz)],
                "palette": palette, "blocks": entries, "entities": []}


def disc(b, cx, cz, r, y, name, props=None):
    for x in range(cx - r - 1, cx + r + 2):
        for z in range(cz - r - 1, cz + r + 2):
            if math.hypot(x - cx, z - cz) <= r + 0.3:
                b.put(x, y, z, name, props)


def front_open(x, z, half_angle=34):
    a = math.degrees(math.atan2(x - C, z - C))
    return abs(a) < half_angle


def sanctum():
    b = Build(0x5A4C)
    moss = Noise(b.rnd, 5)

    def surface(x, z, d):
        n = moss(x, z)
        return "minecraft:moss_block" if n > 0.15 else "minecraft:calcite" if n < -0.55 else "minecraft:end_stone"

    b.island(surface, lambda x, z, d: "minecraft:end_stone", "minecraft:end_stone",
             ["minecraft:calcite", "minecraft:purpur_block", "minecraft:end_stone_bricks"])
    y = TOP + 1
    disc(b, C, C, 10, TOP, "minecraft:smooth_quartz")
    disc(b, C, C, 9, y, "minecraft:quartz_bricks")
    disc(b, C, C, 3, y + 1, "minecraft:purpur_block")
    disc(b, C, C, 2, y + 1, "minecraft:chiseled_quartz_block")
    for k in range(12):
        a = k * math.pi * 2 / 12 + math.pi / 12
        px, pz = C + round(math.sin(a) * 8), C + round(math.cos(a) * 8)
        if front_open(px, pz, 20):
            continue
        hgt = 10
        for yy in range(y + 1, y + 1 + hgt):
            b.put(px, yy, pz, "minecraft:quartz_pillar", {"axis": "y"})
        b.put(px, y + 1 + hgt, pz, "minecraft:chiseled_quartz_block")
        b.put(px, y + 2 + hgt, pz, "minecraft:end_rod", {"facing": "up"})
    for k in range(64):
        a = k * math.pi * 2 / 64
        px, pz = C + round(math.sin(a) * 8), C + round(math.cos(a) * 8)
        if not front_open(px, pz, 16):
            b.put(px, y + 11, pz, "minecraft:smooth_quartz_slab", {"type": "bottom", "waterlogged": "false"}, replace=False)
    b.put(C, y + 2, C, "simplywings:assembly_table", {"facing": "south"})
    for dx, dz in ((-2, -2), (2, -2), (-2, 2), (2, 2)):
        b.put(C + dx, y + 2, C + dz, "minecraft:end_rod", {"facing": "up"})
    for z in range(C + 10, C + 24):
        for x in range(C - 1, C + 2):
            ty = b.top(x, z)
            if ty is not None and b.rnd.random() < 0.85:
                b.put(x, ty, z, "minecraft:quartz_bricks" if x == C else "minecraft:smooth_quartz")
    shell = [("minecraft:end_rod", {"facing": "up"}), ("minecraft:white_stained_glass_pane", {}),
             ("minecraft:glass_pane", {}), ("minecraft:light_blue_stained_glass_pane", {}),
             ("minecraft:white_stained_glass", None), ("minecraft:end_rod", {"facing": "down"}),
             ("minecraft:purple_stained_glass_pane", {})]
    cy = y + 6
    for _ in range(5200):
        u, v = b.rnd.uniform(-1, 1), b.rnd.uniform(0, math.pi * 2)
        r = 15 + b.rnd.uniform(-1.8, 1.8)
        s = math.sqrt(1 - u * u)
        x, z = round(C + r * s * math.cos(v)), round(C + r * s * math.sin(v))
        yy = round(cy + u * 12)
        if yy <= y or front_open(x, z, 30) and yy < cy + 9:
            continue
        ty = b.top(x, z)
        if ty is not None and yy <= ty + 1:
            continue
        if b.rnd.random() < 0.42:
            name, props = b.rnd.choice(shell)
            b.put(x, yy, z, name, props, replace=False)
    b.ruin(lambda pos, name: name.startswith("simplywings:") or pos[1] <= y + 1 or "glass" in name or "end_rod" in name,
           lambda pos: 0.0 if pos[1] < y + 4 else 0.08 + (0.35 if pos[0] < C - 2 and pos[2] < C else 0.0) * (pos[1] - y) / 12,
           [("minecraft:quartz_slab", {"type": "bottom", "waterlogged": "false"}), ("minecraft:quartz_bricks", None),
            ("minecraft:quartz_stairs", {"facing": "east", "half": "bottom", "shape": "straight", "waterlogged": "false"})])
    avoid = lambda x, z: math.hypot(x - C, z - C) < 11 or abs(x - C) <= 1 and z > C
    for _ in range(9):
        x, z = b.rnd.randrange(W), b.rnd.randrange(W)
        if b.top(x, z) is not None and not avoid(x, z) and math.hypot(x - C, z - C) > 13:
            b.tree(x, z, "minecraft:birch_log", "minecraft:azalea_leaves", b.rnd.randint(3, 5), 2, "minecraft:flowering_azalea_leaves")
    b.scatter([("minecraft:lily_of_the_valley", None), ("minecraft:oxeye_daisy", None), ("minecraft:white_tulip", None),
               ("minecraft:moss_carpet", None), ("minecraft:azure_bluet", None), ("minecraft:short_grass", None),
               ("minecraft:lilac", "double")], 0.22, lambda x, z: avoid(x, z) or b.get(x, b.top(x, z), z) != "minecraft:moss_block")
    b.hanging("minecraft:end_rod", 0.03, (1, 1), {"facing": "down"})
    return b, {"center": [C, TOP, C]}


def forge():
    b = Build(0xF0E9)
    patch = Noise(b.rnd, 4)

    def surface(x, z, d):
        n = patch(x, z)
        if d <= 1:
            return "minecraft:cobblestone" if n > 0 else "minecraft:stone"
        return "minecraft:grass_block" if n > -0.2 else "minecraft:coarse_dirt" if n > -0.6 else "minecraft:podzol"

    b.island(surface, lambda x, z, d: "minecraft:dirt" if d > 1 else "minecraft:stone", "minecraft:stone",
             ["minecraft:andesite", "minecraft:tuff", "minecraft:coal_ore", "minecraft:gravel", "minecraft:basalt"])
    x0, x1, z0, z1 = C - 7, C + 7, C - 6, C + 5
    y = TOP + 1
    for x in range(x0 - 1, x1 + 2):
        for z in range(z0 - 1, z1 + 2):
            b.put(x, TOP, z, "minecraft:cobblestone")
    for x in range(x0, x1 + 1):
        for z in range(z0, z1 + 1):
            b.put(x, y - 1, z, "minecraft:stone_bricks" if (x + z) % 3 else "minecraft:cracked_stone_bricks")
    wall_h = 6
    for x in range(x0, x1 + 1):
        for z in range(z0, z1 + 1):
            edge = x in (x0, x1) or z in (z0, z1)
            if not edge:
                continue
            for yy in range(y, y + wall_h):
                corner = x in (x0, x1) and z in (z0, z1)
                post = corner or (x in (x0, x1) and (z - z0) % 4 == 0) or (z in (z0, z1) and (x - x0) % 4 == 0)
                if yy < y + 2:
                    name = "minecraft:cobblestone" if b.rnd.random() > 0.25 else "minecraft:mossy_cobblestone"
                elif post:
                    name = "minecraft:stripped_spruce_log"
                elif yy == y + wall_h - 1:
                    name = "minecraft:spruce_log"
                else:
                    name = "minecraft:spruce_planks"
                props = {"axis": "y"} if "log" in name and yy < y + wall_h - 1 else {"axis": "x" if z in (z0, z1) else "z"} if "log" in name else None
                b.put(x, yy, z, name, props)
    for yy in range(y, y + 4):
        for x in range(C - 1, C + 2):
            b.put(x, yy, z1, None)
    for yy in range(y + 2, y + 4):
        for z in (z0 + 3, z1 - 3):
            for x in (x0, x1):
                b.put(x, yy, z, "minecraft:glass_pane")
    ridge = y + wall_h + 4
    for x in range(x0 - 1, x1 + 2):
        for k in range(0, 6):
            yy = y + wall_h - 1 + k
            for side, zz, facing in ((0, z0 - 1 + k, "south"), (1, z1 + 1 - k, "north")):
                b.put(x, yy, zz, "minecraft:spruce_stairs", {"facing": facing, "half": "bottom", "shape": "straight", "waterlogged": "false"})
        b.put(x, ridge + 1, C - 1 + 0, "minecraft:spruce_slab", {"type": "bottom", "waterlogged": "false"})
    for z in range(z0, z1 + 1):
        for k in range(0, 6):
            for x in (x0, x1):
                zz_ok = z0 - 1 + k <= z <= z1 + 1 - k
                if zz_ok and k > 0:
                    b.put(x, y + wall_h - 1 + k, z, "minecraft:spruce_planks")
    cx, cz = x0 + 2, z0 + 2
    for yy in range(y - 1, ridge + 5):
        for x in range(cx - 1, cx + 2):
            for z in range(cz - 1, cz + 2):
                inner = x == cx and z == cz
                if inner and yy > y + 1:
                    continue
                b.put(x, yy, z, "minecraft:bricks" if b.rnd.random() > 0.15 else "minecraft:mud_bricks")
    b.put(cx, ridge + 5, cz, "minecraft:campfire", {"facing": "north", "lit": "true", "signal_fire": "false", "waterlogged": "false"})
    b.put(cx, y, cz, "minecraft:lava_cauldron")
    b.put(cx, y + 1, cz, "minecraft:iron_bars", {"east": "false", "west": "false", "north": "false", "south": "false", "waterlogged": "false"})
    b.put(cx + 2, y, cz, "minecraft:blast_furnace", {"facing": "south", "lit": "true"})
    b.put(cx, y, cz + 2, "minecraft:smoker", {"facing": "east", "lit": "true"})
    b.put(C, y, C, "simplywings:forge_bench", {"facing": "south"})
    b.put(C + 3, y, C - 1, "minecraft:chipped_anvil", {"facing": "east"})
    b.put(C - 3, y, C + 2, "minecraft:damaged_anvil", {"facing": "south"})
    b.put(C + 4, y, C + 2, "minecraft:grindstone", {"face": "floor", "facing": "north"})
    b.put(C + 5, y, C - 4, "minecraft:smithing_table")
    barrel = {"id": "minecraft:barrel", "LootTable": "simplywings:chests/forge_supplies"}
    for bx, bz in ((x1 - 1, z0 + 1), (x1 - 2, z0 + 1), (x1 - 1, z0 + 2), (x0 + 1, z1 - 1), (x1 - 1, z1 - 1)):
        b.put(bx, y, bz, "minecraft:barrel", {"facing": "up", "open": "false"}, dict(barrel))
    for lx, lz in ((C - 3, C - 2), (C + 3, C + 1)):
        for yy in range(y + 3, y + wall_h):
            b.put(lx, yy, lz, "minecraft:chain", {"axis": "y", "waterlogged": "false"})
        b.put(lx, y + 2, lz, "minecraft:lantern", {"hanging": "true", "waterlogged": "false"})
    for x in range(x0 + 1, x1):
        for z in range(z0 + 1, z1):
            if b.get(x, y, z) is None and b.rnd.random() < 0.06:
                b.put(x, y, z, "minecraft:cobblestone_slab", {"type": "bottom", "waterlogged": "false"})
    b.ruin(lambda pos, name: name.startswith("simplywings:") or "barrel" in name or "anvil" in name or pos[1] < y + 2
           or name in ("minecraft:campfire", "minecraft:lava_cauldron", "minecraft:bricks", "minecraft:mud_bricks"),
           lambda pos: 0.05 + (0.75 if pos[0] > C + 1 and pos[1] > y + wall_h - 2 else 0.0) + (0.25 if pos[0] > C + 3 and pos[1] > y + 3 else 0.0),
           [("minecraft:spruce_planks", None), ("minecraft:cobblestone", None),
            ("minecraft:spruce_slab", {"type": "bottom", "waterlogged": "false"}),
            ("minecraft:spruce_stairs", {"facing": "west", "half": "bottom", "shape": "straight", "waterlogged": "false"})])
    for _ in range(14):
        x, z = b.rnd.randrange(W), b.rnd.randrange(W)
        if b.top(x, z) is not None and not (x0 - 3 <= x <= x1 + 3 and z0 - 3 <= z <= z1 + 6):
            b.tree(x, z, "minecraft:spruce_log", "minecraft:spruce_leaves", b.rnd.randint(4, 7), 2)
    avoid = lambda x, z: x0 - 2 <= x <= x1 + 2 and z0 - 2 <= z <= z1 + 2
    b.scatter([("minecraft:fern", None), ("minecraft:short_grass", None), ("minecraft:sweet_berry_bush", {"age": "2"}),
               ("minecraft:large_fern", "double"), ("minecraft:dandelion", None), ("minecraft:mossy_cobblestone_slab", {"type": "bottom", "waterlogged": "false"})],
              0.2, lambda x, z: avoid(x, z) or b.get(x, b.top(x, z), z) not in ("minecraft:grass_block", "minecraft:podzol", "minecraft:coarse_dirt"))
    b.hanging("minecraft:hanging_roots", 0.05, (1, 1), {"waterlogged": "false"})
    return b, {"center": [C, TOP, C]}


def refinery():
    b = Build(0xC10D)
    patch = Noise(b.rnd, 4)

    def surface(x, z, d):
        n = patch(x, z)
        return "minecraft:moss_block" if n > 0.25 else "minecraft:calcite" if n < -0.4 else "minecraft:end_stone"

    b.island(surface, lambda x, z, d: "minecraft:end_stone", "minecraft:end_stone",
             ["minecraft:copper_ore", "minecraft:calcite", "minecraft:smooth_basalt", "minecraft:end_stone_bricks"])
    y = TOP + 1
    copper = ["minecraft:waxed_cut_copper", "minecraft:waxed_exposed_cut_copper", "minecraft:waxed_weathered_cut_copper",
              "minecraft:waxed_oxidized_cut_copper"]
    for x in range(C - 9, C + 10):
        for z in range(C - 9, C + 10):
            if max(abs(x - C), abs(z - C)) <= 9 and abs(x - C) + abs(z - C) <= 15:
                o = min(3, int(math.hypot(x - C, z - C) / 3.5 + b.rnd.random()))
                b.put(x, TOP, z, copper[o])
    for x in range(C - 2, C + 3):
        for z in range(C - 2, C + 3):
            b.put(x, TOP, z, "minecraft:waxed_copper_block")
    for px, py, pz in [(px, py, pz) for px in range(3) for py in range(5) for pz in range(3)]:
        nbt = {"id": "simplywings:cloud_compressor"} if (px, py, pz) == (1, 0, 1) else None
        b.put(C - 1 + px, y + py, C - 1 + pz, "simplywings:cloud_compressor", {"x": str(px), "y": str(py), "z": str(pz)}, nbt)
    for tx, tz in ((C - 7, C - 7), (C + 7, C - 7), (C - 7, C + 7), (C + 7, C + 7)):
        hgt = 12
        for yy in range(y, y + hgt):
            for dx in (0, 1):
                for dz in (0, 1):
                    b.put(tx + dx - (1 if tx > C else 0), yy, tz + dz - (1 if tz > C else 0),
                          "minecraft:waxed_copper_grate" if yy % 4 else "minecraft:waxed_cut_copper")
        b.put(tx, y + hgt, tz, "minecraft:lightning_rod", {"facing": "up", "powered": "false", "waterlogged": "false"})
        b.put(tx, y + hgt - 4, tz + (1 if tz < C else -1), "minecraft:waxed_copper_bulb", {"lit": "true", "powered": "false"})
    for x in range(C - 7, C + 8):
        for z in range(C - 7, C + 8):
            ring = max(abs(x - C), abs(z - C))
            if ring in (6, 7) and y + 11 < H:
                b.put(x, y + 11, z, "minecraft:waxed_cut_copper_slab", {"type": "bottom", "waterlogged": "false"}, replace=False)
            elif ring < 6 and (x + z) % 2 == 0 and ring > 2:
                b.put(x, y + 11, z, "minecraft:light_blue_stained_glass")
    for k, (cx, cz, glass) in enumerate(((C - 4, C - 4, "minecraft:white_stained_glass"),
                                          (C + 4, C - 4, "minecraft:orange_stained_glass"),
                                          (C - 4, C + 4, "minecraft:purple_stained_glass"))):
        b.put(cx, y, cz, "minecraft:waxed_copper_block")
        b.put(cx, y + 1, cz, glass)
        b.put(cx, y + 2, cz, glass)
        b.put(cx, y + 3, cz, "minecraft:waxed_cut_copper_slab", {"type": "bottom", "waterlogged": "false"})
    for x in range(C - 9, C + 10, 3):
        for z in (C - 9, C + 9):
            if b.rnd.random() < 0.7:
                b.put(x, y, z, "minecraft:waxed_copper_grate")
    for z in range(C + 10, C + 24):
        for x in range(C - 1, C + 2):
            ty = b.top(x, z)
            if ty is not None and b.rnd.random() < 0.8:
                b.put(x, ty, z, copper[b.rnd.randint(1, 3)])
    b.ruin(lambda pos, name: name.startswith("simplywings:") or pos[1] <= y,
           lambda pos: 0.04 + (0.5 if pos[1] >= y + 10 and pos[0] > C else 0.0) + (0.2 if pos[2] < C - 5 and pos[1] > y + 6 else 0.0),
           [("minecraft:waxed_oxidized_cut_copper", None), ("minecraft:waxed_copper_grate", None),
            ("minecraft:waxed_cut_copper_slab", {"type": "bottom", "waterlogged": "false"})])
    avoid = lambda x, z: max(abs(x - C), abs(z - C)) <= 10 or abs(x - C) <= 1 and z > C
    for _ in range(8):
        x, z = b.rnd.randrange(W), b.rnd.randrange(W)
        if b.top(x, z) is not None and not avoid(x, z):
            b.tree(x, z, "minecraft:cherry_log", "minecraft:cherry_leaves", b.rnd.randint(3, 5), 2)
    b.scatter([("minecraft:azalea", None), ("minecraft:flowering_azalea", None), ("minecraft:moss_carpet", None),
               ("minecraft:pink_petals", {"facing": "north", "flower_amount": "3"}), ("minecraft:short_grass", None)], 0.22,
              lambda x, z: avoid(x, z) or b.get(x, b.top(x, z), z) != "minecraft:moss_block")
    b.hanging("minecraft:chain", 0.02, (1, 3), {"axis": "y", "waterlogged": "false"})
    return b, {"center": [C, TOP, C]}


def summoning():
    sw, sh = 41, 28
    cx = cz = 20
    floor = 2
    b = Build(0xD5A6, (sw, sh, sw))
    veil = ["minecraft:blackstone", "minecraft:polished_blackstone", "minecraft:obsidian", "minecraft:black_concrete",
            "minecraft:gilded_blackstone", "minecraft:crying_obsidian", "minecraft:polished_blackstone_bricks"]
    weights = [26, 14, 30, 16, 2, 4, 8]
    rough = Noise(b.rnd, 3)
    mid = floor + 8
    for x in range(sw):
        for z in range(sw):
            for y in range(sh):
                dx, dz, dy = x - cx, z - cz, y - mid
                r = math.sqrt(dx * dx + dz * dz + (dy * (1.3 if dy < 0 else 0.8)) ** 2)
                outer = 18.6 + rough(x * 1.3, z * 1.3 + y) * 1.3
                if 14.6 <= r <= outer:
                    b.put(x, y, z, b.rnd.choices(veil, weights)[0])
    for _ in range(30):
        x, z = cx + b.rnd.randint(-12, 12), cz + b.rnd.randint(-12, 12)
        if math.hypot(x - cx, z - cz) > 12:
            continue
        ys = [y for y in range(sh) if b.get(x, y, z)]
        if not ys:
            continue
        low = min(ys)
        for d in range(1, b.rnd.randint(2, 6)):
            b.put(x, low - d, z, "minecraft:blackstone" if d < 3 else "minecraft:basalt", {"axis": "y"} if d >= 3 else None)
    for x in range(sw):
        for z in range(sw):
            d = math.hypot(x - cx, z - cz)
            if d <= 13.8:
                name = "minecraft:polished_blackstone_bricks"
                if d <= 4.6:
                    name = "minecraft:obsidian"
                elif 6.5 <= d <= 7.5:
                    name = "minecraft:purpur_block"
                elif d >= 12.8:
                    name = "minecraft:chiseled_polished_blackstone"
                b.put(x, floor, z, name)
                b.put(x, floor - 1, z, "minecraft:obsidian")
    for side, block in ((1, "minecraft:quartz_bricks"), (-1, "minecraft:red_nether_bricks")):
        for t in range(9):
            span = 1 + t // 2
            for w in range(-span, span + 1):
                x = cx + side * (8 + t // 2)
                z = cz + w
                if math.hypot(x - cx, z - cz) <= 12.6:
                    b.put(x, floor, z, block)
        for k in range(5):
            x = cx + side * (9 + k)
            for z in (cz - 3 - k, cz + 3 + k):
                if math.hypot(x - cx, z - cz) <= 12.6:
                    b.put(x, floor, z, block)
    for r, name in ((3, "minecraft:polished_blackstone_bricks"), (2, "minecraft:obsidian"), (1, "minecraft:crying_obsidian")):
        h = 4 - r
        for x in range(cx - r, cx + r + 1):
            for z in range(cz - r, cz + r + 1):
                for yy in range(floor + 1, floor + 1 + h):
                    b.put(x, yy, z, name)
    top = floor + 4
    b.put(cx, top, cz, "simplywings:draconic_altar", {"facing": "south"})
    for k in range(8):
        a = k * math.pi / 4 + math.pi / 8
        px, pz = cx + round(math.sin(a) * 10), cz + round(math.cos(a) * 10)
        angel = math.sin(a) > 0
        hgt = 7 + (k % 2) * 2
        for yy in range(floor + 1, floor + 1 + hgt):
            b.put(px, yy, pz, "minecraft:quartz_pillar" if angel else "minecraft:polished_basalt", {"axis": "y"})
        b.put(px, floor + 1, pz, "minecraft:chiseled_quartz_block" if angel else "minecraft:chiseled_nether_bricks")
        if angel:
            b.put(px, floor + 1 + hgt, pz, "minecraft:end_rod", {"facing": "up"})
        else:
            b.put(px, floor + 1 + hgt, pz, "minecraft:soul_lantern", {"hanging": "false", "waterlogged": "false"})
    for i in range(-9, 10, 3):
        for t in range(0, 15):
            a = t / 14 * math.pi
            x = cx + i
            y = floor + 2 + round(math.sin(a) * 11)
            z = cz + round(math.cos(a) * 12)
            b.put(x, y, z, "minecraft:bone_block", {"axis": "x"})
    for x in range(cx - 12, cx + 13):
        b.put(x, floor + 13, cz, "minecraft:bone_block", {"axis": "x"})
    for _ in range(14):
        x, z = cx + b.rnd.randint(-11, 11), cz + b.rnd.randint(-11, 11)
        d = math.hypot(x - cx, z - cz)
        if d < 5 or d > 12:
            continue
        roof = [y for y in range(floor + 6, sh) if b.get(x, y, z)]
        if not roof:
            continue
        ry = min(roof)
        length = b.rnd.randint(1, 3)
        for k in range(1, length + 1):
            b.put(x, ry - k, z, "minecraft:chain", {"axis": "y", "waterlogged": "false"})
        b.put(x, ry - length - 1, z, "minecraft:soul_lantern" if x < cx else "minecraft:lantern", {"hanging": "true", "waterlogged": "false"})
    for _ in range(28):
        x, z = cx + b.rnd.randint(-12, 12), cz + b.rnd.randint(-12, 12)
        d = math.hypot(x - cx, z - cz)
        if not (5 < d < 12.5) or b.get(x, floor + 1, z) is not None:
            continue
        pick = b.rnd.random()
        if pick < 0.4:
            b.put(x, floor + 1, z, "minecraft:amethyst_cluster", {"facing": "up", "waterlogged": "false"})
        elif pick < 0.75:
            color = "white" if x > cx else "red"
            b.put(x, floor + 1, z, f"minecraft:{color}_candle",
                  {"candles": str(b.rnd.randint(1, 4)), "lit": "true", "waterlogged": "false"})
        else:
            b.put(x, floor + 1, z, "minecraft:skeleton_skull" if x > cx else "minecraft:wither_skeleton_skull",
                  {"rotation": str(b.rnd.randint(0, 15))})
    return b, {"center": [cx, floor, cz]}


def fountain():
    b = Build(0xF0A7)
    patch = Noise(b.rnd, 4)

    def surface(x, z, d):
        n = patch(x, z)
        return "minecraft:moss_block" if n > 0.3 else "minecraft:calcite" if n < -0.3 else "minecraft:end_stone"

    b.island(surface, lambda x, z, d: "minecraft:end_stone", "minecraft:end_stone",
             ["minecraft:prismarine", "minecraft:calcite", "minecraft:amethyst_block", "minecraft:end_stone_bricks"])
    y = TOP + 1
    for x in range(W):
        for z in range(W):
            d = math.hypot(x - C, z - C)
            if d <= 11.5:
                b.put(x, TOP, z, "minecraft:dark_prismarine" if d > 10.5 else "minecraft:prismarine_bricks")
            if 8.5 < d <= 9.6:
                b.put(x, y, z, "minecraft:quartz_bricks")
                b.put(x, y + 1, z, "minecraft:smooth_quartz_slab", {"type": "bottom", "waterlogged": "false"})
            elif d <= 8.5:
                lantern = (x + 2 * z) % 6 == 0 and d > 3
                b.put(x, TOP, z, "minecraft:sea_lantern" if lantern else "minecraft:prismarine_bricks")
                b.put(x, y, z, "minecraft:water", {"level": "0"})
    for x in range(C - 1, C + 2):
        for z in range(C - 1, C + 2):
            centre = (x, z) == (C, C)
            for yy in range(y, y + 2):
                b.put(x, yy, z, "minecraft:quartz_pillar" if centre else "minecraft:smooth_quartz", {"axis": "y"} if centre else None)
    b.put(C, y + 2, C, "simplywings:offering_fountain")
    for k in range(6):
        a = k * math.pi / 3
        px, pz = C + round(math.sin(a) * 13), C + round(math.cos(a) * 13)
        if front_open(px, pz, 22):
            continue
        for yy in range(y, y + 9):
            b.put(px, yy, pz, "minecraft:quartz_pillar", {"axis": "y"})
        b.put(px, y + 9, pz, "minecraft:chiseled_quartz_block")
        b.put(px, y + 10, pz, "minecraft:amethyst_cluster", {"facing": "up", "waterlogged": "false"})
        b.put(px, y + 4, pz + (1 if pz < C else -1), "minecraft:sea_lantern")
    for k in range(48):
        a = k * math.pi * 2 / 48
        px, pz = C + round(math.sin(a) * 13), C + round(math.cos(a) * 13)
        if not front_open(px, pz, 16):
            b.put(px, y + 9, pz, "minecraft:smooth_quartz_slab", {"type": "top", "waterlogged": "false"}, replace=False)
    for z in range(C + 12, C + 24):
        for x in range(C - 1, C + 2):
            ty = b.top(x, z)
            if ty is not None and b.rnd.random() < 0.85:
                b.put(x, ty, z, "minecraft:prismarine_bricks" if x == C else "minecraft:dark_prismarine")
    avoid = lambda x, z: math.hypot(x - C, z - C) < 15 or abs(x - C) <= 1 and z > C
    for _ in range(8):
        x, z = b.rnd.randrange(W), b.rnd.randrange(W)
        if b.top(x, z) is not None and not avoid(x, z):
            b.tree(x, z, "minecraft:cherry_log", "minecraft:cherry_leaves", b.rnd.randint(3, 5), 2)
    b.scatter([("minecraft:moss_carpet", None), ("minecraft:pink_petals", {"facing": "north", "flower_amount": "4"}),
               ("minecraft:short_grass", None)], 0.22,
              lambda x, z: avoid(x, z) or b.get(x, b.top(x, z), z) != "minecraft:moss_block")
    b.hanging("minecraft:glow_lichen", 0.03, (1, 1), {"down": "false", "up": "true", "north": "false", "south": "false",
                                                       "east": "false", "west": "false", "waterlogged": "false"})
    return b, {"center": [C, TOP, C]}


def write_loot():
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


def main():
    STRUCTURES.mkdir(parents=True, exist_ok=True)
    for name, fn in (("sky_sanctum", sanctum), ("wing_forge", forge), ("cloud_refinery", refinery),
                     ("summoning_sanctum", summoning), ("offering_fountain", fountain)):
        b, _ = fn()
        root = b.nbt()
        (STRUCTURES / f"{name}.nbt").write_bytes(gzip.compress(struct.pack(">bH", 10, 0) + payload(root)))
        print(name, len(root["blocks"]), "blocks,", len(root["palette"]), "states")
    write_loot()


if __name__ == "__main__":
    main()
