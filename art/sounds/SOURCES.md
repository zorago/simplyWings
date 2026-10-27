# Sources des sons

Tous les sons du mod viennent de bibliothèques **CC0** (domaine public : usage commercial libre, sans attribution obligatoire). Licence vérifiée sur la page de chaque son le 26 octobre 2026. Les fichiers d'origine vont dans `art/sounds/source/` (ignoré par git, à retélécharger depuis les liens ci-dessous). `python -I tools/sounds/build_sounds.py` les découpe, les centre, ajoute les fondus, normalisent chaque fichier au RMS max de sa cible de sonie (`tools/sounds/loudness.json`, plafond de pic vrai à −1 dBTP), les convertit en OGG mono 44,1 kHz dans `assets/simplywings/sounds/`, puis réécrit `sounds.json` depuis le catalogue `tools/sounds/catalog.json` (familles, une entrée `wing.<paire>.flap` et `flap_strong` par paire) et les sous-titres de `en_us.json`.

## Freesound (aperçus HQ, CC0)

| Fichier local | Page | Auteur | Utilisé pour |
|---|---|---|---|
| `fs_670509_huge_wing_flaps.mp3` | https://freesound.org/s/670509/ | 2create | `wing/strong/flap` (coups 1 à 3 gardés ; les coups 4 et 5, à 5,87 s et 9,27 s, rejetés) |
| `fs_759529_large_flying_creature.mp3` | https://freesound.org/s/759529/ | Cerise_Virtuelle | `wing/membrane/flap` (démon, ptéro, phantom, Furies) |
| `fs_593487_umbrella_bat_wings.mp3` | https://freesound.org/s/593487/ | Scottrex05 | `wing/leather/flap` (chauve-souris, planeurs) |
| `fs_711122_cape_wings.mp3` | https://freesound.org/s/711122/ | Cultureshock007 | `wing/feather/flap` (les 3 seules variations restantes) ; famille `soft` (papillon de nuit, Psyché) à 30 % du volume et hauteur 1,3 |
| `fs_482733_insect_wing.mp3` | https://freesound.org/s/482733/ | kalhan | **rejeté** par zorago (« horrible »), plus utilisé |
| `fs_814044_creation_magic_blast.mp3` | https://freesound.org/s/814044/ | qubodup | `gacha/blast` (éclat de l'étoile, cartes SS) |
| `fs_814053_shockwave.mp3` | https://freesound.org/s/814053/ | qubodup | `gacha/shockwave` (pilier, éclat, cartes SSS et mythiques) |
| `fs_814047_fantasy_implosion.mp3` | https://freesound.org/s/814047/ | qubodup | `gacha/implosion` (éclat SSS et mythique) |
| `fs_832552_crystal_ball.mp3` | https://freesound.org/s/832552/ | harrisonlace | `gacha/rune_charge` (nappe de l'autel, 2 houles) |
| `fs_859497_powerup_loop.mp3` | https://freesound.org/s/859497/ | simranzenov | `gacha/beam` (montée du pilier) |
| `fs_714257_puff_of_smoke.mp3` | https://freesound.org/s/714257/ | qubodup | `gacha/puff` (nuages, offrande qui s'élève) |
| `fs_454072.mp3` | https://freesound.org/s/454072/ | kyles | `flight/wind` : boucle de vent du vol de déplacement (30 à 46 s, fondu enchaîné) |
| `fs_496237.mp3` | https://freesound.org/s/496237/ | fury12 | **rejeté** (trop réaliste, agressif au casque), plus utilisé |
| `fs_504626.mp3` | https://freesound.org/s/504626/ | leonelmail | `flight/landing_hard` : arrivée rapide au sol et crash |
| `fs_434339.mp3` | https://freesound.org/s/434339/ | draftcraft | `block/forge_bench/hammer` : 6 coups de marteau sur enclume |
| `fs_234782.mp3` | https://freesound.org/s/234782/ | wubitog | `block/forge_bench/quench` : trempe |
| `fs_487748.mp3` | https://freesound.org/s/487748/ | stib | `block/cloud_compressor/loop` : boucle du compresseur (et poussée de Dédale, hauteur 1,4) |
| `fs_814061.mp3` | https://freesound.org/s/814061/ | YouMightLikeThis | `fury/night_charge` : grésillement électrique de la charge de Night |
| `fs_582631.mp3` | https://freesound.org/s/582631/ | ironcross32 | `fury/crackle` : craquement en couche sur la charge de Night |
| `fs_407052.mp3` | https://freesound.org/s/407052/ | Sojan | **rejeté** par zorago (trop artificiel), remplacé par `fs_871766` ; plus utilisé |
| `fs_875132.mp3` | https://freesound.org/s/875132/ | hotpin7 | `essence/pour` : essence versée dans une jauge |
| `fs_511485.mp3` | https://freesound.org/s/511485/ | MLaudio | `essence/full` : conteneur plein |
| `fs_561287.mp3` | https://freesound.org/s/561287/ | angelkunev | `ui/equip` : ailes équipées ou retirées, infusion d'énergie, ailes de la table d'assemblage |
| `fs_841982.mp3` | https://freesound.org/s/841982/ | kevinklang | téléchargé, pas encore utilisé (vent de rechange) |
| `fs_521972.mp3` | https://freesound.org/s/521972/ | Kastenfrosch | téléchargé, pas encore utilisé (marteau de rechange) |
| `fs_260554_fire_whoosh.mp3` | https://freesound.org/s/260554/ | LookIMadeAThing | `gacha/ignite` (flamme de forge, hauteur 0,85) |
| `fs_260555_fire_whoosh_2.mp3` | https://freesound.org/s/260555/ | LookIMadeAThing | `gacha/ignite` |
| `fs_244926_fire_whoosh.mp3` | https://freesound.org/s/244926/ | hnhnh | `gacha/ignite` |
| `fs_651108_energy_beam_3.mp3` | https://freesound.org/s/651108/ | LPHypeR | `gacha/beam` : rayon d'énergie pure (hauteur 0,85) |
| `fs_387924_fire_massive.mp3` | https://freesound.org/s/387924/ | Aegersum | `gacha/beam_roar` : grondement de flammes joué sous le rayon (souffle atomique) |
| `fs_871766_arc_sparking.mp3` | https://freesound.org/s/871766/ | harrisonlace | « tzzt » d'électricité statique découpés en rafales (`bursts`) : `fury/light_charge`, `wing/storm/layer` |
| `fs_650516_energy_beam_2.mp3` | https://freesound.org/s/650516/ | LPHypeR | téléchargé, pas encore utilisé (rayon de rechange) |
| `fs_393066_electric_zap.mp3` | https://freesound.org/s/393066/ | Wakerone | téléchargé, pas encore utilisé (électricité de rechange) |
| `fs_524588_sparks_fly.mp3` | https://freesound.org/s/524588/ | _bliind | téléchargé, pas encore utilisé (étincelles de rechange) |
| `fs_186374_bonfire_lit.mp3` | https://freesound.org/s/186374/ | samararaine | téléchargé, pas encore utilisé (allumage de brasier de rechange) |
| `fs_466795_bumblebee.mp3` | https://freesound.org/s/466795/ | Breviceps | téléchargé, pas encore utilisé (bourdon, si le bourdon vanilla ne convient pas) |
| `fs_570203_dragonfly_by.mp3` | https://freesound.org/s/570203/ | RatBird | téléchargé, pas encore utilisé (libellule, enregistrement faible et bruité) |

