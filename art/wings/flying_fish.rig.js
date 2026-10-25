var RIG = (() => {
  const S = [2.5, 22.5, 2.4]
  const V = [2.0, 15.5, 2.4]
  const groups = [
    { name: 'shoulder', origin: S },
    { name: 'pectoral', parent: 'shoulder', origin: S },
    { name: 'pelvic', origin: V }
  ]
  const cubes = [
    { name: 'fin_root', group: 'shoulder', from: [1.8, 20.8, 1.7], to: [4.2, 24.2, 3.3], kind: 'root' },
    { name: 'pelvic_root', group: 'pelvic', from: [1.4, 14.6, 1.9], to: [3, 16.4, 3], kind: 'root' }
  ]
  const pectoral = [
    { angle: 2, length: 19 }, { angle: -8, length: 19 }, { angle: -18, length: 18 }, { angle: -28, length: 17 },
    { angle: -38, length: 15 }, { angle: -49, length: 13 }, { angle: -60, length: 11 }
  ]
  const pelvic = [{ angle: -20, length: 10 }, { angle: -33, length: 10 }, { angle: -46, length: 9 }, { angle: -59, length: 8 }]
  const fin = (list, prefix, parent, O, zf, t) => list.forEach((r, i) => {
    const k = i + 1
    groups.push({ name: prefix + k, parent, origin: O, rot: [0, 0, r.angle] })
    cubes.push({ name: prefix + k + '_spine', group: prefix + k, from: [O[0], O[1] - t, zf - t], to: [O[0] + r.length, O[1] + t, zf + t], kind: 'spine', index: k, count: list.length })
  })
  const zp = S[2] + 0.45, zv = V[2] + 0.35
  fin(pectoral, 'ray_', 'pectoral', S, zp, 0.3)
  fin(pelvic, 'pelvic_', 'pelvic', V, zv, 0.25)
  const rig = { model: 'flying_fish', texture: [128, 64], S, V, pectoral, pelvic, groups, cubes }
  rig.used = PACK(rig, 128)
  const r3 = (v) => Math.round(v * 1000) / 1000
  const rail = (bone, O, length, z) => [0, 0.25, 0.5, 0.75, 1].map(t => {
    const p = GEOM.world(rig, bone, [O[0] + Math.max(0.6, length * t), O[1]])
    return [bone, r3(p[0]), r3(p[1]), z]
  })
  let u = 0
  const panels = []
  rig.sheets = {}
  for (const [list, prefix, O, z, name] of [[pectoral, 'ray_', S, zp, 'pectoral'], [pelvic, 'pelvic_', V, zv, 'pelvic']]) {
    const rails = list.map((r, i) => rail(prefix + (i + 1), O, r.length + 0.5, z))
    const pts = rails.flat()
    const x0 = Math.floor(Math.min(...pts.map(q => q[1])) - 1), x1 = Math.ceil(Math.max(...pts.map(q => q[1])) + 1)
    const y0 = Math.ceil(Math.max(...pts.map(q => q[2])) + 1), y1 = Math.floor(Math.min(...pts.map(q => q[2])) - 1)
    const w = x1 - x0, h = y0 - y1
    const sheet = { x: x0, y: y0, w, h, ventral: [u, rig.used], dorsal: [u + w, rig.used] }
    u += 2 * w + 1
    rig.sheets[name] = { ...sheet, rays: list.map((r, i) => [O, GEOM.world(rig, prefix + (i + 1), [O[0] + r.length + 0.5, O[1]])]), O }
    for (let i = 0; i + 1 < rails.length; i++) {
      panels.push({ name: name + '_' + (i + 1), rows: 2, a: rails[i], b: rails[i + 1], sheet: { x: x0, y: y0, ventral: sheet.ventral, dorsal: sheet.dorsal } })
    }
  }
  rig.membranes = { sheet: panels[0].sheet, panels }
  return rig
})()
