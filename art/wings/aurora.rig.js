var RIG = (() => {
  const z = 2.4
  const curtains = [
    { name: 'veil', origin: [2.5, 23.5, z], rot: 18, segs: [{ len: 6, h: 13, bend: -4 }, { len: 6, h: 15, bend: -8 }, { len: 6, h: 14, bend: -10 }, { len: 5, h: 11, bend: -12 }], z: 0.7 },
    { name: 'hind_veil', origin: [2.2, 20, z], rot: -6, segs: [{ len: 5, h: 10, bend: -6 }, { len: 5, h: 11, bend: -9 }, { len: 5, h: 9, bend: -12 }], z: 0.3 }
  ]
  const groups = []
  const cubes = []
  for (const c of curtains) {
    const [ox, oy] = c.origin
    groups.push({ name: c.name, origin: c.origin, rot: [0, 0, c.rot] })
    cubes.push({ name: c.name + '_clasp', group: c.name, from: [ox - 0.7, oy - 0.7, z - 0.4], to: [ox + 0.7, oy + 0.7, z + 1.2], kind: 'clasp' })
    let x = ox, parent = c.name
    c.segs.forEach((s, i) => {
      const rail = c.name + '_rail_' + (i + 1), drape = c.name + '_drape_' + (i + 1)
      groups.push({ name: rail, parent, origin: [x, oy, z], rot: [0, 0, s.bend] })
      groups.push({ name: drape, parent: rail, origin: [x, oy, z] })
      cubes.push({ name: rail + '_ray', group: rail, from: [x, oy - 0.25, z + c.z - 0.2], to: [x + s.len + 0.2, oy + 0.35, z + c.z + 0.2], kind: 'ray' })
      x += s.len
      parent = rail
    })
  }
  const rig = { model: 'aurora', texture: [128, 64], curtains, groups, cubes }
  rig.used = PACK(rig, 128)
  const panels = []
  let u0 = 0
  rig.strips = []
  for (const c of curtains) {
    const [ox, oy] = c.origin
    const zz = z + c.z
    const total = c.segs.reduce((a, s) => a + s.len, 0)
    const height = Math.max(...c.segs.map(s => s.h)) + 1
    const strip = { curtain: c.name, w: total + 1, h: height, ventral: [u0, rig.used], dorsal: [u0 + total + 1, rig.used], segs: c.segs }
    u0 += 2 * (total + 1)
    rig.strips.push(strip)
    const top = [], bottom = []
    let x = ox
    const point = (bone, px, py, u, v) => {
      const w = GEOM.world(rig, bone, [px, py])
      return [bone, Math.round(w[0] * 1000) / 1000, Math.round(w[1] * 1000) / 1000, zz, u, v]
    }
    c.segs.forEach((sg, i) => {
      const rail = c.name + '_rail_' + (i + 1), drape = c.name + '_drape_' + (i + 1)
      top.push(point(rail, x, oy + 0.4, x - ox, 0))
      bottom.push(point(drape, x, oy - sg.h, x - ox, sg.h))
      x += sg.len
      if (i === c.segs.length - 1) {
        top.push(point(rail, x, oy + 0.4, x - ox, 0))
        bottom.push(point(drape, x, oy - sg.h, x - ox, sg.h))
      }
    })
    panels.push({ name: c.name, rows: 2, a: top, b: bottom, sheet: { x: 0, y: 0, ventral: strip.ventral, dorsal: strip.dorsal } })
  }
  rig.membranes = { sheet: panels[0].sheet, panels }
  return rig
})()
