var RIG = (() => {
  const S = [2.5, 22.5, 2.4], E = [10.5, 22.5, 2.4], M = [18.5, 22.5, 2.4]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'hand', parent: 'forearm', origin: M, rot: [0, 0, -10] }
  ]
  const cubes = [
    { name: 'arm_web', group: 'arm', from: [S[0], 11.5, 2.4], to: [E[0], 24.5, 2.42], kind: 'web', asym: true },
    { name: 'forearm_web', group: 'forearm', from: [E[0], 12.5, 2.44], to: [M[0], 24.5, 2.46], kind: 'web', asym: true },
    { name: 'hand_web', group: 'hand', from: [M[0], 15, 2.48], to: [M[0] + 14, 24, 2.5], kind: 'web', asym: true },
    { name: 'arm_bone', group: 'arm', from: [S[0], 22.7, 1.8], to: [E[0], 24.3, 3.0], kind: 'bone' },
    { name: 'forearm_bone', group: 'forearm', from: [E[0], 22.9, 1.9], to: [M[0], 24.1, 2.9], kind: 'bone' },
    { name: 'hand_bone', group: 'hand', from: [M[0], 23.1, 2.05], to: [M[0] + 13, 23.9, 2.75], kind: 'bone' },
    { name: 'elbow', group: 'forearm', from: [E[0] - 0.8, 22.5, 1.6], to: [E[0] + 0.8, 24.5, 3.2], kind: 'knuckle' },
    { name: 'wrist', group: 'hand', from: [M[0] - 0.7, 22.6, 1.7], to: [M[0] + 0.7, 24.4, 3.1], kind: 'knuckle' }
  ]
  const rig = { model: 'phantom', texture: [128, 64], S, E, M, groups, cubes }
  rig.used = PACK(rig, 128)
  const copies = cubes.filter(c => c.asym).map(c => ({ ...c, uv: null, source: c }))
  const sub = { cubes: copies }
  PACK(sub, 128, rig.used)
  for (const c of copies) c.source.leftUv = c.uv
  rig.used = Math.max(...copies.map(c => c.uv[1] + (c.to[1] - c.from[1]) + 1))
  return rig
})()
