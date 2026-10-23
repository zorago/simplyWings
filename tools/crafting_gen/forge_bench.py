import json
import random
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / "src/main/resources/assets/simplywings"
TEX = ASSETS / "textures/block"


def noise(seed):
    rnd = random.Random(seed)
    return lambda: rnd.random()


def stone():
    r = noise(1)
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            row = y // 4
            off = 4 if row % 2 else 0
            mortar = y % 4 == 3 or (x + off) % 8 == 7
            if mortar:
                c = (28, 24, 26)
            else:
                v = 58 + int(r() * 16) - (8 if y % 4 == 2 else 0) + (6 if y % 4 == 0 else 0)
                c = (v, v - 4, v - 2)
            img.putpixel((x, y), c + (255,))
    return img


def iron():
    r = noise(2)
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            v = 64 + int(r() * 10)
            if y in (0, 15) or x in (0, 15):
                v = 44
            elif y == 1 or x == 1:
                v = 92
            if r() < 0.04:
                c = (110, 62, 34)
            else:
                c = (v, v + 2, v + 8)
            img.putpixel((x, y), c + (255,))
    for x, y in [(3, 3), (12, 3), (3, 12), (12, 12)]:
        img.putpixel((x, y), (150, 152, 160, 255))
        img.putpixel((x + 1, y + 1), (30, 30, 36, 255))
    return img


def top():
    r = noise(3)
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            v = 96 + int(r() * 14)
            if 5 <= x <= 10 and 4 <= y <= 11:
                v += 22
            if y in (0, 15) or x in (0, 15):
                v = 52
            img.putpixel((x, y), (v, v + 2, v + 6, 255))
    for k in range(4):
        x, y = int(r() * 12) + 2, int(r() * 12) + 2
        img.putpixel((x, y), (150, 90, 50, 255))
    return img


def ember():
    r = noise(4)
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            t = r()
            if (x // 2 + y // 2) % 3 == 0:
                c = (40, 22, 18)
            elif t < 0.3:
                c = (255, 214, 90)
            elif t < 0.7:
                c = (250, 130, 40)
            else:
                c = (190, 60, 24)
            img.putpixel((x, y), c + (255,))
    return img


def wood():
    r = noise(5)
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            v = int(r() * 12)
            c = (104 + v, 70 + v, 40 + v // 2) if x % 4 != 0 else (78, 50, 28)
            img.putpixel((x, y), c + (255,))
    return img


def box(name, a, b, tex, faces="nsewud", light=False):
    dirs = {"n": "north", "s": "south", "e": "east", "w": "west", "u": "up", "d": "down"}
    el = {"name": name, "from": a, "to": b,
          "faces": {dirs[f]: {"texture": f"#{tex}"} for f in faces}}
    if light:
        el["neoforge_data"] = {"block_light": 15, "sky_light": 15}
    return el


def main():
    TEX.mkdir(parents=True, exist_ok=True)
    for name, fn in [("stone", stone), ("iron", iron), ("top", top), ("ember", ember), ("wood", wood)]:
        fn().save(TEX / f"forge_bench_{name}.png")
    elements = [
        box("plinth", [0, 0, 1], [16, 3, 15], "stone"),
        box("plinth_lip", [1, 3, 2], [15, 4, 14], "stone", "nsewu"),
        box("waist", [4, 4, 4], [12, 9, 12], "iron", "nsew"),
        box("waist_front", [3, 4, 5], [13, 6, 11], "iron", "nsewu"),
        box("face", [1, 9, 2], [15, 12, 14], "iron", "nsewd"),
        box("face_top", [1, 12, 2], [15, 12.01, 14], "top", "u"),
        box("horn", [6, 10, 14], [10, 12, 16], "iron", "sewud"),
        box("horn_tip", [7, 10.5, 16], [9, 11.5, 16.01], "iron", "s"),
        box("heel", [2, 10, 0], [14, 12, 2], "iron", "newud"),
        box("embers", [9, 12, 3], [14, 13, 7], "ember", "nsewu", True),
        box("grate", [8.5, 12, 2.5], [14.5, 12.5, 7.5], "iron", "nsew"),
        box("hammer_head", [3, 12, 9], [7, 14, 11], "iron", "nsewud"),
        box("hammer_handle", [4.5, 12, 3], [5.5, 13, 9], "wood", "nsewu"),
        box("tongs_a", [10, 12, 9], [10.5, 12.5, 14], "iron", "ewu"),
        box("tongs_b", [11.5, 12, 9], [12, 12.5, 14], "iron", "ewu"),
    ]
    model = {
        "parent": "minecraft:block/block",
        "render_type": "minecraft:cutout",
        "textures": {"stone": "simplywings:block/forge_bench_stone", "iron": "simplywings:block/forge_bench_iron",
                     "top": "simplywings:block/forge_bench_top", "ember": "simplywings:block/forge_bench_ember",
                     "wood": "simplywings:block/forge_bench_wood", "particle": "simplywings:block/forge_bench_iron"},
        "elements": elements,
    }
    (ASSETS / "models/block/forge_bench.json").write_text(json.dumps(model, indent=2) + "\n", encoding="utf-8", newline="\n")
    (ASSETS / "models/item/forge_bench.json").write_text(json.dumps({"parent": "simplywings:block/forge_bench"}, indent=2) + "\n",
                                                          encoding="utf-8", newline="\n")
    variants = {f"facing={f}": ({"model": "simplywings:block/forge_bench", "y": y} if y else {"model": "simplywings:block/forge_bench"})
                for f, y in [("south", 0), ("west", 90), ("north", 180), ("east", 270)]}
    (ASSETS / "blockstates/forge_bench.json").write_text(json.dumps({"variants": variants}, indent=2) + "\n",
                                                         encoding="utf-8", newline="\n")
    loot = {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": [{"type": "minecraft:item", "name": "simplywings:forge_bench"}],
                                                   "conditions": [{"condition": "minecraft:survives_explosion"}]}]}
    (ROOT / "src/main/resources/data/simplywings/loot_table/blocks/forge_bench.json").write_text(
        json.dumps(loot, indent=2) + "\n", encoding="utf-8", newline="\n")
    print("ok")


if __name__ == "__main__":
    main()
