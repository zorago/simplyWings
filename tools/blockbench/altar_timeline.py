import math
import random

from altar_anim import (B, FLAME_BASE, HOVER, SIGIL_Y, WING_Y, IGNITE, Noise1, ease_in_out, ease_out_expo, flame_positions,
                        ring, segment, smooth)

TIER_COLORS = ["white", "blue", "violet", "gold", "red"]
TIERS = ["b", "a", "s", "ss", "sss"]


class Plan:
    def __init__(self, name, tier, mythic=None, pulls=None):
        self.name = name
        self.tier = tier
        self.mythic = mythic
        self.pulls = pulls
        rank = TIERS.index(tier)
        self.rank = rank
        self.sigil = rank >= 2
        self.sigil_h = 64 if rank == 2 else 96
        self.stacks = 1 if rank == 0 else 2 if rank == 1 else 3
        self.pillar_h = 26 if rank < 2 else self.sigil_h
        self.dragon = rank >= 3
        self.cracks = rank == 4
        self.wings = [0, 0, 0.6, 0.8, 1.0][rank]
        self.beams = rank >= 2
        self.shards = [12, 24, 36, 44, 48][rank]
        self.waves = [1, 2, 2, 3, 3][rank]
        self.birth = 4.6 if rank >= 2 else 4.4
        if pulls:
            self.spawn = []
            s = 4.7
            for p in pulls:
                self.spawn.append(s)
                s += [0.42, 0.6, 0.95, 1.4, 1.95][TIERS.index(p)]
            self.desc = s + 0.8
            self.crack_t = self.spawn[pulls.index("sss")] + 1.95 * 0.7 if "sss" in pulls else None
            self.cracks = self.crack_t is not None
            self.hover = self.desc + 3.4
        else:
            self.desc = self.birth + (1.6 if self.sigil else 0.8)
            self.hover = self.desc + [1.8, 2.2, 2.8, 3.6, 4.2][rank]
        chain = list(range(1, rank + 1))
        self.beats = [] if pulls else [(self.desc + (self.hover - self.desc) * (0.18 + 0.72 * (k + 1) / (len(chain) + 1)), c, 0.06 + 0.012 * c)
                                       for k, c in enumerate(chain)]
        self.burst = self.hover + [0.7, 0.8, 1.2, 1.4, 1.6][rank] + (0.6 if mythic else 0)
        self.reveal = self.burst + 0.3
        self.settle = self.reveal + ([1.2, 1.4, 3.6, 3.8, 4.0][rank])
        self.end = self.settle + [1.6, 1.8, 2.6, 3.0, 3.2][rank]
        self.hitstops = [(t, d, 0.1) for t, _, d in self.beats] + [(self.burst, 0.06 + 0.015 * rank, 0.05)]
        self.top = (self.sigil_h * B - 30) if self.sigil else 24 * B

    def warp(self, t):
        out = t
        for start, dur, factor in self.hitstops:
            if t > start:
                out -= min(t - start, dur) * (1 - factor)
        return out

    def star_path(self, t, offset=0.0, turns=None):
        t0, t1 = self.desc + offset, self.hover + offset * 0.35
        if t < t0:
            return (0, self.top, 0)
        u = ease_in_out((self.warp(t) - self.warp(t0)) / max(0.01, self.warp(t1) - self.warp(t0)))
        u = min(1.0, u)
        a = u * (turns or (1.2 + 0.3 * self.rank)) * 2 * math.pi + offset * 9
        r = math.sin(math.pi * u) ** 0.8 * (60 + 25 * self.rank)
        y = self.top * (1 - u) + HOVER[1] * u
        return (r * math.cos(a), y, r * math.sin(a))



def build_extras(rig, pulls):
    cam = rig.bone("camera", (0, 0, 0))
    rig.box(cam, (0, 0, 0), (10, 8, 14), "black")
    rig.box(cam, (0, 0, 9), (6, 6, 5), "rock")
    rig.box(cam, (0, 0, 12), (5, 5, 1), "s_blue")
    rig.box(cam, (0, 6, -3), (8, 4, 4), "black")
    tgt = rig.bone("camera_target", (0, 0, 0))
    rig.box(tgt, (0, 0, 0), (3, 3, 3), "s_red", rot=[45, 45, 0])
    rig.bone("multi_stars", (0, 0, 0))
    for i, p in enumerate(pulls):
        b = rig.bone(f"multi_star_{i}", (0, 0, 0), "multi_stars")
        c = TIER_COLORS[TIERS.index(p)]
        rig.box(b, (0, 0, 0), (26, 26, 26), f"s_{c}", rot=[45, 0, 35.26])
        rig.box(b, (0, 0, 0), (12, 12, 12), "s_white", rot=[45, 45, 0])
        rig.box(b, (0, 0, 0), (48, 48, 48), f"h_{c}", rot=[45, 45, 0])
        rig.box(b, (0, 0, 0), (6, 70, 6), "s_white")
        rig.box(b, (0, 0, 0), (70, 6, 6), "s_white")
    ec = rig.bone("eclipse", (0, SIGIL_Y + 60, 0))
    for r in range(0, 520, 40):
        ring(rig, ec, (0, SIGIL_Y + 60, 0), max(20, r), max(8, int(r / 9)), "black", thick=44, height=8)
    ring(rig, ec, (0, SIGIL_Y + 58, 0), 540, 80, "violet", thick=20, height=6, glow="h_violet")
    pl = rig.bone("night_plasma", (0, 0, 0))
    rig.box(pl, (0, 0, 0), (60, 60, 60), "s_blue", rot=[45, 0, 35.26])
    rig.box(pl, (0, 0, 0), (40, 40, 40), "s_white", rot=[45, 45, 0])
    rig.box(pl, (0, 0, 0), (110, 110, 110), "h_violet", rot=[0, 45, 45])
    for i in range(10):
        rig.box(pl, (0, 0, 0), (4, 4, 120), "s_violet" if i % 2 else "s_blue", rot=[(i * 41) % 180 - 90, (i * 67) % 360, 0])
    lv = rig.bone("light_veil", (0, 0, 0))
    for k in range(18):
        a = 2 * math.pi * k / 18
        for j in range(5):
            y = 40 + j * 110
            r = 380 - j * 40
            rig.box(lv, (r * math.cos(a), y, r * math.sin(a)), (130, 120, 6), "h_white", rot=[0, -math.degrees(a) + 90, 0])
    lp = rig.bone("light_prism", (0, SIGIL_Y - 200, 0))
    rig.box(lp, (0, SIGIL_Y - 200, 0), (140, 140, 140), "white", rot=[45, 0, 35.26])
    rig.box(lp, (0, SIGIL_Y - 200, 0), (220, 220, 220), "h_gold", rot=[0, 45, 45])
    for i in range(8):
        rig.box(lp, (0, SIGIL_Y - 200, 0), (8, 8, 600), "ray", rot=[0, i * 22.5, 0])


def hide_unused(rig, A):
    used = set(rig.keys.get(A, {}))

    def touched(g):
        if g["name"] in used:
            return True
        return any(isinstance(c, dict) and touched(c) for c in g["children"])

    for g in rig.roots:
        if not touched(g):
            rig.key(A, g["name"], "scale", 0, (0, 0, 0), "step")


