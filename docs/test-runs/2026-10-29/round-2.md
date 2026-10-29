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
