# Route de craft · Copper Wings

`simplywings:copper` · **Rare** · item `copper_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-cuivre)

La mécanique du cuivre de la 1.21 : des plaques rivetées tachées de vert-de-gris.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Matières premières | 6 types du mod (22 objets) · 5 types vanilla (17 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Redstone Pulse (BLOCK · Redstone Ore, Deepslate Redstone Ore), Copper Spark (BLOCK · Copper Ore, Deepslate Copper Ore), Verdigris Flake (BLOCK · Blocs de cuivre `weathered_*` et `oxidized_*` (tag `simplywings:aged_copper`)) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Copper Spark `simplywings:copper_spark` | 10 | `BLOCK` | Copper Ore, Deepslate Copper Ore | — | 25 % |
| Redstone Pulse `simplywings:redstone_pulse` | 2 | `BLOCK` | Redstone Ore, Deepslate Redstone Ore | — | 30 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 5 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Verdigris Flake `simplywings:verdigris_flake` | 2 | `BLOCK` | Blocs de cuivre `weathered_*` et `oxidized_*` (tag `simplywings:aged_copper`) | — | 25 % |
| Copper Bulb `minecraft:copper_bulb` | 1 | `VANILLA` | Établi : cuivre + bâton de blaze + redstone | — | — |
| Copper Ingot `minecraft:copper_ingot` | 4 | `VANILLA` | Minage du cuivre, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 4 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Piston `minecraft:piston` | 2 | `VANILLA` | Établi | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Copper Core `simplywings:copper_core` ×1 | `GRID` | 1 | 1 | Copper Ingot ×4, Redstone Pulse ×2, Iron Ingot ×2, Copper Bulb ×1 | 30 | 40 |
| 3 | Copper Wing `simplywings:copper_wing` ×2 | `GRID` | 1 | 2 | Copper Spark ×5, Sky Iron Fleck ×2, Piston ×1, Verdigris Flake ×1 | 2 × 15 | 70 |
| 4 | Copper Wings `simplywings:copper_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Copper Core → #2 · Aile G : Copper Wing → #3 · Aile D : Copper Wing → #3 | 100 | **170** |

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

### #2 · Copper Core · `simplywings:copper_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ _ _ .
_ C R C _
_ I B I _
_ C R C _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `C` | Copper Ingot `minecraft:copper_ingot` | 4 | 0 |
| `R` | Redstone Pulse `simplywings:redstone_pulse` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 2 | 0 |
| `B` | Copper Bulb `minecraft:copper_bulb` | 1 | 0 |

### #3 · Copper Wing · `simplywings:copper_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. C C C .
_ N P N _
_ C V C _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `C` | Copper Spark `simplywings:copper_spark` | 5 | 0 |
| `N` | Sky Iron Fleck `simplywings:sky_iron` | 2 | 0 |
| `P` | Piston `minecraft:piston` | 1 | 0 |
| `V` | Verdigris Flake `simplywings:verdigris_flake` | 1 | 0 |

## Pourquoi ces ingrédients

- **Verdigris Flake** : en cassant du cuivre vieilli ou oxydé. Les chambres d'épreuves en sont pleines, et on peut aussi laisser vieillir le sien.
- **Copper Spark** : la tôle des rémiges, arrachée aux filons de cuivre. **Copper Bulb** : l'ampoule allumée à l'épaule.
- **Piston** : le piston du bras ; **Sky Iron Fleck** : les rivets.
- **Redstone Pulse** : ce qui fait tourner la machine, sur les filons de redstone.
