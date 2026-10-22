# Route de craft · Seraph Wings

`simplywings:seraph` · **Légendaire** · item `seraph_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-séraphin)

La vitesse céleste : six ailes d'ange dorées. Le séraphin est l'ange le plus élevé : sa route reprend celle de l'ange et la dore.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 3 niveaux de craft avant les pièces (max 3 en légendaire) |
| Crafts | 24 en `GRID` + 1 assemblage |
| Cloud Essence | 755 (pièces) + 800 (assemblage) = **1555** |
| Matières premières | 10 types du mod (51 objets) · 12 types vanilla (76 objets) |
| Drops du mod | Prism Shard (BLOCK · Diamond Ore, Deepslate Diamond Ore), Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Void Grit (BLOCK · End Stone), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Gust Mote (MOB · Breeze), Heavenly Down (MOB · Chicken), Pristine Feather (MOB · Chicken), Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Ember Wisp `simplywings:ember_wisp` | 2 | `MOB` | Blaze | — | 30 % |
| Gilded Vein `simplywings:gilded_vein` | 14 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Gust Mote `simplywings:gust_mote` | 8 | `MOB` | Breeze | — | 40 % |
| Heavenly Down `simplywings:heavenly_down` | 10 | `MOB` | Chicken | Tué dans la couche de nuages (bande Y de la config Cloud Essence) | 60 % |
| Prism Shard `simplywings:prism_shard` | 1 | `BLOCK` | Diamond Ore, Deepslate Diamond Ore | — | 50 % |
| Pristine Feather `simplywings:pristine_feather` | 10 | `MOB` | Chicken | — | 25 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Void Grit `simplywings:void_grit` | 2 | `BLOCK` | End Stone | — | 8 % |
| Blaze Powder `minecraft:blaze_powder` | 2 | `VANILLA` | Établi : bâton de blaze | — | — |
| Diamond `minecraft:diamond` | 12 | `VANILLA` | Minage du diamant | — | — |
| End Rod `minecraft:end_rod` | 4 | `VANILLA` | Établi : bâton de blaze + chorus éclaté | — | — |
| Feather `minecraft:feather` | 10 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Glass Bottle `minecraft:glass_bottle` | 4 | `VANILLA` | Établi : verre | — | — |
| Gold Ingot `minecraft:gold_ingot` | 22 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Nether Star `minecraft:nether_star` | 1 | `VANILLA` | Wither (invocable à volonté) | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 2 | `VANILLA` | Phantoms | — | — |
| White Dye `minecraft:white_dye` | 5 | `VANILLA` | Établi : poudre d'os ou muguet | — | — |
| Wind Charge `minecraft:wind_charge` | 4 | `VANILLA` | Établi : bâton de breeze | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Bottled Gale `simplywings:bottled_gale` ×4 | `GRID` | 1 | 4 | Gust Mote ×2, Wind Charge ×1, Glass Bottle ×1 | 4 × 15 | 70 |
| 3 | Blessed Plume `simplywings:blessed_plume` ×10 | `GRID` | 1 | 5 | Feather ×2, Heavenly Down ×2, Pristine Feather ×2, White Dye ×1 | 5 × 15 | 145 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × 15 | 175 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 25 | 200 |
| 6 | Seraph Core `simplywings:seraph_core` ×1 | `GRID` | 2 | 1 | Bottled Gale ×2 → #2, Nether Star ×1, Diamond ×4, Gilded Vein ×1 | 100 | 300 |
| 7 | Golden Plume `simplywings:golden_plume` ×10 | `GRID` | 2 | 5 | Gold Ingot ×2, Blessed Plume ×2 → #3, Gilded Vein ×2 | 5 × 35 | 475 |
| 8 | Celestial Frame `simplywings:celestial_frame` ×2 | `GRID` | 2 | 2 | End Rod ×2, Void Grit ×1, Diamond ×2, Gilded Frame ×1 → #4 | 2 × 35 | 545 |
| 9 | Radiant Harness `simplywings:radiant_harness` ×1 | `GRID` | 3 | 1 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1 → #5, Prism Shard ×1 | 50 | 595 |
| 10 | Seraph Wing `simplywings:seraph_wing` ×2 | `GRID` | 3 | 2 | Golden Plume ×5 → #7, Celestial Frame ×1 → #8, Bottled Gale ×1 → #2 | 2 × 80 | 755 |
| 11 | Seraph Wings `simplywings:seraph_wings` | `ASM` | — | 1 | Harnais : Radiant Harness → #9 · Cœur : Seraph Core → #6 · Aile G : Seraph Wing → #10 · Aile D : Seraph Wing → #10 | 800 | **1555** |

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

### #2 · Bottled Gale · `simplywings:bottled_gale` · partagé

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

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

### #3 · Blessed Plume · `simplywings:blessed_plume` · partagé

`GRID` · niveau 1 · 15 Cloud Essence · donne ×2

```
. _ _ _ .
_ F H F _
_ P W P _
_ _ H _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 2 | 0 |
| `H` | Heavenly Down `simplywings:heavenly_down` | 2 | 0 |
| `P` | Pristine Feather `simplywings:pristine_feather` | 2 | 0 |
| `W` | White Dye `minecraft:white_dye` | 1 | 0 |

