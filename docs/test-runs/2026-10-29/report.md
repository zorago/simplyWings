# Rapport de la campagne de test du 29 octobre 2026

Campagne autonome selon `TEST_PLAN.md`, de 8 h 55 à environ 10 h 45, sans intervention de zorago. Journaux détaillés : `round-1.md` et `round-2.md`. Captures : `run/studio/shots/`, `run-mp2/studio/shots/`, planches `run/studio/sheets/`.

## Résultat

**Le mod sort de la campagne en version 1.0.0 (bêta).** Jar `build/libs/simplywings-1.0.0.jar` copié dans `A:\CurseForge\Instances\temp_buildSimplyWings\mods` (l'ancien 0.1.0 retiré).

- Round 1 (code d'origine) : 56 vérifications. Aucun crash pendant le round, aucune erreur `simplywings` au journal. Tous les bugs du § 16 reproduits, 3 nouveaux défauts trouvés (doublons JEI, « 1 feathers », portail de sortie posé dans le sanctuaire d'invocation quand il précède la première visite de l'End).
- Corrections : 22 commits, d'abord sur une branche séparée pendant le round 1 (`fixes-1029`), puis fusionnés.
- Round 2 (code corrigé) : tous les KO corrigés repassent ; multijoueur testé sur le serveur dédié avec deux clients. Une régression de fond trouvée et corrigée : expulsion « floating » à la reconnexion en plein vol.

## Corrigé et testé

| Point | Preuve |
|---|---|
| Crash du wiki depuis la table, 82 pages sur 270 qui plantaient | 270/270, retour arrière OK |
| Barre de charge des Furies qui se rechargeait malgré le cooldown | plus de recharge après la fenêtre |
| Maniabilité au frein | 4,2° de cap au lieu de 42,2° |
| Toutes les touches du mod configurables, W monte par défaut, `invertPitch` supprimé | vol complet aux nouvelles touches |
| Cape masquée avec des ailes | cape de studio, avec et sans ailes |
| Auréole de l'ange au-dessus de la tête, suit la tête | captures face, profil, dos, tête baissée |
| Sons des cartes, du chœur et de la musique des loots entendus par les autres joueurs | Dev2 reçoit tout le show d'un ×10 de Dev |
| Plume d'offrande en croix | une seule face |
| Cercle d'invocation au niveau du sol de l'autel | capture sur bassin d'eau |
| Nouveau fond de l'écran de l'autel | capture |
| Flammes de l'autel recouvertes par l'eau | flammes nettes au-dessus d'un bassin |
| Lignes de 0,1 px des plans fins | cause trouvée (gonflement des cubes plats) |
| Loot de la forge retiré, tonneaux de la table remis à neuf | tonneau vidé puis rerempli |
| Doublons JEI des conteneurs d'essence | plus d'avertissement |
| Message « 1 feathers » | singulier |
| Reconnexion en plein vol (kick) | reprise en stationnaire, plus de kick |
| 5 structures de zorago | 4 fidèles à 150/150 états de blocs, la 5e voir plus bas |

## Fait, à valider par zorago (art et ressenti)

- **Autel draconique refait** (même taille, ailes symétriques, atlas pixel pour pixel) : `run/studio/shots/A_sheet2.png`.
- **Fontaine refaite** (octogone de 4 blocs, multibloc de 21 cellules, collision, menu, casse, pose refusée sans place) : `F_sheet.png`, `F_close.png`.
- **Furies** : ailes principales × 1,15, queue à 8 segments avec idles et marche : `run/studio/sheets/night_poses.png`, `light_poses.png`.
- Ressenti du frein à 10 % et des nouvelles touches.

## Reste ouvert

- **/tp en vol sur serveur** : le serveur signale « moved too quickly » à chaque `/tp` (même pour un joueur sans ailes) et Dev a été expulsé deux fois pour « floating » juste après un `/tp` ; pas reproduit ensuite. À surveiller avec les portails, perles et mods de téléportation du pack.
- **Sanctuaire d'invocation et portail de l'End** : posé par commande avant toute visite de l'End, le portail de sortie du dragon s'est construit sur l'autel (le sanctuaire est centré en x 0, z 0). En jeu normal le portail existe déjà à la 1re mort du dragon ; à vérifier avec YUNG's Better End Island.
- La « musique finale » que zorago n'a pas entendue n'a pas été reproduite : `music_burst` et `music_loot` jouent chez l'invocateur et chez le spectateur.
- Non testé : le pack complet (bloc S), shaderpacks et Distant Horizons, la casse du compresseur à la main, sauvegarde complète (pitié, historique, jauges) après redémarrage.

## Fichiers ajoutés pour la release

`LICENSE` (MIT), `CHANGELOG.md`, `docs/CURSEFORGE.md` (brouillon de page), `README.md` mis à jour (version, contrôles, liste avant release).
