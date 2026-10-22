# Route de craft · Wings of Yin and Yang

`simplywings:yin_yang` · **Légendaire** · item `yin_yang_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-du-yin-et-du-yang)

L'équilibre, première paire asymétrique : une aile noire et une aile blanche. *Paire prévue, pas encore dans le mod* : la route est prête pour son ajout.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 3 niveaux de craft avant les pièces (max 3 en légendaire) |
| Crafts | 18 en `GRID` + 1 assemblage |
| Cloud Essence | 645 (pièces) + 800 (assemblage) = **1445** |
| Matières premières | 12 types du mod (35 objets) · 12 types vanilla (63 objets) |
| Drops du mod | Prism Shard (BLOCK · Diamond Ore, Deepslate Diamond Ore), Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Pale Quartz (BLOCK · Nether Quartz Ore), Void Grit (BLOCK · End Stone), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Abyssal Ink (MOB · Squid, Glow Squid), Echo Resonance (BLOCK · Sculk Shrieker), Pristine Feather (MOB · Chicken), Heavenly Down (MOB · Chicken), Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Abyssal Ink `simplywings:abyssal_ink` | 3 | `MOB` | Squid, Glow Squid | — | 40 % |
| Echo Resonance `simplywings:echo_resonance` | 3 | `BLOCK` | Sculk Shrieker | — | 50 % |
| Ember Wisp `simplywings:ember_wisp` | 2 | `MOB` | Blaze | — | 30 % |
| Gilded Vein `simplywings:gilded_vein` | 3 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Heavenly Down `simplywings:heavenly_down` | 3 | `MOB` | Chicken | Tué dans la couche de nuages (bande Y de la config Cloud Essence) | 60 % |
| Pale Quartz `simplywings:pale_quartz` | 2 | `BLOCK` | Nether Quartz Ore | — | 25 % |
| Prism Shard `simplywings:prism_shard` | 1 | `BLOCK` | Diamond Ore, Deepslate Diamond Ore | — | 50 % |
| Pristine Feather `simplywings:pristine_feather` | 12 | `MOB` | Chicken | — | 25 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Void Grit `simplywings:void_grit` | 2 | `BLOCK` | End Stone | — | 8 % |
| Black Dye `minecraft:black_dye` | 1 | `VANILLA` | Établi : poche d'encre | — | — |
| Blaze Powder `minecraft:blaze_powder` | 2 | `VANILLA` | Établi : bâton de blaze | — | — |
| Bone Meal `minecraft:bone_meal` | 3 | `VANILLA` | Établi : os | — | — |
| Diamond `minecraft:diamond` | 14 | `VANILLA` | Minage du diamant | — | — |
| End Rod `minecraft:end_rod` | 4 | `VANILLA` | Établi : bâton de blaze + chorus éclaté | — | — |
| Feather `minecraft:feather` | 12 | `VANILLA` | Poulets, tonneaux de la forge | — | — |
| Gold Ingot `minecraft:gold_ingot` | 12 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Obsidian `minecraft:obsidian` | 2 | `VANILLA` | Eau sur lave | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 2 | `VANILLA` | Phantoms | — | — |
| White Dye `minecraft:white_dye` | 1 | `VANILLA` | Établi : poudre d'os ou muguet | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Umbral Plume `simplywings:umbral_plume` ×6 | `GRID` | 1 | 3 | Feather ×4, Abyssal Ink ×1, Echo Resonance ×1 | 3 × 20 | 70 |
| 3 | Lucent Plume `simplywings:lucent_plume` ×6 | `GRID` | 1 | 3 | Pristine Feather ×4, Bone Meal ×1, Heavenly Down ×1 | 3 × 20 | 130 |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × 15 | 160 |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 25 | 185 |
| 6 | Yin Sphere `simplywings:yin_sphere` ×1 | `GRID` | 2 | 1 | Obsidian ×2, Umbral Plume ×1 → #2, Diamond ×1 | 40 | 225 |
| 7 | Yang Sphere `simplywings:yang_sphere` ×1 | `GRID` | 2 | 1 | Pale Quartz ×2, Lucent Plume ×1 → #3, Diamond ×1 | 40 | 265 |
| 8 | Celestial Frame `simplywings:celestial_frame` ×2 | `GRID` | 2 | 2 | End Rod ×2, Void Grit ×1, Diamond ×2, Gilded Frame ×1 → #4 | 2 × 35 | 335 |
| 9 | Radiant Harness `simplywings:radiant_harness` ×1 | `GRID` | 3 | 1 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1 → #5, Prism Shard ×1 | 50 | 385 |
| 10 | Taiji Core `simplywings:yin_yang_core` ×1 | `GRID` | 3 | 1 | Diamond ×4, Yin Sphere ×1 → #6, Yang Sphere ×1 → #7 | 100 | 485 |
| 11 | Yin Wing `simplywings:yin_wing` ×1 | `GRID` | 3 | 1 | Umbral Plume ×5 → #2, Celestial Frame ×1 → #8, Black Dye ×1 | 80 | 565 |
| 12 | Yang Wing `simplywings:yang_wing` ×1 | `GRID` | 3 | 1 | Lucent Plume ×5 → #3, Celestial Frame ×1 → #8, White Dye ×1 | 80 | 645 |
| 13 | Wings of Yin and Yang `simplywings:yin_yang_wings` | `ASM` | — | 1 | Harnais : Radiant Harness → #9 · Cœur : Taiji Core → #10 · Aile G : Yin Wing → #11 · Aile D : Yang Wing → #12 | 800 | **1445** |

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

### #2 · Umbral Plume · `simplywings:umbral_plume`

`GRID` · niveau 1 · 20 Cloud Essence · donne ×2

```
. _ _ _ .
_ F K F _
_ F E F _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Feather `minecraft:feather` | 4 | 0 |
| `K` | Abyssal Ink `simplywings:abyssal_ink` | 1 | 0 |
| `E` | Echo Resonance `simplywings:echo_resonance` | 1 | 0 |

