# Handoff — Mod d'ailes (NeoForge 1.21.1)

Ce fichier sert de point d'entrée pour la session Claude Code. La spec complète et à jour est dans `CAHIER_DES_CHARGES.md`, qui fait foi en cas de doute. Les étapes 1 à 5 sont codées (vol, rendu, 40 paires), ainsi qu'un premier écran Ailes et le HUD de stamina. Les routes de craft se conçoivent dans `CRAFTING.md`.

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
9. Contenu : routes de craft de `CRAFTING.md` en JSON, recettes mythiques.
10. Refonte art, en parallèle du gameplay (détail dans `CAHIER_DES_CHARGES.md`, section « Pipeline art ») :
    1. installer Blockbench, son plugin MCP et l'outil de capture en jeu (`/simplywings pose`, `/simplywings shot`, `runClient --quickPlaySingleplayer`) ;
    2. trancher l'import des modèles (chargeur JSON maison recommandé) et migrer `WingModel` sur le rig commun `shoulder` / `arm` / `forearm` / `hand` ;
    3. le designer livre l'aile hero, puis Claude écrit le skill style guide dans `.claude/skills/` ;
    4. Claude décline les 40 paires et la queue des Furies, avec captures Blockbench et en jeu à chaque paire ;
    5. one-shots en keyframes (déploiement, repli, atterrissage), puis finition des textures par le designer.

## Prochaine session

Les 21 paires rares sont refaites dans Blockbench (modèle, texture pxl, classe `render/rig/wings/<Paire>Rig`), validées par des aperçus Blockbench uniquement. À faire d'abord, quand zorago laisse le PC (le studio partage le GPU avec son jeu) : `tools/studio/start.sh` puis `python -I tools/studio/review.py <modèle>` pour chaque rare (bat, crow, owl, gull, hummingbird, eagle, swift, pigeon, peacock, parrot, bee, moth, beetle, squirrel, flying_fish, phantom, amethyst, ornithopter, kite, copper, patchwork), corriger, puis test en solo et sur le serveur dédié. Ensuite le batch épique, puis légendaire et mythique, avec la même chaîne (`art/wings/<modèle>.rig.js` + `.px.js`, `tools/blockbench/wing_builder.js`, détails dans `docs/SOURCES.md`). Les Ailes du Yin et du Yang (légendaire asymétrique, 9/10 partout) sont décrites dans `WINGS.md` mais pas encore dans le mod.

Chaque étape doit se tester en solo **et** sur un serveur dédié local avant de passer à la suivante.

## Points encore ouverts

Ils sont listés à la fin de `CAHIER_DES_CHARGES.md`, section « Décisions et points ouverts ». Pose la question à zorago quand une étape en dépend, sans trancher seul.
