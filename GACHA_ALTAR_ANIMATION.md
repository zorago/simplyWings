# Animation d'invocation de l'autel, dans le monde

Animation **3D réelle, dans le monde**, jouée sur l'île d'invocation autour de l'autel draconique et de ses six flammes. **Tous les joueurs qui ont l'autel en vue la voient en direct**, de loin, de très haut ou de très bas. Seuls les cartes (interface) et les mouvements de caméra sont réservés au joueur qui invoque.

Ce document remplace la comète du § 6 de `GACHA_ANIMATIONS.md` pour la partie « monde ». Ce qui reste de `GACHA_ANIMATIONS.md` : les cartes, le récapitulatif ×10, l'accessibilité, les couleurs de palier et le prototype de motion design (§ 0), qui reste la référence de rythme (single B validé).

Sources de travail :

- Scène Blockbench : `art/gacha/summoning_animation.bbmodel`, générée par `python -I tools/blockbench/altar_anim.py` à partir de l'export de l'île (`/simplywings sanctum export summoning_sanctum`, puis `tools/blockbench/altar_scene.py` pour le décor seul).
- Repère : 1 bloc = 16 unités Blockbench ; origine au centre de l'autel, au ras du sol de l'île (dessus des blocs du sol). L'autel est à y + 2 blocs, les bedrocks des flammes à y + 1 bloc, à 7 blocs de l'autel en hexagone.

## 1. Les six flammes

Ordre donné par zorago : en partant de derrière l'autel à droite (`anchor_3`) et en tournant dans le sens horaire vu du dessus.

