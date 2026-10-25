import base64
import copy
import io
import json
import math
import subprocess
import sys
import uuid
import zipfile
from pathlib import Path

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
RIGS = ROOT / "src" / "main" / "resources" / "assets" / "simplywings" / "rigs"
SCENES = ROOT / "art" / "scenes"
BAKE = ROOT / "build" / "rigbake"
DEG = 180 / math.pi
TOLERANCE = {"rotation": 0.4, "position": 0.02, "scale": 0.005}
PIVOT = [0, 14.4, 0]
SCENE_GROUPS = ("scene_pitch", "scene_roll", "scene_membrane")
PLAYER = "player"

MANNEQUIN = [
    ("head", [0, 24, 0], [-4, 24, -4], [4, 32, 4], [0, 0]),
    ("body", [0, 24, 0], [-4, 12, -2], [4, 24, 2], [16, 16]),
    ("right_arm", [5, 22, 0], [4, 12, -2], [8, 24, 2], [40, 16]),
    ("left_arm", [-5, 22, 0], [-8, 12, -2], [-4, 24, 2], [32, 48]),
    ("right_leg", [1.9, 12, 0], [-0.1, 0, -2], [3.9, 12, 2], [0, 16]),
    ("left_leg", [-1.9, 12, 0], [-3.9, 0, -2], [0.1, 12, 2], [16, 48]),
]


def uid():
    return str(uuid.uuid4())


def bake(wing):
    gradlew = "gradlew.bat" if sys.platform == "win32" else "./gradlew"
    subprocess.run([str(ROOT / gradlew), "--no-configuration-cache", "-q", "bakeRig", f"-Pwing={wing}"],
                   cwd=ROOT, check=True, shell=sys.platform == "win32")
    return json.loads((BAKE / f"{wing}.json").read_text(encoding="utf-8"))


def steve():
    jar = next((ROOT / "build" / "moddev" / "artifacts").glob("*client-extra*.jar"))
    with zipfile.ZipFile(jar) as z:
        return Image.open(io.BytesIO(z.read("assets/minecraft/textures/entity/player/wide/steve.png"))).convert("RGBA")


def decode(texture):
    source = texture.get("source", "")
    if source.startswith("data:"):
        return Image.open(io.BytesIO(base64.b64decode(source.split(",", 1)[1]))).convert("RGBA")
    return Image.open(texture["path"]).convert("RGBA")


def encode(image):
    out = io.BytesIO()
    image.save(out, "PNG")
    return "data:image/png;base64," + base64.b64encode(out.getvalue()).decode()


def box_faces(size, uv, texture):
    w, h, d = size
    u, v = uv
    return {
        "north": {"uv": [u + d, v + d, u + d + w, v + d + h], "texture": texture},
        "east": {"uv": [u, v + d, u + d, v + d + h], "texture": texture},
        "south": {"uv": [u + 2 * d + w, v + d, u + 2 * d + 2 * w, v + d + h], "texture": texture},
        "west": {"uv": [u + d + w, v + d, u + 2 * d + w, v + d + h], "texture": texture},
        "up": {"uv": [u + d + w, v + d, u + d, v], "texture": texture},
        "down": {"uv": [u + d + 2 * w, v, u + d + w, v + d], "texture": texture},
    }


def group(name, origin, export=False):
    return {"uuid": uid(), "export": export, "locked": False, "origin": list(origin), "rotation": [0, 0, 0],
            "color": 0, "name": name, "children": [], "reset": False, "shade": True, "mirror_uv": False,
            "selected": False, "visibility": True, "autouv": 0, "isOpen": False, "primary_selected": False}


def thin(keys, tol):
    if len(keys) <= 2:
        return keys
    out = [keys[0]]
    for i in range(1, len(keys) - 1):
        a, b, c = out[-1], keys[i], keys[i + 1]
        span = c[0] - a[0]
        f = (b[0] - a[0]) / span if span else 0
        if any(abs(a[1][k] + (c[1][k] - a[1][k]) * f - b[1][k]) > tol for k in range(3)):
            out.append(b)
    out.append(keys[-1])
    return out


