import json
import shutil
from collections import OrderedDict
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
DATA = ROOT / "src/main/resources/data"
LANG = ROOT / "src/main/resources/assets/simplywings/lang/en_us.json"

NIGHT = {"condition": "minecraft:time_check", "value": {"min": 13000, "max": 23000}, "period": 24000}
THUNDER = {"condition": "minecraft:weather_check", "thundering": True}
SNEAK = {"condition": "minecraft:entity_properties", "entity": "attacker", "predicate": {"flags": {"is_sneaking": True}}}
SPRINT = {"condition": "minecraft:entity_properties", "entity": "attacker", "predicate": {"flags": {"is_sprinting": True}}}
CHARGED = {"condition": "minecraft:entity_properties", "entity": "this", "predicate": {"nbt": "{powered:1b}"}}


def y(lo=None, hi=None):
    bounds = {}
    if lo is not None:
        bounds["min"] = lo
    if hi is not None:
        bounds["max"] = hi
    return {"condition": "minecraft:location_check", "predicate": {"position": {"y": bounds}}}


def biomes(value):
    return {"condition": "minecraft:location_check", "predicate": {"biomes": value}}


def structures(value):
    return {"condition": "minecraft:location_check", "predicate": {"structures": value}}


def dark(level):
    return {"condition": "minecraft:location_check", "predicate": {"light": {"light": {"max": level}}}}


def state(block, props):
    return {"condition": "minecraft:block_state_property", "block": block, "properties": props}


def S(kind, targets, chance, conditions=(), note=None, lo=1, hi=1, key=None):
    return dict(kind=kind, targets=targets, chance=chance, conditions=list(conditions), note=note, min=lo, max=hi, key=key)


AGED_COPPER = [f"minecraft:{p}{b}" for p in ("weathered_", "oxidized_", "waxed_weathered_", "waxed_oxidized_")
               for b in ("copper", "cut_copper", "cut_copper_stairs", "cut_copper_slab", "chiseled_copper", "copper_grate",
                         "copper_bulb", "copper_door", "copper_trapdoor")]

