# Carnet de règles des ailes

Carnet de logique du polish, paire par paire, avec zorago. Chaque correction faite sur une paire devient une règle qui s'applique aussi aux paires suivantes de la même famille (ou à toutes, selon sa portée).

## Comment on s'en sert

- Une règle a un identifiant (`G1`, `P3`, `M2`…), une portée (famille) et la paire où elle est née.
- Quand zorago dit « applique comme pour les ailes de type plume précédentes ET modifie x, y et z », on applique **toutes** les règles de la famille plume (et les règles générales), puis les nouveaux points x, y et z. Les nouveaux points deviennent à leur tour des règles s'ils valent pour la famille.
- Avant de polir une paire : relire les règles générales et celles de sa famille, et cocher chaque règle dans son entrée du journal (appliquée, ou sans objet et pourquoi).
- Une règle qui se révèle fausse sur une paire plus tard n'est pas effacée : on la précise (exception, condition) et on note où.

## Familles

Classement établi le 25 octobre 2026 à partir des modèles (`rigs/*.bbmodel`) et des classes de rig. Les noms sont ceux des JSON d'aile (le modèle est entre parenthèses quand il diffère). Une paire peut emprunter à plusieurs familles et cumuler plusieurs aspects (voir le catalogue).

| Famille | Paires | Squelette |
| --- | --- | --- |
| **Plume** (oiseau, `FeatherWing`) | common (`basic`), crow, owl, gull, eagle, swift, pigeon, parrot, hummingbird, peacock, amethyst, copper, patchwork, fire, earth, nebula, shadow, storm, angel, seraph, yin_yang | épaule > bras > avant-bras > main > primaires, rangées de plumes |
| **Membrane à doigts** | bat, demon, ptero, night, light | bras, avant-bras, main, 4 doigts à 2 phalanges, peau entre les os (night et light : `DragonWing`) |
| **Membrane sans doigts / planeur** | squirrel, flying_fish, phantom, kite | os longs ou rayons, peau ou voile tendue, peu ou pas de battement |
| **Insecte** | bee, ancient (`dragonfly`), beetle, moth, psyche | ailes rigides avant et arrière, sans coude. Vrombissantes (bee, ancient, beetle) ou lentes (moth, psyche). Élytres (beetle) |
| **Machine** | ornithopter, daedalus, kite | longerons, engrenages, liaisons, propulseurs ; mouvement mécanique, pas organique |
| **Matière non squelettique** | aurora, air, water, forest | voiles, rubans, segments de fluide, branches : la forme vient de la matière, pas d'os d'oiseau |

## Catalogue des aspects

Chaque aspect demande sa propre logique. Le plan de review (en fin de document) choisit une paire dominante par aspect, pour qu'au bout de la phase 1, chaque aspect ait ses règles.

| Code | Aspect | Paires concernées |
| --- | --- | --- |
| A1 | Aile de plumes battante (base) | toute la famille plume |
| A2 | Membrane tendue entre des doigts, qui se replie en éventail | bat, demon, ptero, night, light |
| A3 | Planeur : membrane ou voile sans doigts, peu de battement | squirrel, flying_fish, phantom, kite |
| A4 | Vrombissement (≥ 8 battements/s, flou par plans fantômes `ghost`) | bee, ancient, beetle, hummingbird |
| A5 | Insecte lent : ailes avant et arrière rigides couplées, sans coude | moth, psyche |
| A6 | Élytres : carapace qui s'ouvre avant que l'aile se déplie | beetle |
| A7 | Plusieurs paires d'ailes à coordonner | seraph (3 paires) ; avant + arrière : bee, ancient, moth, psyche, air, aurora, forest, night, light |
| A8 | Pièces attachées ou ornements qui ne sont pas l'aile | fire (braises), earth (cristaux), nebula (étoiles, galaxie), shadow (yeux), storm (arcs), angel (auréole, scapulaires), yin_yang (taiji), peacock (traîne), psyche (lampe, flammes), daedalus (sac, engrenages, propulseurs, flammes), patchwork (rabats), forest (fleurs, feuilles), water (gouttes), ancient (broche) |
| A9 | Queue ou traîne | night, light, peacock, kite, squirrel, psyche |
| A10 | Matériau : émissif, translucide, calque lumineux | émissif : fire, aurora, psyche, storm ; translucide : air, ancient, bee, beetle, flying_fish, hummingbird, nebula, shadow, water ; calque `glow` : amethyst, angel, copper, daedalus, demon, earth, light, nebula, night, phantom, seraph, shadow |
| A11 | Matière souple ou sans os (tissu, ruban, fluide, végétal) | aurora (voiles), air (voiles et rubans), water (segments et gouttes), forest (branches) |
| A12 | Mécanique : articulations rigides, liaisons, pièces qui tournent | ornithopter, daedalus, kite, copper (plumes de métal) |
| A13 | Poussée continue (pas de coup par coup, `FLAG_THRUST`) | bee (25/s), hummingbird (30/s), ancient (20/s), daedalus (20/s), beetle (12/s) |
| A14 | Asymétrie voulue (gauche ≠ droite) | yin_yang, patchwork |
| A15 | Battement lent et majestueux (≤ 0,8/s) | angel (0,5), psyche (0,6), aurora (0,7), demon (0,7), earth, nebula, kite (0,8) |
| A16 | Mythique : pouvoirs des Furies (charge, dash, invisibilité, transmog) | night, light |
| A17 | Signature d'espèce d'oiseau (alula, doigts écartés, aile en M, faucille…) | crow, owl, gull, eagle, swift, pigeon, parrot, hummingbird |
| A18 | Stationnaire spécial, qui contredit G5 (vol sur place en huit horizontal) | hummingbird, bee, ancient |

## Check-list pour polir une paire

1. Lire sa ligne dans les familles et ses aspects dans le catalogue, puis toutes les règles générales et celles de ses familles.
2. Générer sa scène (`python -I tools/blockbench/scene.py <modèle>`) et l'ouvrir dans Blockbench, en mode Animate.
3. Pour chaque règle, noter dans son entrée du journal : appliquée, adaptée (comment) ou sans objet (pourquoi).
4. Corriger avec zorago point par point. Chaque correction qui vaut pour d'autres paires devient une règle ici.
5. Fin de passe : `WINGS.md` à jour, jar reconstruit, commit et push. Noter ce qui reste à vérifier en jeu.

## Outil : la scène Blockbench

- `python -I tools/blockbench/scene.py <modèle>` fait tourner la vraie classe de rig Java (`./gradlew bakeRig`, sans lancer Minecraft) et écrit `art/scenes/<modèle>.bbmodel` : le modèle, un mannequin Steve, et les animations prêtes à jouer (repos, ébrouement, envol chargé, saut, atterrissage, battement, plané, virages, décrochage, piqués, ailes rentrées, frein, stationnaire, et `full_flight` qui enchaîne tout). Le corps se couche et se cabre comme en jeu (`scene_pitch`, `scene_roll`).
- On retouche le **modèle** (et la texture) directement dans la scène. `python -I tools/blockbench/scene.py <modèle> --pull` les recopie dans `rigs/<modèle>.bbmodel` (et le PNG), sans le mannequin ni les groupes de scène.
- Les **animations** de la scène sont un rendu du code : les retoucher sert à montrer ce qu'on veut, puis la retouche est traduite dans `render/rig/wings/<Paire>Rig.java` et la scène est régénérée. Une régénération écrase les keyframes retouchés à la main.

## Règles générales (toutes les paires)

