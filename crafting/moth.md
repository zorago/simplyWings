# Route de craft · Moth Wings

`simplywings:moth` · **Rare** · item `moth_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-papillon-de-nuit)

Poudré et nocturne, le papillon de nuit flotte autour des lumières.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Matières premières | 5 types du mod (18 objets) · 6 types vanilla (21 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Moth Dust (BLOCK · Torch, Wall Torch, Lantern), Fleece Tuft (MOB · Sheep) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Fleece Tuft `simplywings:fleece_tuft` | 4 | `MOB` | Sheep | — | 40 % |
| Moth Dust `simplywings:moth_dust` | 8 | `BLOCK` | Torch, Wall Torch, Lantern | De nuit | 25 % |
| Silk Thread `simplywings:silk_thread` | 3 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Brown Dye `minecraft:brown_dye` | 4 | `VANILLA` | Établi : fèves de cacao | — | — |
| Iron Ingot `minecraft:iron_ingot` | 6 | `VANILLA` | Minage du fer, fonte | — | — |
| Lantern `minecraft:lantern` | 1 | `VANILLA` | Établi : torche + pépites de fer | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Pink Dye `minecraft:pink_dye` | 2 | `VANILLA` | Établi : pivoine, tulipe rose | — | — |
| White Wool `minecraft:white_wool` | 2 | `VANILLA` | Moutons | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Moth Core `simplywings:moth_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Moth Dust ×2, White Wool ×2, Lantern ×1 | 30 | 40 |
| 3 | Moth Wing `simplywings:moth_wing` ×2 | `GRID` | 1 | 2 | Fleece Tuft ×2, Moth Dust ×3, Pink Dye ×1, Brown Dye ×2, Silk Thread ×1 | 2 × 15 | 70 |
| 4 | Moth Wings `simplywings:moth_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Moth Core → #2 · Aile G : Moth Wing → #3 · Aile D : Moth Wing → #3 | 100 | **170** |

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

### #2 · Moth Core · `simplywings:moth_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ _ _ .
_ I D I _
_ W L W _
_ I D I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `D` | Moth Dust `simplywings:moth_dust` | 2 | 0 |
| `W` | White Wool `minecraft:white_wool` | 2 | 0 |
| `L` | Lantern `minecraft:lantern` | 1 | 0 |

### #3 · Moth Wing · `simplywings:moth_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. W D W .
_ D P D _
_ B S B _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `W` | Fleece Tuft `simplywings:fleece_tuft` | 2 | 0 |
| `D` | Moth Dust `simplywings:moth_dust` | 3 | 0 |
| `P` | Pink Dye `minecraft:pink_dye` | 1 | 0 |
| `B` | Brown Dye `minecraft:brown_dye` | 2 | 0 |
| `S` | Silk Thread `simplywings:silk_thread` | 1 | 0 |

## Pourquoi ces ingrédients

- **Moth Dust** : en cassant torches et lanternes **de nuit**. Les papillons de nuit tournent autour des lumières.
- **Lantern** dans le cœur : la lumière qui les attire.
- **White Wool** et **Fleece Tuft** : la touffe de fourrure crème entre les épaules, et la mite qui mange la laine.
- **Pink Dye** et **Brown Dye** : la bordure rose et les zigzags bruns ; **Silk Thread** : le cocon.
