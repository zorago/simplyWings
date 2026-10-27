import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CATALOG = ROOT / "tools/sounds/catalog.json"
WINGS = ROOT / "src/main/resources/data/simplywings/simplywings/wing"

FEATHER = "oga_wings_flap_large/wings_flap_large.flac"
CAPE = "fs_711122_cape_wings.mp3"
CREATURE = "fs_759529_large_flying_creature.mp3"
UMBRELLA = "fs_593487_umbrella_bat_wings.mp3"
HUGE = "fs_670509_huge_wing_flaps.mp3"
INTERFACE = "kenney_interface-sounds/Audio/"
IMPACT = "kenney_impact-sounds/Audio/"
FURY = "user/night_fury_dive.mp3"
CASINO = "kenney_casino-audio/Audio/"
UI_AUDIO = "kenney_ui-audio/Audio/"


def clip(source, start=0.0, end=None, peak=-3.0, fade_in=0.004, fade_out=0.25, echo=0.0, loop=0.0, tempo=1.0, pitch=1.0, lp=0):
    return {"source": source, "start": start, "end": end, "peak": peak, "fade_in": fade_in, "fade_out": fade_out, "echo": echo,
            "loop": loop, "tempo": tempo, "pitch": pitch, "lp": lp, "gain_db": 0.0}


FILES = {
    "wing/feather/flap": [clip(CAPE, 22.16, 22.75), clip(CAPE, 25.94, 26.5), clip(CAPE, 29.39, 30.0)],
    "wing/membrane/flap": [clip(CREATURE, s - 0.04, s + 0.75, -2.0) for s in (2.28, 3.39, 4.49, 5.54, 6.55)],
    "wing/leather/flap": [clip(UMBRELLA, s - 0.03, s + 0.32, -4.0, fade_out=0.15) for s in (1.81, 2.72, 3.59, 4.50)],
    "wing/insect/flap": [clip(f"synth:burr_{f}", 0, None, -8.0, fade_out=0.05) for f in (120, 140, 165)],
    "wing/strong/flap": [clip(HUGE, 2.42, 3.62, -1.5, fade_out=0.4), clip(HUGE, 3.72, 4.3, -1.5), clip(HUGE, 5.06, 5.6, -1.5)],
    "fury/dive": [clip(FURY, 0.0, 5.0, -1.0, fade_out=0.03, echo=3.0)],
    "fury/invisible": [clip(FURY, 0.0, None, -1.0, fade_out=0.05, echo=3.0)],
    "gacha/ignite": [clip(f"oga_magical_{n}.ogg", 0, None, -2.0, fade_out=0.3) for n in ("1_0", "2", "4", "5", "6_0", "7_0")],
    "gacha/offer": [clip("oga_magical_3.ogg", 0, None, -2.0, fade_out=0.4)],
    "gacha/rune_charge": [clip("fs_832552_crystal_ball.mp3", 1.0, 9.6, -3.0, fade_in=0.3, fade_out=2.5),
                          clip("fs_832552_crystal_ball.mp3", 9.2, 17.6, -3.0, fade_in=0.3, fade_out=2.5)],
    "gacha/beam": [clip("fs_859497_powerup_loop.mp3", 0, None, -2.0, fade_in=0.05, fade_out=0.8)],
    "gacha/shockwave": [clip("fs_814053_shockwave.mp3", 0, 6.95, -1.0, fade_out=2.0)],
    "gacha/blast": [clip("fs_814044_creation_magic_blast.mp3", 0, 6.6, -1.0, fade_out=2.0)],
    "gacha/implosion": [clip("fs_814047_fantasy_implosion.mp3", 0, 7.7, -1.0, fade_out=2.0)],
    "gacha/puff": [clip("fs_714257_puff_of_smoke.mp3", 0, None, -3.0, fade_out=0.15)],
    "gacha/bell": [clip(f"{IMPACT}impactBell_heavy_00{n}.ogg", 0, None, -2.0, fade_out=0.3) for n in range(5)],
    "gacha/chime": [clip(f"{IMPACT}impactGlass_medium_00{n}.ogg", 0, None, -4.0, fade_out=0.2) for n in range(5)],
    "gacha/twinkle": [clip(f"{IMPACT}impactGlass_light_00{n}.ogg", 0, None, -6.0, fade_out=0.1) for n in range(5)],
    "ui/card_deal": [clip(f"{CASINO}card-slide-{n}.ogg", 0, None, -8.0, fade_out=0.1, lp=6000) for n in range(1, 9)],
    "ui/card_flip": [clip(f"{CASINO}card-shove-{n}.ogg", 0, None, -6.0, fade_out=0.1, lp=6000) for n in range(1, 5)]
                    + [clip(f"{CASINO}card-fan-1.ogg", 0, None, -6.0, fade_out=0.15, lp=6000)],
    "ui/card_shuffle": [clip(f"{CASINO}card-shuffle.ogg", 0, None, -7.0, fade_out=0.3, lp=5000)],
    "ui/card_place": [clip(f"{CASINO}card-place-{n}.ogg", 0, None, -6.0, fade_out=0.1, lp=4000, pitch=0.85) for n in range(1, 5)],
    "ui/confirm": [clip(f"{INTERFACE}bong_001.ogg", 0, None, -9.0, fade_out=0.06, pitch=0.7, lp=1800),
                   clip(f"{UI_AUDIO}switch3.ogg", 0, None, -9.0, fade_out=0.04, pitch=0.7, lp=1800)],
    "ui/equip": [clip("fs_561287.mp3", 0.2, 4.4, -3.0, fade_out=1.0)],
    "ui/open": [clip(f"{INTERFACE}open_00{n}.ogg", 0, None, -10.0, fade_out=0.05, pitch=0.75, lp=2000) for n in (2, 3, 4)],
    "fury/night_charge": [clip("fs_814061.mp3", 0.0, 2.6, -2.0, fade_in=0.4, fade_out=0.4)],
    "fury/crackle": [clip("fs_582631.mp3", 0.08, 0.9, -4.0, fade_out=0.2)],
    "fury/light_charge": [clip("fs_407052.mp3", 0.4, 20.0, -2.0, fade_in=0.2, fade_out=0.3, tempo=8.0)],
    "flight/wind": [clip("fs_454072.mp3", 30.0, 46.0, -3.0, loop=1.5)],
    "flight/landing_hard": [clip("fs_504626.mp3", 0.05, 1.4, -2.0, fade_out=0.4)],
    "flight/landing_soft": [clip(f"{IMPACT}impactSoft_medium_00{n}.ogg", 0, None, -8.0, fade_out=0.1) for n in range(4)],
    "wing/buzz/loop": [clip("synth:buzz_loop", 0, None, -8.0, loop=-1)],
    "block/forge_bench/hammer": [clip("fs_434339.mp3", s - 0.03, s + 0.9, -3.0, fade_out=0.4) for s in (0.23, 4.17, 5.74, 12.16, 13.78, 18.69)],
    "block/forge_bench/quench": [clip("fs_234782.mp3", 0.05, None, -4.0, fade_out=0.5)],
    "block/cloud_compressor/loop": [clip("fs_487748.mp3", 0.0, 7.6, -4.0, loop=0.8)],
    "essence/pour": [clip("fs_875132.mp3", 1.6, 2.6, -5.0, fade_out=0.2), clip("fs_875132.mp3", 2.9, 3.95, -5.0, fade_out=0.2)],
    "essence/full": [clip("fs_511485.mp3", 0, None, -6.0, fade_out=0.3)],
    "ui/denied": [clip(f"{INTERFACE}back_00{n}.ogg", 0, None, -9.0, fade_out=0.04, pitch=0.6, lp=1500) for n in (2, 3)],
}

