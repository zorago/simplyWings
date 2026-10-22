# Route de craft · Gull Wings

`simplywings:gull` · **Rare** · item `gull_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-goéland)

Le voyageur des côtes : de longues ailes fines qui planent des heures.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Matières premières | 5 types du mod (14 objets) · 5 types vanilla (25 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Gull Feather (FISH · Pêche), Abyssal Ink (MOB · Squid, Glow Squid) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Abyssal Ink `simplywings:abyssal_ink` | 2 | `MOB` | Squid, Glow Squid | — | 40 % |
| Gull Feather `simplywings:gull_feather` | 8 | `FISH` | Pêche | Biomes `beach`, `snowy_beach`, `stony_shore` | 20 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Feather `minecraft:feather` | 8 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Iron Ingot `minecraft:iron_ingot` | 8 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Nautilus Shell `minecraft:nautilus_shell` | 1 | `VANILLA` | Pêche (trésor), noyés | — | — |
| Raw Cod `minecraft:cod` | 2 | `VANILLA` | Pêche | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Gull Core `simplywings:gull_core` ×1 | `GRID` | 1 | 1 | Gull Feather ×2, Iron Ingot ×4, Raw Cod ×2, Nautilus Shell ×1 | 30 | 40 |
| 3 | Gull Wing `simplywings:gull_wing` ×2 | `GRID` | 1 | 2 | Feather ×4, Gull Feather ×3, Abyssal Ink ×1, Iron Ingot ×1 | 2 × 15 | 70 |
| 4 | Gull Wings `simplywings:gull_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Gull Core → #2 · Aile G : Gull Wing → #3 · Aile D : Gull Wing → #3 | 100 | **170** |

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

### #2 · Gull Core · `simplywings:gull_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ G _ .
_ I C I _
_ I N I _
_ _ C _ _
. _ G _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gull Feather `simplywings:gull_feather` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `C` | Raw Cod `minecraft:cod` | 2 | 0 |
| `N` | Nautilus Shell `minecraft:nautilus_shell` | 1 | 0 |

### #3 · Gull Wing · `simplywings:gull_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
F F G G K
_ I F G _
_ _ F _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 4 | 0 |
| `G` | Gull Feather `simplywings:gull_feather` | 3 | 0 |
| `K` | Abyssal Ink `simplywings:abyssal_ink` | 1 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Gull Feather** : à la pêche depuis une plage. Le goéland vole le poisson des pêcheurs : ici, c'est le pêcheur qui ramène la plume.
- **Raw Cod** : les provisions du long voyage, et ce que le goéland vient chercher.
- **Nautilus Shell** : un trésor de pêche ou un noyé, la mer qui rappelle l'endurance au large.
- **Abyssal Ink** : l'encre des calmars du large, pour les pointes noires des rémiges.
