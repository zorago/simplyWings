# Routes de craft des ailes

Les règles communes à toutes les routes, les objets partagés, le registre des drops du mod et le récapitulatif des coûts. **Chaque paire a sa route complète dans son propre fichier**, `crafting/<id>.md` (matières premières, étapes, recettes dessinées, justification des ingrédients). Seule la paire commune reste décrite ici. Le cadre général (table d'assemblage, Cloud Essence, énergie ailée) est dans `CAHIER_DES_CHARGES.md`, section « Craft : table d'assemblage ».

Dernière mise à jour : 22 octobre 2026.

## Règles des routes

Fixées par zorago le 22 octobre 2026, par ordre d'importance.

**1. Non négociables**

1. **Sources 100 % vanilla** : tout s'obtient avec Minecraft et Simply Wings seuls, sans Create, Ad Astra ni aucun autre mod.
2. **Craft répétable** : rien ne dépend d'une ressource unique (œuf de dragon, etc.). Tout se refarme : le dragon se réinvoque, le Wither aussi, les raids, les générateurs d'épreuves et les géodes se renouvellent.
3. **Profondeur maximale** des pièces (harnais, cœur, ailes), en niveaux de craft :

| Rareté | Niveau max d'une pièce | Lecture |
| --- | --- | --- |
| Rare | 1 | Les 4 pièces se craftent directement depuis les matières premières |
| Épique | 2 | Un intermédiaire possible avant la pièce |
| Légendaire | 3 | Deux niveaux d'intermédiaires |
| Mythique | 5 | Quatre niveaux d'intermédiaires |

**2. Important** : les ressources sont cohérentes avec l'aile, et se récupèrent par un gameplay qui la rappelle (la chouette chasse de nuit, le goéland pêche sur la plage, le Dédale passe par le labyrinthe des chambres d'épreuves…). Un métal pilier par palier : **fer** (rare), **or** (épique), **diamant** (légendaire), **netherite** (mythique). Plumes, cuir, bâtons, ficelle, verre et teintures reviennent dans beaucoup de recettes.

**Équilibre vanilla / mod** (22 octobre) : dans chaque route, à peu près autant de **types** de matières premières du mod que de types vanilla. Les quantités restent plus généreuses côté vanilla (par exemple 6 cuirs pour 2 Supple Hide dans le harnais). Une famille de drops communs rend l'item imprégné utile partout : minerais bruts en grotte (Sky Iron Fleck, Copper Spark, Gilded Vein, Redstone Pulse, Lapis Glint, Emerald Sliver, Soot Flake, Prism Shard, Pale Quartz, Ancient Ember), mobs courants (Supple Hide, Silk Thread, Hollow Bone, Elastic Gel, Pristine Feather, Fleece Tuft, Abyssal Ink, Ghast Wisp), blocs (Void Grit, Glimmer Spore) et pêche (Glimmering Scale).

**3. Information** : une ressource peut venir de n'importe où (bloc cassé, mob tué, craft, pêche), **sauf des coffres de loot et de l'élevage**. Les drops du mod exigent que l'outil, l'arme ou la canne à pêche porte l'énergie ailée.

## Niveaux de craft

- **Niveau 0** (matière première) : tout ce qui est vanilla, y compris ce qui se fabrique à l'établi ou au four (lingots, bâtons, teintures, lanterne, balise…), et les drops du mod (registre ci-dessous).
- **Niveau n** : un objet du mod fabriqué dans l'onglet `GRID` a pour niveau 1 + le niveau le plus haut de ses ingrédients.
- La profondeur d'une paire = le niveau le plus haut parmi ses 4 pièces. L'assemblage (`ASM`) ne compte pas.
- Les harnais forment une **lignée** : chaque palier reprend le harnais du palier d'en dessous (Iron-Buckled → Gilded → Radiant → Fury Saddle). Le harnais est le même pour toutes les paires d'un palier ; le cœur et les ailes sont propres à chaque paire.
- Les ailes gauche et droite sont le même objet, crafté deux fois. Seule exception : Yin et Yang (`yin_wing` à gauche, `yang_wing` à droite).
- Les niveaux, les quantités et les coûts des fichiers `crafting/*.md` sont calculés et vérifiés par un générateur : formes de 21 cases, niveaux sous le plafond de chaque rareté, quantités totales.

