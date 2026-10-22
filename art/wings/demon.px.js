include("lib/rig_geom.js")
include("lib/rig_pack.js")
include("demon.rig.js")

pal({
  e: "#140505", d: "#2a0808", D: "#3a0c0c", m: "#4e1010", M: "#641616", L: "#7a1c1a",
  E: "#3a0e0c", v: "#5a1414", V: "#742020", n: "#8a2a24", N: "#a03a2c",
  x: "#0c0303",
  k: "#100808", b: "#1e1010", B: "#2e1818", c: "#4a2a26", C: "#6a3c34",
  h: "#2a1a14", H: "#c8b090", i: "#e6d6b8",
  g: "#ff6a1a", G: "#ffb050", r: "#d8380c",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const pt = (g, x, y) => GEOM.world(R, g, [x, y])
const W2 = [R.W[0], R.W[1]]
const F = R.fingers.map(f => {
  const g = "finger_" + f.k
  return { ...f, j: pt(g, R.W[0] + f.p, R.W[1]), tip: pt(g + "_tip", R.W[0] + f.p + f.d, R.W[1]) }
})
const bones = [[[R.S[0], R.S[1]], [R.E[0], R.E[1]]], [[R.E[0], R.E[1]], W2]]
for (const f of F) bones.push([W2, f.j], [f.j, f.tip])
const body = [R.S[0], 7.2]

const panels = {}
for (let i = 0; i < 3; i++) {
  const a = F[i], b = F[i + 1]
  const edge = [a.tip, ...GEOM.sag(a.tip, b.tip, 3.2 + 0.5 * i, W2, 8), b.tip]
  panels["finger_" + a.k] = { poly: [W2, a.j, ...edge, b.j], edge }
}
const last = F[3]
const back = [last.tip, ...GEOM.sag(last.tip, body, 4, [13, 22.5], 12), body]
const pat = { poly: [[R.S[0], R.S[1]], W2, last.j, ...back], edge: back }
const frontEdge = [[R.S[0], 25.3], [6, 25.1], [10, 24.6], [14, 24.1], [18, 23.6], [21, 23.2], [W2[0], 23]]
const front = { poly: [[R.S[0], R.S[1]], ...frontEdge, W2], edge: frontEdge }
const holes = [[16.5, 11.5, 1.1], [29, 9.5, 0.9], [34.5, 16, 0.8], [7.5, 10, 0.8]]

const panelOf = (cube) => {
  if (cube.name.endsWith("_front")) return front
  if (cube.group.endsWith("membrane")) return pat
  return panels["finger_" + /^finger_(\d)/.exec(cube.group)[1]]
}

const lineDist = (p, pts) => {
  let d = Infinity
  for (let i = 0; i + 1 < pts.length; i++) d = Math.min(d, GEOM.segDist(p, pts[i], pts[i + 1]))
  return d
}
const boneDist = (p) => Math.min(...bones.map(([a, b]) => GEOM.segDist(p, a, b)))
const vein = (p) => {
  let best = Infinity
  for (const [a, b] of bones) {
    const mx = (a[0] + b[0]) / 2, my = (a[1] + b[1]) / 2
    const dx = b[0] - a[0], dy = b[1] - a[1], l = Math.hypot(dx, dy) || 1
    const nx = dy / l, ny = -dx / l
    for (let k = 1; k <= 2; k++) {
      const c = [mx + nx * 3.5 * k * 0.6, my + ny * 3.5 * k * 0.6]
      best = Math.min(best, GEOM.segDist(p, [mx, my], c))
    }
  }
  return best
}

const tone = (dorsal, p, panel, glow) => {
  const bd = boneDist(p), td = lineDist(p, panel.edge)
  if (glow) {
    if (td < 0.9 && bd > 0.9) return "G"
    if (td < 1.8 && bd > 0.9) return "g"
    if (td < 3.2 && vein(p) < 0.5) return "r"
    return null
  }
  if (td < 0.9 && bd > 0.9) return dorsal ? "e" : "E"
  if (bd < 1.2) return dorsal ? "d" : "v"
  if (vein(p) < 0.5) return dorsal ? "x" : "D"
  if (td < 2.2) return dorsal ? "D" : "V"
  if (bd < 2.4) return dorsal ? "M" : "N"
  return dorsal ? (bd > 5 ? "m" : "L") : (bd > 5 ? "V" : "n")
}

const membrane = (glow) => (c) => {
  for (const cube of GEOM.plates(R)) {
    const panel = panelOf(cube)
    GEOM.pixels(R, cube, (x, y, p, dorsal) => {
      if (holes.some(([hx, hy, hr]) => Math.hypot(p[0] - hx, p[1] - hy) < hr)) return
      const inPoly = GEOM.inside(panel.poly, p)
      if (!inPoly && (GEOM.edgeDist(panel.poly, p) > 0.55 || boneDist(p) > 1.0)) return
      const t = inPoly ? tone(dorsal, p, panel, glow) : (glow ? null : (dorsal ? "d" : "v"))
      if (t) c.px(x, y, t)
    })
  }
}

const solids = { bone: ["c", "B", "b"], knuckle: ["C", "c", "B"], horn: ["i", "H", "h"], carapace: ["C", "B", "k"] }
const boxes = (c) => {
  for (const cube of R.cubes) {
    const s = solids[cube.kind]
    if (!s) continue
    const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
    const fw = Math.ceil(2 * (w + d) - 1e-6), fh = Math.ceil(h + d - 1e-6)
    const [u, v] = cube.uv
    c.rect(u, v, fw, fh, s[1])
    c.hline(u, v, fw, s[0])
    c.hline(u, v + fh - 1, fw, s[2])
    if (cube.kind === "horn") for (let i = Math.floor(fw / 2); i < fw; i++) c.px(u + i, v + Math.floor(fh / 2), "h")
    if (cube.kind === "carapace") for (let i = 1; i < fw; i += 3) c.px(u + i, v + 2, "r")
  }
}

const glowBoxes = (c) => {
  for (const cube of R.cubes) if (cube.kind === "carapace") {
    const [u, v] = cube.uv
    const w = cube.to[0] - cube.from[0], d = cube.to[2] - cube.from[2]
    const fw = Math.ceil(2 * (w + d) - 1e-6)
    for (let i = 1; i < fw; i += 3) c.px(u + i, v + 2, "g")
  }
}

const mannequin = (c) => {
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("demon_wings", { w: R.texture[0], h: R.texture[1] }, membrane(false), boxes, mannequin)
sprite("demon_glow", { w: R.texture[0], h: R.texture[1] }, membrane(true), glowBoxes)
