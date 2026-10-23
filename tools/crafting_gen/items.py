import json
import math
import shutil
import subprocess
from collections import OrderedDict
from pathlib import Path

from PIL import Image, ImageDraw

from art import GEMS, ITEMS, METALS, palette, px_js, ramp
from data import C, D, TANKS, V
from essence import ASM, KINDS, LABEL, cost, json_cost
from lore import CRAFT_LORE, DROP_LORE, EXTRA, PAIR_LORE
from pairs import PAIRS
import loot

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
ASSETS = ROOT / "src/main/resources/assets/simplywings"
JAVA = ROOT / "src/main/java/com/zorago/simplywings/item/MaterialIds.java"
PXL = ROOT / ".claude/skills/pxl-pixel-art/src/node/cli.ts"
SKIP_PAIRS = set()
STANDALONE = ["cloud_absorber"] + TANKS
RECIPES = ROOT / "src/main/resources/data/simplywings/recipe"
TEMPLATES = [
    "Of {list}, it is hammered out at the Forge Bench.",
    "Bring {list} to the Forge Bench, and it will take form.",
    "The Forge Bench welds it from {list}.",
    "Born at the Forge Bench, from {list}.",
]


def essence_text(c):
    parts = [f"{c[k]} {LABEL[k]}" for k in KINDS if k in c]
    return (parts[0] if len(parts) == 1 else ", ".join(parts[:-1]) + " and " + parts[-1]) + " Essence"


def name(i):
    if i in C:
        return C[i]["name"]
    if i in D:
        return D[i]["name"]
    return V[i][0]


def inputs(i):
    seen = []
    for row in C[i]["rows"]:
        for ch in row:
            if ch not in "._" and C[i]["key"][ch] not in seen:
                seen.append(C[i]["key"][ch])
    return seen


def listing(names):
    return names[0] if len(names) == 1 else ", ".join(names[:-1]) + " and " + names[-1]


def used_items():
    needed, stack = [], []
    for p in PAIRS:
        if p["id"] in SKIP_PAIRS:
            continue
        stack += [p["harness"], p["core"], p["left"], p["right"]]
    while stack:
        i = stack.pop()
        if i in needed or i in V:
            continue
        needed.append(i)
        if i in C:
            stack += inputs(i)
    drops = [i for i in D if i in needed]
    crafts = [i for i in C if i in needed]
    return drops, crafts


def pair_of(item):
    for p in PAIRS:
        if item in (p["core"], p["left"], p["right"]):
            return p
    return None


