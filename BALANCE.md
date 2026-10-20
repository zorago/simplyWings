# Équilibrage des ailes

Tableau de suivi des plages de puissance par tier et de la position de chaque paire. Les valeurs vivent dans `src/main/resources/data/simplywings/simplywings/wing/*.json` et se modifient sans recompiler (relancer le monde suffit). La forme et l'animation de chaque paire (nombre d'ailes, profil de battement) sont dans `anim/WingStyle.java`.

Dernière mise à jour : 20 octobre 2026.

## Plages par tier (stats équipées)

| Stat | Unité | Commune | Rare | Épique | Légendaire |
| --- | --- | --- | --- | --- | --- |
| Stamina | points | 80 – 120 | 120 – 170 | 170 – 220 | 220 – 280 |
| Accélération (gain par seconde de battement, et puissance du frein) | m/s par s | 4 – 8 | 8 – 15 | 8 – 14 | 8 – 12 |
| Vitesse max (croisière en battant) | m/s | 14 – 22 | 22 – 32 | 32 – 46 | 46 – 62 |
| Vitesse min (décrochage) | m/s | 6 – 9 | 6 – 10 | 8 – 12 | 10 – 14 |
| Maniabilité | °/s | 50 – 80 | 60 – 140 | 50 – 110 | 60 – 100 |
| Stabilisation | 0 – 10 | 2 – 5 | 2 – 6 | 2 – 6 | 0,5 – 4 |
| Battements par seconde (déplacement) | /s | 1,5 – 2,4 | 1,5 – 3,0 | 0,5 – 25 | 0,4 – 1,2 |
| Vitesse en stationnaire (horizontale et verticale) | m/s | 6 – 10 | 9 – 13 | 12 – 16 | 16 – 22 |
| Traînée (`drag`, multiplicateur) | × | 0,9 – 1,1 | 0,5 – 1,0 | 0,8 – 1,1 | 0,9 – 1,1 |

Le gain d'un battement vaut accélération ÷ battements par seconde. À partir de 8 battements par seconde, l'aile est continue : la poussée s'applique à chaque tick tant que sprint est maintenu. Le coût en stamina est identique par seconde pour toutes les paires : un battement coûte `flapCost × 2 ÷ battements par seconde`.

## Position des paires existantes

Le pourcentage indique où se place la valeur dans la plage de son tier (0 % = minimum, 100 % = maximum).

| Paire | Tier | Stamina | Accél. | V max | V min | Maniab. | Stabil. | Batt./s | Station. | Traînée | Atteindre 90 % de V max |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Commune (`common`) | Commune | 100 (50 %) | 6 (50 %) | 18 (50 %) | 8 (67 %) | 70 (67 %) | 3 (33 %) | 2,0 (56 %) | 8 (50 %) | 1,0 | 5 battements, 2,4 s |
| Chauve-souris (`bat`) | Rare | 150 (60 %) | 13 (71 %) | 28 (60 %) | 7 (25 %) | 130 (88 %) | 3 (25 %) | 2,6 (73 %) | 11 (50 %) | 0,6 | 5 battements, 1,9 s |
| Ancienne / libellule (`ancient`) | Épique | 200 (60 %) | 10 (33 %) | 40 (57 %) | 10 (50 %) | 80 (50 %) | 4 (50 %) | 20 (80 %, continue) | 14 (50 %) | 1,0 | poussée continue, 4,1 s |
| Ange (`angel`) | Légendaire | 240 (33 %) | 9 (25 %) | 58 (75 %) | 12 (50 %) | 85 (63 %) | 1 (14 %) | 0,5 (13 %) | 19 (50 %) | 1,0 | 3 battements, 5,5 s |

Identités :
- **Commune** : la version de base, des élytres améliorées.
- **Chauve-souris** : aérodynamique (traînée 0,6) et très maniable, mais vitesse max basse.
- **Ancienne / libellule** : 4 ailes d'un seul bloc qui vibrent à 25 Hz. Accélération presque linéaire, et la seule paire stable en stationnaire.
- **Ange** : la puissance. Un battement toutes les 2 secondes : longue montée, puis coup sec qui pousse d'environ 18 m/s. Très instable en stationnaire.

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
