# Les ailes de Simply Wings

Catalogue des paires d'ailes : identité, apparence, ressenti en vol et stats. Les chiffres viennent des JSON (`src/main/resources/data/simplywings/simplywings/wing/`) et de la config serveur `[stats]`. Les règles d'équilibrage sont dans `BALANCE.md`.

Dernière mise à jour : 20 octobre 2026.

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
| Ailes anciennes | Épique | 6,0 | 3,5 | 2,3 | 9,1 | 4,0 | Une libellule faite pour construire |
| Ailes d'ange | Légendaire | **10** | 7,0 | 7,5 | 4,1 | 1,0 | L'endurance, un vol majestueux |
| Ailes de démon | Légendaire | 5,5 | **10** | 8,5 | 5,0 | 1,5 | La puissance brute, des coups violents |
| Ailes de séraphin | Légendaire | 7,0 | 6,0 | **10** | 4,5 | 4,0 | La vitesse, six ailes dorées |
| Ailes de sylphe | Légendaire | 5,0 | 7,5 | 6,0 | **10** | 4,5 | Les virages instantanés d'un esprit de l'air |
| Ailes de faucon pèlerin | Légendaire | 5,5 | 7,0 | **10** | 3,0 | 1,5 | *Rôle à redéfinir : vitesse, comme le séraphin* |
| Ailes d'albatros | Légendaire | **10** | 3,5 | 7,0 | 3,5 | 2,0 | *Rôle à redéfinir : endurance, comme l'ange* |

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

## Ailes anciennes

`simplywings:ancient` · **Épique** · item `ancient_wings` · modèle libellule

**Vibe** : un insecte capable de faire du surplace, l'outil du bâtisseur. Très stable et très maniable en stationnaire, mais médiocre en vol de déplacement : lente, elle accélère et freine mal. En échange, elle ne décroche presque jamais.

**Apparence** : **4 ailes**, deux paires avant et arrière, chacune d'un seul bloc. Membranes translucides irisées, veinées de vert sombre. Toujours étalées à plat, en V vers l'arrière au repos.

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

## Ailes d'ange

`simplywings:angel` · **Légendaire** · item `angel_wings` · **Endurance maxée**

**Vibe** : l'endurance et la grâce. Un battement toutes les 2 secondes : longue montée majestueuse, puis poussée ample. Elles ne s'épuisent presque jamais et planent très loin grâce à leur envergure, mais elles sont très instables en stationnaire.

**Apparence** : une grande aile blanc crème par côté, en trois segments. Les plumes s'allongent vers la pointe et descendent jusqu'aux pieds au repos.

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

## Ailes de faucon pèlerin

`simplywings:falcon` · **Légendaire** · item `falcon_wings` · **Vitesse maxée** · *rôle à redéfinir (doublon du séraphin)*

**Vibe** : la vitesse pure. Les ailes les plus rapides en croisière, faites pour le piqué en repli faucon. Elles virent mal et décrochent tôt : il faut garder de l'élan.

**Apparence** : une aile pointue par côté, brun-crème rayée de bandes sombres. Les plumes raccourcissent vers une pointe effilée.

**En vol** :
- **Déplacement** : 1,6 battement par seconde, avec un coup bref et nerveux.
- **Stationnaire** : 1,4 Hz, très instable.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 5,5/10 | 188 de stamina |
| Accélération | 7,0/10 | 7,6 m/s par s, soit +4,8 m/s par battement |
| Vitesse | **10/10** | 64 m/s |
| Maniabilité | 3,0/10 | 73 °/s |
| Stabilité | 1,5/10 | 1,5/10 |
| Plané *(interne)* | 6,0/10 | traînée × 0,8 |
| Portance *(interne)* | 1,0/10 | décroche sous 12,9 m/s |

---

## Ailes d'albatros

`simplywings:albatross` · **Légendaire** · item `albatross_wings` · **Endurance maxée** · *rôle à redéfinir (doublon de l'ange)*

**Vibe** : l'endurance. Des ailes interminables pour traverser le monde sans se poser. Elles accélèrent lentement mais ne s'épuisent jamais et planent très loin.

**Apparence** : une aile très longue et très fine par côté, la plus grande envergure du mod. Blanche, avec des pointes noires.

**En vol** :
- **Déplacement** : 0,8 battement par seconde, avec des coups amples et économes.
- **Stationnaire** : 0,9 Hz.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | **10/10** | 300 de stamina |
| Accélération | 3,5/10 | 4,8 m/s par s, soit +6 m/s par battement |
| Vitesse | 7,0/10 | 48 m/s |
| Maniabilité | 3,5/10 | 79 °/s |
| Stabilité | 2,0/10 | 2/10 |
| Plané *(interne)* | 8,5/10 | traînée × 0,55 |
| Portance *(interne)* | 4,0/10 | décroche sous 9,6 m/s |

---

## Ailes de sylphe

`simplywings:sylph` · **Légendaire** · item `sylph_wings` · **Maniabilité maxée**

**Vibe** : un sylphe, l'esprit élémentaire de l'air des légendes européennes, insaisissable et virevoltant. Il change de direction à l'instant, slalome entre les arbres et les tours, et semble danser dans le vent.

**Apparence** : **3 ailes féeriques par côté**, translucides et irisées, cyan pâle et nacré. Elles battent en vague, l'une après l'autre.

**En vol** :
- **Déplacement** : 6 battements par seconde, rapides et légers.
- **Stationnaire** : 6 Hz, avec un balancement très réduit.

| Stat | Note | Valeur |
| --- | --- | --- |
| Endurance | 5,0/10 | 175 de stamina |
| Accélération | 7,5/10 | 8 m/s par s, soit +1,3 m/s par battement |
| Vitesse | 6,0/10 | 43 m/s |
| Maniabilité | **10/10** | 150 °/s |
| Stabilité | 4,5/10 | 4,5/10 |
| Plané *(interne)* | 5,5/10 | traînée × 0,85 |
| Portance *(interne)* | 6,0/10 | décroche sous 7,4 m/s |

---

## Ailes de séraphin

`simplywings:seraph` · **Légendaire** · item `seraph_wings` · **Vitesse maxée**

**Vibe** : la vitesse céleste. Six ailes qui battent ensemble comme une seule et emportent le joueur plus vite que tout. Elles restent assez stables pour se poser avec précision.

**Apparence** : **6 ailes d'ange dorées**, trois par côté, en éventail : une haute, une centrale, une basse, qui battent avec un léger décalage.

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

## Paramètres d'identité

Ces paramètres ne dépendent pas de la collection : ils définissent le caractère de chaque paire.

| Paire | Battements/s (déplacement) | Stationnaire : cadence | Stationnaire : vitesse | Amplitude du balancement | Ailes par côté |
| --- | --- | --- | --- | --- | --- |
| Commune | 2,0 | 1,2 Hz | 8 m/s | × 1 | 1 |
| Chauve-souris | 2,6 | 1,8 Hz | 11 m/s | × 1 | 1 |
| Ancienne | 20, continu | 25 Hz | 14 m/s | × 0,1 | 2 |
| Ange | 0,5 | 0,6 Hz | 19 m/s | × 1 | 1 |
| Faucon pèlerin | 1,6 | 1,4 Hz | 16 m/s | × 1 | 1 |
| Albatros | 0,8 | 0,9 Hz | 14 m/s | × 1 | 1 |
| Démon | 0,7 | 1,0 Hz | 18 m/s | × 1 | 1 |
| Séraphin | 0,9 | 0,9 Hz | 20 m/s | × 1 | 3 |
| Sylphe | 6 | 6 Hz | 18 m/s | × 0,4 | 3 |