FAMILIES = {
    "feather": ("wing.feather.flap", 1.0, 1.0),
    "feather_matter": ("wing.feather.flap", 0.6, 1.1),
    "element": ("wing.feather.flap", 0.35, 1.15),
    "membrane": ("wing.membrane.flap", 1.0, 1.0),
    "leather": ("wing.leather.flap", 1.0, 1.0),
    "glider": ("wing.leather.flap", 0.5, 0.85),
    "buzz": ("wing.insect.flap", 0.8, 1.2),
    "soft": ("wing.feather.flap", 0.3, 1.3),
    "mechanism": ("minecraft:block.wooden_trapdoor.open", 0.6, 0.8),
}

PAIRS = {
    "common": ("feather", 1.0), "crow": ("feather", 0.95), "owl": ("feather", 0.9, 0.25), "gull": ("feather", 1.1),
    "eagle": ("feather", 0.85), "swift": ("feather", 1.25), "pigeon": ("feather", 1.15), "parrot": ("feather", 1.2),
    "peacock": ("feather", 0.95), "patchwork": ("feather", 1.05), "angel": ("feather", 0.9), "seraph": ("feather", 0.85),
    "yin_yang": ("feather", 1.0),
    "amethyst": ("feather_matter", 1.0), "copper": ("feather_matter", 0.95), "earth": ("feather_matter", 0.8),
    "fire": ("feather_matter", 1.0), "storm": ("feather_matter", 1.05), "shadow": ("feather_matter", 0.85),
    "nebula": ("feather_matter", 1.0),
    "water": ("element", 0.9), "air": ("element", 1.2), "aurora": ("element", 1.1), "forest": ("element", 0.95),
    "demon": ("membrane", 0.8), "ptero": ("membrane", 0.9), "phantom": ("membrane", 1.05), "night": ("membrane", 0.9),
    "light": ("membrane", 1.05), "bat": ("leather", 1.1),
    "squirrel": ("glider", 1.2), "flying_fish": ("glider", 1.1), "kite": ("glider", 0.9),
    "hummingbird": ("buzz", 1.6), "bee": ("buzz", 1.4), "beetle": ("buzz", 0.9), "ancient": ("buzz", 0.7),
    "moth": ("soft", 1.0), "psyche": ("soft", 1.15),
    "ornithopter": ("mechanism", 1.0), "daedalus": ("mechanism", 0.7),
}

