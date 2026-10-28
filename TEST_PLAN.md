# Plan de test complet : campagne autonome du 28 octobre 2026

Ce plan fait foi pour l'ordre et la méthode des tests. `ToDoTesting.md` reste la liste des points et de leur état : chaque résultat de la campagne y est reporté (case cochée, ou note d'échec sous la ligne).

Contexte : zorago est absent toute la matinée, Claude dispose de plusieurs heures. Le PC est libre : on peut lancer Minecraft (client de dev, studio, serveur dédié). Le pack CurseForge `PleasePlayWithMe` reste interdit (ni lancé, ni modifié). L'instance `temp_buildSimplyWings` reçoit seulement le jar final, sans être lancée.

## 0. Règles de la campagne

1. **Ne jamais s'arrêter sur un échec.** Chaque test donne un statut, puis on passe au suivant :
   - `OK` : conforme à l'attendu ;
   - `KO` : écart observé (noter l'observé, l'attendu, la preuve) ;
   - `BLOQUÉ` : impossible à exécuter à cause d'un autre KO (noter lequel) ;
   - `HUMAIN` : seul zorago peut juger (écoute, ressenti, direction artistique) ; il passe dans la liste du § 5.
   
   Un crash du jeu est un KO : on note le rapport (`run/crash-reports`), on relance le studio et on reprend au test suivant.
