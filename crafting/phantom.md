# Route de craft · Phantom Wings

`simplywings:phantom` · **Rare** · item `phantom_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-phantom)

Le cauchemar des insomniaques : la membrane déchirée du phantom.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Drops du mod | Phantom Sinew (MOB · Phantom) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Phantom Sinew `simplywings:phantom_sinew` | 6 | `MOB` | Phantom | — | 50 % |
| Bone `minecraft:bone` | 2 | `VANILLA` | Squelettes | — | — |
| Glow Ink Sac `minecraft:glow_ink_sac` | 1 | `VANILLA` | Poulpes luisants | — | — |
| Iron Ingot `minecraft:iron_ingot` | 9 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 10 | `VANILLA` | Phantoms | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Phantom Core `simplywings:phantom_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Phantom Membrane ×2, Phantom Sinew ×2, Glow Ink Sac ×1 | 30 | 40 |
| 3 | Phantom Wing `simplywings:phantom_wing` ×2 | `GRID` | 1 | 2 | Bone ×1, Phantom Membrane ×4, Phantom Sinew ×2, Iron Ingot ×1 | 2 × 15 | 70 |
| 4 | Phantom Wings `simplywings:phantom_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Phantom Core → #2 · Aile G : Phantom Wing → #3 · Aile D : Phantom Wing → #3 | 100 | **170** |

## Recettes

Grille 5×5 sans les coins : `.` = coin absent, `_` = case vide.

### #1 · Iron-Buckled Harness · `simplywings:iron_harness` · partagé

`GRID` · niveau 1 · 10 Cloud Essence · donne ×1

```
. L _ L .
_ L _ L _
_ I S I _
_ L I L _
. L _ L .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `L` | Leather `minecraft:leather` | 8 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 3 | 0 |
| `S` | String `minecraft:string` | 1 | 0 |

### #2 · Phantom Core · `simplywings:phantom_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

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

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. B M M .
_ M S M _
_ _ S I _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Bone `minecraft:bone` | 1 | 0 |
| `M` | Phantom Membrane `minecraft:phantom_membrane` | 4 | 0 |
| `S` | Phantom Sinew `simplywings:phantom_sinew` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Phantom Membrane** et **Phantom Sinew** : sur les phantoms, donc après trois nuits sans dormir. Les nervures vertes qui luisent viennent du sinew.
- **Glow Ink Sac** : la lueur des nervures dans le noir.
- **Bone** : l'os gris-vert du bord d'attaque.
- La recette est volontairement asymétrique, comme les déchirures de l'aile.
