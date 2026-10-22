var RIG = (() => {
  const S = [2.4, 22.8, 2.4]
  const H = [2.2, 20.2, 2.4]
  const fore = [[0, 1], [5, 0.2], [12, 0.4], [19, 1.4], [24, 3.2], [26.5, 6.5], [25.5, 10.5], [21.5, 13.5], [15.5, 14.8], [9, 13.2], [4, 9.5], [0.8, 5]]
  const hind = [[0, 0.6], [6, 0.8], [12, 2.6], [16, 6], [17.2, 10], [15.5, 14], [12.5, 17.5], [8, 19.5], [4, 17], [1.2, 11], [0, 5]]
  const groups = [
    { name: 'fore', origin: S, rot: [0, 0, 12] },
    { name: 'hind', origin: H, rot: [0, 0, -10] },
    { name: 'hind_tail', parent: 'hind', origin: [H[0] + 8, H[1] - 19, H[2]], rot: [0, 0, -12] },
    { name: 'hind_tail_tip', parent: 'hind_tail', origin: [H[0] + 8, H[1] - 25, H[2]], rot: [0, 0, 10] },
    { name: 'lamp', origin: [0, 19, 3.6], single: true }
  ]
  const cubes = [
    { name: 'fore_wing', group: 'fore', from: [S[0], S[1] - 15, S[2] + 0.5], to: [S[0] + 27, S[1], S[2] + 0.52], kind: 'fore', shape: fore },
    { name: 'hind_wing', group: 'hind', from: [H[0], H[1] - 20, H[2] + 0.25], to: [H[0] + 18, H[1], H[2] + 0.27], kind: 'hind', shape: hind },
    { name: 'fore_root', group: 'fore', from: [S[0] - 0.6, S[1] - 1.8, S[2] - 0.3], to: [S[0] + 1.6, S[1] + 0.4, S[2] + 1], kind: 'root' },
    { name: 'hind_root', group: 'hind', from: [H[0] - 0.6, H[1] - 1.6, H[2] - 0.3], to: [H[0] + 1.4, H[1] + 0.4, H[2] + 0.9], kind: 'root' },
    { name: 'hind_tail_plate', group: 'hind_tail', from: [H[0] + 6.5, H[1] - 25, H[2] + 0.25], to: [H[0] + 9.5, H[1] - 18.5, H[2] + 0.27], kind: 'tail' },
    { name: 'hind_tail_flame', group: 'hind_tail_tip', from: [H[0] + 6.5, H[1] - 31, H[2] + 0.29], to: [H[0] + 9.5, H[1] - 24.5, H[2] + 0.31], kind: 'tail_flame' },
    { name: 'lamp_chain', group: 'lamp', from: [-0.25, 18, 3.4], to: [0.25, 21, 3.8], kind: 'chain' },
    { name: 'lamp_body', group: 'lamp', from: [-1.2, 15.6, 2.8], to: [1.2, 18, 4.4], kind: 'lamp' },
    { name: 'lamp_spout', group: 'lamp', from: [1.2, 16.2, 3.2], to: [2.4, 17, 4], kind: 'lamp' },
    { name: 'lamp_flame', group: 'lamp', from: [1.6, 17, 3.4], to: [2.6, 19, 3.42], kind: 'lamp_flame' }
  ]
  const margin = (wing, shape, origin, z, count, from, to, sizeL) => {
    for (let i = 0; i < count; i++) {
      const t = from + (to - from) * (i + 0.5) / count
      const idx = Math.floor(t * (shape.length - 1))
      const a = shape[idx], b = shape[Math.min(shape.length - 1, idx + 1)]
      const f = t * (shape.length - 1) - idx
      const px = a[0] + (b[0] - a[0]) * f, py = a[1] + (b[1] - a[1]) * f
      const cx = shape.reduce((s, p) => s + p[0], 0) / shape.length, cy = shape.reduce((s, p) => s + p[1], 0) / shape.length
      const ang = Math.atan2(-(py - cy), px - cx) * 180 / Math.PI
      const ox = origin[0] + px, oy = origin[1] - py
      const name = wing + '_flame_' + (i + 1)
      groups.push({ name, parent: wing, origin: [ox, oy, origin[2]], rot: [0, 0, ang] })
      cubes.push({ name: name + '_tongue', group: name, from: [ox - 0.5, oy - 1.5, origin[2] + z + 0.06 + 0.02 * (i % 2)], to: [ox + sizeL, oy + 1.5, origin[2] + z + 0.08 + 0.02 * (i % 2)], kind: 'soulfire', index: i })
    }
  }
  margin('fore', fore, S, 0.5, 6, 0.28, 0.82, 4)
  margin('hind', hind, H, 0.25, 4, 0.3, 0.62, 3.5)
  const rig = { model: 'psyche', texture: [128, 128], S, H, groups, cubes }
  rig.used = PACK(rig, 128)
  const fl = cubes.filter(c => c.kind === 'soulfire')
  for (const c of fl) c.uv = fl[0].uv.slice()
  return rig
})()
