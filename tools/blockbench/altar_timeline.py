import math
import random

from altar_anim import (B, CRYSTAL, FLAME_SPEC, HOVER, PLAYER, SIGIL_Y, WING_Y, IGNITE, BANDS, Noise1, ease_in_out,
                        ease_out_expo, flame_positions, octa, ring, smooth, voxel_shell)

TIER_COLORS = ["white", "blue", "violet", "gold", "red"]
TIERS = ["b", "a", "s", "ss", "sss"]
PRE = 2.4
POST = 2.4
FLAME_SPEED = {"fire": 1.0, "dark": 0.55, "earth": 0.45, "water": 0.75, "light": 0.8, "air": 1.3}
FLYER_CATEGORIES = {"b": ["common"], "a": ["common"], "s": ["rare", "common"], "ss": ["epic", "rare"], "sss": ["legendary", "epic"]}
CONTAINERS = ("flyers", "clouds", "offerings", "particles", "multi_stars")


class Plan:
    def __init__(self, name, tier, mythic=None, pulls=None, particles=False):
        self.name = name
        self.tier = tier
        self.mythic = mythic
        self.pulls = pulls
        self.particles = particles
        rank = TIERS.index(tier)
        self.rank = rank
        self.sigil = rank >= 2
        self.sigil_h = 64 if rank == 2 else 96
        self.stacks = 1 if rank == 0 else 2 if rank == 1 else 3
        self.pillar_h = 26 if rank < 2 else self.sigil_h
        self.categories = ["mythic", "legendary"] if mythic else FLYER_CATEGORIES[tier]
        self.cracks = rank == 4
        self.wings = [0, 0, 0.6, 0.8, 1.0][rank]
        self.beams = rank >= 2
        self.waves = [1, 2, 2, 3, 3][rank]
        self.birth = 4.6 if rank >= 2 else 4.4
        self.offerings = len(pulls) if pulls else 1
        self.crack_t = None
        if pulls:
            self.spawn = []
            s = 4.7
            for p in pulls:
                self.spawn.append(s)
                s += [0.42, 0.6, 0.95, 1.4, 1.95][TIERS.index(p)]
            self.desc = s + 0.8
            self.hover = self.desc + 3.4
            self.crack_t = self.spawn[pulls.index("sss")] + 1.95 * 0.7 if "sss" in pulls else None
            self.cracks = self.crack_t is not None
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

    def star_path(self, t, offset=0.0):
        t0, t1 = self.desc + offset, self.hover + offset * 0.35
        if t < t0:
            return (0, self.top, 0)
        u = min(1.0, ease_in_out((self.warp(t) - self.warp(t0)) / max(0.01, self.warp(t1) - self.warp(t0))))
        a = u * (1.2 + 0.3 * self.rank) * 2 * math.pi + offset * 9
        r = math.sin(math.pi * u) ** 0.8 * (60 + 25 * self.rank)
        y = self.top * (1 - u) + HOVER[1] * u
        return (r * math.cos(a), y, r * math.sin(a))


def build_extras(rig, pulls):
    glow = rig.textures.by_ref["simplywings:gacha/glow"]
    cam = rig.bone("camera", (0, 0, 0))
    rig.box(cam, (0, 0, 0), (6, 5, 8), "black")
    rig.box(cam, (0, 0, 5), (3.5, 3.5, 3), "rock")
    rig.box(cam, (0, 0, 6.8), (2.6, 2.6, 0.6), "s_blue")
    tgt = rig.bone("camera_target", (0, 0, 0))
    rig.box(tgt, (0, 0, 0), (2, 2, 2), "s_red", rot=[45, 45, 0])
    rig.bone("multi_stars", (0, 0, 0))
    for i, p in enumerate(pulls):
        b = rig.bone(f"multi_star_{i}", (0, 0, 0), "multi_stars")
        c = TIER_COLORS[TIERS.index(p)]
        octa(rig, b, (0, 0, 0), 13, 3.5, f"s_{c}")
        rig.box(b, (0, 0, 0), (2, 60, 2), "s_white")
        rig.box(b, (0, 0, 0), (60, 2, 2), "s_white")
        rig.crossed(b, (0, 0, 0), 64, 64, glow, count=2, uv=(32, 32))
    ec = rig.bone("eclipse", (0, SIGIL_Y + 60, 0))
    for r in range(16, 520, 22):
        ring(rig, ec, (0, SIGIL_Y + 60, 0), r, max(10, int(r / 6)), "eclipse", thick=24, height=4)
    ring(rig, ec, (0, SIGIL_Y + 58, 0), 540, 160, "violet", thick=8, height=4, glow="energy")
    pl = rig.bone("night_plasma", (0, 0, 0))
    voxel_shell(rig, pl, (0, 0, 0), lambda x, y, z: math.sqrt(x * x + y * y + z * z) <= 30, 30, 5,
                lambda x, y, z: "s_white" if y > 14 else "s_blue" if (x + z) % 10 < 5 else "s_violet")
    rig.crossed(pl, (0, 0, 0), 150, 150, glow, count=3, uv=(32, 32))
    for i in range(12):
        rig.box(pl, (0, 0, 0), (2, 2, 110), "s_violet" if i % 2 else "s_blue", rot=[(i * 41) % 180 - 90, (i * 67) % 360, 0])
    lv = rig.bone("light_veil", (0, 0, 0))
    for k in range(36):
        a = 2 * math.pi * k / 36
        for j in range(5):
            rig.box(lv, ((380 - j * 40) * math.cos(a), 40 + j * 110, (380 - j * 40) * math.sin(a)), (66, 100, 2), "feather", rot=[0, -math.degrees(a) + 90, 0])
    lp = rig.bone("light_prism", (0, SIGIL_Y - 200, 0))
    octa(rig, lp, (0, SIGIL_Y - 200, 0), 72, 8, "white", core="gold")
    rig.crossed(lp, (0, SIGIL_Y - 200, 0), 320, 320, glow, count=3, uv=(32, 32))
    for i in range(8):
        rig.box(lp, (0, SIGIL_Y - 200, 0), (3, 3, 600), "ray", rot=[0, i * 22.5, 0])


