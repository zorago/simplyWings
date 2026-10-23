# Route de craft · Wings of Daedalus

`simplywings:daedalus` · **Légendaire** · item `daedalus_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-dédale)

Dédale, l'inventeur du labyrinthe, et ses ailes mécaniques. La route passe par le labyrinthe de Minecraft : les chambres d'épreuves.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 3 niveaux de craft avant les pièces (max 3 en légendaire) |
| Crafts | 25 en `GRID` + 1 assemblage |
| Essences | 100 nuage + 200 vapeur + 365 éthérée (pièces) + 250 nuage + 250 vapeur + 300 éthérée (assemblage) = **350 nuage + 450 vapeur + 665 éthérée** |
| Matières premières | 9 types du mod (43 objets) · 10 types vanilla (55 objets) |
| Drops du mod | Prism Shard (BLOCK · Diamond Ore, Deepslate Diamond Ore), Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Gust Mote (MOB · Breeze), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Copper Spark (BLOCK · Copper Ore, Deepslate Copper Ore), Soot Flake (BLOCK · Coal Ore, Deepslate Coal Ore), Pristine Feather (MOB · Chicken) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Copper Spark `simplywings:copper_spark` | 18 | `BLOCK` | Copper Ore, Deepslate Copper Ore | — | 25 % |
| Gilded Vein `simplywings:gilded_vein` | 7 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Gust Mote `simplywings:gust_mote` | 2 | `MOB` | Breeze | — | 40 % |
| Prism Shard `simplywings:prism_shard` | 1 | `BLOCK` | Diamond Ore, Deepslate Diamond Ore | — | 50 % |
| Pristine Feather `simplywings:pristine_feather` | 4 | `MOB` | Chicken | — | 25 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Soot Flake `simplywings:soot_flake` | 7 | `BLOCK` | Coal Ore, Deepslate Coal Ore | — | 20 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Breeze Rod `minecraft:breeze_rod` | 2 | `VANILLA` | Breezes (chambres d'épreuves) | — | — |
| Crafter `minecraft:crafter` | 1 | `VANILLA` | Établi | — | — |
| Diamond `minecraft:diamond` | 10 | `VANILLA` | Minage du diamant | — | — |
| Gold Ingot `minecraft:gold_ingot` | 4 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Honeycomb `minecraft:honeycomb` | 2 | `VANILLA` | Cisailles sur une ruche pleine | — | — |
| Iron Ingot `minecraft:iron_ingot` | 23 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Observer `minecraft:observer` | 2 | `VANILLA` | Établi | — | — |
| Ominous Trial Key `minecraft:ominous_trial_key` | 1 | `VANILLA` | Générateur d'épreuves sinistre (Mauvais présage d'épreuve) | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 2 | `VANILLA` | Phantoms | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Brass Ingot `simplywings:brass_ingot` ×12 | `GRID` | 1 | 6 | Copper Spark ×3, Gilded Vein ×1 | 6 × (10 éthérée) | 10 nuage + 60 éthérée |
| 3 | Steel Plate `simplywings:steel_plate` ×14 | `GRID` | 1 | 7 | Iron Ingot ×3, Soot Flake ×1 | 7 × (10 éthérée) | 10 nuage + 130 éthérée |
| 4 | Icarus Feather `simplywings:icarus_feather` ×4 | `GRID` | 1 | 2 | Pristine Feather ×2, Honeycomb ×1 | 2 × (15 éthérée) | 10 nuage + 160 éthérée |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 10 nuage + 15 vapeur | 20 nuage + 15 vapeur + 160 éthérée |
| 6 | Wind Thruster `simplywings:wind_thruster` ×2 | `GRID` | 2 | 2 | Brass Ingot ×2 → #2, Gust Mote ×1, Steel Plate ×2 → #3, Breeze Rod ×1, Observer ×1 | 2 × (20 vapeur + 20 éthérée) | 20 nuage + 55 vapeur + 200 éthérée |
| 7 | Brass Armature `simplywings:brass_armature` ×2 | `GRID` | 2 | 2 | Brass Ingot ×4 → #2, Diamond ×1 | 2 × (20 vapeur + 20 éthérée) | 20 nuage + 95 vapeur + 240 éthérée |
| 8 | Radiant Harness `simplywings:radiant_harness` ×1 | `GRID` | 3 | 1 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1 → #5, Prism Shard ×1 | 25 vapeur + 25 éthérée | 20 nuage + 120 vapeur + 265 éthérée |
| 9 | Daedalus Core `simplywings:daedalus_core` ×1 | `GRID` | 3 | 1 | Wind Thruster ×2 → #6, Ominous Trial Key ×1, Diamond ×4, Crafter ×1 | 30 nuage + 30 vapeur + 40 éthérée | 50 nuage + 150 vapeur + 305 éthérée |
| 10 | Daedalus Wing `simplywings:daedalus_wing` ×2 | `GRID` | 3 | 2 | Steel Plate ×5 → #3, Brass Armature ×1 → #7, Icarus Feather ×2 → #4 | 2 × (25 nuage + 25 vapeur + 30 éthérée) | 100 nuage + 200 vapeur + 365 éthérée |
| 11 | Wings of Daedalus `simplywings:daedalus_wings` | `ASM` | — | 1 | Harnais : Radiant Harness → #8 · Cœur : Daedalus Core → #9 · Aile G : Daedalus Wing → #10 · Aile D : Daedalus Wing → #10 | 250 nuage + 250 vapeur + 300 éthérée | **350 nuage + 450 vapeur + 665 éthérée** |

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

### #2 · Brass Ingot · `simplywings:brass_ingot`

`GRID` · niveau 1 · essence : 10 éthérée · donne ×2

```
. _ _ _ .
_ C G C _
_ _ C _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `C` | Copper Spark `simplywings:copper_spark` | 3 | 0 |
| `G` | Gilded Vein `simplywings:gilded_vein` | 1 | 0 |

