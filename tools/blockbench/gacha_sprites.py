import io
import json
import math
import random
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "src/main/resources/assets/simplywings/textures/gacha"

FLAME_RAMPS = {
    "fire": [(120, 14, 8), (205, 45, 20), (255, 120, 24), (255, 196, 60), (255, 240, 170), (255, 255, 235)],
    "dark": [(10, 4, 18), (34, 10, 56), (72, 22, 118), (130, 52, 210), (188, 112, 255), (232, 200, 255)],
    "earth": [(52, 34, 18), (96, 66, 34), (110, 112, 52), (92, 160, 62), (146, 220, 110), (220, 255, 190)],
    "water": [(8, 30, 96), (20, 72, 182), (44, 128, 226), (92, 178, 246), (170, 222, 255), (240, 252, 255)],
    "light": [(150, 104, 30), (226, 168, 52), (255, 212, 92), (255, 236, 160), (255, 250, 222), (255, 255, 255)],
    "air": [(70, 120, 140), (110, 178, 196), (160, 222, 236), (200, 240, 248), (232, 252, 255), (255, 255, 255)],
}
FLAME_SPEED = {"fire": 1.0, "dark": 0.55, "earth": 0.45, "water": 0.75, "light": 0.8, "air": 1.3}
FRAMES = 8
SIZE = 32


def periodic_noise(x, y, f, seed, speed):
    rnd = random.Random(seed)
    v = 0.0
    for k in range(5):
        fx = rnd.uniform(0.15, 0.45) * (1 + k * 0.6)
        fy = rnd.uniform(0.12, 0.35) * (1 + k * 0.6)
        ph = rnd.uniform(0, 6.28)
        cycles = max(1, round(speed * (1 + k * 0.5)))
        v += math.sin(x * fx + (y + 0) * fy * 0.3 + ph + 2 * math.pi * cycles * f / FRAMES) * math.cos(y * fy + ph * 0.7 + 2 * math.pi * cycles * f / FRAMES) / (1 + k * 0.7)
    return v / 2.2


def flame_sheet(element):
    ramp = FLAME_RAMPS[element]
    speed = FLAME_SPEED[element]
    img = Image.new("RGBA", (SIZE, SIZE * FRAMES), (0, 0, 0, 0))
    for f in range(FRAMES):
        for py in range(SIZE):
            for px in range(SIZE):
                u = (px + 0.5) / SIZE * 2 - 1
                h = 1 - (py + 0.5) / SIZE
                twist = 0.18 * math.sin(h * 5 + 2 * math.pi * f / FRAMES) * (1.6 if element == "air" else 1)
                width = 1.15 * (h ** 0.22 if h > 0 else 0) * (1 - h) ** 1.25 + 0.06 * (1 - h)
                width *= 1 + (0.25 + 0.55 * h) * periodic_noise(px, py, f, hash(element) & 0xFFFF, speed)
                d = abs(u - twist * h) / max(0.02, width)
                if d > 1 or h > 0.98:
                    continue
                inten = (1 - d) * (0.55 + 0.45 * (1 - h)) + 0.25 * periodic_noise(px * 1.7, py * 1.7, f, 99, speed)
                idx = max(0, min(len(ramp) - 1, int(inten * (len(ramp) + 0.6))))
                alpha = 255 if element not in ("water", "air", "light") else 215
                if d > 0.82:
                    alpha = int(alpha * 0.85)
                img.putpixel((px, f * SIZE + py), ramp[idx] + (alpha,))
    return img


def glow_sheet(color=(214, 170, 255)):
    img = Image.new("RGBA", (SIZE, SIZE * FRAMES), (0, 0, 0, 0))
    for f in range(FRAMES):
        pulse = 0.85 + 0.15 * math.sin(2 * math.pi * f / FRAMES)
        for py in range(SIZE):
            for px in range(SIZE):
                dx, dy = (px + 0.5) / SIZE * 2 - 1, (py + 0.5) / SIZE * 2 - 1
                r = math.hypot(dx, dy)
                star = max(0.0, 1 - abs(dx) * 7) * max(0.0, 1 - abs(dy)) + max(0.0, 1 - abs(dy) * 7) * max(0.0, 1 - abs(dx))
                a = max(0.0, 1 - r / pulse) ** 2.2 + 0.6 * star * pulse
                if a < 0.05:
                    continue
                a = min(1.0, a)
                c = tuple(int(color[k] + (255 - color[k]) * a ** 1.5) for k in range(3))
                img.putpixel((px, f * SIZE + py), c + (int(255 * a),))
    return img