def hide_unused(rig, A):
    used = set(rig.keys.get(A, {}))

    def touched(g):
        if g["name"] in used:
            return True
        return any(isinstance(c, dict) and touched(c) for c in g["children"])

    for g in rig.roots:
        if not touched(g):
            rig.key(A, g["name"], "scale", 0, (0, 0, 0), "step", absolute=True)
        elif g["name"] in CONTAINERS:
            for c in g["children"]:
                if isinstance(c, dict) and not touched(c):
                    rig.key(A, c["name"], "scale", 0, (0, 0, 0), "step", absolute=True)


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


def lerp3(a, b, u):
    return tuple(a[k] + (b[k] - a[k]) * u for k in range(3))


REF = [0, 1.9, 4.0, 4.6, 6.2, 10.4, 12.0, 21.0]
CAM = {
    "R": [(0, 30), (1.3, 24), (1.9, 21), (3.6, 16), (4.0, 19), (5.2, 24), (6.2, 26), (8.0, 32), (9.6, 28), (10.4, 19), (11.6, 15), (12.0, 13),
          (12.6, 22), (14.0, 30), (17.0, 33), (21.0, 30)],
    "H": [(0, 13), (1.3, 9), (1.9, 6), (3.6, 3), (4.0, 2.5), (5.2, 4), (6.2, 6), (8.0, 14), (9.6, 12), (10.4, 8), (12.0, 5), (12.6, 7),
          (14.0, 11), (21.0, 9)],
    "TY": [(0, 3), (1.9, 3.5), (3.6, 4), (4.0, 14), (5.2, 40), (6.2, 50), (8.0, 34), (9.6, 18), (10.4, 7), (12.0, 6.5), (12.6, 13),
           (14.0, 17), (21.0, 6)],
    "WS": [(0, 0), (4.6, 0), (5.4, 0.55), (6.2, 0.6), (8.0, 0.45), (9.6, 0.3), (10.4, 0.0), (21.0, 0)],
    "OM": [(0, 0.45), (1.2, 0.6), (1.9, 0.75), (3.6, 1.0), (4.0, 0.6), (5.2, 0.35), (6.2, 0.5), (7.5, 1.1), (9.0, 1.6), (10.4, 1.9),
           (11.6, 0.9), (12.0, 0.25), (12.6, 0.4), (14.0, 0.3), (21.0, 0.2)],
}
SHOULDER = (PLAYER[0] + 18, PLAYER[1] + 40, PLAYER[2] + 36)


def offering_pos(P, i, T):
    n = P.offerings
    hand = (PLAYER[0] - 6, PLAYER[1] + 30, PLAYER[2] - 8)
    a = 2 * math.pi * i / n
    crown = (PLAYER[0] + (16 * math.cos(a) if n > 1 else 0), PLAYER[1] + 56, PLAYER[2] - 14 + (16 * math.sin(a) if n > 1 else 0))
    t0 = 0.3 + 0.05 * i
    t1 = 1.05 + 0.03 * i
    t2 = 1.8 + 0.06 * i
    if T < t0:
        return hand
    if T < t1:
        u = ease_in_out((T - t0) / (t1 - t0))
        return (hand[0] + (crown[0] - hand[0]) * u, hand[1] + (crown[1] - hand[1]) * u + 10 * math.sin(math.pi * u), hand[2] + (crown[2] - hand[2]) * u)
    u = ease_in_out(min(1.0, (T - t1) / (t2 - t1)))
    p = lerp3(crown, CRYSTAL, u)
    return (p[0], p[1] + 34 * math.sin(math.pi * u), p[2])


