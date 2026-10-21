var RIG = (() => {
  const S = [2.5, 22.5, 2.6]
  const J = [12.5, 22.5, 2.6]
  const T = [0, 12, 3.4]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'spar', parent: 'shoulder', origin: S, rot: [0, 0, 6] },
    { name: 'sail_inner', parent: 'spar', origin: S },
    { name: 'outer', parent: 'spar', origin: J },
    { name: 'sail_outer', parent: 'outer', origin: J },
    { name: 'strut', parent: 'shoulder', origin: S, rot: [0, 0, -52] },
    { name: 'tail_1', origin: T, single: true }
  ]
  const cubes = [
    { name: 'spar_inner', group: 'spar', from: [S[0], S[1] - 0.35, S[2] - 0.35], to: [J[0], S[1] + 0.35, S[2] + 0.35], kind: 'stick' },
    { name: 'spar_outer', group: 'outer', from: [J[0], J[1] - 0.3, J[2] - 0.3], to: [J[0] + 9, J[1] + 0.3, J[2] + 0.3], kind: 'stick' },
    { name: 'strut_stick', group: 'strut', from: [S[0], S[1] - 0.3, S[2] - 0.3], to: [S[0] + 17, S[1] + 0.3, S[2] + 0.3], kind: 'stick' },
    { name: 'binding', group: 'shoulder', from: [S[0] - 0.7, S[1] - 0.7, S[2] - 0.7], to: [S[0] + 0.7, S[1] + 0.7, S[2] + 0.7], kind: 'string' },
    { name: 'sail_inner_plate', group: 'sail_inner', from: [S[0], 6, S[2] + 0.1], to: [J[0], 23, S[2] + 0.12], kind: 'sail' },
    { name: 'sail_outer_plate', group: 'sail_outer', from: [J[0], 11, S[2] + 0.14], to: [J[0] + 9, 23, S[2] + 0.16], kind: 'sail' }
  ]
  let parent = 'tail_1', top = T[1]
  for (let k = 1; k <= 5; k++) {
    const g = 'tail_' + k
    if (k > 1) groups.push({ name: g, parent, origin: [T[0], top, T[2]], single: true })
    cubes.push({ name: g + '_ribbon', group: g, from: [T[0] - 0.75, top - 3.5, T[2]], to: [T[0] + 0.75, top, T[2] + 0.02], kind: 'ribbon', index: k })
    cubes.push({ name: g + '_bow', group: g, from: [T[0] - 1.5, top - 3.7, T[2] + 0.04], to: [T[0] + 1.5, top - 2.3, T[2] + 0.06], kind: 'bow', index: k })
    parent = g
    top -= 3.5
  }
  const rig = { model: 'kite', texture: [64, 64], S, J, T, groups, cubes }
  rig.used = PACK(rig, 64)
  return rig
})()
