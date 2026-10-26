import gzip
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / "src/main/resources/assets/simplywings/gacha/altar_show.json.gz"
EXCLUDED_ROOTS = {"floor", "build", "anchors", "altar", "player", "particles"}
EXCLUDED_ANIMS = {"summon_x10_particles"}
LIT_ROOTS = {"flyers", "clouds", "offerings"}
FACES = ("north", "east", "south", "west", "up", "down")
INTERP = {"linear": 0, "catmullrom": 1, "step": 2, "bezier": 1}


def r(v):
    return round(float(v), 3)


def export(rig, textures, animations, meta):
    used = {}
    tex_out = []

    def tex_index(i):
        if i not in used:
            e = textures.entries[i]
            frames = e["height"] // e["width"] if "frame_time" in e else 1
            tex_out.append({"path": e["path"], "uw": e["uv_width"], "uh": e["uv_height"] if frames == 1 else e["uv_width"], "frames": frames,
                            "frameTime": e.get("frame_time", 0)})
            used[i] = len(tex_out) - 1
        return used[i]

    bones = []
    index = {}

    def walk(g, parent, lit):
        idx = len(bones)
        index[g["name"]] = idx
        cubes = []
        for child in g["children"]:
            if isinstance(child, dict):
                continue
            e = rig.by_uuid[child]
            inflate = e.get("inflate", 0) or 0
            frm = [e["from"][k] - inflate for k in range(3)]
            to = [e["to"][k] + inflate for k in range(3)]
            faces = []
            for f in FACES:
                face = e.get("faces", {}).get(f)
                if not face or face.get("texture") is None:
                    faces.append(None)
                    continue
                uv = face.get("uv", [0, 0, 0, 0])
                faces.append([r(uv[0]), r(uv[1]), r(uv[2]), r(uv[3]), tex_index(face["texture"]), int(face.get("rotation", 0) or 0)])
            cubes.append({"f": [r(v) for v in frm], "t": [r(v) for v in to], "o": [r(v) for v in e.get("origin", [0, 0, 0])],
                          "r": [r(v) for v in (e.get("rotation") or [0, 0, 0])], "faces": faces})
        bones.append({"name": g["name"], "parent": parent, "pivot": [r(v) for v in g["origin"]], "rot": [r(v) for v in g.get("rotation", [0, 0, 0])],
                      "lit": lit, "cubes": cubes})
        for child in g["children"]:
            if isinstance(child, dict):
                walk(child, idx, lit)

    for g in rig.roots:
        if g["name"] not in EXCLUDED_ROOTS:
            walk(g, -1, g["name"] in LIT_ROOTS)
    anims = {}
    for anim in animations:
        if anim["name"] in EXCLUDED_ANIMS:
            continue
        tracks = []
        for animator in anim["animators"].values():
            b = index.get(animator["name"])
            if b is None:
                continue
            channels = {}
            for kf in animator["keyframes"]:
                dp = kf["data_points"][0]
                channels.setdefault(kf["channel"], []).append([r(kf["time"]), r(dp["x"]), r(dp["y"]), r(dp["z"]), INTERP.get(kf["interpolation"], 0)])
            for ch, keys in channels.items():
                keys.sort(key=lambda k: k[0])
                tracks.append({"bone": b, "ch": {"position": 0, "rotation": 1, "scale": 2}[ch], "keys": [v for k in keys for v in k]})
        anims[anim["name"]] = {"length": anim["length"], "loop": anim["loop"] == "loop", "tracks": tracks, "meta": meta.get(anim["name"], {})}
    data = {"textures": tex_out, "bones": bones, "anims": anims}
    OUT.parent.mkdir(parents=True, exist_ok=True)
    raw = json.dumps(data, separators=(",", ":")).encode("utf-8")
    OUT.write_bytes(gzip.compress(raw, 9))
    return len(bones), len(raw), OUT.stat().st_size
