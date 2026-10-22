# Route de craft · Pterosaur Wings

`simplywings:ptero` · **Épique** · item `ptero_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-ptérosaure)

Le planeur préhistorique : une membrane de cuir ocre tendue par un doigt immense.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 14 en `GRID` + 1 assemblage |
| Cloud Essence | 310 (pièces) + 300 (assemblage) = **610** |
| Drops du mod | Fossil Fragment (BLOCK · Bone Block) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Fossil Fragment `simplywings:fossil_fragment` | 9 | `BLOCK` | Bone Block | Y ≤ 40 | 25 % |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Bone Block `minecraft:bone_block` | 3 | `VANILLA` | Fossiles, établi : poudre d'os | — | — |
| Gold Ingot `minecraft:gold_ingot` | 19 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 26 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Nautilus Shell `minecraft:nautilus_shell` | 1 | `VANILLA` | Pêche (trésor), noyés | — | — |
| Orange Dye `minecraft:orange_dye` | 4 | `VANILLA` | Établi : tulipe orange | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 4 | `VANILLA` | Phantoms | — | — |
| Pitcher Plant `minecraft:pitcher_plant` | 1 | `VANILLA` | Gousse déterrée par un renifleur, puis plantée | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Fossil Spar `simplywings:fossil_spar` ×3 | `GRID` | 1 | 3 | Fossil Fragment ×3, Gold Ingot ×1, Bone Block ×1 | 3 × 15 | 55 |
| 3 | Ochre Membrane `simplywings:ochre_membrane` ×4 | `GRID` | 1 | 4 | Leather ×4, Orange Dye ×1, Phantom Membrane ×1 | 4 × 15 | 115 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 145 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 170 |
| 6 | Pterosaur Core `simplywings:ptero_core` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Fossil Spar ×1 → #2, Pitcher Plant ×1, Nautilus Shell ×1 | 60 | 230 |
| 7 | Pterosaur Wing `simplywings:ptero_wing` ×2 | `GRID` | 2 | 2 | Fossil Spar ×1 → #2, Ochre Membrane ×2 → #3, Gilded Frame ×1 → #4 | 2 × 40 | 310 |
| 8 | Pterosaur Wings `simplywings:ptero_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Pterosaur Core → #6 · Aile G : Pterosaur Wing → #7 · Aile D : Pterosaur Wing → #7 | 300 | **610** |

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

### #2 · Fossil Spar · `simplywings:fossil_spar`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
_ F _ _ _
_ _ F G _
_ _ _ F B
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Fossil Fragment `simplywings:fossil_fragment` | 3 | 0 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 1 | 0 |
| `B` | Bone Block `minecraft:bone_block` | 1 | 0 |

### #3 · Ochre Membrane · `simplywings:ochre_membrane`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
_ L O L _
_ L P L _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `L` | Leather `minecraft:leather` | 4 | 0 |
| `O` | Orange Dye `minecraft:orange_dye` | 1 | 0 |
| `P` | Phantom Membrane `minecraft:phantom_membrane` | 1 | 0 |

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

### #6 · Pterosaur Core · `simplywings:ptero_core`

`GRID` · niveau 2 · 60 Cloud Essence · donne ×1

```
. _ _ _ .
_ G S G _
_ G P G _
_ _ N _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `S` | Fossil Spar `simplywings:fossil_spar` | 1 | 1 |
| `P` | Pitcher Plant `minecraft:pitcher_plant` | 1 | 0 |
| `N` | Nautilus Shell `minecraft:nautilus_shell` | 1 | 0 |

### #7 · Pterosaur Wing · `simplywings:ptero_wing`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ S M M _
_ _ F _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `S` | Fossil Spar `simplywings:fossil_spar` | 1 | 1 |
| `M` | Ochre Membrane `simplywings:ochre_membrane` | 2 | 1 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |

## Pourquoi ces ingrédients

- **Fossil Fragment** : sur les blocs d'os des fossiles enfouis. Il donne la **Fossil Spar**, le doigt géant qui porte la pointe de l'aile.
- **Ochre Membrane** (intermédiaire) : cuir, teinture orange et membrane de phantom pour la souplesse.
- **Pitcher Plant** : une plante préhistorique, déterrée par le renifleur.
- **Nautilus Shell** : un fossile vivant, et les océans que traversaient les ptérosaures.
