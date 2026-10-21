var RIG = (() => {
  const S = [2.5, 22.5, 2.6]
  const P = [1.0, 23.5, 3.0]
  const shape = [[0, 0.8], [6, 0.5], [12, 1.2], [16.5, 2.6], [17.2, 4.5], [15, 6.6], [10, 7.6], [5, 7.2], [1.5, 5.5], [0, 3]]
  const groups = [
    { name: 'shell', origin: P },
    { name: 'wing', origin: S },
    { name: 'wing_tip', parent: 'wing', origin: [S[0] + 8, S[1], S[2]] },
    { name: 'ghost_a', origin: S },
    { name: 'ghost_b', origin: S }
  ]
  const cubes = [
    { name: 'shell_body', group: 'shell', from: [0.3, 10.5, 2.4], to: [5.3, 23.5, 3.9], kind: 'shell' },
    { name: 'shell_round', group: 'shell', from: [0.8, 9.5, 2.5], to: [4.8, 10.5, 3.8], kind: 'shell' },
    { name: 'shell_apex', group: 'shell', from: [1.5, 8.7, 2.6], to: [4, 9.5, 3.6], kind: 'shell' },
    { name: 'shell_humerus', group: 'shell', from: [4.6, 19.5, 2.5], to: [5.8, 23.5, 3.75], kind: 'shell_dark' },
    { name: 'shell_suture', group: 'shell', from: [0.3, 10.5, 3.9], to: [1.1, 23.5, 4.15], kind: 'suture' },
    { name: 'wing_base', group: 'wing', from: [S[0], S[1] - 7.5, S[2] + 0.1], to: [S[0] + 8, S[1] + 0.5, S[2] + 0.12], kind: 'membrane', shape, offset: 0 },
    { name: 'wing_outer', group: 'wing_tip', from: [S[0] + 8, S[1] - 7.5, S[2] + 0.14], to: [S[0] + 18, S[1] + 0.5, S[2] + 0.16], kind: 'membrane', shape, offset: 8 },
    { name: 'wing_root', group: 'wing', from: [S[0] - 0.4, S[1] - 0.6, S[2] - 0.5], to: [S[0] + 1.2, S[1] + 0.6, S[2] + 0.6], kind: 'root' },
    { name: 'ghost_a_plate', group: 'ghost_a', from: [S[0], S[1] - 7.5, S[2] + 0.2], to: [S[0] + 18, S[1] + 0.5, S[2] + 0.22], kind: 'ghost', shape, offset: 0 },
    { name: 'ghost_b_plate', group: 'ghost_b', from: [S[0], S[1] - 7.5, S[2] + 0.2], to: [S[0] + 18, S[1] + 0.5, S[2] + 0.22], kind: 'ghost', shape, offset: 0 }
  ]
  const rig = { model: 'beetle', texture: [128, 64], S, P, groups, cubes }
  rig.used = PACK(rig, 128)
  const ghosts = cubes.filter(c => c.kind === 'ghost')
  ghosts[1].uv = ghosts[0].uv.slice()
  return rig
})()
