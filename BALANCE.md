# Équilibrage des ailes

Tableau de suivi des plages de puissance par tier et de la position de chaque paire. Les valeurs vivent dans `src/main/resources/data/simplywings/simplywings/wing/*.json` et se modifient sans recompiler (relancer le monde suffit).

Dernière mise à jour : 20 octobre 2026.

## Plages par tier (stats équipées)

| Stat | Unité | Commune | Rare | Épique | Légendaire |
| --- | --- | --- | --- | --- | --- |
| Stamina | points | 80 – 120 | 120 – 170 | 170 – 220 | 220 – 280 |
| Accélération (gain d'un battement à basse vitesse, et puissance du frein) | m/s | 2 – 4 | 4 – 6,5 | 6,5 – 10 | 10 – 16 |
| Vitesse max (croisière en battant) | m/s | 14 – 22 | 22 – 32 | 32 – 46 | 46 – 62 |
| Vitesse min (décrochage) | m/s | 6 – 9 | 6 – 10 | 8 – 12 | 10 – 14 |
| Maniabilité | °/s | 50 – 80 | 60 – 130 | 50 – 110 | 60 – 100 |
| Stabilisation | 0 – 10 | 4 – 7 | 2 – 8 | 2 – 7 | 1 – 5 |
| Battements par seconde (déplacement) | /s | 1,8 – 2,4 | 1,4 – 3,0 | 1,2 – 2,2 | 1,0 – 1,8 |
| Vitesse en stationnaire (horizontale et verticale) | m/s | 6 – 10 | 9 – 13 | 12 – 16 | 16 – 22 |

Repère de ressenti : il faut environ 5 battements pour atteindre 90 % de la vitesse max, 4 pour une légendaire. Simulation à plat depuis la vitesse min, avec la traînée par défaut.

## Position des paires existantes

Le pourcentage indique où se place la valeur dans la plage de son tier (0 % = minimum, 100 % = maximum).

| Paire | Tier | Stamina | Accél. | V max | V min | Maniab. | Stabil. | Batt./s | Station. | Battements → 90 % V max |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Commune (`common`) | Commune | 100 (50 %) | 3 (50 %) | 18 (50 %) | 8 (67 %) | 70 (67 %) | 5 (33 %) | 2,0 (33 %) | 8 (50 %) | 5 |
| Chauve-souris (`bat`) | Rare | 150 (60 %) | 5 (40 %) | 28 (60 %) | 7 (25 %) | 120 (86 %) | 6 (67 %) | 2,6 (75 %) | 11 (50 %) | 5 |
| Ancienne (`ancient`) | Épique | 200 (60 %) | 8 (43 %) | 40 (57 %) | 10 (50 %) | 60 (17 %) | 4 (40 %) | 1,5 (30 %) | 14 (50 %) | 5 |
| Ange (`angel`) | Légendaire | 240 (33 %) | 14 (67 %) | 58 (75 %) | 12 (50 %) | 85 (63 %) | 2 (25 %) | 1,2 (25 %) | 19 (50 %) | 4 |

Identités :
- **Commune** : la plus simple, sans point fort.
- **Chauve-souris** : la plus maniable et stable, mais lente.
- **Ancienne** : un planeur lourd, rapide en ligne droite et peu maniable.
- **Ange** : puissance brute, mais très instable en stationnaire.

## Bonus globaux

Règle : la somme des globaux de toute la collection (hors mythique) égale les stats équipées de la légendaire de référence. Pour la stabilisation, le total vaut ce qu'il faut pour amener la paire la moins stable à 10/10.

| Stat | Total final visé | Par paire actuelle (10 %) |
| --- | --- | --- |
| Stamina | 240 | 24 |
| Accélération | 14 | 1,4 |
| Vitesse max | 58 | 5,8 |
| Maniabilité | 85 | 8,5 |
| Stabilisation | 8 | 0,8 |

Avec les 4 paires de test débloquées, chaque paire reçoit +40 % des stats de la légendaire. La commune passe alors à 41 m/s de vitesse max et 8,6 d'accélération. **Pour juger le ressenti brut d'une paire, il faut la tester seule** : `/simplywings reset`, puis débloquer uniquement celle-là.

Quand la liste définitive des paires sera fixée, les 10 % par paire seront remplacés par une répartition du total final sur toutes les paires.

## Physique globale (config serveur, `simplywings-server.toml`)

| Paramètre | Valeur | Effet |
| --- | --- | --- |
| `terminalVelocity` | 110 m/s | Plafond absolu, atteint seulement en repli faucon |
| `diveDragSpeed` | 60 m/s | Vitesse terminale d'un piqué vertical ailes ouvertes |
| `glideDragSpeed` | 150 m/s | Traînée à plat. Plus haut = moins de perte entre deux battements |
| `tuckDragFactor` | 0,35 | Traînée en repli faucon |
| `airDrag` | 1,0 | Multiplicateur global de la traînée |
| `brakeFactor` | 2,0 | Décélération du frein = accélération × facteur |
| `flapCost` | 5 | Stamina par battement |
| `airJumpCost` | 20 | Stamina par saut ailé |