- **G1 — Le joueur donne l'intention, les ailes exécutent** (née sur `basic`, au frein). Le corps du joueur montre ce qu'il **veut** faire (avancer, piquer, s'arrêter, se poser), et les ailes travaillent pour lui. Jamais l'inverse : le corps ne doit pas avoir l'air traîné, écrasé ou ballotté par des ailes plus fortes que lui. Pour chaque état, on se demande : « qu'est-ce que le joueur veut faire, et comment un être ailé surpuissant le ferait-il ? ». La posture vient de cette intention, puis les ailes s'organisent autour pour produire l'effort.
  - Frein : c'est l'**atterrissage d'un être ailé surpuissant**, en deux temps (référence : le « test drive » de Krokmou dans *Dragons* 1). **Au-dessus de la vitesse minimale de vol**, c'est un parachute : ailes totalement déployées, levées vers l'avant et creusées, **fixes** (pas de battement, qui rendrait le freinage incohérent à cette vitesse). Seul un tremblement fin des bouts d'aile montre la pression de l'air, et une traînée de nuage dense part des deux extrémités des ailes (G2). **Sous la vitesse minimale**, pendant la transition vers le stationnaire (drapeau `FLAG_SLOW`, poids `WingMotion.brakeBeat`), les ailes passent aux coups de freinage battus décrits plus bas. Pour préparer le stationnaire, elles repassent **derrière les bras** (les articulations restent dans le dos pendant tout le cycle), et les bras du joueur redescendent le long du corps, comme un personnage Minecraft normal. Le corps se redresse quel que soit le regard, à peu près vertical et penché de 8° en arrière (`BodyPose.angle`, `LANDING_LEAN`). Les jambes sont dessous, l'une un peu en avant et l'autre en arrière, prêtes à toucher le sol. Les bras sont écartés sur les côtés pour l'équilibre, avec un léger balancement. La tête regarde devant, un peu vers le bas (`FlightPose`). Les ailes sont levées et ramenées vers l'avant, la face ventrale tournée vers l'avant pour prendre l'air. Sous la vitesse minimale seulement, elles donnent des coups de freinage puissants vers l'avant (4/3 par seconde), avec une remontée repliée au coude et au poignet, et les bouts d'aile qui suivent en retard.
  - Pose parachute de `basic`, réglée par zorago dans Blockbench, sur le côté gauche, puis appliquée en miroir : bras de l'aile (−38,9 ; 47,6 ; −7,1) en keyframe Blockbench, avant-bras tourné de −20° et main de −12,5° autour de Y, épaule à `STROKE` −0,525 et `SWEEP` 0,704. Les autres paires partent de cette silhouette et l'adaptent à leur anatomie.
  - Repère d'axes quand le corps est debout : `STROKE` positif envoie l'aile vers le dos et négatif vers l'avant du corps ; `SWEEP` positif la lève. En vol couché, les mêmes axes donnent le haut et le bas (stroke), l'avant et l'arrière (sweep). Toujours raisonner dans le repère du corps de l'état concerné.

- **G2 — Traînée au bout des ailes au freinage** (née sur `basic`). Chaque classe de rig déclare ses os de bout d'aile dans `tipBones()` : l'os dont le cube va le plus loin, un par côté (pour `basic` : `right_primary_1`, `left_primary_1`). Le rendu repère chaque frame le coin du cube le plus éloigné du corps, et le tick y fait naître une traînée de nuage, continue entre deux ticks : 4 particules par bout d'aile et par tick en parachute, 1 sous la vitesse minimale. Sans `tipBones()`, la traînée retombe sur une estimation à 1,6 bloc de chaque côté.

- **G3 — Envol chargé : se gonfler, puis BLAST** (née sur `basic`). Pendant la charge (Alt maintenu au sol), les ailes se déploient complètement et se lèvent haut au-dessus du dos, en V, l'éventail grand ouvert, les bouts d'aile retroussés. Elles se « gonflent » avec un tremblement fin qui grandit avec la charge (au carré), et de la poussière part des pieds. À la fin de la charge : le **BLAST**, un coup d'aile unique et violent qui part de tout en haut et descend très bas (30 % du one-shot `WingMotion.launch`, 16 ticks), avec les bouts d'aile qui suivent en retard, puis un repli de récupération vers le vol. Il s'accompagne d'un anneau de nuages au sol, de bouffées sous le joueur et d'un battement de dragon. Le battement normal est effacé pendant le blast. Cette animation sert surtout aux **autres joueurs** : le début et la fin de la charge leur sont envoyés (`TakeoffCharge` → `TakeoffEvent`, un paquet à chaque bout), et chaque client détecte lui-même le départ (passage au vol de déplacement avec une charge ≥ 0,8).

- **G4 — Amplitude anatomique du battement** (née sur `basic`, sources : études XROMM de l'articulation de l'épaule des oiseaux). Le premier os, attaché au dos (humérus, `STROKE` de l'épaule), monte jusqu'à environ +50 à +80° au-dessus du plan du dos, mais ne descend jamais à plus d'environ −5° sous l'épaule : il ne passe pas sous les bras du joueur. Seuls le deuxième et le troisième os (avant-bras, main, puis plumes) vont plus bas, en se pliant vers le bas en fin de coup. Le battement est une vague qui part du corps vers le bout de l'aile : l'humérus mène, l'avant-bras et la main suivent en retard.
  - **Avant / arrière, comme à la nage** (précision de zorago) : l'aile remonte **vers l'avant**, repliée (retour de bras de brasse), puis frappe vers le bas **et vers l'arrière** pour pousser l'air derrière (cycle `SWEEP` : +0,3 en haut, −0,3 en bas). Vu de dessus, le bout d'aile avance pendant la remontée et recule pendant la frappe.
  - **Descente (coup moteur)** : aile tendue, balayée vers l'arrière, l'humérus descend à −5°, puis l'avant-bras (−35°) et la main (−40°) fouettent sous l'épaule en fin de coup.
  - **Remontée (récupération)** : l'aile se replie au poignet dès le début, l'humérus remonte en premier pendant que la main traîne encore en bas, puis la main se déroule vers le haut. En haut, l'avant-bras et la main finissent relevés (+15 à +20° chacun, bout d'aile vers +80°), au-dessus et un peu en arrière du dos.
  - Repères de `basic`, réglés par zorago dans Blockbench : en haut, bras de l'aile vers +50°, avant-bras +20°, main +17,5° ; en bas, bras vers −3°, avant-bras −35°, main −40°. Dans le code, ce sont les cycles `STROKE`, `BEND_ELBOW` et `BEND_WRIST`.
  - S'applique à **tout** ce qui bat : battement de croisière, stationnaire (l'humérus va de −5° à +47° et l'avant-bras donne la portée vers l'avant), décrochage, BLAST de l'envol (l'humérus s'arrête à −5° et le reste est fait par l'avant-bras et la main). Seules les poses fixes voulues par zorago peuvent amener l'aile devant, comme le parachute du frein, corps debout.

- **G5 — Le stationnaire pousse l'air vers le sol** (née sur `basic`). En stationnaire, le but est de lutter contre la gravité, pas d'avancer. Le corps est debout, alors l'aile tourne d'environ 80° autour de sa longueur (`TWIST` de l'état `HOVER`, `HOVER_PLANE`) : son plan devient presque horizontal, les plumes traînent vers l'arrière, et sa face du dessous regarde le sol. Le battement passe sur l'axe haut/bas (`SWEEP` : humérus d'environ −6° à +47°, toujours dans la limite de G4), avec un léger huit d'avant en arrière sur `STROKE`. Pendant la frappe vers le bas, la surface est presque à plat (normale à environ 20° de la verticale) et pousse l'air au sol. Pendant la remontée, l'aile se replie et se met de profil pour fendre l'air. La cambrure suit le mouvement. Les « un peu moins de 90° » voulus par zorago gardent une petite poussée vers l'arrière, qui stabilise.

- **G6 — Pas de particules en cubes dans le modèle** (née sur `fire`). Les braises, étincelles, poussières, etc. ne sont jamais des cubes 3D animés dans le rig (trop lourd, et ça fait ramer Blockbench quand ils apparaissent). Elles passent par les vraies particules Minecraft (`client/WingEffects`), réglées par paire.