def camera_keys(rig, A, P, star_pos):
    anchors = [0, 1.9, 4.0, P.birth, P.desc, P.hover, P.burst, P.end]
    scale = 1.0 if P.sigil else 0.55

    def orbit(t):
        tr = remap(t, anchors, REF)
        th = 0.6 + integral(CAM["OM"], tr)
        r = curve(CAM["R"], tr) * B * (0.8 + 0.2 * scale + (0.15 if P.wings == 0 and tr > 12 else 0))
        h = curve(CAM["H"], tr) * B * (0.6 + 0.4 * scale)
        ty = curve(CAM["TY"], tr) * B * (scale if 4.0 < tr < 10.4 else 1)
        ws = curve(CAM["WS"], tr)
        pos = (r * math.cos(th), h, r * math.sin(th))
        sp = star_pos(t)
        return pos, (sp[0] * ws, ty * (1 - ws) + sp[1] * ws, sp[2] * ws)

    total = PRE + P.end + POST
    near = (SHOULDER[0] - 6, SHOULDER[1] + 6, SHOULDER[2] + 10)

    def look(T):
        fpos = [offering_pos(P, i, T) for i in range(P.offerings)]
        fmean = tuple(sum(p[k] for p in fpos) / len(fpos) for k in range(3))
        return lerp3(CRYSTAL, fmean, 0.7 * math.sin(math.pi * min(1, T / 1.9)))

    def cam(T):
        if T < 1.7:
            return lerp3(SHOULDER, near, smooth((T - 0.2) / 1.4)), look(T)
        if T < PRE:
            u = ease_in_out((T - 1.7) / (PRE - 1.7))
            p1, t1 = orbit(0)
            return lerp3(near, p1, u), lerp3(look(T), t1, u)
        if T <= PRE + P.end:
            return orbit(T - PRE)
        u = ease_in_out((T - PRE - P.end) / POST)
        p0, t0 = orbit(P.end)
        return lerp3(p0, SHOULDER, u), lerp3(t0, CRYSTAL, u)

    last = [None]

    def rot(T):
        pos, tgt = cam(T)
        dx, dy, dz = tgt[0] - pos[0], tgt[1] - pos[1], tgt[2] - pos[2]
        yaw = math.degrees(math.atan2(dx, dz))
        pitch = -math.degrees(math.atan2(dy, math.hypot(dx, dz)))
        if last[0] is not None:
            while yaw - last[0] > 180:
                yaw -= 360
            while yaw - last[0] < -180:
                yaw += 360
        last[0] = yaw
        return (pitch, yaw, 0)

    extra = [PRE + b[0] for b in P.beats] + [PRE + P.burst, PRE, 1.7, PRE + P.end]
    rig.sample(A, "camera", "position", 0, total, lambda T: cam(T)[0], rate=8, extra=extra, absolute=True)
    rig.sample(A, "camera", "rotation", 0, total, rot, rate=8, extra=extra, absolute=True)
    rig.sample(A, "camera_target", "position", 0, total, lambda T: cam(T)[1], rate=8, extra=extra, absolute=True)


def prologue(rig, A, P):
    for i in range(P.offerings):
        b = f"offering_{i}"
        rig.key(A, b, "scale", 0, (0, 0, 0), "linear", absolute=True)
        rig.key(A, b, "scale", 0.25 + 0.05 * i, (0, 0, 0), "linear", absolute=True)
        rig.key(A, b, "scale", 0.45 + 0.05 * i, (1.3, 1.3, 1.3), absolute=True)
        rig.key(A, b, "scale", 0.6 + 0.05 * i, (1, 1, 1), absolute=True)
        t_end = 1.8 + 0.06 * i
        rig.key(A, b, "scale", t_end - 0.05, (1, 1, 1), "linear", absolute=True)
        rig.key(A, b, "scale", t_end + 0.12, (0, 0, 0), "linear", absolute=True)
        rig.sample(A, b, "position", 0.2, t_end + 0.12, lambda T, i=i: offering_pos(P, i, T), rate=14, absolute=True)
        rig.sample(A, b, "rotation", 0.2, t_end + 0.12, lambda T: (20 * math.sin(T * 5), 360 * T, 0), rate=8, absolute=True)
    rig.key(A, "player_arm_r", "rotation", 0, (0, 0, 0), absolute=True)
    rig.key(A, "player_arm_r", "rotation", 0.3, (95, 0, -8), absolute=True)
    rig.key(A, "player_arm_r", "rotation", 0.45, (85, 0, -6), absolute=True)
    rig.key(A, "player_arm_r", "rotation", 1.9, (80, 0, -6), absolute=True)
    rig.key(A, "player_arm_r", "rotation", 2.4, (0, 0, 0), absolute=True)


def flame_body_scale(P, name, quiet):
    ig = IGNITE[name]

    def fs(t):
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
        if t < P.hover:
            s = 1.2
            for k, (bt, _, _) in enumerate(P.beats):
                if bt - 0.1 <= t < bt + 0.6:
                    s += (0.35 + 0.15 * k) * math.sin(math.pi * (t - bt + 0.1) / 0.7) ** 1.5
            return s
        if t < P.burst:
            if P.mythic:
                keep = (P.mythic == "night" and name == "dark") or (P.mythic == "light" and name == "light")
                return 1.2 + 2.2 * smooth((t - P.hover) / 0.8) if keep else 1.2 * (1 - smooth((t - P.hover) / 0.6))
            return 1.2 - 0.95 * ease_in_out((t - P.hover) / max(0.2, P.burst - P.hover - 0.1))
        if t < P.reveal:
            return 0.25
        big = 1.0 + (1.0 if P.rank >= 2 else 0.4)
        if t < P.reveal + 0.6:
            return 0.25 + (big - 0.25) * ease_out_expo((t - P.reveal) / 0.6)
        if t < P.settle:
            return big - 0.4 * (big - 1) * ease_in_out((t - P.reveal - 0.6) / max(0.1, P.settle - P.reveal - 0.6))
        return big - 0.4 * (big - 1) - 0.6 * (big - 1) * ease_in_out((t - P.settle) / max(0.1, P.end - P.settle))

    return fs


