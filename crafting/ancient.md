# Route de craft · Meganeura Wings

`simplywings:ancient` · **Épique** · item `ancient_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-méganeure)

La libellule géante du Carbonifère, prise dans l'ambre. La route passe par les fossiles et le renifleur, la créature ancienne de Minecraft.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 11 en `GRID` + 1 assemblage |
| Cloud Essence | 265 (pièces) + 300 (assemblage) = **565** |
| Matières premières | 7 types du mod (29 objets) · 7 types vanilla (39 objets) |
| Drops du mod | Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Raw Resin (BLOCK · Spruce Log), Fossil Fragment (BLOCK · Bone Block), Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Ember Wisp `simplywings:ember_wisp` | 2 | `MOB` | Blaze | — | 30 % |
| Fossil Fragment `simplywings:fossil_fragment` | 4 | `BLOCK` | Bone Block | Y ≤ 40 | 25 % |
| Gilded Vein `simplywings:gilded_vein` | 3 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Raw Resin `simplywings:raw_resin` | 16 | `BLOCK` | Spruce Log | Biomes `old_growth_pine_taiga`, `old_growth_spruce_taiga` | 12 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Blaze Powder `minecraft:blaze_powder` | 2 | `VANILLA` | Établi : bâton de blaze | — | — |
| Brown Dye `minecraft:brown_dye` | 2 | `VANILLA` | Établi : fèves de cacao | — | — |
| Glass Pane `minecraft:glass_pane` | 10 | `VANILLA` | Établi : verre | — | — |
| Gold Ingot `minecraft:gold_ingot` | 14 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Torchflower `minecraft:torchflower` | 1 | `VANILLA` | Graine déterrée par un renifleur, puis plantée | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Fossil Amber `simplywings:fossil_amber` ×8 | `GRID` | 1 | 4 | Raw Resin ×4, Fossil Fragment ×1 | 4 × 15 | 70 |
| 3 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × 15 | 100 |
| 4 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 25 | 125 |
| 5 | Meganeura Core `simplywings:ancient_core` ×1 | `GRID` | 2 | 1 | Fossil Amber ×4 → #2, Gold Ingot ×2, Torchflower ×1 | 60 | 185 |
| 6 | Meganeura Wing `simplywings:ancient_wing` ×2 | `GRID` | 2 | 2 | Glass Pane ×5, Fossil Amber ×2 → #2, Gilded Frame ×1 → #3, Brown Dye ×1 | 2 × 40 | 265 |
| 7 | Meganeura Wings `simplywings:ancient_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #4 · Cœur : Meganeura Core → #5 · Aile G : Meganeura Wing → #6 · Aile D : Meganeura Wing → #6 | 300 | **565** |

## Recettes

Grille 5×5 sans les coins : `.` = coin absent, `_` = case vide.

### #1 · Iron-Buckled Harness · `simplywings:iron_harness` · partagé

`GRID` · niveau 1 · 10 Cloud Essence · donne ×1

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

### #2 · Fossil Amber · `simplywings:fossil_amber`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×2

```
. _ _ _ .
_ R _ R _
_ _ F _ _
_ R _ R _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `R` | Raw Resin `simplywings:raw_resin` | 4 | 0 |
| `F` | Fossil Fragment `simplywings:fossil_fragment` | 1 | 0 |

### #3 · Gilded Frame · `simplywings:gilded_frame` · partagé

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

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

### #4 · Gilded Harness · `simplywings:gilded_harness` · partagé

`GRID` · niveau 2 · 25 Cloud Essence · donne ×1

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

### #5 · Meganeura Core · `simplywings:ancient_core`

`GRID` · niveau 2 · 60 Cloud Essence · donne ×1

```
. _ _ _ .
_ A G A _
_ _ T _ _
_ A G A _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `A` | Fossil Amber `simplywings:fossil_amber` | 4 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `T` | Torchflower `minecraft:torchflower` | 1 | 0 |

### #6 · Meganeura Wing · `simplywings:ancient_wing`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. P P P .
_ A F A _
_ P B P _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Glass Pane `minecraft:glass_pane` | 5 | 0 |
| `A` | Fossil Amber `simplywings:fossil_amber` | 2 | 1 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |
| `B` | Brown Dye `minecraft:brown_dye` | 1 | 0 |

## Pourquoi ces ingrédients

- **Raw Resin** : sur les épicéas des vieilles taïgas, les forêts les plus anciennes du jeu. **Fossil Fragment** : sur les blocs d'os des fossiles enfouis (Y ≤ 40). Objet partagé avec le ptérosaure.
- **Fossil Amber** (intermédiaire) : résine et fossile, l'ambre qui a figé la méganeure.
- **Torchflower** : une fleur disparue, ressuscitée par le renifleur. Les libellules vivent près des fleurs.
- **Glass Pane** et **Brown Dye** : la membrane translucide veinée de brun fossile, montée sur la **Gilded Frame**.
