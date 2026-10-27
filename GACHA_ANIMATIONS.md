# Animations du gacha : cahier des charges

Cahier des charges des animations d'invocation de l'autel draconique, **une animation 3D + 2D par rareté** : B, A, S, SS, SSS, mythique Night et mythique Light. Il complète `gatcha.md`, qui fait foi pour les règles du gacha, et `SOUND_PLAN.md` (archive : `docs/archive/SOUND_DESIGN.md`), qui fait foi pour les sons.

Ce document sert d'entrée unique au **skill Claude de motion design** qui générera les animations (voir § 9). Tout ce que le skill doit savoir y est écrit : intentions, découpage temporel, contenu 3D, contenu 2D, sons, contraintes techniques, formats de sortie et critères d'acceptation. Les points marqués **à valider** se posent à zorago avant la production de l'animation concernée.

État au 25 octobre 2026 : des animations **provisoires** tournent déjà dans `client/gui/AltarScreen` (comète colorée, fausse piste bleue pour S et plus, flash, cartes, rayons, sons vanilla). Elles servent de référence de rythme, pas de direction artistique.

**État au 26 octobre 2026** : un **prototype de motion design** existe, hors jeu (§ 0). Le single B est validé par zorago, caméra comprise ; les autres films sont livrés et restent à valider. Là où le prototype contredit les § 4 à 6, c'est lui qui donne la direction, sous réserve des points du § 0.4.

## 0. Prototype de motion design (26 octobre 2026)

### 0.1 Sources

- Dossier `GATCHA_ANIMATION_SOURCE/` à la racine du dépôt, produit par zorago dans une conversation claude.ai (motion design), hors de Claude Code :
  - 7 vidéos MP4 1920 × 1080, **30 i/s** (choisi pour être facile à analyser), H.264, **sans son** : `single_b` (14,4 s), `single_a` (15,3 s), `single_s` (16,8 s), `single_ss` (18,3 s), `single_sss` (20,2 s), `multi_x10` (34 s), `multi_x10_double_sss` (36,3 s) ;
  - `gacha-motion-code.zip` : le code complet qui produit ces vidéos (dossier `gacha-motion/`).
- Le zip contient :

