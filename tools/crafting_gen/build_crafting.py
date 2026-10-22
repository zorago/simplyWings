from pathlib import Path

H = Path(__file__).parent
head = (H / "crafting_head.md").read_text(encoding="utf-8")
reg = (H / "_registry.txt").read_text(encoding="utf-8")
sh = (H / "_shared.txt").read_text(encoding="utf-8").splitlines()
recap = (H / "_recap.txt").read_text(encoding="utf-8")
rep = {"iron_harness": "toutes sauf la commune", "gilded_harness": "épiques, légendaires, mythiques",
       "radiant_harness": "légendaires, mythiques"}
out = []
for line in sh:
    for k, v in rep.items():
        if f"`simplywings:{k}`" in line:
            cells = line.split(" | ")
            cells[-1] = v + " |"
            line = " | ".join(cells)
    out.append(line)
tail = f"""
## Objets partagés

Les intermédiaires utilisés par plusieurs paires. Leur recette dessinée est recopiée dans chaque route qui s'en sert.

{chr(10).join(out)}

## Objets de base

| # | Produit | Où | Entrées | Essence |
| --- | --- | --- | --- | --- |
| C1 | Cloud Absorber `simplywings:cloud_absorber` ×1 | `GRID` | Glass ×4, Copper Ingot ×2, Updraft Shard ×2, Feather ×1 | 0 |
| C2 | Cloud Flask `simplywings:cloud_flask` (vide) ×2 | `GRID` | Glass ×4, Feather ×1 | 0 |
| C3 | Cloud Flask (pleine, 100 unités) | `CLOUD` | Flacon vide, absorbeur dans l'inventaire, vol dans la couche de nuages (Y 186 à 204, 2 unités/s) | 0 |
| C4 | Assembly Table `simplywings:assembly_table` | Forge de l'End | Déjà posée dans la structure | 0 |
| C5 | Forge Compass `simplywings:forge_compass` | Mort d'un dragon | Donné aux joueurs de l'île centrale, à partir du 2e dragon | 0 |

## Récapitulatif

{recap}

## Common Wings · `simplywings:common` · Commune

Les 4 emplacements de l'assemblage prennent directement des items vanilla. Les tonneaux de la forge fournissent plumes et cuir, l'élytre vient d'une cité de l'End.

| # | Produit | Où | Entrées | Essence | Cumul |
| --- | --- | --- | --- | --- | --- |
| 1 | Feather `minecraft:feather` ×2 | `VANILLA` | Tonneaux de la forge | 0 | 0 |
| 2 | Leather `minecraft:leather` ×1 | `VANILLA` | Tonneaux de la forge | 0 | 0 |
| 3 | Elytra `minecraft:elytra` ×1, enchantée ou non | `VANILLA` | Cité de l'End | 0 | 0 |
| 4 | Common Wings `simplywings:common_wings` | `ASM` | Harnais : → #2 · Cœur : → #3 · Aile G : 1 → #1 · Aile D : 1 → #1 | 0 | **0** |
"""
(H.parents[1] / "CRAFTING.md").write_text(head + reg + "\n" + tail, encoding="utf-8", newline="\n")
