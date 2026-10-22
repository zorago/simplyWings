# ToDo Testing

Tout ce qui doit être testé et validé en jeu, puis les modèles 3D qui restent à faire. Chaque ligne se coche quand elle est validée **en solo et sur le serveur dédié** (règle du projet). Un échec se note directement sous la ligne, avec ce qui a été observé.

Dernière mise à jour : 22 octobre 2026. Rien de ce fichier n'a encore été testé en jeu avec un client : tout a été écrit et compilé sur le PC portable. Seul un serveur dédié de dev a tourné, pour valider les données, la structure et un drop (voir § 1).

## 0. Préparation

- [ ] `./gradlew build`, puis copier `build/libs/simplywings-*.jar` dans `A:\CurseForge\Instances\PleasePlayWithMe\mods` (retirer l'ancien jar).
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
- [ ] Connexion d'un client au serveur dédié sans « registry mismatch » (le registre `loot_source` est synchronisé sans ses conditions).
- [ ] `/reload` ne casse rien (recettes, sources de loot, JEI se recharge).

## 2. Items (232) et tooltips

- [ ] L'onglet créatif Simply Wings contient : la table, le flacon vide, le flacon plein, l'absorbeur, le compas de la forge, les 41 paires, puis les 188 matériaux et pièces.
- [ ] Aucune texture violette et noire (texture manquante), ni dans l'inventaire, ni en main, ni au sol.
- [ ] Chaque item du mod affiche, sous son nom : la description en italique gris, une ligne vide, puis l'énigme en violet clair précédée de ✦. Vérifier sur un échantillon de chaque famille : drop (Hushed Down, Dragon Scale), intermédiaire (Gilded Frame, Fury Plasma), pièce (Owl Core, Night Fury Wing), harnais, paire (Owl Wings, Wings of Yin and Yang), Cloud Flask, Cloud Absorber, Forge Compass, Assembly Table.
- [ ] Les retours à la ligne des tooltips tombent bien (environ 200 px) et ne coupent pas les mots.
- [ ] Les tooltips existants restent en dessous : « Right-click to add these wings… », remplissage du flacon, ligne « Winged energy ».
- [ ] Piles : drops et intermédiaires par 64, pièces (harnais, cœurs, ailes) par 16, absorbeur et compas par 1, flacon vide par 16, flacon plein par 1.
- [ ] Les icônes des pièces « aile » sont lisibles (certaines sont petites : abeille, cerf-volant, colibri).

## 3. Énergie ailée

- [ ] Onglet « Winged Energy » de la table : poser un item l'imprègne (éclat d'enchantement et ligne de tooltip), le reposer retire l'énergie.
- [ ] L'énergie survit à la sauvegarde, à la mort (si l'item est gardé) et au transfert de dimension.
- [ ] Elle fonctionne sur n'importe quel item (escalier, bâton, épée, pioche, canne, arc).

## 4. Drops imprégnés (59 sources)

Règle générale à valider d'abord :
- [ ] Sans énergie ailée, aucun drop du mod ne tombe, jamais.
- [ ] **Bloc** : il compte si l'outil en main est imprégné.
- [ ] **Mob** : il compte si l'item en main principale du tueur est imprégné. Un kill à l'arc ou au trident compte si l'arc ou le trident tenu est imprégné (décision du 22 octobre : on regarde l'arme du tireur).
- [ ] **Pêche** : elle compte si la canne est imprégnée.
- [ ] Les drops vanilla normaux tombent toujours en plus.
- [ ] Les explosions (TNT, creeper) ne donnent jamais de drop du mod.

Une ligne par source (« ok » quand le drop tombe dans la bonne condition **et** ne tombe pas hors condition) :

