# Mixins et access transformers

État au 20 octobre 2026 : **aucun mixin, aucun access transformer.**

| Besoin | Mécanisme utilisé à la place | Fichier |
| --- | --- | --- |
| Pas de kick « floating too long » | `AttributeModifier` transitoire −100 % sur `minecraft:generic.gravity` → `getMaximumFlyingTicks` infini | `flight/ServerFlight.java` |
| La souris tourne la caméra, pas le corps | `CalculatePlayerTurnEvent` (sensibilité −1/3) + `MouseHandler.getXVelocity()` / `getYVelocity()` | `client/FlightCamera.java` |
| Caméra orbitale, « laissée derrière », « dans la tête » | `ViewportEvent.ComputeCameraAngles` + `CalculateDetachedCameraDistanceEvent` + `Options.setCameraType` | `client/FlightCamera.java` |
| Corps à l'horizontale en vol | `RenderPlayerEvent.Pre` / `Post` | `render/BodyPose.java` |
| Jambes qui moulinent en vol | `walkAnimation.setSpeed(0)` dans `PlayerTickEvent.Post` | `client/SimplyWingsClient.java` |
| Neutraliser les déplacements vanilla en vol | `MovementInputUpdateEvent` | `client/ClientFlight.java` |

Candidat unique si le prototype le justifie : `PlayerModel.setupAnim` en `@Inject` TAIL, pour la pose des bras et de la tête en vol de déplacement (aujourd'hui, le joueur à l'horizontale regarde le sol).
