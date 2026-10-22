# Route de craft · Wings of the Forest Spirit

`simplywings:forest` · **Épique** · item `forest_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-lesprit-sylvestre)

Un esprit de la forêt, comme un kodama : feuilles, fleurs et branches.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 2 niveaux de craft avant les pièces (max 2 en épique) |
| Crafts | 9 en `GRID` + 1 assemblage |
| Cloud Essence | 240 (pièces) + 300 (assemblage) = **540** |
| Matières premières | 8 types du mod (19 objets) · 8 types vanilla (38 objets) |
| Drops du mod | Gilded Vein (BLOCK · Gold Ore, Deepslate Gold Ore, Nether Gold Ore), Blossom Spirit (BLOCK · Cherry Leaves), Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Ghast Wisp (MOB · Ghast), Acorn (BLOCK · Oak Leaves, Dark Oak Leaves), Glimmer Spore (BLOCK · Glow Lichen) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Acorn `simplywings:acorn` | 6 | `BLOCK` | Oak Leaves, Dark Oak Leaves | — | 5 % |
| Blossom Spirit `simplywings:blossom_spirit` | 4 | `BLOCK` | Cherry Leaves | — | 6 % |
| Ghast Wisp `simplywings:ghast_wisp` | 1 | `MOB` | Ghast | — | 50 % |
| Gilded Vein `simplywings:gilded_vein` | 1 | `BLOCK` | Gold Ore, Deepslate Gold Ore, Nether Gold Ore | — | 30 % |
| Glimmer Spore `simplywings:glimmer_spore` | 3 | `BLOCK` | Glow Lichen | — | 30 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Glass `minecraft:glass` | 4 | `VANILLA` | Fonte du sable | — | — |
| Gold Ingot `minecraft:gold_ingot` | 12 | `VANILLA` | Minage de l'or, piglins, fonte | — | — |
| Iron Ingot `minecraft:iron_ingot` | 2 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Oak Leaves `minecraft:oak_leaves` | 4 | `VANILLA` | Chênes, cisailles ou toucher de soie | — | — |
| Oak Log `minecraft:oak_log` | 3 | `VANILLA` | Chênes | — | — |
| Pink Petals `minecraft:pink_petals` | 4 | `VANILLA` | Cerisaies | — | — |
| Spore Blossom `minecraft:spore_blossom` | 1 | `VANILLA` | Plafond des grottes luxuriantes | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 | 10 |
| 2 | Spirit Vessel `simplywings:spirit_vessel` ×1 | `GRID` | 1 | 1 | Gold Ingot ×4, Glass ×4, Ghast Wisp ×1 | 20 | 30 |
| 3 | Living Bough `simplywings:living_bough` ×3 | `GRID` | 1 | 3 | Acorn ×2, Oak Log ×1, Glimmer Spore ×1 | 3 × 15 | 75 |
| 4 | Gilded Harness `simplywings:gilded_harness` ×1 | `GRID` | 2 | 1 | Gold Ingot ×4, Leather ×2, Iron-Buckled Harness ×1 → #1, Gilded Vein ×1 | 25 | 100 |
| 5 | Forest Spirit Core `simplywings:forest_core` ×1 | `GRID` | 2 | 1 | Blossom Spirit ×2, Gold Ingot ×2, Spore Blossom ×1, Spirit Vessel ×1 → #2, Living Bough ×1 → #3 | 60 | 160 |
| 6 | Forest Spirit Wing `simplywings:forest_wing` ×2 | `GRID` | 2 | 2 | Oak Leaves ×2, Blossom Spirit ×1, Pink Petals ×2, Living Bough ×1 → #3, Gold Ingot ×1 | 2 × 40 | 240 |
| 7 | Wings of the Forest Spirit `simplywings:forest_wings` | `ASM` | — | 1 | Harnais : Gilded Harness → #4 · Cœur : Forest Spirit Core → #5 · Aile G : Forest Spirit Wing → #6 · Aile D : Forest Spirit Wing → #6 | 300 | **540** |

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

### #2 · Spirit Vessel · `simplywings:spirit_vessel` · partagé

`GRID` · niveau 1 · 20 Cloud Essence · donne ×1

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

### #3 · Living Bough · `simplywings:living_bough`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. _ _ _ .
_ A O A _
_ _ M _ _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `A` | Acorn `simplywings:acorn` | 2 | 0 |
| `O` | Oak Log `minecraft:oak_log` | 1 | 0 |
| `M` | Glimmer Spore `simplywings:glimmer_spore` | 1 | 0 |

### #4 · Gilded Harness · `simplywings:gilded_harness` · partagé

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

### #5 · Forest Spirit Core · `simplywings:forest_core`

`GRID` · niveau 2 · 60 Cloud Essence · donne ×1

```
. _ _ _ .
_ B G B _
_ S V L _
_ _ G _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Blossom Spirit `simplywings:blossom_spirit` | 2 | 0 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 2 | 0 |
| `S` | Spore Blossom `minecraft:spore_blossom` | 1 | 0 |
| `V` | Spirit Vessel `simplywings:spirit_vessel` | 1 | 1 |
| `L` | Living Bough `simplywings:living_bough` | 1 | 1 |

### #6 · Forest Spirit Wing · `simplywings:forest_wing`

`GRID` · niveau 2 · 40 Cloud Essence · donne ×1

```
. _ _ _ .
_ K B K _
_ P L P _
_ _ G _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `K` | Oak Leaves `minecraft:oak_leaves` | 2 | 0 |
| `B` | Blossom Spirit `simplywings:blossom_spirit` | 1 | 0 |
| `P` | Pink Petals `minecraft:pink_petals` | 2 | 0 |
| `L` | Living Bough `simplywings:living_bough` | 1 | 1 |
| `G` | Gold Ingot `minecraft:gold_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Blossom Spirit** : en cassant des feuilles de cerisier, les fleurs roses de l'aile. **Acorn** : sur les chênes (partagé avec l'écureuil volant).
- **Living Bough** (intermédiaire) : la branche vivante qui sert d'armature, avec la **Glimmer Spore** du lichen luisant.
- **Spore Blossom** : l'esprit des grottes luxuriantes, la forêt souterraine.
- **Oak Leaves** et **Pink Petals** : le feuillage dentelé tacheté de fleurs roses.
