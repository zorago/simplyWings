import argparse
import glob
import io
import json
import re
import subprocess
import sys
import zipfile
from pathlib import Path

from PIL import Image, ImageDraw

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
UI = HERE / "ui" / "sprites"
MOD_TEX = ROOT / "src/main/resources/assets/simplywings/textures"
GUI = MOD_TEX / "gui/assembly_table"

BG = (35, 38, 46, 255)
LABEL = (64, 64, 64, 255)
MUTED = (100, 100, 100, 255)
WHITE = (255, 255, 255, 255)
KINDS = [("nuage", (221, 242, 255)), ("vapeur", (255, 156, 90)), ("éthérée", (201, 168, 240)), ("intensifiée", (255, 230, 138))]
GAUGE_CAP = 1000


def essences(text):
    out = {k: 0 for k, _ in KINDS}
    for n, k in re.findall(r"(\d+) (nuage|vapeur|éthérée|intensifiée)", text):
        out[k] += int(n)
    return out
EDGE_COLORS = [(244, 196, 48), (93, 202, 255), (255, 120, 160), (140, 230, 120), (200, 150, 255),
               (255, 160, 80), (90, 230, 210), (240, 240, 240)]
RARITY_BAND = {"Rare": "band_rare", "Épique": "band_epic", "Légendaire": "band_legendary", "Mythique": "band_mythic"}
ACTION = {"MOB": ("band_kill", "icon_kill", "Tuer"), "BLOCK": ("band_break", "icon_break", "Casser"),
          "FISH": ("band_fish", "icon_fish", "Pêcher")}
MOB_FACES = {
    "bat": ("bat", (0, 6, 8, 6)), "rabbit": ("rabbit/brown", (37, 4, 5, 5)), "chicken": ("chicken", (3, 3, 4, 6)),
    "silverfish": ("silverfish", (2, 2, 3, 2)), "phantom": ("phantom", (5, 5, 7, 3)), "blaze": ("blaze", (8, 8, 8, 8)),
    "guardian": ("guardian", (16, 16, 12, 12)), "elder guardian": ("guardian_elder", (16, 16, 12, 12)),
    "breeze": ("breeze/breeze", (7, 8, 10, 8)), "creeper": ("creeper/creeper", (8, 8, 8, 8)),
    "enderman": ("enderman/enderman", (8, 8, 8, 8)), "shulker": ("shulker/shulker", (6, 58, 6, 6)),
    "vex": ("illager/vex", (5, 5, 5, 5)), "ender dragon": ("enderdragon/dragon", (128, 46, 16, 16)),
    "cow": ("cow/cow", (6, 6, 8, 8)), "spider": ("spider/spider", (40, 12, 8, 8)), "skeleton": ("skeleton/skeleton", (8, 8, 8, 8)),
    "slime": ("slime/slime", (8, 8, 8, 8)), "sheep": ("sheep/sheep", (8, 8, 6, 6)), "squid": ("squid/squid", (12, 12, 12, 16)),
    "ghast": ("ghast/ghast", (16, 16, 16, 16)),
}
MOB_ITEMS = {"goat": "goat_horn"}
BLOCK_ALIASES = {"#minecraft:small_flowers": "poppy", "#minecraft:base_stone_overworld": "stone",
                 "blocs de cuivre": "oxidized_copper", "wheat": "wheat", "pêche": "fishing_rod"}
FLAT_ICON_BLOCKS = {"glass_pane": "block/glass", "end_rod": "block/end_rod", "lightning_rod": "block/lightning_rod",
                    "torchflower": "block/torchflower", "spore_blossom": "block/spore_blossom"}
ENTITY_ICONS = {"wither_skeleton_skull": ("skeleton/wither_skeleton", (8, 8, 8, 8))}
FOLIAGE = (72, 181, 24)
COND_ICONS = [("nuit", "cond_night"), ("orage", "cond_storm"), ("accroupi", "cond_sneak"), ("sprint", "cond_sprint"),
              ("y ≥", "cond_up"), ("y ≤", "cond_down"), ("nuages", "cond_cloud"), ("village", "cond_village"),
              ("lumière", "cond_dark"), ("pleine", "cond_full"), ("biome", "cond_biome"), ("partout", "cond_chance")]


