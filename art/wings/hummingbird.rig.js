var RIG = (() => {
  const b = BIRD({
    arm: 2, forearm: 3, hand: 3, bone: 0.7,
    tertials: 4, secondaries: 5, handSheet: 6, handCoverts: 3, coverts: 3, marginal: 2,
    primaryWidth: 3, primaryStep: [0.35, 0.25], primaryDz: 0.05,
    primaries: [
      { angle: 0, length: 10 }, { angle: -6, length: 10 }, { angle: -12, length: 10 }, { angle: -18, length: 9 },
      { angle: -25, length: 9 }, { angle: -32, length: 8 }, { angle: -40, length: 7 }, { angle: -49, length: 6 },
      { angle: -59, length: 5 }
    ]
  })
  const S = b.S
  for (const g of ['ghost_a', 'ghost_b']) {
    b.groups.push({ name: g, origin: S })
    b.cubes.push({ name: g + '_plate', group: g, from: [S[0], 14.5, S[2] + 0.5], to: [S[0] + 19, 24.5, S[2] + 0.52], kind: 'ghost' })
  }
  const rig = { model: 'hummingbird', texture: [64, 64], ...b }
  rig.used = PACK(rig, 64)
  const ghosts = rig.cubes.filter(c => c.kind === 'ghost')
  ghosts[1].uv = ghosts[0].uv.slice()
  return rig
})()