## OpenGameArt (CC0)

| Fichier local | Source | Utilisé pour |
|---|---|---|
| `oga_wings_flap_large/wings_flap_large.flac` | https://opengameart.org/content/large-wings-flap | **rejeté** par zorago (« je déteste le son »), plus utilisé |
| `oga_magical_1_0.ogg` … `oga_magical_7_0.ogg` (`oga_magical_*`) | https://opengameart.org/sites/default/files/magical_1_0.ogg (et `magical_2.ogg`, `magical_3.ogg`, `magical_4.ogg`, `magical_5.ogg`, `magical_6_0.ogg`, `magical_7_0.ogg`) | `gacha/ignite` (6 variations), `gacha/offer` (`magical_3`) |

## Kenney (CC0, https://kenney.nl)

| Pack | Fichiers | Utilisé pour |
|---|---|---|
| Interface Sounds (https://kenney.nl/assets/interface-sounds), dossier `kenney_interface-sounds/` | `bong_001`, `back_002-003`, `open_002-004` | `ui/confirm`, `ui/denied`, `ui/open` (pas encore joué) |
| Impact Sounds (https://kenney.nl/assets/impact-sounds), dossier `kenney_impact-sounds/` | `impactBell_heavy_000-004`, `impactGlass_medium_000-004`, `impactGlass_light_000-004`, `impactSoft_medium_000-003` | `gacha/bell`, `gacha/chime`, `gacha/twinkle`, `flight/landing_soft` |
| UI Audio (https://kenney.nl/assets/ui-audio), dossier `kenney_ui-audio/` | `switch3` | `ui/confirm` (2e variation) |

## Kenney Casino Audio (CC0, https://kenney.nl/assets/casino-audio)

Dossier local `kenney_casino-audio/`.

| Fichiers | Utilisé pour |
|---|---|
| `card-slide-1-8` | `ui/card_deal` (carte distribuée) |
| `card-shove-1-4`, `card-fan-1` | `ui/card_flip` (retournement) |
| `card-shuffle` | `ui/card_shuffle` (anticipation des cartes S et plus) |
| `card-place-1-4` (hauteur 0,85) | `ui/card_place` (carte posée, base de chaque palier) |

Tous les sons d'interface passent par deux passe-bas (6000 Hz pour les cartes, 1500 à 2000 Hz pour `ui/confirm`, `ui/denied`, `ui/open`) et sont rabaissés (hauteur 0,6 à 0,75) : discrets, graves et doux, comme le veut zorago.

## Sons synthétisés (aucune source externe)

`tools/sounds/build_sounds.py`, fonction `synth`. Les deux ont été **rejetés** par zorago (« hélicoptère ») le 27 octobre et remplacés par le bourdon vanilla (`entity.bee.loop`, `entity.bee.pollinate`) ; la fonction reste pour mémoire :
- `wing/buzz/loop` : vrombissement « jeu vidéo » en boucle parfaite de 2 s, deux dents de scie à 92 et 92,5 Hz (battement lent), sous-octave carrée, trémolo à 30 Hz, souffle filtré, passe-bas 1300 Hz avec bosse à 650 Hz (filtrage par FFT, donc sans couture à la boucle) ;
- `wing/insect/flap` : « brrrp » de 0,3 s (glissando 120, 140 ou 165 Hz vers −35 %, trémolo à 34 Hz), coups d'aile des paires à vrombissement.

## Sample fourni par zorago (hors CC0)

| Fichier local | Origine | Utilisé pour |
|---|---|---|
| `user/night_fury_dive.mp3` | cri de piqué de Night Fury isolé par zorago (`audio-editor-output.mp3`, 6,48 s, explosion du piqué à ≈ 5,1 s) | `fury/dive` : 0 à 5,0 s + 3 s d'écho qui s'atténue (Night et Light) ; `fury/invisible` : sample entier + 3 s d'écho, hauteur 0,97 (Night) et 1,03 (Light), joué au dash qui rend invisible |

Ce son vient du film : il n'est **pas** libre de droits. Il convient pour un usage privé (le pack entre amis) ; avant une publication du mod, il faudra le remplacer par une recréation originale (`SOUND_PLAN.md` § 5).

## Choix faits sans écoute

Les sons ont été choisis sur leur description, leur durée et leur enveloppe (découpage automatique des coups). À valider à l'oreille en jeu : `ToDoTesting.md` § 15.
