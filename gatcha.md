# Gacha des ailes : invocations, autel draconique et fontaine aux offrandes

Dernier morceau de gameplay du mod, imaginé par zorago le 25 octobre 2026. Ce document fait foi pour la conception ; les points marqués **à valider** se posent à zorago avant l'étape qui en dépend.

## Pourquoi un gacha, et pourquoi il ne casse pas le jeu

Le gacha est une **voie bonus** pour obtenir des ailes et des ressources, à côté des routes de craft (qui restent la voie principale et sûre). Trois garde-fous :

- **Pas d'achat, pas de farm infini** : une invocation ne s'obtient que par l'exploration (coffres du monde) ou le recyclage d'items du mod (fontaine aux offrandes), avec un rendement volontairement faible (voir § Économie).
- **Garanties transparentes** : chaque palier a une garantie dure, une garantie douce visible, et un compteur de pitié affiché dans l'interface. **Pas de 50/50** : ce qui tombe est tiré dans le palier entier, il n'y a pas de bannière « vedette ».
- **Un but dès le début de partie** : l'item d'invocation est le seul du mod qui apparaît **sans la condition « imprégné »** (énergie ailée). Le joueur en trouve dans les coffres avant même de connaître le mod : c'est un indice de son existence et une promesse (« il y a quelque chose à faire avec ça »).

## L'item : l'invocation

- Nom de travail : **Summoning Feather** (`simplywings:summoning_feather`), « plume d'invocation ». Nom définitif **à valider**.
- Non craftable, empilable par 64, rareté épique (nom en couleur).
- Tooltip : description romancée + énigme d'obtention, comme tous les items du mod.

### Où on la trouve

1. **Coffres de loot du monde** : un Global Loot Modifier ajoute des invocations à **toutes les loot tables de coffres vanilla** (`minecraft:chests/*`), au rang des trésors. Compatible Lootr (Lootr régénère chaque coffre par joueur à partir des mêmes loot tables, donc le modificateur s'applique à chaque joueur). Les tables de coffres des autres mods (`*:chests/*`) sont incluses par défaut, désactivables en config.
   - Hors de l'End : chance par coffre **4 %**, **+2 % par niveau de chance** (attribut `luck`), 1 à 2 invocations. Soit environ **0,06 invocation par coffre** en moyenne.
   - **Dans l'End** (tables `end_city_treasure` et toutes les tables de coffres générées dans l'End, y compris celles des mods de structures de l'End) : **5 invocations en moyenne par coffre** (3 à 7, +1 par niveau de chance). Une petite cité de l'End rapporte ainsi une dizaine d'invocations : ça redonne envie d'explorer l'End vanilla, souvent délaissé une fois les élytres trouvées.
2. **Fontaine aux offrandes** (cinquième structure, voir plus bas) : elle transforme les items du mod en invocations.

## Les paliers de loot

Cinq paliers, du plus commun au plus rare. Chaque tirage donne exactement un palier, puis un objet tiré au hasard dans ce palier.

| Palier | Contenu | Taux de base |
| --- | --- | --- |
| **B** | Une pile de **ressources primaires** du mod : tout ce qui n'est pas craftable (drops imprégnés, essences en flacon, matières de base des routes). La taille de la pile dépend de la valeur de la ressource (beaucoup de communes, peu de rares). | le reste (≈ 86 %) |
| **A** | Un **objet craftable** du mod : intermédiaires, harnais, cœurs, pièces d'aile (tout ce qui sort d'une table ou d'un établi, hors paires d'ailes). | 12 % |
| **S** | Une **paire d'ailes rare** (21 paires). | 0,6 % |
| **SS** | Une **paire d'ailes épique** (11 paires). | 0,1 % |
| **SSS** | Une **paire d'ailes légendaire** (6 paires). Puis **1 chance sur 100** que ce SSS devienne une **paire mythique** (Night ou Light, au hasard parmi les deux, de préférence celle que le joueur n'a pas encore), avec une animation spéciale. | 0,03 % |

La paire `common` n'est pas dans le gacha (elle est donnée au départ).

### Garanties (pitié)

Trois compteurs par joueur, un par palier garanti, sauvegardés avec la collection (Data Attachment conservé à la mort) :

