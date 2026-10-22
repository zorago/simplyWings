include("lib/rig_geom.js")
include("lib/rig_pack.js")
include("ptero.rig.js")

pal({
  k: "#3a2410", e: "#4e3216", d: "#64421e", D: "#7a5226", m: "#8e6030", M: "#a6743c", L: "#ba8848",
  E: "#9a6a38", v: "#b07c44", V: "#c49052", n: "#d4a464", N: "#e2b878", P: "#eccb90",
  r: "#7c3a20", R: "#9a4c2a",
  b: "#a8926a", B: "#c8b28a", c: "#e2d0a8", C: "#f2e6c8",
  t: "#241a10", T: "#ece0c0",
  f: "#5a3418", F: "#7a4a26", g: "#9c6436", G: "#be844c",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const y = R.S[1]
const pt = (g, q) => GEOM.world(R, g, q)
const chain = [[R.S[0], y], [R.E[0], y], [R.W[0], y], [R.K[0], y]]
for (let i = 0; i < R.phal.length; i++) {
  const j = R.joints[i + 1]
  chain.push(pt("finger_" + (i + 1), [j[0], y]))
}
const tip = chain[chain.length - 1]
const body = [[R.hip[0] - 0.7, R.hip[1]], [R.S[0] - 0.7, 16], [R.S[0] - 0.7, y]]
const poly = [...chain, ...R.edge.slice(1), ...body]
const front = [[R.E[0] - 4.5, y], [R.E[0] - 4.5, y + 0.7], [R.E[0] - 1, y + 2.2], [R.E[0] + 3, y + 1.9], [R.W[0] + 1, y + 0.4], [R.W[0] + 1, y]]

const lineDist = (p, pts) => {
  let d = Infinity
  for (let i = 0; i + 1 < pts.length; i++) d = Math.min(d, GEOM.segDist(p, pts[i], pts[i + 1]))
  return d
}

const tone = (dorsal, p) => {
  const bd = lineDist(p, chain)
  const td = lineDist(p, R.edge)
  const span = p[0] / tip[0]
  if (td < 0.8) return dorsal ? "k" : "e"
  if (td < 1.7) return dorsal ? "e" : "E"
  if (bd < 1.0) return dorsal ? "d" : "v"
  const fiber = (bd * 0.95 + span * 1.3) % 2.4
  if (fiber < 0.55 && bd > 1.5 && td > 2) return dorsal ? "D" : "V"
  if (Math.hypot(p[0] - 24, p[1] - 16.5) < 1.6 || Math.hypot(p[0] - 11, p[1] - 14) < 1.2) return dorsal ? "r" : "n"
  const band = bd < 3.5 ? 2 : bd < 6.5 ? 1 : 0
  const ramp = dorsal ? ["m", "M", "L"] : ["n", "N", "P"]
  return ramp[Math.min(2, band + (span > 0.55 ? 0 : 0))]
}

const membrane = (c) => {
  for (const cube of R.cubes.filter(x => x.kind === "membrane")) {
    GEOM.pixels(R, cube, (x, yy, p, dorsal) => {
      if (GEOM.inside(poly, p)) c.px(x, yy, tone(dorsal, p))
    })
  }
  for (const cube of R.cubes.filter(x => x.kind === "propatagium")) {
    GEOM.pixels(R, cube, (x, yy, p, dorsal) => {
      if (!GEOM.inside(front, p)) return
      const ed = GEOM.edgeDist(front, p)
      c.px(x, yy, ed < 0.8 ? (dorsal ? "e" : "E") : (dorsal ? "M" : "N"))
    })
  }
}

const solids = {
  bone: ["c", "B", "b"], joint: ["C", "c", "B"], knuckle: ["C", "c", "b"], finger: ["c", "B", "b"],
  claw: ["T", "t", "t"], fuzz: ["G", "F", "f"]
}
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
    if (cube.kind === "claw") c.vline(u, v, fh, "T")
    if (cube.kind === "fuzz") for (const [x, yy] of [[1, 1], [3, 3], [6, 2], [8, 4], [11, 1], [13, 3], [2, 5], [9, 6]]) c.px(u + x % fw, v + yy % fh, x % 2 ? "g" : "G")
  }
}

const mannequin = (c) => {
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("ptero_wings", { w: R.texture[0], h: R.texture[1] }, membrane, boxes, mannequin)
