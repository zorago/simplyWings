var RIG = (() => {
  const S = [2.5, 22.5, 2.4]
  const H = [2.4, 19.5, 2.4]
  const fore = [[0, 0.5], [6, 1.4], [13, 2.2], [20, 2.8], [20.6, 6], [17.5, 10.5], [12.5, 13.2], [7, 11.5], [2.5, 7.5], [0, 4.5]]
  const hind = [[0, 0.3], [7, 0.8], [12, 3], [14.3, 7.2], [12.6, 12], [8.4, 13.8], [4.2, 12], [1.2, 7.8], [0, 3.5]]
  const groups = [
    { name: 'fore', origin: S, rot: [0, 0, 14] },
    { name: 'hind', origin: H, rot: [0, 0, -8] },
    { name: 'tuft', origin: [0, 22, 2.4], single: true }
  ]
  const cubes = [
    { name: 'fore_wing', group: 'fore', from: [S[0], S[1] - 14, S[2] + 0.45], to: [S[0] + 21, S[1] + 0, S[2] + 0.47], kind: 'fore', shape: fore },
    { name: 'hind_wing', group: 'hind', from: [H[0], H[1] - 14, H[2] + 0.3], to: [H[0] + 15, H[1] + 0, H[2] + 0.32], kind: 'hind', shape: hind },
    { name: 'fore_root', group: 'fore', from: [S[0] - 0.5, S[1] - 1.5, S[2] - 0.2], to: [S[0] + 2, S[1] + 0.5, S[2] + 1], kind: 'root' },
    { name: 'tuft_fur', group: 'tuft', from: [-2.5, 19.5, 2.1], to: [2.5, 24.5, 3.6], kind: 'tuft' }
  ]
  const rig = { model: 'moth', texture: [128, 64], S, H, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
