var RIG = (() => {
  const y = 22.5, z = 2.6
  const S = [3.2, y, z], E = [11, y, z], W = [19.5, y, z], T = [27, y, z]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'spar_root', parent: 'shoulder', origin: S },
    { name: 'spar_mid', parent: 'spar_root', origin: E },
    { name: 'spar_tip', parent: 'spar_mid', origin: W },
    { name: 'gear', parent: 'shoulder', origin: [S[0], y, z + 1.2] },
    { name: 'elbow_gear', parent: 'spar_mid', origin: [E[0], y, z + 1] },
    { name: 'aileron', parent: 'spar_tip', origin: [W[0] + 1, y - 7, z + 0.6] },
    { name: 'pack', origin: [0, 20, 3.4], single: true },
    { name: 'thruster_right', parent: 'pack', origin: [4.4, 22, 4.2], single: true },
    { name: 'thruster_left', parent: 'pack', origin: [-4.4, 22, 4.2], single: true },
    { name: 'flame_right', parent: 'thruster_right', origin: [4.4, 15.5, 4.2], single: true },
    { name: 'flame_left', parent: 'thruster_left', origin: [-4.4, 15.5, 4.2], single: true }
  ]
  const cubes = [
    { name: 'housing', group: 'shoulder', from: [1.6, 20.6, 1.6], to: [4.8, 24.4, 4], kind: 'brass_box' },
    { name: 'spar_root_tube', group: 'spar_root', from: [S[0], y - 0.8, z - 0.6], to: [E[0], y + 0.8, z + 0.6], kind: 'brass' },
    { name: 'spar_mid_tube', group: 'spar_mid', from: [E[0], y - 0.7, z - 0.55], to: [W[0], y + 0.7, z + 0.55], kind: 'brass' },
    { name: 'spar_tip_tube', group: 'spar_tip', from: [W[0], y - 0.6, z - 0.5], to: [T[0], y + 0.6, z + 0.5], kind: 'brass' },
    { name: 'elbow_joint', group: 'spar_mid', from: [E[0] - 1.1, y - 1.1, z - 0.9], to: [E[0] + 1.1, y + 1.1, z + 0.9], kind: 'brass_box' },
    { name: 'wrist_joint', group: 'spar_tip', from: [W[0] - 1, y - 1, z - 0.8], to: [W[0] + 1, y + 1, z + 0.8], kind: 'brass_box' },
    { name: 'strut_root', group: 'spar_root', from: [S[0] + 1, y - 9.5, z - 0.35], to: [S[0] + 1.8, y - 0.5, z + 0.35], kind: 'brass' },
    { name: 'strut_mid', group: 'spar_mid', from: [E[0] + 3, y - 10, z - 0.3], to: [E[0] + 3.7, y - 0.5, z + 0.3], kind: 'brass' },
    { name: 'strut_tip', group: 'spar_tip', from: [W[0] + 3, y - 8, z - 0.3], to: [W[0] + 3.6, y - 0.5, z + 0.3], kind: 'brass' },
    { name: 'gear_disc', group: 'gear', from: [S[0] - 1.6, y - 1.6, z + 1.2], to: [S[0] + 1.6, y + 1.6, z + 1.5], kind: 'gear' },
    { name: 'elbow_gear_disc', group: 'elbow_gear', from: [E[0] - 1.3, y - 1.3, z + 1], to: [E[0] + 1.3, y + 1.3, z + 1.3], kind: 'gear' },
    { name: 'aileron_plate', group: 'aileron', from: [W[0] + 1, y - 10.5, z + 0.6], to: [T[0] + 1, y - 7, z + 0.75], kind: 'steel' },
    { name: 'pack_body', group: 'pack', from: [-3.2, 16.5, 2.2], to: [3.2, 23.5, 4.6], kind: 'brass_box' },
    { name: 'pack_dial', group: 'pack', from: [-1, 21, 4.6], to: [1, 23, 4.8], kind: 'dial' }
  ]
  const panels = [
    { name: 'panel_1', parent: 'spar_root', x0: S[0] + 0.5, x1: E[0], h: 10, z: 0.5 },
    { name: 'panel_2', parent: 'spar_mid', x0: E[0], x1: W[0], h: 11, z: 0.62 },
    { name: 'panel_3', parent: 'spar_tip', x0: W[0], x1: T[0] - 1, h: 7, z: 0.74 },
    { name: 'panel_4', parent: 'spar_tip', x0: W[0] + 2, x1: T[0] + 2, h: 3.5, z: 0.86, top: 1 }
  ]
  panels.forEach((p, i) => {
    groups.push({ name: p.name, parent: p.parent, origin: [p.x0, y, z] })
    const top = y + (p.top || 0)
    cubes.push({ name: p.name + '_sheet', group: p.name, from: [p.x0, top - p.h, z + p.z], to: [p.x1, top - 0.4, z + p.z + 0.15], kind: 'steel', index: i })
  })
  const feathers = [{ x: 22, len: 9, rot: -78 }, { x: 24.5, len: 10, rot: -68 }, { x: 27, len: 10, rot: -56 }]
  feathers.forEach((f, i) => {
    const name = 'icarus_' + (i + 1)
    groups.push({ name, parent: 'spar_tip', origin: [f.x, y - 6.5, z + 0.9], rot: [0, 0, f.rot] })
    cubes.push({ name: name + '_quill', group: name, from: [f.x, y - 8, z + 0.9 + 0.04 * i], to: [f.x + f.len, y - 5, z + 0.92 + 0.04 * i], kind: 'feather' })
    cubes.push({ name: name + '_wax', group: name, from: [f.x - 0.6, y - 7.2, z + 0.7], to: [f.x + 1, y - 5.8, z + 1.1], kind: 'wax' })
  })
  for (const [side, sx] of [['right', 4.4], ['left', -4.4]]) {
    cubes.push({ name: 'thruster_' + side + '_barrel', group: 'thruster_' + side, from: [sx - 1.4, 17.5, 2.8], to: [sx + 1.4, 23.5, 5.6], kind: 'brass_box' })
    cubes.push({ name: 'thruster_' + side + '_ring', group: 'thruster_' + side, from: [sx - 1.7, 21.5, 2.5], to: [sx + 1.7, 22.5, 5.9], kind: 'steel_box' })
    cubes.push({ name: 'thruster_' + side + '_nozzle', group: 'thruster_' + side, from: [sx - 1.1, 15.8, 3.1], to: [sx + 1.1, 17.5, 5.3], kind: 'steel_box' })
    cubes.push({ name: 'flame_' + side + '_jet', group: 'flame_' + side, from: [sx - 0.9, 10.5, 4.2], to: [sx + 0.9, 15.8, 4.22], kind: 'jet' })
    cubes.push({ name: 'flame_' + side + '_cross', group: 'flame_' + side, from: [sx, 10.5, 3.3], to: [sx + 0.02, 15.8, 5.1], kind: 'jet_side' })
  }
  const rig = { model: 'daedalus', texture: [128, 128], S, E, W, T, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
