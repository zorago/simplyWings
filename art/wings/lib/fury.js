var FURY = (o) => {
  const groups = [], cubes = []
  const rad = Math.PI / 180
  const wing = (w) => {
    const P = w.prefix
    const y = w.S[1], z = w.S[2]
    const S = w.S, E = [S[0] + w.arm, y, z], W = [E[0] + w.forearm, y, z]
    groups.push({ name: P + 'shoulder', origin: S, rot: [0, 0, w.rot || 0] })
    groups.push({ name: P + 'arm', parent: P + 'shoulder', origin: S })
    groups.push({ name: P + 'arm_membrane', parent: P + 'arm', origin: S })
    groups.push({ name: P + 'forearm', parent: P + 'arm', origin: E })
    groups.push({ name: P + 'forearm_membrane', parent: P + 'forearm', origin: E })
    groups.push({ name: P + 'hand', parent: P + 'forearm', origin: W })
    groups.push({ name: P + 'thumb', parent: P + 'hand', origin: W, rot: [0, 0, 50] })
    const fz = (i) => z + 0.18 - 0.06 * i
    cubes.push({ name: P + 'arm_web', group: P + 'arm_membrane', from: [S[0], y - w.armDepth, z - 0.05], to: [E[0], y, z - 0.03], kind: 'web', wing: P })
    cubes.push({ name: P + 'forearm_web', group: P + 'forearm_membrane', from: [E[0], y - w.armDepth, z + 0.02], to: [W[0], y, z + 0.04], kind: 'web', wing: P })
    cubes.push({ name: P + 'arm_front', group: P + 'arm_membrane', from: [S[0], y, z - 0.05], to: [E[0], y + w.front, z - 0.03], kind: 'web', wing: P })
    cubes.push({ name: P + 'forearm_front', group: P + 'forearm_membrane', from: [E[0], y, z + 0.02], to: [W[0], y + w.front * 0.7, z + 0.04], kind: 'web', wing: P })
    const t = w.bone
    cubes.push({ name: P + 'shoulder_cap', group: P + 'shoulder', from: [S[0] - 1.4 * t, y - 1.4 * t, z - 1 * t], to: [S[0] + 1.6 * t, y + 1.4 * t, z + 1.2 * t], kind: 'skin', wing: P })
    cubes.push({ name: P + 'arm_bone', group: P + 'arm', from: [S[0], y - 1 * t, z - 0.7 * t], to: [E[0], y + 1 * t, z + 0.7 * t], kind: 'skin', wing: P })
    cubes.push({ name: P + 'forearm_bone', group: P + 'forearm', from: [E[0], y - 0.8 * t, z - 0.6 * t], to: [W[0], y + 0.8 * t, z + 0.6 * t], kind: 'skin', wing: P })
    cubes.push({ name: P + 'elbow', group: P + 'forearm', from: [E[0] - 1.1 * t, y - 1.1 * t, z - 0.9 * t], to: [E[0] + 1.1 * t, y + 1.1 * t, z + 0.9 * t], kind: 'skin', wing: P })
    cubes.push({ name: P + 'wrist', group: P + 'hand', from: [W[0] - 1 * t, y - 1 * t, z - 0.8 * t], to: [W[0] + 1 * t, y + 1 * t, z + 0.8 * t], kind: 'skin', wing: P })
    cubes.push({ name: P + 'thumb_claw', group: P + 'thumb', from: [W[0], y - 0.4 * t, z - 0.4 * t], to: [W[0] + 2.5 * t, y + 0.4 * t, z + 0.4 * t], kind: 'claw', wing: P })
    w.fingers.forEach((f, i) => {
      const g = P + 'finger_' + (i + 1), tp = g + '_tip'
      const j = [W[0] + f.p, y, z]
      groups.push({ name: g, parent: P + 'hand', origin: W, rot: [0, 0, f.angle] })
      groups.push({ name: tp, parent: g, origin: j, rot: [0, 0, f.droop] })
      cubes.push({ name: g + '_bone', group: g, from: [W[0], y - 0.5 * t, z - 0.5 * t], to: [j[0], y + 0.5 * t, z + 0.5 * t], kind: 'skin', wing: P })
      cubes.push({ name: tp + '_bone', group: tp, from: [j[0], y - 0.35 * t, z - 0.35 * t], to: [j[0] + f.d, y + 0.35 * t, z + 0.35 * t], kind: 'skin', wing: P })
      const n = w.fingers[i + 1]
      if (n) {
        const delta = (f.angle - n.angle) * rad
        const reach = (n.p + n.d) * Math.sin(delta)
        const h1 = Math.ceil(Math.min(f.p * Math.tan(Math.min(delta, 1.3)), reach) + 1)
        const h2 = Math.ceil(reach + 2)
        groups.push({ name: g + '_web', parent: g, origin: W })
        groups.push({ name: tp + '_web', parent: tp, origin: j })
        cubes.push({ name: g + '_membrane', group: g + '_web', from: [W[0], y - h1, fz(i)], to: [j[0], y, fz(i) + 0.02], kind: 'web', wing: P })
        cubes.push({ name: tp + '_membrane', group: tp + '_web', from: [j[0], y - h2, fz(i)], to: [j[0] + f.d, y, fz(i) + 0.02], kind: 'web', wing: P })
      }
    })
    return { S, E, W }
  }
  const main = wing(o.main)
  const hind = wing(o.hind)
  const tail = o.tail
  let ty = tail.origin[1]
  let parent = null
  tail.segs.forEach((s, i) => {
    const g = 'tail_' + (i + 1)
    groups.push({ name: g, parent: parent || undefined, origin: [0, ty, tail.origin[2]], single: true })
    cubes.push({ name: g + '_body', group: g, from: [-s.w / 2, ty - s.len - 0.3, tail.origin[2] - s.d / 2], to: [s.w / 2, ty, tail.origin[2] + s.d / 2], kind: 'tail', index: i })
    if (i === tail.mid || i === tail.tip) {
      const big = i === tail.tip
      const fw = big ? tail.tipFin : tail.midFin
      for (const side of ['right', 'left']) {
        const sx = side === 'right' ? 1 : -1
        const fg = g + '_fin_' + side
        const ox = sx * s.w / 2
        groups.push({ name: fg, parent: g, origin: [ox, ty - 0.5, tail.origin[2]], rot: [0, 0, sx * (big ? -12 : -25)], single: true })
        const x0 = sx > 0 ? ox : ox - fw[0], x1 = sx > 0 ? ox + fw[0] : ox
        cubes.push({ name: fg + '_vane', group: fg, from: [x0, ty - 0.5 - fw[1], tail.origin[2] - 0.02], to: [x1, ty - 0.5, tail.origin[2]], kind: big ? 'tip_fin' : 'mid_fin', side, red: big && side === 'left' && tail.red })
      }
    }
    ty -= s.len
    parent = g
  })
  o.spines.forEach((sp, i) => {
    const g = 'spine_' + (i + 1)
    groups.push({ name: g, parent: sp.parent, origin: [0, sp.y, sp.z], rot: [sp.rot || -30, 0, 0], single: true })
    cubes.push({ name: g + '_plate', group: g, from: [-0.35, sp.y, sp.z], to: [0.35, sp.y + sp.h, sp.z + sp.l], kind: 'spine' })
    if (o.glowSpines) {
      groups.push({ name: g + '_glow', parent: g, origin: [0, sp.y, sp.z], single: true })
      cubes.push({ name: g + '_charge', group: g + '_glow', from: [-0.4, sp.y - 0.2, sp.z - 0.2], to: [0.4, sp.y + sp.h + 0.3, sp.z + sp.l + 0.3], kind: 'spine_glow' })
    }
  })
  return { main, hind, groups, cubes }
}
