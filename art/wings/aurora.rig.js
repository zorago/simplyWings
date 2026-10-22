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
      const zz = z + c.z + 0.03 * (i % 2)
      cubes.push({ name: drape + '_light', group: drape, from: [x, oy - s.h, zz], to: [x + s.len + 0.3, oy + 0.6, zz + 0.02], kind: 'light', curtain: c.name, index: i, total: c.segs.length })
      cubes.push({ name: rail + '_ray', group: rail, from: [x, oy - 0.25, z + c.z - 0.2], to: [x + s.len + 0.2, oy + 0.35, z + c.z + 0.2], kind: 'ray' })
      x += s.len
      parent = rail
    })
  }
  const rig = { model: 'aurora', texture: [128, 64], curtains, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
