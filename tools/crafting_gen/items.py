import json
import shutil
import subprocess
from collections import OrderedDict
from pathlib import Path

from PIL import Image

from art import GEMS, ITEMS, METALS, palette, px_js, ramp
from data import C, D, V
from lore import CRAFT_LORE, DROP_LORE, EXTRA, PAIR_LORE
from pairs import PAIRS

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
ASSETS = ROOT / "src/main/resources/assets/simplywings"
JAVA = ROOT / "src/main/java/com/zorago/simplywings/item/MaterialIds.java"
PXL = ROOT / ".claude/skills/pxl-pixel-art/src/node/cli.ts"
SKIP_PAIRS = {"yin_yang"}
ASM_COST = {"Rare": 100, "Épique": 300, "Légendaire": 800, "Mythique": 1000}
TEMPLATES = [
    "Of {list}, it is shaped at the Assembly Table.",
    "Bring {list} to the Assembly Table, and it will take form.",
    "The Assembly Table weaves it from {list}.",
    "Born at the Assembly Table, from {list}.",
]


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
        out[f"item.simplywings.{p['id']}_wings.riddle"] = (
            f"At the Assembly Table, join the {name(p['harness'])}, the {name(p['core'])} and two {name(p['left'])}s, "
            f"for {ASM_COST[p['rarity']]} Cloud Essence.")
    out["item.simplywings.common_wings.lore"] = PAIR_LORE["common"][0]
    out["item.simplywings.common_wings.riddle"] = "At the Assembly Table, join leather, an elytra and two feathers. No Cloud Essence needed."
    out["item.simplywings.cloud_flask.lore"] = EXTRA["cloud_flask"][0]
    out["item.simplywings.cloud_flask.riddle"] = "At the Assembly Table, wrap a feather in glass. Then fly through the clouds with a Cloud Absorber to fill it."
    out["block.simplywings.assembly_table.lore"] = EXTRA["assembly_table"][0]
    out["block.simplywings.assembly_table.riddle"] = EXTRA["assembly_table"][1]
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
    for i in drops + crafts:
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
            half = src.crop((0, 0, src.width // 2, src.height))
            bbox = half.getbbox()
            if bbox:
                half = half.crop(bbox)
            out = Image.new("RGBA", (16, 16))
            out.alpha_composite(half, ((16 - half.width) // 2, (16 - half.height) // 2))
            out.save(tex / f"{i}.png")
    missing = [i for i in drops + crafts if not (tex / f"{i}.png").exists()]
    return missing


def main():
    drops, crafts = used_items()
    write_lang(lang(drops, crafts))
    write_models(drops + crafts)
    write_java(drops, crafts)
    missing = textures(drops, crafts)
    print(f"{len(drops)} drops, {len(crafts)} crafted, missing textures: {missing}")


if __name__ == "__main__":
    main()
