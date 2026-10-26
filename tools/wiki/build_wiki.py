import json
import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
RES = ROOT / "src/main/resources"
DATA = RES / "data/simplywings"
OUT = RES / "assets/simplywings/wiki"
CHAPTERS = Path(__file__).resolve().parent / "chapters"
LANG = json.loads((RES / "assets/simplywings/lang/en_us.json").read_text(encoding="utf-8"))
MATERIALS = (ROOT / "src/main/java/com/zorago/simplywings/item/MaterialIds.java").read_text(encoding="utf-8")
RARITIES = ["common", "rare", "epic", "legendary", "mythic"]
STATS = ["endurance", "acceleration", "speed", "handling", "stability", "glide", "lift"]


def materials(name):
    block = re.search(name + r" = List\.of\((.*?)\);", MATERIALS, re.S).group(1)
    return re.findall(r'"([a-z0-9_]+)"', block)


def name(item):
    ns, path = item.split(":")
    return LANG.get(f"item.{ns}.{path}") or LANG.get(f"block.{ns}.{path}") or path.replace("_", " ").title()


def lore(item):
    ns, path = item.split(":")
    for kind in ("item", "block"):
        if f"{kind}.{ns}.{path}.lore" in LANG:
            return LANG[f"{kind}.{ns}.{path}.lore"], LANG.get(f"{kind}.{ns}.{path}.riddle", "")
    return "", ""


def load(path):
    return json.loads(path.read_text(encoding="utf-8"))


def ingredient(v):
    if isinstance(v, dict):
        return v.get("item") or v.get("tag", "minecraft:barrier")
    return v


def grid_block(recipe_file):
    r = load(recipe_file)
    key = {k: ingredient(v) for k, v in r["key"].items()}
    note = f"Forge Bench, {r['essence']} winged energy" if "essence" in r else "Forge Bench"
    return {"type": "grid", "pattern": r["pattern"], "key": key, "output": r["result"]["id"], "count": r["result"].get("count", 1), "note": note}


def essence_note(essence):
    if not essence:
        return ""
    if not isinstance(essence, dict):
        return f"{essence} Cloud Essence"
    return ", ".join(f"{v} {k.title()} Essence" for k, v in essence.items())


def assembly_block(recipe_file):
    r = load(recipe_file)
    inputs = [ingredient(r[k]) for k in ("harness", "core", "left_wing", "right_wing") if k in r]
    note = "Assembly Table" + (", " + essence_note(r.get("essence")) if r.get("essence") else "")
    return {"type": "recipe", "inputs": inputs, "output": r["result"]["id"], "note": note}


