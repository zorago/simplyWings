# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## État du projet

Mod d'ailes standalone pour **NeoForge 1.21.1** (Simply Wings). **Version 1.0.0 (bêta)** depuis la campagne de test du 29 octobre 2026 (`docs/test-runs/2026-10-29/`, rapport `report.md`) : 41 paires, progression de survie, gacha, wiki, son complet. Licence MIT, journal `CHANGELOG.md`, brouillon de page `docs/CURSEFORGE.md`.

- `CAHIER_DES_CHARGES.md` : spec complète, **fait foi** en cas de doute (stats, formules de vol, table d'architecture, points ouverts).
- `HANDOFF.md` : point d'entrée, étapes de travail et ordre d'implémentation.

- `WINGS.md` : catalogue des paires (identité, apparence, ressenti, stats). À mettre à jour avec chaque paire ajoutée ou modifiée.
- `README.md` : vitrine du repo, en anglais (présentation, état, contrôles, commandes, TODO avant release). À tenir à jour à chaque fonctionnalité terminée.
- `CRAFTING.md` : règles des routes de craft (sources vanilla, répétabilité, profondeur max par rareté), objets partagés, registre des drops du mod et récapitulatif des coûts. La route complète de chaque paire est dans `crafting/<id>.md` (matières premières, étapes, recettes dessinées). À mettre à jour avec chaque recette ajoutée ou modifiée.
- `BALANCE.md` : les 7 stats (échelle 0-100, valeurs physiques à 0 et à 100 dans la config serveur `[stats]`), les scores de chaque paire et la répartition des globaux. À tenir à jour à chaque changement de stats ou de physique.
- `wingsRules.md` : carnet de logique du polish des ailes, une paire à la fois avec zorago. Règles générales et par famille (plume, membrane, insecte…), cumulatives : une règle née sur une paire s'applique aux suivantes de sa famille. Journal des passes. À relire avant de polir une paire, à compléter après chaque modification.
- `gatcha.md` : conception du gacha des ailes (invocations, paliers B à SSS et pitié, autel draconique, fontaine aux offrandes, les deux nouvelles structures). Fait foi pour ce chantier.
- `GACHA_ANIMATIONS.md` : cahier des charges des animations d'invocation (3D + 2D, une par palier : B, A, S, SS, SSS, mythiques Night et Light), entrée du futur skill de motion design (format de timeline, livrables, budgets, accessibilité). Son § 0 décrit le prototype du 26 octobre, qui fait référence là où il contredit les fiches.
- `GATCHA_ANIMATION_SOURCE/` : références des animations du gacha pour Blockbench. 7 films MP4 1920 × 1080 à 30 i/s, sans son (singles B, A, S, SS, SSS et deux ×10), et `gacha-motion-code.zip`, le code JavaScript qui les produit : moteur low poly, `state(plan, T)` fonction pure du temps, timelines JSON horodatées, README de portage.
- `GACHA_ALTAR_ANIMATION.md` : animation d'invocation **dans le monde**, autour de l'autel et de ses six flammes (feu, ténèbres, terre, eau, lumière, air), visible en direct par tous les joueurs ; faisabilité (passe de rendu dédiée, sans brouillard, au-dessus de la hauteur max), techniques, timeline du master SSS, sons et musique. Scène générée par `python -I tools/blockbench/altar_anim.py` → `art/gacha/summoning_animation.bbmodel`.
- `SOUND_PLAN.md` : plan du son de la v1, **vanilla d'abord** (règles, corrections, cibles de sonie, périmètre id par id, passes d'écoute, recréation des sons des Furies avant publication). Fait foi pour le son ; l'ancien cahier des charges (≈ 730 fichiers) est archivé dans `docs/archive/SOUND_DESIGN.md`.
- `SOUND_BENCH.md` : cahier des charges du banc de son `tools/sound-bench/` (`npm run dev`, http://localhost:5177 : liste, ▶ Jeu comme en jeu, référence vanilla, mesures, éditeur, candidats, vue 3D des ailes depuis `./gradlew bakeBench`, vue du gacha). Source unique des sons : `tools/sounds/catalog.json` (statut, réglages, couches, verdicts), lu par `python -I tools/sounds/build_sounds.py` (normalisation au RMS max par cible de `tools/sounds/loudness.json`, plafond de pic vrai) ; santé : `python -I tools/sounds/audit.py`. Sources et licences dans `art/sounds/SOURCES.md` (fichiers d'origine dans `art/sounds/source/`, ignoré par git). Sons d'ailes par paire et couches de matière (`wing.<paire>.layer`) joués par `client/WingSounds`, jamais par les particules ; cues du gacha dans `assets/simplywings/gacha/show_sounds.json` (`client/show/ShowSounds`).
- `TEST_PLAN.md` : plan de test complet (campagne autonome, faite le 29 octobre) : règles (ne jamais s'arrêter sur un échec, journal dans `docs/test-runs/`), préparation, blocs de test A à S, corrections entre deux rounds, matrice des effets de bord, liste pour zorago.
- `ToDoTesting.md` : tout ce qui reste à tester et valider en jeu, et les modèles 3D épiques, légendaires et mythiques qui restent à faire.
- `docs/ANALYSE_REFERENCES.md` : analyse des mods de référence et du pack, avec la table d'architecture corrigée. **Elle prime sur la table du cahier des charges** là où elle la corrige (anti-kick, caméra, souris).

Étape en cours : **gacha des ailes** (`gatcha.md`, codé avec des animations provisoires dans le package `gacha`, à tester en jeu : `ToDoTesting.md` § 14 ; prototype de motion design des animations dans `GATCHA_ANIMATION_SOURCE/`, single B validé, décrit dans `GACHA_ANIMATIONS.md` § 0, portage Java en direct à faire), puis particules et passes d'écoute du son (`SOUND_PLAN.md` § 4, dans le banc de son), wiki complet, les 2 structures manquantes, et **tests en jeu** ; la **refonte art est terminée** (les 41 paires polies et validées par zorago le 25 octobre, voir `wingsRules.md`) (point de reprise en tête de `ToDoTesting.md`) ; les routes de craft des 41 paires sont conçues (`crafting/`) et générées en recettes JSON (`data/simplywings/recipe/component` et `assembly`). Analyse validée. Un premier écran Ailes, le bouton d'inventaire et le HUD de stamina existent. Les 188 matériaux et pièces des routes sont enregistrés (`item/MaterialIds`, générés), avec texture et tooltip : description romancée puis énigme d'obtention (`client/ItemLore`, clés `<item>.lore` et `<item>.riddle` dans `en_us.json`, pour tous les items du mod). Quatre essences (`essence/` : nuage, vapeur, éthérée, intensifiée), flacon et 4 réservoirs (`EssenceContainerItem`, composant `simplywings:essence`), récolte en vol dans les 3 dimensions (`flight/CloudHarvest`, config `[cloud]`), substitut de stamina par l'essence intensifiée (`essence/EssenceReserve`, serveur + prédiction client). Table d'assemblage (assemblage seul) et établi de la forge (`forge_bench` : grille 5×5 + énergie ailée), base commune `block/EssenceBench*` avec 4 jauges ; coûts en essences répartis par palier par `tools/crafting_gen/essence.py`. Autel draconique : modèle de bloc généré par `python -I tools/crafting_gen/altar_model.py` (atlas 128 × 128 en UV pixel pour pixel). Fontaine aux offrandes : multibloc octogonal de 21 cellules (`gacha/OfferingFountainBlock`), dessinée par `render/FountainRenderer` depuis `rigs/offering_fountain.bbmodel` généré par `python -I tools/crafting_gen/fountain_model.py`. Compresseur de nuage (`block/CloudCompressor*`, multibloc 3×3×5, rendu par `render/CloudCompressorRenderer` depuis `rigs/cloud_compressor.bbmodel` généré par `tools/crafting_gen/compressor_model.py`, config `[compressor]`). Drops imprégnés faits (`loot/LootSource`, registre `simplywings:loot_source` synchronisé sans ses conditions, `loot/WingedLootModifier`, JSON générés par `tools/crafting_gen/loot.py`). Trois sanctuaires de l'End faits (`structure/EndSanctums` : compteur de morts du dragon en SavedData, 3 positions à 512 blocs et 120°, un compas chacun, réparations sans écraser les blocs existants ; templates `sky_sanctum`, `wing_forge`, `cloud_refinery` générés par `tools/crafting_gen/sanctums.py` ; config `[forge]` ; commandes `/simplywings sanctum <nom>` et `/simplywings dragon`). Plugin JEI fait (`compat/jei`, API 19.64.0.453 en `compileOnly`, JEI en `localRuntime`). Tests à faire : `ToDoTesting.md`. Audit des items : `python tools/crafting_gen/audit.py`.

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

Contrôles : en stationnaire, les touches de déplacement vanilla (avancer, reculer, gauche, droite, saut pour monter, sneak pour descendre). En vol de déplacement, six touches du mod, catégorie Simply Wings (`client/ModKeys`) : monter W, descendre S, tourner à gauche A, tourner à droite D, accélérer Ctrl gauche, freiner Maj gauche ; chaque paire opposée s'annule si les deux sont pressées (accélérer + freiner = ni l'un ni l'autre), et les touches de direction n'ont que 10 % d'effet au frein. Déplacement (Alt) et menu (J) sont aussi des touches du mod. Saut en l'air = saut ailé (coûte `airJumpCost` de stamina, sans limite de nombre) ; sous ce coût, sauts de secours gratuits (2, rechargés au sol) ; double tap **très rapide** (`doubleTapTicks`, 4 ticks) = mode stabilisé, le saut ailé du premier tap est alors remboursé ; Alt gauche = bascule instantanée stabilisé ↔ déplacement en l'air, et au sol (hors stationnaire) Alt **maintenu** 1 s = envol chargé directement en mode déplacement, possible en marchant ou en courant, puis Alt ignoré 1,5 s pour éviter un passage accidentel en stationnaire ; accélérer maintenu = battements ; freiner = frein ; directions = déplacent le point visé, et le cap le suit avec un ressort critique (pas de rotation directe).

Les paramètres propres à chaque paire (cadence et vitesse du stationnaire, identique à l’horizontale et à la verticale : `hover_rate`, `hover_speed`) vivent dans le JSON de l'aile, pas dans la config serveur. Commande de debug : `/simplywings unlock <aile>|unlock all|equip|unequip|stamina|reset|info` (`unlock all` débloque tout le registre). Mythiques Night et Light (`data/FurySet`, `flight/FuryServer`, `client/FuryEffects`, queue `render/TailModel` + `anim/TailAnimator`) : Night possédée = stamina infinie, Light possédée = transmog (bouton « Equip appearance » dans le menu, l'apparence change tout le visuel, rythme des battements compris via `WingAnimator.beat` en boucle ; physique, stats et pouvoirs des Furies restent ceux de la paire équipée), cri en repli faucon, charge au frein 2 s puis sprint = dash + invisibilité. Menu des ailes : touche J (`client/gui/WingsScreen`, liste de toutes les ailes du registre, verrouillées grisées, équiper/retirer via `EquipWing`).

## Pipeline art

Refonte des modèles, textures et animations : section « Pipeline art » du cahier des charges. En résumé : Blockbench + MCP, rig commun `shoulder` / `arm` / `forearm` / `hand` pour toutes les ailes, procédural pour tout ce qui réagit au vol, keyframes `AnimationDefinition` seulement pour les one-shots, boucle visuelle obligatoire (captures Blockbench + captures en jeu) avant de juger une aile, style figé par un skill dans `.claude/skills/` après l'aile hero du designer. Outillage installé (Blockbench + MCP, skills `blockbench-*` et `pxl-pixel-art`). Un modèle Blockbench par paire dans `assets/simplywings/rigs/<modèle>.bbmodel` (toutes ses ailes, noms d'os uniques), à taille réelle, chargé par `render/RigLoader` et animé par **une classe par paire** `render/rig/wings/<Paire>Rig` (enregistrée dans `render/rig/WingRigs`), qui lit l'état de vol `anim/WingMotion` et sa mémoire `anim/RigMemory`, avec la boîte à outils `anim/Cycle`, `render/rig/FeatherWing`, `render/rig/Bone`. Chaque paire est polie comme une entité unique (skill `simplywings-wing-style`). Validation en jeu par le studio : `tools/studio/start.sh` puis `python -I tools/studio/review.py <modèle>` (planches dans `run/studio/sheets/`). **Toutes les sources, règles et pièges sont dans `docs/SOURCES.md`, à relire avant de toucher à une aile.** Scène Blockbench de polish : `python -I tools/blockbench/scene.py <modèle>` (échantillonne la vraie classe de rig via `./gradlew bakeRig -Pwing=<modèle>`, sans lancer Minecraft) écrit `art/scenes/<modèle>.bbmodel` (ignoré par git, regénérable) avec mannequin Steve et toutes les animations ; `--pull` recopie le modèle et la texture retouchés dans `rigs/`. Les membranes de peau s'y affichent grâce au plugin Blockbench `tools/blockbench/simplywings_membranes.js` (installé dans le Blockbench de zorago). Les modèles générés par un `art/wings/<modèle>.rig.js` se reconstruisent dans Blockbench avec `tools/blockbench/wing_builder.js` (`swb.fresh`, `swb.step(swb.all(), ['right'])`, `swb.mirrorAll()`, `swb.save()`), puis il faut réintégrer la texture PNG dans le `.bbmodel`.

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

