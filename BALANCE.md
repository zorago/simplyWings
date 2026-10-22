# Équilibrage des ailes

Les ailes ont 7 stats notées de 0 à 100. Le menu n'en affiche que 5 : Plané et Portance sont des stats internes, jugées trop techniques pour le joueur, mais elles restent actives et la collection les remplit aussi. Dans les JSON (`src/main/resources/data/simplywings/simplywings/wing/*.json`), `equipped` donne les scores de la paire et `global` son bonus de collection. Le menu affiche chaque stat en barre de 10 carreaux : 1 carreau = 10 points, donc 3,7/10 = 37 points.

La valeur physique correspondant à chaque score est réglée dans la config serveur, section `[stats]` : valeur à 0, valeur à 100, interpolation linéaire. La forme et l'animation de chaque paire sont dans `anim/WingStyle.java`.

Dernière mise à jour : 22 octobre 2026.

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

- Score effectif = score de la paire + somme des bonus `global` de toute la collection, plafonné à 100, puis + le bonus `overcap` de la paire (seulement Night et Light : +20 en vitesse, soit 12/10 et 74 m/s). Les échelles de la config s'extrapolent au-delà de 100.
- Les mythiques (Night, Light) ne donnent aucun bonus de collection. Posséder Night donne la stamina infinie, posséder Light débloque la transmog (visuel seulement, aucun effet sur les stats).
- Pour chaque stat, la somme des bonus de toutes les paires vaut **100 − le score le plus bas** parmi les paires. Avec la collection complète, toutes les paires sont donc à 10/10 partout.
- Répartition par palier : **50 %** pour le palier de base (commune + rares), **30 %** pour les épiques, **20 %** pour les légendaires. Dans un palier, chaque paire donne la même part du total des points de bonus.
- **Diversification** : chaque paire donne surtout dans ses points forts. Le poids d'une stat vaut (score ÷ meilleur score de la paire)⁴. Ensuite, un équilibrage itératif garantit deux choses : chaque palier fournit exactement sa part de chaque stat, et chaque paire sa part du total. Ainsi chaque légendaire donne surtout sa stat maxée, la pierre de l'endurance et de la stabilité, l'orage de la vitesse, etc.
- Le calcul est fait par `tools/balance_globals.py` (poids (score ÷ meilleur score)⁴ + 0,01, puis équilibrage itératif) : `python tools/balance_globals.py --write` réécrit les `global` de tous les JSON quand on ajoute ou modifie une paire.

| Stat | Bonus total de la collection |
| --- | --- |
| Endurance | 80 |
| Accélération | 75 |
| Vitesse | 88 |
| Maniabilité | 73 |
| Stabilité | 90 |
| Plané (interne) | 75 |
| Portance (interne) | 75 |

Bonus `global` de chaque paire (en points sur 100) :

