# Route de craft · Patchwork Wings

`simplywings:patchwork` · **Rare** · item `patchwork_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-rafistolées)

Cuir, plumes et ficelle : la rare de départ, bricolée avec les restes des tonneaux de la forge.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Essences | 30 nuage (pièces) + 100 nuage (assemblage) = **130 nuage** |
| Matières premières | 5 types du mod (12 objets) · 6 types vanilla (29 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Pristine Feather (MOB · Chicken), Fleece Tuft (MOB · Sheep) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Fleece Tuft `simplywings:fleece_tuft` | 2 | `MOB` | Sheep | — | 40 % |
| Pristine Feather `simplywings:pristine_feather` | 2 | `MOB` | Chicken | — | 25 % |
| Silk Thread `simplywings:silk_thread` | 5 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Feather `minecraft:feather` | 6 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Green Wool `minecraft:green_wool` | 2 | `VANILLA` | Moutons, teinture | — | — |
| Iron Ingot `minecraft:iron_ingot` | 4 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 12 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Red Wool `minecraft:red_wool` | 3 | `VANILLA` | Moutons, teinture | — | — |
| Stick `minecraft:stick` | 2 | `VANILLA` | Établi : planches | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Patchwork Core `simplywings:patchwork_core` ×1 | `GRID` | 1 | 1 | Leather ×2, Silk Thread ×2, Iron Ingot ×2, Red Wool ×1, Pristine Feather ×2 | 10 nuage | 20 nuage |
| 3 | Patchwork Wing `simplywings:patchwork_wing` ×2 | `GRID` | 1 | 2 | Feather ×3, Red Wool ×1, Fleece Tuft ×1, Green Wool ×1, Leather ×2, Stick ×1, Silk Thread ×1 | 2 × (5 nuage) | 30 nuage |
| 4 | Patchwork Wings `simplywings:patchwork_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Patchwork Core → #2 · Aile G : Patchwork Wing → #3 · Aile D : Patchwork Wing → #3 | 100 nuage | **130 nuage** |

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

### #2 · Patchwork Core · `simplywings:patchwork_core`

`GRID` · niveau 1 · essence : 10 nuage · donne ×1

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
| `T` | Silk Thread `simplywings:silk_thread` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 2 | 0 |
| `W` | Red Wool `minecraft:red_wool` | 1 | 0 |
| `F` | Pristine Feather `simplywings:pristine_feather` | 2 | 0 |

### #3 · Patchwork Wing · `simplywings:patchwork_wing`

`GRID` · niveau 1 · essence : 5 nuage · donne ×1

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
| `B` | Fleece Tuft `simplywings:fleece_tuft` | 1 | 0 |
| `G` | Green Wool `minecraft:green_wool` | 1 | 0 |
| `L` | Leather `minecraft:leather` | 2 | 0 |
| `S` | Stick `minecraft:stick` | 1 | 0 |
| `T` | Silk Thread `simplywings:silk_thread` | 1 | 0 |

## Pourquoi ces ingrédients

- Les restes des tonneaux de la forge (plumes, cuir) et ce qu'on ramasse en route : c'est la rare qu'on peut faire tout de suite après la commune.
- **Red Wool**, **Green Wool**, **Fleece Tuft** : les pièces de laine cousues ; **Silk Thread** : les coutures et la ficelle.
- **Feather** et **Pristine Feather** : des rémiges dépareillées.
- **Stick** : l'attelle de bois de l'aile droite. Coûts minimaux (cœur 10, ailes 5) : la rare la moins chère.
