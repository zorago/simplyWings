# Route de craft · Wings of the Storm

`simplywings:storm` · **Épique** · item `storm_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-lorage)

L'esprit de l'orage : des éclairs qui crépitent. La route se fait sous la pluie.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 13 en `GRID` + 1 assemblage |
| Essences | 90 nuage + 210 vapeur (pièces) + 150 nuage + 150 vapeur (assemblage) = **240 nuage + 360 vapeur** |
| Matières premières | 11 types du mod (40 objets) · 11 types vanilla (45 objets) |
| Drops du mod | Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Updraft Shard (BLOCK · `#minecraft:base_stone_overworld`), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Copper Spark (BLOCK · Copper Ore, Deepslate Copper Ore), Thunder Spark (MOB · Creeper), Redstone Pulse (BLOCK · Redstone Ore, Deepslate Redstone Ore), Gust Mote (MOB · Breeze), Ghast Wisp (MOB · Ghast), Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Copper Spark `simplywings:copper_spark` | 8 | `BLOCK` | Copper Ore, Deepslate Copper Ore | — | 25 % |
| Ember Wisp `simplywings:ember_wisp` | 2 | `MOB` | Blaze | — | 30 % |
| Ghast Wisp `simplywings:ghast_wisp` | 1 | `MOB` | Ghast | — | 50 % |
| Gilded Vein `simplywings:gilded_vein` | 3 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Gust Mote `simplywings:gust_mote` | 2 | `MOB` | Breeze | — | 40 % |
| Redstone Pulse `simplywings:redstone_pulse` | 8 | `BLOCK` | Redstone Ore, Deepslate Redstone Ore | — | 30 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Thunder Spark `simplywings:thunder_spark` | 8 | `MOB` | Creeper | Pendant un orage (creeper chargé : 100 % ×2) | 50 % |
| Updraft Shard `simplywings:updraft_shard` | 4 | `BLOCK` | `#minecraft:base_stone_overworld` | Y ≥ 150 | 8 % |
| Blaze Powder `minecraft:blaze_powder` | 2 | `VANILLA` | Établi : bâton de blaze | — | — |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Glass Bottle `minecraft:glass_bottle` | 1 | `VANILLA` | Établi : verre | — | — |
| Glowstone Dust `minecraft:glowstone_dust` | 1 | `VANILLA` | Pierre lumineuse du Nether, sorcières | — | — |
| Gold Ingot `minecraft:gold_ingot` | 18 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Light Blue Dye `minecraft:light_blue_dye` | 2 | `VANILLA` | Établi : orchidée bleue | — | — |
| Lightning Rod `minecraft:lightning_rod` | 4 | `VANILLA` | Établi : lingots de cuivre | — | — |
| Purple Dye `minecraft:purple_dye` | 2 | `VANILLA` | Établi : rouge + bleu | — | — |
| Wind Charge `minecraft:wind_charge` | 1 | `VANILLA` | Établi : bâton de breeze | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Charged Coil `simplywings:charged_coil` ×4 | `GRID` | 1 | 4 | Copper Spark ×2, Thunder Spark ×2, Redstone Pulse ×2, Lightning Rod ×1 | 4 × (15 vapeur) | 10 nuage + 60 vapeur |
| 3 | Bottled Gale `simplywings:bottled_gale` ×1 | `GRID` | 1 | 1 | Gust Mote ×2, Wind Charge ×1, Glass Bottle ×1 | 15 vapeur | 10 nuage + 75 vapeur |
| 4 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Wisp ×1 | 20 vapeur | 10 nuage + 95 vapeur |
| 5 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × (15 vapeur) | 10 nuage + 125 vapeur |
| 6 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 10 nuage + 15 vapeur | 20 nuage + 140 vapeur |
| 7 | Storm Core `simplywings:storm_core` ×1 | `GRID` | 2 | 1 | Charged Coil ×2 → #2, Gold Ingot ×2, Bottled Gale ×1 → #3, Spirit Vessel ×1 → #4, Glowstone Dust ×1 | 30 nuage + 30 vapeur | 50 nuage + 170 vapeur |
| 8 | Storm Wing `simplywings:storm_wing` ×2 | `GRID` | 2 | 2 | Light Blue Dye ×1, Charged Coil ×1 → #2, Purple Dye ×1, Updraft Shard ×2, Gilded Frame ×1 → #5 | 2 × (20 nuage + 20 vapeur) | 90 nuage + 210 vapeur |
| 9 | Wings of the Storm `simplywings:storm_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #6 · Cœur : Storm Core → #7 · Aile G : Storm Wing → #8 · Aile D : Storm Wing → #8 | 150 nuage + 150 vapeur | **240 nuage + 360 vapeur** |

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

### #2 · Charged Coil · `simplywings:charged_coil`

`GRID` · niveau 1 · essence : 15 vapeur · donne ×1

```
. _ _ _ .
_ C T C _
_ R L R _
_ _ T _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `C` | Copper Spark `simplywings:copper_spark` | 2 | 0 |
| `T` | Thunder Spark `simplywings:thunder_spark` | 2 | 0 |
| `R` | Redstone Pulse `simplywings:redstone_pulse` | 2 | 0 |
| `L` | Lightning Rod `minecraft:lightning_rod` | 1 | 0 |

