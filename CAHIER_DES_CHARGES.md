# Mod d'ailes — Cahier des charges

Version du 20 octobre 2026 · zorago

## Vision et contraintes

Un mod d'ailes magiques pour NeoForge 1.21.1, pensées uniquement pour le vol (le combat est hors périmètre). Sa progression repose sur une collection et pousse à explorer tout le jeu vanilla.

Contraintes non négociables :

- **Standalone** : aucune dépendance, ni Curios, ni GeckoLib, ni lib de config tierce. Toutes les libs utiles sont écrites dans le mod.
- **Minimum d'override** : on passe par les events et extensions NeoForge, et les mixins ne servent qu'en dernier recours, documentés un par un.
- **Rendu** : les ailes sont faites de cubes (ModelPart vanilla) avec des RenderTypes vanilla, pour rester compatible avec Iris, les shaders et Sodium-like. Les effets passent par des particules classiques.
- **Multi et performance** : le serveur fait autorité sur la stamina, la collection et les stats. Les paquets réseau sont minimaux, et rien de coûteux ne tourne à chaque tick par joueur.
- **Publication** : CurseForge, avec la mention d'un développement en grande partie assisté par IA (code, textures, animations).

## Collection et interface

Les ailes ne sont pas des items portés mais des entrées de collection débloquées une fois pour toutes. Il n'y a donc ni slot ni Curios.

**Déblocage**

- On assemble ou on loote une paire d'ailes, qui reste un item temporaire. Un clic droit dessus consomme l'item, ajoute la paire à la collection et l'équipe automatiquement.
- Au tout premier déblocage (en pratique la paire commune de départ), le bouton « ailes » apparaît dans l'inventaire, avec l'écran Ailes et ses panneaux de tutoriel de base : modes de vol, touches, stamina, collection et stats.
- Changer de paire est instantané et sans contrainte. On peut aussi ne rien équiper. Une paire débloquée ne se retire pas et ne s'échange pas.

**Bouton d'inventaire**

- Une icône en forme d'ailes placée **sous l'inventaire**, alignée sur son bord gauche (`x ≥ leftPos`). La bande à gauche de l'inventaire est réservée aux favoris et à l'historique de JEI (voir `docs/ANALYSE_REFERENCES.md`).
- Elle est injectée via `ScreenEvent.Init`, sans mixin, et repositionnée sur `leftPos` à chaque ouverture ou fermeture du livre de recettes, qui décale l'inventaire.

**Écran Ailes**

- À gauche, la liste de la collection, avec rareté, nom et paire équipée.
- À droite, le panneau d'aperçu : une moitié pour le joueur, l'autre pour les stats de la paire (stats équipées) et le total des bonus globaux.
- **Une seule visualisation** : le joueur debout et immobile en pose idle, les ailes dans le dos animées d'un léger mouvement. La caméra tourne lentement autour à 360° en boucle.
- Le rendu du joueur passe par la fonction vanilla de rendu d'entité en GUI, avec une rotation pilotée par le temps.
- L'écran est purement client. Équiper envoie un seul paquet au serveur, qui valide et resynchronise.

## Système de stats

Chaque paire a deux jeux de stats, comme dans Aion 2. Les **stats équipées** ne s'appliquent que quand la paire est portée. Les **stats globales** sont acquises dès le déblocage et s'additionnent pour toute la collection, quelle que soit la paire portée.

| Stat | Effet en vol | Unité interne |
| --- | --- | --- |
| Stamina | Réserve d'énergie consommée par les battements et le maintien en l'air | points |
| Accélération | Gain de vitesse par battement, et puissance du freinage (même stat) | blocs/s par battement |
| Vitesse max | Vitesse de croisière atteignable en battant des ailes, hors dive | blocs/s |
| Maniabilité | Vitesse de rotation (lacet et tangage) avec ZQSD | degrés/s |
| Stabilisation | Qualité du mode stabilisé, de 0 à 10 (10 = vol créatif) | 0–10, plafonnée à 10 |

**Règle d'équilibrage**

- La somme des stats globales de toutes les paires commune, rare, épique et légendaire (y compris les globaux des légendaires) égale les stats équipées d'une paire légendaire.
- C'est voulu : une légendaire équipée avec la collection complète vaut donc 2× une légendaire.
- Le nombre de paires par rareté n'est pas fixé. Il sera décidé selon l'ambiance de la collection, puis les globaux seront répartis en conséquence.
- Stabilisation : avec la collection complète (hors mythique), toutes les paires atteignent 10/10. Le global de stabilisation compense donc l'écart de base de chaque paire.
- Chaque paire est une identité, à la manière des dragons de HTTYD : un gros bonus se paie par un sacrifice. Les ailes d'ange à plumes ont beaucoup d'accélération, de freinage et de vitesse, mais 2/10 en stabilisation. Les ailes d'abeille montent à 8/10 en stabilisation, mais sont lentes et freinent mal.

