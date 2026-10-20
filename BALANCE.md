# Équilibrage des ailes

Tableau de suivi des plages de puissance par tier et de la position de chaque paire. Les valeurs vivent dans `src/main/resources/data/simplywings/simplywings/wing/*.json` et se modifient sans recompiler (relancer le monde suffit). La forme et l'animation de chaque paire (nombre d'ailes, profil de battement) sont dans `anim/WingStyle.java`.

Dernière mise à jour : 20 octobre 2026.

## Plages par tier (stats équipées)

| Stat | Unité | Commune | Rare | Épique | Légendaire |
| --- | --- | --- | --- | --- | --- |
| Stamina | points | 80 – 120 | 120 – 170 | 170 – 220 | 220 – 280 |
| Accélération (gain par seconde de battement, et puissance du frein) | m/s par s | 4 – 8 | 8 – 15 | 8 – 14 | 8 – 12 |
| Vitesse max (croisière en battant) | m/s | 14 – 22 | 22 – 32 | 32 – 46 | 46 – 62 |
| Modificateur de décrochage (`stall_factor`) | × V min | 1,1 – 1,3 | 0,8 – 1,3 | 0,4 – 1,1 | 0,7 – 1,0 |
| Vitesse min (décrochage) | m/s | 6 – 9 | 6 – 10 | 8 – 12 | 10 – 14 |
| Maniabilité | °/s | 50 – 80 | 60 – 140 | 50 – 110 | 60 – 100 |
| Stabilisation | 0 – 10 | 2 – 5 | 2 – 6 | 2 – 6 | 0,5 – 4 |
| Battements par seconde (déplacement) | /s | 1,5 – 2,4 | 1,5 – 3,0 | 0,5 – 25 | 0,4 – 1,2 |
| Vitesse en stationnaire (horizontale et verticale) | m/s | 6 – 10 | 9 – 13 | 12 – 16 | 16 – 22 |
| Traînée (`drag`, multiplicateur) | × | 0,9 – 1,1 | 0,5 – 1,0 | 0,8 – 1,1 | 0,9 – 1,1 |

Le gain d'un battement vaut accélération ÷ battements par seconde. À partir de 8 battements par seconde, l'aile est continue : la poussée s'applique à chaque tick tant que sprint est maintenu. Le coût en stamina est identique par seconde pour toutes les paires : un battement coûte `flapCost × 2 ÷ battements par seconde`.

## Position des paires existantes

Le pourcentage indique où se place la valeur dans la plage de son tier (0 % = minimum, 100 % = maximum).

| Paire | Tier | Stamina | Accél. | V max | Décrochage (V min × modif.) | Maniab. | Stabil. | Batt./s | Station. | Traînée | Saut ailé | Atteindre 90 % de V max |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Commune (`common`) | Commune | 100 (50 %) | 6 (50 %) | 18 (50 %) | 8 × 1,2 = 9,6 (malus) | 70 (67 %) | 3 (33 %) | 2,0 (56 %) | 8 (50 %) | 1,0 | ≈ 1 bloc | 5 battements |
| Chauve-souris (`bat`) | Rare | 150 (60 %) | 13 (71 %) | 28 (60 %) | 7 × 1,25 = 8,75 (malus) | 130 (88 %) | 3 (25 %) | 2,6 (73 %) | 11 (50 %) | 0,6 | ≈ 1,3 bloc | 5 battements |
| Ancienne / libellule (`ancient`) | Épique | 200 (60 %) | 5 (sous la plage) | 24 (sous la plage) | 10 × 0,45 = 4,5 (gros bonus) | 140 (au-dessus) | 4 (50 %) | 20 (continue) | 14 (50 %) | 1,0 | ≈ 0,6 bloc | poussée continue, lente |
| Ange (`angel`) | Légendaire | 240 (33 %) | 9 (25 %) | 58 (75 %) | 12 × 1,0 = 12 | 85 (63 %) | 1 (14 %) | 0,5 (13 %) | 19 (50 %) | 0,7 (plane longtemps) | ≈ 4,4 blocs | 3 battements |

« Sous la plage » ou « au-dessus » signale un compromis volontaire : une paire peut sortir de la plage de son tier sur une stat si son identité le justifie.