| Drop | Source | Condition à tester | Chance | OK |
| --- | --- | --- | --- | --- |
| Bat Membrane | tuer une chauve-souris | — | 50 % | [ ] |
| Crow Feather | casser du blé | mûr seulement (`age=7`), rien sur du blé jeune | 10 % | [ ] |
| Hushed Down | tuer un lapin | de nuit **et** accroupi ; rien de jour, rien debout | 50 % | [ ] |
| Gull Feather | pêcher | bouchon dans un biome plage (beach, snowy beach, stony shore) | 20 % | [ ] |
| Nectar Drop | casser une petite fleur | — | 15 % | [ ] |
| Raptor Talon | tuer une chèvre | Y ≥ 120 | 35 % | [ ] |
| Updraft Shard | miner de la pierre de l'Overworld | Y ≥ 150 | 8 % | [ ] |
| Swift Quill | tuer un poulet | en sprint | 30 % | [ ] |
| Homing Feather | tuer un poulet | dans un village (structure) | 35 % | [ ] |
| Peacock Plume | tuer un poulet | en jungle | 35 % | [ ] |
| Macaw Plume | casser des feuilles de jungle | — | 5 % | [ ] |
| Royal Jelly | casser un nid d'abeilles | plein (`honey_level=5`) | 100 % | [ ] |
| Royal Jelly | casser une ruche | pleine | 100 % | [ ] |
| Moth Dust | casser torche, torche murale, lanterne | de nuit | 25 % | [ ] |
| Chitin Shard | tuer un poisson d'argent | — | 25 % | [ ] |
| Acorn | casser des feuilles de chêne ou de chêne noir | — | 5 % | [ ] |
| Fin Ray | pêcher | océan chaud ou tiède | 20 % | [ ] |
| Phantom Sinew | tuer un phantom | — | 50 % | [ ] |
| Resonant Shard | casser un amas d'améthyste adulte | — | 30 % | [ ] |
| Verdigris Flake | casser du cuivre vieilli ou oxydé (tag `simplywings:aged_copper`) | rien sur du cuivre neuf | 25 % | [ ] |
| Fossil Fragment | casser un bloc d'os | Y ≤ 40 | 25 % | [ ] |
| Raw Resin | casser une bûche d'épicéa | vieille taïga (pins ou épicéas) | 12 % | [ ] |
| Ember Wisp | tuer un blaze | — | 30 % | [ ] |
| Tide Pearl | tuer un gardien | — | 20 % | [ ] |
| Tide Pearl ×3 | tuer un gardien ancien | — | 100 % | [ ] |
| Living Rock | miner de la deepslate ou du tuf | Y ≤ 0 | 5 % | [ ] |
| Gust Mote | tuer un breeze | — | 40 % | [ ] |
| Thunder Spark | tuer un creeper | pendant un orage | 50 % | [ ] |
| Thunder Spark ×2 | tuer un creeper chargé | — | 100 % | [ ] |
| Polar Glimmer | casser glace, glace compacte, glace bleue | de nuit, biome enneigé (`#c:is_snowy`) | 15 % | [ ] |
| Shade Wisp | tuer un enderman | lumière ≤ 7 à l'endroit du kill | 25 % | [ ] |
| Stardust | tuer un shulker | — | 35 % | [ ] |
| Blossom Spirit | casser des feuilles de cerisier | — | 6 % | [ ] |
| Heavenly Down | tuer un poulet | Y 186 à 204 (couche de nuages) | 60 % | [ ] |
| Vex Wing | tuer un vex | — | 40 % | [ ] |
| Soul Wisp | creuser sable ou terre des âmes | vallée des âmes | 8 % | [ ] |
| Echo Resonance | casser un hurleur de sculk | — | 50 % | [ ] |
| Dragon Scale ×12 | tuer l'Ender Dragon | coup fatal avec un item imprégné | 100 % | [ ] |
| Supple Hide | tuer vache, champimeuh, cheval, âne, lama | — | 35 % | [ ] |
| Silk Thread | tuer une araignée ou une araignée venimeuse | — | 40 % | [ ] |
| Hollow Bone | tuer squelette, vagabond, bogged | — | 35 % | [ ] |
| Elastic Gel | tuer un slime ou un cube de magma | — | 40 % | [ ] |
| Pristine Feather | tuer un poulet | — | 25 % | [ ] |
| Fleece Tuft | tuer un mouton | — | 40 % | [ ] |
| Abyssal Ink | tuer un poulpe ou un poulpe luisant | — | 40 % | [ ] |
| Ghast Wisp | tuer un ghast | — | 50 % | [ ] |
| Sky Iron Fleck | miner du fer | — | 25 % | [ ] |
| Copper Spark | miner du cuivre | — | 25 % | [ ] |
| Gilded Vein | miner de l'or (Overworld et Nether) | — | 30 % | [ ] |
| Redstone Pulse | miner de la redstone | — | 30 % | [ ] |
| Lapis Glint | miner du lapis | — | 30 % | [ ] |
| Emerald Sliver | miner de l'émeraude | — | 60 % | [ ] |
| Soot Flake | miner du charbon | — | 20 % | [ ] |
| Prism Shard | miner du diamant | — | 50 % | [ ] |
| Pale Quartz | miner du quartz du Nether | — | 25 % | [ ] |
| Ancient Ember | miner des débris antiques | — | 60 % | [ ] |
| Void Grit | casser de la pierre de l'End | — | 8 % | [ ] |
| Glimmer Spore | casser du lichen luisant | — | 30 % | [ ] |
| Glimmering Scale | pêcher | partout | 10 % | [ ] |

