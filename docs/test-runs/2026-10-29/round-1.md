# Round 1 : campagne du 29 octobre 2026

Plan : `TEST_PLAN.md`. Statuts : OK, KO, BLOQUÉ, HUMAIN, NT (non testé, avec la raison).

| id | statut | observé | preuve |
|---|---|---|---|
| P1 | OK | build sans erreur, 5 templates de structure dans le jar, audit : seuls les 4 sons des Furies signalés (attendu) | build/libs/simplywings-0.1.0.jar |
| A1 | OK | client démarré, aucune erreur ni avertissement simplywings au journal | latest.log |
| A2 | OK | 1473 recettes chargées sans erreur de parsing (141 component, 40 assembly + 3 autres), 59 loot_source | latest.log |
| A3 | OK | /reload sans erreur ; JEI avertit de 20 doublons dans l'onglet créatif (voir O0) | latest.log |
| O0 | KO | JEI : « 20 duplicate items » dans l'onglet Simply Wings = les 5 conteneurs × 4 essences remplis, que JEI ne distingue pas faute d'interpréteur de sous-type pour le composant simplywings:essence | latest.log 08:58:18 |
| B1 | OK | onglet créatif : 270 entrées (blocs, plume, conteneurs vides et pleins, absorbeurs, compas, paires, matériaux) ; planche des 250 items : aucune texture manquante | shots/icons_0.png |
| B2 | OK | tooltips description + ligne vide + énigme ✦ sur drop, paire, flacon, compas, plume, autel, Yin et Yang ; note : la description de l'autel parle de membrane et de cristal de dragon, à revoir avec la refonte (plus de dragon) | log tip |
| B3 | OK | piles : drop 64, cœur 16, absorbeur 1, compas 1, flacon vide 16, plume d'invocation 64, paire 1 | log inv |
| B5 | OK | clic droit sur une paire : débloquée et équipée, son ui.unlock ; 2e fois : déjà possédée. Consommation de l'item à revérifier en survie (créatif : non consommé, normal) | soundlog |
| E1 | OK | 183 crafts sans échec : 133 composants + réservoirs, flacon et absorbeurs à l'établi, 41 assemblages + paire commune à la table ; coûts prélevés = coûts des recettes | build/table_test.out |
| E6 | OK | paire commune : cuir, élytre, 2 plumes, sans essence (balayage) | build/table_test.out |
| H1 | OK | saut au sol puis saut ailé en l'air : stamina 167,7 → 147,7 (coût 20), wing.eagle.flap_strong + couche | soundlog |
| H2 | OK | stamina 0 en l'air : 2 sauts de secours (2 → 1 → 0), le 3e ne fait rien ; recharge à 2 au sol | status |
| H3 | OK | double tap rapide en l'air : stationnaire, saut ailé remboursé (stamina 166,3) | status |
| H4 | OK | stationnaire : avancer 4 blocs/s, monter 4,3 blocs en 1 s, descendre plus lentement, drain environ 2,5/s ; battements faibles du stationnaire 1/s (wing.eagle.flap) | status, soundlog |
| H5 | OK | Alt en stationnaire : déplacement instantané | flight |
| H6 | OK | Alt maintenu au sol : envol chargé en déplacement à −40° et 15,6 m/s | flight |
| H7 | OK | sprint : battements, vitesse 18,8 → 36,2 m/s, stamina 166 → 61 | flight |
| H9 | OK | plané en palier : stamina 61 → 70 en 3 s, vitesse quasi conservée | status |
| H8 | OK | avancer = piquer (pitch +35,6°), à inverser par le correctif des touches ; A et D sur le cap | flight |
| H12 | OK | frein : 34,7 → 7,3 m/s en 1,5 s puis stationnaire ; coup d'aile fort puis flight.brake | flight, soundlog |
| I1 | OK | Night possédée : stamina 300, infinie | status |
| I3 | OK | Night : charge au frein (40 ticks, éclairs visuels pendant la charge), sprint dans la fenêtre : dash à 104,6 m/s, invisibilité 300 ticks, night_fury.invisible + night_fury.thunder | flight, soundlog, data get |
| I4 | KO (connu) | charge au frein avec Night : après la fenêtre (40 + 10 ticks), retour en stationnaire ici ; la recharge malgré le cooldown n'apparaît que si la vitesse reste au-dessus du seuil d'arrêt (piqué), comme décrit par zorago. Corrigé sur fixes-1029 | flight |
| I6 | OK | Light : dash à 104,6 m/s, light_fury.blast + invisible, puis light_fury.invisible_blast 6 s après | soundlog |
| L1 | OK | autel posé, clic droit main vide : écran, tracker 0/100/500/1000, onglets ; avec une plume en main à 5,5 blocs : rien (portée) | shots/L1_screen_b.png |
| L2 | OK | ×1 : 1 plume retirée (128 → 127), compteurs de pitié à 1 | clear count, gacha info |
| L3 | OK | ×1 B, ×10 forcé, SSS et mythique forcés : show complet, cartes, cues attendus (×10 : 10 deal/flip/pop/place, 6 shuffle, blasts small, medium, big une fois chacun, 3 sky_crack, 2 wing_beat, music_burst puis choir + music_loot) | shots/L3_sheet.png, L3_x10_sheet.png, M2_sheet.png |
| L4 | OK | clic pendant le show : rien, le show continue (un premier essai mythique a montré les cartes à 8 s, non reproduit ensuite avec ni sans clic) | shots/C_pair.png |
| L5 | OK | fermeture des cartes : objet donné (Redstone Pulse), inventaire à jour à la fermeture de l'écran | clear count |
| L6 | KO (connu) | cartes, chœur et musique des loots joués en son d'interface (relative, master) : rien chez les autres joueurs. Corrigé sur fixes-1029 (relais serveur) | soundlog |
| L10 | KO (connu) | plume d'offrande en croix : le plan a 2 faces superposées et le rendu sans culling montre la face arrière inversée. Corrigé sur fixes-1029 (1 face) | shots/L3_feather_zoom.png |
| L7 | OK | pitié dure : S à 100, SS à 500, SSS à 1000, compteurs remis à zéro (S remis aussi par un SS), gacha info | chat |
| L11 | OK (1 KO texte) | fontaine : 100 Abyssal Ink (0,01) → 1 plume, Owl Wings (1,0) → 1 plume, réserve 0,00, la terre sans valeur rendue à la fermeture ; message « gives you 1 feathers » (pluriel fautif au singulier) | menu, chat |
| L13 | OK | coffres : 200 tirages de simple_dungeon → 11 plumes, 200 de end_city_treasure → 61 (cohérent avec 5 % et 20 % × 1 à 3) | drops |
| M1 | OK sauf invocation | sanctum céleste, forge, raffinerie, fontaine : 150 états de blocs échantillonnés chacun, 150/150 identiques au template. Sanctuaire d'invocation : 115/150, les écarts sont tous autour de l'autel (y 20 à 24) où le portail de sortie du dragon (bedrock) a été posé : posé par commande avant toute visite de l'End, l'End a cherché le podium sur la colonne la plus haute en (0, 0), c'est-à-dire le sanctuaire. En jeu normal (1re mort du dragon) le portail existe déjà ; risque avec des mods qui recréent le combat (YUNG's Better End Island) à vérifier | tools/studio/structure_check.py, shots/M_summon_top.png |
| M3 | OK | tonneaux de la table d'assemblage : cuir et plumes posés par zorago | data get block |
| M4 | OK | /simplywings dragon : morts 1, 2, 3 comptées ; compas seulement sur l'île centrale (joueur loin : rien, conforme) | chat |
| M5 | KO (connu) | tonneau vidé puis morts 2 et 3 : pas rerempli. Corrigé sur fixes-1029 | data get block |
| N2 | KO (connu) | wikicheck : 82 pages sur 270 lèvent une NullPointerException à l'ouverture directe (même cause que le crash de la table, § 16). Corrigé sur fixes-1029 | log wikifail |
| J1 | OK | menu J : liste, aperçu 3D, stats, boutons Equip appearance, Equip, Unequip, bouton Wiki | shots/J1_menu.png |
| J2 | OK | clic sur une paire : ui.select | soundlog |
| K4 | KO (connu) | auréole de l'ange : anneau vertical derrière la tête, à hauteur de la nuque, ne suit pas la tête | shots/K_before.png |
| K5 | KO (connu) | cape de test (cape on) visible entre les ailes | shots/K_before.png |
| K3 | NT | lignes de 0,1 px : pas visibles à cette distance sur les captures ; cause identifiée dans le code (gonflement anti-scintillement des cubes plats), corrigée sur fixes-1029, à capturer de près au round 2 | shots/K_before.png |
| F1 | OK | stationnaire à Y 195 avec absorbeur de base et 2 flacons vides : un flacon détaché et rempli de 11 essence de nuage en 10 s | inv |
| F5 | OK | 31 essence.drip en 10 s pendant le remplissage | soundlog |
| F6 | OK | stamina mise à 0 en stationnaire avec un flacon intensifié : stamina remontée à 38,5, flacon 100 → 60, boucle essence.reserve | status, inv, soundlog |
| G2 | OK | 1 flacon de nuage + 1 de vapeur + 1 d'éthérée (100 chacun) → réservoir de fer à 500 d'intensifiée ; arrêt quand les entrées sont vides | menu |
| G1 | OK partiel | casser une partie (setblock destroy) retire les 45 blocs et rend les conteneurs ; l'item du compresseur ne tombe pas, mais ce n'est pas une casse par un joueur (à refaire à la main) | drops |
| A7 | OK | mort en stationnaire : vol coupé, réapparition au sol, mode NONE, stamina et sauts de secours rechargés, paire gardée | status |
| A8 | OK | téléport vers le Nether en vol de déplacement puis retour : vol coupé proprement, aucun kick ni erreur | status, latest.log |
| O1 | OK | les 7 catégories JEI (assemblage, composants, compression, énergie, drops de mobs, de blocs, pêche) s'ouvrent et s'affichent, chances, quantités et conditions lisibles | shots/O_sheet.png |
| O5 | OK | page d'info de la plume d'invocation | shots/O5_feather.png |
| Q | reporté | multijoueur testé au round 2 sur le build corrigé (relais des sons des cartes, cape, auréole vues par un autre joueur) |  |
| A5 | reporté | sauvegarde et rechargement testés au round 2 |  |
| S | reporté | pack complet en fin de campagne si le temps le permet |  |

## Bilan du round 1

- Tests exécutés : 56 lignes. KO nouveaux : O0 (doublons JEI), L11 (« 1 feathers »), M1 (portail de sortie posé dans le sanctuaire d'invocation quand il précède la première visite de l'End). Les autres KO sont les bugs connus du § 16, reproduits : crash et pages du wiki (82/270), barre des Furies, sons des cartes non relayés, plume en croix, cercle caché, auréole, cape, tonneaux non reremplis.
- Aucun crash pendant le round, aucune erreur `simplywings` au journal.
- Corrections faites pendant le round, sur la branche `fixes-1029` (non chargées dans le jeu testé) : wiki, barre et cooldown des Furies, maniabilité au frein, touches du mod, cape, relais des sons des cartes, doublons JEI, loot de la forge et tonneaux, cercle d'invocation, nouveau fond, transparence des flammes, auréole, lignes des cubes plats, queue et ailes des Furies, plume en croix.
