# Route de craft · Pterosaur Wings

`simplywings:ptero` · **Épique** · item `ptero_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-ptérosaure)

Le planeur préhistorique : une membrane de cuir ocre tendue par un doigt immense.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 14 en `GRID` + 1 assemblage |
| Essences | 90 nuage + 220 vapeur (pièces) + 150 nuage + 150 vapeur (assemblage) = **240 nuage + 370 vapeur** |
| Matières premières | 8 types du mod (25 objets) · 7 types vanilla (53 objets) |
| Drops du mod | Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Fossil Fragment (BLOCK · Bone Block), Hollow Bone (MOB · Skeleton, Stray, Bogged), Elastic Gel (MOB · Slime, Magma Cube), Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Elastic Gel `simplywings:elastic_gel` | 4 | `MOB` | Slime, Magma Cube | — | 40 % |
| Ember Wisp `simplywings:ember_wisp` | 2 | `MOB` | Blaze | — | 30 % |
| Fossil Fragment `simplywings:fossil_fragment` | 9 | `BLOCK` | Bone Block | Y ≤ 40 | 25 % |
| Gilded Vein `simplywings:gilded_vein` | 3 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Hollow Bone `simplywings:hollow_bone` | 3 | `MOB` | Skeleton, Stray, Bogged | — | 35 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Blaze Powder `minecraft:blaze_powder` | 2 | `VANILLA` | Établi : bâton de blaze | — | — |
| Gold Ingot `minecraft:gold_ingot` | 19 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 24 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Nautilus Shell `minecraft:nautilus_shell` | 1 | `VANILLA` | Pêche (trésor), noyés | — | — |
| Orange Dye `minecraft:orange_dye` | 4 | `VANILLA` | Établi : tulipe orange | — | — |
| Pitcher Plant `minecraft:pitcher_plant` | 1 | `VANILLA` | Gousse déterrée par un renifleur, puis plantée | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Fossil Spar `simplywings:fossil_spar` ×3 | `GRID` | 1 | 3 | Fossil Fragment ×3, Gold Ingot ×1, Hollow Bone ×1 | 3 × (15 vapeur) | 10 nuage + 45 vapeur |
| 3 | Ochre Membrane `simplywings:ochre_membrane` ×4 | `GRID` | 1 | 4 | Leather ×4, Orange Dye ×1, Elastic Gel ×1 | 4 × (15 vapeur) | 10 nuage + 105 vapeur |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × (15 vapeur) | 10 nuage + 135 vapeur |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 10 nuage + 15 vapeur | 20 nuage + 150 vapeur |
| 6 | Pterosaur Core `simplywings:ptero_core` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Fossil Spar ×1 → #2, Pitcher Plant ×1, Nautilus Shell ×1 | 30 nuage + 30 vapeur | 50 nuage + 180 vapeur |
| 7 | Pterosaur Wing `simplywings:ptero_wing` ×2 | `GRID` | 2 | 2 | Fossil Spar ×1 → #2, Ochre Membrane ×2 → #3, Gilded Frame ×1 → #4 | 2 × (20 nuage + 20 vapeur) | 90 nuage + 220 vapeur |
| 8 | Pterosaur Wings `simplywings:ptero_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Pterosaur Core → #6 · Aile G : Pterosaur Wing → #7 · Aile D : Pterosaur Wing → #7 | 150 nuage + 150 vapeur | **240 nuage + 370 vapeur** |

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

### #2 · Fossil Spar · `simplywings:fossil_spar`

`GRID` · niveau 1 · essence : 15 vapeur · donne ×1

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
| `B` | Hollow Bone `simplywings:hollow_bone` | 1 | 0 |

### #3 · Ochre Membrane · `simplywings:ochre_membrane`

`GRID` · niveau 1 · essence : 15 vapeur · donne ×1

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
| `P` | Elastic Gel `simplywings:elastic_gel` | 1 | 0 |

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

### #6 · Pterosaur Core · `simplywings:ptero_core`

`GRID` · niveau 2 · essence : 30 nuage + 30 vapeur · donne ×1

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

`GRID` · niveau 2 · essence : 20 nuage + 20 vapeur · donne ×1

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

- **Fossil Fragment** : sur les blocs d'os des fossiles enfouis. Avec un **Hollow Bone**, il donne la **Fossil Spar**, le doigt géant qui porte la pointe de l'aile.
- **Ochre Membrane** (intermédiaire) : cuir, teinture orange et **Elastic Gel** pour la souplesse.
- **Pitcher Plant** : une plante préhistorique, déterrée par le renifleur.
- **Nautilus Shell** : un fossile vivant, et les océans que traversaient les ptérosaures.