def remap(t, src, dst):
    if t <= src[0]:
        return dst[0] + (t - src[0])
    for i in range(len(src) - 1):
        if t <= src[i + 1]:
            u = (t - src[i]) / max(1e-6, src[i + 1] - src[i])
            return dst[i] + (dst[i + 1] - dst[i]) * u
    return dst[-1] + (t - src[-1])


def curve(keys, t):
    if t <= keys[0][0]:
        return keys[0][1]
    for i in range(len(keys) - 1):
        a, b = keys[i], keys[i + 1]
        if t <= b[0]:
            u = ease_in_out((t - a[0]) / max(1e-6, b[0] - a[0]))
            return a[1] + (b[1] - a[1]) * u
    return keys[-1][1]


def integral(keys, t, step=0.02):
    total, x = 0.0, 0.0
    while x < t:
        dt = min(step, t - x)
        total += curve(keys, x + dt / 2) * dt
        x += dt
    return total


REF = [0, 1.9, 4.0, 4.6, 6.2, 10.4, 12.0, 21.0]
CAM = {
    "R": [(0, 36), (1.3, 26), (1.9, 21), (3.6, 16), (4.0, 19), (5.2, 24), (6.2, 26), (8.0, 32), (9.6, 28), (10.4, 19), (11.6, 15), (12.0, 13),
          (12.6, 22), (14.0, 30), (17.0, 33), (21.0, 34)],
    "H": [(0, 16), (1.3, 9), (1.9, 6), (3.6, 3), (4.0, 2.5), (5.2, 4), (6.2, 6), (8.0, 14), (9.6, 12), (10.4, 8), (12.0, 5), (12.6, 7),
          (14.0, 11), (21.0, 13)],
    "TY": [(0, 3), (1.9, 3.5), (3.6, 4), (4.0, 14), (5.2, 40), (6.2, 50), (8.0, 34), (9.6, 18), (10.4, 7), (12.0, 6.5), (12.6, 13),
           (14.0, 17), (21.0, 12)],
    "WS": [(0, 0), (4.6, 0), (5.4, 0.55), (6.2, 0.6), (8.0, 0.45), (9.6, 0.3), (10.4, 0.0), (21.0, 0)],
    "OM": [(0, 0.45), (1.2, 0.6), (1.9, 0.75), (3.6, 1.0), (4.0, 0.6), (5.2, 0.35), (6.2, 0.5), (7.5, 1.1), (9.0, 1.6), (10.4, 1.9),
           (11.6, 0.9), (12.0, 0.25), (12.6, 0.4), (14.0, 0.3), (21.0, 0.2)],
}


def camera_keys(rig, A, plan, star_pos):
    anchors = [0, 1.9, 4.0, plan.birth, plan.desc, plan.hover, plan.burst, plan.end]
    scale = 1.0 if plan.sigil else 0.55

    def at(t):
        tr = remap(t, anchors, REF)
        th = -0.95 + integral(CAM["OM"], tr)
        r = curve(CAM["R"], tr) * B * (0.8 + 0.2 * scale + (0.15 if plan.wings == 0 and tr > 12 else 0))
        h = curve(CAM["H"], tr) * B * (0.6 + 0.4 * scale)
        ty = curve(CAM["TY"], tr) * B * (scale if tr > 4.0 and tr < 10.4 else 1)
        ws = curve(CAM["WS"], tr)
        pos = (r * math.cos(th), h, r * math.sin(th))
        sp = star_pos(t)
        tgt = (sp[0] * ws, ty * (1 - ws) + sp[1] * ws, sp[2] * ws)
        return pos, tgt

    def rot(t):
        pos, tgt = at(t)
        dx, dy, dz = tgt[0] - pos[0], tgt[1] - pos[1], tgt[2] - pos[2]
        yaw = math.degrees(math.atan2(dx, dz))
        pitch = math.degrees(math.atan2(dy, math.hypot(dx, dz)))
        return pitch, yaw

    last = [None]

    def unwrap(t):
        p, y = rot(t)
        if last[0] is not None:
            while y - last[0] > 180:
                y -= 360
            while y - last[0] < -180:
                y += 360
        last[0] = y
        return (p, y, 0)

    extra = [b[0] for b in plan.beats] + [plan.burst]
    rig.sample(A, "camera", "position", 0, plan.end, lambda t: at(t)[0], rate=8, extra=extra)
    rig.sample(A, "camera", "rotation", 0, plan.end, unwrap, rate=8, extra=extra)
    rig.sample(A, "camera_target", "position", 0, plan.end, lambda t: at(t)[1], rate=8, extra=extra)


def looped(fn, period):
    if not period:
        return fn

    def out(t):
        a, b = fn(t), fn(t - period)
        w = t / period
        return tuple(a[k] * (1 - w) + b[k] * w for k in range(3))
    return out