def looped(fn, period):
    if not period:
        return fn

    def out(t):
        a, b = fn(t), fn(t - period)
        w = t / period
        return tuple(a[k] * (1 - w) + b[k] * w for k in range(3))
    return out


def flames(rig, A, P, end, quiet=False):
    L = end if quiet else None
    for name, x, z, yaw in flame_positions():
        sp = FLAME_SPEED[name] * (0.5 if quiet else 1.0)
        H = FLAME_SPEC[name]["H"]
        body = f"flame_{name}_body"
        rig.groups[f"flame_{name}"]["rotation"] = [0, round(yaw, 2), 0]
        n = Noise1(hash(name) & 0xFFFF)
        n2 = Noise1((hash(name) >> 8) & 0xFFFF)
        fs = flame_body_scale(P, name, quiet)
        rig.sample(A, body, "scale", 0, end, looped(lambda t, fs=fs, n=n, sp=sp: (fs(t) * (1 - 0.03 * n(t * sp + 3)), fs(t) * (1 + 0.06 * n(t * 1.2 * sp)), fs(t) * (1 - 0.03 * n(t * sp + 3))), L),
                   rate=8, extra=[IGNITE[name], IGNITE[name] + 0.08, IGNITE[name] + 0.3, P.burst, P.reveal])

        def lean(t, n2=n2):
            v = 0.0
            if not quiet:
                if 1.9 <= t < 3.6:
                    v = 14 * ease_in_out((t - 1.9) / 1.0)
                elif 3.6 <= t < P.hover:
                    v = 14 - 10 * ease_in_out((t - 3.6) / 1.0)
                elif P.hover <= t < P.burst:
                    v = 4 + 36 * ease_in_out((t - P.hover) / max(0.2, P.burst - P.hover - 0.1))
                elif P.burst <= t < P.burst + 0.6:
                    v = 40 - 52 * ease_out_expo((t - P.burst) / 0.6)
                elif P.burst + 0.6 <= t < P.burst + 2.0:
                    v = -12 + 12 * ease_in_out((t - P.burst - 0.6) / 1.4)
            return (-v + 3 * n2(t * 0.5), 0, 2 * n2(t * 0.4 + 5))

        rig.sample(A, body, "rotation", 0, end, looped(lean, L), rate=6, extra=[P.burst, P.burst + 0.6])
        for b in range(len(BANDS)):
            nb = Noise1(400 + b * 13 + len(name))
            amp = 0.4 + 1.1 * b
            rig.sample(A, f"flame_{name}_band_{b}", "position", 0, end,
                       looped(lambda t, nb=nb, amp=amp, sp=sp, b=b: (amp * nb(t * 1.8 * sp), 0.3 * amp * nb(t * 2.3 * sp + 4), amp * nb(t * 1.6 * sp + 9 + b)), L), rate=7)
            rig.sample(A, f"flame_{name}_band_{b}", "scale", 0, end,
                       looped(lambda t, nb=nb, b=b, sp=sp: (1 + 0.05 * (b + 1) * nb(t * 2.4 * sp + 2), 1 + 0.08 * (b + 1) * nb(t * 2.1 * sp + 6), 1 + 0.05 * (b + 1) * nb(t * 2.2 * sp + 8)), L), rate=7)
        for k in range(3):
            tb = f"flame_{name}_tongue_{k}"
            period = (0.9 + 0.25 * k) / max(0.35, sp)
            t = (k * 0.31) % period
            rig.key(A, tb, "scale", 0, (0, 0, 0), "linear")
            drift = (3 * math.cos(k * 2.1), 3 * math.sin(k * 2.1))
            while t < end - 0.05:
                rig.key(A, tb, "position", t, (0, 0, 0), "linear")
                rig.key(A, tb, "scale", t, (0, 0, 0), "linear")
                rig.key(A, tb, "scale", t + period * 0.2, (1, 1.2, 1), "linear")
                rig.key(A, tb, "position", t + period - 0.01, (drift[0], H * 0.5, drift[1]), "linear")
                rig.key(A, tb, "scale", t + period - 0.01, (0.1, 0.4, 0.1), "linear")
                t += period
        spin = (40 if name != "air" else 160) * (1 if name != "water" else -1)
        if L:
            turns = max(1, round(abs(spin) * sp * L / 360)) * (1 if spin > 0 else -1)
            rig.sample(A, f"flame_{name}_sheet", "rotation", 0, end, lambda t, turns=turns: (0, turns * 360 * t / L, 0), rate=1, interp="linear")
        else:
            rig.sample(A, f"flame_{name}_sheet", "rotation", 0, end, lambda t, spin=spin, sp=sp: (0, spin * sp * t, 0), rate=1, interp="linear")
    eyes_on = P.beats[-1][0] if (P.beats and P.rank == 4) else None
    if P.mythic == "night":
        eyes_on = P.hover
    if eyes_on is not None and not quiet:
        rig.key(A, "flame_dark_eyes", "scale", 0, (0, 0, 0), "linear")
        rig.key(A, "flame_dark_eyes", "scale", eyes_on, (0, 0, 0), "linear")
        rig.key(A, "flame_dark_eyes", "scale", eyes_on + 0.15, (1.4, 1.4, 1.4))
        rig.key(A, "flame_dark_eyes", "scale", eyes_on + 0.35, (1, 1, 1))
        rig.key(A, "flame_dark_eyes", "scale", P.burst, (1, 1, 1), "linear")
        rig.key(A, "flame_dark_eyes", "scale", P.burst + 0.15, (0, 0, 0), "linear")
    else:
        rig.key(A, "flame_dark_eyes", "scale", 0, (0, 0, 0), "step", absolute=True)