- **G7 — Ornement qui traverse une articulation = peau** (née sur `fire`). Une pièce décorative ou une surface qui s'étend sur plusieurs os (crête, cœur lumineux, bordure, voile) n'est jamais une plaque rigide posée sur un seul os : elle passe sous les articulations et s'en détache dès que l'aile bouge. Elle devient une peau (M1), avec ses points accrochés à chaque os qu'elle longe. Si elle doit se replier avec des plumes, son bord libre s'accroche aux plumes elles-mêmes, pas à l'os. Chaque panneau peut avoir sa propre feuille de texture (`sheet` dans le panneau). Un point peut aussi porter ses coordonnées de texture explicites (`[os, x, y, z, u, v]`), pour les tissus dont la texture doit rester droite même si les os sont tournés au repos (voiles d'`aurora`). Une feuille peut être **animée** (née sur `water`, demande de zorago) : `frames`, `fps` et `step` dans `sheet`. Les images sont empilées dans la texture (comme les items animés de Minecraft, qui ne marchent pas sur une texture d'entité), et `Membrane` passe de l'une à l'autre avec l'horloge de rendu. Pour `water` : 10 images à 5 par seconde, une boucle de 2 s, avec la vague qui court sur l'eau. Le plugin Blockbench suit le temps de la timeline.

- **G8 — Les ailes partent des omoplates** (née sur `seraph`). Toute paire principale s'accroche sur le haut du dos, au niveau des omoplates, jamais sur la tête ni au-dessus. Une paire en plus va soit sur les omoplates aussi (juste sous la première), soit plus bas comme gouvernail (vers les hanches). Les idles au sol restent compacts sur le dos : pas d'aile dressée au-dessus de la tête ni enroulée autour des jambes.

- **G9 — Plusieurs paires : superposées au calme, écartées à l'effort** (née sur `seraph`, référence : le Stormcutter de Valka dans *Dragons 2*, dont la seconde paire se plaque sous la première ou s'ouvre en X). Les paires principales identiques se plaquent l'une sur l'autre pour n'en former qu'une au repos, au plané et en piqué. Elles s'écartent en X au stationnaire, en accélération (battement), à la charge, au BLAST et dans les idles (poids lissé `SPLIT`), et battent en phase. L'écartement respecte G4 : c'est la paire arrière (la plus éloignée du dos) qui monte, celle du bas ne descend presque pas (−0,08). Pour bien marquer « deux ailes » (retour de zorago), la paire arrière monte vers le haut de la tête au stationnaire (+0,62 en hauteur) et vers le haut du dos en accélération, en vol couché (+0,52 sur `STROKE`). Ailleurs, l'écart est de +0,32. Exception voulue au parachute du frein (pose fixe) : la paire arrière monte (+0,3) et celle du dessous descend (−0,35), pour augmenter la surface de freinage.

## Règles de famille

### Plume

