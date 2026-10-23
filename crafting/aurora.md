# Route de craft · Aurora Wings

`simplywings:aurora` · **Épique** · item `aurora_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-daurore)

Une aurore boréale qui a pris forme. La route se fait la nuit, sur la glace.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 12 en `GRID` + 1 assemblage |
| Essences | 90 nuage + 195 vapeur (pièces) + 150 nuage + 150 vapeur (assemblage) = **240 nuage + 345 vapeur** |
| Matières premières | 8 types du mod (24 objets) · 9 types vanilla (47 objets) |
| Drops du mod | Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Polar Glimmer (BLOCK · Ice, Packed Ice, Blue Ice), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Ghast Wisp (MOB · Ghast), Glimmer Spore (BLOCK · Glow Lichen), Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Ember Wisp `simplywings:ember_wisp` | 2 | `MOB` | Blaze | — | 30 % |
| Ghast Wisp `simplywings:ghast_wisp` | 1 | `MOB` | Ghast | — | 50 % |
| Gilded Vein `simplywings:gilded_vein` | 3 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Glimmer Spore `simplywings:glimmer_spore` | 4 | `BLOCK` | Glow Lichen | — | 30 % |
| Polar Glimmer `simplywings:polar_glimmer` | 10 | `BLOCK` | Ice, Packed Ice, Blue Ice | De nuit, biomes `#c:is_snowy` | 15 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Blaze Powder `minecraft:blaze_powder` | 2 | `VANILLA` | Établi : bâton de blaze | — | — |
| Blue Ice `minecraft:blue_ice` | 1 | `VANILLA` | Icebergs, toucher de soie | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Glass Pane `minecraft:glass_pane` | 4 | `VANILLA` | Établi : verre | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Lime Dye `minecraft:lime_dye` | 4 | `VANILLA` | Fonte du cornichon de mer, établi | — | — |
| Purple Dye `minecraft:purple_dye` | 4 | `VANILLA` | Établi : rouge + bleu | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Wisp ×1 | 20 vapeur | 10 nuage + 20 vapeur |
| 3 | Aurora Veil `simplywings:aurora_veil` ×4 | `GRID` | 1 | 4 | Polar Glimmer ×2, Glimmer Spore ×1, Lime Dye ×1, Purple Dye ×1 | 4 × (15 vapeur) | 10 nuage + 80 vapeur |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × (15 vapeur) | 10 nuage + 110 vapeur |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 10 nuage + 15 vapeur | 20 nuage + 125 vapeur |
| 6 | Aurora Core `simplywings:aurora_core` ×1 | `GRID` | 2 | 1 | Polar Glimmer ×2, Gold Ingot ×2, Spirit Vessel ×1 → #2, Blue Ice ×1 | 30 nuage + 30 vapeur | 50 nuage + 155 vapeur |
| 7 | Aurora Wing `simplywings:aurora_wing` ×2 | `GRID` | 2 | 2 | Aurora Veil ×2 → #3, Gilded Frame ×1 → #4, Glass Pane ×2 | 2 × (20 nuage + 20 vapeur) | 90 nuage + 195 vapeur |
| 8 | Aurora Wings `simplywings:aurora_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Aurora Core → #6 · Aile G : Aurora Wing → #7 · Aile D : Aurora Wing → #7 | 150 nuage + 150 vapeur | **240 nuage + 345 vapeur** |

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

### #2 · Spirit Vessel · `simplywings:spirit_vessel` · partagé

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

### #3 · Aurora Veil · `simplywings:aurora_veil` · partagé

`GRID` · niveau 1 · essence : 15 vapeur · donne ×1

```
. _ _ _ .
_ P G P _
_ L _ U _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Polar Glimmer `simplywings:polar_glimmer` | 2 | 0 |
| `G` | Glimmer Spore `simplywings:glimmer_spore` | 1 | 0 |
| `L` | Lime Dye `minecraft:lime_dye` | 1 | 0 |
| `U` | Purple Dye `minecraft:purple_dye` | 1 | 0 |

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

### #6 · Aurora Core · `simplywings:aurora_core`

`GRID` · niveau 2 · essence : 30 nuage + 30 vapeur · donne ×1

```
. _ _ _ .
_ P _ P _
_ G V G _
_ _ B _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Polar Glimmer `simplywings:polar_glimmer` | 2 | 0 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `V` | Spirit Vessel `simplywings:spirit_vessel` | 1 | 1 |
| `B` | Blue Ice `minecraft:blue_ice` | 1 | 0 |

### #7 · Aurora Wing · `simplywings:aurora_wing`

`GRID` · niveau 2 · essence : 20 nuage + 20 vapeur · donne ×1

```
. _ _ _ .
_ A F A _
_ E _ E _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `A` | Aurora Veil `simplywings:aurora_veil` | 2 | 1 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |
| `E` | Glass Pane `minecraft:glass_pane` | 2 | 0 |

## Pourquoi ces ingrédients

- **Polar Glimmer** : en cassant de la glace **de nuit** dans un biome enneigé, sous le ciel des aurores.
- **Aurora Veil** (intermédiaire, réutilisé par la Light Fury) : la lueur polaire, la **Glimmer Spore** du lichen luisant, le vert et le violet.
- **Blue Ice** : le froid polaire au centre du cœur.
- **Glass Pane** : des rideaux de lumière translucides.