### #3 · Bottled Gale · `simplywings:bottled_gale` · partagé

`GRID` · niveau 1 · essence : 15 vapeur · donne ×1

```
. _ _ _ .
_ M W M _
_ _ B _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `M` | Gust Mote `simplywings:gust_mote` | 2 | 0 |
| `W` | Wind Charge `minecraft:wind_charge` | 1 | 0 |
| `B` | Glass Bottle `minecraft:glass_bottle` | 1 | 0 |

### #4 · Spirit Vessel · `simplywings:spirit_vessel` · partagé

`GRID` · niveau 1 · essence : 20 vapeur · donne ×1

```
. _ _ _ .
_ G A G _
_ A W A _
_ G A G _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `A` | Glass `minecraft:glass` | 4 | 0 |
| `W` | Ghast Wisp `simplywings:ghast_wisp` | 1 | 0 |

### #5 · Gilded Frame · `simplywings:gilded_frame` · partagé

`GRID` · niveau 1 · essence : 15 vapeur · donne ×1

```
. _ _ _ .
_ G P G _
_ _ V _ _
_ G E G _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `P` | Blaze Powder `minecraft:blaze_powder` | 1 | 0 |
| `V` | Gilded Vein `simplywings:gilded_vein` | 1 | 0 |
| `E` | Ember Wisp `simplywings:ember_wisp` | 1 | 0 |

### #6 · Gilded Harness · `simplywings:gilded_harness` · partagé

`GRID` · niveau 2 · essence : 10 nuage + 15 vapeur · donne ×1

```
. G _ G .
_ L H L _
_ G V G _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `L` | Leather `minecraft:leather` | 2 | 0 |
| `H` | Iron-Buckled Harness `simplywings:iron_harness` | 1 | 1 |
| `V` | Gilded Vein `simplywings:gilded_vein` | 1 | 0 |

### #7 · Storm Core · `simplywings:storm_core`

`GRID` · niveau 2 · essence : 30 nuage + 30 vapeur · donne ×1

```
. _ _ _ .
_ K G K _
_ B V W _
_ _ G _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `K` | Charged Coil `simplywings:charged_coil` | 2 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `B` | Bottled Gale `simplywings:bottled_gale` | 1 | 1 |
| `V` | Spirit Vessel `simplywings:spirit_vessel` | 1 | 1 |
| `W` | Glowstone Dust `minecraft:glowstone_dust` | 1 | 0 |

### #8 · Storm Wing · `simplywings:storm_wing`

`GRID` · niveau 2 · essence : 20 nuage + 20 vapeur · donne ×1

```
. _ _ _ .
_ L K P _
_ U F U _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `L` | Light Blue Dye `minecraft:light_blue_dye` | 1 | 0 |
| `K` | Charged Coil `simplywings:charged_coil` | 1 | 1 |
| `P` | Purple Dye `minecraft:purple_dye` | 1 | 0 |
| `U` | Updraft Shard `simplywings:updraft_shard` | 2 | 0 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |

## Pourquoi ces ingrédients

- **Thunder Spark** : sur un creeper tué **pendant un orage**, le temps des creepers chargés (100 % et ×2 sur un creeper chargé).
- **Charged Coil** (intermédiaire) : le paratonnerre, la **Copper Spark** et la **Redstone Pulse** des filons, l'électricité de Minecraft.
- **Bottled Gale** : l'orage, c'est aussi du vent ; **Updraft Shard** : les cumulonimbus montent haut.
- **Light Blue Dye** et **Purple Dye** : du bleu clair près du dos au violet orageux au bord.
