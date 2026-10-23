# Route de craft · Parrot Wings

`simplywings:parrot` · **Rare** · item `parrot_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-perroquet)

Le clin d'œil au perroquet de Minecraft : un ara rouge, jaune et bleu.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Essences | 70 nuage (pièces) + 100 nuage (assemblage) = **170 nuage** |
| Matières premières | 6 types du mod (18 objets) · 6 types vanilla (21 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Macaw Plume (BLOCK · Jungle Leaves), Lapis Glint (BLOCK · Lapis Ore, Deepslate Lapis Ore), Pristine Feather (MOB · Chicken) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Lapis Glint `simplywings:lapis_glint` | 2 | `BLOCK` | Lapis Ore, Deepslate Lapis Ore | — | 30 % |
| Macaw Plume `simplywings:macaw_plume` | 8 | `BLOCK` | Jungle Leaves | — | 5 % |
| Pristine Feather `simplywings:pristine_feather` | 4 | `MOB` | Chicken | — | 25 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 1 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Cocoa Beans `minecraft:cocoa_beans` | 2 | `VANILLA` | Cabosses de la jungle | — | — |
| Iron Ingot `minecraft:iron_ingot` | 8 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 6 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Note Block `minecraft:note_block` | 1 | `VANILLA` | Établi : planches + redstone | — | — |
| Red Dye `minecraft:red_dye` | 2 | `VANILLA` | Établi : coquelicot, betterave | — | — |
| Yellow Dye `minecraft:yellow_dye` | 2 | `VANILLA` | Établi : pissenlit | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Parrot Core `simplywings:parrot_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Macaw Plume ×2, Cocoa Beans ×2, Note Block ×1 | 30 nuage | 40 nuage |
| 3 | Parrot Wing `simplywings:parrot_wing` ×2 | `GRID` | 1 | 2 | Macaw Plume ×3, Red Dye ×1, Yellow Dye ×1, Lapis Glint ×1, Pristine Feather ×2, Iron Ingot ×1 | 2 × (15 nuage) | 70 nuage |
| 4 | Parrot Wings `simplywings:parrot_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Parrot Core → #2 · Aile G : Parrot Wing → #3 · Aile D : Parrot Wing → #3 | 100 nuage | **170 nuage** |

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

### #2 · Parrot Core · `simplywings:parrot_core`

`GRID` · niveau 1 · essence : 30 nuage · donne ×1

```
. _ _ _ .
_ I M I _
_ C N C _
_ I M I _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `M` | Macaw Plume `simplywings:macaw_plume` | 2 | 0 |
| `C` | Cocoa Beans `minecraft:cocoa_beans` | 2 | 0 |
| `N` | Note Block `minecraft:note_block` | 1 | 0 |

### #3 · Parrot Wing · `simplywings:parrot_wing`

`GRID` · niveau 1 · essence : 15 nuage · donne ×1

```
. M M M .
_ R Y B _
_ F I F _
_ _ _ _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `M` | Macaw Plume `simplywings:macaw_plume` | 3 | 0 |
| `R` | Red Dye `minecraft:red_dye` | 1 | 0 |
| `Y` | Yellow Dye `minecraft:yellow_dye` | 1 | 0 |
| `B` | Lapis Glint `simplywings:lapis_glint` | 1 | 0 |
| `F` | Pristine Feather `simplywings:pristine_feather` | 2 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |

## Pourquoi ces ingrédients

- **Macaw Plume** : en cassant des feuilles de jungle, la canopée où nichent les perroquets. On ne tue pas de perroquet.
- **Note Block** : le perroquet danse sur la musique et imite les sons. **Cocoa Beans** : la jungle, encore.
- **Red Dye**, **Yellow Dye**, **Lapis Glint** : les trois couleurs de l'ara, posées en ligne dans la recette comme les bandes de l'aile.
- **Pristine Feather** : des plumes impeccables pour un plumage éclatant.