- [ ] Toucher de soie et Fortune : le drop du mod tombe quand même (il ne dépend que de l'énergie ailée).
- [ ] Les blocs qui ne lâchent rien sans le bon outil (minerais à la main) : vérifier qu'on obtient bien le drop du mod avec une pioche imprégnée, et noter le comportement à la main.
- [ ] Équilibrage : noter le temps réel pour réunir les drops d'une rare, d'une épique, d'une légendaire, puis ajuster les chances dans `tools/crafting_gen/loot.py` (relancer `python tools/crafting_gen/items.py`).

## 5. Cloud Essence

- [ ] Le Cloud Flask se fabrique à la table (onglet Components, verre ×4 et plume → ×2), sans essence.
- [ ] Le Cloud Absorber se fabrique à la table (verre ×4, cuivre ×2, Updraft Shard ×2, plume), sans essence.
- [ ] Avec l'absorbeur dans l'inventaire, en vol dans l'Overworld entre Y 186 et 204 : +2 essence par seconde dans le premier flacon non plein, puis le suivant.
- [ ] Un flacon vide pris dans une pile de 16 se détache proprement ; si l'inventaire est plein, il tombe au sol.
- [ ] Rien ne se passe : au sol, hors de la bande, sans absorbeur, dans le Nether ou l'End.
- [ ] Stationnaire : il récolte aussi par défaut ; `[cloud] hoverHarvest = false` le coupe.
- [ ] Config `[cloud]` : `minY`, `maxY`, `essencePerSecond` pris en compte après redémarrage du serveur.
- [ ] Le flacon plein se vide dans le réservoir de la table (1 000 maximum) et ressort vide.
- [ ] Bande Y : vérifier visuellement qu'elle colle aux nuages vanilla (192) avec et sans shaderpack, et avec Distant Horizons.

## 6. Table d'assemblage

- [ ] Les trois onglets (Assembly, Components, Winged Energy) et le réservoir.
- [ ] Onglet Components : la grille 5×5 sans coins accepte les 133 recettes générées. Tester au moins une recette par famille :
  - harnais : Iron-Buckled, Gilded, Radiant, Fury Saddle ;
  - armatures : Gilded Frame, Celestial Frame, Dragonbone Spar ;
  - Spirit Vessel ;
  - un cœur et une aile de rare, d'épique, de légendaire, de mythique ;
  - les intermédiaires qui donnent plusieurs exemplaires : Blessed Plume ×2, Night Scale ×4, Brass Ingot ×2.
- [ ] Une recette en miroir (aile asymétrique, Fossil Spar, Swift Wing) marche dans les deux sens.
- [ ] L'essence est consommée à la sortie du résultat ; le craft est refusé si le réservoir est insuffisant (coût affiché en rouge).
- [ ] Shift-clic sur le résultat : pas de duplication, essence débitée pour chaque craft.
- [ ] Onglet Assembly : les 40 recettes de paires (`recipe/assembly`), avec le bon coût (100, 300, 800, 1 000).
- [ ] La paire commune s'assemble toujours avec cuir, élytre et 2 plumes, sans essence.
- [ ] Route complète d'une rare en survie (par exemple la chouette), de bout en bout, avec les drops imprégnés.
- [ ] Route complète d'une épique, d'une légendaire, puis d'une mythique (en créatif, pour vérifier la chaîne).
- [ ] Planches de référence dans `craft-img/` : elles correspondent aux recettes en jeu.

## 7. Forge de l'End et compas

- [ ] Premier dragon tué : aucune forge, aucun compas.
- [ ] Deuxième dragon (réinvoqué avec 4 cristaux) : la forge apparaît dans le vide à environ 512 blocs de (0, 0), à Y 80. Les joueurs à moins de 192 blocs de (0, 0) reçoivent un compas (inventaire plein : il tombe au sol) et un message.
- [ ] La structure : plateforme, 4 piliers en purpur, end rods, la table d'assemblage au centre, 6 tonneaux remplis de plumes, cuir et ficelle.
- [ ] Troisième dragon : la forge est réparée au même endroit (blocs cassés reposés, tonneaux à nouveau remplis), les blocs posés par les joueurs dans les zones vides de la structure restent.
- [ ] La position est mémorisée (SavedData `simplywings_wing_forge`) : changer `[forge] radius` après coup ne la déplace pas.
- [ ] Compas : l'aiguille pointe vers la forge dans l'End, tourne dans le vide dans l'Overworld et le Nether.
- [ ] `/simplywings forge` : même résultat, sans dragon.
- [ ] YUNG's Better End Island : la condition « deuxième dragon » marche toujours (flag `hasPreviouslyKilledDragon`).
- [ ] Le design de la forge est une base : à retravailler en jeu avec un bloc de structure, puis réexporter `data/simplywings/structure/wing_forge.nbt` (ou modifier `tools/crafting_gen/forge_nbt.py`).

## 8. JEI et gestionnaires de recettes

- [ ] JEI absent : le mod se charge sans erreur (dépendance optionnelle, plugin isolé dans `compat.jei`).
- [ ] Catégories JEI :
  - **Wing Assembly** : harnais en haut, ailes gauche et droite, cœur au centre, résultat, coût en Cloud Essence ;
  - **Wing Components** : grille 5×5 sans coins, résultat avec la quantité, coût ;
  - **Winged Energy** : un item, puis la table, puis l'item imprégné ;
  - **Winged Mob Drops**, **Winged Block Drops**, **Winged Fishing** : trois catégories séparées, une par type de récupération. Chaque recette montre la source (œuf du mob, bloc, ou canne), l'outil imprégné requis, le drop, la **chance en %**, la **quantité** (« Quantity: 3 » ou « Quantity: 1 to 2 ») et la condition en violet.
- [ ] Les 59 sources se répartissent bien : 27 mobs, 29 blocs, 3 pêche (aucune ne manque, aucune en double).
- [ ] Les outils imprégnés (épée, pioche, canne) sont catalyseurs de leur catégorie : cliquer dessus dans JEI ouvre la liste de leurs drops.
- [ ] Les tags s'affichent bien comme sources : minerais (`#minecraft:iron_ores`…), pierre (`#minecraft:base_stone_overworld`), fleurs, cuivre vieilli ; les blocs défilent dans le slot source.
- [ ] Les textes ne débordent pas (chance et quantité à droite, condition sur plusieurs lignes).
- [ ] « Recettes » (R) et « usages » (U) marchent sur : un drop (Hushed Down → où il tombe, où il sert), un minerai (minerai de fer → Sky Iron Fleck), un œuf de mob, une pièce, une paire.
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

- [ ] `/simplywings unlock simplywings:yin_yang` : la paire est dans le menu (J), rareté Légendaire, stats 9/10 partout.
- [ ] Rendu : aile gauche noire à reflets bleu nuit, aile droite blanc nacré (texture 128×64, une moitié par côté). Vérifier qu'elles ne sont pas inversées.
- [ ] Effets : deux sphères noire et blanche en orbite dans le dos, plus lentes et plus serrées en stationnaire ; à chaque battement, un anneau moitié noir, moitié blanc, et deux tintements (grave et aigu).
- [ ] Vol : 1 battement par seconde (+9,2 m/s), stationnaire à 0,5 Hz et 19 m/s, presque immobile.
- [ ] Transmog (Light) : l'apparence Yin et Yang se pose sur une autre paire.
- [ ] Recette d'assemblage : Radiant Harness, Taiji Core, Yin Wing à gauche, Yang Wing à droite (l'ordre gauche et droite compte).

