# Route de craft · Wings of Daedalus

`simplywings:daedalus` · **Légendaire** · item `daedalus_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-dédale)

Dédale, l'inventeur du labyrinthe, et ses ailes mécaniques. La route passe par le labyrinthe de Minecraft : les chambres d'épreuves.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 3 niveaux de craft avant les pièces (max 3 en légendaire) |
| Crafts | 25 en `GRID` + 1 assemblage |
| Cloud Essence | 665 (pièces) + 800 (assemblage) = **1465** |
| Drops du mod | Aucun : uniquement des ressources vanilla |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Breeze Rod `minecraft:breeze_rod` | 2 | `VANILLA` | Breezes (chambres d'épreuves) | — | — |
| Coal `minecraft:coal` | 7 | `VANILLA` | Minage du charbon | — | — |
| Copper Ingot `minecraft:copper_ingot` | 18 | `VANILLA` | Minage du cuivre, fonte | — | — |
| Crafter `minecraft:crafter` | 1 | `VANILLA` | Établi | — | — |
| Diamond `minecraft:diamond` | 10 | `VANILLA` | Minage du diamant | — | — |
| Feather `minecraft:feather` | 4 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Gold Ingot `minecraft:gold_ingot` | 10 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Honeycomb `minecraft:honeycomb` | 2 | `VANILLA` | Cisailles sur une ruche pleine | — | — |
| Iron Ingot `minecraft:iron_ingot` | 24 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Observer `minecraft:observer` | 2 | `VANILLA` | Établi | — | — |
| Ominous Trial Key `minecraft:ominous_trial_key` | 1 | `VANILLA` | Générateur d'épreuves sinistre (Mauvais présage d'épreuve) | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 2 | `VANILLA` | Phantoms | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |
| Wind Charge `minecraft:wind_charge` | 2 | `VANILLA` | Établi : bâton de breeze | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Brass Ingot `simplywings:brass_ingot` ×12 | `GRID` | 1 | 6 | Copper Ingot ×3, Gold Ingot ×1 | 6 × 10 | 70 |
| 3 | Steel Plate `simplywings:steel_plate` ×14 | `GRID` | 1 | 7 | Iron Ingot ×3, Coal ×1 | 7 × 10 | 140 |
| 4 | Icarus Feather `simplywings:icarus_feather` ×4 | `GRID` | 1 | 2 | Feather ×2, Honeycomb ×1 | 2 × 15 | 170 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1 | 25 | 195 |
| 6 | Wind Thruster `simplywings:wind_thruster` ×2 | `GRID` | 2 | 2 | Brass Ingot ×2 → #2, Wind Charge ×1, Steel Plate ×2 → #3, Breeze Rod ×1, Observer ×1 | 2 × 40 | 275 |
| 7 | Brass Armature `simplywings:brass_armature` ×2 | `GRID` | 2 | 2 | Brass Ingot ×4 → #2, Diamond ×1 | 2 × 40 | 355 |
| 8 | Radiant Harness `simplywings:radiant_harness` ×1 | `GRID` | 3 | 1 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1 → #5 | 50 | 405 |
| 9 | Daedalus Core `simplywings:daedalus_core` ×1 | `GRID` | 3 | 1 | Wind Thruster ×2 → #6, Ominous Trial Key ×1, Diamond ×4, Crafter ×1 | 100 | 505 |
| 10 | Daedalus Wing `simplywings:daedalus_wing` ×2 | `GRID` | 3 | 2 | Steel Plate ×5 → #3, Brass Armature ×1 → #7, Icarus Feather ×2 → #4 | 2 × 80 | 665 |
| 11 | Wings of Daedalus `simplywings:daedalus_wings` | `ASM` | — | 1 | Harnais : Radiant Harness → #8 · Cœur : Daedalus Core → #9 · Aile G : Daedalus Wing → #10 · Aile D : Daedalus Wing → #10 | 800 | **1465** |

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

### #2 · Brass Ingot · `simplywings:brass_ingot`

`GRID` · niveau 1 · 10 Cloud Essence · donne ×2

```
. _ _ _ .
_ C G C _
_ _ C _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `C` | Copper Ingot `minecraft:copper_ingot` | 3 | 0 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 1 | 0 |

### #3 · Steel Plate · `simplywings:steel_plate`

`GRID` · niveau 1 · 10 Cloud Essence · donne ×2

```
. _ _ _ .
_ I C I _
_ _ I _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 3 | 0 |
| `C` | Coal `minecraft:coal` | 1 | 0 |

### #4 · Icarus Feather · `simplywings:icarus_feather`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×2

```
. _ _ _ .
_ F H F _
_ _ _ _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 2 | 0 |
| `H` | Honeycomb `minecraft:honeycomb` | 1 | 0 |

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

### #6 · Wind Thruster · `simplywings:wind_thruster`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ B W B _
_ P X P _
_ _ O _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Brass Ingot `simplywings:brass_ingot` | 2 | 1 |
| `W` | Wind Charge `minecraft:wind_charge` | 1 | 0 |
| `P` | Steel Plate `simplywings:steel_plate` | 2 | 1 |
| `X` | Breeze Rod `minecraft:breeze_rod` | 1 | 0 |
| `O` | Observer `minecraft:observer` | 1 | 0 |

### #7 · Brass Armature · `simplywings:brass_armature`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ B D B _
_ B _ B _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Brass Ingot `simplywings:brass_ingot` | 4 | 1 |
| `D` | Diamond `minecraft:diamond` | 1 | 0 |

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

### #9 · Daedalus Core · `simplywings:daedalus_core`

`GRID` · niveau 3 · 100 Cloud Essence · donne ×1

```
. _ _ _ .
_ T K T _
_ D C D _
_ D _ D _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `T` | Wind Thruster `simplywings:wind_thruster` | 2 | 2 |
| `K` | Ominous Trial Key `minecraft:ominous_trial_key` | 1 | 0 |
| `D` | Diamond `minecraft:diamond` | 4 | 0 |
| `C` | Crafter `minecraft:crafter` | 1 | 0 |

### #10 · Daedalus Wing · `simplywings:daedalus_wing`

`GRID` · niveau 3 · 80 Cloud Essence · donne ×1

```
. P P P .
_ P A P _
_ I _ I _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Steel Plate `simplywings:steel_plate` | 5 | 1 |
| `A` | Brass Armature `simplywings:brass_armature` | 1 | 2 |
| `I` | Icarus Feather `simplywings:icarus_feather` | 2 | 1 |

## Pourquoi ces ingrédients

- **Ominous Trial Key** : la clé du labyrinthe, gagnée dans une chambre d'épreuves sinistre. On s'évade du labyrinthe comme Dédale.
- **Wind Thruster** (intermédiaire) : bâton et charge de breeze, laiton, acier et observateur, les réacteurs vectorisés qui donnent la maniabilité maxée.
- **Brass Ingot** et **Steel Plate** : le laiton de l'armature et l'acier des panneaux rivetés. **Crafter** : la machine qui fabrique.
- **Icarus Feather** : des plumes collées à la cire de rayon de miel, les ailes d'Icare. Un rappel : ne pas voler trop près du soleil.
