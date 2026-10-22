# Route de craft · Bee Wings

`simplywings:bee` · **Rare** · item `bee_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-dabeille)

Le clin d'œil à l'abeille de Minecraft : quatre petites ailes translucides qui bourdonnent.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 60 (pièces) + 100 (assemblage) = **160** |
| Drops du mod | Royal Jelly (BLOCK · Bee Nest, Beehive) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Royal Jelly `simplywings:royal_jelly` | 2 | `BLOCK` | Bee Nest, Beehive | Pleine (`honey_level=5`) | 100 % |
| Glass Pane `minecraft:glass_pane` | 8 | `VANILLA` | Établi : verre | — | — |
| Honey Bottle `minecraft:honey_bottle` | 1 | `VANILLA` | Fiole sur une ruche pleine | — | — |
| Honeycomb `minecraft:honeycomb` | 4 | `VANILLA` | Cisailles sur une ruche pleine | — | — |
| Iron Ingot `minecraft:iron_ingot` | 7 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| String `minecraft:string` | 5 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Bee Core `simplywings:bee_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Honeycomb ×2, Royal Jelly ×2, Honey Bottle ×1 | 30 | 40 |
| 3 | Bee Wing `simplywings:bee_wing` ×2 | `GRID` | 1 | 2 | Glass Pane ×4, String ×2, Honeycomb ×1 | 2 × 10 | 60 |
| 4 | Bee Wings `simplywings:bee_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Bee Core → #2 · Aile G : Bee Wing → #3 · Aile D : Bee Wing → #3 | 100 | **160** |

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

### #2 · Bee Core · `simplywings:bee_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ _ _ .
_ I H I _
_ J B J _
_ I H I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `H` | Honeycomb `minecraft:honeycomb` | 2 | 0 |
| `J` | Royal Jelly `simplywings:royal_jelly` | 2 | 0 |
| `B` | Honey Bottle `minecraft:honey_bottle` | 1 | 0 |

### #3 · Bee Wing · `simplywings:bee_wing`

`GRID` · niveau 1 · 10 Cloud Essence · donne ×1

```
. _ _ _ .
_ P S P _
_ P H P _
_ _ S _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Glass Pane `minecraft:glass_pane` | 4 | 0 |
| `S` | String `minecraft:string` | 2 | 0 |
| `H` | Honeycomb `minecraft:honeycomb` | 1 | 0 |

## Pourquoi ces ingrédients

- **Royal Jelly** : en cassant un nid ou une ruche **pleins**. Ça énerve tout l'essaim : il faut un feu de camp dessous ou de quoi courir vite.
- **Honeycomb** et **Honey Bottle** : la récolte normale de la ruche.
- **Glass Pane** : la membrane translucide ; **String** : les nervures.
- Petite aile, petite recette, comme le colibri.
