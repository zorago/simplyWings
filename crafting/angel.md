# Route de craft · Angel Wings

`simplywings:angel` · **Légendaire** · item `angel_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-dange)

L'endurance et la grâce : de grandes ailes blanches. La route monte dans les nuages.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 3 niveaux de craft avant les pièces (max 3 en légendaire) |
| Crafts | 17 en `GRID` + 1 assemblage |
| Cloud Essence | 560 (pièces) + 800 (assemblage) = **1360** |
| Drops du mod | Heavenly Down (MOB · Chicken) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Heavenly Down `simplywings:heavenly_down` | 12 | `MOB` | Chicken | Tué dans la couche de nuages (bande Y de la config Cloud Essence) | 60 % |
| Blaze Powder `minecraft:blaze_powder` | 4 | `VANILLA` | Établi : bâton de blaze | — | — |
| Diamond `minecraft:diamond` | 15 | `VANILLA` | Minage du diamant | — | — |
| End Rod `minecraft:end_rod` | 4 | `VANILLA` | Établi : bâton de blaze + chorus éclaté | — | — |
| Feather `minecraft:feather` | 24 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Glowstone Dust `minecraft:glowstone_dust` | 4 | `VANILLA` | Pierre lumineuse du Nether, sorcières | — | — |
| Gold Ingot `minecraft:gold_ingot` | 16 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 5 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 2 | `VANILLA` | Phantoms | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |
| Totem of Undying `minecraft:totem_of_undying` | 1 | `VANILLA` | Évocateurs (raids, manoirs) | — | — |
| White Dye `minecraft:white_dye` | 6 | `VANILLA` | Établi : poudre d'os ou muguet | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Blessed Plume `simplywings:blessed_plume` ×12 | `GRID` | 1 | 6 | Feather ×4, Heavenly Down ×2, White Dye ×1 | 6 × 15 | 100 |
| 3 | Halo `simplywings:halo` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glowstone Dust ×4, Diamond ×1 | 25 | 125 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×2, Iron Ingot ×1 | 2 × 15 | 155 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 180 |
| 6 | Angel Core `simplywings:angel_core` ×1 | `GRID` | 2 | 1 | Blessed Plume ×2 → #2, Halo ×1 → #3, Diamond ×4, Totem of Undying ×1 | 100 | 280 |
| 7 | Celestial Frame `simplywings:celestial_frame` ×2 | `GRID` | 2 | 2 | End Rod ×2, Diamond ×2, Gilded Frame ×1 → #4 | 2 × 35 | 350 |
| 8 | Radiant Harness `simplywings:radiant_harness` ×1 | `GRID` | 3 | 1 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1 → #5 | 50 | 400 |
| 9 | Angel Wing `simplywings:angel_wing` ×2 | `GRID` | 3 | 2 | Blessed Plume ×5 → #2, Celestial Frame ×1 → #7, Diamond ×1 | 2 × 80 | 560 |
| 10 | Angel Wings `simplywings:angel_wings` | `ASM` | — | 1 | Harnais : Radiant Harness → #8 · Cœur : Angel Core → #6 · Aile G : Angel Wing → #9 · Aile D : Angel Wing → #9 | 800 | **1360** |

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

### #2 · Blessed Plume · `simplywings:blessed_plume` · partagé

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

### #3 · Halo · `simplywings:halo`

`GRID` · niveau 1 · 25 Cloud Essence · donne ×1

```
. _ _ _ .
_ G W G _
_ W D W _
_ G W G _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |
| `W` | Glowstone Dust `minecraft:glowstone_dust` | 4 | 0 |
| `D` | Diamond `minecraft:diamond` | 1 | 0 |

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

### #6 · Angel Core · `simplywings:angel_core`

`GRID` · niveau 2 · 100 Cloud Essence · donne ×1

```
. _ _ _ .
_ P H P _
_ D T D _
_ D _ D _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Blessed Plume `simplywings:blessed_plume` | 2 | 1 |
| `H` | Halo `simplywings:halo` | 1 | 1 |
| `D` | Diamond `minecraft:diamond` | 4 | 0 |
| `T` | Totem of Undying `minecraft:totem_of_undying` | 1 | 0 |

### #7 · Celestial Frame · `simplywings:celestial_frame` · partagé

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

### #9 · Angel Wing · `simplywings:angel_wing`

`GRID` · niveau 3 · 80 Cloud Essence · donne ×1

```
. P P P .
_ P C P _
_ _ D _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Blessed Plume `simplywings:blessed_plume` | 5 | 1 |
| `C` | Celestial Frame `simplywings:celestial_frame` | 1 | 2 |
| `D` | Diamond `minecraft:diamond` | 1 | 0 |

## Pourquoi ces ingrédients

- **Heavenly Down** : sur un poulet tué **dans la couche de nuages**, là où on récolte la Cloud Essence. Il faut y monter son poulet (laisse, bateau, ascenseur).
- **Blessed Plume** (intermédiaire, partagé avec le séraphin) : le duvet céleste sur des plumes blanchies.
- **Halo** (intermédiaire) : un anneau d'or et de lumière autour d'un diamant.
- **Totem of Undying** : l'ange gardien, qui sauve de la mort. L'endurance maxée de la paire.