## 10. Bonus de collection

- [ ] Les bonus des 6 légendaires ont changé (une sixième légendaire) : vérifier l'affichage des stats globales dans le menu.
- [ ] Collection complète hors mythiques : toutes les stats à 10/10.

## 11. Régressions et correctifs déjà connus

À revérifier après ces ajouts, puis à traiter (liste de `HANDOFF.md`, « Prochaine session ») :
- [ ] Vol : stabilisé, déplacement, envol chargé, dive, frein, crash, sauts de secours, stamina, aucun kick sur le serveur dédié.
- [ ] Night : stamina infinie, cri du piqué, charge et dash avec invisibilité.
- [ ] Correctif 1 : saut au reset du cycle de battement (toutes les ailes).
  - 22/10 : codé dans `WingAnimator` (phase continue qui ne revient jamais à zéro, amplitude par ressort, recalage progressif sur le coup ; simulé sans saut). Reste à le sentir en vol réel.
- [ ] Correctif 2 : z-fighting des plaques superposées, de près et de loin (toutes les ailes).
- [ ] Correctif 3 : battements trop rapides (aigle, chouette en priorité) ; proposer les nouvelles valeurs de `flap_rate`.
- [ ] Correctif 4 : mêmes corrections sur la commune (`CommonRig`, `basic.bbmodel`).
- [ ] Animation de récupération après l'atterrissage : à ajouter à chaque rare.
- [x] Passe studio des 21 rares (planches dans `run/studio/sheets/`) : 22/10, la commune et les 21 rares se chargent et s'affichent dans les 15 états, sans texture manquante.

