# ToDo Testing

Tout ce qui doit être testé et validé en jeu, puis les modèles 3D qui restent à faire. Chaque ligne se coche quand elle est validée **en solo et sur le serveur dédié** (règle du projet). Un échec se note directement sous la ligne, avec ce qui a été observé.

Dernière mise à jour : 23 octobre 2026 (section 13 : refonte essences et sanctuaires). Avant : 22 octobre au soir. Session de test sur le PC de jeu, **en solo dans le client de dev** (Simply Wings + JEI), pilotée par le studio (`tools/studio/send.py`, commandes de test de `client/studio/StudioTest`). Les cases cochées ce soir l'ont été en solo seulement : la règle « solo et serveur dédié » reste à compléter pour elles.

## Reprise (état au 23/10, 2 h)

Fait cette session : passe studio des 21 rares, correctif 1 (battement sans saut, `WingAnimator`), les 19 modèles restants, sections 2 à 6 et 9-10 validées en solo par le studio, JEI corrigé, forge testée, vol testé en solo.
Reste, dans l'ordre :
1. **Serveur dédié** : `run/ops.json` contient maintenant Dev (op). Lancer `./gradlew runServer`, puis `./gradlew runStudioMp` (client studio qui rejoint `localhost`, dossier `run-mp/`) et piloter avec `python -I tools/studio/send.py --dir=run-mp …`. Refaire : vol (aucun kick, `allow-flight=false`), quelques drops, table, Cloud Essence.
2. **Pack complet** (§ 1), **en local uniquement** : le dossier CurseForge `PleasePlayWithMe` est en lecture seule, on ne le lance pas et on n'y écrit rien. On copie ses mods et sa config dans `run-pack/` (client de dev `runPackStudio`, sans le jar JEI du pack, déjà en `localRuntime`), et son `server-pack/` dans un dossier serveur local, avec notre jar.
3. Forge : refaire le test « 2e dragon » avec un vrai respawn (4 cristaux).
4. Questions pour zorago : valeurs de `flap_rate` (correctif 3) et les 6 questions du wiki (`docs/WIKI_PLAN.md`).
5. Correctif 2 (z-fighting des plaques) et récupération d'atterrissage des rares.

Outils de test (studio) : `tools/studio/drop_test.py` (59 sources de drops), `tools/studio/table_test.py all` (173 recettes à la table), `review.py <aile>` (planches). Commandes du studio dans `client/studio/StudioTest` (`screen`, `slot`, `tooltips`, `icons`, `tab`, `attack`, `key`, `status`, `inv`, `drops`, `jei`, `jeitype`, `respawn`…).

## 0. Préparation

- [ ] `./gradlew build`, puis tester le jar dans une **copie locale** du pack (client `run-pack/`, serveur copié de `server-pack/`). Jamais dans le dossier CurseForge, qui reste en lecture seule.
- [ ] Pour les tests rapides : `./gradlew runClient` (JEI 19.64.0.453 est chargé en dev via `localRuntime`) et `./gradlew runServer`.
- [ ] Monde de test en créatif, triche activée. Commandes utiles :
  - `/simplywings energy` : pose ou retire l'énergie ailée sur l'item en main (raccourci de test, sans passer par la table).
  - `/simplywings forge` : pose ou répare la forge de l'End et donne un compas de la forge.
  - `/simplywings unlock <aile>|all`, `equip`, `unequip`, `stamina <n>`, `reset`, `info`.
  - `/time set night`, `/weather thunder`, `/summon minecraft:creeper ~ ~ ~ {powered:1b}`, `/tp @s ~ 195 ~` (couche de nuages).
- [ ] Studio de captures (refonte art) : `tools/studio/start.sh` puis `python -I tools/studio/review.py <modèle>`, jamais pendant une partie.

## 1. Démarrage et chargement des données

