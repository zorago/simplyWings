var RIG = (() => {
  const b = BIRD({
    arm: 10, forearm: 13, hand: 6, bone: 1.2,
    tertials: 16, secondaries: 20, handSheet: 19, handCoverts: 8, coverts: 8, marginal: 5,
    alula: { angle: 22, length: 7 },
    primaryWidth: 5, primaryStep: [0.55, 0.35], primaryDz: 0.06,
    primaries: [
      { angle: 4, length: 16 }, { angle: -6, length: 21 }, { angle: -15, length: 24 }, { angle: -24, length: 26 },
      { angle: -33, length: 26 }, { angle: -42, length: 25 }, { angle: -51, length: 24 }, { angle: -60, length: 23 },
      { angle: -69, length: 22 }, { angle: -78, length: 21 }, { angle: -87, length: 20 }
    ]
  })
  b.groups.push({ name: 'scapulars', parent: 'shoulder', origin: b.S })
  b.cubes.push({ name: 'scapular_plate', group: 'scapulars', from: [b.S[0] - 1, b.S[1] - 7, b.S[2] + 0.7], to: [b.S[0] + 5, b.S[1] + 1.5, b.S[2] + 0.72], kind: 'scapulars' })
  const ring = { name: 'aureole', origin: [0, 27.5, 6.6], single: true }
  b.groups.push(ring)
  const n = 12, r = 5.6
  for (let i = 0; i < n; i++) {
    const a = (i / n) * 360
    const cx = r * Math.cos(a * Math.PI / 180), cy = 27.5 + r * Math.sin(a * Math.PI / 180)
    b.cubes.push({ name: 'aureole_' + (i + 1), group: 'aureole', from: [cx - 1.6, cy - 0.45, 6.4], to: [cx + 1.6, cy + 0.45, 6.8], origin: [cx, cy, 6.6], rot: [0, 0, a + 90], kind: 'aureole' })
  }
  const rig = { model: 'angel', texture: [128, 128], ...b }
  rig.used = PACK(rig, 128)
  const segs = rig.cubes.filter(c => c.kind === 'aureole')
  for (const c of segs) c.uv = segs[0].uv.slice()
  return rig
})()