def lang(drops, crafts):
    out = OrderedDict()
    for i in drops:
        lore, riddle = DROP_LORE[i]
        out[f"item.simplywings.{i}"] = D[i]["name"]
        out[f"item.simplywings.{i}.lore"] = lore
        out[f"item.simplywings.{i}.riddle"] = riddle
    for i in crafts:
        p = pair_of(i)
        if i in CRAFT_LORE:
            lore = CRAFT_LORE[i]
        elif p and i == p["core"]:
            lore = PAIR_LORE[p["id"]][1]
        else:
            lore = PAIR_LORE[p["id"]][2]
        riddle = TEMPLATES[sum(map(ord, i)) % len(TEMPLATES)].format(list=listing([name(x) for x in inputs(i)]))
        out[f"item.simplywings.{i}"] = C[i]["name"]
        out[f"item.simplywings.{i}.lore"] = lore
        out[f"item.simplywings.{i}.riddle"] = riddle
    for p in PAIRS:
        if p["id"] in SKIP_PAIRS:
            continue
        out[f"item.simplywings.{p['id']}_wings.lore"] = PAIR_LORE[p["id"]][0]
        wings = (f"and two {name(p['left'])}s" if p["left"] == p["right"]
                 else f", a {name(p['left'])} and a {name(p['right'])}")
        out[f"item.simplywings.{p['id']}_wings.riddle"] = (
            f"At the Assembly Table, join the {name(p['harness'])}, the {name(p['core'])}{'' if wings[0] == ',' else ' '}{wings}, "
            f"for {essence_text(ASM[p['rarity']])}.")
    out["item.simplywings.common_wings.lore"] = PAIR_LORE["common"][0]
    out["item.simplywings.common_wings.riddle"] = "At the Assembly Table, join leather, an elytra and two feathers. No Cloud Essence needed."
    out["item.simplywings.cloud_flask.lore"] = EXTRA["cloud_flask"][0]
    out["item.simplywings.cloud_flask.riddle"] = "At the Forge Bench, wrap a feather in glass. Then fly with a Cloud Absorber through the clouds, above the Nether's lava or over the End's void to fill it."
    for i in STANDALONE:
        out[f"item.simplywings.{i}"] = C[i]["name"]
        out[f"item.simplywings.{i}.lore"] = CRAFT_LORE[i]
    out["item.simplywings.cloud_absorber.riddle"] = (
        "Of Glass, Copper Ingot, Updraft Shard and a Feather, it is hammered out at the Forge Bench. "
        "Keep it in your bag and fly: it fills your flasks and tanks with the essence of the sky around you.")
    for t in TANKS:
        r = C[t]
        out[f"item.simplywings.{t}.riddle"] = (
            f"Of {listing([name(x) for x in inputs(t)])}, it is welded at the Forge Bench, for {essence_text(cost(t))}. "
            "When your stamina runs dry, its Intensified Essence keeps your wings beating.")
    for c, title, nth, place in [("sanctum_compass", "Sanctum Compass", "second", "Sky Sanctum"),
                                 ("forge_compass", "Forge Compass", "third", "Wing Forge"),
                                 ("refinery_compass", "Refinery Compass", "fourth", "Cloud Refinery")]:
        out[f"item.simplywings.{c}"] = title
        out[f"item.simplywings.{c}.lore"] = CRAFT_LORE[c]
        out[f"item.simplywings.{c}.riddle"] = (
            f"Stand on the central End island when the {nth} dragon falls, or any one after. It points to the {place}.")
    out["item.simplywings.yin_yang_wings"] = "Wings of Yin and Yang"
    out["wing.simplywings.yin_yang"] = "Wings of Yin and Yang"
    for b in ("assembly_table", "forge_bench"):
        out[f"block.simplywings.{b}.lore"] = EXTRA[b][0]
        out[f"block.simplywings.{b}.riddle"] = EXTRA[b][1]
    return out


def write_lang(entries):
    path = ASSETS / "lang/en_us.json"
    data = json.loads(path.read_text(encoding="utf-8"), object_pairs_hook=OrderedDict)
    data.update(entries)
    path.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8", newline="\n")


def write_models(ids):
    for i in ids:
        (ASSETS / f"models/item/{i}.json").write_text(json.dumps(
            {"parent": "minecraft:item/generated", "textures": {"layer0": f"simplywings:item/{i}"}}, indent=2) + "\n",
            encoding="utf-8", newline="\n")


def write_java(drops, crafts):
    crafts = [i for i in crafts if i not in STANDALONE]
    parts = [i for i in crafts if pair_of(i) or i.endswith("_harness") or i == "fury_saddle"]
    others = [i for i in crafts if i not in parts]
    fmt = lambda ids: ",\n            ".join(f'"{i}"' for i in ids)
    JAVA.write_text(f"""package com.zorago.simplywings.item;

import java.util.List;

public final class MaterialIds {{
    public static final List<String> DROPS = List.of(
            {fmt(drops)});

    public static final List<String> INTERMEDIATES = List.of(
            {fmt(others)});

    public static final List<String> PARTS = List.of(
            {fmt(parts)});

    private MaterialIds() {{
    }}
}}
""", encoding="utf-8", newline="\n")


