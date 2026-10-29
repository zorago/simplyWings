# Gacha des ailes : invocations, autel draconique et fontaine aux offrandes

Dernier morceau de gameplay du mod, imaginé par zorago le 25 octobre 2026. Ce document fait foi pour la conception ; les points marqués **à valider** se posent à zorago avant l'étape qui en dépend.

**État (25 octobre 2026, soir)** : tout est implémenté, avec des animations provisoires ; rien n'est encore testé en jeu (voir `ToDoTesting.md` § 14). Les points « à valider » ont reçu une valeur par défaut, notée **[défaut]**, à confirmer par zorago.

**Animations (26 octobre 2026)** : un prototype de motion design existe, hors jeu, dans `GATCHA_ANIMATION_SOURCE/` (7 films MP4 et le code qui les produit). Le single B est validé par zorago, caméra comprise ; les singles A à SSS et deux ×10 restent à valider. Il n'y a pas encore les mythiques ni le son. Le détail est dans `GACHA_ANIMATIONS.md` § 0, et l'écran du jeu tourne toujours avec les animations provisoires.

**Correction de zorago (25 octobre, nuit)** : un seul exemplaire par tirage B et A (plus de piles), économie **exponentielle** à la fontaine (B 100 pour 1, A 10 pour 1, S 1 pour 1, SS 1 pour 10, SSS 1 pour 100), coffres à 10 % / 20 % / 30 % selon la dimension avec toujours 1 à 3 invocations, autel et fontaine **cassables et récupérables**, blocs du mod régénérés à chaque dragon et structures entièrement reconstruites à partir du sixième. Le présent document est à jour de cette correction ; les animations finales sont spécifiées dans `GACHA_ANIMATIONS.md`.

## Pourquoi un gacha, et pourquoi il ne casse pas le jeu

Le gacha est une **voie bonus** pour obtenir des ailes et des ressources, à côté des routes de craft (qui restent la voie principale et sûre). Trois garde-fous :

- **Pas d'achat, pas de farm infini** : une invocation ne s'obtient que par l'exploration (coffres du monde) ou le recyclage d'items du mod (fontaine aux offrandes), avec un rendement maîtrisé (voir § Économie).
- **Garanties transparentes** : chaque palier a une garantie dure, une garantie douce visible, et un compteur de pitié affiché dans l'interface. **Pas de 50/50** : ce qui tombe est tiré dans le palier entier, il n'y a pas de bannière « vedette ».
- **Un but dès le début de partie** : l'item d'invocation est le seul du mod qui apparaît **sans la condition « imprégné »** (énergie ailée). Le joueur en trouve dans les coffres avant même de connaître le mod : c'est un indice de son existence et une promesse (« il y a quelque chose à faire avec ça »).

## L'item : l'invocation

- **Summoning Feather** (`simplywings:summoning_feather`), « plume d'invocation » **[défaut]**.
- Non craftable, empilable par 64, rareté épique (nom en couleur).
- Tooltip : description romancée + énigme d'obtention, comme tous les items du mod.

### Où on la trouve

1. **Coffres de loot du monde** : un Global Loot Modifier ajoute des invocations à **toutes les loot tables de coffres vanilla** (`minecraft:chests/*`), au rang des trésors. Compatible Lootr (Lootr régénère chaque coffre par joueur à partir des mêmes loot tables, donc le modificateur s'applique à chaque joueur). Les tables de coffres des autres mods (`*:chests/*`) sont incluses par défaut, désactivables en config.
   - Chance par coffre selon la dimension où le coffre est ouvert : **10 %** dans l'Overworld (et toute autre dimension), **20 %** dans le Nether (et les tables `bastion` et `nether_bridge`), **30 %** dans l'End (et les tables `end_city`). **+5 % par niveau de chance** (attribut `luck`).
   - Contenu : **toujours 1 à 3 invocations, jamais plus**, quelle que soit la chance ou la dimension. Soit en moyenne 0,20 invocation par coffre dans l'Overworld, 0,40 dans le Nether, 0,60 dans l'End.
2. **Fontaine aux offrandes** (cinquième structure, voir plus bas) : elle transforme les items du mod en invocations.

## Les paliers de loot

Cinq paliers, du plus commun au plus rare. Chaque tirage donne exactement un palier, puis un objet tiré au hasard dans ce palier.

| Palier | Contenu | Taux de base |
| --- | --- | --- |
| **B** | **Une** ressource primaire du mod (un seul exemplaire) : un drop non craftable des routes (56 objets, tous équiprobables). | le reste (≈ 86 %) |
| **A** | **Un** objet craftable du mod : intermédiaires, harnais, cœurs, pièces d'aile (132 objets, tous équiprobables, hors paires d'ailes). | 12 % |
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

