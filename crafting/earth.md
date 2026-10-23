# Route de craft · Wings of Stone

`simplywings:earth` · **Épique** · item `earth_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-pierre)

L'esprit de la terre : lourdes, solides, inépuisables. La route descend sous Y 0.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 14 en `GRID` + 1 assemblage |
| Essences | 90 nuage + 225 vapeur (pièces) + 150 nuage + 150 vapeur (assemblage) = **240 nuage + 375 vapeur** |
| Matières premières | 8 types du mod (24 objets) · 8 types vanilla (50 objets) |
| Drops du mod | Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Resonant Shard (BLOCK · Amethyst Cluster (adulte)), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Living Rock (BLOCK · Deepslate, Tuff), Ghast Wisp (MOB · Ghast), Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Ember Wisp `simplywings:ember_wisp` | 2 | `MOB` | Blaze | — | 30 % |
| Ghast Wisp `simplywings:ghast_wisp` | 1 | `MOB` | Ghast | — | 50 % |
| Gilded Vein `simplywings:gilded_vein` | 3 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Living Rock `simplywings:living_rock` | 12 | `BLOCK` | Deepslate, Tuff | Y ≤ 0 | 5 % |
| Resonant Shard `simplywings:resonant_shard` | 2 | `BLOCK` | Amethyst Cluster (adulte) | — | 30 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Amethyst Shard `minecraft:amethyst_shard` | 4 | `VANILLA` | Géodes d'améthyste | — | — |
| Blaze Powder `minecraft:blaze_powder` | 2 | `VANILLA` | Établi : bâton de blaze | — | — |
| Cobbled Deepslate `minecraft:cobbled_deepslate` | 6 | `VANILLA` | Minage sous Y 0 | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Moss Block `minecraft:moss_block` | 6 | `VANILLA` | Grottes luxuriantes, poudre d'os sur la mousse | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Mossbound Stone `simplywings:mossbound_stone` ×6 | `GRID` | 1 | 6 | Living Rock ×2, Moss Block ×1, Cobbled Deepslate ×1 | 6 × (15 vapeur) | 10 nuage + 90 vapeur |
| 3 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Wisp ×1 | 20 vapeur | 10 nuage + 110 vapeur |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × (15 vapeur) | 10 nuage + 140 vapeur |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 10 nuage + 15 vapeur | 20 nuage + 155 vapeur |
| 6 | Stone Core `simplywings:earth_core` ×1 | `GRID` | 2 | 1 | Mossbound Stone ×2 → #2, Gold Ingot ×2, Resonant Shard ×2, Spirit Vessel ×1 → #3 | 30 nuage + 30 vapeur | 50 nuage + 185 vapeur |
| 7 | Stone Wing `simplywings:earth_wing` ×2 | `GRID` | 2 | 2 | Mossbound Stone ×2 → #2, Gilded Frame ×1 → #4, Amethyst Shard ×2 | 2 × (20 nuage + 20 vapeur) | 90 nuage + 225 vapeur |
| 8 | Wings of Stone `simplywings:earth_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Stone Core → #6 · Aile G : Stone Wing → #7 · Aile D : Stone Wing → #7 | 150 nuage + 150 vapeur | **240 nuage + 375 vapeur** |

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

### #2 · Mossbound Stone · `simplywings:mossbound_stone`

`GRID` · niveau 1 · essence : 15 vapeur · donne ×1

```
. _ _ _ .
_ R M R _
_ _ D _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `R` | Living Rock `simplywings:living_rock` | 2 | 0 |
| `M` | Moss Block `minecraft:moss_block` | 1 | 0 |
| `D` | Cobbled Deepslate `minecraft:cobbled_deepslate` | 1 | 0 |

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

### #6 · Stone Core · `simplywings:earth_core`

`GRID` · niveau 2 · essence : 30 nuage + 30 vapeur · donne ×1

```
. _ _ _ .
_ M G M _
_ A V A _
_ _ G _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `M` | Mossbound Stone `simplywings:mossbound_stone` | 2 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `A` | Resonant Shard `simplywings:resonant_shard` | 2 | 0 |
| `V` | Spirit Vessel `simplywings:spirit_vessel` | 1 | 1 |

### #7 · Stone Wing · `simplywings:earth_wing`

`GRID` · niveau 2 · essence : 20 nuage + 20 vapeur · donne ×1

```
. _ _ _ .
_ M F M _
_ A _ A _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `M` | Mossbound Stone `simplywings:mossbound_stone` | 2 | 1 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |
| `A` | Amethyst Shard `minecraft:amethyst_shard` | 2 | 0 |

## Pourquoi ces ingrédients

- **Living Rock** : en minant la deepslate et le tuf sous Y 0, au plus profond de la terre.
- **Mossbound Stone** (intermédiaire) : la roche et la mousse près du dos.
- **Resonant Shard** (partagé avec l'améthyste) et **Amethyst Shard** : les éclats qui brillent dans le noir.
- **Cobbled Deepslate** : le poids de la pierre.