STRONG_PITCH = {"membrane": 0.85, "leather": 1.2, "glider": 1.2, "buzz": 1.5, "soft": 1.4, "mechanism": 1.1}
STRONG_VOLUME = {"element": 0.5, "glider": 0.5, "buzz": 0.35, "soft": 0.3, "mechanism": 0.4}

LAYERS = {
    "feathers": [("minecraft:entity.parrot.fly", 0.4, 0.8)],
    "angel": [("minecraft:block.amethyst_block.chime", 0.5, 1.4)],
    "demon": [("minecraft:entity.blaze.shoot", 0.45, 0.55)],
    "seraph": [("minecraft:block.beacon.power_select", 0.35, 1.6)],
    "psyche": [("minecraft:particle.soul_escape", 0.6, 1.2)],
    "fire": [("minecraft:item.firecharge.use", 0.35, 1.3)],
    "water": [("minecraft:entity.boat.paddle_water", 0.5, 0.9)],
    "earth": [("minecraft:block.basalt.break", 0.6, 0.6)],
    "air": [("minecraft:entity.breeze.idle_air", 0.6, 1.2)],
    "storm": [("minecraft:entity.lightning_bolt.impact", 0.25, 1.8)],
    "aurora": [("minecraft:block.amethyst_block.resonate", 0.5, 1.2)],
    "shadow": [("minecraft:entity.enderman.teleport", 0.25, 0.6)],
    "nebula": [("minecraft:block.amethyst_cluster.break", 0.4, 1.5)],
    "forest": [("minecraft:block.azalea_leaves.break", 0.6, 0.9)],
    "amethyst": [("minecraft:block.amethyst_block.chime", 0.5, 1.0)],
    "copper": [("minecraft:block.copper.hit", 0.6, 0.8)],
    "yin_yang": [("minecraft:block.amethyst_block.chime", 0.5, 0.6), ("minecraft:block.amethyst_block.chime", 0.5, 1.7)],
    "ptero": [("minecraft:entity.phantom.flap", 0.7, 0.6)],
}
PAIR_EFFECT = {p: "feathers" for p in ("crow", "owl", "gull", "eagle", "swift", "pigeon", "peacock", "parrot")}
PAIR_EFFECT.update({p: p for p in ("angel", "demon", "seraph", "psyche", "fire", "water", "earth", "air", "storm", "aurora",
                                   "shadow", "nebula", "forest", "amethyst", "copper", "yin_yang", "ptero")})
PAIR_EFFECT["phantom"] = "ptero"

SUBTITLES = {
    "wing.flap": "Wings flap",
    "wing.flap_strong": "Wings beat",
    "flight.takeoff_launch": "Wings launch",
    "gacha.offer": "Offering burns",
    "gacha.ignite": "Altar flame ignites",
    "gacha.rune_charge": "Altar hums",
    "gacha.beam": "Pillar of light rises",
    "gacha.shockwave": "Sky shakes",
    "gacha.blast": "Star bursts",
    "gacha.implosion": "Magic collapses",
    "gacha.puff": "Cloud puffs",
    "gacha.bell": "Star chimes",
    "gacha.chime": "Crystal rings",
    "gacha.twinkle": "Star twinkles",
    "ui.card_deal": "Card dealt",
    "ui.card_flip": "Card turns",
    "ui.card_shuffle": "Cards shuffle",
    "ui.card_place": "Card laid down",
    "ui.confirm": "Card revealed",
    "ui.denied": "Not enough feathers",
    "ui.equip": "Wings unfold",
    "ui.open": "Menu opens",
    "wing.buzz": "Wings buzz",
    "flight.wind": "Wind rushes",
    "flight.landing_hard": "Heavy landing",
    "flight.landing_soft": "Landing",
    "block.forge_bench.hammer": "Hammer strikes",
    "block.forge_bench.quench": "Metal hisses",
    "block.cloud_compressor.loop": "Compressor churns",
    "essence.pour": "Essence pours",
    "essence.full": "Container fills up",
    "night_fury.charge": "Night Fury crackles",
    "light_fury.charge": "Light Fury charges plasma",
    "light_fury.blast": "Plasma blast",
    "night_fury.dive": "Night Fury shrieks",
    "light_fury.dive": "Light Fury shrieks",
    "night_fury.invisible": "Night Fury vanishes",
    "light_fury.invisible": "Light Fury vanishes",
}

