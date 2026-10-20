# Les ailes de Simply Wings

Catalogue des paires d'ailes : identité, apparence, ressenti en vol et stats. Les chiffres viennent des JSON (`src/main/resources/data/simplywings/simplywings/wing/`) et de la config serveur `[stats]`. Les règles d'équilibrage sont dans `BALANCE.md`.

Dernière mise à jour : 20 octobre 2026.

## Paliers d'ambiance

- **Commune** : la base, des élytres améliorées.
- **Rare** : des créatures réelles (oiseaux, insectes, mammifères, mobs vanilla) et des ailes bricolées ou mécaniques, dans une ambiance vanilla, simple, « faite main ». Effets discrets ou absents.
- **Épique** : des esprits, des éléments et des créatures disparues (préhistoriques), avec des thèmes qui sortent du vanilla.
- **Légendaire** : des dieux, des êtres divins et des figures mythologiques. Chaque légendaire maxe une stat visible.
- **Mythique** : Night et Light, les ailes des Furies de *Dragons*. 10/10 partout, +2 en vitesse au-delà du plafond, cachées en « ??? » tant qu'elles ne sont pas débloquées. Les deux ensemble donnent la stamina infinie.

## Lire les stats

- Le joueur voit **5 stats sur 10** (Endurance, Accélération, Vitesse, Maniabilité, Stabilité). En interne, chaque stat est notée sur 100 : 7,6/10 = 76 points.
- Deux stats sont **internes**, jamais affichées : **Plané** (traînée de l'air) et **Portance** (vitesse de décrochage).
- Les valeurs ci-dessous sont celles de la paire **seule**. La collection ajoute des bonus jusqu'à 10/10 partout quand elle est complète.
- **Gain par battement** = accélération ÷ battements par seconde. C'est ce que rapporte un coup d'aile, et ce qui fixe la hauteur du saut ailé.

## Vue d'ensemble

| Paire | Rareté | Endur. | Accél. | Vitesse | Maniab. | Stabil. | En une phrase |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Ailes communes | Commune | 2,0 | 5,0 | 1,2 | 2,7 | 3,0 | Des élytres améliorées, le point de départ |
| Ailes de chauve-souris | Rare | 4,0 | 7,0 | 3,1 | 8,2 | 3,0 | Agiles et aérodynamiques |
| Ailes de corbeau | Rare | 4 | 5 | 3,5 | **7,8** | 3 | Rusé et vif |
| Ailes de chouette | Rare | 4,5 | 3,5 | 2,5 | 5 | **7** | Le vol silencieux |
| Ailes de goéland | Rare | **6,5** | 3,5 | 3,5 | 4 | 3 | Le voyageur des côtes |
| Ailes de colibri | Rare | 2,5 | 5 | 2,5 | **7,5** | 7,5 | Le roi du surplace |
| Ailes d'aigle | Rare | 4,5 | **7,5** | 4,5 | 4 | 2,5 | Le rapace |
| Ailes de martinet | Rare | 3,5 | **6** | 5,5 | 6 | 2 | Le plus rapide des oiseaux |
| Ailes de pigeon voyageur | Rare | **8** | 4 | 3 | 4 | 3,5 | Gris, banal et infatigable |
| Ailes de paon | Rare | 3 | 3 | 1,5 | 3 | **6,5** | La paire vitrine |
| Ailes de perroquet | Rare | 3,5 | 5,5 | 3 | **7,5** | 4 | Le clin d'œil au perroquet de Minecraft |
| Ailes d'abeille | Rare | 3 | 4,5 | 2,5 | 6 | **6,5** | Le clin d'œil à l'abeille de Minecraft |
| Ailes de papillon de nuit | Rare | 4,5 | 3 | 2,5 | 4,5 | **6** | Poudré et nocturne |
| Ailes de scarabée | Rare | **6,5** | 6,5 | 3 | 3 | 4 | Le cousin des élytres |
| Ailes d'écureuil volant | Rare | 3,5 | 2,5 | 3 | **7** | 3 | Le planeur |
| Ailes de poisson volant | Rare | 3 | 4,5 | **5** | 4 | 2,5 | Décalé et fun |
| Ailes de phantom | Rare | 4 | 4,5 | **5,5** | 5 | 2,5 | Le cauchemar des insomniaques |
| Ailes d'améthyste | Rare | 4,5 | 3,5 | 2,5 | 6,5 | **7** | Du cristal vanilla |
| Ailes d'ornithoptère | Rare | **7** | 3,5 | 2,5 | 3,5 | 6,5 | La machine de Léonard de Vinci |
| Ailes de cerf-volant | Rare | 2,5 | 2,5 | 3 | **4,5** | 3 | Papier et baguettes |
| Ailes de cuivre | Rare | 5 | **7,2** | 3,5 | 4 | 4,5 | La mécanique du cuivre de la 1 |
| Ailes rafistolées | Rare | **4,5** | 4,5 | 3,5 | 4,5 | 4 | Cuir, plumes et ficelle |
| Ailes de méganeure | Épique | 6,0 | 3,5 | 2,3 | 9,1 | 4,0 | Une libellule fossile faite pour construire |
| Ailes de ptérosaure | Épique | 7,5 | 5,5 | 4,5 | 4,0 | 3,0 | Le planeur préhistorique |
| Ailes de flamme | Épique | 3,5 | 8,5 | 6,0 | 5,0 | 2,0 | L'élément feu : des coups explosifs |
| Ailes de la marée | Épique | 6,0 | 4,5 | 4,5 | 8,0 | 5,5 | L'élément eau : fluide et maniable |
| Ailes de pierre | Épique | 8,5 | 4,0 | 2,5 | 3,0 | 7,5 | L'élément terre : lourdes, solides, inépuisables |
| Ailes du vent | Épique | 4,5 | 6,0 | 8,2 | 6,5 | 2,5 | L'élément air : légères et rapides |
| Ailes de l'orage | Épique | 4,0 | 7,5 | 8,5 | 8,0 | 2,0 | Des éclairs crépitants, vifs et nerveux |
| Ailes d'aurore | Épique | 6,5 | 4,0 | 5,0 | 5,5 | 8,0 | Des voiles d'aurore boréale, paisibles |
| Ailes d'ombre | Épique | 5,0 | 8,0 | 6,5 | 8,5 | 3,0 | De la fumée noire, vive et discrète |
| Ailes de nébuleuse | Épique | 8,0 | 5,0 | 8,0 | 4,5 | 4,0 | Un ciel étoilé, un voyage au long cours |
| Ailes de l'esprit sylvestre | Épique | 7,5 | 4,5 | 3,5 | 6,0 | 7,0 | Feuilles et fleurs, calmes et patientes |
| Ailes d'ange | Légendaire | **10** | 7,0 | 7,5 | 4,1 | 1,0 | L'endurance, un vol majestueux |
| Ailes de démon | Légendaire | 5,5 | **10** | 8,5 | 5,0 | 1,5 | La puissance brute, des coups violents |
| Ailes de séraphin | Légendaire | 7,0 | 6,0 | **10** | 4,5 | 4,0 | La vitesse, six ailes dorées |
| Ailes de Psyché | Légendaire | 6,5 | 5,0 | 5,0 | 6,0 | **10** | Énergie d'âme éthérée qui tient en apesanteur |
| Ailes de Dédale | Légendaire | 5,5 | 7,5 | 6,0 | **10** | 5,0 | Ailes mécaniques à réacteurs vectorisés |

---

## Ailes communes

`simplywings:common` · **Commune** · item `common_wings`

**Vibe** : la première paire, celle qui débloque le mod. Des élytres améliorées, sans point fort ni vrai défaut. Elles décrochent facilement, ce qui apprend au joueur à gérer sa vitesse.

**Apparence** : une aile par côté, en trois segments, faite de plaques grises simples. Au repos, repliée le long du corps à l'extérieur des épaules.

**En vol** :
- **Déplacement** : 2 battements par seconde, réguliers, de +3 m/s chacun. Environ 5 battements pour atteindre la vitesse max.
- **Stationnaire** : battement à 1,2 Hz et instable, avec un balancement d'environ 0,8 bloc d'amplitude.

**Obtention** : assemblée dans la forge de l'End avec 2 plumes, 1 élytre (enchantée ou non) et 1 cuir.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 2,0/10 | 100 de stamina |
| Accélération | 5,0/10 | 6 m/s par s, soit +3 m/s par battement |
| Vitesse | 1,2/10 | 18 m/s |
| Maniabilité | 2,7/10 | 70 °/s |
| Stabilité | 3,0/10 | 3/10 |
| Plané *(interne)* | 4,0/10 | traînée × 1,0 |
| Portance *(interne)* | 4,0/10 | décroche sous 9,6 m/s |

---

## Ailes de chauve-souris

`simplywings:bat` · **Rare** · item `bat_wings`

**Vibe** : aérodynamiques et nerveuses. Elles fendent l'air avec peu de traînée et virent sec, mais leur vitesse de pointe reste modeste. Elles décrochent facilement.

**Apparence** : une membrane sombre violet-gris par côté, sur trois segments fins, qui s'affine en pointe vers l'extrémité.

**En vol** :
- **Déplacement** : 2,6 battements par seconde, rapides et vifs. Elles gardent bien leur vitesse entre deux battements.
- **Stationnaire** : battement à 1,8 Hz, aussi instable que la commune.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 4,0/10 | 150 de stamina |
| Accélération | 7,0/10 | 7,6 m/s par s, soit +2,9 m/s par battement |
| Vitesse | 3,1/10 | 28 m/s |
| Maniabilité | 8,2/10 | 130 °/s |
| Stabilité | 3,0/10 | 3/10 |
| Plané *(interne)* | 8,0/10 | traînée × 0,6 |
| Portance *(interne)* | 4,8/10 | décroche sous 8,7 m/s |

---

## Ailes de corbeau

`simplywings:crow` · **Rare** · item `crow_wings`

**Vibe** : Rusé et vif. Des plumes noires mates aux reflets bleutés : le corbeau vire sec et se faufile partout.

**Apparence** : Une aile de plumes noires par côté, aux longues rémiges en pointe.

**Effets** : À chaque battement, quelques plumes tombent avec un bruissement d'ailes.

**En vol** :
- **Déplacement** : 2,4 battements par seconde, +2,5 m/s par coup.
- **Stationnaire** : 1,6 Hz, instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 4/10 | 150 de stamina |
| Accélération | 5/10 | 6 m/s par s, soit +2,5 m/s par battement |
| Vitesse | 3,5/10 | 30 m/s |
| Maniabilité | 7,8/10 | 126 °/s |
| Stabilité | 3/10 | 3/10 |
| Plané *(interne)* | 5/10 | traînée × 0,9 |
| Portance *(interne)* | 4,5/10 | décroche sous 9,1 m/s |

---

## Ailes de chouette

`simplywings:owl` · **Rare** · item `owl_wings`

**Vibe** : Le vol silencieux. De larges ailes duveteuses qui tiennent en place sans bruit et planent doucement.

**Apparence** : Une aile large et arrondie par côté, brun clair barré de bandes sombres.

**Effets** : À chaque battement, quelques plumes tombent avec un bruissement d'ailes.

**En vol** :
- **Déplacement** : 1,2 battement par seconde, +4 m/s par coup.
- **Stationnaire** : 1 Hz, peu de balancement.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 4,5/10 | 162 de stamina |
| Accélération | 3,5/10 | 4,8 m/s par s, soit +4 m/s par battement |
| Vitesse | 2,5/10 | 25 m/s |
| Maniabilité | 5/10 | 95 °/s |
| Stabilité | 7/10 | 7/10 |
| Plané *(interne)* | 7,2/10 | traînée × 0,68 |
| Portance *(interne)* | 6/10 | décroche sous 7,4 m/s |

---

## Ailes de goéland

`simplywings:gull` · **Rare** · item `gull_wings`

**Vibe** : Le voyageur des côtes. De longues ailes fines qui planent des heures sans fatigue.

**Apparence** : Une longue aile étroite par côté, blanche et grise, aux pointes noires.

**Effets** : À chaque battement, quelques plumes tombent avec un bruissement d'ailes.

**En vol** :
- **Déplacement** : 1 battement par seconde, +4,8 m/s par coup.
- **Stationnaire** : 1,4 Hz, très instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 6,5/10 | 212 de stamina |
| Accélération | 3,5/10 | 4,8 m/s par s, soit +4,8 m/s par battement |
| Vitesse | 3,5/10 | 30 m/s |
| Maniabilité | 4/10 | 84 °/s |
| Stabilité | 3/10 | 3/10 |
| Plané *(interne)* | 8/10 | traînée × 0,6 |
| Portance *(interne)* | 5,5/10 | décroche sous 8 m/s |

---

## Ailes de colibri

`simplywings:hummingbird` · **Rare** · item `hummingbird_wings`

**Vibe** : Le roi du surplace. De toutes petites ailes qui battent si vite qu'elles deviennent floues : un stationnaire précis, mais une réserve minuscule.

**Apparence** : Une toute petite aile vert irisé par côté, qui vibre en continu.

**Effets** : Aucun, des ailes simples et discrètes.

**En vol** :
- **Déplacement** : Les ailes vibrent à 30 Hz, poussée continue tant que sprint est maintenu.
- **Stationnaire** : 40 Hz, peu de balancement.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 2,5/10 | 112 de stamina |
| Accélération | 5/10 | 6 m/s par s, en continu |
| Vitesse | 2,5/10 | 25 m/s |
| Maniabilité | 7,5/10 | 122 °/s |
| Stabilité | 7,5/10 | 7,5/10 |
| Plané *(interne)* | 3/10 | traînée × 1,1 |
| Portance *(interne)* | 7/10 | décroche sous 6,3 m/s |

---

## Ailes d'aigle

`simplywings:eagle` · **Rare** · item `eagle_wings`

**Vibe** : Le rapace. De larges ailes aux doigts écartés, des coups puissants qui arrachent le joueur du sol.

**Apparence** : Une grande aile brune par côté, aux rémiges écartées comme des doigts.

**Effets** : À chaque battement, quelques plumes tombent avec un bruissement d'ailes.

**En vol** :
- **Déplacement** : 1 battement par seconde, +8 m/s par coup.
- **Stationnaire** : 1 Hz, instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 4,5/10 | 162 de stamina |
| Accélération | 7,5/10 | 8 m/s par s, soit +8 m/s par battement |
| Vitesse | 4,5/10 | 35 m/s |
| Maniabilité | 4/10 | 84 °/s |
| Stabilité | 2,5/10 | 2,5/10 |
| Plané *(interne)* | 6/10 | traînée × 0,8 |
| Portance *(interne)* | 3,5/10 | décroche sous 10,2 m/s |

---

## Ailes de martinet

`simplywings:swift` · **Rare** · item `swift_wings`

**Vibe** : Le plus rapide des oiseaux. Des ailes en forme de faux, faites pour filer, incapables de tenir en place.

**Apparence** : Une aile sombre en faux par côté, très effilée vers la pointe.

**Effets** : À chaque battement, quelques plumes tombent avec un bruissement d'ailes.

**En vol** :
- **Déplacement** : 2,8 battements par seconde, +2,4 m/s par coup.
- **Stationnaire** : 2,4 Hz, très instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 3,5/10 | 138 de stamina |
| Accélération | 6/10 | 6,8 m/s par s, soit +2,4 m/s par battement |
| Vitesse | 5,5/10 | 41 m/s |
| Maniabilité | 6/10 | 106 °/s |
| Stabilité | 2/10 | 2/10 |
| Plané *(interne)* | 6,5/10 | traînée × 0,75 |
| Portance *(interne)* | 4/10 | décroche sous 9,6 m/s |

---

## Ailes de pigeon voyageur

`simplywings:pigeon` · **Rare** · item `pigeon_wings`

**Vibe** : Gris, banal et infatigable. Le pigeon voyageur n'impressionne personne, mais il vole des heures.

**Apparence** : Une aile grise par côté, barrée de deux bandes noires.

**Effets** : À chaque battement, quelques plumes tombent avec un bruissement d'ailes.

**En vol** :
- **Déplacement** : 2,2 battements par seconde, +2,4 m/s par coup.
- **Stationnaire** : 1,8 Hz, instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 8/10 | 250 de stamina |
| Accélération | 4/10 | 5,2 m/s par s, soit +2,4 m/s par battement |
| Vitesse | 3/10 | 28 m/s |
| Maniabilité | 4/10 | 84 °/s |
| Stabilité | 3,5/10 | 3,5/10 |
| Plané *(interne)* | 4/10 | traînée × 1 |
| Portance *(interne)* | 4,5/10 | décroche sous 9,1 m/s |

---

## Ailes de paon

`simplywings:peacock` · **Rare** · item `peacock_wings`

**Vibe** : La paire vitrine. Magnifique, mais le paon vole mal : lent, lourd, il plane à peine. En revanche, il tient bien la pose.

**Apparence** : Une aile vert-bleu par côté, qui descend bas et porte des ocelles dorés.

**Effets** : À chaque battement, quelques plumes tombent avec un bruissement d'ailes.

**En vol** :
- **Déplacement** : 1,6 battement par seconde, +2,8 m/s par coup.
- **Stationnaire** : 1,2 Hz, balancement modéré.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 3/10 | 125 de stamina |
| Accélération | 3/10 | 4,4 m/s par s, soit +2,8 m/s par battement |
| Vitesse | 1,5/10 | 20 m/s |
| Maniabilité | 3/10 | 73 °/s |
| Stabilité | 6,5/10 | 6,5/10 |
| Plané *(interne)* | 2,5/10 | traînée × 1,15 |
| Portance *(interne)* | 3/10 | décroche sous 10,7 m/s |

---

## Ailes de perroquet

`simplywings:parrot` · **Rare** · item `parrot_wings`

**Vibe** : Le clin d'œil au perroquet de Minecraft. Des couleurs vives et un vol agile entre les arbres.

**Apparence** : Une aile par côté en bandes rouge, jaune et bleue.

**Effets** : À chaque battement, quelques plumes tombent avec un bruissement d'ailes.

**En vol** :
- **Déplacement** : 2,6 battements par seconde, +2,5 m/s par coup.
- **Stationnaire** : 2 Hz, instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 3,5/10 | 138 de stamina |
| Accélération | 5,5/10 | 6,4 m/s par s, soit +2,5 m/s par battement |
| Vitesse | 3/10 | 28 m/s |
| Maniabilité | 7,5/10 | 122 °/s |
| Stabilité | 4/10 | 4/10 |
| Plané *(interne)* | 4,5/10 | traînée × 0,95 |
| Portance *(interne)* | 5/10 | décroche sous 8,5 m/s |

---

## Ailes d'abeille

`simplywings:bee` · **Rare** · item `bee_wings`

**Vibe** : Le clin d'œil à l'abeille de Minecraft. Quatre petites ailes qui bourdonnent : un bon stationnaire, mais peu de vitesse.

**Apparence** : **4 petites ailes** translucides veinées de brun, toujours à plat, qui vibrent en continu.

**Effets** : Aucun, des ailes simples et discrètes.

**En vol** :
- **Déplacement** : Les ailes vibrent à 25 Hz, poussée continue tant que sprint est maintenu.
- **Stationnaire** : 30 Hz, peu de balancement.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 3/10 | 125 de stamina |
| Accélération | 4,5/10 | 5,6 m/s par s, en continu |
| Vitesse | 2,5/10 | 25 m/s |
| Maniabilité | 6/10 | 106 °/s |
| Stabilité | 6,5/10 | 6,5/10 |
| Plané *(interne)* | 2,5/10 | traînée × 1,15 |
| Portance *(interne)* | 6,5/10 | décroche sous 6,8 m/s |

---

## Ailes de papillon de nuit

`simplywings:moth` · **Rare** · item `moth_wings`

**Vibe** : Poudré et nocturne. Le papillon de nuit flotte doucement et se pose où il veut, sans jamais décrocher.

**Apparence** : **4 ailes** poudrées beige et brun, avec des ocelles sombres, toujours déployées.

**Effets** : Aucun, des ailes simples et discrètes.

**En vol** :
- **Déplacement** : 1,8 battement par seconde, +2,4 m/s par coup.
- **Stationnaire** : 1,5 Hz, balancement modéré.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 4,5/10 | 162 de stamina |
| Accélération | 3/10 | 4,4 m/s par s, soit +2,4 m/s par battement |
| Vitesse | 2,5/10 | 25 m/s |
| Maniabilité | 4,5/10 | 90 °/s |
| Stabilité | 6/10 | 6/10 |
| Plané *(interne)* | 4,5/10 | traînée × 0,95 |
| Portance *(interne)* | 7,2/10 | décroche sous 6,1 m/s |

---

## Ailes de scarabée

`simplywings:beetle` · **Rare** · item `beetle_wings`

**Vibe** : Le cousin des élytres. Deux coques vertes rigides s'ouvrent sur des ailes membraneuses qui bourdonnent : robuste et endurant, mais pataud.

**Apparence** : Deux élytres vert métallique et, dessous, deux ailes membraneuses brunes qui vibrent en continu.

**Effets** : Aucun, des ailes simples et discrètes.

**En vol** :
- **Déplacement** : Les ailes vibrent à 12 Hz, poussée continue tant que sprint est maintenu.
- **Stationnaire** : 20 Hz, peu de balancement.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 6,5/10 | 212 de stamina |
| Accélération | 6,5/10 | 7,2 m/s par s, en continu |
| Vitesse | 3/10 | 28 m/s |
| Maniabilité | 3/10 | 73 °/s |
| Stabilité | 4/10 | 4/10 |
| Plané *(interne)* | 3/10 | traînée × 1,1 |
| Portance *(interne)* | 3,5/10 | décroche sous 10,2 m/s |

---

## Ailes d'écureuil volant

`simplywings:squirrel` · **Rare** · item `squirrel_wings`

**Vibe** : Le planeur. Une membrane de fourrure tendue entre les bras : elle bat à peine, mais plane loin et se pilote au millimètre.

**Apparence** : Une membrane de fourrure grise par côté, courte et large, au bord clair.

**Effets** : Aucun, des ailes simples et discrètes.

**En vol** :
- **Déplacement** : 1,2 battement par seconde, +3,3 m/s par coup.
- **Stationnaire** : 1,2 Hz, très instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 3,5/10 | 138 de stamina |
| Accélération | 2,5/10 | 4 m/s par s, soit +3,3 m/s par battement |
| Vitesse | 3/10 | 28 m/s |
| Maniabilité | 7/10 | 117 °/s |
| Stabilité | 3/10 | 3/10 |
| Plané *(interne)* | 7,8/10 | traînée × 0,62 |
| Portance *(interne)* | 5/10 | décroche sous 8,5 m/s |

---

## Ailes de poisson volant

`simplywings:flying_fish` · **Rare** · item `flying_fish_wings`

**Vibe** : Décalé et fun. Des nageoires translucides qui frétillent et filent au ras des vagues.

**Apparence** : Une nageoire translucide bleu argenté par côté, rayée de fines arêtes.

**Effets** : Aucun, des ailes simples et discrètes.

**En vol** :
- **Déplacement** : 3 battements par seconde, +1,9 m/s par coup.
- **Stationnaire** : 2,5 Hz, très instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 3/10 | 125 de stamina |
| Accélération | 4,5/10 | 5,6 m/s par s, soit +1,9 m/s par battement |
| Vitesse | 5/10 | 38 m/s |
| Maniabilité | 4/10 | 84 °/s |
| Stabilité | 2,5/10 | 2,5/10 |
| Plané *(interne)* | 7/10 | traînée × 0,7 |
| Portance *(interne)* | 3/10 | décroche sous 10,7 m/s |

---

## Ailes de phantom

`simplywings:phantom` · **Rare** · item `phantom_wings`

**Vibe** : Le cauchemar des insomniaques. La membrane déchirée du phantom, rapide en piqué.

**Apparence** : Une membrane bleu-gris par côté, aux nervures vertes et au bord déchiré.

**Effets** : À chaque battement, un claquement de membrane et un petit souffle au bout des ailes.

**En vol** :
- **Déplacement** : 1,3 battement par seconde, +4,3 m/s par coup.
- **Stationnaire** : 1,2 Hz, instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 4/10 | 150 de stamina |
| Accélération | 4,5/10 | 5,6 m/s par s, soit +4,3 m/s par battement |
| Vitesse | 5,5/10 | 41 m/s |
| Maniabilité | 5/10 | 95 °/s |
| Stabilité | 2,5/10 | 2,5/10 |
| Plané *(interne)* | 6/10 | traînée × 0,8 |
| Portance *(interne)* | 4/10 | décroche sous 9,6 m/s |

---

## Ailes d'améthyste

`simplywings:amethyst` · **Rare** · item `amethyst_wings`

**Vibe** : Du cristal vanilla. Des éclats d'améthyste qui tintent à chaque battement et tiennent le joueur bien en place.

**Apparence** : Une aile de facettes d'améthyste par côté, dont certaines brillent faiblement dans le noir.

**Effets** : Des éclats violets flottent autour des ailes, et chaque battement tinte comme un bloc d'améthyste.

**En vol** :
- **Déplacement** : 1,4 battement par seconde, +3,4 m/s par coup.
- **Stationnaire** : 1,1 Hz, peu de balancement.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 4,5/10 | 162 de stamina |
| Accélération | 3,5/10 | 4,8 m/s par s, soit +3,4 m/s par battement |
| Vitesse | 2,5/10 | 25 m/s |
| Maniabilité | 6,5/10 | 112 °/s |
| Stabilité | 7/10 | 7/10 |
| Plané *(interne)* | 3/10 | traînée × 1,1 |
| Portance *(interne)* | 5/10 | décroche sous 8,5 m/s |

---

## Ailes d'ornithoptère

`simplywings:ornithopter` · **Rare** · item `ornithopter_wings`

**Vibe** : La machine de Léonard de Vinci. Une armature de bois et de la toile tendue : lente mais robuste, et stable.

**Apparence** : Une aile par côté, en toile écrue tendue sur des baguettes de bois.

**Effets** : Aucun, des ailes simples et discrètes.

**En vol** :
- **Déplacement** : 1,2 battement par seconde, +4 m/s par coup.
- **Stationnaire** : 1 Hz, peu de balancement.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 7/10 | 225 de stamina |
| Accélération | 3,5/10 | 4,8 m/s par s, soit +4 m/s par battement |
| Vitesse | 2,5/10 | 25 m/s |
| Maniabilité | 3,5/10 | 78 °/s |
| Stabilité | 6,5/10 | 6,5/10 |
| Plané *(interne)* | 4/10 | traînée × 1 |
| Portance *(interne)* | 5/10 | décroche sous 8,5 m/s |

---

## Ailes de cerf-volant

`simplywings:kite` · **Rare** · item `kite_wings`

**Vibe** : Papier et baguettes. Presque aucune puissance, mais il porte au moindre souffle et ne décroche presque jamais.

**Apparence** : Une voile de papier rayée rouge et blanc par côté, tendue sur des baguettes.

**Effets** : Aucun, des ailes simples et discrètes.

**En vol** :
- **Déplacement** : 0,8 battement par seconde, +5 m/s par coup.
- **Stationnaire** : 0,8 Hz, très instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 2,5/10 | 112 de stamina |
| Accélération | 2,5/10 | 4 m/s par s, soit +5 m/s par battement |
| Vitesse | 3/10 | 28 m/s |
| Maniabilité | 4,5/10 | 90 °/s |
| Stabilité | 3/10 | 3/10 |
| Plané *(interne)* | 7,8/10 | traînée × 0,62 |
| Portance *(interne)* | 7,8/10 | décroche sous 5,4 m/s |

---

## Ailes de cuivre

`simplywings:copper` · **Rare** · item `copper_wings`

**Vibe** : La mécanique du cuivre de la 1.21. Des plaques rivetées qui poussent fort, déjà tachées de vert-de-gris.

**Apparence** : Une aile de plaques de cuivre rivetées par côté, piquée de taches de vert-de-gris.

**Effets** : Des copeaux de cuivre s'échappent en vol, et chaque battement sonne comme une plaque de cuivre.

**En vol** :
- **Déplacement** : 1,6 battement par seconde, +4,8 m/s par coup.
- **Stationnaire** : 1,4 Hz, balancement modéré.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 5/10 | 175 de stamina |
| Accélération | 7,2/10 | 7,8 m/s par s, soit +4,8 m/s par battement |
| Vitesse | 3,5/10 | 30 m/s |
| Maniabilité | 4/10 | 84 °/s |
| Stabilité | 4,5/10 | 4,5/10 |
| Plané *(interne)* | 3/10 | traînée × 1,1 |
| Portance *(interne)* | 3,5/10 | décroche sous 10,2 m/s |

---

## Ailes rafistolées

`simplywings:patchwork` · **Rare** · item `patchwork_wings`

**Vibe** : Cuir, plumes et ficelle. Des ailes bricolées sans point fort ni point faible : la rare de départ.

**Apparence** : Une aile par côté, faite de pièces de cuir et de tissu cousues ensemble.

**Effets** : Aucun, des ailes simples et discrètes.

**En vol** :
- **Déplacement** : 1,8 battement par seconde, +3,1 m/s par coup.
- **Stationnaire** : 1,4 Hz, instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 4,5/10 | 162 de stamina |
| Accélération | 4,5/10 | 5,6 m/s par s, soit +3,1 m/s par battement |
| Vitesse | 3,5/10 | 30 m/s |
| Maniabilité | 4,5/10 | 90 °/s |
| Stabilité | 4/10 | 4/10 |
| Plané *(interne)* | 4,5/10 | traînée × 0,95 |
| Portance *(interne)* | 4,5/10 | décroche sous 9,1 m/s |

---

## Ailes de méganeure

`simplywings:ancient` · **Épique** · item `ancient_wings` · modèle libellule

**Vibe** : la méganeure, libellule géante du Carbonifère, il y a 300 millions d'années, avec 70 cm d'envergure. Ses ailes sont restées prises dans l'ambre. C'est un insecte capable de faire du surplace, l'outil du bâtisseur. Très stable et très maniable en stationnaire, mais médiocre en vol de déplacement : lente, elle accélère et freine mal. En échange, elle ne décroche presque jamais.

**Apparence** : **4 ailes**, deux paires avant et arrière, chacune d'un seul bloc. Membranes translucides couleur d'ambre, veinées de brun fossile et tachetées. Toujours étalées à plat, en V vers l'arrière au repos.

**Effets** : quelques grains d'ambre tombent des ailes.

**En vol** :
- **Déplacement** : les ailes vibrent à 25 Hz. La poussée est continue tant que sprint est maintenu, avec une accélération presque linéaire.
- **Stationnaire** : bourdonnement sans balancement, seule une légère dérive subsiste.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 6,0/10 | 200 de stamina |
| Accélération | 3,5/10 | 4,8 m/s par s, en continu |
| Vitesse | 2,3/10 | 24 m/s |
| Maniabilité | 9,1/10 | 140 °/s |
| Stabilité | 4,0/10 | 4/10, et pratiquement sans balancement grâce à la vibration |
| Plané *(interne)* | 4,0/10 | traînée × 1,0 |
| Portance *(interne)* | 8,6/10 | décroche sous 4,5 m/s |

---

## Ailes de ptérosaure

`simplywings:ptero` · **Épique** · item `ptero_wings`

**Vibe** : le planeur préhistorique. Les ptérosaures traversaient des océans en planant sur des ailes de peau tendues par un seul doigt immense. Elles battent peu, mais chaque coup est ample. Elles ont du fond et planent très loin, mais elles sont lourdes à manœuvrer.

**Apparence** : une membrane de cuir ocre par côté. L'avant-bras est court, et le doigt qui porte la pointe est très long. La membrane s'affine jusqu'à la pointe, avec des fibres plus sombres.

**Effets** : à chaque battement, un claquement de membrane et un petit souffle au bout des ailes.

**En vol** :
- **Déplacement** : 0,9 battement par seconde, +7,1 m/s par coup.
- **Stationnaire** : 1,1 Hz, instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 7,5/10 | 238 de stamina |
| Accélération | 5,5/10 | 6,4 m/s par s, soit +7,1 m/s par battement |
| Vitesse | 4,5/10 | 35 m/s |
| Maniabilité | 4,0/10 | 84 °/s |
| Stabilité | 3,0/10 | 3/10 |
| Plané *(interne)* | 8,5/10 | traînée × 0,55 |
| Portance *(interne)* | 5,5/10 | décroche sous 8 m/s |

---

## Ailes de flamme

`simplywings:fire` · **Épique** · item `fire_wings` · élément feu

**Vibe** : un esprit du feu. Les ailes sont faites de flammes vives : elles brûlent fort et vite, avec des coups secs et explosifs. Elles s'épuisent vite et tiennent mal en place.

**Apparence** : une aile de feu par côté, en trois segments, lumineuse dans le noir. Elle passe du jaune près du dos à l'orange, puis au rouge vers le bord, qui se découpe en langues de flamme.

**Effets** :
- **En continu** : des flammes et des étincelles s'échappent des ailes, avec une traînée de fumée au-dessus de 20 m/s.
- **À chaque battement** : une gerbe de flammes projetée vers l'arrière, des étincelles et le souffle d'une boule de feu.

**En vol** :
- **Déplacement** : 1,4 battement par seconde, +6,3 m/s par coup.
- **Stationnaire** : 1,6 Hz, très instable, avec un léger scintillement des flammes.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 3,5/10 | 138 de stamina |
| Accélération | 8,5/10 | 8,8 m/s par s, soit +6,3 m/s par battement |
| Vitesse | 6,0/10 | 43 m/s |
| Maniabilité | 5,0/10 | 95 °/s |
| Stabilité | 2,0/10 | 2/10 |
| Plané *(interne)* | 3,5/10 | traînée × 1,05 |
| Portance *(interne)* | 3,5/10 | décroche sous 10,2 m/s |

---

## Ailes de la marée

`simplywings:water` · **Épique** · item `water_wings` · élément eau

**Vibe** : un esprit de l'eau. Le vol est fluide, comme une nage dans l'air : les ailes ondulent, glissent et virent sans effort. Elles sont plutôt stables et gardent bien leur élan.

**Apparence** : une aile d'eau translucide par côté, bleue avec des reflets clairs qui ondulent et un liseré d'écume sur le bord. Elle respire lentement comme une vague.

**Effets** :
- **En continu** : des gouttes tombent des ailes. En vol, de l'écume s'en détache, et des éclaboussures partent du bout des ailes au-dessus de 18 m/s.
- **À chaque battement** : des éclaboussures, des gouttes qui tombent et le bruit d'une rame dans l'eau.

**En vol** :
- **Déplacement** : 1 battement par seconde, +5,6 m/s par coup.
- **Stationnaire** : 0,8 Hz, balancement modéré.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 6,0/10 | 200 de stamina |
| Accélération | 4,5/10 | 5,6 m/s par s, soit +5,6 m/s par battement |
| Vitesse | 4,5/10 | 35 m/s |
| Maniabilité | 8,0/10 | 128 °/s |
| Stabilité | 5,5/10 | 5,5/10 |
| Plané *(interne)* | 8,0/10 | traînée × 0,6 |
| Portance *(interne)* | 5,0/10 | décroche sous 8,5 m/s |

---

## Ailes de pierre

`simplywings:earth` · **Épique** · item `earth_wings` · élément terre

**Vibe** : un esprit de la terre. Des ailes de roche lourdes et lentes qui ne s'épuisent presque jamais et tiennent très bien en place. Elles volent bas et lentement, virent mal, et ne planent pas : la pierre veut retourner au sol.

**Apparence** : une aile de pierre par côté, en trois segments épais : roche grise fissurée, mousse près du dos et éclats d'améthyste qui brillent dans le noir.

**Effets** :
- **En continu** : de la poussière de terre tombe des ailes. En vol, des éclats de pierre s'en détachent.
- **À chaque battement** : des éclats de pierre et de mousse, un nuage de poussière et un craquement de roche.

**En vol** :
- **Déplacement** : 0,8 battement par seconde, lent et lourd, +6,5 m/s par coup.
- **Stationnaire** : 0,7 Hz, très peu de balancement.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 8,5/10 | 263 de stamina |
| Accélération | 4,0/10 | 5,2 m/s par s, soit +6,5 m/s par battement |
| Vitesse | 2,5/10 | 25 m/s |
| Maniabilité | 3,0/10 | 73 °/s |
| Stabilité | 7,5/10 | 7,5/10 |
| Plané *(interne)* | 2,5/10 | traînée × 1,15 |
| Portance *(interne)* | 4,5/10 | décroche sous 9 m/s |

---

## Ailes du vent

`simplywings:air` · **Épique** · item `air_wings` · élément air

**Vibe** : un esprit du vent. Des ailes presque invisibles, légères et rapides, qui portent loin et ne décrochent presque pas. Comme le vent, elles ne tiennent pas en place.

**Apparence** : **4 ailes** de vent, deux par côté : des voiles blanches très transparentes, striées comme des courants d'air, qui s'effilochent au bord.

**Effets** :
- **En continu** : des filets d'air blanc partent vers l'arrière. Au-dessus de 20 m/s, des traînées partent du bout des ailes.
- **À chaque battement** : deux petites rafales au bout des ailes, des filets d'air et un souffle de vent.

**En vol** :
- **Déplacement** : 1,6 battement par seconde, +4,3 m/s par coup.
- **Stationnaire** : 1,4 Hz, très instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 4,5/10 | 163 de stamina |
| Accélération | 6,0/10 | 6,8 m/s par s, soit +4,3 m/s par battement |
| Vitesse | 8,2/10 | 55 m/s |
| Maniabilité | 6,5/10 | 112 °/s |
| Stabilité | 2,5/10 | 2,5/10 |
| Plané *(interne)* | 7,5/10 | traînée × 0,65 |
| Portance *(interne)* | 7,5/10 | décroche sous 5,8 m/s |

---

## Ailes de l'orage

`simplywings:storm` · **Épique** · item `storm_wings` · esprit de l'orage

**Vibe** : un esprit de la tempête. Des ailes d'éclairs qui crépitent, rapides et nerveuses : elles filent et virent sec, mais l'énergie s'épuise vite et elles ne tiennent pas en place.

**Apparence** : une aile d'énergie électrique par côté, lumineuse dans le noir, du bleu clair près du dos au violet orageux au bord, traversée d'éclairs blancs. Le bord s'effiloche.

**Effets** :
- **En continu** : des étincelles électriques crépitent sur les ailes, et au-dessus de 20 m/s elles partent du bout des ailes.
- **À chaque battement** : une décharge d'étincelles vers l'arrière et le claquement d'un éclair.

**En vol** :
- **Déplacement** : 1,8 battement par seconde, +4,4 m/s par coup.
- **Stationnaire** : 2 Hz, avec un grésillement des ailes, très instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 4,0/10 | 150 de stamina |
| Accélération | 7,5/10 | 8 m/s par s, soit +4,4 m/s par battement |
| Vitesse | 8,5/10 | 56 m/s |
| Maniabilité | 8,0/10 | 128 °/s |
| Stabilité | 2,0/10 | 2/10 |
| Plané *(interne)* | 5,0/10 | traînée × 0,9 |
| Portance *(interne)* | 4,5/10 | décroche sous 9 m/s |

---

## Ailes d'aurore

`simplywings:aurora` · **Épique** · item `aurora_wings` · esprit du ciel polaire

**Vibe** : une aurore boréale qui a pris forme. Des voiles de lumière verte et violette, lents et paisibles, qui portent le joueur comme un rideau de ciel. Elles planent très loin et tiennent bien en place, mais elles accélèrent doucement.

**Apparence** : **4 voiles** de lumière, deux par côté, lumineux dans le noir, en rideaux verticaux qui passent du vert au violet. Elles restent déployées et respirent lentement.

**Effets** :
- **En continu** : des poussières vertes et violettes s'élèvent doucement des voiles.
- **À chaque battement** : une vague de lumière colorée et une résonance cristalline.

**En vol** :
- **Déplacement** : 0,7 battement par seconde, +7,4 m/s par coup.
- **Stationnaire** : 0,6 Hz, presque immobile.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 6,5/10 | 213 de stamina |
| Accélération | 4,0/10 | 5,2 m/s par s, soit +7,4 m/s par battement |
| Vitesse | 5,0/10 | 38 m/s |
| Maniabilité | 5,5/10 | 101 °/s |
| Stabilité | 8,0/10 | 8/10 |
| Plané *(interne)* | 8,5/10 | traînée × 0,55 |
| Portance *(interne)* | 7,0/10 | décroche sous 6,3 m/s |

---

## Ailes d'ombre

`simplywings:shadow` · **Épique** · item `shadow_wings` · esprit de l'ombre

**Vibe** : un esprit des ténèbres. Des ailes de fumée noire qui se défont sur les bords, rapides à réagir : départs vifs et virages serrés. Elles sont discrètes mais peu stables.

**Apparence** : une aile de fumée sombre par côté, translucide, violet-noir avec des remous. Quelques yeux violets brillent dans la fumée, et le bord se défait en volutes.

**Effets** :
- **En continu** : de la fumée noire s'échappe des ailes, et de l'encre en vol.
- **À chaque battement** : un nuage de fumée noire vers l'arrière et un son de téléportation étouffé.

**En vol** :
- **Déplacement** : 1,2 battement par seconde, +7 m/s par coup.
- **Stationnaire** : 1,3 Hz, instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 5,0/10 | 175 de stamina |
| Accélération | 8,0/10 | 8,4 m/s par s, soit +7 m/s par battement |
| Vitesse | 6,5/10 | 46 m/s |
| Maniabilité | 8,5/10 | 134 °/s |
| Stabilité | 3,0/10 | 3/10 |
| Plané *(interne)* | 4,5/10 | traînée × 0,95 |
| Portance *(interne)* | 4,0/10 | décroche sous 9,6 m/s |

---

## Ailes de nébuleuse

`simplywings:nebula` · **Épique** · item `nebula_wings` · esprit des étoiles

**Vibe** : un morceau de ciel nocturne. Des ailes de nébuleuse où scintillent des étoiles : elles ont un fond immense et emportent loin et vite, comme un voyage entre les étoiles. Elles sont lourdes à manœuvrer.

**Apparence** : une aile par côté, translucide, en nuages bleu nuit et pourpres. Des étoiles blanches brillent dans le noir.

**Effets** :
- **En continu** : des étoiles scintillent autour des ailes. Au-dessus de 25 m/s, des traînées lumineuses partent du bout des ailes.
- **À chaque battement** : une pluie d'étoiles et un tintement d'améthyste.

**En vol** :
- **Déplacement** : 0,8 battement par seconde, +7,5 m/s par coup.
- **Stationnaire** : 0,9 Hz, balancement modéré.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 8,0/10 | 250 de stamina |
| Accélération | 5,0/10 | 6 m/s par s, soit +7,5 m/s par battement |
| Vitesse | 8,0/10 | 54 m/s |
| Maniabilité | 4,5/10 | 90 °/s |
| Stabilité | 4,0/10 | 4/10 |
| Plané *(interne)* | 6,0/10 | traînée × 0,8 |
| Portance *(interne)* | 5,0/10 | décroche sous 8,5 m/s |

---

## Ailes de l'esprit sylvestre

`simplywings:forest` · **Épique** · item `forest_wings` · esprit de la forêt

**Vibe** : un esprit de la forêt, comme un kodama. Des ailes de feuilles et de fleurs, calmes et patientes : elles tiennent bien en place et durent longtemps, mais elles sont lentes.

**Apparence** : **4 ailes** de feuilles, deux par côté, vertes et tachetées de fleurs roses, sur une armature de branches. Le bord est dentelé comme un feuillage.

**Effets** :
- **En continu** : des pétales tombent des ailes, et des spores flottent autour.
- **À chaque battement** : une volée de pétales et un bruissement de feuilles.

**En vol** :
- **Déplacement** : 1 battement par seconde, +5,6 m/s par coup.
- **Stationnaire** : 1 Hz, peu de balancement.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 7,5/10 | 238 de stamina |
| Accélération | 4,5/10 | 5,6 m/s par s, soit +5,6 m/s par battement |
| Vitesse | 3,5/10 | 30 m/s |
| Maniabilité | 6,0/10 | 106 °/s |
| Stabilité | 7,0/10 | 7/10 |
| Plané *(interne)* | 5,5/10 | traînée × 0,85 |
| Portance *(interne)* | 6,5/10 | décroche sous 6,9 m/s |

---

## Ailes d'ange

`simplywings:angel` · **Légendaire** · item `angel_wings` · **Endurance maxée**

**Vibe** : l'endurance et la grâce. Un battement toutes les 2 secondes : longue montée majestueuse, puis poussée ample. Elles ne s'épuisent presque jamais et planent très loin grâce à leur envergure, mais elles sont très instables en stationnaire.

**Apparence** : une grande aile blanc crème par côté, en trois segments. Les plumes s'allongent vers la pointe et descendent jusqu'aux pieds au repos.

**Effets** :
- **Rendu** : les pointes des plumes brillent doucement (passe lumineuse).
- **En continu** : des plumes se détachent et tombent pendant le vol, et des étincelles blanches flottent autour des ailes.
- **À chaque battement** : une volée de plumes, des souffles d'air au bout des ailes et un tintement cristallin.

**En vol** :
- **Déplacement** : +15 m/s par battement.
- **Stationnaire** : un grand battement toutes les 1,7 s, avec un balancement d'environ 1 bloc.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | **10/10** | 300 de stamina |
| Accélération | 7,0/10 | 7,6 m/s par s, soit +15 m/s par battement |
| Vitesse | 7,5/10 | 51 m/s |
| Maniabilité | 4,1/10 | 85 °/s |
| Stabilité | 1,0/10 | 1/10 |
| Plané *(interne)* | 8,5/10 | traînée × 0,55 |
| Portance *(interne)* | 3,0/10 | décroche sous 10,7 m/s |

---

## Ailes de démon

`simplywings:demon` · **Légendaire** · item `demon_wings` · **Accélération maxée**

**Vibe** : la puissance brute et la violence. Une membrane de cuir qui se lève lentement puis frappe d'un coup sec et brutal, plus vite et plus fort que tout le reste. Elles sont instables en stationnaire et ne planent pas très bien.

**Apparence** : une grande membrane par côté, en trois segments épais. Rouge sombre veinée de noir, avec une lueur plus vive sur le bord de fuite.

**Effets** :
- **Rendu** : les veines et le bord de fuite rougeoient comme de la braise (passe lumineuse).
- **En continu** : des braises flottent autour de la membrane, avec des flammèches et de la fumée en vol.
- **À chaque battement** : une explosion de flammes projetée vers l'arrière, de la fumée épaisse, une goutte de lave et le souffle d'un blaze.

**En vol** :
- **Déplacement** : 0,7 battement par seconde. Le coup arrive très tard dans le cycle et très vite, pour +14 m/s.
- **Saut ailé** : environ 3 blocs.
- **Stationnaire** : 1 Hz, très instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 5,5/10 | 188 de stamina |
| Accélération | **10/10** | 10 m/s par s, soit +14 m/s par battement |
| Vitesse | 8,5/10 | 56 m/s |
| Maniabilité | 5,0/10 | 95 °/s |
| Stabilité | 1,5/10 | 1,5/10 |
| Plané *(interne)* | 4,0/10 | traînée × 1,0 |
| Portance *(interne)* | 2,5/10 | décroche sous 11,3 m/s |

---

## Ailes de Psyché

`simplywings:psyche` · **Légendaire** · item `psyche_wings` · **Stabilité maxée**

**Vibe** : la stabilité absolue par la magie. Dans la mythologie grecque, Psyché est la déesse de l'âme, représentée avec des ailes de papillon. Ici, ses ailes ne sont pas de chair : ce sont des flammes d'âme éthérées, bleu-cyan et lumineuses, qui dessinent quatre ailes de papillon. Elles respirent lentement, et l'énergie qui en émane tient le joueur en apesanteur, parfaitement immobile.

**Apparence** : 4 ailes de papillon, deux par côté, faites de flammes translucides cyan et nacrées qui ondulent sur les bords. Elles brillent dans le noir (rendu émissif) et restent déployées même au repos.

**Effets** :
- **En continu** : des flammes d'âme s'élèvent des ailes, et des âmes s'en échappent parfois.
- **À chaque battement** : une gerbe de flammes d'âme, avec un son d'âme qui s'échappe.

**En vol** :
- **Déplacement** : 0,6 battement par seconde, lent et ample.
- **Stationnaire** : respiration très lente (0,5 Hz), aucun balancement, 20 m/s de placement.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 6,5/10 | 213 de stamina |
| Accélération | 5,0/10 | 6 m/s par s, soit +10 m/s par battement |
| Vitesse | 5,0/10 | 38 m/s |
| Maniabilité | 6,0/10 | 106 °/s |
| Stabilité | **10/10** | 10/10 |
| Plané *(interne)* | 6,0/10 | traînée × 0,8 |
| Portance *(interne)* | 7,5/10 | décroche sous 5,8 m/s |

---

## Ailes de séraphin

`simplywings:seraph` · **Légendaire** · item `seraph_wings` · **Vitesse maxée**

**Vibe** : la vitesse céleste. Six ailes qui battent ensemble comme une seule et emportent le joueur plus vite que tout. Elles restent assez stables pour se poser avec précision.

**Apparence** : **6 ailes d'ange dorées**, trois par côté, en éventail : une haute, une centrale, une basse, qui battent avec un léger décalage.

**Effets** :
- **Rendu** : les plumes et les os dorés rayonnent (passe lumineuse).
- **En continu** : une poussière d'or flotte autour des 6 ailes. Au-dessus de 25 m/s, deux traînées lumineuses partent du bout des ailes.
- **À chaque battement** : un anneau de lumière se propage vers l'arrière, avec une pluie d'or et le son d'un beacon.

**En vol** :
- **Déplacement** : 0,9 battement par seconde, avec des coups amples (+7,6 m/s).
- **Stationnaire** : 0,9 Hz, balancement modéré.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 7,0/10 | 225 de stamina |
| Accélération | 6,0/10 | 6,8 m/s par s, soit +7,6 m/s par battement |
| Vitesse | **10/10** | 64 m/s |
| Maniabilité | 4,5/10 | 90 °/s |
| Stabilité | 4,0/10 | 4/10 |
| Plané *(interne)* | 6,0/10 | traînée × 0,8 |
| Portance *(interne)* | 4,0/10 | décroche sous 9,6 m/s |

---

## Ailes de Dédale

`simplywings:daedalus` · **Légendaire** · item `daedalus_wings` · **Maniabilité maxée**

**Vibe** : les ailes de l'inventeur. Dans la mythologie grecque, Dédale a fabriqué des ailes pour s'évader du labyrinthe avec son fils Icare. Ici, c'est une machine : une armature de laiton, des panneaux d'acier rivetés et deux réacteurs dans le dos. Rien ne bat, tout pousse. Les réacteurs vectorisés changent de cap à l'instant : c'est la paire la plus maniable.

**Apparence** : une aile mécanique par côté, en trois segments épais, dont les panneaux d'acier rivetés s'affinent vers la pointe.

**Effets** : les réacteurs crachent des flammes et de la fumée, vers le bas en stationnaire et vers l'arrière pendant la poussée.

**En vol** :
- **Déplacement** : poussée continue tant que sprint est maintenu. Les ailes ne font que vibrer légèrement.
- **Stationnaire** : vibration du moteur, léger balancement.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 5,5/10 | 188 de stamina |
| Accélération | 7,5/10 | 8 m/s par s, en continu |
| Vitesse | 6,0/10 | 43 m/s |
| Maniabilité | **10/10** | 150 °/s |
| Stabilité | 5,0/10 | 5/10 |
| Plané *(interne)* | 4,5/10 | traînée × 0,95 |
| Portance *(interne)* | 6,5/10 | décroche sous 6,9 m/s |

---

## Les mythiques : Night et Light

Clin d'œil à *Dragons* (How to Train Your Dragon) : les ailes de la Furie Nocturne (Krokmou, « Toothless ») et de la Furie Éclair. Ce sont les ailes « dieu ».

- **Rareté Mythique**, au-dessus des légendaires. Dans le menu, tant qu'elles ne sont pas débloquées, elles s'affichent en **« ??? »**, sans nom, sans stats et sans aperçu.
- **Stats** : 10/10 partout, plané et portance internes compris (aérodynamique maximale, en gardant tout le freinage). Elles ont en plus un **bonus de +2 en vitesse au-delà du plafond** (score 120) : 74 m/s en croisière, plus que le séraphin (64 m/s).
  - Vérifié : ça reste sous la vitesse de piqué. Ailes ouvertes, un piqué vertical plafonne vers 95 m/s, et en repli faucon il atteint 110 m/s (`terminalVelocity`). Le piqué reste donc plus rapide que le vol battu.
- **Récompense de la paire** : posséder **les deux** donne la **stamina infinie**, quelle que soit la paire équipée. Elles ne donnent pas de bonus de collection.
- **Corps** : en plus des ailes (une grande paire et une petite paire arrière), une **queue** de 6 segments part du bas du dos. Elle traîne par terre debout, ondule en marchant, pend et se balance en stationnaire, et suit le vol en déplacement avec du retard, comme une vraie queue. Elle porte deux paires d'ailerons ; chez Night, l'aileron gauche du bout est rouge, avec une marque blanche (la prothèse d'Harold).
- **Modèle et textures** : volontairement simples (prototype), à affiner par un modeleur.

### Easter egg 1 : le cri du piqué
Quand le repli faucon s'active en piqué (sprint maintenu au-delà de la vitesse max), le **cri strident** de la Furie retentit et suit le joueur, comme dans le premier film. Les joueurs autour l'entendent aussi.

### Easter egg 2 : la charge et la disparition
En vol (déplacement ou stationnaire), **maintenir le frein 2 secondes** remplit la barre de charge, la même que celle de l'envol. Le joueur reste en l'air pendant la charge. Ensuite, **appuyer sur sprint dans la demi-seconde** déclenche le dash : le joueur est propulsé à la vitesse maximale du mod (110 m/s) et devient **invisible** (15 s, config serveur `[fury] invisibilityTicks`, ailes et queue comprises). Cooldown de 5 s.
- **Night** : pendant la charge, des éclairs de Minecraft tombent **autour** du joueur, purement visuels (ni feu ni dégâts), et des étincelles électriques crépitent **sur** lui. Au dash : un bruit sourd, une légère fumée, et il disparaît, sans tir.
- **Light** : pendant la charge, un bruit de charge et du plasma bleu-blanc qui converge vers la gueule. Au dash : un tir de plasma part de la gueule, explose devant, et le joueur traverse l'explosion pour disparaître.

### Sons
Les sons sont des événements du mod (`assets/simplywings/sounds.json`) : `night_fury.dive`, `night_fury.dash`, `light_fury.dive`, `light_fury.charge`, `light_fury.blast`. Pour l'instant, ils pointent vers des sons vanilla de remplacement. Pour mettre les vrais sons, il suffit de déposer les `.ogg` dans `assets/simplywings/sounds/` et de remplacer l'entrée correspondante par `{"name": "simplywings:<fichier>"}`. Les sons des films sont protégés par le droit d'auteur : pour une version publique, il faut une recréation libre de droits.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 10/10 | 300 de stamina (infinie avec les deux paires) |
| Accélération | 10/10 | 10 m/s par s, soit +8,3 m/s par battement |
| Vitesse | **12/10** | 74 m/s |
| Maniabilité | 10/10 | 150 °/s |
| Stabilité | 10/10 | 10/10 |
| Plané *(interne)* | 10/10 | traînée × 0,4 |
| Portance *(interne)* | 10/10 | décroche sous 3 m/s |

---

## Paramètres d'identité

Ces paramètres ne dépendent pas de la collection : ils définissent le caractère de chaque paire.

| Paire | Battements/s (déplacement) | Stationnaire : cadence | Stationnaire : vitesse | Ailes par côté |
| --- | --- | --- | --- | --- |
| Commune | 2,0 | 1,2 Hz | 8 m/s | 1 |
| Chauve-souris | 2,6 | 1,8 Hz | 11 m/s | 1 |
| Corbeau | 2,4 | 1,6 Hz | 10 m/s | 1 |
| Chouette | 1,2 | 1 Hz | 8 m/s | 1 |
| Goéland | 1 | 1,4 Hz | 9 m/s | 1 |
| Colibri | 30, continu | 40 Hz | 12 m/s | 1 |
| Aigle | 1 | 1 Hz | 10 m/s | 1 |
| Martinet | 2,8 | 2,4 Hz | 11 m/s | 1 |
| Pigeon voyageur | 2,2 | 1,8 Hz | 8 m/s | 1 |
| Paon | 1,6 | 1,2 Hz | 7 m/s | 1 |
| Perroquet | 2,6 | 2 Hz | 10 m/s | 1 |
| Abeille | 25, continu | 30 Hz | 10 m/s | 2 |
| Papillon de nuit | 1,8 | 1,5 Hz | 8 m/s | 2 |
| Scarabée | 12, continu | 20 Hz | 9 m/s | 2 |
| Écureuil volant | 1,2 | 1,2 Hz | 8 m/s | 1 |
| Poisson volant | 3 | 2,5 Hz | 9 m/s | 1 |
| Phantom | 1,3 | 1,2 Hz | 11 m/s | 1 |
| Améthyste | 1,4 | 1,1 Hz | 9 m/s | 1 |
| Ornithoptère | 1,2 | 1 Hz | 8 m/s | 1 |
| Cerf-volant | 0,8 | 0,8 Hz | 7 m/s | 1 |
| Cuivre | 1,6 | 1,4 Hz | 9 m/s | 1 |
| Rafistolées | 1,8 | 1,4 Hz | 9 m/s | 1 |
| Méganeure | 20, continu | 25 Hz | 14 m/s | 2 |
| Ptérosaure | 0,9 | 1,1 Hz | 10 m/s | 1 |
| Flamme | 1,4 | 1,6 Hz | 14 m/s | 1 |
| Marée | 1,0 | 0,8 Hz | 13 m/s | 1 |
| Pierre | 0,8 | 0,7 Hz | 10 m/s | 1 |
| Vent | 1,6 | 1,4 Hz | 16 m/s | 2 |
| Orage | 1,8 | 2,0 Hz | 16 m/s | 1 |
| Aurore | 0,7 | 0,6 Hz | 14 m/s | 2 |
| Ombre | 1,2 | 1,3 Hz | 15 m/s | 1 |
| Nébuleuse | 0,8 | 0,9 Hz | 16 m/s | 1 |
| Esprit sylvestre | 1,0 | 1,0 Hz | 12 m/s | 2 |
| Ange | 0,5 | 0,6 Hz | 19 m/s | 1 |
| Démon | 0,7 | 1,0 Hz | 18 m/s | 1 |
| Séraphin | 0,9 | 0,9 Hz | 20 m/s | 3 |
| Psyché | 0,6 | 0,5 Hz (respiration) | 20 m/s | 2 |
| Dédale | 20, continu (réacteurs) | 30 Hz (vibration) | 18 m/s | 1 |
| Night | 1,2 | 1,2 Hz | 24 m/s | 2 + queue |
| Light | 1,2 | 1,2 Hz | 24 m/s | 2 + queue |

## Instabilité du stationnaire

L'instabilité dépend uniquement de la stat **Stabilité** : 10/10 = parfaitement immobile, et en dessous, le joueur bouge d'autant plus que la note est basse.
- Ailes qui battent (moins de 8 battements par seconde) : balancement rythmé, calé sur les battements, plus une petite dérive à chaque battement.
- Ailes qui vibrent en continu (méganeure, colibri, abeille, scarabée, Dédale) : remous aléatoires de la même ampleur, qui changent de direction environ toutes les 0,7 s.

Les mots « instable », « peu de balancement », etc. dans les fiches décrivent la sensation ; c'est la note de stabilité qui fait foi.

## Effets et première personne

- Les particules sont complètes seulement en vol de déplacement. En stationnaire, elles tombent à 25 %, et au sol ou pendant les sauts ailés à 10 %. Les sons baissent aussi.
- En vue à la première personne, le joueur ne voit jamais ses propres particules : celles qui apparaîtraient devant la caméra, ou qui partiraient vers elle, ne sont pas créées. Les autres joueurs les voient normalement.
