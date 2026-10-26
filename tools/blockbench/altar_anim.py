import io
import json
import math
import random
import sys
from pathlib import Path

from PIL import Image

sys.path.insert(0, str(Path(__file__).resolve().parent))
import altar_scene as scene  # noqa: E402

ROOT = scene.ROOT
OUT = ROOT / "art/gacha/summoning_animation.bbmodel"
B = 16
FLOOR_TOP = 0
FLAME_BASE = 2 * B
HOVER = (0, 6 * B, 0)
SIGIL_Y = 96 * B
WING_Y = 14 * B

FLAMES = [("fire", (-4, 6)), ("dark", (-7, 0)), ("earth", (-4, -6)), ("water", (4, -6)), ("light", (7, 0)), ("air", (4, 6))]
IGNITE = {name: 0.4 + 0.22 * i for i, (name, _) in enumerate(FLAMES)}

COLORS = {
    "red": (205, 45, 25), "orange": (255, 125, 25), "yellow": (255, 222, 90), "ember": (255, 170, 60),
    "black": (24, 12, 34), "purple": (72, 22, 115), "violet": (172, 84, 255), "eye": (255, 96, 40),
    "rock": (92, 82, 72), "moss": (82, 142, 52), "crystal": (96, 232, 128), "dirt": (112, 76, 46),
    "deep": (22, 72, 182), "water": (52, 142, 232), "foam": (214, 242, 255),
    "gold": (255, 210, 92), "white": (255, 252, 236), "ray": (255, 240, 170, 170),
    "pale": (206, 238, 244), "cyan": (150, 222, 236), "leaf": (172, 222, 122),
    "s_white": (255, 255, 255), "s_blue": (90, 170, 255), "s_violet": (180, 108, 255), "s_gold": (255, 194, 58),
    "s_red": (255, 74, 94),
    "h_white": (255, 255, 255, 90), "h_blue": (90, 170, 255, 90), "h_violet": (180, 108, 255, 90),
    "h_gold": (255, 194, 58, 90), "h_red": (255, 74, 94, 90),
    "rune": (206, 146, 255), "circle": (186, 126, 255), "circle_hot": (236, 206, 255), "energy": (200, 150, 255, 150),
    "pillar": (214, 176, 255, 120), "pillar_core": (255, 255, 255, 220),
    "spectral": (52, 26, 86, 200), "spectral_glow": (206, 126, 255), "feather": (238, 228, 255, 215),
    "feather_edge": (184, 146, 255, 215), "crack": (255, 42, 64), "wave": (236, 214, 255, 190), "flash": (255, 255, 255, 200),
}


class Palette:
    CELL = 4

    def __init__(self):
        self.names = list(COLORS)
        self.size = 64
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


class Rig:
    def __init__(self, palette, texture_index):
        self.palette = palette
        self.tex = texture_index
        self.groups = {}
        self.roots = []
        self.elements = []
        self.keys = {}
        self.lengths = {}
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

        walk(self.groups[bone])

    def bone(self, name, origin, parent=None, rotation=None):
        g = {"name": name, "origin": [float(v) for v in origin], "rotation": rotation or [0, 0, 0], "color": 0,
             "uuid": scene.new_id(), "export": True, "isOpen": False, "locked": False, "visibility": True,
             "autouv": 0, "children": []}
        self.groups[name] = g
        (self.groups[parent]["children"] if parent else self.roots).append(g)
        return name

    def box(self, bone, center, size, color, rot=None, pivot=None, name=None):
        frm = [center[k] - size[k] / 2 for k in range(3)]
        to = [center[k] + size[k] / 2 for k in range(3)]
        faces = {}
        for face in ("north", "east", "south", "west", "up", "down"):
            shade = 0 if face == "up" else 2 if face == "down" else 1
            faces[face] = {"uv": self.palette.uv(color, shade), "texture": self.tex}
        element = scene.cube(name or f"{bone}_{len(self.groups[bone]['children'])}", frm, to, faces,
                             origin=list(pivot or center), rotation=rot)
        self.elements.append(element)
        self.by_uuid[element["uuid"]] = element
        self.groups[bone]["children"].append(element["uuid"])
        return element

    def key(self, anim, bone, channel, t, vec, interp="catmullrom"):
        self.keys.setdefault(anim, {}).setdefault(bone, {}).setdefault(channel, {})[round(t, 4)] = (vec, interp)

    def sample(self, anim, bone, channel, t0, t1, fn, rate=12, interp="catmullrom", extra=()):
        n = max(1, int(math.ceil((t1 - t0) * rate)))
        times = sorted(set([t0 + (t1 - t0) * i / n for i in range(n + 1)] + [t for t in extra if t0 < t < t1]))
        for t in times:
            self.key(anim, bone, channel, t, fn(t), interp)

    def hide_until(self, anim, bone, t):
        self.key(anim, bone, "scale", 0, (0, 0, 0), "linear")
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
                                       "uuid": scene.new_id(), "time": t, "color": -1, "interpolation": interp})
                animators[self.groups[bone]["uuid"]] = {"name": bone, "type": "bone", "keyframes": frames}
            out.append({"uuid": scene.new_id(), "name": anim, "loop": "loop" if anim == "idle" else "once",
                        "override": False, "length": length, "snapping": 24, "selected": False,
                        "anim_time_update": "", "blend_weight": "", "start_delay": "", "loop_delay": "",
                        "animators": animators})
        return out