- **P1 — Rangées de plumes sur le bras et l'avant-bras** (née sur `basic`). Pas de plaque unique sous les deux os du centre : au moins 4 plumes séparées par os (rémiges tertiaires sur le bras, secondaires sur l'avant-bras), posées à la suite le long de l'os et qui se chevauchent un peu. Chaque plume est un os à elle, attaché à sa racine sur l'os porteur : groupes `<côté>_tertial_feather_<k>` dans `<côté>_tertials`, `<côté>_secondary_feather_<k>` dans `<côté>_secondaries` et `<côté>_hand_feather_<k>` dans `<côté>_hand_feathers` (la main aussi : plus aucune plaque sous les os, seules les petites couvertures restent en plaque) (numérotés du corps vers le bout de l'aile). `FeatherWing` les détecte : au repli, chaque plume tourne autour de sa propre racine pour pendre sous l'os, et l'éventail se referme (plumes parallèles). Déployée, l'éventail s'ouvre selon l'angle de repos de chaque plume : les tertiaires penchent vers le corps, les secondaires vers le bout de l'aile. Profondeur : chaque plume de la rangée a son propre Z, décalé de 0,025, pour éviter le scintillement. Texture : une bande par plume (face ventrale puis face dorsale), deux tons alternés, rachis clair au centre, pointe sombre arrondie.

- **P1 dans la bibliothèque d'oiseau** : `art/wings/lib/bird.js` génère les rangées de P1 quand l'aile passe `rows: true` (tertiaires et secondaires : au moins 4, environ une plume pour 2 px d'os ; main : 4). Les plaques correspondantes disparaissent. Côté texture, chaque plume reprend le motif de la plaque d'origine avec les tons alternés (`alternate` dans `seraph.px.js`). Les oiseaux de la phase 2 n'ont qu'à activer l'option. Les plaques de couvertures du bras et de l'avant-bras (`coverts`) ont alors leur propre os (`arm_coverts`, `greater_coverts`) et se rentrent entre les plumes quand l'aile se replie (échelle en hauteur 1 − repli). Sinon, restées dépliées, elles dépassaient du repli (repéré par zorago sur `daedalus`, corrigé aussi sur `seraph`).

- **P2 — Plan de portance des plumes** (née sur `basic`). L'aile forme une seule surface qui emprisonne l'air dessous, sans plume qui casse ou en traverse une autre. Deux mécanismes dans `FeatherWing` :
  - **Torsion de raccord aux articulations** : quand l'avant-bras ou la main se plie hors du plan (`BEND_ELBOW`, `BEND_WRIST`), les plumes de la rangée tournent progressivement autour de leur tige. Les tertiaires près du coude prennent la moitié du pli de l'avant-bras. Les secondaires vont de −½ pli du coude (côté corps) à +½ pli du poignet (côté main). La plaque de main reprend −0,3 du pli du poignet.
  - **Rangée de la main, entre deux plans** (retour de zorago : les plumes de la main sortaient du plan). La main tourne sur elle-même (`HAND_TWIST`) et porte les primaires, ce que les secondaires ne font pas. Les plumes de la main ne suivent donc pas la main telle quelle. `FeatherWing.alignHandRow` calcule le plan de la dernière secondaire et celui de la dernière primaire, puis amène chaque plume de la main entre les deux, par la plus petite rotation (15 % du chemin vers les primaires pour la plume côté corps, 85 % pour celle côté bout). Résultat sur toutes les animations de `basic` : au plus 5° d'écart entre deux plumes voisines (contre 48° avant), et l'écart total entre secondaires et primaires (jusqu'à 30°) est réparti sur toute la rangée.
  - **Cambrure** (canal `RigPose.CAMBER`, rotation X des plumes et de la plaque de main) : en vol, le bord de fuite descend un peu sous l'aile (−0,15 rad), comme un profil d'aile creusé dessous. Pendant la frappe, l'air plaque les plumes vers le haut. Pendant la remontée, elles s'ouvrent vers le bas pour laisser passer l'air (décalage du cycle `STROKE` × 1,2). En parachute, l'air qui arrive de face les rabat vers le dos (+0,35, avec un tremblement). En stationnaire, la cambrure suit l'aller-retour.

### Membrane à doigts

Principe (zorago) : une membrane de peau reste un plan qui pousse l'air, comme les plumes. Toutes les règles générales s'appliquent (G1 à G5, `FlightRules` partagé), seule la surface change.

- **M1 — Membrane peau, sans trou** (née sur `bat`). La membrane n'est plus faite de plaques rigides : c'est une peau déformable. Chaque sommet est accroché à un os (son « père ») à sa position de repos, et suit cet os. Deux panneaux voisins partagent les mêmes points d'accroche, donc aucun trou ne peut s'ouvrir, quel que soit le pli. Définition par paire dans `rigs/<modèle>.membranes.json` (hors du `.bbmodel`, que Blockbench réécrit) :
  - `sheet` : la texture est une seule feuille de l'aile déployée, vue de face, à 1 px par unité. `x`, `y` donnent le coin haut-gauche dans le repère du modèle. `ventral` et `dorsal` donnent la position des deux faces dans la texture. Chaque sommet prend la couleur de l'endroit où il se trouve au repos, donc la texture ne dépend pas du découpage.
  - `panels` : chaque panneau relie deux rails de même longueur (`a`, `b`), points `[os, x, y, z]` du côté droit en coordonnées Blockbench au repos (`root` = le corps). Le côté gauche est construit en miroir. `rows` découpe le panneau en largeur pour qu'il puisse se bomber.
  - Pour `bat` : plagiopatagium (flanc, de l'épaule à la hanche, jusqu'au bras, l'avant-bras et le doigt 4), trois dactylopatagiums (entre les doigts 1-2, 2-3 et 3-4), propatagium (devant le bras, de l'épaule au poignet).
  - Rendu : `render/rig/Membrane`, quads émis dans le même `RenderType` vanilla que les os (compatible Iris), deux couches à 0,01 px d'écart, l'une avec la face dorsale, l'autre avec la face ventrale.
  - Blockbench : le plugin `tools/blockbench/simplywings_membranes.js` dessine la même peau dans la scène et la fait suivre les os pendant l'animation (il lit `art/scenes/<modèle>.membranes.json`, copié par `scene.py`).
- **M1 pour les Furies** : `art/wings/lib/fury.js` génère la peau quand le rig passe `skin: true`. Les plaques de membrane disparaissent, `FURY_SKIN` calcule les panneaux à partir des os au repos (plagiopatagium jusqu'au flanc, un dactylopatagium entre deux doigts, propatagium) pour la grande paire et la paire arrière, et `fury_px.js` peint une feuille par paire. Le JSON s'écrit avec `node tools/blockbench/rig_skin.js <modèle>`.

- **M2 — Membrane bombée** (née sur `bat`). Une membrane plate fait « plaque ». En vol, elle se bombe vers le dos, de 12 % de sa largeur au maximum, comme une voile gonflée par l'air qui la porte : la face du dessous est creusée et emprisonne l'air. Au plané, elle est légèrement courbée (`GLIDE_BILLOW` 0,5). Elle se gonfle plus à la frappe, se creuse dans l'autre sens à la remontée et se gonfle fort en parachute (même source que la cambrure des plumes, `RigPose.CAMBER`). Repliée ou au sol, elle est à plat. La valeur passe par `Membrane.billow`, que la scène reçoit sur le groupe `scene_membrane`.
- **M3 — Doigts en éventail** (née sur `bat`). Le battement ouvre et ferme l'éventail des doigts : ils se resserrent à la remontée pour réduire la surface (cycle `FAN` négatif), puis s'ouvrent en grand à la frappe. Les règles d'amplitude (G4) s'appliquent au bras, l'avant-bras et la main exactement comme pour l'oiseau.

- **M4 — Dos de créature** (née sur `night`, pour les Furies et toute paire qui « devient » une créature : queue, gouvernails, épines). Une plaque dorsale à écailles, légèrement bombée (trois couches en escalier : base, arête, quille), couvre le dos du joueur et le suit. Elle cache le skin entre les ailes et fait la jonction entre les ailes, les gouvernails des hanches et la queue. Dans `fury.js` : option `backPlate: true`, peinte par `plate` dans `fury_px.js` (rangées d'écailles décalées, couleurs `skin*` de la paire).

### Planeur (membrane sans doigts)

- **S1 — Le planeur vole avec les bras du joueur** (née sur `squirrel`, demande de zorago : « une vraie vibe d'écureuil volant »). En vol, les bras du joueur s'écartent (croix au plané, vers l'avant au frein, pompage au battement, bras intérieur replié en virage, rentrés en piqué) et les jambes s'écartent un peu. Le squelette est réduit au strict nécessaire : **un seul os par aile**, qui recopie la pose réelle du bras du joueur (`WingRig.playerArms`, ramenée dans le repère du torse), et une seule peau tendue de l'arête intérieure du bras, jusqu'à la main, au flanc jusqu'au bas du torse (`rigs/squirrel.membranes.json`, UV explicites dessinées bras à l'horizontale). La peau est dans le plan médian du corps (z = 0), qui passe par l'axe de l'épaule, et son coin du haut est fixé au torse : l'aisselle ne bouge pas quand le bras balance, tourne ou se lève, donc le tissu ne se déchire jamais. Pour ça, une classe de rig peut poser le corps du joueur (`WingRig.poseBody`, appelé par `FlightPose` après la pose de vol, dans tous les modes). L'aile est donc collée à la main **en permanence**, exactement, au sol aussi : la marche, la frappe et le minage tendent la peau ; bras le long du corps, elle disparaît contre le flanc. À la charge, les bras se lèvent et s'écartent en tremblant. Pas de coude ni d'IK : le pli suit le bras humain.
- **Queue :** elle part du bas du dos, collée au joueur (pivot sur la surface du dos, pas de décalage), et ne doit jamais passer sous les pieds. En vol, elle suit les jambes, à peine relevée (base 0,08 rad, bout 0,12 rad) ; elle ne se relève franchement qu'au frein et à l'atterrissage.

### Insecte

Les ailes d'insecte sont des plaques rigides montées sur une charnière, sans coude ni poignet. Les règles générales s'y appliquent en version « charnière » (nées sur `bee`, validées par zorago sans modification).

- **I1 — Vrombissement dans les limites de G4.** Le battement oscille autour d'un centre placé pour que le point le plus bas reste à −5° sous l'épaule (`centre = −5° + 1,15 × amplitude`). L'amplitude dépend de l'état : plané 0,45, stationnaire 0,55, frein lent 0,25. Les plans fantômes (`ghost`) donnent le flou et suivent la même limite.
- **I2 — Stationnaire d'insecte (A18).** Comme G5, le plan des ailes pivote d'environ 80° pour devenir presque horizontal. Mais le battement reste d'avant en arrière, comme chez les vrais insectes et les colibris, avec une inversion de l'incidence à chaque demi-coup (`TWIST` ± 0,55 × cos de la phase). C'est elle qui pousse l'air vers le sol.
- **I3 — Parachute ailes fixes.** Au frein au-dessus de la vitesse minimale, plus de vrombissement : les deux paires sont figées, déployées, levées et inclinées comme le parachute de `basic` (épaule + bras réunis : `STROKE` 0,31, levée 0,85, incidence 0,48), avec un tremblement fin. Les fantômes disparaissent. Sous la vitesse minimale, le vrombissement revient, derrière les bras.
- **I5 — Élytres (A6)** (née sur `beetle`). Deux temps, toujours dans cet ordre : les élytres s'ouvrent (poids `OPEN`), puis l'aile se déplie de dessous (`UNFOLD`, qui suit l'ouverture avec du retard). Au repli, c'est l'inverse : l'aile rentre d'abord, puis les coques se referment. À la charge, les élytres s'ouvrent dès le début pour libérer les ailes. Au parachute, les élytres se lèvent et s'écartent davantage pour servir d'aérofreins, avec un léger tremblement, pendant que les ailes restent figées (I3). Au stationnaire, le plan des ailes est presque horizontal à hauteur d'épaules (I2). La coque recouvre l'aile, donc pour ne pas la gêner elle passe **au-dessus** : elle se lève haut et s'ouvre vers l'extérieur (pose réglée par zorago dans Blockbench sur `left_shell`, mise en miroir : levée 1,9 rad, ouverture 0,70, écart 0,17).

- **I4 — Envol : le vrombissement monte, puis rafale.** Pendant la charge, les ailes se déplient et se lèvent, et le vrombissement grandit avec la charge (au carré). Le BLAST devient une rafale : amplitude maximale d'un coup, puis décroissance sur le one-shot `launch`.

### Objet / machine

- **O1 — Une machine à jetpack : les ailes ne servent qu'à tourner et freiner** (née sur `daedalus`, référence : les ailes EXO-7 du Faucon de Marvel ; règle réécrite après le premier passage). La propulsion est entièrement au jetpack : accélération, envol (charge puis rafale), stationnaire. Les ailes restent repliées au sol, à la charge, au saut, à l'atterrissage, au stationnaire et en repli faucon, écartées en V pour dégager les réacteurs. Elles ne se déploient qu'en vol de déplacement, figées en plané, et ne bougent que pour tourner (roulis, incidence différentielle gauche/droite et flèche, comme un gouvernail) et pour freiner (pose parachute de G1). Aucun battement, aucun frémissement de vol. Traînées au bout des lames.
- **O2 — Lames plutôt que plaques** (née sur `daedalus`). Une aile mécanique n'est pas faite de panneaux disjoints qui laissent des trous. Elle est faite de lames de métal qui se chevauchent comme des plumes, en rangées régulières et harmonieuses (squelette d'oiseau `bird.js` avec `rows: true`, lames en acier à bord de laiton), et se termine par de longues lames tranchantes et pointues en éventail.

### Élémentaire / magique

Aucune pour l'instant.

## Journal des passes

### 1. Ailes communes (`basic`, famille plume)

Statut : **terminé** (25 octobre 2026). Os, animations et règles figés par zorago. Reste à faire : la vérification en jeu (traînées du frein, BLAST vu par un autre joueur sur le serveur dédié).

Modifications :

1. Les plaques uniques du bras (tertiaires) et de l'avant-bras (secondaires) se déformaient mal au repli. Elles sont remplacées par 4 tertiaires (longueurs 8, 9, 9, 10 ; inclinaison −12° à 0°) et 5 secondaires (longueurs 11 à 12 ; inclinaison 2° à 14°), une plume par os, qui se replient sous l'os.

2. Frein (retour de zorago : « écrasé vers l'avant, comme si les ailes étaient plus puissantes que lui ») : le corps se redresse en posture d'atterrissage (penché de 8° en arrière au lieu de 50° vers l'avant), les jambes passent dessous, les bras s'écartent, la tête regarde devant. Les ailes sont levées et vers l'avant, avec des coups de freinage puissants (`CommonRig`, `BRAKE_BEAT`).

3. Frein en deux temps (retour de zorago) : parachute fixe au-dessus de la vitesse minimale, avec la pose qu'il a réglée (bras de l'aile gauche, mise en miroir), un tremblement des bouts d'aile et une traînée aux deux extrémités. Les coups de freinage battus ne restent que sous la vitesse minimale, jusqu'au stationnaire. Nouvelles animations dans la scène : `brake_slow` et `brake_to_hover`.
4. À côté, pour toutes les paires : vitesse maximale atteignable par gravité doublée (`terminalVelocity` 110 → 220 m/s, `diveDragSpeed` 60 → 120 m/s), sans toucher aux vitesses max des ailes, pour que le joueur puisse se mettre en danger et avoir besoin d'un freinage d'urgence violent.

5. Frein lent (sous la vitesse minimale) : les ailes battent derrière les bras (`STROKE` de base +0,25 au lieu de −0,35, amplitude 0,25), et les bras du joueur redescendent en pose normale (`FlightPose`, poids `brakeBeat`), pour une transition douce vers le stationnaire.

6. Envol chargé (retour de zorago) : déploiement et gonflement tremblant pendant la charge, puis BLAST au départ (voir G3). La charge est maintenant visible des autres joueurs. Animation de scène `takeoff` (remplace `takeoff_charge`), et `full_flight` passe par le blast.

7. Battement (retour de zorago : les ailes descendaient trop bas et passaient sous les bras) : nouveau cycle anatomique (G4). L'humérus va de −5° à +53° au lieu de −43° à +60°, et l'avant-bras et la main fouettent sous l'épaule en fin de coup et se relèvent en haut. Même limite pour le stationnaire, le décrochage et le BLAST.

8. Retour de zorago (« à 80 % parfait ») : remontée vers l'avant et frappe vers l'arrière, comme à la nage (G4). Plan de portance des plumes : torsion de raccord au coude et au poignet, et cambrure dynamique (P2).

9. Plaque de la main remplacée par 4 plumes (longueurs 12, 12, 11, 11 ; inclinaison 14° à 16°), avec la même torsion de raccord et la même cambrure (P1, P2). La rangée de plumes est maintenant continue du corps jusqu'aux primaires.

10. Plumes de la main hors du plan (repérées par zorago dans `full_flight`) : alignement automatique entre le plan des secondaires et celui des primaires (P2).

11. Stationnaire (retour de zorago : l'air partait vers l'avant) : même animation, mais le plan de l'aile pivote de 80° et le battement passe de haut en bas, pour pousser l'air au sol (G5).

Règles créées : P1, P2, G1, G2, G3, G4, G5 (G6 et G7 viennent de `fire`).

### 2. Ailes de chauve-souris (`bat`, famille membrane à doigts)

Statut : **terminé** (25 octobre 2026). Validé par zorago du premier coup (« rien à redire »), toutes les animations comprises. La méthode de la famille membrane à doigts est validée (M1 à M3 + `FlightRules`). Reste à vérifier en jeu : le rendu de la peau dans Minecraft (position, éclairage, festons en transparence).

Modifications :

1. Règles générales G1 à G5 sorties de `CommonRig` dans un outil partagé, `render/rig/FlightRules` (sans aucun changement pour `basic`, vérifié : écart 0 sur toutes les frames), et appliquées à `bat` : parachute et frein lent, BLAST, amplitude anatomique, nage avant/arrière, stationnaire qui pousse l'air au sol, traînées au bout du doigt 1.
2. Membranes en peau déformable (M1) à la place des 20 plaques. Texture refaite en une seule feuille par face (`bat.px.js`, fonction `sheet`).
3. Membrane bombée en vol (M2) et éventail des doigts au battement (M3).

Règles créées : M1, M2, M3.

### 3. Ailes de feu (`fire`, famille plume, aspects A8 et A10)

Statut : **terminé** (25 octobre 2026), validé par zorago après le second passage. Reste à vérifier en jeu : le cœur et la crête en peau en émissif.

Règles appliquées avant revue :

- G1 à G5 : via `FlightRules`, avec le cycle anatomique adapté au battement sec du feu (montée 0,45, frappe très courte 0,12). Parachute, frein lent, BLAST, stationnaire qui pousse l'air au sol, traînées au bout de la flamme `primary_2`.
- P1 : les langues de feu sont des plumes. 4 tertiaires (une ajoutée), 4 secondaires et 4 nouvelles flammes sur la main, chacune un os (`tertial_feather_k`, `secondary_feather_k`, `hand_feather_k`), qui se replient une à une sous l'os. Elles gardent leur vacillement et leur allongement propres (`FireRig.flame`).
- P2 : torsion de raccord, cambrure et alignement de la rangée de la main, repris par `FeatherWing`.
- M1 appliquée hors de la famille membrane : le cœur blanc-jaune sous les flammes était fait de trois plaques rigides (bras, avant-bras, main), qui s'ouvraient aux articulations. C'est maintenant une peau continue accrochée aux trois os (`rigs/fire.membranes.json`, feuille de texture peinte par `coreSheet` dans `fire.px.js`), légèrement bombée en vol.

Modifications après le premier passage de zorago (« 70 % correct ») :

1. La crête de petites flammes du bord d'attaque était une plaque posée sur la main et passait en travers du coude. Elle sortait au-dessus des épaules une fois l'aile repliée. C'est maintenant une peau accrochée au bras, à l'avant-bras et à la main (G7, feuille `CREST`).
2. Le cœur en peau faisait de grands panneaux jaunes une fois replié, parce que son bas était accroché aux os. Son bord bas est maintenant accroché aux flammes (première et dernière de chaque os, à 7 px de la racine), donc il se replie avec elles et vacille avec elles (G7).
3. Braises en cubes 3D supprimées (G6). Les flammes et étincelles restent en vraies particules.

Ce qui reste propre au feu : vacillement de chaque langue, embrasement au coup (chaleur), flammes qui traînent avec la vitesse, 3 idles (embrasement, braises qui couvent, secousse d'étincelles), émissif.

### 4. Ailes d'abeille (`bee`, famille insecte)

Statut : **terminé** (25 octobre 2026), validé par zorago sans modification. Note de zorago : les insectes plus complexes (ancient, beetle, moth, psyche) auront peut-être des idles à revoir.

Règles appliquées avant revue : G1 (I3), G2 (traînée au bout de l'aile avant), G3 (I4), G4 (I1), G5 (I2), G6 (rien à faire), G7 (sans objet : pas d'ornement à cheval sur des os).

### 5. Ailes de séraphin (`seraph`, famille plume, aspect A7)

Statut : **terminé** (25 octobre 2026). Validé par zorago (« superbe taf ») après une dernière retouche : la paire arrière monte plus haut à l'effort, vers la tête au stationnaire et vers le haut du dos en accélération (G9). Reste à vérifier en jeu.

Retour de zorago sur le premier passage : vol, battement et frein corrects, mais les ailes étaient attachées sur la tête, et les idles prenaient beaucoup trop de place. Refonte (G8, G9) :

1. Deux grandes paires identiques, plus grandes d'environ 25 % (bras 11, avant-bras 15, main 6, primaires jusqu'à 27), accrochées sur les omoplates, la seconde juste sous la première (1,6 px plus bas, 0,35 plus près du dos). Elles se superposent au calme et s'écartent en X à l'effort (G9).
2. Une petite paire de gouvernails près des hanches (comme les ailerons de queue de Krokmou), en plaques : repliés au repos, déployés en vol, ils s'inclinent à l'opposé du virage et frémissent quand les grandes paires battent.
3. Trois idles compacts : les deux paires s'écartent puis se rejoignent, les gouvernails s'agitent, les paires ondulent l'une contre l'autre.
4. Idée notée pour la suite, pas faite : les séraphins de l'iconographie sont souvent couverts d'yeux.

Premier passage, conservé pour l'historique :

Règles appliquées avant revue :

- P1 sur les trois paires (`rows: true`) : 15 plumes par aile pour la paire centrale, 13 pour la haute, 12 pour la basse. P2 repris par `FeatherWing`.
- G1 à G5 via `FlightRules`, pour chaque paire. Coordination des trois paires (A7), proposée :
  - vague décalée : la paire haute bat un peu avant, la basse un peu après (même décalage pour le stationnaire), et le BLAST de l'envol part en cascade de haut en bas ;
  - en éventail : la paire haute est plus levée et la basse plus basse, en parachute (×1,4) comme à la charge (×1,6). Au plané, les paires s'étagent de la tête aux pieds ;
  - les 5 idles au sol de seraph sont conservés.

### 6. Furie nocturne (`night`, membrane à doigts, aspects A7, A9, A16)

Statut : **terminé** (25 octobre 2026), validé par zorago après l'ajout de la plaque dorsale. Reste à vérifier en jeu : peaux, épines émissives.

Règles appliquées avant revue :

- M1 à M3 sur la grande paire et la paire arrière : peau sans trou (18 panneaux), bombée en vol, doigts en éventail au battement.
- G1 à G5 via `FlightRules` sur la grande paire (parachute, frein lent, BLAST, amplitude anatomique, nage, stationnaire au sol, traînées au bout du doigt 1). La paire arrière suit la grande avec un léger retard, et s'abaisse en parachute pour ajouter de la surface.
- G8 : la grande paire est sur les omoplates, la petite paire arrière plus bas, comme les gouvernails de seraph.
- Retour de zorago (animations validées) : il manquait un dos complet, le skin du joueur ressortait entre les ailes et les épines. Ajout de la plaque dorsale à écailles (M4), aussi sur `light`.
- Conservé tel quel : la queue (`DragonTail`) et ses ailerons, les épines et leur charge de plasma, la prothèse rouge, les 8 idles et les pouvoirs des Furies.

### 7. Ailes de scarabée (`beetle`, famille insecte, aspect A6)

Statut : **terminé** (25 octobre 2026), validé par zorago.

Règles appliquées avant revue : I1 à I4 (battement au-dessus de −5°, stationnaire à plat avec inversion d'incidence, parachute figé, charge et rafale), G2 (traînée au bout de l'aile), G8 (élytres et ailes sur les omoplates), I5 (élytres en deux temps, aérofreins au parachute).

Retour de zorago : tout bon sauf le stationnaire, où les deux carapaces traversaient les ailes. Premier essai (élytres rabattues le long du dos) refusé. Corrigé avec la pose de zorago : au stationnaire, les élytres montent au-dessus des ailes (I5).

### 8. Ailes de papillon de nuit (`moth`, famille insecte, aspect A5)

Statut : **terminé** (25 octobre 2026), validé par zorago sans modification.

Règles appliquées avant revue (version lente des règles insecte, sans vrombissement ni fantômes) :

- I1 / G4 : battement ample mais jamais sous −5° (y compris l'inclinaison en virage, bornée par côté), remontée vers l'avant et frappe vers l'arrière ; en haut du coup, les ailes se rapprochent comme le « clap » des vrais papillons. L'aile postérieure suit l'antérieure avec un léger retard (elles restent accrochées).
- I2 / G5 : stationnaire à plat, battement de haut en bas, inversion d'incidence et léger huit, la postérieure en retard.
- I3 / G1 : parachute figé (même pose que `basic`, postérieure un peu plus basse), puis frein lent derrière les bras.
- I4 / G3 : le frisson d'échauffement existant grandit pendant la charge, puis BLAST unique (une paire lente ne vrombit pas).
- G2 : traînées au bout des ailes antérieures. Conservé : les petites irrégularités du vol de papillon de nuit.

### 9. Ailes de Dédale (`daedalus`, famille machine, aspects A12, A13)

Statut : **terminé** (25 octobre 2026), validé par zorago après la correction des couvertures.

Retour de zorago sur le premier passage : beaucoup de problèmes. Refonte complète sur le modèle des ailes du Faucon (Marvel) :

1. Design : les longerons, panneaux d'acier, entretoises, aileron, engrenages et les trois plumes d'Icare sont supprimés. Les ailes sont maintenant des rangées de lames d'acier à bord de laiton qui se chevauchent (4 sur le bras, 5 sur l'avant-bras, 4 sur la main), plus 7 longues lames tranchantes en éventail au bout (O2). Elles sortent des côtés d'un sac dorsal à deux réacteurs centraux.
2. Animation (O1) : tout est fait au jetpack (vibration et flamme qui montent à la charge, rafale à l'envol, poussée vers le sol au stationnaire, flamme orientée avec la montée et le virage). Les ailes restent repliées en V sauf en vol de déplacement, où elles planent figées et ne bougent que pour tourner et freiner.
3. Idles : ratés de réacteur, déploiement de contrôle des ailes, balayage des tuyères.

### 10. Ailes d'aurore (`aurora`, matière non squelettique, aspects A11, A15)

Statut : **terminé** (25 octobre 2026), validé par zorago sans modification.

Règles appliquées avant revue :

- G7 : les rideaux étaient des pans rigides, un par segment de rail, qui s'ouvraient quand le rail ondule. Chaque rideau est maintenant un seul tissu continu : le haut est accroché aux rails, le bas aux pans qui ondulent (`rigs/aurora.membranes.json`, généré par `aurora.rig.js`). Il se gonfle en vol, beaucoup en parachute (une voile de lumière qui freine), et suit le battement.
- G1 à G5 : parachute (rideaux levés vers l'avant, la paire arrière plus basse), frein lent ; charge où les voiles se dressent et respirent en tremblant, puis BLAST ; battement anatomique lent au-dessus de −5° avec nage avant/arrière ; stationnaire à plat, battement de haut en bas ; traînées au bout du rail.
- Conservé : ondulation le long des rails, lumière qui respire, 3 idles (couronne, vague, plissé).
- Outil : le plugin Blockbench laisse passer la transparence (seuil alpha 0,02 au lieu de 0,5), sinon le bas violet des voiles disparaissait.

### 11. Ailes d'eau (`water`, matière non squelettique, aspect A11)

Statut : **terminé** (25 octobre 2026), validé par zorago.

Règles appliquées avant revue :

- G7 : l'aile était faite de 5 plaques d'eau rigides, une par segment, qui s'ouvraient quand la vague courbe les segments. C'est maintenant une seule nappe d'eau continue, accrochée à la crête et au bas de chaque segment (`rigs/water.membranes.json`, généré par `water.rig.js`), peinte d'un seul tenant (`water.px.js`). Elle se bombe en vol et suit le fouet du battement. Repliée, la cascade et sa volute restent d'un seul tenant.
- G6 : les gouttes en cubes 3D sont supprimées (les vraies particules d'eau existent déjà dans `WingEffects`).
- G1 à G5 : parachute, frein lent, charge où l'aile se lève et ondule de plus en plus, puis BLAST ; battement anatomique au-dessus de −5° avec nage avant/arrière, le fouet segment par segment conservé ; stationnaire à plat, nage de haut en bas comme une raie ; traînées au bout du dernier segment.
- Conservé : la vague permanente de l'épaule à la pointe, les 3 idles (vague dans la cascade, verse, déploiement en onde), l'éclaboussure à l'atterrissage.

Retour de zorago (le style liquide marche) :

1. Repliée, la cascade remontait en volute vers la tête : l'eau coule maintenant vers les pieds, avec juste une légère courbe vers l'extérieur.
2. Texture animée : 10 images, 5 par seconde, la vague et la houle courent sur la nappe (feuille animée, voir M1 / G7).
3. Repliées, les deux ailes se superposent légèrement : l'aile gauche passe sur la droite (épaule gauche tournée de 12,5°, pose réglée par zorago dans Blockbench sur `idle_ground`, appliquée à toutes les poses repliées).
4. Le coin bas de l'aile côté dos (la « base de la lame ») est arrondi sur 4 px, pour ne plus avoir un bord coupé une fois refermée. La ligne droite contre le dos est gardée (plus de 10 px).

### 12. Ailes de forêt (`forest`, matière non squelettique, aspects A11, A8, A7)

Statut : **terminé** (25 octobre 2026), validé par zorago sans modification.

Règles appliquées avant revue :

- Pas de peau : les feuilles et les fleurs ont chacune leur os et suivent leur branche, comme des plumes. Le feuillage peut laisser passer le jour, c'est naturel.
- G1 à G5 sur les branches : parachute (branche principale levée vers l'avant, branche arrière abaissée pour ajouter de la surface, comme `seraph` et `night`) ; frein lent ; charge où les branches se lèvent et les feuilles frémissent de plus en plus, puis BLAST ; battement anatomique au-dessus de −5° (virages compris, côté par côté) avec nage avant/arrière, le fouet des segments conservé ; stationnaire à plat, battement de haut en bas ; traînées au bout de la branche.
- Conservé : le frémissement de chaque feuille avec la vitesse, les fleurs, les 3 idles (floraison, bruissement, étirement vers le soleil).

### 13. Ailes de polatouche (`squirrel`, membrane sans doigts / planeur, aspects A3, A9)

Statut : **terminé**, validé par zorago.

Règles appliquées avant revue :

- M1 en version planeur : les deux plaques rigides (bras et avant-bras), qui s'écartaient au coude, deviennent une seule peau tendue du flanc (de l'épaule à la cheville) jusqu'au bras, à l'avant-bras et au bout de l'éperon (`rigs/squirrel.membranes.json`). Le liseré crème et la bande sombre du bord sont repeints sur une feuille d'un seul tenant. Bombée au plané, beaucoup en parachute, à plat repliée.
- G1 à G5 : parachute, frein lent ; charge et BLAST discrets (le polatouche ne fait que pomper des bras) ; pompage au-dessus de −5° (virages compris) avec une petite nage avant/arrière ; stationnaire à plat ; traînées au bout des éperons.
- Conservé : le bras intérieur qui se replie en virage, les éperons qui sortent au plané, la queue gouvernail et relevée à l'atterrissage.

Retour de zorago :

1. La queue passait sous les pieds en vol : elle reste maintenant relevée vers l'arrière.
2. Animation spéciale : le bout de l'éperon est collé à la main du joueur, et les ailes suivent les bras, qui s'écartent comme un vrai écureuil volant (S1).
3. Le bout de l'aile doit rester accroché à la main **tout le temps**, même au sol quand on frappe : collage sur la pose réelle du bras, dans tous les modes, et pose de charge bras levés. Erreur mesurée ≤ 1 px sur toutes les animations.
4. Le coude de l'aile pliait à l'envers d'un coude humain (correction de zorago sur `left_forearm`) : biais du coude miroir et contrainte de signe dans l'IK, vérifié des deux côtés.
5. Squelette simplifié à la demande de zorago : un seul os par aile qui suit le bras du joueur, et le tissu du bout du bras au bas du torse. L'IK, l'avant-bras et l'éperon disparaissent.
6. Le point d'attache du haut du torse ne suivait pas le joueur et déchirait le tissu : le plugin de scène ancrait le torse sur `right_shoulder` (absent du nouveau squelette), il prend maintenant `scene_roll`. En plus, la peau passe dans le plan de l'axe de l'épaule et son coin du haut est fixé au torse.
7. L'ancienne membrane restait affichée (deux instances du plugin après un rechargement) : le plugin efface maintenant toute membrane déjà présente avant d'en construire une. La queue était trop haute et décollée du joueur : elle part du bas du dos, collée, et ne se relève presque plus en vol.

### 14. Ailes Yin et Yang (`yin_yang`, plume, aspect A14)

Statut : **terminé**, validé par zorago.

Règles appliquées avant revue :

- P1 : rangées de plumes (`rows: true`) sur le bras, l'avant-bras et la main, couvertures sur leurs propres os. Peintes dans les deux palettes (Yin bleu nuit, Yang nacré), tons alternés. Le bord de fuite en S des secondaires est gardé, porté par la longueur de chaque plume.
- G1 à G5 et P2 via `FlightRules` : parachute et bras de parachute, charge et BLAST, battement anatomique (l'ancien cycle descendait à −43°), stationnaire à plat (`HOVER_PLANE`), traînées au bout des primaires (`tipBones`).
- Conservé : le médaillon taiji qui tourne (plus lentement en stationnaire, il grossit un peu quand on bat sur place) et les 5 idles (balance, cercle, respiration, échange, méditation).

Asymétrie (A14), proposée à zorago :

- Le Yin suit le Yang : l'aile gauche bat avec un léger retard (6 % du cycle, 0,35 rad en stationnaire), comme les deux moitiés du taiji qui se poursuivent.
- Équilibre au plané : les bouts d'aile ondulent lentement en opposition (l'un monte quand l'autre descend), comme une balance.
- Au repos et à l'atterrissage, les deux ailes respirent en décalé.

Retour de zorago :

1. Tout est validé. Les deux points du taiji passent de 2 px à 1 px (le pixel collé à la bordure est retiré).
2. Les « yeux » des ailes (cercle blanc sur le Yin, noir sur le Yang), qui formaient un yin-yang vu de dos, étaient à moitié cachés sous les rangées de plumes. Ils passent sur les grandes couvertures de l'avant-bras, plus haut sur la plaque, et les couvertures remontent au-dessus des plumes (comme sur un vrai oiseau) : le cercle entier se voit de dos en vol.

### 15. Ailes de colibri (`hummingbird`, plume + vrombissement, aspects A4, A17, A18)

Statut : **terminé**, validé par zorago sans modification.

Règles appliquées avant revue (aile de plumes, mais règles insecte pour le vrombissement) :

- I1 / G4 : le vrombissement oscille autour d'un centre qui garde le point le plus bas à −5° sous l'épaule (l'ancien descendait à environ −45°). Amplitude : plané 0,45, stationnaire 0,6, frein lent 0,3. Les deux fantômes translucides suivent la même limite.
- I2 / A18 : stationnaire à plat (`HOVER_PLANE`) à hauteur d'épaules, battement d'avant en arrière avec inversion d'incidence à chaque demi-coup, et un huit horizontal plus marqué qu'en vol (balayage haut/bas au double de la fréquence).
- I3 / G1 : parachute figé avec la pose de `basic` (aile et bras de parachute), plus de vrombissement ni de fantômes, tremblement des bouts d'aile ; sous la vitesse minimale, le vrombissement revient derrière les bras.
- I4 / G3 : à la charge, l'aile se déplie et se lève, le vrombissement grandit (au carré), puis rafale à l'envol.
- G2 : traînées au bout de `primary_1`.
- P1 sans objet : le bras (2 px) et l'avant-bras (3 px) sont trop courts pour porter des rangées de plumes ; l'aile du colibri est presque toute en main, et ses primaires sont déjà des plumes séparées.
- Conservé : coude et poignet verrouillés, battement visuel sur l'horloge de rendu (~10 Hz apparents), courte rafale de bourdonnement au repos.

### 16. Ailes d'aigle (`eagle`, plume, aspect A17)

Statut : en cours, scène prête pour le jugement de zorago sur `full_flight`.

Règles appliquées avant revue :

- P1 : rangées de plumes (`rows: true`), couvertures sur leurs propres os, tons alternés (texture passée à 128×128 pour la place). P2 repris par `FeatherWing`.
- G1 à G5 via `FlightRules` : parachute et bras de parachute, frein lent, charge et BLAST, battement anatomique (l'ancien cycle descendait à −60°), stationnaire à plat, traînées au bout de `primary_1`. Le cycle suit le rythme physique de la paire (montée 0,45, frappe 0,22) : remontée lente avec la pause en haut, puis coup de descente sec.

Signature d'espèce (A17), conservée et adaptée :

- Doigts écartés : les 6 rémiges échancrées gardent un fort relèvement (courbe des pointes 1,35 → 0,75), relevés en plané (+0,5) et frémissants au vent ; les 3 dernières restent plates.
- Alule levée au frein lent, à l'atterrissage, au décrochage et en stationnaire.
- Doigts qui vibrent à l'atterrissage et au frein lent (pas en parachute, figé par G1).
- Idle : grand étirement des deux ailes au sol, moins haut qu'avant pour rester sur le dos (G8).

## Plan de review

Deux phases. **Phase 1** : une paire dominante par aspect, choisie pour ouvrir le plus de logique nouvelle, revue une à une avec zorago, jusqu'à ce que chaque aspect ait ses règles. **Phase 2** : toutes les autres paires, une à une aussi. On applique d'abord toutes les règles de leurs familles et de leurs aspects, puis on ne polit que ce qui leur est propre.

### Phase 1 : paires dominantes

| # | Paire | Aspects ouverts | Pourquoi elle | Prépare |
| --- | --- | --- | --- | --- |
| 1 | common (`basic`) | A1 | Base de la famille plume. **Terminé.** | toute la famille plume |
| 2 | bat | A2 | Anatomie la plus éloignée de l'oiseau : doigts, peau qui se tend et se plisse. **Terminé**, méthode membrane validée. | demon, ptero, night, light, phantom |
| 3 | fire | A8, A10 (émissif) | Plume + pièces attachées + émissif. **Terminé** (G6, G7). | earth, nebula, shadow, storm, amethyst, copper, angel, peacock, psyche |
| 4 | bee | A4, A7, A13, A18, A10 (translucide) | Vrombissement, flou par plans fantômes, deux paires couplées, poussée continue, stationnaire d'insecte. **Terminé** (I1 à I4). | ancient, beetle, hummingbird |
| 5 | seraph | A7 (plumes) | Plusieurs paires à coordonner. **Terminé**, refondu en deux grandes paires superposées et des gouvernails (G8, G9). | angel, et les ailes arrière de night, light, air, aurora, forest |
| 6 | night | A9, A16 | Aile de dragon (`DragonWing`), paire arrière, queue (`DragonTail`), pouvoirs des Furies. | light, et les queues de peacock, kite, squirrel, psyche |
| 7 | beetle | A6 | Élytres qui s'ouvrent, puis aile qui se déplie : séquence en deux temps à l'envol et au repli. | — |
| 8 | moth | A5 | Insecte lent : ailes avant et arrière couplées, sans articulation, battement souple. | psyche |
| 9 | daedalus | A12, A13 | Machine : engrenages, longerons, propulseurs orientables, flammes, poussée continue. | ornithopter, kite, copper |
| 10 | aurora | A11 (tissu), A15 | Voiles sans os qui ondulent et retombent, battement lent. | air, psyche |
| 11 | water | A11 (fluide) | Segments et gouttes : une aile qui coule plutôt qu'elle ne se plie. | — |
| 12 | forest | A11 (végétal), A8 | Branches rigides, feuilles et fleurs qui réagissent au vent, paire arrière. | — |
| 13 | squirrel | A3, A9 | Planeur : membrane sans doigts tendue entre les membres, queue de stabilisation. | flying_fish, phantom, kite |
| 14 | yin_yang | A14 | Asymétrie voulue : deux ailes qui ne font pas la même chose, avec le taiji. | patchwork |
| 15 | hummingbird | A4 + A1, A17, A18 | Hybride plume et vrombissement, stationnaire en huit horizontal (exception à G5). | — |
| 16 | **eagle** | A17 | Premier oiseau d'espèce : doigts écartés, alula, battement lent et ample. Ouvre la méthode des signatures d'espèce. | crow, owl, gull, swift, pigeon, parrot |

### Phase 2 : toutes les autres paires

| Paire | Familles et aspects à appliquer | Propre à elle |
| --- | --- | --- |
| crow | P, A17 | battements en aviron |
| owl | P, A17 | grands coups doux et silencieux |
| gull | P, A17 | plané en M |
| swift | P, A17 | faucille verrouillée, petits coups |
| pigeon | P, A17 | claquement d'ailes en haut |
| parrot | P, A17 | couleurs, battement nerveux |
| peacock | P, A8, A9 | traîne |
| amethyst | P, A10 (calque) | plumes de cristal |
| copper | P, A10, A12 | plumes de métal, vert-de-gris |
| patchwork | P, A8, A14 | rabats rapiécés |
| earth | P, A8, A10, A15 | cristaux, battement lourd |
| nebula | P, A8, A10, A15 | étoiles et galaxie |
| shadow | P, A8, A10 | yeux |
| storm | P, A8, A10 | arcs électriques |
| angel | P, A7, A8, A10, A15 | auréole, scapulaires, battement très lent |
| demon | M, A10, A15 | membrane lourde |
| ptero | M | propatagium, griffes, membrane d'un seul tenant |
| light | M, A7, A9, A16 | transmog |
| phantom | A3, A10 | membrane de mort-vivant |
| flying_fish | A3, A10 | nageoires à rayons |
| kite | A3, A9, A12, A15 | longerons, voile, queue |
| ornithopter | A12 | bielles et poulies |
| ancient (`dragonfly`) | I, A4, A7, A8, A13, A18 | quatre ailes indépendantes, broche |
| psyche | I, A5, A8, A9, A10, A15 | flammes d'âme, lampe |
| air | A7, A10, A11 | voiles et rubans |

P = famille plume (P1, P2), M = membrane à doigts, I = insecte. Les règles générales (G1 à G5) s'appliquent toujours, adaptées à l'anatomie.

**Prochaine paire : eagle** (phase 1, n° 16).
