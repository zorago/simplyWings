import base64
import io
import json
import math
import random
import uuid
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
ASSETS = ROOT / "src/main/resources/assets/simplywings"
RIG = ASSETS / "rigs/offering_fountain.bbmodel"
TEXTURE = ASSETS / "textures/block/offering_fountain.png"
ICON = ASSETS / "textures/item/offering_fountain.png"
TEX_W = 256
RADIUS = 32

MATERIALS = {
    "quartz": ((236, 232, 222), (198, 192, 180), (252, 250, 244), 0.03, (214, 206, 190)),
    "quartz_dark": ((206, 200, 188), (168, 160, 148), (226, 222, 212), 0.04, (186, 178, 164)),
    "gold": ((232, 186, 72), (176, 128, 36), (255, 228, 132), 0.0, None),
    "floor": ((54, 120, 132), (36, 86, 98), (82, 156, 166), 0.06, (110, 190, 196)),
    "water": ((70, 138, 228), (48, 104, 196), (150, 200, 255), 0.0, None),
    "amethyst": ((156, 104, 214), (110, 68, 168), (214, 176, 250), 0.0, None),
    "feather": ((246, 244, 252), (196, 194, 214), (255, 255, 255), 0.0, None),
}
TRANSLUCENT = {"water": 150}

elements = []
outliner = []
groups = {}


def uid():
    return str(uuid.uuid4())


def group(name, origin=(0, 0, 0), parent=None):
    g = {"name": name, "origin": list(origin), "rotation": [0, 0, 0], "uuid": uid(), "export": True,
         "isOpen": False, "children": []}
    groups[name] = g
    (groups[parent]["children"] if parent else outliner).append(g)
    return name


def cube(g, name, a, b, material, origin=None, rotation=(0, 0, 0)):
    e = {"name": name, "type": "cube", "uuid": uid(), "from": [round(v, 3) for v in a], "to": [round(v, 3) for v in b],
         "origin": [round(v, 3) for v in (origin if origin else ((a[0] + b[0]) / 2, (a[1] + b[1]) / 2, (a[2] + b[2]) / 2))],
         "rotation": list(rotation), "box_uv": True, "rescale": False, "inflate": 0, "material": material}
    elements.append(e)
    groups[g]["children"].append(e["uuid"])
    return e


def disk(g, name, radius, y0, y1, material):
    side = 2 * radius * math.tan(math.pi / 8)
    for k in range(4):
        cube(g, f"{name}_{k}", (-radius, y0, -side / 2), (radius, y1, side / 2), material, origin=(0, y0, 0), rotation=(0, k * 45, 0))


def wall(g, name, radius, thick, y0, y1, material):
    side = 2 * radius * math.tan(math.pi / 8)
    for k in range(8):
        a = math.radians(k * 45)
        cx, cz = math.sin(a) * (radius - thick / 2), math.cos(a) * (radius - thick / 2)
        cube(g, f"{name}_{k}", (cx - side / 2, y0, cz - thick / 2), (cx + side / 2, y1, cz + thick / 2), material,
             origin=(cx, y0, cz), rotation=(0, k * 45 if k * 45 <= 180 else k * 45 - 360, 0))


def wing(g, name, side, y, z):
    s = 1 if side > 0 else -1
    for i, (length, lift) in enumerate(((9, 0), (8, 2.2), (6.5, 4.2), (5, 6))):
        x0 = s * 4.5
        cube(g, f"{name}_feather{i}", (min(x0, x0 + s * length), y + lift, z - 0.25), (max(x0, x0 + s * length), y + lift + 2.2, z + 0.25),
             "feather", origin=(x0, y + lift + 1.1, z), rotation=(0, 0, s * (12 + i * 9)))
    cube(g, f"{name}_bone", (min(s * 4, s * 7), y + 6.5, z - 0.5), (max(s * 4, s * 7), y + 8, z + 0.5), "gold",
         origin=(s * 4, y + 7, z), rotation=(0, 0, s * 35))