2. **Journal** : `docs/test-runs/2026-10-28/round-1.md`, puis `round-2.md`. Une ligne par test : `| id | statut | observé | preuve |`. Une preuve, c'est une ligne de `run/studio/log.txt`, une capture (`shot`, dans `run/studio/`), un extrait de `logs/latest.log` ou un rapport de crash. Les captures se jugent en les ouvrant (Read).
3. **Journal technique** : après chaque bloc, chercher dans `run/logs/latest.log` (et `run-mp*/logs`) les `ERROR`, `Exception` et `WARN` qui citent `simplywings`. Chaque nouvelle erreur est un KO du bloc.
4. **Ordre de la matinée** :
   - **Préparation** (§ 1) ;
   - **Round 1** : tous les blocs du § 3, sans rien corriger ;
   - **Tri** : les KO du round 1 et les points du § 16 de `ToDoTesting.md`, classés par gravité (crash, puis bloquant, puis gameplay, puis visuel, puis art) ;
   - **Corrections** (§ 4) : un commit et un `git push` par correctif ou groupe cohérent, sans commentaire dans le code, en anglais dans le mod ;
   - **Round 2** : tout ce qui n'est pas OK au round 1, plus tous les blocs touchés par un fichier modifié (matrice du § 4.2), pour attraper les effets de bord ;
   - **Livraison** : `./gradlew build`, copie du jar dans `A:\CurseForge\Instances\temp_buildSimplyWings\mods` (remplace l'ancien), rapport final pour zorago (§ 6).
5. **Budget** : si le temps manque, priorité aux crashs, au vol, au gacha et aux structures. Les blocs longs et peu risqués (pack complet, retouches d'art) passent en dernier. Le report de ce qui n'a pas été fait est explicite dans le rapport.
6. **Pilotage** : le studio (`tools/studio/start.sh`, puis `python -I tools/studio/send.py "<cmd>;<cmd>"`). Commandes utiles de `client/studio/StudioTest` et `Studio` :
   - `cmd <commande>`, `wait <ticks>`, `shot <nom>`, `status`, `flight` (état interne : vitesse, frein, charge des Furies, réserve) ;
   - `key <nom> <ticks>` (touches : `jump`, `sprint`, `sneak`, `forward`, `back`, `left`, `right`, `travel`, `brake`, `menu`…), `look <yaw> <pitch>`, `select <slot>`, `use`, `useon x y z`, `attack [portée] [type]` ;
   - `screen inventory|wings|close`, `slot`, `click <fx> <fy>`, `button <id>`, `tab`, `tooltips`, `icons`, `inv`, `tip` ;
   - `jei`, `jeitype`, `pull <n>`, `respawn`, `newworld <nom>` ;
   - **nouveaux (27 octobre au soir)** : `soundlog on|off` (chaque son joué dans `log.txt` : id, source, position), `wikipages` (liste des 256 pages), `wikicheck [id,…]` (construit et dessine chaque page, note les exceptions), `flight`.
   
   Bancs automatiques existants : `tools/studio/drop_test.py` (59 drops), `tools/studio/table_test.py all` (179 recettes), `tools/studio/review.py <modèle>` (planches des ailes).
7. **Multijoueur** : `./gradlew runServer` (dossier `run/`, monde plat, `online-mode=false`, `allow-flight=false`, Dev et Dev2 op), puis `./gradlew runStudioMp` (Dev, `run-mp/`) et `./gradlew runStudioMp2` (Dev2, `run-mp2/`, ajouté le 27 octobre au soir). On pilote chaque client avec `send.py --dir=run-mp` ou `--dir=run-mp2`.

## 1. Préparation (avant le round 1)

- **P1.** `git pull`, `./gradlew build` sans erreur, `python -I tools/sounds/audit.py` (seuls les 4 bloquants de publication des Furies sont attendus).
- **P2.** Monde des builds de zorago : la copie de `temp_buildSimplyWings/saves/SimplyWings Builds` dans `run/saves/SimplyWings Builds` est faite le 27 au soir (l'ancien monde est gardé en sauvegarde à côté). Vérifier sa présence et son `level.dat`.
- **P3.** *(Fait le 27 au soir : monde copié, ancien monde gardé sous `SimplyWings Builds (old 2026-10-26)`, 5 templates exportés, contrôlés et copiés dans le mod. Le matin : seulement vérifier qu'ils sont dans le jar.)* **Export des 5 structures** (point du § 16) : `./gradlew runBuilds` avec le studio, puis `cmd simplywings sanctum export <nom>` pour `sky_sanctum`, `wing_forge`, `cloud_refinery`, `summoning_sanctum` et `offering_fountain`. Copier les `.nbt` de `run/saves/SimplyWings Builds/generated/simplywings/structures/` vers `src/main/resources/data/simplywings/structure/`. Avant l'export, vérifier que chaque cadre contient bien le build entier (captures de chaque cadre vu de haut et de côté). Après la copie, rebuild : c'est le seul changement de contenu fait avant le round 1, car tous les tests de structures en dépendent.
- **P4.** Monde de test neuf : `newworld Test28` (plat, créatif, triche), `gamerule doDaylightCycle false`, `doWeatherCycle false`, `keepInventory true`, `time set 6000`.
- **P5.** `soundlog on` pour toute la campagne : les sons se vérifient par leur déclenchement (id, moment, position). Le rendu à l'oreille passe en `HUMAIN`.

## 2. Ce qui ne se teste pas sans zorago

Classé `HUMAIN` d'office, regroupé dans le § 5 : jugement à l'oreille (niveaux, timbre, saturation, fatigue), ressenti du vol (ressorts, inertie, maniabilité), direction artistique (îles, autel, fontaine, ailes), lisibilité à 60 i/s avec shaderpack. On les prépare quand même (captures, journal des sons) pour qu'il n'ait plus qu'à valider.

## 3. Round 1 : les blocs de test

Chaque test : **id**, action, attendu. « (MP) » : à refaire sur le serveur dédié avec deux clients. Les références `§ n` renvoient à `ToDoTesting.md`.

### A. Démarrage, données, robustesse (≈ 15 min)

- A1. Le client de dev démarre, aucune erreur `simplywings` dans `latest.log`.
- A2. 133 recettes `component`, 40 `assembly`, registre `loot_source` (59 entrées), GLM `winged_drops`, `offering` des valeurs de revente : chargés sans erreur de parsing.
- A3. `/reload` : aucune erreur, recettes et sources toujours là (vérifier une recette dans JEI et un drop).
- A4. Serveur dédié : démarrage sans erreur, connexion de Dev puis Dev2 sans « registry mismatch » (MP).
- A5. Sauvegarde et rechargement du monde : collection, paire équipée, apparence, stamina, compteurs du gacha, historique, jauges des établis, contenu du compresseur, énergie ailée sur les items, positions et morts des sanctuaires (SavedData) conservés.
- A6. Déconnexion pendant le vol, pendant une charge des Furies, pendant le show du gacha et pendant les cartes : aucune erreur ; à la reconnexion, état cohérent (pas de vol fantôme, caméra rendue, objets du tirage donnés).
- A7. Mort en vol (`kill`) : vol coupé, réapparition au sol sans ailes déployées, stamina et sauts de secours rechargés, collection gardée.
- A8. Changement de dimension en vol (portail du Nether, portail de l'End, `execute in … run tp`) : vol coupé ou repris sans kick ni état bloqué.
- A9. Config client : l'écran de configuration du mod s'ouvre depuis la liste des mods ; les 9 clés sont lisibles.
- A10. JEI absent : impossible à tester sans retirer JEI du `localRuntime` ; on lance une fois `runServer` (JEI absent côté serveur) et on note le reste comme non testé.

### B. Items et tooltips (≈ 15 min)

- B1. Onglet créatif : toutes les entrées dans l'ordre attendu (table, établi, compresseur, autel, fontaine, flacons et réservoirs, absorbeurs, compas, plume d'invocation, 41 paires, 188 matériaux et pièces) ; `icons` : aucune texture manquante.
- B2. Tooltips d'un échantillon de chaque famille : description en italique gris, ligne vide, énigme violette avec ✦ ; retours à la ligne propres.
- B3. Tailles de pile : drops et intermédiaires 64, pièces 16, absorbeurs et compas 1, flacons vides 16, flacons pleins 1.
- B4. Flacons et réservoirs d'essence : teinte par essence, éclat sur l'essence intensifiée, ligne de quantité.
- B5. Paire en main, clic droit : elle s'ajoute à la collection, l'item est consommé, son de déblocage (`ui.unlock`, vérifié au `soundlog`), rien si la paire est déjà possédée (message).

### C. Énergie ailée (≈ 10 min)

- C1. Onglet énergie de l'établi de la forge : imprégner un item (éclat, ligne de tooltip), puis retirer l'énergie.
- C2. Persistance : sauvegarde, mort avec `keepInventory`, changement de dimension, coffre (A5).
- C3. N'importe quel item (épée, pioche, canne, bâton, bloc).

### D. Drops imprégnés (≈ 25 min)

- D1. `python -I tools/studio/drop_test.py` (les 59 sources) : chaque drop tombe dans sa condition, jamais hors condition ni sans énergie.
- D2. Toucher de soie et Fortune : le drop du mod tombe, Fortune ne le multiplie pas.
- D3. Minerai cassé à la main (sans outil adapté) : noter le comportement.
- D4. Explosion de TNT : aucun drop du mod.

### E. Établis : table d'assemblage, établi de la forge (≈ 30 min)

- E1. `python -I tools/studio/table_test.py all` : 179 / 179 (133 composants, réservoirs, flacon et absorbeurs à l'établi, 41 assemblages à la table), coûts prélevés dans les bonnes jauges.
- E2. Jauges : flacons et réservoirs versés dans la bonne jauge, refus d'une essence dans la mauvaise, plafond de chaque jauge.
- E3. Recette refusée si une jauge est insuffisante (résultat absent ou grisé, aucun item perdu).
- E4. Shift-clic sur le résultat : pas de duplication, essence débitée à chaque craft, arrêt quand l'essence manque.
- E5. Recettes en miroir (Fossil Spar, Swift Wing, ailes asymétriques) dans les deux sens.
- E6. Paire commune : cuir, élytre et 2 plumes, sans essence.
- E7. Fermer l'écran avec des items dans la grille : ils reviennent dans l'inventaire (ou tombent si l'inventaire est plein).
- E8. Casser un établi avec des essences dans ses jauges : comportement noté (perte ou conservation dans l'item).
- E9. Sons : marteau, trempe, infusion, essence versée (`soundlog`) ; ambiance discrète de l'établi de la forge près du bloc.
- E10. Bouton Wiki de chaque établi (voir N1, crash connu sur la table d'assemblage).

### F. Essences, absorbeurs, réserve (≈ 25 min)

- F1. Récolte dans l'Overworld entre Y 186 et 204, en déplacement puis en stationnaire (`hoverHarvest`) : remplissage du premier conteneur non plein, puis du suivant, puis des conteneurs vides (pile détachée proprement).
- F2. Vapeur au-dessus de la lave du Nether (hauteur `steamHeight`), éthérée au-dessus du vide de l'End à plus de 200 blocs du centre : un conteneur d'une autre essence n'est pas rempli.
- F3. Rien au sol, sans absorbeur, hors zone, inventaire sans conteneur libre.
- F4. Les 5 absorbeurs : débit mesuré sur 20 s (flacon 100 s, fer 200 s, puis 400, 800, 1 600 s pour un plein), le meilleur absorbeur de l'inventaire compte.
- F5. Sons : gouttes (`essence.drip`) seulement pendant un remplissage, irrégulières ; `essence.full` quand un conteneur se remplit ; rien quand tout est plein.
- F6. Réserve d'essence intensifiée : stamina à 0 en l'air avec un flacon intensifié → la stamina remonte par paquets de 20, le flacon baisse ; en déplacement (battements) et en stationnaire ; rien au sol, en créatif ni avec Night ; boucle de fumée (`essence.reserve`) pendant la réserve, fondu après.

### G. Compresseur de nuage (≈ 20 min)

- G1. Pose 3 × 3 × 5 par l'item ; pose refusée s'il manque de la place ; casser n'importe quelle partie casse tout et rend l'item.
- G2. Menu : 1 + 1 + 1 → 5 ; arrêt si une entrée est vide ou la sortie pleine ; reprise ensuite.
- G3. Rendu : engrenages et cylindre sur 360°, pistons, fumée en marche, arrêt progressif ; liquide derrière la vitre (capture).
- G4. Sons : boucle qui suit la vitesse et s'arrête quand on le casse ; bulles et brassage.
- G5. Sauvegarde et rechargement en pleine compression.

### H. Vol : contrôles et physique (≈ 60 min)

Paires de référence : la commune, la chouette, l'aigle, l'abeille (poussée continue), la chauve-souris, le cerf-volant, Yin et Yang, l'ange, le démon, Night, Light. `status` et `flight` après chaque action.

- H1. Saut au sol puis saut en l'air : saut ailé, coût `airJumpCost`, battement fort.
- H2. Sous le coût : 2 sauts de secours gratuits, puis plus rien ; recharge au sol.
- H3. Double tap très rapide (4 ticks) : passage en stationnaire, saut ailé remboursé ; double tap lent : deux sauts ailés.
- H4. Stationnaire : déplacement ZQSD, montée (saut) et descente (sneak), vitesse `hover_speed` de la paire, dérive et balancement selon la stabilité, drain de stamina, sortie quand la stamina est vide.
- H5. Alt en l'air : bascule stationnaire ↔ déplacement, instantanée.
- H6. Alt maintenu 1 s au sol : envol chargé en déplacement (barre de charge, `flight.takeoff_launch` une seule fois), possible en marchant et en courant, puis Alt ignoré 1,5 s.
- H7. Déplacement : sprint = battements (gain au coup, `strikeAt`), coût par battement, vitesse plafonnée à la vitesse max de la paire ; poussée continue pour l'abeille (`FLAG_THRUST`, aucun paquet par battement).
- H8. Direction : W et S sur le tangage, A et D sur le cap, ressort critique (pas de rotation directe). **Avant le correctif** : W pique (à noter, le correctif inverse) ; voir R2.
- H9. Plané : la stamina remonte (`glideRegen`) 10 ticks après le dernier battement.
- H10. Piqué : vitesse qui monte vers `terminalVelocity`, vent plus fort ; ailes repliées (tuck) au-delà de la vitesse max avec sprint et tangage ≥ `tuckMinPitch`.
- H11. Décrochage sous la vitesse de portance : chute, puis récupération.
- H12. Frein : décélération, retour au stationnaire à basse vitesse (si stamina) ; sans stamina, descente lente (`zeroStaminaSinkSpeed`). **Après correctif** : les touches n'ont plus que 10 % d'effet au frein, la souris reste libre.
- H13. Crash contre un mur au-dessus de `crashThreshold` : vol coupé, dégâts proportionnels, son d'atterrissage dur ; sous le seuil, rien.
- H14. Atterrissages doux et durs, aucun dégât de chute à l'atterrissage d'un vol (`onFall`).
- H15. Entrée dans l'eau ou la lave, élytre, monture, lit, spectateur, vol créatif : le vol des ailes se coupe ou ne démarre pas.
- H16. Créatif : stamina infinie ; mode survie : stamina de la paire.
- H17. Aucun kick pour vol sur le serveur dédié (`allow-flight=false`) après 2 minutes de vol varié (MP).
- H18. Caméra : distance, roulis en virage, découplage de la souris en déplacement (sensibilité −1/3), retour du type de caméra d'origine.
- H19. Balayage des 41 paires : `cmd simplywings equip <paire>`, envol, 5 s de battements, stationnaire, atterrissage ; `status` sans anomalie, une capture en vol par paire, aucune erreur au journal.
- H20. Joueur distant (MP) : Dev2 voit les ailes de Dev, battements reconstruits, BLAST de l'envol, mode et frein, rien quand il est invisible.

### I. Furies (≈ 30 min)

- I1. Night possédée : stamina infinie (toutes paires équipées).
- I2. Light possédée : bouton « Equip appearance », l'apparence change tout le visuel (rythme compris), la physique reste celle de la paire équipée ; « Reset appearance ».
- I3. Charge au frein (2 s), barre de charge, puis sprint dans la fenêtre (0,5 s) : dash à la vitesse terminale, invisibilité (`invisibilityTicks`), sons de charge, de dash, d'invisibilité.
- I4. **Bug connu (§ 16)** : frein gardé après la fenêtre → la barre se recharge malgré le cooldown de 5 s. Mesurer et noter (`flight` : `furyCharge`, `furyWindow`).
- I5. Night : tonnerre au dash, entendu par Dev2 à environ 190 blocs (MP), foudre visuelle sur le joueur et 4 éclairs autour, sans dégâts ni feu.
- I6. Light : explosion (son seul) 6 s après le dash, à l'endroit du dash.
- I7. Cri de piqué de chaque Furie.
- I8. Queue : jamais dans les jambes (capture en vol, au sol, en marche) ; **après correctif** : idles au repos, mouvement à la marche, 1 ou 2 segments de plus.
- I9. Première paire d'ailes (§ 16) : capture avant et après le correctif.

### J. Menu des ailes (J) et bouton d'inventaire (≈ 15 min)

- J1. Touche J : liste de toutes les paires du registre ; verrouillées grisées ; cachées en « ??? ».
- J2. Clic sur une paire possédée : sélection (`ui.select`) ; sur une verrouillée : `ui.locked` ; boutons Équiper et Retirer (actifs ou non), sons.
- J3. Aperçu 3D, stats, bonus de collection (sixième légendaire), marqueurs équipée et apparence.
- J4. Défilement (molette), longue liste.
- J5. Bouton Ailes de l'inventaire : survie et créatif, ne chevauche ni JEI ni le livre de recettes.
- J6. Déblocage hors gacha (`unlock`) : level up ; pendant un tirage : rien.

### K. Rendu des ailes et du joueur (≈ 40 min)

- K1. `python -I tools/studio/review.py <modèle>` pour un échantillon (une par famille + Furies) : 15 états sans texture manquante.
- K2. **Transparence (§ 16)** : flammes de l'autel, ailes de feu, ailes émissives et translucides devant l'eau et sous ou au-dessus des nuages (captures dans la même scène).
- K3. **Plans fins (§ 16)** : lignes de 0,1 px sur les membranes des Furies et sur toutes les paires à plans (captures de près, de profil).
- K4. **Auréole de l'ange (§ 16)** : capture au repos, en marche, en vol, en stationnaire.
- K5. **Cape (§ 16)** : `cape on` (cape de test posée sur le joueur de dev), en vol : capture avant correctif ; après correctif, plus aucune cape tant que des ailes sont équipées, cape de retour sans ailes.
- K6. Ailes émissives la nuit ; z-fighting des faces superposées de près et de loin.
- K7. Traînées au freinage depuis les bons os (orage, méganeure).
- K8. Polatouche : bout de l'aile collé à la main en marche, frappe et minage.
- K9. Pose du joueur en vol (profilé, cabré au frein, collé en piqué, roulis, stationnaire).

### L. Gacha : autel, show, cartes, fontaine (≈ 60 min)

- L1. Autel posé (`setblock` puis vraie structure `summoning_sanctum`), clic droit : fenêtre, illustration (nouveau fond après correctif), tracker, onglets Historique et Taux.
- L2. `gacha feathers 64` ; ×1 et ×10 : plumes retirées ; refus sans plumes (son `ui.denied`, message) ; créatif sans coût.
- L3. Show dans le monde, un tirage par palier forcé (`gacha force ss|sss|mythic|x10`, puis un B et un A naturels) : captures à t = 0, 2, 5, 7, 10 s, à l'éclat et aux ailes. Le show suit la timeline (`altar_show.json.gz`), la caméra suit l'os caméra puis revient au joueur.
- L4. **Plus de skip** : clic, Échap, espace et Entrée ignorés pendant le show.
- L5. Cartes : distribution, retournement, « Click to reveal all », fermeture, objets donnés en une fois (ou jetés si l'inventaire est plein), réclamation à la déconnexion.
- L6. Sons du show (`soundlog`) comparés à `show_sounds.json` : chaque cue au bon moment (±1 tick) et au bon endroit, par palier ; musique du ×10 jusqu'au bout (24,9 s) ; musique des loots en fondu à la fermeture. **Chez celui qui invoque ET chez un spectateur (Dev2, MP)** : aujourd'hui les cartes, le chœur et la musique des loots ne jouent que chez celui qui invoque (§ 16) ; chercher aussi la musique finale que zorago n'a pas entendue (`music_burst`, `music_loot`).
- L7. Pitié : `gacha pity 99 0 0` → S garanti ; `0 499 0` → SS ; `0 0 999` → SSS ; compteurs remis à zéro ; `gacha info` ; `gacha reset`.
- L8. Un B ou un A donne un seul objet ; onglet Taux à jour.
- L9. Dev2 à moins de 512 blocs voit et entend le show de Dev (MP) ; au-delà, rien.
- L10. **Bugs connus (§ 16)** : item de plume dessiné deux fois en croix ; cercle d'invocation 2 blocs sous l'autel (caché par le build) ; captures.
- L11. Fontaine : 100 objets B → 1 plume, 10 objets A → 1, paires rare, épique, légendaire → 1, 10, 100 ; réserve des fractions ; objets sans valeur rendus à la fermeture ; ambiance de la fontaine.
- L12. Autel et fontaine : cassés à la pioche (pas à la main), tombent en item, reposés ailleurs, fonctionnent ; ambiance de l'autel.
- L13. Coffres : `/loot spawn` cent fois sur `chests/simple_dungeon` (≈ 5 %, 1 à 3 plumes), Nether (10 %), `end_city_treasure` (20 %) ; jamais plus de 3 plumes.

### M. Structures, dragon et compas (≈ 45 min)

Sur les **nouveaux templates** exportés en P3.

- M1. `/simplywings sanctum <nom>` pour les 5 : pose complète, comparée au monde de build (captures sous le même angle), **états des blocs conservés** (trappes, escaliers, dalles, portes, orientation, eau) ; échantillon de blocs lu avec `/data get block` et comparé.
- M2. Établis présents et fonctionnels dans chaque structure (table, établi, compresseur, autel, fontaine), renderers et show bien placés autour de l'autel.
- M3. Tonneaux de la table d'assemblage : cuir et plumes posés par zorago ; forge sans loot.
- M4. Morts du dragon : `/simplywings dragon` successifs et une vraie mort (`summon ender_dragon`, `damage … by @s`) : 1re → sanctuaire d'invocation, 2e → sanctuaire céleste, 3e → forge, 4e → raffinerie, 5e → fontaine ; compas donnés une seule fois par joueur, message ; son d'apparition (`structure.sanctum_appear`) pour les joueurs de l'End.
- M5. Régénération : casser autel, fontaine, établi, compresseur, vider un tonneau, puis une mort → ils reviennent, tonneaux remis à neuf, le reste intact ; à partir de la 6e mort, trous et blocs étrangers dans le template réparés, constructions dans le vide gardées.
- M6. Compas : prise en main (son), aiguille vers la cible dans l'End, tourne ailleurs ; un compas par structure.
- M7. Pas d'endermen naturels à moins de 36 blocs des centres.
- M8. Position mémorisée : changer `[forge] radius` ne déplace pas une structure déjà posée.

### N. Wiki (≈ 15 min)

- N1. Ouverture depuis la table d'assemblage (**crash connu**), l'établi, le compresseur et le menu J.
- N2. `wikicheck` : les 256 pages se construisent et se dessinent sans exception ; `wikipages` : aucune page orpheline.
- N3. Navigation : catégories, retour, fil d'Ariane, défilement, liens ; captures de 10 pages variées (paire, matériau, chapitre, recette).

### O. JEI (≈ 20 min)

- O1. Catégories : assemblage, composants, énergie, drops de mobs, de blocs, de pêche, compression de nuage ; textes sans débordement.
- O2. Répartition des 59 sources (27 mobs, 29 blocs, 3 pêche) ; tags qui défilent.
- O3. Catalyseurs (outils imprégnés, table, établi, compresseur) ; R et U sur un drop, un minerai, une pièce, une paire, un œuf de mob.
- O4. Zones cliquables de l'écran de la table, bouton « + » de transfert sur le bon onglet seulement.
- O5. Pages d'info (table, compas, flacons, absorbeurs, plume d'invocation).

### P. Sons (≈ 30 min)

Tous les sons sont validés à l'oreille par zorago (27 octobre au soir) : on ne vérifie plus que leur déclenchement (`soundlog`), l'absence d'erreur, et l'audibilité du gacha pour les spectateurs. Un correctif qui touche un son le fait réécouter (`HUMAIN`).

- P1. `/simplywings sound list` puis chaque id joué une fois : aucun « Unable to play unknown soundEvent », aucun fichier OGG manquant au journal.
- P2. Vol : battement par paire, coup fort, vrombissement (et son arrêt), vent, envol, atterrissages, crash, **stationnaire** (battement très faible, espacement irrégulier, jamais pour les paires à vrombissement), **frein** (coup fort puis souffle qui baisse avec la vitesse, fondu au relâchement), **frôlement** (Dev2 passe vite à moins de 5 blocs, MP).
- P3. Interface : menu J (sélection, verrouillée, apparence, équiper, retirer), refus, cartes.
- P4. Blocs et monde : ambiances de l'autel, de la fontaine et de l'établi ; compas ; apparition d'un sanctuaire ; compresseur.
- P5. Furies : charges, dash, invisibilité, tonnerre de Night à distance, explosion de Light.
- P6. Sous-titres activés : chaque son du mod a un sous-titre lisible (captures).

### Q. Multijoueur (≈ 30 min, regroupe les « (MP) »)

- Q1. A4, H17, H20, I5, L9, P2 (frôlement).
- Q2. Équiper, retirer et changer d'apparence chez Dev : Dev2 voit le changement.
- Q3. Invisibilité de Dev après un dash : invisible pour Dev2, ailes comprises, puis visible.
- Q4. Deux tirages simultanés à deux autels : les shows ne se mélangent pas.
- Q5. Aucun paquet par tick : compter les paquets du mod sur 10 s de vol (journal réseau du serveur, ou instrumentation temporaire retirée ensuite).

### R. Configs et touches (≈ 15 min)

- R1. Config serveur : changer `rescueJumps`, `flapCost`, `[cloud] minY`, `[gacha] rateSSS` dans `serverconfig`, redémarrer : pris en compte.
- R2. **Touches (§ 16, après correctif)** : catégorie SimplyWings du menu Touches avec **toutes** les actions du mod (directions, monter, descendre, saut ailé, stationnaire, battements, frein, déplacement, menu, dash) ; par défaut W monte et S descend ; `invertPitch` supprimé ; chaque touche remappée (dont W et S échangées) prise en compte en vol, et sauvegardée dans `options.txt`.
- R3. Touche Frein non assignée par défaut ; sneak freine.

### S. Pack complet (en dernier, ≈ 30 min, si le temps le permet)

- S1. `run-pack/` (copie locale du pack, `runPackStudio`) avec le nouveau jar : démarrage, aucune erreur `simplywings`, un vol, un tirage, la table ; Iris et un shaderpack : ailes, show et transparence (captures).

## 4. Corrections entre les deux rounds

### 4.1 Ordre

1. Crashs (wiki, et tout crash du round 1).
2. Gameplay : barre de charge et cooldown des Furies, maniabilité au frein, toutes les touches du mod configurables et suppression de `invertPitch`, sons du gacha relayés aux joueurs autour (cartes comprises).
3. Gacha : cercle d'invocation à hauteur du sol de l'autel, plume dessinée une seule fois, nouveau fond d'écran.
4. Structures : loot de la forge retiré, tonneaux fixes de la table d'assemblage remis à neuf à la régénération, dépendances aux positions des établis.
5. Rendu : transparence devant l'eau et les nuages, lignes des plans fins, auréole de l'ange, cape supprimée tant que des ailes sont équipées (sans mixin).
6. Art : Furies (première paire plus grande, queue animée et plus longue), refonte de l'autel puis de la fontaine (octogone de 4 blocs, blocs de collision invisibles, densité de texels juste). Ces livrables restent **à valider par zorago** : captures et scène Blockbench à son retour.
7. Les KO du round 1 qui ne sont pas dans cette liste, selon leur gravité.

Chaque correctif met à jour les documents concernés (`ToDoTesting.md`, `README.md`, `CAHIER_DES_CHARGES.md` pour les contrôles, `wingsRules.md` pour les ailes, `GACHA_ALTAR_ANIMATION.md` pour le show) dans le même commit.

### 4.2 Matrice des effets de bord (pour le round 2)

| Fichiers modifiés | Blocs à refaire |
|---|---|
| `client/ClientFlight`, `ModKeys`, `ClientConfig`, `FlightCamera` | H, I, P2, Q, R |
| `flight/FuryServer`, `FuryEffects`, `data/FurySet`, paquets des Furies | I, P5, Q |
| `render/`, `anim/`, `rigs/*.bbmodel`, `RigLoader`, `WingsLayer`, `BodyPose` | K, H19, I8-I9, J3 |
| `client/show/*`, `gacha/*`, `AltarScreen`, `CardDeck`, `altar_show.json.gz`, `show_sounds.json` | L, P (gacha), Q4 |
| `structure/EndSanctums`, templates `structure/*.nbt`, loot des tonneaux | M, L1, L12, E (établis posés par les structures), G |
| blocs (`block/*`, autel, fontaine), modèles et textures de blocs | E, G, L11-L12, M2, B1 |
| `client/wiki/*` | N, E10 |
| `network/Payloads`, `ModNetwork` | Q en entier, A4, A6 |
| `essence/*`, `CloudHarvest` | F, E2 |
| sons (`sounds.json`, `catalog.json`, `WingSounds`, `FlightLoops`) | P, audit |

## 5. Liste pour zorago (à son retour)

Préparée pendant la campagne, avec captures et liens :
- sons : tous validés le 27 au soir ; seulement réécouter le gacha côté spectateur après le correctif ;
- ressenti du vol : frein à 10 % de maniabilité, nouvelles touches (W monte), barre des Furies ;
- art : refonte de l'autel et de la fontaine, ailes et queue des Furies, auréole ;
- structures exportées : fidèles au build (captures comparées) ;
- tout ce qui est `HUMAIN` dans les deux rounds.

## 6. Rapport final

`docs/test-runs/2026-10-28/report.md` : chiffres (OK, KO, bloqués, humains, par round), correctifs (commits), ce qui reste KO et pourquoi, ce qui n'a pas pu être testé, liste du § 5. `ToDoTesting.md` mis à jour (cases et notes). Jar copié dans `temp_buildSimplyWings`.
