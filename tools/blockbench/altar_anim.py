import base64
import io
import json
import math
import random
import sys
from pathlib import Path

from PIL import Image

sys.path.insert(0, str(Path(__file__).resolve().parent))
import altar_scene as scene  # noqa: E402
import gacha_sprites as sprites  # noqa: E402

ROOT = scene.ROOT
OUT = ROOT / "art/gacha/summoning_animation.bbmodel"
RIGS = ROOT / "src/main/resources/assets/simplywings/rigs"
ITEM_TEX = ROOT / "src/main/resources/assets/simplywings/textures/item/summoning_feather.png"
B = 16
FLOOR_TOP = 0
FLOOR_LIFT = 32.2
FLAME_BASE = 2 * B
HOVER = (0, 6 * B, 0)
SIGIL_Y = 96 * B
WING_Y = 14 * B
PLAYER = (0, 0, 72)
CRYSTAL = (0, 50, 0)
V = 3

FLAMES = [("fire", (-4, 6)), ("dark", (-7, 0)), ("earth", (-4, -6)), ("water", (4, -6)), ("light", (7, 0)), ("air", (4, 6))]
IGNITE = {name: 0.4 + 0.22 * i for i, (name, _) in enumerate(FLAMES)}
FLYER_MODELS = {"common": ["basic", "basic"], "rare": ["eagle", "owl"], "epic": ["storm", "forest"],
                "legendary": ["angel", "seraph"], "mythic": ["night", "light"]}
FLYERS_PER_CATEGORY = 3
CLOUDS = 24

COLORS = {
    "red": (205, 45, 25), "orange": (255, 125, 25), "yellow": (255, 222, 90), "ember": (255, 170, 60),
    "black": (24, 12, 34), "purple": (72, 22, 115), "violet": (172, 84, 255), "eye": (255, 96, 40),
    "rock": (92, 82, 72), "moss": (82, 142, 52), "crystal": (96, 232, 128), "dirt": (112, 76, 46),
    "deep": (22, 72, 182), "water": (52, 142, 232), "foam": (214, 242, 255),
    "gold": (255, 210, 92), "white": (255, 252, 236), "ray": (255, 240, 170, 170),
    "pale": (206, 238, 244), "cyan": (150, 222, 236), "leaf": (172, 222, 122),
    "s_white": (255, 255, 255), "s_blue": (90, 170, 255), "s_violet": (180, 108, 255), "s_gold": (255, 194, 58),
    "s_red": (255, 74, 94),
    "rune": (206, 146, 255), "circle": (186, 126, 255), "circle_hot": (236, 206, 255), "energy": (200, 150, 255, 150),
    "pillar": (214, 176, 255, 120), "pillar_core": (255, 255, 255, 220),
    "spectral": (52, 26, 86, 200), "spectral_glow": (206, 126, 255), "feather": (238, 228, 255, 215),
    "feather_edge": (184, 146, 255, 215), "crack": (255, 42, 64), "wave": (236, 214, 255, 190), "flash": (255, 255, 255, 200),
    "skin": (196, 146, 110), "hair": (70, 46, 26), "shirt": (0, 160, 168), "pants": (56, 58, 150), "shoe": (70, 70, 74),
    "shadow": (14, 8, 22), "shadow_edge": (88, 40, 148), "cloud": (236, 236, 248, 235), "cloud_shade": (176, 172, 204, 235),
    "eclipse": (6, 2, 12),
}
for _el, _ramp in sprites.FLAME_RAMPS.items():
    for _i, _c in enumerate(_ramp):
        COLORS[f"f_{_el}_{_i}"] = _c + ((215,) if _el in ("water", "air") else ())


