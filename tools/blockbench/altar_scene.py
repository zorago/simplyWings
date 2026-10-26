import base64
import gzip
import io
import json
import math
import struct
import sys
import uuid
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
WORLD_EXPORT = ROOT / "run/saves/SimplyWings Builds/generated/simplywings/structures/summoning_sanctum.nbt"
RESOURCE = ROOT / "src/main/resources/data/simplywings/structure/summoning_sanctum.nbt"
ASSETS = ROOT / "src/main/resources/assets/simplywings"
VANILLA = ROOT / "build/moddev/artifacts/neoforge-21.1.257-client-extra-aka-minecraft-resources.jar"
OUT = ROOT / "art/gacha/summoning_altar.bbmodel"
FLOOR_RADIUS = 14
ANCHOR = "minecraft:bedrock"
ALTAR = "simplywings:draconic_altar"


def read_tag(f, t):
    if t == 1:
        return struct.unpack(">b", f.read(1))[0]
    if t == 2:
        return struct.unpack(">h", f.read(2))[0]
    if t == 3:
        return struct.unpack(">i", f.read(4))[0]
    if t == 4:
        return struct.unpack(">q", f.read(8))[0]
    if t == 5:
        return struct.unpack(">f", f.read(4))[0]
    if t == 6:
        return struct.unpack(">d", f.read(8))[0]
    if t == 7:
        return f.read(read_tag(f, 3))
    if t == 8:
        return f.read(struct.unpack(">H", f.read(2))[0]).decode()
    if t == 9:
        kind = read_tag(f, 1)
        return [read_tag(f, kind) for _ in range(read_tag(f, 3))]
    if t == 10:
        out = {}
        while True:
            kind = read_tag(f, 1)
            if kind == 0:
                return out
            name = read_tag(f, 8)
            out[name] = read_tag(f, kind)
    if t == 11:
        return [read_tag(f, 3) for _ in range(read_tag(f, 3))]
    if t == 12:
        return [read_tag(f, 4) for _ in range(read_tag(f, 3))]
    raise ValueError(t)


def load_template(path):
    f = io.BytesIO(gzip.decompress(path.read_bytes()))
    read_tag(f, 1)
    read_tag(f, 8)
    root = read_tag(f, 10)
    palette = root["palette"]
    return root["size"], {tuple(b["pos"]): palette[b["state"]] for b in root["blocks"]}


class Textures:
    def __init__(self):
        self.jar = zipfile.ZipFile(VANILLA)
        self.names = set(self.jar.namelist())
        self.index = {}
        self.list = []

    def png(self, ref):
        ns, path = ref.split(":", 1) if ":" in ref else ("minecraft", ref)
        if ns == "minecraft":
            name = f"assets/minecraft/textures/{path}.png"
            return self.jar.read(name) if name in self.names else None
        file = ROOT / f"src/main/resources/assets/{ns}/textures/{path}.png"
        return file.read_bytes() if file.exists() else None

    def get(self, ref):
        if ref not in self.index:
            data = self.png(ref)
            if data is None:
                return None
            self.index[ref] = len(self.list)
            self.list.append((ref, data))
        return self.index[ref]

    def block(self, name, face):
        ns, path = name.split(":", 1)
        base = f"{ns}:block/{path}"
        side = "_top" if face in ("up", "down") else "_side"
        for ref in (base + side, base, base.removesuffix("_bricks") + "_bricks"):
            if self.png(ref) is not None:
                return self.get(ref)
        return None


def new_id():
    return str(uuid.uuid4())


def cube(name, frm, to, faces, origin=None, rotation=None, color=0):
    faces = {f: faces.get(f, {"uv": [0, 0, 0, 0], "texture": None}) for f in ("north", "east", "south", "west", "up", "down")}
    element = {"name": name, "box_uv": False, "render_order": "default", "locked": False, "export": True,
               "from": frm, "to": to, "autouv": 0, "color": color, "origin": origin or [0, 0, 0],
               "faces": faces, "type": "cube", "uuid": new_id()}
    if rotation:
        element["rotation"] = rotation
    return element


def to_bb(x, y, z, center):
    cx, cy, cz = center
    return [(x - cx) * 16 - 8, (y - cy) * 16, (z - cz) * 16 - 8]


SOLID_SKIP = {"minecraft:air"}


