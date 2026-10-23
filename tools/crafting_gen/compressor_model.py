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
RIG = ASSETS / "rigs/cloud_compressor.bbmodel"
TEXTURE = ASSETS / "textures/block/cloud_compressor.png"
ICON = ASSETS / "textures/item/cloud_compressor.png"
TEX_W = 256

MATERIALS = {
    "copper": ((190, 108, 64), (150, 80, 46), (226, 146, 96), 0.05, (92, 160, 140)),
    "copper_dark": ((138, 74, 44), (104, 54, 32), (170, 98, 60), 0.08, (80, 140, 120)),
    "brass": ((212, 168, 70), (160, 120, 40), (246, 214, 120), 0.0, None),
    "iron": ((96, 98, 108), (66, 68, 78), (138, 140, 150), 0.04, (120, 70, 40)),
    "dark": ((52, 50, 58), (34, 32, 40), (78, 76, 86), 0.0, None),
    "glass": ((210, 236, 255), (150, 190, 220), (255, 255, 255), 0.0, None),
    "liquid": ((255, 214, 96), (240, 170, 50), (255, 246, 190), 0.0, None),
    "dial": ((236, 230, 210), (120, 110, 90), (255, 255, 245), 0.0, None),
    "needle": ((200, 40, 30), (150, 20, 20), (240, 90, 70), 0.0, None),
    "cloud": ((221, 242, 255), (170, 200, 220), (255, 255, 255), 0.0, None),
    "steam": ((255, 156, 90), (220, 110, 60), (255, 200, 150), 0.0, None),
    "ethereal": ((201, 168, 240), (150, 120, 200), (232, 214, 255), 0.0, None),
}

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


def cube(g, name, a, b, material, origin=None, rotation=(0, 0, 0), inflate=0):
    e = {"name": name, "type": "cube", "uuid": uid(), "from": list(a), "to": list(b),
         "origin": list(origin if origin else ((a[0] + b[0]) / 2, (a[1] + b[1]) / 2, (a[2] + b[2]) / 2)),
         "rotation": list(rotation), "box_uv": True, "rescale": False, "inflate": inflate, "material": material}
    elements.append(e)
    groups[g]["children"].append(e["uuid"])
    return e


