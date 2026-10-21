var RIG = (() => {
  const S = [2.5, 22.5, 2.4]
  const H = [2.2, 20.8, 2.4]
  const groups = [
    { name: 'fore', origin: S, rot: [0, 0, 8] },
    { name: 'hind', origin: H, rot: [0, 0, -14] },
    { name: 'fore_ghost_a', origin: S, rot: [0, 0, 8] },
    { name: 'fore_ghost_b', origin: S, rot: [0, 0, 8] },
    { name: 'hind_ghost_a', origin: H, rot: [0, 0, -14] },
    { name: 'hind_ghost_b', origin: H, rot: [0, 0, -14] }
  ]
  const cubes = [
    { name: 'fore_hinge', group: 'fore', from: [S[0] - 0.5, S[1] - 0.75, S[2] - 0.5], to: [S[0] + 1, S[1] + 0.75, S[2] + 0.7], kind: 'hinge' },
    { name: 'hind_hinge', group: 'hind', from: [H[0] - 0.5, H[1] - 0.5, H[2] - 0.4], to: [H[0] + 0.8, H[1] + 0.5, H[2] + 0.5], kind: 'hinge' },
    { name: 'fore_wing', group: 'fore', from: [S[0], S[1] - 3, S[2] + 0.3], to: [S[0] + 12, S[1] + 3, S[2] + 0.32], kind: 'fore' },
    { name: 'hind_wing', group: 'hind', from: [H[0], H[1] - 2, H[2] + 0.2], to: [H[0] + 8, H[1] + 2, H[2] + 0.22], kind: 'hind' },
    { name: 'fore_ghost_a_plate', group: 'fore_ghost_a', from: [S[0], S[1] - 3, S[2] + 0.36], to: [S[0] + 12, S[1] + 3, S[2] + 0.38], kind: 'fore_ghost' },
    { name: 'fore_ghost_b_plate', group: 'fore_ghost_b', from: [S[0], S[1] - 3, S[2] + 0.36], to: [S[0] + 12, S[1] + 3, S[2] + 0.38], kind: 'fore_ghost' },
    { name: 'hind_ghost_a_plate', group: 'hind_ghost_a', from: [H[0], H[1] - 2, H[2] + 0.26], to: [H[0] + 8, H[1] + 2, H[2] + 0.28], kind: 'hind_ghost' },
    { name: 'hind_ghost_b_plate', group: 'hind_ghost_b', from: [H[0], H[1] - 2, H[2] + 0.26], to: [H[0] + 8, H[1] + 2, H[2] + 0.28], kind: 'hind_ghost' }
  ]
  const rig = { model: 'bee', texture: [64, 64], S, H, groups, cubes }
  rig.used = PACK(rig, 64)
  const share = (kind) => {
    const list = cubes.filter(c => c.kind === kind)
    for (const c of list) c.uv = list[0].uv.slice()
  }
  share('fore_ghost')
  share('hind_ghost')
  return rig
})()
