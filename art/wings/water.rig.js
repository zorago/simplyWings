var RIG = (() => {
  const y = 22.5, z = 2.4
  const S = [2.5, y, z]
  const segs = [{ len: 6, h: 13 }, { len: 6, h: 12 }, { len: 6, h: 10 }, { len: 6, h: 8 }, { len: 6, h: 6 }]
  const groups = [{ name: 'shoulder', origin: S }]
  const cubes = [
    { name: 'spring', group: 'shoulder', from: [1.2, 20.8, 1.6], to: [4.4, 24.2, 3.4], kind: 'spring' }
  ]
  let x = S[0], parent = 'shoulder'
  segs.forEach((g, i) => {
    const k = i + 1, name = 'seg_' + k
    groups.push({ name, parent, origin: [x, y, z], rot: [0, 0, -3] })
    const lift = Math.round(2.6 - 0.5 * i)
    cubes.push({ name: name + '_water', group: name, from: [x, y - g.h, z + (i % 2 ? 0.06 : 0)], to: [x + g.len + (k === segs.length ? 2 : 0.5), y + lift, z + 0.02 + (i % 2 ? 0.06 : 0)], kind: 'water', index: i, lift })
    cubes.push({ name: name + '_crest', group: name, from: [x, y - 0.6, z - 0.5], to: [x + g.len + 0.3, y + 0.6, z + 0.5], kind: 'crest', index: i })
    x += g.len
    parent = name
  })
  const drops = [{ seg: 5, at: [30, 18, 2.6] }, { seg: 4, at: [24, 14.5, 2.6] }, { seg: 3, at: [17, 12, 2.6] }]
  drops.forEach((d, i) => {
    groups.push({ name: 'drop_' + (i + 1), parent: 'seg_' + d.seg, origin: d.at })
    cubes.push({ name: 'drop_cube_' + (i + 1), group: 'drop_' + (i + 1), from: [d.at[0] - 0.45, d.at[1] - 0.7, d.at[2] - 0.45], to: [d.at[0] + 0.45, d.at[1] + 0.5, d.at[2] + 0.45], kind: 'drop' })
  })
  const rig = { model: 'water', texture: [128, 64], S, segs, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
