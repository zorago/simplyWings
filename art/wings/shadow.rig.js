var RIG = (() => {
  const y = 22.5, z = 2.4
  const S = [2.5, y, z], E = [10, y, z], W = [19.5, y, z], H = [24, y, z]
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
    { name: 'heart', group: 'shoulder', from: [1.3, 21.2, 1.5], to: [4, 23.9, 3.7], kind: 'heart' }
  ]
  const wisp = (name, parent, origin, rot, from, to, kind, index) => {
    groups.push({ name, parent, origin, rot: [0, 0, rot] })
    cubes.push({ name: name + '_wisp', group: name, from, to, kind, index })
  }
  const row = (name, parent, list, zBase, zStep, offset) => list.forEach((t, i) => {
    wisp(name + '_feather_' + (i + 1), parent, [t.x, y, z], t.rot, [t.x - 2.5, y - t.len, z + zBase + zStep * i], [t.x + 2.5, y + 0.5, z + zBase + zStep * i + 0.02], 'plume', i + offset)
  })
  row('tertial', 'tertials', [{ x: 3.6, len: 12, rot: -4 }, { x: 5.6, len: 13, rot: -7 }, { x: 7.6, len: 13, rot: -10 }, { x: 9.4, len: 14, rot: -13 }], 0.68, 0.04, 0)
  row('secondary', 'secondaries', [{ x: 11.2, len: 15, rot: -5 }, { x: 13.2, len: 16, rot: -7 }, { x: 15.2, len: 16, rot: -10 }, { x: 17.2, len: 15, rot: -13 }, { x: 19, len: 15, rot: -16 }], 0.83, 0.04, 4)
  row('hand', 'hand_feathers', [{ x: 20.2, len: 15, rot: -19 }, { x: 21.4, len: 14, rot: -23 }, { x: 22.6, len: 14, rot: -27 }, { x: 23.6, len: 13, rot: -31 }], 0.98, 0.02, 9)
  const prim = [{ angle: 6, len: 13 }, { angle: -8, len: 15 }, { angle: -23, len: 16 }, { angle: -38, len: 16 }, { angle: -53, len: 15 }, { angle: -68, len: 13 }]
  prim.forEach((p, i) => {
    const px = H[0] - 0.6 * i, py = y - 0.3 * i
    wisp('primary_' + (i + 1), 'primaries', [px, py, z], p.angle, [px, py - 2.5, z + 1.15 + 0.07 * i], [px + p.len, py + 2.5, z + 1.17 + 0.07 * i], 'curl_h', i)
  })
  const eyes = [
    { parent: 'arm', at: [6.5, y - 3, z + 1.3] }, { parent: 'forearm', at: [13, y - 4, z + 1.3] },
    { parent: 'forearm', at: [17, y - 7, z + 1.35] }, { parent: 'hand', at: [23, y - 3.5, z + 1.6] },
    { parent: 'hand', at: [28, y - 7, z + 1.75] }
  ]
  eyes.forEach((e, i) => {
    const g = 'eye_' + (i + 1)
    groups.push({ name: g, parent: e.parent, origin: e.at })
    cubes.push({ name: g + '_white', group: g, from: [e.at[0] - 2.5, e.at[1] - 2, e.at[2]], to: [e.at[0] + 2.5, e.at[1] + 2, e.at[2] + 0.02], kind: 'eye' })
    groups.push({ name: g + '_pupil', parent: g, origin: e.at })
    cubes.push({ name: g + '_slit', group: g + '_pupil', from: [e.at[0] - 0.5, e.at[1] - 1, e.at[2] + 0.04], to: [e.at[0] + 0.5, e.at[1] + 1, e.at[2] + 0.06], kind: 'pupil' })
  })
  const rig = { model: 'shadow', texture: [128, 64], S, E, W, H, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
