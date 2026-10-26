# Sources des sons

Tous les sons du mod viennent de bibliothèques **CC0** (domaine public : usage commercial libre, sans attribution obligatoire). Licence vérifiée sur la page de chaque son le 26 octobre 2026. Les fichiers d'origine vont dans `art/sounds/source/` (ignoré par git, à retélécharger depuis les liens ci-dessous). `python -I tools/sounds/build_sounds.py` les découpe, les centre, ajoute les fondus, normalise le pic, les convertit en OGG mono 44,1 kHz dans `assets/simplywings/sounds/`, puis réécrit `sounds.json` (familles, une entrée `wing.<paire>.flap` et `flap_strong` par paire) et les sous-titres de `en_us.json`.

## Freesound (aperçus HQ, CC0)

| Fichier local | Page | Auteur | Utilisé pour |
|---|---|---|---|
| `fs_670509_huge_wing_flaps.mp3` | https://freesound.org/s/670509/ | 2create | `wing/strong/flap` (coups puissants, envol chargé, ailes du gacha) |
| `fs_759529_large_flying_creature.mp3` | https://freesound.org/s/759529/ | Cerise_Virtuelle | `wing/membrane/flap` (démon, ptéro, phantom, Furies) |
| `fs_593487_umbrella_bat_wings.mp3` | https://freesound.org/s/593487/ | Scottrex05 | `wing/leather/flap` (chauve-souris, planeurs) |
| `fs_711122_cape_wings.mp3` | https://freesound.org/s/711122/ | Cultureshock007 | `wing/feather/flap` (3 variations) |
| `fs_482733_insect_wing.mp3` | https://freesound.org/s/482733/ | kalhan | `wing/insect/flap` (vrombissement, insectes lents) |
| `fs_814044_creation_magic_blast.mp3` | https://freesound.org/s/814044/ | qubodup | `gacha/blast` (éclat de l'étoile, cartes SS) |
| `fs_814053_shockwave.mp3` | https://freesound.org/s/814053/ | qubodup | `gacha/shockwave` (pilier, éclat, cartes SSS et mythiques) |
| `fs_814047_fantasy_implosion.mp3` | https://freesound.org/s/814047/ | qubodup | `gacha/implosion` (éclat SSS et mythique) |
| `fs_832552_crystal_ball.mp3` | https://freesound.org/s/832552/ | harrisonlace | `gacha/rune_charge` (nappe de l'autel, 2 houles) |
| `fs_859497_powerup_loop.mp3` | https://freesound.org/s/859497/ | simranzenov | `gacha/beam` (montée du pilier) |
| `fs_714257_puff_of_smoke.mp3` | https://freesound.org/s/714257/ | qubodup | `gacha/puff` (nuages, offrande qui s'élève) |

## OpenGameArt (CC0)

| Fichier local | Source | Utilisé pour |
|---|---|---|
| `oga_wings_flap_large/wings_flap_large.flac` | https://opengameart.org/content/large-wings-flap | `wing/feather/flap` (3 variations) |
| `oga_magical_1_0.ogg` … `oga_magical_7_0.ogg` | https://opengameart.org/sites/default/files/magical_1_0.ogg (et `magical_2.ogg`, `magical_3.ogg`, `magical_4.ogg`, `magical_5.ogg`, `magical_6_0.ogg`, `magical_7_0.ogg`) | `gacha/ignite` (6 variations), `gacha/offer` (`magical_3`) |

## Kenney (CC0, https://kenney.nl)

| Pack | Fichiers | Utilisé pour |
|---|---|---|
| Interface Sounds (https://kenney.nl/assets/interface-sounds) | `drop_001-004`, `maximize_001-004`, `confirmation_001-002`, `error_004`, `error_008` | `ui/card_deal`, `ui/card_flip`, `ui/confirm`, `ui/denied` |
| Impact Sounds (https://kenney.nl/assets/impact-sounds) | `impactBell_heavy_000-004`, `impactGlass_medium_000-004`, `impactGlass_light_000-004` | `gacha/bell`, `gacha/chime`, `gacha/twinkle` |
| UI Audio (https://kenney.nl/assets/ui-audio) | téléchargé, pas encore utilisé | réserve pour l'interface |

## Choix faits sans écoute

Les sons ont été choisis sur leur description, leur durée et leur enveloppe (découpage automatique des coups). À valider à l'oreille en jeu : `ToDoTesting.md` § 15.