class Palette:
    CELL = 4

    def __init__(self):
        self.names = list(COLORS)
        self.size = 128
        img = Image.new("RGBA", (self.size, self.size), (0, 0, 0, 0))
        for i, name in enumerate(self.names):
            c = COLORS[name]
            rgb, a = c[:3], c[3] if len(c) > 3 else 255
            shades = [tuple(min(255, int(v * 1.22 + 12)) for v in rgb), rgb, tuple(int(v * 0.62) for v in rgb)]
            for s, shade in enumerate(shades):
                x, y = self.cell(i, s)
                for dx in range(self.CELL):
                    for dy in range(self.CELL):
                        img.putpixel((x + dx, y + dy), shade + (a,))
        buf = io.BytesIO()
        img.save(buf, "PNG")
        self.png = buf.getvalue()

    def cell(self, index, shade):
        n = index * 3 + shade
        per_row = self.size // self.CELL
        return (n % per_row) * self.CELL, (n // per_row) * self.CELL

    def uv(self, name, shade):
        x, y = self.cell(self.names.index(name), shade)
        return [x + 1, y + 1, x + 3, y + 3]


def smooth(t):
    t = max(0.0, min(1.0, t))
    return t * t * (3 - 2 * t)


def ease_out_expo(t):
    t = max(0.0, min(1.0, t))
    return 1 - 2 ** (-10 * t) if t < 1 else 1.0


def ease_in_out(t):
    t = max(0.0, min(1.0, t))
    return 0.5 - 0.5 * math.cos(math.pi * t)


class Noise1:
    def __init__(self, seed):
        rnd = random.Random(seed)
        self.parts = [(rnd.uniform(0.6, 1.0), rnd.uniform(3.0, 9.0), rnd.uniform(0, 6.28)) for _ in range(3)]

    def __call__(self, t):
        return sum(a * math.sin(f * t + p) for a, f, p in self.parts) / 2.2


class Textures:
    def __init__(self, scene_tex):
        self.entries = [{"ref": ref, "data": data} for ref, data in scene_tex.list]
        self.by_ref = {e["ref"]: i for i, e in enumerate(self.entries)}

    def add(self, ref, data, uv=None, frames=None, path=None):
        if ref in self.by_ref:
            return self.by_ref[ref]
        img = Image.open(io.BytesIO(data))
        w, h = img.size
        entry = {"ref": ref, "data": data, "width": w, "height": h, "uv_width": (uv or (w, h))[0], "uv_height": (uv or (w, h))[1], "path": path}
        if frames:
            entry.update({"uv_height": entry["uv_width"], "frame_time": frames})
        self.entries.append(entry)
        self.by_ref[ref] = len(self.entries) - 1
        return self.by_ref[ref]

    def json(self):
        out = []
        for i, e in enumerate(self.entries):
            w, h = e.get("width", 16), e.get("height", 16)
            t = {"name": e["ref"].split("/")[-1] + ".png", "folder": "", "namespace": "", "id": str(i),
                 "width": w, "height": h, "uv_width": e.get("uv_width", 16), "uv_height": e.get("uv_height", 16),
                 "particle": False, "render_mode": "default", "render_sides": "auto", "visible": True,
                 "internal": True, "saved": True, "uuid": scene.new_id(),
                 "source": "data:image/png;base64," + base64.b64encode(e["data"]).decode()}
            if "frame_time" in e:
                t.update({"frame_time": e["frame_time"], "frame_order_type": "loop", "frame_interpolate": False})
            out.append(t)
        return out


class Rig:
    def __init__(self, palette, textures):
        self.palette = palette
        self.textures = textures
        self.tex = textures.add("simplywings:gacha/palette", palette.png, path="simplywings:textures/gacha/palette.png")
        self.meta = {}
        self.groups = {}
        self.roots = []
        self.elements = []
        self.keys = {}
        self.lengths = {}
        self.offsets = {}
        self.by_uuid = {}

    def scale_tree(self, bone, factor, base):
        def move(v):
            return [base[k] + (v[k] - base[k]) * factor for k in range(3)]

        def walk(g):
            g["origin"] = move(g["origin"])
            for child in g["children"]:
                if isinstance(child, dict):
                    walk(child)
                else:
                    e = self.by_uuid[child]
                    e["from"], e["to"], e["origin"] = move(e["from"]), move(e["to"]), move(e["origin"])
                    if "inflate" in e:
                        e["inflate"] *= factor

        walk(self.groups[bone])

    def bone(self, name, origin, parent=None, rotation=None):
        g = {"name": name, "origin": [float(v) for v in origin], "rotation": rotation or [0, 0, 0], "color": 0,
             "uuid": scene.new_id(), "export": True, "isOpen": False, "locked": False, "visibility": True,
             "autouv": 0, "children": []}
        self.groups[name] = g
        (self.groups[parent]["children"] if parent else self.roots).append(g)
        return name

    def add_element(self, bone, element):
        self.elements.append(element)
        self.by_uuid[element["uuid"]] = element
        self.groups[bone]["children"].append(element["uuid"])
        return element

    def box(self, bone, center, size, color, rot=None, pivot=None, name=None):
        frm = [center[k] - size[k] / 2 for k in range(3)]
        to = [center[k] + size[k] / 2 for k in range(3)]
        faces = {}
        for face in ("north", "east", "south", "west", "up", "down"):
            shade = 0 if face == "up" else 2 if face == "down" else 1
            faces[face] = {"uv": self.palette.uv(color, shade), "texture": self.tex}
        element = scene.cube(name or f"{bone}_{len(self.groups[bone]['children'])}", frm, to, faces,
                             origin=list(pivot or center), rotation=rot)
        return self.add_element(bone, element)

    def plane(self, bone, center, w, h, texture, rot=None, uv=(16, 16)):
        frm = [center[0] - w / 2, center[1] - h / 2, center[2]]
        to = [center[0] + w / 2, center[1] + h / 2, center[2]]
        faces = {"north": {"uv": [0, 0, uv[0], uv[1]], "texture": texture}, "south": {"uv": [0, 0, uv[0], uv[1]], "texture": texture}}
        element = scene.cube(f"{bone}_plane_{len(self.groups[bone]['children'])}", frm, to, faces, origin=list(center), rotation=rot)
        return self.add_element(bone, element)

    def crossed(self, bone, center, w, h, texture, count=3, uv=(16, 16), tilt=0):
        for i in range(count):
            self.plane(bone, center, w, h, texture, rot=[tilt, 180 / count * i, 0], uv=uv)

    def key(self, anim, bone, channel, t, vec, interp="catmullrom", absolute=False, raw=False):
        t = t if absolute else t + self.offsets.get(anim, 0.0)
        if raw:
            pass
        elif channel == "position":
            vec = (-vec[0], vec[1], vec[2])
        elif channel == "rotation":
            vec = (-vec[0], -vec[1], vec[2])
        self.keys.setdefault(anim, {}).setdefault(bone, {}).setdefault(channel, {})[round(t, 4)] = (tuple(vec), interp)

    def sample(self, anim, bone, channel, t0, t1, fn, rate=12, interp="catmullrom", extra=(), absolute=False, raw=False):
        n = max(1, int(math.ceil((t1 - t0) * rate)))
        times = sorted(set([t0 + (t1 - t0) * i / n for i in range(n + 1)] + [t for t in extra if t0 < t < t1]))
        for t in times:
            self.key(anim, bone, channel, t, fn(t), interp, absolute, raw)

    def hide_until(self, anim, bone, t):
        self.key(anim, bone, "scale", 0, (0, 0, 0), "linear", absolute=True)
        self.key(anim, bone, "scale", t, (0, 0, 0), "linear")

    def animations(self):
        out = []
        for anim, bones in self.keys.items():
            length = self.lengths[anim]
            animators = {}
            for bone, channels in bones.items():
                scale = channels.get("scale")
                if scale:
                    last_t = max(scale)
                    last = scale[last_t][0]
                    if any(abs(v) < 1e-6 for v in last) and any(abs(v) > 1e-6 for v in last):
                        scale[round(last_t + 0.01, 4)] = ((0, 0, 0), "linear")
                frames = []
                for channel, keys in channels.items():
                    for t, (vec, interp) in sorted(keys.items()):
                        frames.append({"channel": channel, "data_points": [{"x": round(vec[0], 3), "y": round(vec[1], 3),
                                                                             "z": round(vec[2], 3)}],
                                       "time": t, "interpolation": interp})
                animators[self.groups[bone]["uuid"]] = {"name": bone, "type": "bone", "keyframes": frames}
            out.append({"uuid": scene.new_id(), "name": anim, "loop": "loop" if anim == "idle" else "once",
                        "override": False, "length": length, "snapping": 24, "selected": False,
                        "anim_time_update": "", "blend_weight": "", "start_delay": "", "loop_delay": "",
                        "animators": animators})
        return out


def ring(rig, bone, center, radius, count, color, thick=4, height=3, glow=None):
    if glow:
        ring(rig, bone, center, radius, count, glow, thick=thick * 2.6, height=height * 0.4)
    cx, cy, cz = center
    seg = 2 * math.pi * radius / count * 0.74
    for i in range(count):
        a = 2 * math.pi * i / count
        x, z = cx + radius * math.cos(a), cz + radius * math.sin(a)
        rig.box(bone, (x, cy, z), (seg, height, thick), color, rot=[0, -math.degrees(a) + 90, 0])


def merge_cells(rig, bone, cells, v, origin):
    ox, oy, oz = origin
    rows = {}
    for (i, j, k), color in cells.items():
        rows.setdefault((j, k, color), []).append(i)
    runs = {}
    for (j, k, color), xs in rows.items():
        xs.sort()
        start = prev = xs[0]
        for x in xs[1:] + [None]:
            if x is not None and x == prev + 1:
                prev = x
                continue
            runs.setdefault((j, start, prev, color), []).append(k)
            if x is not None:
                start = prev = x
    rects = {}
    for (j, i0, i1, color), ks in runs.items():
        ks.sort()
        start = prev = ks[0]
        for k in ks[1:] + [None]:
            if k is not None and k == prev + 1:
                prev = k
                continue
            rects.setdefault((i0, i1, start, prev, color), []).append(j)
            if k is not None:
                start = prev = k
    count = 0
    for (i0, i1, k0, k1, color), js in rects.items():
        js.sort()
        start = prev = js[0]
        for j in js[1:] + [None]:
            if j is not None and j == prev + 1:
                prev = j
                continue
            cx, cy, cz = (i0 + i1) / 2 * v, (start + prev) / 2 * v, (k0 + k1) / 2 * v
            rig.box(bone, (ox + cx, oy + cy, oz + cz), ((i1 - i0 + 1) * v, (prev - start + 1) * v, (k1 - k0 + 1) * v), color)
            count += 1
            if j is not None:
                start = prev = j
    return count


def voxel_shell(rig, bone, center, inside, extent, v, color_fn):
    n = int(math.ceil(extent / v))
    cells = {}
    for i in range(-n, n + 1):
        for j in range(-n, n + 1):
            for k in range(-n, n + 1):
                if inside(i * v, j * v, k * v):
                    cells[(i, j, k)] = color_fn(i * v, j * v, k * v)
    return merge_cells(rig, bone, cells, v, center)


def flame_positions():
    for name, (dx, dz) in FLAMES:
        yield name, dx * B, dz * B, math.degrees(math.atan2(dx, dz))


FLAME_SPEC = {
    "fire": {"H": 66, "R": 11.5, "twist": 0.25, "jag": 0.45},
    "dark": {"H": 70, "R": 11.5, "twist": 0.5, "jag": 0.35},
    "earth": {"H": 56, "R": 12, "twist": 0.1, "jag": 0.55},
    "water": {"H": 64, "R": 11, "twist": 0.65, "jag": 0.3},
    "light": {"H": 72, "R": 10.5, "twist": 0.15, "jag": 0.25},
    "air": {"H": 74, "R": 10.5, "twist": 1.1, "jag": 0.4},
}
BANDS = [(0.0, 0.3), (0.3, 0.55), (0.55, 0.78), (0.78, 1.0)]


def flame_radius(name, spec, x, y, z, rnd_field):
    H, R = spec["H"], spec["R"]
    h = y / H
    if h < 0 or h >= 1:
        return None
    r = R * (0.68 + 0.32 * math.sin(math.pi * min(h / 0.3, 1.0) / 2)) * (1 - h) ** 1.3
    ang = math.atan2(z, x)
    r *= 1 + spec["jag"] * h * rnd_field(ang, h)
    ox = R * 0.35 * h * math.sin(h * 5.5) * spec["twist"]
    oz = R * 0.35 * h * math.cos(h * 5.5) * spec["twist"]
    return r, ox, oz


def build_flame(rig, name, x, z, tex):
    spec = FLAME_SPEC[name]
    H, R = spec["H"], spec["R"]
    rnd = random.Random(hash(name) & 0xFFFF)
    lobes = [(rnd.uniform(2, 5), rnd.uniform(0, 6.28), rnd.uniform(0.5, 1.0)) for _ in range(3)]

    def field(ang, h):
        return sum(a * math.sin(f * ang + p + h * 7) for f, p, a in lobes) / 2.0

    root = rig.bone(f"flame_{name}", (x, FLAME_BASE, z))
    body = rig.bone(f"flame_{name}_body", (x, FLAME_BASE, z), root)
    cells = {}
    n = int(R * 1.6 / V) + 2
    for i in range(-n, n + 1):
        for k in range(-n, n + 1):
            for j in range(0, int(H / V) + 1):
                px, py, pz = i * V, j * V + V / 2, k * V
                rr = flame_radius(name, spec, px, py, pz, field)
                if rr is None:
                    continue
                r, ox, oz = rr
                if math.hypot(px - ox, pz - oz) <= r:
                    cells[(i, j, k)] = py / H
    bands = [rig.bone(f"flame_{name}_band_{b}", (x, FLAME_BASE + lo * H, z), body) for b, (lo, hi) in enumerate(BANDS)]
    ramp = [f"f_{name}_{i}" for i in range(6)]
    per_band = [{} for _ in BANDS]
    for (i, j, k), h in cells.items():
        b = next(idx for idx, (lo, hi) in enumerate(BANDS) if lo <= h < hi or idx == len(BANDS) - 1)
        edge = math.hypot(i, k) * V / max(1.0, R * (1 - h) ** 1.3)
        level = (1 - h) * 3.6 + (1 - min(1.0, edge)) * 1.4 + rnd.uniform(-0.45, 0.45)
        color = ramp[max(0, min(5, int(level)))]
        if name == "earth" and rnd.random() < 0.12 and h < 0.7:
            color = "rock"
        per_band[b][(i, j, k)] = color
    for b, band_cells in enumerate(per_band):
        if band_cells:
            merge_cells(rig, bands[b], band_cells, V, (x, FLAME_BASE + V / 2, z))
    for t in range(3):
        tb = rig.bone(f"flame_{name}_tongue_{t}", (x, FLAME_BASE + H * 0.8, z), body)
        a = t * 2.1
        bx, bz = x + 2.5 * math.cos(a), z + 2.5 * math.sin(a)
        for dy, s in ((0, 2), (V, 2), (2 * V, 1)):
            for dx in range(-s + 1, s):
                rig.box(tb, (bx + dx * V * 0.5, FLAME_BASE + H * 0.82 + dy, bz), (V * 0.9, V * 0.9, V * 0.9), ramp[4 + (dy > V)])
    sheet = rig.bone(f"flame_{name}_sheet", (x, FLAME_BASE, z), body)
    rig.crossed(sheet, (x, FLAME_BASE + H * 0.55, z), R * 2.6, H * 1.2, tex, count=3, uv=(32, 32))
    if name == "dark":
        eyes = rig.bone("flame_dark_eyes", (x, FLAME_BASE + H * 0.4, z), body)
        fx, fz = -x / math.hypot(x, z), -z / math.hypot(x, z)
        for s in (-1, 1):
            rig.box(eyes, (x + fx * (R * 0.7) + fz * 3 * s, FLAME_BASE + H * 0.42, z + fz * (R * 0.7) - fx * 3 * s), (2.6, 1.4, 2.6), "eye")


def build_flames(rig):
    for name, x, z, _ in flame_positions():
        tex = rig.textures.add(f"simplywings:gacha/flame_{name}", SPRITES[f"flame_{name}"], frames=2, path=f"simplywings:textures/gacha/flame_{name}.png")
        build_flame(rig, name, x, z, tex)


def build_floor(rig):
    rig.bone("runes", (0, 0, 0))
    for i in range(24):
        a = 2 * math.pi * i / 24
        x, z = 72 * math.cos(a), 72 * math.sin(a)
        b = rig.bone(f"rune_{i}", (x, FLOOR_LIFT + 0.5, z), "runes")
        rig.box(b, (x, FLOOR_LIFT + 0.6, z), (3, 1, 7), "rune", rot=[0, -math.degrees(a), 0])
        rig.box(b, (x, FLOOR_LIFT + 1.1, z), (1.5, 0.6, 2.5), "circle_hot", rot=[0, -math.degrees(a) + 45, 0])
    rig.bone("floor_circle", (0, FLOOR_LIFT + 0.4, 0))
    rig.bone("floor_ring_outer", (0, FLOOR_LIFT + 0.4, 0), "floor_circle")
    ring(rig, "floor_ring_outer", (0, FLOOR_LIFT + 0.4, 0), 128, 80, "circle", thick=2.5, height=0.8, glow="energy")
    for i in range(24):
        a = 2 * math.pi * i / 24
        rig.box("floor_ring_outer", (134 * math.cos(a), FLOOR_LIFT + 0.5, 134 * math.sin(a)), (3, 1, 3), "circle_hot", rot=[0, 45 + i * 15, 0])
    rig.bone("floor_ring_inner", (0, FLOOR_LIFT + 0.4, 0), "floor_circle")
    ring(rig, "floor_ring_inner", (0, FLOOR_LIFT + 0.4, 0), 96, 60, "circle", thick=1.5, height=0.8)
    for _, x, z, _ in flame_positions():
        a = math.atan2(z, x)
        rig.box("floor_circle", (76 * math.cos(a), FLOOR_LIFT + 0.5, 76 * math.sin(a)), (70, 0.8, 1.5), "circle_hot", rot=[0, -math.degrees(a), 0])


BEAM_COLORS = {"fire": "f_fire_3", "dark": "f_dark_3", "earth": "f_earth_3", "water": "f_water_3", "light": "f_light_3", "air": "f_air_2"}


def build_streams(rig):
    rig.bone("element_beams", (0, 0, 0))
    for name, x, z, _ in flame_positions():
        top = WING_Y + 120
        base = FLAME_BASE + FLAME_SPEC[name]["H"]
        b = rig.bone(f"beam_{name}", (x, base, z), "element_beams")
        rig.box(b, (x, (base + top) / 2, z), (4, top - base, 4), BEAM_COLORS[name])
        rig.box(b, (x, (base + top) / 2, z), (10, top - base, 10), "energy")
        voxel_shell(rig, b, (x, top, z), lambda px, py, pz: abs(px) + abs(py) + abs(pz) <= 9, 9, 3, lambda *a: BEAM_COLORS[name])
    rig.bone("streams", (0, 0, 0))
    for name, x, z, _ in flame_positions():
        base = FLAME_BASE + FLAME_SPEC[name]["H"] * 0.8
        b = rig.bone(f"stream_{name}", (x, base, z), "streams")
        p0, p2, p1 = (x, base, z), HOVER, (x * 0.4, 170, z * 0.4)
        for i in range(22):
            t = (i + 0.5) / 22
            pt = [(1 - t) ** 2 * p0[k] + 2 * (1 - t) * t * p1[k] + t * t * p2[k] for k in range(3)]
            s = 3.2 - 1.4 * t
            rig.box(b, pt, (s, s, s), BEAM_COLORS[name] if i % 3 else "circle_hot", rot=[45, 45, 0])


def build_sky(rig):
    for k, (h, r, n) in enumerate(((10 * B, 5 * B, 44), (24 * B, 8 * B, 60), (44 * B, 12 * B, 80))):
        root = rig.bone(f"stack_{k + 1}", (0, h, 0))
        ring(rig, root, (0, h, 0), r, n, "circle", thick=3, height=2, glow="energy")
        inner = rig.bone(f"stack_{k + 1}_inner", (0, h, 0), root)
        ring(rig, inner, (0, h, 0), r * 0.72, n - 16, "circle_hot", thick=1.5, height=1.2)
        for i in range(12):
            a = 2 * math.pi * i / 12
            rig.box(inner, (r * 0.86 * math.cos(a), h, r * 0.86 * math.sin(a)), (4, 1.5, 4), "rune", rot=[0, 45 + 30 * i, 0])
    sig = rig.bone("sky_sigil", (0, SIGIL_Y, 0))
    for nm, r, n, c, th in (("outer", 480, 120, "circle", 7), ("mid", 400, 100, "circle_hot", 4), ("inner", 260, 72, "circle", 5)):
        b = rig.bone(f"sky_sigil_{nm}", (0, SIGIL_Y, 0), sig)
        ring(rig, b, (0, SIGIL_Y, 0), r, n, c, thick=th, height=4, glow="energy")
    for i in range(48):
        a = 2 * math.pi * i / 48
        rig.box("sky_sigil_mid", (440 * math.cos(a), SIGIL_Y, 440 * math.sin(a)), (7, 3, 7), "rune", rot=[0, 45 + i * 7.5, 0])
    br = rig.bone("sky_sigil_branches", (0, SIGIL_Y, 0), sig)
    for i in range(12):
        a = 2 * math.pi * i / 12
        rig.box(br, (370 * math.cos(a), SIGIL_Y, 370 * math.sin(a)), (220, 2, 3), "circle_hot", rot=[0, -math.degrees(a), 0])
        voxel_shell(rig, br, (500 * math.cos(a), SIGIL_Y, 500 * math.sin(a)), lambda px, py, pz: abs(px) + abs(py) * 2 + abs(pz) <= 12, 12, 4,
                    lambda *a: "rune")
    teeth = rig.bone("sky_sigil_teeth", (0, SIGIL_Y, 0), sig)
    for i in range(32):
        a = 2 * math.pi * (i + 0.5) / 32
        ln = 40 if i % 2 else 90
        rig.box(teeth, (470 * math.cos(a), SIGIL_Y - ln / 2, 470 * math.sin(a)), (4, ln, 4), "circle_hot" if i % 2 else "rune", rot=[0, 45, 0])
        rig.box(teeth, (470 * math.cos(a), SIGIL_Y - ln - 4, 470 * math.sin(a)), (7, 7, 7), "s_violet", rot=[45, 45, 0])
    hexa = rig.bone("sky_sigil_hex", (0, SIGIL_Y, 0), sig)
    for tri in range(2):
        for j in range(3):
            a0 = 2 * math.pi * j / 3 + tri * math.pi / 3
            a1 = a0 + 2 * math.pi / 3
            p0 = (250 * math.cos(a0), 250 * math.sin(a0))
            p1 = (250 * math.cos(a1), 250 * math.sin(a1))
            mid = ((p0[0] + p1[0]) / 2, (p0[1] + p1[1]) / 2)
            ang = math.degrees(math.atan2(p1[1] - p0[1], p1[0] - p0[0]))
            rig.box(hexa, (mid[0], SIGIL_Y, mid[1]), (433, 2, 2.5), "circle", rot=[0, -ang, 0])
    pil = rig.bone("pillar", (0, FLOOR_TOP, 0))
    rig.box(pil, (0, SIGIL_Y / 2, 0), (22, SIGIL_Y, 22), "pillar")
    rig.box(pil, (0, SIGIL_Y / 2, 0), (8, SIGIL_Y, 8), "pillar_core")
    rig.box(pil, (0, SIGIL_Y / 2, 0), (48, SIGIL_Y, 48), "energy")
    for h in ("a", "b"):
        sign = 1 if h == "a" else -1
        hb = rig.bone(f"pillar_helix_{h}", (0, 0, 0), pil)
        for i in range(130):
            u = i / 130
            a = sign * u * 2 * math.pi * 8 + (0 if h == "a" else math.pi)
            rig.box(hb, (36 * math.cos(a), u * SIGIL_Y, 36 * math.sin(a)), (5, 5, 5), "circle_hot" if i % 3 else "s_white", rot=[45, -math.degrees(a), 0])
    cr = rig.bone("cracks", (0, SIGIL_Y, 0))
    rnd = random.Random(9)
    for i in range(14):
        a = 2 * math.pi * i / 14 + rnd.uniform(-0.2, 0.2)
        b = rig.bone(f"crack_{i}", (300 * math.cos(a), SIGIL_Y, 300 * math.sin(a)), cr)
        px, py, pz = 300 * math.cos(a), SIGIL_Y, 300 * math.sin(a)
        for s in range(8):
            da = a + rnd.uniform(-0.4, 0.4)
            ln = rnd.uniform(36, 70)
            nx, ny, nz = px + ln * math.cos(da), py + rnd.uniform(-34, 34), pz + ln * math.sin(da)
            rig.box(b, ((px + nx) / 2, (py + ny) / 2, (pz + nz) / 2), (ln, 3, 3), "crack",
                    rot=[0, -math.degrees(da), math.degrees(math.atan2(ny - py, ln))])
            if rnd.random() < 0.4:
                ba = da + rnd.choice((-1, 1)) * rnd.uniform(0.6, 1.0)
                bl = ln * 0.5
                rig.box(b, (nx + bl / 2 * math.cos(ba), ny, nz + bl / 2 * math.sin(ba)), (bl, 2, 2), "crack", rot=[0, -math.degrees(ba), 0])
            px, py, pz = nx, ny, nz


def octa(rig, bone, center, radius, v, color, core="s_white"):
    return voxel_shell(rig, bone, center, lambda px, py, pz: abs(px) + abs(py) + abs(pz) <= radius, radius, v,
                       lambda px, py, pz: core if max(abs(px), abs(py), abs(pz)) >= radius - v * 0.5 else color)


def build_star(rig):
    glow = rig.textures.add("simplywings:gacha/glow", SPRITES["glow"], frames=2, path="simplywings:textures/gacha/glow.png")
    rig.bone("star", (0, 0, 0))
    for c in ("white", "blue", "violet", "gold", "red"):
        b = rig.bone(f"star_{c}", (0, 0, 0), "star")
        octa(rig, b, (0, 0, 0), 20, 4, f"s_{c}")
        rig.box(b, (0, 0, 0), (3, 120, 3), "s_white")
        rig.box(b, (0, 0, 0), (120, 3, 3), "s_white")
        rig.box(b, (0, 0, 0), (3, 3, 120), "s_white")
        rig.box(b, (0, 0, 0), (2.5, 80, 2.5), f"s_{c}", rot=[45, 0, 0])
        rig.box(b, (0, 0, 0), (2.5, 80, 2.5), f"s_{c}", rot=[-45, 0, 0])
    sg = rig.bone("star_glow", (0, 0, 0), "star")
    rig.crossed(sg, (0, 0, 0), 110, 110, glow, count=3, uv=(32, 32))
    sr = rig.bone("star_ring", (0, 0, 0), "star")
    ring(rig, sr, (0, 0, 0), 60, 40, "circle_hot", thick=2.5, height=1.5)
    for g in range(8):
        b = rig.bone(f"star_ghost_{g}", (0, 0, 0))
        rig.crossed(b, (0, 0, 0), 70, 70, glow, count=2, uv=(32, 32))
    fl = rig.bone("flash", HOVER)
    rig.crossed(fl, HOVER, 60, 60, glow, count=3, uv=(32, 32))
    for i in range(16):
        rig.box(fl, HOVER, (1.5, 1.5, 70 + 20 * (i % 3)), "s_white" if i % 2 else "circle_hot", rot=[(i * 47) % 180 - 90, (i * 73) % 360, 0])
    birth = rig.bone("birth_flash", (0, SIGIL_Y, 0))
    rig.crossed(birth, (0, SIGIL_Y, 0), 60, 60, glow, count=3, uv=(32, 32))


def build_waves(rig):
    for i in range(3):
        b = rig.bone(f"ground_wave_{i}", (0, 2, 0))
        ring(rig, b, (0, 2, 0), 32, 64, "wave", thick=1.6, height=1.2)
    for i in range(4):
        b = rig.bone(f"beat_wave_{i}", (0, 0, 0))
        ring(rig, b, (0, 0, 0), 32, 56, "wave", thick=1.4, height=1.2)
    b = rig.bone("pillar_wave", (0, 2, 0))
    ring(rig, b, (0, 2, 0), 32, 64, "circle_hot", thick=1.6, height=1)


def segment(rig, bone, p0, p1, width, height, color, roll=0):
    dx, dy, dz = p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]
    ln = math.sqrt(dx * dx + dy * dy + dz * dz)
    yaw = math.degrees(math.atan2(dx, dz))
    pitch = -math.degrees(math.atan2(dy, math.hypot(dx, dz)))
    mid = ((p0[0] + p1[0]) / 2, (p0[1] + p1[1]) / 2, (p0[2] + p1[2]) / 2)
    rig.box(bone, mid, (width, height, ln), color, rot=[pitch, yaw, roll])


