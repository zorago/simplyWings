# Plan du son de la v1 : vanilla d'abord

Plan de travail du son de Simply Wings pour la v1, rédigé le 27 octobre 2026. Il remplace `SOUND_DESIGN.md` comme document actif. L'outil qui sert à l'appliquer est décrit dans `SOUND_BENCH.md`.

`SOUND_DESIGN.md` part dans `docs/archive/SOUND_DESIGN.md` avec un bandeau en tête : « Archive. Vision long terme (≈ 730 fichiers), plus le plan actif. Voir `SOUND_PLAN.md`. » Ses descriptions par paire restent une bonne mine d'idées et de mots-clés, mais elles ne sont plus une todo.

---

## 0. Règles

1. **Vanilla d'abord.** Un son du mod n'est créé que si aucun son vanilla (seul, transposé ou en couche) ne fait le travail. Les sons vanilla sont dans le style du jeu, déjà mixés, légaux et gratuits.
2. **Le silence est une réponse valable.** Un événement sans son en v1 n'est pas un oubli. L'élytre vanilla n'a presque qu'un son de vent.
3. **Stylisé plutôt que réaliste** dès qu'un son est continu ou proche de l'oreille. Rien d'agressif au casque. Aucun son ne doit fatiguer après une heure de vol.
4. **Interface discrète, grave et douce**, jamais de tintement aigu. Pour les cartes, des bruits de cartes.
5. **Aucun son choisi sans écoute.** Claude Code propose des candidats, mesure et applique ; zorago écoute et tranche dans le banc. Un son passe `validated` seulement après écoute par zorago, et `in_game` seulement après test en jeu.
6. **Licences** : CC0 de préférence. Toute autre licence est notée et justifiée dans `art/sounds/SOURCES.md` avant d'être utilisée. Jamais de son extrait d'un film, d'un jeu ou d'une série, même retravaillé, dans une version publique.

---

## 1. Corrections préalables (code et script, avant toute écoute)

Mesures faites le 27 octobre sur les 96 OGG du repo. Ces problèmes faussent l'écoute : ils passent avant les passes du § 4.

| # | Problème mesuré | Correction |
|---|---|---|
| 1.1 | `build_sounds.py` normalise **au pic** (−1,5 à −10 dBFS), pas à la sonie. Résultat : membrane ≈ 5 dB plus forte que plume (RMS max −9 contre −14) ; `wing/feather/flap_2` 2 dB sous les deux autres et deux fois plus sourd (centre spectral 457 Hz contre ≈ 1 000) ; `ui/card_deal` étalé sur 9 dB (−22 à −31) ; `gacha/shockwave` à −3,8 de RMS max, ≈ 6 dB au-dessus du reste du gacha ; `fury/night_charge` à −6, très fort pour un son positionnel | Normalisation au RMS max sur 100 ms par cible de catégorie (§ 2), puis plafond de pic vrai à −1 dBTP |
| 1.2 | Attaques en retard : `wing/membrane/flap_1` 114 ms, `wing/strong/flap_1` et `_2` 81 à 91 ms, `wing/feather/flap_*` 60 à 71 ms, `ui/card_place_3` 349 ms, `ui/card_place_4` 300 ms, `ui/card_deal_8` 260 ms | Début rogné à 3 ms avant le premier passage à −30 dB du pic. Cible : attaque < 10 ms partout, sauf sons à montée voulue (charges, nappes, cri de piqué) marqués `slow_attack: true` dans le catalogue |
| 1.3 | La couche de matière des paires (feu, eau, cuivre, améthyste, orage…) est jouée par `WingEffects`, **seulement si les particules sont actives** (`WingEffects.active`). Particules coupées : ces paires deviennent une plume molle | Nouveaux ids `wing.<paire>.layer` dans `sounds.json` (vanilla, mêmes réglages qu'aujourd'hui). `WingSounds` joue `flap` puis `layer` ensemble, pour le joueur local et les distants (`ClientNet.onFlapEvent`), sans passer par `WingEffects`. `WingEffects` ne joue plus aucun son |
| 1.4 | `sounds.json` n'a aucun `attenuation_distance` ni `preload` : tout s'éteint à 16 blocs | Le catalogue porte `attenuation` (32 pour les battements, 48 pour les coups puissants, 24 pour les vrombissements et atterrissages, 12 à 16 pour les blocs) et `preload` (battements, vent) ; `build_sounds.py` les écrit |
| 1.5 | Raccords de boucle suspects : `flight/wind` (saut de 0,041 entre fin et début), `block/cloud_compressor/loop` (0,023) | Fondu enchaîné refait, raccord sur un passage à zéro, vérification à l'écoute en boucle dans le banc |
| 1.6 | `ui/equip` dure 4,2 s et il est brillant (centre spectral ≈ 5 kHz) : contraire à la règle 4. Il est joué aux boutons Équiper et Retirer, à l'infusion d'énergie et à la table d'assemblage | Remplacé par un son court (< 0,6 s), grave et doux ; garder l'actuel seulement pour la table d'assemblage si zorago l'aime là |
| 1.7 | Les sons de la forge, de la trempe et du conteneur plein sont très brillants (centre spectral 5 à 8,5 kHz) | À écouter en premier dans la passe blocs ; passe-bas si besoin |
| 1.8 | Les temps des sons du gacha sont codés en dur dans `ClientShows.tick` et `CardDeck` | Sortis dans `gacha/show_sounds.json` (`SOUND_BENCH.md` § 6.1) |

