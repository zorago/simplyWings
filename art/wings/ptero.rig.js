var RIG = (() => {
  const y = 22.5, z = 2.4
  const S = [2.5, y, z], E = [7, y, z], W = [15, y, z], K = [18.5, y, z]
  const phal = [{ len: 8, rot: -6 }, { len: 7.5, rot: -4 }, { len: 6.5, rot: -4 }, { len: 5.5, rot: -5 }]
  const hip = [3.2, 8.5]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'propatagium', parent: 'forearm', origin: E },
    { name: 'hand', parent: 'forearm', origin: W },
    { name: 'claws', parent: 'hand', origin: K, rot: [0, 0, 55] }
  ]
  const cubes = []
  const rad = Math.PI / 180
  const joints = [K.slice()]
  let parent = 'hand', at = K.slice(), angle = 0
  for (let i = 0; i < phal.length; i++) {
    const p = phal[i], g = 'finger_' + (i + 1)
    groups.push({ name: g, parent, origin: at.slice(), rot: [0, 0, p.rot] })
    angle += p.rot
    const t = 0.55 - 0.08 * i
    cubes.push({ name: g + '_bone', group: g, from: [at[0], y - t, z - t], to: [at[0] + p.len, y + t, z + t], kind: 'finger', index: i })
    const next = [at[0] + p.len, at[1], at[2]]
    joints.push(next)
    parent = g
    at = next
  }
  const world = (gname, q) => GEOM.world({ groups }, gname, q)
  const tipW = world('finger_4', [at[0], y])
  const edge = [tipW, ...GEOM.sag(tipW, hip, 2.2, [12, 24], 14), hip]
  cubes.push(
    { name: 'shoulder_fuzz', group: 'shoulder', from: [1.4, 20.6, 1.2], to: [5, 24.4, 3.6], kind: 'fuzz' },
    { name: 'arm_bone', group: 'arm', from: [S[0], y - 1.05, z - 0.8], to: [E[0], y + 1.05, z + 0.8], kind: 'bone' },
    { name: 'forearm_bone', group: 'forearm', from: [E[0], y - 0.8, z - 0.7], to: [W[0], y + 0.8, z + 0.7], kind: 'bone' },
    { name: 'elbow', group: 'forearm', from: [E[0] - 0.9, y - 1.1, z - 0.9], to: [E[0] + 0.9, y + 1.1, z + 0.9], kind: 'joint' },
    { name: 'hand_bone', group: 'hand', from: [W[0], y - 0.7, z - 0.6], to: [K[0], y + 0.7, z + 0.6], kind: 'bone' },
    { name: 'wrist', group: 'hand', from: [W[0] - 0.8, y - 1, z - 0.8], to: [W[0] + 0.8, y + 1, z + 0.8], kind: 'joint' },
    { name: 'knuckle', group: 'finger_1', from: [K[0] - 0.9, y - 1, z - 0.9], to: [K[0] + 0.9, y + 1, z + 0.9], kind: 'knuckle' },
    { name: 'pteroid', group: 'propatagium', from: [E[0] - 4.5, y + 0.1, z - 0.3], to: [E[0] + 0.5, y + 0.7, z + 0.3], kind: 'bone' },
    { name: 'claw_1', group: 'claws', from: [K[0], y - 0.3, z - 0.9], to: [K[0] + 2.2, y + 0.3, z - 0.3], kind: 'claw' },
    { name: 'claw_2', group: 'claws', from: [K[0], y - 0.3, z - 0.3], to: [K[0] + 2.6, y + 0.3, z + 0.3], kind: 'claw' },
    { name: 'claw_3', group: 'claws', from: [K[0], y - 0.3, z + 0.3], to: [K[0] + 2, y + 0.3, z + 0.9], kind: 'claw' },
    { name: 'tip_claw', group: 'finger_4', from: [at[0] - 0.4, y - 0.25, z - 0.25], to: [at[0] + 1.6, y + 0.25, z + 0.25], kind: 'claw' }
  )
  const rig = { model: 'ptero', texture: [128, 64], S, E, W, K, hip, phal, joints, edge, groups, cubes }
  rig.used = PACK(rig, 128)
  const r3 = (v) => Math.round(v * 1000) / 1000
  const pin = (g, w) => [g, r3(w[0]), r3(w[1]), z]
  const atB = (g, x, yy) => pin(g, GEOM.world(rig, g, [x, yy]))
  const chain = [atB('hand', W[0], y), atB('finger_1', K[0], y)]
  for (let i = 1; i < phal.length; i++) chain.push(atB('finger_' + (i + 1), joints[i][0], y))
  chain.push(atB('finger_4', joints[phal.length][0], y))
  const back = edge.slice().reverse()
  const lens = [0]
  for (let i = 1; i < back.length; i++) lens.push(lens[i - 1] + Math.hypot(back[i][0] - back[i - 1][0], back[i][1] - back[i - 1][1]))
  const along = (t) => {
    const L = t * lens[lens.length - 1]
    let i = 1
    while (i < lens.length - 1 && lens[i] < L) i++
    const u = (L - lens[i - 1]) / ((lens[i] - lens[i - 1]) || 1)
    return [back[i - 1][0] + u * (back[i][0] - back[i - 1][0]), back[i - 1][1] + u * (back[i][1] - back[i - 1][1])]
  }
  const n = chain.length - 1
  const marks = [[0, 'root'], [0.1, 'root'], [0.6, 'finger_3'], [0.75, 'finger_4'], [0.88, 'finger_4']]
  const trailing = [...marks.map(([t, g]) => g === 'root' ? ['root', ...along(t).map(r3), z - 0.1] : pin(g, along(t))), chain[n]]
  const flank = [atB('arm', S[0], y), ...[1, 2, 3].map(k => ['root', r3(S[0] + 0.25 * k), r3(y - (y - hip[1]) * k / 4), z - 0.1]), ['root', r3(hip[0]), r3(hip[1]), z - 0.1]]
  const mid1 = (S[0] + E[0]) / 2, mid2 = (E[0] + W[0]) / 2
  const arm = [atB('arm', S[0], y), atB('arm', mid1, y), atB('forearm', E[0], y), atB('forearm', mid2, y), atB('hand', W[0], y)]
  const panels = [
    { name: 'brachiopatagium', rows: 3, a: [...flank, ...trailing.slice(1)], b: [...arm, ...chain.slice(1)] },
    {
      name: 'propatagium', rows: 1, a: arm,
      b: [atB('propatagium', S[0], y + 0.7), atB('propatagium', mid1, y + 2), atB('propatagium', E[0], y + 2.2), atB('propatagium', mid2, y + 1.6), atB('hand', W[0], y)]
    }
  ]
  const pts = panels.flatMap(q => [...q.a, ...q.b])
  const x0 = Math.floor(Math.min(...pts.map(q => q[1])) - 1), x1 = Math.ceil(Math.max(...pts.map(q => q[1])) + 1)
  const y0 = Math.ceil(Math.max(...pts.map(q => q[2])) + 1), y1 = Math.floor(Math.min(...pts.map(q => q[2])) - 1)
  rig.sheet = { x: x0, y: y0, w: x1 - x0, h: y0 - y1, ventral: [0, rig.used], dorsal: [x1 - x0, rig.used] }
  rig.membranes = { sheet: { x: x0, y: y0, ventral: rig.sheet.ventral, dorsal: rig.sheet.dorsal }, panels }
  return rig
})()
