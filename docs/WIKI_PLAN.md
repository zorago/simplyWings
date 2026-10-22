# Plan du wiki : quatrième onglet de la table d'assemblage

Proposition à valider par zorago avant toute implémentation. Rien n'est codé.

## Objectif

Un guide intégré au jeu, dans la table d'assemblage, qui accompagne la progression sans la gâcher. JEI donne déjà toutes les recettes et toutes les sources de drops à qui les cherche. Le wiki raconte et oriente : il dit quoi faire ensuite et révèle le contenu au fil de la partie.

## Principe anti-spoil

1. **Trois états par page** :
   - **cachée** : la page n'apparaît pas du tout ;
   - **teaser** : titre en « ??? » et une seule ligne d'indice (l'énigme de l'item, déjà écrite dans `en_us.json`) ;
   - **révélée** : la page complète.
2. **Ce qui révèle une page**, toujours un geste du joueur, jamais un simple passage de temps :
   - avoir eu un item dans son inventaire, au moins une fois (drop, pièce, paire) ;
   - avoir débloqué une paire (collection) ;
   - avoir atteint un palier (première rare, première épique…) ;
   - avoir vu une structure (la forge, ou recevoir le compas).
3. **Paliers** : une page d'un palier n'existe même pas en teaser tant que le palier précédent n'est pas atteint. Les mythiques restent entièrement cachées (« ??? » comme dans le menu Ailes) jusqu'à la première légendaire.
4. **Le teaser donne l'énigme, jamais la réponse** : l'énigme dit déjà où chercher (« Under the moon, crouched and silent… »). La chance exacte et la quantité restent dans JEI, pour ceux qui veulent les chiffres.

## Structure

### Chapitre 1 · Premiers battements (toujours visible)
- **Les ailes et la collection** : débloquer, équiper, stats équipées et bonus globaux.
- **Voler** : stabilisé, déplacement, envol chargé, dive, frein, stamina, sauts de secours. Les touches s'affichent avec les vraies assignations du joueur.
- **Les paires communes** : comment assembler la première.

### Chapitre 2 · La forge (révélé à la réception du compas ou à l'ouverture de la table)
- **La Forge des ailes** : deuxième dragon, réparation, compas, tonneaux.
- **La table d'assemblage** : les quatre onglets, le réservoir.
- **L'énergie ailée** : imprégner un outil, une arme, une canne ; un arc compte s'il est tenu en main.

### Chapitre 3 · La Cloud Essence (révélé à la fabrication du premier flacon ou de l'absorbeur)
- Flacon, absorbeur, couche de nuages, remplissage du réservoir, coûts par palier.

### Chapitre 4 · Les drops imprégnés (révélé dès qu'un item imprégné existe)
- Une page par **famille d'activité**, pour guider sans lister : en grotte (minerais), à la chasse (mobs), aux champs et en forêt (blocs de surface), à la pêche, dans le Nether, dans l'End.
- Dans chaque famille, une ligne par drop : **teaser** (icône grisée et énigme) tant que le drop n'a jamais été obtenu, **révélé** (icône, nom, description, lien JEI) ensuite.
- Un compteur par famille : « 7 / 12 trouvés ».

### Chapitre 5 · Les paliers d'ailes
- **Rares** (visibles dès la paire commune) : une page par paire, en teaser au départ (silhouette, énigme de la paire, palier, coût total). Révélée quand le joueur a tenu une de ses pièces : description, ressenti de vol, matières premières clés.
- **Épiques** (teaser après la première rare), **légendaires** (après la première épique), **mythiques** (cachées, puis « ??? » après la première légendaire, puis révélées à la première pièce mythique tenue).
- Une fois la paire **débloquée** : stats, bonus global, et l'arbre de craft complet (la planche de `craft-img/` redessinée en interface, ou un bouton qui ouvre JEI sur la paire).

### Chapitre 6 · Secrets (caché jusqu'à la découverte)
- Night : stamina infinie, cri, dash. Light : transmog. Pages révélées seulement quand le joueur possède la paire.

## Interface

- Quatrième onglet de la table, après Assembly, Components et Winged Energy, avec l'icône d'un livre ailé.
- **Colonne de gauche** : chapitres et pages, avec des pastilles « nouveau » sur les pages qui viennent d'être révélées.
- **Zone de droite** : page avec texte, icônes d'items cliquables (si JEI est présent, clic = recettes ; sans JEI, on affiche l'énigme), barre de défilement.
- **Le même écran est accessible depuis le menu Ailes (J)**, en lecture seule. On peut ainsi consulter le wiki loin de la forge, puisque la table est dans l'End.

## Données et technique

- **Contenu data-driven** : `assets/simplywings/wiki/<chapitre>/<page>.json` (titre, blocs de texte, items, conditions de révélation), textes dans `en_us.json`. On ajoute une paire = on ajoute une page, sans recompiler.
- **Suivi des découvertes côté serveur** : un ensemble d'ids d'items « déjà vus » dans l'attachment joueur (`WingsData`). Il est rempli quand un item entre dans l'inventaire (`ItemEntityPickupEvent`, résultat de craft, scan léger de l'inventaire à l'ouverture de la table). Il est envoyé au client à la connexion, puis par ajout (un paquet par nouvel item, jamais par tick). Il est conservé à la mort.
- **Génération** : les pages des paires et des drops sont générées par `tools/crafting_gen` à partir de `data.py`, `pairs.py` et `lore.py`, comme les routes. Seuls les chapitres 1 à 3 et 6 sont écrits à la main.

## Questions à trancher

1. **Accès hors forge** : le wiki aussi dans le menu Ailes (J), ou seulement à la table ?
2. **Niveau de révélation des routes** : dès qu'une pièce de la paire a été tenue (proposé), ou seulement quand la paire est débloquée ?
3. **Chiffres** : chances et quantités seulement dans JEI (proposé), ou aussi dans les pages révélées ?
4. **Langue** : le wiki suit la règle du mod (anglais seulement) ; on confirme ?
5. **Arbre de craft dans le wiki** : un arbre dessiné en interface (plus joli, plus long à faire) ou un renvoi vers JEI (rapide) ?
6. **Multijoueur** : découvertes par joueur (proposé), ou partagées par équipe ?
