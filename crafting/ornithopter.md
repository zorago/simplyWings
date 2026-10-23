# Route de craft · Ornithopter Wings

`simplywings:ornithopter` · **Rare** · item `ornithopter_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-dornithoptère)

La machine de Léonard de Vinci, bois, toile et laiton. Une aile faite main, avec des matériaux choisis.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Essences | 70 nuage (pièces) + 100 nuage (assemblage) = **170 nuage** |
| Matières premières | 6 types du mod (17 objets) · 5 types vanilla (25 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Copper Spark (BLOCK · Copper Ore, Deepslate Copper Ore), Updraft Shard (BLOCK · `#minecraft:base_stone_overworld`), Fleece Tuft (MOB · Sheep) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Copper Spark `simplywings:copper_spark` | 2 | `BLOCK` | Copper Ore, Deepslate Copper Ore | — | 25 % |
| Fleece Tuft `simplywings:fleece_tuft` | 6 | `MOB` | Sheep | — | 40 % |
| Silk Thread `simplywings:silk_thread` | 5 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Updraft Shard `simplywings:updraft_shard` | 1 | `BLOCK` | `#minecraft:base_stone_overworld` | Y ≥ 150 | 8 % |
| Copper Ingot `minecraft:copper_ingot` | 4 | `VANILLA` | Minage du cuivre, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 4 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Piston `minecraft:piston` | 1 | `VANILLA` | Établi | — | — |
| Stick `minecraft:stick` | 10 | `VANILLA` | Établi : planches | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Ornithopter Core `simplywings:ornithopter_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×2, Copper Spark ×2, Piston ×1, Copper Ingot ×2, Updraft Shard ×1 | 30 nuage | 40 nuage |
| 3 | Ornithopter Wing `simplywings:ornithopter_wing` ×2 | `GRID` | 1 | 2 | Stick ×5, Fleece Tuft ×3, Silk Thread ×2, Copper Ingot ×1 | 2 × (15 nuage) | 70 nuage |
| 4 | Ornithopter Wings `simplywings:ornithopter_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Ornithopter Core → #2 · Aile G : Ornithopter Wing → #3 · Aile D : Ornithopter Wing → #3 | 100 nuage | **170 nuage** |

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

### #2 · Ornithopter Core · `simplywings:ornithopter_core`

`GRID` · niveau 1 · essence : 30 nuage · donne ×1

```
. _ _ _ .
_ I _ I _
_ N P N _
_ C U C _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 2 | 0 |
| `N` | Copper Spark `simplywings:copper_spark` | 2 | 0 |
| `P` | Piston `minecraft:piston` | 1 | 0 |
| `C` | Copper Ingot `minecraft:copper_ingot` | 2 | 0 |
| `U` | Updraft Shard `simplywings:updraft_shard` | 1 | 0 |

### #3 · Ornithopter Wing · `simplywings:ornithopter_wing`

`GRID` · niveau 1 · essence : 15 nuage · donne ×1

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
| `W` | Fleece Tuft `simplywings:fleece_tuft` | 3 | 0 |
| `T` | Silk Thread `simplywings:silk_thread` | 2 | 0 |
| `C` | Copper Ingot `minecraft:copper_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- La machine de Léonard de Vinci : de l'artisanat, mais avec des matériaux de choix ramassés à l'outil imprégné.
- **Stick** : les longerons et les 6 nervures ; **Fleece Tuft** : la toile écrue ; **Silk Thread** : les ligatures.
- **Copper Ingot** et **Copper Spark** : les charnières et la poulie en laiton ; **Piston** : l'entraînement des ailes.
- **Updraft Shard** : Léonard essayait ses machines du haut des collines, il faut aller chercher le vent.
