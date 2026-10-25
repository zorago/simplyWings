var RIG = (() => {
  const b = BIRD({
    rows: true, arm: 8, forearm: 10, hand: 4, bone: 1.2,
    tertials: 10, secondaries: 12, handSheet: 12, handCoverts: 5, coverts: 6, marginal: 3,
    primaryWidth: 4, primaryStep: [0.55, 0.32],
    primaries: [
      { angle: -3, length: 14 }, { angle: -13, length: 16 }, { angle: -23, length: 16 }, { angle: -33, length: 15 },
      { angle: -43, length: 14 }, { angle: -54, length: 13 }, { angle: -66, length: 12 }
    ]
  })
  const S = b.S, E = b.E
  b.cubes.push(
    { name: 'bulb', group: 'shoulder', from: [S[0] - 1, S[1] + 0.5, S[2] - 1], to: [S[0] + 1, S[1] + 2.5, S[2] + 1], kind: 'bulb' },
    { name: 'bulb_cap', group: 'shoulder', from: [S[0] - 1.25, S[1] + 2.5, S[2] - 1.25], to: [S[0] + 1.25, S[1] + 3, S[2] + 1.25], kind: 'cap' },
    { name: 'piston_rod', group: 'arm', from: [S[0] + 1, S[1] + 1, S[2] - 0.3], to: [E[0] - 1, S[1] + 1.6, S[2] + 0.3], kind: 'rod' },
    { name: 'piston_body', group: 'arm', from: [S[0] + 0.5, S[1] + 0.7, S[2] - 0.6], to: [S[0] + 4.5, S[1] + 1.9, S[2] + 0.6], kind: 'cap' },
    { name: 'elbow_gear', group: 'forearm', from: [E[0] - 1.2, S[1] - 1.2, S[2] - 0.8], to: [E[0] + 1.2, S[1] + 1.2, S[2] + 0.8], kind: 'gear' }
  )
  const rig = { model: 'copper', texture: [128, 128], ...b }
  rig.used = PACK(rig, 128)
  const plates = rig.cubes.filter(c => c.kind !== 'bone' && c.to[2] - c.from[2] < 0.05)
  const copies = plates.map(c => ({ ...c, uv: null, source: c }))
  PACK({ cubes: copies }, 128, rig.used)
  for (const c of copies) c.source.leftUv = c.uv
  rig.used = Math.max(...copies.map(c => c.uv[1] + Math.ceil(c.to[1] - c.from[1]) + 1))
  return rig
})()