def ring(rig, bone, center, radius, count, color, thick=4, height=3, glow=None):
    if glow:
        ring(rig, bone, center, radius, count, glow, thick=thick * 3.2, height=height * 0.45)
    cx, cy, cz = center
    seg = 2 * math.pi * radius / count * 0.78
    for i in range(count):
        a = 2 * math.pi * i / count
        x, z = cx + radius * math.cos(a), cz + radius * math.sin(a)
        rig.box(bone, (x, cy, z), (seg, height, thick), color, rot=[0, -math.degrees(a) + 90, 0])


def flame_positions():
    for name, (dx, dz) in FLAMES:
        yield name, dx * B, dz * B, math.degrees(math.atan2(dx, dz))


def layers(rig, name, body, x, z, specs, rot_step=0, shrink=0.8):
    y = FLAME_BASE
    for i, (w, h, c, ox, oz) in enumerate(specs):
        lb = rig.bone(f"flame_{name}_layer_{i}", (x, y, z), body)
        rig.box(lb, (x + ox, y + h / 2, z + oz), (w, h, w), c, rot=[0, rot_step * i, 0])
        y += h * shrink
    return y


def build_flames(rig):
    for name, x, z, yaw in flame_positions():
        root = rig.bone(f"flame_{name}", (x, FLAME_BASE, z))
        body = rig.bone(f"flame_{name}_body", (x, FLAME_BASE, z), root)
        if name == "fire":
            layers(rig, name, body, x, z, [(12, 7, "red", 0, 0), (10.5, 8, "orange", 0.6, -0.4), (9, 8, "orange", -0.5, 0.5),
                                           (7.5, 7, "ember", 0.4, 0.3), (6, 7, "yellow", -0.3, -0.4), (4.5, 6, "yellow", 0.3, 0.2),
                                           (3, 5, "white", 0, 0)], rot_step=17)
            rig.box(body, (x, FLAME_BASE + 14, z), (6, 22, 6), "yellow", rot=[0, 45, 0])
            for i in range(6):
                a = math.pi / 3 * i + 0.4
                t = rig.bone(f"flame_fire_tongue_{i}", (x + 3 * math.cos(a), FLAME_BASE + 5, z + 3 * math.sin(a)), body)
                h = 12 + 5 * (i % 3)
                rig.box(t, (x + 4.5 * math.cos(a), FLAME_BASE + 5 + h / 2, z + 4.5 * math.sin(a)), (3, h, 3), ("red", "orange", "ember")[i % 3],
                        rot=[16 * math.sin(a), 0, -16 * math.cos(a)])
            for i in range(8):
                e = rig.bone(f"flame_fire_ember_{i}", (x, FLAME_BASE, z), body)
                a = i * 0.79
                rig.box(e, (x + 5 * math.cos(a), FLAME_BASE + 4, z + 5 * math.sin(a)), (1.6, 1.6, 1.6), "ember" if i % 2 else "yellow")
        elif name == "dark":
            layers(rig, name, body, x, z, [(5, 5, "black", 0, 0), (7, 6, "purple", 0.5, 0), (10, 7, "black", 0, 0.5), (12, 8, "purple", -0.5, 0),
                                           (10, 7, "black", 0, -0.5), (7, 6, "purple", 0.4, 0.4)], rot_step=24, shrink=0.88)
            rig.box(body, (x, FLAME_BASE + 18, z), (4, 20, 4), "violet", rot=[0, 45, 0])
            for i in range(4):
                a = math.pi / 2 * i + 0.3
                t = rig.bone(f"flame_dark_tongue_{i}", (x + 4 * math.cos(a), FLAME_BASE + 26, z + 4 * math.sin(a)), body)
                for k in range(3):
                    rig.box(t, (x + (5 + k * 2) * math.cos(a), FLAME_BASE + 30 + k * 5, z + (5 + k * 2) * math.sin(a)), (3 - k * 0.6, 6, 3 - k * 0.6),
                            "purple" if k % 2 else "black", rot=[22 * math.sin(a) * (k + 1), 0, -22 * math.cos(a) * (k + 1)])
            eyes = rig.bone("flame_dark_eyes", (x, FLAME_BASE + 20, z), body)
            fx, fz = -x / math.hypot(x, z), -z / math.hypot(x, z)
            for s in (-1, 1):
                rig.box(eyes, (x + fx * 7 + fz * 2.4 * s, FLAME_BASE + 22, z + fz * 7 - fx * 2.4 * s), (2.4, 1.2, 2.4), "eye")
        elif name == "earth":
            core = rig.bone("flame_earth_layer_0", (x, FLAME_BASE + 9, z), body)
            rig.box(core, (x, FLAME_BASE + 9, z), (12, 9, 12), "rock", rot=[8, 25, -6])
            rig.box(core, (x, FLAME_BASE + 14, z), (11, 2, 11), "moss", rot=[8, 25, -6])
            rig.box(core, (x, FLAME_BASE + 4, z), (7, 6, 7), "dirt", rot=[0, 40, 0])
            rig.box(core, (x + 3, FLAME_BASE + 6, z - 2), (5, 5, 5), "rock", rot=[20, 10, 30])
            for i, (ox, oz, h) in enumerate(((-2, 1, 9), (2, -1, 12), (0, 3, 7), (-3, -2, 6))):
                rig.box(core, (x + ox, FLAME_BASE + 17 + h / 2, z + oz), (2.5, h, 2.5), "crystal", rot=[10 * ox, 0, 8 * oz])
            orbit = rig.bone("flame_earth_orbit", (x, FLAME_BASE + 10, z), body)
            for i in range(8):
                a = 2 * math.pi * i / 8
                r = 11 + 2 * (i % 2)
                rig.box(orbit, (x + r * math.cos(a), FLAME_BASE + 10 + 3 * math.sin(3 * a), z + r * math.sin(a)),
                        (3 + i % 2, 3 + i % 2, 3 + i % 2), ("rock", "crystal", "dirt")[i % 3], rot=[30, 45 * i, 20])
        elif name == "water":
            layers(rig, name, body, x, z, [(10, 6, "deep", 0, 0), (9, 6, "water", 0, 0), (8.5, 6, "deep", 0, 0), (8, 6, "water", 0, 0),
                                           (7, 6, "deep", 0, 0), (6, 5, "water", 0, 0), (4.5, 4, "foam", 0, 0)], rot_step=22, shrink=0.95)
            crown = rig.bone("flame_water_crown", (x, FLAME_BASE + 36, z), body)
            for i in range(6):
                a = math.pi / 3 * i
                rig.box(crown, (x + 4 * math.cos(a), FLAME_BASE + 38, z + 4 * math.sin(a)), (1.6, 5, 1.6), "foam", rot=[25 * math.sin(a), 0, -25 * math.cos(a)])
            orbit = rig.bone("flame_water_orbit", (x, FLAME_BASE + 16, z), body)
            for i in range(12):
                a = 2 * math.pi * i / 12
                rig.box(orbit, (x + 10 * math.cos(a), FLAME_BASE + 16 + 5 * math.sin(2 * a), z + 10 * math.sin(a)), (2, 2, 2), "foam" if i % 2 else "water")
        elif name == "light":
            core = rig.bone("flame_light_layer_0", (x, FLAME_BASE + 18, z), body)
            rig.box(core, (x, FLAME_BASE + 18, z), (8, 8, 8), "white", rot=[45, 0, 35])
            rig.box(core, (x, FLAME_BASE + 18, z), (12, 12, 12), "ray", rot=[0, 45, 45])
            rig.box(core, (x, FLAME_BASE + 6, z), (4, 10, 4), "gold", rot=[0, 45, 0])
            rays = rig.bone("flame_light_rays", (x, FLAME_BASE + 18, z), body)
            for i in range(4):
                a = math.pi / 4 * i
                rig.box(rays, (x, FLAME_BASE + 18, z), (1.2, 34 if i % 2 == 0 else 22, 1.2), "gold", rot=[0, 0, 45 * i])
            rig.box(rays, (x, FLAME_BASE + 18, z), (1.2, 1.2, 26), "gold")
            halo = rig.bone("flame_light_halo", (x, FLAME_BASE + 18, z), body)
            ring(rig, halo, (x, FLAME_BASE + 18, z), 11, 12, "gold", thick=1.2, height=1.2)
        elif name == "air":
            for r in range(4):
                rb = rig.bone(f"flame_air_ribbon_{r}", (x, FLAME_BASE, z), body)
                for i in range(8):
                    a = i * 0.85 + r * 1.57
                    rad = 2.5 + i * 0.85
                    rig.box(rb, (x + rad * math.cos(a), FLAME_BASE + 3 + i * 4.2, z + rad * math.sin(a)), (6, 1.2, 2),
                            "pale" if (i + r) % 2 else "cyan", rot=[0, -math.degrees(a), 12])
            leaves = rig.bone("flame_air_leaves", (x, FLAME_BASE + 12, z), body)
            for i in range(6):
                a = math.pi / 3 * i
                rig.box(leaves, (x + 8 * math.cos(a), FLAME_BASE + 8 + i * 4, z + 8 * math.sin(a)), (3, 0.6, 2), "leaf" if i % 2 else "pale", rot=[20, 30 * i, 0])


