# Simply Wings

Collectible magic wings for Minecraft **1.21.1** on **NeoForge**. Wings are made only for flight: a stabilized hover for building, and a keyboard-steered travel flight with wing beats, dives, stalls and stamina. Every pair has its own identity and stats, and the collection rewards exploring the whole vanilla game.

- Mod id: `simplywings` · Version: `1.0.0` (beta) · NeoForge `21.1.257` · License: MIT
- Standalone: no dependency besides NeoForge. JEI support is optional (a JEI plugin, also read by EMI).
- Built for the *PleasePlayWithMe* modpack (195 mods: Iris, Sodium, Distant Horizons, Create Aeronautics + Sable, YUNG's Better End Island…), with **zero mixins and zero access transformers** to avoid conflicts.

## Status

**Beta (1.0.0).** Everything is in: flight, 41 pairs, the survival progression (five End sanctums, Assembly Table, Forge Bench, four essences, Cloud Compressor, winged drops), the wing gacha with its in-world summoning show, the Offering Fountain, the wiki and the full sound design. A complete automated test campaign ran on October 29, 2026 in single player and on a dedicated server (`docs/test-runs/2026-10-29/`). Debug commands (`/simplywings …`) are reserved to operators.

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
| Air jump (costs stamina) | Wing jump / Hover / Ascend (Space) in the air |
| Hover (stabilized mode) | Very fast double tap on Wing jump |
| Hover ↔ travel flight | Travel flight (Left Alt) |
| Charged takeoff into travel flight | Hold Travel flight for 1 s on the ground |
| Wing beats, Fury dash | Beat wings / Fury dash (Left Ctrl) |
| Brake | Descend / Brake (Left Shift), or the dedicated Brake key (unbound) |
| Climb / dive (travel flight), forward / back (hover) | W / S |
| Turn (travel flight), strafe (hover) | A / D |
| Wings menu | J |

Every action is a key of its own in the *Simply Wings* category of the Controls menu: swap W and S there if you prefer an inverted pitch. In travel flight, the mouse only orbits the camera; the keys move an aim point that the heading follows smoothly, at 10 % while braking. When stamina is too low for an air jump, 2 free rescue jumps reset the fall and cancel fall damage, and recharge on the ground.

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
/simplywings sound <id> [pitch] [volume]   client side: play a mod or vanilla sound event
/simplywings sound list <group>            client side: play every variation of the matching events, 1.5 s apart
/simplywings sound stop                    client side: stop the list and all playing sounds
```

## Configuration

- `simplywings-server.toml`: physics (gravity, terminal velocity 220 m/s, drag, brake, takeoff), stabilized mode, stamina costs and regeneration, crash damage, Fury invisibility, and the physical scale of every stat.
- `simplywings-client.toml`: camera distance and transitions, double-tap window, hover speed curve (`EASE_IN` or `SMOOTHSTEP`), particles.
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
| `gatcha.md` | Wing gacha: summons, loot tiers and pity, Draconic Altar, Offering Fountain, the two new structures (French) |
| `wingsRules.md` | Polish rules and journal of the 41 pairs (French) |
| `docs/ANALYSE_REFERENCES.md` | Analysis of the reference mods and the modpack, architecture decisions |
| `docs/MIXINS.md` | Override budget: what replaces each would-be mixin |
| `docs/SOURCES.md` | Research sources and the rules applied to models, animation, textures and interfaces |

## TODO before release

State on October 29, 2026, after the test campaign. Design documents are in French. Items marked *after beta* can ship later without blocking the beta.

### Done
- [x] **41 pairs**: Blockbench model, pxl texture and dedicated animation class for each pair, polished one by one with the author (`wingsRules.md`).
- [x] **Survival progression**: Assembly Table, Forge Bench, four essences, flask and tanks, harvest in the three dimensions, Cloud Compressor, stamina substitute, winged energy and 59 drop sources, crafting routes of every pair, optional JEI plugin.
- [x] **Wing gacha** (`gatcha.md`): summoning feathers in chest loot, five tiers plus mythics, pity, Draconic Altar with its in-world summoning show and cards, Offering Fountain.
- [x] **Five End sanctums** built by hand by the author, placed on dragon deaths and repaired on later ones, one compass each.
- [x] **Sound design** complete and validated by the author (`SOUND_PLAN.md`), heard by nearby players.
- [x] **Wiki**: 24 chapters and a page per pair, material and component (270 pages).
- [x] Debug commands reserved to operators; every flight action is a configurable key.

### To validate by the author
- [ ] New Draconic Altar and Offering Fountain models, larger Fury wings and longer tails, brake handling at 10 %.

### To check in the full modpack
- [ ] High-speed dives with Distant Horizons, Iris shaderpacks (emissive pairs, translucency, summoning show).
- [ ] YUNG's Better End Island and the other End mods: the summoning sanctum sits at the top of the sky above the main island.
- [ ] Teleports while flying on a server (portals, pearls, teleport mods): a kick for floating was seen twice after a `/tp` during the test campaign.

### After beta
- [ ] Particles of every pair polished and checked with shaderpacks.
- [ ] Tutorial panels and final layout of the Wings screen.
- [ ] Open question in the specification: name of the block drops category.

### Release
- [x] Full pass in single player and on a dedicated server (October 29, 2026, `docs/test-runs/2026-10-29/`).
- [x] License (MIT), version `1.0.0`, `CHANGELOG.md`.
- [ ] CurseForge page (draft in `docs/CURSEFORGE.md`) and screenshots, published by the author.

## Credits

Sound effects are CC0 (public domain) or under the Pixabay Content License, from [Pixabay](https://pixabay.com), [Freesound](https://freesound.org) (2create, Cerise_Virtuelle, Scottrex05, Cultureshock007, kalhan, qubodup, harrisonlace, simranzenov), [OpenGameArt](https://opengameart.org) and [Kenney](https://kenney.nl). Full list in `art/sounds/SOURCES.md`.
