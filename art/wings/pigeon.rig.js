var RIG = (() => {
  const b = BIRD({
    arm: 8, forearm: 10, hand: 4,
    tertials: 10, secondaries: 12, handSheet: 12, handCoverts: 5, coverts: 6, marginal: 3,
    alula: { angle: 16, length: 4 },
    primaries: [
      { angle: -4, length: 15 }, { angle: -12, length: 17 }, { angle: -20, length: 17 }, { angle: -28, length: 16 },
      { angle: -37, length: 15 }, { angle: -46, length: 14 }, { angle: -56, length: 13 }, { angle: -67, length: 12 },
      { angle: -79, length: 12 }
    ]
  })
  const rig = { model: 'pigeon', texture: [128, 64], ...b }
  rig.used = PACK(rig, 128)
  return rig
})()
