var RIG = (() => {
  const y = 22.5, z = 2.4
  const S = [2.5, y, z], E = [10.5, y, z], W = [20.5, y, z], H = [25, y, z]
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
    { name: 'arm_cloud', group: 'tertials', from: [S[0] - 0.5, y - 11, z + 0.6], to: [E[0] + 0.5, y + 2, z + 0.62], kind: 'cloud', part: 0 },
    { name: 'forearm_cloud', group: 'secondaries', from: [E[0], y - 12, z + 0.75], to: [W[0] + 0.5, y + 2, z + 0.77], kind: 'cloud', part: 1 },
    { name: 'hand_cloud', group: 'hand_feathers', from: [W[0], y - 11, z + 0.9], to: [H[0] + 2, y + 2, z + 0.92], kind: 'cloud', part: 2 },
    { name: 'arm_ridge', group: 'arm', from: [S[0], y - 0.7, z - 0.5], to: [E[0], y + 0.7, z + 0.6], kind: 'ridge' },
    { name: 'forearm_ridge', group: 'forearm', from: [E[0], y - 0.6, z - 0.45], to: [W[0], y + 0.6, z + 0.55], kind: 'ridge' },
    { name: 'hand_ridge', group: 'hand', from: [W[0], y - 0.5, z - 0.4], to: [H[0], y + 0.5, z + 0.5], kind: 'ridge' }
  ]
  const lobe = (name, parent, origin, rot, from, to, kind, index) => {
    groups.push({ name, parent, origin, rot: [0, 0, rot] })
    cubes.push({ name: name + '_lobe', group: name, from, to, kind, index })
  }
  const tert = [{ x: 4.5, len: 7, rot: -5 }, { x: 8.5, len: 8, rot: -12 }]
  tert.forEach((t, i) => lobe('tertial_' + (i + 1), 'tertials', [t.x, y - 9, z], t.rot, [t.x - 3, y - 9 - t.len, z + 0.68 + 0.04 * i], [t.x + 3, y - 9, z + 0.7 + 0.04 * i], 'lobe_v', i))
  const sec = [{ x: 12.5, len: 8, rot: -5 }, { x: 16, len: 9, rot: -10 }, { x: 19.5, len: 8, rot: -16 }]
  sec.forEach((t, i) => lobe('secondary_' + (i + 1), 'secondaries', [t.x, y - 10, z], t.rot, [t.x - 3, y - 10 - t.len, z + 0.83 + 0.04 * i], [t.x + 3, y - 10, z + 0.85 + 0.04 * i], 'lobe_v', i + 2))
  const prim = [{ angle: 4, len: 14 }, { angle: -12, len: 16 }, { angle: -28, len: 16 }, { angle: -44, len: 15 }, { angle: -60, len: 13 }]
  prim.forEach((p, i) => {
    const px = H[0] - 0.7 * i, py = y - 0.35 * i
    lobe('primary_' + (i + 1), 'primaries', [px, py, z], p.angle, [px, py - 3, z + 1.15 + 0.07 * i], [px + p.len, py + 3, z + 1.17 + 0.07 * i], 'lobe_h', i)
  })
  groups.push({ name: 'galaxy', parent: 'hand', origin: [W[0] + 4, y - 5, z + 1.7] })
  cubes.push({ name: 'galaxy_disc', group: 'galaxy', from: [W[0] + 1, y - 8, z + 1.7], to: [W[0] + 7, y - 2, z + 1.72], kind: 'galaxy' })
  const stars = [
    { parent: 'arm', at: [5, y - 5, z + 1.2] }, { parent: 'forearm', at: [13, y - 3, z + 1.2] }, { parent: 'forearm', at: [17.5, y - 8, z + 1.25] },
    { parent: 'hand', at: [28, y - 2, z + 1.8] }, { parent: 'hand', at: [33, y - 9, z + 1.9] }, { parent: 'arm', at: [8, y - 10, z + 1.2] }
  ]
  stars.forEach((s, i) => {
    groups.push({ name: 'star_' + (i + 1), parent: s.parent, origin: s.at })
    cubes.push({ name: 'star_' + (i + 1) + '_spark', group: 'star_' + (i + 1), from: [s.at[0] - 1.5, s.at[1] - 1.5, s.at[2]], to: [s.at[0] + 1.5, s.at[1] + 1.5, s.at[2] + 0.02], kind: 'star' })
  })
  const rig = { model: 'nebula', texture: [128, 64], S, E, W, H, groups, cubes }
  rig.used = PACK(rig, 128)
  const sparks = cubes.filter(c => c.kind === 'star')
  for (const c of sparks) c.uv = sparks[0].uv.slice()
  return rig
})()
