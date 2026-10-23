# Route de craft · Phantom Wings

`simplywings:phantom` · **Rare** · item `phantom_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-phantom)

Le cauchemar des insomniaques : la membrane déchirée du phantom.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Essences | 70 nuage (pièces) + 100 nuage (assemblage) = **170 nuage** |
| Matières premières | 5 types du mod (12 objets) · 4 types vanilla (25 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Phantom Sinew (MOB · Phantom), Hollow Bone (MOB · Skeleton, Stray, Bogged) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Hollow Bone `simplywings:hollow_bone` | 2 | `MOB` | Skeleton, Stray, Bogged | — | 35 % |
| Phantom Sinew `simplywings:phantom_sinew` | 6 | `MOB` | Phantom | — | 50 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Glow Ink Sac `minecraft:glow_ink_sac` | 1 | `VANILLA` | Poulpes luisants | — | — |
| Iron Ingot `minecraft:iron_ingot` | 8 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 10 | `VANILLA` | Phantoms | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Phantom Core `simplywings:phantom_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Phantom Membrane ×2, Phantom Sinew ×2, Glow Ink Sac ×1 | 30 nuage | 40 nuage |
| 3 | Phantom Wing `simplywings:phantom_wing` ×2 | `GRID` | 1 | 2 | Hollow Bone ×1, Phantom Membrane ×4, Phantom Sinew ×2, Iron Ingot ×1 | 2 × (15 nuage) | 70 nuage |
| 4 | Phantom Wings `simplywings:phantom_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Phantom Core → #2 · Aile G : Phantom Wing → #3 · Aile D : Phantom Wing → #3 | 100 nuage | **170 nuage** |

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

### #2 · Phantom Core · `simplywings:phantom_core`

`GRID` · niveau 1 · essence : 30 nuage · donne ×1

```
. _ _ _ .
_ I M I _
_ S G S _
_ I M I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `M` | Phantom Membrane `minecraft:phantom_membrane` | 2 | 0 |
| `S` | Phantom Sinew `simplywings:phantom_sinew` | 2 | 0 |
| `G` | Glow Ink Sac `minecraft:glow_ink_sac` | 1 | 0 |

### #3 · Phantom Wing · `simplywings:phantom_wing`

`GRID` · niveau 1 · essence : 15 nuage · donne ×1

```
. B M M .
_ M S M _
_ _ S I _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Hollow Bone `simplywings:hollow_bone` | 1 | 0 |
| `M` | Phantom Membrane `minecraft:phantom_membrane` | 4 | 0 |
| `S` | Phantom Sinew `simplywings:phantom_sinew` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Phantom Membrane** et **Phantom Sinew** : sur les phantoms, donc après trois nuits sans dormir. Les nervures vertes qui luisent viennent du sinew.
- **Glow Ink Sac** : la lueur des nervures dans le noir.
- **Hollow Bone** : l'os mort-vivant du bord d'attaque, sur les squelettes.
- La recette est volontairement asymétrique, comme les déchirures de l'aile.