def build_floor(rig):
    rig.bone("runes", (0, 0, 0))
    for i in range(24):
        a = 2 * math.pi * i / 24
        x, z = 72 * math.cos(a), 72 * math.sin(a)
        b = rig.bone(f"rune_{i}", (x, 0.5, z), "runes")
        rig.box(b, (x, 0.6, z), (4, 1, 8), "rune", rot=[0, -math.degrees(a), 0])
        rig.box(b, (x, 1.2, z), (2, 0.6, 3), "circle_hot", rot=[0, -math.degrees(a) + 45, 0])
    rig.bone("floor_circle", (0, 0.4, 0))
    rig.bone("floor_ring_outer", (0, 0.4, 0), "floor_circle")
    ring(rig, "floor_ring_outer", (0, 0.4, 0), 128, 56, "circle", thick=4, height=0.8, glow="energy")
    for i in range(12):
        a = 2 * math.pi * i / 12
        rig.box("floor_ring_outer", (134 * math.cos(a), 0.5, 134 * math.sin(a)), (5, 1, 5), "circle_hot", rot=[0, 45 + i * 30, 0])
    rig.bone("floor_ring_inner", (0, 0.4, 0), "floor_circle")
    ring(rig, "floor_ring_inner", (0, 0.4, 0), 96, 40, "circle", thick=2, height=0.8)
    for _, x, z, _ in flame_positions():
        a = math.atan2(z, x)
        mid = 76
        rig.box("floor_circle", (mid * math.cos(a), 0.5, mid * math.sin(a)), (70, 0.8, 2), "circle_hot", rot=[0, -math.degrees(a), 0])