INFO = {
    "wing.feather.flap": ("Coup d'aile en déplacement, frappe de WingStyle.strikeAt ; distants via FlapEvent", ["client/ClientFlight.java", "client/ClientNet.java", "client/WingSounds.java"],
                          "Descente de l'aile : poussée d'air ronde et courte, froissement de plumes discret.",
                          ["wing flap", "feather", "cape flap", "bird takeoff", "whoosh short"], "minecraft:entity.phantom.flap", "wing_flap"),
    "wing.membrane.flap": ("Coup d'aile des paires à membrane", ["client/WingSounds.java"],
                           "Membrane tendue qui claque, plus grave et plus sèche que la plume.",
                           ["leather wing flap", "dragon wing", "membrane flap", "large creature wings"], "minecraft:entity.ender_dragon.flap", "wing_flap"),
    "wing.leather.flap": ("Coup d'aile de la chauve-souris et des planeurs", ["client/WingSounds.java"],
                          "Petit claquement de toile souple, court.", ["bat wings", "umbrella flap", "cloth flap"], "minecraft:entity.bat.takeoff", "wing_flap"),
    "wing.insect.flap": ("Coup d'aile des paires à vrombissement", ["client/WingSounds.java"],
                         "Bref bourdonnement stylisé, pas de vrai insecte.", ["insect wing", "buzz short", "burr"], "minecraft:entity.bee.loop", "wing_flap"),
    "wing.strong.flap": ("Coup puissant : premier battement, saut ailé, envol", ["client/ClientFlight.java", "client/WingSounds.java"],
                         "Grand battement lourd, plein de souffle.", ["huge wing flap", "dragon wings", "large flap whoosh"], "minecraft:entity.ender_dragon.flap", "wing_flap_strong"),
    "wing.buzz.loop": ("Poussée continue des paires à vrombissement (FLAG_THRUST)", ["client/FlightLoops.java"],
                       "Bourdonnement synthétique de jeu vidéo, rond, sans aigu.", ["insect buzz loop", "hum loop", "drone wings"], "minecraft:entity.bee.loop", "buzz_loop"),
    "flight.wind": ("Vent de vol, volume piloté par la vitesse", ["client/FlightLoops.java"],
                    "Souffle continu sans motif reconnaissable.", ["wind loop", "air rush", "flying wind"], "minecraft:item.elytra.flying", "wind_loop"),
    "flight.takeoff_launch": ("Envol chargé (Alt maintenu)", ["client/SimplyWingsClient.java"], "Coup puissant un peu plus aigu.", [], None, None),
    "flight.landing_soft": ("Atterrissage lent", ["client/ClientFlight.java"], "Pose douce, petit bruit sourd.", ["soft landing", "thud soft", "footstep land"], None, "landing"),
    "flight.landing_hard": ("Atterrissage rapide et crash", ["client/ClientFlight.java", "flight/ServerFlight.java"],
                            "Choc lourd au sol, sans effet de dégâts.", ["heavy landing", "body fall", "thud heavy"], None, "landing"),
    "night_fury.dive": ("Cri de piqué de Night", ["client/FuryEffects.java"], "Cri de piqué qui monte (sample du film, privé).", ["dive scream", "jet whistle", "raptor screech"], None, "fury_big"),
    "light_fury.dive": ("Cri de piqué de Light", ["client/FuryEffects.java"], "Même cri que Night.", [], None, "fury_big"),
    "night_fury.invisible": ("Dash d'invisibilité de Night", ["client/FuryEffects.java"], "Sample complet avec écho (privé).", ["vanish whoosh", "jet pass"], None, "fury_big"),
    "light_fury.invisible": ("Dash d'invisibilité de Light", ["client/FuryEffects.java"], "Comme Night, un peu plus aigu.", [], None, "fury_big"),
    "night_fury.charge": ("Charge au frein de Night (2 s)", ["client/FuryEffects.java"], "Grésillement électrique qui monte.", ["electric charge", "plasma charge", "crackle rise"], None, "fury_big"),
    "night_fury.crackle": ("Grésillement pendant la charge de Night", ["client/FuryEffects.java"], "Craquement court.", ["electric crackle", "spark"], None, "fury_big"),
    "light_fury.charge": ("Charge au frein de Light", ["client/FuryEffects.java"], "Montée plasma claire.", ["plasma rise", "energy charge"], None, "fury_big"),
    "light_fury.blast": ("Dash de Light", ["client/FuryEffects.java"], "Explosion vanilla.", [], None, None),
    "gacha.offer": ("Offrande posée sur l'autel (début du show)", ["client/show/ClientShows.java"], "Souffle magique bref.", ["magic whoosh", "offering", "spell cast"], None, "gacha_small"),
    "gacha.ignite": ("Allumage d'une flamme élémentaire de l'autel", ["client/show/ClientShows.java", "block/AssemblyTableEffects.java"], "Embrasement magique court.", ["magic ignite", "fire whoosh magic"], None, "gacha_small"),
    "gacha.rune_charge": ("Charge des runes, retombée finale", ["client/show/ClientShows.java", "block/AssemblyTableEffects.java"], "Nappe cristalline qui monte.", ["crystal hum", "magic charge", "rising drone"], None, "gacha_small"),
    "gacha.beam": ("Pilier de lumière", ["client/show/ClientShows.java", "block/AssemblyTableEffects.java"], "Montée d'énergie.", ["power up", "beam rise", "energy"], None, "gacha_big"),
    "gacha.shockwave": ("Onde de choc (pilier, SSS)", ["client/show/ClientShows.java", "client/gui/CardDeck.java"], "Grondement d'onde de choc.", ["shockwave", "boom deep"], None, "gacha_big"),
    "gacha.blast": ("Éclat de l'étoile", ["client/show/ClientShows.java", "client/gui/CardDeck.java", "block/AssemblyTableEffects.java"], "Explosion magique.", ["magic blast", "explosion fantasy"], None, "gacha_big"),
    "gacha.implosion": ("Implosion (palier SS et plus)", ["client/show/ClientShows.java", "client/gui/CardDeck.java"], "Implosion magique.", ["implosion", "reverse boom"], None, "gacha_big"),
    "gacha.puff": ("Nuage qui éclate (vols des ailes)", ["client/show/ClientShows.java"], "Pouf de fumée.", ["puff smoke", "poof"], None, "gacha_small"),
    "gacha.bell": ("Battements de l'étoile, cartes S+", ["client/show/ClientShows.java", "client/gui/CardDeck.java", "block/AssemblyTableEffects.java"], "Cloche lourde.", ["bell heavy", "gong"], None, "gacha_small"),
    "gacha.chime": ("Battements de l'étoile", ["client/show/ClientShows.java"], "Verre qui sonne.", ["glass chime", "crystal ring"], None, "gacha_small"),
    "gacha.twinkle": ("Étoiles B et A du ×10", ["client/show/ClientShows.java"], "Petit éclat de verre.", ["glass light", "twinkle"], None, "gacha_small"),
    "ui.card_deal": ("Carte distribuée", ["client/gui/CardDeck.java"], "Glissement de carte.", ["card slide", "card deal"], "minecraft:ui.button.click", "cards"),
    "ui.card_flip": ("Carte retournée", ["client/gui/CardDeck.java"], "Carte poussée.", ["card flip", "card shove"], "minecraft:ui.button.click", "cards"),
    "ui.card_shuffle": ("Tension avant une carte S+", ["client/gui/CardDeck.java"], "Battement de cartes.", ["card shuffle"], "minecraft:ui.button.click", "cards"),
    "ui.card_place": ("Carte ouverte", ["client/gui/CardDeck.java"], "Carte posée.", ["card place", "card down"], "minecraft:ui.button.click", "cards"),
    "ui.confirm": ("Carte A révélée, table d'assemblage", ["client/gui/CardDeck.java", "block/AssemblyTableEffects.java"], "Bong grave et doux.", ["soft bong", "ui confirm low"], "minecraft:ui.button.click", "ui"),
    "ui.denied": ("Plumes insuffisantes à l'autel", ["client/gui/AltarScreen.java"], "Refus grave et doux.", ["ui back low", "denied soft"], "minecraft:ui.button.click", "ui"),
    "ui.equip": ("Équiper / retirer (menu J), infusion de l'établi, table d'assemblage", ["client/gui/WingsScreen.java", "block/ForgeBenchEffects.java", "block/AssemblyTableEffects.java"],
                 "Aujourd'hui 4,2 s et brillant ; à remplacer par un son court, grave et doux.", ["cloth unfold soft", "wings unfold", "soft whoosh low"], "minecraft:ui.button.click", "ui"),
    "ui.open": ("Ouverture du menu (prêt, pas branché)", [], "Ouverture douce.", ["ui open soft"], "minecraft:ui.button.click", "ui"),
    "block.forge_bench.hammer": ("Coups de marteau de l'établi de la forge", ["block/ForgeBenchEffects.java"], "Marteau sur enclume.", ["hammer anvil", "smith hammer"], "minecraft:block.anvil.use", "block_oneshot"),
    "block.forge_bench.quench": ("Trempe à la fin d'une forge", ["block/ForgeBenchEffects.java"], "Sifflement de métal trempé.", ["quench hiss", "steam hiss"], "minecraft:block.fire.extinguish", "block_oneshot"),
    "block.cloud_compressor.loop": ("Boucle du compresseur en marche", ["block/CloudCompressorEffects.java"], "Machine qui brasse, supportable longtemps.", ["machine loop", "pump loop", "churn"], "minecraft:block.furnace.fire_crackle", "block_loop"),
    "essence.pour": ("Essence versée dans un établi", ["block/EssenceBenchBlockEntity.java"], "Liquide versé.", ["pour liquid", "potion pour"], "minecraft:item.bottle.fill", "block_oneshot"),
    "essence.full": ("Conteneur plein en vol", ["flight/CloudHarvest.java"], "Petit signal plein.", ["bottle full", "magic fill"], None, "block_oneshot"),
}