| Palier | Garantie douce | Garantie dure | Hausse par tirage pendant la garantie douce |
| --- | --- | --- | --- |
| S | à partir du 81e tirage | 100e tirage | +5 % par tirage (100 % au 100e) |
| SS | à partir du 401e tirage | 500e tirage | +1 % par tirage (100 % au 500e) |
| SSS | à partir du 801e tirage | 1000e tirage | +0,5 % par tirage (100 % au 1000e) |

Règles :

- Taux du tirage n° *n* d'un palier : `taux de base + max(0, n − début de la garantie douce) × hausse`, plafonné à 100 %.
- On tire du plus rare au moins rare : SSS d'abord, puis SS, puis S, puis A, puis B.
- Un compteur **repart à zéro** dès qu'un objet de **son palier ou d'un palier supérieur** tombe (un SS compte comme « au moins un S » : il remet aussi le compteur S à zéro). C'est le fonctionnement habituel des gachas.
- Si deux garanties dures tombent sur le même tirage, le palier le plus haut gagne ; le compteur de l'autre continue.
- Le multi ×10 n'a pas de garantie propre : ce sont simplement 10 tirages à la suite, compteurs compris.

### Résultats attendus (simulation de 2 millions de tirages)

| Palier | Taux réel (garanties comprises) | Un objet de ce palier ou mieux tous les… | Pire cas observé |
| --- | --- | --- | --- |
| A | 12,0 % | 8 tirages | — |
| S | 1,35 % | 59 tirages | 96 |
| SS | 0,22 % | 277 tirages | 437 |
| SSS | 0,14 % | 730 tirages | 856 |
| Mythique | 0,0014 % | ≈ 73 000 tirages | — |

