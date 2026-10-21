---
name: simplywings-wing-style
description: Style guide and workflow for making or polishing a SimplyWings wing pair (Blockbench rig, pxl texture, dedicated Java rig class, in-game studio review). Load before modelling, texturing or animating any wing pair, or the Night/Light tails.
---

# SimplyWings wing style

Every pair is a unique creature or object, polished as far as possible: its own silhouette, its own texture,
its own animation class. The common wings (`basic`) are the floor, not the target. A professional designer
reviews everything afterwards, so push each pair to its best rather than to "good enough".

Read before starting: the pair's entry in `WINGS.md`, `docs/SOURCES.md` (anatomy, rig rules, pitfalls),
and the memory note on art direction.

## Art direction

- **Minecraft voxel, Cobblemon-like**: readable chunky shapes, clean pixel art at 1 px per model unit,
  flat-shaded colour ramps. Never Pixelmon-like smooth "realistic" models. "We are in Minecraft, not Half-Life 2."
- Complexity is welcome when it serves the silhouette (reference: Isle of Berk dragons). There is no hard
  cube budget: a pair can use 60 to 150 cubes for both sides. Stop when extra cubes no longer change the
  silhouette or the motion.
- Silhouette first: judge the pair as a black shape from the back, the side and above. Each pair must be
  recognisable from its silhouette alone.
- Palettes: 4 to 6 tones per material, dark to light ramps, dorsal face darker than the ventral face.
  No noise for its own sake; detail comes from feather rows, veins, rivets, cracks, etc.
- Asymmetry is allowed when it is meaningful (Yin and Yang, patchwork, phantom tears, copper verdigris,
  crystals, cracks, lightning, flames, shadow eyes, nebula stars). Real animals stay symmetric.

## Model (Blockbench, Modded Entity, box UV)

- One file per pair: `src/main/resources/assets/simplywings/rigs/<model>.bbmodel`, real size, 1 px per unit.
  `<model>` is the `model` path of the wing JSON (e.g. `dragonfly` for the meganeura).
- All wings of the pair are in the file. Bone names are unique: `right_` / `left_` for the main pair,
  `right_hind_` / `left_hind_` for a second pair, `right_low_` / `left_low_` for a third, other parts with
  their own names (`tail_1`, `right_shell`, `thruster_left`, ...). The Java rig class decides what each bone does.
- Shoulders attach at (±2.5, 22.5, 2.4) in Blockbench coordinates. Keep the `ref_*` player mannequin with
  `export: false`.
- Feathered wings follow the bird rig: `shoulder > arm (+ tertials) > forearm (+ secondaries) > hand
  (+ hand_feathers, hand_coverts, primaries > primary_1..N)`, so `FeatherWing` can drive them. Membranes,
  insects, crystals and machines use the bones their anatomy needs.
- Feathers and membranes are zero-thickness plates (dz = 0) with cutout transparency. Layer offsets in Z:
  tertials 0.75, secondaries 0.9, hand sheet 1.0, coverts 1.05, primaries 1.15+.
- No gaps when spread: for a feather fan, `L × sin(a) < w`. Check the spread pose from the back.
- Folded: no intersection with the body, tips at knee height at most for bird wings.
- Left side of a symmetric pair = mirror of the right (X flipped, Y and Z rotations negated, `mirror_uv`).

## Texture (pxl)

- Script `art/wings/<model>.px.js`, output copied to `src/main/resources/assets/simplywings/textures/entity/<model>_wings.png`
  (and `<model>_glow.png` for the emissive overlay when the pair glows).
- Box UV plates: north face (ventral) at `(u, v)`, south face (dorsal) at `(u + width, v)`.
- Feathers alternate two tones, dark tips, scalloped trailing edges in transparency.

## Animation (one Java class per pair)

- `render/rig/wings/<Pair>Rig.java` extends `WingRig`, registered in `WingRigs.ALL`.
- Input: `WingMotion` (state weights GROUND, CHARGE, AIR, LANDING, HOVER, CRUISE, DIVE, BRAKE, TUCK, plus
  stall, beat phase, hover phase, buzz, bank, speed, climb, rouse). Per-player memory: `RigMemory`
  (springs ticked in `tick`, read with `get(slot, partialTick)`).
- Shared toolkit, not a generic animator: `Cycle` (looping Catmull-Rom keys), `FeatherWing` (bird fold
  anatomy and fan), `Bone` (rotate, move, scale, visible). Each pair writes its own poses per state and its
  own cycles. Reuse code, never reduce a pair to "the common rig with other numbers".
- The flap cycle timing must match `WingStyle.upFraction` / `downFraction` of the pair (the physics applies
  the gain at the strike).
- Give each pair a signature motion drawn from its real counterpart (e.g. crow rowing beats, owl soft
  wide strokes, gull M-shaped glide, hummingbird figure-eight, swift locked sickle with flicks, pigeon
  wing clap at the top, eagle splayed upturned fingers, bat membrane billowing, beetle shell opening).

## Review loop (mandatory before judging a pair)

1. `tools/studio/start.sh` (once per Java change; restarts the dev client with `-Dsimplywings.studio=true`).
2. For model or texture edits only: `./gradlew processResources`, then send `reload` through the studio.
3. `python -I tools/studio/review.py <model>` writes `run/studio/sheets/<model>_poses.png`,
   `<model>_beat_back.png` and `<model>_beat_side.png`. Read them, fix, repeat.
4. Blockbench captures (offscreen view) for modelling details; `tools/blockbench/rig_preview.js` gives
   `swSnapshot`, `swPair(fold, pose)` to preview a feather pose inside Blockbench.

Studio commands (`run/studio/commands.txt`, one per line): `begin`, `end`, `wing <model>`,
`state <ground|charge|air|landing|hover|cruise|dive|brake|tuck> [amount] [stall]`, `beat <t|off>`,
`hover <0..1|off>`, `time <t|off>`, `rouse <0..1|off>`, `live`, `cam <yaw> <pitch> <distance> [height]`,
`cmd <command>`, `reload`, `wait <ticks>`, `shot <name>`, `mark <id>`, `quit`.

## Done means

- Poses sheet: rest folded cleanly, every state reads well, no gap, no clipping.
- Beat sheets: clear downstroke power, folded recovery, follow-through on the tips.
- `WINGS.md` entry updated (appearance, signature motion), commit and push.
