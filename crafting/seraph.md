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
| Drops du mod | Gust Mote (MOB · Breeze), Heavenly Down (MOB · Chicken) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Gust Mote `simplywings:gust_mote` | 8 | `MOB` | Breeze | — | 40 % |
| Heavenly Down `simplywings:heavenly_down` | 10 | `MOB` | Chicken | Tué dans la couche de nuages (bande Y de la config Cloud Essence) | 60 % |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Block of Gold `minecraft:gold_block` | 1 | `VANILLA` | Établi : 9 lingots d'or | — | — |
| Diamond `minecraft:diamond` | 12 | `VANILLA` | Minage du diamant | — | — |
| End Rod `minecraft:end_rod` | 4 | `VANILLA` | Établi : bâton de blaze + chorus éclaté | — | — |
| Feather `minecraft:feather` | 20 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Glass Bottle `minecraft:glass_bottle` | 4 | `VANILLA` | Établi : verre | — | — |
| Glowstone Dust `minecraft:glowstone_dust` | 10 | `VANILLA` | Pierre lumineuse du Nether, sorcières | — | — |
| Gold Ingot `minecraft:gold_ingot` | 22 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Nether Star `minecraft:nether_star` | 1 | `VANILLA` | Wither (invocable à volonté) | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 2 | `VANILLA` | Phantoms | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |
| White Dye `minecraft:white_dye` | 5 | `VANILLA` | Établi : poudre d'os ou muguet | — | — |
| Wind Charge `minecraft:wind_charge` | 4 | `VANILLA` | Établi : bâton de breeze | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Bottled Gale `simplywings:bottled_gale` ×4 | `GRID` | 1 | 4 | Gust Mote ×2, Wind Charge ×1, Glass Bottle ×1 | 4 × 15 | 70 |
| 3 | Blessed Plume `simplywings:blessed_plume` ×10 | `GRID` | 1 | 5 | Feather ×4, Heavenly Down ×2, White Dye ×1 | 5 × 15 | 145 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 175 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 200 |
| 6 | Seraph Core `simplywings:seraph_core` ×1 | `GRID` | 2 | 1 | Bottled Gale ×2 → #2, Nether Star ×1, Diamond ×4, Block of Gold ×1 | 100 | 300 |
| 7 | Golden Plume `simplywings:golden_plume` ×10 | `GRID` | 2 | 5 | Gold Ingot ×2, Blessed Plume ×2 → #3, Glowstone Dust ×2 | 5 × 35 | 475 |
| 8 | Celestial Frame `simplywings:celestial_frame` ×2 | `GRID` | 2 | 2 | End Rod ×2, Diamond ×2, Gilded Frame ×1 → #4 | 2 × 35 | 545 |
| 9 | Radiant Harness `simplywings:radiant_harness` ×1 | `GRID` | 3 | 1 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1 → #5 | 50 | 595 |
| 10 | Seraph Wing `simplywings:seraph_wing` ×2 | `GRID` | 3 | 2 | Golden Plume ×5 → #7, Celestial Frame ×1 → #8, Bottled Gale ×1 → #2 | 2 × 80 | 755 |
| 11 | Seraph Wings `simplywings:seraph_wings` | `ASM` | — | 1 | Harnais : Radiant Harness → #9 · Cœur : Seraph Core → #6 · Aile G : Seraph Wing → #10 · Aile D : Seraph Wing → #10 | 800 | **1555** |

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
_ F W F _
_ _ H _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 4 | 0 |
| `H` | Heavenly Down `simplywings:heavenly_down` | 2 | 0 |
| `W` | White Dye `minecraft:white_dye` | 1 | 0 |

### #4 · Gilded Frame · `simplywings:gilded_frame` · partagé

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
_ G P G _
_ _ I _ _
_ G P G _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `P` | Blaze Powder `minecraft:blaze_powder` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

### #5 · Gilded Harness · `simplywings:gilded_harness` · partagé

`GRID` · niveau 2 · 25 Cloud Essence · donne ×1

```
. G _ G .
_ L H L _
_ G _ G _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `L` | Leather `minecraft:leather` | 2 | 0 |
| `H` | Iron-Buckled Harness `simplywings:iron_harness` | 1 | 1 |

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
| `X` | Block of Gold `minecraft:gold_block` | 1 | 0 |

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
| `W` | Glowstone Dust `minecraft:glowstone_dust` | 2 | 0 |

### #8 · Celestial Frame · `simplywings:celestial_frame` · partagé

`GRID` · niveau 2 · 35 Cloud Essence · donne ×1

```
. _ _ _ .
_ E _ E _
_ D F D _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `E` | End Rod `minecraft:end_rod` | 2 | 0 |
| `D` | Diamond `minecraft:diamond` | 2 | 0 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |

### #9 · Radiant Harness · `simplywings:radiant_harness` · partagé

`GRID` · niveau 3 · 50 Cloud Essence · donne ×1

```
. D _ D .
_ P H P _
_ D _ D _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `D` | Diamond `minecraft:diamond` | 4 | 0 |
| `P` | Phantom Membrane `minecraft:phantom_membrane` | 2 | 0 |
| `H` | Gilded Harness `simplywings:gilded_harness` | 1 | 2 |

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

- **Golden Plume** (intermédiaire) : la Blessed Plume de l'ange, dorée à l'or et à la pierre lumineuse.
- **Nether Star** : la lumière ardente des séraphins (« ceux qui brûlent »), arrachée au Wither.
- **Bottled Gale** : la vitesse maxée, le vent qui porte les six ailes.
- **Block of Gold** : six ailes dorées.
