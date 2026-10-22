# Route de craft · Wings of Flame

`simplywings:fire` · **Épique** · item `fire_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-flamme)

L'esprit du feu : des coups secs et explosifs. La route passe par les forteresses du Nether.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 14 en `GRID` + 1 assemblage |
| Cloud Essence | 315 (pièces) + 300 (assemblage) = **615** |
| Drops du mod | Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Ember Wisp `simplywings:ember_wisp` | 12 | `MOB` | Blaze | — | 30 % |
| Blaze Powder `minecraft:blaze_powder` | 14 | `VANILLA` | Établi : bâton de blaze | — | — |
| Fire Charge `minecraft:fire_charge` | 2 | `VANILLA` | Établi : poudre de blaze + poudre à canon + charbon | — | — |
| Ghast Tear `minecraft:ghast_tear` | 1 | `VANILLA` | Ghasts | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Magma Cream `minecraft:magma_cream` | 6 | `VANILLA` | Cubes de magma | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Living Flame `simplywings:living_flame` ×6 | `GRID` | 1 | 6 | Ember Wisp ×2, Blaze Powder ×1, Magma Cream ×1 | 6 × 15 | 100 |
| 3 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Tear ×1 | 20 | 120 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 150 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 175 |
| 6 | Flame Core `simplywings:fire_core` ×1 | `GRID` | 2 | 1 | Living Flame ×2 → #2, Gold Ingot ×2, Fire Charge ×2, Spirit Vessel ×1 → #3 | 60 | 235 |
| 7 | Flame Wing `simplywings:fire_wing` ×2 | `GRID` | 2 | 2 | Living Flame ×2 → #2, Gilded Frame ×1 → #4, Blaze Powder ×2 | 2 × 40 | 315 |
| 8 | Wings of Flame `simplywings:fire_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Flame Core → #6 · Aile G : Flame Wing → #7 · Aile D : Flame Wing → #7 | 300 | **615** |

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

### #2 · Living Flame · `simplywings:living_flame`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
_ E P E _
_ _ M _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `E` | Ember Wisp `simplywings:ember_wisp` | 2 | 0 |
| `P` | Blaze Powder `minecraft:blaze_powder` | 1 | 0 |
| `M` | Magma Cream `minecraft:magma_cream` | 1 | 0 |

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

### #6 · Flame Core · `simplywings:fire_core`

`GRID` · niveau 2 · 60 Cloud Essence · donne ×1

```
. _ _ _ .
_ L G L _
_ C V C _
_ _ G _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `L` | Living Flame `simplywings:living_flame` | 2 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `C` | Fire Charge `minecraft:fire_charge` | 2 | 0 |
| `V` | Spirit Vessel `simplywings:spirit_vessel` | 1 | 1 |

### #7 · Flame Wing · `simplywings:fire_wing`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ L F L _
_ P _ P _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `L` | Living Flame `simplywings:living_flame` | 2 | 1 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |
| `P` | Blaze Powder `minecraft:blaze_powder` | 2 | 0 |

## Pourquoi ces ingrédients

- **Ember Wisp** : sur les blazes, les esprits de feu de Minecraft.
- **Living Flame** (intermédiaire) : la braise, la poudre de blaze et la crème de magma, le feu qui tient en forme.
- **Fire Charge** : les coups explosifs de la paire.
- Le cœur enferme les flammes dans le **Spirit Vessel** (partagé par tous les esprits) ; l'aile se monte sur la **Gilded Frame**.