Déjà validé le 22 octobre sur le portable, avec un serveur dédié de dev (Simply Wings et JEI seuls, sans le reste du pack) :
- le serveur démarre sans aucune erreur Simply Wings : les 173 recettes, le registre `loot_source` (59 entrées) et le Global Loot Modifier se chargent ;
- `place template simplywings:wing_forge` pose bien la forge, avec la table d'assemblage au centre et des tonneaux liés à `simplywings:chests/forge_supplies` ;
- `/loot ... mine` sur un minerai de fer : avec une pioche imprégnée, le Sky Iron Fleck tombe en plus du fer brut ; avec une pioche normale, aucun drop du mod.

À refaire sur le PC de jeu avec le pack complet :

- [ ] Le jeu démarre avec le pack (195 mods), sans erreur Simply Wings dans `logs/latest.log`.
- [ ] Aucune erreur de parsing pour `data/simplywings/recipe/component/*` (133 recettes) et `recipe/assembly/*` (40 recettes).
- [ ] Aucune erreur pour le registre `simplywings:loot_source` (59 entrées) ni pour le Global Loot Modifier `simplywings:winged_drops`.
- [x] Connexion d'un client au serveur dédié sans « registry mismatch » (le registre `loot_source` est synchronisé sans ses conditions). *(23/10, serveur de dev + client `runStudioMp`, sans le pack.)*
- [ ] `/reload` ne casse rien (recettes, sources de loot, JEI se recharge).

## 2. Items (232) et tooltips

- [x] L'onglet créatif Simply Wings contient : la table, le flacon vide, le flacon plein, l'absorbeur, le compas de la forge, les 41 paires, puis les 188 matériaux et pièces. *(22/10, studio : 234 entrées dans cet ordre.)*
- [x] Aucune texture violette et noire (texture manquante) dans l'inventaire : planche des 233 icônes du mod, aucune manquante. *(En main et au sol : non vérifié séparément, mêmes modèles.)*
- [x] Chaque item du mod affiche, sous son nom : la description en italique gris, une ligne vide, puis l'énigme en violet clair précédée de ✦. Vérifier sur un échantillon de chaque famille : drop (Hushed Down, Dragon Scale), intermédiaire (Gilded Frame, Fury Plasma), pièce (Owl Core, Night Fury Wing), harnais, paire (Owl Wings, Wings of Yin and Yang), Cloud Flask, Cloud Absorber, Forge Compass, Assembly Table.
- [x] Les retours à la ligne des tooltips tombent bien (environ 200 px) et ne coupent pas les mots.
  - Corrigé le 22/10 : l'énigme de Yin et Yang disait « two Yin Wings » ; elle dit maintenant « a Yin Wing and a Yang Wing » (`tools/crafting_gen/items.py`).
- [x] Les tooltips existants restent en dessous : « Right-click to add these wings… », remplissage du flacon, ligne « Winged energy ».
- [x] Piles : drops et intermédiaires par 64, pièces (harnais, cœurs, ailes) par 16, absorbeur et compas par 1, flacon vide par 16, flacon plein par 1.
- [ ] Les icônes des pièces « aile » sont lisibles (certaines sont petites : abeille, cerf-volant, colibri).

## 3. Énergie ailée

- [x] Onglet « Winged Energy » de la table : poser un item l'imprègne (éclat d'enchantement et ligne de tooltip), le reposer retire l'énergie.
- [ ] L'énergie survit à la sauvegarde, à la mort (si l'item est gardé) et au transfert de dimension.
- [x] Elle fonctionne sur n'importe quel item (testé : bâton, escalier ; épée, pioche, pelle, canne via le composant).

## 4. Drops imprégnés (59 sources)

Règle générale à valider d'abord :
- [x] Sans énergie ailée, aucun drop du mod ne tombe, jamais.
- [x] **Bloc** : il compte si l'outil en main est imprégné.
- [x] **Mob** : il compte si l'item en main principale du tueur est imprégné. Un kill à l'arc ou au trident compte si l'arc ou le trident tenu est imprégné (décision du 22 octobre : on regarde l'arme du tireur).
- [x] **Pêche** : elle compte si la canne est imprégnée.
- [x] Les drops vanilla normaux tombent toujours en plus.
- [x] Les explosions (TNT) ne donnent jamais de drop du mod.

