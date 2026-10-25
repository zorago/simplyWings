import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SRC = ROOT / "src/main"
ASSETS = SRC / "resources/assets/simplywings"
DATA = SRC / "resources/data/simplywings"


def registered():
    java = (SRC / "java/com/zorago/simplywings/item/ModItems.java").read_text(encoding="utf-8")
    ids = [f"{w}_wings" for w in re.findall(r'wing\("(\w+)", Rarity', java)]
    ids += re.findall(r'ITEMS\.register\("(\w+)"', java)
    ids += re.findall(r'tank\("(\w+)"', java)
    ids += re.findall(r'absorber\("(\w+)"', java)
    mats = (SRC / "java/com/zorago/simplywings/item/MaterialIds.java").read_text(encoding="utf-8")
    ids += re.findall(r'"(\w+)"', mats)
    blocks = (SRC / "java/com/zorago/simplywings/block/ModBlocks.java").read_text(encoding="utf-8")
    block_items = re.findall(r'registerSimpleBlockItem\("(\w+)"|ITEMS\.register\w*\("(\w+)"', blocks)
    return ids, [a or b for a, b in block_items]


def main():
    items, blocks = registered()
    lang = json.loads((ASSETS / "lang/en_us.json").read_text(encoding="utf-8"))
    recipes = {}
    for f in DATA.glob("recipe/**/*.json"):
        recipes[f] = json.loads(f.read_text(encoding="utf-8"))
    consumed, produced = set(), set()
    for r in recipes.values():
        produced.add(r["result"]["id"].split(":")[1])
        text = json.dumps({k: v for k, v in r.items() if k != "result"})
        consumed.update(re.findall(r'simplywings:(\w+)', text))
    wings = {p.stem for p in (DATA / "simplywings/wing").glob("*.json")}
    problems = []
    for i in items:
        key = f"item.simplywings.{i}"
        checks = {
            "model": (ASSETS / f"models/item/{i}.json").exists(),
            "texture": (ASSETS / f"textures/item/{i}.png").exists() or i.endswith("_compass"),
            "name": key in lang,
            "lore": f"{key}.lore" in lang,
            "riddle": f"{key}.riddle" in lang,
        }
        if i.endswith("_wings"):
            pair = i[:-6]
            checks["wing json"] = pair in wings
            checks["wing name"] = f"wing.simplywings.{pair}" in lang
            checks["obtainable"] = i in produced
        elif not i.endswith("_compass") and i not in ("cloud_absorber", "cloud_flask", "fury_essence_tank", "fury_cloud_absorber", "summoning_feather"):
            checks["used"] = i in consumed
        for name, ok in checks.items():
            if not ok:
                problems.append(f"{i}: missing {name}")
    for b in blocks:
        for suffix in ("", ".lore", ".riddle"):
            if f"block.simplywings.{b}{suffix}" not in lang:
                problems.append(f"block {b}: missing lang {suffix or 'name'}")
    for w in wings:
        if f"{w}_wings" not in items:
            problems.append(f"wing json {w} has no item")
    print(f"{len(items)} items, {len(blocks)} block items, {len(recipes)} recipes")
    print("\n".join(problems) if problems else "no problem found")


if __name__ == "__main__":
    main()