### #3 · Lucent Plume · `simplywings:lucent_plume`

`GRID` · niveau 1 · 20 Cloud Essence · donne ×2

```
. _ _ _ .
_ F B F _
_ F H F _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Pristine Feather `simplywings:pristine_feather` | 4 | 0 |
| `B` | Bone Meal `minecraft:bone_meal` | 1 | 0 |
| `H` | Heavenly Down `simplywings:heavenly_down` | 1 | 0 |

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

### #6 · Yin Sphere · `simplywings:yin_sphere`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ O P O _
_ _ D _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `O` | Obsidian `minecraft:obsidian` | 2 | 0 |
| `P` | Umbral Plume `simplywings:umbral_plume` | 1 | 1 |
| `D` | Diamond `minecraft:diamond` | 1 | 0 |

### #7 · Yang Sphere · `simplywings:yang_sphere`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ Q P Q _
_ _ D _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `Q` | Pale Quartz `simplywings:pale_quartz` | 2 | 0 |
| `P` | Lucent Plume `simplywings:lucent_plume` | 1 | 1 |
| `D` | Diamond `minecraft:diamond` | 1 | 0 |

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

### #10 · Taiji Core · `simplywings:yin_yang_core`

`GRID` · niveau 3 · 100 Cloud Essence · donne ×1

```
. _ _ _ .
_ D _ D _
_ Y _ A _
_ D _ D _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `D` | Diamond `minecraft:diamond` | 4 | 0 |
| `Y` | Yin Sphere `simplywings:yin_sphere` | 1 | 2 |
| `A` | Yang Sphere `simplywings:yang_sphere` | 1 | 2 |

### #11 · Yin Wing · `simplywings:yin_wing`

`GRID` · niveau 3 · 80 Cloud Essence · donne ×1

```
. P P P .
_ P C P _
_ _ K _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Umbral Plume `simplywings:umbral_plume` | 5 | 1 |
| `C` | Celestial Frame `simplywings:celestial_frame` | 1 | 2 |
| `K` | Black Dye `minecraft:black_dye` | 1 | 0 |

### #12 · Yang Wing · `simplywings:yang_wing`

`GRID` · niveau 3 · 80 Cloud Essence · donne ×1

```
. P P P .
_ P C P _
_ _ W _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `P` | Lucent Plume `simplywings:lucent_plume` | 5 | 1 |
| `C` | Celestial Frame `simplywings:celestial_frame` | 1 | 2 |
| `W` | White Dye `minecraft:white_dye` | 1 | 0 |

## Pourquoi ces ingrédients

- Deux ailes différentes : la **Yin Wing** (gauche) et la **Yang Wing** (droite), chacune avec sa propre chaîne.
- **Yin** : **Echo Resonance** du deep dark, **Abyssal Ink** et obsidienne, le silence et le noir. **Yang** : **Heavenly Down** des nuages, **Pristine Feather**, poudre d'os et **Pale Quartz**, la lumière et le blanc.
- **Yin Sphere** et **Yang Sphere** : les deux sphères qui orbitent dans le dos, réunies dans le **Taiji Core**.
- Les deux moitiés du monde : le plus profond (deep dark) et le plus haut (couche de nuages).