Contrôle après les corrections : relancer `tools/sounds/audit.py` (§ 7), toutes les lignes au vert sauf les exceptions marquées.

---

## 2. Cibles de sonie (point de départ)

Valeurs de départ en RMS max sur 100 ms, à **recaler à l'oreille** dans le banc contre la référence vanilla de chaque catégorie (bouton ▶ Référence). Fichier : `tools/sounds/loudness.json`.

| Cible | RMS max de départ | Référence vanilla | Remarque |
|---|---|---|---|
| `wing_flap` | −14 dB | `entity.phantom.flap` | toutes les familles au même niveau ; la chouette baisse par le `volume` de sa paire, pas par le fichier |
| `wing_flap_strong` | −11 dB | `entity.ender_dragon.flap` | 3 dB sous la référence |
| `wing_layer` | sans objet (vanilla) | — | réglé par `volume` dans le catalogue |
| `buzz_loop` | −19 dB | `entity.bee.loop` | |
| `wind_loop` | −17 dB | `item.elytra.flying` | volume piloté par la vitesse dans le code |
| `landing` | −16 dB | pas de chute vanilla | |
| `ui` | −24 dB | `ui.button.click` | |
| `cards` | −24 dB | `ui.button.click` | écart maximal 2 dB entre variations |
| `gacha_small` | −16 dB | — | étincelles, carillons, cartes B et A |
| `gacha_big` | −10 dB | `ui.toast.challenge_complete` | onde de choc, éclat, implosion |
| `block_loop` | −22 dB | `block.furnace.fire_crackle` | on travaille à côté longtemps |
| `block_oneshot` | −18 dB | `block.anvil.use` (plus doux) | |
| `fury_big` | −10 dB | — | cris, charges, dash |

Règle d'écart : dans un même groupe, les variations restent à ± 1,5 dB les unes des autres.

---

## 3. Périmètre de la v1, id par id

Statuts : `custom` (fichiers du mod), `vanilla`, `alias` (autre id du mod), `silent`, `todo` (à trouver). « Existant » = le fichier actuel est gardé tel quel en attendant l'écoute.

### 3.1 Ailes : familles

