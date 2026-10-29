import json
import math
import random
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / "src/main/resources/assets/simplywings"
TEXTURE = ASSETS / "textures/block/draconic_altar.png"
SIZE = 128

MATERIALS = {
    "obsidian": ((44, 30, 64), (28, 18, 42), (70, 52, 98)),
    "stone": ((70, 60, 82), (50, 42, 60), (96, 86, 110)),
    "purpur": ((176, 140, 196), (138, 104, 160), (208, 178, 224)),
    "gold": ((232, 186, 72), (176, 128, 36), (255, 228, 132)),
    "crystal": ((186, 120, 255), (130, 70, 220), (236, 206, 255)),
    "feather": ((240, 238, 248), (190, 186, 210), (255, 255, 255)),
    "feather_tip": ((206, 186, 240), (160, 136, 206), (232, 220, 255)),
}

elements = []


def box(a, b, mat, rot=None, faces="nsewud"):
    elements.append({"from": list(a), "to": list(b), "mat": mat, "rot": rot, "faces": faces})


def wing(sign):
    def x(v):
        return v if sign > 0 else 16 - v

    def span(x0, x1):
        a, b = x(x0), x(x1)
        return min(a, b), max(a, b)

    ang = 22.5 * sign
    lo, hi = span(11.5, 21)
    box((lo, 12.5, 7.4), (hi, 14, 8.6), "gold", {"angle": ang, "axis": "z", "origin": [x(11.5), 13.25, 8]})
    for i, (cx, top, length) in enumerate(((12.8, 14.4, 7), (14.4, 15.1, 8.5), (16, 15.8, 10), (17.6, 16.5, 11.5),
                                          (19.2, 17.3, 12.5), (20.8, 18.2, 13), (22.4, 19.1, 12.5))):
        lo, hi = span(cx - 1.4, cx + 1.4)
        mat = "feather" if i % 2 == 0 else "feather_tip"
        box((lo, top - length, 7.6), (hi, top, 8.4), mat, {"angle": -ang if i > 2 else 0, "axis": "z", "origin": [x(cx), top, 8]}
            if i > 2 else None, "nsewud")
    for i, (cx, top, length) in enumerate(((13.6, 15.4, 4), (15.6, 16.4, 4.5), (17.6, 17.4, 5), (19.6, 18.6, 5))):
        lo, hi = span(cx - 1.1, cx + 1.1)
        box((lo, top - length, 8.4), (hi, top, 9.2), "feather_tip" if i % 2 == 0 else "feather")


def build():
    box((-2, 0, -2), (18, 2, 18), "obsidian")
    box((-1, 2, -1), (17, 3, 17), "gold")
    box((0, 3, 0), (16, 5, 16), "stone")
    for cx, cz in ((1, 1), (15, 1), (1, 15), (15, 15)):
        box((cx - 1, 5, cz - 1), (cx + 1, 9, cz + 1), "purpur")
        box((cx - 1.5, 9, cz - 1.5), (cx + 1.5, 10, cz + 1.5), "gold")
    box((4, 5, 4), (12, 6, 12), "gold")
    box((5, 6, 5), (11, 15, 11), "obsidian")
    box((4.5, 9, 4.5), (11.5, 10, 11.5), "gold")
    box((4.5, 13, 4.5), (11.5, 14, 11.5), "purpur")
    box((2, 15, 2), (14, 16, 14), "stone")
    box((1.5, 16, 1.5), (14.5, 17, 14.5), "gold")
    box((3, 17, 3), (13, 18, 13), "obsidian")
    box((6, 18, 6), (10, 26, 10), "crystal", {"angle": 45, "axis": "y", "origin": [8, 18, 8]})
    box((4, 18, 7), (6, 22, 9), "crystal", {"angle": 22.5, "axis": "z", "origin": [6, 18, 8]})
    box((10, 18, 7), (12, 21, 9), "crystal", {"angle": -22.5, "axis": "z", "origin": [10, 18, 8]})
    box((7, 18, 4), (9, 21.5, 6), "crystal", {"angle": -22.5, "axis": "x", "origin": [8, 18, 6]})
    box((7, 18, 10), (9, 20.5, 12), "crystal", {"angle": 22.5, "axis": "x", "origin": [8, 18, 10]})
    wing(1)
    wing(-1)


