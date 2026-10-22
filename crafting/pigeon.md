# Route de craft · Homing Pigeon Wings

`simplywings:pigeon` · **Rare** · item `pigeon_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-pigeon-voyageur)

Gris, banal et infatigable : le pigeon voyageur rentre toujours chez lui.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Matières premières | 6 types du mod (16 objets) · 6 types vanilla (25 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Homing Feather (MOB · Chicken), Pristine Feather (MOB · Chicken), Abyssal Ink (MOB · Squid, Glow Squid) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Abyssal Ink `simplywings:abyssal_ink` | 2 | `MOB` | Squid, Glow Squid | — | 40 % |
| Homing Feather `simplywings:homing_feather` | 6 | `MOB` | Chicken | Dans un village (structure `#minecraft:village`) | 35 % |
| Pristine Feather `simplywings:pristine_feather` | 4 | `MOB` | Chicken | — | 25 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Compass `minecraft:compass` | 1 | `VANILLA` | Établi : fer + redstone | — | — |
| Feather `minecraft:feather` | 4 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Iron Ingot `minecraft:iron_ingot` | 8 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Light Gray Dye `minecraft:light_gray_dye` | 4 | `VANILLA` | Établi : azurée, tulipe blanche | — | — |
| Paper `minecraft:paper` | 2 | `VANILLA` | Établi : canne à sucre | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Homing Pigeon Core `simplywings:pigeon_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Paper ×2, Homing Feather ×2, Compass ×1 | 30 | 40 |
| 3 | Homing Pigeon Wing `simplywings:pigeon_wing` ×2 | `GRID` | 1 | 2 | Feather ×2, Homing Feather ×2, Pristine Feather ×2, Abyssal Ink ×1, Light Gray Dye ×2, Iron Ingot ×1 | 2 × 15 | 70 |
| 4 | Homing Pigeon Wings `simplywings:pigeon_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Homing Pigeon Core → #2 · Aile G : Homing Pigeon Wing → #3 · Aile D : Homing Pigeon Wing → #3 | 100 | **170** |

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

### #2 · Homing Pigeon Core · `simplywings:pigeon_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ _ _ .
_ I P I _
_ H C H _
_ I P I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `P` | Paper `minecraft:paper` | 2 | 0 |
| `H` | Homing Feather `simplywings:homing_feather` | 2 | 0 |
| `C` | Compass `minecraft:compass` | 1 | 0 |

### #3 · Homing Pigeon Wing · `simplywings:pigeon_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. F H F .
_ P K P _
_ Y I Y _
_ _ H _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 2 | 0 |
| `H` | Homing Feather `simplywings:homing_feather` | 2 | 0 |
| `P` | Pristine Feather `simplywings:pristine_feather` | 2 | 0 |
| `K` | Abyssal Ink `simplywings:abyssal_ink` | 1 | 0 |
| `Y` | Light Gray Dye `minecraft:light_gray_dye` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Homing Feather** : sur un poulet tué **dans un village**. Le pigeon vit là où vivent les gens. **Pristine Feather** : les plumes impeccables des poulets, pour le plumage lisse.
- **Compass** : le retour au nid, le sens de l'orientation.
- **Paper** : les messages qu'il transporte.
- **Abyssal Ink** et **Light Gray Dye** : les deux bandes noires sur le gris ardoise.