BEAM_COLORS = {"fire": "orange", "dark": "violet", "earth": "crystal", "water": "water", "light": "gold", "air": "cyan"}


def build_streams(rig):
    rig.bone("element_beams", (0, 0, 0))
    for name, x, z, _ in flame_positions():
        b = rig.bone(f"beam_{name}", (x, FLAME_BASE + 40, z), "element_beams")
        top = WING_Y + 120
        rig.box(b, (x, (FLAME_BASE + 40 + top) / 2, z), (7, top - FLAME_BASE - 40, 7), BEAM_COLORS[name])
        rig.box(b, (x, (FLAME_BASE + 40 + top) / 2, z), (16, top - FLAME_BASE - 40, 16), "energy")
        rig.box(b, (x, top, z), (22, 22, 22), BEAM_COLORS[name], rot=[45, 45, 0])
    rig.bone("streams", (0, 0, 0))
    for name, x, z, _ in flame_positions():
        b = rig.bone(f"stream_{name}", (x, FLAME_BASE + 40, z), "streams")
        p0 = (x, FLAME_BASE + 40, z)
        p2 = HOVER
        p1 = (x * 0.4, 170, z * 0.4)
        for i in range(12):
            t = (i + 0.5) / 12
            pt = [(1 - t) ** 2 * p0[k] + 2 * (1 - t) * t * p1[k] + t * t * p2[k] for k in range(3)]
            s = 6 - 3 * t
            sb = rig.bone(f"stream_{name}_{i}", pt, b)
            rig.box(sb, pt, (s, s, s), "energy" if i % 2 else "circle_hot", rot=[45, 45, 0])