## Lire une route

| Colonne | Contenu |
| --- | --- |
| # | Ordre conseillé (par niveau). Les étapes de même niveau se font dans n'importe quel ordre. |
| Produit | Nom anglais affiché en jeu, id et quantité totale produite |
| Où | Voir la légende ci-dessous |
| Niv. | Niveau de craft du produit |
| Crafts | Nombre de fois que la recette est lancée pour une paire |
| Entrées par craft | Ce qu'un craft consomme. Un produit d'une étape précédente est noté `→ #n` |
| Essence | Cloud Essence consommée (crafts × coût) |
| Cumul | Essence consommée depuis le début de la route |

**Légende de « Où »**

| Code | Signification |
| --- | --- |
| `ASM` | Table d'assemblage, onglet 1 « Assembly » : harnais + cœur + aile gauche + aile droite → paire |
| `GRID` | Table d'assemblage, onglet 2 « Components » : grille 5×5 sans les coins (21 cases) |
| `MOB` | Drop d'un mob, coup fatal porté avec un item imprégné d'énergie ailée |
| `BLOCK` | Drop d'un bloc, cassé avec un item imprégné |
| `FISH` | Prise supplémentaire à la pêche, avec une canne imprégnée |
| `VANILLA` | Ressource vanilla obtenue normalement (minage, mob, établi, four, pêche, commerce, brosse) |
| `CLOUD` | Récolte de Cloud Essence en planant dans les nuages |

**Grille `GRID`** (`.` = coin absent, `_` = case vide) :

```
. # # # .
# # # # #
# # # # #
# # # # #
. # # # .
```

## Budget de Cloud Essence

| Rareté | Assemblage (`ASM`) | Flacons | Pièces (`GRID`) | Total d'une paire |
| --- | --- | --- | --- | --- |
| Commune | 0 | 0 | 0 | 0 |
| Rare | 100 | 1 | harnais 10, cœur 30, aile 15 (moins pour les ailes simples) | 130 à 170 |
| Épique | 300 | 3 | intermédiaire 15 à 20, harnais 25, cœur 60, aile 40 | 540 à 615 |
| Légendaire | 800 | 8 | intermédiaire 10 à 40, harnais 50, cœur 100, aile 80 | 1 360 à 1 645 |
| Mythique | 1 000 | 10 (réservoir plein) | intermédiaire 30 à 90, selle 80, cœur 100, aile 100 | 2 690 à 2 735 |

- 1 flacon (`simplywings:cloud_flask`) = 100 unités. La table contient au plus 1 000 unités (10 flacons).
- Un craft `GRID` coûte de 5 à 100 unités. Le total d'une paire = assemblage + tous les crafts de sa route, harnais compris.

## Drops du mod

Le registre `simplywings:loot_source` (synchronisé au client pour JEI). Chaque entrée : la source (mob, bloc ou pêche), des **conditions vanilla facultatives** (biome, heure, météo, hauteur, structure, état du bloc, tueur accroupi ou en sprint, lumière) et une chance. Ces conditions sont des `LootItemCondition` vanilla (`location_check`, `time_check`, `weather_check`, `entity_properties`, `block_state_property`), lues par le Global Loot Modifier : aucun code spécial par paire. Les chances sont des valeurs de départ, à régler en jeu.

Proposition pour le point ouvert « nom des essences de blocs » : il n'y a plus de catégorie générique. Chaque drop porte un nom concret (Acorn, Resonant Shard, Living Rock…), et le mot « Essence » reste réservé à la Cloud Essence.

