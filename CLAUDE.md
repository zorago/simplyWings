# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## État du projet

Mod d'ailes standalone pour **NeoForge 1.21.1** (nom de travail : SimplyWings). Prototype de vol codé, 40 paires.

- `CAHIER_DES_CHARGES.md` : spec complète, **fait foi** en cas de doute (stats, formules de vol, table d'architecture, points ouverts).
- `HANDOFF.md` : point d'entrée, étapes de travail et ordre d'implémentation.

- `WINGS.md` : catalogue des paires (identité, apparence, ressenti, stats). À mettre à jour avec chaque paire ajoutée ou modifiée.
- `README.md` : vitrine du repo, en anglais (présentation, état, contrôles, commandes, TODO avant release). À tenir à jour à chaque fonctionnalité terminée.
- `CRAFTING.md` : route de craft de chaque paire (étapes, origine des pièces, coût en Cloud Essence par étape et au total). À mettre à jour avec chaque recette ajoutée ou modifiée.
- `BALANCE.md` : les 7 stats (échelle 0-100, valeurs physiques à 0 et à 100 dans la config serveur `[stats]`), les scores de chaque paire et la répartition des globaux. À tenir à jour à chaque changement de stats ou de physique.
- `docs/ANALYSE_REFERENCES.md` : analyse des mods de référence et du pack, avec la table d'architecture corrigée. **Elle prime sur la table du cahier des charges** là où elle la corrige (anti-kick, caméra, souris).

Étape en cours : **prototype de vol** (étapes 1 à 5 du handoff) et conception des routes de craft. Analyse validée. Un premier écran Ailes, le bouton d'inventaire et le HUD de stamina existent. Pas encore de table d'assemblage, de Cloud Essence, de forge, de loots ni de JEI.

Code : package `com.zorago.simplywings`, NeoForge 21.1.257 (comme le pack), ModDevGradle.
- `flight/ServerFlight` : autorité serveur (mode, stamina, gravité à 0, sauts de secours, crash, sync).
- `client/ClientFlight` : physique client des deux modes, en m/s, convertie en blocs/tick via `setDeltaMovement` dans `PlayerTickEvent.Pre`.
- `client/FlightCamera` : caméra et découplage souris.
- Ailes procédurales :
  - `anim/WingStyle` : par modèle, 1 à 3 ailes par côté (`Slot` : forme, décalage, phase, amplitude) et profil de battement (montée, coup, vibration, ailes à plat ou non, translucide) ;
  - `anim/WingAnimator` : pose de base lissée par tick, oscillations calculées au rendu (partial tick) ;
  - `render/WingModel` : formes de 1 à 3 segments ;
  - `render/WingsLayer` : dessine chaque emplacement.
- Physique des battements :
  - accélération = gain par seconde, et gain d'un battement = accélération ÷ `flap_rate` ;
  - à partir de 8 battements par seconde (`WingDefinition.CONTINUOUS_FLAP_RATE`), la poussée est continue, signalée par `FLAG_THRUST`, sans paquet par battement ;
  - sinon, le gain s'applique au moment du coup (`WingStyle.strikeAt`), qui envoie le paquet `Flap`.
- Paquets dans `network/Payloads`. Un battement en vol de déplacement est relayé aux joueurs proches (`FlapEvent`) : c'est un événement (environ 2/s pendant qu'on bat des ailes), pas un paquet par tick.

Contrôles du prototype : saut en l'air = saut ailé (coûte `airJumpCost` de stamina, sans limite de nombre) ; sous ce coût, sauts de secours gratuits (2, rechargés au sol) ; double tap **très rapide** (`doubleTapTicks`, 4 ticks) = mode stabilisé, le saut ailé du premier tap est alors remboursé ; Alt gauche = bascule instantanée stabilisé ↔ déplacement en l'air, et au sol (hors stationnaire) Alt **maintenu** 1 s = envol chargé directement en mode déplacement, possible en marchant ou en courant, puis Alt ignoré 1,5 s pour éviter un passage accidentel en stationnaire ; sprint maintenu = battements ; sneak = frein (touche « Frein » non assignée par défaut) ; ZQSD = déplacent le point visé, et le cap le suit avec un ressort critique (pas de rotation directe).

