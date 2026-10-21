var BIRD = (o) => {
  const y = 22.5, z = 2.4, T = 0.02
  const S = [2.5, y, z]
  const E = [S[0] + o.arm, y, z]
  const W = [E[0] + o.forearm, y, z]
  const H = [W[0] + o.hand, y, z]
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
  const th = o.bone || 1
  const cubes = [
    { name: 'arm_bone', group: 'arm', from: [S[0], y - 1.1 * th, z - 0.75 * th], to: [E[0], y + 1.1 * th, z + 0.75 * th], kind: 'bone' },
    { name: 'forearm_bone', group: 'forearm', from: [E[0], y - 0.9 * th, z - 0.6 * th], to: [W[0], y + 0.9 * th, z + 0.6 * th], kind: 'bone' },
    { name: 'hand_bone', group: 'hand', from: [W[0], y - 0.75 * th, z - 0.5 * th], to: [H[0], y + 0.75 * th, z + 0.5 * th], kind: 'bone' },
    { name: 'tertials_plate', group: 'tertials', from: [S[0], y - o.tertials, z + 0.75], to: [E[0], y, z + 0.75], kind: 'tertials' },
    { name: 'secondaries_plate', group: 'secondaries', from: [E[0], y - o.secondaries, z + 0.9], to: [W[0], y, z + 0.9], kind: 'secondaries' },
    { name: 'hand_feathers_plate', group: 'hand_feathers', from: [W[0], y - o.handSheet, z + 1.0], to: [H[0], y, z + 1.0], kind: 'hand_sheet' },
    { name: 'hand_coverts_plate', group: 'hand_coverts', from: [W[0], y - o.handCoverts, z + 1.05], to: [H[0], y, z + 1.05], kind: 'hand_coverts' }
  ]
  if (o.coverts) {
    cubes.push({ name: 'greater_coverts', group: 'secondaries', from: [E[0], y - o.coverts, z + 0.96], to: [W[0], y, z + 0.96], kind: 'greater_coverts' })
    cubes.push({ name: 'arm_coverts', group: 'tertials', from: [S[0], y - Math.min(o.coverts, o.tertials - 2), z + 0.8], to: [E[0], y, z + 0.8], kind: 'arm_coverts' })
  }
  if (o.marginal) {
    cubes.push({ name: 'marginal_coverts', group: 'forearm', from: [E[0], y - o.marginal, z + 1.0], to: [W[0], y + 1, z + 1.0], kind: 'marginal' })
  }
  const P = o.primaries
  const step = o.primaryStep || [0.55, 0.3]
  const width = o.primaryWidth || 4
  for (let k = 1; k <= P.length; k++) {
    const p = P[k - 1]
    const px = H[0] - step[0] * (k - 1), py = y - step[1] * (k - 1)
    const pz = z + 1.15 + (o.primaryDz || 0.06) * (k - 1)
    groups.push({ name: 'primary_' + k, parent: 'primaries', origin: [px, py, z], rot: [0, 0, p.angle] })
    cubes.push({ name: 'feather_' + k, group: 'primary_' + k, from: [px, py - (p.width || width) / 2, pz], to: [px + p.length, py + (p.width || width) / 2, pz], kind: 'primary', index: k })
  }
  if (o.alula) {
    groups.push({ name: 'alula', parent: 'hand', origin: W, rot: [0, 0, o.alula.angle] })
    cubes.push({ name: 'alula_plate', group: 'alula', from: [W[0], y - 0.5, z + 1.2], to: [W[0] + o.alula.length, y + 1.5, z + 1.2], kind: 'alula' })
  }
  for (const c of cubes) if (c.kind !== 'bone') c.to[2] = c.from[2] + T
  return { S, E, W, H, groups, cubes }
}
