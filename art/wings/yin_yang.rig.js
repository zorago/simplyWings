var RIG = (() => {
  const b = BIRD({
    rows: true, arm: 9, forearm: 12, hand: 5, bone: 1.1,
    tertials: 14, secondaries: 18, handSheet: 17, handCoverts: 7, coverts: 7, marginal: 4,
    alula: { angle: 22, length: 6 },
    primaryWidth: 5, primaryStep: [0.55, 0.35],
    primaries: [
      { angle: 4, length: 15 }, { angle: -7, length: 19 }, { angle: -18, length: 22 }, { angle: -29, length: 23 },
      { angle: -40, length: 22 }, { angle: -51, length: 20 }, { angle: -62, length: 19 }, { angle: -73, length: 19 },
      { angle: -84, length: 20 }
    ]
  })
  for (const c of b.cubes) {
    if (c.kind === 'greater_coverts') { c.from[2] += 0.35; c.to[2] += 0.35 }
    if (c.kind === 'arm_coverts') { c.from[2] += 0.1; c.to[2] += 0.1 }
  }
  b.groups.push({ name: 'taiji', origin: [0, 19.5, 5.4], single: true })
  b.cubes.push({ name: 'taiji_disc', group: 'taiji', from: [-4.5, 15, 5.4], to: [4.5, 24, 5.42], kind: 'taiji' })
  b.cubes.push({ name: 'taiji_rim', group: 'taiji', from: [-0.6, 23.8, 5.2], to: [0.6, 24.6, 5.7], kind: 'rim' })
  const rig = { model: 'yin_yang', texture: [256, 128], ...b }
  rig.used = PACK(rig, 256)
  const mirrored = rig.cubes.filter(c => !rig.groups.find(g => g.name === c.group).single)
  const clones = mirrored.map(c => ({ ...c, uv: undefined }))
  rig.used = PACK({ cubes: clones }, 256, rig.used)
  mirrored.forEach((c, i) => { c.leftUv = clones[i].uv })
  return rig
})()
