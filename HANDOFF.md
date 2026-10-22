# Handoff — Mod d'ailes (NeoForge 1.21.1)

Ce fichier sert de point d'entrée pour la session Claude Code. La spec complète et à jour est dans `CAHIER_DES_CHARGES.md`, qui fait foi en cas de doute. Les étapes 1 à 5 sont codées (vol, rendu, 41 paires), ainsi qu'un premier écran Ailes et le HUD de stamina. Les routes de craft des 40 paires sont conçues : règles dans `CRAFTING.md`, une route par paire dans `crafting/<id>.md`.

## Contexte

- Le dev est zorago (apprenti dev web, francophone). Il veut un mod d'ailes standalone pour son modpack NeoForge 1.21.1, publié ensuite sur CurseForge avec la mention d'un développement majoritairement fait par IA.
- Claude fait la majorité du travail : code, textures, modèles en cubes et animations procédurales.
- On échange en français. Le code est écrit sans commentaires, sauf demande explicite.
- Le mod est entièrement en anglais (code, ids, config, UI, HUD, noms d'items) et n'a que `en_us`, sans traduction. Seuls les documents de conception sont en français.

## Règles non négociables

1. **Zéro dépendance** autre que NeoForge : pas de Curios, GeckoLib, Architectury, Cloth Config ni lib de config tierce. Si une lib est nécessaire, on l'écrit dans le mod.
2. **Events et API NeoForge d'abord**. Chaque mixin doit être justifié dans `docs/MIXINS.md` (cible, raison, alternative écartée). Budget issu de l'analyse : 0 access transformer, 0 mixin, au plus 1 (`PlayerModel.setupAnim` TAIL pour les bras).
3. **Rendu** : ModelPart + RenderTypes vanilla uniquement, sans shader ni RenderType custom. Ça doit marcher avec Iris et les shaderpacks.
4. **Serveur autoritaire** pour la collection, la paire équipée, la stamina et les sauts de secours. Physique de vol côté client (le mouvement joueur est client-autoritaire en vanilla). Aucun paquet par tick.
5. **Pas de copie de code** depuis les mods de référence. On les décompile pour comprendre une technique, puis on la réimplémente from scratch.

## Étape 1 — Analyse des mods de référence (terminée et validée)

Les jars sont dans `modsRef/`. Décompilation avec Vineflower dans `modsRef/_decompiled/` (ignoré par git), puis production de `docs/ANALYSE_REFERENCES.md` en répondant précisément à ces questions :

| Mod | Questions |
| --- | --- |
| Better Flight Revived | Comment évite-t-il le kick « flying is not enabled » / « floating too long » ? Où est calculée la stamina (client ou serveur) et comment est-elle synchronisée ? Quels paquets envoie-t-il, à quelle fréquence ? |
| Shoulder Surfing Reloaded | Comment découple-t-il la rotation de la caméra de celle du corps ? Liste exacte de ses mixins liés à la caméra et à la souris. Peut-on obtenir la même chose avec `ViewportEvent.ComputeCameraAngles` + un seul mixin ? |
| Do a Barrel Roll | Représentation des rotations (quaternions ou Euler), lissage, gestion du roll caméra. Ce qui est réutilisable comme approche pour un contrôle ZQSD sans souris. |
| Caelus API | Comment il autorise l'état de vol hors slot torse. Est-ce applicable à notre vol custom, ou notre approche via access transformer suffit-elle ? |
| Wings (fork 1.21.1) | Comment le RenderLayer s'ajoute au joueur, comment l'animation procédurale est pilotée, comment l'état est synchronisé aux joueurs distants. |
| JEI / EMI | Confirmer que la zone à gauche de la hotbar de l'inventaire reste libre. |
| Liste du modpack | Mods à risque de conflit : shaders (Iris/Oculus), moteurs de rendu, mods de caméra, mods qui modifient l'End ou la mort du dragon, mods qui ajoutent des boutons d'inventaire. |

Pour chaque mod : la technique retenue, ce qu'on fera différemment et pourquoi. Termine par une section « Décisions d'architecture » qui confirme ou corrige la table d'architecture du cahier des charges.

**Ne pas commencer le code avant que zorago ait validé `docs/ANALYSE_REFERENCES.md`.**

## Étape 2 — Squelette (après validation)

- Projet généré depuis le MDK NeoForge 1.21.1 officiel. Mod id : `simplywings`.
- Packages par module, alignés sur la table d'architecture : `data` (définitions datapack), `player` (attachment), `network`, `flight` (physique), `camera`, `render`, `anim`, `loot`, `block` (table d'assemblage), `structure`, `client.gui`, `config`.
- Une seule paire de test codée en dur côté datapack, pour valider toute la chaîne de bout en bout.

## Ordre d'implémentation conseillé

1. Attachment joueur + réseau + commande de debug pour débloquer et équiper une paire.
2. Mode stabilisé (physique + validation serveur sans kick). C'est le premier test en multi.
3. Rendu des ailes (RenderLayer, cubes, idle) + synchro aux joueurs distants.
4. Mode déplacement : battements, contrôles ZQSD, caméra découplée, transitions « laissée derrière » et « dans la tête ».
5. Dive, freinage, décrochage, dégâts de crash, stamina à 0, sauts de secours.
6. Écran Ailes + bouton d'inventaire + HUD stamina.
7. Énergie ailée (Data Component) + Global Loot Modifiers + Cloud Essence (flacon, absorbeur, récolte) + table d'assemblage (onglets assemblage et composants, réservoir).
8. Forge de l'End (template NBT, placement à la mort du dragon, SavedData).
9. Contenu : routes de `crafting/*.md` en JSON (recettes `GRID` et `ASM`, entrées du registre `simplywings:loot_source`).
10. Refonte art, en parallèle du gameplay (détail dans `CAHIER_DES_CHARGES.md`, section « Pipeline art ») :
    1. installer Blockbench, son plugin MCP et l'outil de capture en jeu (`/simplywings pose`, `/simplywings shot`, `runClient --quickPlaySingleplayer`) ;
    2. trancher l'import des modèles (chargeur JSON maison recommandé) et migrer `WingModel` sur le rig commun `shoulder` / `arm` / `forearm` / `hand` ;
    3. le designer livre l'aile hero, puis Claude écrit le skill style guide dans `.claude/skills/` ;
    4. Claude décline les 40 paires et la queue des Furies, avec captures Blockbench et en jeu à chaque paire ;
    5. one-shots en keyframes (déploiement, repli, atterrissage), puis finition des textures par le designer.

