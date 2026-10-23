# Route de craft · Wings of Psyche

`simplywings:psyche` · **Légendaire** · item `psyche_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-psyché)

Psyché, la déesse de l'âme aux ailes de papillon : des flammes d'âme qui tiennent en apesanteur.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 3 niveaux de craft avant les pièces (max 3 en légendaire) |
| Crafts | 24 en `GRID` + 1 assemblage |
| Essences | 100 nuage + 320 vapeur + 425 éthérée (pièces) + 250 nuage + 250 vapeur + 300 éthérée (assemblage) = **350 nuage + 570 vapeur + 725 éthérée** |
| Matières premières | 9 types du mod (26 objets) · 11 types vanilla (77 objets) |
| Drops du mod | Prism Shard (BLOCK · Diamond Ore, Deepslate Diamond Ore), Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Glimmer Spore (BLOCK · Glow Lichen), Void Grit (BLOCK · End Stone), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Soul Wisp (BLOCK · Soul Sand, Soul Soil), Ember Wisp (MOB · Blaze) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Ember Wisp `simplywings:ember_wisp` | 2 | `MOB` | Blaze | — | 30 % |
| Gilded Vein `simplywings:gilded_vein` | 3 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Glimmer Spore `simplywings:glimmer_spore` | 4 | `BLOCK` | Glow Lichen | — | 30 % |
| Prism Shard `simplywings:prism_shard` | 1 | `BLOCK` | Diamond Ore, Deepslate Diamond Ore | — | 50 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Soul Wisp `simplywings:soul_wisp` | 10 | `BLOCK` | Soul Sand, Soul Soil | Biome `soul_sand_valley` | 8 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Void Grit `simplywings:void_grit` | 2 | `BLOCK` | End Stone | — | 8 % |
| Blaze Powder `minecraft:blaze_powder` | 2 | `VANILLA` | Établi : bâton de blaze | — | — |
| Diamond `minecraft:diamond` | 17 | `VANILLA` | Minage du diamant | — | — |
| End Rod `minecraft:end_rod` | 4 | `VANILLA` | Établi : bâton de blaze + chorus éclaté | — | — |
| Ender Pearl `minecraft:ender_pearl` | 4 | `VANILLA` | Endermen | — | — |
| Glass Bottle `minecraft:glass_bottle` | 5 | `VANILLA` | Établi : verre | — | — |
| Gold Ingot `minecraft:gold_ingot` | 16 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Phantom Membrane `minecraft:phantom_membrane` | 6 | `VANILLA` | Phantoms | — | — |
| Shulker Shell `minecraft:shulker_shell` | 8 | `VANILLA` | Shulkers (cités de l'End) | — | — |
| Soul Lantern `minecraft:soul_lantern` | 5 | `VANILLA` | Établi : torche des âmes + pépites de fer | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Weightless Pearl `simplywings:weightless_pearl` ×4 | `GRID` | 1 | 4 | Shulker Shell ×2, Ender Pearl ×1, Phantom Membrane ×1 | 4 × (10 vapeur + 15 éthérée) | 10 nuage + 40 vapeur + 60 éthérée |
| 3 | Soul Flame `simplywings:soul_flame` ×10 | `GRID` | 1 | 5 | Soul Wisp ×2, Soul Lantern ×1, Glass Bottle ×1 | 5 × (20 éthérée) | 10 nuage + 40 vapeur + 160 éthérée |
| 4 | Gilded Frame `simplywings:gilded_frame` ×2 | `GRID` | 1 | 2 | Gold Ingot ×4, Blaze Powder ×1, Gilded Vein ×1, Ember Wisp ×1 | 2 × (15 vapeur) | 10 nuage + 70 vapeur + 160 éthérée |
| 5 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 10 nuage + 15 vapeur | 20 nuage + 85 vapeur + 160 éthérée |
| 6 | Psyche's Lamp `simplywings:psyche_lamp` ×1 | `GRID` | 2 | 1 | Diamond ×1, Soul Flame ×2 → #3, Gold Ingot ×4 | 20 vapeur + 20 éthérée | 20 nuage + 105 vapeur + 180 éthérée |
| 7 | Soul Veil `simplywings:soul_veil` ×4 | `GRID` | 2 | 4 | Soul Flame ×2 → #3, Glimmer Spore ×1, Diamond ×1 | 4 × (20 vapeur + 20 éthérée) | 20 nuage + 185 vapeur + 260 éthérée |
| 8 | Celestial Frame `simplywings:celestial_frame` ×2 | `GRID` | 2 | 2 | End Rod ×2, Void Grit ×1, Diamond ×2, Gilded Frame ×1 → #4 | 2 × (15 vapeur + 20 éthérée) | 20 nuage + 215 vapeur + 300 éthérée |
| 9 | Radiant Harness `simplywings:radiant_harness` ×1 | `GRID` | 3 | 1 | Diamond ×4, Phantom Membrane ×2, Gilded Harness ×1 → #5, Prism Shard ×1 | 25 vapeur + 25 éthérée | 20 nuage + 240 vapeur + 325 éthérée |
| 10 | Psyche Core `simplywings:psyche_core` ×1 | `GRID` | 3 | 1 | Weightless Pearl ×2 → #2, Psyche's Lamp ×1 → #6, Diamond ×4 | 30 nuage + 30 vapeur + 40 éthérée | 50 nuage + 270 vapeur + 365 éthérée |
| 11 | Psyche Wing `simplywings:psyche_wing` ×2 | `GRID` | 3 | 2 | Soul Veil ×2 → #7, Celestial Frame ×1 → #8, Weightless Pearl ×1 → #2 | 2 × (25 nuage + 25 vapeur + 30 éthérée) | 100 nuage + 320 vapeur + 425 éthérée |
| 12 | Wings of Psyche `simplywings:psyche_wings` | `ASM` | — | 1 | Harnais : Radiant Harness → #9 · Cœur : Psyche Core → #10 · Aile G : Psyche Wing → #11 · Aile D : Psyche Wing → #11 | 250 nuage + 250 vapeur + 300 éthérée | **350 nuage + 570 vapeur + 725 éthérée** |

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

### #2 · Weightless Pearl · `simplywings:weightless_pearl`

`GRID` · niveau 1 · essence : 10 vapeur + 15 éthérée · donne ×1

```
. _ _ _ .
_ S P S _
_ _ M _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `S` | Shulker Shell `minecraft:shulker_shell` | 2 | 0 |
| `P` | Ender Pearl `minecraft:ender_pearl` | 1 | 0 |
| `M` | Phantom Membrane `minecraft:phantom_membrane` | 1 | 0 |

