# Route de craft · Nebula Wings

`simplywings:nebula` · **Épique** · item `nebula_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-nébuleuse)

Un morceau de ciel étoilé, le voyage au long cours. La route passe par les cités de l'End.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 14 en `GRID` + 1 assemblage |
| Cloud Essence | 315 (pièces) + 300 (assemblage) = **615** |
| Drops du mod | Stardust (MOB · Shulker) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Stardust `simplywings:stardust` | 12 | `MOB` | Shulker | — | 35 % |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Blue Dye `minecraft:blue_dye` | 4 | `VANILLA` | Établi : lapis ou bleuet | — | — |
| Ghast Tear `minecraft:ghast_tear` | 1 | `VANILLA` | Ghasts | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Glowstone Dust `minecraft:glowstone_dust` | 6 | `VANILLA` | Pierre lumineuse du Nether, sorcières | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Popped Chorus Fruit `minecraft:popped_chorus_fruit` | 2 | `VANILLA` | Fonte du fruit de chorus | — | — |
| Purple Dye `minecraft:purple_dye` | 6 | `VANILLA` | Établi : rouge + bleu | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Nebula Cloud `simplywings:nebula_cloud` ×6 | `GRID` | 1 | 6 | Stardust ×2, Glowstone Dust ×1, Purple Dye ×1 | 6 × 15 | 100 |
| 3 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Tear ×1 | 20 | 120 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 150 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 175 |
| 6 | Nebula Core `simplywings:nebula_core` ×1 | `GRID` | 2 | 1 | Nebula Cloud ×2 → #2, Gold Ingot ×2, Popped Chorus Fruit ×2, Spirit Vessel ×1 → #3 | 60 | 235 |
| 7 | Nebula Wing `simplywings:nebula_wing` ×2 | `GRID` | 2 | 2 | Nebula Cloud ×2 → #2, Gilded Frame ×1 → #4, Blue Dye ×2 | 2 × 40 | 315 |
| 8 | Nebula Wings `simplywings:nebula_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Nebula Core → #6 · Aile G : Nebula Wing → #7 · Aile D : Nebula Wing → #7 | 300 | **615** |

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

### #2 · Nebula Cloud · `simplywings:nebula_cloud`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
_ S G S _
_ _ U _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `S` | Stardust `simplywings:stardust` | 2 | 0 |
| `G` | Glowstone Dust `minecraft:glowstone_dust` | 1 | 0 |
| `U` | Purple Dye `minecraft:purple_dye` | 1 | 0 |

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

### #6 · Nebula Core · `simplywings:nebula_core`

`GRID` · niveau 2 · 60 Cloud Essence · donne ×1

```
. _ _ _ .
_ N G N _
_ C V C _
_ _ G _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `N` | Nebula Cloud `simplywings:nebula_cloud` | 2 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `C` | Popped Chorus Fruit `minecraft:popped_chorus_fruit` | 2 | 0 |
| `V` | Spirit Vessel `simplywings:spirit_vessel` | 1 | 1 |

### #7 · Nebula Wing · `simplywings:nebula_wing`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ N F N _
_ B _ B _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `N` | Nebula Cloud `simplywings:nebula_cloud` | 2 | 1 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |
| `B` | Blue Dye `minecraft:blue_dye` | 2 | 0 |

## Pourquoi ces ingrédients

- **Stardust** : sur les shulkers, les gardiens des cités de l'End, au bout du voyage.
- **Nebula Cloud** (intermédiaire) : poussière d'étoile, pierre lumineuse et violet, un nuage d'étoiles.
- **Popped Chorus Fruit** : la flore de l'End, qui fait voyager.
- **Blue Dye** : le bleu nuit de la nébuleuse.
