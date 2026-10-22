import re
import subprocess
import sys
from pathlib import Path

sys.stdout.reconfigure(encoding="utf-8")
root = Path(__file__).resolve().parents[2]
SEND = [sys.executable, "-I", str(root / "tools" / "studio" / "send.py")]
ENERGY = "[simplywings:winged_energy={}]"


def send(*cmds):
    out = subprocess.run(SEND + [";".join(cmds)], capture_output=True, text=True, encoding="utf-8").stdout
    return out.splitlines()


def parse_counts(lines, prefix):
    counts = {}
    for line in lines:
        if line.startswith("drops "):
            for k, v in re.findall(r"([a-z_]+:[a-z_]+)=(\d+)", line):
                counts[k] = counts.get(k, 0) + int(v)
        if line.startswith("inv") and prefix == "inv":
            for k, v in re.findall(r"=([a-z_]+:[a-z_]+)x(\d+)", line):
                counts[k] = counts.get(k, 0) + int(v)
    return counts


def locate(kind, what, dim=None):
    pre = f"execute in {dim} run " if dim else ""
    lines = send(f"cmd {pre}locate {kind} {what}", "wait 60")
    for line in lines:
        m = re.search(r"at \[(-?\d+), (~|-?\d+), (-?\d+)\]", line)
        if m:
            y = None if m.group(2) == "~" else int(m.group(2))
            return int(m.group(1)), y, int(m.group(3))
    raise RuntimeError(f"locate {what} failed: {lines}")


CENTER = ["minecraft:overworld", 0, 140, 0]


def surface(x, z, dim="minecraft:overworld"):
    lines = send(f"cmd execute in {dim} run forceload add {x - 16} {z - 16} {x + 16} {z + 16}", "wait 60",
                 f"cmd execute in {dim} positioned {x} 0 {z} positioned over motion_blocking_no_leaves run tp @s ~ ~ ~ 0 0",
                 "wait 10", "status")
    for line in lines:
        m = re.search(r"status pos=(-?[\d.]+),(-?[\d.]+),(-?[\d.]+)", line)
        if m:
            return int(float(m.group(2)))
    raise RuntimeError(f"surface {x} {z} failed: {lines}")


def biome_ok(x, y, z, biome):
    lines = send(f"cmd execute if biome {x} {y} {z} {biome}", "wait 10")
    return any("Test passed" in l for l in lines)


def arena(x, y, z, dim="minecraft:overworld"):
    CENTER[:] = [dim, x, y, z]
    send(f"cmd execute in {dim} run forceload add {x - 16} {z - 16} {x + 16} {z + 16}", "wait 10",
         f"cmd execute in {dim} run fill {x - 9} {y - 1} {z - 9} {x + 9} {y - 1} {z + 9} minecraft:glass",
         f"cmd execute in {dim} run fill {x - 9} {y} {z - 9} {x + 9} {y + 5} {z + 9} minecraft:air",
         f"cmd execute in {dim} run tp @s {x} {y} {z} 0 0", "wait 20")


def hold(item, energy):
    return [f"cmd item replace entity @s weapon.mainhand with {item}{ENERGY if energy else ''}", "select 0", "wait 2"]


def mob_trials(entity, n, energy, nbt="", extra=None, pre=None):
    d, cx, cy, cz = CENTER
    cmds = [f"cmd execute in {d} run tp @s {cx} {cy} {cz} 0 0", f"cmd kill @e[type={entity}]", "cmd kill @e[type=item]", "cmd clear @s", "cmd difficulty normal", "wait 2"] + hold("minecraft:netherite_sword", energy)
    tag = "{NoAI:1b,Health:1f,Silent:1b" + ("," + nbt if nbt else "") + "}"
    for _ in range(n):
        cmds += (pre or []) + [f"cmd execute in {d} run tp @s {cx} {cy} {cz} 0 0", f"cmd summon {entity} ~ ~ ~4 {tag}", "wait 3", f"attack 5 {entity}", "wait 3", f"attack 5 {entity}", "wait 2"] + (extra or [])
    cmds += ["wait 10", "drops 12", f"cmd execute if entity @e[type={entity},distance=..16]", "wait 2", f"cmd kill @e[type={entity}]", "cmd kill @e[type=item]", "cmd kill @e[type=experience_orb]"]
    lines = send(*cmds)
    counts = parse_counts(lines, "drops")
    left = 0
    for line in lines:
        m = re.search(r"Test passed, count: (\d+)", line)
        if m:
            left = int(m.group(1))
    counts["_killed"] = max(0, n - left)
    return counts


