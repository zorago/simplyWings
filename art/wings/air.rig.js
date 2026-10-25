var RIG = (() => {
  const y = 22.5, z = 2.4
  const S = [2.5, y, z]
  const segs = [{ len: 5.5, rot: 4 }, { len: 5.5, rot: 3 }, { len: 5.5, rot: 0 }, { len: 5.5, rot: -4 }, { len: 5, rot: -7 }]
  const groups = [{ name: 'shoulder', origin: S }]
  const cubes = [
    { name: 'vortex', group: 'shoulder', from: [1.3, 21.1, 1.8], to: [4.3, 24.1, 3.2], kind: 'vortex' },
    { name: 'vortex_core', group: 'shoulder', from: [2.1, 21.9, 3.2], to: [3.5, 23.3, 3.6], kind: 'core' }
  ]
  let x = S[0], parent = 'shoulder'
  segs.forEach((g, i) => {
    const name = 'seg_' + (i + 1)
    groups.push({ name, parent, origin: [x, y, z], rot: [0, 0, g.rot] })
    cubes.push({ name: name + '_jet', group: name, from: [x, y - 0.3, z - 0.3], to: [x + g.len + 0.2, y + 0.4, z + 0.3], kind: 'jet', index: i })
    x += g.len
    parent = name
  })
  const end = x
  const layers = [
    { name: 'drift', depth: [15, 14, 12, 9, 5], tip: 0, dz: 0.02, frames: 8 },
    { name: 'breeze', depth: [10.5, 10, 8.5, 6.5, 4], tip: 2.5, dz: 0.05, frames: 8 },
    { name: 'gust', depth: [6, 5.5, 5, 4, 3], tip: 5, dz: 0.08, frames: 8 }
  ]
  const rig = { model: 'air', texture: [256, 256], S, segs, end, layers, groups, cubes }
  rig.used = PACK(rig, 256)
  const r3 = (v) => Math.round(v * 1000) / 1000
  const at = (k, px, py, zz) => {
    const w = GEOM.world(rig, 'seg_' + k, [px, py])
    return ['seg_' + k, r3(w[0]), r3(w[1]), zz]
  }
  const panels = []
  let u = 0
  for (const L of layers) {
    const zz = z + L.dz
    const a = [], b = []
    let sx = S[0]
    segs.forEach((g, i) => {
      a.push(at(i + 1, sx, y + 0.4, zz))
      b.push(at(i + 1, sx, y - L.depth[i], zz))
      sx += g.len
    })
    const n = segs.length
    a.push(at(n, end + L.tip, y + 0.2, zz))
    b.push(at(n, end + L.tip, y - 0.6, zz))
    const pts = [...a, ...b]
    const x0 = Math.floor(Math.min(...pts.map(q => q[1])) - 1), x1 = Math.ceil(Math.max(...pts.map(q => q[1])) + 1)
    const y0 = Math.ceil(Math.max(...pts.map(q => q[2])) + 1), y1 = Math.floor(Math.min(...pts.map(q => q[2])) - 1)
    const w = x1 - x0, h = y0 - y1
    L.sheet = { x: x0, y: y0, w, h, ventral: [u, rig.used], dorsal: [u + w, rig.used], frames: L.frames, fps: 8, step: [0, h + 1] }
    L.a = a
    L.b = b
    u += 2 * w + 1
    panels.push({ name: L.name, rows: 2, a, b, sheet: { x: x0, y: y0, ventral: L.sheet.ventral, dorsal: L.sheet.dorsal, frames: L.frames, fps: 8, step: L.sheet.step } })
  }
  rig.membranes = { sheet: panels[0].sheet, panels }
  return rig
})()