def channel(frames, value, rest, kind):
    keys = []
    for f in frames:
        v = value(f)
        if v is None:
            continue
        keys.append((f["t"], v))
    if not keys:
        return []
    neutral = rest if kind == "scale" else [0, 0, 0]
    if all(max(abs(v[k] - neutral[k]) for k in range(3)) <= TOLERANCE[kind] for _, v in keys):
        return []
    keys = thin(keys, TOLERANCE[kind])
    return [{"channel": kind, "data_points": [{"x": round(v[0], 4), "y": round(v[1], 4), "z": round(v[2], 4)}],
             "uuid": uid(), "time": round(t, 4), "color": -1, "interpolation": "linear"} for t, v in keys]


def wing_tracks(frames, name, rest, source="bones"):
    r = rest

    def rot(f):
        s = f[source].get(name)
        return None if s is None else [-(s[0] - r[0]) * DEG, -(s[1] - r[1]) * DEG, (s[2] - r[2]) * DEG]

    def pos(f):
        s = f[source].get(name)
        return None if s is None else [-(s[3] - r[3]), -(s[4] - r[4]), s[5] - r[5]]

    def scl(f):
        s = f[source].get(name)
        return None if s is None else [s[6], s[7], s[8]]

    return channel(frames, rot, None, "rotation") + channel(frames, pos, None, "position") \
        + channel(frames, scl, [1, 1, 1], "scale")


def build(wing, baked):
    rig = json.loads((RIGS / f"{wing}.bbmodel").read_text(encoding="utf-8"))
    scene = copy.deepcopy(rig)
    scene["name"] = f"{wing}_scene"
    groups = {g["name"]: g for g in scene.get("groups", [])}
    for g in scene.get("groups", []):
        if g["name"].startswith("ref"):
            g["visibility"] = False
    for e in scene["elements"]:
        if e.get("export") is False and e["name"].startswith("ref"):
            e["visibility"] = False

    texture = 0
    sheet = scene["textures"][0]
    wings = decode(sheet)
    res = scene["resolution"]
    scale = wings.width / res["width"]
    skin = steve().resize((int(64 * scale), int(64 * scale)), Image.NEAREST)
    combined = Image.new("RGBA", (max(wings.width, skin.width), wings.height + skin.height))
    combined.paste(wings, (0, 0))
    combined.paste(skin, (0, wings.height))
    below = res["height"]
    scene["resolution"] = {"width": round(combined.width / scale), "height": round(combined.height / scale)}
    sheet.update({"path": "", "source": encode(combined), "width": combined.width, "height": combined.height,
                  "uv_width": scene["resolution"]["width"], "uv_height": scene["resolution"]["height"]})

    pitch = group("scene_pitch", PIVOT)
    roll = group("scene_roll", PIVOT)
    player = group(PLAYER, [0, 0, 0])
    billow_group = group("scene_membrane", [0, 0, 0])
    scene["groups"] += [pitch, roll, player, billow_group]
    player_node = {"uuid": player["uuid"], "isOpen": False, "children": []}
    parts = {}
    for name, origin, a, b, uv in MANNEQUIN:
        uv = [uv[0], uv[1] + below]
        g = group(f"player_{name}", origin)
        parts[name] = g
        scene["groups"].append(g)
        size = [b[i] - a[i] for i in range(3)]
        cube = {"name": f"player_{name}", "box_uv": True, "render_order": "default", "locked": False,
                "allow_mirror_modeling": True, "from": a, "to": b, "autouv": 0, "color": 7, "origin": origin,
                "uv_offset": uv, "faces": box_faces(size, uv, texture), "type": "cube", "uuid": uid(),
                "export": False}
        scene["elements"].append(cube)
        player_node["children"].append({"uuid": g["uuid"], "isOpen": False, "children": [cube["uuid"]]})
    billow_node = {"uuid": billow_group["uuid"], "isOpen": False, "children": []}
    roll_node = {"uuid": roll["uuid"], "isOpen": True, "children": scene["outliner"] + [player_node, billow_node]}
    scene["outliner"] = [{"uuid": pitch["uuid"], "isOpen": True, "children": [roll_node]}]

    animations = []
    zero = [0, 0, 0, 0, 0, 0, 1, 1, 1]
    for clip in baked["clips"]:
        frames = clip["frames"]
        animators = {}
        for name, rest in baked["rest"].items():
            g = groups.get(name)
            keys = wing_tracks(frames, name, rest)
            if g and keys:
                animators[g["uuid"]] = {"name": name, "type": "bone", "keyframes": keys}
        for name, g in parts.items():
            keys = wing_tracks(frames, name, zero, "player")
            keys = [k for k in keys if k["channel"] == "rotation"]
            if keys:
                animators[g["uuid"]] = {"name": g["name"], "type": "bone", "keyframes": keys}
        body = channel(frames, lambda f: [-f["pitch"], 0, 0], None, "rotation")
        if body:
            animators[pitch["uuid"]] = {"name": "scene_pitch", "type": "bone", "keyframes": body}
        bank = channel(frames, lambda f: [0, f["roll"], 0], None, "rotation")
        if bank:
            animators[roll["uuid"]] = {"name": "scene_roll", "type": "bone", "keyframes": bank}
        membrane = channel(frames, lambda f: [f.get("billow", 0) * 10, 0, 0], None, "position")
        if membrane:
            animators[billow_group["uuid"]] = {"name": "scene_membrane", "type": "bone", "keyframes": membrane}
        animations.append({"uuid": uid(), "name": clip["name"], "loop": clip["loop"], "override": False,
                           "length": round(clip["length"], 4), "snapping": 40, "selected": False,
                           "anim_time_update": "", "blend_weight": "", "start_delay": "", "loop_delay": "",
                           "animators": animators})
    scene["animations"] = animations
    SCENES.mkdir(parents=True, exist_ok=True)
    sidecar = RIGS / f"{wing}.membranes.json"
    if sidecar.exists():
        (SCENES / f"{wing}.membranes.json").write_text(sidecar.read_text(encoding="utf-8"), encoding="utf-8")
    out = SCENES / f"{wing}.bbmodel"
    out.write_text(json.dumps(scene, ensure_ascii=False), encoding="utf-8")
    print(f"{out} : {len(animations)} animations, {out.stat().st_size // 1024} KB")


