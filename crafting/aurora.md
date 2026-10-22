# Route de craft · Aurora Wings

`simplywings:aurora` · **Épique** · item `aurora_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-daurore)

Une aurore boréale qui a pris forme. La route se fait la nuit, sur la glace.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 12 en `GRID` + 1 assemblage |
| Cloud Essence | 285 (pièces) + 300 (assemblage) = **585** |
| Drops du mod | Polar Glimmer (BLOCK · Ice, Packed Ice, Blue Ice) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Polar Glimmer `simplywings:polar_glimmer` | 10 | `BLOCK` | Ice, Packed Ice, Blue Ice | De nuit, biomes `#c:is_snowy` | 15 % |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Blue Ice `minecraft:blue_ice` | 1 | `VANILLA` | Icebergs, toucher de soie | — | — |
| Ghast Tear `minecraft:ghast_tear` | 1 | `VANILLA` | Ghasts | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Glass Pane `minecraft:glass_pane` | 4 | `VANILLA` | Établi : verre | — | — |
| Glow Ink Sac `minecraft:glow_ink_sac` | 4 | `VANILLA` | Poulpes luisants | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Lime Dye `minecraft:lime_dye` | 4 | `VANILLA` | Fonte du cornichon de mer, établi | — | — |
| Purple Dye `minecraft:purple_dye` | 4 | `VANILLA` | Établi : rouge + bleu | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Tear ×1 | 20 | 30 |
| 3 | Aurora Veil `simplywings:aurora_veil` ×4 | `GRID` | 1 | 4 | Polar Glimmer ×2, Glow Ink Sac ×1, Lime Dye ×1, Purple Dye ×1 | 4 × 15 | 90 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 120 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 145 |
| 6 | Aurora Core `simplywings:aurora_core` ×1 | `GRID` | 2 | 1 | Polar Glimmer ×2, Gold Ingot ×2, Spirit Vessel ×1 → #2, Blue Ice ×1 | 60 | 205 |
| 7 | Aurora Wing `simplywings:aurora_wing` ×2 | `GRID` | 2 | 2 | Aurora Veil ×2 → #3, Gilded Frame ×1 → #4, Glass Pane ×2 | 2 × 40 | 285 |
| 8 | Aurora Wings `simplywings:aurora_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Aurora Core → #6 · Aile G : Aurora Wing → #7 · Aile D : Aurora Wing → #7 | 300 | **585** |

## Recettes

Grille 5×5 sans les coins : `.` = coin absent, `_` = case vide.

### #1 · Iron-Buckled Harness · `simplywings:iron_harness` · partagé

`GRID` · niveau 1 · 10 Cloud Essence · donne ×1

```
. L _ L .
_ L _ L _
_ I S I _
_ L I L _
. L _ L .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `L` | Leather `minecraft:leather` | 8 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 3 | 0 |
| `S` | String `minecraft:string` | 1 | 0 |

### #2 · Spirit Vessel · `simplywings:spirit_vessel` · partagé

`GRID` · niveau 1 · 20 Cloud Essence · donne ×1

```
. _ _ _ .
_ G A G _
_ A T A _
_ G A G _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `A` | Glass `minecraft:glass` | 4 | 0 |
| `T` | Ghast Tear `minecraft:ghast_tear` | 1 | 0 |

### #3 · Aurora Veil · `simplywings:aurora_veil` · partagé

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

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
| `G` | Glow Ink Sac `minecraft:glow_ink_sac` | 1 | 0 |
| `L` | Lime Dye `minecraft:lime_dye` | 1 | 0 |
| `U` | Purple Dye `minecraft:purple_dye` | 1 | 0 |

### #4 · Gilded Frame · `simplywings:gilded_frame` · partagé

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
_ G P G _
_ _ I _ _
_ G P G _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `P` | Blaze Powder `minecraft:blaze_powder` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

### #5 · Gilded Harness · `simplywings:gilded_harness` · partagé

`GRID` · niveau 2 · 25 Cloud Essence · donne ×1

```
. G _ G .
_ L H L _
_ G _ G _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `L` | Leather `minecraft:leather` | 2 | 0 |
| `H` | Iron-Buckled Harness `simplywings:iron_harness` | 1 | 1 |

### #6 · Aurora Core · `simplywings:aurora_core`

`GRID` · niveau 2 · 60 Cloud Essence · donne ×1

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

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

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
- **Aurora Veil** (intermédiaire, réutilisé par la Light Fury) : la lueur polaire, l'encre luisante, le vert et le violet.
- **Blue Ice** : le froid polaire au centre du cœur.
- **Glass Pane** : des rideaux de lumière translucides.
