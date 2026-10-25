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
    cubes.push({ name: name + '_crest', group: name, from: [x, y - 0.6, z - 0.5], to: [x + g.len + 0.3, y + 0.6, z + 0.5], kind: 'crest', index: i })
    x += g.len
    parent = name
  })
  const rig = { model: 'water', texture: [128, 64], S, segs, groups, cubes }
  rig.used = PACK(rig, 128)
  const at = (k, px, py) => {
    const w = GEOM.world(rig, 'seg_' + k, [px, py])
    return ['seg_' + k, Math.round(w[0] * 1000) / 1000, Math.round(w[1] * 1000) / 1000, z + 0.04]
  }
  const a = [], b = []
  let sx = S[0]
  segs.forEach((g, i) => {
    a.push(at(i + 1, sx, y + Math.round(2.6 - 0.5 * i)))
    b.push(at(i + 1, sx, y - g.h))
    sx += g.len
  })
  const n = segs.length
  a.push(at(n, sx - 1, y + 0.6), at(n, sx + 2, y - 0.5))
  b.push(at(n, sx - 1, y - segs[n - 1].h + 1), at(n, sx + 2, y - 0.5))
  const pts = [...a, ...b]
  const x0 = Math.floor(Math.min(...pts.map(q => q[1])) - 1), x1 = Math.ceil(Math.max(...pts.map(q => q[1])) + 1)
  const y0 = Math.ceil(Math.max(...pts.map(q => q[2])) + 1), y1 = Math.floor(Math.min(...pts.map(q => q[2])) - 1)
  const sheet = { x: x0, y: y0, w: x1 - x0, h: y0 - y1, ventral: [0, rig.used], dorsal: [x1 - x0, rig.used] }
  rig.sheet = sheet
  rig.membranes = { sheet: { x: sheet.x, y: sheet.y, ventral: sheet.ventral, dorsal: sheet.dorsal }, panels: [{ name: 'water', rows: 2, a, b }] }
  return rig
})()