def find(nodes, target):
    for n in nodes:
        if isinstance(n, dict):
            if n["uuid"] == target:
                return n
            hit = find(n["children"], target)
            if hit:
                return hit
    return None


def pull(wing):
    scene = json.loads((SCENES / f"{wing}.bbmodel").read_text(encoding="utf-8"))
    rig = json.loads((RIGS / f"{wing}.bbmodel").read_text(encoding="utf-8"))
    drop = {g["uuid"] for g in scene["groups"] if g["name"] in SCENE_GROUPS or g["name"] == PLAYER
            or g["name"].startswith("player_")}
    player_cubes = {e["uuid"] for e in scene["elements"] if e["name"].startswith("player_")}
    roll = next(g["uuid"] for g in scene["groups"] if g["name"] == "scene_roll")
    node = find(scene["outliner"], roll)
    children = [c for c in node["children"] if not (isinstance(c, dict) and c["uuid"] in drop)]
    textures = scene["textures"]
    original = rig["textures"][0]
    sheet = textures[0]
    image = decode(sheet)
    scale = image.width / scene["resolution"]["width"]
    wings = image.crop((0, 0, int(rig["resolution"]["width"] * scale), int(rig["resolution"]["height"] * scale)))
    sheet.update({k: original[k] for k in ("path", "width", "height", "uv_width", "uv_height")})
    if wings.tobytes() != decode(original).tobytes():
        wings.save(original["path"])
        sheet["source"] = encode(wings)
        print(f"{original['path']} updated from the scene")
    else:
        sheet["source"] = original["source"]
    groups = [g for g in scene["groups"] if g["uuid"] not in drop]
    for g in groups:
        if g["name"].startswith("ref"):
            g["visibility"] = True
    elements = [e for e in scene["elements"] if e["uuid"] not in player_cubes]
    for e in elements:
        if e["name"].startswith("ref"):
            e.pop("visibility", None)
    rig.update({"elements": elements, "groups": groups, "outliner": children, "textures": textures})
    rig.pop("animations", None)
    (RIGS / f"{wing}.bbmodel").write_text(json.dumps(rig, ensure_ascii=False, separators=(",", ":")), encoding="utf-8")
    print(f"{RIGS / (wing + '.bbmodel')} updated from the scene")


if __name__ == "__main__":
    wing = sys.argv[1]
    if "--pull" in sys.argv:
        pull(wing)
    else:
        build(wing, bake(wing))
