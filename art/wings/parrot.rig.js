var RIG = (() => {
  const b = BIRD({
    arm: 7, forearm: 9, hand: 4,
    tertials: 9, secondaries: 11, handSheet: 11, handCoverts: 5, coverts: 7, marginal: 3,
    alula: { angle: 16, length: 4 },
    primaries: [
      { angle: -3, length: 12 }, { angle: -11, length: 14 }, { angle: -19, length: 15 }, { angle: -27, length: 15 },
      { angle: -36, length: 14 }, { angle: -45, length: 13 }, { angle: -55, length: 12 }, { angle: -66, length: 11 },
      { angle: -78, length: 11 }
    ]
  })
  const rig = { model: 'parrot', texture: [128, 64], ...b }
  rig.used = PACK(rig, 128)
  return rig
})()