| Id | Statut | Contenu | Couvre |
|---|---|---|---|
| `wing.feather.flap` | custom, existant (3 coups de cape) | 4e variation à trouver, `flap_2` à recaler | 13 paires de plumes + bases des paires de matière et d'élément |
| `wing.membrane.flap` | custom, existant (5) | | démon, ptérosaure, phantom, Night, Light |
| `wing.leather.flap` | custom, existant (4) | | chauve-souris, planeurs |
| `wing.insect.flap` | custom, synthé existant (3) | | coups des paires à vrombissement |
| `wing.strong.flap` | custom, existant (3) | | `flap_strong` de toutes les paires, envol |
| `wing.buzz.loop` | custom, synthé existant | | colibri, abeille, scarabée, méganeure |
| famille `mechanism` | vanilla | `block.wooden_trapdoor.open` | ornithoptère, cerf-volant |

### 3.2 Ailes : les 41 paires

Chaque paire a `wing.<paire>.flap` et `wing.<paire>.flap_strong` en `alias` vers sa famille, avec sa hauteur et son volume (table `PAIRS` actuelle, reprise dans le catalogue). Les paires suivantes ont en plus une couche `wing.<paire>.layer`, **vanilla**, reprise de `WingEffects` :

| Paires | Couche vanilla (volume / hauteur actuels) |
|---|---|
| crow, gull, eagle, swift, pigeon, peacock, parrot | `entity.parrot.fly` 0,4 / 0,8 |
| owl | **aucune** (vol silencieux : la couche actuelle contredit l'identité) |
| angel | `block.amethyst_block.chime` 0,5 / 1,4 |
| demon | `entity.blaze.shoot` 0,45 / 0,55 |
| seraph | `block.beacon.power_select` 0,35 / 1,6 |
| psyche | `particle.soul_escape` 0,6 / 1,2 |
| fire | `item.firecharge.use` 0,35 / 1,3 |
| water | `entity.boat.paddle_water` 0,5 / 0,9 |
| earth | `block.basalt.break` 0,6 / 0,6 |
| air | `entity.breeze.idle_air` 0,6 / 1,2 |
| storm | `entity.lightning_bolt.impact` 0,25 / 1,8 |
| aurora | `block.amethyst_block.resonate` 0,5 / 1,2 |
| shadow | `entity.enderman.teleport` 0,25 / 0,6 |
| nebula | `block.amethyst_cluster.break` 0,4 / 1,5 |
| forest | `block.azalea_leaves.break` 0,6 / 0,9 |
| amethyst | `block.amethyst_block.chime` 0,5 / 1,0 |
| copper | `block.copper.hit` 0,6 / 0,8 |
| yin_yang | deux couches : `block.amethyst_block.chime` 0,5 / 0,6 et 0,5 / 1,7 |
| ptero, phantom | `entity.phantom.flap` 0,7 / 0,6 |

Les couches s'ajustent dans le banc (volume, hauteur, choix d'un autre son vanilla). Elles ne deviennent `custom` que si aucune option vanilla ne convient après écoute.

Vrombissements `wing.<paire>.buzz` : `alias` vers `wing.buzz.loop` avec la hauteur de la paire ; Dédale garde l'alias vers la boucle du compresseur.

### 3.3 Vol

