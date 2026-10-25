var RIG = (() => {
  const major = {
    rows: true, arm: 11, forearm: 15, hand: 6, bone: 1.25, tertials: 16, secondaries: 20, handSheet: 20, handCoverts: 8, coverts: 8,
    alula: { angle: 22, length: 7 }, primaryWidth: 5, primaryStep: [0.6, 0.4],
    primaries: [{ angle: 4, length: 17 }, { angle: -8, length: 22 }, { angle: -19, length: 26 }, { angle: -30, length: 27 },
      { angle: -41, length: 26 }, { angle: -52, length: 25 }, { angle: -63, length: 23 }, { angle: -74, length: 22 }, { angle: -85, length: 21 }]
  }
  const wings = [
    { prefix: '', dy: 0, dz: 0, rot: 0, o: major },
    { prefix: 'second_', dy: -1.6, dz: -0.35, rot: 0, o: major },
    {
      prefix: 'rudder_', dy: -9.5, dz: 0.2, rot: -38, o: {
        arm: 3, forearm: 4, hand: 2, bone: 0.7, tertials: 4, secondaries: 5, handSheet: 5, handCoverts: 3,
        primaryWidth: 3, primaryStep: [0.4, 0.25],
        primaries: [{ angle: 4, length: 7 }, { angle: -14, length: 8 }, { angle: -32, length: 8 }, { angle: -50, length: 7 }, { angle: -68, length: 6 }]
      }
    }
  ]
  const groups = [], cubes = []
  for (const w of wings) {
    const b = BIRD(w.o)
    const shift = (v) => [v[0], v[1] + w.dy, v[2] + w.dz]
    const rename = (n) => n ? w.prefix + n : n
    for (const g of b.groups) {
      const ng = { ...g, name: rename(g.name), parent: rename(g.parent), origin: shift(g.origin) }
      if (g.name === 'shoulder') ng.rot = [0, 0, w.rot]
      groups.push(ng)
    }
    for (const c of b.cubes) cubes.push({ ...c, name: rename(c.name), group: rename(c.group), from: shift(c.from), to: shift(c.to), wing: w.prefix || 'main' })
  }
  const rig = { model: 'seraph', texture: [256, 256], wings, groups, cubes }
  rig.used = PACK(rig, 256)
  return rig
})()