def textures(drops, crafts):
    sprites = OrderedDict()
    for i in drops + crafts + STANDALONE:
        p = pair_of(i)
        if p and i == p["core"]:
            pal = ramp(GEMS[p["id"]])
            pal["e"], pal["f"] = METALS[p["rarity"]]
            sprites[i] = ("core", pal)
        elif i in ITEMS:
            spec = ITEMS[i]
            base, main = spec[0], spec[1]
            e = spec[2] if len(spec) > 2 else None
            f = spec[3] if len(spec) > 3 else None
            extra = spec[4] if len(spec) > 4 else None
            sprites[i] = (base, palette(base, main or "#808080", e, f, extra))
    work = HERE / "_art"
    work.mkdir(exist_ok=True)
    (work / "items.px.js").write_text(px_js(sprites), encoding="utf-8")
    subprocess.run(["node", "--experimental-strip-types", "--no-warnings", "-e",
                    f"import('{PXL.as_uri()}').then(m=>m.main(['build','items.px.js','--out','out','--no-preview']))"],
                   cwd=work, check=True, stdout=subprocess.DEVNULL)
    tex = ASSETS / "textures/item"
    for i in sprites:
        shutil.copy(work / "out/sprites" / f"{i}.png", tex / f"{i}.png")
    for i in crafts:
        p = pair_of(i)
        if p and i in (p["left"], p["right"]) and i not in sprites:
            src = Image.open(tex / f"{p['id']}_wings.png").convert("RGBA")
            right = i == p["right"] and p["left"] != p["right"]
            half = src.crop((src.width // 2, 0, src.width, src.height) if right else (0, 0, src.width // 2, src.height))
            bbox = half.getbbox()
            if bbox:
                half = half.crop(bbox)
            out = Image.new("RGBA", (16, 16))
            out.alpha_composite(half, ((16 - half.width) // 2, (16 - half.height) // 2))
            out.save(tex / f"{i}.png")
    compass(tex)
    containers(tex)
    missing = [i for i in drops + crafts + STANDALONE + list(COMPASSES) if not (tex / f"{i}.png").exists()]
    return missing


COMPASSES = {
    "sanctum_compass": ((34, 30, 58, 255), (150, 120, 220, 255), (236, 232, 250, 255), (120, 220, 255, 255)),
    "forge_compass": ((40, 24, 18, 255), (150, 80, 40, 255), (230, 214, 170, 255), (240, 110, 40, 255)),
    "refinery_compass": ((30, 34, 30, 255), (190, 120, 70, 255), (220, 236, 230, 255), (244, 197, 66, 255)),
}


def compass(tex):
    models = ASSETS / "models/item"
    for cid, (rim, ring, face, needle) in COMPASSES.items():
        base = Image.new("RGBA", (16, 16))
        for y in range(16):
            for x in range(16):
                d = math.hypot(x - 7.5, y - 7.5)
                if d <= 7.6:
                    base.putpixel((x, y), rim if d > 6.6 else ring if d > 5.6 else face)
        for k in range(32):
            frame = base.copy()
            draw = ImageDraw.Draw(frame)
            a = k / 32 * 2 * math.pi
            dx, dy = math.sin(a), -math.cos(a)
            draw.line([(7.5, 7.5), (7.5 + dx * 5, 7.5 + dy * 5)], fill=needle)
            draw.line([(7.5, 7.5), (7.5 - dx * 3, 7.5 - dy * 3)], fill=(90, 90, 100, 255))
            frame.putpixel((7, 7), rim)
            frame.save(tex / f"{cid}_{k:02d}.png")
            (models / f"{cid}_{k:02d}.json").write_text(json.dumps(
                {"parent": "minecraft:item/generated", "textures": {"layer0": f"simplywings:item/{cid}_{k:02d}"}},
                indent=2) + "\n", encoding="utf-8", newline="\n")
        shutil.copy(tex / f"{cid}_00.png", tex / f"{cid}.png")
        overrides = [{"predicate": {"simplywings:angle": (k - 0.5) / 32}, "model": f"simplywings:item/{cid}_{k:02d}"}
                     for k in range(1, 32)]
        overrides.append({"predicate": {"simplywings:angle": 31.5 / 32}, "model": f"simplywings:item/{cid}_00"})
        (models / f"{cid}.json").write_text(json.dumps(
            {"parent": "minecraft:item/generated", "textures": {"layer0": f"simplywings:item/{cid}_00"},
             "overrides": overrides}, indent=2) + "\n", encoding="utf-8", newline="\n")


FLASK = [
    "................",
    "......aaaa......",
    "......abba......",
    "......acda......",
    ".....aaaaaa.....",
    "......aefa......",
    "......aefa......",
    ".....ae..fa.....",
    "....ae....fa....",
    "...aeg.....fa...",
    "...aeg.....fa...",
    "...af......ha...",
    "...af......ha...",
    "....af....ha....",
    ".....ahhhha.....",
    "......aaaa......",
]
FLASK_PAL = {"a": (43, 34, 56), "b": (194, 124, 30), "c": (138, 79, 18), "d": (227, 161, 47),
             "e": (216, 240, 255), "f": (159, 208, 234), "g": (255, 255, 255), "h": (95, 134, 163)}
TANK_METALS = {
    "iron_essence_tank": ((52, 52, 60), (120, 122, 132), (176, 178, 186), (222, 224, 230), (140, 90, 50)),
    "gilded_essence_tank": ((70, 44, 16), (170, 110, 30), (226, 170, 52), (253, 226, 122), (120, 70, 30)),
    "radiant_essence_tank": ((24, 60, 70), (60, 160, 170), (120, 220, 220), (210, 250, 250), (40, 90, 110)),
    "fury_essence_tank": ((18, 14, 26), (52, 40, 70), (96, 76, 120), (160, 130, 190), (200, 60, 200)),
}


def gray(v):
    return (v, v, v, 255)


def save_layers(tex, models, iid, frame, liquid_at, levels):
    frame.save(tex / f"{iid}.png")
    overrides = []
    for n in range(1, levels + 1):
        liquid = Image.new("RGBA", (16, 16))
        for (x, y), v in liquid_at(n).items():
            liquid.putpixel((x, y), gray(v))
        liquid.save(tex / f"{iid}_liquid_{n}.png")
        (models / f"{iid}_{n}.json").write_text(json.dumps(
            {"parent": "minecraft:item/generated",
             "textures": {"layer0": f"simplywings:item/{iid}", "layer1": f"simplywings:item/{iid}_liquid_{n}"}},
            indent=2) + "\n", encoding="utf-8", newline="\n")
        overrides.append({"predicate": {"simplywings:fill": round((n - 1) / levels + 0.01, 2)},
                          "model": f"simplywings:item/{iid}_{n}"})
    (models / f"{iid}.json").write_text(json.dumps(
        {"parent": "minecraft:item/generated", "textures": {"layer0": f"simplywings:item/{iid}"},
         "overrides": overrides}, indent=2) + "\n", encoding="utf-8", newline="\n")


def containers(tex):
    models = ASSETS / "models/item"
    frame = Image.new("RGBA", (16, 16))
    for y, row in enumerate(FLASK):
        for x, ch in enumerate(row):
            if ch != ".":
                frame.putpixel((x, y), FLASK_PAL[ch] + (255,))
    inside = {}
    for y in range(7, 15):
        row = FLASK[y]
        xs = [x for x, ch in enumerate(row) if ch != "."]
        for x in range(xs[0] + 1, xs[-1]):
            if row[x] == ".":
                inside[(x, y)] = True
    bottom = 14

    def flask_liquid(n):
        top = {1: 12, 2: 10, 3: 8}[n]
        out = {}
        for (x, y) in inside:
            if y >= top:
                v = 255 if y == top else 230 if (x + y) % 5 else 250
                if y >= bottom - 1:
                    v = 196
                out[(x, y)] = v
        return out

    save_layers(tex, models, "cloud_flask", frame, flask_liquid, 3)
    for iid, (k, d, m, l, accent) in TANK_METALS.items():
        img = Image.new("RGBA", (16, 16))
        put = lambda x, y, c: img.putpixel((x, y), c + (255,))
        for x in range(6, 10):
            put(x, 1, k)
        put(7, 0, k); put(8, 0, k)
        for x in range(4, 12):
            put(x, 2, k)
        for y in range(3, 15):
            put(3, y, k); put(12, y, k)
        for x in range(4, 12):
            put(x, 15, k)
        for y in range(3, 15):
            for x in range(4, 12):
                shade = l if x == 4 else m if x < 7 else d if x > 9 else m
                put(x, y, shade)
        for x in range(6, 10):
            put(x, 2, l if x < 8 else m)
        for y in (3, 14):
            for x in range(4, 12):
                put(x, y, k)
        for y in (4, 13):
            for x in range(4, 12):
                put(x, y, accent if x in (4, 11) else l if x < 7 else m)
        for y in range(5, 13):
            put(5, y, k); put(10, y, k)
            for x in range(6, 10):
                put(x, y, k if y in (5, 12) else (24, 22, 30) if x < 8 else (34, 32, 42))
        put(6, 6, (70, 70, 84))
        window = [(x, y) for y in range(6, 12) for x in range(6, 10)]

        def tank_liquid(n, window=window):
            top = 12 - round(6 * n / 4)
            return {(x, y): (255 if y == top or x == 6 else 196 if x == 9 else 232) for (x, y) in window if y >= top}

        save_layers(tex, models, iid, img, tank_liquid, 4)


def rid(i):
    return f"minecraft:{i}" if i in V else f"simplywings:{i}"


def write_recipes(crafts):
    comp = RECIPES / "component"
    asm = RECIPES / "assembly"
    for folder in (comp, asm):
        if folder.exists():
            shutil.rmtree(folder)
        folder.mkdir(parents=True)
    for i in crafts + STANDALONE:
        r = C[i]
        recipe = {"type": "simplywings:component",
                  "pattern": ["".join(" " if ch in "._" else ch for ch in row) for row in r["rows"]],
                  "key": {k: {"item": rid(v)} for k, v in r["key"].items()},
                  "result": {"id": f"simplywings:{i}", "count": r["out"]},
                  "essence": json_cost(cost(i))}
        (comp / f"{i}.json").write_text(json.dumps(recipe, indent=2) + "\n", encoding="utf-8", newline="\n")
    for p in PAIRS:
        if p["id"] in SKIP_PAIRS:
            continue
        recipe = {"type": "simplywings:assembly",
                  "harness": {"item": rid(p["harness"])}, "core": {"item": rid(p["core"])},
                  "left_wing": {"item": rid(p["left"])}, "right_wing": {"item": rid(p["right"])},
                  "result": {"id": f"simplywings:{p['id']}_wings"}, "essence": json_cost(ASM[p["rarity"]])}
        (asm / f"{p['id']}_wings.json").write_text(json.dumps(recipe, indent=2) + "\n", encoding="utf-8", newline="\n")


def main():
    drops, crafts = used_items()
    write_lang(lang(drops, crafts))
    write_models(drops + crafts + STANDALONE)
    write_models([f"{p['id']}_wings" for p in PAIRS if not (ASSETS / f"models/item/{p['id']}_wings.json").exists()])
    write_recipes(crafts)
    write_java(drops, crafts)
    unsourced = loot.write(drops)
    if unsourced:
        print("drops without loot source:", unsourced)
    missing = textures(drops, crafts)
    print(f"{len(drops)} drops, {len(crafts)} crafted, missing textures: {missing}")


if __name__ == "__main__":
    main()