Les garanties dures ne sont presque jamais atteintes : la garantie douce les fait tomber avant. Le simulateur est dans le dépôt (`python -I tools/gacha/simulate.py [tirages]`) et doit être relancé à chaque changement de taux.

## Économie : la fontaine aux offrandes

La fontaine rachète **ce que donne l'autel**, avec une **économie exponentielle** : chaque palier vaut dix fois le précédent.

| Ce qu'on offre | Invocations rendues | Valeur d'un exemplaire |
| --- | --- | --- |
| 100 objets **B** (ressources primaires) | 1 | 0,01 |
| 10 objets **A** (objets craftables) | 1 | 0,1 |
| 1 paire **S** (rare) | 1 | 1 |
| 1 paire **SS** (épique) | 10 | 10 |
| 1 paire **SSS** (légendaire) | 100 | 100 |
| 1 paire **mythique** | 100 **[défaut]** (même valeur que SSS, puisque c'est un SSS transformé) | 100 |

La partie décimale est gardée en réserve dans la fontaine pour le joueur (rien ne se perd). Les autres items du mod (essences, conteneurs, compas, blocs) n'ont pas de valeur et restent dans la fontaine, rendus à la fermeture.

Les valeurs ne sont pas tapées à la main : `python -I tools/crafting_gen/values.py` lit les listes `DROPS`, `INTERMEDIATES` et `PARTS` de `item/MaterialIds` et la rareté de chaque paire, et écrit `data/simplywings/simplywings_offering/values.json` (rechargé par `/reload`, réglable par les packs).

**Rendement d'un tirage** (simulateur, garanties comprises) : si l'on revendait tout ce qu'on tire, un tirage rapporterait en moyenne **0,19 invocation**, dont seulement 0,02 pour B et A ; le reste vient des paires (la part des SSS domine : 0,14). En pratique, la première copie d'une paire sert à la débloquer : seuls les doublons se revendent, donc le rendement réel est plus bas, et il augmente quand la collection se remplit (c'est voulu : les doublons de fin de partie financent les dernières paires). On ne peut pas boucler : chaque tour perd au moins 80 %.

## Les deux nouvelles structures

Le mod a aujourd'hui trois sanctuaires de l'End (2e, 3e et 4e morts du dragon). Le gacha en ajoute deux, pour **cinq structures** au total.

### 1. Le sanctuaire d'invocation (île au sommet du ciel)

- Apparaît après la **première** mort du dragon. **Changement du 26 octobre (zorago)** : ce n'est plus une salle sous l'île principale, c'est une **île flottante** posée au-dessus du centre de l'End (x = 0, z = 0), le **sommet du template collé à la hauteur maximale** du monde (y = 319 dans l'End vanilla), même gabarit que les autres îles (57 × 52 × 57). Son **dessous est entièrement en béton noir**, pour être invisible contre le ciel de l'End vu d'en bas ; le dessus est un sol d'End comme les autres îles (pierre de l'End, briques, plaques d'obsidienne), et l'autel est au centre, 2 blocs au-dessus du sol sur une petite estrade (5 × 5 puis 3 × 3). Version générée de base, à refaire à la main par zorago (autel central et 6 piliers de flammes). Les mondes où elle a déjà été posée sous l'île gardent leur position.
- Son **compas** n'est donné qu'à la **cinquième** mort du dragon, en même temps que l'apparition de la fontaine.
- Au centre : l'**autel draconique** (« Draconic Altar »), un bloc unique dans la vibe ange, démon et dragon : socle en os de dragon et obsidienne, ailes de plumes blanches d'un côté et de membrane rouge de l'autre, cristaux de l'End en suspension, flammes violettes. Clic droit : ouvre l'interface d'invocation. Il n'est pas craftable, mais il est **cassable à la pioche et récupérable** (il se pose ailleurs, par exemple dans la base du joueur). La mort suivante du dragon en fait repousser un dans le sanctuaire (voir § Régénération).
- Placement : comme les autres sanctuaires (template généré par script), jamais via un hook interne d'`EndDragonFight`.

### 2. La fontaine aux offrandes (quatrième île)

- Apparaît à la **cinquième** mort du dragon : une nouvelle île flottante qui rejoint les trois autres.
- Les îles passent d'un **triangle** (120°) à un **carré** : quatre îles aux quatre coins, à environ 512 blocs du centre, à 90° les unes des autres.
  - Monde neuf : les positions sont calculées en carré dès la première île.
  - Monde existant (trois îles déjà posées en triangle) : on ne déplace rien ; la fontaine se pose dans le plus grand écart entre deux îles **[défaut]**.
- Au centre : la **fontaine aux offrandes**, un bassin d'essence où l'on jette (ou dépose dans une interface) des items du mod ; elle affiche leur valeur et rend les invocations.
- Compas de la fontaine donné à la cinquième mort, avec celui du sanctuaire d'invocation.

### Régénération des cinq structures

- **Tous les blocs du mod** (autel, fontaine, établi de la forge, table d'assemblage, compresseur) sont cassables à la pioche et se récupèrent.
- **À chaque nouvelle mort du dragon**, chaque structure déjà apparue remet en place **les blocs du mod qui manquent** à leur emplacement ; le reste de la structure n'est pas touché.
- **À partir de la sixième mort** (config `gacha.rebuildDragon`), chaque mort **reconstruit entièrement** les cinq structures : chaque bloc du template est reposé là où le monde diffère, **sans les blocs d'air** (les templates n'en contiennent pas : ce que les joueurs ont construit dans les vides reste). Interprétation **[défaut]** : la reconstruction se fait à chaque mort à partir de la sixième, pas une seule fois.
- La toute première pose d'une structure est toujours complète.

Les cinq structures seront ensuite **polies à la main par zorago** (générées par script d'abord, comme les trois sanctuaires actuels).

## L'interface d'invocation (style Wuthering Waves)

- Plein écran : une **illustration de couverture** du mod (générée par IA, retravaillée) avec **Night et Light Fury en ombres noires discrètes dans le ciel**.
- Boutons **Invoquer ×1** et **Invoquer ×10**, avec le nombre d'invocations possédées.
- **Tracker de pitié** toujours visible : trois jauges S (sur 100), SS (sur 500), SSS (sur 1000), avec la zone de garantie douce marquée et le pourcentage actuel de chaque palier.
- Onglets : historique (les 100 derniers tirages), taux et règles (ce document, en clair).
- Texte en anglais, comme tout le mod.

### Animation d'invocation

Direction retenue depuis le prototype du 26 octobre (`GACHA_ANIMATIONS.md` § 0) :

1. **Lancement** :
   - la plume se consume sur l'autel et 24 runes s'allument une à une ;
   - un faisceau monte, et une étoile naît dans le ciel ;
   - elle **retombe en spirale dans la vasque**, en allumant les 4 flammes des piliers (bleu, rouge, blanc, vert).
2. **Escalade** :
   - pendant la chute, la couleur de l'étoile monte palier par palier jusqu'au palier tiré : blanc (B), bleu (A), violet (S), or (SS), rouge sang irisé (SSS) ;
   - en SSS, le ciel se fissure ;
   - la couleur ne descend jamais.
   - Mythiques, encore à créer : l'escalade SSS, puis noir et blanc entrelacés (Night et Light).
3. **Éclat**, puis **révélation** :
   - en ×1, une carte qui jaillit en tournoyant ;
   - en ×10, les 10 étoiles apparaissent une à une dans le ciel, en tournant en anneau, et chacune a son arrivée propre à son palier. Elles tombent ensemble dans la vasque, puis 10 cartes face cachée se retournent dans l'ordre du tirage. Deux SSS dans le même ×10 donnent bien deux étoiles et deux cartes rouges.
   - Bouton « passer ».
3. **Chaque palier** (B, A, S, SS, SSS, mythique Night, mythique Light) a sa propre animation 3D + 2D : cahier des charges complet dans **`GACHA_ANIMATIONS.md`**.

Direction artistique des animations : motion design **low poly**, ombres et lumières travaillées, **sound design complet** (sfx par étape) et musique. Les originaux sont montés et exportés en **MP4** (vitrine et bande-annonce du mod).

Contrainte technique : Minecraft ne lit pas les vidéos et le mod n'a **aucune dépendance**. En jeu, chaque animation est donc rejouée à partir de la même source : soit en planches d'images (textures animées, comme la nappe d'eau), soit reconstruite en temps réel dans l'interface (calques, particules d'interface), avec le son en `.ogg` découpé par étape. Le choix se fait animation par animation, **à valider** au moment de la création.

Depuis le prototype : le ×10 doit être rendu **en direct**, puisqu'il existe environ 9,8 millions de combinaisons de paliers. Le code du prototype est fait pour ça : chaque image est une fonction pure du temps. Le portage visé est un lecteur Java qui recalcule l'état à `T = (ticks + partialTick) / 20`, avec un autel refait en blocs dans Blockbench (assets vanilla, ou build custom de zorago) et des flammes voxelisées. Les singles suivront probablement la même voie, choix **à valider**.

## Implémentation (code)

- `gacha/` : `Gacha` (tirage côté serveur, garanties, pools), `GachaData` (compteurs, réserve, 100 derniers tirages ; attachment `simplywings:gacha`, conservé à la mort), `GachaTier`, `OfferingValues` (valeurs rechargeables), `SummoningLootModifier` (GLM `simplywings:summoning_feathers`), `DraconicAltarBlock` + `AltarMenu`, `OfferingFountainBlock` + `FountainMenu` (9 cases, bouton « Offer », réserve des fractions).
- Réseau : `GachaPull` (client → serveur, ×1 ou ×10, refusé si la fenêtre de l'autel n'est pas ouverte), `GachaState` et `GachaResults` (serveur → client, à l'ouverture et après un tirage, jamais par tick).
- Client : `ClientGacha`, `AltarScreen` (illustration, tracker de pitié, historique, taux ; animation provisoire : comète colorée par le meilleur palier, d'abord bleue puis qui bascule pour S et plus, flash, cartes révélées une à une avec des rayons pour S et plus, récapitulatif ×10, sons vanilla provisoires), `FountainScreen`.
- Structures : `EndSanctums` (îles en carré, sanctuaire d'invocation au-dessus de l'île centrale, sommet du template collé à la hauteur maximale du monde, compas au 5e dragon), templates générés par `tools/crafting_gen/sanctums.py` (`summoning_sanctum`, `offering_fountain`). Modèles et textures de l'autel, de la fontaine, de la plume et de l'illustration : `tools/crafting_gen/gacha_blocks.py` (provisoires, à refaire avec zorago).
- Config serveur `[gacha]` : taux, garanties, coffres (chances par dimension, 1 à 3), hauteur du sanctuaire, dragons de la fontaine, du compas et de la reconstruction.
- Commandes : `/simplywings gacha feathers <n>`, `gacha pity <s> <ss> <sss>`, `gacha reset`, `gacha info`, et `/simplywings sanctum summoning_sanctum|offering_fountain` (pose et compas).

## Ordre d'implémentation proposé (fait, sauf l'étape 7 en provisoire et l'étape 8 reportée)

1. Item d'invocation + Global Loot Modifier des coffres (hors End et End) + config.
2. Données : compteurs de pitié par joueur (attachment), tirage côté serveur, paquets (résultats et compteurs, jamais par tick).
3. Valeurs des items (`values.py`) + simulateur de taux (`tools/gacha/simulate.py`).
4. Sanctuaire d'invocation + autel (bloc, interface minimale fonctionnelle).
5. Fontaine aux offrandes (île, bloc, interface), passage des îles au carré.
6. Interface finale : illustration, tracker, historique.
7. Animations (lancement, révélations, S, SS, SSS, mythique) et sound design, créées avec zorago. **26 octobre** : prototype hors jeu livré, single B validé (`GACHA_ANIMATIONS.md` § 0). Reste : valider les autres films, créer les mythiques et le son, puis porter en Java.
8. Wiki (quatrième onglet de la table) et JEI : pages du gacha, de l'autel et de la fontaine, taux affichés.
