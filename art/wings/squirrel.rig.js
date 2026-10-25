var RIG = (() => {
  const S = [2.5, 22.5, 2.4], E = [8.5, 22.5, 2.4], W = [14.5, 22.5, 2.4]
  const T = [0, 11.5, 2.6]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'spur', parent: 'forearm', origin: W, rot: [0, 0, -20] },
    { name: 'tail', origin: T, single: true },
    { name: 'tail_tip', parent: 'tail', origin: [T[0], T[1] - 7, T[2]], single: true }
  ]
  const edge = [[14.6, 22.6], [16.6, 20.6], [15.4, 17.5], [13, 14.6], [10, 12], [7, 10], [4.5, 8.6], [2.5, 8.2]]
  const panel = [[2.5, 23.6], [8.5, 23.4], [14.5, 23.1], ...edge]
  const cubes = [
    { name: 'arm_bone', group: 'arm', from: [S[0], 21.5, 1.9], to: [E[0], 23.5, 2.9], kind: 'fur' },
    { name: 'forearm_bone', group: 'forearm', from: [E[0], 21.75, 2], to: [W[0], 23.25, 2.8], kind: 'fur' },
    { name: 'spur_bone', group: 'spur', from: [W[0], 22, 2.1], to: [W[0] + 3, 23, 2.7], kind: 'spur' },
    { name: 'paw', group: 'forearm', from: [W[0] - 0.5, 21.5, 1.7], to: [W[0] + 1, 23.5, 3.1], kind: 'paw' },
    { name: 'tail_plate', group: 'tail', from: [T[0] - 3, T[1] - 7, T[2] + 0.6], to: [T[0] + 3, T[1], T[2] + 0.62], kind: 'tail' },
    { name: 'tail_tip_plate', group: 'tail_tip', from: [T[0] - 3.5, T[1] - 15, T[2] + 0.64], to: [T[0] + 3.5, T[1] - 7, T[2] + 0.66], kind: 'tail_tip' }
  ]
  const rig = { model: 'squirrel', texture: [64, 64], S, E, W, T, panel, edge, groups, cubes }
  rig.used = PACK(rig, 64)
  const z = 2.4
  const at = (bone, px, py) => {
    const w = GEOM.world(rig, bone, [px, py])
    return [bone, Math.round(w[0] * 1000) / 1000, Math.round(w[1] * 1000) / 1000, z]
  }
  const flank = [at('arm', S[0], 23.4), ['root', 2.6, 19.5, z - 0.1], ['root', 2.7, 15.8, z - 0.1], ['root', 2.6, 12, z - 0.1], ['root', 2.5, 8.2, z - 0.1]]
  const rail = [at('arm', S[0], 23.4), at('arm', (S[0] + E[0]) / 2, 23.4), at('forearm', E[0], 23.3), at('forearm', W[0], 23.1), at('spur', W[0] + 3, 22.5)]
  const pts = [...flank, ...rail]
  const x0 = Math.floor(Math.min(...pts.map(q => q[1])) - 1), x1 = Math.ceil(Math.max(...pts.map(q => q[1])) + 1)
  const y0 = Math.ceil(Math.max(...pts.map(q => q[2])) + 1), y1 = Math.floor(Math.min(...pts.map(q => q[2])) - 1)
  rig.sheet = { x: x0, y: y0, w: x1 - x0, h: y0 - y1, ventral: [0, rig.used], dorsal: [x1 - x0, rig.used] }
  rig.membranes = { sheet: { x: x0, y: y0, ventral: rig.sheet.ventral, dorsal: rig.sheet.dorsal }, panels: [{ name: 'patagium', rows: 3, a: flank, b: rail }] }
  return rig
})()