def flames(rig, A, plan, end, quiet=False):
    rate = 8
    L = end if quiet else None
    for name, x, z, yaw in flame_positions():
        root = f"flame_{name}"
        body = f"flame_{name}_body"
        ig = IGNITE[name]
        n = Noise1(hash(name) & 0xFFFF)
        n2 = Noise1((hash(name) >> 8) & 0xFFFF)
        rig.groups[root]["rotation"] = [0, round(yaw, 2), 0]

        def flame_scale(t, ig=ig, name=name):
            if quiet:
                return 1.0
            if t < 0.4:
                return 1.0 - 0.7 * smooth(t / 0.4)
            if t < ig:
                return 0.3
            if t < ig + 0.08:
                return 0.3 - 0.12 * smooth((t - ig) / 0.08)
            if t < ig + 0.30:
                return 0.18 + 1.42 * ease_out_expo((t - ig - 0.08) / 0.22)
            if t < ig + 0.80:
                return 1.6 - 0.6 * ease_in_out((t - ig - 0.3) / 0.5)
            if t < 3.6:
                return 1.0
            if t < 4.0:
                return 1.0 - 0.3 * ease_in_out((t - 3.6) / 0.4)
            if t < 4.4:
                return 0.7 + 0.5 * ease_out_expo((t - 4.0) / 0.4)
            if t < plan.hover:
                s = 1.2
                for k, (bt, _, _) in enumerate(plan.beats):
                    if bt - 0.1 <= t < bt + 0.6:
                        s += (0.35 + 0.15 * k) * math.sin(math.pi * (t - bt + 0.1) / 0.7) ** 1.5
                return s
            if t < plan.burst:
                if plan.mythic:
                    keep = (plan.mythic == "night" and name == "dark") or (plan.mythic == "light" and name == "light")
                    return 1.2 + 2.2 * smooth((t - plan.hover) / 0.8) if keep else 1.2 * (1 - smooth((t - plan.hover) / 0.6))
                return 1.2 - 0.95 * ease_in_out((t - plan.hover) / max(0.2, plan.burst - plan.hover - 0.1))
            if t < plan.reveal:
                return 0.25
            big = 1.0 + (1.1 if plan.rank >= 2 else 0.4)
            if t < plan.reveal + 0.6:
                return 0.25 + (big - 0.25) * ease_out_expo((t - plan.reveal) / 0.6)
            if t < plan.settle:
                return big - 0.4 * (big - 1) * ease_in_out((t - plan.reveal - 0.6) / max(0.1, plan.settle - plan.reveal - 0.6))
            return big - 0.4 * (big - 1) - 0.6 * (big - 1) * ease_in_out((t - plan.settle) / max(0.1, plan.end - plan.settle))

        def scale_fn(t, n=n, fs=flame_scale):
            s = fs(t)
            return (s * (1 - 0.04 * n(t * 1.2 + 3)), s * (1 + 0.08 * n(t * 1.1)), s * (1 - 0.04 * n(t * 1.2 + 3)))

        rig.sample(A, body, "scale", 0, end, looped(scale_fn, L), rate=rate, extra=[ig, ig + 0.08, ig + 0.3, plan.burst, plan.reveal])

        def lean_fn(t, n2=n2):
            lean = 0.0
            if quiet:
                lean = 0.0
            elif 1.9 <= t < 3.6:
                lean = 14 * ease_in_out((t - 1.9) / 1.0)
            elif 3.6 <= t < plan.hover:
                lean = 14 - 10 * ease_in_out((t - 3.6) / 1.0)
            elif plan.hover <= t < plan.burst:
                lean = 4 + 36 * ease_in_out((t - plan.hover) / max(0.2, plan.burst - plan.hover - 0.1))
            elif plan.burst <= t < plan.burst + 0.6:
                lean = 40 - 52 * ease_out_expo((t - plan.burst) / 0.6)
            elif plan.burst + 0.6 <= t < plan.burst + 2.0:
                lean = -12 + 12 * ease_in_out((t - plan.burst - 0.6) / 1.4)
            return (lean + 4 * n2(t * 0.5), 0, 3 * n2(t * 0.4 + 5))

        rig.sample(A, body, "rotation", 0, end, looped(lean_fn, L), rate=6, extra=[plan.burst, plan.burst + 0.6])
        for i in range(8):
            lb = f"flame_{name}_layer_{i}"
            if lb not in rig.groups:
                break
            nl = Noise1(300 + i * 7 + len(name))
            amp = 0.35 + 0.28 * i
            sp = 0.55 if quiet else 1.0
            rig.sample(A, lb, "position", 0, end, looped(lambda t, nl=nl, amp=amp, sp=sp: (amp * nl(t * 1.6 * sp), 0.25 * amp * nl(t * 2.0 * sp + 4), amp * nl(t * 1.5 * sp + 9)), L), rate=6)
            rig.sample(A, lb, "scale", 0, end, looped(lambda t, nl=nl, i=i, sp=sp: (1 + 0.06 * nl(t * 2.2 * sp + i), 1 + 0.1 * nl(t * 2.0 * sp + 2 * i), 1 + 0.06 * nl(t * 2.1 * sp + 3 * i)), L), rate=6)
    sp = 0.45 if quiet else 1.0

    def spin(deg_per_s, sym=360):
        if not L:
            return lambda t: (0, deg_per_s * t, 0)
        turns = max(1, round(abs(deg_per_s) * sp * L / sym)) * (1 if deg_per_s > 0 else -1)
        return lambda t: (0, turns * sym * t / L, 0)

    rig.sample(A, "flame_water_crown", "rotation", 0, end, spin(-90 / sp * sp, 60), rate=1, interp="linear")
    wcr = 2 * math.pi * 3 / L if L else 4
    rig.sample(A, "flame_water_crown", "scale", 0, end, lambda t: (1 + 0.3 * math.sin(t * wcr), 1 + 0.4 * math.sin(t * wcr + 1), 1 + 0.3 * math.sin(t * wcr)), rate=6)
    for i in range(6):
        nt = Noise1(100 + i)
        rig.sample(A, f"flame_fire_tongue_{i}", "rotation", 0, end, looped(lambda t, nt=nt: (14 * nt(t * 2.0 * sp), 0, 14 * nt(t * 1.8 * sp + 1)), L), rate=6)
        rig.sample(A, f"flame_fire_tongue_{i}", "scale", 0, end, looped(lambda t, nt=nt: (1, 1 + 0.35 * nt(t * 2.2 * sp + 2), 1), L), rate=6)
    for i in range(8):
        b = f"flame_fire_ember_{i}"
        period = (1.1 + 0.13 * i) / sp
        t = (i * 0.37) % period
        while t < end:
            rig.key(A, b, "position", t, (0, 0, 0), "linear")
            rig.key(A, b, "scale", t, (0, 0, 0), "linear")
            rig.key(A, b, "scale", t + 0.1, (1, 1, 1), "linear")
            rig.key(A, b, "position", t + period - 0.02, (2 * math.sin(i + t), 46 + 6 * (i % 3), 2 * math.cos(i + t)), "linear")
            rig.key(A, b, "scale", t + period - 0.02, (0.2, 0.2, 0.2), "linear")
            t += period
    for i in range(4):
        nd = Noise1(200 + i)
        rig.sample(A, f"flame_dark_tongue_{i}", "rotation", 0, end, looped(lambda t, nd=nd: (20 * nd(t * 0.6 * sp), 0, 20 * nd(t * 0.5 * sp + 2)), L), rate=5)
    for b, r, sym in (("flame_earth_orbit", 50, 45), ("flame_water_orbit", -140, 30), ("flame_light_rays", 60, 45), ("flame_light_halo", -90, 30)):
        rig.sample(A, b, "rotation", 0, end, spin(r, sym), rate=1, interp="linear")
    rig.sample(A, "flame_light_rays", "scale", 0, end, lambda t: tuple([1 + 0.25 * math.sin(t * 2 * math.pi * (round(3.5 * sp * (L or 1) / 6.283) or 1) / (L or 1) if L else t * 3.5)] * 3), rate=6)
    for r in range(4):
        rig.sample(A, f"flame_air_ribbon_{r}", "rotation", 0, end, spin(-(260 + 40 * r)), rate=1, interp="linear")
    rig.sample(A, "flame_air_leaves", "rotation", 0, end, lambda t: (10 * math.sin(t * 2 * math.pi / (L or 4)), spin(200), 0) if False else (10 * math.sin(t * 2 * math.pi / (L or 4)), 0, 0), rate=4)
    rig.sample(A, "flame_earth_layer_0", "rotation", 0, end, lambda t: (6 * math.sin(t * 2 * math.pi / (L or 8)), 0, 5 * math.cos(t * 2 * math.pi / (L or 8))), rate=3)
    rig.sample(A, "flame_earth_body", "position", 0, end, lambda t: (0, 3 * math.sin(t * 2 * math.pi / (L or 6)), 0), rate=4)
    rig.sample(A, "flame_water_body", "rotation", 0, end, spin(120), rate=1, interp="linear")
    eyes_on = plan.beats[-1][0] if (plan.beats and plan.rank == 4) else None
    if plan.mythic == "night":
        eyes_on = plan.hover
    if eyes_on is not None and not quiet:
        rig.key(A, "flame_dark_eyes", "scale", 0, (0, 0, 0), "linear")
        rig.key(A, "flame_dark_eyes", "scale", eyes_on, (0, 0, 0), "linear")
        rig.key(A, "flame_dark_eyes", "scale", eyes_on + 0.15, (1.4, 1.4, 1.4))
        rig.key(A, "flame_dark_eyes", "scale", eyes_on + 0.35, (1, 1, 1))
        rig.key(A, "flame_dark_eyes", "scale", plan.burst, (1, 1, 1), "linear")
        rig.key(A, "flame_dark_eyes", "scale", plan.burst + 0.15, (0, 0, 0), "linear")
    else:
        rig.key(A, "flame_dark_eyes", "scale", 0, (0, 0, 0), "step")


