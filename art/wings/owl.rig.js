var RIG = (() => {
  const b = BIRD({
    arm: 8, forearm: 10, hand: 4, bone: 1.15,
    tertials: 13, secondaries: 16, handSheet: 16, handCoverts: 7, coverts: 7, marginal: 4,
    alula: { angle: 20, length: 5 },
    primaryWidth: 5, primaryStep: [0.5, 0.35],
    primaries: [
      { angle: -4, length: 13 }, { angle: -13, length: 15 }, { angle: -22, length: 16 }, { angle: -31, length: 16 },
      { angle: -40, length: 16 }, { angle: -49, length: 15 }, { angle: -58, length: 15 }, { angle: -68, length: 15 },
      { angle: -78, length: 15 }
    ]
  })
  const rig = { model: 'owl', texture: [128, 64], ...b }
  rig.used = PACK(rig, 128)
  return rig
})()
