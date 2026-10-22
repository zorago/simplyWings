# Route de craft · Wings of Psyche

`simplywings:psyche` · **Légendaire** · item `psyche_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-psyché)

Psyché, la déesse de l'âme aux ailes de papillon : des flammes d'âme qui tiennent en apesanteur.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 3 niveaux de craft avant les pièces (max 3 en légendaire) |
| Crafts | 24 en `GRID` + 1 assemblage |
| Cloud Essence | 845 (pièces) + 800 (assemblage) = **1645** |
| Drops du mod | Soul Wisp (BLOCK · Soul Sand, Soul Soil) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Soul Wisp `simplywings:soul_wisp` | 10 | `BLOCK` | Soul Sand, Soul Soil | Biome `soul_sand_valley` | 8 % |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Diamond `minecraft:diamond` | 17 | `VANILLA` | Minage du diamant | — | — |
| End Rod `minecraft:end_rod` | 4 | `VANILLA` | Établi : bâton de blaze + chorus éclaté | — | — |
| Ender Pearl `minecraft:ender_pearl` | 4 | `VANILLA` | Endermen | — | — |
| Glass Bottle `minecraft:glass_bottle` | 5 | `VANILLA` | Établi : verre | — | — |
| Glow Ink Sac `minecraft:glow_ink_sac` | 4 | `VANILLA` | Poulpes luisants | — | — |
| Gold Ingot `minecraft:gold_ingot` | 16 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 6 | `VANILLA` | Phantoms | — | — |
| Shulker Shell `minecraft:shulker_shell` | 8 | `VANILLA` | Shulkers (cités de l'End) | — | — |
| Soul Lantern `minecraft:soul_lantern` | 5 | `VANILLA` | Établi : torche des âmes + pépites de fer | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Weightless Pearl `simplywings:weightless_pearl` ×4 | `GRID` | 1 | 4 | Shulker Shell ×2, Ender Pearl ×1, Phantom Membrane ×1 | 4 × 25 | 110 |
| 3 | Soul Flame `simplywings:soul_flame` ×10 | `GRID` | 1 | 5 | Soul Wisp ×2, Soul Lantern ×1, Glass Bottle ×1 | 5 × 20 | 210 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 240 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 265 |
| 6 | Psyche's Lamp `simplywings:psyche_lamp` ×1 | `GRID` | 2 | 1 | Diamond ×1, Soul Flame ×2 → #3, Gold Ingot ×4 | 40 | 305 |
| 7 | Soul Veil `simplywings:soul_veil` ×4 | `GRID` | 2 | 4 | Soul Flame ×2 → #3, Glow Ink Sac ×1, Diamond ×1 | 4 × 40 | 465 |
| 8 | Celestial Frame `simplywings:celestial_frame` ×2 | `GRID` | 2 | 2 | End Rod ×2, Diamond ×2, Gilded Frame ×1 → #4 | 2 × 35 | 535 |
| 9 | Radiant Harness `simplywings:radiant_harness` ×1 | `GRID` | 3 | 1 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1 → #5 | 50 | 585 |
| 10 | Psyche Core `simplywings:psyche_core` ×1 | `GRID` | 3 | 1 | Weightless Pearl ×2 → #2, Psyche's Lamp ×1 → #6, Diamond ×4 | 100 | 685 |
| 11 | Psyche Wing `simplywings:psyche_wing` ×2 | `GRID` | 3 | 2 | Soul Veil ×2 → #7, Celestial Frame ×1 → #8, Weightless Pearl ×1 → #2 | 2 × 80 | 845 |
| 12 | Wings of Psyche `simplywings:psyche_wings` | `ASM` | — | 1 | Harnais : Radiant Harness → #9 · Cœur : Psyche Core → #10 · Aile G : Psyche Wing → #11 · Aile D : Psyche Wing → #11 | 800 | **1645** |

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

### #2 · Weightless Pearl · `simplywings:weightless_pearl`

`GRID` · niveau 1 · 25 Cloud Essence · donne ×1

```
. _ _ _ .
_ S P S _
_ _ M _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `S` | Shulker Shell `minecraft:shulker_shell` | 2 | 0 |
| `P` | Ender Pearl `minecraft:ender_pearl` | 1 | 0 |
| `M` | Phantom Membrane `minecraft:phantom_membrane` | 1 | 0 |