def find_jar(arg):
    if arg:
        return Path(arg)
    cands = sorted(ROOT.glob("build/moddev/artifacts/neoforge-*-client-extra-aka-minecraft-resources.jar"))
    cands += [Path(p) for p in glob.glob(str(Path.home() / ".gradle/caches/neoformruntime/artifacts/minecraft_*_client.jar"))]
    return cands[0] if cands else None


class Vanilla:
    def __init__(self, jar):
        self.z = zipfile.ZipFile(jar) if jar and Path(jar).exists() else None
        self.cache = {}

    def raw(self, path):
        if not self.z:
            return None
        try:
            return self.z.read("assets/minecraft/" + path)
        except KeyError:
            return None

    def img(self, path):
        if path in self.cache:
            return self.cache[path]
        data = self.raw(path)
        im = None
        if data:
            im = Image.open(io.BytesIO(data)).convert("RGBA")
            if path.startswith("textures/block/") or path.startswith("textures/item/"):
                if im.height > im.width:
                    im = im.crop((0, 0, im.width, im.width))
        self.cache[path] = im
        return im

    def json(self, path):
        data = self.raw(path)
        return json.loads(data) if data else None

    def tex(self, ref):
        ref = ref.split(":")[-1]
        return self.img(f"textures/{ref}.png")

    def model(self, ref):
        parents, textures = [], {}
        while ref:
            ref = ref.split(":")[-1]
            m = self.json(f"models/{ref}.json")
            if m is None:
                break
            parents.append(ref)
            for k, v in m.get("textures", {}).items():
                textures.setdefault(k, v)
            ref = m.get("parent")
        for _ in range(4):
            for k, v in list(textures.items()):
                if isinstance(v, str) and v.startswith("#"):
                    textures[k] = textures.get(v[1:], v)
        return parents, textures


