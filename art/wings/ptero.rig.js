var RIG = (() => {
  const y = 22.5, z = 2.4
  const S = [2.5, y, z], E = [7, y, z], W = [15, y, z], K = [18.5, y, z]
  const phal = [{ len: 8, rot: -6 }, { len: 7.5, rot: -4 }, { len: 6.5, rot: -4 }, { len: 5.5, rot: -5 }]
  const hip = [3.2, 8.5]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'arm_membrane', parent: 'arm', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'forearm_membrane', parent: 'forearm', origin: E },
    { name: 'propatagium', parent: 'forearm', origin: E },
    { name: 'hand', parent: 'forearm', origin: W },
    { name: 'hand_membrane', parent: 'hand', origin: W },
    { name: 'claws', parent: 'hand', origin: K, rot: [0, 0, 55] }
  ]
  const cubes = []
  const rad = Math.PI / 180
  const joints = [K.slice()]
  let parent = 'hand', at = K.slice(), angle = 0
  for (let i = 0; i < phal.length; i++) {
    const p = phal[i], g = 'finger_' + (i + 1)
    groups.push({ name: g, parent, origin: at.slice(), rot: [0, 0, p.rot] })
    groups.push({ name: g + '_membrane', parent: g, origin: at.slice() })
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
  const edgeY = (x) => {
    let best = null
    for (let i = 0; i + 1 < edge.length; i++) {
      const [a, b] = [edge[i], edge[i + 1]]
      const lo = Math.min(a[0], b[0]), hi = Math.max(a[0], b[0])
      if (x >= lo && x <= hi) {
        const t = (x - a[0]) / ((b[0] - a[0]) || 1)
        const yy = a[1] + t * (b[1] - a[1])
        best = best === null ? yy : Math.min(best, yy)
      }
    }
    return best === null ? y : best
  }
  const depth = (x0, x1, top) => {
    let d = 0
    for (let x = x0; x <= x1; x += 0.5) d = Math.max(d, top - edgeY(x))
    return Math.max(1, Math.ceil(d + 1.2))
  }
  const plate = (name, group, x0, x1, topW, zz) => {
    const h = depth(topW[0], topW[0] + (x1 - x0) + 2, topW[1])
    cubes.push({ name, group, from: [x0, y - h, zz], to: [x1, y, zz + 0.02], kind: 'membrane' })
  }
  plate('arm_web', 'arm_membrane', S[0], E[0], [S[0], y], z - 0.08)
  plate('forearm_web', 'forearm_membrane', E[0], W[0], [E[0], y], z + 0.08)
  plate('hand_web', 'hand_membrane', W[0], K[0], [W[0], y], z - 0.08)
  for (let i = 0; i < phal.length; i++) {
    const g = 'finger_' + (i + 1), j = joints[i]
    const top = world(g, [j[0], y])
    plate(g + '_web', g + '_membrane', j[0], j[0] + phal[i].len, top, z + (i % 2 ? -0.08 : 0.08))
  }
  cubes.push({ name: 'propatagium_web', group: 'propatagium', from: [E[0] - 4.5, y, z + 0.02], to: [W[0] + 1, y + 2.5, z + 0.04], kind: 'propatagium' })
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
  return rig
})()