def feather(rig, bone, attach, theta, length, width, color, z, s):
    d = (math.sin(theta) * s, -math.cos(theta))
    center = (attach[0] + d[0] * length / 2, attach[1] + d[1] * length / 2, z)
    rig.box(bone, center, (width, length, 1.2), color, rot=[0, 0, -math.degrees(theta) * s])


def build_wings(rig):
    rig.bone("wings", (0, WING_Y, 0))
    for side, s in (("r", 1), ("l", -1)):
        sh = (56 * s, WING_Y, 0)
        elbow = (170 * s, WING_Y + 80, 0)
        wrist = (310 * s, WING_Y + 120, 0)
        tip = (420 * s, WING_Y + 90, 0)
        arm = rig.bone(f"wing_{side}", sh, "wings")
        segment(rig, arm, sh, elbow, 7, 7, "feather_edge")
        fore = rig.bone(f"wing_{side}_fore", elbow, arm)
        segment(rig, fore, elbow, wrist, 6, 6, "feather_edge")
        hand = rig.bone(f"wing_{side}_hand", wrist, fore)
        segment(rig, hand, wrist, tip, 5, 5, "feather_edge")
        cov = rig.bone(f"wing_{side}_coverts", sh, arm)
        for k in range(20):
            u = (k + 0.5) / 20
            if u < 0.45:
                q = tuple(sh[m] + (elbow[m] - sh[m]) * u / 0.45 for m in range(3))
                bone = arm
            else:
                q = tuple(elbow[m] + (wrist[m] - elbow[m]) * (u - 0.45) / 0.55 for m in range(3))
                bone = fore
            feather(rig, bone, q, math.radians(4 + 40 * u), 120 + 60 * u, 18, "feather" if k % 2 else "feather_edge", 0, s)
            feather(rig, cov, (q[0], q[1] + 4, 0), math.radians(10 + 36 * u), 56 + 14 * u, 15, "feather_edge" if k % 2 else "feather", 2.0, s)
        for k in range(14):
            u = k / 13
            q = tuple(wrist[m] + (tip[m] - wrist[m]) * u * 0.8 for m in range(3))
            feather(rig, hand, q, math.radians(40 + 48 * u), 210 + 40 * math.sin(math.pi * u * 0.8), 17, "feather_edge" if k % 2 else "feather", -1.4, s)