| Drop | Id | Où | Source | Condition | Chance | Paires |
| --- | --- | --- | --- | --- | --- | --- |
| Bat Membrane | `simplywings:bat_membrane` | `MOB` | Bat | — | 50 % | `bat` |
| Crow Feather | `simplywings:crow_feather` | `BLOCK` | Wheat mûr (`age=7`) | — | 10 % | `crow` |
| Hushed Down | `simplywings:hushed_down` | `MOB` | Rabbit | De nuit, tueur accroupi | 50 % | `owl` |
| Gull Feather | `simplywings:gull_feather` | `FISH` | Pêche | Biomes `beach`, `snowy_beach`, `stony_shore` | 20 % | `gull` |
| Nectar Drop | `simplywings:nectar_drop` | `BLOCK` | `#minecraft:small_flowers` | — | 15 % | `hummingbird`, `bee` |
| Raptor Talon | `simplywings:raptor_talon` | `MOB` | Goat | Tuée à Y ≥ 120 | 35 % | `eagle` |
| Updraft Shard | `simplywings:updraft_shard` | `BLOCK` | `#minecraft:base_stone_overworld` | Y ≥ 150 | 8 % | `eagle`, `swift`, `ornithopter`, `kite`, `air`, `storm` |
| Swift Quill | `simplywings:swift_quill` | `MOB` | Chicken | Tueur en sprint | 30 % | `swift` |
| Homing Feather | `simplywings:homing_feather` | `MOB` | Chicken | Dans un village (structure `#minecraft:village`) | 35 % | `pigeon` |
| Peacock Plume | `simplywings:peacock_plume` | `MOB` | Chicken | Biomes `#minecraft:is_jungle` | 35 % | `peacock` |
| Macaw Plume | `simplywings:macaw_plume` | `BLOCK` | Jungle Leaves | — | 5 % | `parrot` |
| Royal Jelly | `simplywings:royal_jelly` | `BLOCK` | Bee Nest, Beehive | Pleine (`honey_level=5`) | 100 % | `bee` |
| Moth Dust | `simplywings:moth_dust` | `BLOCK` | Torch, Wall Torch, Lantern | De nuit | 25 % | `moth` |
| Chitin Shard | `simplywings:chitin_shard` | `MOB` | Silverfish | — | 25 % | `beetle` |
| Acorn | `simplywings:acorn` | `BLOCK` | Oak Leaves, Dark Oak Leaves | — | 5 % | `squirrel`, `forest` |
| Fin Ray | `simplywings:fin_ray` | `FISH` | Pêche | Biomes `warm_ocean`, `lukewarm_ocean`, `deep_lukewarm_ocean` | 20 % | `flying_fish` |
| Phantom Sinew | `simplywings:phantom_sinew` | `MOB` | Phantom | — | 50 % | `phantom` |
| Resonant Shard | `simplywings:resonant_shard` | `BLOCK` | Amethyst Cluster (adulte) | — | 30 % | `amethyst`, `earth` |
| Verdigris Flake | `simplywings:verdigris_flake` | `BLOCK` | Blocs de cuivre `weathered_*` et `oxidized_*` (tag `simplywings:aged_copper`) | — | 25 % | `copper` |
| Fossil Fragment | `simplywings:fossil_fragment` | `BLOCK` | Bone Block | Y ≤ 40 | 25 % | `ancient`, `ptero` |
| Raw Resin | `simplywings:raw_resin` | `BLOCK` | Spruce Log | Biomes `old_growth_pine_taiga`, `old_growth_spruce_taiga` | 12 % | `ancient` |
| Ember Wisp | `simplywings:ember_wisp` | `MOB` | Blaze | — | 30 % | `ancient`, `ptero`, `fire`, `water`, `earth`, `air`, `storm`, `aurora`, `shadow`, `nebula`, `angel`, `demon`, `seraph`, `psyche`, `yin_yang`, `night`, `light` |
| Tide Pearl | `simplywings:tide_pearl` | `MOB` | Guardian / Elder Guardian | — | 20 % / 100 % ×3 | `water` |
| Living Rock | `simplywings:living_rock` | `BLOCK` | Deepslate, Tuff | Y ≤ 0 | 5 % | `earth` |
| Gust Mote | `simplywings:gust_mote` | `MOB` | Breeze | — | 40 % | `air`, `storm`, `seraph`, `daedalus` |
| Thunder Spark | `simplywings:thunder_spark` | `MOB` | Creeper | Pendant un orage (creeper chargé : 100 % ×2) | 50 % | `storm` |
| Polar Glimmer | `simplywings:polar_glimmer` | `BLOCK` | Ice, Packed Ice, Blue Ice | De nuit, biomes `#c:is_snowy` | 15 % | `aurora`, `light` |
| Shade Wisp | `simplywings:shade_wisp` | `MOB` | Enderman | Lumière ≤ 7 à l'endroit du kill | 25 % | `shadow` |
| Stardust | `simplywings:stardust` | `MOB` | Shulker | — | 35 % | `nebula` |
| Blossom Spirit | `simplywings:blossom_spirit` | `BLOCK` | Cherry Leaves | — | 6 % | `forest` |
| Heavenly Down | `simplywings:heavenly_down` | `MOB` | Chicken | Tué dans la couche de nuages (bande Y de la config Cloud Essence) | 60 % | `angel`, `seraph`, `yin_yang` |
| Vex Wing | `simplywings:vex_wing` | `MOB` | Vex | — | 40 % | `demon` |
| Soul Wisp | `simplywings:soul_wisp` | `BLOCK` | Soul Sand, Soul Soil | Biome `soul_sand_valley` | 8 % | `psyche` |
| Echo Resonance | `simplywings:echo_resonance` | `BLOCK` | Sculk Shrieker | — | 50 % | `yin_yang`, `night` |
| Dragon Scale | `simplywings:dragon_scale` | `MOB` | Ender Dragon | — | 100 % ×12 | `night`, `light` |
| Supple Hide | `simplywings:supple_hide` | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % | `bat`, `crow`, `owl`, `gull`, `hummingbird`, `eagle`, `swift`, `pigeon`, `peacock`, `parrot`, `bee`, `moth`, `beetle`, `squirrel`, `flying_fish`, `phantom`, `amethyst`, `ornithopter`, `kite`, `copper`, `patchwork`, `ancient`, `ptero`, `fire`, `water`, `earth`, `air`, `storm`, `aurora`, `shadow`, `nebula`, `forest`, `angel`, `demon`, `seraph`, `psyche`, `daedalus`, `yin_yang`, `night`, `light` |
| Silk Thread | `simplywings:silk_thread` | `MOB` | Spider, Cave Spider | — | 40 % | `bat`, `crow`, `owl`, `gull`, `hummingbird`, `eagle`, `swift`, `pigeon`, `peacock`, `parrot`, `bee`, `moth`, `beetle`, `squirrel`, `flying_fish`, `phantom`, `amethyst`, `ornithopter`, `kite`, `copper`, `patchwork`, `ancient`, `ptero`, `fire`, `water`, `earth`, `air`, `storm`, `aurora`, `shadow`, `nebula`, `forest`, `angel`, `demon`, `seraph`, `psyche`, `daedalus`, `yin_yang`, `night`, `light` |
| Hollow Bone | `simplywings:hollow_bone` | `MOB` | Skeleton, Stray, Bogged | — | 35 % | `bat`, `phantom`, `ptero` |
| Elastic Gel | `simplywings:elastic_gel` | `MOB` | Slime, Magma Cube | — | 40 % | `bat`, `ptero`, `demon`, `night` |
| Pristine Feather | `simplywings:pristine_feather` | `MOB` | Chicken | — | 25 % | `pigeon`, `parrot`, `patchwork`, `angel`, `seraph`, `daedalus`, `yin_yang` |
| Fleece Tuft | `simplywings:fleece_tuft` | `MOB` | Sheep | — | 40 % | `owl`, `moth`, `ornithopter`, `patchwork` |
| Abyssal Ink | `simplywings:abyssal_ink` | `MOB` | Squid, Glow Squid | — | 40 % | `gull`, `pigeon`, `shadow`, `yin_yang` |
| Ghast Wisp | `simplywings:ghast_wisp` | `MOB` | Ghast | — | 50 % | `fire`, `water`, `earth`, `air`, `storm`, `aurora`, `shadow`, `nebula`, `forest`, `demon`, `night`, `light` |
| Sky Iron Fleck | `simplywings:sky_iron` | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % | `bat`, `crow`, `owl`, `gull`, `hummingbird`, `eagle`, `swift`, `pigeon`, `peacock`, `parrot`, `bee`, `moth`, `beetle`, `squirrel`, `flying_fish`, `phantom`, `amethyst`, `ornithopter`, `kite`, `copper`, `patchwork`, `ancient`, `ptero`, `fire`, `water`, `earth`, `air`, `storm`, `aurora`, `shadow`, `nebula`, `forest`, `angel`, `demon`, `seraph`, `psyche`, `daedalus`, `yin_yang`, `night`, `light` |
| Copper Spark | `simplywings:copper_spark` | `BLOCK` | Copper Ore, Deepslate Copper Ore | — | 25 % | `ornithopter`, `copper`, `storm`, `daedalus` |
| Gilded Vein | `simplywings:gilded_vein` | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % | `eagle`, `ancient`, `ptero`, `fire`, `water`, `earth`, `air`, `storm`, `aurora`, `shadow`, `nebula`, `forest`, `angel`, `demon`, `seraph`, `psyche`, `daedalus`, `yin_yang`, `night`, `light` |
| Redstone Pulse | `simplywings:redstone_pulse` | `BLOCK` | Redstone Ore, Deepslate Redstone Ore | — | 30 % | `copper`, `storm` |
| Lapis Glint | `simplywings:lapis_glint` | `BLOCK` | Lapis Ore, Deepslate Lapis Ore | — | 30 % | `peacock`, `parrot` |
| Emerald Sliver | `simplywings:emerald_sliver` | `BLOCK` | Emerald Ore, Deepslate Emerald Ore | — | 60 % | `crow`, `hummingbird`, `peacock`, `beetle` |
| Soot Flake | `simplywings:soot_flake` | `BLOCK` | Coal Ore, Deepslate Coal Ore | — | 20 % | `demon`, `daedalus`, `night` |
| Prism Shard | `simplywings:prism_shard` | `BLOCK` | Diamond Ore, Deepslate Diamond Ore | — | 50 % | `angel`, `demon`, `seraph`, `psyche`, `daedalus`, `yin_yang`, `night`, `light` |
| Pale Quartz | `simplywings:pale_quartz` | `BLOCK` | Nether Quartz Ore | — | 25 % | `yin_yang`, `light` |
| Ancient Ember | `simplywings:ancient_ember` | `BLOCK` | Ancient Debris | — | 60 % | `night`, `light` |
| Void Grit | `simplywings:void_grit` | `BLOCK` | End Stone | — | 8 % | `nebula`, `angel`, `seraph`, `psyche`, `yin_yang`, `night`, `light` |
| Glimmer Spore | `simplywings:glimmer_spore` | `BLOCK` | Glow Lichen | — | 30 % | `aurora`, `forest`, `psyche`, `light` |
| Glimmering Scale | `simplywings:glimmering_scale` | `FISH` | Pêche | Partout | 10 % | `flying_fish`, `water`, `light` |