### #4 · Gilded Frame · `simplywings:gilded_frame` · partagé

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

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

### #5 · Gilded Harness · `simplywings:gilded_harness` · partagé

`GRID` · niveau 2 · 25 Cloud Essence · donne ×1

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

### #6 · Seraph Core · `simplywings:seraph_core`

`GRID` · niveau 2 · 100 Cloud Essence · donne ×1

```
. _ _ _ .
_ B S B _
_ D X D _
_ D _ D _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Bottled Gale `simplywings:bottled_gale` | 2 | 1 |
| `S` | Nether Star `minecraft:nether_star` | 1 | 0 |
| `D` | Diamond `minecraft:diamond` | 4 | 0 |
| `X` | Gilded Vein `simplywings:gilded_vein` | 1 | 0 |

### #7 · Golden Plume · `simplywings:golden_plume`

`GRID` · niveau 2 · 35 Cloud Essence · donne ×2

```
. _ _ _ .
_ G P G _
_ W P W _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `P` | Blessed Plume `simplywings:blessed_plume` | 2 | 1 |
| `W` | Gilded Vein `simplywings:gilded_vein` | 2 | 0 |

### #8 · Celestial Frame · `simplywings:celestial_frame` · partagé

`GRID` · niveau 2 · 35 Cloud Essence · donne ×1

```
. _ _ _ .
_ E V E _
_ D F D _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `E` | End Rod `minecraft:end_rod` | 2 | 0 |
| `V` | Void Grit `simplywings:void_grit` | 1 | 0 |
| `D` | Diamond `minecraft:diamond` | 2 | 0 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |

### #9 · Radiant Harness · `simplywings:radiant_harness` · partagé

`GRID` · niveau 3 · 50 Cloud Essence · donne ×1

```
. D _ D .
_ P H P _
_ D Q D _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `D` | Diamond `minecraft:diamond` | 4 | 0 |
| `P` | Phantom Membrane `minecraft:phantom_membrane` | 2 | 0 |
| `H` | Gilded Harness `simplywings:gilded_harness` | 1 | 2 |
| `Q` | Prism Shard `simplywings:prism_shard` | 1 | 0 |

### #10 · Seraph Wing · `simplywings:seraph_wing`

`GRID` · niveau 3 · 80 Cloud Essence · donne ×1

```
. P P P .
_ P C P _
_ _ B _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Golden Plume `simplywings:golden_plume` | 5 | 2 |
| `C` | Celestial Frame `simplywings:celestial_frame` | 1 | 2 |
| `B` | Bottled Gale `simplywings:bottled_gale` | 1 | 1 |

## Pourquoi ces ingrédients

- **Golden Plume** (intermédiaire) : la Blessed Plume de l'ange, dorée à l'or et à la **Gilded Vein**.
- **Nether Star** : la lumière ardente des séraphins (« ceux qui brûlent »), arrachée au Wither.
- **Bottled Gale** : la vitesse maxée, le vent qui porte les six ailes.
- **Gilded Vein** au cœur : six ailes dorées, de l'or brut tiré des filons.
