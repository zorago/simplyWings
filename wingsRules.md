# Carnet de règles des ailes

Carnet de logique du polish, paire par paire, avec zorago. Chaque correction faite sur une paire devient une règle qui s'applique aussi aux paires suivantes de la même famille (ou à toutes, selon sa portée).

## Comment on s'en sert

- Une règle a un identifiant (`G1`, `P3`, `M2`…), une portée (famille) et la paire où elle est née.
- Quand zorago dit « applique comme pour les ailes de type plume précédentes ET modifie x, y et z », on applique **toutes** les règles de la famille plume (et les règles générales), puis les nouveaux points x, y et z. Les nouveaux points deviennent à leur tour des règles s'ils valent pour la famille.
- Avant de polir une paire : relire les règles générales et celles de sa famille, et cocher chaque règle dans son entrée du journal (appliquée, ou sans objet et pourquoi).
- Une règle qui se révèle fausse sur une paire plus tard n'est pas effacée : on la précise (exception, condition) et on note où.

## Familles

| Famille | Paires |
| --- | --- |
| Plume (oiseau, rig `FeatherWing`) | common, crow, owl, gull, hummingbird, eagle, swift, pigeon, peacock, parrot, angel, seraph, … |
| Membrane (doigts + peau) | bat, ptero, demon, … |
| Insecte (ailes rigides, élytres) | bee, moth, beetle, dragonfly, psyche, … |
| Objet / machine | ornithopter, kite, copper, daedalus, patchwork, … |
| Élémentaire / magique | fire, water, earth, air, storm, aurora, shadow, nebula, forest, amethyst, … |

La liste se précise au fil des passes ; une paire peut emprunter à deux familles (noter lesquelles dans son entrée).

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

## Règles de famille

### Plume

- **P1 — Rangées de plumes sur le bras et l'avant-bras** (née sur `basic`). Pas de plaque unique sous les deux os du centre : au moins 4 plumes séparées par os (rémiges tertiaires sur le bras, secondaires sur l'avant-bras), posées à la suite le long de l'os et qui se chevauchent un peu. Chaque plume est un os à elle, attaché à sa racine sur l'os porteur : groupes `<côté>_tertial_feather_<k>` dans `<côté>_tertials` et `<côté>_secondary_feather_<k>` dans `<côté>_secondaries` (numérotés du corps vers le bout de l'aile). `FeatherWing` les détecte : au repli, chaque plume tourne autour de sa propre racine pour pendre sous l'os, et l'éventail se referme (plumes parallèles). Déployée, l'éventail s'ouvre selon l'angle de repos de chaque plume : les tertiaires penchent vers le corps, les secondaires vers le bout de l'aile. Profondeur : chaque plume de la rangée a son propre Z, décalé de 0,025, pour éviter le scintillement. Texture : une bande par plume (face ventrale puis face dorsale), deux tons alternés, rachis clair au centre, pointe sombre arrondie.

### Membrane

Aucune pour l'instant.

### Insecte

Aucune pour l'instant.

### Objet / machine

Aucune pour l'instant.

### Élémentaire / magique

Aucune pour l'instant.

## Journal des passes

### 1. Ailes communes (`basic`, famille plume)

Statut : en cours.

Modifications :

1. Les plaques uniques du bras (tertiaires) et de l'avant-bras (secondaires) se déformaient mal au repli. Elles sont remplacées par 4 tertiaires (longueurs 8, 9, 9, 10 ; inclinaison −12° à 0°) et 5 secondaires (longueurs 11 à 12 ; inclinaison 2° à 14°), une plume par os, qui se replient sous l'os.

2. Frein (retour de zorago : « écrasé vers l'avant, comme si les ailes étaient plus puissantes que lui ») : le corps se redresse en posture d'atterrissage (penché de 8° en arrière au lieu de 50° vers l'avant), les jambes passent dessous, les bras s'écartent, la tête regarde devant. Les ailes sont levées et vers l'avant, avec des coups de freinage puissants (`CommonRig`, `BRAKE_BEAT`).

3. Frein en deux temps (retour de zorago) : parachute fixe au-dessus de la vitesse minimale, avec la pose qu'il a réglée (bras de l'aile gauche, mise en miroir), un tremblement des bouts d'aile et une traînée aux deux extrémités. Les coups de freinage battus ne restent que sous la vitesse minimale, jusqu'au stationnaire. Nouvelles animations dans la scène : `brake_slow` et `brake_to_hover`.
4. À côté, pour toutes les paires : vitesse maximale atteignable par gravité doublée (`terminalVelocity` 110 → 220 m/s, `diveDragSpeed` 60 → 120 m/s), sans toucher aux vitesses max des ailes, pour que le joueur puisse se mettre en danger et avoir besoin d'un freinage d'urgence violent.

5. Frein lent (sous la vitesse minimale) : les ailes battent derrière les bras (`STROKE` de base +0,25 au lieu de −0,35, amplitude 0,25), et les bras du joueur redescendent en pose normale (`FlightPose`, poids `brakeBeat`), pour une transition douce vers le stationnaire.

Règles créées : P1, G1, G2.
