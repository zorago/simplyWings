import json
import math
import random
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / "src/main/resources/assets/simplywings"
TEX = ASSETS / "textures/block"
GUI = ASSETS / "textures/gui"
ITEM_TEX = ASSETS / "textures/item"


def rng(seed):
    return random.Random(seed)


def tile(seed, fn):
    r = rng(seed)
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), fn(x, y, r))
    return img


def obsidian():
    def f(x, y, r):
        v = r.random()
        if v < 0.08:
            return (92, 52, 140, 255)
        if v < 0.3:
            return (34, 22, 50, 255)
        return (20, 14, 30, 255) if (x + y * 3) % 7 else (46, 30, 70, 255)
    return tile(11, f)


def blackstone():
    def f(x, y, r):
        row = y // 4
        mortar = y % 4 == 3 or (x + (4 if row % 2 else 0)) % 8 == 7
        if mortar:
            return (16, 14, 18, 255)
        v = 40 + int(r.random() * 12) + (6 if y % 4 == 0 else 0)
        return (v, v - 3, v + 2, 255)
    return tile(12, f)


def gold():
    def f(x, y, r):
        if x in (0, 15) or y in (0, 15):
            return (120, 70, 18, 255)
        v = r.random()
        if (x + y) % 5 == 0:
            return (255, 236, 140, 255)
        return (232, 176, 52, 255) if v > 0.3 else (198, 138, 36, 255)
    return tile(13, f)


def bone():
    def f(x, y, r):
        v = 214 + int(r.random() * 18)
        if y % 6 == 5:
            v -= 40
        if x in (0, 15):
            v -= 30
        return (v, v - 8, v - 30, 255)
    return tile(14, f)


def crystal():
    def f(x, y, r):
        d = abs(x - 7.5) + abs(y - 7.5)
        if d < 3:
            return (250, 220, 255, 255)
        if d < 7:
            return (200, 120, 255, 255)
        if (x - y) % 4 == 0:
            return (230, 170, 255, 255)
        return (140, 60, 210, 255)
    return tile(15, f)