| Paire | Palier | Part | Endur. | Accél. | Vitesse | Maniab. | Stabil. | Plané | Portance |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Commune | Base | 2,3 % | 0,3 | 6,0 | 0,4 | 0,4 | 1,0 | 1,9 | 2,6 |
| Chauve-souris | Base | 2,3 % | 0,5 | 2,8 | 0,8 | 4,1 | 0,2 | 3,5 | 0,7 |
| Corbeau | Base | 2,3 % | 0,9 | 1,4 | 1,9 | 6,1 | 0,3 | 1,0 | 1,0 |
| Chouette | Base | 2,3 % | 1,0 | 0,3 | 0,5 | 0,8 | 4,7 | 3,2 | 2,1 |
| Goéland | Base | 2,3 % | 4,0 | 0,3 | 1,4 | 0,4 | 0,2 | 4,7 | 1,5 |
| Colibri | Base | 2,3 % | 0,1 | 0,8 | 0,4 | 3,1 | 4,9 | 0,1 | 3,1 |
| Aigle | Base | 2,3 % | 1,1 | 5,4 | 3,7 | 0,4 | 0,1 | 1,7 | 0,3 |
| Martinet | Base | 2,3 % | 0,3 | 1,9 | 6,6 | 1,5 | 0,1 | 1,9 | 0,4 |
| Pigeon voyageur | Base | 2,3 % | 9,4 | 0,5 | 0,9 | 0,4 | 0,4 | 0,3 | 0,7 |
| Paon | Base | 2,3 % | 0,6 | 0,4 | 0,5 | 0,3 | 10,1 | 0,2 | 0,5 |
| Perroquet | Base | 2,3 % | 0,6 | 2,1 | 1,2 | 5,6 | 0,8 | 0,7 | 1,5 |
| Abeille | Base | 2,3 % | 0,3 | 0,9 | 0,6 | 2,1 | 4,7 | 0,1 | 3,9 |
| Papillon de nuit | Base | 2,3 % | 1,3 | 0,2 | 0,7 | 0,7 | 3,4 | 0,7 | 5,8 |
| Scarabée | Base | 2,3 % | 5,9 | 4,1 | 1,1 | 0,2 | 0,8 | 0,2 | 0,4 |
| Écureuil volant | Base | 2,3 % | 0,5 | 0,1 | 1,1 | 3,7 | 0,3 | 5,5 | 1,4 |
| Poisson volant | Base | 2,3 % | 0,3 | 0,9 | 6,8 | 0,5 | 0,2 | 3,8 | 0,2 |
| Phantom | Base | 2,3 % | 0,7 | 0,7 | 8,1 | 0,9 | 0,1 | 1,7 | 0,5 |
| Améthyste | Base | 2,3 % | 1,3 | 0,3 | 0,6 | 2,8 | 6,1 | 0,2 | 1,4 |
| Ornithoptère | Base | 2,3 % | 6,1 | 0,3 | 0,5 | 0,2 | 3,9 | 0,4 | 1,2 |
| Cerf-volant | Base | 2,3 % | 0,2 | 0,1 | 0,9 | 0,6 | 0,2 | 4,4 | 6,2 |
| Cuivre | Base | 2,3 % | 2,1 | 6,2 | 2,0 | 0,5 | 1,2 | 0,2 | 0,4 |
| Rafistolées | Base | 2,3 % | 2,4 | 1,7 | 3,1 | 1,3 | 1,3 | 1,2 | 1,7 |
| Méganeure | Épique | 2,7 % | 1,3 | 0,2 | 0,1 | 5,1 | 0,8 | 0,3 | 7,3 |
| Ptérosaure | Épique | 2,7 % | 4,1 | 1,2 | 0,7 | 0,3 | 0,4 | 6,7 | 1,7 |
| Flamme | Épique | 2,7 % | 0,4 | 9,5 | 3,1 | 1,0 | 0,3 | 0,4 | 0,5 |
| Marée | Épique | 2,7 % | 1,6 | 0,5 | 0,7 | 3,8 | 2,7 | 4,8 | 1,1 |
| Pierre | Épique | 2,7 % | 5,6 | 0,3 | 0,1 | 0,1 | 8,2 | 0,1 | 0,7 |
| Vent | Épique | 2,7 % | 0,4 | 1,2 | 5,2 | 1,3 | 0,2 | 2,9 | 4,0 |
| Orage | Épique | 2,7 % | 0,3 | 3,2 | 6,8 | 3,3 | 0,2 | 0,7 | 0,7 |
| Aurore | Épique | 2,7 % | 1,2 | 0,2 | 0,6 | 0,5 | 6,8 | 3,5 | 2,3 |
| Ombre | Épique | 2,7 % | 0,8 | 5,0 | 2,9 | 5,0 | 0,4 | 0,6 | 0,5 |
| Nébuleuse | Épique | 2,7 % | 4,7 | 0,7 | 5,9 | 0,4 | 0,8 | 1,5 | 1,1 |
| Esprit sylvestre | Épique | 2,7 % | 3,4 | 0,5 | 0,2 | 1,1 | 6,3 | 1,0 | 2,7 |
| Ange | Légendaire | 3,3 % | 7,9 | 1,3 | 1,7 | 0,3 | 0,2 | 6,7 | 0,4 |
| Démon | Légendaire | 3,3 % | 1,4 | 9,3 | 5,0 | 1,2 | 0,3 | 0,8 | 0,5 |
| Séraphin | Légendaire | 3,3 % | 3,1 | 1,2 | 8,6 | 0,7 | 1,0 | 2,8 | 1,2 |
| Psyché | Légendaire | 3,3 % | 0,9 | 0,2 | 0,2 | 0,8 | 11,1 | 1,1 | 4,2 |
| Dédale | Légendaire | 3,3 % | 0,8 | 1,7 | 0,8 | 9,4 | 1,3 | 0,7 | 3,9 |
| Yin et Yang | Légendaire | 3,3 % | 1,9 | 1,2 | 1,3 | 2,2 | 4,2 | 3,0 | 4,9 |