FACE_DIRS = {"n": "north", "s": "south", "e": "east", "w": "west", "u": "up", "d": "down"}


def face_size(e, face):
    w = e["to"][0] - e["from"][0]
    h = e["to"][1] - e["from"][1]
    d = e["to"][2] - e["from"][2]
    return {"north": (w, h), "south": (w, h), "east": (d, h), "west": (d, h), "up": (w, d), "down": (w, d)}[face]


def pack():
    regions = []
    for e in elements:
        e["uv"] = {}
        for f in e["faces"]:
            name = FACE_DIRS[f]
            fw, fh = face_size(e, name)
            regions.append((max(1, math.ceil(fw)), max(1, math.ceil(fh)), e, name))
    regions.sort(key=lambda r: -r[1])
    x = y = shelf = 0
    for w, h, e, name in regions:
        if x + w > SIZE:
            x, y, shelf = 0, y + shelf, 0
        e["uv"][name] = (x, y, w, h)
        x += w
        shelf = max(shelf, h)
    if y + shelf > SIZE:
        raise SystemExit(f"atlas overflow: {y + shelf}")


def paint():
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    px = img.load()
    rnd = random.Random(23)
    for e in elements:
        base, shade, light = MATERIALS[e["mat"]]
        for name, (u, v, w, h) in e["uv"].items():
            for j in range(h):
                for i in range(w):
                    n = rnd.random()
                    c = base
                    if e["mat"] in ("obsidian", "stone") and n < 0.12:
                        c = light if n < 0.03 else shade
                    elif n < 0.14:
                        c = tuple(max(0, k - 8) for k in base)
                    elif n > 0.88:
                        c = tuple(min(255, k + 8) for k in base)
                    if e["mat"] == "purpur" and name not in ("up", "down") and j % 3 == 2:
                        c = shade
                    if e["mat"] in ("feather", "feather_tip") and name in ("north", "south"):
                        c = shade if i == w // 2 and j < h - 1 else c
                        if j >= h - 2:
                            c = MATERIALS["feather_tip"][1] if e["mat"] == "feather" else MATERIALS["crystal"][0]
                    if e["mat"] == "crystal" and (i + j) % 4 == 0:
                        c = light
                    if e["mat"] == "gold" and (i + 2 * j) % 7 == 0:
                        c = light
                    edge_dark = (i == w - 1 or j == h - 1) and w > 2 and h > 2
                    edge_light = (i == 0 or j == 0) and w > 2 and h > 2
                    if e["mat"] not in ("feather", "feather_tip") and edge_dark:
                        c = shade
                    elif e["mat"] not in ("feather", "feather_tip") and edge_light:
                        c = light
                    px[u + i, v + j] = c + (255,)
    return img


def model():
    out = []
    s = 16 / SIZE
    for e in elements:
        faces = {}
        for name, (u, v, w, h) in e["uv"].items():
            fw, fh = face_size(e, name)
            faces[name] = {"uv": [round(u * s, 4), round(v * s, 4), round((u + fw) * s, 4), round((v + fh) * s, 4)],
                           "texture": "#altar"}
        el = {"from": [round(c, 3) for c in e["from"]], "to": [round(c, 3) for c in e["to"]], "faces": faces}
        if e["rot"]:
            el["rotation"] = e["rot"]
        out.append(el)
    return {"parent": "minecraft:block/block", "render_type": "minecraft:cutout",
            "textures": {"altar": "simplywings:block/draconic_altar", "particle": "simplywings:block/draconic_altar"},
            "elements": out,
            "display": {"gui": {"rotation": [30, 225, 0], "translation": [0, -1.5, 0], "scale": [0.42, 0.42, 0.42]},
                        "ground": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.2, 0.2, 0.2]},
                        "fixed": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [0.4, 0.4, 0.4]},
                        "thirdperson_righthand": {"rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.3, 0.3, 0.3]},
                        "firstperson_righthand": {"rotation": [0, 45, 0], "translation": [0, 0, 0], "scale": [0.3, 0.3, 0.3]}}}


def main():
    build()
    pack()
    paint().save(TEXTURE)
    (ASSETS / "models/block/draconic_altar.json").write_text(json.dumps(model(), indent=2) + "\n", encoding="utf-8", newline="\n")
    print(f"{len(elements)} elements, atlas {SIZE}x{SIZE}")


if __name__ == "__main__":
    main()