def player_model(rig, bone, origin, shadow=False):
    ox, oy, oz = origin
    c = (lambda n: "shadow") if shadow else (lambda n: n)
    rig.box(bone, (ox, oy + 28, oz), (8, 8, 8), c("skin"))
    if not shadow:
        rig.box(bone, (ox, oy + 31.5, oz + 0.5), (8.4, 1.6, 8.4), "hair")
    else:
        rig.box(bone, (ox - 1.6, oy + 28.5, oz - 4.1), (1.6, 1, 0.4), "spectral_glow")
        rig.box(bone, (ox + 1.6, oy + 28.5, oz - 4.1), (1.6, 1, 0.4), "spectral_glow")
    rig.box(bone, (ox, oy + 18, oz), (8, 12, 4), c("shirt"))
    rig.bone(f"{bone}_arm_r", (ox - 5, oy + 22, oz), bone)
    rig.box(f"{bone}_arm_r", (ox - 6, oy + 18, oz), (4, 12, 4), c("skin"))
    rig.bone(f"{bone}_arm_l", (ox + 5, oy + 22, oz), bone)
    rig.box(f"{bone}_arm_l", (ox + 6, oy + 18, oz), (4, 12, 4), c("skin"))
    for s in (-1, 1):
        rig.box(bone, (ox + 2 * s, oy + 6, oz), (4, 12, 4), c("pants"))
        rig.box(bone, (ox + 2 * s, oy + 1, oz), (4.2, 2, 4.2), c("shoe"))


