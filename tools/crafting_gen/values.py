import json
import math
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DATA = ROOT / "src/main/resources/data/simplywings"
OUT = DATA / "simplywings_offering/values.json"

VANILLA = {
    "oak_log": 0.3, "stick": 0.05, "bamboo": 0.1, "paper": 0.2, "book": 1.2, "leather": 0.6, "rabbit_hide": 0.5,
    "feather": 0.3, "string": 0.3, "bone": 0.3, "bone_meal": 0.1, "ink_sac": 0.4, "glow_ink_sac": 0.8,
    "white_wool": 0.4, "red_wool": 0.5, "blue_wool": 0.6, "green_wool": 0.6, "iron_ingot": 1.0, "iron_nugget": 0.12,
    "gold_ingot": 1.6, "gold_nugget": 0.18, "gold_block": 14.0, "diamond": 8.0, "netherite_ingot": 60.0,
    "netherite_scrap": 14.0, "copper_ingot": 0.5, "coal": 0.3, "charcoal": 0.3, "redstone": 0.3, "lapis_lazuli": 0.5,
    "emerald": 2.5, "quartz": 0.6, "amethyst_shard": 0.8, "calcite": 0.3, "obsidian": 1.5, "crying_obsidian": 3.0,
    "moss_block": 0.4, "cobbled_deepslate": 0.1, "blue_ice": 3.0, "bone_block": 0.9, "glass": 0.3, "glass_pane": 0.12,
    "glass_bottle": 0.3, "lantern": 1.2, "soul_lantern": 1.4, "clock": 7.0, "compass": 4.5, "spyglass": 3.0,
    "note_block": 2.0, "sugar": 0.2, "golden_carrot": 1.6, "cod": 0.4, "tropical_fish": 0.8, "dried_kelp": 0.1,
    "nautilus_shell": 6.0, "honeycomb": 1.0, "honey_bottle": 1.0, "armadillo_scute": 1.5, "oak_leaves": 0.1,
    "pink_petals": 0.3, "spore_blossom": 4.0, "torchflower": 5.0, "pitcher_plant": 5.0, "nether_wart": 1.0,
    "blaze_powder": 2.0, "blaze_rod": 4.0, "ghast_tear": 6.0, "magma_cream": 2.0, "end_rod": 4.0, "dragon_breath": 8.0,
    "phantom_membrane": 2.5, "ender_pearl": 2.0, "wind_charge": 2.0, "breeze_rod": 6.0, "observer": 2.5, "piston": 2.0,
    "lightning_rod": 1.6, "copper_bulb": 2.5, "sculk_sensor": 6.0, "saddle": 10.0, "wither_skeleton_skull": 45.0,
    "nether_star": 150.0, "gunpowder": 0.6, "rabbit_foot": 3.0, "slime_ball": 0.6, "echo_shard": 12.0,
    "heart_of_the_sea": 30.0, "totem_of_undying": 40.0, "trident": 30.0, "shulker_shell": 8.0, "chorus_fruit": 0.5,
    "popped_chorus_fruit": 0.6, "prismarine_shard": 0.6, "prismarine_crystals": 0.8, "turtle_scute": 4.0,
    "goat_horn": 6.0, "experience_bottle": 2.0, "enchanted_book": 6.0, "golden_apple": 14.0, "name_tag": 6.0,
}
DYE = 0.3
DEFAULT_VANILLA = 0.8

TARGET_MULT = {
    "ender_dragon": 30.0, "wither": 30.0, "elder_guardian": 8.0, "shulker": 4.0, "enderman": 2.5, "end_stone": 2.5,
    "blaze": 3.0, "ghast": 3.5, "ancient_debris": 8.0, "breeze": 2.5, "vex": 2.5, "guardian": 2.0, "sculk_shrieker": 3.0,
    "magma_cube": 1.6, "nether_quartz_ore": 1.4, "soul_sand": 1.4, "soul_soil": 1.4, "diamond_ore": 4.0,
    "deepslate_diamond_ore": 4.0, "emerald_ore": 4.0, "deepslate_emerald_ore": 4.0, "phantom": 2.0, "creeper": 1.4,
}
CONDITION_MULT = 1.8
ESSENCE_UNIT = 0.012
CRAFT_PREMIUM = 1.1
WOOD_ANCHOR = ("acorn", 0.005)
TOP_CORE = 20.0
EV_TARGET = 0.10
RATES = {"A": 0.12, "S": 0.01345, "SS": 0.00224, "SSS": 0.00137}


def load(path):
    return json.loads(path.read_text(encoding="utf-8"))


def short(item_id):
    return item_id.split(":", 1)[1]


