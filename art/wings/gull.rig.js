var RIG = (() => {
  const b = BIRD({
    rows: true, arm: 10, forearm: 13, hand: 5, bone: 0.9,
    tertials: 8, secondaries: 9, handSheet: 9, handCoverts: 4, coverts: 4, marginal: 2,
    primaryWidth: 3, primaryStep: [0.6, 0.22], primaryDz: 0.05,
    primaries: [
      { angle: -4, length: 17 }, { angle: -9, length: 18 }, { angle: -15, length: 18 }, { angle: -22, length: 17 },
      { angle: -30, length: 15 }, { angle: -40, length: 13 }, { angle: -52, length: 11 }, { angle: -66, length: 10 }
    ]
  })
  const rig = { model: 'gull', texture: [128, 64], ...b }
  rig.used = PACK(rig, 128)
  return rig
})()
