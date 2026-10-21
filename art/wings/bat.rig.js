var RIG = (() => {
  const S = [2.5, 22.5, 2.4], E = [9.5, 22.5, 2.4], W = [20.5, 22.5, 2.4]
  const fingers = [
    { k: 1, angle: 4, p: 10, d: 9, droop: -7, z: 2.55 },
    { k: 2, angle: -26, p: 9, d: 8, droop: -6, z: 2.5 },
    { k: 3, angle: -57, p: 8, d: 7, droop: -5, z: 2.45 },
    { k: 4, angle: -95, p: 7, d: 6, droop: 4 }
  ]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'arm_membrane', parent: 'arm', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'forearm_membrane', parent: 'forearm', origin: E },
    { name: 'hand', parent: 'forearm', origin: W },
    { name: 'thumb', parent: 'hand', origin: W, rot: [0, 0, 40] }
  ]
  const cubes = [
    { name: 'arm_web', group: 'arm_membrane', from: [2.5, 7.5, 2.35], to: [9.5, 22.5, 2.35] },
    { name: 'forearm_web', group: 'forearm_membrane', from: [9.5, 7.5, 2.4], to: [20.5, 22.5, 2.4] },
    { name: 'arm_front', group: 'arm_membrane', from: [2.5, 22.5, 2.35], to: [9.5, 25.5, 2.35] },
    { name: 'forearm_front', group: 'forearm_membrane', from: [9.5, 22.5, 2.4], to: [20.5, 24.5, 2.4] }
  ]
  const rad = Math.PI / 180
  for (let i = 0; i < fingers.length; i++) {
    const f = fingers[i], n = fingers[i + 1]
    const g = 'finger_' + f.k, t = g + '_tip'
    const j = [W[0] + f.p, W[1], W[2]]
    groups.push({ name: g, parent: 'hand', origin: W, rot: [0, 0, f.angle] })
    groups.push({ name: t, parent: g, origin: j, rot: [0, 0, f.droop] })
    if (n) {
      const delta = (f.angle - n.angle) * rad
      const reach = (n.p + n.d) * Math.sin(delta)
      const h1 = Math.ceil(Math.min(f.p * Math.tan(Math.min(delta, 1.3)), reach) + 1)
      const h2 = Math.ceil(reach + 2)
      groups.push({ name: g + '_web', parent: g, origin: W })
      groups.push({ name: t + '_web', parent: t, origin: j })
      cubes.push({ name: g + '_membrane', group: g + '_web', from: [W[0], W[1] - h1, f.z], to: [j[0], W[1], f.z] })
      cubes.push({ name: t + '_membrane', group: t + '_web', from: [j[0], W[1] - h2, f.z], to: [j[0] + f.d, W[1], f.z] })
    }
  }
  cubes.push(
    { name: 'shoulder_fur', group: 'shoulder', from: [2, 21, 1.4], to: [5, 24, 3.4] },
    { name: 'arm_bone', group: 'arm', from: [2.5, 21.5, 1.9], to: [9.5, 23.5, 2.9] },
    { name: 'forearm_bone', group: 'forearm', from: [9.5, 22, 1.9], to: [20.5, 23, 2.9] },
    { name: 'elbow', group: 'forearm', from: [8.75, 21.75, 1.65], to: [10.25, 23.25, 3.15] },
    { name: 'wrist', group: 'hand', from: [19.75, 21.75, 1.65], to: [21.25, 23.25, 3.15] },
    { name: 'thumb_bone', group: 'thumb', from: [20.5, 22.1, 2.0], to: [23.5, 22.9, 2.8] },
    { name: 'thumb_claw', group: 'thumb', from: [23.5, 22.2, 2.1], to: [24.5, 23.4, 2.7] })
  for (const f of fingers) {
    const g = 'finger_' + f.k, t = g + '_tip', jx = W[0] + f.p
    cubes.push({ name: g + '_bone', group: g, from: [W[0], 22, 1.9], to: [jx, 23, 2.9] })
    cubes.push({ name: t + '_bone', group: t, from: [jx, 22.15, 2.05], to: [jx + f.d, 22.85, 2.75] })
  }
  for (const c of cubes) if (c.from[2] === c.to[2]) c.to[2] = c.from[2] + 0.02
  const rig = { model: 'bat', texture: [128, 64], S, E, W, fingers, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