def flights(rig, A, P, flyers, rnd):
    used = []
    for cat in P.categories:
        used.extend(flyers[cat])
    if P.pulls:
        window = (P.spawn[0], P.desc + 2.4)
    else:
        window = (P.desc - (0.4 if P.sigil else 0.2), P.hover)
    h_range = (16, 62) if P.sigil else (6, 20)
    cloud = 0
    seen = set()
    for wave in range(2):
        span = window[1] - window[0]
        for name, tops in used:
            dur = rnd.uniform(1.3, 1.9)
            start = window[0] + wave * span * 0.45 + rnd.uniform(0, max(0.1, span * 0.5 - dur * 0.5))
            start = min(start, window[1] + 1.0 - dur)
            a0 = rnd.uniform(0, 2 * math.pi)
            sweep = rnd.uniform(3.6, 4.8) * rnd.choice((-1, 1))
            r0 = rnd.uniform(10, 19) * B
            h0 = rnd.uniform(*h_range) * B
            dh = rnd.uniform(-8, 8) * B

            def path(t, a0=a0, sweep=sweep, r0=r0, h0=h0, dh=dh, start=start, dur=dur):
                u = min(1.0, max(0.0, (t - start) / dur))
                e = u * 0.75 + 0.25 * ease_in_out(u)
                a = a0 + sweep * e
                r = r0 * (1 - 0.32 * math.sin(math.pi * e))
                return (r * math.cos(a), h0 + dh * e + 2 * B * math.sin(math.pi * e), r * math.sin(a))

            def heading(t, path=path):
                p0, p1 = path(t - 0.02), path(t + 0.02)
                vx, vy, vz = p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2]
                return math.degrees(math.atan2(-vx, -vz)), math.degrees(math.atan2(vy, math.hypot(vx, vz) + 1e-6))

            if name not in seen:
                rig.key(A, name, "scale", 0, (0, 0, 0), "step", absolute=True)
                seen.add(name)
            rig.key(A, name, "scale", start - 0.01, (0, 0, 0), "linear")
            rig.key(A, name, "scale", start, (0.3, 0.3, 0.3), "linear")
            rig.key(A, name, "scale", start + 0.18, (1, 1, 1), "linear")
            rig.key(A, name, "scale", start + dur - 0.18, (1, 1, 1), "linear")
            rig.key(A, name, "scale", start + dur, (0, 0, 0), "linear")
            rig.sample(A, name, "position", start, start + dur, lambda t, path=path: tuple(v - o for v, o in zip(path(t), (0, 16, 0))), rate=14)
            last = [None]

            def orient(t, heading=heading, last=last):
                yaw, pitch = heading(t)
                if last[0] is not None:
                    while yaw - last[0] > 180:
                        yaw -= 360
                    while yaw - last[0] < -180:
                        yaw += 360
                last[0] = yaw
                return (pitch, yaw, 0)

            rig.sample(A, name, "rotation", start, start + dur, orient, rate=14)
            for top in tops:
                s = 1 if "right" in top else -1 if "left" in top else 0
                if s:
                    ph = rnd.uniform(0, 6)
                    rig.sample(A, top, "rotation", start, start + dur, lambda t, s=s, ph=ph: (0, 0, s * 22 * math.sin(2 * math.pi * 3.2 * t + ph)), rate=20)
            for c, at in ((cloud, start), (cloud + 1, start + dur)):
                cb = f"cloud_{c}"
                rig.key(A, cb, "scale", 0, (0, 0, 0), "step", absolute=True)
                rig.key(A, cb, "position", 0, path(at), "step", absolute=True)
                t0 = at - 0.35
                rig.key(A, cb, "scale", t0, (0, 0, 0), "linear")
                rig.key(A, cb, "scale", t0 + 0.3, (1.15, 1.1, 1.15))
                rig.key(A, cb, "scale", t0 + 0.45, (1, 1, 1))
                rig.key(A, cb, "scale", at + 0.5, (1, 1, 1), "linear")
                rig.key(A, cb, "scale", at + 1.1, (1.4, 0, 1.4), "linear")
                rig.sample(A, cb, "rotation", t0, at + 1.1, lambda t: (0, 20 * t, 0), rate=1, interp="linear")
            cloud += 2