def summon(rig, A, plan, rnd):
    P = plan
    flames(rig, A, P, P.end)
    beats_and_burst = [b[0] for b in P.beats] + [P.burst]
    for i in range(24):
        b = f"rune_{i}"
        on = 1.9 + 0.07 * i
        off = P.end - 1.4 + 0.05 * (23 - i)
        rig.hide_until(A, b, on)
        rig.key(A, b, "scale", on + 0.06, (1.5, 3, 1.5))
        rig.key(A, b, "scale", on + 0.18, (1, 1, 1))
        rig.key(A, b, "position", on, (0, 4, 0))
        rig.key(A, b, "position", on + 0.18, (0, 0, 0))
        for bt in beats_and_burst:
            rig.key(A, b, "scale", bt, (1, 1, 1))
            rig.key(A, b, "scale", bt + 0.05 + 0.004 * i, (1.4, 2.5, 1.4))
            rig.key(A, b, "scale", bt + 0.3 + 0.004 * i, (1, 1, 1))
        rig.key(A, b, "scale", off, (1, 1, 1), "linear")
        rig.key(A, b, "scale", off + 0.2, (0, 0, 0), "linear")

    rig.hide_until(A, "floor_circle", 1.9)
    for t, v in ((2.45, 1.06), (2.7, 1), (3.6, 1), (4.0, 0.9), (4.15, 1.12), (4.5, 1)):
        rig.key(A, "floor_circle", "scale", t, (v, 1, v))
    rig.key(A, "floor_circle", "scale", P.end - 1.4, (1, 1, 1), "linear")
    rig.key(A, "floor_circle", "scale", P.end - 0.4, (0, 1, 0), "linear")
    hs = [h[0] for h in P.hitstops]
    rig.sample(A, "floor_ring_outer", "rotation", 0, P.end, lambda t: (0, 18 * P.warp(t) + (40 * smooth((t - P.hover) / 1.6) if t > P.hover else 0), 0), rate=2, extra=hs)
    rig.sample(A, "floor_ring_inner", "rotation", 0, P.end, lambda t: (0, -30 * P.warp(t), 0), rate=2, extra=hs)

    for name, x, z, _ in flame_positions():
        for i in range(12):
            c = f"stream_{name}_{i}"
            on = 2.2 + IGNITE[name] * 0.3 + i * 0.05
            rig.hide_until(A, c, on)
            rig.key(A, c, "scale", on + 0.12, (1.4, 1.4, 1.4))
            rig.key(A, c, "scale", on + 0.25, (1, 1, 1))
            rig.sample(A, c, "scale", on + 0.3, P.burst, lambda t, i=i: tuple([1 + 0.3 * math.sin(t * 7 - i * 0.7) + (0.6 * smooth((t - P.hover) / 1.2) if t > P.hover else 0)] * 3), rate=4)
            rig.key(A, c, "scale", P.burst + 0.05, (0, 0, 0), "linear")

    ph = P.pillar_h / 96
    rig.hide_until(A, "pillar", 4.0)
    rig.key(A, "pillar", "scale", 4.06, (1.6, 0.35 * ph, 1.6))
    rig.key(A, "pillar", "scale", 4.18, (0.8, ph, 0.8))
    rig.key(A, "pillar", "scale", 4.4, (1, ph, 1))
    rig.sample(A, "pillar", "scale", 4.5, P.burst - 0.1, lambda t: (1 + 0.12 * math.sin(t * 5), ph, 1 + 0.12 * math.sin(t * 5)), rate=4)
    rig.key(A, "pillar", "scale", P.burst, (0.6, ph, 0.6))
    rig.key(A, "pillar", "scale", P.burst + 0.1, (1.6 + 0.3 * P.rank, ph, 1.6 + 0.3 * P.rank))
    rig.key(A, "pillar", "scale", P.burst + 0.6, (1.3, ph, 1.3))
    rig.key(A, "pillar", "scale", P.settle, (1.1, ph, 1.1), "linear")
    rig.key(A, "pillar", "scale", P.end - 1.4, (0, ph, 0), "linear")
    for h in ("a", "b"):
        sign = 1 if h == "a" else -1
        rig.sample(A, f"pillar_helix_{h}", "rotation", 4.0, P.end - 1.4, lambda t, sign=sign: (0, sign * 160 * P.warp(t) + sign * 400 * smooth((t - P.hover) / 1.6), 0), rate=2, interp="linear", extra=hs)
    rnd2 = random.Random(77)
    for i in range(40):
        b = f"mote_{i}"
        period = rnd2.uniform(1.6, 3.2)
        t = 4.05 + rnd2.uniform(0, period)
        top = rnd2.uniform(0.35, 1.0) * SIGIL_Y * ph
        rig.hide_until(A, b, t)
        while t + period < P.settle:
            rig.key(A, b, "position", t, (0, 0, 0), "linear")
            rig.key(A, b, "scale", t + 0.002, (1, 1, 1), "linear")
            rig.key(A, b, "position", t + period - 0.01, (0, top, 0), "linear")
            rig.key(A, b, "scale", t + period - 0.01, (0.3, 2.5, 0.3), "linear")
            rig.key(A, b, "scale", t + period, (0, 0, 0), "linear")
            t += period
    rig.hide_until(A, "pillar_wave", 4.0)
    rig.key(A, "pillar_wave", "scale", 4.02, (0.5, 1, 0.5), "linear")
    rig.key(A, "pillar_wave", "scale", 4.7, (9, 0.5, 9), "linear")
    rig.key(A, "pillar_wave", "scale", 4.9, (10, 0, 10), "linear")

    stack_h = [10, 24, 44]
    for k, (open_t, spin) in enumerate(((4.10, 40), (4.22, -55), (4.34, 70))):
        b = f"stack_{k + 1}"
        if k >= P.stacks:
            rig.key(A, b, "scale", 0, (0, 0, 0), "step")
            continue
        if not P.sigil and stack_h[k] > P.pillar_h:
            rig.key(A, b, "position", 0, (0, (P.pillar_h - 6) * B - stack_h[k] * B, 0), "step")
        rig.hide_until(A, b, open_t)
        rig.key(A, b, "scale", open_t + 0.16, (1.12, 1, 1.12))
        rig.key(A, b, "scale", open_t + 0.36, (1, 1, 1))
        rig.key(A, b, "scale", P.burst, (1, 1, 1))
        rig.key(A, b, "scale", P.burst + 0.12, (1.35, 1, 1.35))
        rig.key(A, b, "scale", P.burst + 0.6, (1.05, 1, 1.05))
        rig.key(A, b, "scale", P.settle + 0.3 * (2 - k), (1, 1, 1), "linear")
        rig.key(A, b, "scale", P.settle + 1.2 + 0.3 * (2 - k), (0, 1, 0), "linear")
        rig.sample(A, b, "rotation", open_t, P.settle + 2.0, lambda t, s=spin: (0, s * P.warp(t) + s * 2 * smooth((t - P.settle) / 1.5), 0), rate=2, interp="linear", extra=hs)
        rig.sample(A, f"stack_{k + 1}_inner", "rotation", open_t, P.settle + 2.0, lambda t, s=spin: (0, -1.6 * s * P.warp(t), 0), rate=2, interp="linear", extra=hs)

    S = "sky_sigil"
    if P.sigil:
        if P.sigil_h != 96:
            rig.key(A, S, "position", 0, (0, (P.sigil_h - 96) * B, 0), "step")
        rig.hide_until(A, S, 4.46)
        rig.key(A, S, "scale", 4.75, (1.08, 1, 1.08))
        rig.key(A, S, "scale", 5.1, (1, 1, 1))
        rig.key(A, S, "scale", P.burst - 0.1, (1, 1, 1))
        rig.key(A, S, "scale", P.burst, (0.92, 1, 0.92))
        rig.key(A, S, "scale", P.burst + 0.25, (1.5, 1, 1.5), "linear")
        rig.key(A, S, "scale", P.burst + 1.4, (2.2, 0, 2.2), "linear")
        for part, spin in (("outer", 12), ("mid", -20), ("inner", 30), ("hex", -8), ("branches", 12), ("teeth", 12)):
            rig.sample(A, f"{S}_{part}", "rotation", 4.46, P.burst + 1.5, lambda t, s=spin: (0, s * P.warp(t) + (6 * s * smooth((t - P.burst) / 1.2) if t > P.burst else 0), 0), rate=2, interp="linear", extra=hs)
        rig.key(A, f"{S}_hex", "scale", 0, (0, 1, 0), "linear")
        rig.key(A, f"{S}_hex", "scale", 4.8, (0, 1, 0), "linear")
        rig.key(A, f"{S}_hex", "scale", 5.2, (1, 1, 1))
        rig.key(A, f"{S}_branches", "position", P.burst, (0, 0, 0))
        rig.key(A, f"{S}_branches", "position", P.burst + 1.4, (0, -260, 0))
        for i, (bt, _, _) in enumerate(P.beats):
            rig.key(A, f"{S}_mid", "scale", bt - 0.05, (1, 1, 1))
            rig.key(A, f"{S}_mid", "scale", bt + 0.04, (1.05 + 0.02 * i, 1, 1.05 + 0.02 * i))
            rig.key(A, f"{S}_mid", "scale", bt + 0.4, (1, 1, 1))
    else:
        rig.key(A, S, "scale", 0, (0, 0, 0), "step")

    rig.key(A, "birth_flash", "position", 0, (0, P.top - SIGIL_Y, 0), "step")
    rig.hide_until(A, "birth_flash", P.birth + 1.3 if P.sigil else P.birth + 0.6)
    t_flash = P.birth + (1.45 if P.sigil else 0.7)
    rig.key(A, "birth_flash", "scale", t_flash, (4 * (0.5 + 0.1 * P.rank),) * 3, "linear")
    rig.key(A, "birth_flash", "scale", t_flash + 0.3, (0, 0, 0), "linear")

    if P.pulls:
        multi(rig, A, P, rnd)
    else:
        star(rig, A, P)

    for k in range(4):
        b = f"beat_wave_{k}"
        if k >= len(P.beats):
            rig.key(A, b, "scale", 0, (0, 0, 0), "step")
            continue
        bt = P.beats[k][0]
        rig.hide_until(A, b, bt)
        rig.key(A, b, "position", 0, P.star_path(bt), "step")
        rig.key(A, b, "scale", bt + 0.01, (0.3, 1, 0.3), "linear")
        rig.key(A, b, "scale", bt + 0.55, (5 + 2.5 * k, 0.6, 5 + 2.5 * k), "linear")
        rig.key(A, b, "scale", bt + 0.75, (6 + 3 * k, 0, 6 + 3 * k), "linear")
    for i in range(10):
        b = f"crack_{i}"
        if not P.cracks:
            rig.key(A, b, "scale", 0, (0, 0, 0), "step")
            continue
        on = (P.beats[-1][0] if P.beats else P.crack_t) + 0.025 * i
        rig.hide_until(A, b, on)
        rig.key(A, b, "scale", on + 0.12, (1.2, 1.2, 1.2))
        rig.key(A, b, "scale", on + 0.3, (1, 1, 1))
        rig.key(A, b, "scale", P.burst, (1, 1, 1), "linear")
        rig.key(A, b, "scale", P.burst + 0.4, (1.6, 0, 1.6), "linear")

    f = 0.45 + 0.14 * P.rank
    rig.hide_until(A, "flash", P.burst)
    rig.key(A, "flash", "scale", P.burst + 0.06, (5 * f,) * 3, "linear")
    rig.key(A, "flash", "scale", P.burst + 0.14, (8 * f,) * 3, "linear")
    rig.key(A, "flash", "scale", P.burst + 0.4, (0, 0, 0), "linear")
    for i, (speed, dur) in enumerate(((22, 0.9), (14, 1.3), (8, 1.9))):
        b = f"ground_wave_{i}"
        if i >= P.waves:
            rig.key(A, b, "scale", 0, (0, 0, 0), "step")
            continue
        sp = speed * (0.5 + 0.125 * P.rank)
        t0 = P.burst + 0.05 + 0.05 * i
        rig.hide_until(A, b, t0)
        rig.key(A, b, "scale", t0 + 0.01, (0.5, 1.6, 0.5), "linear")
        rig.key(A, b, "scale", t0 + dur * 0.7, (sp * 0.85, 0.8, sp * 0.85), "linear")
        rig.key(A, b, "scale", t0 + dur, (sp, 0, sp), "linear")
    for i in range(48):
        b = f"shard_{i}"
        if i >= P.shards:
            rig.key(A, b, "scale", 0, (0, 0, 0), "step")
            continue
        a = rnd.uniform(0, 2 * math.pi)
        el = rnd.uniform(-0.2, 1.1)
        spd = rnd.uniform(160, 460) * (0.6 + 0.1 * P.rank)
        v = (spd * math.cos(a) * math.cos(el), spd * math.sin(el), spd * math.sin(a) * math.cos(el))
        t0 = P.burst + 0.06
        rig.hide_until(A, b, t0 - 0.02)
        rig.key(A, b, "scale", t0, (1.6, 1.6, 1.6), "linear")
        life = rnd.uniform(1.4, 2.4)
        rig.key(A, b, "scale", t0 + life * 0.6, (1, 1, 1), "linear")
        rig.key(A, b, "scale", t0 + life, (0, 0, 0), "linear")
        for j in range(8):
            tt = life * j / 7
            drag = (1 - math.exp(-2.2 * tt)) / 2.2
            rig.key(A, b, "position", t0 + tt, (v[0] * drag, v[1] * drag - 120 * tt * tt, v[2] * drag))
        rig.key(A, b, "rotation", t0, (0, 0, 0), "linear")
        rig.key(A, b, "rotation", t0 + life, (rnd.uniform(-720, 720), rnd.uniform(-720, 720), 0), "linear")

    if P.dragon:
        dragon(rig, A, P)
    else:
        rig.key(A, "dragon", "scale", 0, (0, 0, 0), "step")

    if P.beams:
        for i, (name, x, z, _) in enumerate(flame_positions()):
            b = f"beam_{name}"
            on = P.reveal - 0.1 + 0.05 * i
            rig.hide_until(A, b, on)
            rig.key(A, b, "scale", on + 0.1, (1.6, 1.15 * P.wings, 1.6))
            rig.key(A, b, "scale", on + 0.3, (1, P.wings, 1))
            rig.sample(A, b, "scale", on + 0.35, on + 1.4, lambda t, i=i: (1 + 0.25 * math.sin(t * 10 + i), P.wings, 1 + 0.25 * math.sin(t * 10 + i)), rate=6)
            rig.key(A, b, "scale", on + 1.45, (1, P.wings, 1), "linear")
            rig.key(A, b, "scale", on + 1.8, (0, 1.2 * P.wings, 0), "linear")
    else:
        rig.key(A, "element_beams", "scale", 0, (0, 0, 0), "step")
    if P.wings:
        wings(rig, A, P, rnd)
    else:
        rig.key(A, "wings", "scale", 0, (0, 0, 0), "step")
        rig.key(A, "feathers", "scale", 0, (0, 0, 0), "step")
    if P.mythic:
        mythic(rig, A, P)
    camera_keys(rig, A, P, lambda t: star_pos_for_camera(P, t))
    rig.lengths[A] = round(P.end, 2)
    hide_unused(rig, A)


