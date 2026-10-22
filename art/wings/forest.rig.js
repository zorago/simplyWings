var RIG = (() => {
  const z = 2.4
  const wings = [
    {
      name: 'branch', origin: [2.5, 23, z], rot: 12, segs: [9, 8, 7], bend: [-6, -8],
      leaves: [
        { seg: 0, at: 3, rot: -70, len: 6, w: 4 }, { seg: 0, at: 6.5, rot: -95, len: 7, w: 4 }, { seg: 0, at: 8.5, rot: 40, len: 5, w: 3 },
        { seg: 1, at: 1.5, rot: -60, len: 7, w: 4 }, { seg: 1, at: 4.5, rot: -85, len: 8, w: 5 }, { seg: 1, at: 7, rot: -30, len: 7, w: 4 },
        { seg: 2, at: 2, rot: -50, len: 7, w: 4 }, { seg: 2, at: 4.5, rot: -15, len: 6, w: 4 }, { seg: 2, at: 6.5, rot: 10, len: 6, w: 3 },
        { seg: 1, at: 3, rot: 35, len: 5, w: 3 },
        { seg: 0, at: 1.5, rot: -88, len: 8, w: 5 }, { seg: 0, at: 5, rot: -80, len: 9, w: 5 }, { seg: 1, at: 0.5, rot: -100, len: 9, w: 5 },
        { seg: 1, at: 6, rot: -70, len: 9, w: 5 }, { seg: 2, at: 0.5, rot: -75, len: 8, w: 5 }, { seg: 2, at: 3.5, rot: -45, len: 8, w: 4 }
      ],
      flowers: [{ seg: 0, at: 5, dy: 1.2 }, { seg: 1, at: 6, dy: 1 }, { seg: 2, at: 6.8, dy: 0.6 }],
      z: 0.7
    },
    {
      name: 'hind_branch', origin: [2.3, 19.5, z], rot: -20, segs: [7, 6, 5], bend: [-8, -10],
      leaves: [
        { seg: 0, at: 3, rot: -75, len: 6, w: 4 }, { seg: 0, at: 6, rot: -95, len: 6, w: 4 },
        { seg: 1, at: 2, rot: -70, len: 6, w: 4 }, { seg: 1, at: 4.5, rot: -40, len: 6, w: 4 },
        { seg: 2, at: 2.5, rot: -25, len: 5, w: 3 }, { seg: 2, at: 4.5, rot: 5, len: 5, w: 3 },
        { seg: 0, at: 1, rot: -90, len: 7, w: 5 }, { seg: 1, at: 0.5, rot: -95, len: 7, w: 5 }, { seg: 1, at: 3.5, rot: -60, len: 7, w: 4 }
      ],
      flowers: [{ seg: 1, at: 3, dy: 0.9 }, { seg: 2, at: 4.6, dy: 0.6 }],
      z: 0.3
    }
  ]
  const groups = []
  const cubes = []
  const leafRefs = []
  for (const wg of wings) {
    const [ox, oy] = wg.origin
    const segNames = [wg.name, wg.name + '_mid', wg.name + '_tip']
    const starts = []
    let x = ox
    segNames.forEach((g, i) => {
      groups.push({ name: g, parent: i === 0 ? undefined : segNames[i - 1], origin: [x, oy, z], rot: [0, 0, i === 0 ? wg.rot : wg.bend[i - 1]] })
      const t = 0.8 - 0.18 * i
      cubes.push({ name: g + '_wood', group: g, from: [x, oy - t, z - t], to: [x + wg.segs[i] + 0.4, oy + t, z + t], kind: 'wood' })
      starts.push(x)
      x += wg.segs[i]
    })
    wg.leaves.forEach((l, i) => {
      const name = wg.name + '_leaf_' + (i + 1)
      const px = starts[l.seg] + l.at
      groups.push({ name, parent: segNames[l.seg], origin: [px, oy, z], rot: [0, 0, l.rot] })
      const zz = z + wg.z + 0.05 * (i % 4)
      cubes.push({ name: name + '_blade', group: name, from: [px, oy - l.w / 2, zz], to: [px + l.len, oy + l.w / 2, zz + 0.02], kind: 'leaf', index: i, wing: wg.name })
      cubes.push({ name: name + '_stem', group: name, from: [px - 0.2, oy - 0.25, zz - 0.2], to: [px + 1.4, oy + 0.25, zz + 0.2], kind: 'stem' })
      leafRefs.push(name)
    })
    wg.flowers.forEach((f, i) => {
      const name = wg.name + '_flower_' + (i + 1)
      const px = starts[f.seg] + f.at, py = oy + f.dy
      groups.push({ name, parent: segNames[f.seg], origin: [px, py, z + 0.6] })
      cubes.push({ name: name + '_petals', group: name, from: [px - 1, py - 1, z + 0.3], to: [px + 1, py + 1, z + 1.1], kind: 'petals' })
      cubes.push({ name: name + '_heart', group: name, from: [px - 0.4, py - 0.4, z + 1.05], to: [px + 0.4, py + 0.4, z + 1.3], kind: 'pollen' })
    })
  }
  cubes.push({ name: 'burl', group: 'branch', from: [1.4, 21.5, 1.4], to: [4.2, 24.5, 3.6], kind: 'burl' })
  const rig = { model: 'forest', texture: [128, 64], wings, groups, cubes }
  rig.used = PACK(rig, 128)
  return rig
})()
