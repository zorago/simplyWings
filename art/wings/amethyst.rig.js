var RIG = (() => {
  const y = 22.5, z = 2.4
  const S = [2.5, y, z], E = [9.5, y, z], W = [17.5, y, z], H = [20.5, y, z]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'tertials', parent: 'arm', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'secondaries', parent: 'forearm', origin: E },
    { name: 'hand', parent: 'forearm', origin: W },
    { name: 'primaries', parent: 'hand', origin: W }
  ]
  const cubes = [
    { name: 'arm_bar', group: 'arm', from: [S[0], y - 1, z - 0.5], to: [E[0], y + 1, z + 0.5], kind: 'bar' },
    { name: 'forearm_bar', group: 'forearm', from: [E[0], y - 0.5, z - 0.5], to: [W[0], y + 0.5, z + 0.5], kind: 'bar' },
    { name: 'hand_bar', group: 'hand', from: [W[0], y - 0.5, z - 0.5], to: [H[0], y + 0.5, z + 0.5], kind: 'bar' },
    { name: 'druse_1', group: 'shoulder', from: [2, 22, 1.5], to: [4, 26, 3.5], origin: [3, 22.5, 2.5], rot: [0, 0, 18], kind: 'crystal', glow: true },
    { name: 'druse_2', group: 'shoulder', from: [3.5, 22.5, 2], to: [5.5, 25.5, 3], origin: [4, 22.5, 2.5], rot: [0, 0, -22], kind: 'crystal' },
    { name: 'druse_3', group: 'shoulder', from: [1.5, 23, 2.5], to: [2.5, 25, 3.5], origin: [2, 23, 3], rot: [-15, 0, 35], kind: 'crystal' },
    { name: 'wrist_druse_1', group: 'hand', from: [W[0] - 0.5, y, 1.9], to: [W[0] + 1.5, y + 3, 2.9], origin: [W[0], y, 2.4], rot: [0, 0, 25], kind: 'crystal', glow: true, only: 'right' },
    { name: 'wrist_druse_2', group: 'hand', from: [W[0] + 1, y, 2.0], to: [W[0] + 2, y + 2, 3.0], origin: [W[0] + 1, y, 2.4], rot: [0, 0, -15], kind: 'crystal', only: 'right' },
    { name: 'elbow_druse_1', group: 'forearm', from: [E[0] - 0.5, y, 1.9], to: [E[0] + 1.5, y + 3, 2.9], origin: [E[0], y, 2.4], rot: [0, 0, -20], kind: 'crystal', glow: true, only: 'left' }
  ]
  const hang = (group, row, xs, lengths, z0) => lengths.forEach((L, i) => {
    const x = xs[i], name = row + '_' + (i + 1)
    groups.push({ name, parent: group, origin: [x, y, z0 + 0.6], rot: [0, 0, (i % 2 ? -4 : 3)] })
    const zz = z0 + 0.03 * i
    cubes.push({ name: name + '_shard', group: name, from: [x - 1, y - L - 0.5, zz], to: [x + 1, y - 0.5, zz + 1], kind: 'crystal', glow: i === 1 })
    cubes.push({ name: name + '_tip', group: name, from: [x - 0.5, y - L - 2.5, zz], to: [x + 0.5, y - L - 0.5, zz + 1], kind: 'tip' })
  })
  hang('tertials', 'tertial_feather', [3.6, 5.3, 7, 8.7], [6, 7, 7, 6], z + 0.6)
  hang('secondaries', 'secondary_feather', [10.5, 12.5, 14.5, 16.5], [8, 9, 9, 8], z + 0.75)
  const P = [{ angle: -2, length: 12 }, { angle: -13, length: 14 }, { angle: -24, length: 14 }, { angle: -35, length: 13 },
    { angle: -46, length: 12 }, { angle: -58, length: 10 }, { angle: -70, length: 9 }]
  P.forEach((p, i) => {
    const k = i + 1
    const px = H[0] - 0.6 * i, py = y - 0.35 * i, pz = z + 0.9 + 0.1 * i
    groups.push({ name: 'primary_' + k, parent: 'primaries', origin: [px, py, z], rot: [0, 0, p.angle] })
    cubes.push({ name: 'shard_' + k, group: 'primary_' + k, from: [px, py - 1, pz - 0.5], to: [px + p.length, py + 1, pz + 0.5], kind: 'crystal', glow: k === 3 || k === 6 })
    cubes.push({ name: 'shard_' + k + '_tip', group: 'primary_' + k, from: [px + p.length, py - 0.5, pz - 0.5], to: [px + p.length + 3, py + 0.5, pz + 0.5], kind: 'tip' })
  })
  const rig = { model: 'amethyst', texture: [128, 64], S, E, W, H, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
