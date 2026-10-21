var RIG = (() => {
  const y = 22.5, z = 2.6
  const S = [2.5, y, z], E = [11.5, y, z], W = [20.5, y, z]
  const groups = [
    { name: 'yoke', origin: [0, 22.5, 3.2], single: true },
    { name: 'pulley', parent: 'yoke', origin: [0, 22.5, 4.6], single: true },
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'hand', parent: 'forearm', origin: W, rot: [0, 0, -8] }
  ]
  const cubes = [
    { name: 'yoke_beam', group: 'yoke', from: [-3.5, 21.5, 2.6], to: [3.5, 23.5, 4.2], kind: 'wood' },
    { name: 'pulley_wheel', group: 'pulley', from: [-1.5, 21, 4.2], to: [1.5, 24, 5.0], kind: 'pulley' },
    { name: 'arm_spar', group: 'arm', from: [S[0], y - 0.6, z - 0.6], to: [E[0], y + 0.6, z + 0.6], kind: 'wood' },
    { name: 'forearm_spar', group: 'forearm', from: [E[0], y - 0.5, z - 0.5], to: [W[0], y + 0.5, z + 0.5], kind: 'wood' },
    { name: 'hand_spar', group: 'hand', from: [W[0], y - 0.4, z - 0.4], to: [W[0] + 10, y + 0.4, z + 0.4], kind: 'wood' },
    { name: 'elbow_hinge', group: 'forearm', from: [E[0] - 0.9, y - 0.9, z - 0.9], to: [E[0] + 0.9, y + 0.9, z + 0.9], kind: 'brass' },
    { name: 'wrist_hinge', group: 'hand', from: [W[0] - 0.8, y - 0.8, z - 0.8], to: [W[0] + 0.8, y + 0.8, z + 0.8], kind: 'brass' },
    { name: 'shoulder_mount', group: 'shoulder', from: [S[0] - 1, y - 1, z - 1], to: [S[0] + 1, y + 1, z + 1], kind: 'brass' },
    { name: 'arm_canvas', group: 'arm', from: [S[0], 7, z + 0.05], to: [E[0], y, z + 0.07], kind: 'canvas' },
    { name: 'forearm_canvas', group: 'forearm', from: [E[0], 7, z + 0.09], to: [W[0] + 3, y, z + 0.11], kind: 'canvas' },
    { name: 'hand_canvas', group: 'hand', from: [W[0], 9, z + 0.13], to: [W[0] + 11, y, z + 0.15], kind: 'canvas' }
  ]
  const ribs = [['arm', 6, 8.5, 0], ['arm', 10, 8, -6], ['forearm', 14.5, 8.5, -14], ['forearm', 18.5, 9.5, -22], ['hand', 23, 11, -32], ['hand', 27, 14, -42]]
  ribs.forEach(([g, x, bottom, tilt], i) => {
    cubes.push({ name: 'rib_' + (i + 1), group: g, from: [x - 0.3, bottom, z - 0.3], to: [x + 0.3, y, z + 0.3], origin: [x, y, z], rot: [0, 0, tilt], kind: 'wood' })
    cubes.push({ name: 'lashing_' + (i + 1), group: g, from: [x - 0.5, y - 0.8, z - 0.5], to: [x + 0.5, y + 0.8, z + 0.5], kind: 'rope' })
  })
  const rig = { model: 'ornithopter', texture: [128, 64], S, E, W, ribs, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
