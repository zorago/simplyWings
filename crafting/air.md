# Route de craft · Wings of the Wind

`simplywings:air` · **Épique** · item `air_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-du-vent)

L'esprit du vent, léger et rapide. La route passe par les chambres d'épreuves, chez les breezes.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 12 en `GRID` + 1 assemblage |
| Cloud Essence | 285 (pièces) + 300 (assemblage) = **585** |
| Drops du mod | Updraft Shard (BLOCK · `#minecraft:base_stone_overworld`), Gust Mote (MOB · Breeze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Gust Mote `simplywings:gust_mote` | 8 | `MOB` | Breeze | — | 40 % |
| Updraft Shard `simplywings:updraft_shard` | 2 | `BLOCK` | `#minecraft:base_stone_overworld` | Y ≥ 150 | 8 % |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Ghast Tear `minecraft:ghast_tear` | 1 | `VANILLA` | Ghasts | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Glass Bottle `minecraft:glass_bottle` | 4 | `VANILLA` | Établi : verre | — | — |
| Glass Pane `minecraft:glass_pane` | 4 | `VANILLA` | Établi : verre | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| String `minecraft:string` | 5 | `VANILLA` | Araignées, toiles | — | — |
| Wind Charge `minecraft:wind_charge` | 4 | `VANILLA` | Établi : bâton de breeze | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Bottled Gale `simplywings:bottled_gale` ×4 | `GRID` | 1 | 4 | Gust Mote ×2, Wind Charge ×1, Glass Bottle ×1 | 4 × 15 | 70 |
| 3 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Tear ×1 | 20 | 90 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 120 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 145 |
| 6 | Wind Core `simplywings:air_core` ×1 | `GRID` | 2 | 1 | Bottled Gale ×2 → #2, Gold Ingot ×2, Updraft Shard ×2, Spirit Vessel ×1 → #3 | 60 | 205 |
| 7 | Wind Wing `simplywings:air_wing` ×2 | `GRID` | 2 | 2 | Glass Pane ×2, Bottled Gale ×1 → #2, String ×2, Gilded Frame ×1 → #4 | 2 × 40 | 285 |
| 8 | Wings of the Wind `simplywings:air_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Wind Core → #6 · Aile G : Wind Wing → #7 · Aile D : Wind Wing → #7 | 300 | **585** |

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

### #2 · Bottled Gale · `simplywings:bottled_gale` · partagé

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

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

### #6 · Wind Core · `simplywings:air_core`

`GRID` · niveau 2 · 60 Cloud Essence · donne ×1

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

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

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
| `S` | String `minecraft:string` | 2 | 0 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |

## Pourquoi ces ingrédients

- **Gust Mote** : sur les breezes, les esprits du vent de la 1.21.
- **Bottled Gale** (intermédiaire, partagé avec l'orage et le séraphin) : le vent mis en bouteille avec une charge de vent.
- **Updraft Shard** : les courants ascendants des sommets.
- **Glass Pane** et **String** : des voiles presque invisibles, striées comme des courants d'air.