## Scores des paires (stats équipées, sur 100)

Une légendaire a par définition au moins une stat visible maxée à 100 (en gras), sauf Yin et Yang, à 90 partout. Plané et Portance n'ont pas de légendaire dédiée.

| Paire | Rareté | Endurance | Accél. | Vitesse | Maniab. | Stabilité | Plané | Portance | Identité |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Commune | Commune | 20 | 50 | 12 | 27 | 30 | 40 | 40 | Élytres améliorées, la base |
| Chauve-souris | Rare | 40 | 70 | 31 | 82 | 30 | 80 | 48 | Aérodynamique et agile |
| Corbeau | Rare | 40 | 50 | 35 | 78 | 30 | 50 | 45 | Rusé et vif |
| Chouette | Rare | 45 | 35 | 25 | 50 | 70 | 72 | 60 | Le vol silencieux |
| Goéland | Rare | 65 | 35 | 35 | 40 | 30 | 80 | 55 | Le voyageur des côtes |
| Colibri | Rare | 25 | 50 | 25 | 75 | 75 | 30 | 70 | Le roi du surplace |
| Aigle | Rare | 45 | 75 | 45 | 40 | 25 | 60 | 35 | Le rapace |
| Martinet | Rare | 35 | 60 | 55 | 60 | 20 | 65 | 40 | Le plus rapide des oiseaux |
| Pigeon voyageur | Rare | 80 | 40 | 30 | 40 | 35 | 40 | 45 | Gris, banal et infatigable |
| Paon | Rare | 30 | 30 | 15 | 30 | 65 | 25 | 30 | La paire vitrine |
| Perroquet | Rare | 35 | 55 | 30 | 75 | 40 | 45 | 50 | Le clin d'œil au perroquet de Minecraft |
| Abeille | Rare | 30 | 45 | 25 | 60 | 65 | 25 | 65 | Le clin d'œil à l'abeille de Minecraft |
| Papillon de nuit | Rare | 45 | 30 | 25 | 45 | 60 | 45 | 72 | Poudré et nocturne |
| Scarabée | Rare | 65 | 65 | 30 | 30 | 40 | 30 | 35 | Le cousin des élytres |
| Écureuil volant | Rare | 35 | 25 | 30 | 70 | 30 | 78 | 50 | Le planeur |
| Poisson volant | Rare | 30 | 45 | 50 | 40 | 25 | 70 | 30 | Décalé et fun |
| Phantom | Rare | 40 | 45 | 55 | 50 | 25 | 60 | 40 | Le cauchemar des insomniaques |
| Améthyste | Rare | 45 | 35 | 25 | 65 | 70 | 30 | 50 | Du cristal vanilla |
| Ornithoptère | Rare | 70 | 35 | 25 | 35 | 65 | 40 | 50 | La machine de Léonard de Vinci |
| Cerf-volant | Rare | 25 | 25 | 30 | 45 | 30 | 78 | 78 | Papier et baguettes |
| Cuivre | Rare | 50 | 72 | 35 | 40 | 45 | 30 | 35 | La mécanique du cuivre de la 1 |
| Rafistolées | Rare | 45 | 45 | 35 | 45 | 40 | 45 | 45 | Cuir, plumes et ficelle |
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
| Yin et Yang | Légendaire | 90 | 90 | 90 | 90 | 90 | 90 | 90 | L'équilibre : aucune stat maxée, aucune faiblesse (exception à la règle) |
| Night | Mythique | 100 | 100 | **120** | 100 | 100 | 100 | 100 | Furie Nocturne : les ailes dieu |
| Light | Mythique | 100 | 100 | **120** | 100 | 100 | 100 | 100 | Furie Éclair : les ailes dieu |

## Paramètres d'identité (hors collection)

