# Analyse des mods de référence

Version du 20 octobre 2026 · à valider par zorago avant toute ligne de code.

## Périmètre

| Jar analysé | Source |
| --- | --- |
| `betterflight-neoforge-1.0.2.jar` | `modsRef/` |
| `ShoulderSurfing-NeoForge-1.21.1-5.2.0.jar` | `modsRef/` |
| `do_a_barrel_roll-neoforge-3.7.3+1.21.jar` | `modsRef/` |
| `caelus-neoforge-7.0.1+1.21.1.jar` | `modsRef/` (aussi présent dans le pack) |
| `FantasticWings-v21.1.2-1.21.1-NeoForge.jar` (continuation de Wings par Fuzs) | `modsRef/` |
| `jei-1.21.1-neoforge-19.64.0.453.jar` | copié depuis le pack |
| NeoForge `21.1.257` (classes patchées de Minecraft + events) | install CurseForge du pack |
| Mixins des 195 mods de `PleasePlayWithMe` | scan des jars du pack |

Décompilation avec Vineflower 1.11.1 (`tools/vineflower.jar`) dans `modsRef/_decompiled/`. Rien n'est recopié : seules les techniques sont retenues.

## Synthèse

Trois découvertes changent l'architecture, toutes dans le sens « moins d'override » :

1. **Anti-kick sans access transformer.** Le serveur calcule son seuil de kick avec `getMaximumFlyingTicks(player)`, qui renvoie `Integer.MAX_VALUE` quand `player.getGravity() < 1e-5`. Or `LivingEntity.getDefaultGravity()` lit l'attribut vanilla `minecraft:generic.gravity`. Un `AttributeModifier` transitoire à −100 % pendant le vol supprime le kick, sans AT ni mixin.
2. **Découplage souris sans mixin.** `MouseHandler.turnPlayer` déclenche `CalculatePlayerTurnEvent` avant de calculer la rotation. En forçant la sensibilité à −1/3, le facteur `(s·0,6 + 0,2)³` tombe à 0 et le joueur ne tourne plus. Le delta brut reste lisible par `MouseHandler.getXVelocity()` / `getYVelocity()` (méthodes publiques), et on l'envoie à notre caméra.
3. **Caméra orbitale sans mixin.** Dans `Camera.setup`, NeoForge applique les angles de `ViewportEvent.ComputeCameraAngles`, puis recule la caméra de `CalculateDetachedCameraDistanceEvent` le long de ces angles, avec la détection de murs vanilla (`getMaxZoom`). Choisir yaw, pitch et distance suffit donc à placer la caméra où on veut, y compris figée dans le monde.

Résultat : le budget passe de « 1 mixin + 1 AT » à **0 mixin + 0 AT** pour le vol, la caméra et le kick. Un seul point reste incertain (la pose des bras en vol), avec un mixin de secours documenté plus bas.

## Better Flight Revived (1.0.2)

**Ce qu'il fait**

- **Anti-kick** : il n'a pas de vol custom. Il s'appuie sur l'état élytre vanilla (`startFallFlying()`, côté client puis serveur). Or `handleMovePlayer` exempte `isFallFlying()` du calcul `clientIsFloating`. Le kick est donc évité par l'état élytre lui-même, pas par une astuce.
- **Stamina** : elle est double. Un compteur client (`InputHandler.charge`) sert la prédiction et le HUD. Une `HashMap<UUID,Integer>` statique côté serveur (`ServerFlightStamina`), non persistée, fait foi : le serveur refuse le battement s'il manque de charge.
- **Paquets** :
  - C→S : `flight_action` (enum TAKEOFF / FLAP / BOOST / STOP / RECHARGE), un par action.
  - S→C : `elytra_charge` (un int) renvoyé **à chaque** dépense ou recharge, et `common_config` à la connexion, au respawn et au changement de mode de jeu.
  - La recharge est **pilotée par le client** : il envoie RECHARGE à chaque point regagné et le serveur vérifie le délai. Ça fait environ 1 paquet dans chaque sens par point de stamina.
