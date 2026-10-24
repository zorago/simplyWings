# Simply Wings

Collectible magic wings for Minecraft **1.21.1** on **NeoForge**. Wings are made only for flight: a stabilized hover for building, and a keyboard-steered travel flight with wing beats, dives, stalls and stamina. Every pair has its own identity and stats, and the collection rewards exploring the whole vanilla game.

- Mod id: `simplywings` · Version: `0.1.0` (pre-release) · NeoForge `21.1.257` · License: All Rights Reserved
- Standalone: no dependency besides NeoForge. JEI support is optional (a JEI plugin, also read by EMI).
- Built for the *PleasePlayWithMe* modpack (195 mods: Iris, Sodium, Distant Horizons, Create Aeronautics + Sable, YUNG's Better End Island…), with **zero mixins and zero access transformers** to avoid conflicts.
- Developed largely with AI assistance (code, textures, animations).

## Status

**Prototype.** The flight, the wing rendering and the 40 pairs are in, all available through a debug command. The survival progression (three End sanctums, Assembly Table, Forge Bench, four essences, Cloud Compressor, loot) is implemented and being play-tested; `/simplywings unlock` stays available to operators.

| Area | State |
| --- | --- |
| Flight physics (hover, travel, dive, stall, crash, stamina, rescue jumps) | Done |
| Camera (detached orbit, "left behind" and "into the head" transitions) and mouse decoupling | Done |
| Wing rendering and procedural animation, remote players, particles | Done, art is placeholder (see TODO) |
| 41 wing pairs with stats, collection bonuses and identity parameters (datapack JSON) | Done |
| Mythic wings Night and Light: tail, dive shriek, charged dash with invisibility, set bonuses | Done |
| Transmog (Light bonus) with the "Equip appearance" button | Done |
| Wings menu (J), inventory button, stamina HUD | First version |
| Assembly Table and Forge Bench with four essence gauges, 188 components, 179 recipes with tiered essence costs | Done, to play-test |
| Four essences (Cloud, Steam, Ethereal, Intensified), flask, four tiered tanks, absorber harvest in the three dimensions | Done, to play-test |
| Cloud Compressor (3x3x5 multiblock, animated model) and intensified essence as a stamina substitute | Done, to play-test |
| Winged energy, mob, block and fishing drops (59 sources) | Done, to play-test |
| Three End sanctums (Sky Sanctum, Wing Forge, Cloud Refinery) and their compasses | Done, to play-test |
| JEI integration | Done, to play-test |

## Gameplay

### Collection

Wings are not worn items. A wing item is consumed on right-click: the pair joins the player's collection for good and is equipped. Switching pairs is instant, and the player can also equip nothing.

Each pair has two sets of stats:
- **Equipped stats** apply only while the pair is worn.
- **Collection bonuses** apply as soon as the pair is unlocked, whatever pair is worn. With the full collection, every pair reaches 10/10 everywhere.

### Stats

Seven stats scored 0 to 100. The menu shows five of them out of 10; Glide and Lift stay internal.

| Stat | Effect |
| --- | --- |
| Endurance | Stamina pool for wing beats and hovering |
| Acceleration | Speed gained per second of beating, brake power, air jump height |
| Speed | Max cruise speed while beating |
| Handling | Turn rate |
| Stability | Hover precision (10/10 = creative-like) |
| Glide *(internal)* | Air drag: glide distance, speed lost between beats |
| Lift *(internal)* | Stall speed |

The physical value of each score is set in the server config, section `[stats]`.

### Rarities

| Rarity | Pairs | Theme |
| --- | --- | --- |
| Common | 1 | The starter pair, improved elytra |
| Rare | 21 | Real creatures and handmade machines, vanilla mood |
| Epic | 11 | Spirits, elements and extinct creatures |
| Legendary | 5 | Gods and mythical figures, each maxes one visible stat |
| Mythic | 2 | Night and Light, the Fury wings |

**Mythics**: 10/10 everywhere plus +2 speed beyond the cap, hidden as "???" until unlocked. Owning **Night** grants infinite stamina. Owning **Light** unlocks the transmog: wear the look of any unlocked pair on top of the stats of the equipped one.

### Controls

| Action | Default |
| --- | --- |
| Air jump (costs stamina) | Jump in the air |
| Hover (stabilized mode) | Very fast double tap on jump |
| Hover ↔ travel flight | Left Alt |
| Charged takeoff into travel flight | Hold Left Alt for 1 s on the ground |
| Wing beats | Hold sprint |
| Brake | Sneak (dedicated key, unbound by default) |
| Steer (travel flight) / move (hover) | WASD |
| Wings menu | J |

In travel flight, the mouse only orbits the camera; WASD moves an aim point that the heading follows smoothly. When stamina is too low for an air jump, 2 free rescue jumps reset the fall and cancel fall damage, and recharge on the ground.

### Progression

Three floating sanctums appear in the End, 512 blocks from the main island and 120 degrees apart, one per dragon death from the second on. Every player on the main island gets the matching compass. Later deaths repair the unlocked sanctums.

1. **2nd dragon: Sky Sanctum.** An ethereal temple wrapped in a cloud of end rods and glass, open only at the front. It holds the **Assembly Table**. Until the third dragon, only the **Common Wings** can be assembled: leather, an elytra and two feathers.
2. **3rd dragon: Wing Forge.** A smoking, half-ruined rustic forge with barrels of feathers and leather. It holds the **Forge Bench**: the 5x5 components grid and the winged energy tab.
3. **4th dragon: Cloud Refinery.** A copper and glass yard around the **Cloud Compressor**.

Collect components: mob, block and fishing drops only fall when the killing blow, the break or the catch is made with an item imbued with **winged energy** (applied at the Forge Bench, on any item).

### Essences

| Essence | Harvested with a Cloud Absorber while flying | Used for |
| --- | --- | --- |
| Cloud | Overworld cloud layer | every craft from rare on |
| Steam | Nether, within 16 blocks above lava | epic and higher |
| Ethereal | End, over the void, more than 200 blocks from the centre | legendary and higher |
| Intensified Cloud | Cloud Compressor: 1 Cloud + 1 Steam + 1 Ethereal → 5 | mythic, final by-products, tanks; also stands in for stamina |

- **Absorbers**: five tiers, one per container tier. An absorber of tier n fills the container of its tier in 100 s × 2^(n-1) (flask 100 s, iron tank 200 s, then 400, 800 and 1 600 s); the best absorber in the inventory counts.
- **Containers**: the Essence Flask (100) and four tanks crafted with the harness of each tier: iron 5 000, gilded 20 000, radiant 50 000, fury 100 000. A container holds one essence at a time; with Intensified essence it glows like an enchanted golden apple.
- **Benches**: the Assembly Table and the Forge Bench each have four gauges of 1 000. Drop a container in the essence slot to pour it into the matching gauge. A recipe asks for 1 to 4 essences depending on its tier and size; assembly costs 0 / 100 / 300 / 800 / 1 000 from common to mythic.
- **Stamina**: when stamina runs out in flight, Intensified essence from the inventory refills it, 1 essence for 1 stamina (flasks first, then tanks).
- **Cloud Compressor**: three inputs and one output, each a flask or a tank. It stops when an input runs dry or the output is full.

## Commands

Operator only (permission level 2).

```
/simplywings unlock <wing>   unlock and equip a pair
/simplywings unlock all      unlock the whole registry
/simplywings equip <wing>    equip an owned pair
/simplywings unequip
/simplywings stamina <value>
/simplywings reset           clear the collection
/simplywings info            effective stats, stamina and flight mode
/simplywings sanctum <name>  place or repair sky_sanctum, wing_forge or cloud_refinery and get its compass
/simplywings dragon          count a dragon death (places the unlocked sanctums)
```

## Configuration

- `simplywings-server.toml`: physics (gravity, terminal velocity 110 m/s, drag, brake, takeoff), stabilized mode, stamina costs and regeneration, crash damage, Fury invisibility, and the physical scale of every stat.
- `simplywings-client.toml`: camera distance and transitions, inverted pitch, double-tap window, hover speed curve (`EASE_IN` or `SMOOTHSTEP`), particles.
- Pair data lives in datapack JSON: `data/simplywings/simplywings/wing/<id>.json` (rarity, model, color, flap and hover cadence, equipped stats, collection bonuses).

## Building

```
./gradlew --no-configuration-cache compileJava   # quick check
./gradlew runClient                              # dev client (run/)
./gradlew runServer                              # dev dedicated server
./gradlew build                                  # jar in build/libs
```

There are no automated tests: every step is checked in game, in single player and on a local dedicated server.

## Design documents

Written in French.

| File | Content |
| --- | --- |
| `CAHIER_DES_CHARGES.md` | Full specification, the reference when in doubt |
| `WINGS.md` | Catalogue of the 40 pairs: identity, look, flight feel, stats |
| `BALANCE.md` | Stat scales, pair scores, collection bonus distribution |
| `CRAFTING.md` | Crafting route of every pair, with the essence cost per step and in total |
| `HANDOFF.md` | Implementation order |
| `docs/ANALYSE_REFERENCES.md` | Analysis of the reference mods and the modpack, architecture decisions |
| `docs/MIXINS.md` | Override budget: what replaces each would-be mixin |
| `docs/SOURCES.md` | Research sources and the rules applied to models, animation, textures and interfaces |

## TODO before release

### Gameplay
- [x] Assembly Table and Forge Bench: blocks, block entities, menus, four essence gauges, the two recipe types.
- [x] Four essences, flask and tiered tanks, harvest in the three dimensions, Cloud Compressor, stamina substitute.
- [x] Cloud Absorber: server-side harvest (config `[cloud]`; values to tune in game).
- [x] Winged energy data component, `simplywings:loot_source` registry (59 sources) and the winged drops Global Loot Modifier.
- [x] Three End sanctums placed on the 2nd, 3rd and 4th dragon deaths, repaired on later deaths, one compass each.
- [x] Optional JEI plugin: assembly, 5x5 components, winged energy and winged drops categories, recipe transfer, info pages (EMI reads JEI plugins).
- [ ] Play-test everything listed in `ToDoTesting.md`.
- [x] Component items (harnesses, cores, wings, 56 imbued drops, intermediates), textures, and lore plus riddle tooltips.
- [x] Crafting routes of the 40 non-common pairs (`crafting/`), generated as recipe JSON.
- [x] Mythic recipes for Night and Light.
- [ ] Remove `/simplywings unlock` from normal play once wings can be crafted (keep it for operators).
- [ ] Open question in the specification: name of the block drops category.

### Structure
- [x] Sanctum NBT templates generated by `tools/crafting_gen/sanctums.py`: floating cross islands, buildings, nature, ruins.
- [x] Placement on dragon deaths (`LivingDeathEvent` and a kill counter in `SavedData`), repairs that keep existing blocks.
- [x] `simplywings:chests/forge_supplies` loot table and compasses with the vanilla `lodestone_tracker` component.
- [ ] Polish pass on the sanctum builds (more detail, art direction review).
- [ ] Check with YUNG's Better End Island, BetterEnd and the other End mods of the pack.

### Interface
- [ ] Tutorial panels of the Wings screen (flight modes, keys, stamina, collection and stats).
- [ ] Final layout of the Wings screen, check the inventory button against the other mods' buttons.
- [x] JEI plugin: assembly, components, winged energy, winged drops, info pages.

### Art overhaul
- [x] **Tooling**: Blockbench with its MCP plugin and a live builder (`tools/blockbench/wing_builder.js`), plus the in-game studio (`tools/studio`) that poses the wings and captures review sheets.
- [x] **Rigs**: `.bbmodel` files loaded directly by the game, one animation class per pair with a shared toolkit (`Cycle`, `FeatherWing`, `Bone`).
- [ ] **Reference wing**: a designer makes one complete "hero" wing (model, texture, rig); a style guide skill is written from it.
- [ ] **3D models**: remake every wing model and the Fury tail. Done: common and the 21 rare pairs (each with its own model, texture and animation class, still to be reviewed in the in-game studio). To do: epic, legendary and mythic pairs.
- [ ] **Textures**: redo the wing, glow, tail and item textures of every pair (16 px per block, 4 to 6 shades per material), final polish by the designer.
- [ ] **Animations**: polish the shared procedural animation (beats, glide, dive, brake, hover, stall, banking, blending), add keyframed one-shots (deploy, fold, landing), body and arm pose in flight. The single allowed mixin, `PlayerModel.setupAnim` TAIL for the arms, is used only if needed.
- [ ] **Sounds**: copyright-free recreations of the Fury shriek, charge and dash sounds; they currently point to vanilla placeholders.
- [ ] Particles and effects pass, checked with Iris shaderpacks (emissive render types).

### Release
- [ ] Full pass in single player and on a dedicated server, including high-speed dives with Distant Horizons.
- [ ] Choose the license, set the `versionRange` lower bound, version `1.0.0`.
- [ ] CurseForge page, screenshots, the AI-assisted development notice.
