# Route de craft · Swift Wings

`simplywings:swift` · **Rare** · item `swift_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-martinet)

Le plus rapide des oiseaux, des ailes en faux faites pour filer.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Matières premières | 5 types du mod (13 objets) · 5 types vanilla (24 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Swift Quill (MOB · Chicken), Updraft Shard (BLOCK · `#minecraft:base_stone_overworld`) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Swift Quill `simplywings:swift_quill` | 8 | `MOB` | Chicken | Tueur en sprint | 30 % |
| Updraft Shard `simplywings:updraft_shard` | 1 | `BLOCK` | `#minecraft:base_stone_overworld` | Y ≥ 150 | 8 % |
| Charcoal `minecraft:charcoal` | 2 | `VANILLA` | Fonte de bûches | — | — |
| Feather `minecraft:feather` | 6 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Iron Ingot `minecraft:iron_ingot` | 8 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Sugar `minecraft:sugar` | 2 | `VANILLA` | Établi : canne à sucre | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Swift Core `simplywings:swift_core` ×1 | `GRID` | 1 | 1 | Swift Quill ×2, Iron Ingot ×4, Sugar ×2, Updraft Shard ×1 | 30 | 40 |
| 3 | Swift Wing `simplywings:swift_wing` ×2 | `GRID` | 1 | 2 | Iron Ingot ×1, Feather ×3, Swift Quill ×3, Charcoal ×1 | 2 × 15 | 70 |
| 4 | Swift Wings `simplywings:swift_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Swift Core → #2 · Aile G : Swift Wing → #3 · Aile D : Swift Wing → #3 | 100 | **170** |

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

### #2 · Swift Core · `simplywings:swift_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ Q _ .
_ I S I _
_ I U I _
_ _ S _ _
. _ Q _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `Q` | Swift Quill `simplywings:swift_quill` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `S` | Sugar `minecraft:sugar` | 2 | 0 |
| `U` | Updraft Shard `simplywings:updraft_shard` | 1 | 0 |

### #3 · Swift Wing · `simplywings:swift_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
I F _ _ _
_ F Q _ _
_ _ F Q _
. _ C Q .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |
| `F` | Feather `minecraft:feather` | 3 | 0 |
| `Q` | Swift Quill `simplywings:swift_quill` | 3 | 0 |
| `C` | Charcoal `minecraft:charcoal` | 1 | 0 |

## Pourquoi ces ingrédients

- **Swift Quill** : sur un poulet tué **en sprintant**. Il faut être aussi vif que le martinet.
- **Sugar** : la vitesse (l'ingrédient de la potion de rapidité).
- **Updraft Shard** : le martinet vit dans les airs et ne se pose presque jamais.
- **Charcoal** : le brun suie, et le martinet ramoneur qui niche dans les cheminées. La forme de la recette dessine la faux.