| Ancre | Position (blocs, depuis l'autel) | Flamme | Silhouette et mouvement propres |
| --- | --- | --- | --- |
| `anchor_3` | x −4, z +6 | **Feu** | Langues de flamme en escalier, rouge → orange → jaune, flicker rapide et irrégulier, braises qui montent |
| `anchor_1` | x −7, z 0 | **Ténèbres** | Flamme inversée qui « coule » vers le haut en volutes lentes, noir violacé, cœur violet, deux yeux de braise qui s'ouvrent au climax |
| `anchor_2` | x −4, z −6 | **Terre** | Bloc de roche en lévitation, éclats qui orbitent, mousse et cristaux verts, mouvement lourd et lent, secousses |
| `anchor_4` | x +4, z −6 | **Eau** | Colonne d'eau tournante, anneau de gouttes en orbite, vague qui monte et retombe en boucle |
| `anchor_6` | x +7, z 0 | **Lumière** | Prisme blanc doré qui tourne, rayons en croix qui pulsent, halo |
| `anchor_5` | x +4, z +6 | **Air** | Spirale de vent pâle (rubans de cubes fins) qui tourne vite, feuilles et plumes qui tournoient |

## 2. Faisabilité en jeu (recherche du 26 octobre)

Contraintes du mod : zéro dépendance, ModelPart et RenderTypes vanilla, aucun shader custom, aucun paquet par tick.

### 2.1 Visible de tous, de loin, au-dessus de la hauteur maximale

Vérifié dans les sources de NeoForge 21.1.257 (`LevelRenderer`, `GameRenderer`, `FogRenderer`, `BlockEntityRenderer`) :

- **Pas de BlockEntityRenderer comme support principal.** Par défaut il n'est dessiné qu'à 64 blocs (`getViewDistance`) et seulement si sa section de chunk est visible. On peut l'étendre (`getViewDistance`, `shouldRenderOffScreen` pour la liste globale, `getRenderBoundingBox` = `AABB.INFINITE`), mais il reste lié au chargement du chunk de l'autel.
- **Choix retenu : une passe de rendu dédiée**, abonnée à `RenderLevelStageEvent` (étape `AFTER_PARTICLES` pour le translucide, `AFTER_ENTITIES` pour l'opaque). Elle dessine toutes les invocations actives de la dimension, sans dépendre du chunk de l'autel ni d'une entité. La géométrie peut être n'importe où, **au-dessus de y = 319 compris** : la hauteur maximale ne limite que les blocs, pas le rendu.
- **Plan lointain** : `GameRenderer.getDepthFar()` = distance de rendu en blocs × 4. À 12 chunks, 768 blocs. Un joueur à y = 0 et à 12 chunks (192 blocs) de l'autel, dont le sol est vers y = 286, voit le sigil du ciel (≈ y 380) à ≈ 420 blocs : c'est dans le champ.
- **Brouillard** : le brouillard de terrain finit à la distance de rendu (192 blocs à 12 chunks) ; au-delà, tout prend sa couleur. Pendant notre passe, on coupe le brouillard (`RenderSystem.setShaderFogStart(Float.MAX_VALUE)`, ce que fait déjà `FogRenderer.setupNoFog`), puis on restaure les valeurs. C'est un état de rendu, pas un shader. Avec Iris, le shaderpack gère son propre brouillard : à tester, repli prévu = garder le brouillard pour les joueurs sous shaders.
- **Particules vanilla inutilisables à distance** : `LevelRenderer.addParticleInternal` jette toute particule à plus de 32 blocs de la caméra (sauf `force`). Tous les effets lointains (étincelles, braises, plumes, éclats) sont donc de la **géométrie de notre passe** (cubes et billboards), simulée côté client. Les particules vanilla restent pour le détail de près.
- **Synchronisation, en direct pour tous** : au tirage, le serveur envoie un seul paquet `SummonShow(pos de l'autel, tick de départ, paliers des tirages, graine)` à tous les joueurs de la dimension dans un rayon configurable (défaut 512 blocs horizontaux), et le renvoie une fois à tout joueur qui entre dans ce rayon pendant le spectacle. Chaque client calcule ensuite tout localement : l'animation est une **fonction pure du temps** (`T = (tick courant − tick de départ + partialTick) / 20`), comme le prototype. Aucun paquet par tick ; deux clients voient la même chose au même moment.
- **Lumière** : tout est rendu en pleine lumière (`LightTexture.FULL_BRIGHT`). Cœurs lumineux et halos en `RenderType.lightning()` (couleur par sommet, mélange additif vanilla) ; textures émissives en `RenderType.eyes` (traitées comme émissives par Iris) ; volumes en `entityTranslucent` / `entityCutoutNoCull`. Tout est vanilla.
- **Son** : sons positionnels à l'autel, avec une distance d'atténuation longue dans `sounds.json` (`attenuation_distance` 96 à 160 pour les gros temps forts), pour qu'on entende l'impact de loin.
- **Coût** : quelques milliers de cubes au climax (estimation : 2 500 à 4 000). Ça représente au plus 100 000 sommets par image dans un seul lot par RenderType, ce qui ne pose pas de problème. Les objets à opacité nulle ou à échelle nulle ne sont pas émis.

### 2.2 Réservé à l'invocateur

- Mouvements de caméra (orbite accélérée du prototype, `CAM_B`) : `ViewportEvent.ComputeCameraAngles` et caméra détachée, comme pour le vol. Les autres joueurs gardent leur caméra.
- Cartes, typographie, flash plein écran, récapitulatif ×10 : interface de l'autel (`AltarScreen`), par-dessus la vue du monde.

## 3. Techniques d'animation retenues (pour une fluidité bien au-dessus du prototype)

Références : les ultimes des gachas actuels (*Wuthering Waves*, *Honkai: Star Rail*, *Genshin Impact*, *Zenless Zone Zero*) et les animations d'armes ultimes Blender et Blockbench qui circulent sur TikTok et Instagram. On en retient les invocations à cercles empilés, les « domaines » qui changent le ciel et les entités colossales en silhouette. Les techniques :

1. **Temps continu, jamais au tick** : chaque frame évalue `state(T)` avec le temps interpolé (`partialTick`). Les clés Blockbench sont lues avec une interpolation **Catmull-Rom** (courbe continue en position et en vitesse) ; les mouvements procéduraux (flicker, orbites, ondulations) sont des fonctions sinus et bruit évaluées à chaque frame. Résultat : 60 à 240 i/s selon l'écran, sans à-coup.
2. **Anticipation → action → retombée** sur chaque temps fort : contraction de 80 à 150 ms avant une expansion, dépassement de 5 à 15 % (`easeOutBack`), puis stabilisation amortie (ressort).
3. **Hit-stop (remappage du temps)** : sur chaque battement d'escalade et sur l'éclat, le temps de l'animation ralentit à 10 % pendant 60 à 120 ms, puis repart. On le fait par un remappage `T → T'` global, sans figer le jeu.
4. **Chevauchement et décalage** (overlap, offset) : rien ne démarre au même instant. Les six flammes s'allument à 0,22 s d'écart, les cercles empilés à 0,12 s, et les anneaux d'une même onde à 0,05 s.
5. **Traînées et smears** : l'étoile laisse des copies fantômes (afterimages) échantillonnées dans le passé (T − 0,03 s, T − 0,06 s…), de plus en plus petites et transparentes. Les objets rapides s'étirent dans le sens de leur vitesse (squash and stretch).
6. **Arcs, pas de lignes droites** : l'étoile descend en spirale, les éclats suivent des paraboles et les flux d'énergie des flammes vers l'étoile sont des courbes de Bézier.
7. **Couches d'effet** (comme les VFX de jeux) : un cœur blanc très lumineux, un halo coloré additif, un volume en aplats, des étincelles secondaires et des ondes de choc. Chaque temps fort combine au moins trois couches.
8. **Escalade lisible** : un battement par palier (bleu, violet, or, rouge), chacun plus fort que le précédent (amplitude ×1,25, onde plus large, flammes plus hautes). Il n'y a jamais de descente.
9. **Échelles extrêmes** : le contraste entre le détail proche (runes, braises) et le colossal (sigil de 60 blocs dans le ciel, dragon spectral de 50 blocs d'envergure, ailes de 40 blocs) donne l'effet « ultime ».
10. **Lisibilité à distance** : les grands éléments du ciel ont des silhouettes simples et très contrastées. Le spectateur à 12 chunks doit comprendre « quelque chose est invoqué là-haut ».

## 4. Éléments 3D (bones de la scène Blockbench)

| Groupe | Contenu | Échelle |
| --- | --- | --- |
| `flame_fire` … `flame_air` | Les six flammes (§ 1), chacune avec ses sous-os (langues, orbites, éclats) | 1 à 3 blocs de haut au repos, 6 au climax |
| `runes` | 24 runes au sol en anneau autour de l'estrade (rayon 4,5 blocs), qui s'allument une à une | plaques au sol |
| `floor_circle` | Cercle magique au sol, double anneau contrarotatif + 6 rayons vers les flammes | rayon 8 blocs |
| `stack_1` à `stack_3` | Cercles empilés au-dessus de l'autel (+10, +24, +44 blocs), contrarotatifs | rayons 5, 8, 12 blocs |
| `sky_sigil` | Le grand sigil du ciel, **au-dessus de la hauteur maximale** (+96 blocs au-dessus du sol de l'île, soit au-delà de y = 319 dans l'End) : trois anneaux, douze branches, un hexagramme | rayon 30 blocs |
| `pillar` | Colonne d'énergie de l'autel au sigil | 2 blocs de large, 96 de haut |
| `streams` | Six flux des flammes vers l'autel puis vers l'étoile | arcs |
| `star` | L'étoile d'invocation : deux octaèdres croisés + halo, cinq variantes de couleur (blanc, bleu, violet, or, rouge) + `star_ghost_1…4` (traînées) | 1,5 bloc |
| `waves` | Ondes de choc au sol et dans le ciel (anneaux plats qui s'élargissent) | jusqu'à 40 blocs |
| `shards` | 48 éclats de l'explosion, trajectoires balistiques | petits cubes |
| `cracks` | Fissures rouges du ciel (SSS) autour du sigil | plaques |
| `dragon` | Dragon spectral colossal (SS et SSS) qui tourne autour du sigil, ailes battantes | 50 blocs d'envergure |
| `wings` | La paire d'ailes spectrale révélée au-dessus de l'autel (en jeu : remplacée par la vraie paire tirée, à grande échelle), deux battements puis repli et dissolution | 40 blocs d'envergure |
| `feathers` | Plumes et pétales qui retombent en se balançant | petits cubes |

## 5. Timeline du master SSS (animation `summon_sss`)

Temps en secondes. Les paliers inférieurs coupent des blocs (§ 6). Le hit-stop est déjà compté dans les temps.

| T | Phase | Ce qui se passe |
| --- | --- | --- |
| 0,00 – 0,40 | **Silence** | Les flammes au repos baissent (inspiration), les runes s'éteignent |
| 0,40 – 1,90 | **Éveil** | Les six flammes s'embrasent une à une dans l'ordre feu → ténèbres → terre → eau → lumière → air (0,22 s d'écart) : anticipation (écrasement), jaillissement à 3 blocs avec dépassement, retombée à 2 blocs |
| 1,90 – 3,60 | **Charge** | Les 24 runes s'allument (70 ms d'écart, en spirale), le cercle au sol apparaît et tourne, les flammes se penchent vers l'autel, les six flux naissent et rampent jusqu'au cristal de l'autel |
| 3,60 – 4,00 | **Inspiration** | Tout se contracte : flammes basses, cercle qui rétrécit de 10 % |
| 4,00 | **Colonne** | La colonne jaillit jusqu'au ciel en 0,18 s ; onde au sol ; les cercles empilés s'ouvrent l'un après l'autre (0,12 s) ; le sigil du ciel se déploie (de 0 à 1,08 puis 1) |
| 4,60 – 6,20 | **Naissance de l'étoile** | Au centre du sigil, l'étoile se condense (éclats aspirés), anticipation, flash, anneau d'éveil |
| 6,20 – 10,40 | **Descente** | L'étoile descend en spirale autour de la colonne, avec sa traînée. À partir de 6,6, le **dragon spectral** surgit de l'horizon et tourne autour du sigil |
| 7,20 / 8,05 / 8,90 / 9,75 | **Battements** | Bleu, violet, or, rouge. À chaque battement : hit-stop 70 à 110 ms, changement de couleur, onde dans le ciel, les flammes flambent ensemble, puis secousse |
| 9,75 | **Fissure** | Au battement rouge, le ciel se fissure en rouge autour du sigil |
| 10,40 – 12,00 | **Absorption** | L'étoile se fige à 3 blocs au-dessus de l'autel et tremble. Les six flammes s'étirent en flux vers elle et rapetissent. Le dragon plonge en piqué derrière l'île |
| 12,00 | **Éclat** | Implosion (0,12 s), hit-stop 120 ms, explosion : 48 éclats, trois ondes de choc (rapide, moyenne, lente), flash vertical, le sigil vole en éclats et ses branches tournoient en s'éloignant |
| 12,40 – 16,40 | **Révélation** | Les ailes spectrales se déploient au-dessus de l'autel (+14 blocs) et font deux grands battements qui soufflent des plumes. Les flammes se rallument à la couleur du palier, deux fois plus hautes |
| 16,40 – 19,60 | **Retombée** | Les ailes se replient et se dissolvent vers le ciel, les cercles se referment en spirale, la colonne s'amincit, les plumes finissent de tomber |
| 19,60 – 21,00 | **Repos** | Les flammes reviennent au repos, les runes s'éteignent l'une après l'autre |

La boucle de repos des flammes est l'animation `idle` (2 s, en boucle), jouée en permanence quand aucune invocation n'est active.

## 6. Variantes par palier et ×10

Le master SSS contient tous les éléments. Les autres paliers en sont des coupes :

| Palier | Coupes |
| --- | --- |
| B | Pas de sigil ni de cercles empilés (colonne courte jusqu'à +24), étoile blanche née à +24, pas de battement, éclat petit (1 onde, 12 éclats), pas d'ailes : l'objet monte de l'autel |
| A | Comme B, avec un battement bleu et 2 ondes |
| S | Sigil à +64, deux battements, ailes à 60 % de taille |
| SS | Sigil complet, trois battements, dragon spectral sans plongée, ailes à 80 % |
| SSS | Tout |
| Mythiques | SSS jusqu'à 9,75, puis l'éclipse (Night : tout s'éteint, sauf les yeux de la flamme des ténèbres) ou le blanc (Light : la flamme de lumière avale le ciel), voir `GACHA_ANIMATIONS.md` § 6.6 et § 6.7 |
| ×10 | Dix étoiles naissent dans le sigil l'une après l'autre (couleur de leur propre tirage, arrivée plus forte selon le palier), descendent ensemble en spirale décalée et sont absorbées une à une. L'éclat prend la couleur de la meilleure ; les ailes sont celles du meilleur tirage |

## 7. Sons et musique à récupérer

Tous les sons sont positionnels à l'autel (sauf la musique de l'invocateur). La distance d'atténuation est indiquée.

| Temps | Id | Description (son professionnel visé) | Placeholder vanilla | Atténuation |
| --- | --- | --- | --- | --- |
| 0,40 → 1,90 | `altar.flame.ignite.<élément>` ×6 | Allumage de chaque flamme, une signature par élément : feu (souffle et crépitement), ténèbres (aspiration grave inversée), terre (roche qui se soulève), eau (gerbe et remous), lumière (carillon cristallin), air (bourrasque qui tourne) | `item.firecharge.use`, `entity.warden.sonic_charge`, `block.basalt.break`, `entity.player.splash`, `block.amethyst_block.chime`, `entity.breeze.idle_air` | 48 |
| 0 → fin | `altar.flame.loop.<élément>` | Boucle de chaque flamme au repos, très discrète | — | 16 |
| 1,90 → 3,60 | `altar.rune` ×24 | Tic cristallin par rune, la hauteur monte d'un demi-ton toutes les 4 runes | `block.note_block.chime` | 32 |
| 1,90 → 3,60 | `altar.charge` | Nappe qui monte (riser) de 1,7 s, bourdon grave et filtre qui s'ouvre | `block.beacon.ambient` | 64 |
| 4,00 | `altar.pillar` | Colonne d'énergie : impact grave + sifflement qui monte vers le ciel, 1,2 s | `block.beacon.activate`, `entity.lightning_bolt.thunder` (bas) | 160 |
| 4,10 → 4,50 | `altar.circle_open` ×4 | Ouverture de chaque cercle : « vwoom » métallique tournant, de plus en plus grave | `block.respawn_anchor.charge` | 96 |
| 4,60 → 6,20 | `altar.star_birth` | Condensation (aspiration), puis éclat cristallin pur | `block.amethyst_block.resonate`, `block.end_portal_frame.fill` | 128 |
| 6,20 → 10,40 | `altar.star_fall` (boucle) | Sifflement aérien de la descente, doppler selon la spirale | `entity.ender_eye.launch` | 96 |
| 6,60 | `altar.dragon` | Rugissement spectral lointain, réverbéré, puis battements d'ailes colossaux | `entity.ender_dragon.growl`, `entity.ender_dragon.flap` | 160 |
| 7,20 / 8,05 / 8,90 / 9,75 | `altar.escalate.a` / `.s` / `.ss` / `.sss` | Battements d'escalade : impact + accord qui monte d'une quinte à chaque palier, du plus léger au plus puissant | `block.amethyst_block.chime`, `ui.toast.challenge_complete`, `entity.ender_dragon.growl` | 128 |
| 9,75 | `altar.sky_crack` | Craquement immense qui court dans le ciel, en stéréo | `entity.lightning_bolt.thunder` | 160 |
| 10,40 → 12,00 | `altar.absorb` | Aspiration croissante + tremblement grave | `block.portal.trigger` | 96 |
| 12,00 | `altar.burst` | Implosion (coupure de 120 ms), puis explosion orchestrale : sub, cuivres, verre | `entity.generic.explode`, `entity.firework_rocket.large_blast` | 160 |
| 12,40 → 16,40 | `altar.wings` ×2 | Deux battements d'ailes colossaux, souffle de plumes | `entity.ender_dragon.flap` | 128 |
| 16,40 → 21,00 | `altar.settle` | Retombée : nappe qui se résout, carillons qui s'éteignent | `block.beacon.deactivate` | 64 |
| — | Musique `music.summon` | Thème d'invocation, pour l'invocateur seulement (catégorie MUSIC) : intro mystérieuse sur l'éveil, montée orchestrale sur la descente, tutti sur l'éclat, thème sur la révélation. 21 s, synchronisé sur T = 0. Variantes : `music.summon.short` (B, A), `music.summon.mythic.night` / `.light` | aucune | — |

Les ids sont à ajouter à `SOUND_DESIGN.md` § 6.4 (ils complètent ceux de l'animation d'interface).

## 8. Portage en jeu (après validation dans Blockbench)

1. **Lecteur d'animation Blockbench** en Java (`client/gacha/`) : il charge le `.bbmodel` (géométrie par `RigLoader`, animations par un lecteur de clés `position`, `rotation` et `scale`, avec interpolations `linear`, `catmullrom`, `bezier` et `step`).
2. **Passe de rendu** `RenderLevelStageEvent` (§ 2.1), avec le brouillard coupé.
3. **Paquet** `SummonShow` et registre client des invocations actives.
4. **Effets procéduraux** ajoutés par-dessus les clés : flicker des flammes, traînées de l'étoile, éclats et plumes simulés, hit-stop.
5. Caméra, cartes et musique pour l'invocateur.
6. Tests : solo, serveur dédié, deux clients (un invocateur, un spectateur à 12 chunks et y = 0), avec et sans Iris.