| Fichier | Rôle |
| --- | --- |
| `README.md` | Mode d'emploi écrit pour Claude Code : découpage, live ou pré-rendu, guide de portage vers Minecraft |
| `src/core.js` | Maths, easings, PRNG déterministe (mulberry32), courbes de clés Hermite, intégrale de vitesse angulaire, remappage de temps |
| `src/tiers.js` | **Palette et réglages par palier** (couleurs signal de `GachaTier`, chaîne d'escalade, durées, puissance). Le premier fichier à retoucher |
| `src/altar.js` | La scène low poly, pièce par pièce (`PART …`), en blocs (1 unité = 1 bloc) : dimensions prêtes à refaire dans Blockbench |
| `src/timeline.js` | `GM.plan(pulls)` : liste de tirages → ancres de temps, courbes de caméra, secousses, battements, liste d'événements horodatés |
| `src/state.js` | `GM.state(plan, T)` : l'état complet du monde à l'instant T (caméra, lumières, flammes, étoiles, particules, anneaux, rayons, fissures, flash, cartes) |
| `src/renderer.js` | Projection perspective, tri du peintre, ombrage plat (lune + lumières ponctuelles), halos additifs, canvas 1920 × 1080 |
| `src/cards.js` | Cartes 2D et police pixel 5 × 7 maison (majuscules) |
| `src/anims.js` | Les listes de tirages rendues |
| `player.html` | Lecteur web en direct : choix de l'animation, barre de temps, boutons « Random ×1 » et « Random ×10 », paramètres `?anim=`, `?pulls=`, `?t=`, `?hud=1` |
| `tools/render.js` | Rendu MP4 (Playwright + ffmpeg) et écriture des timelines JSON |
| `out/*.timeline.json` | Pour chaque vidéo : ancres, battements, apparitions, retournements, emplacements des cartes et **événements** (ids de son) |

### 0.2 Ce que zorago a validé

- **Single B, en entier** : la chorégraphie et le rythme.
- **Caméra accélérée** du single B, jugée « parfaite » : environ un tour trois quarts autour de l'autel en 14,4 s, sans jamais s'arrêter (la vitesse monte et descend par courbes). Elle démarre loin et haut puis plonge vers l'autel pendant la combustion de la plume ; elle se rapproche en contre-plongée pendant la charge ; elle atteint sa vitesse maximale juste avant l'éclat, avec un recul pour voir les quatre flammes ; elle ralentit d'un coup et pousse vers l'étoile à l'éclat, puis tourne doucement autour de la carte.
- **Pas encore validés** : les singles A, S, SS, SSS et les deux ×10. Sur l'autre PC, seules des images fixes en ont été vérifiées, pas les films en mouvement.

### 0.3 La chorégraphie du prototype

Elle **remplace la comète qui monte dans le ciel** (§ 4 et 6) : ici, une étoile naît au-dessus de l'autel, puis **retombe dans la vasque**.

**Single** (temps du B ; les phases suivantes s'allongent avec le palier) :

| Temps (B) | Phase |
| --- | --- |
| 0 – 2,2 s | **Autel** : la caméra plonge, la plume d'invocation descend et se consume en braises violettes |
| 2,2 – 4,4 s | **Charge** : 24 runes au sol s'allument une à une (le « chargeur »), le cristal accélère, faisceau vers le ciel à 4,05 s |
| 4,4 – 5,9 s | **Étoile** : pure, lente, éthérée ; la caméra la suit |
| 5,9 s – fin de chute | **Chute** : spirale autour de l'autel, 12 petites étoiles nées dans son sillage, les 4 flammes des piliers s'allument dans l'ordre **bleu, rouge, blanc, vert** (choix de zorago), puis éruption de la vasque. **Escalade** : un battement par palier de la chaîne, avec changement de couleur, anneau, mini-flash et secousse |
| fin de chute – éclat | **Temps d'arrêt** : l'étoile se contracte et aspire les petites étoiles |
| éclat | **Éclat** : flash, rayons, éclats colorés, ondes de choc |
| carte – fin | **Carte** : elle jaillit en tournoyant (2 tours, plus ½ tour par palier), rebond de l'objet, reflet, lettres du palier, étoiles une à une (3 pour S, 4 pour SS, 5 pour SSS), nom lettre par lettre |

- **Escalade** : un B reste blanc et n'a aucun battement. Pour les autres paliers, l'étoile passe par la chaîne A → S → SS → SSS jusqu'à son palier (bleu, violet, or, rouge). Au dernier battement d'un SSS, le ciel **se fissure en rouge**. Il n'y a donc jamais de déception mise en scène : la couleur ne fait que monter.
- **Durées par palier** (s ; tirées des timelines) :

| Palier | Battements | Fin de chute | Éclat | Carte | Fin |
| --- | --- | --- | --- | --- | --- |
| B | — | 8,9 | 9,6 | 10,4 | **14,4** |
| A | A 7,51 | 9,4 | 10,2 | 11,0 | **15,3** |
| S | A 7,29 · S 8,28 | 10,0 | 11,0 | 11,8 | **16,8** |
| SS | A 7,24 · S 8,11 · SS 8,97 | 10,7 | 11,9 | 12,7 | **18,3** |
| SSS | A 7,27 · S 8,07 · SS 8,88 · SSS 9,69 | 11,5 | 13,0 | 13,8 | **20,2** |

Réglages dans `tiers.js` : `fallExtra` (chute en plus des 3 s du B), `hold` (temps d'arrêt), `cardDur` (carte), `power` (particules, secousse, anneaux, lumière).

**Multi ×10** (`multi_x10` = `B B B B B B A S SS SSS`, 34 s) :

1. Les 10 plumes descendent en couronne et se consument.
2. La charge, comme pour un single.
3. Les **10 étoiles apparaissent l'une après l'autre**, dans l'ordre du tirage, et virevoltent en anneau dans le ciel. Leur arrivée dure plus longtemps et frappe plus fort selon le palier (`spawn`, de 0,42 s pour un B à 1,95 s pour un SSS) :
   - **B** : l'étoile s'allume sur place ;
   - **A** : elle descend de 14 blocs ;
   - **S** : elle tombe de haut, avec un anneau dans le ciel et un mini-flash ;
   - **SS** : elle tombe du zénith, avec des rayons dorés et une secousse ;
   - **SSS** : elle arrive de l'horizon et fissure le ciel, avec des rayons rouges et une forte secousse.
4. Elles tombent toutes en spirale dans la vasque, à 0,09 s d'écart. Les 4 flammes s'allument, puis le noyau grossit à chaque étoile absorbée.
5. **Éclat** à la couleur du meilleur palier.
6. **10 cartes** distribuées face cachée devant le joueur, en grille 5 × 2. Leur dos est neutre : rien ne trahit le palier avant l'anticipation.
7. Elles se retournent dans l'ordre du tirage :
   - **B et A** : retournement rapide (0,30 s et 0,45 s) ;
   - **S et plus** : anticipation (la carte tremble, se soulève, liseré de couleur), retournement (un tour et demi pour SS et SSS), rayons, gerbe de particules, flash et secousse de plus en plus forts. Un SSS dure 1,9 s, sans compter son anticipation.
8. La ou les meilleures cartes pulsent jusqu'à la fin.

`multi_x10_double_sss` (36,3 s) montre qu'un joueur très chanceux voit **deux étoiles rouges** dans le ciel et **deux cartes rouges** : chaque étoile porte la couleur de son propre tirage.

### 0.4 Écarts avec le cahier des charges (à trancher avec zorago)

1. **Durées.** Le single B validé dure 14,4 s, contre 2,4 s au § 6.1. Or le § 1 veut des communs courts, puisque B et A représentent 98 % des tirages. Proposition à valider : garder le film complet, et ajouter des points de saut (à l'éclat et à la carte) ainsi qu'un mode rapide pour les tirages répétés.
2. **Vraie paire en 3D** (§ 3.1) : pas encore faite. Pour S et plus, la carte montre une icône d'ailes générique.
3. **Absents du prototype** :
   - la silhouette de dragon (SS) et le dragon de l'End (SSS) des § 6.4 et 6.5 ;
   - les mentions « New! » et « Duplicate » (§ 5) ;
   - les **mythiques Night et Light** (§ 6.6 et 6.7) ;
   - l'écran IDLE (§ 7) ;
   - le son.
4. **Flashs** : le prototype plafonne à 0,7, et à 0,85 pour l'éclat SSS. Le § 8.4 autorise un blanc SSS à 100 % pendant 200 ms : le prototype est plus doux, ce qui va dans le sens de l'accessibilité.
5. **Fausse piste** (§ 4.1) : tous les paliers à partir de A partent en blanc puis passent au bleu, et seul un B reste blanc. La variante « S ou SS violet d'emblée, une fois sur trois » n'est pas faite.
6. **Cadence** : les films sont en 30 i/s pour l'analyse. Le master du § 9 était prévu en 60 i/s avec les sons mixés.
7. **Cartes et police** : le prototype dessine ses cartes sur un canvas, avec une police pixel 5 × 7 maison. En jeu, la police de Minecraft s'applique (§ 5).
8. **Placeholders** : les noms d'objets (`END SHARD`, `DRAGON SCALE`, `VIOLET WINGS`, `GOLDEN WINGS`, `CRIMSON WINGS`) et les icônes sont inventés.
9. **Défaut connu** : le prototype trie les faces sans z-buffer, donc quelques faces peuvent se chevaucher quand la caméra frôle les ailes de l'autel. Le rendu en jeu, avec un vrai z-buffer, n'aura pas ce problème.

### 0.5 Live ou pré-rendu

- Dans le prototype, chaque image est une **fonction pure du temps** : `state(plan, T)` ne dépend jamais de l'image précédente.
- Le **×10 sera forcément rendu en direct** : il y a 5¹⁰, soit environ 9,8 millions de combinaisons de paliers, et on ne peut pas pré-rendre une vidéo par cas. Minecraft ne lit de toute façon pas les vidéos : le direct est donc aussi la voie naturelle pour les singles. Le choix final reste à confirmer.
- En jeu : `T = (ticks + partialTick) / 20`, puis l'état à chaque frame. La couleur de chaque étoile, la longueur de son arrivée et de son retournement viennent de son palier, ce qui donne une animation propre à chaque tirage.

### 0.6 Portage prévu vers le jeu

Souhait de zorago : que Claude Code refasse ces animations à partir du code, avec des **assets Minecraft vanilla**, un **build custom de zorago** pour l'autel, et des **flammes voxelisées**.

1. **Modèles** :
   - chaque `PART` de `altar.js` est refait dans Blockbench, en cubes et en plaques (dimensions en blocs) ;
   - les prismes à 5 à 8 côtés deviennent des cubes tournés ;
   - les flammes deviennent des empilements de cubes qui ondulent, ou une planche de sprites ;
   - zorago peut remplacer l'autel par sa propre construction en blocs.
2. **Couleurs** : `tiers.js` (`PAL`, `TIERS`, `FLAMES`). Les couleurs signal sont celles de `GachaTier` (§ 2).
3. **Timeline** : deux voies, **à valider**.
   - Porter `timeline.js` et `state.js` en Java presque tels quels, puisque ce ne sont que des maths. C'est la voie que conseille le README du prototype.
   - Ou convertir le prototype au format de tracks du § 8.3.
   - Dans les deux cas, les fichiers `out/*.timeline.json` servent de référence pour caler les sons et vérifier le portage.
4. **Caméra** :
   - orbite autour de l'axe Y : l'angle `th` est l'intégrale d'une vitesse angulaire `OM`, ce qui évite tout arrêt sec ;
   - rayon `R`, hauteur `H`, cible `TY` et focale `FL` sont des courbes de clés ;
   - la cible se mélange à la position de l'étoile selon `WS` ;
   - les clés du single B validé sont dans `timeline.js` (`CAM_B`). Les autres paliers les étirent avec `U.remap`.
5. **Rendu** :
   - la 3D passe en `ModelPart` dans un `PoseStack`, en pleine lumière, avec la couleur calculée face par face comme dans `shade()` de `renderer.js` (lune fixe et lumières ponctuelles des flammes et de l'étoile) ;
   - halos, rayons, flash et cartes deviennent des blits d'interface, en mélange additif si le § 3.2 l'autorise.
6. **Accessibilité** : pour `reduceFlashes`, mettre le flash et la secousse à 0, comme le prévoit le README du prototype.
7. **Sons** : les ids des événements du prototype sont listés dans `docs/archive/SOUND_DESIGN.md` § 6.4. Ils complètent ou remplacent les ids du § 6 de ce document.
8. **Reste à créer, comme le prévoit ce cahier** : la vraie paire en 3D, « New! » et « Duplicate », les deux mythiques et l'écran IDLE.

## 1. Intentions

- **Lire le palier sans lire de texte.** Couleur, durée, ampleur et son annoncent le palier. Un joueur qui regarde l'écran du coin de l'œil sait ce qu'il a tiré.
- **Escalade, jamais de déception mise en scène.** Une animation peut monter (fausse piste bleue qui vire au violet, puis à l'or…), mais elle ne descend jamais : on ne montre pas d'or pour finir en B.
- **Les communs sont courts.** B et A sont la grande majorité des tirages (98 %) : leur animation est brève, satisfaisante, et ne fatigue pas après 500 tirages.
- **Les rares sont des événements.** S, SS, SSS et les mythiques sont rares (un S tous les 58 tirages, un SSS tous les 730, un mythique tous les 74 000). Leur animation est longue, spectaculaire, et montre la paire d'ailes **en 3D, en mouvement**, avec ses vraies animations.
- **Univers du mod.** Ange, démon et dragon : plumes blanches et membranes rouges, os de dragon, ciel de l'End, cristaux, flammes violettes. Les mythiques rendent hommage à la Furie Nocturne et à la Furie Éclair, sans reprendre aucun visuel protégé.
- **Style.** Motion design **low poly** : volumes facettés en aplats, ombres et lumières travaillées, peu de textures, beaucoup de lumière. La 3D doit rester **cohérente avec le voxel de Minecraft** : facettes nettes, pas de rendu réaliste, pas de flou de mouvement photographique. Références de rythme : les invocations de *Wuthering Waves* et de *Genshin Impact* (montée, fausse piste, impact, révélation). Références de forme : le low poly des clips de motion design (prismes, éclats, anneaux, rubans).
- **Toujours passable.** Un clic avance, Échap saute au récapitulatif. Jamais plus de 1 s imposée avant qu'un clic soit pris en compte, sauf à la première ouverture d'un SSS ou d'un mythique (2 s).

## 2. Ce qui existe et ce qu'on garde

L'écran `AltarScreen` enchaîne quatre phases : **IDLE** (illustration, tracker de pitié, boutons ×1 et ×10), **LAUNCH** (lancement), **REVEAL** (cartes une par une) et **SUMMARY** (grille du ×10). Les nouvelles animations remplacent le contenu de LAUNCH et de REVEAL ; IDLE et SUMMARY gardent leur rôle, avec un habillage refait (§ 7).

Couleurs des paliers (déjà dans `gacha/GachaTier`, à conserver comme couleurs signal) :

| Palier | Couleur signal | Palette étendue de l'animation |
| --- | --- | --- |
| B | `#DDE4EA` blanc gris | argent, gris perle, bleu nuit très sombre en fond |
| A | `#5AA9FF` bleu | bleu ciel, cyan pâle, blanc |
| S | `#B46CFF` violet | violet, mauve, rose froid, blanc |
| SS | `#FFC23A` or | or, ambre, blanc chaud, brun profond en ombre |
| SSS | `#FF4A5E` rouge sang | rouge, magenta, reflets irisés (cyan, or) sur les arêtes, noir |
| Mythique Night | `#F4F4F4` (étiquette ✦) | noir d'encre `#0B0A12`, plasma bleu-violet `#6F7BFF` → `#B9A6FF`, blanc électrique |
| Mythique Light | `#F4F4F4` (étiquette ✦) | blanc nacré, iridescence rose `#FFB3E1` et cyan `#9FF4FF`, or très pâle |

## 3. Architecture de rendu en jeu

Contraintes du mod (non négociables, voir `CLAUDE.md`) : **aucune dépendance**, **aucun shader ni RenderType custom**, rendu par `ModelPart` et RenderTypes vanilla, effets en particules ou sprites. Minecraft ne lit pas les vidéos. Chaque animation est donc **rejouée en temps réel** dans l'écran, à partir de données produites par le skill.

Une animation = trois couches synchronisées par une même horloge (millisecondes depuis le début de la phase) :

### 3.1 Couche 3D

- Scène low poly rendue dans l'écran avec un `PoseStack` et une projection perspective dédiée, comme l'aperçu d'entité de l'inventaire.
- Géométrie : modèles **Blockbench** (`.bbmodel`, format Modded Entity, cubes et, si besoin, maillages simples) chargés par le chargeur de rigs existant (`render/RigLoader`) ou un chargeur frère. Les facettes low poly sont faites de cubes tournés et de plaques, ce qui reste du `ModelPart`.
- Éclairage : pas de lumière dynamique. Les ombres et lumières sont **peintes dans les couleurs** (faces claires vers la source, faces sombres à l'opposé) et renforcées par des sprites 2D additifs (halo, rayons) posés devant.
- Rendu `RenderType.entityTranslucent` ou `entityCutoutNoCull` en pleine lumière (`LightTexture.FULL_BRIGHT`), avec une couleur multiplicatrice par élément pour les fondus et les teintes.
- La **paire d'ailes tirée** (S et plus) est rendue avec son vrai modèle (`rigs/<modèle>.bbmodel`) et **sa vraie classe de rig** (`render/rig/wings/<Paire>Rig`), sur un mannequin invisible ou un socle, avec un état de vol forcé (repli, déploiement, battement, plané). Pas de copie du modèle : c'est la paire telle qu'elle sera portée. Faisabilité technique **à valider** au début de la production (alimenter `WingMotion` sans joueur).

### 3.2 Couche 2D

- Sprites PNG dessinés par `GuiGraphics.blit` : halos, rayons, éclats, anneaux, poussières, typographie du palier, cartes.
- Deux modes de mélange : alpha normal, et **additif** (`RenderSystem.blendFunc(SRC_ALPHA, ONE)`) pour la lumière. C'est un état GL de l'interface, pas un shader ni un RenderType ; à confirmer avec zorago (**à valider**), sinon tout passe en alpha normal avec des sprites prééclaircis.
- Animations image par image possibles en **planches** (atlas de frames, 24 ou 30 i/s), pour ce qui ne se décrit pas en transformations : explosion d'encre, flamme, éclair. Le reste est animé par transformations (position, échelle, rotation, opacité, teinte) décrites dans la timeline.
- **Particules d'interface** : émetteurs décrits dans la timeline (débit, durée de vie, vitesse, gravité, rotation, couleur de début et de fin, sprite), simulés côté client dans l'écran. Pas de particules du monde.

### 3.3 Couche son

- Un événement sonore par temps fort, en `.ogg`, joué par `SoundManager` dans la catégorie MASTER (ou une catégorie dédiée si `docs/archive/SOUND_DESIGN.md` le décide). Les identifiants et descriptions sont dans `docs/archive/SOUND_DESIGN.md` (§ gacha). En attendant les vrais sons, le placeholder vanilla indiqué dans chaque fiche.
- Musique : une boucle d'ambiance pour l'écran IDLE et un sting par palier rare. Les stings démarrent à un temps précis de la timeline.

### 3.4 Horloge, saut et ×10

- L'horloge part à 0 au début de LAUNCH. Toutes les durées sont en millisecondes, indépendantes du framerate. La simulation de particules est à pas fixe (60 Hz) pour rester identique d'une machine à l'autre.
- **Points de saut** : chaque timeline définit des marqueurs `skip`. Un clic saute au prochain marqueur ; Échap va au récapitulatif (×10) ou à la révélation finale (×1). Les sons coupés au saut s'arrêtent en fondu de 80 ms.
- **×1** : lancement du palier tiré, puis sa révélation.
- **×10** : lancement **du meilleur palier** des dix ; puis chaque carte, dans l'ordre du tirage, avec la **révélation courte** de son palier (B et A : 0,35 s et 0,5 s ; S et plus : leur révélation complète, lancement exclu) ; puis le récapitulatif.

## 4. Structure commune des animations

Chaque animation suit le même squelette en six temps. Les durées varient par palier (§ 6).

1. **Embrasement** : l'autel (en 3D, vu de trois quarts, au premier plan bas) s'allume, les flammes violettes montent, la plume d'invocation est consumée (brûlure en éclats).
2. **Envol** : une **comète-plume** (plume low poly, traînée en ruban) quitte l'autel et monte dans un ciel de l'End low poly (dôme facetté, îles flottantes en silhouette, étoiles en sprites). Sa couleur est celle du **palier de départ** (§ 4.1).
3. **Escalade** (S et plus) : la comète change de couleur, palier par palier, sur des temps musicaux. Chaque changement est un **battement** : contraction, flash court, onde de choc 2D, sting.
4. **Impact** : la comète plonge vers la caméra ou vers l'autel, flash plein écran (limité, voir § 8.4), puis noir ou blanc selon le palier.
5. **Révélation** : la carte du palier apparaît ; pour une paire d'ailes, la paire en 3D se déploie, avec son nom et ses étoiles. Le **nom** et le **palier** apparaissent en typographie animée.
6. **Repos** : boucle tranquille (la paire bat doucement des ailes, particules lentes) jusqu'au clic.

### 4.1 Règles de fausse piste

- Couleur de départ de la comète : B → blanc ; A → bleu ; S → bleu, puis violet ; SS → bleu, violet, puis or ; SSS → bleu, violet, or, puis rouge ; mythique → l'escalade SSS complète, puis **éclipse** (§ 6.6 et 6.7).
- Variante (**à valider**) : pour S, une chance sur trois de partir directement en violet ; pour SS, une sur trois de partir en violet. Le but est que le bleu ne soit pas toujours « un S en devenir » : sinon un départ blanc déçoit à coup sûr. Un départ **blanc** ne monte jamais (c'est un B).
- Un A part en bleu et reste bleu. Le joueur ne sait donc pas, pendant la première seconde, si un bleu est un A ou un rare : c'est voulu.

### 4.2 Décor 3D commun (« le ciel de l'autel »)

À modéliser une fois, partagé par toutes les animations :

| Élément | Description | Budget |
| --- | --- | --- |
| Autel draconique | Version agrandie et détaillée du bloc `draconic_altar` : socle en os de dragon et obsidienne, aile de plumes blanches à gauche, aile de membrane rouge à droite, cristal violet suspendu. Mêmes couleurs que la texture du bloc. | ≤ 120 cubes |
| Ciel | Dôme facetté (icosaèdre à 2 subdivisions) en dégradé violet nuit → noir, étoiles en sprites. | ≤ 80 faces |
| Îles | Cinq silhouettes d'îles flottantes en contre-jour (les cinq structures du mod), très simplifiées, à des profondeurs différentes pour la parallaxe. | ≤ 40 cubes chacune |
| Comète-plume | Plume low poly (rachis + 6 à 8 facettes de vexille), traînée en ruban de 12 segments qui suit la trajectoire. | ≤ 30 éléments |
| Socle de révélation | Disque facetté en os et obsidienne, anneau runique en sprite au sol. | ≤ 40 cubes |

Caméra commune : focale équivalente 35 mm, légère respiration (amplitude 0,3°, période 4 s) pendant les temps calmes ; mouvements avec des courbes d'accélération franches (`easeOutExpo` à l'arrivée, `easeInCubic` au départ).

## 5. Cartes, typographie et récapitulatif

- **Carte** : rectangle portrait 3:4 aux coins taillés, bordure et fond propres à chaque palier (B argent sobre ; A bleu ; S violet avec un liseré de plumes ; SS or avec des écailles ; SSS rouge avec des arêtes irisées ; mythique noir ou nacre selon la Furie). Icône de l'objet au centre (B, A) ou rendu 3D de la paire (S et plus).
- **Typographie** : police de Minecraft (pas de police externe), agrandie en multiples entiers, ombre portée. Le nom du palier (`B`, `A`, `S`, `SS`, `SSS`, `✦`) entre avec une animation propre au palier (§ 6). Le nom de l'objet s'écrit lettre par lettre pour S et plus (25 ms par lettre).
- **Étoiles** : S = 3 étoiles, SS = 4, SSS = 5, mythique = 6 ; elles s'allument une à une (80 ms d'écart) avec un tintement.
- **Mention « New! »** sur une paire que le joueur ne possédait pas ; « Duplicate (+N feathers at the fountain) » sur un doublon, avec la valeur de revente. Textes en anglais.
- **Récapitulatif ×10** : grille 5 × 2 de cartes réduites, entrée en cascade (40 ms d'écart, de gauche à droite), la meilleure carte légèrement plus grande et animée. Infobulles au survol. Bouton « Summon again ×10 » et « Close ».

## 6. Fiches par palier

Les durées s'entendent pour un tirage ×1, sans saut. « t » = temps depuis le début de LAUNCH.

### 6.1 B : ressource primaire

- **Intention** : une petite lueur d'étoile qu'on attrape. Satisfaisant, rapide, sans fanfare.
- **Durée** : 2,4 s (lancement 1,6 s, révélation 0,8 s). En ×10, révélation courte : 0,35 s.
- **Timeline** :
  - 0–0,4 s : embrasement court de l'autel (flammes basses), la plume se consume en 6 éclats gris.
  - 0,4–1,4 s : comète **blanche**, petite, trajectoire en arc vers le haut à gauche ; traînée fine. Caméra fixe.
  - 1,4–1,6 s : la comète redescend et éclate doucement au centre : anneau 2D blanc, 20 particules argent.
  - 1,6–2,4 s : carte B qui se retourne (rotation Y 180° en 220 ms, `easeOutBack`), icône de l'objet qui rebondit une fois. Point de saut à 1,6 s.
- **3D** : décor commun seulement, comète petite (échelle 0,6).
- **2D** : anneau d'impact (planche de 8 frames, 128 px), particules argent (sprite 4 px), lueur douce derrière la carte.
- **Son** : `simplywings:gacha.launch.small` (placeholder `minecraft:item.pickup` grave), `simplywings:gacha.reveal.b` (placeholder `minecraft:entity.experience_orb.pickup`).
- **Accepté si** : on ne s'en lasse pas après 50 tirages d'affilée (test de zorago), et l'objet est lisible dès 1,7 s.

### 6.2 A : objet craftable

- **Intention** : un objet fabriqué, net et précis, comme un engrenage qui s'emboîte. Un cran au-dessus de B.
- **Durée** : 3,0 s (lancement 1,9 s, révélation 1,1 s). En ×10 : 0,5 s.
- **Timeline** :
  - 0–0,5 s : embrasement, flammes moyennes, la plume se consume en éclats bleus.
  - 0,5–1,6 s : comète **bleue**, trajectoire en S, traînée avec deux rubans entrelacés. Léger travelling avant de la caméra (5 %).
  - 1,6–1,9 s : la comète se plante au centre, trois anneaux concentriques bleus décalés de 60 ms, flash bleu léger (opacité 35 %).
  - 1,9–3,0 s : carte A qui glisse du bas avec un léger dépassement, cadre bleu qui s'illumine de gauche à droite (balayage), icône qui tourne d'un quart de tour en se posant. Point de saut à 1,9 s.
- **3D** : comète bleue (échelle 0,8).
- **2D** : anneaux (sprites vectoriels mis à l'échelle), balayage lumineux du cadre (sprite 64 × 8 additif), 40 particules cyan.
- **Son** : `simplywings:gacha.launch.small`, `simplywings:gacha.reveal.a` (placeholder `minecraft:block.note_block.chime`).
- **Accepté si** : un A se distingue d'un B à 0,6 s (couleur et trajectoire), et la révélation reste sous 1,2 s.

### 6.3 S : paire rare

- **Intention** : « quelque chose arrive ». Première vraie montée. La paire d'ailes apparaît et bat des ailes : le joueur voit ce qu'il va porter.
- **Durée** : 5,5 s (lancement 2,9 s, révélation 2,6 s).
- **Timeline** :
  - 0–0,6 s : embrasement plus haut, cristal de l'autel qui pulse deux fois.
  - 0,6–1,7 s : comète **bleue** (ou violette d'emblée, § 4.1), trajectoire en spirale ascendante ; la caméra la suit en panoramique vers le haut.
  - 1,7 s : **battement** : la comète se contracte (échelle 0,7 en 120 ms), flash blanc court (80 ms, 40 %), onde de choc 2D, elle devient **violette**. Sting.
  - 1,7–2,6 s : comète violette plus grosse, traînée de plumes violettes qui se détachent (sprites de plumes, 2 par 100 ms) ; les îles en silhouette s'éclairent en violet par le dessous.
  - 2,6–2,9 s : plongée vers la caméra, flash violet plein écran (60 %, 150 ms), fondu au noir de 100 ms.
  - 2,9–3,8 s : socle de révélation, colonne de lumière violette (sprite additif vertical), la **paire d'ailes** apparaît **repliée** au centre, puis se **déploie** (pose de repos → pose déployée, 600 ms), un battement complet. Plumes violettes qui tombent.
  - 3,8–5,5 s : « S » entre par un zoom arrière (échelle 3 → 1, `easeOutExpo`) avec 3 étoiles, nom de la paire lettre par lettre, mention New! ou Duplicate. La paire passe en boucle de vol stationnaire (sa vraie animation de stationnaire). Points de saut à 2,9 s et 3,8 s.
- **3D** : décor commun, comète (échelle 1,0), paire réelle sur le socle, caméra en léger orbite (15° sur 1,7 s).
- **2D** : onde de choc (planche 12 frames), plumes qui tombent (sprites de 3 formes, teinte violette), colonne de lumière, 3 étoiles.
- **Son** : `gacha.launch.rare`, `gacha.escalate.s`, `gacha.impact.s`, `gacha.reveal.s`, `gacha.star` ×3 (placeholders : `minecraft:block.beacon.activate`, `minecraft:block.amethyst_block.chime`, `minecraft:block.note_block.bell`).
- **Accepté si** : le changement bleu → violet se remarque même sans le son, et la silhouette de la paire est lisible pendant le déploiement.

### 6.4 SS : paire épique

- **Intention** : un trésor. Or, chaleur, écailles de dragon. On sent le poids.
- **Durée** : 7,0 s (lancement 3,6 s, révélation 3,4 s).
- **Timeline** :
  - 0–0,6 s : embrasement, les flammes passent du violet à l'orangé, le sol vibre (tremblement de caméra 2 px, 300 ms).
  - 0,6–1,6 s : comète **bleue** (ou violette d'emblée), spirale.
  - 1,6 s : battement → **violet** (comme S).
  - 1,6–2,6 s : comète violette, les îles s'éclairent.
  - 2,6 s : second battement, plus fort : la comète s'arrête net, se fend en éclats, et un **noyau d'or** en sort. Onde de choc dorée, écailles dorées en sprites qui volent vers la caméra.
  - 2,6–3,3 s : comète **or**, trajectoire en grand arc ; derrière elle, une **silhouette de dragon** (os, low poly, 40 cubes) traverse le ciel en ombre chinoise.
  - 3,3–3,6 s : impact sur l'autel ; pilier de lumière dorée vers le ciel ; flash blanc chaud (70 %, 150 ms).
  - 3,6–4,8 s : socle, rideau d'écailles dorées qui tombe et révèle la paire déployée (pas de pose repliée : elle est déjà ouverte, cadrée en contre-plongée), deux battements puissants qui soufflent les écailles.
  - 4,8–7,0 s : « SS » qui frappe l'écran (échelle 1,6 → 1 en 90 ms, tremblement 3 px), 4 étoiles, nom, New!/Duplicate. Boucle de plané. Points de saut à 3,6 s et 4,8 s.
- **3D** : décor commun, noyau d'or (prisme à 8 facettes), silhouette de dragon, paire réelle, caméra en contre-plongée qui remonte.
- **2D** : éclats de fente (planche 10 frames), écailles dorées (sprites de 2 formes, rotation), pilier de lumière, onde de choc dorée, 4 étoiles.
- **Son** : `gacha.launch.rare`, `gacha.escalate.s`, `gacha.escalate.ss`, `gacha.impact.ss`, `gacha.reveal.ss`, `gacha.star` ×4 (placeholders : `minecraft:ui.toast.challenge_complete`, `minecraft:entity.ender_dragon.flap`).
- **Accepté si** : la fente violet → or est le moment fort, lisible à 2,6 s ; la silhouette du dragon ne masque jamais la comète.

### 6.5 SSS : paire légendaire

- **Intention** : le ciel lui-même répond. Rouge sang, irisation, le dragon de l'End en entier. Un moment qu'on raconte.
- **Durée** : 9,0 s (lancement 4,6 s, révélation 4,4 s). Premier clic accepté à 2 s.
- **Timeline** :
  - 0–0,8 s : embrasement total, l'autel lance une colonne de flammes violettes, la caméra recule (travelling arrière 15 %).
  - 0,8–1,6 s : comète bleue (ou violette).
  - 1,6 s : battement → violet.
  - 2,4 s : battement → or (fente, comme SS).
  - 2,4–3,4 s : comète or, silhouette de dragon.
  - 3,4 s : **troisième battement** : le ciel se fissure. Des fissures rouges (sprites en planche) courent sur le dôme, la comète devient **rouge sang** avec des arêtes irisées (cyan et or qui glissent sur les facettes, animation de teinte), les îles virent au rouge.
  - 3,4–4,3 s : le **dragon de l'End low poly** (silhouette complète, 80 cubes, ailes battantes) passe derrière la comète, rugit ; la comète décrit une boucle autour de lui.
  - 4,3–4,6 s : impact, écran **blanc** (pas rouge, pour limiter l'agression visuelle) 200 ms, puis retour sur un fond rouge sombre.
  - 4,6–6,0 s : socle sur un lac de lumière rouge ; la paire tombe du ciel **repliée** comme un rapace en piqué (repli faucon si la paire en a un), se déploie à 1 m du socle en un battement qui projette une onde de choc rouge et un anneau de plumes irisées.
  - 6,0–9,0 s : « SSS » qui s'écrit lettre par lettre en lames rouges (chaque lettre 120 ms, tremblement), 5 étoiles, nom, New!/Duplicate ; la caméra fait un orbite lent de 40° autour de la paire en vol stationnaire. Points de saut à 4,6 s et 6,0 s.
- **3D** : décor commun, dragon de l'End low poly animé (battement d'ailes propre), comète rouge irisée, paire réelle, orbite caméra.
- **2D** : fissures du ciel (planche 16 frames, 512 px), onde de choc rouge, anneau de plumes irisées (sprites avec cycle de teinte), lames de lettres, 5 étoiles.
- **Son** : `gacha.launch.legendary`, `gacha.escalate.s`, `gacha.escalate.ss`, `gacha.escalate.sss`, `gacha.dragon_roar`, `gacha.impact.sss`, `gacha.reveal.sss`, `gacha.star` ×5 (placeholders : `minecraft:entity.ender_dragon.growl`, `minecraft:block.end_portal.spawn`).
- **Accepté si** : les trois battements sont distincts et de plus en plus forts ; aucun flash n'enfreint § 8.4 ; la paire reste le centre de l'image dès 4,6 s.

### 6.6 Mythique : Night

- **Intention** : la nuit avale tout. Hommage à la Furie Nocturne : noir absolu, tir de plasma bleu-violet, rien n'annonce le dragon avant qu'il ne soit là.
- **Durée** : 13,0 s (lancement 6,8 s, révélation 6,2 s). Premier clic accepté à 2 s. Au premier tirage d'un mythique par le joueur, aucun saut avant 6,8 s (**à valider**).
- **Timeline** :
  - 0–4,6 s : exactement l'animation SSS jusqu'à l'impact (le joueur croit à un SSS).
  - 4,3 s : au lieu de l'impact, **éclipse** : la comète rouge s'arrête, le son se coupe net (silence de 400 ms), toutes les lumières s'éteignent du bord vers le centre (vignette noire qui se referme, 500 ms). Il ne reste que les étoiles.
  - 4,8–5,6 s : les étoiles s'éteignent une par une, de gauche à droite. Noir total. Un battement d'ailes lointain (son seul).
  - 5,6 s : deux **yeux** verts-jaunes à fente s'ouvrent dans le noir (sprite 2D, 400 ms), puis disparaissent.
  - 6,0–6,8 s : **tir de plasma** : une boule bleu-violet traverse l'écran vers la caméra, sifflement montant (le sifflement caractéristique est remplacé par une création originale, voir `docs/archive/SOUND_DESIGN.md`), explosion de plasma plein écran (bleu, 250 ms, limité § 8.4).
  - 6,8–9,0 s : au centre, la paire **Night** déployée en contre-jour, éclairée seulement par des arcs de plasma qui courent sur ses bords d'attaque ; la queue (`render/TailModel`) ondule. Elle fait un battement qui souffle des particules d'ombre.
  - 9,0–13,0 s : « ✦ » qui s'ouvre comme un œil, 6 étoiles bleu-violet, nom en lettres blanches électriques, New!/Duplicate ; boucle : vol stationnaire, plasma qui pulse sur un temps de 2 s. Points de saut à 6,8 s et 9,0 s.
- **3D** : décor SSS jusqu'à l'éclipse, puis vide noir ; paire Night réelle avec sa queue ; sphère de plasma low poly (icosaèdre, facettes en dégradé bleu → blanc au centre).
- **2D** : vignette d'éclipse, yeux, explosion de plasma (planche 16 frames), arcs électriques (planche 8 frames en boucle), particules d'ombre (sprites sombres en alpha normal).
- **Son** : séquence SSS, puis `gacha.eclipse` (coupure + sub grave), `gacha.mythic.night.wingbeat`, `gacha.mythic.night.plasma`, `gacha.mythic.night.reveal`, `gacha.star` ×6 (placeholders : `minecraft:entity.phantom.flap`, `minecraft:entity.warden.sonic_boom` grave, `minecraft:entity.lightning_bolt.thunder`).
- **Accepté si** : le joueur croit à un SSS jusqu'à 4,3 s ; le noir dure assez pour inquiéter (≥ 1,2 s) sans faire croire à un bug (les étoiles qui s'éteignent montrent que c'est voulu).

### 6.7 Mythique : Light

- **Intention** : la lumière se révèle. Hommage à la Furie Éclair : blanc nacré, iridescence, camouflage qui se dissipe ; tout le contraire de Night.
- **Durée** : 13,0 s (lancement 6,8 s, révélation 6,2 s). Mêmes règles de saut que Night.
- **Timeline** :
  - 0–4,3 s : animation SSS (le joueur croit à un SSS).
  - 4,3 s : au lieu de l'impact, la comète rouge **se décolore** : le rouge glisse vers le blanc (600 ms), le ciel rouge pâlit, les fissures se referment en lumière.
  - 4,9–5,8 s : **blanc** progressif (pas de flash : une montée de 900 ms jusqu'à un blanc nacré à 85 %), sur lequel courent des reflets irisés rose et cyan. Sons qui s'élèvent en accord.
  - 5,8–6,8 s : dans le blanc, une forme se devine par **réfraction** : la silhouette de la paire apparaît comme une déformation du fond (sprite de la silhouette en blanc légèrement plus sombre, bords irisés), c'est le camouflage. Elle bat des ailes une fois.
  - 6,8–9,0 s : le camouflage se dissipe des bords d'attaque vers le bout des ailes (masque animé, 900 ms) : la paire **Light** apparaît, nacrée, avec sa queue ; le fond redescend à un ciel de l'aube (bleu très pâle, or).
  - 9,0–13,0 s : « ✦ » qui se forme à partir de quatre éclats de lumière, 6 étoiles nacrées, nom en lettres irisées (cycle de teinte), New!/Duplicate ; boucle : vol stationnaire, reflets qui glissent sur les ailes toutes les 3 s. Points de saut à 6,8 s et 9,0 s.
- **3D** : décor SSS jusqu'à 4,3 s, puis ciel d'aube facetté (même dôme, autre dégradé) ; paire Light réelle avec sa queue.
- **2D** : voile blanc avec reflets irisés (planche 12 frames en boucle), silhouette réfractée, masque de dissipation, éclats lumineux.
- **Son** : séquence SSS, puis `gacha.mythic.light.fade`, `gacha.mythic.light.shimmer`, `gacha.mythic.light.reveal`, `gacha.star` ×6 (placeholders : `minecraft:block.beacon.power_select`, `minecraft:block.amethyst_block.resonate`, `minecraft:block.conduit.activate`).
- **Accepté si** : Night et Light se reconnaissent l'une de l'autre dès 4,5 s (noir contre blanc) ; la montée au blanc ne provoque aucun éblouissement brutal.

## 7. Écran IDLE

Pas une animation de palier, mais à produire dans le même lot :

- Illustration de couverture (480 × 270 de référence, déjà en provisoire `textures/gui/summon_cover.png`) **animée par calques** : ciel qui défile lentement, silhouettes de Night et Light qui glissent très loin dans le ciel (une traversée toutes les 40 s environ), autel qui pulse, poussières de lumière. 4 à 6 calques en parallaxe, sans planche lourde.
- Boutons ×1 et ×10 avec un état survol lumineux (balayage), tracker de pitié dont les jauges se remplissent avec une animation au retour d'un tirage.
- Musique d'ambiance en boucle (voir `docs/archive/SOUND_DESIGN.md`).

## 8. Contraintes techniques et budgets

### 8.1 Résolution et mise à l'échelle

- Référence de composition : **480 × 270** unités d'interface (l'écran se met à l'échelle selon la taille de la fenêtre et l'échelle d'interface). Les sprites sont dessinés pour une échelle d'interface 3 (donc 1440 × 810 pixels réels pour l'écran complet), en **pixel art** ou en aplats nets, sans anticrénelage flou.
- La 3D est cadrée pour un 16:9 et reste correcte de 4:3 à 21:9 (rien d'important à moins de 10 % des bords).

### 8.2 Budgets

| Ressource | Budget par palier | Budget total |
| --- | --- | --- |
| Planches de sprites (PNG) | ≤ 4 atlas de 1024 × 1024 | ≤ 24 atlas, ≤ 12 Mo sur disque |
| Modèles 3D (hors paires) | ≤ 300 cubes à l'écran | décor commun + dragon + extras ≤ 700 cubes |
| Particules d'interface | ≤ 400 vivantes en même temps | — |
| Sons | voir `docs/archive/SOUND_DESIGN.md` | — |

- Les textures sont chargées à l'ouverture de l'écran de l'autel et libérées à la fermeture (pas en permanence en mémoire).
- 60 i/s visés sur une machine modeste (GPU intégré récent) avec les mods lourds du pack (Sodium, Iris). Aucune allocation par frame dans la boucle de rendu.

### 8.3 Données produites

Format de timeline, un fichier JSON par animation, dans `src/main/resources/assets/simplywings/gacha_anim/<palier>.json` :

```json
{
  "id": "ss",
  "length": 7000,
  "launch_end": 3600,
  "skip": [3600, 4800],
  "first_click": 1000,
  "models": { "comet": "simplywings:gacha/comet", "dragon": "simplywings:gacha/dragon_silhouette" },
  "sprites": { "shock_gold": { "atlas": "simplywings:textures/gui/gacha/ss_0.png", "frames": 12, "fps": 30, "size": [128, 128] } },
  "tracks": [
    { "target": "comet", "property": "position", "keys": [[600, [0, -40, 0]], [1600, [20, 60, -10], "easeOutCubic"]] },
    { "target": "comet", "property": "tint", "keys": [[0, "#5AA9FF"], [1600, "#B46CFF", "step"], [2600, "#FFC23A", "step"]] },
    { "target": "camera", "property": "shake", "keys": [[0, 2], [300, 0]] }
  ],
  "emitters": [
    { "at": 2600, "duration": 400, "sprite": "scale_gold", "rate": 120, "life": [600, 900], "speed": [40, 90], "gravity": 30, "color": ["#FFC23A", "#FFF2C0"] }
  ],
  "sounds": [ { "at": 1600, "id": "simplywings:gacha.escalate.s" }, { "at": 2600, "id": "simplywings:gacha.escalate.ss" } ],
  "events": [ { "at": 3600, "type": "reveal_item" }, { "at": 4800, "type": "tier_label" } ]
}
```

- Propriétés animables : `position`, `rotation`, `scale`, `opacity`, `tint`, `frame` (sprite), `shake` (caméra), `fov` (caméra), `pose` (pour la paire : `rest`, `spread`, `flap`, `hover`, `glide`, `tuck`, avec un poids).
- Courbes : `linear`, `step`, `easeInCubic`, `easeOutCubic`, `easeInOutCubic`, `easeOutExpo`, `easeOutBack`, `easeInOutSine`.
- Modèles 3D : `.bbmodel` dans `assets/simplywings/gacha/` (même chargeur que les rigs).
- Le lecteur Java (`client/gui/gacha/`, à écrire) ne connaît que ce format : ajouter ou retoucher une animation ne demande pas de toucher au code.

### 8.4 Accessibilité

- **Flashs** : jamais plus de 3 flashs par seconde ; un flash plein écran ne dépasse pas 70 % d'opacité, sauf le blanc SSS (100 %, 200 ms, une seule fois). Respect de la recommandation WCAG 2.3.1.
- Option client **`reduceFlashes`** (config client du mod) : remplace les flashs par des fondus de 300 ms à 40 % et coupe les tremblements de caméra. Les animations restent identiques par ailleurs.
- Le palier est toujours écrit en clair sur la carte (pas seulement en couleur) pour les joueurs daltoniens.
- Volume pilotable par les curseurs de Minecraft (catégorie choisie dans `docs/archive/SOUND_DESIGN.md`).

## 9. Contrat avec le skill de motion design

Le skill à créer (`.claude/skills/simplywings-motion-design/`) produit chaque animation à partir de ce document. Il reçoit l'identifiant d'une fiche (`b`, `a`, `s`, `ss`, `sss`, `mythic_night`, `mythic_light`, `idle`) et livre, dans `art/gacha/<id>/` :

1. **Storyboard** : 6 à 12 images clés en PNG (480 × 270), une par temps fort de la fiche, annotées du temps. C'est ce que zorago valide **avant** la production.
2. **Modèles** : les `.bbmodel` low poly nécessaires (via Blockbench et son MCP, skills `blockbench-*`), copiés dans `assets/simplywings/gacha/`.
3. **Sprites** : planches PNG générées par script (Python + PIL ou pxl pour le pixel art), copiées dans `assets/simplywings/textures/gui/gacha/`.
4. **Timeline** : le JSON du § 8.3, copié dans `assets/simplywings/gacha_anim/`.
5. **Prévisualisation hors jeu** : un rendu de la timeline (modèles, sprites et timeline lus exactement comme en jeu) exporté en **MP4 1920 × 1080, 60 i/s**, avec les sons placeholders mixés, dans `art/gacha/<id>/preview.mp4`. Le moteur de prévisualisation est libre (navigateur headless avec three.js, ou Python), à condition de lire les mêmes fichiers que le jeu. Le MP4 est le **master** qui servira à la vitrine et à la bande-annonce.
6. **Rapport** : durées mesurées, budgets consommés (§ 8.2), conformité aux flashs (§ 8.4), écarts avec la fiche et pourquoi.

Boucle de travail : storyboard → validation de zorago → production → prévisualisation MP4 → validation → intégration et capture en jeu (studio, quand le PC est libre) → ajustements. Une animation n'est terminée que quand la capture en jeu correspond au MP4.

Ordre de production : **décor commun et lecteur Java** d'abord (sans eux, rien ne tourne), puis B et A (les plus vues, elles fixent la qualité de base), puis S, SS, SSS, puis les deux mythiques, puis IDLE.

**26 octobre 2026** : la première production ne s'est pas faite avec ce skill, mais dans une conversation claude.ai de motion design, qui a produit le prototype du § 0. Elle couvre le storyboard, la prévisualisation en MP4 et une timeline au format du prototype. Le skill `simplywings-motion-design`, quand il sera créé, part de ce code (`gacha-motion-code.zip`) plutôt que de zéro.

## 10. Points à valider avec zorago

- Mélange additif dans l'interface (§ 3.2) : autorisé ou non.
- Rendu de la vraie paire avec sa classe de rig hors joueur (§ 3.1) : faisabilité à prouver par un prototype avant toute production S et plus.
- Variante de fausse piste (§ 4.1) : une chance sur trois de partir directement en violet pour S et SS.
- Pas de saut possible pendant le premier mythique d'un joueur (§ 6.6).
- Mention « Duplicate » avec la valeur de revente à la fontaine (§ 5).
- Durées : les fiches donnent des valeurs de départ ; zorago tranchera sur les prévisualisations.
- **Ajoutés le 26 octobre, après le prototype (§ 0)** :
  - valider les films A, S, SS, SSS et les deux ×10 en mouvement (le B est validé) ;
  - durée des communs : le film complet de 14,4 s pour un B, avec saut et mode rapide pour les tirages répétés, ou un film court spécifique (§ 0.4, point 1) ;
  - portage : traduire `timeline.js` + `state.js` en Java, ou convertir au format de tracks du § 8.3 (§ 0.6, point 3) ;
  - live pour tout, ou pré-rendu en planches pour les singles (§ 0.5) ;
  - autel : la scène du prototype refaite en cubes, ou le build custom de zorago ;
  - flammes : en cubes animés ou en planche de sprites ;
  - garder ou non la silhouette de dragon (SS) et le dragon de l'End (SSS), absents du prototype ;
  - mythiques Night et Light : à produire dans le même langage visuel que le prototype (étoile qui retombe dans l'autel, 4 flammes, carte) ;
  - flashs : garder les plafonds du prototype (0,7 ; 0,85 pour l'éclat SSS) plutôt que le blanc à 100 % du § 8.4.
