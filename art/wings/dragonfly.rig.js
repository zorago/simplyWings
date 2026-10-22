var RIG = (() => {
  const S = [2.4, 22.6, 2.4]
  const H = [2.2, 20.4, 2.4]
  const fore = [[0, 1.2], [3, 0.4], [10, 0.2], [18, 0.4], [23, 0.9], [24.6, 2.2], [24.4, 3.6], [22, 4.9], [17, 5.6], [11, 5.8], [6, 5.2], [2.5, 3.8], [0, 2.6]]
  const hind = [[0, 1.0], [3, 0.3], [10, 0.2], [17, 0.4], [21, 0.9], [22.6, 2.2], [22.3, 3.7], [19.5, 5.0], [14, 5.6], [9, 6.4], [5.5, 7.6], [3, 7.4], [1.2, 5.8], [0, 3.4]]
  for (const q of fore) q[1] *= 7 / 6
  for (const q of hind) q[1] *= 9 / 8
  const groups = [
    { name: 'fore', origin: S, rot: [0, 0, -18] },
    { name: 'fore_ghost_a', parent: 'fore', origin: S },
    { name: 'fore_ghost_b', parent: 'fore', origin: S },
    { name: 'hind', origin: H, rot: [0, 0, -40] },
    { name: 'hind_ghost_a', parent: 'hind', origin: H },
    { name: 'hind_ghost_b', parent: 'hind', origin: H },
    { name: 'brooch', origin: [0, 21.5, 2.4], single: true }
  ]
  const cubes = [
    { name: 'fore_wing', group: 'fore', from: [S[0], S[1] - 7, S[2] + 0.62], to: [S[0] + 25, S[1], S[2] + 0.64], kind: 'fore', shape: fore },
    { name: 'hind_wing', group: 'hind', from: [H[0], H[1] - 9, H[2] + 0.3], to: [H[0] + 23, H[1], H[2] + 0.32], kind: 'hind', shape: hind },
    { name: 'fore_costa', group: 'fore', from: [S[0] + 1, S[1] - 0.7, S[2] + 0.4], to: [S[0] + 19, S[1] + 0.1, S[2] + 0.9], kind: 'costa' },
    { name: 'hind_costa', group: 'hind', from: [H[0] + 1, H[1] - 0.7, H[2] + 0.1], to: [H[0] + 17, H[1] + 0.1, H[2] + 0.55], kind: 'costa' },
    { name: 'fore_stigma', group: 'fore', from: [S[0] + 19.5, S[1] - 1.1, S[2] + 0.5], to: [S[0] + 22, S[1] + 0.1, S[2] + 0.8], kind: 'stigma' },
    { name: 'hind_stigma', group: 'hind', from: [H[0] + 17.5, H[1] - 1.1, H[2] + 0.2], to: [H[0] + 20, H[1] + 0.1, H[2] + 0.45], kind: 'stigma' },
    { name: 'fore_hinge', group: 'fore', from: [S[0] - 0.6, S[1] - 2.2, S[2] - 0.2], to: [S[0] + 1.4, S[1] + 0.4, S[2] + 1.1], kind: 'hinge' },
    { name: 'hind_hinge', group: 'hind', from: [H[0] - 0.6, H[1] - 2.4, H[2] - 0.3], to: [H[0] + 1.2, H[1] + 0.2, H[2] + 0.8], kind: 'hinge' },
    { name: 'fore_ghost_a_plate', group: 'fore_ghost_a', from: [S[0], S[1] - 7, S[2] + 0.7], to: [S[0] + 25, S[1], S[2] + 0.72], kind: 'fore_ghost' },
    { name: 'fore_ghost_b_plate', group: 'fore_ghost_b', from: [S[0], S[1] - 7, S[2] + 0.54], to: [S[0] + 25, S[1], S[2] + 0.56], kind: 'fore_ghost' },
    { name: 'hind_ghost_a_plate', group: 'hind_ghost_a', from: [H[0], H[1] - 9, H[2] + 0.38], to: [H[0] + 23, H[1], H[2] + 0.4], kind: 'hind_ghost' },
    { name: 'hind_ghost_b_plate', group: 'hind_ghost_b', from: [H[0], H[1] - 9, H[2] + 0.22], to: [H[0] + 23, H[1], H[2] + 0.24], kind: 'hind_ghost' },
    { name: 'brooch_setting', group: 'brooch', from: [-2, 19, 2.1], to: [2, 24, 2.9], kind: 'setting' },
    { name: 'brooch_amber', group: 'brooch', from: [-1.5, 19.5, 2.6], to: [1.5, 23.5, 3.6], kind: 'amber' },
    { name: 'brooch_tip', group: 'brooch', from: [-0.5, 18, 2.3], to: [0.5, 19, 2.9], kind: 'setting_tip' }
  ]
  const rig = { model: 'dragonfly', texture: [128, 64], S, H, groups, cubes }
  rig.used = PACK(rig, 128)
  const share = (kind) => {
    const list = cubes.filter(c => c.kind === kind)
    for (const c of list) c.uv = list[0].uv.slice()
  }
  share('fore_ghost')
  share('hind_ghost')
  return rig
})()