def ring(g, name, a, y0, y1, material, sides=16, origin=(0, 0)):
    s = round(2 * a * math.tan(math.pi / sides), 1)
    s = max(1, round(s))
    for k in range(sides // 2):
        cube(g, f"{name}_{k}", (origin[0] - a, y0, origin[1] - s / 2), (origin[0] + a, y1, origin[1] + s / 2), material,
             origin=(origin[0], y0, origin[1]), rotation=(0, k * 180 / sides, 0))


def disk_x(g, name, center, radius, thick, material, teeth=0, tooth_mat=None):
    cx, cy, cz = center
    s = max(1, round(2 * radius * math.tan(math.pi / 8)))
    for k in range(4):
        cube(g, f"{name}_disk{k}", (cx - thick / 2, cy - radius, cz - s / 2), (cx + thick / 2, cy + radius, cz + s / 2),
             material, origin=center, rotation=(k * 45, 0, 0))
    if teeth:
        for k in range(4):
            cube(g, f"{name}_teeth{k}", (cx - thick / 2 + 0.25, cy - radius - 2, cz - 1), (cx + thick / 2 - 0.25, cy + radius + 2, cz + 1),
                 tooth_mat or material, origin=center, rotation=(22.5 + k * 45, 0, 0))


def build():
    frame = group("frame")
    cube(frame, "plinth", (-22, 0, -22), (22, 3, 22), "dark")
    cube(frame, "plinth_trim", (-20, 3, -20), (20, 5, 20), "iron")
    for sx in (-1, 1):
        for sz in (-1, 1):
            cube(frame, f"foot_{sx}_{sz}", (sx * 23 - 2, 0, sz * 23 - 2), (sx * 23 + 2, 4, sz * 23 + 2), "iron")
    ring(frame, "vat", 16, 5, 57, "copper")
    for i, y in enumerate((8, 31, 54)):
        ring(frame, f"band{i}", 17, y, y + 3, "brass")
    ring(frame, "dome", 13, 57, 64, "copper_dark")
    ring(frame, "crown", 8, 64, 69, "copper")
    cube(frame, "hatch", (-4, 69, -4), (4, 71, 4), "brass")
    cube(frame, "hatch_ring", (-5, 68, -5), (5, 69, 5), "iron")
    cube(frame, "chimney", (6.5, 60, -11), (11, 78, -6.5), "iron")
    cube(frame, "chimney_cap", (5.5, 78, -12), (12, 80, -5.5), "dark")
    cube(frame, "chimney_band", (6, 70, -11.5), (11.5, 71, -6), "brass")
    cube(frame, "window_left", (-8, 13, 15), (-6, 48, 17.5), "brass")
    cube(frame, "window_right", (6, 13, 15), (8, 48, 17.5), "brass")
    cube(frame, "window_top", (-8, 46, 15), (8, 48, 17.5), "brass")
    cube(frame, "window_bottom", (-8, 13, 15), (8, 15, 17.5), "brass")
    for y in (22, 30, 38):
        cube(frame, f"window_bar{y}", (-6, y, 16.5), (6, y + 1, 17), "brass")
    cube(frame, "dial", (6, 50, 15.5), (14, 58, 17), "dial")
    cube(frame, "dial_rim", (5.5, 49.5, 15), (14.5, 58.5, 15.8), "brass")
    for sx in (-1, 1):
        cube(frame, f"housing_{sx}", (sx * 11 - 2.5, 57, -2.5), (sx * 11 + 2.5, 66, 2.5), "iron")
        cube(frame, f"housing_cap_{sx}", (sx * 11 - 3, 66, -3), (sx * 11 + 3, 67, 3), "brass")
    cube(frame, "beam", (-13, 75, -1), (13, 77, 1), "iron")
    for name, (cx, cz), mat in [("cloud", (-18, -18), "cloud"), ("steam", (18, -18), "steam"),
                                ("ethereal", (-18, 18), "ethereal"), ("output", (18, 18), "liquid")]:
        cube(frame, f"can_{name}", (cx - 4, 5, cz - 4), (cx + 4, 21, cz + 4), "copper_dark")
        cube(frame, f"can_{name}_glass", (cx - 4.5, 8, cz - 4.5), (cx + 4.5, 17, cz + 4.5), mat)
        cube(frame, f"can_{name}_lid", (cx - 3, 21, cz - 3), (cx + 3, 23, cz + 3), "brass")
        angle = math.degrees(math.atan2(cx, cz))
        mid = 21.5
        px, pz = math.sin(math.radians(angle)) * mid, math.cos(math.radians(angle)) * mid
        cube(frame, f"pipe_{name}", (px - 1.5, 18, pz - 6), (px + 1.5, 21, pz + 6), "brass", origin=(px, 19.5, pz),
             rotation=(0, round(angle / 22.5) * 22.5 if abs(angle) % 45 else angle, 0))
    cube(frame, "axle_left", (-21, 24, 0), (-15, 28, 4), "dark")
    cube(frame, "axle_right", (15, 28, -4), (21, 32, 0), "dark")
    gl = group("gear_left", (-20, 26, 2), "frame")
    disk_x(gl, "gear_left", (-20, 26, 2), 7, 2, "brass", teeth=True)
    cube(gl, "gear_left_hub", (-21.5, 24.5, 0.5), (-18.5, 27.5, 3.5), "dark")
    gs = group("gear_small", (-20, 39.5, -5.5), "frame")
    disk_x(gs, "gear_small", (-20, 39.5, -5.5), 4, 2, "brass", teeth=True)
    cube(gs, "gear_small_hub", (-21.5, 38.5, -6.5), (-18.5, 40.5, -4.5), "dark")
    gr = group("gear_right", (20, 30, -2), "frame")
    disk_x(gr, "gear_right", (20, 30, -2), 9, 2, "copper_dark", teeth=True, tooth_mat="brass")
    cube(gr, "gear_right_hub", (18.5, 28.5, -3.5), (21.5, 31.5, -0.5), "dark")
    for k in range(2):
        cube(gr, f"gear_right_spoke{k}", (19.25, 22, -2.75), (20.75, 38, -1.25), "iron", origin=(20, 30, -2), rotation=(45 + k * 90, 0, 0))
    for i, sx in enumerate((-1, 1)):
        p = group("piston_a" if sx < 0 else "piston_b", (sx * 11, 66, 0), "frame")
        cube(p, f"rod_{i}", (sx * 11 - 1, 66, -1), (sx * 11 + 1, 75, 1), "iron")
        cube(p, f"rod_head_{i}", (sx * 11 - 2, 73, -2), (sx * 11 + 2, 75, 2), "brass")
    n = group("needle", (10, 54, 17.1), "frame")
    cube(n, "needle_arm", (9.75, 54, 17), (10.25, 57.5, 17.3), "needle")
    glass = group("glass")
    cube(glass, "window_glass", (-6, 15, 16.6), (6, 46, 17), "glass")
    liquid = group("liquid", (0, 15, 0))
    cube(liquid, "liquid_column", (-6, 15, 16.05), (6, 45, 16.45), "liquid")


def regions(e):
    w = e["to"][0] - e["from"][0]
    h = e["to"][1] - e["from"][1]
    d = e["to"][2] - e["from"][2]
    return w, h, d


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
    for e in elements:
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
        e["uv_offset"] = list(slots[key])
    height = 1
    while height < y + shelf:
        height *= 2
    return slots, max(height, 64)


def paint(img, slots):
    rnd = random.Random(7)
    px = img.load()
    for (w, h, d, mat), (u, v) in slots.items():
        base, shade, light, speck, speck_col = MATERIALS[mat]
        alpha = 110 if mat == "glass" else 255
        for name, (fx, fy, fw, fh) in faces(u, v, w, h, d).items():
            for j in range(fh):
                for i in range(fw):
                    n = rnd.random()
                    c = base
                    if n < 0.18:
                        c = tuple(max(0, k - 10) for k in c)
                    elif n > 0.85:
                        c = tuple(min(255, k + 8) for k in c)
                    if speck_col and n < speck:
                        c = speck_col
                    edge = i == 0 or j == 0 or i == fw - 1 or j == fh - 1
                    a = alpha
                    if edge:
                        c = shade if (i == fw - 1 or j == fh - 1) else light
                        if mat == "glass":
                            a = 200
                    if mat == "glass" and (i + j) % 7 == 0 and not edge:
                        c, a = light, 170
                    if mat == "liquid" and not edge and rnd.random() < 0.06:
                        c = light
                    if mat in ("copper", "copper_dark", "iron") and name in ("north", "south") and (j in (2, fh - 3)) and i % 3 == 1 and fh > 8:
                        c = (232, 200, 140) if mat != "iron" else (170, 172, 180)
                    if mat == "dial" and name in ("south", "north") and not edge:
                        cxp, cyp = (fw - 1) / 2, (fh - 1) / 2
                        r = math.hypot(i - cxp, j - cyp)
                        ang = math.atan2(j - cyp, i - cxp)
                        if r > fw / 2 - 1.6 and int(math.degrees(ang) + 360) % 45 < 12:
                            c = (40, 36, 30)
                    if 0 <= fx + i < img.width and 0 <= fy + j < img.height:
                        px[fx + i, fy + j] = c + (a,)


def bbmodel(img, height):
    buf = io.BytesIO()
    img.save(buf, "PNG")
    data = "data:image/png;base64," + base64.b64encode(buf.getvalue()).decode()
    out_elements = []
    for e in elements:
        u, v = e["uv_offset"]
        w, h, d = regions(e)
        fc = {k: {"uv": [x, y, x + fw, y + fh], "texture": 0} for k, (x, y, fw, fh) in faces(u, v, w, h, d).items()}
        el = {k: e[k] for k in ("name", "type", "uuid", "from", "to", "origin", "rotation", "box_uv", "rescale", "inflate", "uv_offset")}
        el["faces"] = fc
        el["color"] = 0
        out_elements.append(el)
    model = {
        "meta": {"format_version": "4.10", "model_format": "modded_entity", "box_uv": True},
        "name": "cloud_compressor",
        "model_identifier": "cloud_compressor",
        "visible_box": [3, 5, 2],
        "resolution": {"width": TEX_W, "height": height},
        "elements": out_elements,
        "outliner": outliner,
        "textures": [{"path": "", "name": "cloud_compressor.png", "folder": "block", "namespace": "simplywings",
                      "id": "0", "width": TEX_W, "height": height, "uv_width": TEX_W, "uv_height": height,
                      "particle": False, "render_mode": "default", "visible": True, "mode": "bitmap", "saved": True,
                      "uuid": uid(), "relative_path": "../textures/block/cloud_compressor.png", "source": data}],
    }
    return model


def icon():
    img = Image.new("RGBA", (16, 16))
    p = img.load()
    for y in range(16):
        for x in range(16):
            dx = abs(x - 7.5)
            if 3 <= y <= 13 and dx <= 5.5:
                c = (190, 108, 64) if dx < 4.5 else (150, 80, 46)
                if y in (5, 9, 12):
                    c = (212, 168, 70)
                if 6 <= y <= 11 and dx <= 1.6 and y not in (9,):
                    c = (255, 214, 96)
                p[x, y] = c + (255,)
            elif y == 2 and dx <= 3.5:
                p[x, y] = (138, 74, 44, 255)
            elif y == 1 and dx <= 1.5:
                p[x, y] = (212, 168, 70, 255)
            elif y in (14, 15) and dx <= 7:
                p[x, y] = (52, 50, 58, 255) if y == 15 else (96, 98, 108, 255)
    for y in range(0, 6):
        p[11, y] = (96, 98, 108, 255)
    p[3, 7] = p[3, 8] = (212, 168, 70, 255)
    p[2, 7] = p[2, 8] = p[2, 6] = (160, 120, 40, 255)
    return img


def main():
    build()
    slots, height = pack()
    img = Image.new("RGBA", (TEX_W, height))
    paint(img, slots)
    TEXTURE.parent.mkdir(parents=True, exist_ok=True)
    img.save(TEXTURE)
    RIG.write_text(json.dumps(bbmodel(img, height), indent=1), encoding="utf-8", newline="\n")
    icon().save(ICON)
    print(f"{len(elements)} cubes, texture {TEX_W}x{height}, {len(slots)} uv regions")


if __name__ == "__main__":
    main()
