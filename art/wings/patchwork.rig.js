var RIG = (() => {
  const b = BIRD({
    rows: true, arm: 8, forearm: 10, hand: 4, bone: 1.1,
    tertials: 11, secondaries: 13, handSheet: 13, handCoverts: 5, coverts: 6, marginal: 3,
    primaries: [
      { angle: -4, length: 14 }, { angle: -15, length: 12 }, { angle: -25, length: 15 },
      { angle: -36, length: 13 }, { angle: -48, length: 14 }, { angle: -61, length: 12 }
    ]
  })
  const { S, E, W, H } = b
  const y = 22.5, z = 2.4
  b.groups.push({ name: 'primary_7', parent: 'primaries', origin: [H[0] - 3.6, y - 2, z], rot: [0, 0, -74] })
  b.cubes.push({ name: 'feather_7', group: 'primary_7', from: [H[0] - 3.6, y - 4, z + 1.6], to: [H[0] - 3.6 + 11, y, z + 1.62], kind: 'primary', index: 7, only: 'right' })
  b.cubes.push(
    { name: 'splint', group: 'forearm', from: [E[0] + 1, y + 0.9, z - 0.4], to: [W[0] - 1, y + 1.7, z + 0.4], kind: 'stick', only: 'right' },
    { name: 'bandage_1', group: 'forearm', from: [E[0] + 2.5, y - 1.1, z - 0.9], to: [E[0] + 4, y + 1.9, z + 0.9], kind: 'bandage', only: 'right' },
    { name: 'bandage_2', group: 'forearm', from: [W[0] - 4, y - 1.1, z - 0.9], to: [W[0] - 2.5, y + 1.9, z + 0.9], kind: 'bandage', only: 'right' },
    { name: 'knot', group: 'hand', from: [W[0] - 0.8, y - 0.8, z - 0.8], to: [W[0] + 0.8, y + 0.8, z + 0.8], kind: 'string' },
    { name: 'string_tail', group: 'hand', from: [W[0] - 0.2, y - 4.5, z - 0.2], to: [W[0] + 0.2, y - 0.8, z + 0.2], kind: 'string', only: 'left' }
  )
  b.groups.push({ name: 'flap', parent: 'secondaries', origin: [E[0] + 6, y - 9, z], rot: [0, 0, -18] })
  b.cubes.push({ name: 'flap_plate', group: 'flap', from: [E[0] + 6, y - 14, z + 1.0], to: [E[0] + 10, y - 9, z + 1.02], kind: 'flap', only: 'left' })
  const rig = { model: 'patchwork', texture: [128, 128], ...b }
  rig.used = PACK(rig, 128)
  const plates = rig.cubes.filter(c => c.kind !== 'bone' && c.to[2] - c.from[2] < 0.05 && !c.only)
  const copies = plates.map(c => ({ ...c, uv: null, source: c }))
  PACK({ cubes: copies }, 128, rig.used)
  for (const c of copies) c.source.leftUv = c.uv
  rig.used = Math.max(...copies.map(c => c.uv[1] + Math.ceil(c.to[1] - c.from[1]) + 1))
  return rig
})()