SILENT = {
    "flight.takeoff_charge": "Charge d'Alt maintenu", "flight.dive": "Piqué rapide hors Furies", "flight.brake": "FLAG_BRAKE",
    "flight.stall": "FLAG_STALL", "flight.tuck": "FLAG_TUCK", "flight.flyby": "Passage d'un joueur proche",
    "flight.stamina_low": "HUD de stamina", "flight.stamina_empty": "HUD de stamina", "flight.unfold": "Sortie des ailes",
    "flight.fold": "Rangement des ailes", "flight.glide": "Boucle de plané", "flight.hover_flap": "Battements du stationnaire",
    "fury.idle": "Idles au sol des Furies", "fury.tail": "Queue des Furies",
    "gacha.music": "Musique d'invocation", "gacha.summary": "Écran de résumé", "gacha.skip": "Passer l'animation",
    "gacha.pity_soft": "Pitié douce", "gacha.mythic": "Sons propres des mythiques",
    "ui.select": "Sélection dans le menu J", "ui.locked": "Aile verrouillée", "ui.transmog": "Equip appearance",
    "ui.unlock": "Déblocage d'une aile", "block.ambient": "Ambiances autel, fontaine, établi",
    "structure.sanctum_appear": "Apparition d'un sanctuaire", "item.compass": "Compas des sanctuaires",
    "item.summoning_feather": "Plume d'invocation",
}

