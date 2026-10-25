var RIG = (() => {
  const b = BIRD({
    rows: true, arm: 8, forearm: 9, hand: 4,
    tertials: 11, secondaries: 13, handSheet: 13, handCoverts: 6, coverts: 6, marginal: 3,
    primaries: [
      { angle: -5, length: 13 }, { angle: -14, length: 14 }, { angle: -23, length: 15 }, { angle: -32, length: 15 },
      { angle: -41, length: 14 }, { angle: -50, length: 14 }, { angle: -60, length: 13 }, { angle: -70, length: 12 },
      { angle: -81, length: 12 }
    ]
  })
  const T = [0.5, 12, 2]
  const train = [
    { k: 1, angle: -60, length: 17, z: 2.27, display: 80 },
    { k: 2, angle: -70, length: 18, z: 2.23, display: 60 },
    { k: 3, angle: -80, length: 19, z: 2.19, display: 39 },
    { k: 4, angle: -90, length: 20, z: 2.15, display: 18 }
  ]
  b.groups.push({ name: 'train', origin: T })
  for (const f of train) {
    b.groups.push({ name: 'train_' + f.k, parent: 'train', origin: T, rot: [0, 0, f.angle] })
    b.cubes.push({ name: 'train_' + f.k + '_plate', group: 'train_' + f.k, from: [T[0], T[1] - 2.5, f.z], to: [T[0] + f.length, T[1] + 2.5, f.z + 0.02], kind: 'train', index: f.k })
  }
  const rig = { model: 'peacock', texture: [128, 128], train, T, ...b }
  rig.used = PACK(rig, 128)
  return rig
})()
