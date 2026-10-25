var RIG = (() => {
  const b = BIRD({
    rows: true, arm: 8, forearm: 10, hand: 4,
    tertials: 12, secondaries: 15, handSheet: 15, handCoverts: 6, coverts: 6, marginal: 3,
    alula: { angle: 18, length: 5 },
    primaries: [
      { angle: -2, length: 13 }, { angle: -13, length: 16 }, { angle: -24, length: 18 }, { angle: -34, length: 18 },
      { angle: -43, length: 17 }, { angle: -52, length: 16 }, { angle: -62, length: 15 }, { angle: -73, length: 15 }
    ]
  })
  const rig = { model: 'crow', texture: [128, 64], ...b }
  rig.used = PACK(rig, 128)
  return rig
})()
