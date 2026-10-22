# Route de craft · Peacock Wings

`simplywings:peacock` · **Rare** · item `peacock_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-paon)

La paire vitrine, magnifique et lente. Le paon vient des forêts tropicales.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Matières premières | 6 types du mod (17 objets) · 5 types vanilla (22 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Lapis Glint (BLOCK · Lapis Ore, Deepslate Lapis Ore), Peacock Plume (MOB · Chicken), Emerald Sliver (BLOCK · Emerald Ore, Deepslate Emerald Ore) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Emerald Sliver `simplywings:emerald_sliver` | 1 | `BLOCK` | Emerald Ore, Deepslate Emerald Ore | — | 60 % |
| Lapis Glint `simplywings:lapis_glint` | 4 | `BLOCK` | Lapis Ore, Deepslate Lapis Ore | — | 30 % |
| Peacock Plume `simplywings:peacock_plume` | 8 | `MOB` | Chicken | Biomes `#minecraft:is_jungle` | 35 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Cyan Dye `minecraft:cyan_dye` | 2 | `VANILLA` | Établi : vert + bleu | — | — |
| Feather `minecraft:feather` | 4 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Gold Nugget `minecraft:gold_nugget` | 2 | `VANILLA` | Établi : lingot d'or, piglins zombifiés | — | — |
| Iron Ingot `minecraft:iron_ingot` | 8 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Peacock Core `simplywings:peacock_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Lapis Glint ×2, Peacock Plume ×2, Emerald Sliver ×1 | 30 | 40 |
| 3 | Peacock Wing `simplywings:peacock_wing` ×2 | `GRID` | 1 | 2 | Peacock Plume ×3, Feather ×2, Cyan Dye ×1, Lapis Glint ×1, Iron Ingot ×1, Gold Nugget ×1 | 2 × 15 | 70 |
| 4 | Peacock Wings `simplywings:peacock_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Peacock Core → #2 · Aile G : Peacock Wing → #3 · Aile D : Peacock Wing → #3 | 100 | **170** |

## Recettes

Grille 5×5 sans les coins : `.` = coin absent, `_` = case vide.

### #1 · Iron-Buckled Harness · `simplywings:iron_harness` · partagé

`GRID` · niveau 1 · 10 Cloud Essence · donne ×1

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

### #2 · Peacock Core · `simplywings:peacock_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ _ _ .
_ I L I _
_ P E P _
_ I L I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `L` | Lapis Glint `simplywings:lapis_glint` | 2 | 0 |
| `P` | Peacock Plume `simplywings:peacock_plume` | 2 | 0 |
| `E` | Emerald Sliver `simplywings:emerald_sliver` | 1 | 0 |

### #3 · Peacock Wing · `simplywings:peacock_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ P _ .
_ F P F _
_ C P L _
_ _ I _ _
. _ N _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Peacock Plume `simplywings:peacock_plume` | 3 | 0 |
| `F` | Feather `minecraft:feather` | 2 | 0 |
| `C` | Cyan Dye `minecraft:cyan_dye` | 1 | 0 |
| `L` | Lapis Glint `simplywings:lapis_glint` | 1 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |
| `N` | Gold Nugget `minecraft:gold_nugget` | 1 | 0 |

## Pourquoi ces ingrédients

- **Peacock Plume** : sur un poulet tué en **jungle**, le pays d'origine du paon.
- **Lapis Glint** et **Cyan Dye** : le bleu profond et le bleu-vert des couvertures ; l'éclat de lapis vient des filons.
- **Gold Nugget** : l'anneau doré de l'ocelle au bout de la traîne ; **Emerald Sliver** : le reflet vert bronze.
- La recette dessine la traîne : une colonne de plumes au centre de la grille.