def star_pos_for_camera(P, t):
    if t < P.desc:
        return (0, P.top, 0)
    if t < P.hover:
        return P.star_path(t)
    return HOVER


def star(rig, A, P):
    on = P.birth + (0.6 if P.sigil else 0.1)
    rig.key(A, "star", "position", 0, (0, P.top, 0), "step")
    base = 2.6 if P.sigil else 1.4
    rig.hide_until(A, "star", on)
    t_flash = P.birth + (1.45 if P.sigil else 0.7)
    rig.key(A, "star", "scale", t_flash - 0.35, (base * 0.6,) * 3)
    rig.key(A, "star", "scale", t_flash - 0.1, (base * 0.45,) * 3)
    rig.key(A, "star", "scale", t_flash, (base * 1.3,) * 3)
    rig.key(A, "star", "scale", P.desc, (base,) * 3)
    last = base
    for k, (bt, _, _) in enumerate(P.beats):
        amp = (2.2 + 0.25 * k) * (0.42 + 0.58 * P.star_path(bt)[1] / SIGIL_Y) * (1 if P.sigil else 0.7)
        rig.key(A, "star", "scale", bt - 0.12, (last,) * 3)
        rig.key(A, "star", "scale", bt - 0.02, (last * 0.75,) * 3)
        rig.key(A, "star", "scale", bt + 0.05, (amp * 1.2,) * 3)
        rig.key(A, "star", "scale", bt + 0.35, (amp,) * 3)
        last = amp
    rig.key(A, "star", "scale", P.hover, (1.05,) * 3)
    hold = P.burst - P.hover
    rig.key(A, "star", "scale", P.hover + hold * 0.7, (0.95,) * 3)
    rig.key(A, "star", "scale", P.burst - 0.12, (1.3,) * 3)
    rig.key(A, "star", "scale", P.burst, (0.3,) * 3, "linear")
    rig.key(A, "star", "scale", P.burst + 0.06, (0, 0, 0), "linear")
    rig.sample(A, "star", "position", P.desc, P.hover, P.star_path, rate=12, extra=[b[0] for b in P.beats])
    rig.sample(A, "star", "position", P.hover, P.burst, lambda t: (1.6 * math.sin(t * 61), HOVER[1] + 1.6 * math.sin(t * 47 + 1) * smooth((t - P.hover) / 1.0), 1.6 * math.sin(t * 53 + 2)), rate=16)
    rig.sample(A, "star", "rotation", on, P.burst, lambda t: (35 * P.warp(t), 140 * P.warp(t), 0), rate=2, interp="linear", extra=[h[0] for h in P.hitstops])
    switch = [0.0] + [b[0] for b in P.beats]
    shown = [0] + [c for _, c, _ in P.beats]
    for idx, c in enumerate(TIER_COLORS):
        b = f"star_{c}"
        rig.key(A, b, "scale", 0, (1, 1, 1) if idx == 0 else (0, 0, 0), "step")
        for k, col in enumerate(shown):
            if k == 0:
                continue
            rig.key(A, b, "scale", switch[k], (1, 1, 1) if col == idx else (0, 0, 0), "step")
    rig.sample(A, "star_ring", "rotation", on, P.burst, lambda t: (70 + 20 * math.sin(t), 260 * P.warp(t), 20 * math.cos(t * 1.3)), rate=4)
    rig.key(A, "multi_stars", "scale", 0, (0, 0, 0), "step")
    for g in range(8):
        b = f"star_ghost_{g}"
        lag = 0.035 * (g + 1)
        rig.hide_until(A, b, P.desc + lag)
        size = (2.2 - 0.22 * g) * (1 if P.sigil else 0.6)
        rig.key(A, b, "scale", P.desc + lag + 0.1, (size,) * 3)
        rig.key(A, b, "scale", P.hover - 0.1, (size,) * 3, "linear")
        rig.key(A, b, "scale", P.hover + 0.2, (0, 0, 0), "linear")
        rig.sample(A, b, "position", P.desc + lag, P.hover + 0.2, lambda t, lag=lag: P.star_path(t - lag), rate=12)


