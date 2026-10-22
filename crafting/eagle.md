# Route de craft · Eagle Wings

`simplywings:eagle` · **Rare** · item `eagle_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-daigle)

Le rapace des sommets : des coups puissants qui arrachent du sol.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Drops du mod | Raptor Talon (MOB · Goat), Updraft Shard (BLOCK · `#minecraft:base_stone_overworld`) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Raptor Talon `simplywings:raptor_talon` | 2 | `MOB` | Goat | Tuée à Y ≥ 120 | 35 % |
| Updraft Shard `simplywings:updraft_shard` | 4 | `BLOCK` | `#minecraft:base_stone_overworld` | Y ≥ 150 | 8 % |
| Brown Dye `minecraft:brown_dye` | 4 | `VANILLA` | Établi : fèves de cacao | — | — |
| Feather `minecraft:feather` | 10 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Gold Nugget `minecraft:gold_nugget` | 4 | `VANILLA` | Établi : lingot d'or, piglins zombifiés | — | — |
| Iron Ingot `minecraft:iron_ingot` | 9 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Rabbit's Foot `minecraft:rabbit_foot` | 1 | `VANILLA` | Lapins | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Eagle Core `simplywings:eagle_core` ×1 | `GRID` | 1 | 1 | Raptor Talon ×2, Iron Ingot ×4, Updraft Shard ×2, Rabbit's Foot ×1 | 30 | 40 |
| 3 | Eagle Wing `simplywings:eagle_wing` ×2 | `GRID` | 1 | 2 | Feather ×5, Gold Nugget ×2, Iron Ingot ×1, Brown Dye ×2, Updraft Shard ×1 | 2 × 15 | 70 |
| 4 | Eagle Wings `simplywings:eagle_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Eagle Core → #2 · Aile G : Eagle Wing → #3 · Aile D : Eagle Wing → #3 | 100 | **170** |

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

### #2 · Eagle Core · `simplywings:eagle_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ T _ .
_ I U I _
_ I R I _
_ _ U _ _
. _ T _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `T` | Raptor Talon `simplywings:raptor_talon` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `U` | Updraft Shard `simplywings:updraft_shard` | 2 | 0 |
| `R` | Rabbit's Foot `minecraft:rabbit_foot` | 1 | 0 |

### #3 · Eagle Wing · `simplywings:eagle_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. F F F .
F N I N F
_ B U B _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 5 | 0 |
| `N` | Gold Nugget `minecraft:gold_nugget` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |
| `B` | Brown Dye `minecraft:brown_dye` | 2 | 0 |
| `U` | Updraft Shard `simplywings:updraft_shard` | 1 | 0 |

## Pourquoi ces ingrédients

- **Raptor Talon** : sur une chèvre tuée en altitude (Y ≥ 120). L'aigle royal chasse les chèvres en les précipitant des falaises.
- **Updraft Shard** : en minant la roche au-dessus de Y 150, là où l'aigle prend les ascendances. Objet partagé avec le martinet, le cerf-volant, le vent et l'orage.
- **Rabbit's Foot** : la proie, et la chance du chasseur.
- **Gold Nugget** : les épaulettes fauves dorées ; **Brown Dye** : le plumage brun.
