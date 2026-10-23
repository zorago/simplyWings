# Route de craft · Wings of Shadow

`simplywings:shadow` · **Épique** · item `shadow_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-dombre)

L'esprit des ténèbres, une fumée noire aux yeux violets. La route se fait dans le noir.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 14 en `GRID` + 1 assemblage |
| Essences | 90 nuage + 225 vapeur (pièces) + 150 nuage + 150 vapeur (assemblage) = **240 nuage + 375 vapeur** |
| Matières premières | 8 types du mod (28 objets) · 8 types vanilla (46 objets) |
| Drops du mod | Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Shade Wisp (MOB · Enderman), Abyssal Ink (MOB · Squid, Glow Squid), Ghast Wisp (MOB · Ghast), Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Abyssal Ink `simplywings:abyssal_ink` | 6 | `MOB` | Squid, Glow Squid | — | 40 % |
| Ember Wisp `simplywings:ember_wisp` | 2 | `MOB` | Blaze | — | 30 % |
| Ghast Wisp `simplywings:ghast_wisp` | 1 | `MOB` | Ghast | — | 50 % |
| Gilded Vein `simplywings:gilded_vein` | 3 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Shade Wisp `simplywings:shade_wisp` | 12 | `MOB` | Enderman | Lumière ≤ 7 à l'endroit du kill | 25 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Black Dye `minecraft:black_dye` | 4 | `VANILLA` | Établi : poche d'encre | — | — |
| Blaze Powder `minecraft:blaze_powder` | 2 | `VANILLA` | Établi : bâton de blaze | — | — |
| Ender Pearl `minecraft:ender_pearl` | 6 | `VANILLA` | Endermen | — | — |
| Eye of Ender `minecraft:ender_eye` | 2 | `VANILLA` | Établi : perle de l'Ender + poudre de blaze | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Condensed Shadow `simplywings:condensed_shadow` ×6 | `GRID` | 1 | 6 | Shade Wisp ×2, Abyssal Ink ×1, Ender Pearl ×1 | 6 × (15 vapeur) | 10 nuage + 90 vapeur |
| 3 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Wisp ×1 | 20 vapeur | 10 nuage + 110 vapeur |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × (15 vapeur) | 10 nuage + 140 vapeur |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 10 nuage + 15 vapeur | 20 nuage + 155 vapeur |
| 6 | Shadow Core `simplywings:shadow_core` ×1 | `GRID` | 2 | 1 | Condensed Shadow ×2 → #2, Gold Ingot ×2, Eye of Ender ×2, Spirit Vessel ×1 → #3 | 30 nuage + 30 vapeur | 50 nuage + 185 vapeur |
| 7 | Shadow Wing `simplywings:shadow_wing` ×2 | `GRID` | 2 | 2 | Condensed Shadow ×2 → #2, Gilded Frame ×1 → #4, Black Dye ×2 | 2 × (20 nuage + 20 vapeur) | 90 nuage + 225 vapeur |
| 8 | Wings of Shadow `simplywings:shadow_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Shadow Core → #6 · Aile G : Shadow Wing → #7 · Aile D : Shadow Wing → #7 | 150 nuage + 150 vapeur | **240 nuage + 375 vapeur** |

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

### #2 · Condensed Shadow · `simplywings:condensed_shadow`

`GRID` · niveau 1 · essence : 15 vapeur · donne ×1

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
| `K` | Abyssal Ink `simplywings:abyssal_ink` | 1 | 0 |
| `P` | Ender Pearl `minecraft:ender_pearl` | 1 | 0 |

### #3 · Spirit Vessel · `simplywings:spirit_vessel` · partagé

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

### #6 · Shadow Core · `simplywings:shadow_core`

`GRID` · niveau 2 · essence : 30 nuage + 30 vapeur · donne ×1

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

`GRID` · niveau 2 · essence : 20 nuage + 20 vapeur · donne ×1

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
- **Condensed Shadow** (intermédiaire) : l'ombre, l'**Abyssal Ink** des calmars et une perle de l'Ender, qui permet de se faufiler.
- **Eye of Ender** : les yeux violets qui brillent dans la fumée.
- **Black Dye** : la fumée noire.
