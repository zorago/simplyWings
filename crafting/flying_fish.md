# Route de craft · Flying Fish Wings

`simplywings:flying_fish` · **Rare** · item `flying_fish_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-poisson-volant)

Décalé et fun : des nageoires translucides qui filent au ras des vagues.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Essences | 70 nuage (pièces) + 100 nuage (assemblage) = **170 nuage** |
| Matières premières | 5 types du mod (14 objets) · 5 types vanilla (21 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Fin Ray (FISH · Pêche), Glimmering Scale (FISH · Pêche) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Fin Ray `simplywings:fin_ray` | 8 | `FISH` | Pêche | Biomes `warm_ocean`, `lukewarm_ocean`, `deep_lukewarm_ocean` | 20 % |
| Glimmering Scale `simplywings:glimmering_scale` | 2 | `FISH` | Pêche | Partout | 10 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Glass Pane `minecraft:glass_pane` | 4 | `VANILLA` | Établi : verre | — | — |
| Iron Ingot `minecraft:iron_ingot` | 8 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Light Blue Dye `minecraft:light_blue_dye` | 2 | `VANILLA` | Établi : orchidée bleue | — | — |
| Tropical Fish `minecraft:tropical_fish` | 1 | `VANILLA` | Pêche, océans chauds | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Flying Fish Core `simplywings:flying_fish_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Fin Ray ×2, Glimmering Scale ×2, Tropical Fish ×1 | 30 nuage | 40 nuage |
| 3 | Flying Fish Wing `simplywings:flying_fish_wing` ×2 | `GRID` | 1 | 2 | Fin Ray ×3, Glass Pane ×2, Light Blue Dye ×1, Iron Ingot ×1 | 2 × (15 nuage) | 70 nuage |
| 4 | Flying Fish Wings `simplywings:flying_fish_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Flying Fish Core → #2 · Aile G : Flying Fish Wing → #3 · Aile D : Flying Fish Wing → #3 | 100 nuage | **170 nuage** |

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

### #2 · Flying Fish Core · `simplywings:flying_fish_core`

`GRID` · niveau 1 · essence : 30 nuage · donne ×1

```
. _ _ _ .
_ I R I _
_ K T K _
_ I R I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `R` | Fin Ray `simplywings:fin_ray` | 2 | 0 |
| `K` | Glimmering Scale `simplywings:glimmering_scale` | 2 | 0 |
| `T` | Tropical Fish `minecraft:tropical_fish` | 1 | 0 |

### #3 · Flying Fish Wing · `simplywings:flying_fish_wing`

`GRID` · niveau 1 · essence : 15 nuage · donne ×1

```
. R R R .
_ P L P _
_ _ I _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `R` | Fin Ray `simplywings:fin_ray` | 3 | 0 |
| `P` | Glass Pane `minecraft:glass_pane` | 2 | 0 |
| `L` | Light Blue Dye `minecraft:light_blue_dye` | 1 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Fin Ray** : à la pêche en **océan chaud ou tiède**, là où vivent les poissons volants.
- **Glimmering Scale** : une écaille argentée qui peut mordre partout ; **Tropical Fish** : la mer chaude.
- **Glass Pane** et **Light Blue Dye** : la nageoire translucide bleu argenté.
- La recette dessine l'éventail des rayons.