def import_rig(rig, model, parent, prefix):
    data = json.loads((RIGS / f"{model}.bbmodel").read_text(encoding="utf-8"))
    tex_map = {}
    for i, t in enumerate(data["textures"]):
        raw = base64.b64decode(t["source"].split(",", 1)[1])
        tex_map[i] = rig.textures.add(f"simplywings:rigs/{model}_{i}", raw, uv=(t.get("uv_width", t["width"]), t.get("uv_height", t["height"])),
                                      path=f"simplywings:textures/entity/{t['name']}")
    groups = {g["uuid"]: g for g in data.get("groups", [])}
    elements = {e["uuid"]: e for e in data["elements"]}
    tops = []

    def copy(node, parent_name):
        if isinstance(node, dict):
            g = groups[node["uuid"]]
            if g["name"] == "ref" or g.get("export") is False:
                return
            name = rig.bone(f"{prefix}{g['name']}", g["origin"], parent_name, rotation=list(g.get("rotation", [0, 0, 0])))
            if parent_name == parent:
                tops.append(name)
            for child in node.get("children", []):
                copy(child, name)
        else:
            e = elements.get(node)
            if not e or e.get("export") is False or e.get("type", "cube") != "cube":
                return
            ne = json.loads(json.dumps(e))
            ne["uuid"] = scene.new_id()
            for face in ne.get("faces", {}).values():
                if face.get("texture") is not None:
                    face["texture"] = tex_map.get(face["texture"], face["texture"])
            rig.add_element(parent_name, ne)

    for node in data["outliner"]:
        copy(node, parent)
    return tops


