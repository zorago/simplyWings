# Route de craft · Demon Wings

`simplywings:demon` · **Légendaire** · item `demon_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-démon)

La puissance brute : une membrane rouge sombre qui frappe fort. La route passe par les raids et le Nether.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 3 niveaux de craft avant les pièces (max 3 en légendaire) |
| Crafts | 16 en `GRID` + 1 assemblage |
| Essences | 100 nuage + 230 vapeur + 265 éthérée (pièces) + 250 nuage + 250 vapeur + 300 éthérée (assemblage) = **350 nuage + 480 vapeur + 565 éthérée** |
| Matières premières | 10 types du mod (33 objets) · 11 types vanilla (58 objets) |
| Drops du mod | Prism Shard (BLOCK · Diamond Ore, Deepslate Diamond Ore), Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Ghast Wisp (MOB · Ghast), Ember Wisp (MOB · Blaze), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Soot Flake (BLOCK · Coal Ore, Deepslate Coal Ore), Vex Wing (MOB · Vex), Elastic Gel (MOB · Slime, Magma Cube) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Elastic Gel `simplywings:elastic_gel` | 8 | `MOB` | Slime, Magma Cube | — | 40 % |
| Ember Wisp `simplywings:ember_wisp` | 6 | `MOB` | Blaze | — | 30 % |
| Ghast Wisp `simplywings:ghast_wisp` | 1 | `MOB` | Ghast | — | 50 % |
| Gilded Vein `simplywings:gilded_vein` | 3 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Prism Shard `simplywings:prism_shard` | 1 | `BLOCK` | Diamond Ore, Deepslate Diamond Ore | — | 50 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Soot Flake `simplywings:soot_flake` | 2 | `BLOCK` | Coal Ore, Deepslate Coal Ore | — | 20 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Vex Wing `simplywings:vex_wing` | 8 | `MOB` | Vex | — | 40 % |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Crying Obsidian `minecraft:crying_obsidian` | 1 | `VANILLA` | Ruines de portails, troc avec les piglins | — | — |
| Diamond `minecraft:diamond` | 12 | `VANILLA` | Minage du diamant | — | — |
| Gold Ingot `minecraft:gold_ingot` | 12 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Gunpowder `minecraft:gunpowder` | 4 | `VANILLA` | Creepers, ghasts, sorcières | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Magma Cream `minecraft:magma_cream` | 4 | `VANILLA` | Cubes de magma | — | — |
| Nether Wart `minecraft:nether_wart` | 8 | `VANILLA` | Forteresses du Nether, culture | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 2 | `VANILLA` | Phantoms | — | — |
| Wither Skeleton Skull `minecraft:wither_skeleton_skull` | 1 | `VANILLA` | Wither squelettes | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Brimstone `simplywings:brimstone` ×4 | `GRID` | 1 | 2 | Gunpowder ×2, Blaze Powder ×1, Soot Flake ×1 | 2 × (20 éthérée) | 10 nuage + 40 éthérée |
| 3 | Infernal Membrane `simplywings:infernal_membrane` ×4 | `GRID` | 1 | 4 | Vex Wing ×2, Elastic Gel ×2, Nether Wart ×2, Magma Cream ×1 | 4 × (10 vapeur + 15 éthérée) | 10 nuage + 40 vapeur + 100 éthérée |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × (15 vapeur) | 10 nuage + 70 vapeur + 100 éthérée |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 10 nuage + 15 vapeur | 20 nuage + 85 vapeur + 100 éthérée |
| 6 | Demon Core `simplywings:demon_core` ×1 | `GRID` | 2 | 1 | Brimstone ×2 → #2, Wither Skeleton Skull ×1, Diamond ×4, Crying Obsidian ×1, Ghast Wisp ×1 | 30 nuage + 30 vapeur + 40 éthérée | 50 nuage + 115 vapeur + 140 éthérée |
| 7 | Infernal Frame `simplywings:infernal_frame` ×2 | `GRID` | 2 | 2 | Ember Wisp ×2, Gilded Frame ×1 → #4, Diamond ×2 | 2 × (20 vapeur + 20 éthérée) | 50 nuage + 155 vapeur + 180 éthérée |
| 8 | Radiant Harness `simplywings:radiant_harness` ×1 | `GRID` | 3 | 1 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1 → #5, Prism Shard ×1 | 25 vapeur + 25 éthérée | 50 nuage + 180 vapeur + 205 éthérée |
| 9 | Demon Wing `simplywings:demon_wing` ×2 | `GRID` | 3 | 2 | Infernal Membrane ×2 → #3, Infernal Frame ×1 → #7, Brimstone ×1 → #2 | 2 × (25 nuage + 25 vapeur + 30 éthérée) | 100 nuage + 230 vapeur + 265 éthérée |
| 10 | Demon Wings `simplywings:demon_wings` | `ASM` | — | 1 | Harnais : Radiant Harness → #8 · Cœur : Demon Core → #6 · Aile G : Demon Wing → #9 · Aile D : Demon Wing → #9 | 250 nuage + 250 vapeur + 300 éthérée | **350 nuage + 480 vapeur + 565 éthérée** |

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

### #2 · Brimstone · `simplywings:brimstone`

`GRID` · niveau 1 · essence : 20 éthérée · donne ×2

```
. _ _ _ .
_ G P G _
_ _ M _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gunpowder `minecraft:gunpowder` | 2 | 0 |
| `P` | Blaze Powder `minecraft:blaze_powder` | 1 | 0 |
| `M` | Soot Flake `simplywings:soot_flake` | 1 | 0 |

