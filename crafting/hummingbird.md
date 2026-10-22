# Route de craft · Hummingbird Wings

`simplywings:hummingbird` · **Rare** · item `hummingbird_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-colibri)

Le roi du surplace, de toutes petites ailes floues. Une petite paire, donc une petite route.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 60 (pièces) + 100 (assemblage) = **160** |
| Matières premières | 5 types du mod (14 objets) · 5 types vanilla (21 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Nectar Drop (BLOCK · `#minecraft:small_flowers`), Emerald Sliver (BLOCK · Emerald Ore, Deepslate Emerald Ore) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Emerald Sliver `simplywings:emerald_sliver` | 2 | `BLOCK` | Emerald Ore, Deepslate Emerald Ore | — | 60 % |
| Nectar Drop `simplywings:nectar_drop` | 4 | `BLOCK` | `#minecraft:small_flowers` | — | 15 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 5 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Clock `minecraft:clock` | 1 | `VANILLA` | Établi : or + redstone | — | — |
| Feather `minecraft:feather` | 6 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Iron Ingot `minecraft:iron_ingot` | 6 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Sugar `minecraft:sugar` | 2 | `VANILLA` | Établi : canne à sucre | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Hummingbird Core `simplywings:hummingbird_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Nectar Drop ×2, Sugar ×2, Clock ×1 | 30 | 40 |
| 3 | Hummingbird Wing `simplywings:hummingbird_wing` ×2 | `GRID` | 1 | 2 | Emerald Sliver ×1, Sky Iron Fleck ×2, Feather ×3, Nectar Drop ×1 | 2 × 10 | 60 |
| 4 | Hummingbird Wings `simplywings:hummingbird_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Hummingbird Core → #2 · Aile G : Hummingbird Wing → #3 · Aile D : Hummingbird Wing → #3 | 100 | **160** |

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

### #2 · Hummingbird Core · `simplywings:hummingbird_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ _ _ .
_ I N I _
_ S C S _
_ I N I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `N` | Nectar Drop `simplywings:nectar_drop` | 2 | 0 |
| `S` | Sugar `minecraft:sugar` | 2 | 0 |
| `C` | Clock `minecraft:clock` | 1 | 0 |

### #3 · Hummingbird Wing · `simplywings:hummingbird_wing`

`GRID` · niveau 1 · 10 Cloud Essence · donne ×1

```
. _ _ _ .
_ _ E _ _
_ U F U _
_ _ F N _
. _ F _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `E` | Emerald Sliver `simplywings:emerald_sliver` | 1 | 0 |
| `U` | Sky Iron Fleck `simplywings:sky_iron` | 2 | 0 |
| `F` | Feather `minecraft:feather` | 3 | 0 |
| `N` | Nectar Drop `simplywings:nectar_drop` | 1 | 0 |

## Pourquoi ces ingrédients

- **Nectar Drop** : en cueillant des fleurs. Le colibri vit de nectar ; il suffit d'une prairie fleurie.
- **Sugar** : l'eau sucrée des mangeoires à colibris. **Clock** : la cadence folle et précise des battements.
- **Emerald Sliver** : les couvertures vert émeraude irisé, taillées dans un filon d'émeraude.
- **Sky Iron Fleck** plutôt que des lingots : un peu de métal léger pour une aile minuscule.