def build_flyers(rig):
    rig.bone("flyers", (0, 0, 0))
    out = {}
    for cat, models in FLYER_MODELS.items():
        out[cat] = []
        for k in range(FLYERS_PER_CATEGORY):
            name = f"flyer_{cat}_{k}"
            rig.bone(name, (0, 16, 0), "flyers")
            body = rig.bone(f"{name}_body", (0, 16, 0), name, rotation=[-90, 0, 0])
            player_model(rig, body, (0, 0, 0), shadow=True)
            model = models[k % len(models)]
            tops = import_rig(rig, model, body, f"{name}_")
            rig.scale_tree(name, 2.0, (0, 16, 0))
            out[cat].append((name, tops, model))
    rig.bone("clouds", (0, 0, 0))
    rnd = random.Random(55)
    for i in range(CLOUDS):
        b = rig.bone(f"cloud_{i}", (0, 0, 0), "clouds")
        puffs = [(rnd.uniform(-18, 18), rnd.uniform(-5, 6), rnd.uniform(-14, 14), rnd.uniform(9, 15)) for _ in range(5)]
        voxel_shell(rig, b, (0, 0, 0), lambda px, py, pz: any(math.hypot(px - a, (py - c) * 1.5, pz - d) <= r for a, c, d, r in puffs), 34, 5,
                    lambda px, py, pz: "cloud" if py > -2 else "cloud_shade")
    return out


