var RIG = (() => {
  const S = [2.5, 22.5, 2.4], E = [11, 22.5, 2.4], W = [23.5, 22.5, 2.4]
  const fingers = [
    { k: 1, angle: 8, p: 12, d: 11, droop: -8, z: 2.6 },
    { k: 2, angle: -22, p: 11, d: 10, droop: -7, z: 2.54 },
    { k: 3, angle: -52, p: 10, d: 9, droop: -6, z: 2.48 },
    { k: 4, angle: -86, p: 8, d: 7, droop: 5 }
  ]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'arm_membrane', parent: 'arm', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'forearm_membrane', parent: 'forearm', origin: E },
    { name: 'hand', parent: 'forearm', origin: W },
    { name: 'thumb', parent: 'hand', origin: W, rot: [0, 0, 48] }
  ]
  const cubes = [
    { name: 'arm_web', group: 'arm_membrane', from: [2.5, 6.5, 2.35], to: [11, 22.5, 2.37], kind: 'web' },
    { name: 'forearm_web', group: 'forearm_membrane', from: [11, 6.5, 2.42], to: [23.5, 22.5, 2.44], kind: 'web' },
    { name: 'arm_front', group: 'arm_membrane', from: [2.5, 22.5, 2.35], to: [11, 25.5, 2.37], kind: 'web' },
    { name: 'forearm_front', group: 'forearm_membrane', from: [11, 22.5, 2.42], to: [23.5, 24.5, 2.44], kind: 'web' }
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
      cubes.push({ name: g + '_membrane', group: g + '_web', from: [W[0], W[1] - h1, f.z], to: [j[0], W[1], f.z + 0.02], kind: 'web' })
      cubes.push({ name: t + '_membrane', group: t + '_web', from: [j[0], W[1] - h2, f.z], to: [j[0] + f.d, W[1], f.z + 0.02], kind: 'web' })
    }
  }
  cubes.push(
    { name: 'shoulder_plate', group: 'shoulder', from: [1.2, 20.5, 1.2], to: [5.2, 24.5, 3.8], kind: 'carapace' },
    { name: 'arm_bone', group: 'arm', from: [2.5, 21.2, 1.6], to: [11, 23.8, 3.2], kind: 'bone' },
    { name: 'forearm_bone', group: 'forearm', from: [11, 21.6, 1.7], to: [23.5, 23.4, 3.1], kind: 'bone' },
    { name: 'elbow', group: 'forearm', from: [9.8, 21, 1.3], to: [12.2, 24, 3.5], kind: 'knuckle' },
    { name: 'elbow_spike', group: 'forearm', from: [8.5, 21.8, 1.9], to: [11, 22.8, 2.9], origin: [11, 22.3, 2.4], rot: [0, 0, -150], kind: 'horn' },
    { name: 'wrist', group: 'hand', from: [22.3, 21.3, 1.4], to: [24.7, 23.7, 3.4], kind: 'knuckle' },
    { name: 'thumb_bone', group: 'thumb', from: [23.5, 21.9, 1.8], to: [27.5, 23.1, 3], kind: 'bone' },
    { name: 'thumb_claw', group: 'thumb', from: [27.5, 22, 1.9], to: [30, 23.4, 2.9], origin: [27.5, 22.7, 2.4], rot: [0, 0, -25], kind: 'horn' }
  )
  const spikes = [{ x: 4.5, h: 2.2 }, { x: 7.5, h: 2.8 }, { x: 14, h: 2.4 }, { x: 17.5, h: 3 }, { x: 21, h: 2.2 }]
  spikes.forEach((s, i) => {
    const grp = s.x < 11 ? 'arm' : 'forearm'
    const top = s.x < 11 ? 23.8 : 23.4
    cubes.push({ name: 'spike_' + (i + 1), group: grp, from: [s.x - 0.5, top - 0.3, 2], to: [s.x + 0.5, top + s.h, 2.8], origin: [s.x, top, 2.4], rot: [0, 0, -28], kind: 'horn' })
  })
  for (const f of fingers) {
    const g = 'finger_' + f.k, t = g + '_tip', jx = W[0] + f.p
    cubes.push({ name: g + '_bone', group: g, from: [W[0], 21.85, 1.8], to: [jx, 23.15, 3], kind: 'bone' })
    cubes.push({ name: g + '_joint', group: t, from: [jx - 0.7, 21.8, 1.7], to: [jx + 0.7, 23.2, 3.1], kind: 'knuckle' })
    cubes.push({ name: t + '_bone', group: t, from: [jx, 22.05, 1.95], to: [jx + f.d, 22.95, 2.85], kind: 'bone' })
    cubes.push({ name: t + '_claw', group: t, from: [jx + f.d, 22.1, 2.05], to: [jx + f.d + 2, 22.9, 2.75], origin: [jx + f.d, 22.5, 2.4], rot: [0, 0, -18], kind: 'horn' })
  }
  const rig = { model: 'demon', texture: [128, 128], S, E, W, fingers, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
