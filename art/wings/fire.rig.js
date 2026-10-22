var RIG = (() => {
  const y = 22.5, z = 2.4
  const S = [2.5, y, z], E = [10, y, z], W = [20, y, z], H = [25, y, z]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'tertials', parent: 'arm', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'secondaries', parent: 'forearm', origin: E },
    { name: 'hand', parent: 'forearm', origin: W },
    { name: 'hand_feathers', parent: 'hand', origin: W },
    { name: 'hand_coverts', parent: 'hand', origin: W },
    { name: 'primaries', parent: 'hand', origin: W }
  ]
  const cubes = [
    { name: 'arm_rod', group: 'arm', from: [S[0], y - 0.9, z - 0.7], to: [E[0], y + 0.9, z + 0.7], kind: 'rod' },
    { name: 'forearm_rod', group: 'forearm', from: [E[0], y - 0.75, z - 0.6], to: [W[0], y + 0.75, z + 0.6], kind: 'rod' },
    { name: 'hand_rod', group: 'hand', from: [W[0], y - 0.6, z - 0.5], to: [H[0], y + 0.6, z + 0.5], kind: 'rod' },
    { name: 'shoulder_heart', group: 'shoulder', from: [1.2, 21, 1.4], to: [4.2, 24, 3.6], kind: 'heart' },
    { name: 'arm_core', group: 'tertials', from: [S[0], y - 7, z + 0.6], to: [E[0], y + 1, z + 0.62], kind: 'core', role: 'arm' },
    { name: 'forearm_core', group: 'secondaries', from: [E[0], y - 8, z + 0.62], to: [W[0], y + 1, z + 0.64], kind: 'core', role: 'forearm' },
    { name: 'hand_core', group: 'hand_feathers', from: [W[0], y - 7, z + 0.64], to: [H[0] + 2, y + 1, z + 0.66], kind: 'core', role: 'hand' },
    { name: 'crest', group: 'hand_coverts', from: [E[0] - 2, y + 0.5, z + 0.7], to: [H[0] + 1, y + 3.5, z + 0.72], kind: 'crest' }
  ]
  const tert = [{ x: 3, len: 9, w: 4, rot: -6 }, { x: 5.5, len: 11, w: 4, rot: -10 }, { x: 8, len: 12, w: 4, rot: -14 }]
  tert.forEach((t, i) => {
    const o = [t.x, y, z]
    groups.push({ name: 'tertial_' + (i + 1), parent: 'tertials', origin: o, rot: [0, 0, t.rot] })
    cubes.push({ name: 'tertial_flame_' + (i + 1), group: 'tertial_' + (i + 1), from: [t.x - t.w / 2, y - t.len, z + 0.75 + 0.04 * i], to: [t.x + t.w / 2, y, z + 0.77 + 0.04 * i], kind: 'tongue_v', index: i })
  })
  const sec = [{ x: 11, len: 12, w: 4, rot: -6 }, { x: 13.5, len: 13, w: 4, rot: -9 }, { x: 16, len: 13, w: 4, rot: -13 }, { x: 18.5, len: 12, w: 4, rot: -18 }]
  sec.forEach((t, i) => {
    const o = [t.x, y, z]
    groups.push({ name: 'secondary_' + (i + 1), parent: 'secondaries', origin: o, rot: [0, 0, t.rot] })
    cubes.push({ name: 'secondary_flame_' + (i + 1), group: 'secondary_' + (i + 1), from: [t.x - t.w / 2, y - t.len, z + 0.9 + 0.04 * i], to: [t.x + t.w / 2, y, z + 0.92 + 0.04 * i], kind: 'tongue_v', index: i + 3 })
  })
  const prim = [
    { angle: 4, len: 13 }, { angle: -9, len: 16 }, { angle: -22, len: 18 }, { angle: -35, len: 18 },
    { angle: -48, len: 17 }, { angle: -61, len: 15 }, { angle: -74, len: 13 }
  ]
  prim.forEach((p, i) => {
    const k = i + 1
    const px = H[0] - 0.6 * i, py = y - 0.3 * i
    groups.push({ name: 'primary_' + k, parent: 'primaries', origin: [px, py, z], rot: [0, 0, p.angle] })
    cubes.push({ name: 'flame_' + k, group: 'primary_' + k, from: [px, py - 2.5, z + 1.15 + 0.07 * i], to: [px + p.len, py + 2.5, z + 1.17 + 0.07 * i], kind: 'tongue_h', index: i })
  })
  const embers = [
    { parent: 'forearm', at: [14, y - 6, z + 1.6] }, { parent: 'hand', at: [27, y - 4, z + 1.8] },
    { parent: 'hand', at: [31, y - 11, z + 1.9] }, { parent: 'forearm', at: [18, y - 13, z + 1.7] }
  ]
  embers.forEach((e, i) => {
    groups.push({ name: 'ember_' + (i + 1), parent: e.parent, origin: e.at })
    cubes.push({ name: 'ember_cube_' + (i + 1), group: 'ember_' + (i + 1), from: [e.at[0] - 0.5, e.at[1] - 0.5, e.at[2] - 0.5], to: [e.at[0] + 0.5, e.at[1] + 0.5, e.at[2] + 0.5], kind: 'ember' })
  })
  const rig = { model: 'fire', texture: [128, 128], S, E, W, H, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