def build_player(rig):
    rig.bone("player", PLAYER)
    player_model(rig, "player", PLAYER)
    feather_tex = rig.textures.add("simplywings:item/summoning_feather", ITEM_TEX.read_bytes(), path="simplywings:textures/item/summoning_feather.png")
    rig.bone("offerings", (0, 0, 0))
    for i in range(10):
        b = rig.bone(f"offering_{i}", (0, 0, 0), "offerings")
        rig.plane(b, (0, 0, 0), 9, 9, feather_tex, uv=(16, 16))


def build_particles(rig):
    rnd = random.Random(61)
    rig.bone("particles", (0, 0, 0))
    for name, x, z, _ in flame_positions():
        for i in range(6):
            b = rig.bone(f"pt_ember_{name}_{i}", (x, FLAME_BASE + FLAME_SPEC[name]["H"] * 0.6, z), "particles")
            rig.box(b, (x + rnd.uniform(-5, 5), FLAME_BASE + FLAME_SPEC[name]["H"] * 0.6, z + rnd.uniform(-5, 5)), (1.2, 1.2, 1.2), f"f_{name}_4")
    for i in range(30):
        b = rig.bone(f"pt_mote_{i}", (0, 0, 0), "particles")
        a = rnd.uniform(0, 6.28)
        r = rnd.uniform(30, 120)
        rig.box(b, (r * math.cos(a), 0, r * math.sin(a)), (1.6, 1.6, 1.6), "circle_hot")
    for i in range(32):
        b = rig.bone(f"pt_shard_{i}", HOVER, "particles")
        rig.box(b, HOVER, (2, 2, 2), rnd.choice(["s_white", "circle_hot", "s_gold"]))
    for i in range(24):
        b = rig.bone(f"pt_feather_{i}", (0, 0, 0), "particles")
        rig.box(b, (0, 0, 0), (1, 0.5, 4), "feather")
    for k in range(4):
        for i in range(8):
            b = rig.bone(f"pt_spark_{k}_{i}", (0, 0, 0), "particles")
            rig.box(b, (0, 0, 0), (1.4, 1.4, 1.4), "s_white")


