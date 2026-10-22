# Route de craft · Bat Wings

`simplywings:bat` · **Rare** · item `bat_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-chauve-souris)

Agiles et nerveuses, une vraie main de chauve-souris tendue de membrane. La route se fait dans les grottes, là où vivent les chauves-souris.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 70 (pièces) + 100 (assemblage) = **170** |
| Drops du mod | Bat Membrane (MOB · Bat) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Bat Membrane `simplywings:bat_membrane` | 14 | `MOB` | Bat | — | 50 % |
| Bone `minecraft:bone` | 8 | `VANILLA` | Squelettes | — | — |
| Iron Ingot `minecraft:iron_ingot` | 9 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 12 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Redstone Dust `minecraft:redstone` | 2 | `VANILLA` | Minage de la redstone | — | — |
| Sculk Sensor `minecraft:sculk_sensor` | 1 | `VANILLA` | Deep dark, toucher de soie | — | — |
| String `minecraft:string` | 1 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Bat Core `simplywings:bat_core` ×1 | `GRID` | 1 | 1 | Iron Ingot ×4, Bat Membrane ×2, Redstone Dust ×2, Sculk Sensor ×1 | 30 | 40 |
| 3 | Bat Wing `simplywings:bat_wing` ×2 | `GRID` | 1 | 2 | Bone ×4, Bat Membrane ×6, Iron Ingot ×1, Leather ×2 | 2 × 15 | 70 |
| 4 | Bat Wings `simplywings:bat_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Bat Core → #2 · Aile G : Bat Wing → #3 · Aile D : Bat Wing → #3 | 100 | **170** |

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

### #2 · Bat Core · `simplywings:bat_core`

`GRID` · niveau 1 · 30 Cloud Essence · donne ×1

```
. _ I _ .
_ M R M _
_ I S I _
_ _ R _ _
. _ I _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `I` | Iron Ingot `minecraft:iron_ingot` | 4 | 0 |
| `M` | Bat Membrane `simplywings:bat_membrane` | 2 | 0 |
| `R` | Redstone Dust `minecraft:redstone` | 2 | 0 |
| `S` | Sculk Sensor `minecraft:sculk_sensor` | 1 | 0 |

### #3 · Bat Wing · `simplywings:bat_wing`

`GRID` · niveau 1 · 15 Cloud Essence · donne ×1

```
. B _ B .
B M M M B
_ M I M _
_ L M L _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Bone `minecraft:bone` | 4 | 0 |
| `M` | Bat Membrane `simplywings:bat_membrane` | 6 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |
| `L` | Leather `minecraft:leather` | 2 | 0 |

## Pourquoi ces ingrédients

- **Bat Membrane** : sur les chauves-souris elles-mêmes, dans le noir des grottes. Petites et erratiques, elles se chassent au corps à corps, ce qui rappelle l'agilité de la paire.
- **Bones** pour les quatre doigts de la main, **Leather** pour le patagium jusqu'à la cuisse.
- **Sculk Sensor** dans le cœur : l'écholocation. Il se récupère au toucher de soie dans le deep dark, le voisin des grottes à chauves-souris.
- **Redstone** : le signal du sonar, qui relie le capteur au cœur.