- **Physique** : de simples `player.push()` dans la direction du regard, appliqués par le client. Le serveur ne touche pas au mouvement.
- **Dépendances** : Architectury et YACL obligatoires, Curios optionnel.

**Ce qu'on fera différemment**

| Point | Better Flight | Nous | Pourquoi |
| --- | --- | --- | --- |
| Anti-kick | État élytre | Gravité à 0 via un attribut | Notre vol n'est pas un vol plané élytre. L'état élytre imposerait sa pose, ses dégâts de collision (`kinetic`) et entrerait en conflit avec Caelus et Elytra Slot, présents dans le pack |
| Stamina | Map statique non persistée | Data Attachment persistant, valeur en float | Survit à la déco/reco, sans fuite mémoire et sans `HashMap` globale |
| Recharge | Le client envoie RECHARGE | Le serveur la calcule seul | Le serveur fait autorité, et ça évite un paquet par point regagné |
| Sync stamina | Un paquet S→C à chaque changement | Prédiction client et correction seulement si l'écart dépasse un seuil, ou au passage à 0 ou au max | Moins de paquets. Le battement reste un événement C→S validé par le serveur |
| Config | Paquet maison qui recopie la config | `ModConfigSpec` en type SERVER, synchronisé par NeoForge | Natif, sans paquet à écrire |

**À retenir comme technique** : la validation des battements côté serveur (« pas au sol, en vol, assez de charge ») et un HUD réactif (bordure qui clignote à la dépense et à la recharge).

## Shoulder Surfing Reloaded (5.2.0)

**Ce qu'il fait** : 22 mixins client communs, plus 1 propre à NeoForge et des mixins de compat (Create, Iris, EpicFight…). Ceux qui concernent la caméra et la souris :

| Mixin | Cible | Rôle |
| --- | --- | --- |
| `LocalPlayerMixin` (priorité 1500) | `LocalPlayer.turn` (override complet) | Les deltas souris vont à `ShoulderSurfingCamera.turn()`. Si la caméra les consomme, le joueur ne tourne pas |
| `MouseHandlerMixin` | `@Redirect` de `CameraType.isFirstPerson()` dans `turnPlayer` | Garde le comportement de visée (scoping) en vue épaule |
| `CameraMixin` (commun) | `Camera.setup` : `@Inject` après `setPosition`, `@Redirect` de `move` | Impose sa rotation et son offset épaule, ajoute du sway (roulis) |
| `CameraMixin` (NeoForge) | `@ModifyArg` de `rotationYXZ` dans `setRotation` | Ajoute son propre roulis au quaternion |
| `GameRendererMixin` | `pick`, `getFov`, `bobView` | Visée depuis la caméra, FOV et bobbing |
| `PlayerMixin` | `Player.pick` (override) | Raycast depuis la caméra et non depuis les yeux |

**Peut-on faire pareil avec `ComputeCameraAngles` + un seul mixin ?** Oui, et même **sans mixin** :

- **Rotation** : `ViewportEvent.ComputeCameraAngles` (yaw, pitch et roll modifiables). L'event est déclenché *avant* `setPosition`, puis le recul en troisième personne suit ces angles. On obtient l'orbite libre.
- **Distance** : `CalculateDetachedCameraDistanceEvent.setDistance()`. La détection de murs `getMaxZoom` reste active.
- **Souris** : `CalculatePlayerTurnEvent` (sensibilité forcée à −1/3) et lecture de `MouseHandler.getXVelocity()` / `getYVelocity()`. On applique ensuite nous-mêmes la courbe de sensibilité vanilla (`d = s·0,6+0,2 ; d³·8`) et l'option « inverser la souris ».
- **Mode de caméra** : `Options.setCameraType(THIRD_PERSON_BACK)` en entrée de mode déplacement. On restaure la valeur précédente en sortie, quelle qu'elle soit (Sable ajoute ses propres `CameraType`).

