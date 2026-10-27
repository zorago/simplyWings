> Archive. Vision long terme (≈ 730 fichiers), plus le plan actif. Voir `SOUND_PLAN.md`.

# Cahier des charges du sound design

Spécification de tous les sons de Simply Wings : les ailes (événements de vol communs, familles, 41 paires), les mythiques Night et Light, les blocs, les essences, les sanctuaires et le gacha. Pour chaque son : son déclencheur dans le code, ce qui sonne aujourd'hui (son vanilla provisoire ou rien) et la cible à livrer par un sound designer.

Dernière mise à jour : 26 octobre 2026. Sources : `CLAUDE.md`, `WINGS.md` (identité des paires), `wingsRules.md` (familles), `gatcha.md`, `CAHIER_DES_CHARGES.md`, et le code (`src/main/java/com/zorago/simplywings`). Les ids, noms de fichiers et sous-titres restent en anglais, comme tout le mod.


## 0. Première passe CC0 (26 octobre)

Une première vague de vrais sons remplace une partie des placeholders vanilla. Ce sont des sons **CC0** de Freesound, OpenGameArt et Kenney, listés avec leurs liens dans `art/sounds/SOURCES.md` et construits par `python -I tools/sounds/build_sounds.py` (découpe, fondus, normalisation, OGG mono). Le script réécrit `sounds.json` : il garde les entrées Fury et génère le reste.

