# Équilibrage des ailes

Les ailes ont 7 stats notées de 0 à 100. Le menu n'en affiche que 5 : Plané et Portance sont des stats internes, jugées trop techniques pour le joueur, mais elles restent actives et la collection les remplit aussi. Dans les JSON (`src/main/resources/data/simplywings/simplywings/wing/*.json`), `equipped` donne les scores de la paire et `global` son bonus de collection. Le menu affiche chaque stat en barre de 10 carreaux : 1 carreau = 10 points, donc 3,7/10 = 37 points.

La valeur physique correspondant à chaque score est réglée dans la config serveur, section `[stats]` : valeur à 0, valeur à 100, interpolation linéaire. La forme et l'animation de chaque paire sont dans `anim/WingStyle.java`.

Dernière mise à jour : 20 octobre 2026.

## Les 7 stats

| Stat | Valeur à 0 | Valeur à 100 | Effet |
| --- | --- | --- | --- |
| Endurance | 50 de stamina | 300 de stamina | Réserve pour les battements et le stationnaire |
| Accélération | 2 m/s par s | 10 m/s par s | Gain des battements, puissance du frein, hauteur du saut ailé |
| Vitesse | 12 m/s | 64 m/s | Vitesse de croisière max en battant |
| Maniabilité | 40 °/s | 150 °/s | Vitesse de rotation au clavier |
| Stabilité | 0/10 | 10/10 | Précision du stationnaire |
| Plané (interne) | traînée × 1,4 | traînée × 0,4 | Distance de plané, perte de vitesse entre deux battements |
| Portance (interne) | décroche sous 14 m/s | décroche sous 3 m/s | Vitesse de décrochage |

Le plafond de vitesse (64 m/s) correspond à l'ange avec les 4 premières paires débloquées : c'est la vitesse max visée.

## Règle de la collection

- Score effectif = score de la paire + somme des bonus `global` de toute la collection, plafonné à 100.
- Pour chaque stat, la somme des bonus de toutes les paires vaut **100 − le score le plus bas** parmi les paires. Avec la collection complète, toutes les paires sont donc à 10/10 partout.
- Répartition actuelle : les 4 premières paires (commune, chauve-souris, ancienne, ange) donnent 2,5 % chacune (10 % au total), les 4 autres légendaires (démon, séraphin, Hermès, Dédale) 22,5 % chacune (90 %). Si on ajoute ou retire des paires, il faudra redistribuer.

| Stat | Bonus total de la collection | Par paire de départ (2,5 %) | Par autre légendaire (22,5 %) |
| --- | --- | --- | --- |
| Endurance | 80 | 2 | 18 |
| Accélération | 65 | 1,6 | 14,6 |
| Vitesse | 88 | 2,2 | 19,8 |
| Maniabilité | 73 | 1,8 | 16,4 |
| Stabilité | 90 | 2,25 | 20,25 |
| Plané (interne) | 60 | 1,5 | 13,5 |
| Portance (interne) | 75 | 1,9 | 16,9 |

## Scores des paires (stats équipées, sur 100)

Une légendaire a par définition au moins une stat visible maxée à 100 (en gras). Plané et Portance n'ont pas de légendaire dédiée.

| Paire | Rareté | Endurance | Accél. | Vitesse | Maniab. | Stabilité | Plané | Portance | Identité |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Commune | Commune | 20 | 50 | 12 | 27 | 30 | 40 | 40 | Élytres améliorées, la base |
| Chauve-souris | Rare | 40 | 70 | 31 | 82 | 30 | 80 | 48 | Aérodynamique et agile |
| Ancienne (libellule) | Épique | 60 | 35 | 23 | 91 | 40 | 40 | 86 | Faite pour construire, médiocre en vol |
| Ange | Légendaire | **100** | 70 | 75 | 41 | 10 | 85 | 30 | L'endurance : vol majestueux, plane loin |
| Démon | Légendaire | 55 | **100** | 85 | 50 | 15 | 40 | 25 | La puissance : coups violents et tardifs |
| Hermès | Légendaire | 50 | 75 | 60 | **100** | 45 | 55 | 60 | La maniabilité ; messager des dieux, 3 ailes dorées par côté |
| Séraphin | Légendaire | 70 | 60 | **100** | 45 | 40 | 60 | 40 | La vitesse ; 6 ailes d'ange dorées |
| Dédale | Légendaire | 65 | 55 | 50 | 60 | **100** | 45 | 70 | La stabilité ; ailes mécaniques à réacteurs, poussée continue |

## Paramètres d'identité (hors collection)

| Paire | Battements/s | Stationnaire (cadence, vitesse, amplitude) | Ailes par côté |
| --- | --- | --- | --- |
| Commune | 2,0 | 1,2 Hz, 8 m/s, × 1 | 1 |
| Chauve-souris | 2,6 | 1,8 Hz, 11 m/s, × 1 | 1 |
| Libellule | 20 (continu) | 25 Hz, 14 m/s, × 0,1 | 2 |
| Ange | 0,5 | 0,6 Hz, 19 m/s, × 1 | 1 |
| Hermès | 6 | 6 Hz, 18 m/s, × 0,4 | 3 |
| Dédale | 20 (continu, réacteurs) | 30 Hz, 20 m/s, × 0 | 1 |
| Démon | 0,7 | 1,0 Hz, 18 m/s, × 1 | 1 |
| Séraphin | 0,9 | 0,9 Hz, 20 m/s, × 1 | 3 |

Rappels de mécanique :
- **Gain d'un battement** = accélération ÷ battements par seconde. À partir de 8 battements/s, la poussée est continue.
- **Coût en stamina** : identique par seconde pour toutes les paires.
- **Saut ailé** : vitesse verticale = `airJumpBase` + gain d'un battement × `airJumpPerBeatGain`.
- **Lancement** (envol chargé, ou passage du stationnaire au déplacement) : il part à `launchSpeedFraction` × vitesse max, et le premier battement est disponible tout de suite.
- **Instabilité du stationnaire** : `perturbationSwing` × (10 − stabilité) / 10 × amplitude de la paire.

## Physique globale (config serveur, `simplywings-server.toml`)

| Paramètre | Valeur | Effet |
| --- | --- | --- |
| `terminalVelocity` | 110 m/s | Plafond absolu, atteint seulement en repli faucon |
| `diveDragSpeed` | 60 m/s | Vitesse terminale d'un piqué vertical ailes ouvertes |
| `glideDragSpeed` | 150 m/s | Traînée de base à plat (multipliée par le Plané) |
| `tuckDragFactor` | 0,35 | Traînée en repli faucon |
| `perturbationSwing` | 0,6 bloc | Amplitude d'instabilité du stationnaire à stabilité 0 |
| `launchSpeedFraction` | 0,5 | Vitesse de lancement, en fraction de la vitesse max |
| `brakeFactor` | 2,0 | Décélération du frein = accélération × facteur |
| `flapCost` | 5 | Stamina d'un battement de référence (2 battements/s) |
| `airJumpCost` | 20 | Stamina par saut ailé |