def build_sky(rig):
    for k, (h, r, n) in enumerate(((10 * B, 5 * B, 28), (24 * B, 8 * B, 40), (44 * B, 12 * B, 52))):
        root = rig.bone(f"stack_{k + 1}", (0, h, 0))
        ring(rig, root, (0, h, 0), r, n, "circle", thick=6, height=3, glow="energy")
        inner = rig.bone(f"stack_{k + 1}_inner", (0, h, 0), root)
        ring(rig, inner, (0, h, 0), r * 0.72, n - 8, "circle_hot", thick=2.5, height=1.5)
        for i in range(6):
            a = 2 * math.pi * i / 6
            rig.box(inner, (r * 0.86 * math.cos(a), h, r * 0.86 * math.sin(a)), (7, 2, 7), "rune", rot=[0, 45 + 60 * i, 0])
    sig = rig.bone("sky_sigil", (0, SIGIL_Y, 0))
    for nm, r, n, c, th in (("outer", 480, 84, "circle", 8), ("mid", 400, 70, "circle_hot", 5), ("inner", 260, 48, "circle", 6)):
        b = rig.bone(f"sky_sigil_{nm}", (0, SIGIL_Y, 0), sig)
        ring(rig, b, (0, SIGIL_Y, 0), r, n, c, thick=th * 1.6, height=6, glow="energy")
    for i in range(24):
        a = 2 * math.pi * i / 24
        rig.box("sky_sigil_mid", (440 * math.cos(a), SIGIL_Y, 440 * math.sin(a)), (14, 4, 14), "rune", rot=[0, 45 + i * 15, 0])
    br = rig.bone("sky_sigil_branches", (0, SIGIL_Y, 0), sig)
    for i in range(12):
        a = 2 * math.pi * i / 12
        rig.box(br, (370 * math.cos(a), SIGIL_Y, 370 * math.sin(a)), (220, 3, 5), "circle_hot", rot=[0, -math.degrees(a), 0])
        rig.box(br, (500 * math.cos(a), SIGIL_Y, 500 * math.sin(a)), (22, 5, 22), "rune", rot=[0, 45 - math.degrees(a), 0])
    teeth = rig.bone("sky_sigil_teeth", (0, SIGIL_Y, 0), sig)
    for i in range(24):
        a = 2 * math.pi * (i + 0.5) / 24
        ln = 70 if i % 2 else 120
        rig.box(teeth, (470 * math.cos(a), SIGIL_Y - ln / 2, 470 * math.sin(a)), (10, ln, 10), "circle_hot" if i % 2 else "rune", rot=[0, 45, 0])
        rig.box(teeth, (470 * math.cos(a), SIGIL_Y - ln - 8, 470 * math.sin(a)), (16, 16, 16), "s_violet", rot=[45, 45, 0])
    hexa = rig.bone("sky_sigil_hex", (0, SIGIL_Y, 0), sig)
    for tri in range(2):
        for j in range(3):
            a0 = 2 * math.pi * j / 3 + tri * math.pi / 3
            a1 = a0 + 2 * math.pi / 3
            p0 = (250 * math.cos(a0), 250 * math.sin(a0))
            p1 = (250 * math.cos(a1), 250 * math.sin(a1))
            mid = ((p0[0] + p1[0]) / 2, (p0[1] + p1[1]) / 2)
            ang = math.degrees(math.atan2(p1[1] - p0[1], p1[0] - p0[0]))
            rig.box(hexa, (mid[0], SIGIL_Y, mid[1]), (433, 3, 4), "circle", rot=[0, -ang, 0])
    pil = rig.bone("pillar", (0, FLOOR_TOP, 0))
    rig.box(pil, (0, SIGIL_Y / 2, 0), (26, SIGIL_Y, 26), "pillar")
    rig.box(pil, (0, SIGIL_Y / 2, 0), (10, SIGIL_Y, 10), "pillar_core")
    rig.box(pil, (0, SIGIL_Y / 2, 0), (60, SIGIL_Y, 60), "energy")
    for h in ("a", "b"):
        sign = 1 if h == "a" else -1
        hb = rig.bone(f"pillar_helix_{h}", (0, 0, 0), pil)
        for i in range(90):
            u = i / 90
            a = sign * u * 2 * math.pi * 7 + (0 if h == "a" else math.pi)
            rig.box(hb, (44 * math.cos(a), u * SIGIL_Y, 44 * math.sin(a)), (12, 12, 12), "circle_hot" if i % 3 else "s_white", rot=[45, -math.degrees(a), 0])
    rnd = random.Random(31)
    rig.bone("motes", (0, 0, 0))
    for i in range(40):
        m = rig.bone(f"mote_{i}", (0, 0, 0), "motes")
        a = rnd.uniform(0, 6.28)
        r = rnd.uniform(40, 160)
        rig.box(m, (r * math.cos(a), 0, r * math.sin(a)), (5, 5, 5), rnd.choice(["circle_hot", "s_white", "rune"]), rot=[45, 45, 0])
    cr = rig.bone("cracks", (0, SIGIL_Y, 0))
    rnd = random.Random(9)
    for i in range(10):
        a = 2 * math.pi * i / 10 + rnd.uniform(-0.2, 0.2)
        b = rig.bone(f"crack_{i}", (300 * math.cos(a), SIGIL_Y, 300 * math.sin(a)), cr)
        px, py, pz = 300 * math.cos(a), SIGIL_Y, 300 * math.sin(a)
        for s in range(6):
            da = a + rnd.uniform(-0.35, 0.35)
            ln = rnd.uniform(50, 90)
            nx, ny, nz = px + ln * math.cos(da), py + rnd.uniform(-40, 40), pz + ln * math.sin(da)
            rig.box(b, ((px + nx) / 2, (py + ny) / 2, (pz + nz) / 2), (ln, 5, 5), "crack",
                    rot=[0, -math.degrees(da), math.degrees(math.atan2(ny - py, ln))])
            px, py, pz = nx, ny, nz