### #3 · Soul Flame · `simplywings:soul_flame`

`GRID` · niveau 1 · 20 Cloud Essence · donne ×2

```
. _ _ _ .
_ W L W _
_ _ B _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `W` | Soul Wisp `simplywings:soul_wisp` | 2 | 0 |
| `L` | Soul Lantern `minecraft:soul_lantern` | 1 | 0 |
| `B` | Glass Bottle `minecraft:glass_bottle` | 1 | 0 |

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

### #6 · Psyche's Lamp · `simplywings:psyche_lamp`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ _ D _ _
_ F G F _
_ G G G _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `D` | Diamond `minecraft:diamond` | 1 | 0 |
| `F` | Soul Flame `simplywings:soul_flame` | 2 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |

### #7 · Soul Veil · `simplywings:soul_veil`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ F K F _
_ _ D _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Soul Flame `simplywings:soul_flame` | 2 | 1 |
| `K` | Glow Ink Sac `minecraft:glow_ink_sac` | 1 | 0 |
| `D` | Diamond `minecraft:diamond` | 1 | 0 |

### #8 · Celestial Frame · `simplywings:celestial_frame` · partagé

`GRID` · niveau 2 · 35 Cloud Essence · donne ×1

```
. _ _ _ .
_ E _ E _
_ D F D _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `E` | End Rod `minecraft:end_rod` | 2 | 0 |
| `D` | Diamond `minecraft:diamond` | 2 | 0 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |

### #9 · Radiant Harness · `simplywings:radiant_harness` · partagé

`GRID` · niveau 3 · 50 Cloud Essence · donne ×1

```
. D _ D .
_ P H P _
_ D _ D _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `D` | Diamond `minecraft:diamond` | 4 | 0 |
| `P` | Phantom Membrane `minecraft:phantom_membrane` | 2 | 0 |
| `H` | Gilded Harness `simplywings:gilded_harness` | 1 | 2 |

### #10 · Psyche Core · `simplywings:psyche_core`

`GRID` · niveau 3 · 100 Cloud Essence · donne ×1

```
. _ _ _ .
_ W L W _
_ D _ D _
_ D _ D _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `W` | Weightless Pearl `simplywings:weightless_pearl` | 2 | 1 |
| `L` | Psyche's Lamp `simplywings:psyche_lamp` | 1 | 2 |
| `D` | Diamond `minecraft:diamond` | 4 | 0 |

### #11 · Psyche Wing · `simplywings:psyche_wing`

`GRID` · niveau 3 · 80 Cloud Essence · donne ×1

```
. _ _ _ .
_ V C V _
_ _ W _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `V` | Soul Veil `simplywings:soul_veil` | 2 | 2 |
| `C` | Celestial Frame `simplywings:celestial_frame` | 1 | 2 |
| `W` | Weightless Pearl `simplywings:weightless_pearl` | 1 | 1 |

## Pourquoi ces ingrédients

- **Soul Wisp** : en creusant le sable et la terre des âmes dans la vallée des âmes.
- **Soul Flame** (intermédiaire) : l'âme mise en flacon avec une lanterne des âmes, la flamme bleu-cyan de la paire.
- **Psyche's Lamp** : dans le mythe, Psyché allume une lampe à huile pour voir le visage d'Éros. C'est le cœur de la paire.
- **Weightless Pearl** (intermédiaire) : carapaces de shulker et perle de l'Ender. La lévitation des shulkers, c'est l'apesanteur de la stabilité maxée.