def block_trials(block, n, energy, pos, tool="minecraft:netherite_pickaxe", fish=False, dim="minecraft:overworld"):
    x, y, z = pos
    cmds = ["cmd clear @s"] + hold(tool, energy)
    for _ in range(n):
        if fish:
            cmds.append(f"cmd execute in {dim} run loot give @s fish minecraft:gameplay/fishing {x} {y} {z} mainhand")
        else:
            cmds += [f"cmd execute in {dim} run setblock {x} {y} {z} {block}",
                     f"cmd execute in {dim} run loot give @s mine {x} {y} {z} mainhand"]
    cmds += ["wait 4", "inv"]
    counts = parse_counts(send(*cmds), "inv")
    send(f"cmd execute in {dim} run setblock {x} {y} {z} minecraft:air")
    return counts


results = []


def check(name, item, counts, n, chance, per=1, expect=True):
    n = max(1, counts.get("_killed", n))
    got = counts.get(item, 0)
    rate = got / (n * per)
    if expect:
        ok = got > 0 and (chance >= 1 and rate == 1 or chance < 1 and abs(rate - chance) < max(0.2, 3 * (chance * (1 - chance) / n) ** 0.5))
    else:
        ok = got == 0
    line = f"{'OK ' if ok else 'BAD'} {name}: {item} {got}/{n * per} ({rate:.0%}, expected {chance:.0%}{'' if expect else ' -> 0'})"
    print(line, flush=True)
    results.append(line)
    return ok