def multi(rig, A, P, rnd):
    rig.key(A, "star", "scale", 0, (0, 0, 0), "step")
    for g in range(8):
        rig.key(A, f"star_ghost_{g}", "scale", 0, (0, 0, 0), "step")
    n = len(P.pulls)
    for i, p in enumerate(P.pulls):
        b = f"multi_star_{i}"
        r = TIERS.index(p)
        t0 = P.spawn[i]
        dur = [0.42, 0.6, 0.95, 1.4, 1.95][r]
        a0 = 2 * math.pi * i / n
        ring_r = 330

        def orbit(t, a0=a0):
            a = a0 + 0.35 * (t - 4.7)
            return (ring_r * math.cos(a), P.top - 40 + 20 * math.sin(t * 2 + a0), ring_r * math.sin(a))

        start = orbit(t0 + dur)
        if r == 0:
            arrive = [(t0, start)]
        elif r == 1:
            arrive = [(t0, (start[0], start[1] + 14 * B, start[2])), (t0 + dur, start)]
        elif r == 2:
            arrive = [(t0, (start[0] * 0.6, start[1] + 40 * B, start[2] * 0.6)), (t0 + dur, start)]
        elif r == 3:
            arrive = [(t0, (0, start[1] + 90 * B, 0)), (t0 + dur, start)]
        else:
            arrive = [(t0, (start[0] * 4, start[1] + 10 * B, start[2] * 4)), (t0 + dur * 0.6, (start[0] * 1.6, start[1] + 30 * B, start[2] * 1.6)), (t0 + dur, start)]
        rig.hide_until(A, b, t0)
        size = 1.6 + 0.35 * r
        rig.key(A, b, "scale", t0 + 0.1, (size * 1.3,) * 3)
        rig.key(A, b, "scale", t0 + dur, (size,) * 3)
        for t, pos in arrive:
            rig.key(A, b, "position", t, pos)
        fall0 = P.desc + 0.09 * i
        rig.sample(A, b, "position", t0 + dur + 0.05, fall0, orbit, rate=4)

        def fall(t, i=i, a0=a0, fall0=fall0):
            u = ease_in_out(min(1, (t - fall0) / 2.6))
            o = orbit(fall0)
            a = a0 + 0.35 * (fall0 - 4.7) + u * 2.4 * math.pi
            rr = ring_r * (1 - u) + 30 * math.sin(math.pi * u)
            return (rr * math.cos(a), o[1] * (1 - u) + HOVER[1] * u, rr * math.sin(a))

        rig.sample(A, b, "position", fall0, fall0 + 2.6, fall, rate=10)
        rig.key(A, b, "scale", fall0 + 2.5, (size,) * 3, "linear")
        rig.key(A, b, "scale", fall0 + 2.62, (0, 0, 0), "linear")
        rig.sample(A, b, "rotation", t0, fall0 + 2.6, lambda t: (40 * t, 160 * t, 0), rate=1, interp="linear")
    best = max(TIERS.index(p) for p in P.pulls)
    for idx, c in enumerate(TIER_COLORS):
        rig.key(A, f"star_{c}", "scale", 0, (1, 1, 1) if idx == best else (0, 0, 0), "step")
    rig.key(A, "star", "position", 0, HOVER, "step")
    rig.hide_until(A, "star", P.desc + 2.4)
    rig.key(A, "star", "scale", P.desc + 2.6, (0.8,) * 3)
    for i in range(n):
        t = P.desc + 0.09 * i + 2.62
        rig.key(A, "star", "scale", t, (0.8 + 0.06 * i,) * 3)
        rig.key(A, "star", "scale", t + 0.05, (0.9 + 0.06 * i,) * 3)
    rig.key(A, "star", "scale", P.burst - 0.12, (1.6,) * 3)
    rig.key(A, "star", "scale", P.burst, (0.3,) * 3, "linear")
    rig.key(A, "star", "scale", P.burst + 0.06, (0, 0, 0), "linear")
    rig.sample(A, "star_ring", "rotation", P.desc + 2.4, P.burst, lambda t: (70, 300 * t, 0), rate=2, interp="linear")