### #3 · Steel Plate · `simplywings:steel_plate`

`GRID` · niveau 1 · essence : 10 éthérée · donne ×2

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
| `C` | Soot Flake `simplywings:soot_flake` | 1 | 0 |

### #4 · Icarus Feather · `simplywings:icarus_feather`

`GRID` · niveau 1 · essence : 15 éthérée · donne ×2

```
. _ _ _ .
_ F H F _
_ _ _ _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Pristine Feather `simplywings:pristine_feather` | 2 | 0 |
| `H` | Honeycomb `minecraft:honeycomb` | 1 | 0 |

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

### #6 · Wind Thruster · `simplywings:wind_thruster`

`GRID` · niveau 2 · essence : 20 vapeur + 20 éthérée · donne ×1

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
| `W` | Gust Mote `simplywings:gust_mote` | 1 | 0 |
| `P` | Steel Plate `simplywings:steel_plate` | 2 | 1 |
| `X` | Breeze Rod `minecraft:breeze_rod` | 1 | 0 |
| `O` | Observer `minecraft:observer` | 1 | 0 |

### #7 · Brass Armature · `simplywings:brass_armature`

`GRID` · niveau 2 · essence : 20 vapeur + 20 éthérée · donne ×1

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

### #9 · Daedalus Core · `simplywings:daedalus_core`

`GRID` · niveau 3 · essence : 30 nuage + 30 vapeur + 40 éthérée · donne ×1

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

`GRID` · niveau 3 · essence : 25 nuage + 25 vapeur + 30 éthérée · donne ×1

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
- **Wind Thruster** (intermédiaire) : bâton de breeze, **Gust Mote**, laiton, acier et observateur, les réacteurs vectorisés qui donnent la maniabilité maxée.
- **Brass Ingot** (Copper Spark + Gilded Vein) et **Steel Plate** (fer + Soot Flake, le carbone) : le laiton de l'armature et l'acier des panneaux rivetés. **Crafter** : la machine qui fabrique.
- **Icarus Feather** : des **Pristine Feathers** collées à la cire de rayon de miel, les ailes d'Icare. Un rappel : ne pas voler trop près du soleil.