Identités :
- **Commune** : la version de base, des élytres améliorées. Décroche facilement.
- **Chauve-souris** : aérodynamique (traînée 0,6) et très maniable, mais vitesse max basse. Décroche facilement.
- **Ancienne / libellule** : faite pour construire. Ultra stable en stationnaire, mais médiocre en vol : lente, accélère et freine mal. Elle reste très maniable et décroche à très basse vitesse, comme un insecte capable de faire du surplace.
- **Ange** : la puissance. Un battement toutes les 2 secondes : longue montée, puis coup sec qui pousse d'environ 18 m/s. Pas de bonus de décrochage, mais une traînée faible qui la fait planer longtemps. Très instable en stationnaire.

## Lancement et saut ailé

- **Lancement** (envol chargé au sol, ou passage du stationnaire au déplacement) : il part à `launchSpeedFraction` × V max, soit 50 % par défaut, sans descendre sous la vitesse de décrochage. Le premier battement est disponible tout de suite.
- **Saut ailé** : vitesse verticale = `airJumpBase` (6 m/s) + gain d'un battement × `airJumpPerBeatGain` (0,6). Le gain d'un battement vaut accélération ÷ battements par seconde. L'ange (18 m/s par battement) saute donc environ 4,4 blocs, la libellule (0,25 m/s par battement) à peine 0,6 bloc.

## Instabilité en stationnaire

L'aller-retour vaut `perturbationSwing × (10 − stabilisation) / 10 × hover_swing`, à la verticale et d'avant en arrière. Avec `perturbationSwing` = 0,6 bloc :

| Paire | Stabilisation | Cadence | Amplitude | Aller-retour total |
| --- | --- | --- | --- | --- |
| Commune | 3 | 1,2 Hz | ± 0,42 bloc | ≈ 0,8 bloc |
| Chauve-souris | 3 | 1,8 Hz | ± 0,42 bloc | ≈ 0,8 bloc |
| Libellule | 4 | 25 Hz (vibration) | quasi nulle (`hover_swing` 0,1) | légère dérive seulement |
| Ange | 1 | 0,6 Hz | ± 0,54 bloc | ≈ 1,1 bloc |

## Bonus globaux

Les 4 paires actuelles donnent **ensemble 10 %** du total final, soit 2,5 % chacune. Les 90 % restants viendront des paires ajoutées plus tard. Un joueur qui débute (la commune plus une paire qui lui plaît) a 5 % de bonus.

Règle : la somme des globaux de toute la collection (hors mythique) égale les stats équipées de la légendaire de référence. Pour la stabilisation, le total vaut ce qu'il faut pour amener la paire la moins stable (l'ange, à 1) à 10/10.

| Stat | Total final visé | Par paire actuelle (2,5 %) | 4 paires actuelles (10 %) |
| --- | --- | --- | --- |
| Stamina | 240 | 6 | 24 |
| Accélération | 9 | 0,225 | 0,9 |
| Vitesse max | 58 | 1,45 | 5,8 |
| Maniabilité | 85 | 2,125 | 8,5 |
| Stabilisation | 9 | 0,225 | 0,9 |

## Physique globale (config serveur, `simplywings-server.toml`)

| Paramètre | Valeur | Effet |
| --- | --- | --- |
| `terminalVelocity` | 110 m/s | Plafond absolu, atteint seulement en repli faucon |
| `diveDragSpeed` | 60 m/s | Vitesse terminale d'un piqué vertical ailes ouvertes |
| `glideDragSpeed` | 150 m/s | Traînée à plat. Plus haut = moins de perte entre deux battements |
| `tuckDragFactor` | 0,35 | Traînée en repli faucon |
| `airDrag` | 1,0 | Multiplicateur global de la traînée (s'ajoute au `drag` de chaque paire) |
| `perturbationSwing` | 0,6 bloc | Amplitude d'instabilité du stationnaire à stabilisation 0 |
| `brakeFactor` | 2,0 | Décélération du frein = accélération × facteur |
| `flapCost` | 5 | Stamina d'un battement de référence (2 battements par seconde) |
| `airJumpCost` | 20 | Stamina par saut ailé |
