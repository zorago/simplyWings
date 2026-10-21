# Sources et règles appliquées

Ce document rassemble les recherches faites pour la refonte art (modèles, animations, textures, interfaces) et les règles qui en ont été tirées. Il sert de référence pour la suite : refaire toutes les ailes dans Blockbench, puis affiner les animations, les particules et les sons. Chaque règle dit d'où elle vient et où elle vit dans le code.

Dernière mise à jour : 21 octobre 2026.

## 1. Sources, triées par thème

### Biomécanique du battement
| Source | Ce qu'on en retient |
| --- | --- |
| [Wingbeat kinematics, poignet et trajectoire de la pointe (JEB)](https://cob.silverchair.com/jeb/article-pdf/209/19/3887/1542027/3887.pdf) | La remontée commence par une flexion du coude et du poignet, l'aile monte et recule. En vol lent, la main se retourne (« tip reversal ») et donne un coup vers l'arrière. La pointe décrit une ellipse vue de côté. |
| [Wingbeat kinematics and flight speed (JEB 2004)](https://cob.silverchair.com/jeb/article/207/24/4255/2687/The-relationship-between-wingbeat-kinematics-and) | Le plan du battement et la flexion de l'aile changent avec la vitesse. |
| [3D kinematics of hummingbird flight (Biewener lab)](https://biewenerlab.oeb.harvard.edu/publications/three-dimensional-kinematics-hummingbird-flight) | En stationnaire, le battement est horizontal, en huit, avec une légère flexion du poignet à la remontée. |
| [Downstroke ratio (USC, RSH JEB 2004)](https://sites.usc.edu/drydenwt/files/2022/06/RSH_JEB_2004.pdf) | Définition du rapport temps de descente / cycle. La descente reste de durée stable, la remontée raccourcit avec la vitesse. |

### Anatomie et repli de l'aile
| Source | Ce qu'on en retient |
| --- | --- |
| [Brevet de leurre, repli de l'aile au repos](https://image-ppubs.uspto.gov/dirsearch-public/print/downloadPdf/7225579) | Au repos, l'épaule ramène l'humérus contre le corps et le poignet se plie à fond pour ranger les pointes. |
| [Secondaires et tertiaires (Birds Outside My Window)](https://www.birdsoutsidemywindow.org/2009/12/04/anatomy-secondaries-and-tertials/) | Les secondaires partent de l'avant-bras (ulna), les tertiaires de l'humérus. Repliées, les tertiaires recouvrent presque les primaires. |
| [Comment dessiner des ailes (Chris Wilson)](https://chriswilsonstudio.com/how-to-draw-bird-wings/) | Repères visuels sur la structure épaule, coude, poignet et le rangement des plumes. |

### Décollage, atterrissage, piqué
| Source | Ce qu'on en retient |
| --- | --- |
| [Bird flight (Wikipedia)](https://en.wikipedia.org/wiki/Bird_flight) | Les gros oiseaux décollent face au vent ou en se laissant tomber. Pour atterrir, ils piquent sous le point visé puis remontent pour arriver à vitesse quasi nulle. |
| [Golden eagle landing, ralenti (Cornell)](https://academy.allaboutbirds.org/golden-eagle-landing/) | Piqué très rapide, puis ailes et queue largement ouvertes pour freiner. |
| [How do birds land (Londolozi)](https://blog.londolozi.com/2012/10/10/how-do-birds-land/) | Ailes grandes ouvertes comme un frein à air, incidence forte. |
| [Takeoff and landing mechanisms (SICB)](https://sicb.org/?p=26922) | Le plan du battement s'incline vers le bas au décollage et vers le haut à l'atterrissage. |

### Rémiges, plané
| Source | Ce qu'on en retient |
| --- | --- |
| [Rémiges échancrées (University of Montana)](https://umimpact.umt.edu/en/publications/phylogenetics-and-ecomorphology-of-emarginate-primary-feathers/) | Les rémiges externes se séparent en fentes et travaillent comme des profils indépendants. |
| [Bird flight (Canadian Encyclopedia)](https://thecanadianencyclopedia.ca/en/article/bird-flight) | Les rémiges d'un rapace qui plane se recourbent vers le haut. |

### Animation (principes et pratique pro)
| Source | Ce qu'on en retient |
| --- | --- |
| [Follow-through et overlapping action (CG Wire)](https://blog.cg-wire.com/follow-through-overlapping-action) | La partie qui mène part d'abord, les autres suivent en décalé. Le mouvement secondaire continue après l'action principale. |
| [Cycle de vol de dragon, Craig Caton (NYFA)](https://cai.nyfa.edu/film-school-blog/maya-60-minutes-craig-caton/) | Les sections externes pointent vers le bas pendant que l'aile monte. Ne pas chercher la symétrie parfaite. |
| [Dragon flight cycles, multiplexing (DreamWorks)](https://research.dreamworks.com/wp-content/uploads/2019/10/talk_multiplexing_stage2.pdf) | Une aile de dragon demande beaucoup plus de contrôles qu'un bipède : d'où un rig procédural paramétré. |
| [Animating a dragon (The Rookies)](https://www.therookies.co/blog/breakdowns/animating-a-dragon-for-your-personal-reel-tips-and-techniques) | Un battement, bouclé. Les ailes s'animent en dernier, le mouvement secondaire de la membrane après. |
| [Critique de cycle de vol (IRTC 2002)](https://ftp.fau.de/pub/graphics/irtc/anims/2002-01-15/comments/tbcforce.comments) | La descente est bien plus rapide que la remontée, et les ailes bougent aussi à l'horizontale. |

### Outils
| Source | Usage |
| --- | --- |
| [Plugin MCP Blockbench (jasonjgardner)](https://github.com/jasonjgardner/blockbench-mcp-plugin) | Pilotage de Blockbench par Claude (`.mcp.json`, port 3000, `/bb-mcp`). |
| [Skills Blockbench (jasonjgardner/blockbench-mcp-project)](https://github.com/jasonjgardner/blockbench-mcp-project) | `blockbench-use`, `-modeling`, `-texturing`, `-animation` dans `.claude/skills/`. |
| [claude-pxl (crsolver)](https://github.com/crsolver/claude-pxl) | Pixel art en code (`.px.js`) : textures, icônes, interfaces. Skill `pxl-pixel-art`. |

## 2. Règles d'animation appliquées

Toutes vivent dans `anim/WingAnimator.java` (états et ressorts), `anim/RigCycles.java` (cycle de battement) et `render/RiggedWingModel.java` (mappage vers les os).

| Observation | Règle | Mise en œuvre |
| --- | --- | --- |
| La descente est rapide et puissante, la remontée plus lente | Descente ≈ 25 % du cycle, remontée ≈ 45 %, le reste en fin de course | `upFraction` / `downFraction` du `WingStyle`, clés de `RigCycles.FLAP` |
| Remontée aile pliée, descente aile tendue | Coude et poignet fléchis à mi-remontée, à zéro pendant la descente | Canaux `ELBOW` et `WRIST` du cycle |
| Retournement de la main en vol lent | Supination de la main à la remontée | Canal `HAND_TWIST` (cycle et stationnaire) |
| La pointe décrit une ellipse | Balayage avant à la descente, arrière à la remontée | Canal `SWEEP` déphasé par rapport à `STROKE` |
| Overlapping action | Coude, poignet et pointes suivent l'épaule avec du retard | `BEND_*` = gain × (battement à t − retard − battement à t) ; retards 0,04 / 0,07 / 0,10 du cycle, gains 0,2 / 0,3 / 0,35 |
| Trop de retard donne une aile molle | Flexion cumulée max ≈ 30° en pleine descente | Gains ci-dessus, validés image par image dans Blockbench |
| Puissance, anticipation | Courte pause ailes dressées au-dessus du dos avant la descente | Clé à `up + 0,15 × down` (stroke 1,05) |
| Stationnaire des oiseaux | Battement horizontal en huit, retour aile pliée et main retournée | Bloc « hover » de `WingAnimator.rig` |
| Rapace qui plane | Ailes tendues, léger dièdre, pointes recourbées vers le haut, frémissement | État plané : `STROKE` 0,08, `BEND_TIPS` 0,25, bruit lent |
| Piqué | Repli progressif balayé vers l'arrière, puis goutte (repli faucon) | États piqué et `FLAG_TUCK` |
| Atterrissage, frein | Ailes en avant, très cabrées, éventail ouvert, vibration des pointes | État frein (`TWIST` −0,75, `SWEEP` 0,55, `FAN` 1,2) |
| Décollage | Ailes levées haut avant la première poussée | Envol chargé : `FOLD` → 0,1, `STROKE` → 0,85 |
| Oiseau au repos | Ailes repliées, respiration, parfois une remise en place des plumes | Respiration lente ; « rouse » de 26 ticks toutes les 20 à 55 s |
| Poids, follow-through | Toute transition passe par un ressort amorti avec léger dépassement | Ressort par canal : raideur 0,16, amortissement 0,68 ; repli au sol 0,1 / 0,72 ; frein et repli faucon 0,22 / 0,62 |
| Pas de paquet par tick | Tout est calculé localement à partir de l'état synchronisé | Mêmes événements qu'avant : `flap`, `beat`, `strike`, mode et drapeaux |

Conventions des canaux de `RigPose` (radians, côté droit ; le gauche est en miroir avec le signe `s`) :
- `STROKE` + = vers le dos (vers le haut en vol couché) ; c'est la rotation hors du plan de l'aile (`yRot` de l'épaule).
- `SWEEP` + = vers la tête, dans le plan de l'aile (`zRot` de l'épaule).
- `FOLD` 0 = déployée, 1 = repliée en Z dans le plan du dos.
- `FAN` multiplie l'angle de repos de chaque rémige (1 = repos, 0 = alignées sur la main).

## 3. Règles du modèle (rig)

- **Un modèle Blockbench par paire** : `assets/simplywings/rigs/<modèle>.bbmodel`, format Modded Entity, UV de boîte. Le jeu lit ce fichier tel quel, aucun export n'est nécessaire.
- **Les deux ailes sont modélisées dans Blockbench**. Les os portent le préfixe `right_` ou `left_`. L'aile gauche d'une paire symétrique est le miroir de la droite : géométrie inversée en X, rotations Y et Z inversées, `mirror_uv` activé. Une paire asymétrique (Yin et Yang) a simplement deux géométries différentes.
- **Taille réelle** : le modèle est fait à sa taille finale, avec 1 px de texture par unité. Le facteur `WingStyle.SCALE` (×1,5) ne concerne que les anciens modèles codés en Java.
- **Attache** : épaule droite au pivot (2,5 ; 22,5 ; 2,4), épaule gauche à (−2,5 ; 22,5 ; 2,4), dans les coordonnées Blockbench. Un mannequin de joueur à l'échelle (`ref_*`, `export: false`) sert de repère et n'est pas chargé par le jeu.
- **Os attendus par côté** : `shoulder` > `arm` (+ `tertials`) > `forearm` (+ `secondaries`) > `hand` (+ `hand_feathers`, `hand_coverts`, `primaries` > `primary_1` à `primary_N`). `N` est libre (7 pour la commune, 12 au maximum).
- **Les plumes sont des plaques d'épaisseur nulle** (dz = 0) sous leur os. Le contour vient de la transparence de la texture (rendu cutout).
- **Les plaques de plumes ont leur propre os**, contre-tourné pendant le repli. Chez un vrai oiseau, l'angle des plumes par rapport aux os change quand l'aile se plie. Sans cette contre-rotation, l'aile repliée a des plumes qui pointent vers le corps.
- **Repli** (constantes dans `RiggedWingModel`) : bras −1,25 rad, coude +2,75, poignet −2,85. Résultat : coude bas, poignet à hauteur d'épaule, rémiges qui descendent jusqu'aux genoux.
- **Aucun trou une fois déployée** :
  - pour un éventail de rémiges de longueur `L`, de largeur `w` et d'écart angulaire `a`, il faut `L × sin(a) < w`, sinon des fentes s'ouvrent aux pointes. La commune : `L` = 12 à 16, `a` = 12°, `w` = 4 ;
  - une nappe `hand_feathers` sous la main relie les secondaires à la dernière rémige ;
  - l'éventail ne dépasse jamais 1,25 en jeu (frein, décrochage, battement), pour que les plumes se chevauchent toujours.
- **Superposition** : chaque couche de plumes est décalée en Z (tertiaires 0,75, secondaires 0,9, nappe de la main 1,0, couvertures 1,05, rémiges 1,15 et plus). Les rémiges internes passent au-dessus des externes, comme vu de dos chez un oiseau.

## 4. Pipeline Blockbench et pièges connus

- **Conversion Blockbench → `ModelPart`**, identique à l'export Java officiel (vérifiée sur la commune) :
  - décalage = pivot − pivot du parent, X et Y inversés, +24 en Y pour les os racines ;
  - rotations : `xRot` = −rx, `yRot` = −ry, `zRot` = rz ;
  - boîte : `x` = pivot.x − to.x, `y` = −from.y − taille.y + pivot.y, `z` = from.z − pivot.z ;
  - un cube tourné devient un sous-os à son propre pivot.
- **Chemins de texture sous Windows** : charger les textures avec des antislashs, sinon Blockbench affiche « The imported texture is not contained in a resource pack » et ne trouve pas le namespace.
- **`build.gradle`** : le MDK excluait `**/*.bbmodel` des ressources. Cette ligne a été retirée, puisque nos `.bbmodel` sont des assets du jeu. Les sources de travail restent dans `art/`.
- **`risky_eval`** : pas de commentaires ni de `//` dans le code envoyé. `new Function(...)` perd les variables locales : mettre l'état partagé dans `window` (`swInit`, `swF`).
- **Prévisualiser une pose** : la fonction `swPose(pose)`, définie dans Blockbench, applique exactement le mappage de `RiggedWingModel` (rotation Blockbench = −xRot, −yRot, +zRot). On valide les poses par captures depuis une vue hors écran, sans toucher la caméra de l'utilisateur. Dans la vue de dessus, l'image est inversée : le haut de l'écran correspond au côté ventral.
- **Log** : `Loaded wing rig simplywings:rigs/<nom>.bbmodel with N bones` au chargement, ou un avertissement si le fichier manque. Dans ce cas, l'aile retombe sur l'ancien modèle Java.

## 5. Textures (pxl)

- Une texture par paire, `textures/entity/<modèle>_wings.png`, générée par un script `art/wings/<modèle>.px.js`.
- 1 px par unité de modèle, palette de 4 à 6 teintes par matériau, pas de bruit gratuit.
- Plaques en UV de boîte avec dz = 0 : face nord (ventrale, vue de face) en `(u, v)`, face sud (dorsale, vue de dos) en `(u + largeur, v)`. Les rémiges ont la pointe au bout de la face sud et au début de la face nord.
- Face dorsale plus sombre, face ventrale plus claire. Les plumes alternent deux nuances, pointes sombres, bords de fuite festonnés en transparence.

## 6. Interfaces

- Fonds dessinés avec pxl, en style vanilla (`#C6C6C6`, biseaux, cases creusées). Les coordonnées des cases du script sont les mêmes que celles du `Menu` Java.
- Aucun texte ne déborde : un texte trop long est coupé avec « … » et son infobulle donne le texte complet ; le nom dans la fiche passe sur 2 lignes. Les boutons vanilla font défiler un libellé trop long.

## 7. Checklist pour refaire une paire dans Blockbench

1. Relire la fiche de la paire dans `WINGS.md` (apparence, ailes par côté, rendu).
2. Créer le projet Modded Entity, mettre en place le mannequin, puis modéliser l'aile droite sur le rig commun. Adapter le nombre et la longueur des rémiges en respectant `L × sin(a) < w`.
3. Construire l'aile gauche dans Blockbench : miroir pour une paire symétrique, géométrie propre pour une paire asymétrique.
4. Peindre la texture avec pxl, aux coordonnées UV du modèle.
5. Valider avec `swPose` : déployée (aucun trou, vue de dos), repliée (rangée, pas d'intersection avec le corps), haut et bas du battement, frein, repli faucon.
6. Enregistrer dans `assets/simplywings/rigs/<modèle>.bbmodel`, ajouter le nom à `RiggedWingModel.RIGS`, vérifier le log de chargement.
7. Tester en jeu en solo et sur le serveur dédié, puis mettre à jour `WINGS.md`.
