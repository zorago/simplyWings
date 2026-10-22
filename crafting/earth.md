# Route de craft · Wings of Stone

`simplywings:earth` · **Épique** · item `earth_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-pierre)

L'esprit de la terre : lourdes, solides, inépuisables. La route descend sous Y 0.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 14 en `GRID` + 1 assemblage |
| Cloud Essence | 315 (pièces) + 300 (assemblage) = **615** |
| Drops du mod | Resonant Shard (BLOCK · Amethyst Cluster (adulte)), Living Rock (BLOCK · Deepslate, Tuff) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Living Rock `simplywings:living_rock` | 12 | `BLOCK` | Deepslate, Tuff | Y ≤ 0 | 5 % |
| Resonant Shard `simplywings:resonant_shard` | 2 | `BLOCK` | Amethyst Cluster (adulte) | — | 30 % |
| Amethyst Shard `minecraft:amethyst_shard` | 4 | `VANILLA` | Géodes d'améthyste | — | — |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Cobbled Deepslate `minecraft:cobbled_deepslate` | 6 | `VANILLA` | Minage sous Y 0 | — | — |
| Ghast Tear `minecraft:ghast_tear` | 1 | `VANILLA` | Ghasts | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Moss Block `minecraft:moss_block` | 6 | `VANILLA` | Grottes luxuriantes, poudre d'os sur la mousse | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Mossbound Stone `simplywings:mossbound_stone` ×6 | `GRID` | 1 | 6 | Living Rock ×2, Moss Block ×1, Cobbled Deepslate ×1 | 6 × 15 | 100 |
| 3 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Tear ×1 | 20 | 120 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 150 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 175 |
| 6 | Stone Core `simplywings:earth_core` ×1 | `GRID` | 2 | 1 | Mossbound Stone ×2 → #2, Gold Ingot ×2, Resonant Shard ×2, Spirit Vessel ×1 → #3 | 60 | 235 |
| 7 | Stone Wing `simplywings:earth_wing` ×2 | `GRID` | 2 | 2 | Mossbound Stone ×2 → #2, Gilded Frame ×1 → #4, Amethyst Shard ×2 | 2 × 40 | 315 |
| 8 | Wings of Stone `simplywings:earth_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Stone Core → #6 · Aile G : Stone Wing → #7 · Aile D : Stone Wing → #7 | 300 | **615** |

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

### #2 · Mossbound Stone · `simplywings:mossbound_stone`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

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

### #6 · Stone Core · `simplywings:earth_core`

`GRID` · niveau 2 · 60 Cloud Essence · donne ×1

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

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

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