**Ce qu'on fera différemment** : on n'a pas besoin du raycast depuis la caméra (pas de combat, pas de visée en vol), donc pas de `pick`. Pas d'offset épaule ni de sway imposé. Le roulis de virage passe par le `roll` de `ComputeCameraAngles`.

**Limite à connaître** : avec la sensibilité à −1/3, `player.turn(0, 0)` est quand même appelé. Les mods qui s'accrochent à `turn` dans le pack (Hybrid Aquatic, l'hypnose qui pousse la vue vers un mob) continuent de fonctionner sans rien casser.

## Do a Barrel Roll (3.7.3, build NeoForge)

**Ce qu'il fait**

- **Représentation** : il stocke des **Euler** (pitch et yaw vanilla de l'entité, plus un champ `roll` ajouté par mixin). Il les fait évoluer **en repère local** : il construit les vecteurs `facing` et `left`, tourne autour des axes locaux (`rotateAxis`), puis reconvertit en Euler (`asin` / `atan2`). On a les maths d'un quaternion sans gimbal lock, avec le stockage simple des Euler.
- **Contrôle clavier** : `buttonControls`, delta = puissance × `renderDelta`, appliqué **à chaque frame** et non à chaque tick, pour la fluidité.
- **Lissage** : un `SmoothDouble` vanilla par axe (le filtre de la caméra cinématique).
- **Banking** : roulis induit par le lacet, formule configurable. **Reorient** : retour automatique du roulis vers 0 avec une courbe cubique `−r³/3 + r`.
- **Roll caméra** : mixin `Camera.setup` → `@ModifyArg` sur l'argument roll de `setRotation`. Roll du modèle : mixin `PlayerRenderer.setupRotations`.
- **Sync** : `roll_sync` C→S dès que le roll change (en pratique à chaque tick en virage), relayé S→C aux joueurs qui suivent l'entité via un mixin `ServerEntity.sendChanges`.
- 18 mixins, dont `MouseHandler.turnPlayer` (`@WrapWithCondition` sur `turn`).

**Réutilisable pour un contrôle ZQSD sans souris**

- L'intégration par frame avec `renderDelta` : le lacet et le tangage avancent de `maniabilité × dt`, en degrés par seconde comme dans la spec.
- Le lissage par `SmoothDouble`, ou un ressort critique maison, sur la vitesse angulaire pour éviter les à-coups au tap.
- L'inclinaison visuelle (banking) proportionnelle à la vitesse de lacet, avec retour cubique au neutre.
- Pas besoin de rotation en repère local : notre spec n'a pas de looping. Le tangage est borné et le lacet se fait autour de l'axe vertical du monde, donc des Euler simples suffisent.

**Ce qu'on fera différemment** : le roulis caméra passe par `ComputeCameraAngles`, le roulis du corps par `RenderPlayerEvent.Pre` (voir Wings). Pas de paquet par tick : le roulis des joueurs distants se *déduit* de leur variation de yaw, déjà synchronisée par vanilla.

## Caelus API (7.0.1)

**Ce qu'il fait** : il ajoute un attribut `caelus:fall_flying` (≥ 1 = autorisé). Trois mixins :

- `LivingEntity.updateFallFlying` (`@ModifyArg` de `setSharedFlag(7)`) : garde le flag élytre si l'attribut l'autorise.
- `LocalPlayer.aiStep` (`@ModifyVariable` après `getItemBySlot(CHEST)`) : lance `startFallFlying()` et envoie un paquet sans élytre dans le slot torse.
- `CapeLayer` : masque la cape.

**Applicable à notre vol ?** Non. Caelus étend le **vol plané élytre** vanilla. Notre vol est une physique custom qui n'est pas un vol plané. Le passer par Caelus réintroduirait une dépendance et les comportements élytre (pose, dégâts de collision, durabilité).

**Conséquence** : l'idée de l'access transformer est abandonnée elle aussi (voir la synthèse, gravité à 0). Point de compat : Caelus et Elytra Slot sont dans le pack. À l'entrée en vol d'ailes, on appelle `stopFallFlying()` pour qu'une élytre portée ne prenne pas le relais.

## Wings → Fantastic Wings (21.1.2, Fuzs)

**Ce qu'il fait**

- **RenderLayer** : `LayerWings extends RenderLayer<AbstractClientPlayer, PlayerModel>`, enregistré pour `EntityType.PLAYER` (via Puzzles Lib, l'équivalent de `EntityRenderersEvent.AddLayers`). Il s'accroche au corps avec `getParentModel().body.translateAndRotate(poseStack)`, puis un petit décalage si un plastron est porté. Rendu en `RenderType.entityCutout(texture)`.
- **Modèle** : une hiérarchie `ModelPart` (coracoïde → humérus → ulna → carpes) en `LayerDefinition`. Les plumes sont des **textures extrudées en 3D** : une sous-classe de `ModelPart.Cube` génère un voxel par pixel opaque. Ça reste le pipeline vanilla, donc compatible Iris.
- **Animation procédurale** : une machine d'états (`Idle`, `Lift`, `Glide`, `Fall`, `Land`) avec transitions interpolées sur N ticks. Chaque état fournit une rotation par os. Le battement est une phase `flapCycle` qui avance à chaque tick et se lit en `sin(phase)` avec interpolation `prev`/`cur` par partial tick. Bruit `SimplexNoise` sur le plané et la chute pour un mouvement organique.
- **Joueurs distants** : le serveur synchronise seulement l'état « vole / ne vole pas » et la forme d'aile, aux joueurs proches. **L'animation est déduite localement** du mouvement observé (`x − xo`, `y − yo`) et du pitch. Aucune donnée d'animation ne passe par le réseau.
- **Mixins** : `PlayerRenderer.setupRotations` (corps à l'horizontale), `PlayerModel.setupAnim` (bras et jambes), `ServerGamePacketListenerImpl.handleMovePlayer` (anti-kick : `isFallFlying() || isFlying()`), et d'autres.

**Technique retenue**

- RenderLayer sur le corps, avec une hiérarchie d'os, des transitions d'états interpolées et une phase de battement interpolée au partial tick. Notre mini-lib `anim` reprend ce principe : pose = mélange d'états + phase.
- Animation des joueurs distants **déduite localement** : on synchronise seulement la paire équipée et le mode, sur changement.
- Les plumes extrudées sont une bonne piste pour des ailes à plumes nettes, sans shader.

**Ce qu'on fera différemment**

| Point | Fantastic Wings | Nous |
| --- | --- | --- |
| Anti-kick | Mixin `handleMovePlayer` | Attribut gravité à 0, sans mixin |
| Corps à l'horizontale | Mixin `setupRotations` | `RenderPlayerEvent.Pre` (le `poseStack` est encore modifiable avant le `pushPose` du renderer) et `pop` dans `Post` |
| Bras et jambes | Mixin `PlayerModel.setupAnim` | À valider en prototype. Si la pose vanilla en l'air rend mal, ce sera **le seul mixin du mod** (`@Inject` TAIL, sans redirect, donc compatible avec Player Animator et NotEnoughAnimations) |
| Dépendance | Puzzles Lib | Aucune |
| Forme d'aile | Codée en Java | Définie en datapack, modèle choisi par id |

## JEI (19.64.0.453) : zone du bouton d'inventaire

**Ce n'est pas libre.** `BookmarkOverlayLayout.calculateDisplayArea` réserve **toute la bande `x ∈ [0, guiLeft)` sur toute la hauteur** de l'écran aux favoris (bookmarks). Les boutons « favoris » et « historique » sont posés en bas à gauche, et l'historique de recherche occupe le bas de cette bande. Un bouton collé au bord gauche de l'inventaire, à hauteur de la hotbar, tombe pile dans cette zone.

JEI ne s'en écarte que pour les zones déclarées par un plugin JEI (`IGuiContainerHandler.getGuiExtraAreas`). Il recadre alors sa bande à gauche de notre zone, ce qui est exactement ce qu'il nous faut. Les effets de potion sont à droite et ne gênent pas.

Options, à trancher par zorago :

1. **Plugin JEI optionnel** : une classe `@JeiPlugin` compilée contre l'API JEI en `compileOnly`, chargée seulement si JEI est présent. Le mod reste utilisable sans JEI, mais ça fait une intégration à maintenir.
2. **Bouton à l'intérieur du cadre de l'inventaire**, par exemple dans le coin du panneau du joueur 3D. Aucun conflit possible, mais Curios et Cosmetic Armor posent aussi des boutons dans ce secteur, à vérifier en jeu.
3. **Bouton sous l'inventaire**, centré ou aligné à gauche, hors de la bande JEI tant qu'il reste à `x ≥ guiLeft`.

Recommandation : **option 3 par défaut, plus l'option 1 en bonus**. Le mod fonctionne partout, et avec JEI le bouton peut retourner à l'emplacement prévu dans la spec.

## Mods du pack à risque

Scan des mixins des 195 mods de `PleasePlayWithMe` (NeoForge 21.1.257), filtré sur les classes qu'on touche. Les injections sensibles ont été décompilées.

| Mod | Ce qu'il touche | Risque pour nous | Parade |
| --- | --- | --- | --- |
| **Sable** (moteur physique de Create Aeronautics) | `Camera.setup` : `@Redirect` de `setRotation` (relit les angles de `ComputeCameraAngles`), `@WrapMethod` de `setPosition`, roulis prémultiplié quand on est sur un vaisseau. `@Overwrite` de `CameraType.cycle` et 2 `CameraType` en plus. `handleMovePlayer` (désactive « moved too quickly » sur un vaisseau) | **Faible si on reste sur les events**. Sable lit lui-même les valeurs de `ComputeCameraAngles` : nos angles passent par son pipeline au lieu de se battre avec. Un mixin `Camera` à la Shoulder Surfing serait entré en collision | Restaurer le `CameraType` d'origine (y compris ceux de Sable). Tester le décollage depuis un vaisseau en mouvement et l'atterrissage dessus |
| **Create** | `Camera.setup` → `@ModifyArg` de `getMaxZoom` (zoom contraptions). `PlayerRenderer` | Faible : notre distance passe avant, dans l'event | Tester en vol près d'une contraption |
| **Create Hypertube** | `Camera.setup`, `LocalPlayer`, `handleMovePlayer` | Faible : un joueur dans un tube ne vole pas | Couper le vol si `isPassenger()` ou en tube |
| **Hybrid Aquatic** | `Camera.setup` (après `setPosition`, `getMaxZoom`), `turnPlayer` → `turn` | Faible : l'hypnose pousse le yaw du joueur, qui est ignoré en vol | Rien |
| **Immersive Furniture**, **KnightLib** | `Camera.setup` (assis, cinématiques de boss) | Faible | Couper le vol en passager |
| **Ok Zoomer** | `turnPlayer` (sensibilité en zoom) | Faible : en vol, notre sensibilité prime | Le zoom reste utilisable en mode stabilisé |
| **Player Animator**, **NotEnoughAnimations**, **Better Combat**, **Iron's Spells**, **Simply Swords** | `PlayerModel`, `HumanoidModel`, `PlayerRenderer`, `LivingEntityRenderer` | Moyen pour le rendu : ils déplacent bras et corps | Notre layer suit `body.translateAndRotate` après leurs modifications. Si on ajoute le mixin `setupAnim`, ce sera un `@Inject` TAIL désactivable en config |
| **Iris** | Rendu monde, ombres, armure | Faible : `entityCutout` et `entityTranslucent` sont gérés. L'émissif via `RenderType.eyes` ou `entityTranslucentEmissive` est à tester avec le shaderpack du pack | Prévoir une option « émissif » côté client |
| **Distant Horizons** | `FogRenderer` seulement. Génère aussi des LOD côté serveur (`Distant_Horizons_server_data`) | Nul pour le code. Pour le serveur, un dive à 55 m/s s'ajoute à la génération DH | Garder l'avertissement de la spec au-dessus de 55 m/s |
| **C2ME**, **chunksending**, **smoothchunk** | Chargement et envoi des chunks | Atténue le coût des vols rapides | Rien |
| **YUNG's Better End Island** | **Annule et réécrit** `EndDragonFight` (`setDragonKilled`, `tryRespawn`, `respawnDragon`, `tick`), île centrale et tour redessinées | **Fort pour la forge** : relief et position du portail différents, mais `dragonKilled` et `previouslyKilled` restent mis à jour | Déclencher sur la mort du dragon (`LivingDeathEvent` sur `EnderDragon` dans l'End), pas sur un hook interne de `EndDragonFight`. Vérifier en jeu que le rayon 65 est libre sur cette île |
| **BetterEnd**, **Echoes of the End**, **Moog's End Structures**, **Clear End City** | Terrain de l'End, `tryRespawn`, structures | Moyen : relief et structures autour de l'île | La fondation jusqu'au sol de la spec règle le relief. Chercher un emplacement libre pendant le test |
| **Caelus**, **Elytra Slot**, **Ars Nouveau** (élytre client) | Vol plané élytre | Moyen : une élytre active pendant notre vol | `stopFallFlying()` à l'entrée en vol, vol d'ailes prioritaire |
| **Dragons of Berk** | Montures volantes | Faible | Couper le vol en passager |
| **Numismatic Overhaul** | Mixin `InventoryScreen.init` (bouton dans l'inventaire) | Possible chevauchement du bouton | Vérifier sa position en jeu |
| **FTB Library / Quests**, **Curios**, **Cosmetic Armor**, **Polymorph**, **Simple Inventory Sorter** | Boutons dans ou autour de l'inventaire | Possible chevauchement | Capture de l'inventaire en jeu avant de fixer la position |
| **Lithium** | `EndDragonFight` (pattern matching) | Nul | Rien |

**Touches déjà prises** dans `options.txt` : R (perspective, JEI), V, G, C, X, B, F. La touche du mode déplacement devra éviter ces lettres (proposition : Alt gauche, ou H). Elle reste rebindable.

## Décisions d'architecture

Table du cahier des charges, avec les lignes corrigées en gras.

| Module | Mécanisme retenu | Côté | Changement |
| --- | --- | --- | --- |
| Définitions d'ailes | Registre datapack custom synchronisé (`DataPackRegistryEvent.NewRegistry` avec codec réseau) | Commun | Confirmé |
| Données joueur | Data Attachment avec codec, `copyOnDeath`, stats effectives en cache | Serveur | Confirmé |
| Réseau | `CustomPacketPayload` sur changement : équiper, entrée et sortie de mode, battement (événement), correction de stamina | Les deux | Confirmé. Le relais aux joueurs distants passe par `PacketDistributor.sendToPlayersTrackingEntity` |
| Physique de vol | Tick client : `MovementInputUpdateEvent` lit puis neutralise les impulsions vanilla, et `PlayerTickEvent.Pre` impose `deltaMovement` à chaque tick | Client | Précisé |
| **Validation serveur (anti-kick)** | **`AttributeModifier` transitoire sur `minecraft:generic.gravity` (−100 %, `ADD_MULTIPLIED_TOTAL`) posé et retiré par le serveur à l'entrée et à la sortie du vol. `getMaximumFlyingTicks` passe alors à l'infini** | Serveur | **Remplace l'access transformer** |
| Dégâts de chute et de crash | `LivingFallEvent` (annulation par les sauts de secours, crash selon la vitesse perpendiculaire envoyée avec l'événement d'impact et bornée par le serveur) | Serveur | Confirmé |
| **Caméra** | **`ViewportEvent.ComputeCameraAngles` (yaw, pitch, roll) + `CalculateDetachedCameraDistanceEvent` (distance) + `Options.setCameraType`. « Laissée derrière » : angles orientés du point figé vers le joueur, distance = distance au point figé. « Dans la tête » : distance qui tend vers 0, puis bascule en première personne** | Client | **Sans mixin** |
| **Découplage souris** | **`CalculatePlayerTurnEvent` (sensibilité −1/3) + `MouseHandler.getXVelocity()` / `getYVelocity()`** | Client | **Le mixin prévu disparaît** |
| Rendu des ailes | RenderLayer ajouté au joueur (`EntityRenderersEvent.AddLayers`), `ModelPart`, RenderTypes vanilla | Client | Confirmé |
| **Pose du corps en vol** | **`RenderPlayerEvent.Pre` / `Post` (push, rotation et pop du `poseStack`). Mixin de secours unique et optionnel : `PlayerModel.setupAnim` TAIL, pour les bras** | Client | **Nouveau, au plus 1 mixin** |
| Animation | Mini-lib maison : états avec transitions interpolées + phase de battement au partial tick | Client | Confirmé |
| Joueurs distants | Sync de la paire et du mode sur changement. Phase, banking et intensité de battement déduits localement du mouvement interpolé | Client | **Plus d'envoi périodique d'un « état compact »** |
| Énergie ailée et loots | Data Component + Global Loot Modifiers + `LootItemCondition` maison | Serveur | Confirmé |
| Table d'assemblage | Block, BlockEntity, Menu et `RecipeType` custom | Les deux | Confirmé |
| Interface | `ScreenEvent.Init` (bouton), Screen custom, `RegisterGuiLayersEvent` (HUD) | Client | Position du bouton à revoir (voir JEI) |
| Config | `ModConfigSpec` SERVER et CLIENT | Les deux | Confirmé |
| **Structure (forge de l'End)** | **`LivingDeathEvent` sur `EnderDragon` dans l'End, si `hasPreviouslyKilledDragon()` est vrai → `StructureTemplate` posé dans le vide (≈ 512 blocs, Y fixe) + `SavedData` + compas `lodestone_tracker`** | Serveur | **Précisé, pour ne pas dépendre de `EndDragonFight`, que YUNG's réécrit** |
| **Intégration JEI** | **`@JeiPlugin` optionnel (`compileOnly`) : assemblage, énergie ailée, drops de mobs, essences de blocs** | Client | **Nouveau** |

**Budget d'override final** : 0 access transformer, et 0 mixin, 1 au maximum (`PlayerModel.setupAnim` TAIL, seulement si le prototype le justifie), à documenter dans `docs/MIXINS.md`.

## Cible de version

- NeoForge **21.1.x** pour Minecraft **1.21.1** : c'est la version commune de la communauté et celle du pack (21.1.257).
- Compilation contre la dernière 21.1 stable, tests sur 21.1.257. La borne basse de `versionRange` sera fixée en vérifiant l'ancienneté des events utilisés (`CalculateDetachedCameraDistanceEvent` est le plus récent des trois).

## Réponses de zorago (20 octobre)

1. **Bouton d'inventaire** : sous l'inventaire (option 3).
2. **JEI** : plugin optionnel, avec tous les crafts de la table d'assemblage et un onglet pour les loots de mobs et de blocs. Comme les Global Loot Modifiers ne sont pas synchronisés au client, les sources de loot passent dans un registre datapack synchronisé (`simplywings:loot_source`), lu à la fois par les GLM et par JEI.
3. **Mod id** : `simplywings`.
4. **Touche du mode déplacement** : Alt gauche par défaut.
5. **Forge, correction** : elle n'est plus sur l'île centrale. Elle flotte dans le vide à mi-parcours vers les îles extérieures (≈ 512 blocs), apparaît à partir du deuxième dragon et donne un compas. Ça règle le risque YUNG's, BetterEnd et autres mods d'End identifié plus haut. Le détail est dans le cahier des charges, section « Forge d'ailes dans l'End ».
