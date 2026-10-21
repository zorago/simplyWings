var RIG = (() => {
  const S = [2.5, 22.5, 2.4]
  const V = [2.0, 15.5, 2.4]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'pectoral', parent: 'shoulder', origin: S },
    { name: 'pelvic', origin: V }
  ]
  const cubes = [
    { name: 'fin_root', group: 'shoulder', from: [1.8, 20.8, 1.7], to: [4.2, 24.2, 3.3], kind: 'root' },
    { name: 'pelvic_root', group: 'pelvic', from: [1.4, 14.6, 1.9], to: [3, 16.4, 3], kind: 'root' }
  ]
  const pectoral = [
    { angle: 2, length: 19 }, { angle: -8, length: 19 }, { angle: -18, length: 18 }, { angle: -28, length: 17 },
    { angle: -38, length: 15 }, { angle: -49, length: 13 }, { angle: -60, length: 11 }
  ]
  const pelvic = [{ angle: -20, length: 10 }, { angle: -33, length: 10 }, { angle: -46, length: 9 }, { angle: -59, length: 8 }]
  pectoral.forEach((r, i) => {
    const k = i + 1
    groups.push({ name: 'ray_' + k, parent: 'pectoral', origin: S, rot: [0, 0, r.angle] })
    cubes.push({ name: 'ray_' + k + '_plate', group: 'ray_' + k, from: [S[0], S[1] - 2, S[2] + 0.4 + 0.04 * i], to: [S[0] + r.length, S[1] + 2, S[2] + 0.42 + 0.04 * i], kind: 'ray', index: k, count: pectoral.length })
  })
  pelvic.forEach((r, i) => {
    const k = i + 1
    groups.push({ name: 'pelvic_' + k, parent: 'pelvic', origin: V, rot: [0, 0, r.angle] })
    cubes.push({ name: 'pelvic_' + k + '_plate', group: 'pelvic_' + k, from: [V[0], V[1] - 1.5, V[2] + 0.3 + 0.04 * i], to: [V[0] + r.length, V[1] + 1.5, V[2] + 0.32 + 0.04 * i], kind: 'pelvic', index: k, count: pelvic.length })
  })
  const rig = { model: 'flying_fish', texture: [128, 64], S, V, pectoral, pelvic, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
