# Route de craft · Kite Wings

`simplywings:kite` · **Rare** · item `kite_wings` · fiche : [`WINGS.md`](../WINGS.md#ailes-de-cerf-volant)

Papier et baguettes : la paire la moins chère, faite main, qui porte au moindre souffle.

Règles, légende et objets partagés : [`CRAFTING.md`](../CRAFTING.md). Dernière mise à jour : 22 octobre 2026.

## En bref

| | |
| --- | --- |
| Profondeur | 1 niveau de craft avant les pièces (max 1 en rare) |
| Crafts | 4 en `GRID` + 1 assemblage |
| Cloud Essence | 50 (pièces) + 100 (assemblage) = **150** |
| Drops du mod | Updraft Shard (BLOCK · `#minecraft:base_stone_overworld`) |

## Matières premières

Tout ce que la route consomme (niveau 0), pour une paire complète. Les drops du mod ne tombent que si le coup fatal, le bloc cassé ou la prise est fait avec un item imprégné d'énergie ailée.

| Objet | Qté | Où | Source | Condition | Chance |
| --- | --- | --- | --- | --- | --- |
| Updraft Shard `simplywings:updraft_shard` | 1 | `BLOCK` | `#minecraft:base_stone_overworld` | Y ≥ 150 | 8 % |
| Bamboo `minecraft:bamboo` | 8 | `VANILLA` | Jungles de bambous | — | — |
| Iron Ingot `minecraft:iron_ingot` | 4 | `VANILLA` | Minage du fer, fonte | — | — |
| Leather `minecraft:leather` | 8 | `VANILLA` | Vaches, chevaux, tonneaux de la forge | — | — |
| Paper `minecraft:paper` | 6 | `VANILLA` | Établi : canne à sucre | — | — |
| Red Dye `minecraft:red_dye` | 2 | `VANILLA` | Établi : coquelicot, betterave | — | — |
| String `minecraft:string` | 7 | `VANILLA` | Araignées, toiles | — | — |

## Route

Niv. = niveau de craft (0 = matière première). Crafts = nombre de fois que la recette est lancée. Les entrées sont données pour un seul craft.

| # | Produit | Où | Niv. | Crafts | Entrées par craft | Essence | Cumul |
| --- | --- | --- | --- | --- | --- | --- | --- |
| 1 | Iron-Buckled Harness `simplywings:iron_harness` ×1 | `GRID` | 1 | 1 | Leather ×8, Iron Ingot ×3, String ×1 | 10 | 10 |
| 2 | Kite Core `simplywings:kite_core` ×1 | `GRID` | 1 | 1 | String ×4, Iron Ingot ×1, Bamboo ×2, Updraft Shard ×1 | 20 | 30 |
| 3 | Kite Wing `simplywings:kite_wing` ×2 | `GRID` | 1 | 2 | Bamboo ×3, Paper ×3, Red Dye ×1, String ×1 | 2 × 10 | 50 |
| 4 | Kite Wings `simplywings:kite_wings` | `ASM` | — | 1 | Harnais : Iron-Buckled Harness → #1 · Cœur : Kite Core → #2 · Aile G : Kite Wing → #3 · Aile D : Kite Wing → #3 | 100 | **150** |

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

### #2 · Kite Core · `simplywings:kite_core`

`GRID` · niveau 1 · 20 Cloud Essence · donne ×1

```
. _ _ _ .
_ T I T _
_ B U B _
_ T _ T _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `T` | String `minecraft:string` | 4 | 0 |
| `I` | Iron Ingot `minecraft:iron_ingot` | 1 | 0 |
| `B` | Bamboo `minecraft:bamboo` | 2 | 0 |
| `U` | Updraft Shard `simplywings:updraft_shard` | 1 | 0 |

### #3 · Kite Wing · `simplywings:kite_wing`

`GRID` · niveau 1 · 10 Cloud Essence · donne ×1

```
. _ B _ .
_ P R P _
_ B P B _
_ _ S _ _
. _ _ _ .
```

| Lettre | Ingrédient | Qté | Niv. |
| --- | --- | --- | --- |
| `B` | Bamboo `minecraft:bamboo` | 3 | 0 |
| `P` | Paper `minecraft:paper` | 3 | 0 |
| `R` | Red Dye `minecraft:red_dye` | 1 | 0 |
| `S` | String `minecraft:string` | 1 | 0 |

## Pourquoi ces ingrédients

- **Paper** et **Bamboo** : la voile et les longerons, exactement comme un vrai cerf-volant.
- **Red Dye** : les rayures rouges ; **String** : la ligature et la ligne.
- **Updraft Shard** : un cerf-volant ne vole qu'avec du vent ; il faut monter le chercher en altitude.
- Coûts réduits (cœur 20, ailes 10) : c'est l'aile la plus légère des rares.
