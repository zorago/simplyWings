# Mod d'ailes — Cahier des charges

Version du 21 octobre 2026 · zorago

## Vision et contraintes

Un mod d'ailes magiques pour NeoForge 1.21.1, pensées uniquement pour le vol (le combat est hors périmètre). Sa progression repose sur une collection et pousse à explorer tout le jeu vanilla.

Contraintes non négociables :

- **Standalone** : aucune dépendance, ni Curios, ni GeckoLib, ni lib de config tierce. Toutes les libs utiles sont écrites dans le mod.
- **Minimum d'override** : on passe par les events et extensions NeoForge, et les mixins ne servent qu'en dernier recours, documentés un par un.
- **Rendu** : les ailes sont faites de cubes (ModelPart vanilla) avec des RenderTypes vanilla, pour rester compatible avec Iris, les shaders et Sodium-like. Les effets passent par des particules classiques.
- **Multi et performance** : le serveur fait autorité sur la stamina, la collection et les stats. Les paquets réseau sont minimaux, et rien de coûteux ne tourne à chaque tick par joueur.
- **Publication** : CurseForge, avec la mention d'un développement en grande partie assisté par IA (code, textures, animations).
- **Langue** : le mod est entièrement en anglais (code, identifiants, config, UI, HUD, noms d'items, tooltips, sous-titres) et n'a qu'une langue, `en_us`, sans traduction française. Seuls les documents de conception du repo sont en français.

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

- Ouvert par le bouton d'inventaire ou la touche J (rebindable).
- À gauche, la liste de toutes les paires du registre, avec rareté, nom et paire équipée. Les paires non débloquées sont grisées ; les mythiques non débloquées s'affichent en « ??? ».
- À droite, le panneau d'aperçu : une moitié pour le joueur, l'autre pour les stats de la paire (stats équipées) et le total des bonus globaux.
- **Une seule visualisation** : le joueur debout et immobile en pose idle, les ailes dans le dos animées d'un léger mouvement. La caméra tourne lentement autour à 360° en boucle.
- Le rendu du joueur passe par la fonction vanilla de rendu d'entité en GUI, avec une rotation pilotée par le temps.
- L'écran est purement client. Équiper envoie un seul paquet au serveur, qui valide et resynchronise.

## Système de stats

Chaque paire a deux jeux de stats, comme dans Aion 2. Les **stats équipées** ne s'appliquent que quand la paire est portée. Les **stats globales** sont acquises dès le déblocage et s'additionnent pour toute la collection, quelle que soit la paire portée.

Sept stats, chacune notée de 0 à 100. Le menu en affiche cinq, sur 10 (1 carreau = 10 points) ; Plané et Portance restent internes. La config serveur (section `[stats]`) traduit chaque score en valeur physique (valeur à 0, valeur à 100).

| Stat | Effet en vol |
| --- | --- |
| Endurance | Réserve de stamina consommée par les battements et le stationnaire |
| Accélération | Gain de vitesse par seconde de battement, puissance du frein et hauteur du saut ailé |
| Vitesse | Vitesse de croisière atteignable en battant des ailes, hors piqué |
| Maniabilité | Vitesse de rotation (lacet et tangage) avec ZQSD |
| Stabilité | Qualité du mode stabilisé (100 = vol créatif) |
| Plané | Traînée de l'air : distance de plané et perte de vitesse entre deux battements |
| Portance | Vitesse de décrochage |

**Règle d'équilibrage**

- Une légendaire a par définition au moins une stat visible maxée à 100.
- Avec la collection complète (hors mythiques), toutes les paires sont à 100 dans toutes les stats. Pour chaque stat, la somme des globaux vaut donc 100 − le score le plus bas parmi les paires. Le score effectif est plafonné à 100, puis reçoit le bonus `overcap` de la paire (seulement les mythiques).
- Les valeurs exactes et leur répartition sont suivies dans `BALANCE.md`, le catalogue des paires dans `WINGS.md`.
- Chaque paire est une identité, à la manière des dragons de HTTYD : un gros bonus se paie par un sacrifice. Les ailes d'ange ont l'endurance maxée et beaucoup de vitesse, mais 1/10 en stabilité. Les ailes d'abeille montent à 6,5/10 en stabilité, mais sont lentes.

**Paire commune de départ**

- C'est la seule paire de rareté **commune** : les ailes les plus simples, celles qui débloquent le mod et la progression.
- Ses stats équipées sont basiques. Ses stats globales existent mais sont très faibles (valeurs dans `BALANCE.md`).
- Recette à la table d'assemblage, uniquement avec des ressources vanilla de base : 1 plume par aile (2 au total), 1 élytre en cœur (enchantée ou non), 1 cuir en harnais. Elle ne coûte pas de Cloud Essence.
- Les tonneaux de la forge fournissent plumes et cuir en grande quantité. L'élytre vient des cités de l'End, sur le trajet.

**Paires mythiques : Night et Light**

- Deux paires, les ailes des Furies Nocturne et Éclair de *Dragons* (détail dans `WINGS.md`). Tant qu'elles ne sont pas débloquées, le menu les affiche en « ??? ».
- Stats équipées à 100 partout, plus un bonus `overcap` de +20 en vitesse (74 m/s). Elles ne donnent aucun bonus de collection.
- Posséder **Night** donne la **stamina infinie**, quelle que soit la paire équipée.
- Posséder **Light** débloque la **transmog** : un troisième bouton, « Equip appearance », apparaît dans l'écran Ailes. Il pose l'apparence de la paire sélectionnée (débloquée) sur la paire équipée ; sur la paire déjà portée en apparence, il devient « Reset appearance ». L'apparence couvre tout le visuel : modèle, textures, couleur, profil d'animation, queue et **rythme** des battements (cadence en déplacement et en stationnaire, vibration continue ou non), même si ça ne colle pas au ressenti de vol. Les ailes battent en boucle à la cadence de l'apparence tant que la paire équipée bat. La physique, les stats et les pouvoirs des Furies (cri, charge, dash) restent ceux de la paire équipée. La cohérence n'est pas recherchée : c'est un bonus de fin de partie. Elle est enregistrée côté serveur, conservée quand on change de paire, et envoyée aux joueurs proches avec l'état de vol.
- Chacune demande un craft géant qui couvre l'exploration vanilla : souffle du dragon, étoile du Nether, éclats d'écho, archéologie et sniffer, entre autres, et 1 000 de Cloud Essence à l'assemblage. La liste reste à écrire dans `CRAFTING.md`.
- Toutes les valeurs vivent dans des fichiers JSON de datapack, pour que l'équilibrage et l'ajout de paires se fassent sans recompiler.

## Mode stabilisé

C'est le mode de vol stationnaire pour builder : un vol créatif plus ou moins perturbé par les battements d'ailes, selon la stat de stabilisation S (de 0 à 10).

- **Entrée** : double tap très rapide du saut en l'air (`doubleTapTicks`, 4 ticks, config client). Le saut ailé du premier tap est alors remboursé.
- **Contrôles** : ZQSD pour le plan horizontal, saut pour monter, sneak pour descendre. La caméra reste en première personne. La vitesse (`hover_speed`) et la cadence (`hover_rate`) sont propres à chaque paire, dans son JSON.
- **Perturbation** : chaque battement ajoute une oscillation verticale et une petite dérive, d'amplitude proportionnelle à (10 − S). À 10/10, plus aucune perturbation.
- **Inertie** : toujours présente mais légère, même à 10/10. Lâcher la touche donne un freinage rapide et non instantané.

**Montée en vitesse et petits déplacements au tap**

Pour qu'un tap rapide déplace de quelques pixels seulement, la courbe doit être **plate au départ**. Une courbe logarithmique fait l'inverse : sa pente est maximale à t = 0, donc un tap donnerait le plus gros gain de vitesse relatif. Montée en ease-in proposée :

```latex
v(t) = v_{max} \cdot \left(\frac{t}{T}\right)^{p}, \quad t \le T, \quad p \approx 2
```

Ici T est le temps pour atteindre la vitesse max (il dépend de S), et p règle la finesse au tap. Un tap de 2 ticks avec T = 20 ticks donne 1 % de v max, soit un déplacement quasi nul. Le spam permet donc un placement précis au bloc près. Alternative : une courbe en S (smoothstep), avec un départ plat et une arrivée douce sur v max.

Décision : les deux courbes existent, au choix dans la config client (`stabilizedCurve`, `EASE_IN` par défaut). T vaut `rampTicks` (8 ticks) à stabilité 10, plus `rampTicksPerMissingPoint` (1,5 tick) par point manquant (config serveur).

## Mode déplacement

C'est le vol de voyage, inspiré de Wuthering Waves : la direction se contrôle au clavier et la souris reste libre pour la caméra.

**Activation**

- Une touche dédiée, **Alt gauche** par défaut, rebindable. En l'air, elle bascule instantanément entre stabilisé et déplacement.
- **Envol chargé** : au sol, hors stationnaire, maintenir Alt 1 s (`takeoffChargeTicks`) lance directement en mode déplacement, à l'arrêt, en marchant ou en courant, avec un angle de montée de `takeoffPitch` (40°). Alt est ensuite ignoré 1,5 s, pour éviter un passage accidentel en stationnaire.
- **Saut ailé** : un saut en l'air coûte `airJumpCost` (20) de stamina, sans limite de nombre. Sa vitesse verticale = `airJumpBase` + gain d'un battement × `airJumpPerBeatGain`.
- **Transition caméra « laissée derrière »** : au déclenchement, la caméra se détache de la tête et reste figée à sa position dans le monde. Le joueur part à `launchSpeedFraction` × V max (0,5), avec un premier battement disponible tout de suite, et s'éloigne de la caméra. La caméra rattrape ensuite en douceur sa position de suivi en troisième personne derrière le joueur.
- Position figée pendant 4 ticks (`freezeTicks`), puis rattrapage amorti (ressort critique) sur 10 ticks (`catchUpTicks`), en config client.

**Contrôles**

- ZQSD déplacent un point visé (Z et S en tangage, Q et D en lacet), à la vitesse de la stat de maniabilité. Le cap du joueur suit ce point avec un ressort critique, sans rotation directe, avec une inclinaison visuelle du corps dans les virages. Option client pour inverser le tangage (`invertPitch`).
- La souris pilote uniquement la caméra en orbite libre.
- Toutes les touches sont rebindables dans les contrôles vanilla. Le frein a sa propre touche, non assignée par défaut : c'est alors le sneak.

**Accélération par battements**

- Maintenir sprint déclenche des battements à la cadence de la paire (`flap_rate` dans son JSON, de 0,5 à 3 par seconde pour les ailes qui battent). Le coût en stamina par seconde est le même pour toutes les paires (`flapCost` pour un battement de référence à 2 par seconde).
- Chaque battement donne une impulsion, puis la traînée réduit la vitesse jusqu'au battement suivant. La courbe obtenue est en dents de scie qui montent vers V max, pas une rampe linéaire.
- Gain d'un battement = stat d'accélération ÷ `flap_rate`. À partir de 8 battements par seconde (insectes, colibri, Dédale), la poussée devient continue, sans paquet par battement.

```latex
\Delta v_{battement} = A \cdot \left(1 - \left(\frac{v}{V_{cap}}\right)^{3}\right), \quad V_{cap} = V_{max} + (V_{term} - V_{max}) \cdot \max(0, \sin\theta)
```

A est la stat d'accélération (gain d'un battement à basse vitesse). À plat ou en montée, V cap = V max : le gain reste presque constant puis s'écrase près de la vitesse max (exposant 3), ce qui donne 4 à 5 battements pour l'atteindre. Au-dessus de V max, battre ne sert plus à rien : en piqué, sprint replie les ailes en faucon (traînée réduite) pour foncer vers V term.

