include("lib/rig_geom.js")
include("lib/rig_pack.js")
include("bat.rig.js")

pal({
  e: "#18121b", d: "#241b2a", D: "#2e2336", m: "#382b42", M: "#45354f",
  E: "#30253a", v: "#3f3249", V: "#4c3d57", n: "#594866", N: "#685574",
  k: "#120d14", b: "#21182a", B: "#33273b", c: "#47394f", C: "#5e4e66",
  f: "#2b1f1a", F: "#433027", g: "#5a4232", G: "#70543f",
  w: "#8f8576", W: "#d6cdbd", q: "#b9c0c8", Q: "#a3abb4"
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
const body = [R.S[0], 8.2]

const panels = {}
for (let i = 0; i < 3; i++) {
  const a = F[i], b = F[i + 1]
  const edge = [a.tip, ...GEOM.sag(a.tip, b.tip, 2.6 + 0.4 * i, W2, 8), b.tip]
  panels["finger_" + a.k] = { poly: [W2, a.j, ...edge, b.j], edge }
}
const last = F[3]
const back = [last.tip, ...GEOM.sag(last.tip, body, 3.4, [11, 22.5], 12), body]
const pat = { poly: [[R.S[0], R.S[1]], W2, last.j, ...back], edge: back }
const frontEdge = [[R.S[0], 25.3], [5, 25.2], [8, 24.7], [11, 24.2], [14, 23.7], [17, 23.3], [W2[0], 23]]
const front = { poly: [[R.S[0], R.S[1]], ...frontEdge, W2], edge: frontEdge }

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

const tone = (dorsal, p, panel) => {
  const bd = boneDist(p), td = lineDist(p, panel.edge)
  if (td < 0.85 && bd > 0.9) return dorsal ? "e" : "E"
  if (bd < 1.15) return dorsal ? "d" : "v"
  if (td < 2.1) return dorsal ? "M" : "N"
  if (td > 3.4 && td < 4.3 && bd > 1.8) return dorsal ? "D" : "V"
  if (bd < 2.2) return dorsal ? "D" : "V"
  return dorsal ? "m" : "n"
}

const membrane = (c) => {
  for (const cube of GEOM.plates(R)) {
    const panel = panelOf(cube)
    GEOM.pixels(R, cube, (x, y, p, dorsal) => {
      const inPoly = GEOM.inside(panel.poly, p)
      if (!inPoly && (GEOM.edgeDist(panel.poly, p) > 0.55 || boneDist(p) > 1.0)) return
      c.px(x, y, inPoly ? tone(dorsal, p, panel) : (dorsal ? "d" : "v"))
    })
  }
}

const solids = {
  shoulder_fur: ["G", "F", "f"], thumb_claw: ["W", "w", "w"], elbow: ["C", "c", "B"], wrist: ["C", "c", "B"]
}
const boxes = (c) => {
  for (const cube of R.cubes) {
    if (cube.from[2] === cube.to[2]) continue
    const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
    const fw = Math.ceil(2 * (w + d)), fh = Math.ceil(h + d)
    const [top, mid, low] = solids[cube.name] || (cube.name.includes("_tip") ? ["c", "B", "k"] : ["C", "B", "b"])
    const [u, v] = cube.uv
    c.rect(u, v, fw, fh, mid)
    c.hline(u, v, fw, top)
    c.hline(u, v + fh - 1, fw, low)
    if (cube.name === "shoulder_fur") for (const [x, y] of [[2, 2], [5, 1], [7, 3], [11, 2], [14, 3]]) c.px(u + x, v + y, "g")
  }
}

const mannequin = (c) => {
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("bat_wings", { w: R.texture[0], h: R.texture[1] }, membrane, boxes, mannequin)
