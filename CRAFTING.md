# Routes de craft des ailes

Pour chaque paire : les étapes de fabrication, d'où vient chaque pièce (craft, loot, vanilla) et leur coût en **Cloud Essence**, par étape et au total. Les règles générales sont dans `CAHIER_DES_CHARGES.md`, section « Craft : table d'assemblage » ; ce fichier sert à concevoir et relire les recettes avant de les écrire en JSON.

Dernière mise à jour : 21 octobre 2026.

## Lire une route

Chaque paire a une table. Une ligne = une étape qui produit un objet.

| Colonne | Contenu |
| --- | --- |
| # | Ordre conseillé. Les étapes sans dépendance entre elles peuvent se faire dans n'importe quel ordre. |
| Produit | Nom anglais affiché en jeu et id (`simplywings:*` ou `minecraft:*`), avec la quantité |
| Où | Voir la légende ci-dessous |
| Entrées | Ce que l'étape consomme, avec les quantités. Un produit d'une étape précédente est noté `→ #n` |
| Essence | Cloud Essence consommée par l'étape |
| Cumul | Essence consommée depuis le début de la route |

**Légende de « Où »**

| Code | Signification |
| --- | --- |
| `ASM` | Table d'assemblage, onglet 1 « Assembly » : harnais + cœur + aile gauche + aile droite → paire |
| `GRID` | Table d'assemblage, onglet 2 « Components » : grille 5×5 sans les coins (21 cases) |
| `CRAFT` | Établi vanilla |
| `MOB` | Drop d'un mob, coup fatal porté avec un item imprégné d'énergie ailée (registre `simplywings:loot_source`) |
| `BLOCK` | Drop d'un bloc, cassé avec un item imprégné d'énergie ailée (registre `simplywings:loot_source`) |
| `VANILLA` | Ressource vanilla obtenue normalement (minage, coffre, commerce…) |
| `CLOUD` | Récolte de Cloud Essence en planant dans les nuages |

**Grille `GRID`** (`#` = case utilisable, `.` = coin absent) :

```
. # # # .
# # # # #
# # # # #
# # # # #
. # # # .
```

Quand la forme compte, la recette s'écrit sous la table de la paire avec ce même dessin, une lettre par ingrédient.

## Budget de Cloud Essence

| Rareté | Assemblage (`ASM`) | Équivalent en flacons | Craft d'une pièce (`GRID`) |
| --- | --- | --- | --- |
| Commune | 0 | 0 | 0 (pièces vanilla) |
| Rare | 100 | 1 | 1 à 100 selon la complexité |
| Épique | 300 | 3 | 1 à 100 |
| Légendaire | 800 | 8 | 1 à 100 |
| Mythique | 1 000 | 10 (réservoir plein) | 1 à 100 |

- 1 flacon (`simplywings:cloud_flask`) = 100 unités. La table contient au plus 1 000 unités (10 flacons).
- Le coût d'assemblage ne compte que l'étape `ASM`. Le total d'une paire = assemblage + somme des pièces `GRID` de sa route.

## Objets communs à toutes les routes

| # | Produit | Où | Entrées | Essence | Cumul |
| --- | --- | --- | --- | --- | --- |
| C1 | Cloud Absorber `simplywings:cloud_absorber` ×1 | à définir | à définir | 0 | 0 |
| C2 | Cloud Flask `simplywings:cloud_flask` (vide) ×1 | à définir | à définir | 0 | 0 |
| C3 | Cloud Flask (pleine, 100 unités) | `CLOUD` | Flacon vide, absorbeur dans l'inventaire | 0 | 0 |
| C4 | Assembly Table `simplywings:assembly_table` | Forge de l'End | Déjà posée dans la structure | 0 | 0 |

## Récapitulatif

« À définir » = la route n'est pas encore écrite ; le total est alors au moins le coût d'assemblage.