CODE_VANILLA = [
    ("gacha.dragon_growl", "gacha", "altar", "minecraft:entity.ender_dragon.growl", "Rugissement SSS et mythique, étoiles ×10", ["client/show/ClientShows.java", "client/gui/CardDeck.java"]),
    ("gacha.beacon", "gacha", "altar", "minecraft:block.beacon.activate", "Activation du pilier", ["client/show/ClientShows.java"]),
    ("gacha.element_ignite.fire", "gacha", "altar", "minecraft:item.firecharge.use", "Couche de la flamme fire de l'autel", ["assets/simplywings/gacha/show_sounds.json"]),
    ("gacha.element_ignite.darkness", "gacha", "altar", "minecraft:entity.warden.sonic_charge", "Couche de la flamme darkness de l'autel", ["assets/simplywings/gacha/show_sounds.json"]),
    ("gacha.element_ignite.earth", "gacha", "altar", "minecraft:block.basalt.break", "Couche de la flamme earth de l'autel", ["assets/simplywings/gacha/show_sounds.json"]),
    ("gacha.element_ignite.water", "gacha", "altar", "minecraft:entity.player.splash", "Couche de la flamme water de l'autel", ["assets/simplywings/gacha/show_sounds.json"]),
    ("gacha.element_ignite.light", "gacha", "altar", "minecraft:block.amethyst_block.chime", "Couche de la flamme light de l'autel", ["assets/simplywings/gacha/show_sounds.json"]),
    ("gacha.element_ignite.air", "gacha", "altar", "minecraft:entity.breeze.idle_air", "Couche de la flamme air de l'autel", ["assets/simplywings/gacha/show_sounds.json"]),
    ("block.assembly_table.resonate", "block", "assembly_table", "minecraft:block.amethyst_block.resonate", "Montée de la table d'assemblage", ["block/AssemblyTableEffects.java"]),
    ("block.forge_bench.chime", "block", "forge_bench", "minecraft:block.amethyst_block.chime", "Infusion d'énergie ailée", ["block/ForgeBenchEffects.java"]),
    ("block.cloud_compressor.brew", "block", "cloud_compressor", "minecraft:block.brewing_stand.brew", "Fin de cycle du compresseur", ["block/CloudCompressorBlockEntity.java"]),
    ("block.cloud_compressor.bubbles", "block", "cloud_compressor", "minecraft:block.bubble_column.upwards_ambient", "Bulles du compresseur", ["block/CloudCompressorEffects.java"]),
    ("fountain.offer", "gacha", "fountain", "minecraft:block.amethyst_block.resonate", "Offrande à la fontaine", ["gacha/FountainMenu.java"]),
    ("fountain.payout", "gacha", "fountain", "minecraft:entity.player.levelup", "Gain de la fontaine", ["gacha/FountainMenu.java"]),
]