Les garanties dures ne sont presque jamais atteintes : la garantie douce les fait tomber avant. Le simulateur est dans le dépôt (`tools/gacha/simulate.py`, à créer avec l'implémentation) et doit être relancé à chaque changement de taux.

## Économie : la fontaine aux offrandes

La fontaine recycle **n'importe quel item du mod** en invocations. Chaque item a une **valeur** calculée à partir de sa route de craft :

- valeur d'une ressource primaire = sa difficulté d'obtention (rareté de la source, condition « imprégné », dimension, danger du mob ou du bloc) ;
- valeur d'un objet craftable = somme des valeurs de ses ingrédients (ingrédients vanilla compris, chacun avec sa propre valeur) + le coût en essences de la recette + une petite prime de travail ;
- invocations rendues = valeur ÷ coût d'une invocation, la partie décimale étant gardée en réserve dans la fontaine pour le joueur (rien ne se perd).

Repères voulus par zorago :

- il faut environ **200 ressources primaires de bas niveau** (celles qu'on récupère sur du bois, par exemple) pour **1 invocation** ;
- un **cœur de dernier palier** donne environ **20 invocations**.

**Rendement d'un tirage : environ 10 pour 1.** La valeur moyenne de ce que rapporte un tirage, mesurée avec ces mêmes valeurs de fontaine, vaut environ **10 % du coût d'une invocation**. Conséquences :

- on ne peut pas boucler (tirer, recycler ce qu'on a tiré, retirer) : chaque tour perd environ 90 % ;
- la répartition de ces 10 % entre les paliers fixe la valeur de fontaine des paires d'ailes. Exemple d'équilibre (à recalculer par l'outil) : paire rare ≈ 3 invocations, épique ≈ 8, légendaire ≈ 20, objet A ≈ 0,1, pile B ≈ 0,003 ; un doublon d'aile n'est donc pas perdu, il se recycle.

Les valeurs ne sont pas tapées à la main : un script (`tools/crafting_gen/values.py`, à créer) les calcule depuis `tools/crafting_gen/data.py` et les recettes générées, vérifie les deux repères et le rendement de 10 %, et écrit un JSON de datapack (`data/simplywings/simplywings/offering_value/*.json`), donc réglable par les packs.

## Les deux nouvelles structures

Le mod a aujourd'hui trois sanctuaires de l'End (2e, 3e et 4e morts du dragon). Le gacha en ajoute deux, pour **cinq structures** au total.

### 1. Le sanctuaire d'invocation (sous l'île principale)

- Apparaît après la **première** mort du dragon, **sous l'île principale de l'End**, caché par un **voile de blocs noirs** (blackstone, obsidienne, béton noir) qui le fait passer pour le vide ou la roche. On peut le trouver par hasard en creusant, mais rien ne le signale.
- Son **compas** n'est donné qu'à la **cinquième** mort du dragon, en même temps que l'apparition de la fontaine.
- Au centre : l'**autel draconique** (« Draconic Altar »), un bloc unique dans la vibe ange, démon et dragon : socle en os de dragon et obsidienne, ailes de plumes blanches d'un côté et de membrane rouge de l'autre, cristaux de l'End en suspension, flammes violettes. Clic droit : ouvre l'interface d'invocation. Il n'est pas craftable ; s'il est cassé, il est réparé à la mort suivante du dragon, comme les autres sanctuaires. Un autel en jeu suffit (il n'y a pas d'autel portatif). **À valider** : l'autel est-il cassable par les joueurs ?
- Placement : comme les autres sanctuaires (template généré par script, réparation sans écraser les blocs posés par les joueurs), jamais via un hook interne d'`EndDragonFight`.

### 2. La fontaine aux offrandes (quatrième île)

- Apparaît à la **cinquième** mort du dragon : une nouvelle île flottante qui rejoint les trois autres.
- Les îles passent d'un **triangle** (120°) à un **carré** : quatre îles aux quatre coins, à environ 512 blocs du centre, à 90° les unes des autres.
  - Monde neuf : les positions sont calculées en carré dès la première île.
  - Monde existant (trois îles déjà posées en triangle) : on ne déplace rien ; la fontaine se pose dans le plus grand écart entre deux îles. **À valider** avec zorago.
- Au centre : la **fontaine aux offrandes**, un bassin d'essence où l'on jette (ou dépose dans une interface) des items du mod ; elle affiche leur valeur et rend les invocations.
- Compas de la fontaine donné à la cinquième mort, avec celui du sanctuaire d'invocation.

Les cinq structures seront ensuite **polies à la main par zorago** (générées par script d'abord, comme les trois sanctuaires actuels).

## L'interface d'invocation (style Wuthering Waves)

- Plein écran : une **illustration de couverture** du mod (générée par IA, retravaillée) avec **Night et Light Fury en ombres noires discrètes dans le ciel**.
- Boutons **Invoquer ×1** et **Invoquer ×10**, avec le nombre d'invocations possédées.
- **Tracker de pitié** toujours visible : trois jauges S (sur 100), SS (sur 500), SSS (sur 1000), avec la zone de garantie douce marquée et le pourcentage actuel de chaque palier.
- Onglets : historique (les 100 derniers tirages), taux et règles (ce document, en clair).
- Texte en anglais, comme tout le mod.

### Animation d'invocation

1. **Lancement** : l'autel s'embrase, une plume (ou une comète) traverse un ciel étoilé. Sa couleur annonce le meilleur palier du tirage : B blanc, A bleu, S violet, SS or, SSS rouge sang irisé ; mythique noir et blanc entrelacés (Night et Light).
2. **Révélation** : un objet à la fois en ×1 ; en ×10, révélation une par une puis récapitulatif en grille. Bouton « passer ».
3. **Paliers S, SS et SSS** : chacun a sa propre animation d'apparition, **à créer ensemble** ; le mythique a son animation à lui.

Direction artistique des animations : motion design **low poly**, ombres et lumières travaillées, **sound design complet** (sfx par étape) et musique. Les originaux sont montés et exportés en **MP4** (vitrine et bande-annonce du mod).

Contrainte technique : Minecraft ne lit pas les vidéos et le mod n'a **aucune dépendance**. En jeu, chaque animation est donc rejouée à partir de la même source : soit en planches d'images (textures animées, comme la nappe d'eau), soit reconstruite en temps réel dans l'interface (calques, particules d'interface), avec le son en `.ogg` découpé par étape. Le choix se fait animation par animation, **à valider** au moment de la création.

## Ordre d'implémentation proposé

1. Item d'invocation + Global Loot Modifier des coffres (hors End et End) + config.
2. Données : compteurs de pitié par joueur (attachment), tirage côté serveur, paquets (résultats et compteurs, jamais par tick).
3. Valeurs des items (`values.py`) + simulateur de taux (`tools/gacha/simulate.py`).
4. Sanctuaire d'invocation sous l'île + autel (bloc, interface minimale fonctionnelle).
5. Fontaine aux offrandes (île, bloc, interface), passage des îles au carré.
6. Interface finale : illustration, tracker, historique.
7. Animations (lancement, révélations, S, SS, SSS, mythique) et sound design, créées avec zorago.
8. Wiki (quatrième onglet de la table) et JEI : pages du gacha, de l'autel et de la fontaine, taux affichés.
