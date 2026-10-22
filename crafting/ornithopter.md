# Route de craft · Ornithopter Wings

`simplywings:ornithopter` · **Rare** · item `ornithopter_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-dornithoptère)

La machine de Léonard de Vinci, bois, toile et laiton. Une aile faite main : aucun drop du mod, seulement de l'artisanat vanilla.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Drops du mod | Aucun : uniquement des ressources vanilla |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Copper Ingot `minecraft:copper_ingot` | 4 | `VANILLA` | Minage du cuivre, fonte | — | — |
| Gold Nugget `minecraft:gold_nugget` | 2 | `VANILLA` | Établi : lingot d'or, piglins zombifiés | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Lead `minecraft:lead` | 1 | `VANILLA` | Établi : ficelle + boule de slime | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Piston `minecraft:piston` | 1 | `VANILLA` | Établi | — | — |
| Stick `minecraft:stick` | 10 | `VANILLA` | Établi : planches | — | — |
| String `minecraft:string` | 5 | `VANILLA` | Araignées, toiles | — | — |
| White Wool `minecraft:white_wool` | 6 | `VANILLA` | Moutons | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Ornithopter Core `simplywings:ornithopter_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×2, Gold Nugget ×2, Piston ×1, Copper Ingot ×2, Lead ×1 | 30 | 40 |
| 3 | Ornithopter Wing `simplywings:ornithopter_wing` ×2 | `GRID` | 1 | 2 | Stick ×5, White Wool ×3, String ×2, Copper Ingot ×1 | 2 × 15 | 70 |
| 4 | Ornithopter Wings `simplywings:ornithopter_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Ornithopter Core → #2 · Aile G : Ornithopter Wing → #3 · Aile D : Ornithopter Wing → #3 | 100 | **170** |

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

### #2 · Ornithopter Core · `simplywings:ornithopter_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ _ _ .
_ I _ I _
_ N P N _
_ C L C _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 2 | 0 |
| `N` | Gold Nugget `minecraft:gold_nugget` | 2 | 0 |
| `P` | Piston `minecraft:piston` | 1 | 0 |
| `C` | Copper Ingot `minecraft:copper_ingot` | 2 | 0 |
| `L` | Lead `minecraft:lead` | 1 | 0 |

### #3 · Ornithopter Wing · `simplywings:ornithopter_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. S S S .
S W W W S
_ T C T _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `S` | Stick `minecraft:stick` | 5 | 0 |
| `W` | White Wool `minecraft:white_wool` | 3 | 0 |
| `T` | String `minecraft:string` | 2 | 0 |
| `C` | Copper Ingot `minecraft:copper_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Stick** : les longerons et les 6 nervures ; **White Wool** : la toile écrue.
- **Copper Ingot** et **Gold Nugget** : les charnières en laiton.
- **Piston** et **Lead** : la poulie au centre du dos et la corde qui actionne les ailes.
- Pas de drop imprégné : c'est une machine, pas une créature. Elle se fabrique avec ce qu'on a sous la main.
