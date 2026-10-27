# Sound Bench : cahier des charges de l'outil

Outil local (Node) pour écouter, comparer, régler et valider tous les sons de Simply Wings **à côté de l'animation qui les déclenche**, avant la validation finale en jeu. Objectif : faire 80 % du réglage dans le banc, et ne garder que la validation en jeu.

Rédigé le 27 octobre 2026 pour Claude Code. À lire avec `SOUND_PLAN.md`, qui dit **quoi** régler ; ce document dit **avec quoi**.

Priorité absolue : que ça marche. L'interface peut être moche. Pas de framework lourd, pas de design system.

---

## 0. Principes non négociables

1. **Une seule source de vérité par donnée.** Le banc ne recopie rien à la main :
   - les sons, leurs réglages et les verdicts vivent dans `tools/sounds/catalog.json` (§ 2) ;
   - les poses des ailes viennent de la **vraie classe de rig**, échantillonnée par `RigBake` (§ 5) ;
   - le gacha vient de `assets/simplywings/gacha/altar_show.json.gz`, **le fichier que le jeu rend**, pas des vidéos du prototype Blockbench (qui ont divergé du jeu) ;
   - les temps de déclenchement des sons du gacha sortent du code Java vers un fichier de données lu **par le jeu et par le banc** (§ 6.1).
2. **Le banc ne choisit jamais un son à la place de zorago.** Il propose, mesure et applique. Un son n'est `validated` qu'après écoute par zorago, et `in_game` qu'après test en jeu.
3. **Ce que le banc joue = ce que le jeu jouera.** Même `pitch`, même `volume`, même hasard (± 6 % de hauteur, comme `WingSounds.play`), même variation tirée au hasard, mêmes couches empilées (paire + couche de matière vanilla), même atténuation simulée par un curseur de distance.
4. **Hors du jar.** Tout vit dans `tools/sound-bench/` et `tools/sounds/`. Rien n'est ajouté au mod, sauf le fichier de cues du gacha (§ 6.1) et la commande de debug (§ 8).
5. **Aucun son vanilla copié dans le repo.** Le banc les lit dans le dossier d'assets local de Minecraft.

---

## 1. Stack et lancement

- `tools/sound-bench/` : Node 20+, **Vite** (front) + **Express** (API), un seul `npm run dev` qui lance les deux.
- Front : HTML + TypeScript ou JS simple, **three.js** pour la 3D, **wavesurfer.js** pour les formes d'onde, Web Audio pour la lecture.
- Back : Express, `child_process` vers **ffmpeg** (déjà utilisé par `build_sounds.py`) pour mesurer, découper et encoder.
- Config `tools/sound-bench/bench.config.json` (non versionné, avec un `.example` versionné) :
  - `minecraftAssets` : dossier d'assets de Minecraft (chez zorago, probablement `A:\CurseForge\Install\assets`) ;
  - `assetIndex` : id de l'index de la 1.21.1, lu dans le JSON de version plutôt que codé en dur ;
  - `soundSources` : `art/sounds/source/` ;
  - `ffmpeg` : chemin (repris de `build_sounds.py`).
- `README.md` dans le dossier : installation et lancement en 3 lignes.

---

## 2. Le catalogue : `tools/sounds/catalog.json`

Remplace à la fois la table `FILES` / `PAIRS` de `build_sounds.py` et le rôle de référence de `SOUND_DESIGN.md`. `build_sounds.py` est modifié pour **lire ce fichier** et générer `sounds.json` et les sous-titres depuis lui. Plus de double saisie.

Une entrée par **événement sonore** (id de `sounds.json`) :

```json
{
  "id": "wing.feather.flap",
  "category": "wing",
  "group": "feather",
  "status": "custom",
  "trigger": "Coup d'aile en déplacement, frappe de WingStyle.strikeAt ; distants via FlapEvent",
  "code": ["client/ClientFlight.java", "client/ClientNet.java"],
  "description": "Descente de l'aile : poussée d'air ronde et courte, froissement de plumes discret.",
  "keywords": ["wing flap", "feather", "cape flap", "whoosh short"],
  "reference": "minecraft:entity.phantom.flap",
  "loudness_target": "wing_flap",
  "attenuation": 32,
  "preload": true,
  "stream": false,
  "subtitle": "Wings flap",
  "animations": [{"rig": "basic", "scenario": "travel_cruise", "event": "strike"}],
  "variants": [
    {"file": "wing/feather/flap_1", "source": "fs_711122_cape_wings.mp3", "start": 22.16, "end": 22.75,
     "fade_in": 0.004, "fade_out": 0.25, "pitch": 1.0, "lp": 0, "gain_db": 0}
  ],
  "layers": [],
  "candidates": [],
  "verdict": {"state": "todo", "note": "", "date": null}
}
```