def category(sid):
    head = sid.split(".")[0]
    return {"night_fury": "fury", "light_fury": "fury", "fountain": "gacha", "item": "structure"}.get(head, head)


def group(sid):
    parts = sid.split(".")
    if parts[0] in ("night_fury", "light_fury"):
        return parts[0]
    if parts[0] == "ui" and parts[1].startswith("card"):
        return "cards"
    if parts[0] == "gacha":
        return "altar"
    return parts[1] if len(parts) > 2 else parts[0]


def entry(sid, status, **extra):
    info = INFO.get(sid, ("", [], "", [], None, None))
    e = {"id": sid, "category": category(sid), "group": group(sid), "status": status,
         "trigger": info[0], "code": info[1], "description": info[2], "keywords": info[3], "reference": info[4],
         "loudness_target": info[5], "attenuation": None, "preload": False, "stream": False,
         "subtitle_key": None, "subtitle": None, "declared": True, "slow_attack": False,
         "animations": [], "variants": [], "layers": [], "candidates": [],
         "verdict": {"state": "todo", "note": "", "date": None}}
    e.update(extra)
    return e


def variants(base):
    return [dict(file=f"{base}_{i + 1}", **spec) for i, spec in enumerate(FILES[base])]


def vanilla(event, volume=1.0, pitch=1.0):
    return {"event": event, "volume": round(volume, 3), "pitch": round(pitch, 3)}