### #3 · Infernal Membrane · `simplywings:infernal_membrane`

`GRID` · niveau 1 · essence : 10 vapeur + 15 éthérée · donne ×1

```
. _ _ _ .
_ V L V _
_ N M N _
_ _ L _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `V` | Vex Wing `simplywings:vex_wing` | 2 | 0 |
| `L` | Elastic Gel `simplywings:elastic_gel` | 2 | 0 |
| `N` | Nether Wart `minecraft:nether_wart` | 2 | 0 |
| `M` | Magma Cream `minecraft:magma_cream` | 1 | 0 |

### #4 · Gilded Frame · `simplywings:gilded_frame` · partagé

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

### #5 · Gilded Harness · `simplywings:gilded_harness` · partagé

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

### #6 · Demon Core · `simplywings:demon_core`

`GRID` · niveau 2 · essence : 30 nuage + 30 vapeur + 40 éthérée · donne ×1

```
. _ _ _ .
_ R W R _
_ D C D _
_ D T D _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `R` | Brimstone `simplywings:brimstone` | 2 | 1 |
| `W` | Wither Skeleton Skull `minecraft:wither_skeleton_skull` | 1 | 0 |
| `D` | Diamond `minecraft:diamond` | 4 | 0 |
| `C` | Crying Obsidian `minecraft:crying_obsidian` | 1 | 0 |
| `T` | Ghast Wisp `simplywings:ghast_wisp` | 1 | 0 |

### #7 · Infernal Frame · `simplywings:infernal_frame`

`GRID` · niveau 2 · essence : 20 vapeur + 20 éthérée · donne ×1

```
. _ _ _ .
_ B F B _
_ D _ D _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Ember Wisp `simplywings:ember_wisp` | 2 | 0 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |
| `D` | Diamond `minecraft:diamond` | 2 | 0 |

### #8 · Radiant Harness · `simplywings:radiant_harness` · partagé

`GRID` · niveau 3 · essence : 25 vapeur + 25 éthérée · donne ×1

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

### #9 · Demon Wing · `simplywings:demon_wing`

`GRID` · niveau 3 · essence : 25 nuage + 25 vapeur + 30 éthérée · donne ×1

```
. _ _ _ .
_ M I M _
_ _ R _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `M` | Infernal Membrane `simplywings:infernal_membrane` | 2 | 1 |
| `I` | Infernal Frame `simplywings:infernal_frame` | 1 | 2 |
| `R` | Brimstone `simplywings:brimstone` | 1 | 1 |

## Pourquoi ces ingrédients

- **Vex Wing** : sur les vexs, les petits démons ailés invoqués par les évocateurs (raids, manoirs).
- **Infernal Membrane** (intermédiaire) : ailes de vex, **Elastic Gel** des cubes de magma, verrues du Nether et crème de magma, une membrane rouge veinée de noir.
- **Brimstone** (intermédiaire) : le soufre, poudre à canon, blaze et **Soot Flake** des filons de charbon, les coups explosifs de l'accélération maxée.
- **Infernal Frame** : la Gilded Frame renforcée d'**Ember Wisp** et de diamant. **Wither Skeleton Skull**, **Crying Obsidian** et **Ghast Wisp** au cœur : le Nether dans ce qu'il a de plus sombre.