def build_star(rig):
    rig.bone("star", (0, 0, 0))
    for c in ("white", "blue", "violet", "gold", "red"):
        b = rig.bone(f"star_{c}", (0, 0, 0), "star")
        rig.box(b, (0, 0, 0), (40, 40, 40), f"s_{c}", rot=[45, 0, 35.26])
        rig.box(b, (0, 0, 0), (34, 34, 34), f"s_{c}", rot=[0, 45, 0])
        rig.box(b, (0, 0, 0), (18, 18, 18), "s_white", rot=[45, 45, 0])
        rig.box(b, (0, 0, 0), (76, 76, 76), f"h_{c}", rot=[45, 45, 0])
        rig.box(b, (0, 0, 0), (10, 130, 10), "s_white")
        rig.box(b, (0, 0, 0), (130, 10, 10), "s_white")
        rig.box(b, (0, 0, 0), (10, 10, 130), "s_white")
        rig.box(b, (0, 0, 0), (7, 90, 7), f"s_{c}", rot=[45, 0, 0])
        rig.box(b, (0, 0, 0), (7, 90, 7), f"s_{c}", rot=[-45, 0, 0])
    sr = rig.bone("star_ring", (0, 0, 0), "star")
    ring(rig, sr, (0, 0, 0), 70, 20, "circle_hot", thick=5, height=3)
    for g in range(8):
        b = rig.bone(f"star_ghost_{g}", (0, 0, 0))
        rig.box(b, (0, 0, 0), (34, 34, 34), "h_white" if g % 2 else "energy", rot=[45, 0, 35.26])
    fl = rig.bone("flash", HOVER)
    rig.box(fl, HOVER, (30, 30, 30), "flash", rot=[45, 45, 0])
    rig.box(fl, HOVER, (60, 6, 6), "flash")
    rig.box(fl, HOVER, (6, 60, 6), "flash")
    for i in range(12):
        rig.box(fl, HOVER, (3, 3, 70 + 20 * (i % 3)), "s_white" if i % 2 else "circle_hot", rot=[(i * 47) % 180 - 90, (i * 73) % 360, 0])
    birth = rig.bone("birth_flash", (0, SIGIL_Y, 0))
    rig.box(birth, (0, SIGIL_Y, 0), (30, 30, 30), "flash", rot=[45, 45, 0])


def build_waves(rig):
    for i in range(3):
        b = rig.bone(f"ground_wave_{i}", (0, 2, 0))
        ring(rig, b, (0, 2, 0), 32, 40, "wave", thick=3, height=2)
    for i in range(4):
        b = rig.bone(f"beat_wave_{i}", (0, 0, 0))
        ring(rig, b, (0, 0, 0), 32, 36, "wave", thick=2.5, height=2)
    b = rig.bone("pillar_wave", (0, 2, 0))
    ring(rig, b, (0, 2, 0), 32, 40, "circle_hot", thick=3, height=1.5)
    rnd = random.Random(4)
    rig.bone("shards", HOVER)
    for i in range(48):
        b = rig.bone(f"shard_{i}", HOVER, "shards")
        s = rnd.uniform(4, 10)
        rig.box(b, HOVER, (s, s, s), rnd.choice(["s_white", "s_red", "s_gold", "circle_hot"]), rot=[rnd.uniform(0, 90), rnd.uniform(0, 90), 0])


def segment(rig, bone, p0, p1, width, height, color, roll=0):
    dx, dy, dz = p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]
    ln = math.sqrt(dx * dx + dy * dy + dz * dz)
    yaw = math.degrees(math.atan2(dx, dz))
    pitch = -math.degrees(math.atan2(dy, math.hypot(dx, dz)))
    mid = ((p0[0] + p1[0]) / 2, (p0[1] + p1[1]) / 2, (p0[2] + p1[2]) / 2)
    rig.box(bone, mid, (width, height, ln), color, rot=[pitch, yaw, roll])


