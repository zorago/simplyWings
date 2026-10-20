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
- Répartition actuelle : les 4 premières paires (commune, chauve-souris, méganeure, ange) donnent 2,5 % chacune (10 % au total), les 10 autres épiques (flamme, marée, pierre, vent, ptérosaure, orage, aurore, ombre, nébuleuse, esprit sylvestre) 3 % chacune (30 %), et les 4 autres légendaires (démon, séraphin, Psyché, Dédale) 15 % chacune (60 %). Si on ajoute ou retire des paires, il faudra redistribuer.

| Stat | Bonus total de la collection | Par paire de départ (2,5 %) | Par autre épique (3 %) | Par autre légendaire (15 %) |
| --- | --- | --- | --- | --- |
| Endurance | 80 | 2 | 2,4 | 12 |
| Accélération | 65 | 1,6 | 1,95 | 9,75 |
| Vitesse | 88 | 2,2 | 2,6 | 13,2 |
| Maniabilité | 73 | 1,8 | 2,2 | 10,95 |
| Stabilité | 90 | 2,25 | 2,7 | 13,5 |
| Plané (interne) | 75 | 1,9 | 2,25 | 11,25 |
| Portance (interne) | 75 | 1,9 | 2,25 | 11,25 |

## Scores des paires (stats équipées, sur 100)

Une légendaire a par définition au moins une stat visible maxée à 100 (en gras). Plané et Portance n'ont pas de légendaire dédiée.

| Paire | Rareté | Endurance | Accél. | Vitesse | Maniab. | Stabilité | Plané | Portance | Identité |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Commune | Commune | 20 | 50 | 12 | 27 | 30 | 40 | 40 | Élytres améliorées, la base |
| Chauve-souris | Rare | 40 | 70 | 31 | 82 | 30 | 80 | 48 | Aérodynamique et agile |
| Méganeure (libellule fossile) | Épique | 60 | 35 | 23 | 91 | 40 | 40 | 86 | Faite pour construire, médiocre en vol |
| Ptérosaure | Épique | 75 | 55 | 45 | 40 | 30 | 85 | 55 | Le planeur : fond et plané, lourd à manœuvrer |
| Flamme | Épique | 35 | 85 | 60 | 50 | 20 | 35 | 35 | Le feu : accélération explosive, s'épuise vite |
| Marée | Épique | 60 | 45 | 45 | 80 | 55 | 80 | 50 | L'eau : fluide, maniable, garde son élan |
| Pierre | Épique | 85 | 40 | 25 | 30 | 75 | 25 | 45 | La terre : endurance et stabilité, lente et lourde |
| Vent | Épique | 45 | 60 | 82 | 65 | 25 | 75 | 75 | L'air : vitesse et légèreté, instable |
| Orage | Épique | 40 | 75 | 85 | 80 | 20 | 50 | 45 | Vif et nerveux, s'épuise vite |
| Aurore | Épique | 65 | 40 | 50 | 55 | 80 | 85 | 70 | Paisible : stabilité et plané |
| Ombre | Épique | 50 | 80 | 65 | 85 | 30 | 45 | 40 | Départs vifs et virages serrés |
| Nébuleuse | Épique | 80 | 50 | 80 | 45 | 40 | 60 | 50 | Long cours : endurance et vitesse |
| Esprit sylvestre | Épique | 75 | 45 | 35 | 60 | 70 | 55 | 65 | Calme : endurance et stabilité, lent |
| Ange | Légendaire | **100** | 70 | 75 | 41 | 10 | 85 | 30 | L'endurance : vol majestueux, plane loin |
| Démon | Légendaire | 55 | **100** | 85 | 50 | 15 | 40 | 25 | La puissance : coups violents et tardifs |
| Psyché | Légendaire | 65 | 50 | 50 | 60 | **100** | 60 | 75 | La stabilité ; ailes de papillon en flammes d'âme éthérées |
| Séraphin | Légendaire | 70 | 60 | **100** | 45 | 40 | 60 | 40 | La vitesse ; 6 ailes d'ange dorées |
| Dédale | Légendaire | 55 | 75 | 60 | **100** | 50 | 45 | 65 | La maniabilité ; ailes mécaniques à réacteurs vectorisés |

## Paramètres d'identité (hors collection)

| Paire | Battements/s | Stationnaire (cadence, vitesse, amplitude) | Ailes par côté |
| --- | --- | --- | --- |
| Commune | 2,0 | 1,2 Hz, 8 m/s, × 1 | 1 |
| Chauve-souris | 2,6 | 1,8 Hz, 11 m/s, × 1 | 1 |
| Méganeure | 20 (continu) | 25 Hz, 14 m/s, × 0,1 | 2 |
| Ptérosaure | 0,9 | 1,1 Hz, 10 m/s, × 1,1 | 1 |
| Flamme | 1,4 | 1,6 Hz, 14 m/s, × 1 | 1 |
| Marée | 1,0 | 0,8 Hz, 13 m/s, × 0,6 | 1 |
| Pierre | 0,8 | 0,7 Hz, 10 m/s, × 0,3 | 1 |
| Vent | 1,6 | 1,4 Hz, 16 m/s, × 1,2 | 2 |
| Orage | 1,8 | 2,0 Hz, 16 m/s, × 1,2 | 1 |
| Aurore | 0,7 | 0,6 Hz, 14 m/s, × 0,2 | 2 |
| Ombre | 1,2 | 1,3 Hz, 15 m/s, × 0,9 | 1 |
| Nébuleuse | 0,8 | 0,9 Hz, 16 m/s, × 0,7 | 1 |
| Esprit sylvestre | 1,0 | 1,0 Hz, 12 m/s, × 0,5 | 2 |
| Ange | 0,5 | 0,6 Hz, 19 m/s, × 1 | 1 |
| Psyché | 0,6 | 0,5 Hz, 20 m/s, × 0 | 2 |
| Dédale | 20 (continu, réacteurs) | 30 Hz, 18 m/s, × 0,3 | 1 |
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