def main():
    if OUT.exists():
        shutil.rmtree(OUT)
    (OUT / "pages").mkdir(parents=True)
    pages = {}
    for f in sorted(CHAPTERS.glob("*.json")):
        pages[f.stem.replace("__", "/")] = load(f)

    component_recipes = {load(f)["result"]["id"]: f for f in sorted((DATA / "recipe/component").glob("*.json"))}
    used_in = {}
    for out, f in component_recipes.items():
        for v in load(f)["key"].values():
            used_in.setdefault(ingredient(v), set()).add(out)
    assembly = {}
    for f in sorted((DATA / "recipe/assembly").glob("*.json")) + [DATA / "recipe/common_wings.json"]:
        if f.exists():
            r = load(f)
            if r.get("type") == "simplywings:assembly":
                assembly[r["result"]["id"]] = f
                for k in ("harness", "core", "left_wing", "right_wing"):
                    if k in r:
                        used_in.setdefault(ingredient(r[k]), set()).add(r["result"]["id"])

    wings = {r: [] for r in RARITIES}
    for f in sorted((DATA / "simplywings/wing").glob("*.json")):
        w = load(f)
        pair = f.stem
        item = f"simplywings:{pair}_wings"
        wings[w["rarity"]].append(pair)
        text, riddle = lore(item)
        blocks = [{"type": "wings3d", "wing": f"simplywings:{pair}"},
                  {"type": "text", "text": text},
                  {"type": "heading", "text": "Statistics (when equipped)"},
                  {"type": "stats", "values": {s: w["equipped"].get(s, 0) for s in STATS}},
                  {"type": "text", "text": "Owning this pair also adds a small bonus to every stat of your collection: " + ", ".join(
                      f"{LANG.get('gui.simplywings.stat.' + s, s)} +{w['global'].get(s, 0):g}" for s in STATS) + "."},
                  {"type": "heading", "text": "How to make it"}]
        if item in assembly:
            blocks.append(assembly_block(assembly[item]))
        elif (DATA / "recipe/common_wings.json").exists() and pair == "common":
            r = load(DATA / "recipe/common_wings.json")
            if "key" in r:
                key = {k: ingredient(v) for k, v in r["key"].items()}
                blocks.append({"type": "grid", "pattern": r["pattern"], "key": key, "output": r["result"]["id"], "count": 1, "note": "Crafting Table"})
        if riddle:
            blocks.append({"type": "text", "text": riddle})
        parts = [f"simplywings:{pair}_core", f"simplywings:{pair}_wing"]
        if item in assembly:
            r = load(assembly[item])
            parts = [ingredient(r[k]) for k in ("core", "left_wing") if k in r]
        for part in parts:
            if part in component_recipes:
                blocks.append({"type": "heading", "text": name(part)})
                blocks.append(grid_block(component_recipes[part]))
        if pair in ("night", "light"):
            blocks.append({"type": "link", "page": f"mythics/{pair}", "text": "Read on: the powers of this Fury"})
        blocks.append({"type": "link", "page": "summoning/altar", "text": "This pair can also be summoned at the Draconic Altar"})
        pages[f"wings/{w['rarity']}/{pair}"] = {"title": name(item), "icon": item, "blocks": blocks}

    groups = {"materials/drops": ("Primary materials", "DROPS", "Imbued drops and raw materials, gathered from mobs and blocks. Hover an item to read its riddle, click it to open its page."),
              "materials/components": ("Intermediates", "INTERMEDIATES", "Shared pieces forged from primary materials, used by many routes."),
              "materials/parts": ("Wing parts", "PARTS", "Cores, harnesses and wing parts: the last step before the Assembly Table.")}
    for pid, (title, const, intro) in groups.items():
        ids = [f"simplywings:{i}" for i in materials(const)]
        pages[pid] = {"title": title, "icon": ids[0], "blocks": [{"type": "text", "text": intro}, {"type": "items", "items": ids}]}
        for item in ids:
            text, riddle = lore(item)
            blocks = [{"type": "items", "items": [item]}, {"type": "text", "text": text}]
            if riddle:
                blocks.append({"type": "heading", "text": "Where to find it"})
                blocks.append({"type": "text", "text": riddle})
            if item in component_recipes:
                blocks.append({"type": "heading", "text": "Recipe"})
                blocks.append(grid_block(component_recipes[item]))
            if used_in.get(item):
                blocks.append({"type": "heading", "text": "Used in"})
                blocks.append({"type": "items", "items": sorted(used_in[item])})
            pages[f"items/{item.split(':')[1]}"] = {"title": name(item), "icon": item, "blocks": blocks}

    for rarity in RARITIES:
        pages[f"wings/{rarity}"] = None

    def chapter_ids(prefix):
        return sorted(k for k in pages if k.startswith(prefix + "/") and pages[k] is not None)

    categories = {
        "intro": {"title": "Getting Started", "icon": "simplywings:common_wings", "children": ["intro/welcome", "intro/first_flight"]},
        "flight": {"title": "Flight", "icon": "minecraft:elytra", "children": ["flight/controls", "flight/modes", "flight/stamina", "flight/stats"]},
        "wings": {"title": "Wing Pairs", "icon": "simplywings:eagle_wings", "children": [f"wings/{r}" for r in RARITIES]},
        "crafting": {"title": "Crafting", "icon": "simplywings:assembly_table",
                     "children": ["crafting/overview", "crafting/winged_energy", "crafting/essences", "materials"]},
        "materials": {"title": "Materials", "icon": "simplywings:raptor_talon", "children": ["materials/drops", "materials/components", "materials/parts"]},
        "workshops": {"title": "Workshops", "icon": "simplywings:forge_bench",
                      "children": ["workshops/assembly_table", "workshops/forge_bench", "workshops/cloud_compressor"]},
        "end": {"title": "The End", "icon": "minecraft:dragon_head",
                "children": ["end/dragon", "end/sky_sanctum", "end/wing_forge", "end/cloud_refinery", "end/summoning_sanctum", "end/offering_fountain", "end/compasses"]},
        "summoning": {"title": "Summoning", "icon": "simplywings:summoning_feather", "children": ["summoning/feathers", "summoning/altar", "summoning/fountain"]},
        "mythics": {"title": "The Furies", "icon": "simplywings:night_wings", "children": ["mythics/night", "mythics/light"]},
    }
    for rarity in RARITIES:
        pairs = sorted(wings[rarity], key=lambda p: name(f"simplywings:{p}_wings"))
        icon = f"simplywings:{pairs[0]}_wings" if pairs else "simplywings:common_wings"
        categories[f"wings/{rarity}"] = {"title": rarity.title() + " pairs", "icon": icon, "children": [f"wings/{rarity}/{p}" for p in pairs]}
        del pages[f"wings/{rarity}"]
    missing = [c for cat in categories.values() for c in cat["children"] if c not in categories and c not in pages]
    for c in missing:
        print("missing page:", c)
    for cat in categories.values():
        cat["children"] = [c for c in cat["children"] if c in categories or c in pages]
    tree = {"root": ["intro", "flight", "wings", "crafting", "workshops", "end", "summoning", "mythics"], "categories": categories}
    (OUT / "tree.json").write_text(json.dumps(tree, indent=1), encoding="utf-8")
    for pid, page in pages.items():
        (OUT / "pages" / (pid.replace("/", "__") + ".json")).write_text(json.dumps(page, ensure_ascii=False), encoding="utf-8")
    print(f"{len(pages)} pages, {len(categories)} categories")


if __name__ == "__main__":
    main()
