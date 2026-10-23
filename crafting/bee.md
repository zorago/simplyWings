# Route de craft · Bee Wings

`simplywings:bee` · **Rare** · item `bee_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-dabeille)

Le clin d'œil à l'abeille de Minecraft : quatre petites ailes translucides qui bourdonnent.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Essences | 60 nuage (pièces) + 100 nuage (assemblage) = **160 nuage** |
| Matières premières | 5 types du mod (13 objets) · 5 types vanilla (22 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Royal Jelly (BLOCK · Bee Nest, Beehive), Nectar Drop (BLOCK · `#minecraft:small_flowers`) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Nectar Drop `simplywings:nectar_drop` | 1 | `BLOCK` | `#minecraft:small_flowers` | — | 15 % |
| Royal Jelly `simplywings:royal_jelly` | 2 | `BLOCK` | Bee Nest, Beehive | Pleine (`honey_level=5`) | 100 % |
| Silk Thread `simplywings:silk_thread` | 5 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 3 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Glass Pane `minecraft:glass_pane` | 8 | `VANILLA` | Établi : verre | — | — |
| Honey Bottle `minecraft:honey_bottle` | 1 | `VANILLA` | Fiole sur une ruche pleine | — | — |
| Honeycomb `minecraft:honeycomb` | 3 | `VANILLA` | Cisailles sur une ruche pleine | — | — |
| Iron Ingot `minecraft:iron_ingot` | 4 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Bee Core `simplywings:bee_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×2, Honeycomb ×1, Royal Jelly ×2, Honey Bottle ×1, Sky Iron Fleck ×2, Nectar Drop ×1 | 30 nuage | 40 nuage |
| 3 | Bee Wing `simplywings:bee_wing` ×2 | `GRID` | 1 | 2 | Glass Pane ×4, Silk Thread ×2, Honeycomb ×1 | 2 × (10 nuage) | 60 nuage |
| 4 | Bee Wings `simplywings:bee_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Bee Core → #2 · Aile G : Bee Wing → #3 · Aile D : Bee Wing → #3 | 100 nuage | **160 nuage** |

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

### #2 · Bee Core · `simplywings:bee_core`

`GRID` · niveau 1 · essence : 30 nuage · donne ×1

```
. _ _ _ .
_ I H I _
_ J B J _
_ K N K _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 2 | 0 |
| `H` | Honeycomb `minecraft:honeycomb` | 1 | 0 |
| `J` | Royal Jelly `simplywings:royal_jelly` | 2 | 0 |
| `B` | Honey Bottle `minecraft:honey_bottle` | 1 | 0 |
| `K` | Sky Iron Fleck `simplywings:sky_iron` | 2 | 0 |
| `N` | Nectar Drop `simplywings:nectar_drop` | 1 | 0 |

### #3 · Bee Wing · `simplywings:bee_wing`

`GRID` · niveau 1 · essence : 10 nuage · donne ×1

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
| `S` | Silk Thread `simplywings:silk_thread` | 2 | 0 |
| `H` | Honeycomb `minecraft:honeycomb` | 1 | 0 |

## Pourquoi ces ingrédients

- **Royal Jelly** : en cassant un nid ou une ruche **pleins**. Ça énerve tout l'essaim : il faut un feu de camp dessous ou de quoi courir vite.
- **Nectar Drop** : l'abeille butine les fleurs, comme le colibri. **Honeycomb** et **Honey Bottle** : la récolte normale de la ruche.
- **Glass Pane** : la membrane translucide ; **Silk Thread** : les nervures fines.
- Petite aile, petite recette, comme le colibri.