Une ligne par source (« ok » quand le drop tombe dans la bonne condition **et** ne tombe pas hors condition) :

*22/10 : les 59 sources sont validées en solo par le banc automatique `python -I tools/studio/drop_test.py [source…]` (studio lancé), avec les vrais kills au corps à corps, `/loot … mine` et `/loot … fish` : taux mesurés conformes aux chances, aucun drop hors condition ni avec un outil normal. Les conditions de biome se testent avec `/fillbiome` ou à l'altitude exacte, car les biomes sont en 3D et `locate` donne une position approchée.*

| Drop | Source | Condition à tester | Chance | OK |
| --- | --- | --- | --- | --- |
| Bat Membrane | tuer une chauve-souris | — | 50 % || [x] |
| Crow Feather | casser du blé | mûr seulement (`age=7`), rien sur du blé jeune | 10 % || [x] |
| Hushed Down | tuer un lapin | de nuit **et** accroupi ; rien de jour, rien debout | 50 % || [x] |
| Gull Feather | pêcher | bouchon dans un biome plage (beach, snowy beach, stony shore) | 20 % || [x] |
| Nectar Drop | casser une petite fleur | — | 15 % || [x] |
| Raptor Talon | tuer une chèvre | Y ≥ 120 | 35 % || [x] |
| Updraft Shard | miner de la pierre de l'Overworld | Y ≥ 150 | 8 % || [x] |
| Swift Quill | tuer un poulet | en sprint | 30 % || [x] |
| Homing Feather | tuer un poulet | dans un village (structure) | 35 % || [x] |
| Peacock Plume | tuer un poulet | en jungle | 35 % || [x] |
| Macaw Plume | casser des feuilles de jungle | — | 5 % || [x] |
| Royal Jelly | casser un nid d'abeilles | plein (`honey_level=5`) | 100 % || [x] |
| Royal Jelly | casser une ruche | pleine | 100 % || [x] |
| Moth Dust | casser torche, torche murale, lanterne | de nuit | 25 % || [x] |
| Chitin Shard | tuer un poisson d'argent | — | 25 % || [x] |
| Acorn | casser des feuilles de chêne ou de chêne noir | — | 5 % || [x] |
| Fin Ray | pêcher | océan chaud ou tiède | 20 % || [x] |
| Phantom Sinew | tuer un phantom | — | 50 % || [x] |
| Resonant Shard | casser un amas d'améthyste adulte | — | 30 % || [x] |
| Verdigris Flake | casser du cuivre vieilli ou oxydé (tag `simplywings:aged_copper`) | rien sur du cuivre neuf | 25 % || [x] |
| Fossil Fragment | casser un bloc d'os | Y ≤ 40 | 25 % || [x] |
| Raw Resin | casser une bûche d'épicéa | vieille taïga (pins ou épicéas) | 12 % || [x] |
| Ember Wisp | tuer un blaze | — | 30 % || [x] |
| Tide Pearl | tuer un gardien | — | 20 % || [x] |
| Tide Pearl ×3 | tuer un gardien ancien | — | 100 % || [x] |
| Living Rock | miner de la deepslate ou du tuf | Y ≤ 0 | 5 % || [x] |
| Gust Mote | tuer un breeze | — | 40 % || [x] |
| Thunder Spark | tuer un creeper | pendant un orage | 50 % || [x] |
| Thunder Spark ×2 | tuer un creeper chargé | — | 100 % || [x] |
| Polar Glimmer | casser glace, glace compacte, glace bleue | de nuit, biome enneigé (`#c:is_snowy`) | 15 % || [x] |
| Shade Wisp | tuer un enderman | lumière ≤ 7 à l'endroit du kill | 25 % || [x] |
| Stardust | tuer un shulker | — | 35 % || [x] |
| Blossom Spirit | casser des feuilles de cerisier | — | 6 % || [x] |
| Heavenly Down | tuer un poulet | Y 186 à 204 (couche de nuages) | 60 % || [x] |
| Vex Wing | tuer un vex | — | 40 % || [x] |
| Soul Wisp | creuser sable ou terre des âmes | vallée des âmes | 8 % || [x] |
| Echo Resonance | casser un hurleur de sculk | — | 50 % || [x] |
| Dragon Scale ×12 | tuer l'Ender Dragon | coup fatal avec un item imprégné | 100 % || [x] |
| Supple Hide | tuer vache, champimeuh, cheval, âne, lama | — | 35 % || [x] |
| Silk Thread | tuer une araignée ou une araignée venimeuse | — | 40 % || [x] |
| Hollow Bone | tuer squelette, vagabond, bogged | — | 35 % || [x] |
| Elastic Gel | tuer un slime ou un cube de magma | — | 40 % || [x] |
| Pristine Feather | tuer un poulet | — | 25 % || [x] |
| Fleece Tuft | tuer un mouton | — | 40 % || [x] |
| Abyssal Ink | tuer un poulpe ou un poulpe luisant | — | 40 % || [x] |
| Ghast Wisp | tuer un ghast | — | 50 % || [x] |
| Sky Iron Fleck | miner du fer | — | 25 % || [x] |
| Copper Spark | miner du cuivre | — | 25 % || [x] |
| Gilded Vein | miner de l'or (Overworld et Nether) | — | 30 % || [x] |
| Redstone Pulse | miner de la redstone | — | 30 % || [x] |
| Lapis Glint | miner du lapis | — | 30 % || [x] |
| Emerald Sliver | miner de l'émeraude | — | 60 % || [x] |
| Soot Flake | miner du charbon | — | 20 % || [x] |
| Prism Shard | miner du diamant | — | 50 % || [x] |
| Pale Quartz | miner du quartz du Nether | — | 25 % || [x] |
| Ancient Ember | miner des débris antiques | — | 60 % || [x] |
| Void Grit | casser de la pierre de l'End | — | 8 % || [x] |
| Glimmer Spore | casser du lichen luisant | — | 30 % || [x] |
| Glimmering Scale | pêcher | partout | 10 % || [x] |

