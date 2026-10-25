var RIG = (() => {
  const dx = 0.5, dz = 1.6
  const b = BIRD({
    rows: true, arm: 8, forearm: 10, hand: 5, bone: 1.1, tertials: 10, secondaries: 13, handSheet: 13, handCoverts: 4, coverts: 3,
    primaryWidth: 3, primaryStep: [0.55, 0.3],
    primaries: [{ angle: 2, length: 17 }, { angle: -10, length: 19 }, { angle: -22, length: 20 }, { angle: -34, length: 19 },
      { angle: -46, length: 17 }, { angle: -58, length: 15 }, { angle: -70, length: 13 }]
  })
  const shift = (v) => [v[0] + dx, v[1], v[2] + dz]
  const groups = b.groups.map(g => ({ ...g, origin: shift(g.origin) }))
  const cubes = b.cubes.map(c => ({ ...c, from: shift(c.from), to: shift(c.to) }))
  groups.push(
    { name: 'pack', origin: [0, 20, 3.4], single: true },
    { name: 'thruster_right', parent: 'pack', origin: [1.9, 17, 5.6], single: true },
    { name: 'thruster_left', parent: 'pack', origin: [-1.9, 17, 5.6], single: true },
    { name: 'flame_right', parent: 'thruster_right', origin: [1.9, 13.6, 5.6], single: true },
    { name: 'flame_left', parent: 'thruster_left', origin: [-1.9, 13.6, 5.6], single: true }
  )
  cubes.push(
    { name: 'pack_body', group: 'pack', from: [-3.4, 16, 2.1], to: [3.4, 24, 4.5], kind: 'pack' },
    { name: 'pack_spine', group: 'pack', from: [-1, 16.5, 4.5], to: [1, 23.5, 5.1], kind: 'steel_box' },
    { name: 'pack_dial', group: 'pack', from: [-0.8, 21.6, 5.1], to: [0.8, 23, 5.3], kind: 'dial' }
  )
  cubes.push({ name: 'pack_mount', group: 'pack', from: [-3, 16.5, 4.5], to: [3, 19, 5.4], kind: 'steel_box' })
  for (const [side, sx] of [['right', 1.9], ['left', -1.9]]) {
    cubes.push({ name: 'thruster_' + side + '_barrel', group: 'thruster_' + side, from: [sx - 1.1, 15.2, 4.5], to: [sx + 1.1, 18.6, 6.7], kind: 'steel_box' })
    cubes.push({ name: 'thruster_' + side + '_nozzle', group: 'thruster_' + side, from: [sx - 0.8, 13.6, 4.8], to: [sx + 0.8, 15.2, 6.4], kind: 'brass_box' })
    cubes.push({ name: 'flame_' + side + '_jet', group: 'flame_' + side, from: [sx - 0.7, 8.6, 5.6], to: [sx + 0.7, 13.6, 5.62], kind: 'jet' })
    cubes.push({ name: 'flame_' + side + '_cross', group: 'flame_' + side, from: [sx, 8.6, 4.9], to: [sx + 0.02, 13.6, 6.3], kind: 'jet_side' })
  }
  const rig = { model: 'daedalus', texture: [128, 128], groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
