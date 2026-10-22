# Route de craft · Wings of Shadow

`simplywings:shadow` · **Épique** · item `shadow_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-dombre)

L'esprit des ténèbres, une fumée noire aux yeux violets. La route se fait dans le noir.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 14 en `GRID` + 1 assemblage |
| Cloud Essence | 315 (pièces) + 300 (assemblage) = **615** |
| Drops du mod | Shade Wisp (MOB · Enderman) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Shade Wisp `simplywings:shade_wisp` | 12 | `MOB` | Enderman | Lumière ≤ 7 à l'endroit du kill | 25 % |
| Black Dye `minecraft:black_dye` | 4 | `VANILLA` | Établi : poche d'encre | — | — |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Ender Pearl `minecraft:ender_pearl` | 6 | `VANILLA` | Endermen | — | — |
| Eye of Ender `minecraft:ender_eye` | 2 | `VANILLA` | Établi : perle de l'Ender + poudre de blaze | — | — |
| Ghast Tear `minecraft:ghast_tear` | 1 | `VANILLA` | Ghasts | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Ink Sac `minecraft:ink_sac` | 6 | `VANILLA` | Poulpes | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Condensed Shadow `simplywings:condensed_shadow` ×6 | `GRID` | 1 | 6 | Shade Wisp ×2, Ink Sac ×1, Ender Pearl ×1 | 6 × 15 | 100 |
| 3 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Tear ×1 | 20 | 120 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 150 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 175 |
| 6 | Shadow Core `simplywings:shadow_core` ×1 | `GRID` | 2 | 1 | Condensed Shadow ×2 → #2, Gold Ingot ×2, Eye of Ender ×2, Spirit Vessel ×1 → #3 | 60 | 235 |
| 7 | Shadow Wing `simplywings:shadow_wing` ×2 | `GRID` | 2 | 2 | Condensed Shadow ×2 → #2, Gilded Frame ×1 → #4, Black Dye ×2 | 2 × 40 | 315 |
| 8 | Wings of Shadow `simplywings:shadow_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Shadow Core → #6 · Aile G : Shadow Wing → #7 · Aile D : Shadow Wing → #7 | 300 | **615** |

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

### #2 · Condensed Shadow · `simplywings:condensed_shadow`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
_ S K S _
_ _ P _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `S` | Shade Wisp `simplywings:shade_wisp` | 2 | 0 |
| `K` | Ink Sac `minecraft:ink_sac` | 1 | 0 |
| `P` | Ender Pearl `minecraft:ender_pearl` | 1 | 0 |

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

### #6 · Shadow Core · `simplywings:shadow_core`

`GRID` · niveau 2 · 60 Cloud Essence · donne ×1

```
. _ _ _ .
_ C G C _
_ E V E _
_ _ G _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `C` | Condensed Shadow `simplywings:condensed_shadow` | 2 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `E` | Eye of Ender `minecraft:ender_eye` | 2 | 0 |
| `V` | Spirit Vessel `simplywings:spirit_vessel` | 1 | 1 |

### #7 · Shadow Wing · `simplywings:shadow_wing`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ C F C _
_ B _ B _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `C` | Condensed Shadow `simplywings:condensed_shadow` | 2 | 1 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |
| `B` | Black Dye `minecraft:black_dye` | 2 | 0 |

## Pourquoi ces ingrédients

- **Shade Wisp** : sur un enderman tué **dans l'obscurité** (lumière ≤ 7). Les yeux violets dans la fumée sont les leurs.
- **Condensed Shadow** (intermédiaire) : l'ombre, l'encre et une perle de l'Ender, qui permet de se faufiler.
- **Eye of Ender** : les yeux violets qui brillent dans la fumée.
- **Black Dye** : la fumée noire.
