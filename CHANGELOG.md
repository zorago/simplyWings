# Changelog

## 1.0.0 (beta), October 29, 2026

First public version.

### Flight
- Two flight modes: a stabilized hover for building and a keyboard-steered travel flight with wing beats, glide, dive, tuck, stall, brake, crash and stamina.
- Air jumps, free rescue jumps, very fast double tap to hover, charged takeoff, mouse-orbited camera.
- Hovering uses the vanilla movement keys; travel flight has six keys of its own in the *Simply Wings* category (go up, go down, turn left, turn right, accelerate, brake), opposite keys cancel each other, steering at 10 % while braking.
- Logging back in while flying resumes the hover, so servers do not kick for floating.
- Crashes: hitting anything (ground, wall, tree…) in travel flight and losing more than 10 m/s within 0.15 s hurts, ignoring armor like an elytra crash. A wall hit at the top speed of common wings kills (21 damage), slightly slower survives; losing the whole terminal velocity deals 100 (50 hearts), so it stays lethal with modded extra hearts. Braking and stall recovery never hurt.

### Wings
- 41 pairs, each with its own model, texture, animation, stats, collection bonus, sounds and identity.
- Mythic Night and Light: tails, dive shriek, charged dash with invisibility, set bonuses (infinite stamina, transmog).
- The player's cape is hidden while wings are shown.

### Progression
- Assembly Table, Forge Bench and Cloud Compressor; four essences, flask and tanks, harvest in the three dimensions, intensified essence as a stamina substitute.
- Winged energy and 59 drop sources from mobs, blocks and fishing.
- Five End sanctums placed on successive dragon deaths and repaired on later ones, one compass each; the Sky Sanctum's barrels of leather and feathers are restocked on every repair.

### Wing gacha
- Summoning feathers in chest loot, five tiers plus mythics, pity counters.
- Draconic Altar with an in-world summoning show seen and heard by every nearby player, cards, history and rates.
- Offering Fountain, an octagonal four-block multiblock that turns mod items back into feathers.

### Other
- In-game wiki (270 pages), optional JEI plugin (essence containers told apart by type).
- Full sound design, heard by nearby players.
- No dependency besides NeoForge, no mixin, no access transformer.
