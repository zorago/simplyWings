# Route de craft · Patchwork Wings

`simplywings:patchwork` · **Rare** · item `patchwork_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-rafistolées)

Cuir, plumes et ficelle : la rare de départ, bricolée avec les restes des tonneaux de la forge.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 30 (pièces) + 100 (assemblage) = **130** |
| Drops du mod | Aucun : uniquement des ressources vanilla |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Blue Wool `minecraft:blue_wool` | 2 | `VANILLA` | Moutons, teinture | — | — |
| Feather `minecraft:feather` | 8 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Green Wool `minecraft:green_wool` | 2 | `VANILLA` | Moutons, teinture | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 14 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Red Wool `minecraft:red_wool` | 3 | `VANILLA` | Moutons, teinture | — | — |
| Stick `minecraft:stick` | 2 | `VANILLA` | Établi : planches | — | — |
| String `minecraft:string` | 5 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Patchwork Core `simplywings:patchwork_core` ×1 | `GRID` | 1 | 1 | Leather ×2, String ×2, Iron Ingot ×2, Red Wool ×1, Feather ×2 | 10 | 20 |
| 3 | Patchwork Wing `simplywings:patchwork_wing` ×2 | `GRID` | 1 | 2 | Feather ×3, Red Wool ×1, Blue Wool ×1, Green Wool ×1, Leather ×2, Stick ×1, String ×1 | 2 × 5 | 30 |
| 4 | Patchwork Wings `simplywings:patchwork_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Patchwork Core → #2 · Aile G : Patchwork Wing → #3 · Aile D : Patchwork Wing → #3 | 100 | **130** |

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

### #2 · Patchwork Core · `simplywings:patchwork_core`

`GRID` · niveau 1 · 10 Cloud Essence · donne ×1

```
. _ _ _ .
_ L T L _
_ I W I _
_ F T F _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `L` | Leather `minecraft:leather` | 2 | 0 |
| `T` | String `minecraft:string` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 2 | 0 |
| `W` | Red Wool `minecraft:red_wool` | 1 | 0 |
| `F` | Feather `minecraft:feather` | 2 | 0 |

### #3 · Patchwork Wing · `simplywings:patchwork_wing`

`GRID` · niveau 1 · 5 Cloud Essence · donne ×1

```
. F F F .
_ R B G _
_ L S L _
_ _ T _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 3 | 0 |
| `R` | Red Wool `minecraft:red_wool` | 1 | 0 |
| `B` | Blue Wool `minecraft:blue_wool` | 1 | 0 |
| `G` | Green Wool `minecraft:green_wool` | 1 | 0 |
| `L` | Leather `minecraft:leather` | 2 | 0 |
| `S` | Stick `minecraft:stick` | 1 | 0 |
| `T` | String `minecraft:string` | 1 | 0 |

## Pourquoi ces ingrédients

- Tout vient des tonneaux de la forge (plumes, cuir) et des moutons : c'est la rare qu'on peut faire tout de suite après la commune.
- **Red Wool**, **Blue Wool**, **Green Wool** : les pièces de laine cousues ; **String** : les coutures et la ficelle.
- **Stick** : l'attelle de bois de l'aile droite.
- Coûts minimaux (cœur 10, ailes 5) : la rare la moins chère.
