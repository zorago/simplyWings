# Routes de craft des ailes

Les règles communes à toutes les routes, les objets partagés, le registre des drops du mod et le récapitulatif des coûts. **Chaque paire a sa route complète dans son propre fichier**, `crafting/<id>.md` (matières premières, étapes, recettes dessinées, justification des ingrédients). Seule la paire commune reste décrite ici. Le cadre général (table d'assemblage, établi de la forge, essences, énergie ailée) est dans `CAHIER_DES_CHARGES.md`, sections « Craft : table d'assemblage et établi de la forge » et « Essences ».

Dernière mise à jour : 23 octobre 2026.

## Règles des routes

Fixées par zorago le 22 octobre 2026, par ordre d'importance.

**1. Non négociables**

1. **Sources 100 % vanilla** : tout s'obtient avec Minecraft et Simply Wings seuls, sans Create, Ad Astra ni aucun autre mod.
2. **Craft répétable** : rien ne dépend d'une ressource unique (œuf de dragon, etc.). Tout se refarme : le dragon se réinvoque, le Wither aussi, les raids, les générateurs d'épreuves et les géodes se renouvellent.
3. **Profondeur maximale** des pièces (harnais, cœur, ailes), en niveaux de craft :

| Rareté | Niveau max d'une pièce | Lecture |
| --- | --- | --- |
| Rare | 1 | Les 4 pièces se craftent directement depuis les matières premières |
| Épique | 2 | Un intermédiaire possible avant la pièce |
| Légendaire | 3 | Deux niveaux d'intermédiaires |
| Mythique | 5 | Quatre niveaux d'intermédiaires |

**2. Important** : les ressources sont cohérentes avec l'aile, et se récupèrent par un gameplay qui la rappelle (la chouette chasse de nuit, le goéland pêche sur la plage, le Dédale passe par le labyrinthe des chambres d'épreuves…). Un métal pilier par palier : **fer** (rare), **or** (épique), **diamant** (légendaire), **netherite** (mythique). Plumes, cuir, bâtons, ficelle, verre et teintures reviennent dans beaucoup de recettes.

**Équilibre vanilla / mod** (22 octobre) : dans chaque route, à peu près autant de **types** de matières premières du mod que de types vanilla. Les quantités restent plus généreuses côté vanilla (par exemple 6 cuirs pour 2 Supple Hide dans le harnais). Une famille de drops communs rend l'item imprégné utile partout : minerais bruts en grotte (Sky Iron Fleck, Copper Spark, Gilded Vein, Redstone Pulse, Lapis Glint, Emerald Sliver, Soot Flake, Prism Shard, Pale Quartz, Ancient Ember), mobs courants (Supple Hide, Silk Thread, Hollow Bone, Elastic Gel, Pristine Feather, Fleece Tuft, Abyssal Ink, Ghast Wisp), blocs (Void Grit, Glimmer Spore) et pêche (Glimmering Scale).

**3. Information** : une ressource peut venir de n'importe où (bloc cassé, mob tué, craft, pêche), **sauf des coffres de loot et de l'élevage**. Les drops du mod exigent que l'outil, l'arme ou la canne à pêche porte l'énergie ailée.

## Niveaux de craft

- **Niveau 0** (matière première) : tout ce qui est vanilla, y compris ce qui se fabrique à l'établi ou au four (lingots, bâtons, teintures, lanterne, balise…), et les drops du mod (registre ci-dessous).
- **Niveau n** : un objet du mod fabriqué dans l'onglet `GRID` a pour niveau 1 + le niveau le plus haut de ses ingrédients.
- La profondeur d'une paire = le niveau le plus haut parmi ses 4 pièces. L'assemblage (`ASM`) ne compte pas.
- Les harnais forment une **lignée** : chaque palier reprend le harnais du palier d'en dessous (Iron-Buckled → Gilded → Radiant → Fury Saddle). Le harnais est le même pour toutes les paires d'un palier ; le cœur et les ailes sont propres à chaque paire.
- Les ailes gauche et droite sont le même objet, crafté deux fois. Seule exception : Yin et Yang (`yin_wing` à gauche, `yang_wing` à droite).
- Les niveaux, les quantités et les coûts des fichiers `crafting/*.md` sont calculés et vérifiés par un générateur : formes de 21 cases, niveaux sous le plafond de chaque rareté, quantités totales.

## Lire une route

