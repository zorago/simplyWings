# Route de craft · Flying Squirrel Wings

`simplywings:squirrel` · **Rare** · item `squirrel_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-décureuil-volant)

Le planeur des forêts : une membrane de fourrure tendue entre les pattes.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Essences | 70 nuage (pièces) + 100 nuage (assemblage) = **170 nuage** |
| Matières premières | 4 types du mod (11 objets) · 4 types vanilla (30 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Acorn (BLOCK · Oak Leaves, Dark Oak Leaves) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Acorn `simplywings:acorn` | 3 | `BLOCK` | Oak Leaves, Dark Oak Leaves | — | 5 % |
| Silk Thread `simplywings:silk_thread` | 5 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Iron Ingot `minecraft:iron_ingot` | 6 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Rabbit Hide `minecraft:rabbit_hide` | 12 | `VANILLA` | Lapins | — | — |
| Stick `minecraft:stick` | 4 | `VANILLA` | Établi : planches | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Flying Squirrel Core `simplywings:squirrel_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Acorn ×3, Rabbit Hide ×2 | 30 nuage | 40 nuage |
| 3 | Flying Squirrel Wing `simplywings:squirrel_wing` ×2 | `GRID` | 1 | 2 | Stick ×2, Rabbit Hide ×5, Leather ×1, Silk Thread ×2 | 2 × (15 nuage) | 70 nuage |
| 4 | Flying Squirrel Wings `simplywings:squirrel_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Flying Squirrel Core → #2 · Aile G : Flying Squirrel Wing → #3 · Aile D : Flying Squirrel Wing → #3 | 100 nuage | **170 nuage** |

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

### #2 · Flying Squirrel Core · `simplywings:squirrel_core`

`GRID` · niveau 1 · essence : 30 nuage · donne ×1

```
. _ _ _ .
_ I A I _
_ H A H _
_ I A I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `A` | Acorn `simplywings:acorn` | 3 | 0 |
| `H` | Rabbit Hide `minecraft:rabbit_hide` | 2 | 0 |

### #3 · Flying Squirrel Wing · `simplywings:squirrel_wing`

`GRID` · niveau 1 · essence : 15 nuage · donne ×1

```
. S _ S .
_ H H H _
_ H L H _
_ T _ T _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `S` | Stick `minecraft:stick` | 2 | 0 |
| `H` | Rabbit Hide `minecraft:rabbit_hide` | 5 | 0 |
| `L` | Leather `minecraft:leather` | 1 | 0 |
| `T` | Silk Thread `simplywings:silk_thread` | 2 | 0 |

## Pourquoi ces ingrédients

- **Acorn** : en cassant des feuilles de chêne et de chêne noir. L'écureuil fait ses réserves. Objet partagé avec l'esprit sylvestre.
- **Rabbit Hide** : la fourrure de la membrane, gris-brun dessus, crème dessous.
- **Stick** : l'éperon cartilagineux du poignet ; **Silk Thread** : les attaches à la cheville.
- **Leather** : le cœur de la membrane.
