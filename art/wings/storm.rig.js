var RIG = (() => {
  const y = 22.5, z = 2.4
  const S = [2.5, y, z], E = [9.5, y, z], W = [18.5, y, z], H = [22.5, y, z]
  const rad = Math.PI / 180
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
  const cubes = []
  const zig = (group, start, total, angles, zz, thick, kind) => {
    let p = start.slice()
    const n = angles.length
    angles.forEach((a, i) => {
      const L = total / n + 0.6
      cubes.push({ name: group + '_bolt_' + (i + 1), group, from: [p[0], p[1] - thick / 2, zz], to: [p[0] + L, p[1] + thick / 2, zz + thick], origin: [p[0], p[1], zz], rot: [0, 0, a], kind })
      p = [p[0] + (L - 0.6) * Math.cos(a * rad), p[1] + (L - 0.6) * Math.sin(a * rad)]
    })
    return p
  }
  zig('arm', [S[0], y], E[0] - S[0], [10, -14], z - 0.5, 1.2, 'bone')
  zig('forearm', [E[0], y], W[0] - E[0], [-12, 14, -8], z - 0.45, 1.1, 'bone')
  zig('hand', [W[0], y], H[0] - W[0], [10], z - 0.4, 1, 'bone')
  cubes.push({ name: 'core', group: 'shoulder', from: [1.4, 21.3, 1.4], to: [3.8, 23.7, 3.8], kind: 'core' })
  cubes.push({ name: 'arm_veil', group: 'tertials', from: [S[0], y - 12, z + 0.7], to: [E[0], y + 0.5, z + 0.72], kind: 'veil', part: 'arm' })
  cubes.push({ name: 'forearm_veil', group: 'secondaries', from: [E[0], y - 13, z + 0.85], to: [W[0], y + 0.5, z + 0.87], kind: 'veil', part: 'forearm' })
  cubes.push({ name: 'hand_veil', group: 'hand_feathers', from: [W[0], y - 17, z + 1.0], to: [W[0] + 18, y + 0.5, z + 1.02], kind: 'veil', part: 'hand' })
  const prim = [{ angle: 6, len: 15 }, { angle: -14, len: 17 }, { angle: -34, len: 17 }, { angle: -54, len: 15 }, { angle: -74, len: 13 }]
  prim.forEach((p, i) => {
    const k = i + 1, px = H[0] - 0.8 * i, py = y - 0.4 * i
    groups.push({ name: 'primary_' + k, parent: 'primaries', origin: [px, py, z], rot: [0, 0, p.angle] })
    const turns = i % 2 ? [14, -16, 12] : [-12, 15, -10]
    zig('primary_' + k, [px, py], p.len, turns, z + 1.15 + 0.07 * i, 0.7 - 0.05 * i, 'bolt')
  })
  const arcs = [
    { parent: 'forearm', at: [12, 17, z + 1.4], rot: 62, len: 5 }, { parent: 'forearm', at: [16, 14, z + 1.4], rot: -48, len: 4 },
    { parent: 'arm', at: [5, 15, z + 1.3], rot: 75, len: 4.5 }, { parent: 'hand', at: [26, 15, z + 1.6], rot: 30, len: 5 },
    { parent: 'hand', at: [30, 9, z + 1.7], rot: -60, len: 4 }, { parent: 'hand', at: [22, 10, z + 1.6], rot: 100, len: 4.5 }
  ]
  arcs.forEach((a, i) => {
    groups.push({ name: 'arc_' + (i + 1), parent: a.parent, origin: a.at, rot: [0, 0, a.rot] })
    cubes.push({ name: 'arc_' + (i + 1) + '_a', group: 'arc_' + (i + 1), from: [a.at[0], a.at[1] - 0.3, a.at[2]], to: [a.at[0] + a.len / 2 + 0.3, a.at[1] + 0.3, a.at[2] + 0.4], origin: a.at, rot: [0, 0, 25], kind: 'arc' })
    cubes.push({ name: 'arc_' + (i + 1) + '_b', group: 'arc_' + (i + 1), from: [a.at[0] + a.len / 2, a.at[1] - 0.3, a.at[2]], to: [a.at[0] + a.len, a.at[1] + 0.3, a.at[2] + 0.4], origin: [a.at[0] + a.len / 2, a.at[1], a.at[2]], rot: [0, 0, -30], kind: 'arc' })
  })
  const rig = { model: 'storm', texture: [128, 128], S, E, W, H, prim, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