**Paire commune de départ**

- C'est la seule paire de rareté **commune** : les ailes les plus simples, celles qui débloquent le mod et la progression.
- Ses stats équipées sont basiques. Ses stats globales existent mais sont très faibles.
- Recette à la table d'assemblage, uniquement avec des ressources vanilla de base : 1 plume par aile (2 au total), 1 élytre en cœur (enchantée ou non), 1 cuir en harnais.
- Les tonneaux de la forge fournissent plumes et cuir en grande quantité. L'élytre vient des cités de l'End, sur le trajet.

**Paire mythique**

- Elle demande un craft géant qui couvre l'exploration vanilla : souffle du dragon, étoile du Nether, éclats d'écho, archéologie et sniffer, entre autres. La liste reste à écrire.
- Ses stats équipées sont au niveau moyen d'une légendaire, avec un profil propre. Ce n'est pas une paire « best in slot ».
- Elle débloque deux bonus globaux : la **stamina infinie** et la **transmog** (l'apparence de n'importe quelle paire débloquée sur les stats d'une autre).
- Toutes les valeurs vivent dans des fichiers JSON de datapack, pour que l'équilibrage et l'ajout de paires se fassent sans recompiler.

## Mode stabilisé

C'est le mode de vol stationnaire pour builder : un vol créatif plus ou moins perturbé par les battements d'ailes, selon la stat de stabilisation S (de 0 à 10).

- **Contrôles** : ZQSD pour le plan horizontal, saut pour monter, sneak pour descendre. La caméra reste en première personne.
- **Perturbation** : chaque battement ajoute une oscillation verticale et une petite dérive, d'amplitude proportionnelle à (10 − S). À 10/10, plus aucune perturbation.
- **Inertie** : toujours présente mais légère, même à 10/10. Lâcher la touche donne un freinage rapide et non instantané.

**Montée en vitesse et petits déplacements au tap**

Pour qu'un tap rapide déplace de quelques pixels seulement, la courbe doit être **plate au départ**. Une courbe logarithmique fait l'inverse : sa pente est maximale à t = 0, donc un tap donnerait le plus gros gain de vitesse relatif. Montée en ease-in proposée :

```latex
v(t) = v_{max} \cdot \left(\frac{t}{T}\right)^{p}, \quad t \le T, \quad p \approx 2
```

Ici T est le temps pour atteindre la vitesse max (il dépend de S), et p règle la finesse au tap. Un tap de 2 ticks avec T = 20 ticks donne 1 % de v max, soit un déplacement quasi nul. Le spam permet donc un placement précis au bloc près. Alternative : une courbe en S (smoothstep), avec un départ plat et une arrivée douce sur v max. Choix en attente.

## Mode déplacement

C'est le vol de voyage, inspiré de Wuthering Waves : la direction se contrôle au clavier et la souris reste libre pour la caméra.

**Activation**

- Une touche dédiée, **Alt gauche** par défaut. Le combo sprint + sneak reste une option de config désactivée par défaut, parce qu'en vanilla le sneak coupe le sprint et qu'en mode stabilisé, le sneak sert à descendre.
- **Transition caméra « laissée derrière »** : au déclenchement, la caméra se détache de la tête et reste figée à sa position dans le monde. Le joueur donne un gros battement qui le projette à la **V min de la paire équipée**, et s'éloigne de la caméra. La caméra rattrape ensuite en douceur sa position de suivi en troisième personne derrière le joueur.
- Ordre conseillé : position figée pendant environ 4 ticks, puis rattrapage amorti (ressort critique) sur environ 10 ticks. Les deux durées sont réglables en config client.

**Contrôles**

- Z et S règlent le tangage (piquer et cabrer), avec une option pour inverser l'axe Y. Q et D règlent le lacet, avec une inclinaison visuelle du corps dans les virages.
- La vitesse de rotation vaut la stat de maniabilité. La souris pilote uniquement la caméra en orbite libre.
- Toutes les touches sont rebindables dans les contrôles vanilla.

**Accélération par battements**

- Maintenir sprint déclenche des battements à cadence fixe, environ 2 par seconde selon la paire. Chaque battement coûte de la stamina.
- Chaque battement donne une impulsion, puis la traînée réduit la vitesse jusqu'au battement suivant. La courbe obtenue est en dents de scie qui montent vers V max, pas une rampe linéaire.

```latex
\Delta v_{battement} = A \cdot \left(1 - \frac{v}{V_{cap}}\right), \quad V_{cap} = V_{max} + (V_{term} - V_{max}) \cdot \max(0, \sin\theta)
```

A est la stat d'accélération. À plat ou en montée, V cap = V max : les battements approchent la vitesse max sans la dépasser. En piqué, V cap glisse vers V term : battre des ailes ajoute à la gravité, mais chaque battement rapporte moins à l'approche de la vitesse terminale.

**Freinage**

- Maintenir la touche de frein ouvre les ailes en parachute, comme Krokmou dans la scène du vol d'essai de HTTYD 1. Des traînées blanches partent du bout des ailes.
- La décélération est égale à la stat d'accélération : les grandes ailes à plumes freinent fort, les ailes d'abeille très peu.
- **Transition caméra « balancée dans la tête »** : sous un seuil de vitesse, un dernier battement stoppe net le joueur. La caméra, toujours en troisième personne, est projetée vers l'avant et rentre dans la tête du joueur (interpolation rapide en ease-in, environ 5 ticks, puis bascule en première personne). Le vol passe alors en mode stabilisé.

## Dive, physique et vitesse terminale

La vitesse se joue sur un échange entre altitude et vitesse : piquer accélère au-delà de V max, et remonter redépense cette vitesse en gravité et en friction jusqu'au retour à V max.

Le long de la trajectoire, où θ est l'angle de piqué (positif nez vers le bas) :

```latex
\frac{dv}{dt} = g \sin\theta \; + \; a_{batt}(t) \; - \; k \, v^2 \; - \; k_{sur} \cdot \max(0,\, v - V_{cap})
```

- **a_batt(t)** : les impulsions des battements (stat d'accélération, voir la formule de V cap). Elles s'ajoutent aussi en piqué : accélération + gravité − résistance de l'air.
- **g sin θ** : en piqué, la gravité accélère ; en montée, elle freine. C'est ce qui donne le « cabrer pour reprendre de l'altitude ».
- **k v²** : la résistance de l'air. Son coefficient est calé pour qu'un piqué vertical plafonne à la vitesse terminale V term.
- **k_sur** : une friction supplémentaire au-dessus de V cap, qui ramène doucement à la vitesse de croisière une fois redressé.

```latex
V_{term} = \sqrt{g / k}
```

**Vitesse terminale (config serveur)**

- Valeur par défaut proposée : 55 m/s (environ 2,75 blocs/tick), soit la chute libre humaine à plat. Un piqué tête la première monte vers 80 à 90 m/s.
- Attention à la génération de chunks : l'élytre avec fusées, autour de 30 à 35 m/s, met déjà les serveurs sous tension. Au-delà de 55 m/s, le serveur doit charger plus de terrain qu'il n'en génère. La config affichera un avertissement au-dessus de cette valeur.
- La physique est écrite en m/s puis convertie en blocs/tick (1 bloc = 1 m, 20 ticks/s). On n'utilise pas la gravité d'entité vanilla (0,08 bloc/tick², environ 32 m/s²), trop forte pour un vol réaliste.

**Freinage en piqué**

Maintenir le frein en fonçant vers le sol applique une décélération égale à la stat d'accélération, en concurrence directe avec g sin θ. Une paire faible en accélération ne peut pas compenser un piqué raide : il faut redresser tôt.

**Décrochage**

- Sous V min sans freiner (en cabrant trop longtemps, par exemple), les ailes décrochent : plus de portance, le joueur tombe et prend de la vitesse.
- Pour récupérer, il faut piquer pour retrouver un angle de plané et repasser au-dessus de V min.
- Sinon, il s'écrase au sol avec des dégâts proportionnels à la vitesse d'impact :

```latex
d\acute{e}g\hat{a}ts = c \cdot \max(0,\; v_{impact} - v_{seuil})
```

Ici v impact est la composante de vitesse perpendiculaire au sol, et c et v seuil viennent de la config serveur (par exemple v seuil = 10 m/s). Un atterrissage rasant à grande vitesse reste donc survivable, contrairement à un piqué droit dans le sol.

## Stamina et chute

À 0 de stamina, les ailes restent actives mais ne peuvent plus battre. Le comportement dépend du mode en cours.

| Situation à 0 stamina | Accélérer | Planer | Freiner | Changer de mode | Résultat |
| --- | --- | --- | --- | --- | --- |
| Mode déplacement | Non | Oui | Oui | Non | Il plane et freine jusqu'au sol (le décrochage s'applique toujours) |
| Mode stabilisé | Non | Non | Non | Non | Il tombe (pas d'inertie pour le porter), avec les 2 sauts de secours |

- **Consommation** : chaque battement a un coût. Le maintien en mode stabilisé coûte un débit continu. Le plané, le piqué et le freinage ne coûtent rien.
- **Récupération** : au sol, plus lentement en vol plané. La paire mythique rend la stamina infinie (bonus global).
- **Sauts de secours** : 2 sauts en l'air, chacun remet la vitesse verticale à 0 et annule **totalement** les dégâts de chute accumulés. Ils ne se rechargent qu'au contact du sol. Le premier part souvent par réflexe, le second sert à se sauver vraiment.
- **Autorité** : la stamina est calculée sur le serveur. Le client en affiche une prédiction, corrigée par synchronisation quand ça change de façon significative, et non à chaque tick.

## Craft : table d'assemblage

Toutes les paires se craftent dans la table d'assemblage de la forge de l'End, à partir de 4 pièces : un harnais, un cœur, une aile gauche et une aile droite. Ces pièces sont des items du mod, sauf pour la paire commune de départ, dont les 4 emplacements prennent directement des items vanilla (plume, plume, élytre, cuir).

- **Craft** : à partir d'items vanilla et/ou moddés. Les ingrédients moddés passent par les tags communs `c:`, ce qui permet de fonctionner avec tout mod qui les fournit, sans dépendance.
- **Drops de mobs** : sur une liste de mobs définie, seulement si le coup fatal est porté avec un item imprégné d'« énergie ailée ».
- **Essences de blocs** : sur une liste de blocs vanilla définie, seulement si le bloc est cassé avec un item imprégné. Les essences servent aux différentes paires.
- **Liberté totale** : l'énergie ailée se pose sur n'importe quel item. Le joueur peut tuer un mob avec un escalier ou miner avec une épée s'il le veut.

**Énergie ailée**

C'est un Data Component custom, posé ou retiré sur n'importe quel item à la table d'assemblage. On l'a préféré à un vrai enchantement, qui interagirait avec l'enchanteur, la meule, l'enclume et les mods d'enchantement. Il affiche quand même l'éclat d'enchantement et une ligne de tooltip.

**Sources de loot** : la liste « mob ou bloc → pièce ou essence, avec chance » vit dans un registre datapack du mod, `simplywings:loot_source`, synchronisé au client. Les Global Loot Modifiers ne sont pas envoyés au client, alors que JEI a besoin de ces données (voir « Intégration JEI »).

Les loots passent par des Global Loot Modifiers NeoForge (data-driven, sans mixin) avec une condition maison, qui lisent ce registre. Elle exige deux choses : que l'item tenu porte l'énergie ailée, et que le mob ou le bloc figure dans la liste de la pièce. Sans ces deux conditions, les loot tables restent intactes. Pour les mobs, la condition lit l'item en main principale du tueur au moment du kill.

## Forge d'ailes dans l'End

La forge est la seule structure du mod. Elle n'est pas générée par le worldgen. Elle apparaît à la mort du **deuxième** dragon, puis se répare à chaque mort suivante, toujours au même endroit.

- **Déclencheur** : `LivingDeathEvent` d'un `EnderDragon` dans l'End, côté serveur. La forge apparaît seulement si `EndDragonFight.hasPreviouslyKilledDragon()` est déjà vrai à ce moment-là, c'est-à-dire si un dragon a déjà été tué avant celui-ci. Le premier dragon ne fait donc rien, et la forge apparaît après le premier respawn avec les 4 cristaux. Ça fonctionne aussi sur les anciens mondes, et avec YUNG's Better End Island, qui met ce flag à jour lui aussi.
- **Placement** : une structure NBT faite main (StructureTemplate vanilla), posée **dans le vide, hors de l'île centrale**, à mi-parcours entre l'île centrale et les îles extérieures. Rayon par défaut ≈ 512 blocs (config serveur), angle dérivé de la seed du monde. Elle est hors de portée de tout ce que les mods d'End modifient autour de l'île centrale (YUNG's Better End Island, BetterEnd, Nullscape…).
- **Hauteur** : un Y fixe (config serveur). La structure flotte et apporte sa propre plateforme : on ne lit pas de heightmap et on ne pose pas de fondation. Position et hauteur sont mémorisées dans les SavedData du monde à la première pose, pour que les réparations retombent exactement au même endroit, même si la config change ensuite.
- **Chargement** : les quelques chunks de l'emprise sont chargés (et générés si besoin) au moment de la pose. C'est un coût unique et ponctuel.
- **Réparation** : seuls les blocs non-air du template sont reposés et écrasent ce que le joueur y a mis. Les positions air du template ne touchent jamais aux blocs des joueurs, ni au terrain qu'un mod d'End aurait généré à cet endroit.
- La structure contient la table d'assemblage et **de nombreux tonneaux de plumes et de cuir**. Ils utilisent une loot table du mod (`simplywings:chests/forge_supplies`) avec de grosses quantités, de quoi assembler la paire commune de départ. À chaque réparation, les tonneaux sont reposés avec leur loot table : ce sont des ressources sans valeur, le farm n'est pas un problème.
- **Accès** : en élytre. Le joueur passe par une passerelle de l'End, récupère une élytre dans une cité, puis revole vers la forge grâce au compas. Cette élytre sert aussi de cœur à la paire de départ.

**Compas de la forge**

- À chaque mort de dragon qui pose ou répare la forge, **tous les joueurs présents sur l'île centrale** reçoivent un item `simplywings:forge_compass`. « Sur l'île centrale » = dans l'End, à moins de 192 blocs horizontaux de (0, 0), le même rayon que la barre de boss vanilla, réglable en config serveur.
- Il pointe vers la forge grâce au Data Component vanilla `lodestone_tracker` (GlobalPos de l'End, non lié à une magnétite). L'aiguille passe par `ItemProperties.register` et `CompassItemPropertyFunction` côté client, sans mixin. Hors de l'End, il tourne dans le vide comme un compas vanilla.

## Intégration JEI

JEI est une intégration **optionnelle** : le mod compile contre l'API JEI en `compileOnly`, déclare JEI en dépendance `optional` dans `neoforge.mods.toml`, et la classe `@JeiPlugin` n'est chargée que par JEI. Sans JEI, rien ne change.

| Onglet JEI | Contenu | Source des données |
| --- | --- | --- |
| Table d'assemblage | Assemblage d'une paire à partir de harnais, cœur, aile gauche et aile droite, plus le craft des pièces | Recettes `RecipeType` du mod (synchronisées au client en 1.21.1) |
| Énergie ailée | Poser ou retirer l'énergie ailée sur un item quelconque | Recette spéciale, affichée avec quelques items d'exemple |
| Drops de mobs | Mob (rendu de l'entité), pièce lâchée, chance, condition « coup fatal avec un item imprégné » | Registre `simplywings:loot_source` |
| Essences de blocs | Bloc, essence lâchée, chance, condition « cassé avec un item imprégné » | Registre `simplywings:loot_source` |

La table d'assemblage est déclarée comme catalyseur de ses onglets. Une page d'info JEI sur la table et sur le compas de la forge explique comment trouver la forge (deuxième dragon, compas).

## Architecture technique NeoForge

Presque tout passe par des events et API NeoForge natives. Un seul mixin est probable (découplage souris), plus un access transformer pour l'anti-kick serveur.

| Module | Rôle | Mécanisme | Côté |
| --- | --- | --- | --- |
| Définitions d'ailes | Stats, raretés, pièces, apparence | Registre datapack custom, synchronisé au client | Commun |
| Données joueur | Collection, paire équipée, transmog, stamina, sauts de secours | Data Attachment avec codec, conservé à la mort | Serveur (autorité) |
| Réseau | Équiper, état de vol, stamina | CustomPacketPayload, envoyé seulement sur changement | Les deux |
| Physique de vol | Modes stabilisé et déplacement, dive, décrochage | Tick client du joueur local (le mouvement est client-autoritaire en vanilla) | Client |
| Validation serveur | Pas de kick « flying is not enabled », dégâts de chute et de crash | Access transformer sur le compteur de temps en l'air + LivingFallEvent | Serveur |
| Caméra | 3e personne, orbite libre, transitions « laissée derrière » et « dans la tête » | ViewportEvent.ComputeCameraAngles + caméra détachée interpolée | Client |
| Découplage souris | La souris tourne la caméra, pas le corps | Mixin probable sur la rotation joueur (à confirmer en analysant Shoulder Surfing) | Client |
| Rendu des ailes | Cubes, matériaux solide, transparent, émissif | RenderLayer ajouté au joueur, RenderTypes vanilla | Client |
| Animation | Idle, battements, repli, parachute | Animation procédurale (phase de battement + interpolation), mini-lib maison | Client |
| Joueurs distants | Voir les ailes et l'état des autres | Envoi aux joueurs qui suivent l'entité, interpolation locale | Client |
| Énergie ailée et loots | Imprégnation, drops de mobs, essences de blocs | Data Component + Global Loot Modifiers + condition de loot maison | Serveur |
| Table d'assemblage | Assembler une paire, poser ou retirer l'énergie ailée | Block + BlockEntity + Menu + recettes custom (RecipeType) | Les deux |
| Interface | Bouton inventaire, écran Ailes, HUD stamina | ScreenEvent.Init, Screen custom, RegisterGuiLayersEvent | Client |
| Config | V term, coûts, dégâts de crash, transitions caméra | ModConfigSpec (intégré à NeoForge, pas une lib tierce) | Serveur et client |
| Structure | Forge de l'End (dans le vide, à partir du 2e dragon) et compas | `LivingDeathEvent` du dragon + StructureTemplate + SavedData + `lodestone_tracker` | Serveur |
| Intégration JEI | Onglets assemblage, énergie ailée, drops de mobs, essences de blocs | Plugin `@JeiPlugin` optionnel, API JEI en `compileOnly` | Client |

**Performance**

- Les stats effectives (équipées + globales) sont mises en cache et recalculées seulement quand la collection ou la paire équipée change.
- Rien ne part sur le réseau à chaque tick. Les joueurs distants reçoivent un état compact (mode, phase, vitesse) à faible fréquence et interpolent.
- Les particules sont limitées par une option client et désactivées automatiquement au-delà d'une certaine distance de la caméra.

## Décisions et points ouverts

Les décisions du 20 octobre sont intégrées dans les sections plus haut. Les points ouverts restants ne bloquent pas le début de l'implémentation.

- [ ] **Liste des paires** : nombre par rareté, noms et profils de stats, à fixer selon l'ambiance voulue.
- [ ] **Recette mythique** : la grande liste d'items d'exploration vanilla.
- [ ] **Mobs et blocs sources** : quelle pièce ou essence tombe de quel mob ou bloc.
- [ ] **Kills à distance** : un arc ou un trident imprégné compte-t-il ? Ce sera alors l'arme du tireur, pas le projectile.
- [ ] **Courbe du mode stabilisé** : ease-in ou smoothstep.
- [ ] **Stats de la paire commune** : valeurs exactes, à fixer avec la liste des paires.
- [x] Accès à la forge : en élytre, tonneaux de plumes et de cuir sur place, paire commune de départ.
- [x] Compas : tous les joueurs de l'île centrale, à chaque mort de dragon qui pose ou répare la forge.
- [x] Combat abandonné, hors périmètre.
- [x] Mod id : `simplywings`.
- [x] Bouton d'inventaire : sous l'inventaire.
- [x] Touche du mode déplacement : Alt gauche par défaut, rebindable.
- [x] JEI : plugin optionnel avec tous les crafts et les loots de mobs et de blocs.
- [x] Forge : dans le vide, à mi-parcours vers les îles extérieures, à partir du deuxième dragon, avec un compas.

## Mods à fournir pour analyse

Rien n'est repris tel quel. Chaque jar sert à comprendre comment un problème précis a été résolu, avant d'écrire notre propre version.

| Mod (jar NeoForge 1.21.1 si possible) | Ce qu'on y cherche |
| --- | --- |
| Better Flight Revived | Contournement du kick serveur, synchro des battements, HUD de stamina |
| Shoulder Surfing Reloaded | Découplage caméra / corps, et nombre minimal de mixins pour y arriver |
| Do a Barrel Roll (Fabric accepté) | Maths de contrôle, roll caméra, lissage des rotations |
| Caelus API | Autoriser l'état de vol sans élytre dans le slot torse |
| Wings (fork communautaire 1.21.1) | RenderLayer sur le joueur, animation procédurale des ailes |
| JEI ou EMI (celui du pack) | Vérifier que la zone du bouton d'inventaire reste libre |

Les jars vont dans `reference-mods/`, avec la liste des mods du modpack, pour vérifier les conflits probables : shaders (Iris), moteurs de rendu et mods qui modifient l'End.