SPRITES = {}


def main():
    SPRITES.update(sprites.build())
    source, groups, tex, altar, floor_y, anchors = scene.build()
    textures = Textures(tex)
    palette = Palette()
    rig = Rig(palette, textures)
    build_flames(rig)
    build_floor(rig)
    build_streams(rig)
    build_sky(rig)
    build_star(rig)
    build_waves(rig)
    build_wings(rig)
    flyers = build_flyers(rig)
    build_player(rig)
    build_particles(rig)
    import altar_timeline as tl
    tl.build_extras(rig, ["b", "b", "b", "b", "b", "b", "a", "s", "ss", "sss"])
    tl.animate_all(rig, flyers)
    elements, outliner = [], []
    for gname, items in groups.items():
        if not items:
            continue
        elements.extend(items)
        outliner.append({"name": gname, "origin": [0, 0, 0], "color": 0, "uuid": scene.new_id(), "export": True,
                         "isOpen": False, "locked": gname in ("floor", "build"), "visibility": True,
                         "autouv": 0, "children": [e["uuid"] for e in items]})
    elements.extend(rig.elements)
    outliner.extend(rig.roots)
    bb = {"meta": {"format_version": "4.10", "model_format": "free", "box_uv": False},
          "name": "summoning_animation", "model_identifier": "", "visible_box": [1, 1, 0],
          "variable_placeholders": "", "variable_placeholder_buttons": [], "timeline_setups": [],
          "unhandled_root_fields": {}, "resolution": {"width": 16, "height": 16},
          "elements": elements, "outliner": outliner, "textures": textures.json(), "animations": rig.animations()}
    OUT.write_text(json.dumps(bb, separators=(",", ":")), encoding="utf-8")
    (ROOT / "src/main/resources/assets/simplywings/textures/gacha/palette.png").write_bytes(palette.png)
    import altar_export
    nb, raw, gz = altar_export.export(rig, textures, bb["animations"], rig.meta)
    print(f"runtime: {nb} bones, {raw // 1024} KiB json, {gz // 1024} KiB gzip")
    keys = sum(len(f["keyframes"]) for a in bb["animations"] for f in a["animators"].values())
    print(f"{len(elements)} cubes, {len(rig.groups)} bones, {keys} keyframes, {len(bb['textures'])} textures -> {OUT.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