## Objets partagés

Les intermédiaires utilisés par plusieurs paires. Leur recette dessinée est recopiée dans chaque route qui s'en sert.

| Objet | Id | Niv. | Entrées | Essence | Donne | Paires |
| --- | --- | --- | --- | --- | --- | --- |
| Aurora Veil | `simplywings:aurora_veil` | 1 | Polar Glimmer ×2, Glimmer Spore ×1, Lime Dye ×1, Purple Dye ×1 | 15 | ×1 | `aurora`, `light` |
| Blessed Plume | `simplywings:blessed_plume` | 1 | Feather ×2, Heavenly Down ×2, Pristine Feather ×2, White Dye ×1 | 15 | ×2 | `angel`, `seraph` |
| Bottled Gale | `simplywings:bottled_gale` | 1 | Gust Mote ×2, Wind Charge ×1, Glass Bottle ×1 | 15 | ×1 | `air`, `storm`, `seraph` |
| Fury Plasma | `simplywings:fury_plasma` | 1 | Dragon's Breath ×2, Blaze Powder ×1, Ghast Wisp ×1 | 30 | ×1 | `night`, `light` |
| Gilded Frame | `simplywings:gilded_frame` | 1 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 15 | ×1 | `ancient`, `ptero`, `fire`, `water`, `earth`, `air`, `storm`, `aurora`, `shadow`, `nebula`, `angel`, `demon`, `seraph`, `psyche`, `yin_yang`, `night`, `light` |
| Iron-Buckled Harness | `simplywings:iron_harness` | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | ×1 | toutes sauf la commune |
| Spirit Vessel | `simplywings:spirit_vessel` | 1 | Gold Ingot ×4, Glass ×4, Ghast Wisp ×1 | 20 | ×1 | `fire`, `water`, `earth`, `air`, `storm`, `aurora`, `shadow`, `nebula`, `forest` |
| Celestial Frame | `simplywings:celestial_frame` | 2 | End Rod ×2, Void Grit ×1, Diamond ×2, Gilded Frame ×1 | 35 | ×1 | `angel`, `seraph`, `psyche`, `yin_yang`, `night`, `light` |
| Gilded Harness | `simplywings:gilded_harness` | 2 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1, Gilded Vein ×1 | 25 | ×1 | épiques, légendaires, mythiques |
| Dragonbone Spar | `simplywings:dragonbone_spar` | 3 | Dragon Scale ×2, Celestial Frame ×1, Ancient Ember ×1 | 60 | ×1 | `night`, `light` |
| Radiant Harness | `simplywings:radiant_harness` | 3 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1, Prism Shard ×1 | 50 | ×1 | légendaires, mythiques |
| Fury Saddle | `simplywings:fury_saddle` | 4 | Dragon Scale ×4, Leather ×2, Radiant Harness ×1, Saddle ×1, Netherite Ingot ×1, Ancient Ember ×2 | 80 | ×1 | `night`, `light` |

