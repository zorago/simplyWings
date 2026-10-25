var RIG = (() => {
  const S = [2.5, 22.5, 2.4], E = [10.5, 22.5, 2.4], M = [18.5, 22.5, 2.4]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'hand', parent: 'forearm', origin: M, rot: [0, 0, -10] }
  ]
  const cubes = [
    { name: 'arm_bone', group: 'arm', from: [S[0], 22.7, 1.8], to: [E[0], 24.3, 3.0], kind: 'bone' },
    { name: 'forearm_bone', group: 'forearm', from: [E[0], 22.9, 1.9], to: [M[0], 24.1, 2.9], kind: 'bone' },
    { name: 'hand_bone', group: 'hand', from: [M[0], 23.1, 2.05], to: [M[0] + 13, 23.9, 2.75], kind: 'bone' },
    { name: 'elbow', group: 'forearm', from: [E[0] - 0.8, 22.5, 1.6], to: [E[0] + 0.8, 24.5, 3.2], kind: 'knuckle' },
    { name: 'wrist', group: 'hand', from: [M[0] - 0.7, 22.6, 1.7], to: [M[0] + 0.7, 24.4, 3.1], kind: 'knuckle' }
  ]
  const rig = { model: 'phantom', texture: [128, 64], S, E, M, groups, cubes }
  rig.used = PACK(rig, 128)
  const r3 = (v) => Math.round(v * 1000) / 1000
  const z = 2.45
  const tip = GEOM.world(rig, 'hand', [M[0] + 14, 23.5])
  const trail = [[tip[0] + 0.3, tip[1] - 0.6], [28, 19.2], [24.5, 17.4], [21, 16], [18, 14.8], [14.5, 13.9], [11, 13.2], [7.5, 12.6], [4.5, 12.3], [S[0], 12.6]]
  const yAt = (x) => {
    for (let i = 0; i + 1 < trail.length; i++) {
      const [x0, y0] = trail[i], [x1, y1] = trail[i + 1]
      if ((x <= x0 && x >= x1) || (x >= x0 && x <= x1)) return y0 + (y1 - y0) * (x - x0) / ((x1 - x0) || 1)
    }
    return trail[trail.length - 1][1]
  }
  const lead = [['arm', S[0], 24.5], ['arm', 6.5, 24.5], ['forearm', E[0], 24.5], ['forearm', 14.5, 24.4],
    ['hand', ...GEOM.world(rig, 'hand', [M[0], 24.3])], ['hand', ...GEOM.world(rig, 'hand', [M[0] + 7, 24.2])], ['hand', tip[0] + 0.3, tip[1] + 0.9]]
  const bones = ['arm', 'arm', 'forearm', 'forearm', 'hand', 'hand', 'hand']
  const low = lead.map((p, i) => i === lead.length - 1 ? [bones[i], tip[0] + 0.3, tip[1] - 1.3] : [bones[i], p[1], yAt(p[1]) - 0.8])
  const a = lead.map(p => [p[0], r3(p[1]), r3(p[2]), z])
  const b = low.map(p => [p[0], r3(p[1]), r3(p[2]), z])
  rig.lead = lead.map(p => [p[1], p[2]])
  rig.trail = trail
  rig.tip = tip
  const pts = [...a, ...b]
  const x0 = Math.floor(Math.min(...pts.map(q => q[1])) - 1), x1 = Math.ceil(Math.max(...pts.map(q => q[1])) + 1)
  const y0 = Math.ceil(Math.max(...pts.map(q => q[2])) + 1), y1 = Math.floor(Math.min(...pts.map(q => q[2])) - 1)
  rig.sheet = { x: x0, y: y0, w: x1 - x0, h: y0 - y1, ventral: [0, rig.used], dorsal: [x1 - x0, rig.used] }
  rig.membranes = { sheet: { x: x0, y: y0, ventral: rig.sheet.ventral, dorsal: rig.sheet.dorsal }, panels: [{ name: 'web', rows: 3, a, b }] }
  return rig
})()
