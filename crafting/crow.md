# Route de craft · Crow Wings

`simplywings:crow` · **Rare** · item `crow_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-corbeau)

Rusé et vif, noir à reflets bleutés. Le corbeau pille les champs et collectionne ce qui brille.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Drops du mod | Crow Feather (BLOCK · Wheat mûr (`age=7`)) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Crow Feather `simplywings:crow_feather` | 8 | `BLOCK` | Wheat mûr (`age=7`) | — | 10 % |
| Emerald `minecraft:emerald` | 2 | `VANILLA` | Minage, commerce villageois | — | — |
| Feather `minecraft:feather` | 8 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Ink Sac `minecraft:ink_sac` | 4 | `VANILLA` | Poulpes | — | — |
| Iron Ingot `minecraft:iron_ingot` | 9 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Spyglass `minecraft:spyglass` | 1 | `VANILLA` | Établi : cuivre + améthyste | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Crow Core `simplywings:crow_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Crow Feather ×2, Emerald ×2, Spyglass ×1 | 30 | 40 |
| 3 | Crow Wing `simplywings:crow_wing` ×2 | `GRID` | 1 | 2 | Feather ×4, Crow Feather ×3, Ink Sac ×2, Iron Ingot ×1 | 2 × 15 | 70 |
| 4 | Crow Wings `simplywings:crow_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Crow Core → #2 · Aile G : Crow Wing → #3 · Aile D : Crow Wing → #3 | 100 | **170** |

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

### #2 · Crow Core · `simplywings:crow_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ _ _ .
_ I F I _
_ E P E _
_ I F I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `F` | Crow Feather `simplywings:crow_feather` | 2 | 0 |
| `E` | Emerald `minecraft:emerald` | 2 | 0 |
| `P` | Spyglass `minecraft:spyglass` | 1 | 0 |

### #3 · Crow Wing · `simplywings:crow_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. F C F .
_ F C F _
_ K I K _
_ _ C _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 4 | 0 |
| `C` | Crow Feather `simplywings:crow_feather` | 3 | 0 |
| `K` | Ink Sac `minecraft:ink_sac` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Crow Feather** : en moissonnant le blé mûr. Les corbeaux s'invitent dans les champs, l'épouvantail n'y peut rien. Une ferme de blé et de la poudre d'os suffisent.
- **Emerald** : le corbeau collectionne ce qui brille ; les émeraudes se troquent au village, à côté des champs.
- **Spyglass** : l'œil perçant du corvidé, qui repère tout de loin.
- **Ink Sac** : le noir mat des rémiges.
