var RIG = (() => {
  const z = 2.4
  const sails = [
    { name: 'sail', origin: [2.5, 23, z], rot: 10, ribbons: [{ h: 3, len: [8, 8, 8] }, { h: 3, len: [8, 8, 7] }, { h: 3, len: [7, 7, 7] }, { h: 3, len: [6, 6, 6] }], z: 0.6 },
    { name: 'hind_sail', origin: [2.3, 19.5, z], rot: -16, ribbons: [{ h: 3, len: [7, 6, 6] }, { h: 2.5, len: [6, 6, 5] }, { h: 2.5, len: [5, 5, 4] }], z: 0.25 }
  ]
  const groups = []
  const cubes = []
  for (const sail of sails) {
    const [ox, oy] = sail.origin
    groups.push({ name: sail.name, origin: sail.origin, rot: [0, 0, sail.rot] })
    cubes.push({ name: sail.name + '_root', group: sail.name, from: [ox - 0.6, oy - 1.6, z - 0.4], to: [ox + 1.4, oy + 0.6, z + 0.9], kind: 'root' })
    cubes.push({ name: sail.name + '_edge', group: sail.name, from: [ox, oy, z + sail.z + 0.05], to: [ox + 22, oy + 1.5, z + sail.z + 0.07], kind: 'edge' })
    let top = oy
    sail.ribbons.forEach((r, i) => {
      let x = ox, parent = sail.name
      r.len.forEach((L, j) => {
        const g = sail.name + '_ribbon_' + (i + 1) + '_' + (j + 1)
        groups.push({ name: g, parent, origin: [x, top - r.h / 2, z] })
        const zz = z + sail.z + 0.02 * ((i + j) % 2)
        cubes.push({ name: g + '_plate', group: g, from: [x, top - r.h, zz], to: [x + L + 0.4, top, zz + 0.02], kind: 'ribbon', ribbon: i, seg: j, last: j === r.len.length - 1, sail: sail.name })
        x += L
        parent = g
      })
      top -= r.h
    })
  }
  const rig = { model: 'air', texture: [128, 64], sails, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