### #3 · Soul Flame · `simplywings:soul_flame`

`GRID` · niveau 1 · essence : 20 éthérée · donne ×2

```
. _ _ _ .
_ W L W _
_ _ B _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `W` | Soul Wisp `simplywings:soul_wisp` | 2 | 0 |
| `L` | Soul Lantern `minecraft:soul_lantern` | 1 | 0 |
| `B` | Glass Bottle `minecraft:glass_bottle` | 1 | 0 |

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

### #6 · Psyche's Lamp · `simplywings:psyche_lamp`

`GRID` · niveau 2 · essence : 20 vapeur + 20 éthérée · donne ×1

```
. _ _ _ .
_ _ D _ _
_ F G F _
_ G G G _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `D` | Diamond `minecraft:diamond` | 1 | 0 |
| `F` | Soul Flame `simplywings:soul_flame` | 2 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 4 | 0 |

### #7 · Soul Veil · `simplywings:soul_veil`

`GRID` · niveau 2 · essence : 20 vapeur + 20 éthérée · donne ×1

```
. _ _ _ .
_ F K F _
_ _ D _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `F` | Soul Flame `simplywings:soul_flame` | 2 | 1 |
| `K` | Glimmer Spore `simplywings:glimmer_spore` | 1 | 0 |
| `D` | Diamond `minecraft:diamond` | 1 | 0 |

### #8 · Celestial Frame · `simplywings:celestial_frame` · partagé

`GRID` · niveau 2 · essence : 15 vapeur + 20 éthérée · donne ×1

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

### #10 · Psyche Core · `simplywings:psyche_core`

`GRID` · niveau 3 · essence : 30 nuage + 30 vapeur + 40 éthérée · donne ×1

```
. _ _ _ .
_ W L W _
_ D _ D _
_ D _ D _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `W` | Weightless Pearl `simplywings:weightless_pearl` | 2 | 1 |
| `L` | Psyche's Lamp `simplywings:psyche_lamp` | 1 | 2 |
| `D` | Diamond `minecraft:diamond` | 4 | 0 |

### #11 · Psyche Wing · `simplywings:psyche_wing`

`GRID` · niveau 3 · essence : 25 nuage + 25 vapeur + 30 éthérée · donne ×1

```
. _ _ _ .
_ V C V _
_ _ W _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `V` | Soul Veil `simplywings:soul_veil` | 2 | 2 |
| `C` | Celestial Frame `simplywings:celestial_frame` | 1 | 2 |
| `W` | Weightless Pearl `simplywings:weightless_pearl` | 1 | 1 |

## Pourquoi ces ingrédients

- **Soul Wisp** : en creusant le sable et la terre des âmes dans la vallée des âmes.
- **Soul Flame** (intermédiaire) : l'âme mise en flacon avec une lanterne des âmes, la flamme bleu-cyan de la paire. **Soul Veil** : avec la **Glimmer Spore**, la lueur des ailes de papillon.
- **Psyche's Lamp** : dans le mythe, Psyché allume une lampe à huile pour voir le visage d'Éros. C'est le cœur de la paire.
- **Weightless Pearl** (intermédiaire) : carapaces de shulker et perle de l'Ender. La lévitation des shulkers, c'est l'apesanteur de la stabilité maxée.