def particles(rig, A, P, rnd):
    for name, x, z, _ in flame_positions():
        sp = FLAME_SPEED[name]
        for i in range(6):
            b = f"pt_ember_{name}_{i}"
            period = rnd.uniform(1.0, 1.6) / max(0.4, sp)
            t = rnd.uniform(0, period)
            rig.key(A, b, "scale", 0, (0, 0, 0), "linear")
            dx, dz = rnd.uniform(-8, 8), rnd.uniform(-8, 8)
            while t < P.end:
                rig.key(A, b, "position", t, (0, 0, 0), "linear")
                rig.key(A, b, "scale", t + 0.01, (1, 1, 1), "linear")
                rig.key(A, b, "position", t + period - 0.01, (dx, FLAME_SPEC[name]["H"] * 0.9, dz), "linear")
                rig.key(A, b, "scale", t + period - 0.01, (0.3, 0.3, 0.3), "linear")
                rig.key(A, b, "scale", t + period, (0, 0, 0), "linear")
                t += period
    for i in range(30):
        b = f"pt_mote_{i}"
        period = rnd.uniform(1.6, 3.0)
        t = 4.05 + rnd.uniform(0, period)
        top = rnd.uniform(0.3, 1.0) * P.pillar_h * B
        rig.hide_until(A, b, t)
        while t + period < P.settle:
            rig.key(A, b, "position", t, (0, 0, 0), "linear")
            rig.key(A, b, "scale", t + 0.01, (1, 1, 1), "linear")
            rig.key(A, b, "position", t + period - 0.01, (0, top, 0), "linear")
            rig.key(A, b, "scale", t + period, (0, 0, 0), "linear")
            t += period
    for i in range(32):
        b = f"pt_shard_{i}"
        a = rnd.uniform(0, 2 * math.pi)
        el = rnd.uniform(-0.2, 1.1)
        spd = rnd.uniform(160, 420)
        v = (spd * math.cos(a) * math.cos(el), spd * math.sin(el), spd * math.sin(a) * math.cos(el))
        t0 = P.burst + 0.06
        rig.hide_until(A, b, t0 - 0.02)
        life = rnd.uniform(1.2, 2.0)
        rig.key(A, b, "scale", t0, (1, 1, 1), "linear")
        rig.key(A, b, "scale", t0 + life, (0, 0, 0), "linear")
        for j in range(6):
            tt = life * j / 5
            drag = (1 - math.exp(-2.2 * tt)) / 2.2
            rig.key(A, b, "position", t0 + tt, (v[0] * drag, v[1] * drag - 120 * tt * tt, v[2] * drag))
    if P.wings:
        for i in range(24):
            b = f"pt_feather_{i}"
            t0 = P.reveal + 1.2 + (i % 12) * 0.08 + (1.0 if i >= 12 else 0)
            x0, y0, z0 = rnd.uniform(-300, 300) * P.wings, WING_Y + rnd.uniform(-30, 60), rnd.uniform(-30, 30)
            fall = rnd.uniform(3.5, 4.8)
            ph = rnd.uniform(0, 6.28)
            rig.hide_until(A, b, t0)
            rig.key(A, b, "scale", t0 + 0.1, (1, 1, 1), "linear")
            rig.key(A, b, "scale", t0 + fall, (0, 0, 0), "linear")
            rig.sample(A, b, "position", t0, t0 + fall, lambda t, t0=t0, x0=x0, y0=y0, z0=z0, fall=fall, ph=ph:
                       (x0 + 22 * math.sin((t - t0) * 2.0 + ph), y0 - (y0 - 6) * ((t - t0) / fall) ** 1.15, z0 + 14 * math.cos((t - t0) * 1.7 + ph)), rate=4)
    bursts = [(bt, P.star_path(bt)) for bt, _, _ in P.beats]
    if P.pulls:
        bursts = [(P.spawn[i] + 0.3, (0, P.top - 40, 0)) for i, p in enumerate(P.pulls) if TIERS.index(p) >= 2][:4]
    for k in range(min(4, len(bursts))):
        bt, pos = bursts[k]
        for i in range(8):
            b = f"pt_spark_{k}_{i}"
            a = 2 * math.pi * i / 8 + k
            d = (math.cos(a) * 90, rnd.uniform(-30, 40), math.sin(a) * 90)
            rig.hide_until(A, b, bt)
            rig.key(A, b, "position", bt, pos, "linear")
            rig.key(A, b, "scale", bt + 0.01, (1.5, 1.5, 1.5), "linear")
            rig.key(A, b, "position", bt + 0.6, (pos[0] + d[0], pos[1] + d[1], pos[2] + d[2]))
            rig.key(A, b, "scale", bt + 0.6, (0, 0, 0), "linear")