def particle_sheet(kind, size=8, frames=4):
    rnd = random.Random(hash(kind) & 0xFFFF)
    img = Image.new("RGBA", (size, size * frames), (0, 0, 0, 0))
    spec = {
        "ember": [(255, 240, 170), (255, 170, 60), (220, 70, 20)],
        "wisp": [(232, 200, 255), (150, 70, 230), (60, 16, 100)],
        "pebble": [(170, 160, 140), (110, 100, 86), (70, 62, 52)],
        "drop": [(240, 252, 255), (110, 190, 250), (30, 90, 200)],
        "spark": [(255, 255, 255), (255, 236, 160), (255, 196, 70)],
        "gust": [(255, 255, 255), (210, 244, 250), (150, 210, 226)],
        "mote": [(255, 255, 255), (226, 200, 255), (170, 110, 255)],
        "shard": [(255, 255, 255), (236, 210, 255), (180, 120, 255)],
        "feather": [(250, 246, 255), (214, 196, 250), (150, 120, 220)],
        "smoke": [(120, 110, 140), (80, 70, 100), (40, 34, 56)],
    }[kind]
    c = size / 2
    for f in range(frames):
        k = 1 - f / frames
        for py in range(size):
            for px in range(size):
                dx, dy = px + 0.5 - c, py + 0.5 - c
                if kind == "feather":
                    inside = abs(dx + dy * 0.2) < 1.2 + (c - abs(dy)) * 0.25 and abs(dy) < c - 0.5
                    shade = 0 if abs(dx) < 0.6 else 1 if dx < 0 else 2
                elif kind in ("drop",):
                    inside = math.hypot(dx, dy * 0.8 + 0.6) < c * 0.7 * (0.6 + 0.4 * k) or (abs(dx) < (dy + c) * 0.25 and dy < 0)
                    shade = 0 if dx < -0.5 and dy < 0 else 1 if dx < 1 else 2
                elif kind in ("spark", "shard", "mote"):
                    r = c * (0.55 + 0.45 * k)
                    inside = abs(dx) + abs(dy) < r or (abs(dx) < 0.6 and abs(dy) < r * 1.3) or (abs(dy) < 0.6 and abs(dx) < r * 1.3)
                    shade = 0 if abs(dx) + abs(dy) < r * 0.4 else 1 if abs(dx) + abs(dy) < r * 0.75 else 2
                elif kind == "gust":
                    inside = abs(dy - math.sin(dx * 0.9 + f) * 1.2) < 0.9 and abs(dx) < c * (0.5 + 0.5 * k)
                    shade = 0 if dx > 0 else 1
                else:
                    r = c * (0.45 + 0.5 * (k if kind != "smoke" else 1 - k * 0.5)) * (1 + 0.15 * rnd.random())
                    inside = math.hypot(dx, dy) < r
                    shade = 0 if math.hypot(dx, dy) < r * 0.45 else 1 if math.hypot(dx, dy) < r * 0.8 else 2
                if inside:
                    a = 255 if kind not in ("smoke", "gust", "wisp") else int(200 * (0.4 + 0.6 * k))
                    img.putpixel((px, f * size + py), spec[shade] + (a,))
    return img


def png(img):
    buf = io.BytesIO()
    img.save(buf, "PNG")
    return buf.getvalue()


def build():
    out = {}
    for element in FLAME_RAMPS:
        out[f"flame_{element}"] = flame_sheet(element)
    out["glow"] = glow_sheet()
    for kind in ("ember", "wisp", "pebble", "drop", "spark", "gust", "mote", "shard", "feather", "smoke"):
        out[f"particle_{kind}"] = particle_sheet(kind)
    OUT.mkdir(parents=True, exist_ok=True)
    for name, img in out.items():
        img.save(OUT / f"{name}.png")
    particle_dir = ROOT / "src/main/resources/assets/simplywings/textures/particle"
    json_dir = ROOT / "src/main/resources/assets/simplywings/particles"
    particle_dir.mkdir(parents=True, exist_ok=True)
    json_dir.mkdir(parents=True, exist_ok=True)
    for name, img in out.items():
        if not name.startswith("particle_") and name != "glow":
            continue
        kind = "flare" if name == "glow" else name[len("particle_"):]
        w = img.width
        frames = img.height // w
        names = []
        for f in range(frames):
            frame = img.crop((0, f * w, w, f * w + w))
            frame.save(particle_dir / f"gacha_{kind}_{f}.png")
            names.append(f"simplywings:gacha_{kind}_{f}")
        (json_dir / f"gacha_{kind}.json").write_text(json.dumps({"textures": names}) + chr(10), encoding="utf-8")
    return {name: png(img) for name, img in out.items()}


if __name__ == "__main__":
    for name in build():
        print(name)