## Prochaine session

**Session de test du 22 octobre au soir** : suivre `ToDoTesting.md` dans l'ordre (chargement, items, drops imprégnés, Cloud Essence, table, forge, JEI, Yin et Yang), puis les correctifs ci-dessous.

Ordre fixé par zorago (21/10/2026) : **1. correctifs ci-dessous** (rares + commune) → **2. épiques** → **3. légendaires** → **4. mythiques** → **5. gameplay** réfléchi et bien intégré à Minecraft, avec des lignes de craft de plus en plus complexes en montant les paliers.

Avant tout, quand zorago laisse le PC : passe studio sur les 21 rares (`tools/studio/start.sh`, puis `python -I tools/studio/review.py <modèle>`), jamais pendant qu'il joue (le studio partage le GPU avec son jeu).

### Correctifs à faire (rares et commune)

1. **Saut dans le battement, sur toutes les ailes.** Au « reset » du cycle, les ailes se téléportent d'un coup, ce qui casse la sensation naturelle. Règle : des keyframes procédurales qui bouclent, et **jamais de remise à zéro brutale d'une position** ; même une animation annulée se termine par un lissage, même rapide.
   - Causes probables, à vérifier dans `anim/WingAnimator` : `strokeProgress` repart à 0 (nouvel appui sur sprint, fin de boucle) alors que le cycle était ailleurs ; `beat` passe à −1 et `beatWeight` tombe à 0 d'une image à l'autre (`m.beating()` devient faux, toute la contribution du battement disparaît) ; `beatWeight = 1 − hoverRaw` change d'un coup ; les poids d'état qui basculent sans ressort.
   - Correctif proposé : la phase du battement ne se remet jamais à zéro, elle continue de tourner et seule l'amplitude monte ou descend, par un ressort dans `RigMemory` ; un nouveau battement reprend depuis la phase courante, ou se cale sur la prochaine phase compatible ; enfin, un filet de sécurité commun : chaque canal de sortie (`RigPose` et rotations propres à une paire) passe par un ressort critique raide, qui absorbe tout saut restant sans ajouter de mollesse.
2. **Artefacts visuels des plaques plates superposées, sur toutes les ailes.** Les plaques coplanaires ou presque (couches de plumes à 0,02-0,06 d'écart, éventail replié, ailes gauche et droite qui se croisent, membranes adjacentes) se battent au z-buffer, surtout de loin. Avec les RenderTypes vanilla, pas de polygon offset possible.
   - Correctif proposé, en trois étapes. **Partition** : chaque pixel de la surface de l'aile appartient à une seule plaque. Les masques se calculent depuis la géométrie partagée, comme pour la membrane de la chauve-souris (`lib/rig_geom.js`). Les recouvrements de plumes sont peints dans la texture au lieu d'être faits par des plaques superposées. **Décalage franc** : là où un recouvrement reste inévitable (éventail replié, croisement en X du martinet), on décale d'au moins 0,1 à 0,15 en Z, toujours dans le même ordre, et dynamiquement pendant le repli (`Bone.move`). **Fusion** : on fusionne les plaques coplanaires d'un même os (secondaires et couvertures sur une seule plaque) dès que l'animation ne les sépare pas.
   - À vérifier au studio, de près et de loin, sur chaque paire.
3. **Battements trop rapides pour beaucoup d'ailes.** La chauve-souris garde une cadence cohérente ; l'aigle et la chouette doivent avoir une vibe majestueuse.
   - Attention : `flap_rate` (JSON de l'aile) règle aussi la physique (gain par coup = accélération ÷ `flap_rate`, appliqué au moment du coup). Le ralentir rend chaque coup plus puissant à accélération égale.
   - Pistes : baisser `flap_rate` (aigle et chouette en priorité, puis goéland, paon, pigeon, corbeau, perroquet…) et revoir `BALANCE.md` et `WINGS.md` ; ou découpler la cadence visuelle de la cadence physique, mais le coup visuel doit rester calé sur l'application du gain.
   - Proposer les valeurs à zorago avant de toucher l'équilibrage.
4. **Appliquer les mêmes corrections à la commune** (`CommonRig`, `basic.bbmodel` : plaques à dz = 0, à passer à 0,02 et à partitionner).

### Animations d'idle et de récupération

Les idles des rares (une par paire : étirement de la chouette, danse du perroquet, roue du paon…) sont validées et très appréciées. Règle par palier :
- **Rare** : 1 idle par paire (déjà fait), plus **une animation de récupération après l'atterrissage** (à ajouter à chaque rare : remise en place, secousse, repli…).
- **Épique** : 3 idles tirées au hasard.
- **Légendaire** : 5 idles tirées au hasard.
- **Mythique** : tout ce qu'on trouve.

## Points encore ouverts

Ils sont listés à la fin de `CAHIER_DES_CHARGES.md`, section « Décisions et points ouverts ». Pose la question à zorago quand une étape en dépend, sans trancher seul.
