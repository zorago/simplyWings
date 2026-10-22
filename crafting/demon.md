# Route de craft · Demon Wings

`simplywings:demon` · **Légendaire** · item `demon_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-démon)

La puissance brute : une membrane rouge sombre qui frappe fort. La route passe par les raids et le Nether.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 3 niveaux de craft avant les pièces (max 3 en légendaire) |
| Crafts | 16 en `GRID` + 1 assemblage |
| Cloud Essence | 595 (pièces) + 800 (assemblage) = **1395** |
| Drops du mod | Vex Wing (MOB · Vex) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Vex Wing `simplywings:vex_wing` | 8 | `MOB` | Vex | — | 40 % |
| Blaze Powder `minecraft:blaze_powder` | 6 | `VANILLA` | Établi : bâton de blaze | — | — |
| Blaze Rod `minecraft:blaze_rod` | 4 | `VANILLA` | Blazes (forteresses du Nether) | — | — |
| Crying Obsidian `minecraft:crying_obsidian` | 1 | `VANILLA` | Ruines de portails, troc avec les piglins | — | — |
| Diamond `minecraft:diamond` | 12 | `VANILLA` | Minage du diamant | — | — |
| Ghast Tear `minecraft:ghast_tear` | 1 | `VANILLA` | Ghasts | — | — |
| Gold Ingot `minecraft:gold_ingot` | 12 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Gunpowder `minecraft:gunpowder` | 4 | `VANILLA` | Creepers, ghasts, sorcières | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 18 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Magma Cream `minecraft:magma_cream` | 6 | `VANILLA` | Cubes de magma | — | — |
| Nether Wart `minecraft:nether_wart` | 8 | `VANILLA` | Forteresses du Nether, culture | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 2 | `VANILLA` | Phantoms | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |
| Wither Skeleton Skull `minecraft:wither_skeleton_skull` | 1 | `VANILLA` | Wither squelettes | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Brimstone `simplywings:brimstone` ×4 | `GRID` | 1 | 2 | Gunpowder ×2, Blaze Powder ×1, Magma Cream ×1 | 2 × 20 | 50 |
| 3 | Infernal Membrane `simplywings:infernal_membrane` ×4 | `GRID` | 1 | 4 | Vex Wing ×2, Leather ×2, Nether Wart ×2, Magma Cream ×1 | 4 × 25 | 150 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 180 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 205 |
| 6 | Demon Core `simplywings:demon_core` ×1 | `GRID` | 2 | 1 | Brimstone ×2 → #2, Wither Skeleton Skull ×1, Diamond ×4, Crying Obsidian ×1, Ghast Tear ×1 | 100 | 305 |
| 7 | Infernal Frame `simplywings:infernal_frame` ×2 | `GRID` | 2 | 2 | Blaze Rod ×2, Gilded Frame ×1 → #4, Diamond ×2 | 2 × 40 | 385 |
| 8 | Radiant Harness `simplywings:radiant_harness` ×1 | `GRID` | 3 | 1 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1 → #5 | 50 | 435 |
| 9 | Demon Wing `simplywings:demon_wing` ×2 | `GRID` | 3 | 2 | Infernal Membrane ×2 → #3, Infernal Frame ×1 → #7, Brimstone ×1 → #2 | 2 × 80 | 595 |
| 10 | Demon Wings `simplywings:demon_wings` | `ASM` | — | 1 | Harnais : Radiant Harness → #8 · Cœur : Demon Core → #6 · Aile G : Demon Wing → #9 · Aile D : Demon Wing → #9 | 800 | **1395** |

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

### #2 · Brimstone · `simplywings:brimstone`

`GRID` · niveau 1 · 20 Cloud Essence · donne ×2

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
| `M` | Magma Cream `minecraft:magma_cream` | 1 | 0 |

### #3 · Infernal Membrane · `simplywings:infernal_membrane`

`GRID` · niveau 1 · 25 Cloud Essence · donne ×1

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
| `L` | Leather `minecraft:leather` | 2 | 0 |
| `N` | Nether Wart `minecraft:nether_wart` | 2 | 0 |
| `M` | Magma Cream `minecraft:magma_cream` | 1 | 0 |

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

### #6 · Demon Core · `simplywings:demon_core`

`GRID` · niveau 2 · 100 Cloud Essence · donne ×1

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
| `T` | Ghast Tear `minecraft:ghast_tear` | 1 | 0 |

### #7 · Infernal Frame · `simplywings:infernal_frame`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ B F B _
_ D _ D _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Blaze Rod `minecraft:blaze_rod` | 2 | 0 |
| `F` | Gilded Frame `simplywings:gilded_frame` | 1 | 1 |
| `D` | Diamond `minecraft:diamond` | 2 | 0 |

### #8 · Radiant Harness · `simplywings:radiant_harness` · partagé

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

### #9 · Demon Wing · `simplywings:demon_wing`

`GRID` · niveau 3 · 80 Cloud Essence · donne ×1

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
- **Infernal Membrane** (intermédiaire) : ailes de vex, cuir, verrues du Nether et crème de magma, une membrane rouge veinée de noir.
- **Brimstone** (intermédiaire) : le soufre, poudre à canon et blaze, les coups explosifs de l'accélération maxée.
- **Infernal Frame** : la Gilded Frame renforcée de bâtons de blaze et de diamant. **Wither Skeleton Skull**, **Crying Obsidian** et **Ghast Tear** au cœur : le Nether dans ce qu'il a de plus sombre.
