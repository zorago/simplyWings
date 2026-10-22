# Route de craft · Amethyst Wings

`simplywings:amethyst` · **Rare** · item `amethyst_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-daméthyste)

Du cristal vanilla : des éclats d'améthyste qui tintent.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Matières premières | 4 types du mod (8 objets) · 4 types vanilla (27 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Resonant Shard (BLOCK · Amethyst Cluster (adulte)) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Resonant Shard `simplywings:resonant_shard` | 4 | `BLOCK` | Amethyst Cluster (adulte) | — | 30 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Amethyst Shard `minecraft:amethyst_shard` | 12 | `VANILLA` | Géodes d'améthyste | — | — |
| Calcite `minecraft:calcite` | 1 | `VANILLA` | Couche des géodes, montagnes | — | — |
| Iron Ingot `minecraft:iron_ingot` | 8 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Amethyst Core `simplywings:amethyst_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Resonant Shard ×2, Amethyst Shard ×2, Calcite ×1 | 30 | 40 |
| 3 | Amethyst Wing `simplywings:amethyst_wing` ×2 | `GRID` | 1 | 2 | Amethyst Shard ×5, Resonant Shard ×1, Iron Ingot ×1 | 2 × 15 | 70 |
| 4 | Amethyst Wings `simplywings:amethyst_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Amethyst Core → #2 · Aile G : Amethyst Wing → #3 · Aile D : Amethyst Wing → #3 | 100 | **170** |

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

### #2 · Amethyst Core · `simplywings:amethyst_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ _ _ .
_ I R I _
_ A C A _
_ I R I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `R` | Resonant Shard `simplywings:resonant_shard` | 2 | 0 |
| `A` | Amethyst Shard `minecraft:amethyst_shard` | 2 | 0 |
| `C` | Calcite `minecraft:calcite` | 1 | 0 |

### #3 · Amethyst Wing · `simplywings:amethyst_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. A A A .
_ A R A _
_ _ I _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `A` | Amethyst Shard `minecraft:amethyst_shard` | 5 | 0 |
| `R` | Resonant Shard `simplywings:resonant_shard` | 1 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Resonant Shard** : en cassant un amas d'améthyste adulte, au cœur des géodes. Objet partagé avec les ailes de pierre.
- **Amethyst Shard** : les 7 éclats de la main.
- **Calcite** : la couche blanche de la géode, autour du cœur.
- **Iron Ingot** : les barres qui tiennent le cristal.