def build():
    stone = group("stone")
    disk(stone, "plinth", RADIUS, 0, 2, "quartz_dark")
    disk(stone, "basin_floor", RADIUS - 2, 2, 3, "floor")
    wall(stone, "basin_wall", RADIUS, 3, 2, 11, "quartz")
    wall(stone, "basin_cap", RADIUS + 0.5, 4, 11, 12.5, "gold")
    for k in range(8):
        a = math.radians(k * 45 + 22.5)
        r = RADIUS / math.cos(math.pi / 8) - 2.2
        cx, cz = math.sin(a) * r, math.cos(a) * r
        cube(stone, f"post_{k}", (cx - 1.5, 12.5, cz - 1.5), (cx + 1.5, 16, cz + 1.5), "quartz")
        cube(stone, f"post_cap_{k}", (cx - 2, 16, cz - 2), (cx + 2, 17, cz + 2), "gold")
    disk(stone, "pedestal_foot", 9, 3, 6, "quartz_dark")
    disk(stone, "pedestal", 5.5, 6, 23, "quartz")
    for y in (10, 18):
        disk(stone, f"pedestal_band_{y}", 6.2, y, y + 1.2, "gold")
    disk(stone, "bowl", 15, 23, 25, "quartz")
    wall(stone, "bowl_rim", 15, 2, 25, 27.5, "quartz")
    wall(stone, "bowl_trim", 15.4, 2.6, 27.5, 28.3, "gold")
    disk(stone, "column", 3.2, 25, 37, "quartz")
    disk(stone, "column_band", 3.8, 31, 32, "gold")
    disk(stone, "cup", 7, 37, 38.5, "quartz")
    wall(stone, "cup_rim", 7, 1.5, 38.5, 40, "gold")
    for k, (cx, cz) in enumerate(((0, 6.3), (0, -6.3), (6.3, 0), (-6.3, 0))):
        rot = (0, 0, 0) if cz else (0, 90, 0)
        wg = group(f"wings_{k}", (cx, 12, cz), "stone")
        wing(wg, f"wings_{k}_r", 1, 11, 0)
        wing(wg, f"wings_{k}_l", -1, 11, 0)
        for e in elements[-10:]:
            if rot[1]:
                for key in ("from", "to", "origin"):
                    x, y, z = e[key]
                    e[key] = [round(z, 3), y, round(x, 3)]
                f, t = e["from"], e["to"]
                e["from"], e["to"] = [min(f[0], t[0]), f[1], min(f[2], t[2])], [max(f[0], t[0]), t[1], max(f[2], t[2])]
                e["rotation"] = [-e["rotation"][2], 0, 0]
            for key in ("from", "to", "origin"):
                e[key] = [round(e[key][0] + cx, 3), e[key][1], round(e[key][2] + cz, 3)]
    crystal = group("crystal", (0, 40, 0))
    cube(crystal, "crystal_core", (-2, 40, -2), (2, 49, 2), "amethyst", origin=(0, 40, 0), rotation=(0, 45, 0))
    for k, (dx, dz, h, tilt) in enumerate(((2.5, 0, 6, 20), (-2.5, 0, 5, -20), (0, 2.5, 5.5, 0), (0, -2.5, 4.5, 0))):
        rot = (0, 0, -tilt) if dz == 0 else (tilt or (18 if dz > 0 else -18), 0, 0)
        cube(crystal, f"crystal_shard_{k}", (dx - 1.2, 40, dz - 1.2), (dx + 1.2, 40 + h, dz + 1.2), "amethyst", origin=(dx, 40, dz), rotation=rot)
    water = group("water")
    disk(water, "pool", RADIUS - 3.2, 9, 9.6, "water")
    disk(water, "bowl_water", 13, 26, 26.6, "water")
    disk(water, "cup_water", 5.5, 39, 39.4, "water")
    for k in range(8):
        a = math.radians(k * 45)
        for name, r, y0, y1 in (("upper", 7.6, 27, 39.2), ("lower", 15.8, 9.5, 26.4)):
            cx, cz = math.sin(a) * r, math.cos(a) * r
            cube(water, f"stream_{name}_{k}", (cx - 0.6, y0, cz - 0.6), (cx + 0.6, y1, cz + 0.6), "water")


def regions(e):
    return (e["to"][0] - e["from"][0], e["to"][1] - e["from"][1], e["to"][2] - e["from"][2])


def faces(u, v, w, h, d):
    w, h, d = math.ceil(w), math.ceil(h), math.ceil(d)
    return {
        "up": (u + d, v, w, d), "down": (u + d + w, v, w, d),
        "east": (u, v + d, d, h), "north": (u + d, v + d, w, h),
        "west": (u + d + w, v + d, d, h), "south": (u + 2 * d + w, v + d, w, h),
    }


def pack():
    slots = {}
    x = y = shelf = 0
    for e in sorted(elements, key=lambda e: -math.ceil(regions(e)[1])):
        w, h, d = regions(e)
        key = (math.ceil(w), math.ceil(h), math.ceil(d), e["material"])
        if key not in slots:
            rw, rh = 2 * key[0] + 2 * key[2], key[2] + key[1]
            if x + rw > TEX_W:
                x, y = 0, y + shelf
                shelf = 0
            slots[key] = (x, y)
            x += rw
            shelf = max(shelf, rh)
    for e in elements:
        w, h, d = regions(e)
        e["uv_offset"] = list(slots[(math.ceil(w), math.ceil(h), math.ceil(d), e["material"])])
    height = 1
    while height < y + shelf:
        height *= 2
    return slots, max(height, 64)