Champs :

| Champ | Valeurs |
|---|---|
| `category` | `wing`, `flight`, `fury`, `gacha`, `ui`, `block`, `essence`, `structure` |
| `group` | famille ou paire (`feather`, `membrane`, `copper`…), ou sous-domaine (`cards`, `altar`) |
| `status` | `vanilla` (pointe vers un événement Minecraft), `custom` (fichiers du mod), `silent` (volontairement rien en v1), `alias` (pointe vers un autre id du mod, avec `pitch` / `volume`), `todo` (à trouver) |
| `vanilla` | pour `vanilla` et dans `layers` : `{"event": "minecraft:block.copper.hit", "volume": 0.6, "pitch": 0.8}` |
| `alias` | pour `alias` : `{"target": "simplywings:wing.feather.flap", "volume": 0.6, "pitch": 1.1}` |
| `layers` | sons joués **en même temps** par le code (ex. la couche de matière de `WingEffects`). Le banc les joue ensemble pour juger le résultat réel |
| `loudness_target` | nom d'une cible de sonie (§ 4.3) |
| `candidates` | samples déposés dans `tools/sounds/candidates/<id>/`, avec leurs réglages, en attente de choix |
| `verdict.state` | `todo`, `keep`, `reject`, `revise`, `validated`, `in_game` |

Génération initiale (tâche 1) : un script `tools/sounds/catalog_init.py` remplit le catalogue depuis `sounds.json`, la table `FILES` / `PAIRS` actuelle, les couches de `WingEffects.java`, les tableaux de `SOUND_DESIGN.md` (déclencheur, description) et les décisions de `SOUND_PLAN.md` § 3 (statut). Les mots-clés sont proposés par Claude Code, **en anglais** (Freesound est en anglais). Ensuite, le catalogue est édité par le banc ou à la main, plus par ce script.

---

## 3. Écran principal : la liste