SOURCES = {
    "bat_membrane": [S("mob", ["minecraft:bat"], 0.5)],
    "crow_feather": [S("block", ["minecraft:wheat"], 0.1, [state("minecraft:wheat", {"age": "7"})], "Fully grown wheat")],
    "hushed_down": [S("mob", ["minecraft:rabbit"], 0.5, [NIGHT, SNEAK], "At night, while sneaking")],
    "gull_feather": [S("fish", [], 0.2, [biomes(["minecraft:beach", "minecraft:snowy_beach", "minecraft:stony_shore"])], "Fishing from a beach")],
    "nectar_drop": [S("block", ["#minecraft:small_flowers"], 0.15)],
    "raptor_talon": [S("mob", ["minecraft:goat"], 0.35, [y(120)], "Above Y 120")],
    "updraft_shard": [S("block", ["#minecraft:base_stone_overworld"], 0.08, [y(150)], "Above Y 150")],
    "swift_quill": [S("mob", ["minecraft:chicken"], 0.3, [SPRINT], "While sprinting")],
    "homing_feather": [S("mob", ["minecraft:chicken"], 0.35, [structures("#minecraft:village")], "Inside a village")],
    "peacock_plume": [S("mob", ["minecraft:chicken"], 0.35, [biomes("#minecraft:is_jungle")], "In a jungle")],
    "macaw_plume": [S("block", ["minecraft:jungle_leaves"], 0.05)],
    "royal_jelly": [S("block", ["minecraft:bee_nest"], 1.0, [state("minecraft:bee_nest", {"honey_level": "5"})], "Full of honey"),
                    S("block", ["minecraft:beehive"], 1.0, [state("minecraft:beehive", {"honey_level": "5"})], "Full of honey", key="royal_jelly_hive")],
    "moth_dust": [S("block", ["minecraft:torch", "minecraft:wall_torch", "minecraft:lantern"], 0.25, [NIGHT], "At night")],
    "chitin_shard": [S("mob", ["minecraft:silverfish"], 0.25)],
    "acorn": [S("block", ["minecraft:oak_leaves", "minecraft:dark_oak_leaves"], 0.05)],
    "fin_ray": [S("fish", [], 0.2, [biomes(["minecraft:warm_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean"])], "Fishing in a warm ocean")],
    "phantom_sinew": [S("mob", ["minecraft:phantom"], 0.5)],
    "resonant_shard": [S("block", ["minecraft:amethyst_cluster"], 0.3)],
    "verdigris_flake": [S("block", ["#simplywings:aged_copper"], 0.25, note="Weathered or oxidized copper")],
    "fossil_fragment": [S("block", ["minecraft:bone_block"], 0.25, [y(None, 40)], "Below Y 40")],
    "raw_resin": [S("block", ["minecraft:spruce_log"], 0.12, [biomes(["minecraft:old_growth_pine_taiga", "minecraft:old_growth_spruce_taiga"])], "In an old growth taiga")],
    "ember_wisp": [S("mob", ["minecraft:blaze"], 0.3)],
    "tide_pearl": [S("mob", ["minecraft:guardian"], 0.2),
                   S("mob", ["minecraft:elder_guardian"], 1.0, lo=3, hi=3, key="tide_pearl_elder")],
    "living_rock": [S("block", ["minecraft:deepslate", "minecraft:tuff"], 0.05, [y(None, 0)], "Below Y 0")],
    "gust_mote": [S("mob", ["minecraft:breeze"], 0.4)],
    "thunder_spark": [S("mob", ["minecraft:creeper"], 0.5, [THUNDER], "During a thunderstorm"),
                      S("mob", ["minecraft:creeper"], 1.0, [CHARGED], "Charged creeper", 2, 2, key="thunder_spark_charged")],
    "polar_glimmer": [S("block", ["minecraft:ice", "minecraft:packed_ice", "minecraft:blue_ice"], 0.15, [NIGHT, biomes("#c:is_snowy")], "At night, in a snowy biome")],
    "shade_wisp": [S("mob", ["minecraft:enderman"], 0.25, [dark(7)], "In the dark (light 7 or less)")],
    "stardust": [S("mob", ["minecraft:shulker"], 0.35)],
    "blossom_spirit": [S("block", ["minecraft:cherry_leaves"], 0.06)],
    "heavenly_down": [S("mob", ["minecraft:chicken"], 0.6, [y(186, 204)], "In the cloud layer (Y 186 to 204)")],
    "vex_wing": [S("mob", ["minecraft:vex"], 0.4)],
    "soul_wisp": [S("block", ["minecraft:soul_sand", "minecraft:soul_soil"], 0.08, [biomes(["minecraft:soul_sand_valley"])], "In a soul sand valley")],
    "echo_resonance": [S("block", ["minecraft:sculk_shrieker"], 0.5)],
    "dragon_scale": [S("mob", ["minecraft:ender_dragon"], 1.0, lo=12, hi=12)],
    "supple_hide": [S("mob", ["minecraft:cow", "minecraft:mooshroom", "minecraft:horse", "minecraft:donkey", "minecraft:llama"], 0.35)],
    "silk_thread": [S("mob", ["minecraft:spider", "minecraft:cave_spider"], 0.4)],
    "hollow_bone": [S("mob", ["minecraft:skeleton", "minecraft:stray", "minecraft:bogged"], 0.35)],
    "elastic_gel": [S("mob", ["minecraft:slime", "minecraft:magma_cube"], 0.4)],
    "pristine_feather": [S("mob", ["minecraft:chicken"], 0.25)],
    "fleece_tuft": [S("mob", ["minecraft:sheep"], 0.4)],
    "abyssal_ink": [S("mob", ["minecraft:squid", "minecraft:glow_squid"], 0.4)],
    "ghast_wisp": [S("mob", ["minecraft:ghast"], 0.5)],
    "sky_iron": [S("block", ["#minecraft:iron_ores"], 0.25)],
    "copper_spark": [S("block", ["#minecraft:copper_ores"], 0.25)],
    "gilded_vein": [S("block", ["#minecraft:gold_ores"], 0.3)],
    "redstone_pulse": [S("block", ["#minecraft:redstone_ores"], 0.3)],
    "lapis_glint": [S("block", ["#minecraft:lapis_ores"], 0.3)],
    "emerald_sliver": [S("block", ["#minecraft:emerald_ores"], 0.6)],
    "soot_flake": [S("block", ["#minecraft:coal_ores"], 0.2)],
    "prism_shard": [S("block", ["#minecraft:diamond_ores"], 0.5)],
    "pale_quartz": [S("block", ["minecraft:nether_quartz_ore"], 0.25)],
    "ancient_ember": [S("block", ["minecraft:ancient_debris"], 0.6)],
    "void_grit": [S("block", ["minecraft:end_stone"], 0.08)],
    "glimmer_spore": [S("block", ["minecraft:glow_lichen"], 0.3)],
    "glimmering_scale": [S("fish", [], 0.1)],
}


def write(drop_ids):
    out = DATA / "simplywings/simplywings/loot_source"
    if out.exists():
        shutil.rmtree(out)
    out.mkdir(parents=True)
    lang = json.loads(LANG.read_text(encoding="utf-8"), object_pairs_hook=OrderedDict)
    missing = [d for d in drop_ids if d not in SOURCES]
    for item, entries in SOURCES.items():
        for e in entries:
            key = e["key"] or item
            data = {"kind": e["kind"], "item": f"simplywings:{item}", "chance": e["chance"]}
            if e["targets"]:
                data["targets"] = e["targets"]
            if e["min"] != 1 or e["max"] != 1:
                data["min"], data["max"] = e["min"], e["max"]
            if e["conditions"]:
                data["conditions"] = e["conditions"]
            if e["note"]:
                data["note"] = f"loot.simplywings.{key}"
                lang[f"loot.simplywings.{key}"] = e["note"]
            (out / f"{key}.json").write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8", newline="\n")
    LANG.write_text(json.dumps(lang, indent=2, ensure_ascii=False) + "\n", encoding="utf-8", newline="\n")
    tag = DATA / "simplywings/tags/block/aged_copper.json"
    tag.parent.mkdir(parents=True, exist_ok=True)
    tag.write_text(json.dumps({"values": AGED_COPPER}, indent=2) + "\n", encoding="utf-8", newline="\n")
    glm = DATA / "simplywings/loot_modifiers/winged_drops.json"
    glm.parent.mkdir(parents=True, exist_ok=True)
    glm.write_text(json.dumps({"type": "simplywings:winged_drops", "conditions": []}, indent=2) + "\n", encoding="utf-8", newline="\n")
    index = DATA / "neoforge/loot_modifiers/global_loot_modifiers.json"
    index.parent.mkdir(parents=True, exist_ok=True)
    index.write_text(json.dumps({"replace": False, "entries": ["simplywings:winged_drops"]}, indent=2) + "\n", encoding="utf-8", newline="\n")
    return missing