## Git

- Après chaque modification : commit puis `git push` sur le remote, sans attendre qu'on le demande.
- Ne jamais citer Claude comme co-auteur : aucune ligne `Co-Authored-By` ni mention de Claude dans les commits et les PR.

## Règles non négociables

1. **Zéro dépendance** hors NeoForge : ni Curios, GeckoLib, Architectury, Cloth Config, ni lib de config tierce (la config passe par `ModConfigSpec`). Si une lib manque, on l'écrit dans le mod. Seule exception validée : JEI en intégration optionnelle (API en `compileOnly`, dépendance `optional`, code JEI isolé dans un package `compat.jei` chargé uniquement par `@JeiPlugin`).
2. **Events et API NeoForge d'abord.** Chaque mixin est justifié dans `docs/MIXINS.md` (cible, raison, alternative écartée). Budget issu de l'analyse : 0 access transformer, 0 mixin, au plus 1 (`PlayerModel.setupAnim` TAIL pour les bras, seulement si le prototype le justifie). Les mécanismes sans mixin :
   - anti-kick : attribut `minecraft:generic.gravity` à −100 % pendant le vol, ce qui donne `getMaximumFlyingTicks` = infini ;
   - souris : `CalculatePlayerTurnEvent` avec la sensibilité à −1/3, et lecture de `MouseHandler.getXVelocity()` / `getYVelocity()` ;
   - caméra : `ViewportEvent.ComputeCameraAngles` + `CalculateDetachedCameraDistanceEvent` + `Options.setCameraType` (restaurer le type d'origine, Sable ajoute les siens) ;
   - pose du corps : `RenderPlayerEvent.Pre` / `Post`.
3. **Rendu** : `ModelPart` + RenderTypes vanilla uniquement, sans shader ni RenderType custom, pour rester compatible Iris et shaderpacks. Effets en particules classiques. Seule géométrie hors `ModelPart` : les membranes de peau (`render/rig/Membrane`, définies dans `rigs/<modèle>.membranes.json`), émises en quads dans le même RenderType vanilla que les os de la paire.
4. **Autorité** : le serveur fait foi pour la collection, la paire équipée, la stamina et les sauts de secours. La physique de vol tourne côté client (mouvement joueur client-autoritaire en vanilla). **Aucun paquet par tick** : envoi sur changement seulement, état compact à faible fréquence pour les joueurs distants.
5. **Pas de copie de code** depuis les mods de référence : on décompile pour comprendre une technique, puis on la réimplémente from scratch.

## Architecture prévue (après validation)

Projet généré depuis le MDK NeoForge 1.21.1 officiel (Gradle). Packages par module, alignés sur la table d'architecture du cahier des charges : `data` (définitions d'ailes en registre datapack synchronisé), `player` (Data Attachment avec codec, conservé à la mort), `network` (CustomPacketPayload), `flight` (physique client), `camera`, `render` (RenderLayer joueur), `anim` (mini-lib d'animation procédurale), `loot` (Data Component « énergie ailée » + Global Loot Modifiers + condition maison), `block` (table d'assemblage, établi de la forge, compresseur : Block, BlockEntity, Menu, RecipeType), `essence` (essences, conteneurs, jauges, réserve de stamina), `structure` (trois sanctuaires de l'End : mort du dragon + StructureTemplate + SavedData), `client.gui`, `config`.

Points transverses qui demandent de lire plusieurs sections du cahier des charges :

- Les stats effectives = stats équipées de la paire + somme des globaux de la collection. Elles sont mises en cache et recalculées seulement quand la collection ou la paire équipée change.
- Toutes les valeurs d'équilibrage vivent dans des JSON de datapack, pas dans le code.
- La physique est écrite en m/s puis convertie en blocs/tick (1 bloc = 1 m, 20 ticks/s). On n'utilise pas la gravité d'entité vanilla.
- Une seule paire de test codée côté datapack pour valider la chaîne de bout en bout au début.
- Les joueurs distants ne reçoivent que la paire et le mode, sur changement. Phase de battement, banking et intensité se déduisent localement du mouvement interpolé, comme dans Fantastic Wings.
- Les sanctuaires de l'End se déclenchent sur `LivingDeathEvent` de l'`EnderDragon` (2e mort : Sanctuaire céleste, 3e : Forge, 4e : Raffinerie), comptées dans les SavedData du mod (`hasPreviouslyKilledDragon()` ne sert qu'à rattraper le 1er dragon d'un ancien monde), jamais sur un hook interne de `EndDragonFight`, que YUNG's Better End Island annule et réécrit. Ils flottent dans le vide à ≈ 512 blocs, aux sommets d'un triangle équilatéral.
- Les sources de loot (mob ou bloc → pièce) vivent dans un registre datapack synchronisé, `simplywings:loot_source`, parce que les GLM ne sont pas envoyés au client et que JEI en a besoin.
- JEI occupe toute la bande `x < leftPos` de l'inventaire (favoris, historique) : le bouton Ailes va donc sous l'inventaire.

## Commandes

```
./gradlew --no-configuration-cache compileJava   # vérification rapide
./gradlew runClient      # client de dev (dossier run/)
./gradlew runServer      # serveur dédié de dev (run/eula.txt déjà accepté, flat, online-mode=false)
./gradlew build          # jar dans build/libs, à copier dans le dossier mods du pack
python tools/crafting_gen/gen.py && python tools/crafting_gen/build_crafting.py   # routes crafting/*.md et CRAFTING.md depuis tools/crafting_gen/data.py
python tools/crafting_gen/items.py   # items du mod : MaterialIds.java, modèles, textures pxl, noms + lore + énigme dans en_us.json
python tools/craft_tree/craft_tree.py [id…]   # planches visuelles des routes (crafting/*.md) dans craft-img/
```

`craft_tree.py` lit les textures vanilla dans le jar client-extra de ModDevGradle (`build/moddev/artifacts/`, créé par un premier `./gradlew compileJava`) et celles du mod dans `assets/simplywings/textures/item/`. Une texture absente devient le « ? » de `tools/craft_tree/ui.px.js` (sprites pxl de l'interface, reconstruits automatiquement si `ui/sprites/` est vide). À relancer après chaque modification d'une route ou d'une texture d'item.

Il n'y a pas de tests automatisés : la validation se fait en jeu, en solo et sur le serveur dédié.

Chaque étape de l'ordre d'implémentation se teste en solo **et** sur un serveur dédié local avant de passer à la suivante.
