# Route de craft · Wings of the Wind

`simplywings:air` · **Épique** · item `air_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-du-vent)

L'esprit du vent, léger et rapide. La route passe par les chambres d'épreuves, chez les breezes.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 12 en `GRID` + 1 assemblage |
| Essences | 90 nuage + 195 vapeur (pièces) + 150 nuage + 150 vapeur (assemblage) = **240 nuage + 345 vapeur** |
| Matières premières | 8 types du mod (24 objets) · 8 types vanilla (46 objets) |
| Drops du mod | Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Updraft Shard (BLOCK · `#minecraft:base_stone_overworld`), Silk Thread (MOB · Spider, Cave Spider), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Gust Mote (MOB · Breeze), Ghast Wisp (MOB · Ghast), Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Ember Wisp `simplywings:ember_wisp` | 2 | `MOB` | Blaze | — | 30 % |
| Ghast Wisp `simplywings:ghast_wisp` | 1 | `MOB` | Ghast | — | 50 % |
| Gilded Vein `simplywings:gilded_vein` | 3 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Gust Mote `simplywings:gust_mote` | 8 | `MOB` | Breeze | — | 40 % |
| Silk Thread `simplywings:silk_thread` | 5 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Updraft Shard `simplywings:updraft_shard` | 2 | `BLOCK` | `#minecraft:base_stone_overworld` | Y ≥ 150 | 8 % |
| Blaze Powder `minecraft:blaze_powder` | 2 | `VANILLA` | Établi : bâton de blaze | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Glass Bottle `minecraft:glass_bottle` | 4 | `VANILLA` | Établi : verre | — | — |
| Glass Pane `minecraft:glass_pane` | 4 | `VANILLA` | Établi : verre | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Wind Charge `minecraft:wind_charge` | 4 | `VANILLA` | Établi : bâton de breeze | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Bottled Gale `simplywings:bottled_gale` ×4 | `GRID` | 1 | 4 | Gust Mote ×2, Wind Charge ×1, Glass Bottle ×1 | 4 × (15 vapeur) | 10 nuage + 60 vapeur |
| 3 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Wisp ×1 | 20 vapeur | 10 nuage + 80 vapeur |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × (15 vapeur) | 10 nuage + 110 vapeur |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 10 nuage + 15 vapeur | 20 nuage + 125 vapeur |
| 6 | Wind Core `simplywings:air_core` ×1 | `GRID` | 2 | 1 | Bottled Gale ×2 → #2, Gold Ingot ×2, Updraft Shard ×2, Spirit Vessel ×1 → #3 | 30 nuage + 30 vapeur | 50 nuage + 155 vapeur |
| 7 | Wind Wing `simplywings:air_wing` ×2 | `GRID` | 2 | 2 | Glass Pane ×2, Bottled Gale ×1 → #2, Silk Thread ×2, Gilded Frame ×1 → #4 | 2 × (20 nuage + 20 vapeur) | 90 nuage + 195 vapeur |
| 8 | Wings of the Wind `simplywings:air_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Wind Core → #6 · Aile G : Wind Wing → #7 · Aile D : Wind Wing → #7 | 150 nuage + 150 vapeur | **240 nuage + 345 vapeur** |

## Recettes

Grille 5×5 sans les coins : `.` = coin absent, `_` = case vide.

### #1 · Iron-Buckled Harness · `simplywings:iron_harness` · partagé

`GRID` · niveau 1 · essence : 10 nuage · donne ×1

```
. L _ L .
_ H _ H _
_ I T I _
_ L K L _
. L _ L .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `L` | Leather `minecraft:leather` | 6 | 0 |
| `H` | Supple Hide `simplywings:supple_hide` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 2 | 0 |
| `T` | Silk Thread `simplywings:silk_thread` | 1 | 0 |
| `K` | Sky Iron Fleck `simplywings:sky_iron` | 1 | 0 |

### #2 · Bottled Gale · `simplywings:bottled_gale` · partagé

`GRID` · niveau 1 · essence : 15 vapeur · donne ×1

```
. _ _ _ .
_ M W M _
_ _ B _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `M` | Gust Mote `simplywings:gust_mote` | 2 | 0 |
| `W` | Wind Charge `minecraft:wind_charge` | 1 | 0 |
| `B` | Glass Bottle `minecraft:glass_bottle` | 1 | 0 |

### #3 · Spirit Vessel · `simplywings:spirit_vessel` · partagé

`GRID` · niveau 1 · essence : 20 vapeur · donne ×1

```
. _ _ _ .
_ G A G _
_ A W A _
_ G A G _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `A` | Glass `minecraft:glass` | 4 | 0 |
| `W` | Ghast Wisp `simplywings:ghast_wisp` | 1 | 0 |

### #4 · Gilded Frame · `simplywings:gilded_frame` · partagé

`GRID` · niveau 1 · essence : 15 vapeur · donne ×1

```
. _ _ _ .
_ G P G _
_ _ V _ _
_ G E G _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `P` | Blaze Powder `minecraft:blaze_powder` | 1 | 0 |
| `V` | Gilded Vein `simplywings:gilded_vein` | 1 | 0 |
| `E` | Ember Wisp `simplywings:ember_wisp` | 1 | 0 |

### #5 · Gilded Harness · `simplywings:gilded_harness` · partagé

`GRID` · niveau 2 · essence : 10 nuage + 15 vapeur · donne ×1

```
. G _ G .
_ L H L _
_ G V G _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `L` | Leather `minecraft:leather` | 2 | 0 |
| `H` | Iron-Buckled Harness `simplywings:iron_harness` | 1 | 1 |
| `V` | Gilded Vein `simplywings:gilded_vein` | 1 | 0 |

### #6 · Wind Core · `simplywings:air_core`

`GRID` · niveau 2 · essence : 30 nuage + 30 vapeur · donne ×1

```
. _ _ _ .
_ B G B _
_ U V U _
_ _ G _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Bottled Gale `simplywings:bottled_gale` | 2 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `U` | Updraft Shard `simplywings:updraft_shard` | 2 | 0 |
| `V` | Spirit Vessel `simplywings:spirit_vessel` | 1 | 1 |

### #7 · Wind Wing · `simplywings:air_wing`

`GRID` · niveau 2 · essence : 20 nuage + 20 vapeur · donne ×1

```
. _ _ _ .
_ P B P _
_ S F S _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Glass Pane `minecraft:glass_pane` | 2 | 0 |
| `B` | Bottled Gale `simplywings:bottled_gale` | 1 | 1 |
| `S` | Silk Thread `simplywings:silk_thread` | 2 | 0 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |

## Pourquoi ces ingrédients

- **Gust Mote** : sur les breezes, les esprits du vent de la 1.21.
- **Bottled Gale** (intermédiaire, partagé avec l'orage et le séraphin) : le vent mis en bouteille avec une charge de vent.
- **Updraft Shard** : les courants ascendants des sommets.
- **Glass Pane** et **Silk Thread** : des voiles presque invisibles, striées comme des courants d'air.
