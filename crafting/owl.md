# Route de craft · Owl Wings

`simplywings:owl` · **Rare** · item `owl_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-chouette)

Le vol silencieux : de larges ailes duveteuses qui chassent de nuit.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Drops du mod | Hushed Down (MOB · Rabbit) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Hushed Down `simplywings:hushed_down` | 8 | `MOB` | Rabbit | De nuit, tueur accroupi | 50 % |
| Book `minecraft:book` | 1 | `VANILLA` | Établi : papier + cuir | — | — |
| Feather `minecraft:feather` | 10 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Golden Carrot `minecraft:golden_carrot` | 2 | `VANILLA` | Établi : carotte + pépites d'or | — | — |
| Iron Ingot `minecraft:iron_ingot` | 9 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |
| White Wool `minecraft:white_wool` | 4 | `VANILLA` | Moutons | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Owl Core `simplywings:owl_core` ×1 | `GRID` | 1 | 1 | Book ×1, Iron Ingot ×4, Hushed Down ×2, Golden Carrot ×2 | 30 | 40 |
| 3 | Owl Wing `simplywings:owl_wing` ×2 | `GRID` | 1 | 2 | Feather ×5, White Wool ×2, Hushed Down ×3, Iron Ingot ×1 | 2 × 15 | 70 |
| 4 | Owl Wings `simplywings:owl_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Owl Core → #2 · Aile G : Owl Wing → #3 · Aile D : Owl Wing → #3 | 100 | **170** |

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

### #2 · Owl Core · `simplywings:owl_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ B _ .
_ I D I _
_ G _ G _
_ I D I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Book `minecraft:book` | 1 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `D` | Hushed Down `simplywings:hushed_down` | 2 | 0 |
| `G` | Golden Carrot `minecraft:golden_carrot` | 2 | 0 |

### #3 · Owl Wing · `simplywings:owl_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. F F F .
_ W D W _
_ D I D _
_ F _ F _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 5 | 0 |
| `W` | White Wool `minecraft:white_wool` | 2 | 0 |
| `D` | Hushed Down `simplywings:hushed_down` | 3 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Hushed Down** : sur un lapin tué **de nuit**, **accroupi**. C'est la chasse de la chouette : nocturne, furtive, et le lapin est sa proie.
- **White Wool** : la laine étouffe les vibrations en vanilla (le sculk ne l'entend pas), comme le peigne de la première rémige rend le vol silencieux.
- **Golden Carrot** : la vision nocturne. **Book** : la chouette savante.
- **Feather** : la base de toute aile d'oiseau.
