# Simply Wings

Collectible magic wings for Minecraft **1.21.1** on **NeoForge**. Wings are made only for flight: a stabilized hover for building, and a keyboard-steered travel flight with wing beats, dives, stalls and stamina. Every pair has its own identity and stats, and the collection rewards exploring the whole vanilla game.

- Mod id: `simplywings` · Version: `0.1.0` (pre-release) · NeoForge `21.1.257` · License: All Rights Reserved
- Standalone: no dependency besides NeoForge. JEI support will be optional.
- Built for the *PleasePlayWithMe* modpack (195 mods: Iris, Sodium, Distant Horizons, Create Aeronautics + Sable, YUNG's Better End Island…), with **zero mixins and zero access transformers** to avoid conflicts.
- Developed largely with AI assistance (code, textures, animations).

## Status

**Prototype.** The flight, the wing rendering and the 40 pairs are in, all available through a debug command. The survival progression (crafting, assembly table, Cloud Essence, End forge, loot) is designed but not implemented yet: today, wings can only be obtained with `/simplywings unlock`.

| Area | State |
| --- | --- |
| Flight physics (hover, travel, dive, stall, crash, stamina, rescue jumps) | Done |
| Camera (detached orbit, "left behind" and "into the head" transitions) and mouse decoupling | Done |
| Wing rendering and procedural animation, remote players, particles | Done, art is placeholder (see TODO) |
| 40 wing pairs with stats, collection bonuses and identity parameters (datapack JSON) | Done |
| Mythic wings Night and Light: tail, dive shriek, charged dash with invisibility, set bonuses | Done |
| Transmog (Light bonus) with the "Equip appearance" button | Done |
| Wings menu (J), inventory button, stamina HUD | First version |
| Assembly Table, Cloud Essence, components, crafting routes | Designed, not implemented |
| Winged energy, mob and block drops | Designed, not implemented |
| End forge structure and forge compass | Designed, not implemented |
| JEI integration | Designed, not implemented |

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

### Progression (planned)

1. Kill the Ender Dragon a **second** time: the **Wing Forge** appears floating in the void about 512 blocks from the main island, and every player on the main island gets a **Forge Compass**.
2. Fly there with an elytra. The forge holds the **Assembly Table** and barrels of feathers and leather: the **Common Wings** need 2 feathers, 1 elytra and 1 leather, and no Cloud Essence.
3. Gather **Cloud Essence** by flying through the cloud layer with a **Cloud Absorber** in the inventory: it fills the **Cloud Flasks** (100 units each) one after another.
4. Collect components: mob drops and block drops only fall when the killing blow or the break is made with an item imbued with **winged energy** (applied at the table, on any item).
5. Craft components on the table's grid and assemble pairs. Assembly costs 0 / 100 / 300 / 800 / 1 000 Cloud Essence from common to mythic.

### Assembly Table (planned)

| Tab | Content |
| --- | --- |
| Assembly | Harness + core + left wing + right wing → pair. Apply or remove winged energy on an item |
| Components | 5×5 crafting grid without its four corners (21 slots: 3 on the top and bottom rows, 5 on the three middle rows) |

Both tabs share a Cloud Essence tank of up to 1 000 units (10 flasks). A component craft costs 1 to 100 units depending on its complexity.

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
| `CRAFTING.md` | Crafting route of every pair, with Cloud Essence cost per step and in total |
| `HANDOFF.md` | Implementation order |
| `docs/ANALYSE_REFERENCES.md` | Analysis of the reference mods and the modpack, architecture decisions |
| `docs/MIXINS.md` | Override budget: what replaces each would-be mixin |
| `docs/SOURCES.md` | Research sources and the rules applied to models, animation, textures and interfaces |

## TODO before release

### Gameplay
- [ ] Assembly Table: block, block entity, menu, the two tabs, the Cloud Essence tank, the two recipe types.
- [ ] Cloud Essence: Cloud Flask and Cloud Absorber items, server-side harvest in the cloud layer (Y band, rate, which flight modes count).
- [ ] Winged energy data component, `simplywings:loot_source` registry, Global Loot Modifiers and the custom loot condition.
- [ ] Component items (harnesses, cores, wings) and their textures.
- [ ] Write the crafting route of the 39 non-common pairs in `CRAFTING.md`, then as recipe JSON.
- [ ] Mythic recipes for Night and Light: the big list of vanilla exploration items.
- [ ] Remove `/simplywings unlock` from normal play once wings can be crafted (keep it for operators).
- [ ] Open questions in the specification: ranged kills with an imbued bow or trident, name of the block essences.

### Structure
- [ ] Wing Forge NBT template: platform, Assembly Table, supply barrels.
- [ ] Placement on the second dragon death (`LivingDeathEvent`), repair on later deaths, position kept in `SavedData`.
- [ ] `simplywings:chests/forge_supplies` loot table.
- [ ] Forge Compass with the vanilla `lodestone_tracker` component.
- [ ] Check with YUNG's Better End Island, BetterEnd and the other End mods of the pack.

### Interface
- [ ] Tutorial panels of the Wings screen (flight modes, keys, stamina, collection and stats).
- [ ] Final layout of the Wings screen, check the inventory button against the other mods' buttons.
- [ ] JEI plugin: assembly, components, winged energy, mob drops, block drops, info pages.

### Art overhaul
- [ ] **Tooling**: Blockbench with its MCP plugin, plus an in-game capture tool (dev-only commands to pose the wings and take a screenshot from `runClient`) so every model is checked in its real render.
- [ ] **Shared rig**: one skeleton for every wing (`shoulder`, `arm`, `forearm`, `hand`, feather or membrane groups), and a loader for Blockbench geometry JSON.
- [ ] **Reference wing**: a designer makes one complete "hero" wing (model, texture, rig); a style guide skill is written from it.
- [ ] **3D models**: remake every wing model (40 pairs, 1 to 3 wings per side) and the Fury tail from the reference. The current cube models are prototypes.
- [ ] **Textures**: redo the wing, glow, tail and item textures of every pair (16 px per block, 4 to 6 shades per material), final polish by the designer.
- [ ] **Animations**: polish the shared procedural animation (beats, glide, dive, brake, hover, stall, banking, blending), add keyframed one-shots (deploy, fold, landing), body and arm pose in flight. The single allowed mixin, `PlayerModel.setupAnim` TAIL for the arms, is used only if needed.
- [ ] **Sounds**: copyright-free recreations of the Fury shriek, charge and dash sounds; they currently point to vanilla placeholders.
- [ ] Particles and effects pass, checked with Iris shaderpacks (emissive render types).

### Release
- [ ] Full pass in single player and on a dedicated server, including high-speed dives with Distant Horizons.
- [ ] Choose the license, set the `versionRange` lower bound, version `1.0.0`.
- [ ] CurseForge page, screenshots, the AI-assisted development notice.