def dragon(rig, A, P):
    w = P.warp
    t_on = P.desc + 0.4
    h = 78 * B * P.sigil_h / 96

    def pos(t):
        if t < P.hover:
            th = 0.9 + 2 * math.pi * (w(t) - w(t_on)) / 4.4
            r = 46 * B
            return (r * math.cos(th), h + 60 * math.sin(th * 2), r * math.sin(th)), th
        th0 = 0.9 + 2 * math.pi * (w(P.hover) - w(t_on)) / 4.4
        u = ease_in_out((t - P.hover) / 1.4)
        th = th0 + 0.9 * u
        r = 46 * B * (1 - 0.3 * u)
        return (r * math.cos(th), h + 60 * math.sin(th0 * 2) - (h + 40 * B) * u ** 1.6, r * math.sin(th)), th

    end = P.hover + 1.4
    rig.hide_until(A, "dragon", t_on)
    rig.key(A, "dragon", "scale", t_on + 0.7, (1, 1, 1))
    rig.key(A, "dragon", "scale", end - 0.4, (1, 1, 1), "linear")
    rig.key(A, "dragon", "scale", end, (0, 0, 0), "linear")
    rig.sample(A, "dragon", "position", t_on, end, lambda t: pos(t)[0], rate=8, extra=[b[0] for b in P.beats])
    rig.sample(A, "dragon", "rotation", t_on, end,
               lambda t: (40 * smooth((t - P.hover) / 0.8) if t > P.hover else -8 * math.sin(t * 1.3), -math.degrees(pos(t)[1]),
                          -12 + (12 * smooth((t - P.hover) / 0.6) if t > P.hover else 0)), rate=8)
    for side, s in (("r", 1), ("l", -1)):
        rig.sample(A, f"dragon_wing_{side}", "rotation", t_on, end, lambda t, s=s: (0, 0, s * (8 + 30 * math.sin(2 * math.pi * w(t) / 1.15) + (28 * smooth((t - P.hover) / 0.5) if t > P.hover else 0))), rate=10)
        rig.sample(A, f"dragon_wing_{side}_tip", "rotation", t_on, end, lambda t, s=s: (0, 0, s * 22 * math.sin(2 * math.pi * w(t) / 1.15 - 0.9)), rate=10)
    rig.sample(A, "dragon_neck", "rotation", t_on, end, lambda t: (6 * math.sin(t * 2.1), 10 * math.sin(t * 1.3), 0), rate=4)
    rig.sample(A, "dragon_tail", "rotation", t_on, end, lambda t: (6 * math.sin(t * 2.3 + 1), 14 * math.sin(t * 1.7), 0), rate=5)
    r0 = t_on + 0.4
    for dt, ang in ((0, 0), (0.25, 8), (0.4, -26), (1.0, -22), (1.3, 0)):
        rig.key(A, "dragon_head", "rotation", r0 + dt, (ang, 0, 0))
    for dt, ang in ((0, 0), (0.3, 0), (0.42, 34), (1.0, 30), (1.35, 0)):
        rig.key(A, "dragon_jaw", "rotation", r0 + dt, (ang, 0, 0))
    for dt, ang in ((0, 0), (0.3, -18), (0.8, 6)):
        rig.key(A, "dragon_head", "rotation", P.hover + dt, (ang, 0, 0))
    for dt, ang in ((0.1, 0), (0.25, 28), (0.9, 0)):
        rig.key(A, "dragon_jaw", "rotation", P.hover + dt, (ang, 0, 0))


