# Route de craft · Wings of the Tide

`simplywings:water` · **Épique** · item `water_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-la-marée)

L'esprit de l'eau, fluide comme une nage. La route passe par les monuments océaniques.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 14 en `GRID` + 1 assemblage |
| Cloud Essence | 315 (pièces) + 300 (assemblage) = **615** |
| Drops du mod | Tide Pearl (MOB · Guardian / Elder Guardian) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Tide Pearl `simplywings:tide_pearl` | 12 | `MOB` | Guardian / Elder Guardian | — | 20 % / 100 % ×3 |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Ghast Tear `minecraft:ghast_tear` | 1 | `VANILLA` | Ghasts | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Nautilus Shell `minecraft:nautilus_shell` | 2 | `VANILLA` | Pêche (trésor), noyés | — | — |
| Prismarine Crystals `minecraft:prismarine_crystals` | 6 | `VANILLA` | Gardiens | — | — |
| Prismarine Shard `minecraft:prismarine_shard` | 10 | `VANILLA` | Gardiens | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Flowing Tide `simplywings:flowing_tide` ×6 | `GRID` | 1 | 6 | Tide Pearl ×2, Prismarine Crystals ×1, Prismarine Shard ×1 | 6 × 15 | 100 |
| 3 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Tear ×1 | 20 | 120 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 150 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 175 |
| 6 | Tide Core `simplywings:water_core` ×1 | `GRID` | 2 | 1 | Flowing Tide ×2 → #2, Gold Ingot ×2, Nautilus Shell ×2, Spirit Vessel ×1 → #3 | 60 | 235 |
| 7 | Tide Wing `simplywings:water_wing` ×2 | `GRID` | 2 | 2 | Flowing Tide ×2 → #2, Gilded Frame ×1 → #4, Prismarine Shard ×2 | 2 × 40 | 315 |
| 8 | Wings of the Tide `simplywings:water_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #5 · Cœur : Tide Core → #6 · Aile G : Tide Wing → #7 · Aile D : Tide Wing → #7 | 300 | **615** |

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

### #2 · Flowing Tide · `simplywings:flowing_tide`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
_ T C T _
_ _ S _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `T` | Tide Pearl `simplywings:tide_pearl` | 2 | 0 |
| `C` | Prismarine Crystals `minecraft:prismarine_crystals` | 1 | 0 |
| `S` | Prismarine Shard `minecraft:prismarine_shard` | 1 | 0 |

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

### #6 · Tide Core · `simplywings:water_core`

`GRID` · niveau 2 · 60 Cloud Essence · donne ×1

```
. _ _ _ .
_ T G T _
_ N V N _
_ _ G _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `T` | Flowing Tide `simplywings:flowing_tide` | 2 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `N` | Nautilus Shell `minecraft:nautilus_shell` | 2 | 0 |
| `V` | Spirit Vessel `simplywings:spirit_vessel` | 1 | 1 |

### #7 · Tide Wing · `simplywings:water_wing`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ T F T _
_ S _ S _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `T` | Flowing Tide `simplywings:flowing_tide` | 2 | 1 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |
| `S` | Prismarine Shard `minecraft:prismarine_shard` | 2 | 0 |

## Pourquoi ces ingrédients

- **Tide Pearl** : sur les gardiens (et 3 par gardien ancien), les sentinelles de l'océan.
- **Flowing Tide** (intermédiaire) : perle de marée et prismarine, l'eau qui garde sa forme.
- **Nautilus Shell** : la spirale de la vague.
- **Prismarine Shard** : le liseré d'écume au bord de l'aile.
