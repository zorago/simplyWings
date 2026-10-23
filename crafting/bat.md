# Route de craft · Bat Wings

`simplywings:bat` · **Rare** · item `bat_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-chauve-souris)

Agiles et nerveuses, une vraie main de chauve-souris tendue de membrane. La route se fait dans les grottes, là où vivent les chauves-souris.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 23 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Essences | 70 nuage (pièces) + 100 nuage (assemblage) = **170 nuage** |
| Matières premières | 6 types du mod (28 objets) · 4 types vanilla (19 objets) |
| Drops du mod | Supple Hide (MOB · Cow, Mooshroom, Horse, Donkey, Llama), Silk Thread (MOB · Spider, Cave Spider), Sky Iron Fleck (BLOCK · Iron Ore, Deepslate Iron Ore), Bat Membrane (MOB · Bat), Hollow Bone (MOB · Skeleton, Stray, Bogged), Elastic Gel (MOB · Slime, Magma Cube) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Bat Membrane `simplywings:bat_membrane` | 12 | `MOB` | Bat | — | 50 % |
| Elastic Gel `simplywings:elastic_gel` | 2 | `MOB` | Slime, Magma Cube | — | 40 % |
| Hollow Bone `simplywings:hollow_bone` | 8 | `MOB` | Skeleton, Stray, Bogged | — | 35 % |
| Silk Thread `simplywings:silk_thread` | 1 | `MOB` | Spider, Cave Spider | — | 40 % |
| Sky Iron Fleck `simplywings:sky_iron` | 3 | `BLOCK` | Iron Ore, Deepslate Iron Ore | — | 25 % |
| Supple Hide `simplywings:supple_hide` | 2 | `MOB` | Cow, Mooshroom, Horse, Donkey, Llama | — | 35 % |
| Iron Ingot `minecraft:iron_ingot` | 6 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 10 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Redstone Dust `minecraft:redstone` | 2 | `VANILLA` | Minage de la redstone | — | — |
| Sculk Sensor `minecraft:sculk_sensor` | 1 | `VANILLA` | Deep dark, toucher de soie | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×6, Supple Hide ×2, Iron Ingot ×2, Silk Thread ×1, Sky Iron Fleck ×1 | 10 nuage | 10 nuage |
| 2 | Bat Core `simplywings:bat_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×2, Bat Membrane ×2, Redstone Dust ×2, Sky Iron Fleck ×2, Sculk Sensor ×1 | 30 nuage | 40 nuage |
| 3 | Bat Wing `simplywings:bat_wing` ×2 | `GRID` | 1 | 2 | Hollow Bone ×4, Bat Membrane ×5, Iron Ingot ×1, Leather ×2, Elastic Gel ×1 | 2 × (15 nuage) | 70 nuage |
| 4 | Bat Wings `simplywings:bat_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Bat Core → #2 · Aile G : Bat Wing → #3 · Aile D : Bat Wing → #3 | 100 nuage | **170 nuage** |

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

### #2 · Bat Core · `simplywings:bat_core`

`GRID` · niveau 1 · essence : 30 nuage · donne ×1

```
. _ I _ .
_ M R M _
_ K S K _
_ _ R _ _
. _ I _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 2 | 0 |
| `M` | Bat Membrane `simplywings:bat_membrane` | 2 | 0 |
| `R` | Redstone Dust `minecraft:redstone` | 2 | 0 |
| `K` | Sky Iron Fleck `simplywings:sky_iron` | 2 | 0 |
| `S` | Sculk Sensor `minecraft:sculk_sensor` | 1 | 0 |

### #3 · Bat Wing · `simplywings:bat_wing`

`GRID` · niveau 1 · essence : 15 nuage · donne ×1

```
. B _ B .
B M M M B
_ M I M _
_ L G L _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Hollow Bone `simplywings:hollow_bone` | 4 | 0 |
| `M` | Bat Membrane `simplywings:bat_membrane` | 5 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |
| `L` | Leather `minecraft:leather` | 2 | 0 |
| `G` | Elastic Gel `simplywings:elastic_gel` | 1 | 0 |

## Pourquoi ces ingrédients

- **Bat Membrane** : sur les chauves-souris elles-mêmes, dans le noir des grottes. Petites et erratiques, elles se chassent au corps à corps, ce qui rappelle l'agilité de la paire.
- **Hollow Bone** : les os creux des squelettes, croisés dans les mêmes grottes, pour les quatre doigts de la main. **Elastic Gel** des slimes : l'élasticité de la membrane qui se contracte quand l'aile se plie.
- **Sculk Sensor** dans le cœur : l'écholocation. Il se récupère au toucher de soie dans le deep dark, le voisin des grottes à chauves-souris.
- **Redstone** : le signal du sonar ; **Sky Iron Fleck** : le fer des filons, cassé dans les grottes avec l'outil imprégné.