| Paire | Id | Rareté | Pièces `GRID` | Assemblage | Total |
| --- | --- | --- | --- | --- | --- |
| Common Wings | `common` | Commune | 0 | 0 | **0** |
| Bat Wings | `bat` | Rare | à définir | 100 | ≥ 100 |
| Crow Wings | `crow` | Rare | à définir | 100 | ≥ 100 |
| Owl Wings | `owl` | Rare | à définir | 100 | ≥ 100 |
| Gull Wings | `gull` | Rare | à définir | 100 | ≥ 100 |
| Hummingbird Wings | `hummingbird` | Rare | à définir | 100 | ≥ 100 |
| Eagle Wings | `eagle` | Rare | à définir | 100 | ≥ 100 |
| Swift Wings | `swift` | Rare | à définir | 100 | ≥ 100 |
| Homing Pigeon Wings | `pigeon` | Rare | à définir | 100 | ≥ 100 |
| Peacock Wings | `peacock` | Rare | à définir | 100 | ≥ 100 |
| Parrot Wings | `parrot` | Rare | à définir | 100 | ≥ 100 |
| Bee Wings | `bee` | Rare | à définir | 100 | ≥ 100 |
| Moth Wings | `moth` | Rare | à définir | 100 | ≥ 100 |
| Beetle Wings | `beetle` | Rare | à définir | 100 | ≥ 100 |
| Flying Squirrel Wings | `squirrel` | Rare | à définir | 100 | ≥ 100 |
| Flying Fish Wings | `flying_fish` | Rare | à définir | 100 | ≥ 100 |
| Phantom Wings | `phantom` | Rare | à définir | 100 | ≥ 100 |
| Amethyst Wings | `amethyst` | Rare | à définir | 100 | ≥ 100 |
| Ornithopter Wings | `ornithopter` | Rare | à définir | 100 | ≥ 100 |
| Kite Wings | `kite` | Rare | à définir | 100 | ≥ 100 |
| Copper Wings | `copper` | Rare | à définir | 100 | ≥ 100 |
| Patchwork Wings | `patchwork` | Rare | à définir | 100 | ≥ 100 |
| Meganeura Wings | `ancient` | Épique | à définir | 300 | ≥ 300 |
| Pterosaur Wings | `ptero` | Épique | à définir | 300 | ≥ 300 |
| Wings of Flame | `fire` | Épique | à définir | 300 | ≥ 300 |
| Wings of the Tide | `water` | Épique | à définir | 300 | ≥ 300 |
| Wings of Stone | `earth` | Épique | à définir | 300 | ≥ 300 |
| Wings of the Wind | `air` | Épique | à définir | 300 | ≥ 300 |
| Wings of the Storm | `storm` | Épique | à définir | 300 | ≥ 300 |
| Aurora Wings | `aurora` | Épique | à définir | 300 | ≥ 300 |
| Wings of Shadow | `shadow` | Épique | à définir | 300 | ≥ 300 |
| Nebula Wings | `nebula` | Épique | à définir | 300 | ≥ 300 |
| Wings of the Forest Spirit | `forest` | Épique | à définir | 300 | ≥ 300 |
| Angel Wings | `angel` | Légendaire | à définir | 800 | ≥ 800 |
| Demon Wings | `demon` | Légendaire | à définir | 800 | ≥ 800 |
| Seraph Wings | `seraph` | Légendaire | à définir | 800 | ≥ 800 |
| Wings of Psyche | `psyche` | Légendaire | à définir | 800 | ≥ 800 |
| Wings of Daedalus | `daedalus` | Légendaire | à définir | 800 | ≥ 800 |
| Night Wings | `night` | Mythique | à définir | 1 000 | ≥ 1 000 |
| Light Wings | `light` | Mythique | à définir | 1 000 | ≥ 1 000 |

## Routes

### Common Wings · `simplywings:common` · Commune

Les 4 emplacements de l'assemblage prennent directement des items vanilla. Les tonneaux de la forge fournissent plumes et cuir, l'élytre vient d'une cité de l'End.

| # | Produit | Où | Entrées | Essence | Cumul |
| --- | --- | --- | --- | --- | --- |
| 1 | Feather `minecraft:feather` ×2 | `VANILLA` | Tonneaux de la forge | 0 | 0 |
| 2 | Leather `minecraft:leather` ×1 | `VANILLA` | Tonneaux de la forge | 0 | 0 |
| 3 | Elytra `minecraft:elytra` ×1, enchantée ou non | `VANILLA` | Cité de l'End | 0 | 0 |
| 4 | Common Wings `simplywings:common_wings` | `ASM` | Harnais : → #2 · Cœur : → #3 · Aile G : 1 → #1 · Aile D : 1 → #1 | 0 | **0** |

### Gabarit d'une paire

Exemple de forme seulement : les objets ci-dessous ne sont pas des recettes validées.

| # | Produit | Où | Entrées | Essence | Cumul |
| --- | --- | --- | --- | --- | --- |
| 1 | `<loot>` ×n | `MOB` | Mob, item imprégné | 0 | 0 |
| 2 | `<pièce>` Harness | `GRID` | → #1 ×n, vanilla… | 10 | 10 |
| 3 | `<pièce>` Core | `GRID` | … | 40 | 50 |
| 4 | `<pièce>` Wing ×2 (gauche et droite) | `GRID` | … (le coût compte par craft) | 2 × 15 | 80 |
| 5 | `<paire>` | `ASM` | Harnais → #2 · Cœur → #3 · Ailes → #4 | 100 | **180** |
