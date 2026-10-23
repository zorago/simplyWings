# Route de craft · Owl Wings

`simplywings:owl` · **Rare** · item `owl_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-chouette)

Le vol silencieux : de larges ailes duveteuses qui chassent de nuit.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Essences | 70 nuage (pièces) + 100 nuage (assemblage) = **170 nuage** |
| Matières premières | 5 types du mod (16 objets) · 5 types vanilla (27 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Hushed Down (MOB · Rabbit), Fleece Tuft (MOB · Sheep) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Fleece Tuft `simplywings:fleece_tuft` | 4 | `MOB` | Sheep | — | 40 % |
| Hushed Down `simplywings:hushed_down` | 8 | `MOB` | Rabbit | De nuit, tueur accroupi | 50 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Book `minecraft:book` | 1 | `VANILLA` | Établi : papier + cuir | — | — |
| Feather `minecraft:feather` | 10 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Golden Carrot `minecraft:golden_carrot` | 2 | `VANILLA` | Établi : carotte + pépites d'or | — | — |
| Iron Ingot `minecraft:iron_ingot` | 8 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Owl Core `simplywings:owl_core` ×1 | `GRID` | 1 | 1 | Book ×1, Iron Ingot ×4, Hushed Down ×2, Golden Carrot ×2 | 30 nuage | 40 nuage |
| 3 | Owl Wing `simplywings:owl_wing` ×2 | `GRID` | 1 | 2 | Feather ×5, Fleece Tuft ×2, Hushed Down ×3, Iron Ingot ×1 | 2 × (15 nuage) | 70 nuage |
| 4 | Owl Wings `simplywings:owl_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Owl Core → #2 · Aile G : Owl Wing → #3 · Aile D : Owl Wing → #3 | 100 nuage | **170 nuage** |

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

### #2 · Owl Core · `simplywings:owl_core`

`GRID` · niveau 1 · essence : 30 nuage · donne ×1

```
. _ B _ .
_ I D I _
_ G _ G _
_ I D I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Book `minecraft:book` | 1 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `D` | Hushed Down `simplywings:hushed_down` | 2 | 0 |
| `G` | Golden Carrot `minecraft:golden_carrot` | 2 | 0 |

### #3 · Owl Wing · `simplywings:owl_wing`

`GRID` · niveau 1 · essence : 15 nuage · donne ×1

```
. F F F .
_ W D W _
_ D I D _
_ F _ F _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 5 | 0 |
| `W` | Fleece Tuft `simplywings:fleece_tuft` | 2 | 0 |
| `D` | Hushed Down `simplywings:hushed_down` | 3 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Hushed Down** : sur un lapin tué **de nuit**, **accroupi**. C'est la chasse de la chouette : nocturne, furtive, et le lapin est sa proie.
- **Fleece Tuft** : la laine des moutons étouffe les vibrations en vanilla (le sculk ne l'entend pas), comme le peigne de la première rémige rend le vol silencieux.
- **Golden Carrot** : la vision nocturne. **Book** : la chouette savante.
- **Feather** : la base de toute aile d'oiseau.
