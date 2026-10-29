# Round 2 : campagne du 29 octobre 2026

Build corrigé (`main` après fusion de `fixes-1029`). Retests des KO du round 1 et des blocs touchés par les correctifs (matrice du § 4.2 de `TEST_PLAN.md`).

| id | statut | observé | preuve |
|---|---|---|---|
| N1 | OK | bouton Wiki de la table d'assemblage : le wiki s'ouvre sur sa page (fil d'Ariane Wiki > Workshops > The Assembly Table), retour : Workshops, accueil, puis la table ; plus de crash | shots/N1_wiki.png, N1_back.png |
| N2 | OK | wikicheck : 270 pages construites et dessinées, 0 échec (82 au round 1) | log |
| R2 | OK | touches du mod (simplywings.forward, back, left, right, ascend, descend, beat, travel) : stationnaire, double tap, déplacement, battements fonctionnent ; W monte (pitch −35°), S pique (+34,8°) | flight |
| H12b | OK | frein + droite 10 ticks : cap +4,2° contre +42,2° sans frein (10 %) | flight |
| I4 | OK | Night, frein maintenu en piqué (vitesse au-dessus du seuil) : charge 40, fenêtre, puis charge 0 et plus de recharge ; stationnaire à l'arrêt | flight |
| K4 | OK | auréole : anneau à plat au-dessus de la tête, de face, de profil et de dos, suit l'inclinaison de la tête | shots/K_after2.png, K_halo_head.png |
| K5 | OK | cape de test : masquée avec des ailes, visible sans ailes | shots/K_after2.png |
| I9 | OK | Night : ailes principales plus grandes (×1,15), membranes suivent | shots/K_after.png |
| L1b | OK | nouveau fond de l'écran de l'autel (960 × 540) | shots/L_r2.png |
| L10 | OK | plume d'offrande dessinée une seule fois (plus de croix) ; cercle d'invocation et runes au niveau du sol de l'autel | shots/L_r2.png |
| K2 | OK | flammes de l'autel au repos et du show au-dessus d'un bassin d'eau : plus recouvertes par l'eau | shots/L_r2.png |
| M5 | OK | tonneau de la table d'assemblage vidé puis structure régénérée : cuir et plumes de retour | data get block |
| O0 | OK | plus d'avertissement « duplicate items » de JEI au chargement (interpréteur de sous-type des conteneurs d'essence) | latest.log |
| A4 | OK | serveur dédié : Dev et Dev2 rejoignent sans registry mismatch, aucune erreur | build/server.log |
| Q2 | OK | Dev2 voit les ailes d'ange de Dev et l'auréole au-dessus de sa tête | run-mp2/studio/shots/Q2_dev2_sees_angel.png |
| L6 | OK | ×10 de Dev à l'autel : Dev2 (à 5 blocs) reçoit tout le show et maintenant aussi les cartes (10 deal, 6 shuffle, 10 flip, 10 pop, 10 place), le chœur et la musique des loots, en positionnel à l'autel | run-mp2 soundlog |
| P2b | OK | frôlement : Dev passe à 1,5 bloc de Dev2 à environ 35 m/s, Dev2 entend flight.flyby et les battements | run-mp2 soundlog |
| I5 | OK | dash de Night près de Dev2 : charge, grésillement, tonnerre (night_fury.thunder) et invisible reçus par Dev2 | run-mp2 soundlog |
| H17 | OK partiel | 30 s de vol varié sans kick ; en revanche, téléportation par commande (/tp) pendant le vol : le serveur signale « moved too quickly » à chaque /tp (aussi pour Dev2 sans ailes) et Dev a été expulsé pour « floating » deux fois juste après un /tp. Non reproduit sur un /tp pendant le stationnaire. À surveiller en jeu réel (portails, perles, mods de téléportation) | build/server.log |
| A6 | KO → corrigé | reconnexion en plein vol : expulsé pour « floating » 5 s après la connexion (serveur en mode sans vol, gravité normale). Corrigé (reprise en stationnaire), retesté : en l'air à la reconnexion, mode STABILIZED des deux côtés, pas de kick après 15 s | build/server.log, status |
| A5 | OK partiel | redémarrage du serveur dédié : collection, paire équipée (eagle) et stamina de Dev conservées ; le reste (pitié, historique, jauges, compresseur) non revérifié | status, simplywings info |
| F1 | OK | nouvelle fontaine : octogone de 4 blocs, 21 cellules (coins exclus), menu depuis n'importe quelle cellule, casse de tout le multibloc, pose refusée avec message sans place, acceptée sinon ; assemblée aussi dans la structure offering_fountain | shots/F_sheet.png, F_close.png, chat |
| A1b | OK | nouvel autel draconique : même taille, ailes symétriques, atlas pixel pour pixel | shots/A_sheet2.png |
| I8 | OK | queues de Night et Light : 8 segments, idles et marche ; 15 états sans traverser les jambes | run/studio/sheets/night_poses.png, light_poses.png |

## Bilan du round 2

Tous les KO du round 1 corrigés repassent. Une régression de fond trouvée et corrigée (reconnexion en plein vol). Restent ouverts : /tp en vol sur serveur (non reproduit) et le portail de sortie posé sur le sanctuaire d'invocation quand il précède la première visite de l'End. Voir `report.md`.