def drops():
    out = {}
    for f in sorted((DATA / "simplywings/loot_source").glob("*.json")):
        d = load(f)
        item = short(d["item"])
        chance = max(0.02, float(d.get("chance", 1.0)))
        mult = max([TARGET_MULT.get(short(t.lstrip("#")), 1.0) for t in d.get("targets", [])] or [1.0])
        if d.get("conditions"):
            mult *= CONDITION_MULT
        raw = 0.5 * mult / chance ** 0.6
        out[item] = min(out.get(item, math.inf), raw)
    return out


def vanilla(item):
    if item.endswith("_dye"):
        return DYE
    return VANILLA.get(item, DEFAULT_VANILLA)


def recipes():
    out = {}
    for kind in ("component", "assembly"):
        for f in sorted((DATA / "recipe" / kind).glob("*.json")):
            d = load(f)
            result = short(d["result"]["id"])
            count = d["result"].get("count", 1)
            inputs = {}
            if kind == "component":
                for row in d["pattern"]:
                    for ch in row:
                        if ch != " ":
                            key = d["key"][ch]["item"]
                            inputs[key] = inputs.get(key, 0) + 1
            else:
                for slot in ("harness", "core", "left_wing", "right_wing"):
                    key = d[slot]["item"]
                    inputs[key] = inputs.get(key, 0) + 1
            e = d.get("essence", 0)
            essence = sum(e.values()) if isinstance(e, dict) else e
            out[result] = (inputs, essence, count)
    return out


def main():
    raw = dict(drops())
    recs = recipes()

    def value(item_id, stack=()):
        ns, item = item_id.split(":", 1)
        if ns == "minecraft":
            return vanilla(item)
        if item in raw:
            return raw[item]
        if item in stack or item not in recs:
            return DEFAULT_VANILLA
        inputs, essence, count = recs[item]
        total = sum(value(k, stack + (item,)) * n for k, n in inputs.items())
        raw[item] = (total * CRAFT_PREMIUM + essence * ESSENCE_UNIT) / count
        return raw[item]

    for item in recs:
        value("simplywings:" + item)
    wood = raw[WOOD_ANCHOR[0]]
    cores = {k: v for k, v in raw.items() if k.endswith("_core")}
    top = max(cores.values())
    b = math.log(TOP_CORE / WOOD_ANCHOR[1]) / math.log(top / wood)
    a = WOOD_ANCHOR[1] / wood ** b
    mapped = {k: a * v ** b for k, v in raw.items()}

    wing_rarity = {}
    for f in (DATA / "simplywings/wing").glob("*.json"):
        wing_rarity[f.stem + "_wings"] = load(f)["rarity"]
    groups = {"rare": [], "epic": [], "legendary": [], "mythic": []}
    for w, r in wing_rarity.items():
        if w in mapped and r in groups:
            groups[r].append(mapped[w])
    craftable = [v for k, v in mapped.items() if k in recs and k not in wing_rarity and k not in ("cloud_flask",)]
    a_mean = len(craftable) / sum(1 / v for v in craftable)
    avg = {r: sum(v) / len(v) for r, v in groups.items() if v}
    rest = RATES["A"] * a_mean + RATES["S"] * avg["rare"] + RATES["SS"] * avg["epic"] + RATES["SSS"] * avg["legendary"]
    b_stack = max(0.0, (EV_TARGET - rest) / (1 - sum(RATES.values())))
    ev = (1 - sum(RATES.values())) * b_stack + rest
    print(f"B pile value for the target: {b_stack:.4f} summons (config gacha.bStackValue)")
    print(f"curve: value = {a:.4g} * raw^{b:.3f} (raw acorn {wood:.3g}, top core {top:.3g} = {max(cores, key=cores.get)})")
    print(f"A weighted mean {a_mean:.3f}; wing pair means " + ", ".join(f"{r} {v:.2f}" for r, v in avg.items()))
    print(f"expected offering value of one pull: {ev:.3f} summons (target {EV_TARGET})")
    OUT.parent.mkdir(parents=True, exist_ok=True)
    out = {"simplywings:" + k: round(v, 5) for k, v in sorted(mapped.items())}
    OUT.write_text(json.dumps(out, indent=2) + "\n", encoding="utf-8", newline="\n")
    print(f"{len(out)} values written to {OUT.relative_to(ROOT)}")
    for k in ("acorn", "crow_feather", "dragon_scale", "iron_harness", "fury_saddle", "bat_core", "seraph_core",
              "bat_wings", "fire_wings", "seraph_wings", "night_wings"):
        if k in mapped:
            print(f"  {k}: {mapped[k]:.3f}")


if __name__ == "__main__":
    main()