| Colonne | Contenu |
| --- | --- |
| # | Ordre conseillé (par niveau). Les étapes de même niveau se font dans n'importe quel ordre. |
| Produit | Nom anglais affiché en jeu, id et quantité totale produite |
| Où | Voir la légende ci-dessous |
| Niv. | Niveau de craft du produit |
| Crafts | Nombre de fois que la recette est lancée pour une paire |
| Entrées par craft | Ce qu'un craft consomme. Un produit d'une étape précédente est noté `→ #n` |
| Essence | Essences consommées (crafts × coût), par type : nuage, vapeur, éthérée, intensifiée |
| Cumul | Essence consommée depuis le début de la route |

**Légende de « Où »**

| Code | Signification |
| --- | --- |
| `ASM` | Table d'assemblage (Sanctuaire céleste, 2e dragon) : harnais + cœur + aile gauche + aile droite → paire |
| `GRID` | Établi de la forge (Forge rustique, 3e dragon), onglet « Components » : grille 5×5 sans les coins (21 cases) |
| `MOB` | Drop d'un mob, coup fatal porté avec un item imprégné d'énergie ailée |
| `BLOCK` | Drop d'un bloc, cassé avec un item imprégné |
| `FISH` | Prise supplémentaire à la pêche, avec une canne imprégnée |
| `VANILLA` | Ressource vanilla obtenue normalement (minage, mob, établi, four, pêche, commerce, brosse) |
| `CLOUD` | Récolte d'essence en vol avec un Cloud Absorber : nuage (Overworld), vapeur (Nether), éthérée (End) |

**Grille `GRID`** (`.` = coin absent, `_` = case vide) :

```
. # # # .
# # # # #
# # # # #
# # # # #
. # # # .
```

## Budget d'essences

Quatre essences, quatre jauges de 1 000 par établi. Le générateur répartit le coût de chaque recette selon deux règles (`tools/crafting_gen/essence.py`) :

- **Palier** d'un objet = la rareté la plus basse des paires qui l'utilisent (un objet partagé reste au prix du palier le plus bas). Rare → nuage ; épique → + vapeur ; légendaire → + éthérée ; mythique et réservoirs → + intensifiée.
- **Complexité** = nombre d'essences demandées, d'après le coût total : jusqu'à 20 → 1, jusqu'à 50 → 2, jusqu'à 90 → 3, au-delà → 4 (sans dépasser le palier). Ce sont les essences les plus récentes du palier qui sont demandées : un petit craft mythique ne prend que de l'intensifiée, un gros prend les quatre.

| Rareté | Assemblage (`ASM`) | Pièces (`GRID`, coût total par craft) |
| --- | --- | --- |
| Commune | 0 | — |
| Rare | 100 nuage | harnais 10, cœur 30, aile 15 |
| Épique | 150 nuage + 150 vapeur | intermédiaire 15 à 20, harnais 25, cœur 60, aile 40 |
| Légendaire | 250 nuage + 250 vapeur + 300 éthérée | intermédiaire 10 à 40, harnais 50, cœur 100, aile 80 |
| Mythique | 250 de chaque | intermédiaire 30 à 90, selle 80, cœur 100, aile 100 |

- 1 flacon (`simplywings:cloud_flask`) = 100 unités d'une essence. Les réservoirs (`iron`, `gilded`, `radiant`, `fury_essence_tank`) contiennent 5 000, 20 000, 50 000 et 100 000 unités.
- Réservoirs : harnais du palier + réservoir précédent, 100 / 200 / 400 / 800 unités réparties sur les quatre essences.

## Drops du mod

Le registre `simplywings:loot_source` (synchronisé au client pour JEI). Chaque entrée : la source (mob, bloc ou pêche), des **conditions vanilla facultatives** (biome, heure, météo, hauteur, structure, état du bloc, tueur accroupi ou en sprint, lumière) et une chance. Ces conditions sont des `LootItemCondition` vanilla (`location_check`, `time_check`, `weather_check`, `entity_properties`, `block_state_property`), lues par le Global Loot Modifier : aucun code spécial par paire. Les chances sont des valeurs de départ, à régler en jeu.

Proposition pour le point ouvert « nom des essences de blocs » : il n'y a plus de catégorie générique. Chaque drop porte un nom concret (Acorn, Resonant Shard, Living Rock…), et le mot « Essence » reste réservé aux quatre essences du ciel.