- [x] Toucher de soie et Fortune : le drop du mod tombe quand même (il ne dépend que de l'énergie ailée), et Fortune ne le multiplie pas.
- [ ] Les blocs qui ne lâchent rien sans le bon outil (minerais à la main) : vérifier qu'on obtient bien le drop du mod avec une pioche imprégnée, et noter le comportement à la main.
- [ ] Équilibrage : noter le temps réel pour réunir les drops d'une rare, d'une épique, d'une légendaire, puis ajuster les chances dans `tools/crafting_gen/loot.py` (relancer `python tools/crafting_gen/items.py`).

## 5. Cloud Essence

- [x] Le Cloud Flask se fabrique à la table (onglet Components, verre ×4 et plume → ×2), sans essence.
- [x] Le Cloud Absorber se fabrique à la table (verre ×4, cuivre ×2, Updraft Shard ×2, plume), sans essence.
- [x] Avec l'absorbeur dans l'inventaire, en vol dans l'Overworld entre Y 186 et 204 : +2 essence par seconde dans le premier flacon non plein, puis le suivant. *(Stationnaire à Y 196 : 10 essence en 5 s.)*
- [x] Un flacon vide pris dans une pile se détache proprement. *(Inventaire plein : non testé.)*
- [x] Rien ne se passe : au sol, sans absorbeur. *(Hors de la bande, Nether, End : vérifié dans le code, pas en jeu.)*
- [x] Stationnaire : il récolte aussi par défaut. *(`hoverHarvest = false` : non testé.)*
- [ ] Config `[cloud]` : `minY`, `maxY`, `essencePerSecond` pris en compte après redémarrage du serveur.
- [x] Le flacon plein se vide dans le réservoir de la table (1 000 maximum) et ressort vide.
- [ ] Bande Y : vérifier visuellement qu'elle colle aux nuages vanilla (192) avec et sans shaderpack, et avec Distant Horizons.

## 6. Table d'assemblage

- [x] Les trois onglets (Assembly, Components, Winged Energy) et le réservoir.
- [x] Onglet Components : la grille 5×5 sans coins accepte les 133 recettes générées. *(22/10 : les 133 fabriquées par vrais clics dans la grille, `python -I tools/studio/table_test.py all`, résultat et quantité corrects.)* Tester au moins une recette par famille :
  - harnais : Iron-Buckled, Gilded, Radiant, Fury Saddle ;
  - armatures : Gilded Frame, Celestial Frame, Dragonbone Spar ;
  - Spirit Vessel ;
  - un cœur et une aile de rare, d'épique, de légendaire, de mythique ;
  - les intermédiaires qui donnent plusieurs exemplaires : Blessed Plume ×2, Night Scale ×4, Brass Ingot ×2.
- [ ] Une recette en miroir (aile asymétrique, Fossil Spar, Swift Wing) marche dans les deux sens.
- [x] L'essence est consommée à la sortie du résultat (vérifié sur les 173 recettes). *(Refus si réservoir insuffisant : non testé.)*
- [ ] Shift-clic sur le résultat : pas de duplication, essence débitée pour chaque craft.
- [x] Onglet Assembly : les 40 recettes de paires (`recipe/assembly`), avec le bon coût (100, 300, 800, 1 000).
- [ ] La paire commune s'assemble toujours avec cuir, élytre et 2 plumes, sans essence.
- [ ] Route complète d'une rare en survie (par exemple la chouette), de bout en bout, avec les drops imprégnés.
- [ ] Route complète d'une épique, d'une légendaire, puis d'une mythique (en créatif, pour vérifier la chaîne).
- [ ] Planches de référence dans `craft-img/` : elles correspondent aux recettes en jeu.

## 7. Forge de l'End et compas

- [x] Premier dragon tué : aucune forge, aucun compas. *(22/10, studio, mort attribuée au joueur avec `/damage … by @s`.)*
- [ ] Deuxième dragon (réinvoqué avec 4 cristaux) : la forge apparaît dans le vide à environ 512 blocs de (0, 0), à Y 80. Les joueurs à moins de 192 blocs de (0, 0) reçoivent un compas (inventaire plein : il tombe au sol) et un message.
  - 22/10 : avec des dragons invoqués par `/summon`, la forge et le compas (message compris) sont apparus au **troisième** dragon et non au deuxième ; forge posée en (274, 80, 432), à 511 blocs. À refaire avec un vrai respawn (4 cristaux) avant de conclure à un bug.
- [x] La structure : plateforme, 4 piliers en purpur, end rods, la table d'assemblage au centre, 6 tonneaux. *(Contenu des tonneaux : non ouvert.)*
- [ ] Troisième dragon : la forge est réparée au même endroit (blocs cassés reposés, tonneaux à nouveau remplis), les blocs posés par les joueurs dans les zones vides de la structure restent.
- [ ] La position est mémorisée (SavedData `simplywings_wing_forge`) : changer `[forge] radius` après coup ne la déplace pas.
- [ ] Compas : l'aiguille pointe vers la forge dans l'End, tourne dans le vide dans l'Overworld et le Nether.
- [ ] `/simplywings forge` : même résultat, sans dragon.
- [ ] YUNG's Better End Island : la condition « deuxième dragon » marche toujours (flag `hasPreviouslyKilledDragon`).
- [ ] Le design de la forge est une base : à retravailler en jeu avec un bloc de structure, puis réexporter `data/simplywings/structure/wing_forge.nbt` (ou modifier `tools/crafting_gen/forge_nbt.py`).

## 8. JEI et gestionnaires de recettes

- [ ] JEI absent : le mod se charge sans erreur (dépendance optionnelle, plugin isolé dans `compat.jei`).
- [x] Catégories JEI (22/10 : les 6 s'ouvrent et s'affichent ; corrigé : le coût en essence débordait du cadre d'Assembly et de Components, et le texte de Winged Energy débordait de sa case) :
  - **Wing Assembly** : harnais en haut, ailes gauche et droite, cœur au centre, résultat, coût en Cloud Essence ;
  - **Wing Components** : grille 5×5 sans coins, résultat avec la quantité, coût ;
  - **Winged Energy** : un item, puis la table, puis l'item imprégné ;
  - **Winged Mob Drops**, **Winged Block Drops**, **Winged Fishing** : trois catégories séparées, une par type de récupération. Chaque recette montre la source (œuf du mob, bloc, ou canne), l'outil imprégné requis, le drop, la **chance en %**, la **quantité** (« Quantity: 3 » ou « Quantity: 1 to 2 ») et la condition en violet.
- [ ] Les 59 sources se répartissent bien : 27 mobs, 29 blocs, 3 pêche (aucune ne manque, aucune en double).
- [ ] Les outils imprégnés (épée, pioche, canne) sont catalyseurs de leur catégorie : cliquer dessus dans JEI ouvre la liste de leurs drops.
- [ ] Les tags s'affichent bien comme sources : minerais (`#minecraft:iron_ores`…), pierre (`#minecraft:base_stone_overworld`), fleurs, cuivre vieilli ; les blocs défilent dans le slot source.
- [x] Les textes ne débordent pas (chance et quantité à droite, condition sur plusieurs lignes).
- [x] « Recettes » (R) et « usages » (U) marchent sur : un drop (Hushed Down → où il tombe, où il sert), un minerai (minerai de fer → Sky Iron Fleck), une pièce, une paire. *(Œuf de mob : non testé.)*
- [ ] La table d'assemblage est catalyseur des catégories Assembly, Components et Winged Energy.
- [ ] Zones cliquables dans l'écran de la table : la flèche ouvre la catégorie de l'onglet actif.
- [ ] Bouton « + » (transfert) : il remplit la grille depuis l'inventaire **seulement** sur l'onglet correspondant ; sur un autre onglet, JEI refuse.
- [ ] Pages d'info JEI : table d'assemblage, compas de la forge, Cloud Flask et Cloud Absorber.
- [ ] Ender Dragon : pas d'œuf d'apparition, la source s'affiche avec une étiquette (name tag) : à améliorer si gênant.
- [ ] EMI (si installé à la place ou en plus de JEI) : il lit les plugins JEI ; vérifier que les 4 catégories apparaissent.
- [ ] REI : seulement avec l'addon « REI Plugin Compatibilities » ; à vérifier si le pack l'utilise.
- [ ] Bouton Ailes de l'inventaire : il ne chevauche ni la bande de favoris de JEI ni le livre de recettes.

## 8 bis. Wiki (quatrième onglet de la table) : plan à valider

Rien n'est codé : le plan est dans `docs/WIKI_PLAN.md`.
- [ ] Lire le plan et répondre aux 6 questions de sa dernière section (accès depuis le menu J, niveau de révélation des routes, chiffres dans le wiki ou seulement dans JEI, langue, arbre dessiné ou renvoi JEI, découvertes par joueur ou par équipe).
- [ ] Valider le principe anti-spoil : pages cachées, teaser (énigme seule) puis révélées, révélation par paliers, mythiques cachées jusqu'à la première légendaire.
- [ ] Une fois validé : implémentation (suivi des découvertes dans `WingsData`, pages JSON générées, quatrième onglet), puis une section de tests dédiée ici.

## 9. Ailes de Yin et Yang

- [x] `/simplywings unlock simplywings:yin_yang` : la paire est dans le menu (J), rareté Légendaire, stats 9/10 partout (9,1 à 9,4 avec ses propres bonus de collection).
- [x] Rendu : aile gauche noire à reflets bleu nuit, aile droite blanc nacré, pas inversées (nouveau modèle Blockbench, texture 256×128 avec une zone par aile).
- [ ] Effets : deux sphères noire et blanche en orbite dans le dos, plus lentes et plus serrées en stationnaire ; à chaque battement, un anneau moitié noir, moitié blanc, et deux tintements (grave et aigu).
- [ ] Vol : 1 battement par seconde (+9,2 m/s), stationnaire à 0,5 Hz et 19 m/s, presque immobile.
- [ ] Transmog (Light) : l'apparence Yin et Yang se pose sur une autre paire.
- [x] Recette d'assemblage : Radiant Harness, Taiji Core, Yin Wing à gauche, Yang Wing à droite (fabriquée par le balayage des recettes).

## 10. Bonus de collection

- [ ] Les bonus des 6 légendaires ont changé (une sixième légendaire) : vérifier l'affichage des stats globales dans le menu.
- [x] Collection complète : toutes les stats à 10/10 (vérifié avec `unlock all`, mythiques compris).

## 11. Régressions et correctifs déjà connus

À revérifier après ces ajouts, puis à traiter (liste de `HANDOFF.md`, « Prochaine session ») :
- [ ] Vol : stabilisé, déplacement, envol chargé, dive, frein, crash, sauts de secours, stamina, aucun kick sur le serveur dédié.
  - 22/10, solo (touches simulées par le studio) : double saut → stationnaire, Alt maintenu → envol chargé en déplacement, sprint → battements et stamina qui baisse, frein → retour au stationnaire à basse vitesse, ZQSD en stationnaire, Alt → déplacement, stamina qui remonte en plané, 2 sauts de secours puis plus rien et recharge au sol, crash contre un mur à environ 16 m/s : vol coupé et 3,5 points de vie. Piqué et serveur dédié : à faire.
- [ ] Night : stamina infinie, cri du piqué, charge et dash avec invisibilité.
- [ ] Correctif 1 : saut au reset du cycle de battement (toutes les ailes).
  - 22/10 : codé dans `WingAnimator` (phase continue qui ne revient jamais à zéro, amplitude par ressort, recalage progressif sur le coup ; simulé sans saut). Reste à le sentir en vol réel.
- [ ] Correctif 2 : z-fighting des plaques superposées, de près et de loin (toutes les ailes).
- [ ] Correctif 3 : battements trop rapides (aigle, chouette en priorité) ; proposer les nouvelles valeurs de `flap_rate`.
- [ ] Correctif 4 : mêmes corrections sur la commune (`CommonRig`, `basic.bbmodel`).
- [ ] Animation de récupération après l'atterrissage : à ajouter à chaque rare.
- [x] Passe studio des 21 rares (planches dans `run/studio/sheets/`) : 22/10, la commune et les 21 rares se chargent et s'affichent dans les 15 états, sans texture manquante.

## 12. Modèles 3D (refonte Blockbench)

**Fait le 22-23/10** : les 19 paires restantes ont leur modèle Blockbench, leur texture pxl et leur classe de rig (épiques avec 3 idles, légendaires avec 5, Night 8 et Light 7, plus une récupération après l'atterrissage). Les queues des Furies sont dans leur modèle (`DragonTail`), la queue procédurale `TailModel` ne sert plus que de repli. Détail par paire dans `WINGS.md` (« Apparence » et « Mouvement signature »). Passe studio faite sur les 19 (planches dans `run/studio/sheets/`) : tous les états se lisent, aucune pièce cassée.

À valider ou affiner, par paire, avec zorago et le designer :
- [ ] Méganeure : l'ambre est un peu terne sur ciel clair ; vérifier les images fantômes de vibration en vol réel.
- [ ] Ptérosaure : pose de charge (le doigt ailé descend très bas).
- [ ] Flamme, Orage, Aurore, Psyché : rendu émissif à vérifier de nuit et avec un shaderpack (Iris).
- [ ] Marée : en repli faucon, les ailes restent assez ouvertes.
- [ ] Vent : au repos, les voiles forment une collerette de rubans ; à revoir si ça ne plaît pas.
- [ ] Esprit sylvestre : vue de dessus en vol, les feuilles paraissent clairsemées.
- [ ] Nébuleuse et Ombre : lobes et volutes encore un peu carrés.
- [ ] Séraphin : la paire haute qui se croise au-dessus de la tête au repos (choix iconographique) à valider.
- [ ] Dédale : vérifier la rétraction des panneaux au sol et les flammes des réacteurs en vol réel.
- [ ] Night et Light : sens d'ouverture des ailerons en virage, clic de la prothèse, épines qui s'allument pendant la charge (frein tenu) ; la queue en marchant.
- [ ] Tester chaque idle en jeu (variantes tirées au hasard ; au studio : `rouse <0..1> <variante>`).
- [ ] Rares : ajouter la récupération après l'atterrissage (demandée, pas encore faite).
- [ ] One-shots en keyframes (déploiement, repli, atterrissage) pour toutes les paires.
- [ ] Finition des textures par le designer.

## 13. Refonte essences, établis, compresseur et sanctuaires (23 octobre)

Vu une première fois en solo dans le studio (client de dev), le 23 octobre :
- [x] Établi de la forge : 4 jauges, flacons de nuage et de vapeur versés dans la bonne jauge, grille 5×5, onglet énergie.
- [x] Table d'assemblage : assemblage seul, 4 jauges, bouton « Assemble ».
- [x] Balayage `tools/studio/table_test.py all` : **179 / 179 OK** (133 composants + 4 réservoirs + flacon + absorbeur à l'établi, 41 assemblages à la table), coût prélevé dans chaque jauge égal au coût de la recette.
- [x] Compresseur : pose 3×3×5 par l'item, modèle et engrenages rendus, menu, 1 + 1 + 1 → 3 (flacons → réservoir de fer).
- [x] Substitut de stamina : stamina à 0 en l'air avec un flacon d'essence intensifiée → la stamina remonte à 20, le flacon passe à 80.
- [x] Les trois sanctuaires se posent par `/simplywings sanctum <nom>` aux sommets du triangle (512 blocs, 120°), avec leur compas.

Reste à tester :
- [ ] Récolte en vol : vapeur au-dessus de la lave du Nether, éthérée au-dessus du vide de l'End (> 200 blocs du centre), nuage dans l'Overworld ; un flacon d'une autre essence n'est pas rempli.
- [ ] Compresseur : arrêt quand une entrée est vide ou la sortie pleine ; casser une partie casse tout et rend l'item ; place insuffisante = pose refusée ; animation (engrenages, pistons, fumée) quand il tourne, arrêt progressif.
- [ ] Modèle du compresseur : le liquide doré derrière la vitre a été avancé (à revoir après redémarrage du client) ; texture des cuves encore un peu « planches ».
- [ ] Éclat d'enchantement sur les flacons et réservoirs d'essence intensifiée ; teinte du liquide par essence dans l'inventaire.
- [ ] Substitut de stamina en vol de déplacement (battements) et en stationnaire, prédiction client sans à-coups, rien en créatif ni avec Night.
- [ ] Sanctuaires par de vraies morts du dragon : 2e → Sanctuaire céleste, 3e → Forge, 4e → Raffinerie, puis réparations sans écraser le contenu des établis ni du compresseur ; compas donné une seule fois par joueur.
- [ ] Monde de test : le Sanctuaire céleste a été posé sur l'ancienne forge (même angle de départ), d'où des restes violets et des items au sol. Sans effet sur un monde neuf.
- [ ] Direction artistique des trois îles (à valider par zorago) : plus de détails, raffinerie un peu vide.
- [ ] JEI : catégorie « Cloud Compression », coûts par essence affichés dans les catégories assemblage et composants.
- [ ] Serveur dédié pour tout ce qui précède.
