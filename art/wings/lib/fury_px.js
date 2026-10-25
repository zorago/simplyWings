var FURY_PAINT = (R, T) => {
  const lineDist = (p, pts) => {
    let d = Infinity
    for (let i = 0; i + 1 < pts.length; i++) d = Math.min(d, GEOM.segDist(p, pts[i], pts[i + 1]))
    return d
  }
  const hash = (x, y, s) => {
    const v = Math.sin(x * 12.9898 + y * 78.233 + s * 37.719) * 43758.5453
    return v - Math.floor(v)
  }
  const wingData = (P, desc) => {
    const g = (n) => P + n
    const y = desc.S[1]
    const pt = (grp, x, yy) => GEOM.world(R, grp, [x, yy === undefined ? y : yy])
    const W2 = pt(P + 'hand', desc.W[0])
    const S2 = pt(P + 'arm', desc.S[0])
    const E2 = pt(P + 'forearm', desc.E[0])
    const fingers = []
    R.groups.filter(q => new RegExp('^' + P + 'finger_\\d$').test(q.name)).forEach((q) => {
      const k = q.name.slice(P.length)
      const tipG = R.groups.find(x => x.name === P + k + '_tip')
      const tipCube = R.cubes.find(c => c.name === P + k + '_tip_bone')
      fingers.push({ k, j: pt(P + k + '_tip', tipG.origin[0]), tip: pt(P + k + '_tip', tipCube.to[0]) })
    })
    const bones = [[S2, E2], [E2, W2]]
    for (const f of fingers) bones.push([W2, f.j], [f.j, f.tip])
    const panels = {}
    for (let i = 0; i + 1 < fingers.length; i++) {
      const a = fingers[i], b = fingers[i + 1]
      const edge = [a.tip, ...GEOM.sag(a.tip, b.tip, T.sag + 0.4 * i, W2, 8), b.tip]
      panels[a.k] = { poly: [W2, a.j, ...edge, b.j], edge }
    }
    const last = fingers[fingers.length - 1]
    const body = [desc.S[0], desc.body]
    const back = [last.tip, ...GEOM.sag(last.tip, body, T.sag + 0.6, E2, 12), body]
    const pat = { poly: [S2, E2, W2, last.j, ...back, [desc.S[0], y - 2.5]], edge: back }
    const fe = [pt(P + 'shoulder', desc.S[0], y + desc.front), pt(P + 'arm', (desc.S[0] + desc.E[0]) / 2, y + desc.front * 0.95),
      pt(P + 'forearm', desc.E[0], y + desc.front * 0.8), pt(P + 'forearm', (desc.E[0] + desc.W[0]) / 2, y + desc.front * 0.5)]
    const front = { poly: [S2, ...fe, W2, E2], edge: fe }
    return { bones, panels, pat, front }
  }
  const data = {
    '': wingData('', { ...R.main, body: R.cubes.some(q => q.kind === 'web') ? R.main.S[1] - 14.5 : R.hind.S[1] + 0.5, front: 3 }),
    'hind_': wingData('hind_', { ...R.hind, body: R.hind.S[1] - 7, front: 1.5 })
  }
  const panelOf = (cube) => {
    const d = data[cube.wing]
    if (cube.name.endsWith('_front')) return [d, d.front]
    if (cube.group.endsWith('arm_membrane')) return [d, d.pat]
    const k = /(finger_\d)/.exec(cube.group)[1]
    return [d, d.panels[k]]
  }
  const tone = (dorsal, p, panel, d, glow) => {
    const bd = Math.min(...d.bones.map(([a, b]) => GEOM.segDist(p, a, b)))
    const td = lineDist(p, panel.edge)
    if (glow) return T.glowEdge && td < 0.9 && bd > 0.9 ? T.glowEdge : null
    if (td < 0.9 && bd > 0.9) return dorsal ? T.edge : T.edgeV
    if (bd < 1.1) return dorsal ? T.nearBone : T.nearBoneV
    const scale = (Math.floor(p[0] * 0.8) + Math.floor(p[1] * 0.8)) % 3 === 0 && hash(Math.floor(p[0]), Math.floor(p[1]), 4) > 0.55
    if (scale) return dorsal ? T.scale : T.scaleV
    if (td < 2.2) return dorsal ? T.rim : T.rimV
    return dorsal ? (bd > 4.5 ? T.deep : T.mid) : (bd > 4.5 ? T.deepV : T.midV)
  }
  const membranes = (glow) => (c) => {
    for (const cube of R.cubes.filter(q => q.kind === 'web')) {
      const [d, panel] = panelOf(cube)
      GEOM.pixels(R, cube, (x, yy, p, dorsal) => {
        const inPoly = GEOM.inside(panel.poly, p)
        const bd = Math.min(...d.bones.map(([a, b]) => GEOM.segDist(p, a, b)))
        if (!inPoly && (GEOM.edgeDist(panel.poly, p) > 0.55 || bd > 1.0)) return
        const t = inPoly ? tone(dorsal, p, panel, d, glow) : (glow ? null : (dorsal ? T.nearBone : T.nearBoneV))
        if (t) c.px(x, yy, t)
      })
    }
  }
  const skinSheets = (glow) => (c) => {
    if (R.cubes.some(q => q.kind === 'web')) return
    const skin = FURY_SKIN(R)
    for (const P of Object.keys(skin.sheets)) {
      const sh = skin.sheets[P]
      const d = data[P]
      const list = [d.front, d.pat, ...Object.values(d.panels)]
      for (const [u0, v0, dorsal] of [[sh.ventral[0], sh.ventral[1], false], [sh.dorsal[0], sh.dorsal[1], true]]) {
        for (let u = 0; u < sh.w; u++) {
          for (let v = 0; v < sh.h; v++) {
            const p = [sh.x + u + 0.5, sh.y - v - 0.5]
            const panel = list.find(q => GEOM.inside(q.poly, p))
            const bd = Math.min(...d.bones.map(([a, b]) => GEOM.segDist(p, a, b)))
            const t = panel ? tone(dorsal, p, panel, d, glow) : (!glow && bd < 0.9 ? (dorsal ? T.nearBone : T.nearBoneV) : null)
            if (t) c.px(u0 + u, v0 + v, t)
          }
        }
      }
    }
  }
  const box = (c, cube, top, mid, low, speck) => {
    const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], dd = cube.to[2] - cube.from[2]
    const fw = Math.ceil(2 * (w + dd) - 1e-6), fh = Math.ceil(h + dd - 1e-6)
    const [u, v] = cube.uv
    c.rect(u, v, fw, fh, mid)
    c.hline(u, v, fw, top)
    c.hline(u, v + fh - 1, fw, low)
    if (speck) for (let i = 0; i < fw * fh / 6; i++) c.px(u + Math.floor(hash(i, 1, u) * fw), v + Math.floor(hash(i, 2, v) * fh), speck)
  }
  const plate = (c, cube) => {
    const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], dd = cube.to[2] - cube.from[2]
    const fw = Math.ceil(2 * (w + dd) - 1e-6), fh = Math.ceil(h + dd - 1e-6)
    const [u, v] = cube.uv
    for (let x = 0; x < fw; x++) {
      for (let y = 0; y < fh; y++) {
        const row = Math.floor(y / 2)
        const col = Math.floor((x + (row % 2) * 2) / 4)
        const lx = (x + (row % 2) * 2) % 4
        const ly = y % 2
        let t = (col + row) % 3 === 0 ? T.skinSpeck : T.skin
        if (ly === 1 && (lx === 0 || lx === 3)) t = T.skinLow
        else if (ly === 0 && (lx === 1 || lx === 2)) t = T.skinTop
        c.px(u + x, v + y, t)
      }
    }
  }
  const fin = (cube) => (x, row, dorsal, w, h) => {
    const t = row / Math.max(1, h - 1)
    const outer = cube.side === 'right' ? x / (w - 1) : 1 - x / (w - 1)
    const lim = 1 - 0.55 * Math.pow(outer, 1.4)
    if (t > lim) return null
    const edge = t > lim - 0.18 || (outer > 0.92)
    if (cube.red) {
      const cx = w / 2, cy = h * 0.4
      const mark = Math.abs(x + 0.5 - cx) < 1.2 && Math.abs(row + 0.5 - cy) < 1.2 && !(Math.abs(x + 0.5 - cx) < 0.5 && Math.abs(row + 0.5 - cy) < 0.5)
      if (mark && dorsal) return T.mark
      return edge ? T.redEdge : T.red
    }
    return edge ? (dorsal ? T.edge : T.edgeV) : (dorsal ? T.fin : T.finV)
  }
  const base = (c) => {
    membranes(false)(c)
    skinSheets(false)(c)
    for (const cube of R.cubes) {
      if (cube.kind === 'skin') box(c, cube, T.skinTop, T.skin, T.skinLow, T.skinSpeck)
      else if (cube.kind === 'claw') box(c, cube, T.clawTop, T.claw, T.claw)
      else if (cube.kind === 'tail') box(c, cube, T.skinTop, T.skin, T.skinLow, T.skinSpeck)
      else if (cube.kind === 'plate') plate(c, cube)
      else if (cube.kind === 'spine') box(c, cube, T.spineTop, T.spine, T.spineLow)
      else if (cube.kind === 'mid_fin' || cube.kind === 'tip_fin') FEATHERS.plate(c, cube, fin(cube))
    }
    c.rect(0, R.texture[1] - 16, 32, 16, "q")
    for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
  }
  const glow = (c) => {
    membranes(true)(c)
    skinSheets(true)(c)
    for (const cube of R.cubes) if (cube.kind === 'spine_glow') box(c, cube, T.glowHi, T.glow, T.glow)
  }
  return { base, glow }
}