- Colonne gauche : arbre **catégorie → groupe → id**, avec un compteur de verdicts par groupe (`3/5 validés`). Filtres : statut, verdict, « sons qui sortent de leur cible de sonie », « joués le plus souvent » (ordre de `SOUND_PLAN.md` § 4). Recherche plein texte sur id, description, mots-clés.
- Panneau central, pour l'id choisi :
  - déclencheur, fichiers de code, description, **mots-clés** (copiables d'un clic, avec un lien de recherche Freesound `https://freesound.org/search/?q=<mots>&f=license:"Creative Commons 0"`) ;
  - une ligne par **variation** : ▶ (comme le lecteur OGG de VS Code), forme d'onde, durée, sonie, attaque ;
  - **▶ Jeu** : joue une variation au hasard avec le hasard du code et les `layers`, comme en jeu ;
  - **▶ Référence** : joue le son vanilla de `reference`, à la même distance simulée, pour comparer les niveaux ;
  - **▶ Rafale** : 8 déclenchements au rythme réel (par exemple `flap_rate` de la paire) pour juger la répétition et la fatigue ;
  - curseur **distance** (1 à 48 blocs) : atténuation linéaire de Minecraft jusqu'à `attenuation` ;
  - verdict et note.
- Panneau droit : la vue d'animation (§ 5 et § 6), synchronisée.

Raccourcis clavier : espace = ▶ Jeu, R = référence, 1 à 9 = variation n, K / X / V = keep / reject / revise, flèches = id suivant / précédent. Le but est d'enchaîner 40 sons en 20 minutes.

---

## 4. Mesure, édition, export (API ffmpeg)

### 4.1 Mesures affichées pour chaque fichier

Sonie intégrée (LUFS) **et** RMS max sur 100 ms (les fichiers de moins de 400 ms renvoient −70 LUFS : c'est le cas de beaucoup de battements, le RMS est donc la mesure utile), pic vrai, délai d'attaque (temps jusqu'à −20 dB du pic), centre spectral (indicateur de « brillance »), saut au raccord pour les boucles, canaux et fréquence. Les seuils de `SOUND_PLAN.md` § 2 colorent les valeurs hors cible.

### 4.2 Éditeur de variation

Sur la forme d'onde : poignées début / fin, fondus d'entrée et de sortie, hauteur, passe-bas, gain. **Aperçu instantané** en Web Audio, sans réencoder. Bouton « Appliquer » : écrit les réglages dans `variants[i]` du catalogue. Bouton « Rogner l'attaque » : place le début 3 ms avant le premier passage à −30 dB du pic.

Pour les boucles : vue du raccord (fin + début collés), réglage du fondu enchaîné, lecture en boucle sans fin.

### 4.3 Cibles de sonie

Normalisation **au RMS max** par catégorie, puis plafond de pic vrai à −1 dBTP. Les cibles de départ sont dans `SOUND_PLAN.md` § 2, éditables dans `tools/sounds/loudness.json`. Bouton « Normaliser le groupe » : aligne toutes les variations d'un groupe sur la cible.

### 4.4 Candidats

Glisser un fichier (mp3, wav, flac, ogg) sur un id : il est copié dans `tools/sounds/candidates/<id>/` (ignoré par git, comme `art/sounds/source/`), mesuré, normalisé à la cible pour l'aperçu, et listé à côté des variations actuelles. « Promouvoir » : le candidat devient une variation (le fichier part dans `art/sounds/source/`, une ligne est ajoutée à `art/sounds/SOURCES.md` avec un champ licence **obligatoire** : CC0, ou autre avec justification).

### 4.5 Export

« Construire » appelle `python -I tools/sounds/build_sounds.py`, qui lit le catalogue, encode les OGG mono 44,1 kHz q5, écrit `sounds.json` (avec `attenuation_distance`, `preload`, `stream`) et `en_us.json`. Option « Copier vers l'instance » : copie le jar ou le dossier de ressources vers l'instance locale de test (jamais vers le pack CurseForge public), pour un F3+T en jeu.

---

## 5. Vue 3D des ailes (three.js, données de `RigBake`)

Pas de conversion Java → JS. Le repo a déjà ce qu'il faut : `./gradlew bakeRig -Pwing=<modèle>` instancie la vraie classe `render/rig/wings/<Paire>Rig` avec `WingMotion` et `RigMemory`, sans lancer Minecraft, et échantillonne les os pour les scènes Blockbench (`tools/blockbench/scene.py`). Le banc réutilise ce moteur.

### 5.1 Export « bench » de `RigBake`

Ajouter un mode de sortie (par exemple `-Pformat=bench`) qui écrit `build/rigbake/<modèle>.bench.json` :

- pour chaque **scénario** : les transformations locales de chaque os (ailes **et** mannequin `head`, `body`, bras, jambes, déjà présents dans `RigBake.body`) à chaque pas de simulation (les `SUBSTEPS` actuels, donc 40 i/s) ;
- **la liste des événements sonores horodatés** du scénario, produits par la même logique que le jeu : `strike` (point de frappe `strikeAt`), `launch`, `land_soft`, `land_hard`, `mode_travel`, `mode_hover`, `tuck`, `brake`, `stall`, `rouse` (idle). C'est ce qui permet de caler le son sur la frappe exacte, sans repère posé à la main ;
- les métadonnées utiles : `flap_rate`, `hover_rate`, vitesse simulée (pour piloter la boucle de vent et les vrombissements).

Scénarios minimum : `ground_idle` (avec chaque variante de `rouse`), `hover`, `travel_cruise`, `travel_fast`, `glide`, `dive_tuck`, `brake`, `stall`, `takeoff` (charge + lancement), `air_jump`, `landing_soft`, `landing_hard`. Chaque scénario peut aussi être enchaîné dans un **vol complet** de 30 s (décollage → croisière → piqué → frein → stationnaire → atterrissage) pour juger l'ensemble.

Une tâche Gradle `bakeBench` boucle sur toutes les paires de `WingRigs` et écrit un index `build/rigbake/index.json`. Le banc la lance au démarrage si les fichiers sont plus vieux que les classes compilées.

### 5.2 Rendu

- Chargeur `.bbmodel` maison (cubes, groupes et pivots, UV par face, texture PNG embarquée en base64) avec filtrage `NearestFilter` pour le pixel art. Même convention d'unités que `RigLoader` (1/16 de bloc, axes de Minecraft).
- **Membranes** : porter la logique de `tools/blockbench/simplywings_membranes.js` et de `render/rig/Membrane.java` (lecture de `<modèle>.membranes.json`), sinon les ailes de peau (chauve-souris, démon, ptérosaure, Furies…) sont vides.
- Mannequin Steve avec un skin par défaut.
- Caméra orbitale, sol, fond neutre. Bouton « vue joueur » (caméra derrière) et « vue voisin » (à 10 blocs, de côté), parce qu'un même son se juge différemment selon le point de vue.
- Timeline sous la vue : lecture, pause, ralenti ×0,25, boucle, **marqueurs des événements sonores** cliquables (chaque marqueur joue son son). Les sons sont déclenchés par les événements de la timeline, avec les réglages du catalogue.
- Particules : non. Le banc juge le son contre le mouvement, pas les effets.

### 5.3 Contrôle de fidélité

Pour 3 paires (`basic`, `bat`, `bee`), comparer une image du banc avec la planche du studio (`tools/studio/review.py`) à la même frame. Écart visible = bug du chargeur, à corriger avant d'aller plus loin.

---

## 6. Vue du gacha (three.js, données du jeu)

### 6.1 Sortir les cues du code (prérequis, touche le mod)

Aujourd'hui les sons de l'autel sont codés en dur dans `ClientShows.tick` (temps relatifs aux métadonnées `pre`, `birth`, `burst`…, volumes de 0,8 à 8, hauteurs, positions) et dans `CardDeck` / `AltarScreen`. Les déplacer dans `assets/simplywings/gacha/show_sounds.json` :

```json
{
  "summon_b": [
    {"t": 0.05, "from": "start", "sound": "simplywings:gacha.offer", "volume": 2.0, "pitch": 1.0, "at": "altar"},
    {"t": 1.9, "from": "pre", "sound": "simplywings:gacha.rune_charge", "volume": 4.0, "pitch": 0.9, "at": "altar"}
  ]
}
```

`ClientShows` lit ce fichier (rechargé avec F3+T) et garde en code uniquement ce qui dépend de l'état (boucle sur les éléments `ignite`, nombre d'étoiles en ×10). Le comportement en jeu doit rester **identique** avant et après : écouter un B et un SSS avant et après le changement. Même traitement pour les cartes (`CardDeck.openSound`, distribution, retournement).

### 6.2 Rendu

- Lire `altar_show.json.gz` : `textures`, `bones` (1 424 os, pivot, rotation, cubes, `lit`), `anims` (`summon_b` … `summon_x10`, `summon_mythic_*`, `idle`), pistes par os et par canal, clés `[t, x, y, z, interp]`. Porter l'évaluation de `ShowModel` / `ShowRenderer` (interpolations 0, 1 et 2, visibilité, teinte), et les textures animées (`frames`, `frameTime`).
- Caméra : celle du jeu si elle est dans l'animation, sinon une caméra fixe face à l'autel.
- Timeline avec les marqueurs de `show_sounds.json` : **glisser un marqueur** change son temps dans le fichier, et un bouton permet d'en ajouter un ou de le supprimer. C'est le principal outil de réglage du gacha.
- Les cartes 2D (écran de l'autel) : un panneau 2D simple sous la vue 3D, qui affiche la séquence de cartes et ses marqueurs. Pas besoin de reproduire le visuel exact.
- Un bouton « Passer » qui simule le saut d'animation, pour vérifier qu'aucun son ne reste coincé ni ne se déclenche en rafale.

---

## 7. Interface et autres sons sans animation

Pour l'UI, les blocs, les essences et les Furies sans scène : la vue droite affiche une maquette statique (capture du menu, par exemple `docs/` ou une capture à fournir par zorago) et un bouton qui rejoue la séquence réelle (ex. : ouvrir le menu J → sélectionner → équiper). Pour les boucles de blocs : lecture en boucle avec un curseur de vitesse qui reproduit la modulation du code (volume et hauteur du compresseur).

---

## 8. Pont vers le jeu

- Commande client `/simplywings sound <id> [pitch] [volume]` : joue un son du mod ou vanilla sur le joueur. Commande `/simplywings sound list <groupe>` pour enchaîner les variations.
- Workflow de validation : « Construire » dans le banc → copier vers l'instance de test → F3+T en jeu → test → verdict `in_game` dans le banc.

---

## 9. Découpage en tâches pour Claude Code

Chaque tâche se termine par un commit et un court test manuel décrit à zorago.

1. **Catalogue** : schéma, `catalog_init.py`, `build_sounds.py` qui lit le catalogue (la sortie `sounds.json` doit rester équivalente à l'actuelle, sauf les corrections de `SOUND_PLAN.md` § 1).
2. **Banc v0** : serveur, liste, lecture des fichiers du mod **et des sons vanilla** (via l'index d'assets), ▶ Jeu avec hasard et couches, ▶ Référence, verdicts sauvegardés, raccourcis clavier.
3. **Mesures et édition** : formes d'onde, mesures, éditeur de variation, normalisation par cible, candidats par glisser-déposer, export.
4. **Ailes 3D** : export bench de `RigBake` avec événements sonores, `bakeBench`, chargeur `.bbmodel`, membranes, timeline, contrôle de fidélité (§ 5.3).
5. **Gacha** : extraction des cues vers `show_sounds.json` (avec vérification à l'oreille que le jeu n'a pas changé), rendu de `altar_show.json.gz`, marqueurs déplaçables.
6. **Pont** : commande `/simplywings sound`, copie vers l'instance de test.

Les tâches 1 à 3 suffisent pour commencer les passes d'écoute de `SOUND_PLAN.md`. Les tâches 4 et 5 peuvent avancer en parallèle des passes.

## 10. Hors périmètre

Pas de mixage multipiste, pas de synthèse avancée dans l'interface (les sons synthétisés restent dans `build_sounds.py`, fonction `synth`), pas de rendu des particules, pas de version web publique, pas de compte ni de base de données : un fichier JSON suffit.
