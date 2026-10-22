# Routes de craft des ailes

Les règles communes à toutes les routes, les objets partagés, le registre des drops du mod et le récapitulatif des coûts. **Chaque paire a sa route complète dans son propre fichier**, `crafting/<id>.md` (matières premières, étapes, recettes dessinées, justification des ingrédients). Seule la paire commune reste décrite ici. Le cadre général (table d'assemblage, Cloud Essence, énergie ailée) est dans `CAHIER_DES_CHARGES.md`, section « Craft : table d'assemblage ».

Dernière mise à jour : 22 octobre 2026.

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
| Essence | Cloud Essence consommée (crafts × coût) |
| Cumul | Essence consommée depuis le début de la route |

**Légende de « Où »**

| Code | Signification |
| --- | --- |
| `ASM` | Table d'assemblage, onglet 1 « Assembly » : harnais + cœur + aile gauche + aile droite → paire |
| `GRID` | Table d'assemblage, onglet 2 « Components » : grille 5×5 sans les coins (21 cases) |
| `MOB` | Drop d'un mob, coup fatal porté avec un item imprégné d'énergie ailée |
| `BLOCK` | Drop d'un bloc, cassé avec un item imprégné |
| `FISH` | Prise supplémentaire à la pêche, avec une canne imprégnée |
| `VANILLA` | Ressource vanilla obtenue normalement (minage, mob, établi, four, pêche, commerce, brosse) |
| `CLOUD` | Récolte de Cloud Essence en planant dans les nuages |

**Grille `GRID`** (`.` = coin absent, `_` = case vide) :

```
. # # # .
# # # # #
# # # # #
# # # # #
. # # # .
```

## Budget de Cloud Essence

| Rareté | Assemblage (`ASM`) | Flacons | Pièces (`GRID`) | Total d'une paire |
| --- | --- | --- | --- | --- |
| Commune | 0 | 0 | 0 | 0 |
| Rare | 100 | 1 | harnais 10, cœur 30, aile 15 (moins pour les ailes simples) | 130 à 170 |
| Épique | 300 | 3 | intermédiaire 15 à 20, harnais 25, cœur 60, aile 40 | 540 à 615 |
| Légendaire | 800 | 8 | intermédiaire 10 à 40, harnais 50, cœur 100, aile 80 | 1 360 à 1 645 |
| Mythique | 1 000 | 10 (réservoir plein) | intermédiaire 30 à 90, selle 80, cœur 100, aile 100 | 2 690 à 2 735 |

- 1 flacon (`simplywings:cloud_flask`) = 100 unités. La table contient au plus 1 000 unités (10 flacons).
- Un craft `GRID` coûte de 5 à 100 unités. Le total d'une paire = assemblage + tous les crafts de sa route, harnais compris.

## Drops du mod

Le registre `simplywings:loot_source` (synchronisé au client pour JEI). Chaque entrée : la source (mob, bloc ou pêche), des **conditions vanilla facultatives** (biome, heure, météo, hauteur, structure, état du bloc, tueur accroupi ou en sprint, lumière) et une chance. Ces conditions sont des `LootItemCondition` vanilla (`location_check`, `time_check`, `weather_check`, `entity_properties`, `block_state_property`), lues par le Global Loot Modifier : aucun code spécial par paire. Les chances sont des valeurs de départ, à régler en jeu.

Proposition pour le point ouvert « nom des essences de blocs » : il n'y a plus de catégorie générique. Chaque drop porte un nom concret (Acorn, Resonant Shard, Living Rock…), et le mot « Essence » reste réservé à la Cloud Essence.