Les paramètres propres à chaque paire (cadence et vitesse du stationnaire, identique à l’horizontale et à la verticale : `hover_rate`, `hover_speed`) vivent dans le JSON de l'aile, pas dans la config serveur. Commande de debug : `/simplywings unlock <aile>|unlock all|equip|unequip|stamina|reset|info` (`unlock all` débloque tout le registre). Mythiques Night et Light (`data/FurySet`, `flight/FuryServer`, `client/FuryEffects`, queue `render/TailModel` + `anim/TailAnimator`) : Night possédée = stamina infinie, Light possédée = transmog (bouton « Equip appearance » dans le menu, l'apparence change tout le visuel, rythme des battements compris via `WingAnimator.beat` en boucle ; physique, stats et pouvoirs des Furies restent ceux de la paire équipée), cri en repli faucon, charge au frein 2 s puis sprint = dash + invisibilité. Menu des ailes : touche J (`client/gui/WingsScreen`, liste de toutes les ailes du registre, verrouillées grisées, équiper/retirer via `EquipWing`).

## Pipeline art

Refonte des modèles, textures et animations : section « Pipeline art » du cahier des charges. En résumé : Blockbench + MCP, rig commun `shoulder` / `arm` / `forearm` / `hand` pour toutes les ailes, procédural pour tout ce qui réagit au vol, keyframes `AnimationDefinition` seulement pour les one-shots, boucle visuelle obligatoire (captures Blockbench + captures en jeu) avant de juger une aile, style figé par un skill dans `.claude/skills/` après l'aile hero du designer. Prochaine étape : installation de Blockbench, du MCP et de la vision en jeu.

## Pack cible et outils d'analyse

