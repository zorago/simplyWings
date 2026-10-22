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
    { name: 'arm_smoke', group: 'tertials', from: [S[0] - 0.5, y - 9, z + 0.6], to: [E[0] + 0.5, y + 1.5, z + 0.62], kind: 'sheet', part: 0 },
    { name: 'forearm_smoke', group: 'secondaries', from: [E[0], y - 10, z + 0.75], to: [W[0] + 0.5, y + 1.5, z + 0.77], kind: 'sheet', part: 1 },
    { name: 'hand_smoke', group: 'hand_feathers', from: [W[0], y - 9, z + 0.9], to: [H[0] + 2, y + 1.5, z + 0.92], kind: 'sheet', part: 2 },
    { name: 'heart', group: 'shoulder', from: [1.3, 21.2, 1.5], to: [4, 23.9, 3.7], kind: 'heart' }
  ]
  const wisp = (name, parent, origin, rot, from, to, kind, index) => {
    groups.push({ name, parent, origin, rot: [0, 0, rot] })
    cubes.push({ name: name + '_wisp', group: name, from, to, kind, index })
  }
  const tert = [{ x: 4, len: 8, rot: -4 }, { x: 7.5, len: 9, rot: -12 }]
  tert.forEach((t, i) => wisp('tertial_' + (i + 1), 'tertials', [t.x, y - 7, z], t.rot, [t.x - 3, y - 7 - t.len, z + 0.68 + 0.04 * i], [t.x + 3, y - 7, z + 0.7 + 0.04 * i], 'curl_v', i))
  const sec = [{ x: 11.5, len: 9, rot: -5 }, { x: 14.5, len: 10, rot: -9 }, { x: 17.5, len: 9, rot: -15 }]
  sec.forEach((t, i) => wisp('secondary_' + (i + 1), 'secondaries', [t.x, y - 8, z], t.rot, [t.x - 3, y - 8 - t.len, z + 0.83 + 0.04 * i], [t.x + 3, y - 8, z + 0.85 + 0.04 * i], 'curl_v', i + 2))
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
    groups.push({ name: 'eye_' + (i + 1), parent: e.parent, origin: e.at })
    cubes.push({ name: 'eye_' + (i + 1) + '_glint', group: 'eye_' + (i + 1), from: [e.at[0] - 1.5, e.at[1] - 0.5, e.at[2]], to: [e.at[0] + 1.5, e.at[1] + 0.5, e.at[2] + 0.02], kind: 'eye' })
  })
  const rig = { model: 'shadow', texture: [128, 64], S, E, W, H, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
