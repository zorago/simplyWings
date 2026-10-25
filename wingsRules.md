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

Aucune pour l'instant.

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

Règles créées : P1.