- **Ailes** : 5 familles de fichiers, `wing.feather.flap` (6 variations), `wing.membrane.flap` (5), `wing.leather.flap` (4), `wing.insect.flap` (3) et `wing.strong.flap` (5). Chaque paire a son `wing.<paire>.flap` et son `wing.<paire>.flap_strong`, qui pointent vers une famille avec sa hauteur et son volume (table `PAIRS` du script : famille du § 4.1, hauteur propre à la paire, la chouette à 25 % du volume). La famille `mechanism` reste sur la trappe vanilla. Le code ne joue que des ids de paire (`client/WingSounds`, apparence du transmog comprise) : coups du déplacement, saut ailé, bascule, sortie du déplacement, saut de secours, et les battements des joueurs distants (`FlapEvent`, coupés si le joueur est invisible). `flight.takeoff_launch` remplace `ENDER_DRAGON_FLAP` à l'envol chargé.
- **Autel** : `gacha.offer`, `gacha.ignite` (en couche sous le son vanilla de l'élément), `gacha.rune_charge`, `gacha.beam`, `gacha.shockwave`, `gacha.blast`, `gacha.implosion`, `gacha.puff`, `gacha.bell`, `gacha.chime`, `gacha.twinkle`, joués par `ClientShows.tick`. Restent vanilla : le rugissement du dragon (SSS, mythique) et l'activation de balise sous l'onde du pilier.
- **Cartes** : `ui.card_deal` à chaque carte distribuée, `gacha.twinkle` au début de l'anticipation (S et plus), `ui.card_flip` au retournement, puis une couche par palier (`CardDeck.openSound`). `ui.denied` remplace le « non » du villageois.
- **Furies** (deuxième passe, même jour) : le cri de piqué est un sample isolé par zorago (hors CC0, voir `art/sounds/SOURCES.md`), coupé à 5,0 s juste avant l'explosion du piqué, plus 3 s d'écho qui s'éteint, pour Night et Light. Le sample entier plus 3 s d'écho, à hauteur 0,97 (Night) et 1,03 (Light), joue au dash qui rend invisible (`night_fury.invisible`, `light_fury.invisible`) ; `night_fury.dash` (explosion vanilla) est supprimé, et le tir de Light garde `light_fury.blast` en plus. Charges : `night_fury.charge` (grésillement) + `night_fury.crackle`, `light_fury.charge` (montée de plasma en 2,4 s).
- **Vol** (deuxième passe) : `flight.wind`, boucle locale non spatialisée dès 8 m/s en déplacement (volume 0 → 0,75 de 8 à 60 m/s, hauteur 0,8 → 1,25) ; `wing.<paire>.buzz` pour les paires continues (boucle suivie, joueur local et distants à moins de 48 blocs, active en stationnaire ou avec `FLAG_THRUST`, hauteur +25 % avec la vitesse ; Dédale réutilise la boucle du compresseur) ; `flight.landing_soft` et `flight.landing_hard` à l'arrivée au sol (seuil 10 m/s) avec un petit battement de la paire ; le crash avec dégâts joue `flight.landing_hard` pour les autres joueurs. Les `PHANTOM_FLAP` joués par le serveur au saut ailé et au battement sont supprimés (les joueurs proches entendent déjà la paire via `FlapEvent`). Code : `client/FlightLoops`.
- **Blocs et objets** (deuxième passe) : marteau et trempe de l'établi de la forge, infusion d'énergie (`ui.equip`), boucle du compresseur (une instance par compresseur, volume et hauteur suivant sa vitesse), table d'assemblage refaite avec les sons de l'autel (`gacha.rune_charge`, `gacha.ignite`, `gacha.beam`, puis éclat, coup d'aile puissant et cloche, puis `ui.equip` et `ui.confirm`), `essence.pour` (au plus une fois par seconde) et `essence.full` (un conteneur se remplit en vol, entendu par le joueur seul), `ui.equip` aux boutons Équiper et Retirer du menu J.
- **Retours de zorago après écoute (26 octobre)** :
  - rejetés : les 3 coups `wing_flap_large` d'OpenGameArt (plume), l'enregistrement d'aile d'insecte, l'abeille réaliste (« j'ai envie de m'arracher le casque »), les coups 4 et 5 de `wing.strong.flap`, les « tings » au retournement des cartes ;
  - remplacés : plume = les 3 coups de cape seuls ; vrombissement et coups d'insecte **synthétisés**, plus vibrants et « jeu vidéo » ; insectes lents = plume très douce ; cartes = bruits de cartes (glissement, retournement, mélange, carte posée, Kenney Casino), la cloche ne reste qu'à partir de S et plus bas ; sons d'interface plus discrets, graves et doux (passe-bas, hauteur 0,6 à 0,75) ;
  - gardés : membrane, cuir, coups puissants 1 à 3, envol, sons des Furies, vent, atterrissages, autel, blocs, essences.
  - **Règle pour la suite** : stylisé plutôt que réaliste quand le son est continu ou proche de l'oreille ; interface discrète, grave, douce, jamais de tintement aigu ; pour les cartes, des bruits de cartes.

### Sons encore vides (à faire)

| Id | Où le brancher | Piste |
|---|---|---|
| `wing.<famille>.glide` | boucle de plané, sous le vent | souffle filtré par famille, ou le vent avec un passe-bas |
| `wing.<paire>.hover_flap` | battements du stationnaire, sur la phase de `WingAnimator` | version retenue de `flap`, −6 dB |
| `flight.dive` | piqué rapide hors Furies | sifflement montant synthétisé |
| `flight.brake`, `flight.stall`, `flight.tuck` | `FLAG_BRAKE`, `FLAG_STALL`, `FLAG_TUCK` | coup d'aile puissant grave à faible volume, froissement |
| `flight.mode_travel`, `flight.mode_hover` | bascules Alt et double tap | aujourd'hui un simple battement de la paire |
| `flight.takeoff_charge` | charge d'Alt maintenu | souffle qui monte, synthétisable |
| `flight.stamina_low`, `flight.stamina_empty`, `flight.rescue_jump` | HUD de stamina | très discret, grave |
| `wing.<famille>.unfold` / `fold` | sortie et rangement des ailes | froissement par famille |
| `essence.harvest`, `essence.reserve` | `CloudHarvest`, `EssenceReserve` | aspiration douce, une fois par seconde au plus |
| `block.forge_bench.ambient`, `block.cloud_compressor.start` / `stop` / `compress` | établi chargé, cycles du compresseur | braises, soupape |
| `structure.sanctum_appear`, `item.compass.give` / `lock`, `item.summoning_feather.pickup` | `EndSanctums`, compas, plume | grondement lointain, aimant |
| `ui.open` (prêt, pas branché), `ui.select`, `ui.locked`, `ui.transmog`, `ui.unlock.<rareté>` | menu J, wiki, établis | doux et grave, règle ci-dessus |
| `gacha.summary`, `gacha.skip`, `gacha.pity_soft`, `gacha.offer` / `payout` de la fontaine | `AltarScreen`, `FountainMenu` | bruits de cartes, eau |
| musique d'invocation, `gacha.dragon_roar`, `gacha.eclipse`, sons des mythiques du gacha | `ClientShows` | rugissement original, musique à composer |
| `fury.night_idle_*`, `fury.light_idle_*`, `fury.tail` | idles au sol des Furies | petits sons d'animal |
| `night_fury.dive`, `night_fury.invisible`, `light_fury.*` | déjà joués | sample du film : recréation originale obligatoire avant une publication |
- Les sons ont été choisis sur description et enveloppe, sans écoute : validation à l'oreille dans `ToDoTesting.md` § 15.

---

## 1. Objectifs et direction sonore

- **Organique et crédible, dans un monde voxel.** Le visuel est un voxel « Cobblemon » soigné ; le son, lui, vise un rendu de jeu professionnel : de vraies matières (plume, peau, chitine, toile, métal, cristal, roche, eau, feu), enregistrées ou synthétisées proprement, sans effet « cartoon » ni bip électronique. Le grain reste proche des sons vanilla (courts, secs, un peu de réverbération naturelle) pour ne pas jurer à côté d'eux.
- **Chaque paire se reconnaît à l'oreille**, les yeux fermés, à 20 blocs : la chouette presque muette, le pigeon qui claque, l'abeille qui bourdonne, le cuivre qui cliquette, la pierre qui gronde. La signature tient surtout dans le **battement** (le son le plus fréquent) et dans une couche de **plané** propre à la paire.
- **Lisible à distance.** Un joueur qui vole au loin doit s'entendre avant de se voir : attaque nette sur le coup d'aile (transitoire dans 1 à 4 kHz), corps grave court (100 à 300 Hz) pour le poids. Les détails fins (froissement, cliquetis) disparaissent naturellement avec la distance.
- **Aucune boucle agaçante.** Le vol dure longtemps : les boucles (vent, vrombissement, stationnaire) sont discrètes, sans motif répétitif audible, longues (8 à 20 s) ou granulaires, et se modulent avec la vitesse. Aucun son ne doit fatiguer après une heure de vol.
- **Respect du mix Minecraft.** Les ailes passent par la catégorie **PLAYERS**, les blocs par **BLOCKS**, l'interface par **MASTER** (ce que fait `SimpleSoundInstance.forUI`), les cris de dragon entendus de loin par **PLAYERS** aussi. Le mod n'ajoute pas de catégorie (ce serait une extension d'enum, hors de la règle « zéro mixin »). Les niveaux se calent sur des références vanilla (§ 2.4) pour que le curseur « Joueurs » du joueur reste juste.
- **Le silence fait partie du design.** La chouette, l'écureuil volant, le cerf-volant et l'ombre sont volontairement plus discrets que la moyenne : c'est leur identité, pas un oubli.

---

## 2. Contraintes techniques

### 2.1 Format et fichiers

| Point | Règle |
| --- | --- |
| Déclaration | `src/main/resources/assets/simplywings/sounds.json` (existe déjà, 5 entrées Fury). Fichiers dans `assets/simplywings/sounds/`, sous-dossiers par domaine : `flight/`, `wing/<famille>/`, `wing/<paire>/`, `fury/`, `block/`, `essence/`, `gacha/`, `ui/`. |
| Format | OGG Vorbis, qualité 5 à 6 (≈ 128 à 160 kb/s), 44,1 kHz (48 kHz accepté, mais pas de mélange dans une même famille). |
| Canaux | **Mono** pour tout son positionnel (OpenAL ne spatialise que le mono : un fichier stéréo joue sans position ni atténuation). **Stéréo** seulement pour l'interface, les stingers du gacha et le cri du piqué entendu par le joueur lui-même (joué en `forUI`). |
| Bords | Pas de silence en tête (moins de 5 ms avant l'attaque) ; fondu de sortie de 10 à 30 ms ; les boucles bouclent sans clic (passage à zéro, même phase en début et en fin). |
| Streaming | `"stream": true` uniquement pour les fichiers de plus de 10 s (boucles longues, stinger mythique, ambiance de l'autel). Minecraft n'a que quelques canaux de streaming : tout le reste est chargé en mémoire. |
| Préchargement | `"preload": true` pour les battements et le vent (joués très souvent, pas de latence au premier coup). |
| Nommage des fichiers | `snake_case`, numérotés : `wing/owl/flap_1.ogg` … `flap_4.ogg`. |

### 2.2 Convention des ids

| Domaine | Id | Exemple |
| --- | --- | --- |
| Vol, commun à toutes les paires | `simplywings:flight.<événement>` | `flight.wind`, `flight.landing_hard` |
| Famille sonore (couche ou défaut) | `simplywings:wing.<famille>.<événement>` | `wing.feather.flap`, `wing.buzz.hover` |
| Paire | `simplywings:wing.<paire>.<événement>` | `wing.owl.flap`, `wing.kite.glide` |
| Mythiques | `simplywings:<night_fury|light_fury>.<événement>` | ids existants conservés : `night_fury.dive`, `night_fury.dash`, `light_fury.dive`, `light_fury.charge`, `light_fury.blast` |
| Blocs | `simplywings:block.<bloc>.<événement>` | `block.cloud_compressor.loop` |
| Essences, objets | `simplywings:essence.<événement>`, `simplywings:item.<item>.<événement>` | `essence.harvest`, `item.compass.lock` |
| Gacha | `simplywings:gacha.<événement>` | `gacha.reveal.sss` |
| Interface | `simplywings:ui.<événement>` | `ui.equip` |

`<paire>` = le nom du JSON d'aile (`common`, `ancient`, `water`…), pas le nom du modèle. Chaque paire a **toujours** son entrée `wing.<paire>.flap` (et ses autres événements) dans `sounds.json`, même tant qu'elle réutilise la famille : l'entrée pointe alors vers la famille avec `{"name": "simplywings:wing.feather.flap", "type": "event", "pitch": 0.9}`. Le code ne joue donc que des ids de paire, et un vrai son se branche sans toucher au Java. C'est déjà le mécanisme des entrées Fury, qui pointent vers des sons vanilla.

### 2.3 Variations

- **Battements, sauts, atterrissages** : 4 variations (au minimum 3), tirées au hasard par `sounds.json` (`weight` égal). Pas deux fois la même de suite si possible (gestion côté code avec un petit historique, sinon le hasard suffit à 4).
- **Hasard côté code** sur chaque one-shot : hauteur ± 6 %, volume ± 1,5 dB. Aujourd'hui toutes les hauteurs sont fixes (§ 3.1) : à corriger en même temps que l'arrivée des vrais sons.
- **Hauteur liée à la paire** : la cadence (`flap_rate`) ne modifie plus la hauteur une fois les sons propres livrés (aujourd'hui `ClientFlight:659` calcule `0.6 + flapRate × 0.3`, un palliatif pour différencier les paires avec un seul son).
- **Intensité** : le battement a deux variantes d'intensité quand la paire le justifie (`flap` normal, `flap_strong` pour l'envol chargé et les gros coups du démon, de l'aigle, du pigeon au décollage).

### 2.4 Niveaux et références

Les valeurs ci-dessous sont des repères de livraison, à confirmer à l'oreille dans le jeu à côté de la référence vanilla jouée au même volume (curseurs à 100 %).

| Type | Référence vanilla | Cible |
| --- | --- | --- |
| Battement standard | `minecraft:entity.phantom.flap` | pic à −3 dBFS, même sonie perçue que la référence (± 2 dB) |
| Gros battement (démon, aigle, envol) | `minecraft:entity.ender_dragon.flap` | 3 dB sous la référence (le dragon est un boss) |
| Vent de plané (joueur lui-même) | `minecraft:item.elytra.flying` | même sonie à vitesse égale, plus de grave à haute vitesse |
| Vrombissement continu | `minecraft:entity.bee.loop` | 2 à 4 dB au-dessus pour la paire elle-même, à 1 bloc |
| Interface | `minecraft:ui.button.click` | même sonie |
| Stinger gacha SSS / mythique | `minecraft:ui.toast.challenge_complete` | jusqu'à +3 dB, pic à −1 dBTP |

Pic vrai maximal : −1 dBTP partout. Pas de compression de bus agressive : la dynamique doit rester naturelle.

### 2.5 Atténuation

`sounds.json` permet `attenuation_distance` par son (16 blocs par défaut ; c'est la distance à laquelle le son devient inaudible). Repères :

| Son | Distance |
| --- | --- |
| Battement, saut ailé, envol | 32 (on entend un joueur voler avant de le voir) |
| Gros battement (démon, ange, ptérosaure, aigle, mythiques) | 48 |
| Vent de plané, frein, stationnaire | 16 |
| Vrombissement (colibri, abeille, scarabée, méganeure, Dédale) | 24 |
| Atterrissage, crash | 24 |
| Cri du piqué des Furies (joueurs autour) | 96 |
| Dash, tir de plasma | 64 |
| Boucles de blocs (compresseur, autel, fontaine) | 12 à 16 |

### 2.6 Déclenchement : aucun paquet par tick

Les sons suivent les événements déjà présents dans le code, sans nouveau paquet périodique :

- **Battement en déplacement** : le coup est joué en local (`ClientFlight`) puis relayé aux joueurs proches par l'événement `FlapEvent` (environ 2 par seconde pendant qu'on bat des ailes), reçu dans `ClientNet.onFlapEvent`. C'est là que se joue `wing.<paire>.flap` pour les joueurs distants.
- **Poussée continue** (8 battements par seconde et plus) : pas de paquet par battement. Le drapeau `FLAG_THRUST` est synchronisé avec le mode ; une **boucle client** (`AbstractTickableSoundInstance`) suit le joueur et lit ce drapeau à chaque tick, localement.
- **Changements de mode** (sol, stationnaire, déplacement) et drapeaux (`FLAG_BRAKE`, `FLAG_STALL`, `FLAG_TUCK`, `FLAG_THRUST`, `FLAG_SLOW`) : déjà envoyés sur changement. Les one-shots de mode (passage en stationnaire, repli faucon, frein) se déclenchent sur la transition, côté client, pour tous les joueurs.
- **Battements du stationnaire** : ils ne voyagent pas sur le réseau. Le son se cale sur la phase de `WingAnimator`, qui tourne déjà localement pour chaque joueur visible (local et distants).
- **Boucles pilotées par la vitesse** (vent, frémissements, vrombissement) : une instance `AbstractTickableSoundInstance` par joueur volant, qui lit la vitesse interpolée (`getDeltaMovement` côté local, différence de positions pour les distants) et règle volume et hauteur à chaque tick, sans réseau. L'instance s'arrête seule (`stop()`) quand le joueur se pose, sort de portée ou change de paire.
- **Envol chargé** : `TakeoffCharge` / `TakeoffEvent` existent déjà (début et fin de charge) ; le lancement est détecté par `WingAnimator.consumeLaunch()`.
- **Crash** : le paquet `Impact` existe ; le serveur joue le son de crash pour tout le monde (un événement, pas un flux).

### 2.7 Sous-titres

Chaque événement a un sous-titre `subtitles.simplywings.<id>` dans `en_us.json`, en anglais (déjà fait pour les 5 sons Fury). Les variantes d'une même famille partagent un sous-titre générique pour ne pas inonder l'écran : « Wings flap », « Wings buzz », « Wind rushes », « Wings fold ». Les sons forts ou uniques ont le leur (« Fire wings roar », « Copper wings clank », « Night Fury shrieks »).

### 2.8 Points du code actuel à corriger avec les vrais sons

1. **Battement en double** pour les joueurs distants : le serveur joue `PHANTOM_FLAP` (`ServerFlight:190`) **et** le client du voisin joue le son d'effet de la paire (`ClientNet:66` → `WingEffects.strike`). Cible : le serveur ne joue plus rien, `ClientNet.onFlapEvent` joue `wing.<paire>.flap`, qui contient déjà sa couche de matière.
2. **Son couplé aux particules** : `WingEffects.Emitter.sound` n'est appelé que si les particules sont activées et le joueur visible (`WingEffects.active`). Couper les particules coupe donc les sons d'aile. Les sons doivent avoir leur propre chemin.
3. **Toutes les paires battent avec le même son** (`PHANTOM_FLAP`), y compris la chouette « silencieuse » qui ajoute en plus `PARROT_FLY`.
4. **Les ailes à poussée continue sont muettes** (colibri, abeille, scarabée, méganeure, Dédale) : elles n'envoient pas de `Flap` et n'ont pas de boucle.
5. **Éclairs de la charge de Night** (`FuryServer:70-77`) : de vrais `LightningBolt` en `visualOnly`, qui jouent le tonnerre vanilla (catégorie WEATHER, entendu de très loin). À remplacer par `night_fury.charge` (crépitement localisé) et un tonnerre lointain atténué.

---

## 3. Événements de vol communs à toutes les paires

### 3.1 État actuel

| Événement | Déclencheur (code) | Son actuel |
| --- | --- | --- |
| Saut ailé | `ClientFlight.airJump` (l. 349) ; serveur `ServerFlight.onAirJump` | `PHANTOM_FLAP` 0,7 / 1,2 (`ClientFlight:358`) ; aux autres : `PHANTOM_FLAP` 0,5 / 1,2 (`ServerFlight:217`) + effet de paire |
| Saut de secours | `ClientFlight.rescueJump` (l. 362) | `PHANTOM_FLAP` 0,8 / 1,4 (`ClientFlight:368`), local seulement |
| Envol chargé (charge) | `TakeoffCharge`, `animator.charge()` | rien |
| Envol chargé (lancement) | `ClientFlight.takeoff` (l. 413) ; `SimplyWingsClient.takeoffEffects` | `PHANTOM_FLAP` 1 / 0,9 (`ClientFlight:423`) + `ENDER_DRAGON_FLAP` 1,2 / 1,25 (`SimplyWingsClient:168`, tous les joueurs) |
| Bascule stationnaire → déplacement (Alt) | `ClientFlight.toggleTravel` | `PHANTOM_FLAP` 0,8 / 1,1 (`ClientFlight:447`) |
| Bascule déplacement → stationnaire (Alt, ou frein à l'arrêt) | `ClientFlight.stopTravel` | `PHANTOM_FLAP` 0,8 / 0,9 (`ClientFlight:453`) |
| Double tap → stationnaire, double tap → chute | `ClientFlight` l. 330-345 | rien (le saut ailé du premier tap a déjà sonné) |
| Battement en déplacement | `ClientFlight` l. 650-662, paquet `Flap` | `PHANTOM_FLAP` 0,7 / `0.6 + flapRate × 0.3` (`ClientFlight:659`) + effet de paire (`WingEffects.strike`) ; aux autres : `PHANTOM_FLAP` 0,6 / 1,3 (`ServerFlight:190`) + effet de paire (`ClientNet:66`) |
| Poussée continue | `FLAG_THRUST` | rien |
| Battements du stationnaire | `WingAnimator` | rien |
| Plané, vent, piqué | vitesse | rien |
| Repli faucon | `FLAG_TUCK` | rien (sauf cri des Furies) |
| Frein, décrochage | `FLAG_BRAKE`, `FLAG_STALL` | rien |
| Atterrissage | `ClientFlight` l. 734 (sol en déplacement) | rien |
| Crash | `Payloads.Impact` → `ServerFlight.onImpact` | son de blessure vanilla, seulement si dégâts (`flyIntoWall`) |
| Stamina faible / vide | `ClientWings.stamina()` | rien |
| Équiper / retirer (menu J) | `WingsScreen.equip`, `EquipWing` | clic de bouton vanilla |
| Débloquer une paire (item) | `WingItem.use` | rien (message dans la barre d'action) |

### 3.2 Cibles

Priorité : **P1** = entendu en permanence ou à chaque vol, **P2** = fréquent, **P3** = ponctuel.

| Id | Événement et déclencheur cible | Description du son cible | Couches | Durée | Var. | Prio |
| --- | --- | --- | --- | --- | --- | --- |
| `wing.<paire>.flap` | Coup d'aile en déplacement (frappe de `WingStyle.strikeAt`) ; distants via `FlapEvent` | La descente de l'aile : une poussée d'air grave et ronde (« whoomph »), suivie de la matière propre à la paire. Calé sur le point bas du coup, pas sur son début. Spécifié par paire au § 4. | air déplacé (corps grave) + matière + magie éventuelle | 0,25 à 0,6 s | 4 | P1 |
| `wing.<paire>.flap_strong` | Envol chargé, saut ailé, premier coup après la bascule | Même coup, plus ample : grave plus long, matière plus saturée, petite queue de souffle | idem + souffle de sol au décollage | 0,5 à 0,9 s | 3 | P1 |
| `wing.<paire>.hover_flap` | Battements du stationnaire (< 8 battements/s), sur la phase de `WingAnimator` | Version retenue du `flap`, −6 dB, plus courte, sans transitoire dure : on doit pouvoir rester en stationnaire 5 minutes sans fatigue | variante allégée de `flap` | 0,2 à 0,4 s | 4 | P1 |
| `wing.<paire>.buzz` | Boucle, paires continues (colibri, abeille, scarabée, méganeure, Dédale), active avec `FLAG_THRUST` ou en stationnaire | Vrombissement ou réacteur propre à la paire (§ 4). Hauteur +0 à +25 % avec la poussée, volume avec la vitesse ; démarrage (0,3 s de montée) et arrêt (0,5 s de descente) en fondu dans l'instance | une boucle + un one-shot `buzz_start` | boucle 4 à 8 s | 1 + 2 starts | P1 |
| `flight.wind` | Boucle locale dès 8 m/s en déplacement (joueur lui-même seulement) | Le vent aux oreilles, comme l'élytre : bruit large bande filtré, souffle grave à basse vitesse, sifflement aéré au-delà de 40 m/s. Volume 0 → 1 de 8 à 60 m/s, hauteur 0,8 → 1,25 | grave (rumble 60-200 Hz) + souffle (1-4 kHz) | boucle 15-20 s, sans motif | 1 | P1 |
| `flight.wind_fast` | Couche au-dessus de 45 m/s | Turbulence plus serrée, battement de l'air contre les oreilles, sans hurlement | — | boucle 10 s | 1 | P2 |
| `flight.dive` | Piqué : pente < −45° et vitesse > 50 m/s, ou `FLAG_TUCK` | Sifflement montant, tonal discret (comme un faucon pèlerin en piqué, ou un bombardier en sourdine), hauteur liée à la vitesse jusqu'à 110 m/s | sifflement + souffle | boucle | 1 | P2 |
| `flight.flyby` | Joueur distant qui passe à moins de 8 blocs à plus de 25 m/s (calcul local) | Passage d'air rapide avec effet Doppler pré-calculé, plus la matière de la paire en écho | souffle + `flap` filtré | 0,8 à 1,2 s | 3 | P2 |
| `wing.<famille>.glide` | Boucle de plané propre à la famille ou à la paire (frémissement du bord de fuite, voile, flammes) | Voir § 4 ; volume ∝ vitesse, sous le vent général | une boucle | 6 à 12 s | 1 | P2 |
| `flight.tuck` | Entrée en repli faucon (`FLAG_TUCK` posé) | Ailes plaquées d'un coup : froissement sec et court, air coupé net | matière + coupure d'air | 0,3 s | 3 | P2 |
| `flight.brake` | `FLAG_BRAKE` posé en déplacement ; volume ∝ vitesse | L'aile se cabre en parachute : l'air s'engouffre, gros « fwoomp » puis frémissement de la matière tant que le frein dure | one-shot d'ouverture + boucle de frémissement | 0,6 s + boucle | 3 + 1 | P2 |
| `flight.stall` | `FLAG_STALL` posé | Décrochage : l'aile perd l'air, battements mous, petit « flap-flap » désordonné, perte de portance audible | — | 0,8 s | 3 | P2 |
| `flight.mode_travel` | Bascule vers le déplacement (Alt en l'air, `toggleTravel`) | Ailes qui se rabattent vers l'arrière et élan vers l'avant : souffle court qui avance, suivi de `flap_strong` | souffle + `flap_strong` | 0,5 s | 3 | P2 |
| `flight.mode_hover` | Passage en stationnaire (`stopTravel`, double tap) | Les ailes s'ouvrent en grand pour freiner et se mettre à plat : froissement large, air qui s'arrête | matière + souffle inversé | 0,5 s | 3 | P2 |
| `wing.<famille>.unfold` | Ailes qui s'ouvrent (sortie du sol, équipement) | Déploiement : froissement de plumes, peau qui se tend, coque qui s'ouvre, déclic mécanique, selon la famille | matière | 0,4 à 0,8 s | 2 | P2 |
| `wing.<famille>.fold` | Ailes rangées (retour au sol, retrait) | Inverse du déploiement, plus court et plus doux | matière | 0,3 à 0,6 s | 2 | P2 |
| `flight.takeoff_charge` | Boucle pendant la charge d'Alt maintenu (1 s), joueur et voisins (`TakeoffEvent`) | Tension qui monte : ailes qui s'ouvrent et se tendent, inspiration d'air grave, montée de hauteur avec la charge (au carré, comme le visuel) | matière tendue + souffle montant | 1 s, coupé au lâcher | 1 | P2 |
| `flight.takeoff_launch` | Lancement (`consumeLaunch`) | Explosion d'air vers le bas, poussière et herbe soufflées, puis `flap_strong` de la paire. Remplace le `ENDER_DRAGON_FLAP` actuel | impact grave + souffle de sol + paire | 0,8 à 1,2 s | 3 | P1 |
| `flight.air_jump` | Saut ailé (`airJump`) | Pas de son propre : `flap_strong` de la paire, plus un petit souffle sous les pieds | — | — | — | P1 |
| `flight.rescue_jump` | Saut de secours (stamina vide) | Coup d'aile faible et paniqué : matière froissée, peu de corps, souffle court, presque un « hoquet » ; on doit sentir la détresse | matière + respiration discrète | 0,3 s | 3 | P2 |
| `flight.landing_soft` | Arrivée au sol lente (< 10 m/s), depuis le stationnaire ou le déplacement | Ailes qui freinent en parachute puis pieds posés : froissement, pas assourdi (le pas vanilla du bloc joue en plus) | matière + pied | 0,4 s | 3 | P2 |
| `flight.landing_hard` | Arrivée au sol rapide (> 10 m/s) sans crash | Réception lourde, ailes qui claquent en se repliant, glissade | impact + matière | 0,6 s | 3 | P2 |
| `flight.crash` | `ServerFlight.onImpact` avec dégâts (sol ou mur) ; joué par le serveur pour tous | Choc mat et lourd, plumes ou membrane froissées, souffle coupé ; le son de blessure vanilla reste en dessous | impact + matière + souffle coupé | 0,6 à 1 s | 3 | P2 |
| `flight.stamina_low` | Passage sous 20 % (une fois, réarmé au-dessus de 30 %), local seulement | Respiration courte et rythmée du joueur, ou un battement de cœur feutré ; non spatialisé, très discret | voix ou cœur | 1 s | 2 | P2 |
| `flight.stamina_empty` | Stamina à 0, local seulement | Ailes qui lâchent : froissement mou et souffle qui retombe, ton descendant | matière + souffle | 0,8 s | 2 | P2 |
| `flight.rescue_ready` | Sauts de secours rechargés au sol | Non : silencieux (trop fréquent pour mériter un son) | — | — | — | — |
| `ui.equip` / `ui.unequip` | Boutons du menu J (`WingsScreen`) | Bouton : clic doux + froissement court de la famille de la paire choisie | ui + `unfold` / `fold` filtré | 0,3 s | 2 | P3 |
| `ui.unlock.<rareté>` | Déblocage d'une paire (`WingItem.use`, gacha) | Stinger par rareté : rare = carillon court de bois et plume ; épique = carillon cristallin plus large ; légendaire = chœur bref et cuivres doux ; mythique = rugissement lointain de Furie + accord grave | stinger | 1,5 à 3 s | 1 par rareté | P3 |

---

## 4. Familles et paires

### 4.1 Familles sonores

Les familles sonores suivent `wingsRules.md`, avec deux ajustements : le vrombissement réunit le colibri (une plume) et les insectes rapides, et les plumes « de matière » (cristal, cuivre, pierre, feu, orage, ombre, nébuleuse) gardent la base plume mais leur matière domine.

| Famille | Paires | Base sonore commune | Défaut `flap` tant que la paire n'a pas son son |
| --- | --- | --- | --- |
| `feather` (plume) | common, crow, owl, gull, eagle, swift, pigeon, parrot, peacock, patchwork, angel, seraph, yin_yang | Rémiges qui fendent l'air : souffle grave + froissement de plumes. Référence : colombes, corneilles, buses enregistrées au décollage | `minecraft:entity.parrot.fly` mixé à `entity.phantom.flap` |
| `feather_matter` (plume de matière) | amethyst, copper, earth, fire, storm, shadow, nebula | Base plume légère, la matière fait le son | son d'effet actuel de la paire |
| `membrane` (peau à doigts et sans doigts) | bat, demon, ptero, phantom, night, light | Peau tendue qui claque comme une voile ou un parapluie qu'on ouvre d'un coup ; corps grave, pas de froissement | `minecraft:entity.phantom.flap` |
| `glider` (planeur, voile) | squirrel, flying_fish, kite | Peu de battement ; le son vit dans le plané (voile qui fasseye, fourrure dans le vent, nageoires qui vibrent) | `minecraft:entity.phantom.flap` à volume réduit |
| `buzz` (vrombissement) | hummingbird, bee, beetle, ancient | Boucle de vrombissement + démarrage et arrêt ; pas de coup individuel | `minecraft:entity.bee.loop` |
| `soft` (insecte lent) | moth, psyche | Battements poudrés, très feutrés, presque sans grave | `minecraft:entity.phantom.flap` à volume réduit |
| `mechanism` (machine) | ornithopter, daedalus | Bois, métal, engrenages, réacteur : rien d'organique | `minecraft:block.wooden_trapdoor.open` / `minecraft:block.blastfurnace.fire_crackle` |
| `element` (matière non squelettique) | water, air, aurora, forest | La matière seule : eau, vent, lumière, feuillage ; aucun son d'os ou de plume | son d'effet actuel de la paire |

### 4.2 Fiches par paire

Pour chaque paire : identité sonore, battement cible, couches et événements propres, son actuel dans le code, son provisoire à mettre dans `sounds.json` en attendant le vrai son. « Effet actuel » = le son joué par `WingEffects.strike` en plus du `PHANTOM_FLAP` commun ; il n'est joué que si les particules sont actives (§ 2.8).

#### Communes

**1. Ailes communes** · `common` (modèle `basic`) · famille `feather`
- Identité : la plume « neutre » de référence, sur laquelle toutes les autres se comparent. Pigeon biset ordinaire, ni rare ni noble.
- Battement cible (2/s) : souffle moyen, froissement de rémiges propre et court, sans caractère marqué. Le son par défaut de la famille plume.
- Événements propres : aucun. Décrochage fréquent : `flight.stall` doit être bien lisible avec cette paire, c'est elle qui l'apprend au joueur.
- Actuel : `PHANTOM_FLAP` seul. Effet : aucun.
- Provisoire : `minecraft:entity.parrot.fly` (hauteur 0,8) + `minecraft:entity.phantom.flap` (1,2).

#### Rares

**2. Chauve-souris** · `bat` · famille `membrane`
- Identité : peau fine et élastique, nerveuse. Référence : roussette en vol (grandes chauves-souris frugivores), parapluie de poche qu'on secoue.
- Battement cible (2,6/s) : claquement sec et léger de membrane, aigu pour une membrane, peu de grave ; la remontée fait un petit froissement de cuir fin.
- Couches propres : `wing.bat.glide` = frémissement rapide du bord de fuite (fasseyement de nylon fin), plus fort au frein et en piqué. Option : pépiement ultrasonique transposé, très bas dans le mix, au décollage.
- Actuel : `PHANTOM_FLAP`. Effet : aucun.
- Provisoire : `minecraft:entity.bat.takeoff` (hauteur 1,1, volume 0,6).

**3. Corbeau** · `crow` · famille `feather`
- Identité : corvidé, battement « à la rame » profond, doigts écartés. Référence : grand corbeau et corneille noire en vol battu, souffle audible des primaires échancrées.
- Battement cible (2,4/s) : souffle bien rond avec un « vouf » double (les doigts qui se séparent), froissement sec et mat.
- Événements propres : `wing.crow.idle` = deux petits coups d'ailes nerveux au sol (déjà animés), courts et vifs.
- Actuel : `PHANTOM_FLAP` + `PARROT_FLY` 0,4 / 0,8.
- Provisoire : `minecraft:entity.parrot.fly` (0,75).

**4. Chouette** · `owl` · famille `feather`
- Identité : **le vol silencieux**. Référence : chouette effraie, chouette lapone (franges des rémiges qui cassent le bruit).
- Battement cible (1,2/s) : souffle grave, très doux et lent, presque seulement ressenti ; **aucune haute fréquence**, aucun froissement. 8 à 10 dB sous les autres plumes. À 15 blocs, on ne l'entend plus.
- Événements propres : atterrissage en parachute à peine audible ; `wing.owl.idle` (étirement d'aile) = léger froissement de duvet.
- Actuel : `PHANTOM_FLAP` + `PARROT_FLY` (contraire à l'identité).
- Provisoire : `minecraft:entity.phantom.flap` (hauteur 0,6, volume 0,25), sans `parrot.fly`.

**5. Goéland** · `gull` · famille `feather`
- Identité : longues ailes fines de bord de mer, plané en M. Référence : goéland argenté qui plane face au vent.
- Battement cible (1/s) : coup raide et court, peu de froissement, un souffle allongé.
- Couches propres : `wing.gull.glide` = vent de mer dans des rémiges tendues, légers ajustements de poignet (petits froissements espacés, comme dans les rafales). Option : très rare cri de goéland lointain (1 fois sur 40 battements, réglable), à valider avec zorago.
- Actuel : `PHANTOM_FLAP` + `PARROT_FLY`.
- Provisoire : `minecraft:entity.parrot.fly` (0,7) ; plané : rien.

**6. Colibri** · `hummingbird` · famille `buzz`
- Identité : le bourdonnement du colibri (le « hum » qui lui donne son nom anglais), 30 à 40 Hz. Référence : colibri à gorge rubis en surplace.
- Boucle cible `wing.hummingbird.buzz` : vrombissement doux et grave-médium (fondamentale vers 40 Hz, harmoniques autour de 200-400 Hz), plus soyeux qu'un insecte, sans grésillement. Montée de hauteur en poussée.
- Événements propres : `buzz_start` court et pétillant ; `wing.hummingbird.idle` = la courte rafale de bourdonnement au repos (déjà animée) ; petits « tic » secs de changement de direction en stationnaire (optionnel).
- Actuel : rien en déplacement (poussée continue muette), `PHANTOM_FLAP` sur les sauts.
- Provisoire : `minecraft:entity.bee.loop` (hauteur 0,7, volume 0,6).

**7. Aigle** · `eagle` · famille `feather`
- Identité : rapace, remontée lente et coup de descente explosif. Référence : aigle royal au décollage, coups lourds et espacés.
- Battement cible (1/s) : gros « whoomph » grave, attaque forte, froissement de grandes rémiges ; c'est le plus puissant des oiseaux rares. `flap_strong` très marqué (arrachement du sol).
- Couches propres : `wing.eagle.glide` = doigts écartés qui vibrent dans le vent (sifflement doux et irrégulier) ; au frein, alule et doigts qui frémissent.
- Actuel : `PHANTOM_FLAP` + `PARROT_FLY`.
- Provisoire : `minecraft:entity.ender_dragon.flap` (hauteur 1,5, volume 0,4).

**8. Martinet** · `swift` · famille `feather`
- Identité : faux rigide, rafales de petits coups entre de longues glissades. Référence : martinet noir (et son passage sifflé au ras des toits).
- Battement cible (2,8/s) : coup court, sec, aigu, presque un « frrt » ; les rafales doivent s'enchaîner sans lourdeur.
- Couches propres : `flight.flyby` du martinet = sifflement aigu et tranchant (le cri « srii » très filtré, ou l'air sur la faux) ; piqué en V étroit, sifflement plus aigu que la moyenne.
- Actuel : `PHANTOM_FLAP` + `PARROT_FLY`.
- Provisoire : `minecraft:entity.parrot.fly` (hauteur 1,3).

**9. Pigeon voyageur** · `pigeon` · famille `feather`
- Identité : le claquement des ailes au-dessus du dos au décollage, typique du pigeon. Référence : pigeon biset qui s'envole d'une place.
- Battement cible (2,2/s) : souffle moyen avec un **claquement** sec en haut de la remontée, fort au décollage et à basse vitesse, qui disparaît en croisière (variante `flap_clap` sous 15 m/s, `flap` au-dessus). Le sifflement de rémiges en plus, léger.
- Événements propres : `flap_strong` au décollage = deux ou trois claquements ; `wing.pigeon.idle` = ébrouement de plumes au sol.
- Actuel : `PHANTOM_FLAP` + `PARROT_FLY`.
- Provisoire : `minecraft:entity.parrot.fly` (1,0) + `minecraft:block.wool.hit` pour le claquement.

**10. Paon** · `peacock` · famille `feather`
- Identité : vol lourd et laborieux, traîne immense. Référence : paon bleu qui monte se percher, battements bruyants et maladroits.
- Battement cible (1,6/s) : coups lourds et un peu essoufflés, beaucoup de froissement, les plumes de la traîne qui frottent.
- Événements propres : `wing.peacock.train` = **le frisson de la roue** (le « shiver » du vrai paon : vibration rapide et sèche de plumes, 0,8 s) quand la roue se dresse en stationnaire et au sol ; traîne qui balaie en virage (froissement doux, boucle ∝ vitesse de rotation).
- Actuel : `PHANTOM_FLAP` + `PARROT_FLY`.
- Provisoire : `minecraft:entity.parrot.fly` (0,7) ; roue : `minecraft:block.azalea_leaves.break` (hauteur 1,6, volume 0,5).

**11. Perroquet** · `parrot` · famille `feather`
- Identité : clin d'œil au perroquet de Minecraft, coups vifs et agiles. Référence : ara en vol entre les arbres.
- Battement cible (2,6/s) : vif et coloré, froissement léger, plus aigu que le corbeau ; garder une parenté nette avec `entity.parrot.fly` vanilla.
- Événements propres : `wing.parrot.dance` = petits frétillements d'ailes rythmés pendant la danse au sol (sur le tempo de l'animation).
- Actuel : `PHANTOM_FLAP` + `PARROT_FLY`.
- Provisoire : `minecraft:entity.parrot.fly` (1,1).

**12. Abeille** · `bee` · famille `buzz`
- Identité : clin d'œil à l'abeille de Minecraft, quatre ailes accrochées. Référence : bourdon terrestre plutôt qu'abeille domestique, pour plus de rondeur.
- Boucle cible `wing.bee.buzz` : bourdonnement rond et chaud (fondamentale vers 150-200 Hz), parent de `entity.bee.loop` mais plus riche et plus grave (le joueur est plus gros qu'une abeille). Plus ample en stationnaire qu'en déplacement.
- Événements propres : `wing.bee.idle` = court bourdonnement d'échauffement au sol ; `buzz_start` avec une petite montée.
- Actuel : rien en vol (poussée continue).
- Provisoire : `minecraft:entity.bee.loop` (hauteur 0,8).

**13. Papillon de nuit** · `moth` · famille `soft`
- Identité : poudré, nocturne, battements amples et erratiques. Référence : sphinx tête-de-mort et grand paon de nuit (froissement poudreux).
- Battement cible (1,8/s) : froissement poudré très doux, papier de soie, souffle léger, irrégulier (variations de timing de ± 10 % déjà dans l'animation).
- Événements propres : `wing.moth.shiver` = frisson d'échauffement au sol et avant l'envol (vibration rapide et fine, 1 s).
- Actuel : `PHANTOM_FLAP`. Effet : aucun.
- Provisoire : `minecraft:block.wool.hit` (hauteur 1,4, volume 0,4).

**14. Scarabée** · `beetle` · famille `buzz`
- Identité : coques rigides et ailes membraneuses qui vrombissent, pataud. Référence : hanneton et cétoine dorée (vrombissement lourd, choc de chitine).
- Boucle cible `wing.beetle.buzz` : vrombissement grave et un peu rugueux (fondamentale vers 80-120 Hz), avec la vibration des élytres en surcouche.
- Événements propres : `wing.beetle.elytra_open` = déclic de chitine des coques qui s'ouvrent puis froissement des ailes qui se déplient (deux temps, comme l'animation) ; `elytra_close` = l'inverse, avec un « clac » de fermeture ; petits chocs de coque au repos.
- Actuel : rien en vol.
- Provisoire : boucle `minecraft:entity.bee.loop` (hauteur 0,5) ; ouverture : `minecraft:item.armor.equip_elytra` (hauteur 1,3).

**15. Écureuil volant** · `squirrel` · famille `glider`
- Identité : membrane de fourrure tendue entre les bras, bat à peine. Référence : polatouche (silencieux), drap qu'on tend.
- Battement cible (1,2/s) : pompage des bras, à peine audible : tissu doux tiré, sans claquement.
- Couches propres : `wing.squirrel.glide` = vent dans une fourrure (souffle doux, mat, sans sifflement) ; atterrissage en parachute = membrane qui se gonfle (« pouf » feutré).
- Actuel : `PHANTOM_FLAP`.
- Provisoire : `minecraft:block.wool.hit` (hauteur 0,8, volume 0,4).

**16. Poisson volant** · `flying_fish` · famille `glider`
- Identité : décalé et fun, nageoires translucides qui frétillent. Référence : exocet qui fend l'eau, aileron qui vibre comme une carte sur les rayons d'un vélo.
- Battement cible (3/s) : petit claquement humide et élastique, « flip » de nageoire.
- Couches propres : `wing.flying_fish.glide` = vibration rapide et fine des rayons (bourdonnement léger, 25-40 Hz) avec une pointe humide ; `wing.flying_fish.flop` = le « flop » de poisson hors de l'eau au sol (déjà animé).
- Actuel : `PHANTOM_FLAP`.
- Provisoire : `minecraft:entity.tropical_fish.flop` (hauteur 0,9) ; plané : rien.

**17. Phantom** · `phantom` · famille `membrane`
- Identité : la membrane déchirée du mob vanilla, vague de l'épaule à la pointe. Référence directe : les sons du phantom de Minecraft.
- Battement cible (1,3/s) : claquement de membrane lourd et un peu creux, suivi d'un souffle ; en garder la parenté avec `entity.phantom.flap`, mais plus déchiré (fasseyement des trous).
- Couches propres : `wing.phantom.glide` = ondulation lente de raie, membrane trouée qui siffle doucement ; piqué = `entity.phantom.swoop` retravaillé (sa signature).
- Actuel : `PHANTOM_FLAP` + effet `PTERO` = `PHANTOM_FLAP` 0,7 / 0,6.
- Provisoire : `minecraft:entity.phantom.flap` (0,9) ; piqué : `minecraft:entity.phantom.swoop` (1,0, volume 0,5).

**18. Améthyste** · `amethyst` · famille `feather_matter`
- Identité : éclats de cristal qui tintent à chaque battement. Référence : bloc d'améthyste vanilla, carillon de verre, bols chantants.
- Battement cible (1,4/s) : souffle léger + **tintement cristallin** des éclats qui « sonnent » puis s'amortissent (attaque claire, queue de 1 à 1,5 s). Chaque variation est un accord différent de la même gamme (pentatonique), pour que des battements successifs fassent une petite mélodie sans dissonance.
- Couches propres : `wing.amethyst.hover` = éclats qui dérivent et se frôlent, tintements rares et doux.
- Actuel : `PHANTOM_FLAP` + `AMETHYST_BLOCK_CHIME` 0,5 / 1,0.
- Provisoire : `minecraft:block.amethyst_block.chime` (1,0).

**19. Ornithoptère** · `ornithopter` · famille `mechanism`
- Identité : la machine de Léonard de Vinci, bois et toile, manivelle. Référence : gréement de voilier, rouet, métier à tisser.
- Battement cible (1,2/s) : grincement de bois et de corde à la remontée, toile qui se gonfle et le panneau de main qui **claque en butée** (« clac » de bois) à la descente ; la poulie qui tourne (cliquetis de cliquet).
- Couches propres : `wing.ornithopter.glide` = craquements de bois espacés, toile qui vibre ; repli = fagot de baguettes qui s'entrechoquent.
- Actuel : `PHANTOM_FLAP`. Effet : aucun.
- Provisoire : `minecraft:block.wooden_trapdoor.open` (hauteur 0,8, volume 0,5) + `minecraft:block.wool.hit`.

**20. Cerf-volant** · `kite` · famille `glider` (et machine)
- Identité : papier et baguettes, porte au moindre souffle. Référence : cerf-volant de papier, voile de dériveur qui fasseye.
- Battement cible (0,8/s) : tir de ficelle, papier tendu qui « ploc », très léger.
- Couches propres : `wing.kite.glide` = **le son principal** : la voile qui fasseye et claque au vent en permanence, plus fort avec la vitesse ; la queue de rubans qui frissonne (froissement aigu doux).
- Actuel : `PHANTOM_FLAP`. Effet : aucun.
- Provisoire : `minecraft:item.book.page_turn` (hauteur 0,8) ; plané : rien.

**21. Cuivre** · `copper` · famille `feather_matter` (et machine)
- Identité : automate de cuivre de la 1.21, plaques rivetées, coups qui s'arrêtent net. Référence : sons de cuivre vanilla (grille, ampoule, golem de cuivre), armure à plaques.
- Battement cible (1,6/s) : coup sec qui s'arrête en butée (« tchonk » métallique mat), suivi d'un **cliquetis des plaques** ; le cuivre résonne court, pas comme une cloche.
- Couches propres : `wing.copper.glide` = micro-ajustements par crans (petits cliquets espacés) ; vert-de-gris : un léger crissement occasionnel.
- Actuel : `PHANTOM_FLAP` + `COPPER_HIT` 0,6 / 0,8.
- Provisoire : `minecraft:block.copper.hit` (0,8) + `minecraft:block.copper_grate.step` (1,2).

**22. Rafistolées** · `patchwork` · famille `feather`
- Identité : cuir, plumes et ficelle, battements maladroits. Référence : vieux sac de cuir, couture qui tire.
- Battement cible (1,8/s) : mélange désordonné de plume et de cuir, un peu bancal ; une variation sur quatre « rate » (coup mou, comme le coude qui cède).
- Couches propres : `wing.patchwork.glide` = la pièce décousue qui flotte et claque au vent, plus fort avec la vitesse.
- Actuel : `PHANTOM_FLAP`. Effet : aucun.
- Provisoire : `minecraft:entity.parrot.fly` (0,9) + `minecraft:item.armor.equip_leather` (volume 0,3).

#### Épiques

**23. Méganeure** · `ancient` (modèle `dragonfly`) · famille `buzz`
- Identité : libellule géante du Carbonifère, figée dans l'ambre. Référence : anax empereur et aeschne (froissement sec de cellophane des quatre ailes en opposition).
- Boucle cible `wing.ancient.buzz` : vrombissement sec et crépitant, plus « papier » qu'une abeille, avec un battement lent audible (les paires avant et arrière en opposition → légère modulation à 20-25 Hz). Excellente stabilité en stationnaire : la boucle doit être calme.
- Événements propres : `wing.ancient.idle` = vrombissement d'échauffement ; petits craquements d'ambre (grains qui tombent, très discrets, sur l'effet de particules).
- Actuel : rien en vol (poussée continue, effet `FOSSIL` sans son).
- Provisoire : `minecraft:entity.bee.loop` (hauteur 1,3, volume 0,5).

**24. Ptérosaure** · `ptero` · famille `membrane`
- Identité : planeur préhistorique, un doigt immense, peu de battements très amples. Référence : grand voilier en virement de bord, voile qui se gonfle d'un coup ; cris de cigogne et de pélican pour l'animal.
- Battement cible (0,9/s) : gros « vlouf » de peau tendue, grave et long, la pointe qui fouette en retard (second petit claquement 150 ms après).
- Couches propres : `wing.ptero.glide` = frémissement du bord de fuite qui augmente avec la vitesse (voile qui vibre) ; `wing.ptero.idle` = secousse (peau qui claque comme un drap).
- Actuel : `PHANTOM_FLAP` + effet `PTERO` = `PHANTOM_FLAP` 0,7 / 0,6.
- Provisoire : `minecraft:entity.phantom.flap` (0,6) + `minecraft:entity.ender_dragon.flap` (1,6, volume 0,3).

**25. Flamme** · `fire` · famille `feather_matter`
- Identité : un esprit du feu, coups secs et explosifs. Référence : torche qu'on agite, lance-flammes court, bûcher qui s'embrase.
- Battement cible (1,4/s) : **embrasement** : souffle de flamme projeté (« fwoosh ») avec crépitement d'étincelles, attaque explosive.
- Couches propres : `wing.fire.glide` = grondement de flamme continu (feu de cheminée qui tire), volume et grave ∝ vitesse, avec étincelles qui crépitent ; au-dessus de 20 m/s, flamme qui « tire » plus fort. Atterrissage : flamme qui vacille puis reprend.
- Actuel : `PHANTOM_FLAP` + `FIRECHARGE_USE` 0,35 / 1,3.
- Provisoire : `minecraft:item.firecharge.use` (1,2) ; plané : `minecraft:block.fire.ambient`.

**26. Marée** · `water` · famille `element`
- Identité : un esprit de l'eau, vol fluide comme une nage. Référence : rame dans l'eau calme, vague qui roule, raie manta qui frappe la surface.
- Battement cible (1/s) : coup de rame ample et liquide (« splash » doux puis écoulement), gouttes qui retombent.
- Couches propres : `wing.water.glide` = eau qui ruisselle et écume (ruisseau, pas de cascade), éclaboussures du bout des ailes au-dessus de 18 m/s ; `wing.water.hover` = nage lente de raie, clapotis.
- Actuel : `PHANTOM_FLAP` + `BOAT_PADDLE_WATER` 0,5 / 0,9.
- Provisoire : `minecraft:entity.boat.paddle_water` (0,9) ; plané : `minecraft:block.water.ambient` (volume 0,3).

**27. Pierre** · `earth` · famille `feather_matter`
- Identité : un esprit de la terre, ailes de roche lourdes, la pierre veut retourner au sol. Référence : éboulis, meule de pierre, pas d'un golem de fer.
- Battement cible (0,8/s) : grondement grave et lourd, éclats qui **s'entrechoquent** (cailloux secoués dans une main) ; le plus grave de toutes les ailes rares et épiques.
- Couches propres : `wing.earth.glide` = poussière et gravillons qui s'écoulent ; atterrissage = choc lourd vers le bas (petit séisme, grave sous 80 Hz) ; `wing.earth.unfold` = déploiement qui grince (pierre contre pierre).
- Actuel : `PHANTOM_FLAP` + `BASALT_BREAK` 0,6 / 0,6.
- Provisoire : `minecraft:block.basalt.break` (0,6) + `minecraft:block.gravel.break` (0,8).

**28. Vent** · `air` · famille `element`
- Identité : un esprit du vent, presque invisible, léger et rapide. Référence : la breeze et la charge de vent de la 1.21, rafale dans un col.
- Battement cible (1,6/s) : rafale courte, souffle tourbillonnant, aucune matière solide.
- Couches propres : `wing.air.glide` = sifflement de vent qui accélère avec la vitesse, voiles qui se gonflent par bouffées ; au-dessus de 20 m/s, traînées qui sifflent aux pointes. Le vent de la paire remplace en partie `flight.wind` (même famille de son, ne pas doubler).
- Actuel : `PHANTOM_FLAP` + `BREEZE_IDLE_AIR` 0,6 / 1,2.
- Provisoire : `minecraft:entity.breeze.idle_air` (1,2) ; `flap_strong` : `minecraft:entity.wind_charge.wind_burst` (1,4, volume 0,4).

**29. Orage** · `storm` · famille `feather_matter`
- Identité : un esprit de la tempête, éclairs crépitants, nerveux. Référence : arc électrique, bobine Tesla, claquement de fouet électrique.
- Battement cible (1,8/s) : **décharge** : claquement sec d'arc électrique (« ktchak ») avec grésillement qui retombe.
- Couches propres : `wing.storm.glide` = grésillement permanent des pointes, arcs qui sautent au hasard (granulaire, sans boucle audible), plus dense avec la vitesse ; tonnerre très lointain et grave une fois sur 10 battements au-dessus de 40 m/s.
- Actuel : `PHANTOM_FLAP` + `LIGHTNING_BOLT_IMPACT` 0,25 / 1,8.
- Provisoire : `minecraft:entity.lightning_bolt.impact` (1,8, volume 0,25).

**30. Aurore** · `aurora` · famille `element`
- Identité : aurore boréale, voiles de lumière lents et paisibles. Référence : nappes de synthétiseur doux, verre frotté, chant diphonique très lointain.
- Battement cible (0,7/s) : vague de lumière : nappe cristalline qui enfle et retombe, sans attaque ; résonance de verre frotté.
- Couches propres : `wing.aurora.glide` = drone harmonique doux qui respire avec l'animation, accord tenu (majeur, sans tierce trop marquée), volume bas ; c'est le seul son de vol franchement musical.
- Actuel : `PHANTOM_FLAP` + `AMETHYST_BLOCK_RESONATE` 0,5 / 1,2.
- Provisoire : `minecraft:block.amethyst_block.resonate` (1,2).

**31. Ombre** · `shadow` · famille `feather_matter`
- Identité : un esprit des ténèbres, fumée noire, vive et discrète. Référence : souffle étouffé, tissu noir dans l'eau, téléportation d'enderman très assourdie.
- Battement cible (1,2/s) : souffle sourd et étouffé, comme filtré par un rideau ; une pointe de « vwip » inversé (son joué à l'envers) pour l'étrangeté.
- Couches propres : `wing.shadow.glide` = fumée qui se défait (bruissement bas, sifflement grave) ; `wing.shadow.eyes` = petit son humide et feutré quand les yeux clignent (idle, très discret).
- Actuel : `PHANTOM_FLAP` + `ENDERMAN_TELEPORT` 0,25 / 0,6.
- Provisoire : `minecraft:entity.enderman.teleport` (0,6, volume 0,25).

**32. Nébuleuse** · `nebula` · famille `feather_matter`
- Identité : un morceau de ciel nocturne, long voyage entre les étoiles. Référence : scintillement de carillon de vent lointain, boîte à musique ralentie.
- Battement cible (0,8/s) : souffle ample et doux + **pluie d'étoiles** (petits tintements aigus épars, panoramisés dans le mono par le timing).
- Couches propres : `wing.nebula.glide` = scintillement granulaire dont la densité suit la vitesse (la galaxie qui tourne plus vite), sur un souffle profond ; au-dessus de 25 m/s, traînées qui chantent doucement.
- Actuel : `PHANTOM_FLAP` + `AMETHYST_CLUSTER_BREAK` 0,4 / 1,5.
- Provisoire : `minecraft:block.amethyst_cluster.break` (1,5, volume 0,4).

**33. Esprit sylvestre** · `forest` · famille `element`
- Identité : un kodama, ailes de feuilles et de fleurs, calmes. Référence : branche feuillue qu'on secoue, haie au vent, cerisier en fleurs.
- Battement cible (1/s) : bruissement de feuillage ample (« fshhh ») et souple, branches qui fouettent doucement.
- Couches propres : `wing.forest.glide` = feuilles qui frémissent avec leur propre phase, plus fort avec la vitesse ; `wing.forest.bloom` = floraison (petits « pop » doux de bourgeons, idle).
- Actuel : `PHANTOM_FLAP` + `AZALEA_LEAVES_BREAK` 0,6 / 0,9.
- Provisoire : `minecraft:block.azalea_leaves.break` (0,9) ; plané : `minecraft:block.cherry_leaves.hit` (volume 0,3).

#### Légendaires

**34. Ange** · `angel` · famille `feather`
- Identité : endurance et grâce, un battement toutes les 2 secondes. Référence : cygne tuberculé en vol (le « chant » de ses ailes), grand-duc ; aura sacrée.
- Battement cible (0,5/s) : longue montée silencieuse puis **coup bref et puissant** : souffle très ample et grave, froissement de grandes plumes, et un tintement cristallin haut et doux en queue (une seule note claire, réverbérée comme dans une nef).
- Couches propres : `wing.angel.halo` = bourdon très doux de l'auréole (sinus pur, presque inaudible, au repos et en stationnaire) ; `flap_strong` avec une montée chorale très brève (un souffle de chœur, pas une phrase).
- Actuel : `PHANTOM_FLAP` + `AMETHYST_BLOCK_CHIME` 0,5 / 1,4.
- Provisoire : `minecraft:entity.ender_dragon.flap` (1,3, volume 0,4) + `minecraft:block.amethyst_block.chime` (1,4).

**35. Démon** · `demon` · famille `membrane`
- Identité : puissance brute, membrane de cuir qui frappe d'un coup sec et brutal. Référence : bâche de camion qui claque dans la tempête, fouet, souffle de forge.
- Battement cible (0,7/s) : **claquement de cuir violent** (le plus fort du mod hors mythiques), grave puissant, puis souffle de braise ; remontée lente et menaçante (grincement de cuir).
- Couches propres : `wing.demon.glide` = braises qui crépitent sur la membrane, flammèches ; `wing.demon.idle` = intimidation (ailes grandes ouvertes : claquement + grondement de forge).
- Actuel : `PHANTOM_FLAP` + `BLAZE_SHOOT` 0,45 / 0,55.
- Provisoire : `minecraft:entity.ender_dragon.flap` (1,1, volume 0,5) + `minecraft:entity.blaze.shoot` (0,55, volume 0,3).

**36. Psyché** · `psyche` · famille `soft`
- Identité : stabilité absolue, flammes d'âme bleu-cyan en ailes de papillon, apesanteur. Référence : flamme d'âme vanilla, respiration lente, verre soufflé.
- Battement cible (0,6/s) : respiration : souffle doux qui inspire à la montée et expire à la descente, avec un voile de flamme froide (crépitement très fin, sans chaleur) et un écho d'âme.
- Couches propres : `wing.psyche.hover` = apesanteur : drone très calme, légèrement ondulant, qui donne la sensation d'immobilité ; `wing.psyche.lamp` = tintement doux de la lampe en pendule (idle) ; âme qui s'échappe parfois (`particle.soul_escape` retravaillé).
- Actuel : `PHANTOM_FLAP` + `SOUL_ESCAPE` 0,6 / 1,2.
- Provisoire : `minecraft:particle.soul_escape` (1,2).

**37. Séraphin** · `seraph` · famille `feather`
- Identité : la vitesse céleste, six ailes qui battent comme une seule. Référence : envol d'une volée de pigeons (plusieurs ailes en même temps), cloche et cuivre.
- Battement cible (0,9/s) : souffle large et épais (trois paires qui se superposent avec quelques millisecondes d'écart, effet « chorus » naturel) + **anneau de lumière** : note dorée cuivrée qui se propage (un beacon plus noble).
- Couches propres : `wing.seraph.glide` = au-dessus de 25 m/s, traînées dorées qui chantent (sifflement harmonique) ; le séraphin doit avoir le plus beau `flight.flyby` du mod.
- Actuel : `PHANTOM_FLAP` + `BEACON_POWER_SELECT` 0,35 / 1,6.
- Provisoire : `minecraft:entity.parrot.fly` (0,7) + `minecraft:block.beacon.power_select` (1,6, volume 0,35).

**38. Dédale** · `daedalus` · famille `mechanism`
- Identité : la machine de l'inventeur, laiton et acier, deux réacteurs vectorisés ; rien ne bat, tout pousse. Référence : jetpack et réacteur de démonstration (les ailes EXO-7 du Faucon), chalumeau, turbine.
- Boucle cible `wing.daedalus.buzz` : **réacteur** : souffle de combustion grave et régulier + sifflement de turbine qui monte avec la poussée ; en stationnaire, poussée vers le bas plus étouffée.
- Événements propres : `buzz_start` = allumage (clic d'allumeur, « whump » de gaz) ; `buzz_stop` = extinction et turbine qui ralentit ; `wing.daedalus.vector` = sifflements courts des tuyères qui pivotent en virage serré ; `wing.daedalus.idle` = ratés de réacteur (toux, pétarade) ; repli et déploiement de contrôle = servomoteurs et plaques d'acier.
- Actuel : rien en vol (poussée continue, effet `THRUSTERS` sans son).
- Provisoire : `minecraft:block.blastfurnace.fire_crackle` (boucle émulée) + `minecraft:entity.firework_rocket.launch` (0,6) pour l'allumage.

**39. Yin et Yang** · `yin_yang` · famille `feather`
- Identité : l'équilibre, deux ailes asymétriques, le Yin suit le Yang avec un léger retard. Référence : bol chantant tibétain, gong frappé doucement.
- Battement cible (0,9/s) : souffle calme et régulier + **deux tintements**, grave pour le Yin et aigu pour le Yang, le second en léger retard (quinte juste ou octave, à valider) ; jamais dissonant.
- Couches propres : `wing.yin_yang.orbit` = les deux sphères qui orbitent derrière le dos : très léger battement acoustique entre deux sinus proches (« wah-wah » lent), uniquement en stationnaire.
- Actuel : `PHANTOM_FLAP` + `AMETHYST_BLOCK_CHIME` 0,5 / 0,6 et 0,5 / 1,7 (déjà les deux tintements).
- Provisoire : identique à l'actuel.

#### Mythiques

**40. Night** · `night` · famille `membrane` — détails au § 5.
**41. Light** · `light` · famille `membrane` — détails au § 5.

### 4.3 Récapitulatif : sons actuels par paire

| Effet (`WingStyle.Effects`) | Paires | Son d'effet actuel (`WingEffects.java`) |
| --- | --- | --- |
| `NONE` | common, bat, hummingbird, bee, moth, beetle, squirrel, flying_fish, ornithopter, kite, patchwork, night, light | aucun |
| `FEATHERS` | crow, owl, gull, eagle, swift, pigeon, peacock, parrot | `PARROT_FLY` 0,4 / 0,8 (l. 476) |
| `ANGEL` | angel | `AMETHYST_BLOCK_CHIME` 0,5 / 1,4 (l. 349) |
| `DEMON` | demon | `BLAZE_SHOOT` 0,45 / 0,55 (l. 363) |
| `SERAPH` | seraph | `BEACON_POWER_SELECT` 0,35 / 1,6 (l. 375) |
| `PSYCHE` | psyche | `SOUL_ESCAPE` 0,6 / 1,2 (l. 382) |
| `FIRE` | fire | `FIRECHARGE_USE` 0,35 / 1,3 (l. 393) |
| `WATER` | water | `BOAT_PADDLE_WATER` 0,5 / 0,9 (l. 404) |
| `EARTH` | earth | `BASALT_BREAK` 0,6 / 0,6 (l. 415) |
| `AIR` | air | `BREEZE_IDLE_AIR` 0,6 / 1,2 (l. 427) |
| `STORM` | storm | `LIGHTNING_BOLT_IMPACT` 0,25 / 1,8 (l. 434) |
| `AURORA` | aurora | `AMETHYST_BLOCK_RESONATE` 0,5 / 1,2 (l. 441) |
| `SHADOW` | shadow | `ENDERMAN_TELEPORT` 0,25 / 0,6 (l. 452) |
| `NEBULA` | nebula | `AMETHYST_CLUSTER_BREAK` 0,4 / 1,5 (l. 462) |
| `FOREST` | forest | `AZALEA_LEAVES_BREAK` 0,6 / 0,9 (l. 469) |
| `AMETHYST` | amethyst | `AMETHYST_BLOCK_CHIME` 0,5 / 1,0 (l. 482) |
| `COPPER` | copper | `COPPER_HIT` 0,6 / 0,8 (l. 489) |
| `YIN_YANG` | yin_yang | `AMETHYST_BLOCK_CHIME` 0,5 / 0,6 + 0,5 / 1,7 (l. 503-504) |
| `PTERO` | ptero, phantom | `PHANTOM_FLAP` 0,7 / 0,6 (l. 511) |
| `FOSSIL` | ancient | aucun (particules seulement) |
| `THRUSTERS` | daedalus | aucun (particules continues seulement) |

Le volume de ces sons est multiplié par `0.4 + 0.6 × densité` (densité de particules : 100 % en déplacement, 25 % en stationnaire, 10 % au sol et en saut ailé).

---

## 5. Mythiques : Night et Light

Clin d'œil à *Dragons* (Furie Nocturne et Furie Éclair). Les sons des films sont protégés : **recréation originale obligatoire**, jamais d'extrait, même retravaillé. Les ids existants sont conservés.

| Id | Déclencheur | Actuel (`sounds.json`) | Cible |
| --- | --- | --- | --- |
| `wing.night.flap` / `wing.light.flap` | Battement (1,2/s) | `PHANTOM_FLAP` (commun) | Grande membrane de dragon : claquement profond, souple, très puissant mais pas brutal (le démon est brutal, la Furie est athlétique), petite paire arrière en écho 80 ms après. Night plus mat et sombre, Light plus aérien, avec un très léger scintillement en queue. |
| `night_fury.dive` / `light_fury.dive` | Le piqué dépasse la vitesse max (`FuryEffects.tick`, transition vers `FLAG_TUCK` ou `terminalDive`, cooldown) ; le joueur l'entend en `forUI` (non spatialisé), les autres via `EntityBoundSoundInstance` à volume 1,5 | `minecraft:entity.phantom.swoop`, hauteur 1,6 (Night) et 1,9 (Light) | **Le cri du piqué** : hurlement strident qui monte, mélange de cri de rapace (faucon pèlerin, buse), de sifflement de moteur à réaction et de grincement de métal, avec un Doppler intégré. 2,5 à 3 s. Version Light plus aiguë et plus pure. Livrer une version stéréo (joueur) et une mono (joueurs autour, atténuation 96). |
| `night_fury.charge` *(nouveau)* | Frein maintenu en déplacement (`FuryEvent.CHARGE_START` → `STOP`) | rien pour Night ; les éclairs visuels de `FuryServer:70-77` jouent le tonnerre vanilla | Montée électrique : crépitement d'étincelles sur le corps, grondement grave qui enfle sur 2 s, épines qui s'allument (petits « tic » successifs de la base vers la queue). Boucle de maintien quand la charge est pleine. Tonnerre lointain et sourd en remplacement des vrais éclairs (catégorie PLAYERS, atténuation 48). |
| `light_fury.charge` | `CHARGE_START`, suivi du joueur (`EntityBoundSoundInstance`) | `minecraft:block.beacon.activate`, hauteur 1,4 | Plasma qui converge vers la gueule : aspiration montante, bourdonnement ionisé, sifflement aigu qui monte jusqu'à saturation à 2 s. |
| `night_fury.dash` | Dash (sprint dans la demi-seconde après la charge) | `minecraft:entity.generic.explode`, hauteur 0,5, volume 0,6 | Bruit sourd et profond (onde de pression, pas d'explosion), coup d'aile énorme puis aspiration qui se referme : il disparaît. |
| `light_fury.blast` | Dash de Light (tir de plasma, explosion à ≈ 30 blocs) | `minecraft:entity.generic.explode`, hauteur 1,2, volume 0,8 | Tir : « pew » grave et rond de plasma (pas laser), sifflement qui part devant, puis explosion bleue lointaine (claquement + traîne cristalline). Deux sons : `light_fury.shot` à la gueule, `light_fury.blast` au point d'impact (atténuation 64). |
| `fury.invisible_start` / `fury.invisible_end` *(nouveaux)* | Effet d'invisibilité posé (15 s) puis fini, joueur lui-même | rien | Début : souffle qui s'éteint, comme un son passé en sourdine ; fin : retour du son, léger « pop » d'air. Pendant l'invisibilité, les sons d'ailes du joueur sont joués à −12 dB pour lui et **coupés pour les autres** (furtivité cohérente avec le visuel). |
| `fury.night_idle_*`, `fury.light_idle_*` *(nouveaux)* | Idles au sol (8 pour Night, 7 pour Light, déjà animés) | rien | Petits sons d'animal, sans voix humaine : ronronnement grave, ébrouement, queue qui fouette (claquement de cuir), clic mécanique de la prothèse de Night (métal et cuir), épines hérissées (chitine), bâillement. Light : souffles plus doux, scintillements, toilette. |
| `fury.tail` *(nouveau)* | Queue qui traîne au sol et fouette en virage | rien | Frottement de cuir sur le sol en marchant (très bas) ; fouet discret quand la queue sert de gouvernail en virage serré. |
| Stamina infinie (Night possédée) | — | — | Aucun son : `flight.stamina_low` et `flight.stamina_empty` ne jouent jamais. |
| Transmog (Light possédée) | Bouton « Equip appearance » | clic vanilla | `ui.transmog` : scintillement court et irisé. **Règle** : en transmog, les sons de battement suivent l'**apparence** (comme le rythme visuel), la physique reste celle de la paire équipée. Le code choisit donc l'id `wing.<look>.flap`. |

---

## 6. Le reste du mod

### 6.1 Blocs : sons de matériau (`SoundType`)

| Bloc | `SoundType` actuel (`ModBlocks`) | Cible |
| --- | --- | --- |
| Table d'assemblage | `STONE` (l. 36) | Garder `STONE`. |
| Établi de la forge | `ANVIL` (l. 51) | Garder `ANVIL` pour poser et casser, mais remplacer les pas (l'enclume est trop forte pour un pas) par `METAL`. Nécessite un `SoundType` maison (construit dans le code, sans dépendance). |
| Compresseur de nuage | `COPPER` (l. 66) | Garder. |
| Autel draconique | `DEEPSLATE_BRICKS` (l. 83) | Garder. |
| Fontaine aux offrandes | `AMETHYST` (l. 96) | Garder. |

### 6.2 Blocs : sons d'activité

| Id | Déclencheur | Actuel | Cible | Prio |
| --- | --- | --- | --- | --- |
| `block.assembly_table.charge` | Assemblage, phase de charge (`AssemblyTableEffects.charge`) | `BEACON_POWER_SELECT` au début (l. 109), `AMETHYST_BLOCK_RESONATE` montant toutes les 10 ticks (l. 106), `RESPAWN_ANCHOR_CHARGE` à 20, 40, 60 ticks (l. 112), `BEACON_ACTIVATE` 12 ticks avant l'apogée (l. 115) | Une montée continue d'un seul tenant (un fichier calé sur `ASSEMBLY_TICKS`) : nappe cristalline qui monte, pulsations d'énergie ailée à 1, 2 et 3 s, battements d'ailes fantômes de plus en plus forts, aspiration finale | P3 |
| `block.assembly_table.climax` | Apogée (`climax`) | `TOTEM_USE` + `FIREWORK_ROCKET_LARGE_BLAST` + `ENDER_DRAGON_FLAP` + `UI_TOAST_CHALLENGE_COMPLETE` (l. 133-136) | Un impact unique : flash sonore (souffle inversé coupé net), grand coup d'ailes, carillon de réussite. Remplace les 4 sons empilés | P3 |
| `block.assembly_table.aftermath` | Après l'apogée (`aftermath`) | `ENDER_DRAGON_FLAP` à 4 ticks (l. 143), `PLAYER_LEVELUP` à 10 ticks (l. 146) | Les ailes de la paire assemblée battent deux fois au-dessus de la table (utiliser `wing.<paire>.flap` de la paire fabriquée !), puis note de récompense douce | P3 |
| `block.forge_bench.hammer` | Fabrication d'un composant, 3 coups (`ForgeBenchEffects.component`, ticks 0, 6, 12) | `ANVIL_USE` 0,5 / 0,9-1,14 (l. 44) | Marteau sur métal chaud : coups mats et chantants, moins agressifs que l'enclume vanilla, 3 variations qui montent | P3 |
| `block.forge_bench.quench` | Trempe (tick 14) | `LAVA_EXTINGUISH` 0,6 / 1,4 (l. 53) | Trempe à l'eau : sifflement de vapeur court + bouillonnement | P3 |
| `block.forge_bench.energy` | Infusion d'énergie ailée (`energy`, tick 0) | `ENCHANTMENT_TABLE_USE` 1 / 1,2 + `AMETHYST_BLOCK_CHIME` 1 / 1,5 (l. 66-67) | Souffle de plumes qui tourbillonne et se pose, carillon d'énergie | P3 |
| `block.forge_bench.ambient` | Établi chargé (`fill() > 0`), aléatoire | rien (fumée et braises visuelles seulement) | Braises qui crépitent, très bas, aléatoire (1 son toutes les 5 à 15 s), atténuation 8 | P3 |
| `block.cloud_compressor.loop` | Compresseur en marche (boucle, `AbstractTickableSoundInstance` au centre du multibloc) | `PISTON_EXTEND` aléatoire (l. 38 de `CloudCompressorEffects`), `BUBBLE_COLUMN_UPWARDS_AMBIENT` aléatoire (l. 41) | Boucle de machine à vapeur douce : engrenages, piston lent et régulier, bouillonnement de nuage, soupape qui siffle de temps en temps. Rythme calé sur la vitesse d'animation du rendu. Doux : on travaille à côté pendant longtemps | P2 |
| `block.cloud_compressor.start` / `stop` | Début et fin de cycle | rien | Montée en pression (soupape, engrenages qui s'emballent) ; décompression (soupir de vapeur) | P3 |
| `block.cloud_compressor.compress` | Une unité compressée (`CloudCompressorBlockEntity.compress`) | `BREWING_STAND_BREW` 0,5 / 0,7, une fois sur 4 (l. 138) | Coup de presse étouffé + condensation (goutte lourde dans le réservoir) | P3 |

### 6.3 Essences, objets, structures

| Id | Déclencheur | Actuel | Cible | Prio |
| --- | --- | --- | --- | --- |
| `essence.harvest` | `CloudHarvest`, une fois par seconde, conteneur qui se remplit pendant le vol (joueur lui-même, non spatialisé) | rien | Aspiration courte de nuage dans du verre : souffle humide aspiré + léger tintement de verre ; volume très bas, variation par essence (nuage : doux, vapeur : plus chaud et sifflant, éthérée : cristallin) | P2 |
| `essence.full` | Un conteneur devient plein | rien | Bouchon : petit « tok » de verre + tintement | P3 |
| `essence.reserve` | `EssenceReserve` : l'essence intensifiée remplace la stamina (premier tick d'usage) | rien | Montée d'énergie brève (inspiration claire), pour signaler que l'on puise dans la réserve | P3 |
| `essence.pour` | Flacon ou réservoir vidé dans une jauge de table ou d'établi | rien | Liquide gazeux versé : glouglou léger et aérien | P3 |
| `item.summoning_feather.pickup` | Ramassage d'une plume d'invocation | ramassage vanilla | Ramassage vanilla + souffle doré très court (la plume est un indice du mod : elle doit intriguer) | P3 |
| `structure.sanctum_appear` | Apparition d'un sanctuaire après la mort du dragon (`EndSanctums`) | rien | Grondement lointain et réverbéré dans tout l'End (catégorie AMBIENT, non spatialisé pour les joueurs dans l'End), accord grave qui se résout | P3 |
| `item.compass.give` / `item.compass.lock` | Compas reçu (`EndSanctums.giveCompasses`) ; compas tenu qui pointe | rien | Réception : tintement magnétique ; tenu près du sanctuaire (< 64 blocs) : léger bourdonnement d'aimant | P3 |

### 6.4 Autel draconique et invocation (gacha)

Tous les sons de l'écran passent par `AltarScreen.sound` (`SimpleSoundInstance.forUI`, catégorie MASTER, stéréo autorisée). Attention : la méthode prend `(event, pitch, volume)`, dans cet ordre. Direction : motion design low poly, sfx par étape, chaque palier plus spectaculaire que le précédent ; les originaux sont montés sur les vidéos MP4 et découpés en `.ogg` par étape (`gatcha.md`).

| Id | Déclencheur | Actuel | Cible | Prio |
| --- | --- | --- | --- | --- |
| `block.draconic_altar.ambient` | Boucle au bloc (atténuation 12) | rien | Flammes violettes qui ronflent doucement, cristaux de l'End en suspension qui tintent, souffle lointain de dragon toutes les 20 à 40 s | P2 |
| `gacha.open` | Ouverture de l'écran | rien | Flammes de l'autel qui s'embrasent, nappe grave qui s'ouvre, battement d'ailes lointain | P2 |
| `gacha.denied` | Tirage sans assez de plumes (`AltarScreen.pull`, l. 485) | `VILLAGER_NO` (hauteur 1, volume 0,6) | « Non » sobre : plume qui s'éteint, son mat et court (pas de villageois) | P3 |
| `gacha.launch` | Début du tirage (`AltarScreen` l. 89) | `BEACON_ACTIVATE` (hauteur 1,5, volume 0,8) | La plume brûle et s'élève : allumage + souffle ascendant, 1 s | P1 |
| `gacha.launch_rare` | Meilleur palier ≥ S (l. 91) | `ENDER_EYE_LAUNCH` (0,6 / 0,7) | La comète bascule de couleur : sifflement qui accélère, montée harmonique | P1 |
| `gacha.flash` | Flash à 82 % de l'animation (l. 304) | `AMETHYST_BLOCK_RESONATE` (1,6 / 1) | Impact lumineux : coup sourd + éclat cristallin | P1 |
| `gacha.reveal.b` | Carte B (l. 112) | `ITEM_PICKUP` (1,2 / 0,6) | Retournement de carte doux, petit souffle | P1 |
| `gacha.reveal.a` | Carte A (l. 113) | `NOTE_BLOCK_CHIME` (1,4 / 0,8) | Retournement + carillon bleu bref | P1 |
| `gacha.reveal.s` | Carte S (l. 115-116) | `AMETHYST_BLOCK_CHIME` (0,8 / 1) + `NOTE_BLOCK_BELL` (1 / 0,9) | Rayons : accord violet ascendant, cloche, 1,5 s | P1 |
| `gacha.reveal.ss` | Carte SS (l. 118) | `UI_TOAST_CHALLENGE_COMPLETE` (1,1 / 0,9) | Or : fanfare courte de cuivres doux et carillons, grand coup d'ailes, 2,5 s | P1 |
| `gacha.reveal.sss` | Carte SSS (l. 120-121) | `UI_TOAST_CHALLENGE_COMPLETE` (0,8 / 1) + `ENDER_DRAGON_GROWL` (1,2 / 0,35) | Rouge sang irisé : silence d'une demi-seconde, puis accord orchestral grave, chœur bref, rugissement draconique lointain, 3 à 4 s | P1 |
| `gacha.reveal.mythic` | Mythique (l. 124-125) | `END_PORTAL_SPAWN` (1 / 0,5) + `ENDER_DRAGON_GROWL` (0,7 / 0,5) | Noir et blanc entrelacés : le cri des deux Furies (recréé) qui se croisent, plasma, coupure, puis thème final grave ; 5 à 8 s, `stream: true` | P1 |
| `gacha.summary` | Récapitulatif ×10 | rien | Cartes qui se rangent (glissements courts en cascade), accord de conclusion selon le meilleur palier | P2 |
| `gacha.skip` | Bouton « passer » | rien | Souffle coupé court | P3 |
| `gacha.pity_soft` | Un compteur entre en garantie douce | rien | Tintement discret sur la jauge | P3 |

#### Sons des animations finales (`GACHA_ANIMATIONS.md`)

Le tableau ci-dessus couvre l'animation provisoire. Les animations finales découpent le son par temps fort ; leurs ids remplacent `gacha.launch`, `gacha.launch_rare`, `gacha.flash` et `gacha.reveal.mythic`, et sont déclenchés par la timeline JSON de chaque palier (champ `sounds`). Les cartes `gacha.reveal.b` à `gacha.reveal.sss` restent.

| Id | Temps fort | Cible | Prio |
| --- | --- | --- | --- |
| `gacha.launch.small` | Embrasement et envol, B et A | Plume qui s'enflamme (allumage court, crépitement), souffle qui monte, 1,2 s | P1 |
| `gacha.launch.rare` | Embrasement et envol, S et SS | Même base, flammes plus hautes, nappe qui s'ouvre, battement de cœur grave, 1,8 s | P1 |
| `gacha.launch.legendary` | Embrasement et envol, SSS et mythiques | Colonne de flammes, grondement de sol, chœur grave tenu, 2,4 s | P1 |
| `gacha.escalate.s` | Battement bleu → violet | Contraction (aspiration courte) puis éclat cristallin violet, montée d'une quinte | P1 |
| `gacha.escalate.ss` | Fente violet → or | Fissure de verre, noyau d'or qui sort : coup de cymbale inversée + carillon chaud | P1 |
| `gacha.escalate.sss` | Ciel qui se fissure (rouge) | Craquement énorme qui court d'un bord à l'autre (stéréo), basse qui tombe d'une octave | P1 |
| `gacha.dragon_roar` | Passage du dragon de l'End (SSS) | Rugissement draconique original (pas le dragon vanilla), lointain puis proche, 1,5 s | P1 |
| `gacha.impact.s` / `.ss` / `.sss` | Impact final de la comète | Coup sourd + éclat ; plus grave, plus large et plus long à chaque palier (0,6 / 0,9 / 1,4 s) | P1 |
| `gacha.star` | Chaque étoile qui s'allume | Tintement court ; la hauteur monte d'un demi-ton à chaque étoile | P2 |
| `gacha.eclipse` | Éclipse des mythiques (Night) | Coupure nette de tout le mix (le code coupe les autres sons), puis sub grave à peine audible, 1,2 s | P1 |
| `gacha.mythic.night.wingbeat` | Battement lointain dans le noir | Un seul battement de grande membrane, très réverbéré | P1 |
| `gacha.mythic.night.plasma` | Tir de plasma | Sifflement montant original (création, pas d'emprunt au film), puis détonation électrique | P1 |
| `gacha.mythic.night.reveal` | Révélation de Night | Thème grave et sombre, arcs électriques, 4 s, `stream: true` | P1 |
| `gacha.mythic.light.fade` | Le rouge qui se décolore (Light) | Accord qui se résout vers le majeur, filtres qui s'ouvrent, 0,6 s | P1 |
| `gacha.mythic.light.shimmer` | Montée au blanc et camouflage | Nappe aérienne irisée (verre frotté, harmoniques), crescendo de 1 s | P1 |
| `gacha.mythic.light.reveal` | Révélation de Light | Thème lumineux, carillons et chœur clair, 4 s, `stream: true` | P1 |

#### Événements du prototype de motion design (26 octobre 2026)

Le prototype des animations (`GACHA_ANIMATIONS.md` § 0, sources dans `GATCHA_ANIMATION_SOURCE/`) écrit, pour chaque film, une timeline JSON avec tous ses événements horodatés (`out/<film>.timeline.json` dans `gacha-motion-code.zip`). Les vidéos sont muettes : ces timelines sont la référence pour caler les sons. Ses identifiants suivent la convention `simplywings:gacha.<événement>` :

| Id | Moment (single B, sauf mention) | Statut |
| --- | --- | --- |
| `gacha.launch` | 0 s, début du tirage | déjà prévu (provisoire) |
| `gacha.feather.burn` | 1,25 s, la plume se consume sur l'autel | **nouveau** |
| `gacha.rune.charge` | 2,45 s, les 24 runes s'allument une à une (0,068 s d'écart) | **nouveau** |
| `gacha.beam` | 4,05 s, faisceau vers le ciel | **nouveau** |
| `gacha.star.appear` | 4,4 s, l'étoile naît (single) | **nouveau** |
| `gacha.star.spawn.b` … `.sss` | ×10 : arrivée de chaque étoile dans le ciel, plus longue et plus forte selon le palier (0,42 s pour B, 1,95 s pour SSS) | **nouveau** |
| `gacha.escalate.a` | battement blanc → bleu | **nouveau** (le § ci-dessus commence à `.s`) |
| `gacha.escalate.s` / `.ss` / `.sss` | battements suivants ; en SSS, le ciel se fissure | déjà prévus |
| `gacha.star.fall` | 5,9 s, l'étoile commence sa chute en spirale | **nouveau** |
| `gacha.flame.ignite` | 4 fois pendant la chute, paramètre `flame` = `blue`, `red`, `white`, `green` (dans cet ordre) | **nouveau** : un son par couleur, ou un son avec 4 variations |
| `gacha.flame.erupt` | éruption de la vasque, vers 82 % de la chute | **nouveau** |
| `gacha.impact.b` / `.a` | éclat d'un B (9,6 s) ou d'un A | **nouveau** (le § ci-dessus commence à `.s`) |
| `gacha.impact.s` / `.ss` / `.sss` | éclat, plus violent selon le palier | déjà prévus |
| `gacha.card.spin` | single : la carte jaillit en tournoyant | **nouveau** |
| `gacha.card.deal` | ×10 : les 10 cartes sont distribuées face cachée | **nouveau** |
| `gacha.reveal.b` … `.sss` | single : vers 0,85 s après `card.spin` ; ×10 : chaque retournement, paramètre `index` | déjà prévus |
| `gacha.star` | chaque étoile de la carte (0,08 s d'écart) | déjà prévu |

- Pas encore dans le prototype : `gacha.dragon_roar`, `gacha.eclipse` et tous les sons des mythiques (ces animations n'existent pas encore), `gacha.summary`, `gacha.skip`.
- Les sons de lancement `gacha.launch.small`, `.rare` et `.legendary` du tableau précédent ne correspondent plus à une phase unique : le lancement du prototype enchaîne plume, runes et faisceau. Il reste à choisir entre un son par étape (`feather.burn`, `rune.charge`, `beam`) et un long son de lancement par palier. **À valider** avec zorago.
- Durées des films à couvrir : B 14,4 s, A 15,3 s, S 16,8 s, SS 18,3 s, SSS 20,2 s, ×10 environ 34 à 36 s. Une musique ou une nappe doit tenir sur toute la durée, et s'arrêter proprement si le joueur saute l'animation.

### 6.5 Fontaine aux offrandes

| Id | Déclencheur | Actuel | Cible | Prio |
| --- | --- | --- | --- | --- |
| `block.offering_fountain.ambient` | Boucle au bloc (atténuation 16) | rien | Eau d'essence qui coule dans le bassin : ruissellement clair et lent, petites bulles qui montent et tintent | P2 |
| `gacha.offer` | Bouton « Offer » (`FountainMenu`, l. 116) | `AMETHYST_BLOCK_RESONATE` 1 / 1,2 (serveur, BLOCKS) | Objets qui se dissolvent dans l'eau : plongeon doux + dissolution pétillante | P2 |
| `gacha.payout` | Plumes rendues (l. 118) | `PLAYER_LEVELUP` 0,6 / 1,4 | Plumes qui remontent de l'eau : bulle qui éclate + carillon de récompense, une note par plume rendue jusqu'à 5 | P2 |

### 6.6 Interface

| Id | Déclencheur | Actuel | Cible | Prio |
| --- | --- | --- | --- | --- |
| `ui.wings_button` | Bouton Ailes sous l'inventaire | clic vanilla | Clic vanilla + froissement de plume très court | P3 |
| `ui.open` | Ouverture du menu J | rien | Froissement de plumes doux (page qui tourne) | P3 |
| `ui.select` | Choix d'une paire dans la liste | clic vanilla | Clic + mini-signature de la famille (3 dB sous le clic) | P3 |
| `ui.locked` | Clic sur une paire verrouillée (« ??? » compris) | rien | Clic mat, étouffé | P3 |
| `ui.equip`, `ui.unequip`, `ui.transmog`, `ui.unlock.<rareté>` | voir § 3.2 et § 5 | — | — | P3 |

---

## 7. Mix et priorités

### 7.1 Hiérarchie (du plus fort au plus discret, à distance égale)

1. Stingers du gacha (écran plein, rien d'autre ne joue).
2. Cri du piqué des Furies, dash, tir de plasma.
3. Crash, envol chargé, gros battements (démon, ange, ptérosaure, aigle, mythiques).
4. Battements des autres paires, sauts ailés.
5. Vrombissements continus.
6. Vent de plané du joueur, frein, piqué.
7. Couches de plané propres aux paires, stationnaire.
8. Boucles de blocs, ambiances de l'autel et de la fontaine.
9. Idles, récolte d'essence, cliquetis de détail.

### 7.2 Atténuation entre sons (ducking, côté code)

Minecraft n'a pas de bus de mixage : le « ducking » se fait en baissant le volume des instances tickables du mod.

| Quand | Ce qui baisse | De combien |
| --- | --- | --- |
| Le joueur bat des ailes | Son propre `flight.wind` | −3 dB pendant 200 ms (le coup passe devant) |
| Repli faucon / piqué | Couches de plané de la paire | −6 dB (le sifflement de piqué prend la place) |
| Cri du piqué (joueur lui-même) | Vent et piqué | −9 dB pendant le cri |
| Charge de Furie | Vent | −6 dB |
| Invisibilité de Furie | Tous les sons d'ailes du joueur | −12 dB pour lui, coupés pour les autres |
| Écran de l'autel ouvert | Boucles du mod (vent, compresseur, autel) | −12 dB ; le gacha joue en MASTER |
| Plusieurs joueurs volants proches | Leurs couches de plané | une seule couche active par paire et par joueur, et au plus 4 joueurs distants avec boucles (les plus proches) |

### 7.3 Voix simultanées

- Minecraft dispose d'environ 247 voix statiques et 8 voix de streaming : les sons du mod doivent rester sobres pour ne pas affamer le reste du pack (195 mods).
- Par joueur volant : au plus **1 boucle** de vol (vent ou vrombissement) + **1 boucle de plané** de paire + les one-shots. Le vent `flight.wind` est réservé au joueur local.
- Joueurs distants : boucles seulement dans un rayon de 32 blocs et pour les **4 plus proches** ; au-delà, seuls les battements (`FlapEvent`) et les événements forts sont joués.
- Battements : pas plus d'un battement par joueur toutes les 100 ms (garde-fou si deux événements arrivent ensemble).
- Pluie d'étoiles, étincelles, tintements : pré-mixés dans le fichier du battement, jamais une voix par particule.

### 7.4 Distance d'écoute des autres joueurs

| Ce qu'on entend | Jusqu'à |
| --- | --- |
| Battement d'un joueur | 32 blocs (48 pour les gros battements) |
| Vrombissement continu | 24 blocs |
| Plané, frein, stationnaire | 16 blocs |
| Passage rapide (`flight.flyby`) | 8 blocs (déclenchement) |
| Cri du piqué des Furies | 96 blocs |

---

## 8. Livrables et planning

### 8.1 Liste des fichiers

| Lot | Contenu | Fichiers (variations comprises) |
| --- | --- | --- |
| A. Vol commun | `flight.wind`, `wind_fast`, `dive`, `flyby` (3), `tuck` (3), `brake` (3 + 1 boucle), `stall` (3), `mode_travel` (3), `mode_hover` (3), `takeoff_charge`, `takeoff_launch` (3), `rescue_jump` (3), `landing_soft` (3), `landing_hard` (3), `crash` (3), `stamina_low` (2), `stamina_empty` (2) | ≈ 44 |
| B. Familles | 8 familles × (`flap` 4 + `hover_flap` 4 + `unfold` 2 + `fold` 2) ; `glide` pour membrane, glider, element ; `buzz` + `buzz_start` (2) pour buzz | ≈ 110 |
| C. Battements des paires | 36 paires qui battent × (`flap` 4 + `flap_strong` 3 + `hover_flap` 4) | ≈ 400 au total, dont la moitié peut être dérivée de la famille (mêmes prises, autre traitement) |
| D. Boucles et sons propres des paires | 5 boucles `buzz` (+ starts et stops), ≈ 25 couches `glide` ou `hover`, ≈ 20 événements propres (roue du paon, claquement du pigeon, élytres du scarabée, flop du poisson, ratés de réacteur, orbite du Yin et du Yang…) | ≈ 70 |
| E. Mythiques | battements Night et Light (2 × 7), 2 cris (stéréo + mono), charges (2), dash, tir + explosion, invisibilité (2), idles (15), queue (2) | ≈ 45 |
| F. Blocs, essences, structures | assemblage (3), forge (4), compresseur (4), essences (4 × 3), plume, sanctuaire, compas (2) | ≈ 30 |
| G. Gacha et interface | autel (ambiance + 12 étapes), fontaine (3), interface (≈ 8), déblocages par rareté (4) | ≈ 30 |
| **Total** | | **≈ 730 fichiers OGG**, dont ≈ 250 de vraies prises distinctes |

Avec chaque lot : les entrées de `sounds.json`, les sous-titres dans `en_us.json`, et les sources non compressées (WAV 24 bits, sessions) archivées hors du jar.

### 8.2 Ordre de production

Les sons les plus fréquents d'abord, puis ceux qui rendent les paires reconnaissables.

1. **Lot A, partie vent et battement** : `flight.wind`, `flight.takeoff_launch`, et les `flap` des 8 familles. À ce stade, toutes les paires pointent vers leur famille et le jeu sonne déjà juste.
2. **Corrections de code** du § 2.8 (battement en double, sons couplés aux particules, hauteurs fixes, ids de paire dans le code, boucles tickables).
3. **Lot B complet** + les 5 boucles `buzz` (les paires muettes aujourd'hui).
4. **Lot C**, dans l'ordre des paires les plus jouées : communes, puis les 21 rares (pigeon, chouette, aigle, cuivre, améthyste en tête, car leur identité est surtout sonore), puis les 11 épiques, puis les 6 légendaires.
5. **Reste du lot A** (frein, décrochage, atterrissages, crash, stamina) et **lot D**.
6. **Lot G** (gacha), produit avec les animations finales et leurs vidéos.
7. **Lot E** (mythiques), cris compris, avec validation spécifique du droit (recréation originale).
8. **Lot F** (blocs, essences, structures).

### 8.3 Validation

- Chaque lot s'écoute en jeu, en solo **et** sur le serveur dédié (joueur distant), sans lancer le pack CurseForge : copie locale seulement.
- Grille d'écoute par paire : reconnaissable les yeux fermés à 5, 20 et 40 blocs ; 5 minutes de stationnaire sans fatigue ; 5 minutes de croisière sans motif répétitif audible ; aucun son plus fort que la référence vanilla de son type (§ 2.4).
- Les résultats et les sons validés se notent dans `ToDoTesting.md`, comme pour le reste du mod.
