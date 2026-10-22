var RIG = (() => {
  const y = 22.5, z = 2.4
  const S = [2.5, y, z], E = [10, y, z], W = [19, y, z], H = [25, y, z]
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
    { name: 'shoulder_boulder', group: 'shoulder', from: [0.8, 20.2, 1.2], to: [5.2, 25.2, 4.2], kind: 'rock', moss: true },
    { name: 'arm_slab', group: 'arm', from: [S[0] + 1, y - 2.2, z - 1.1], to: [E[0] + 0.5, y + 2, z + 1.3], kind: 'rock', moss: true },
    { name: 'arm_lump', group: 'arm', from: [S[0] + 3, y + 1.5, z - 0.6], to: [S[0] + 7, y + 3, z + 1.1], kind: 'rock', moss: true, origin: [S[0] + 5, y + 2, z], rot: [0, 0, 6] },
    { name: 'forearm_slab', group: 'forearm', from: [E[0], y - 1.6, z - 0.9], to: [W[0] + 0.5, y + 1.6, z + 1.1], kind: 'rock' },
    { name: 'forearm_lump', group: 'forearm', from: [E[0] + 2, y + 1, z - 0.5], to: [E[0] + 6.5, y + 2.4, z + 0.9], kind: 'rock', origin: [E[0] + 4, y + 1.5, z], rot: [0, 0, -5] },
    { name: 'elbow_stone', group: 'forearm', from: [E[0] - 1.4, y - 1.8, z - 1.3], to: [E[0] + 1.4, y + 1.8, z + 1.5], kind: 'rock' },
    { name: 'hand_slab', group: 'hand', from: [W[0], y - 1.2, z - 0.7], to: [H[0], y + 1.2, z + 0.9], kind: 'rock' },
    { name: 'wrist_stone', group: 'hand', from: [W[0] - 1.2, y - 1.4, z - 1], to: [W[0] + 1.2, y + 1.4, z + 1.2], kind: 'rock' }
  ]
  const shard = (name, group, from, to, kind) => cubes.push({ name, group, from, to, kind })
  shard('hand_sheet', 'hand_feathers', [W[0] - 0.5, y - 9.5, z + 0.9], [H[0] - 0.5, y - 1, z + 1.6], 'slate')
  const tert = [{ x: 4.5, len: 9, rot: -4 }, { x: 8, len: 10, rot: -10 }]
  tert.forEach((t, i) => {
    groups.push({ name: 'tertial_' + (i + 1), parent: 'tertials', origin: [t.x, y - 1.5, z], rot: [0, 0, t.rot] })
    shard('tertial_shard_' + (i + 1), 'tertial_' + (i + 1), [t.x - 1.75, y - 1.5 - t.len, z + 0.9 + 0.5 * i], [t.x + 1.75, y - 1.5, z + 1.7 + 0.5 * i], 'slate')
  })
  const sec = [{ x: 11.5, len: 10, rot: -6 }, { x: 14.5, len: 11, rot: -10 }, { x: 17.5, len: 10.5, rot: -15 }]
  sec.forEach((t, i) => {
    groups.push({ name: 'secondary_' + (i + 1), parent: 'secondaries', origin: [t.x, y - 1, z], rot: [0, 0, t.rot] })
    shard('secondary_shard_' + (i + 1), 'secondary_' + (i + 1), [t.x - 1.9, y - 1 - t.len, z + 1.1 + 0.45 * i], [t.x + 1.9, y - 1, z + 1.9 + 0.45 * i], 'slate')
  })
  const prim = [{ angle: 2, len: 10 }, { angle: -14, len: 12 }, { angle: -30, len: 13 }, { angle: -46, len: 12 }, { angle: -62, len: 11 }]
  prim.forEach((p, i) => {
    const px = H[0] - 0.8 * i, py = y - 0.4 * i
    groups.push({ name: 'primary_' + (i + 1), parent: 'primaries', origin: [px, py, z], rot: [0, 0, p.angle] })
    shard('primary_shard_' + (i + 1), 'primary_' + (i + 1), [px - 0.5, py - 1.8, z + 1.2 + 0.5 * i], [px + p.len, py + 1.8, z + 2 + 0.5 * i], 'slate_h')
  })
  const crystals = [
    { name: 'crystal_1', parent: 'arm', at: [6.5, y + 2.8, z + 0.2], rot: [0, 0, 18], size: [1.4, 4, 1.4] },
    { name: 'crystal_2', parent: 'arm', at: [8.2, y + 2.2, z + 0.6], rot: [0, 0, -14], size: [1, 2.6, 1] },
    { name: 'crystal_3', parent: 'forearm', at: [15, y + 1.4, z + 0.2], rot: [0, 0, -24], size: [1.2, 3.4, 1.2] },
    { name: 'crystal_4', parent: 'hand', at: [H[0] - 0.2, y + 0.3, z + 0.2], rot: [0, 0, -78], size: [1.5, 4.5, 1.5] },
    { name: 'crystal_5', parent: 'shoulder', at: [2, 25, z + 0.4], rot: [12, 0, 28], size: [1, 2.4, 1] }
  ]
  crystals.forEach((c) => {
    groups.push({ name: c.name, parent: c.parent, origin: c.at, rot: c.rot })
    const [w, h, d] = c.size
    cubes.push({ name: c.name + '_gem', group: c.name, from: [c.at[0] - w / 2, c.at[1], c.at[2] - d / 2], to: [c.at[0] + w / 2, c.at[1] + h, c.at[2] + d / 2], kind: 'crystal' })
  })
  const rig = { model: 'earth', texture: [128, 128], S, E, W, H, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
