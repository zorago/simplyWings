var RIG = (() => {
  const wings = [
    {
      prefix: '', dy: 0, dz: 0, rot: 0, o: {
        rows: true, arm: 9, forearm: 12, hand: 5, bone: 1.1, tertials: 13, secondaries: 16, handSheet: 16, handCoverts: 7, coverts: 7,
        alula: { angle: 22, length: 6 }, primaryWidth: 5, primaryStep: [0.55, 0.35],
        primaries: [{ angle: 4, length: 14 }, { angle: -8, length: 18 }, { angle: -19, length: 21 }, { angle: -30, length: 22 },
          { angle: -41, length: 21 }, { angle: -52, length: 20 }, { angle: -63, length: 19 }, { angle: -74, length: 18 }, { angle: -85, length: 17 }]
      }
    },
    {
      prefix: 'high_', dy: 2.2, dz: -0.5, rot: 32, o: {
        rows: true, arm: 7, forearm: 9, hand: 4, bone: 0.9, tertials: 9, secondaries: 11, handSheet: 11, handCoverts: 6,
        primaryWidth: 4, primaryStep: [0.5, 0.3],
        primaries: [{ angle: 4, length: 11 }, { angle: -9, length: 14 }, { angle: -22, length: 16 }, { angle: -35, length: 16 },
          { angle: -48, length: 15 }, { angle: -61, length: 14 }, { angle: -74, length: 13 }]
      }
    },
    {
      prefix: 'low_', dy: -5.5, dz: 0.6, rot: -30, o: {
        rows: true, arm: 6, forearm: 8, hand: 4, bone: 0.85, tertials: 8, secondaries: 10, handSheet: 10, handCoverts: 5,
        primaryWidth: 4, primaryStep: [0.5, 0.3],
        primaries: [{ angle: 4, length: 10 }, { angle: -9, length: 13 }, { angle: -22, length: 15 }, { angle: -35, length: 15 },
          { angle: -48, length: 14 }, { angle: -61, length: 13 }, { angle: -74, length: 12 }]
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
  const rig = { model: 'seraph', texture: [256, 128], wings, groups, cubes }
  rig.used = PACK(rig, 256)
  return rig
})()
