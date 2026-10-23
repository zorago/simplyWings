# Route de craft · Beetle Wings

`simplywings:beetle` · **Rare** · item `beetle_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-scarabée)

Le cousin des élytres : deux coques vertes rigides sur des ailes membraneuses.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Essences | 70 nuage (pièces) + 100 nuage (assemblage) = **170 nuage** |
| Matières premières | 5 types du mod (11 objets) · 5 types vanilla (30 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Chitin Shard (MOB · Silverfish), Emerald Sliver (BLOCK · Emerald Ore, Deepslate Emerald Ore) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Chitin Shard `simplywings:chitin_shard` | 6 | `MOB` | Silverfish | — | 25 % |
| Emerald Sliver `simplywings:emerald_sliver` | 1 | `BLOCK` | Emerald Ore, Deepslate Emerald Ore | — | 60 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Armadillo Scute `minecraft:armadillo_scute` | 6 | `VANILLA` | Brosse sur un tatou, mue naturelle | — | — |
| Glass Pane `minecraft:glass_pane` | 8 | `VANILLA` | Établi : verre | — | — |
| Green Dye `minecraft:green_dye` | 2 | `VANILLA` | Fonte du cactus | — | — |
| Iron Ingot `minecraft:iron_ingot` | 8 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Beetle Core `simplywings:beetle_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Armadillo Scute ×2, Chitin Shard ×2, Emerald Sliver ×1 | 30 nuage | 40 nuage |
| 3 | Beetle Wing `simplywings:beetle_wing` ×2 | `GRID` | 1 | 2 | Armadillo Scute ×2, Chitin Shard ×2, Glass Pane ×4, Iron Ingot ×1, Green Dye ×1 | 2 × (15 nuage) | 70 nuage |
| 4 | Beetle Wings `simplywings:beetle_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Beetle Core → #2 · Aile G : Beetle Wing → #3 · Aile D : Beetle Wing → #3 | 100 nuage | **170 nuage** |

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

### #2 · Beetle Core · `simplywings:beetle_core`

`GRID` · niveau 1 · essence : 30 nuage · donne ×1

```
. _ _ _ .
_ I S I _
_ C E C _
_ I S I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `S` | Armadillo Scute `minecraft:armadillo_scute` | 2 | 0 |
| `C` | Chitin Shard `simplywings:chitin_shard` | 2 | 0 |
| `E` | Emerald Sliver `simplywings:emerald_sliver` | 1 | 0 |

### #3 · Beetle Wing · `simplywings:beetle_wing`

`GRID` · niveau 1 · essence : 15 nuage · donne ×1

```
. S C S .
_ P C P _
_ P I P _
_ _ D _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `S` | Armadillo Scute `minecraft:armadillo_scute` | 2 | 0 |
| `C` | Chitin Shard `simplywings:chitin_shard` | 2 | 0 |
| `P` | Glass Pane `minecraft:glass_pane` | 4 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |
| `D` | Green Dye `minecraft:green_dye` | 1 | 0 |

## Pourquoi ces ingrédients

- **Chitin Shard** : sur les poissons d'argent, les seuls « insectes » de Minecraft, cachés dans la pierre infestée.
- **Armadillo Scute** : la carapace rigide des élytres, obtenue à la brosse sur un tatou.
- **Emerald Sliver** et **Green Dye** : le vert métallique de la coque.
- **Glass Pane** : l'aile membraneuse translucide repliée dessous.