def summon(rig, A, P, rnd, flyers):
    rig.offsets[A] = PRE
    prologue(rig, A, P)
    flames(rig, A, P, P.end + POST)
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
        for i in range(20):
            c = f"stream_{name}_{i}"
            on = 2.2 + IGNITE[name] * 0.3 + i * 0.03
            rig.hide_until(A, c, on)
            rig.key(A, c, "scale", on + 0.1, (1.5, 1.5, 1.5))
            rig.key(A, c, "scale", on + 0.22, (1, 1, 1))
            rig.sample(A, c, "scale", on + 0.3, P.burst, lambda t, i=i: tuple([1 + 0.35 * math.sin(t * 8 - i * 0.6) + (0.6 * smooth((t - P.hover) / 1.2) if t > P.hover else 0)] * 3), rate=4)
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
    rig.hide_until(A, "pillar_wave", 4.0)
    rig.key(A, "pillar_wave", "scale", 4.02, (0.5, 1, 0.5), "linear")
    rig.key(A, "pillar_wave", "scale", 4.7, (9, 0.5, 9), "linear")
    rig.key(A, "pillar_wave", "scale", 4.9, (10, 0, 10), "linear")

    stack_h = [10, 24, 44]
    for k, (open_t, spin) in enumerate(((4.10, 40), (4.22, -55), (4.34, 70))):
        b = f"stack_{k + 1}"
        if k >= P.stacks:
            continue
        if not P.sigil and stack_h[k] > P.pillar_h - 4:
            rig.key(A, b, "position", 0, (0, (P.pillar_h - 6) * B - stack_h[k] * B, 0), "step", absolute=True)
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
            rig.key(A, S, "position", 0, (0, (P.sigil_h - 96) * B, 0), "step", absolute=True)
        rig.hide_until(A, S, 4.46)
        rig.key(A, S, "scale", 4.75, (1.08, 1, 1.08))
        rig.key(A, S, "scale", 5.1, (1, 1, 1))
        rig.key(A, S, "scale", P.burst - 0.1, (1, 1, 1))
        rig.key(A, S, "scale", P.burst, (0.92, 1, 0.92))
        rig.key(A, S, "scale", P.burst + 0.25, (1.5, 1, 1.5), "linear")
        rig.key(A, S, "scale", P.burst + 1.4, (2.2, 0, 2.2), "linear")
        for part, spin in (("outer", 12), ("mid", -20), ("inner", 30), ("hex", -8), ("branches", 12), ("teeth", 12)):
            rig.sample(A, f"{S}_{part}", "rotation", 4.46, P.burst + 1.5, lambda t, s=spin: (0, s * P.warp(t) + (6 * s * smooth((t - P.burst) / 1.2) if t > P.burst else 0), 0), rate=2, interp="linear", extra=hs)
        rig.key(A, f"{S}_hex", "scale", 0, (0, 1, 0), "linear", absolute=True)
        rig.key(A, f"{S}_hex", "scale", 4.8, (0, 1, 0), "linear")
        rig.key(A, f"{S}_hex", "scale", 5.2, (1, 1, 1))
        rig.key(A, f"{S}_branches", "position", P.burst, (0, 0, 0))
        rig.key(A, f"{S}_branches", "position", P.burst + 1.4, (0, -260, 0))
        for i, (bt, _, _) in enumerate(P.beats):
            rig.key(A, f"{S}_mid", "scale", bt - 0.05, (1, 1, 1))
            rig.key(A, f"{S}_mid", "scale", bt + 0.04, (1.05 + 0.02 * i, 1, 1.05 + 0.02 * i))
            rig.key(A, f"{S}_mid", "scale", bt + 0.4, (1, 1, 1))

    rig.key(A, "birth_flash", "position", 0, (0, P.top - SIGIL_Y, 0), "step", absolute=True)
    t_flash = P.birth + (1.45 if P.sigil else 0.7)
    rig.hide_until(A, "birth_flash", t_flash - 0.15)
    rig.key(A, "birth_flash", "scale", t_flash, (3 * (0.5 + 0.12 * P.rank),) * 3, "linear")
    rig.key(A, "birth_flash", "scale", t_flash + 0.35, (0, 0, 0), "linear")

    if P.pulls:
        multi(rig, A, P)
    else:
        star(rig, A, P)

    for k in range(min(4, len(P.beats))):
        b = f"beat_wave_{k}"
        bt = P.beats[k][0]
        rig.hide_until(A, b, bt)
        rig.key(A, b, "position", 0, P.star_path(bt), "step", absolute=True)
        rig.key(A, b, "scale", bt + 0.01, (0.3, 1, 0.3), "linear")
        rig.key(A, b, "scale", bt + 0.55, (5 + 2.5 * k, 0.6, 5 + 2.5 * k), "linear")
        rig.key(A, b, "scale", bt + 0.75, (6 + 3 * k, 0, 6 + 3 * k), "linear")
    if P.cracks:
        for i in range(14):
            b = f"crack_{i}"
            on = (P.beats[-1][0] if P.beats else P.crack_t) + 0.025 * i
            rig.hide_until(A, b, on)
            rig.key(A, b, "scale", on + 0.12, (1.2, 1.2, 1.2))
            rig.key(A, b, "scale", on + 0.3, (1, 1, 1))
            rig.key(A, b, "scale", P.burst, (1, 1, 1), "linear")
            rig.key(A, b, "scale", P.burst + 0.4, (1.6, 0, 1.6), "linear")

    f = 0.45 + 0.14 * P.rank
    rig.hide_until(A, "flash", P.burst)
    rig.key(A, "flash", "scale", P.burst + 0.06, (4 * f,) * 3, "linear")
    rig.key(A, "flash", "scale", P.burst + 0.14, (6.5 * f,) * 3, "linear")
    rig.key(A, "flash", "scale", P.burst + 0.45, (0, 0, 0), "linear")
    for i, (speed, dur) in enumerate(((22, 0.9), (14, 1.3), (8, 1.9))):
        if i >= P.waves:
            continue
        b = f"ground_wave_{i}"
        sp = speed * (0.5 + 0.125 * P.rank)
        t0 = P.burst + 0.05 + 0.05 * i
        rig.hide_until(A, b, t0)
        rig.key(A, b, "scale", t0 + 0.01, (0.5, 1.6, 0.5), "linear")
        rig.key(A, b, "scale", t0 + dur * 0.7, (sp * 0.85, 0.8, sp * 0.85), "linear")
        rig.key(A, b, "scale", t0 + dur, (sp, 0, sp), "linear")

    flights(rig, A, P, flyers, rnd)

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
    if P.wings:
        wings(rig, A, P)
    if P.mythic:
        mythic(rig, A, P)
    if P.particles:
        particles(rig, A, P, random.Random(5))
    camera_keys(rig, A, P, lambda t: star_pos_for_camera(P, t))
    rig.lengths[A] = round(PRE + P.end + POST, 2)
    hide_unused(rig, A)