def tint(im, color):
    out = im.copy()
    px = out.load()
    for y in range(out.height):
        for x in range(out.width):
            r, g, b, a = px[x, y]
            px[x, y] = (r * color[0] // 255, g * color[1] // 255, b * color[2] // 255, a)
    return out


def iso(top, front, side):
    out = Image.new("RGBA", (16, 16))
    faces = [(top, (8, 0), (8, 4), (-8, 4), 1.0), (front, (0, 4), (8, 4), (0, 8), 0.8), (side, (8, 8), (8, -4), (0, 8), 0.62)]
    for py in range(16):
        for px in range(16):
            x, y = px + 0.5, py + 0.5
            for im, o, u, v, shade in faces:
                det = u[0] * v[1] - u[1] * v[0]
                dx, dy = x - o[0], y - o[1]
                a = (dx * v[1] - dy * v[0]) / det
                b = (u[0] * dy - u[1] * dx) / det
                if 0 <= a < 1 and 0 <= b < 1:
                    t = im.getpixel((min(15, int(a * im.width)), min(15, int(b * im.height))))
                    if t[3]:
                        out.putpixel((px, py), (int(t[0] * shade), int(t[1] * shade), int(t[2] * shade), 255))
                    break
    return out


class Icons:
    def __init__(self, vanilla, unknown):
        self.v = vanilla
        self.unknown = unknown
        self.cache = {}

    def get(self, rid):
        if rid in self.cache:
            return self.cache[rid]
        ns, _, name = rid.partition(":")
        im = self.mod(name) if ns == "simplywings" else self.vanilla(name)
        self.cache[rid] = im or self.unknown
        return self.cache[rid]

    def mod(self, name):
        p = MOD_TEX / "item" / f"{name}.png"
        if not p.exists():
            return None
        im = Image.open(p).convert("RGBA")
        return im.crop((0, 0, im.width, im.width)).resize((16, 16), Image.NEAREST)

    def fit(self, im, leaves=False):
        if im is None:
            return None
        if leaves:
            im = tint(im, FOLIAGE)
        return im.resize((16, 16), Image.NEAREST) if im.size != (16, 16) else im

    def vanilla(self, name):
        if name in ENTITY_ICONS:
            return self.face(*ENTITY_ICONS[name])
        if name in FLAT_ICON_BLOCKS:
            return self.fit(self.v.tex(FLAT_ICON_BLOCKS[name]))
        leaves = "leaves" in name and "cherry" not in name and "azalea" not in name
        parents, t = self.v.model(f"item/{name}")
        if not parents:
            parents, t = self.v.model(f"block/{name}")
        if not parents:
            return self.fit(self.v.tex(f"block/{name}"), leaves)
        if "layer0" in t:
            base = self.v.tex(t["layer0"])
            if base is None:
                return None
            base = base.copy()
            if "layer1" in t and self.v.tex(t["layer1"]):
                base.alpha_composite(self.v.tex(t["layer1"]))
            return self.fit(base)
        if any(p.endswith(("cross", "tinted_cross")) for p in parents):
            return self.fit(self.v.tex(t.get("cross", "")), leaves)

        def pick(*keys):
            for k in keys:
                if k in t and not str(t[k]).startswith("#"):
                    im = self.v.tex(t[k])
                    if im:
                        return tint(im, FOLIAGE) if leaves else im
            return None

        top = pick("top", "end", "up", "all", "particle")
        front = pick("front", "north", "side", "all", "particle")
        side = pick("side", "east", "west", "all", "particle")
        if not (top and front and side):
            return None
        return iso(top.resize((16, 16), Image.NEAREST), front.resize((16, 16), Image.NEAREST), side.resize((16, 16), Image.NEAREST))

    def face(self, tex, rect):
        im = self.v.img(f"textures/entity/{tex}.png")
        if im is None:
            return None
        x, y, w, h = rect
        crop = im.crop((x, y, x + w, y + h))
        s = max(1, 14 // max(w, h))
        crop = crop.resize((w * s, h * s), Image.NEAREST)
        out = Image.new("RGBA", (16, 16))
        out.alpha_composite(crop, ((16 - crop.width) // 2, (16 - crop.height) // 2))
        return out

    def source(self, src, what):
        first = re.split(r",| / ", what)[0]
        first = re.sub(r"\(.*?\)", "", first).replace("`", "").strip()
        key = first.lower().replace(" mûr", "").strip()
        if src == "FISH":
            return self.get("minecraft:fishing_rod")
        if src == "MOB":
            if key in MOB_ITEMS:
                return self.get("minecraft:" + MOB_ITEMS[key])
            if key in MOB_FACES:
                return self.face(*MOB_FACES[key]) or self.unknown
            return self.unknown
        for k, v in BLOCK_ALIASES.items():
            if key.startswith(k.lower()):
                return self.get("minecraft:" + v)
        return self.get("minecraft:" + key.replace(" ", "_"))


class Font:
    def __init__(self, vanilla):
        self.glyphs = {}
        self.space = 4
        self.load(vanilla, "font/include/default.json")
        self.load(vanilla, "font/include/space.json")

    def load(self, v, path):
        data = v.json(path)
        if not data:
            return
        for prov in data.get("providers", []):
            if prov.get("type") == "space":
                self.space = prov.get("advances", {}).get(" ", 4)
            if prov.get("type") != "bitmap":
                continue
            im = v.tex(prov["file"].replace(".png", ""))
            if im is None:
                continue
            rows = prov["chars"]
            ch_w, ch_h = im.width // len(rows[0]), im.height // len(rows)
            height = prov.get("height", 8)
            ascent = prov.get("ascent", 7)
            if height != ch_h:
                continue
            for r, row in enumerate(rows):
                for c, ch in enumerate(row):
                    if ch == "\u0000" or ch in self.glyphs:
                        continue
                    g = im.crop((c * ch_w, r * ch_h, (c + 1) * ch_w, (r + 1) * ch_h))
                    bbox = g.getchannel("A").getbbox()
                    width = bbox[2] if bbox else 0
                    self.glyphs[ch] = (g, width, ascent)

    def norm(self, s):
        return s.replace("≥", ">=").replace("≤", "<=").replace("→", "->").replace("’", "'").replace("×", "x")

    def width(self, s):
        w = 0
        for ch in self.norm(s):
            if ch == " ":
                w += self.space
            else:
                w += self.glyphs.get(ch, self.glyphs.get("?", (None, 5, 7)))[1] + 1
        return w

    def draw(self, img, x, y, s, color, shadow=False):
        if shadow:
            self.draw(img, x + 1, y + 1, s, tuple(c // 4 for c in color[:3]) + (255,))
        for ch in self.norm(s):
            if ch == " ":
                x += self.space
                continue
            g = self.glyphs.get(ch) or self.glyphs.get("?")
            if not g:
                x += 6
                continue
            gi, w, ascent = g
            mask = gi.getchannel("A")
            solid = Image.new("RGBA", gi.size, color)
            img.paste(solid, (x, y + 7 - ascent), mask)
            x += w + 1
        return x

    def wrap(self, s, width):
        lines, cur = [], ""
        for word in s.split():
            test = (cur + " " + word).strip()
            if self.width(test) <= width or not cur:
                cur = test
            else:
                lines.append(cur)
                cur = word
        if cur:
            lines.append(cur)
        return lines

    def fit(self, s, width):
        if self.width(s) <= width:
            return s
        while s and self.width(s + "…") > width:
            s = s[:-1]
        return s + "…"


def slice9(src, w, h, c=6):
    out = Image.new("RGBA", (w, h))
    sw, sh = src.size
    xs = [(0, c, 0, c), (c, sw - c, c, w - c), (sw - c, sw, w - c, w)]
    ys = [(0, c, 0, c), (c, sh - c, c, h - c), (sh - c, sh, h - c, h)]
    for sx0, sx1, dx0, dx1 in xs:
        for sy0, sy1, dy0, dy1 in ys:
            if dx1 > dx0 and dy1 > dy0:
                out.alpha_composite(src.crop((sx0, sy0, sx1, sy1)).resize((dx1 - dx0, dy1 - dy0), Image.NEAREST), (dx0, dy0))
    return out


def slice3(src, w, c=4):
    out = Image.new("RGBA", (w, src.height))
    out.alpha_composite(src.crop((0, 0, c, src.height)), (0, 0))
    out.alpha_composite(src.crop((c, 0, src.width - c, src.height)).resize((w - 2 * c, src.height), Image.NEAREST), (c, 0))
    out.alpha_composite(src.crop((src.width - c, 0, src.width, src.height)), (w - c, 0))
    return out


def slot(img, x, y):
    d = ImageDraw.Draw(img)
    d.rectangle((x, y, x + 17, y + 17), fill=(139, 139, 139, 255))
    d.line((x, y, x + 16, y), fill=(55, 55, 55, 255))
    d.line((x, y, x, y + 16), fill=(55, 55, 55, 255))
    d.line((x + 1, y + 17, x + 17, y + 17), fill=WHITE)
    d.line((x + 17, y + 1, x + 17, y + 17), fill=WHITE)


def parse(path):
    t = path.read_text(encoding="utf-8")
    d = {"file": path.stem}
    d["name"] = re.search(r"^# Route de craft · (.+)$", t, re.M).group(1)
    m = re.search(r"^`simplywings:(\w+)` · \*\*(.+?)\*\* · item `(\w+)`", t, re.M)
    d["id"], d["rarity"], d["item"] = m.group(1), m.group(2), m.group(3)
    for key, label in [("depth", "Profondeur"), ("crafts", "Crafts"), ("essence", "Essences")]:
        mm = re.search(rf"^\| {label} \| (.+?) \|$", t, re.M)
        d[key] = mm.group(1) if mm else ""
    sec = lambda name: t.split(f"## {name}", 1)[1].split("\n## ", 1)[0] if f"## {name}" in t else ""
    d["raws"] = []
    for row in sec("Matières premières").splitlines():
        mm = re.match(r"^\| (.+?) `(\w+:\w+)` \| (\d+) \| `(\w+)` \| (.+?) \| (.+?) \| (.+?) \|$", row)
        if mm:
            d["raws"].append(dict(name=mm.group(1), id=mm.group(2), qty=int(mm.group(3)), src=mm.group(4),
                                  what=mm.group(5), cond=mm.group(6), chance=mm.group(7)))
    d["route"] = []
    for row in sec("Route").splitlines():
        mm = re.match(r"^\| (\d+) \| (.+?) `(\w+:\w+)`(?: ×(\d+))? \| `(\w+)` \| (.+?) \| (\d+) \| (.+?) \| (.+?) \| (.+?) \|$", row)
        if mm:
            d["route"].append(dict(num=int(mm.group(1)), name=mm.group(2), id=mm.group(3), made=int(mm.group(4) or 1),
                                   where=mm.group(5), level=mm.group(6), crafts=int(mm.group(7)), inputs=mm.group(8),
                                   essence=mm.group(9), cum=mm.group(10)))
    d["recipes"] = {}
    for block in sec("Recettes").split("\n### ")[1:]:
        mm = re.match(r"#(\d+) · (.+?) · `(\w+:\w+)`", block)
        if not mm:
            continue
        info = re.search(r"niveau (\d+) · essence : (.+?) · donne ×(\d+)", block)
        shape = re.search(r"```\n(.*?)\n```", block, re.S).group(1).splitlines()
        key = {k: rid for k, rid in re.findall(r"^\| `(.)` \| .+? `(\w+:\w+)` \|", block, re.M)}
        d["recipes"][mm.group(3)] = dict(num=int(mm.group(1)), name=mm.group(2), id=mm.group(3), level=int(info.group(1)),
                                         cost=essences(info.group(2)), out=int(info.group(3)),
                                         rows=[r.split(" ") for r in shape], key=key)
    asm = next(r for r in d["route"] if r["where"] == "ASM")
    by_num = {r["num"]: r["id"] for r in d["route"]}
    slots = {}
    for label, k in [("Harnais", "harness"), ("Cœur", "core"), ("Aile G", "left"), ("Aile D", "right")]:
        mm = re.search(rf"{label} : .+? → #(\d+)", asm["inputs"])
        slots[k] = by_num[int(mm.group(1))]
    d["asm"] = dict(slots=slots, id=asm["id"], cost=essences(asm["essence"]), total=asm["cum"].strip("*"))
    return d


class Board:
    def __init__(self, ui, icons, font, gui):
        self.ui, self.icons, self.font, self.gui = ui, icons, font, gui

    def icon(self, img, rid, x, y, count=None):
        img.alpha_composite(self.icons.get(rid), (x, y))
        if count and count > 1:
            s = str(count)
            self.font.draw(img, x + 17 - self.font.width(s), y + 9, s, WHITE, shadow=True)

    def panel(self, name):
        src = self.gui[name]
        out = Image.new("RGBA", (176, 116))
        out.alpha_composite(src.crop((0, 0, 176, 112)), (0, 0))
        out.alpha_composite(src.crop((0, 202, 176, 206)), (0, 112))
        return out

    def tinted(self, name, color):
        im = self.gui[name].copy()
        px = im.load()
        for yy in range(im.height):
            for xx in range(im.width):
                r, g, b, a = px[xx, yy]
                if a:
                    px[xx, yy] = (r * color[0] // 255, g * color[1] // 255, b * color[2] // 255, a)
        return im

    def tank(self, img, cost):
        for i, (k, color) in enumerate(KINDS):
            fill = round(70 * max(0.0, min(1.0, cost[k] / GAUGE_CAP)))
            x = 9 + i * 6
            if fill:
                img.alpha_composite(self.tinted("gauge_fill", color).crop((0, 70 - fill, 5, 70)), (x, 18 + 70 - fill))
            img.alpha_composite(self.gui["gauge_ticks"], (x, 18))
        img.alpha_composite(self.icons.get("simplywings:cloud_flask"), (8, 92))

    def cost(self, img, cost, y):
        line = 0
        for k, color in KINDS:
            if cost[k] <= 0:
                continue
            yy = y + line * 9
            img.alpha_composite(self.tinted("cost_icon", color), (140, yy))
            self.font.draw(img, 151, yy, str(cost[k]), LABEL)
            line += 1
        return line

    def centered(self, img, s, cx, y, color=LABEL):
        self.font.draw(img, cx - self.font.width(s) // 2, y, s, color)

    def craft_node(self, r, step):
        img = self.panel("forge_bench_components")
        self.font.draw(img, 8, 6, self.font.fit(f"#{r['num']} {r['name']}", 162), LABEL)
        cell = 0
        for y, row in enumerate(r["rows"]):
            for x, ch in enumerate(row):
                if ch == ".":
                    continue
                if ch != "_":
                    self.icon(img, r["key"][ch], 34 + x * 18, 18 + y * 18)
                cell += 1
        self.icon(img, r["id"], 150, 54, r["out"])
        self.tank(img, r["cost"])
        lines = self.cost(img, r["cost"], 76)
        self.font.draw(img, 140 if lines < 3 else 100, 76 + 9 * min(lines, 2) if lines < 3 else 94, f"x{step['crafts']}", LABEL)
        self.font.draw(img, 140 if lines < 3 else 100, 86 + 9 * min(lines, 2) if lines < 3 else 104, f"niv. {r['level']}", MUTED)
        return img

    def asm_node(self, d):
        img = self.panel("assembly_table_assembly")
        self.font.draw(img, 8, 6, self.font.fit("Assemblage", 60), LABEL)
        s = d["asm"]["slots"]
        self.icon(img, s["harness"], 80, 20)
        self.icon(img, s["core"], 80, 44)
        self.icon(img, s["left"], 56, 44)
        self.icon(img, s["right"], 104, 44)
        img.alpha_composite(self.gui["arrow_progress"], (125, 45))
        self.icon(img, d["asm"]["id"], 150, 44)
        self.tank(img, d["asm"]["cost"])
        self.cost(img, d["asm"]["cost"], 66)
        return img

    def title_node(self, d):
        w, h = 260, 100
        img = slice9(self.ui["card"], w, h)
        img.alpha_composite(slice3(self.ui[RARITY_BAND[d["rarity"]]], w - 8), (4, 4))
        self.font.draw(img, 9, 6, self.font.fit(d["name"], w - 20), WHITE, shadow=True)
        big = self.icons.get(f"simplywings:{d['item']}").resize((64, 64), Image.NEAREST)
        slot(img, 8, 20)
        img.paste((139, 139, 139, 255), (9, 21, 25, 37))
        dd = ImageDraw.Draw(img)
        dd.rectangle((8, 20, 75, 87), fill=(139, 139, 139, 255), outline=(55, 55, 55, 255))
        dd.line((9, 87, 75, 87), fill=WHITE)
        dd.line((75, 21, 75, 87), fill=WHITE)
        img.alpha_composite(big, (10, 22))
        lines = [(f"{d['rarity']} · simplywings:{d['id']}", LABEL),
                 (f"Total : {d['asm']['total']}", LABEL),
                 (f"Crafts : {d['crafts'].replace('`', '')}", MUTED),
                 (d["depth"].split(" avant")[0].replace("niveaux de craft", "niveaux").replace("niveau de craft", "niveau")
                  + " avant les pièces", MUTED)]
        y = 22
        for s, c in lines:
            for ln in self.font.wrap(s, w - 90):
                self.font.draw(img, 82, y, ln, c)
                y += 10
        return img

    def drop_node(self, raw):
        w = 176
        band, ic, verb = ACTION[raw["src"]]
        what = raw["what"].replace("`", "")
        body = []
        body.append((f"Source : {what}", LABEL))
        cond = raw["cond"].replace("`", "")
        icons = [n for k, n in COND_ICONS if k in cond.lower()] if cond != "—" else []
        cond_lines = self.font.wrap(f"Condition : {cond}", w - 18) if cond != "—" else []
        src_lines = self.font.wrap(body[0][0], w - 18)
        h = 44 + 10 * len(src_lines) + (12 if icons else 0) + 10 * len(cond_lines) + 6
        img = slice9(self.ui["card"], w, h)
        img.alpha_composite(slice3(self.ui[band], w - 8), (4, 4))
        img.alpha_composite(self.ui[ic], (6, 3))
        self.font.draw(img, 20, 6, self.font.fit(f"{verb} · {raw['name']}", w - 28), WHITE, shadow=True)
        slot(img, 8, 20)
        img.alpha_composite(self.icons.source(raw["src"], raw["what"]), (9, 21))
        img.alpha_composite(self.ui["arrow"], (29, 24))
        slot(img, 46, 20)
        self.icon(img, raw["id"], 47, 21)
        self.font.draw(img, 68, 25, f"x{raw['qty']}", LABEL)
        img.alpha_composite(self.ui["cond_chance"], (w - 12 - self.font.width(raw["chance"]) - 10, 24))
        self.font.draw(img, w - 10 - self.font.width(raw["chance"]), 25, raw["chance"], LABEL)
        y = 42
        for ln in src_lines:
            self.font.draw(img, 9, y, ln, LABEL)
            y += 10
        if icons:
            x = 9
            for n in icons:
                img.alpha_composite(self.ui[n], (x, y))
                x += 11
            y += 12
        for ln in cond_lines:
            self.font.draw(img, 9, y, ln, MUTED)
            y += 10
        return img

    def vanilla_node(self, raw):
        w = 112
        lines = self.font.wrap(raw["name"], w - 34)[:2]
        src = self.font.wrap(raw["what"].replace("`", ""), w - 14)[:3]
        top = max(28, 7 + 10 * (len(lines) + 1)) + 2
        h = top + 10 * len(src) + 4
        img = slice9(self.ui["card"], w, h)
        slot(img, 6, 6)
        self.icon(img, raw["id"], 7, 7)
        y = 7
        for ln in lines:
            self.font.draw(img, 28, y, ln, LABEL)
            y += 10
        self.font.draw(img, 28, y, f"x{raw['qty']}", LABEL)
        y = top
        for ln in src:
            self.font.draw(img, 7, y, ln, MUTED)
            y += 10
        return img


def layout(d, board, max_w):
    nodes = {}
    rows = []

    def add(key, img, consumers, row):
        nodes[key] = dict(img=img, consumers=consumers, row=row)

    add("title", board.title_node(d), [], 0)
    add("asm", board.asm_node(d), ["title"], 1)
    consumers = {}
    for rid in d["asm"]["slots"].values():
        consumers.setdefault(rid, [])
        if "asm" not in consumers[rid]:
            consumers[rid].append("asm")
    for rid, r in d["recipes"].items():
        for x in r["key"].values():
            consumers.setdefault(x, [])
            if rid not in consumers[x]:
                consumers[x].append(rid)
    steps = {r["id"]: r for r in d["route"]}
    max_level = max(r["level"] for r in d["recipes"].values())
    bands = [("Pair", 0), ("Assemblage · table d'assemblage", 1)]
    row = 2
    for lvl in range(max_level, 0, -1):
        ids = [rid for rid, r in d["recipes"].items() if r["level"] == lvl]
        for rid in ids:
            add(rid, board.craft_node(d["recipes"][rid], steps[rid]), consumers.get(rid, []), row)
        bands.append((f"Niveau {lvl} · crafts de composants", row))
        row += 1
    drops = [r for r in d["raws"] if r["src"] in ACTION]
    if drops:
        for r in drops:
            add(r["id"], board.drop_node(r), consumers.get(r["id"], []), row)
        bands.append(("Récupération · drops du mod (item imprégné d'énergie ailée)", row))
        row += 1
    for r in d["raws"]:
        if r["src"] not in ACTION:
            add(r["id"], board.vanilla_node(r), consumers.get(r["id"], []), row)
    bands.append(("Récupération · ressources vanilla", row))

    gap_x, margin = 14, 24
    placed_rows = []
    by_row = {}
    for k, n in nodes.items():
        by_row.setdefault(n["row"], []).append(k)
    pos = {}
    y = margin
    band_y = {}
    for r in sorted(by_row):
        keys = by_row[r]

        def bary(k):
            xs = [pos[c][0] + nodes[c]["img"].width / 2 for c in nodes[k]["consumers"] if c in pos]
            return sum(xs) / len(xs) if xs else 1e9

        keys.sort(key=bary)
        lines, cur, cur_w = [], [], 0
        for k in keys:
            w = nodes[k]["img"].width
            if cur and cur_w + gap_x + w > max_w:
                lines.append(cur)
                cur, cur_w = [], 0
            cur_w += (gap_x if cur else 0) + w
            cur.append(k)
        if cur:
            lines.append(cur)
        if r > 0:
            y += 42
        band_y[r] = y - 15
        for line in lines:
            lw = sum(nodes[k]["img"].width for k in line) + gap_x * (len(line) - 1)
            placed_rows.append((line, lw, y))
            y += max(nodes[k]["img"].height for k in line) + 12
        y -= 12
    total_w = max(lw for _, lw, _ in placed_rows) + 2 * margin
    total_w = max(total_w, 520)
    for line, lw, ly in placed_rows:
        x = (total_w - lw) // 2
        for k in line:
            pos[k] = (x, ly)
            x += nodes[k]["img"].width + gap_x
    return nodes, pos, bands, band_y, total_w, y + margin


def render(d, board, max_w):
    nodes, pos, bands, band_y, W, H = layout(d, board, max_w)
    img = Image.new("RGBA", (W, H), BG)
    draw = ImageDraw.Draw(img)
    incoming = {}
    for k, n in nodes.items():
        for c in n["consumers"]:
            incoming.setdefault(c, []).append(k)
    for k in incoming:
        incoming[k].sort(key=lambda s: pos[s][0])
    color_i = 0
    for k, n in nodes.items():
        if not n["consumers"]:
            continue
        col = EDGE_COLORS[color_i % len(EDGE_COLORS)] + (255,)
        color_i += 1
        x0 = pos[k][0] + n["img"].width // 2
        y0 = pos[k][1]
        lane = y0 - 22 - (color_i % 4) * 3
        for c in n["consumers"]:
            if c not in pos:
                continue
            sibs = incoming[c]
            i = sibs.index(k)
            px = pos[c][0] + nodes[c]["img"].width // 2 + int((i - (len(sibs) - 1) / 2) * 6)
            py = pos[c][1] + nodes[c]["img"].height
            draw.line([(x0, y0), (x0, lane), (px, lane), (px, py + 3)], fill=col, width=1)
            draw.polygon([(px - 2, py + 4), (px + 2, py + 4), (px, py + 1)], fill=col)
    for label, r in bands:
        if r == 0 or r not in band_y:
            continue
        by = band_y[r]
        img.alpha_composite(slice3(board.ui["band_level"], board.font.width(label) + 14), (16, by - 2))
        board.font.draw(img, 23, by, label, WHITE, shadow=True)
    for k, n in nodes.items():
        img.alpha_composite(n["img"], pos[k])
    return img


def build_ui():
    sprites = UI.glob("*.png")
    if not list(sprites):
        cli = ROOT / ".claude/skills/pxl-pixel-art/src/node/cli.ts"
        subprocess.run(["node", "--experimental-strip-types", "--no-warnings", "-e",
                        f"import('{cli.as_posix()}').then(m=>m.main(['build','ui.px.js','--out','ui']))"],
                       cwd=HERE, check=True)
    return {p.stem: Image.open(p).convert("RGBA") for p in UI.glob("*.png")}


def main():
    ap = argparse.ArgumentParser(description="Render a visual craft tree per wing pair from crafting/*.md")
    ap.add_argument("pairs", nargs="*", help="pair ids (default: all)")
    ap.add_argument("--jar", help="Minecraft client resources jar (default: ModDevGradle client-extra)")
    ap.add_argument("--out", default=str(ROOT / "craft-img"))
    ap.add_argument("--max-width", type=int, default=1900, help="row width before wrapping, in GUI pixels")
    args = ap.parse_args()
    jar = find_jar(args.jar)
    if not jar:
        print("warning: no Minecraft jar found, vanilla textures will be '?'", file=sys.stderr)
    vanilla = Vanilla(jar)
    ui = build_ui()
    gui = {p.stem: Image.open(p).convert("RGBA") for p in GUI.glob("*.png")}
    board = Board(ui, Icons(vanilla, ui["unknown"]), Font(vanilla), gui)
    out = Path(args.out)
    out.mkdir(parents=True, exist_ok=True)
    files = sorted((ROOT / "crafting").glob("*.md"))
    for f in files:
        if args.pairs and f.stem not in args.pairs:
            continue
        d = parse(f)
        img = render(d, board, args.max_width)
        scale = max(1, min(4, 6000 // img.width, 8000 // img.height))
        img = img.resize((img.width * scale, img.height * scale), Image.NEAREST)
        img.save(out / f"{d['id']}.png", optimize=True)
        print(f"{d['id']}: {img.width}x{img.height} (x{scale})")


if __name__ == "__main__":
    main()