La traînée k dépend de l'angle : faible à plat (`glideDragSpeed`, 150 m/s par défaut, pour que la vitesse ne fonde pas entre deux battements), forte en piqué ailes ouvertes (`diveDragSpeed`, 60 m/s). Seul le repli faucon dépasse cette vitesse, jusqu'à V term (110 m/s par défaut).

**Freinage**

- Maintenir la touche de frein ouvre les ailes en parachute, comme Krokmou dans la scène du vol d'essai de HTTYD 1. Des traînées blanches partent du bout des ailes.
- La décélération vaut la stat d'accélération × `brakeFactor` (2) : les ailes puissantes freinent fort, les faibles très peu.
- **Transition caméra « balancée dans la tête »** : sous V min × `stopSpeedFactor` (0,6), un dernier battement stoppe net le joueur. La caméra, toujours en troisième personne, est projetée vers l'avant et rentre dans la tête du joueur (interpolation rapide en ease-in, 5 ticks `intoHeadTicks`, puis bascule en première personne). Le vol passe alors en mode stabilisé.

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

- Valeur par défaut : `terminalVelocity` = 110 m/s, atteinte seulement en repli faucon (sprint maintenu en piqué au-delà de V max, au moins `tuckMinPitch` = 15°). Ailes ouvertes, un piqué vertical plafonne à `diveDragSpeed` (60 m/s) ; à plat, la traînée vaut `glideDragSpeed` (150 m/s), multipliée par la stat de Plané.
- Attention à la génération de chunks : l'élytre avec fusées, autour de 30 à 35 m/s, met déjà les serveurs sous tension. Au-delà de 55 m/s, le serveur doit charger plus de terrain qu'il n'en génère. Le commentaire de la config l'indique.
- La physique est écrite en m/s puis convertie en blocs/tick (1 bloc = 1 m, 20 ticks/s). On n'utilise pas la gravité d'entité vanilla (0,08 bloc/tick², environ 32 m/s²), trop forte pour un vol réaliste.

