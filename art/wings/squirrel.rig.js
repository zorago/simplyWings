var RIG = (() => {
  const S = [2.5, 22.5, 2.4], E = [8.5, 22.5, 2.4], W = [14.5, 22.5, 2.4]
  const T = [0, 11.5, 2.6]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'arm', parent: 'shoulder', origin: S },
    { name: 'inner_membrane', parent: 'arm', origin: S },
    { name: 'forearm', parent: 'arm', origin: E },
    { name: 'outer_membrane', parent: 'forearm', origin: E },
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
    { name: 'inner_plate', group: 'inner_membrane', from: [S[0], 8, 2.35], to: [E[0], 24, 2.37], kind: 'membrane' },
    { name: 'outer_plate', group: 'outer_membrane', from: [E[0], 10, 2.4], to: [E[0] + 9, 24, 2.42], kind: 'membrane' },
    { name: 'tail_plate', group: 'tail', from: [T[0] - 3, T[1] - 7, T[2] + 0.6], to: [T[0] + 3, T[1], T[2] + 0.62], kind: 'tail' },
    { name: 'tail_tip_plate', group: 'tail_tip', from: [T[0] - 3.5, T[1] - 15, T[2] + 0.64], to: [T[0] + 3.5, T[1] - 7, T[2] + 0.66], kind: 'tail_tip' }
  ]
  const rig = { model: 'squirrel', texture: [64, 64], S, E, W, T, panel, edge, groups, cubes }
  rig.used = PACK(rig, 64)
  return rig
})()