## Objets de base

| # | Produit | Où | Entrées | Essence |
| --- | --- | --- | --- | --- |
| C1 | Cloud Absorber `simplywings:cloud_absorber` ×1 | `GRID` | Glass ×4, Copper Ingot ×2, Updraft Shard ×2, Feather ×1 | 0 |
| C2 | Cloud Flask `simplywings:cloud_flask` (vide) ×2 | `GRID` | Glass ×4, Feather ×1 | 0 |
| C3 | Cloud Flask (pleine, 100 unités) | `CLOUD` | Flacon vide, absorbeur dans l'inventaire, vol dans la couche de nuages (Y 186 à 204, 2 unités/s) | 0 |
| C4 | Assembly Table `simplywings:assembly_table` | Forge de l'End | Déjà posée dans la structure | 0 |
| C5 | Forge Compass `simplywings:forge_compass` | Mort d'un dragon | Donné aux joueurs de l'île centrale, à partir du 2e dragon | 0 |

## Récapitulatif

| Paire | Id | Rareté | Profondeur | Crafts `GRID` | Pièces | Assemblage | Total | Route |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Common Wings | `common` | Commune | 0 | 0 | 0 | 0 | **0** | ci-dessous |
| Bat Wings | `bat` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/bat.md`](crafting/bat.md) |
| Crow Wings | `crow` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/crow.md`](crafting/crow.md) |
| Owl Wings | `owl` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/owl.md`](crafting/owl.md) |
| Gull Wings | `gull` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/gull.md`](crafting/gull.md) |
| Hummingbird Wings | `hummingbird` | Rare | 1 / 1 | 4 | 60 | 100 | **160** | [`crafting/hummingbird.md`](crafting/hummingbird.md) |
| Eagle Wings | `eagle` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/eagle.md`](crafting/eagle.md) |
| Swift Wings | `swift` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/swift.md`](crafting/swift.md) |
| Homing Pigeon Wings | `pigeon` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/pigeon.md`](crafting/pigeon.md) |
| Peacock Wings | `peacock` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/peacock.md`](crafting/peacock.md) |
| Parrot Wings | `parrot` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/parrot.md`](crafting/parrot.md) |
| Bee Wings | `bee` | Rare | 1 / 1 | 4 | 60 | 100 | **160** | [`crafting/bee.md`](crafting/bee.md) |
| Moth Wings | `moth` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/moth.md`](crafting/moth.md) |
| Beetle Wings | `beetle` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/beetle.md`](crafting/beetle.md) |
| Flying Squirrel Wings | `squirrel` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/squirrel.md`](crafting/squirrel.md) |
| Flying Fish Wings | `flying_fish` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/flying_fish.md`](crafting/flying_fish.md) |
| Phantom Wings | `phantom` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/phantom.md`](crafting/phantom.md) |
| Amethyst Wings | `amethyst` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/amethyst.md`](crafting/amethyst.md) |
| Ornithopter Wings | `ornithopter` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/ornithopter.md`](crafting/ornithopter.md) |
| Kite Wings | `kite` | Rare | 1 / 1 | 4 | 50 | 100 | **150** | [`crafting/kite.md`](crafting/kite.md) |
| Copper Wings | `copper` | Rare | 1 / 1 | 4 | 70 | 100 | **170** | [`crafting/copper.md`](crafting/copper.md) |
| Patchwork Wings | `patchwork` | Rare | 1 / 1 | 4 | 30 | 100 | **130** | [`crafting/patchwork.md`](crafting/patchwork.md) |
| Meganeura Wings | `ancient` | Épique | 2 / 2 | 11 | 265 | 300 | **565** | [`crafting/ancient.md`](crafting/ancient.md) |
| Pterosaur Wings | `ptero` | Épique | 2 / 2 | 14 | 310 | 300 | **610** | [`crafting/ptero.md`](crafting/ptero.md) |
| Wings of Flame | `fire` | Épique | 2 / 2 | 14 | 315 | 300 | **615** | [`crafting/fire.md`](crafting/fire.md) |
| Wings of the Tide | `water` | Épique | 2 / 2 | 14 | 315 | 300 | **615** | [`crafting/water.md`](crafting/water.md) |
| Wings of Stone | `earth` | Épique | 2 / 2 | 14 | 315 | 300 | **615** | [`crafting/earth.md`](crafting/earth.md) |
| Wings of the Wind | `air` | Épique | 2 / 2 | 12 | 285 | 300 | **585** | [`crafting/air.md`](crafting/air.md) |
| Wings of the Storm | `storm` | Épique | 2 / 2 | 13 | 300 | 300 | **600** | [`crafting/storm.md`](crafting/storm.md) |
| Aurora Wings | `aurora` | Épique | 2 / 2 | 12 | 285 | 300 | **585** | [`crafting/aurora.md`](crafting/aurora.md) |
| Wings of Shadow | `shadow` | Épique | 2 / 2 | 14 | 315 | 300 | **615** | [`crafting/shadow.md`](crafting/shadow.md) |
| Nebula Wings | `nebula` | Épique | 2 / 2 | 14 | 315 | 300 | **615** | [`crafting/nebula.md`](crafting/nebula.md) |
| Wings of the Forest Spirit | `forest` | Épique | 2 / 2 | 9 | 240 | 300 | **540** | [`crafting/forest.md`](crafting/forest.md) |
| Angel Wings | `angel` | Légendaire | 3 / 3 | 17 | 560 | 800 | **1360** | [`crafting/angel.md`](crafting/angel.md) |
| Demon Wings | `demon` | Légendaire | 3 / 3 | 16 | 595 | 800 | **1395** | [`crafting/demon.md`](crafting/demon.md) |
| Seraph Wings | `seraph` | Légendaire | 3 / 3 | 24 | 755 | 800 | **1555** | [`crafting/seraph.md`](crafting/seraph.md) |
| Wings of Psyche | `psyche` | Légendaire | 3 / 3 | 24 | 845 | 800 | **1645** | [`crafting/psyche.md`](crafting/psyche.md) |
| Wings of Daedalus | `daedalus` | Légendaire | 3 / 3 | 25 | 665 | 800 | **1465** | [`crafting/daedalus.md`](crafting/daedalus.md) |
| Wings of Yin and Yang | `yin_yang` | Légendaire | 3 / 3 | 18 | 645 | 800 | **1445** | [`crafting/yin_yang.md`](crafting/yin_yang.md) |
| Night Wings | `night` | Mythique | 5 / 5 | 35 | 1690 | 1000 | **2690** | [`crafting/night.md`](crafting/night.md) |
| Light Wings | `light` | Mythique | 5 / 5 | 39 | 1735 | 1000 | **2735** | [`crafting/light.md`](crafting/light.md) |

## Common Wings · `simplywings:common` · Commune

Les 4 emplacements de l'assemblage prennent directement des items vanilla. Les tonneaux de la forge fournissent plumes et cuir, l'élytre vient d'une cité de l'End.

| # | Produit | Où | Entrées | Essence | Cumul |
| --- | --- | --- | --- | --- | --- |
| 1 | Feather `minecraft:feather` ×2 | `VANILLA` | Tonneaux de la forge | 0 | 0 |
| 2 | Leather `minecraft:leather` ×1 | `VANILLA` | Tonneaux de la forge | 0 | 0 |
| 3 | Elytra `minecraft:elytra` ×1, enchantée ou non | `VANILLA` | Cité de l'End | 0 | 0 |
| 4 | Common Wings `simplywings:common_wings` | `ASM` | Harnais : → #2 · Cœur : → #3 · Aile G : 1 → #1 · Aile D : 1 → #1 | 0 | **0** |