def star_pos_for_camera(P, t):
    if t < P.desc:
        return (0, P.top, 0)
    if t < P.hover:
        return P.star_path(t)
    return HOVER


def star(rig, A, P):
    on = P.birth + (0.6 if P.sigil else 0.1)
    rig.key(A, "star", "position", 0, (0, P.top, 0), "step", absolute=True)
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
        rig.key(A, b, "scale", 0, (1, 1, 1) if idx == 0 else (0, 0, 0), "step", absolute=True)
        for k, col in enumerate(shown):
            if k:
                rig.key(A, b, "scale", switch[k], (1, 1, 1) if col == idx else (0, 0, 0), "step")
    rig.sample(A, "star_ring", "rotation", on, P.burst, lambda t: (70 + 20 * math.sin(t), 260 * P.warp(t), 20 * math.cos(t * 1.3)), rate=4)
    rig.sample(A, "star_glow", "rotation", on, P.burst, lambda t: (0, 90 * t, 0), rate=2, interp="linear")
    for g in range(8):
        b = f"star_ghost_{g}"
        lag = 0.035 * (g + 1)
        rig.hide_until(A, b, P.desc + lag)
        size = (1.3 - 0.13 * g) * (1 if P.sigil else 0.6)
        rig.key(A, b, "scale", P.desc + lag + 0.1, (size,) * 3)
        rig.key(A, b, "scale", P.hover - 0.1, (size,) * 3, "linear")
        rig.key(A, b, "scale", P.hover + 0.2, (0, 0, 0), "linear")
        rig.sample(A, b, "position", P.desc + lag, P.hover + 0.2, lambda t, lag=lag: P.star_path(t - lag), rate=12)


def multi(rig, A, P):
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

        def fall(t, a0=a0, fall0=fall0, orbit=orbit):
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
        rig.key(A, f"star_{c}", "scale", 0, (1, 1, 1) if idx == best else (0, 0, 0), "step", absolute=True)
    rig.key(A, "star", "position", 0, HOVER, "step", absolute=True)
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


def wings(rig, A, P):
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


def idle(rig, A):
    length = 8.0
    flames(rig, A, Plan("idle", "b"), length, quiet=True)
    rig.key(A, "player", "scale", 0, (1, 1, 1), "step", absolute=True)
    rig.lengths[A] = length
    hide_unused(rig, A)


def animate_all(rig, flyers):
    rnd = random.Random(7)
    for tier in TIERS:
        summon(rig, f"summon_{tier}", Plan(f"summon_{tier}", tier), rnd, flyers)
    summon(rig, "summon_mythic_night", Plan("summon_mythic_night", "sss", mythic="night"), rnd, flyers)
    summon(rig, "summon_mythic_light", Plan("summon_mythic_light", "sss", mythic="light"), rnd, flyers)
    pulls = ["b", "b", "b", "b", "b", "b", "a", "s", "ss", "sss"]
    summon(rig, "summon_x10", Plan("summon_x10", "sss", pulls=pulls), random.Random(11), flyers)
    summon(rig, "summon_x10_particles", Plan("summon_x10_particles", "sss", pulls=pulls, particles=True), random.Random(11), flyers)
    idle(rig, "idle")