def main():
    if CATALOG.exists() and "--force" not in sys.argv:
        raise SystemExit(f"{CATALOG} exists; pass --force to rebuild it from the seed tables")
    out = []
    for family in ("feather", "membrane", "leather", "insect", "strong"):
        out.append(entry(f"wing.{family}.flap", "custom", group=family, variants=variants(f"wing/{family}/flap"),
                         animations=[{"rig": "basic", "scenario": "cruise_flap", "event": "strike"}]))
    out.append(entry("wing.buzz.loop", "custom", group="buzz", variants=variants("wing/buzz/loop"), loop=True,
                     animations=[{"rig": "bee", "scenario": "hover", "event": "loop"}]))
    for base in FILES:
        if base.startswith(("gacha/", "ui/", "block/", "essence/", "flight/")):
            sid = base.replace("/", ".")
            out.append(entry(sid, "todo" if sid == "ui.equip" else "custom", subtitle_key=sid, subtitle=SUBTITLES[sid],
                             variants=variants(base), loop=base.endswith("loop") or base == "flight/wind",
                             slow_attack=sid in ("gacha.rune_charge", "gacha.beam", "flight.wind")))
    out.append(entry("flight.takeoff_launch", "alias", subtitle_key="flight.takeoff_launch", subtitle=SUBTITLES["flight.takeoff_launch"],
                     alias={"target": "simplywings:wing.strong.flap", "volume": 1.0, "pitch": 1.1},
                     animations=[{"rig": "basic", "scenario": "takeoff", "event": "launch"}]))
    out.append(entry("night_fury.dive", "custom", subtitle_key="night_fury.dive", subtitle=SUBTITLES["night_fury.dive"],
                     variants=variants("fury/dive"), slow_attack=True, license_blocker=True))
    out.append(entry("light_fury.dive", "custom", subtitle_key="light_fury.dive", subtitle=SUBTITLES["light_fury.dive"],
                     variants_from="night_fury.dive", slow_attack=True, license_blocker=True))
    inv = variants("fury/invisible")
    out.append(entry("night_fury.invisible", "custom", subtitle_key="night_fury.invisible", subtitle=SUBTITLES["night_fury.invisible"],
                     variants=inv, pitch=0.97, slow_attack=True, license_blocker=True))
    out.append(entry("light_fury.invisible", "custom", subtitle_key="light_fury.invisible", subtitle=SUBTITLES["light_fury.invisible"],
                     variants_from="night_fury.invisible", pitch=1.03, slow_attack=True, license_blocker=True))
    out.append(entry("night_fury.charge", "custom", subtitle_key="night_fury.charge", subtitle=SUBTITLES["night_fury.charge"],
                     variants=variants("fury/night_charge"), slow_attack=True))
    out.append(entry("night_fury.crackle", "custom", subtitle_key="night_fury.charge", variants=variants("fury/crackle")))
    out.append(entry("light_fury.charge", "custom", subtitle_key="light_fury.charge", subtitle=SUBTITLES["light_fury.charge"],
                     variants=variants("fury/light_charge"), slow_attack=True))
    out.append(entry("light_fury.blast", "vanilla", subtitle_key="light_fury.blast", subtitle=SUBTITLES["light_fury.blast"],
                     vanilla=vanilla("minecraft:entity.generic.explode", 0.8, 1.2)))

    pairs = sorted(p.stem for p in WINGS.glob("*.json"))
    for pair in pairs:
        family, pitch, *rest = PAIRS[pair]
        volume = rest[0] if rest else 1.0
        target, family_volume, family_pitch = FAMILIES[family]
        layers = [vanilla(*layer) for layer in LAYERS.get(PAIR_EFFECT.get(pair), [])]
        model = json.loads((WINGS / f"{pair}.json").read_text(encoding="utf-8"))["model"].split(":")[1]
        anim = [{"rig": model, "scenario": "cruise_flap", "event": "strike"}]
        flap = dict(group=pair, subtitle_key="wing.flap", layers=layers, animations=anim, family=family)
        if pair == "common":
            flap["subtitle"] = SUBTITLES["wing.flap"]
        if ":" in target and not target.startswith("simplywings:"):
            out.append(entry(f"wing.{pair}.flap", "vanilla", vanilla=vanilla(target, volume * family_volume, pitch * family_pitch), **flap))
        else:
            out.append(entry(f"wing.{pair}.flap", "alias", alias={"target": "simplywings:" + target, "volume": round(volume * family_volume, 3),
                                                                  "pitch": round(pitch * family_pitch, 3)}, **flap))
        if family == "buzz" or pair == "daedalus":
            loop = "simplywings:block.cloud_compressor.loop" if pair == "daedalus" else "simplywings:wing.buzz.loop"
            out.append(entry(f"wing.{pair}.buzz", "alias", group=pair, subtitle_key="wing.buzz",
                             subtitle=SUBTITLES["wing.buzz"] if pair == "ancient" else None,
                             alias={"target": loop, "volume": 1.0, "pitch": round(pitch * (1.4 if pair == "daedalus" else 1.0), 3)},
                             animations=[{"rig": model, "scenario": "hover", "event": "loop"}], loop=True))
        strong = dict(group=pair, subtitle_key="wing.flap_strong", family=family,
                      animations=[{"rig": model, "scenario": "takeoff", "event": "launch"}],
                      alias={"target": "simplywings:wing.strong.flap", "volume": round(volume * STRONG_VOLUME.get(family, 1.0), 3),
                             "pitch": round(pitch * STRONG_PITCH.get(family, 1.0), 3)})
        if pair == "common":
            strong["subtitle"] = SUBTITLES["wing.flap_strong"]
        out.append(entry(f"wing.{pair}.flap_strong", "alias", **strong))

    for sid, why in SILENT.items():
        out.append(entry(sid, "silent", trigger=why, declared=False))
    for sid, cat, grp, event, why, code in CODE_VANILLA:
        out.append(entry(sid, "vanilla", category=cat, group=grp, trigger=why, code=code, declared=False, vanilla=vanilla(event)))

    for e in out:
        if e["category"] == "wing" and e["loudness_target"] is None and e["status"] == "custom":
            e["loudness_target"] = "wing_flap"
    CATALOG.write_text(json.dumps({"version": 1, "sounds": out}, indent=1, ensure_ascii=False) + "\n", encoding="utf-8", newline="\n")
    print(f"{len(out)} entries written to {CATALOG.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
