var RIG = (() => {
  const z = 2.4
  const sails = [
    { name: 'sail', origin: [2.5, 23, z], rot: 10, ribbons: [{ h: 3, len: [8, 8, 8] }, { h: 3, len: [8, 8, 7] }, { h: 3, len: [7, 7, 7] }, { h: 3, len: [6, 6, 6] }], z: 0.6 },
    { name: 'hind_sail', origin: [2.3, 19.5, z], rot: -16, ribbons: [{ h: 3, len: [7, 6, 6] }, { h: 2.5, len: [6, 6, 5] }, { h: 2.5, len: [5, 5, 4] }], z: 0.25 }
  ]
  const groups = []
  const cubes = []
  const ribbons = []
  for (const sail of sails) {
    const [ox, oy] = sail.origin
    groups.push({ name: sail.name, origin: sail.origin, rot: [0, 0, sail.rot] })
    cubes.push({ name: sail.name + '_root', group: sail.name, from: [ox - 0.6, oy - 1.6, z - 0.4], to: [ox + 1.4, oy + 0.6, z + 0.9], kind: 'root' })
    cubes.push({ name: sail.name + '_edge', group: sail.name, from: [ox, oy, z + sail.z + 0.05], to: [ox + 22, oy + 1.5, z + sail.z + 0.07], kind: 'edge' })
    let top = oy
    sail.ribbons.forEach((r, i) => {
      let x = ox, parent = sail.name
      const joints = []
      r.len.forEach((L, j) => {
        const g = sail.name + '_ribbon_' + (i + 1) + '_' + (j + 1)
        groups.push({ name: g, parent, origin: [x, top - r.h / 2, z] })
        joints.push({ bone: g, x })
        x += L
        parent = g
      })
      joints.push({ bone: parent, x })
      ribbons.push({ sail: sail.name, index: i, ox, top, h: r.h, len: r.len, total: x - ox, z: z + sail.z + 0.02 * (i % 2), joints })
      top -= r.h
    })
  }
  const rig = { model: 'air', texture: [128, 64], sails, groups, cubes, ribbons }
  rig.used = PACK(rig, 128)
  const panels = []
  let u = 0, v = rig.used
  for (const rb of ribbons) {
    const w = Math.ceil(rb.total) + 1, h = Math.ceil(rb.h)
    if (u + 2 * w > 128) {
      u = 0
      v += 4
    }
    rb.ventral = [u, v]
    rb.dorsal = [u + w, v]
    rb.w = w
    u += 2 * w
    const point = (bone, px, py, pu, pv) => {
      const p = GEOM.world(rig, bone, [px, py])
      return [bone, Math.round(p[0] * 1000) / 1000, Math.round(p[1] * 1000) / 1000, rb.z, pu, pv]
    }
    const a = rb.joints.map(j => point(j.bone, j.x, rb.top, j.x - rb.ox, 0))
    const b = rb.joints.map(j => point(j.bone, j.x, rb.top - rb.h, j.x - rb.ox, rb.h))
    panels.push({ name: rb.sail + '_ribbon_' + (rb.index + 1), rows: 1, a, b, sheet: { x: 0, y: 0, ventral: rb.ventral, dorsal: rb.dorsal } })
  }
  rig.membranes = { sheet: panels[0].sheet, panels }
  return rig
})()
