# Mixins et access transformers

État au 24 octobre 2026 : **un seul mixin** (`PlayerModel.setupAnim`, voir plus bas), aucun access transformer.

| Besoin | Mécanisme utilisé à la place | Fichier |
| --- | --- | --- |
| Pas de kick « floating too long » | `AttributeModifier` transitoire −100 % sur `minecraft:generic.gravity` → `getMaximumFlyingTicks` infini | `flight/ServerFlight.java` |
| La souris tourne la caméra, pas le corps | `CalculatePlayerTurnEvent` (sensibilité −1/3) + `MouseHandler.getXVelocity()` / `getYVelocity()` | `client/FlightCamera.java` |
| Caméra orbitale, « laissée derrière », « dans la tête » | `ViewportEvent.ComputeCameraAngles` + `CalculateDetachedCameraDistanceEvent` + `Options.setCameraType` | `client/FlightCamera.java` |
| Corps à l'horizontale en vol | `RenderPlayerEvent.Pre` / `Post` | `render/BodyPose.java` |
| Jambes qui moulinent en vol | `walkAnimation.setSpeed(0)` dans `PlayerTickEvent.Post` | `client/SimplyWingsClient.java` |
| Neutraliser les déplacements vanilla en vol | `MovementInputUpdateEvent` | `client/ClientFlight.java` |

## Mixin utilisé

| Cible | Raison | Alternative écartée |
| --- | --- | --- |
| `PlayerModel.setupAnim` (`@Inject` TAIL, priorité 1500), `mixin/PlayerModelMixin` → `render/FlightPose` | Animer bras, jambes et tête pendant le vol (demande de zorago du 24/10) : profilé en croisière, bras écartés et jambes en avant au freinage, collés en piqué, roulis en virage, bras et jambes qui flottent en stationnaire. Aucun event NeoForge ne passe après `setupAnim` : tout ce qu'on règle avant est écrasé, et les calques de rendu arrivent trop tard. | Modifier les parts dans `RenderPlayerEvent.Pre` (écrasé par `setupAnim`), une lib d'animation (dépendance interdite). |

Le mixin n'agit que si le joueur porte des ailes et vole (mode stabilisé ou déplacement), et recopie ensuite manches, jambières et chapeau. La priorité 1500 le fait passer après Not Enough Animations (présent dans le pack) ; hors vol, il ne touche à rien. Le cabré du corps au freinage (40°) passe, lui, par `RenderPlayerEvent` (`render/BodyPose`).