def build_dragon(rig):
    rig.bone("dragon", (0, 0, 0))
    body = rig.bone("dragon_body", (0, 0, 0), "dragon")
    spine = [(0, 0, 120), (0, 4, 60), (0, 6, 0), (0, 2, -60), (0, -4, -120)]
    widths = [54, 64, 60, 48, 36]
    for i in range(len(spine) - 1):
        segment(rig, body, spine[i], spine[i + 1], widths[i], widths[i] * 0.8, "spectral")
        segment(rig, body, (spine[i][0], spine[i][1] + widths[i] * 0.42, spine[i][2]),
                (spine[i + 1][0], spine[i + 1][1] + widths[i + 1] * 0.42, spine[i + 1][2]), 6, 6, "spectral_glow")
    for z in range(-110, 120, 22):
        rig.box(body, (0, 36 if z > -60 else 26, z), (6, 18, 6), "spectral_glow", rot=[35, 0, 0])
    tail = rig.bone("dragon_tail", (0, -4, -120), "dragon")
    prev = (0, -4, -120)
    for k in range(10):
        nxt = (0, -4 - k * 3, -120 - (k + 1) * 34)
        w = 34 * (1 - k / 11)
        segment(rig, tail, prev, nxt, w, w * 0.8, "spectral")
        rig.box(tail, (nxt[0], nxt[1] + w * 0.5, nxt[2]), (4, 10, 4), "spectral_glow", rot=[35, 0, 0])
        prev = nxt
    rig.box(tail, (0, prev[1], prev[2] - 14), (30, 4, 30), "spectral_glow", rot=[0, 45, 0])
    neck = rig.bone("dragon_neck", (0, 6, 120), "dragon")
    prev = (0, 6, 120)
    for k in range(5):
        nxt = (0, 10 + k * 9, 120 + (k + 1) * 26)
        segment(rig, neck, prev, nxt, 34 - k * 2, 30 - k * 2, "spectral")
        rig.box(neck, (0, nxt[1] + 18, nxt[2]), (4, 12, 4), "spectral_glow", rot=[35, 0, 0])
        prev = nxt
    head = rig.bone("dragon_head", prev, "dragon_neck")
    hx, hy, hz = prev
    rig.box(head, (hx, hy + 6, hz + 26), (44, 28, 52), "spectral")
    rig.box(head, (hx, hy - 2, hz + 64), (30, 16, 40), "spectral")
    rig.box(head, (hx, hy - 14, hz + 52), (26, 8, 44), "spectral", rot=[12, 0, 0])
    for s in (-1, 1):
        rig.box(head, (hx + 16 * s, hy + 14, hz + 36), (8, 5, 10), "eye")
        segment(rig, head, (hx + 14 * s, hy + 18, hz + 18), (hx + 22 * s, hy + 44, hz - 26), 7, 7, "spectral_glow")
        segment(rig, head, (hx + 18 * s, hy + 4, hz + 4), (hx + 30 * s, hy + 16, hz - 34), 5, 5, "spectral_glow")
    jaw = rig.bone("dragon_jaw", (hx, hy - 10, hz + 10), "dragon_head")
    rig.box(jaw, (hx, hy - 18, hz + 46), (26, 8, 44), "spectral")
    for side, s in (("r", 1), ("l", -1)):
        sh = (26 * s, 18, 70)
        w = rig.bone(f"dragon_wing_{side}", sh, "dragon")
        elbow = (220 * s, 40, 110)
        segment(rig, w, sh, elbow, 18, 16, "spectral")
        segment(rig, w, (sh[0], sh[1] + 9, sh[2]), (elbow[0], elbow[1] + 9, elbow[2]), 5, 5, "spectral_glow")
        tip = rig.bone(f"dragon_wing_{side}_tip", elbow, f"dragon_wing_{side}")
        wrist = (420 * s, 30, 40)
        segment(rig, tip, elbow, wrist, 14, 12, "spectral")
        segment(rig, tip, (elbow[0], elbow[1] + 7, elbow[2]), (wrist[0], wrist[1] + 7, wrist[2]), 4, 4, "spectral_glow")
        fingers = [(15, 420), (-20, 400), (-52, 340), (-82, 290), (-112, 250)]
        ends = []
        for ang, ln in fingers:
            r = math.radians(ang)
            end = (wrist[0] + s * ln * math.cos(r), wrist[1] - 10, wrist[2] + ln * math.sin(r))
            segment(rig, tip, wrist, end, 6, 6, "spectral_glow")
            ends.append(end)
        anchors = ends + [(sh[0] + 30 * s, sh[1], sh[2] - 170)]
        for k in range(len(anchors) - 1):
            e0, e1 = anchors[k], anchors[k + 1]
            for j in range(7):
                u = (j + 0.5) / 7
                q0 = tuple(wrist[m] + (e0[m] - wrist[m]) * u for m in range(3))
                q1 = tuple(wrist[m] + (e1[m] - wrist[m]) * u for m in range(3))
                width = math.dist(q0, q1) * 1.15
                ln = math.dist(wrist, e0) / 7 * 1.25
                cx = tuple((q0[m] + q1[m]) / 2 for m in range(3))
                yaw = math.degrees(math.atan2(q1[0] - q0[0], q1[2] - q0[2])) + 90
                rig.box(tip if k < len(anchors) - 2 else w, cx, (width, 2, ln), "spectral", rot=[0, yaw, 0])