## 12. Modèles 3D restants (refonte Blockbench)

Fait : la commune et les 21 rares (un `.bbmodel` par paire dans `assets/simplywings/rigs/` et une classe `render/rig/wings/<Paire>Rig`), en attente de la passe studio.

Reste à faire : les épiques, les légendaires et les mythiques. Aujourd'hui, ils utilisent l'ancien modèle procédural (`render/WingModel` + `anim/WingStyle`), avec des plaques simples. Pour chacun : un `.bbmodel` à taille réelle sur le rig commun `shoulder` / `arm` / `forearm` / `hand`, une texture, une classe `<Paire>Rig` enregistrée dans `WingRigs`, puis la boucle visuelle (captures Blockbench et studio). Relire `docs/SOURCES.md` et le skill `simplywings-wing-style` avant chaque paire.

Animations demandées par palier : **épique** 3 idles tirées au hasard, **légendaire** 5 idles, **mythique** tout ce qu'on trouve ; plus une récupération après l'atterrissage.

### Épiques (11)

| Paire | Modèle actuel | Cible (`WINGS.md`) | Points d'attention | Fait |
| --- | --- | --- | --- | --- |
| Meganeura `ancient` | `DRAGONFLY`, 2 ailes par côté | 4 ailes de libellule d'un seul bloc, membranes ambre translucides veinées de brun fossile, à plat, en V au repos | translucide, vibration continue (20 battements/s), effets fossiles | [ ] |
| Pterosaur `ptero` | `PTERO`, 3 segments | membrane de cuir ocre, avant-bras court, 4e doigt immense jusqu'à la pointe, fibres sombres | un seul doigt porteur, membrane jusqu'à la cuisse | [ ] |
| Flame `fire` | `FIRE` | aile de flammes, jaune près du dos, orange puis rouge, langues de flamme au bord | émissive (ETHEREAL), langues animées | [ ] |
| Tide `water` | `WATER` | aile d'eau translucide bleue, reflets qui ondulent, liseré d'écume | translucide, ondulation lente (« respire comme une vague ») | [ ] |
| Stone `earth` | `EARTH` | 3 segments épais de roche grise fissurée, mousse près du dos, éclats d'améthyste lumineux | couche lumineuse `earth_glow` | [ ] |
| Wind `air` | `AIR`, 2 ailes par côté | 4 voiles blanches très transparentes, striées comme des courants d'air, effilochées | très translucide | [ ] |
| Storm `storm` | `STORM` | énergie électrique bleu clair à violet, éclairs blancs, bord effiloché | émissive, crépitement | [ ] |
| Aurora `aurora` | `AURORA`, 2 voiles par côté | rideaux verticaux de lumière vert à violet, toujours déployés | émissive, respiration lente | [ ] |
| Shadow `shadow` | `SHADOW` | fumée violet-noir translucide, remous, yeux violets, bord en volutes | couche lumineuse `shadow_glow` (yeux) | [ ] |
| Nebula `nebula` | `NEBULA` | nuages bleu nuit et pourpres, étoiles blanches | couche lumineuse `nebula_glow` | [ ] |
| Forest Spirit `forest` | `FOREST`, 2 ailes par côté | 4 ailes de feuilles vertes tachetées de fleurs roses, armature de branches, bord dentelé | feuillage découpé | [ ] |

