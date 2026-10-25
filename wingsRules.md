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

Aucune pour l'instant.

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

Statut : scène prête (`art/scenes/basic.bbmodel`), en attente des retours de zorago.

Modifications :

- (à remplir pendant la passe)

Règles créées : (à remplir)