- Pack de destination : `A:\CurseForge\Instances\PleasePlayWithMe` (NeoForge 21.1.257, 195 mods, liste dans `MODLIST.md`). Mods lourds sur le moteur : Iris, Sodium, Distant Horizons, Create Aeronautics + Sable, YUNG's Better End Island. La règle « zéro mixin / zéro dépendance » sert d'abord à éviter les conflits avec eux.
- Jars de référence : `modsRef/`. Sources décompilées : `modsRef/_decompiled/` (à exclure de git). Classes vanilla patchées par NeoForge : `modsRef/_decompiled/_mc/`. Mixins sensibles du pack : `modsRef/_decompiled/_pack/`.
- Jars NeoForge et Minecraft du pack : `A:\CurseForge\Install\libraries\net\neoforged\neoforge\21.1.257\` (`-client.jar` = classes Minecraft patchées en noms Mojang, `-universal.jar` = API et events NeoForge).
- Décompiler : `java -jar tools/vineflower.jar -log=WARN <jar ou dossier de .class> <dossier de sortie>`
- Lister les mixins du pack qui touchent une classe : `python -I tools/scan_mixins.py "A:/CurseForge/Instances/PleasePlayWithMe/mods"`. C'est une heuristique (référence de type dans une classe mixin) : il faut décompiler le mixin pour confirmer.

## Communication

- On échange en français avec zorago (apprenti dev web).
- Code **sans commentaires**, sauf demande explicite.
- Le mod est **entièrement en anglais** : code, ids, commentaires de config, UI, HUD, noms d'items, tooltips, sous-titres. Une seule langue, `en_us.json`, pas de `fr_fr.json`. Seuls les documents de conception (`*.md`) sont en français ; `README.md` est en anglais.
- Les points ouverts (fin du cahier des charges) se posent à zorago quand une étape en dépend, sans trancher seul.
- Mod id : `simplywings` (tous les ids sont `simplywings:*`).

## Règles non négociables

1. **Zéro dépendance** hors NeoForge : ni Curios, GeckoLib, Architectury, Cloth Config, ni lib de config tierce (la config passe par `ModConfigSpec`). Si une lib manque, on l'écrit dans le mod. Seule exception validée : JEI en intégration optionnelle (API en `compileOnly`, dépendance `optional`, code JEI isolé dans un package `compat.jei` chargé uniquement par `@JeiPlugin`).
2. **Events et API NeoForge d'abord.** Chaque mixin est justifié dans `docs/MIXINS.md` (cible, raison, alternative écartée). Budget issu de l'analyse : 0 access transformer, 0 mixin, au plus 1 (`PlayerModel.setupAnim` TAIL pour les bras, seulement si le prototype le justifie). Les mécanismes sans mixin :
   - anti-kick : attribut `minecraft:generic.gravity` à −100 % pendant le vol, ce qui donne `getMaximumFlyingTicks` = infini ;
   - souris : `CalculatePlayerTurnEvent` avec la sensibilité à −1/3, et lecture de `MouseHandler.getXVelocity()` / `getYVelocity()` ;
   - caméra : `ViewportEvent.ComputeCameraAngles` + `CalculateDetachedCameraDistanceEvent` + `Options.setCameraType` (restaurer le type d'origine, Sable ajoute les siens) ;
   - pose du corps : `RenderPlayerEvent.Pre` / `Post`.
3. **Rendu** : `ModelPart` + RenderTypes vanilla uniquement, sans shader ni RenderType custom, pour rester compatible Iris et shaderpacks. Effets en particules classiques.
4. **Autorité** : le serveur fait foi pour la collection, la paire équipée, la stamina et les sauts de secours. La physique de vol tourne côté client (mouvement joueur client-autoritaire en vanilla). **Aucun paquet par tick** : envoi sur changement seulement, état compact à faible fréquence pour les joueurs distants.
5. **Pas de copie de code** depuis les mods de référence : on décompile pour comprendre une technique, puis on la réimplémente from scratch.

## Architecture prévue (après validation)

Projet généré depuis le MDK NeoForge 1.21.1 officiel (Gradle). Packages par module, alignés sur la table d'architecture du cahier des charges : `data` (définitions d'ailes en registre datapack synchronisé), `player` (Data Attachment avec codec, conservé à la mort), `network` (CustomPacketPayload), `flight` (physique client), `camera`, `render` (RenderLayer joueur), `anim` (mini-lib d'animation procédurale), `loot` (Data Component « énergie ailée » + Global Loot Modifiers + condition maison), `block` (table d'assemblage : Block, BlockEntity, Menu, RecipeType), `structure` (forge de l'End : mort du dragon + StructureTemplate + SavedData), `client.gui`, `config`.

Points transverses qui demandent de lire plusieurs sections du cahier des charges :

- Les stats effectives = stats équipées de la paire + somme des globaux de la collection. Elles sont mises en cache et recalculées seulement quand la collection ou la paire équipée change.
- Toutes les valeurs d'équilibrage vivent dans des JSON de datapack, pas dans le code.
- La physique est écrite en m/s puis convertie en blocs/tick (1 bloc = 1 m, 20 ticks/s). On n'utilise pas la gravité d'entité vanilla.
- Une seule paire de test codée côté datapack pour valider la chaîne de bout en bout au début.
- Les joueurs distants ne reçoivent que la paire et le mode, sur changement. Phase de battement, banking et intensité se déduisent localement du mouvement interpolé, comme dans Fantastic Wings.
- La forge de l'End se déclenche sur `LivingDeathEvent` de l'`EnderDragon`, seulement si `hasPreviouslyKilledDragon()` est déjà vrai (donc à partir du 2e dragon), jamais sur un hook interne de `EndDragonFight`, que YUNG's Better End Island annule et réécrit. Elle flotte dans le vide à ≈ 512 blocs, hors de l'île centrale.
- Les sources de loot (mob ou bloc → pièce) vivent dans un registre datapack synchronisé, `simplywings:loot_source`, parce que les GLM ne sont pas envoyés au client et que JEI en a besoin.
- JEI occupe toute la bande `x < leftPos` de l'inventaire (favoris, historique) : le bouton Ailes va donc sous l'inventaire.

## Commandes

```
./gradlew --no-configuration-cache compileJava   # vérification rapide
./gradlew runClient      # client de dev (dossier run/)
./gradlew runServer      # serveur dédié de dev (run/eula.txt déjà accepté, flat, online-mode=false)
./gradlew build          # jar dans build/libs, à copier dans le dossier mods du pack
```

Il n'y a pas de tests automatisés : la validation se fait en jeu, en solo et sur le serveur dédié.

Chaque étape de l'ordre d'implémentation se teste en solo **et** sur un serveur dédié local avant de passer à la suivante.
