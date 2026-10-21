var RIG = (() => {
  const b = BIRD({
    arm: 4, forearm: 5, hand: 5, bone: 0.85,
    tertials: 5, secondaries: 6, handSheet: 7, handCoverts: 4, coverts: 3, marginal: 2,
    primaryWidth: 3, primaryStep: [0.55, 0.3], primaryDz: 0.05,
    primaries: [
      { angle: -10, length: 22 }, { angle: -14, length: 21 }, { angle: -19, length: 20 }, { angle: -25, length: 18 },
      { angle: -32, length: 15 }, { angle: -40, length: 12 }, { angle: -50, length: 10 }, { angle: -62, length: 8 }
    ]
  })
  const rig = { model: 'swift', texture: [128, 64], ...b }
  rig.used = PACK(rig, 128)
  return rig
})()