def main(only):
    send("end", "screen close", "cmd gamemode creative", "cmd gamerule doMobLoot true", "cmd gamerule doDaylightCycle false",
         "cmd weather clear", "cmd time set 6000", "cmd difficulty normal", "cmd gamerule mobGriefing false")
    sky = (0, 140, 0)
    arena(*sky)
    N = 24

    def want(name):
        return not only or name in only

    mobs = [
        ("bat_membrane", "minecraft:bat", 0.5), ("chitin_shard", "minecraft:silverfish", 0.25),
        ("elastic_gel", "minecraft:slime", 0.4, "Size:0"), ("elastic_gel_magma", "minecraft:magma_cube", 0.4, "Size:0"),
        ("ember_wisp", "minecraft:blaze", 0.3), ("fleece_tuft", "minecraft:sheep", 0.4),
        ("ghast_wisp", "minecraft:ghast", 0.5), ("gust_mote", "minecraft:breeze", 0.4),
        ("hollow_bone", "minecraft:skeleton", 0.35), ("hollow_bone_stray", "minecraft:stray", 0.35),
        ("hollow_bone_bogged", "minecraft:bogged", 0.35), ("phantom_sinew", "minecraft:phantom", 0.5),
        ("pristine_feather", "minecraft:chicken", 0.25), ("silk_thread", "minecraft:spider", 0.4),
        ("silk_thread_cave", "minecraft:cave_spider", 0.4), ("stardust", "minecraft:shulker", 0.35),
        ("supple_hide", "minecraft:cow", 0.35), ("supple_hide_mooshroom", "minecraft:mooshroom", 0.35),
        ("supple_hide_horse", "minecraft:horse", 0.35), ("supple_hide_donkey", "minecraft:donkey", 0.35),
        ("supple_hide_llama", "minecraft:llama", 0.35), ("tide_pearl", "minecraft:guardian", 0.2),
        ("vex_wing", "minecraft:vex", 0.4), ("abyssal_ink", "minecraft:squid", 0.4),
        ("abyssal_ink_glow", "minecraft:glow_squid", 0.4), ("raptor_talon", "minecraft:goat", 0.35),
    ]
    for m in mobs:
        name, entity, chance = m[0], m[1], m[2]
        nbt = m[3] if len(m) > 3 else ""
        item = "simplywings:" + re.sub(r"_(magma|stray|bogged|cave|mooshroom|horse|donkey|llama|glow)$", "", name)
        if not want(name):
            continue
        check(name, item, mob_trials(entity, N, True, nbt), N, chance)
        check(name + " (plain sword)", item, mob_trials(entity, 8, False, nbt), 8, chance, expect=False)

    if want("tide_pearl_elder"):
        check("tide_pearl_elder", "simplywings:tide_pearl", mob_trials("minecraft:elder_guardian", 8, True), 8, 1, per=3)
    if want("thunder_spark"):
        send("cmd weather clear")
        check("thunder_spark (clear)", "simplywings:thunder_spark", mob_trials("minecraft:creeper", 10, True), 10, 0.5, expect=False)
        check("thunder_spark_charged", "simplywings:thunder_spark", mob_trials("minecraft:creeper", 6, True, "powered:1b"), 6, 1, per=2)
        send("cmd weather thunder")
        check("thunder_spark (thunder)", "simplywings:thunder_spark", mob_trials("minecraft:creeper", N, True), N, 0.5)
        send("cmd weather clear")
    if want("hushed_down"):
        send("cmd time set 6000")
        check("hushed_down (day, sneaking)", "simplywings:hushed_down",
              mob_trials("minecraft:rabbit", 10, True, pre=["key sneak 8"]), 10, 0.5, expect=False)
        send("cmd time set 18000")
        check("hushed_down (night, standing)", "simplywings:hushed_down", mob_trials("minecraft:rabbit", 10, True), 10, 0.5, expect=False)
        check("hushed_down (night, sneaking)", "simplywings:hushed_down",
              mob_trials("minecraft:rabbit", N, True, pre=["key sneak 8"]), N, 0.5)
        send("cmd time set 6000")
    if want("shade_wisp"):
        send("cmd time set 6000")
        check("shade_wisp (day)", "simplywings:shade_wisp", mob_trials("minecraft:enderman", 10, True), 10, 0.25, expect=False)
        send("cmd time set 18000")
        check("shade_wisp (night)", "simplywings:shade_wisp", mob_trials("minecraft:enderman", N, True), N, 0.25)
        send("cmd time set 6000")
    if want("swift_quill"):
        check("swift_quill (walking)", "simplywings:swift_quill", mob_trials("minecraft:chicken", 10, True), 10, 0.3, expect=False)
        check("swift_quill (sprinting)", "simplywings:swift_quill",
              mob_trials("minecraft:chicken", N, True, pre=["key forward 6", "key sprint 6"]), N, 0.3)
        arena(*sky)
    if want("heavenly_down"):
        arena(0, 195, 0)
        check("heavenly_down (y195)", "simplywings:heavenly_down", mob_trials("minecraft:chicken", N, True), N, 0.6)
        arena(*sky)
        check("heavenly_down (y140)", "simplywings:heavenly_down", mob_trials("minecraft:chicken", 10, True), 10, 0.6, expect=False)
    if want("raptor_talon_low"):
        arena(0, 100, 0)
        check("raptor_talon (y100)", "simplywings:raptor_talon", mob_trials("minecraft:goat", 10, True), 10, 0.35, expect=False)
        arena(*sky)
    if want("peacock_plume"):
        x, y, z = locate("biome", "#minecraft:is_jungle")
        arena(x, surface(x, z) + 1, z)
        print("jungle biome at arena:", biome_ok(x, CENTER[2], z, "#minecraft:is_jungle"))
        check("peacock_plume (jungle)", "simplywings:peacock_plume", mob_trials("minecraft:chicken", N, True), N, 0.35)
        arena(*sky)
        check("peacock_plume (elsewhere)", "simplywings:peacock_plume", mob_trials("minecraft:chicken", 10, True), 10, 0.35, expect=False)
    if want("homing_feather"):
        x, _, z = locate("structure", "#minecraft:village")
        arena(x, surface(x, z) + 1, z)
        check("homing_feather (village)", "simplywings:homing_feather", mob_trials("minecraft:chicken", N, True), N, 0.35)
        arena(*sky)
        check("homing_feather (elsewhere)", "simplywings:homing_feather", mob_trials("minecraft:chicken", 10, True), 10, 0.35, expect=False)
    if want("dragon_scale"):
        check("dragon_scale", "simplywings:dragon_scale", mob_trials("minecraft:ender_dragon", 1, True, extra=["wait 10"]), 1, 1, per=12)

    B = (2, 140, 3)
    blocks = [
        ("acorn", "minecraft:oak_leaves", 0.05), ("acorn_dark", "minecraft:dark_oak_leaves", 0.05),
        ("ancient_ember", "minecraft:ancient_debris", 0.6), ("blossom_spirit", "minecraft:cherry_leaves", 0.06),
        ("copper_spark", "minecraft:copper_ore", 0.25), ("copper_spark_deep", "minecraft:deepslate_copper_ore", 0.25),
        ("echo_resonance", "minecraft:sculk_shrieker", 0.5), ("emerald_sliver", "minecraft:emerald_ore", 0.6),
        ("gilded_vein", "minecraft:gold_ore", 0.3), ("gilded_vein_nether", "minecraft:nether_gold_ore", 0.3),
        ("glimmer_spore", "minecraft:glow_lichen", 0.3), ("lapis_glint", "minecraft:lapis_ore", 0.3),
        ("macaw_plume", "minecraft:jungle_leaves", 0.05), ("nectar_drop", "minecraft:poppy", 0.15),
        ("nectar_drop_tulip", "minecraft:orange_tulip", 0.15), ("pale_quartz", "minecraft:nether_quartz_ore", 0.25),
        ("prism_shard", "minecraft:diamond_ore", 0.5), ("redstone_pulse", "minecraft:redstone_ore", 0.3),
        ("resonant_shard", "minecraft:amethyst_cluster", 0.3), ("sky_iron", "minecraft:iron_ore", 0.25),
        ("sky_iron_deep", "minecraft:deepslate_iron_ore", 0.25), ("soot_flake", "minecraft:coal_ore", 0.2),
        ("verdigris_flake", "minecraft:weathered_copper", 0.25), ("verdigris_flake_oxidized", "minecraft:oxidized_copper", 0.25),
        ("void_grit", "minecraft:end_stone", 0.08),
        ("royal_jelly", "minecraft:bee_nest[honey_level=5]", 1.0), ("royal_jelly_hive", "minecraft:beehive[honey_level=5]", 1.0),
        ("crow_feather", "minecraft:wheat[age=7]", 0.1),
    ]
    NB = 60
    for name, block, chance in blocks:
        if not want(name):
            continue
        item = "simplywings:" + re.sub(r"_(dark|deep|nether|tulip|oxidized|hive)$", "", name)
        tool = "minecraft:shears" if "leaves" in block else "minecraft:netherite_pickaxe"
        check(name, item, block_trials(block, NB, True, B, tool), NB, chance)
        check(name + " (plain tool)", item, block_trials(block, 12, False, B, tool), 12, chance, expect=False)
    if want("crow_feather"):
        check("crow_feather (young wheat)", "simplywings:crow_feather", block_trials("minecraft:wheat[age=3]", 30, True, B), 30, 0.1, expect=False)
    if want("verdigris_flake"):
        check("verdigris_flake (new copper)", "simplywings:verdigris_flake", block_trials("minecraft:copper_block", 30, True, B), 30, 0.25, expect=False)
    if want("royal_jelly"):
        check("royal_jelly (honey 2)", "simplywings:royal_jelly", block_trials("minecraft:bee_nest[honey_level=2]", 10, True, B), 10, 1, expect=False)
    if want("moth_dust"):
        send("cmd time set 6000")
        check("moth_dust (day)", "simplywings:moth_dust", block_trials("minecraft:torch", 30, True, B), 30, 0.25, expect=False)
        send("cmd time set 18000")
        for b in ("minecraft:torch", "minecraft:lantern"):
            check(f"moth_dust (night, {b})", "simplywings:moth_dust", block_trials(b, NB, True, B), NB, 0.25)
        send("cmd time set 6000")
    if want("updraft_shard"):
        check("updraft_shard (y140)", "simplywings:updraft_shard", block_trials("minecraft:stone", 40, True, B), 40, 0.08, expect=False)
        check("updraft_shard (y160)", "simplywings:updraft_shard", block_trials("minecraft:stone", 150, True, (2, 160, 3)), 150, 0.08)
        check("updraft_shard (andesite y160)", "simplywings:updraft_shard", block_trials("minecraft:andesite", 150, True, (2, 160, 3)), 150, 0.08)
    if want("fossil_fragment"):
        check("fossil_fragment (y140)", "simplywings:fossil_fragment", block_trials("minecraft:bone_block", 30, True, B), 30, 0.25, expect=False)
        check("fossil_fragment (y30)", "simplywings:fossil_fragment", block_trials("minecraft:bone_block", NB, True, (2, 30, 3)), NB, 0.25)
    if want("living_rock"):
        check("living_rock (y140)", "simplywings:living_rock", block_trials("minecraft:deepslate", 60, True, B), 60, 0.05, expect=False)
        check("living_rock (y-20)", "simplywings:living_rock", block_trials("minecraft:deepslate", 200, True, (2, -20, 3)), 200, 0.05)
        check("living_rock tuff (y-20)", "simplywings:living_rock", block_trials("minecraft:tuff", 200, True, (2, -20, 3)), 200, 0.05)
    if want("raw_resin"):
        x, y, z = locate("biome", "minecraft:old_growth_spruce_taiga")
        send(f"cmd forceload add {x} {z}", "wait 80")
        check("raw_resin (old growth)", "simplywings:raw_resin", block_trials("minecraft:spruce_log", 150, True, (x, 80, z)), 150, 0.12)
        check("raw_resin (elsewhere)", "simplywings:raw_resin", block_trials("minecraft:spruce_log", 40, True, B), 40, 0.12, expect=False)
    if want("polar_glimmer"):
        x, y, z = 40, 140, 40
        send(f"cmd forceload add {x - 8} {z - 8} {x + 8} {z + 8}", "wait 20",
             f"cmd fillbiome {x - 4} {y - 4} {z - 4} {x + 4} {y + 4} {z + 4} minecraft:snowy_plains", "wait 5")
        print("snowy biome at test pos:", biome_ok(x, y, z, "#c:is_snowy"))
        send("cmd time set 18000")
        check("polar_glimmer (night snowy)", "simplywings:polar_glimmer", block_trials("minecraft:ice", NB, True, (x, y, z)), NB, 0.15)
        check("polar_glimmer (night elsewhere)", "simplywings:polar_glimmer", block_trials("minecraft:ice", 20, True, B), 20, 0.15, expect=False)
        send("cmd time set 6000")
        check("polar_glimmer (day snowy)", "simplywings:polar_glimmer", block_trials("minecraft:ice", 20, True, (x, y, z)), 20, 0.15, expect=False)
    if want("soul_wisp"):
        x, y, z = locate("biome", "minecraft:soul_sand_valley", "minecraft:the_nether")
        send(f"cmd execute in minecraft:the_nether run forceload add {x} {z}", "wait 20")
        check("soul_wisp (valley)", "simplywings:soul_wisp", block_trials("minecraft:soul_sand", 150, True, (x, y, z), "minecraft:netherite_shovel", dim="minecraft:the_nether"), 150, 0.08)
        check("soul_wisp (overworld)", "simplywings:soul_wisp", block_trials("minecraft:soul_sand", 40, True, B, "minecraft:netherite_shovel"), 40, 0.08, expect=False)
    if want("fishing"):
        check("glimmering_scale (fishing)", "simplywings:glimmering_scale", block_trials(None, 150, True, B, "minecraft:fishing_rod", fish=True), 150, 0.1)
        check("glimmering_scale (plain rod)", "simplywings:glimmering_scale", block_trials(None, 40, False, B, "minecraft:fishing_rod", fish=True), 40, 0.1, expect=False)
        x, y, z = locate("biome", "minecraft:beach")
        send(f"cmd forceload add {x} {z}", "wait 80")
        check("gull_feather (beach)", "simplywings:gull_feather", block_trials(None, 100, True, (x, 63, z), "minecraft:fishing_rod", fish=True), 100, 0.2)
        check("gull_feather (elsewhere)", "simplywings:gull_feather", block_trials(None, 40, True, B, "minecraft:fishing_rod", fish=True), 40, 0.2, expect=False)
        x, y, z = locate("biome", "minecraft:warm_ocean")
        send(f"cmd forceload add {x} {z}", "wait 80")
        check("fin_ray (warm ocean)", "simplywings:fin_ray", block_trials(None, 100, True, (x, 62, z), "minecraft:fishing_rod", fish=True), 100, 0.2)
        check("fin_ray (elsewhere)", "simplywings:fin_ray", block_trials(None, 40, True, B, "minecraft:fishing_rod", fish=True), 40, 0.2, expect=False)
    if want("explosion"):
        send("cmd clear @s", "cmd kill @e[type=item]", "cmd gamerule mobGriefing true")
        cmds = []
        for i in range(10):
            cmds += [f"cmd setblock {2 + i % 3} 140 {5 + i // 3} minecraft:iron_ore"]
        cmds += ["cmd summon minecraft:tnt 3 140 7 {fuse:1}", "wait 30", "drops 12", "cmd kill @e[type=item]", "cmd gamerule mobGriefing false"]
        check("explosion (tnt on iron ore)", "simplywings:sky_iron", parse_counts(send(*cmds), "drops"), 10, 0.25, expect=False)

    bad = [r for r in results if r.startswith("BAD")]
    print(f"\n{len(results) - len(bad)}/{len(results)} OK")
    for r in bad:
        print(r)


main(sys.argv[1:])
