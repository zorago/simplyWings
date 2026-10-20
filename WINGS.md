# Les ailes de Simply Wings

Catalogue des paires d'ailes : identité, apparence, ressenti en vol et stats. Les chiffres viennent des JSON (`src/main/resources/data/simplywings/simplywings/wing/`) et de la config serveur `[stats]`. Les règles d'équilibrage sont dans `BALANCE.md`.

Dernière mise à jour : 20 octobre 2026.

## Paliers d'ambiance

- **Commune** : la base, des élytres améliorées.
- **Rare** : des créatures réelles, dans une ambiance vanilla, simple, « faite main ».
- **Épique** : des esprits, des éléments et des créatures disparues (préhistoriques), avec des thèmes qui sortent du vanilla.
- **Légendaire** : des dieux, des êtres divins et des figures mythologiques. Chaque légendaire maxe une stat visible.

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
| Ailes de méganeure | Épique | 6,0 | 3,5 | 2,3 | 9,1 | 4,0 | Une libellule fossile faite pour construire |
| Ailes de ptérosaure | Épique | 7,5 | 5,5 | 4,5 | 4,0 | 3,0 | Le planeur préhistorique |
| Ailes de flamme | Épique | 3,5 | 8,5 | 6,0 | 5,0 | 2,0 | L'élément feu : des coups explosifs |
| Ailes de la marée | Épique | 6,0 | 4,5 | 4,5 | 8,0 | 5,5 | L'élément eau : fluide et maniable |
| Ailes de pierre | Épique | 8,5 | 4,0 | 2,5 | 3,0 | 7,5 | L'élément terre : lourdes, solides, inépuisables |
| Ailes du vent | Épique | 4,5 | 6,0 | 8,2 | 6,5 | 2,5 | L'élément air : légères et rapides |
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

## Paramètres d'identité

Ces paramètres ne dépendent pas de la collection : ils définissent le caractère de chaque paire.

| Paire | Battements/s (déplacement) | Stationnaire : cadence | Stationnaire : vitesse | Amplitude du balancement | Ailes par côté |
| --- | --- | --- | --- | --- | --- |
| Commune | 2,0 | 1,2 Hz | 8 m/s | × 1 | 1 |
| Chauve-souris | 2,6 | 1,8 Hz | 11 m/s | × 1 | 1 |
| Méganeure | 20, continu | 25 Hz | 14 m/s | × 0,1 | 2 |
| Ptérosaure | 0,9 | 1,1 Hz | 10 m/s | × 1,1 | 1 |
| Flamme | 1,4 | 1,6 Hz | 14 m/s | × 1 | 1 |
| Marée | 1,0 | 0,8 Hz | 13 m/s | × 0,6 | 1 |
| Pierre | 0,8 | 0,7 Hz | 10 m/s | × 0,3 | 1 |
| Vent | 1,6 | 1,4 Hz | 16 m/s | × 1,2 | 2 |
| Ange | 0,5 | 0,6 Hz | 19 m/s | × 1 | 1 |
| Démon | 0,7 | 1,0 Hz | 18 m/s | × 1 | 1 |
| Séraphin | 0,9 | 0,9 Hz | 20 m/s | × 1 | 3 |
| Psyché | 0,6 | 0,5 Hz (respiration) | 20 m/s | × 0 | 2 |
| Dédale | 20, continu (réacteurs) | 30 Hz (vibration) | 18 m/s | × 0,3 | 1 |

## Effets et première personne

- Les particules sont complètes seulement en vol de déplacement. En stationnaire, elles tombent à 25 %, et au sol ou pendant les sauts ailés à 10 %. Les sons baissent aussi.
- En vue à la première personne, le joueur ne voit jamais ses propres particules : celles qui apparaîtraient devant la caméra, ou qui partiraient vers elle, ne sont pas créées. Les autres joueurs les voient normalement.