def paint(img, slots):
    rnd = random.Random(11)
    px = img.load()
    for (w, h, d, mat), (u, v) in slots.items():
        base, shade, light, speck, speck_col = MATERIALS[mat]
        alpha = TRANSLUCENT.get(mat, 255)
        for name, (fx, fy, fw, fh) in faces(u, v, w, h, d).items():
            for j in range(fh):
                for i in range(fw):
                    n = rnd.random()
                    c = base
                    if mat in ("quartz", "quartz_dark") and name in ("north", "south", "east", "west") and fh >= 6 and j % 4 == 3:
                        c = shade
                    elif n < 0.16:
                        c = tuple(max(0, k - 9) for k in c)
                    elif n > 0.86:
                        c = tuple(min(255, k + 7) for k in c)
                    if speck_col and n < speck:
                        c = speck_col
                    edge = i == 0 or j == 0 or i == fw - 1 or j == fh - 1
                    a = alpha
                    if edge and mat != "water":
                        c = shade if (i == fw - 1 or j == fh - 1) else light
                    if mat == "water":
                        wave = math.sin(i * 0.9 + j * 0.45) + math.sin(j * 1.3 - i * 0.3)
                        c = light if wave > 1.4 else (shade if wave < -1.2 else base)
                        a = alpha + (40 if wave > 1.4 else 0)
                    if mat == "amethyst" and (i + 2 * j) % 5 == 0:
                        c = light
                    if mat == "feather" and name in ("north", "south") and i == fw // 2:
                        c = shade
                    if mat == "gold" and n > 0.93:
                        c = light
                    if 0 <= fx + i < img.width and 0 <= fy + j < img.height:
                        px[fx + i, fy + j] = c + (min(255, a),)


def bbmodel(img, height):
    buf = io.BytesIO()
    img.save(buf, "PNG")
    data = "data:image/png;base64," + base64.b64encode(buf.getvalue()).decode()
    out = []
    for e in elements:
        u, v = e["uv_offset"]
        w, h, d = regions(e)
        el = {k: e[k] for k in ("name", "type", "uuid", "from", "to", "origin", "rotation", "box_uv", "rescale", "inflate", "uv_offset")}
        el["faces"] = {k: {"uv": [x, y, x + fw, y + fh], "texture": 0} for k, (x, y, fw, fh) in faces(u, v, w, h, d).items()}
        el["color"] = 0
        out.append(el)
    return {
        "meta": {"format_version": "4.10", "model_format": "modded_entity", "box_uv": True},
        "name": "offering_fountain",
        "model_identifier": "offering_fountain",
        "visible_box": [4, 4, 2],
        "resolution": {"width": TEX_W, "height": height},
        "elements": out,
        "outliner": outliner,
        "textures": [{"path": "", "name": "offering_fountain.png", "folder": "block", "namespace": "simplywings",
                      "id": "0", "width": TEX_W, "height": height, "uv_width": TEX_W, "uv_height": height,
                      "particle": False, "render_mode": "default", "visible": True, "mode": "bitmap", "saved": True,
                      "uuid": uid(), "relative_path": "../textures/block/offering_fountain.png", "source": data}],
    }


def icon():
    img = Image.new("RGBA", (16, 16))
    p = img.load()
    quartz, shade, gold = (236, 232, 222, 255), (186, 180, 168, 255), (232, 186, 72, 255)
    water, amethyst = (80, 150, 232, 255), (170, 116, 228, 255)
    for x in range(1, 15):
        for y in (13, 14):
            p[x, y] = shade if y == 14 else quartz
        p[x, 12] = gold if x in (1, 14) else water
    for x in (1, 14):
        for y in (10, 11):
            p[x, y] = quartz
    for y in range(6, 12):
        for x in (7, 8):
            p[x, y] = quartz if y != 9 else gold
    for x in range(4, 12):
        p[x, 6] = quartz
        p[x, 5] = water if 5 <= x <= 10 else gold
    for x in range(2, 14):
        if x not in range(6, 10):
            p[x, 11 if x in (2, 13) else 11] = water
    for y, xs in ((1, (7, 8)), (2, (6, 7, 8, 9)), (3, (7, 8)), (4, (7, 8))):
        for x in xs:
            p[x, y] = amethyst
    for y in range(7, 11):
        p[5, y] = p[10, y] = water
    return img


def main():
    build()
    slots, height = pack()
    img = Image.new("RGBA", (TEX_W, height))
    paint(img, slots)
    img.save(TEXTURE)
    RIG.write_text(json.dumps(bbmodel(img, height), indent=1), encoding="utf-8", newline="\n")
    icon().save(ICON)
    print(f"{len(elements)} cubes, texture {TEX_W}x{height}, {len(slots)} uv regions")


if __name__ == "__main__":
    main()