### Légendaires (6)

| Paire | Modèle actuel | Cible (`WINGS.md`) | Points d'attention | Fait |
| --- | --- | --- | --- | --- |
| Angel `angel` | `ANGEL` | grande aile blanc crème, plumes allongées vers la pointe, jusqu'aux pieds au repos | battement majestueux (0,5/s), couche `angel_glow` | [ ] |
| Demon `demon` | `DEMON` | grande membrane rouge sombre veinée de noir, 3 segments épais, lueur au bord de fuite | coup sec et brutal, couche `demon_glow` | [ ] |
| Seraph `seraph` | `SERAPH`, 3 ailes par côté | 6 ailes d'ange dorées en éventail (haute, centrale, basse), léger décalage de battement | couche `seraph_glow` | [ ] |
| Psyche `psyche` | `PSYCHE_FORE` + `PSYCHE_HIND` | 4 ailes de papillon en flammes d'âme cyan et nacrées, bords qui ondulent, toujours déployées | émissive | [ ] |
| Daedalus `daedalus` | `DAEDALUS` | armature de laiton, panneaux d'acier rivetés, deux réacteurs dans le dos | rien ne bat, tout pousse (réacteurs vectorisés), effets `THRUSTERS` | [ ] |
| Yin and Yang `yin_yang` | `YIN_YANG` (ajouté le 22 octobre, gabarit proche de l'ange) | gauche : plumes noires à reflets bleu nuit, « œil » blanc près du poignet ; droite : plumes blanc nacré, « œil » noir ; bords de fuite en S ; repliées, les pointes forment le cercle du taijitu | **première paire asymétrique** : deux ailes différentes dans le même `.bbmodel` (préfixes `left_` / `right_`) | [ ] |

### Mythiques (2)

| Paire | Modèle actuel | Cible (`WINGS.md`) | Points d'attention | Fait |
| --- | --- | --- | --- | --- |
| Night `night` | `NIGHT_MAIN` + `NIGHT_HIND`, queue `TailModel` | ailes de la Furie Nocturne : grande paire et petite paire arrière, cuir noir | queue de 6 segments avec 2 paires d'ailerons ; aileron gauche du bout rouge à marque blanche (prothèse d'Harold) | [ ] |
| Light `light` | `LIGHT_MAIN` + `LIGHT_HIND`, queue `TailModel` | ailes de la Furie Éclair : blanches, irisées | queue et ailerons blancs ; effets de plasma et de camouflage | [ ] |

- [ ] Queues des Furies : passer `render/TailModel` + `anim/TailAnimator` en Blockbench, avec le même rig et le même style que les ailes.
- [ ] One-shots en keyframes (déploiement, repli, atterrissage) pour toutes les paires.
- [ ] Finition des textures par le designer.