def wings(rig, A, P, rnd):
    s0 = P.wings
    t = P.reveal
    rig.hide_until(A, "wings", t)
    rig.key(A, "wings", "scale", t + 0.35, (1.05 * s0,) * 3)
    rig.key(A, "wings", "scale", t + 0.55, (s0,) * 3)
    fold_t = P.settle
    rig.key(A, "wings", "scale", fold_t + 0.8, (s0,) * 3, "linear")
    rig.key(A, "wings", "scale", fold_t + 1.8, (0, 0, 0), "linear")
    rig.key(A, "wings", "position", t, (0, -60, 0))
    rig.key(A, "wings", "position", t + 0.5, (0, 0, 0))
    rig.key(A, "wings", "position", t + 2.6, (0, 10, 0))
    rig.key(A, "wings", "position", fold_t + 0.8, (0, 20, 0))
    rig.key(A, "wings", "position", fold_t + 1.8, (0, 160, 0))
    flaps = [(t + 1.2, 0.8), (t + 2.2, 0.8)] if P.settle - t > 3 else [(t + 1.2, 0.8)]

    def pose(x):
        folded = (-62, -95, -60)
        if x < t + 0.2:
            return folded
        if x < t + 0.9:
            u = (x - t - 0.2) / 0.7
            e = ease_out_expo(u)
            over = math.sin(math.pi * min(1, u * 1.3)) * 0.12
            return tuple(folded[k] * (1 - e) - folded[k] * over for k in range(3))
        for ft, fd in flaps:
            if ft - 0.25 <= x < ft:
                u = ease_in_out((x - ft + 0.25) / 0.25)
                return (18 * u, -10 * u, 10 * u)
            if ft <= x < ft + fd:
                u = (x - ft) / fd
                down = math.sin(math.pi * min(1, u * 1.6))
                up = smooth((u - 0.6) / 0.4)
                return (18 - 58 * down * (1 - up) - 18 * up, -10 + 30 * down * (1 - up) + 10 * up, 10 - 10 * up)
        if x < fold_t:
            return (4 * math.sin(x * 2), 0, 0)
        if x < fold_t + 0.8:
            u = ease_in_out((x - fold_t) / 0.8)
            return tuple(folded[k] * u * 0.7 for k in range(3))
        return tuple(folded[k] * 0.7 for k in range(3))

    extra = [t + 0.9] + [f[0] for f in flaps]
    for side, s in (("r", 1), ("l", -1)):
        rig.sample(A, f"wing_{side}", "rotation", t, fold_t + 1.8, lambda x, s=s: (0, 0, s * pose(x)[0]), rate=16, extra=extra)
        rig.sample(A, f"wing_{side}_fore", "rotation", t, fold_t + 1.8, lambda x, s=s: (0, 0, s * pose(x)[1]), rate=16, extra=extra)
        rig.sample(A, f"wing_{side}_hand", "rotation", t, fold_t + 1.8, lambda x, s=s: (0, 0, s * (pose(x)[2] + 8 * math.sin(x * 4))), rate=16, extra=extra)
        rig.sample(A, f"wing_{side}_coverts", "rotation", t, fold_t + 1.8, lambda x, s=s: (0, 0, s * 0.3 * pose(x - 0.06)[0]), rate=10)
    count = int(30 * s0)
    for i in range(30):
        b = f"feather_{i}"
        if i >= count:
            rig.key(A, b, "scale", 0, (0, 0, 0), "step")
            continue
        t0 = flaps[0][0] + 0.1 + (i % 15) * 0.07 + (1.0 if i >= 15 and len(flaps) > 1 else 0)
        x0 = rnd.uniform(-300, 300) * s0
        y0 = WING_Y + rnd.uniform(-40, 60) * s0
        z0 = rnd.uniform(-30, 30)
        fall = rnd.uniform(4.0, 5.2)
        rig.hide_until(A, b, t0)
        rig.key(A, b, "scale", t0 + 0.2, (1.4,) * 3, "linear")
        rig.key(A, b, "scale", t0 + fall * 0.85, (1.2,) * 3, "linear")
        rig.key(A, b, "scale", t0 + fall, (0, 0, 0), "linear")
        ph = rnd.uniform(0, 6.28)
        rig.sample(A, b, "position", t0, t0 + fall, lambda x, t0=t0, x0=x0, y0=y0, z0=z0, fall=fall, ph=ph:
                   (x0 * (1 + 0.25 * (x - t0) / fall) + 22 * math.sin((x - t0) * 2.0 + ph), y0 - (y0 - 6) * ((x - t0) / fall) ** 1.15, z0 + 14 * math.cos((x - t0) * 1.7 + ph)), rate=4)
        rig.sample(A, b, "rotation", t0, t0 + fall, lambda x, ph=ph: (35 * math.sin(x * 2.0 + ph), 70 * x, 25 * math.cos(x * 1.7 + ph)), rate=4)


def mythic(rig, A, P):
    h0 = P.hover
    if P.mythic == "night":
        rig.hide_until(A, "eclipse", h0 - 0.2)
        rig.key(A, "eclipse", "scale", h0 + 0.6, (1, 1, 1))
        rig.key(A, "eclipse", "scale", P.burst + 0.2, (1, 1, 1))
        rig.key(A, "eclipse", "scale", P.settle, (1.3, 1, 1.3), "linear")
        rig.key(A, "eclipse", "scale", P.settle + 1.0, (0, 0, 0), "linear")
        rig.sample(A, "eclipse", "rotation", h0, P.settle + 1.0, lambda t: (0, 8 * t, 0), rate=1, interp="linear")
        rig.hide_until(A, "night_plasma", h0 + 1.0)
        rig.key(A, "night_plasma", "position", h0 + 1.0, (0, SIGIL_Y, 0), "linear")
        rig.key(A, "night_plasma", "scale", h0 + 1.1, (1.4, 1.4, 1.4))
        rig.key(A, "night_plasma", "position", P.burst, HOVER)
        rig.key(A, "night_plasma", "scale", P.burst - 0.05, (1, 1, 1), "linear")
        rig.key(A, "night_plasma", "scale", P.burst + 0.1, (2.5, 2.5, 2.5), "linear")
        rig.key(A, "night_plasma", "scale", P.burst + 0.45, (0, 0, 0), "linear")
        rig.sample(A, "night_plasma", "rotation", h0 + 1.0, P.burst + 0.45, lambda t: (300 * t, 500 * t, 0), rate=2, interp="linear")
        for b in ("light_veil", "light_prism"):
            rig.key(A, b, "scale", 0, (0, 0, 0), "step")
    else:
        rig.hide_until(A, "light_prism", h0)
        rig.key(A, "light_prism", "scale", h0 + 0.8, (1, 1, 1))
        rig.key(A, "light_prism", "scale", P.burst, (1.2, 1.2, 1.2))
        rig.key(A, "light_prism", "scale", P.burst + 0.4, (0, 0, 0), "linear")
        rig.sample(A, "light_prism", "rotation", h0, P.burst + 0.4, lambda t: (0, 60 * t, 0), rate=1, interp="linear")
        rig.hide_until(A, "light_veil", h0 + 0.3)
        rig.key(A, "light_veil", "scale", h0 + 1.2, (1, 1, 1), "linear")
        rig.key(A, "light_veil", "scale", P.reveal + 0.6, (1, 1, 1), "linear")
        rig.key(A, "light_veil", "scale", P.reveal + 1.6, (1.6, 1.4, 1.6), "linear")
        rig.key(A, "light_veil", "scale", P.reveal + 1.61, (0, 0, 0), "step")
        rig.sample(A, "light_veil", "rotation", h0 + 0.3, P.reveal + 1.6, lambda t: (0, -20 * t, 0), rate=1, interp="linear")
        for b in ("eclipse", "night_plasma"):
            rig.key(A, b, "scale", 0, (0, 0, 0), "step")


def idle(rig, A):
    length = 8.0
    plan = Plan("idle", "b")
    flames(rig, A, plan, length, quiet=True)
    rig.lengths[A] = length
    hide_unused(rig, A)


def animate_all(rig):
    rnd = random.Random(7)
    for tier in TIERS:
        summon(rig, f"summon_{tier}", Plan(f"summon_{tier}", tier), rnd)
    summon(rig, "summon_mythic_night", Plan("summon_mythic_night", "sss", mythic="night"), rnd)
    summon(rig, "summon_mythic_light", Plan("summon_mythic_light", "sss", mythic="light"), rnd)
    pulls = ["b", "b", "b", "b", "b", "b", "a", "s", "ss", "sss"]
    plan = Plan("summon_x10", "sss", pulls=pulls)
    summon(rig, "summon_x10", plan, rnd)
    idle(rig, "idle")