| Id | Statut |
|---|---|
| `flight.wind` | custom existant, **à comparer** à `item.elytra.flying` dans le banc ; garder le meilleur |
| `flight.takeoff_launch` | alias `wing.strong.flap` |
| `flight.landing_soft` | custom existant (Kenney) |
| `flight.landing_hard` | custom existant, aussi joué au crash |
| saut ailé, saut de secours, bascules de mode | déjà couverts par les battements de la paire (pas d'id propre) |
| `flight.hover_flap` | battement de la paire, très faible (volume 0,12), espacement légèrement aléatoire autour de la cadence du stationnaire (`ClientFlight`, local seulement) |
| `flight.brake` | custom Pixabay (tempête de sable en boucle) : coup d'aile fort au début du frein, puis le souffle s'éteint avec la vitesse (`FlightLoops`, joueurs proches compris) |
| `flight.flyby` | custom Pixabay, 3 variations au hasard : un joueur ailé frôle à moins de 5 blocs avec plus de 14 m/s de vitesse relative (2 s entre deux) |
| `flight.takeoff_charge`, `dive`, `stall`, `tuck`, `stamina_*`, `unfold`, `fold`, `glide` | **silent**, choix de zorago (le vent suffit au plané et au piqué) |

### 3.4 Furies

| Id | Statut |
|---|---|
| `night_fury.dive`, `light_fury.dive`, `night_fury.invisible`, `light_fury.invisible` | sample du film : OK pour le pack privé, **bloquant pour la publication** (§ 5) |
| `night_fury.charge`, `night_fury.crackle`, `light_fury.charge` | custom existants, `night_fury.charge` à baisser (§ 1.1) |
| `light_fury.blast` | vanilla (`entity.generic.explode`) |
| idles, queue, battements spécifiques | silent (choix de zorago) ; les battements suivent la famille membrane |

### 3.5 Gacha et interface

| Id | Statut |
|---|---|
| `gacha.offer`, `ignite`, `rune_charge`, `beam`, `shockwave`, `blast`, `implosion`, `puff`, `bell`, `chime`, `twinkle` | custom existants, à régler sur la vraie animation du jeu dans le banc ; `shockwave` à baisser |
| rugissement du dragon, activation de balise | vanilla |
| `gacha.summary`, `pity_soft` | silent ; le skip du show est **supprimé** (il laissait les sons du show tourner) |
| `ui.card_deal`, `card_flip`, `card_shuffle`, `card_place` | custom existants (Kenney Casino), niveaux à aligner |
| `ui.confirm`, `ui.denied`, `ui.open` | custom existants |
| `ui.equip` | **todo** : remplacer (§ 1.6) |
| `ui.select`, `ui.locked`, `ui.transmog`, `ui.unlock` | vanilla : `ui.button.click` (0,25, 1,4), `block.chest.locked`, `item.armor.equip_elytra`, `entity.player.levelup` (0,5, 1,2, hors gacha) |

### 3.6 Blocs et essences

| Id | Statut |
|---|---|
| `block.forge_bench.hammer`, `quench`, `block.cloud_compressor.loop`, `essence.pour`, `essence.full` | custom existants, à écouter (§ 1.5, § 1.7) |
| matériaux des blocs (`SoundType`) | vanilla, inchangés |
| `block.altar.ambient`, `block.fountain.ambient`, `block.forge_bench.ambient` | vanilla discrets dans `animateTick` : `block.beacon.ambient` (1/40), `block.water.ambient` (1/24), `block.furnace.fire_crackle` (1/10) |
| `structure.sanctum_appear` | vanilla `block.end_portal.spawn` (0,5, 0,8), pour tous les joueurs de l'End à la première apparition |
| `item.compass` | vanilla `item.lodestone_compass.lock` à la prise en main |
| `essence.drip` | custom Pixabay, 4 gouttes : très faibles, hauteur et cadence variables, seulement quand un conteneur se remplit en vol (client) |
| `essence.reserve` | custom Pixabay, fumée en boucle dont volume et hauteur errent, tant que la réserve d'essence intensifiée remplit la stamina |
| plume d'invocation | silent : couverte par le ramassage vanilla, la fontaine et l'offrande |
| fontaine (`FountainMenu`) | vanilla actuel (`amethyst_block.resonate`, `player.levelup`) |

### 3.7 Bilan

Environ **95 fichiers custom aujourd'hui**, ≈ 90 à garder ou recaler, **≈ 5 à trouver** (`ui.equip`, 4e variation plume, éventuellement le vent) et **4 à recréer avant publication** (cris et disparitions des Furies). Le reste est vanilla ou silencieux. Plus jamais 730.

---

## 4. Passes d'écoute

Une passe = un groupe d'ids écouté dans le banc, réglé, construit, testé en jeu. Les verdicts sont dans le catalogue ; `ToDoTesting.md` § 15 renvoie simplement au banc.

| Ordre | Passe | Critère de sortie |
|---|---|---|
| 0 | Corrections du § 1 | `audit.py` au vert |
| 1 | Battements des familles (3.1) sur la vue 3D, en croisière et en stationnaire | chaque famille se reconnaît ; 2 min de rafale sans fatigue ; aucun clic |
| 2 | Couches de matière des paires (3.2), paire par paire, sur la vue 3D | chaque paire se reconnaît les yeux fermés à 5 et 20 blocs (curseur de distance) ; la chouette presque muette |
| 3 | Vent, envol, atterrissages (3.3) sur le vol complet de 30 s | 5 min de croisière sans motif répétitif audible |
| 4 | Interface et cartes (3.5) | aucune carte inaudible ni plus forte que les autres ; rien d'aigu |
| 5 | Gacha (3.5) sur `altar_show.json.gz`, palier par palier, puis ×10 | chaque palier plus fort que le précédent ; « Passer » propre ; rien au-dessus de la référence `toast.challenge_complete` + 3 dB |
| 6 | Blocs et essences (3.6) | boucle du compresseur supportable 10 min à côté |
| 7 | Furies (3.4), puis recréation (§ 5) | validé en jeu, en solo et sur le serveur |

Après chaque passe : test en jeu, solo **et** sur le serveur dédié (joueur distant), dans une instance locale, jamais dans le pack CurseForge public.

---

## 5. Recréation des sons des Furies (bloquant pour la publication)

Le cri de piqué et la disparition viennent du film (`art/sounds/source/user/night_fury_dive.mp3`). Deux voies, au choix de zorago :

- **Montage de sources CC0** : cri de rapace (faucon, buse), passage de réacteur ou sifflement de piqué, grincement métallique, montée de hauteur et Doppler, assemblés dans `build_sounds.py` (fonction dédiée, comme `synth`). Le banc sert à comparer les essais au sample privé, **sans** jamais intégrer le sample à la version publique.
- **Commande à un sound designer** pour ces seuls sons (4 fichiers : 2 cris, 2 disparitions), avec la description de l'ancien § 5 de `SOUND_DESIGN.md` comme brief.

Tant que ce n'est pas fait : la publication CurseForge attend. Le pack privé peut garder le sample.

---

## 6. Après la v1 (pas avant)

Dans l'ordre du rapport effort / effet : `hover_flap` (alias des battements à −6 dB, aucun nouveau fichier), `flight.takeoff_charge`, `flight.brake`, plané par famille, musique d'invocation, idles. Chaque ajout passe par le banc et la règle 1.

---

## 7. Santé du dépôt : `tools/sounds/audit.py`

Script à créer (tâche de la passe 0), relancé après chaque passe et avant chaque publication. Sortie lisible + code de retour non nul en cas d'erreur. Il vérifie :

1. Chaque id de `sounds.json` existe dans le catalogue et inversement ; chaque fichier référencé existe ; aucun OGG orphelin.
2. Chaque son joué dans le code (`ModSounds`, `SoundEvents.*` encore utilisés, ids passés à `WingSounds`) est dans le catalogue, avec son statut.
3. Par fichier : RMS max dans la cible ± 2 dB, pic vrai ≤ −1 dBTP, attaque < 10 ms (sauf `slow_attack`), mono 44,1 kHz pour tout son positionnel, raccord de boucle < 0,005.
4. Écart ≤ 1,5 dB entre variations d'un même groupe.
5. Aucun son lié aux particules (`WingEffects` ne joue plus de son).
6. Chaque source de `art/sounds/source/` utilisée a sa ligne dans `SOURCES.md` avec une licence ; liste des sources **non libres** (aujourd'hui : le sample des Furies) en rouge, avec « bloquant pour la publication ».
7. Compteurs : ids par statut (`custom`, `vanilla`, `alias`, `silent`, `todo`) et par verdict, pour voir le périmètre d'un coup d'œil et vérifier qu'il ne regonfle pas.

Quand zorago revient pour une analyse de santé, la première chose à lire est la sortie de ce script.