**Freinage en piqué**

Maintenir le frein en fonçant vers le sol applique la décélération du frein (accélération × `brakeFactor`), en concurrence directe avec g sin θ. Une paire faible en accélération ne peut pas compenser un piqué raide : il faut redresser tôt.

**Décrochage**

- Sous V min sans freiner (en cabrant trop longtemps, par exemple), les ailes décrochent : plus de portance, le joueur tombe et prend de la vitesse.
- Pour récupérer, il faut piquer pour retrouver un angle de plané et repasser au-dessus de V min.
- Sinon, il s'écrase au sol avec des dégâts proportionnels à la vitesse d'impact :

```latex
d\acute{e}g\hat{a}ts = c \cdot \max(0,\; v_{impact} - v_{seuil})
```

Ici v impact est la composante de vitesse perpendiculaire au sol, et c et v seuil viennent de la config serveur (`crashDamagePerSpeed` = 0,5, `crashThreshold` = 10 m/s). Un atterrissage rasant à grande vitesse reste donc survivable, contrairement à un piqué droit dans le sol.

## Stamina et chute

À 0 de stamina, les ailes restent actives mais ne peuvent plus battre. Le comportement dépend du mode en cours.

| Situation à 0 stamina | Accélérer | Planer | Freiner | Changer de mode | Résultat |
| --- | --- | --- | --- | --- | --- |
| Mode déplacement | Non | Oui | Oui | Non | Il plane et freine jusqu'au sol (le décrochage s'applique toujours ; frein à 0 stamina : descente à `zeroStaminaSinkSpeed`, 3 m/s) |
| Mode stabilisé | Non | Non | Non | Non | Il tombe (pas d'inertie pour le porter), avec les 2 sauts de secours |

- **Consommation** : chaque battement a un coût, le saut ailé aussi (`airJumpCost`). Le maintien en mode stabilisé coûte un débit continu (`stabilizedDrain`, 2,5/s). Le plané, le piqué et le freinage ne coûtent rien.
- **Récupération** : au sol (`groundRegen`, 20/s), plus lentement en vol plané (`glideRegen`, 3/s). Posséder Night et Light rend la stamina infinie.
- **Sauts de secours** : quand la stamina est sous le coût d'un saut ailé, 2 sauts gratuits (`rescueJumps`) restent disponibles. Chacun remet la vitesse verticale à 0 et annule **totalement** les dégâts de chute accumulés. Ils ne se rechargent qu'au contact du sol. Le premier part souvent par réflexe, le second sert à se sauver vraiment.
- **Autorité** : la stamina est calculée sur le serveur. Le client en affiche une prédiction, corrigée par synchronisation quand ça change de façon significative, et non à chaque tick.

## Craft : table d'assemblage

Toutes les paires se craftent dans la table d'assemblage de la forge de l'End (`simplywings:assembly_table`, « Assembly Table »). Les routes de chaque paire (étapes, origine des pièces, coût en Cloud Essence par étape et au total) sont tenues dans `CRAFTING.md`.

**Onglet 1 « Assembly »**

- 4 emplacements : un harnais, un cœur, une aile gauche et une aile droite → la paire. Ces pièces sont des items du mod, sauf pour la paire commune de départ, dont les 4 emplacements prennent directement des items vanilla (plume, plume, élytre, cuir).
- Pose et retrait de l'énergie ailée sur un item.

**Onglet 2 « Components »**

- Une grille de craft 5×5 sans les quatre coins, soit 21 cases : 3 cases centrées sur la première et la dernière ligne, 5 sur les trois lignes du milieu. Elle sert à fabriquer les pièces (harnais, cœurs, ailes) et leurs intermédiaires.
- Recettes avec forme, sur un `RecipeType` du mod, plus le coût en Cloud Essence de la recette.

**Réservoir de Cloud Essence**

- Les deux onglets partagent le même réservoir, qui contient au plus **1 000 unités**, soit 10 flacons. Un craft ne se lance que si le réservoir contient assez d'essence, et la consomme à la sortie du résultat.
- Coût d'un assemblage (onglet 1) selon la rareté :

| Rareté | Cloud Essence | Flacons |
| --- | --- | --- |
| Commune | 0 | 0 |
| Rare | 100 | 1 |
| Épique | 300 | 3 |
| Légendaire | 800 | 8 |
| Mythique | 1 000 | 10, le réservoir plein |

- Craft d'une pièce (onglet 2) : de 1 à 100 unités, selon sa complexité et ses ingrédients. La valeur vit dans le JSON de la recette.

**Pièces**

- **Craft** : à partir d'items vanilla et/ou moddés, dans l'onglet 2 (ou à l'établi vanilla pour les intermédiaires simples). Les ingrédients moddés passent par les tags communs `c:`, ce qui permet de fonctionner avec tout mod qui les fournit, sans dépendance.
- **Drops de mobs** : sur une liste de mobs définie, seulement si le coup fatal est porté avec un item imprégné d'« énergie ailée ».
- **Essences de blocs** : sur une liste de blocs vanilla définie, seulement si le bloc est cassé avec un item imprégné. Les essences servent aux différentes paires.
- **Liberté totale** : l'énergie ailée se pose sur n'importe quel item. Le joueur peut tuer un mob avec un escalier ou miner avec une épée s'il le veut.

**Énergie ailée**

C'est un Data Component custom, posé ou retiré sur n'importe quel item à la table d'assemblage. On l'a préféré à un vrai enchantement, qui interagirait avec l'enchanteur, la meule, l'enclume et les mods d'enchantement. Il affiche quand même l'éclat d'enchantement et une ligne de tooltip.

**Sources de loot** : la liste « mob ou bloc → pièce ou essence, avec chance » vit dans un registre datapack du mod, `simplywings:loot_source`, synchronisé au client. Les Global Loot Modifiers ne sont pas envoyés au client, alors que JEI a besoin de ces données (voir « Intégration JEI »).

Les loots passent par des Global Loot Modifiers NeoForge (data-driven, sans mixin) avec une condition maison, qui lisent ce registre. Elle exige deux choses : que l'item tenu porte l'énergie ailée, et que le mob ou le bloc figure dans la liste de la pièce. Sans ces deux conditions, les loot tables restent intactes. Pour les mobs, la condition lit l'item en main principale du tueur au moment du kill.

## Cloud Essence

Ressource propre au mod, consommée par la table d'assemblage. Elle se récolte en planant dans les nuages.

- **Cloud Flask** (`simplywings:cloud_flask`) : le contenant. Il contient de 0 à 100 unités, stockées dans un Data Component du mod et affichées dans le tooltip et en barre sur l'item.
- **Cloud Absorber** (`simplywings:cloud_absorber`) : il suffit qu'il soit dans l'inventaire. Pendant que le joueur vole dans la couche de nuages, il remplit les flacons de l'inventaire l'un après l'autre : le premier flacon non plein jusqu'à 100, puis le suivant. Un seul absorbeur suffit pour tout l'inventaire ; en avoir plusieurs ne remplit pas plus vite.
- **Couche de nuages** : dans l'Overworld, une bande de Y autour de la hauteur des nuages vanilla (192, à vérifier dans `DimensionSpecialEffects` du jar du pack). Le serveur ne connaît pas le rendu des nuages : la bande est en config serveur, et Distant Horizons ou un shaderpack qui déplace les nuages visibles ne changent rien.
- **Calcul serveur** : le serveur vérifie la position et le mode de vol à intervalle fixe (par exemple 1 fois par seconde), pas à chaque tick, et ajoute l'essence. Le client ne voit que l'item mis à jour, sans paquet dédié.
- **Remplissage de la table** : un flacon déposé dans la table vide son contenu dans le réservoir, dans la limite des 1 000 unités, et ressort vide.

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
| Assemblage | Assemblage d'une paire à partir de harnais, cœur, aile gauche et aile droite, avec son coût en Cloud Essence | Recettes `RecipeType` du mod (synchronisées au client en 1.21.1) |
| Composants | Grille 5×5 sans les coins, avec le coût en Cloud Essence | Recettes `RecipeType` du mod |
| Énergie ailée | Poser ou retirer l'énergie ailée sur un item quelconque | Recette spéciale, affichée avec quelques items d'exemple |
| Drops de mobs | Mob (rendu de l'entité), pièce lâchée, chance, condition « coup fatal avec un item imprégné » | Registre `simplywings:loot_source` |
| Essences de blocs | Bloc, essence lâchée, chance, condition « cassé avec un item imprégné » | Registre `simplywings:loot_source` |

La table d'assemblage est déclarée comme catalyseur de ses onglets. Une page d'info JEI sur la table et sur le compas de la forge explique comment trouver la forge (deuxième dragon, compas). Une page d'info sur le Cloud Flask et le Cloud Absorber explique la récolte dans les nuages.

## Architecture technique NeoForge

Tout passe par des events et API NeoForge natives : 0 access transformer et 0 mixin aujourd'hui (détail dans `docs/MIXINS.md`). Un seul mixin reste possible, `PlayerModel.setupAnim` TAIL pour les bras, si le prototype le justifie.

| Module | Rôle | Mécanisme | Côté |
| --- | --- | --- | --- |
| Définitions d'ailes | Stats, raretés, pièces, apparence | Registre datapack custom, synchronisé au client | Commun |
| Données joueur | Collection, paire équipée, apparence (transmog), stamina, sauts de secours | Data Attachment avec codec, conservé à la mort | Serveur (autorité) |
| Réseau | Équiper, état de vol, stamina, battements | CustomPacketPayload, envoyé seulement sur changement ; un battement en déplacement est un événement relayé aux joueurs proches | Les deux |
| Physique de vol | Modes stabilisé et déplacement, dive, décrochage | `MovementInputUpdateEvent` + `PlayerTickEvent.Pre` sur le joueur local (le mouvement est client-autoritaire en vanilla) | Client |
| Validation serveur | Pas de kick « flying is not enabled », dégâts de chute et de crash | `AttributeModifier` −100 % sur `minecraft:generic.gravity` pendant le vol + `LivingFallEvent` | Serveur |
| Caméra | 3e personne, orbite libre, transitions « laissée derrière » et « dans la tête » | `ViewportEvent.ComputeCameraAngles` + `CalculateDetachedCameraDistanceEvent` + `Options.setCameraType` | Client |
| Découplage souris | La souris tourne la caméra, pas le corps | `CalculatePlayerTurnEvent` + `MouseHandler.getXVelocity()` / `getYVelocity()`, sans mixin | Client |
| Rendu des ailes | Cubes, matériaux solide, transparent, émissif | RenderLayer ajouté au joueur, RenderTypes vanilla ; pose du corps par `RenderPlayerEvent.Pre` / `Post`. Modèles Blockbench sur un rig commun (voir « Pipeline art ») | Client |
| Animation | Idle, battements, repli, parachute | Animation procédurale commune à toutes les ailes (phase de battement + interpolation), mini-lib maison ; one-shots en `AnimationDefinition` vanilla | Client |
| Outillage art | Captures en jeu pour la boucle visuelle | Commandes de debug client (pose, capture via `Screenshot.grab`), réservées au dev | Client |
| Joueurs distants | Voir les ailes et l'état des autres | Paire et mode sur changement ; phase, banking et intensité déduits localement du mouvement interpolé | Client |
| Énergie ailée et loots | Imprégnation, drops de mobs, essences de blocs | Data Component + Global Loot Modifiers + condition de loot maison | Serveur |
| Cloud Essence | Flacons, absorbeur, récolte dans les nuages | Data Component sur le flacon + vérification serveur à intervalle fixe | Serveur |
| Table d'assemblage | Onglet assemblage et énergie ailée, onglet grille de composants, réservoir de Cloud Essence | Block + BlockEntity + Menu + 2 RecipeType custom | Les deux |
| Interface | Bouton inventaire, écran Ailes, HUD stamina | ScreenEvent.Init, Screen custom, RegisterGuiLayersEvent | Client |
| Config | V term, coûts, dégâts de crash, transitions caméra | ModConfigSpec (intégré à NeoForge, pas une lib tierce) | Serveur et client |
| Structure | Forge de l'End (dans le vide, à partir du 2e dragon) et compas | `LivingDeathEvent` du dragon + StructureTemplate + SavedData + `lodestone_tracker` | Serveur |
| Intégration JEI | Onglets assemblage, énergie ailée, drops de mobs, essences de blocs | Plugin `@JeiPlugin` optionnel, API JEI en `compileOnly` | Client |

**Performance**

- Les stats effectives (équipées + globales) sont mises en cache et recalculées seulement quand la collection ou la paire équipée change.
- Rien ne part sur le réseau à chaque tick. Les joueurs distants reçoivent la paire et le mode sur changement, plus les battements comme événements, et déduisent le reste du mouvement interpolé.
- Les particules sont limitées par une option client et désactivées automatiquement au-delà d'une certaine distance de la caméra.

## Pipeline art : modèles, textures, animations

Objectif : Claude produit environ 90 % des modèles et des animations, à partir d'une aile de référence faite par un designer, qui assure aussi la finition des textures. Source : le brainstorm « simplyWings — Pipeline modèles 3D & animations » du 21 octobre 2026.

**Outils**

- **Blockbench desktop** avec le plugin MCP [jasonjgardner/blockbench-mcp-plugin](https://github.com/jasonjgardner/blockbench-mcp-plugin), le plus mature (alternative plus légère : [sosadly/blockbench-mcp](https://github.com/sosadly/blockbench-mcp)). Il connecte Claude à l'API JavaScript de Blockbench pour créer et éditer les modèles et les textures pixel art, et pour faire des captures.
- Installation prévue : dans Blockbench, File > Plugins > Load Plugin from URL avec `https://jasonjgardner.github.io/blockbench-mcp-plugin/mcp.js`, puis `claude mcp add blockbench --transport http http://localhost:3000/bb-mcp`. URL, port et transport sont à vérifier dans la doc du plugin au moment de l'installation.
- Pas de Blender, inadapté au low voxel Minecraft. Pas de GeckoLib (zéro dépendance).

**Format des modèles**

- Les ailes restent des `ModelPart` vanilla, rendues avec des RenderTypes vanilla (compatibilité Iris et shaders).
- Deux façons d'amener un modèle Blockbench dans le mod (point ouvert) :
  - l'export « Modded Entity » de Blockbench : une classe Java `LayerDefinition` / `ModelPart` en Mojmap par modèle. Simple, mais 40 classes générées et une recompilation à chaque retouche ;
  - un chargeur maison, qui lit dans les assets le JSON de géométrie exporté par Blockbench et construit le `LayerDefinition`. Plus de code au départ, mais les modèles se retouchent sans recompiler, se remplacent par resource pack, et restent data-driven comme les définitions d'ailes. C'est la voie recommandée.

**Rig commun**

C'est le levier n°1 : tous les modèles partagent le même squelette, avec les mêmes noms de bones et les mêmes pivots.

| Bone | Rôle | Équivalent actuel |
| --- | --- | --- |
| `shoulder` | Racine, attache dans le dos : déploiement, balayage, coup d'aile | Pivot du `Slot` de `anim/WingStyle` |
| `arm` | Premier segment | Segment `shoulder` de `render/WingModel` |
| `forearm` | Deuxième segment | Segment `elbow` |
| `hand` | Pointe | Segment `tip` |
| Groupes de plumes ou de membranes | Enfants des segments, mouvements secondaires | Aucun (plaques fixes) |

- Les ailes à un seul segment (insectes, Psyché) n'utilisent que `shoulder` et `arm`.
- Une seule animation procédurale sert toutes les ailes, comme aujourd'hui avec `anim/WingAnimator` et `anim/WingPose`. Chaque aile n'apporte que ses paramètres dans `WingStyle` : amplitude, raideur, délai entre segments, profil de battement. On polit une fois, le gain vaut partout.

**Animations**

- **Procédural** pour tout ce qui réagit au vol : battements, plané, piqué, frein, stationnaire, décrochage, inclinaison en virage, avec blending entre états. Des rotations pilotées par sinus, vitesse, pitch, mode et stamina. C'est la base actuelle, à garder et à polir.
- **Keyframes Blockbench** seulement pour les one-shots : déploiement, repli, atterrissage, avec le système vanilla `AnimationDefinition`. En 1.21.1, `KeyframeAnimations.animate` exige un `HierarchicalModel` (vérifié dans les sources NeoForge 21.1.257) : le modèle d'aile doit en devenir un, ou le mod échantillonne lui-même les keyframes. Le fondu entre un one-shot et le procédural reste à écrire.

**Boucle visuelle**

Sans voir le résultat, Claude modélise à l'aveugle et plafonne vers 60 %. Il lui faut deux sources d'images :

- **Captures Blockbench via le MCP** : forme, silhouette, texture.
- **Captures en jeu automatisées**, un outil qui vit dans le repo. Claude Code lance `runClient` avec `--quickPlaySingleplayer <monde de test>`. Une commande de debug client fige le joueur et les ailes dans un état (repos, battement à une phase donnée, plané, piqué, frein, stationnaire) et place la caméra. La capture est prise avec `Screenshot.grab` dans `run/screenshots/`, puis Claude relit l'image. C'est le seul moyen de valider le rendu réel : éclairage, shaders, échelle par rapport au joueur.
- Commandes proposées : `/simplywings pose <state> [phase]` et `/simplywings shot <name>`. Elles sont réservées au dev : absentes du jar de release, ou limitées aux opérateurs.

**Style guide**

Un skill Claude Code versionné dans le repo (`.claude/skills/`) fige le style pour que chaque aile reste cohérente :

- densité de pixels : 16 px par bloc, sans mélange de résolutions ;
- budget de cubes par aile ;
- règles de silhouette ;
- palettes de 4 à 6 teintes par matériau, avec rampes d'ombrage ;
- conventions du rig ;
- captures de référence des pièces jugées réussies.

**Ordre de travail**

1. Installer Blockbench, le MCP et l'outil de capture en jeu.
2. Le designer fait une aile « hero » complète : modèle, texture, rig. Quelques heures de travail.
3. Claude écrit le style guide à partir d'elle, puis décline les autres paires. Il extrapole très bien un exemple, beaucoup moins bien un style à inventer.
4. Le designer repasse en finition, surtout sur les textures.

Les 90 % sont réalistes pour le gameplay, le rig, l'animation procédurale et la géométrie. Ils ne le sont pas pour la texture pixel art from scratch : Claude s'en sort en procédural scripté (rampes, bruit, palettes imposées), mais le rendu reste générique.

## Décisions et points ouverts

Les décisions du 20 et du 21 octobre sont intégrées dans les sections plus haut.

- [x] **Routes de craft** (22 octobre) : règles dans `CRAFTING.md`, une route par paire dans `crafting/<id>.md`. Profondeur max des pièces : 1 niveau de craft en rare, 2 en épique, 3 en légendaire, 5 en mythique. Sources 100 % vanilla, répétables, sans coffre ni élevage.
- [x] **Recettes mythiques** (22 octobre) : `crafting/night.md` et `crafting/light.md`. Les éclats d'écho (coffres de cité antique seulement) sont remplacés par l'Echo Resonance, drop des hurleurs de sculk.
- [x] **Mobs et blocs sources** (22 octobre) : registre dans `CRAFTING.md`, section « Drops du mod », avec des conditions vanilla facultatives (biome, heure, météo, hauteur, structure, tueur accroupi ou en sprint). Ajout d'une source `FISH` (canne imprégnée). Chances à régler en jeu.
- [ ] **Kills à distance** : un arc ou un trident imprégné compte-t-il ? Ce sera alors l'arme du tireur, pas le projectile.
- [ ] **Cloud Essence** : bande de Y et débit de la récolte, modes de vol qui comptent (déplacement seul, ou aussi le stationnaire), recettes du flacon et de l'absorbeur, flacons empilables vides ou non.
- [ ] **Nom des essences de blocs** : proposition à valider, plus de catégorie générique, chaque drop a un nom concret et « Essence » reste réservé à la Cloud Essence.
- [x] **Import des modèles** (21 octobre) : chargeur maison. Le mod lit directement le `.bbmodel` (format Modded Entity, UV de boîte) dans `assets/simplywings/rigs/<modèle>.bbmodel`, avec la même conversion que l'export Java de Blockbench. Un modèle par paire, les deux ailes modélisées dans Blockbench (préfixes `right_` / `left_`, miroir fait dans Blockbench), pour permettre les paires asymétriques. Modèles à taille réelle, environ 50 % plus grands que les anciens ; les anciens modèles Java prennent le même agrandissement via `WingStyle.SCALE`. Première aile migrée : la commune (`basic`), rig `shoulder` / `arm` (+ `tertials`) / `forearm` (+ `secondaries`) / `hand` (+ `hand_feathers`, `hand_coverts`) / `primaries` (7 rémiges). Règles et sources : `docs/SOURCES.md`.
- [ ] **Designer et aile hero** : qui, et quelle paire. Une seule référence couvre mal les familles très différentes (plumes, membranes, insectes, mécanique, éthéré) : prévoir peut-être une référence par famille.
- [ ] **Budget de cubes** par aile, à fixer sur l'aile hero.
- [ ] **One-shots** : faire de `WingModel` un `HierarchicalModel`, ou échantillonner les keyframes nous-mêmes.
- [x] Liste des paires : 40 paires (1 commune, 21 rares, 11 épiques, 5 légendaires, 2 mythiques), dans `WINGS.md`.
- [x] Stats de la paire commune et de toutes les paires : dans `BALANCE.md`.
- [x] Courbe du mode stabilisé : les deux, au choix en config client, ease-in par défaut.
- [x] Langue : mod entièrement en anglais, sans traduction.
- [x] Bonus mythiques : Night = stamina infinie, Light = transmog.
- [x] Table d'assemblage : deux onglets (assemblage, grille 5×5 sans les coins) et un réservoir de 1 000 unités de Cloud Essence.
- [x] Accès à la forge : en élytre, tonneaux de plumes et de cuir sur place, paire commune de départ.
- [x] Compas : tous les joueurs de l'île centrale, à chaque mort de dragon qui pose ou répare la forge.
- [x] Combat abandonné, hors périmètre.
- [x] Mod id : `simplywings`.
- [x] Bouton d'inventaire : sous l'inventaire.
- [x] Touche du mode déplacement : Alt gauche par défaut, rebindable.
- [x] JEI : plugin optionnel avec tous les crafts et les loots de mobs et de blocs.
- [x] Forge : dans le vide, à mi-parcours vers les îles extérieures, à partir du deuxième dragon, avec un compas.

## Mods de référence

Analyse terminée et validée : `docs/ANALYSE_REFERENCES.md`, qui prime sur cette table là où elle la corrige. Rien n'est repris tel quel. Chaque jar sert à comprendre comment un problème précis a été résolu, avant d'écrire notre propre version.

| Mod (jar NeoForge 1.21.1 si possible) | Ce qu'on y cherche |
| --- | --- |
| Better Flight Revived | Contournement du kick serveur, synchro des battements, HUD de stamina |
| Shoulder Surfing Reloaded | Découplage caméra / corps, et nombre minimal de mixins pour y arriver |
| Do a Barrel Roll (Fabric accepté) | Maths de contrôle, roll caméra, lissage des rotations |
| Caelus API | Autoriser l'état de vol sans élytre dans le slot torse |
| Wings (fork communautaire 1.21.1) | RenderLayer sur le joueur, animation procédurale des ailes |
| JEI ou EMI (celui du pack) | Vérifier que la zone du bouton d'inventaire reste libre |

Les jars sont dans `modsRef/` (sources décompilées dans `modsRef/_decompiled/`, hors git).