| Paire | Battements/s | Stationnaire (cadence, vitesse) | Ailes par côté |
| --- | --- | --- | --- |
| Commune | 2,0 | 1,2 Hz, 8 m/s | 1 |
| Chauve-souris | 2,6 | 1,8 Hz, 11 m/s | 1 |
| Corbeau | 2,4 | 1,6 Hz, 10 m/s | 1 |
| Chouette | 1,2 | 1 Hz, 8 m/s | 1 |
| Goéland | 1 | 1,4 Hz, 9 m/s | 1 |
| Colibri | 30, continu | 40 Hz, 12 m/s | 1 |
| Aigle | 1 | 1 Hz, 10 m/s | 1 |
| Martinet | 2,8 | 2,4 Hz, 11 m/s | 1 |
| Pigeon voyageur | 2,2 | 1,8 Hz, 8 m/s | 1 |
| Paon | 1,6 | 1,2 Hz, 7 m/s | 1 |
| Perroquet | 2,6 | 2 Hz, 10 m/s | 1 |
| Abeille | 25, continu | 30 Hz, 10 m/s | 2 |
| Papillon de nuit | 1,8 | 1,5 Hz, 8 m/s | 2 |
| Scarabée | 12, continu | 20 Hz, 9 m/s | 2 |
| Écureuil volant | 1,2 | 1,2 Hz, 8 m/s | 1 |
| Poisson volant | 3 | 2,5 Hz, 9 m/s | 1 |
| Phantom | 1,3 | 1,2 Hz, 11 m/s | 1 |
| Améthyste | 1,4 | 1,1 Hz, 9 m/s | 1 |
| Ornithoptère | 1,2 | 1 Hz, 8 m/s | 1 |
| Cerf-volant | 0,8 | 0,8 Hz, 7 m/s | 1 |
| Cuivre | 1,6 | 1,4 Hz, 9 m/s | 1 |
| Rafistolées | 1,8 | 1,4 Hz, 9 m/s | 1 |
| Méganeure | 20 (continu) | 25 Hz, 14 m/s | 2 |
| Ptérosaure | 0,9 | 1,1 Hz, 10 m/s | 1 |
| Flamme | 1,4 | 1,6 Hz, 14 m/s | 1 |
| Marée | 1,0 | 0,8 Hz, 13 m/s | 1 |
| Pierre | 0,8 | 0,7 Hz, 10 m/s | 1 |
| Vent | 1,6 | 1,4 Hz, 16 m/s | 2 |
| Orage | 1,8 | 2,0 Hz, 16 m/s | 1 |
| Aurore | 0,7 | 0,6 Hz, 14 m/s | 2 |
| Ombre | 1,2 | 1,3 Hz, 15 m/s | 1 |
| Nébuleuse | 0,8 | 0,9 Hz, 16 m/s | 1 |
| Esprit sylvestre | 1,0 | 1,0 Hz, 12 m/s | 2 |
| Ange | 0,5 | 0,6 Hz, 19 m/s | 1 |
| Psyché | 0,6 | 0,5 Hz, 20 m/s | 2 |
| Dédale | 20 (continu, réacteurs) | 30 Hz, 18 m/s | 1 |
| Démon | 0,7 | 1,0 Hz, 18 m/s | 1 |
| Séraphin | 0,9 | 0,9 Hz, 20 m/s | 3 |
| Yin et Yang | 1,0 | 0,5 Hz, 19 m/s | 1 (asymétrique) |

Rappels de mécanique :
- **Gain d'un battement** = accélération ÷ battements par seconde. À partir de 8 battements/s, la poussée est continue.
- **Coût en stamina** : identique par seconde pour toutes les paires.
- **Saut ailé** : vitesse verticale = `airJumpBase` + gain d'un battement × `airJumpPerBeatGain`.
- **Lancement** (envol chargé, ou passage du stationnaire au déplacement) : il part à `launchSpeedFraction` × vitesse max, et le premier battement est disponible tout de suite.
- **Instabilité du stationnaire** : `perturbationSwing` × (10 − stabilité) / 10, seulement selon la stat. Balancement rythmé pour les ailes qui battent, remous aléatoires de même ampleur pour les ailes à vibration continue (8 battements/s ou plus).

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