def main():
    source = Path(sys.argv[1]) if len(sys.argv) > 1 else (WORLD_EXPORT if WORLD_EXPORT.exists() else RESOURCE)
    size, blocks = load_template(source)
    altar = next(p for p, s in blocks.items() if s["Name"] == ALTAR)
    floor_y = altar[1] - 2
    center = (altar[0], floor_y, altar[2])
    tex = Textures()
    groups = {"floor": [], "build": [], "anchors": [], "altar": []}
    anchors = []
    solid = {p for p, s in blocks.items() if s["Name"] != ALTAR}
    for pos, state in sorted(blocks.items()):
        x, y, z = pos
        name = state["Name"]
        if name == ALTAR:
            continue
        near = math.hypot(x - altar[0], z - altar[2]) <= FLOOR_RADIUS
        if y < floor_y and not near:
            continue
        if y < floor_y - 1:
            continue
        faces = {}
        for face, (dx, dy, dz) in (("north", (0, 0, -1)), ("south", (0, 0, 1)), ("east", (1, 0, 0)),
                                    ("west", (-1, 0, 0)), ("up", (0, 1, 0)), ("down", (0, -1, 0))):
            if (x + dx, y + dy, z + dz) in solid:
                continue
            t = tex.block(name, face)
            faces[face] = {"uv": [0, 0, 16, 16], "texture": t if t is not None else None}
        if not faces and name != ANCHOR:
            continue
        frm = to_bb(x, y, z, center)
        label = name.split(":", 1)[1]
        if name == ANCHOR:
            anchors.append(pos)
            group = "anchors"
            label = f"anchor_{len(anchors)}"
        elif y == floor_y - 1:
            group = "floor"
        else:
            group = "build"
        groups[group].append(cube(label, frm, [frm[0] + 16, frm[1] + 16, frm[2] + 16], faces,
                                  color=1 if group == "anchors" else 0))
    model = json.loads((ASSETS / "models/block/draconic_altar.json").read_text(encoding="utf-8"))
    refs = {k: v for k, v in model["textures"].items()}
    base = to_bb(*altar, center)
    for i, element in enumerate(model["elements"]):
        frm = [base[k] + element["from"][k] for k in range(3)]
        to = [base[k] + element["to"][k] for k in range(3)]
        faces = {}
        for face, data in element.get("faces", {}).items():
            ref = refs.get(data["texture"].lstrip("#"), data["texture"])
            t = tex.get(ref)
            entry = {"uv": data.get("uv", [0, 0, 16, 16]), "texture": t}
            if "rotation" in data:
                entry["rotation"] = data["rotation"]
            faces[face] = entry
        rotation = origin = None
        if "rotation" in element:
            r = element["rotation"]
            origin = [base[k] + r["origin"][k] for k in range(3)]
            rotation = [0, 0, 0]
            rotation["xyz".index(r["axis"])] = r["angle"]
        groups["altar"].append(cube(f"altar_{i}", frm, to, faces, origin, rotation, color=4))
    elements, outliner = [], []
    for gname, items in groups.items():
        if not items:
            continue
        elements.extend(items)
        outliner.append({"name": gname, "origin": [0, 0, 0], "color": 0, "uuid": new_id(), "export": True,
                         "isOpen": gname == "anchors", "locked": gname == "floor", "visibility": True,
                         "autouv": 0, "children": [e["uuid"] for e in items]})
    textures = []
    for i, (ref, data) in enumerate(tex.list):
        textures.append({"name": ref.split("/")[-1] + ".png", "folder": "", "namespace": "", "id": str(i),
                         "width": 16, "height": 16, "uv_width": 16, "uv_height": 16, "particle": False,
                         "render_mode": "default", "render_sides": "auto", "visible": True, "internal": True,
                         "saved": True, "uuid": new_id(),
                         "source": "data:image/png;base64," + base64.b64encode(data).decode()})
    bb = {"meta": {"format_version": "4.10", "model_format": "free", "box_uv": False},
          "name": "summoning_altar", "model_identifier": "", "visible_box": [1, 1, 0],
          "variable_placeholders": "", "variable_placeholder_buttons": [], "timeline_setups": [],
          "unhandled_root_fields": {}, "resolution": {"width": 16, "height": 16},
          "elements": elements, "outliner": outliner, "textures": textures, "animations": []}
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(bb), encoding="utf-8")
    print(f"{source.name}: altar at {altar} (template), floor y {floor_y}")
    for i, (x, y, z) in enumerate(anchors, 1):
        dx, dz = x - altar[0], z - altar[2]
        print(f"anchor_{i}: template {x} {y} {z}, from the altar dx {dx:+d} dy {y - altar[1]:+d} dz {dz:+d}, "
              f"{math.hypot(dx, dz):.2f} blocks, angle {math.degrees(math.atan2(dx, -dz)) % 360:.0f} deg from north")
    print(f"{len(elements)} cubes, {len(textures)} textures -> {OUT.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