def feather():
    def f(x, y, r):
        if x == 7 or x == 8:
            return (200, 196, 210, 255)
        edge = abs(x - 7.5) > 6.5 - (y % 4) * 0.3
        if edge and r.random() < 0.5:
            return (0, 0, 0, 0)
        v = 236 + int(r.random() * 18)
        if (y + (x // 3)) % 4 == 0:
            v -= 22
        return (v, v, min(255, v + 6), 255)
    return tile(16, f)


def membrane():
    def f(x, y, r):
        if x % 5 == 0:
            return (70, 12, 16, 255)
        if y > 12 and (x * 3 + y) % 5 == 0:
            return (0, 0, 0, 0)
        v = r.random()
        return (150, 24, 30, 255) if v > 0.4 else (120, 18, 26, 255) if v > 0.1 else (200, 60, 50, 255)
    return tile(17, f)


def ember():
    def f(x, y, r):
        v = r.random()
        if (x // 2 + y // 2) % 3 == 0:
            return (36, 10, 60, 255)
        return (200, 120, 255, 255) if v < 0.3 else (140, 70, 230, 255) if v < 0.7 else (90, 40, 170, 255)
    return tile(18, f)


def quartz():
    def f(x, y, r):
        if x in (0, 15) or y in (0, 15):
            return (196, 188, 178, 255)
        v = 232 + int(r.random() * 14)
        return (v, v - 4, v - 10, 255)
    return tile(21, f)


def prismarine():
    def f(x, y, r):
        row = y // 4
        mortar = y % 4 == 3 or (x + (4 if row % 2 else 0)) % 8 == 7
        if mortar:
            return (30, 70, 70, 255)
        v = r.random()
        return (70, 150, 140, 255) if v > 0.5 else (90, 176, 160, 255) if v > 0.15 else (130, 210, 196, 255)
    return tile(22, f)


def water():
    def f(x, y, r):
        w = math.sin(x * 0.8 + y * 0.5) + math.sin(x * 0.3 - y * 0.9)
        if w > 1.3:
            return (210, 245, 255, 220)
        if w > 0.6:
            return (110, 200, 240, 200)
        return (50, 120, 210, 190)
    return tile(23, f)


def amethyst():
    def f(x, y, r):
        d = abs(x - 7.5)
        if d < 1.5:
            return (255, 236, 255, 255)
        return (196, 132, 255, 255) if (y + int(d)) % 3 else (150, 90, 230, 255)
    return tile(24, f)


def box(name, a, b, tex, faces="nsewud", light=False, rot=None, shade=True):
    dirs = {"n": "north", "s": "south", "e": "east", "w": "west", "u": "up", "d": "down"}
    el = {"name": name, "from": a, "to": b, "faces": {dirs[f]: {"texture": f"#{tex}"} for f in faces}}
    if light:
        el["neoforge_data"] = {"block_light": 15, "sky_light": 15}
    if rot:
        el["rotation"] = rot
    if not shade:
        el["shade"] = False
    return el


def altar():
    els = [
        box("base", [-2, 0, -2], [18, 3, 18], "obsidian"),
        box("base_trim", [-1, 3, -1], [17, 4, 17], "gold", "nsewu"),
        box("step", [1, 4, 1], [15, 6, 15], "blackstone"),
        box("column", [4, 6, 4], [12, 12, 12], "blackstone", "nsew"),
        box("column_band", [3.5, 9, 3.5], [12.5, 10, 12.5], "gold", "nsewud"),
        box("bowl", [2, 12, 2], [14, 14, 14], "obsidian", "nsewd"),
        box("bowl_rim", [2, 14, 2], [14, 14.5, 14], "gold", "nsewu"),
        box("ember_bed", [3, 14, 3], [13, 14.6, 13], "ember", "u", True),
        box("crystal", [6, 15, 6], [10, 24, 10], "crystal", light=True, rot={"origin": [8, 19, 8], "axis": "y", "angle": 45}),
        box("crystal_tip", [7, 24, 7], [9, 27, 9], "crystal", light=True, rot={"origin": [8, 25, 8], "axis": "y", "angle": 45}),
    ]
    for k, (x, z) in enumerate([(-1, -1), (14, -1), (-1, 14), (14, 14)]):
        els.append(box(f"claw_{k}_root", [x, 4, z], [x + 3, 7, z + 3], "bone"))
        dx = 1 if x < 8 else -1
        dz = 1 if z < 8 else -1
        tx = x + (1 if dx > 0 else 0)
        tz = z + (1 if dz > 0 else 0)
        els.append(box(f"claw_{k}_curve", [tx, 7, tz], [tx + 2, 12, tz + 2], "bone",
                       rot={"origin": [tx + 1, 7, tz + 1], "axis": "x", "angle": 22.5 * dz}))
    for side, sign in (("angel", -1), ("demon", 1)):
        x0 = -7 if sign < 0 else 18
        tex = "feather" if sign < 0 else "membrane"
        for k in range(3):
            y0 = 9 + k * 3
            length = 10 - k * 2
            a = [x0 - (length if sign < 0 else 0) + 7 * (1 if sign < 0 else -1), y0, 7.5 + k * 0.05]
            b = [a[0] + length, y0 + 9 - k, 8.5 + k * 0.05]
            els.append(box(f"{side}_wing_{k}", [min(a[0], b[0]), a[1], a[2]], [max(a[0], b[0]), b[1], b[2]], tex, "nsew",
                           rot={"origin": [8 + sign * 5, y0, 8], "axis": "z", "angle": -22.5 * sign}))
    els.append(box("horn_left", [3, 13, 13], [5, 20, 15], "bone", rot={"origin": [4, 13, 14], "axis": "x", "angle": 22.5}))
    els.append(box("horn_right", [11, 13, 13], [13, 20, 15], "bone", rot={"origin": [12, 13, 14], "axis": "x", "angle": 22.5}))
    return els, {"obsidian": "draconic_altar_obsidian", "blackstone": "draconic_altar_blackstone", "gold": "draconic_altar_gold",
                 "bone": "draconic_altar_bone", "crystal": "draconic_altar_crystal", "feather": "draconic_altar_feather",
                 "membrane": "draconic_altar_membrane", "ember": "draconic_altar_ember"}


def fountain():
    els = [
        box("base", [-4, 0, -4], [20, 2, 20], "prismarine"),
        box("wall_n", [-4, 2, -4], [20, 6, -2], "quartz"),
        box("wall_s", [-4, 2, 18], [20, 6, 20], "quartz"),
        box("wall_w", [-4, 2, -2], [-2, 6, 18], "quartz"),
        box("wall_e", [18, 2, -2], [20, 6, 18], "quartz"),
        box("water", [-2, 2, -2], [18, 5, 18], "water", "u", shade=False),
        box("pillar", [5, 2, 5], [11, 12, 11], "quartz", "nsew"),
        box("pillar_band", [4.5, 9, 4.5], [11.5, 10, 11.5], "prismarine", "nsewud"),
        box("cup", [2, 12, 2], [14, 14, 14], "quartz", "nsewd"),
        box("cup_water", [3, 14, 3], [13, 13.9, 13], "water", "u", shade=False),
        box("amethyst", [6, 14, 6], [10, 22, 10], "amethyst", light=True, rot={"origin": [8, 18, 8], "axis": "y", "angle": 45}),
    ]
    for k, (x, z) in enumerate([(7, -1), (7, 15), (-1, 7), (15, 7)]):
        els.append(box(f"spout_{k}", [x, 10, z], [x + 2, 11, z + 2], "prismarine"))
    return els, {"quartz": "offering_fountain_quartz", "prismarine": "offering_fountain_prismarine",
                 "water": "offering_fountain_water", "amethyst": "offering_fountain_amethyst"}


def write_block(name, elements, textures, facing):
    model = {"parent": "minecraft:block/block", "render_type": "minecraft:cutout",
             "textures": {**{k: f"simplywings:block/{v}" for k, v in textures.items()},
                          "particle": f"simplywings:block/{next(iter(textures.values()))}"},
             "elements": elements,
             "display": {"gui": {"rotation": [30, 225, 0], "translation": [0, -1.5, 0], "scale": [0.42, 0.42, 0.42]},
                         "ground": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.2, 0.2, 0.2]},
                         "fixed": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [0.4, 0.4, 0.4]},
                         "thirdperson_righthand": {"rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.3, 0.3, 0.3]},
                         "firstperson_righthand": {"rotation": [0, 45, 0], "translation": [0, 0, 0], "scale": [0.3, 0.3, 0.3]}}}
    (ASSETS / f"models/block/{name}.json").write_text(json.dumps(model, indent=2) + "\n", encoding="utf-8", newline="\n")
    (ASSETS / f"models/item/{name}.json").write_text(json.dumps({"parent": f"simplywings:block/{name}"}, indent=2) + "\n",
                                                     encoding="utf-8", newline="\n")
    if facing:
        variants = {f"facing={f}": ({"model": f"simplywings:block/{name}", "y": y} if y else {"model": f"simplywings:block/{name}"})
                    for f, y in [("south", 0), ("west", 90), ("north", 180), ("east", 270)]}
    else:
        variants = {"": {"model": f"simplywings:block/{name}"}}
    (ASSETS / f"blockstates/{name}.json").write_text(json.dumps({"variants": variants}, indent=2) + "\n",
                                                     encoding="utf-8", newline="\n")


def feather_item():
    img = Image.new("RGBA", (16, 16))
    pal = {"o": (40, 20, 70, 255), "v": (150, 70, 230, 255), "l": (210, 160, 255, 255), "w": (255, 246, 255, 255),
           "g": (255, 210, 90, 255), "q": (120, 50, 190, 255)}
    art = [
        "............ow..",
        "...........owl..",
        "..........owlv..",
        ".........owlvq..",
        "........owlvvq..",
        ".......owlvvqo..",
        "......owlvvqo...",
        ".....owlvvqo....",
        "....owlvvqo.....",
        "...owlvvqo......",
        "...olvvqo.......",
        "..olvqoo........",
        "..gooo..........",
        ".g..............",
        "g...............",
        "................",
    ]
    for y, row in enumerate(art):
        for x, ch in enumerate(row):
            if ch in pal:
                img.putpixel((x, y), pal[ch])
    return img


def cover():
    w, h = 480, 270
    img = Image.new("RGBA", (w, h))
    px = img.load()
    for y in range(h):
        t = y / h
        for x in range(w):
            r = int(18 + 40 * t + 20 * math.sin(x * 0.01))
            g = int(8 + 18 * t)
            b = int(40 + 60 * (1 - abs(t - 0.45) * 1.6))
            px[x, y] = (max(0, r), max(0, g), max(0, min(255, b)), 255)
    rnd = rng(31)
    draw = ImageDraw.Draw(img)
    for _ in range(420):
        x, y = rnd.randrange(w), rnd.randrange(int(h * 0.75))
        v = rnd.randint(150, 255)
        draw.point((x, y), fill=(v, v, min(255, v + 20), 255))
    for k in range(5):
        cx, cy, rr = rnd.randrange(w), rnd.randrange(h // 2), rnd.randint(20, 60)
        for i in range(rr, 0, -2):
            a = int(18 * (1 - i / rr))
            draw.ellipse([cx - i, cy - i // 2, cx + i, cy + i // 2], fill=(140, 60, 200, a))

    def dragon(cx, cy, s, flip):
        sign = -1 if flip else 1
        body = [(cx - 30 * s * sign, cy), (cx, cy - 4 * s), (cx + 26 * s * sign, cy - 2 * s), (cx + 40 * s * sign, cy + 4 * s),
                (cx + 26 * s * sign, cy + 4 * s), (cx, cy + 4 * s)]
        draw.polygon(body, fill=(6, 4, 12, 235))
        for wing in (-1, 1):
            draw.polygon([(cx - 4 * s * sign, cy - 2 * s), (cx + 6 * s * sign, cy - 2 * s),
                          (cx + 2 * s * sign, cy - 30 * s * wing - 2 * s), (cx - 14 * s * sign, cy - 22 * s * wing),
                          (cx - 22 * s * sign, cy - 8 * s * wing)], fill=(6, 4, 12, 225))
        draw.polygon([(cx - 30 * s * sign, cy), (cx - 50 * s * sign, cy + 6 * s), (cx - 46 * s * sign, cy + 2 * s)], fill=(6, 4, 12, 225))

    dragon(120, 70, 1.3, False)
    dragon(360, 52, 1.0, True)
    for x in range(w):
        base = h - 50 + int(10 * math.sin(x * 0.02) + 6 * math.sin(x * 0.07))
        for y in range(base, h):
            px[x, y] = (10, 6, 18, 255)
    for k in range(3):
        ix, iy, iw = 80 + k * 150, 150 + (k % 2) * 20, 40 + k * 8
        draw.ellipse([ix - iw, iy - 6, ix + iw, iy + 6], fill=(30, 22, 44, 255))
        draw.polygon([(ix - iw, iy), (ix + iw, iy), (ix, iy + 30)], fill=(22, 16, 34, 255))
    return img


def main():
    TEX.mkdir(parents=True, exist_ok=True)
    GUI.mkdir(parents=True, exist_ok=True)
    painters = {"draconic_altar_obsidian": obsidian, "draconic_altar_blackstone": blackstone, "draconic_altar_gold": gold,
                "draconic_altar_bone": bone, "draconic_altar_crystal": crystal, "draconic_altar_feather": feather,
                "draconic_altar_membrane": membrane, "draconic_altar_ember": ember, "offering_fountain_quartz": quartz}
    for name, fn in painters.items():
        fn().save(TEX / f"{name}.png")
    els, tex = altar()
    write_block("draconic_altar", els, tex, True)
    feather_item().save(ITEM_TEX / "summoning_feather.png")
    (ASSETS / "models/item/summoning_feather.json").write_text(json.dumps(
        {"parent": "minecraft:item/generated", "textures": {"layer0": "simplywings:item/summoning_feather"}}, indent=2) + "\n",
        encoding="utf-8", newline="\n")
    print("ok")


if __name__ == "__main__":
    main()
