var RIG = (() => {
  const b = BIRD({
    rows: true, arm: 9, forearm: 12, hand: 5, bone: 1.25,
    tertials: 13, secondaries: 17, handSheet: 17, handCoverts: 7, coverts: 7, marginal: 4,
    alula: { angle: 20, length: 6 },
    primaryWidth: 5, primaryStep: [0.6, 0.4],
    primaries: [
      { angle: 3, length: 15 }, { angle: -8, length: 19 }, { angle: -19, length: 21 }, { angle: -29, length: 21 },
      { angle: -38, length: 20 }, { angle: -47, length: 19 }, { angle: -56, length: 18 }, { angle: -66, length: 17 },
      { angle: -77, length: 16 }
    ]
  })
  const rig = { model: 'eagle', texture: [128, 128], ...b }
  rig.used = PACK(rig, 128)
  return rig
})()
