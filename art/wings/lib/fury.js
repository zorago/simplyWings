var FURY = (o) => {
  const groups = [], cubes = []
  const rad = Math.PI / 180
  const wing = (w) => {
    const P = w.prefix
    const y = w.S[1], z = w.S[2]
    const S = w.S, E = [S[0] + w.arm, y, z], W = [E[0] + w.forearm, y, z]
    groups.push({ name: P + 'shoulder', origin: S, rot: [0, 0, w.rot || 0] })
    groups.push({ name: P + 'arm', parent: P + 'shoulder', origin: S })
    if (!o.skin) groups.push({ name: P + 'arm_membrane', parent: P + 'arm', origin: S })
    groups.push({ name: P + 'forearm', parent: P + 'arm', origin: E })
    if (!o.skin) groups.push({ name: P + 'forearm_membrane', parent: P + 'forearm', origin: E })
    groups.push({ name: P + 'hand', parent: P + 'forearm', origin: W })
    groups.push({ name: P + 'thumb', parent: P + 'hand', origin: W, rot: [0, 0, 50] })
    const fz = (i) => z + 0.18 - 0.06 * i
    if (!o.skin) {
      cubes.push({ name: P + 'arm_web', group: P + 'arm_membrane', from: [S[0], y - w.armDepth, z - 0.05], to: [E[0], y, z - 0.03], kind: 'web', wing: P })
      cubes.push({ name: P + 'forearm_web', group: P + 'forearm_membrane', from: [E[0], y - w.armDepth, z + 0.02], to: [W[0], y, z + 0.04], kind: 'web', wing: P })
      cubes.push({ name: P + 'arm_front', group: P + 'arm_membrane', from: [S[0], y, z - 0.05], to: [E[0], y + w.front, z - 0.03], kind: 'web', wing: P })
      cubes.push({ name: P + 'forearm_front', group: P + 'forearm_membrane', from: [E[0], y, z + 0.02], to: [W[0], y + w.front * 0.7, z + 0.04], kind: 'web', wing: P })
    }
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
      if (n && !o.skin) {
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

var FURY_SKIN = (R) => {
  const panels = []
  const sheets = {}
  let u = 0
  const wings = [['', R.main, R.hind.S[1] + 0.5, 3], ['hind_', R.hind, R.hind.S[1] - 7, 1.5]]
  for (const [P, base, body, front] of wings) {
    const desc = { ...base, front }
    const y = desc.S[1], z = desc.S[2]
    const at = (bone, grp, x, yy) => {
      const p = GEOM.world(R, P + grp, [x, yy === undefined ? y : yy])
      return [P + bone, Math.round(p[0] * 1000) / 1000, Math.round(p[1] * 1000) / 1000, z]
    }
    const isFinger = (name) => name.startsWith(P + 'finger_') && !isNaN(Number(name.slice(P.length + 7))) && name.length > P.length + 7
    const fingers = R.groups.filter(g => isFinger(g.name)).map(g => {
      const k = g.name.slice(P.length)
      const tipG = R.groups.find(x => x.name === P + k + '_tip')
      const tipCube = R.cubes.find(c => c.name === P + k + '_tip_bone')
      const j = tipG.origin[0], e = tipCube.to[0]
      return [at('hand', 'hand', desc.W[0]), at(k, k, (desc.W[0] + j) / 2), at(k + '_tip', k + '_tip', j),
        at(k + '_tip', k + '_tip', (j + e) / 2), at(k + '_tip', k + '_tip', e)]
    })
    const S = at('arm', 'arm', desc.S[0])
    const last = fingers[fingers.length - 1]
    const rail = [S, at('forearm', 'forearm', desc.E[0]), at('hand', 'hand', desc.W[0]), last[2], last[4]]
    const flank = [S, ...[0, 1 / 3, 2 / 3, 1].map(t => ['root', Math.round((desc.S[0] + 0.4 + 1.2 * t) * 1000) / 1000, Math.round((y - 2.5 - (y - 2.5 - body) * t) * 1000) / 1000, z - 0.1])]
    const list = [{ name: P + 'plagiopatagium', rows: 3, a: flank, b: rail }]
    for (let i = 0; i + 1 < fingers.length; i++) list.push({ name: P + 'dactylopatagium_' + (i + 1), rows: 2, a: fingers[i], b: fingers[i + 1] })
    const mid1 = (desc.S[0] + desc.E[0]) / 2, mid2 = (desc.E[0] + desc.W[0]) / 2
    list.push({
      name: P + 'propatagium', rows: 1,
      a: [S, at('arm', 'arm', mid1), at('forearm', 'forearm', desc.E[0]), at('forearm', 'forearm', mid2), at('hand', 'hand', desc.W[0])],
      b: [at('shoulder', 'shoulder', desc.S[0], y + desc.front), at('arm', 'arm', mid1, y + desc.front * 0.95),
        at('forearm', 'forearm', desc.E[0], y + desc.front * 0.8), at('forearm', 'forearm', mid2, y + desc.front * 0.5), at('hand', 'hand', desc.W[0])]
    })
    const pts = list.flatMap(q => [...q.a, ...q.b])
    const x0 = Math.floor(Math.min(...pts.map(q => q[1])) - 1), x1 = Math.ceil(Math.max(...pts.map(q => q[1])) + 1)
    const y0 = Math.ceil(Math.max(...pts.map(q => q[2])) + 1), y1 = Math.floor(Math.min(...pts.map(q => q[2])) - 1)
    const w = x1 - x0, h = y0 - y1
    const sheet = { x: x0, y: y0, w, h, ventral: [u, R.used], dorsal: [u + w, R.used] }
    u += 2 * w + 1
    sheets[P] = sheet
    for (const q of list) panels.push({ ...q, sheet: { x: sheet.x, y: sheet.y, ventral: sheet.ventral, dorsal: sheet.dorsal } })
  }
  return { sheet: panels[0].sheet, panels, sheets }
}