def feather(rig, bone, attach, theta, length, width, color, z, s):
    d = (math.sin(theta) * s, -math.cos(theta))
    center = (attach[0] + d[0] * length / 2, attach[1] + d[1] * length / 2, z)
    rig.box(bone, center, (width, length, 1.6), color, rot=[0, 0, -math.degrees(theta) * s])
    rig.box(bone, (attach[0] + d[0] * (length - 6), attach[1] + d[1] * (length - 6), z + 0.9), (width * 0.55, 12, 1), "s_white", rot=[0, 0, -math.degrees(theta) * s])


def build_wings(rig):
    rig.bone("wings", (0, WING_Y, 0))
    for side, s in (("r", 1), ("l", -1)):
        sh = (56 * s, WING_Y, 0)
        elbow = (170 * s, WING_Y + 80, 0)
        wrist = (310 * s, WING_Y + 120, 0)
        tip = (420 * s, WING_Y + 90, 0)
        arm = rig.bone(f"wing_{side}", sh, "wings")
        segment(rig, arm, sh, elbow, 12, 12, "feather_edge")
        fore = rig.bone(f"wing_{side}_fore", elbow, arm)
        segment(rig, fore, elbow, wrist, 10, 10, "feather_edge")
        hand = rig.bone(f"wing_{side}_hand", wrist, fore)
        segment(rig, hand, wrist, tip, 8, 8, "feather_edge")
        cov = rig.bone(f"wing_{side}_coverts", sh, arm)
        for k in range(14):
            u = (k + 0.5) / 14
            if u < 0.45:
                q = tuple(sh[m] + (elbow[m] - sh[m]) * u / 0.45 for m in range(3))
                bone = arm
            else:
                q = tuple(elbow[m] + (wrist[m] - elbow[m]) * (u - 0.45) / 0.55 for m in range(3))
                bone = fore
            feather(rig, bone, q, math.radians(4 + 40 * u), 120 + 60 * u, 26, "feather" if k % 2 else "feather_edge", 0, s)
            feather(rig, cov, (q[0], q[1] + 4, 0), math.radians(10 + 36 * u), 60 + 14 * u, 22, "feather_edge" if k % 2 else "feather", 2.4, s)
        for k in range(10):
            u = k / 9
            q = tuple(wrist[m] + (tip[m] - wrist[m]) * u * 0.8 for m in range(3))
            feather(rig, hand, q, math.radians(40 + 48 * u), 210 + 40 * math.sin(math.pi * u * 0.8), 24, "feather_edge" if k % 2 else "feather", -1.6, s)
    rnd = random.Random(12)
    rig.bone("feathers", (0, 0, 0))
    for i in range(30):
        b = rig.bone(f"feather_{i}", (0, 0, 0), "feathers")
        rig.box(b, (0, 0, 0), (3, 0.8, 9), rnd.choice(["feather", "feather_edge", "s_violet"]), rot=[0, rnd.uniform(0, 180), 0])


def main():
    source, groups, tex, altar, floor_y, anchors = scene.build()
    palette = Palette()
    tex.list.append(("simplywings:gacha/palette", palette.png))
    rig = Rig(palette, len(tex.list) - 1)
    build_flames(rig)
    for name, x, z, _ in flame_positions():
        rig.scale_tree(f"flame_{name}", 1.6, (x, FLAME_BASE, z))
    build_floor(rig)
    build_streams(rig)
    build_sky(rig)
    build_star(rig)
    build_waves(rig)
    build_dragon(rig)
    build_wings(rig)
    import altar_timeline as tl
    tl.build_extras(rig, ["b", "b", "b", "b", "b", "b", "a", "s", "ss", "sss"])
    tl.animate_all(rig)
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
    textures = scene.textures_json(tex)
    textures[-1].update({"width": palette.size, "height": palette.size, "uv_width": palette.size, "uv_height": palette.size})
    bb = {"meta": {"format_version": "4.10", "model_format": "free", "box_uv": False},
          "name": "summoning_animation", "model_identifier": "", "visible_box": [1, 1, 0],
          "variable_placeholders": "", "variable_placeholder_buttons": [], "timeline_setups": [],
          "unhandled_root_fields": {}, "resolution": {"width": 16, "height": 16},
          "elements": elements, "outliner": outliner, "textures": textures, "animations": rig.animations()}
    OUT.write_text(json.dumps(bb), encoding="utf-8")
    keys = sum(len(f["keyframes"]) for a in bb["animations"] for f in a["animators"].values())
    print(f"{len(elements)} cubes, {len(rig.groups)} bones, {keys} keyframes -> {OUT.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
