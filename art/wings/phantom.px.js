include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("phantom.rig.js")

pal({
  e: "#1a2138", d: "#222b48", D: "#2a3558", m: "#334068", M: "#3e4e7c",
  E: "#3a4870", v: "#4a5a86", V: "#566896", n: "#62759f",
  g: "#4f9a78", G: "#6cbf94", h: "#5c8a7a", H: "#86b8a4",
  b: "#4e6660", B: "#6b8780", c: "#8aa69c", k: "#1c2522",
  l: "#7dffc0c8", L: "#b4ffd8e0",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS
const tip = R.tip
const lead = [[R.S[0], 24.5], [R.E[0], 24.5], [R.M[0], 24.3], [tip[0], tip[1] + 0.6]]

const trailing = (seed) => {
  const base = R.trail
  const out = []
  let r = seed
  const rnd = () => { r = (r * 9301 + 49297) % 233280; return r / 233280 }
  for (let i = 0; i < base.length - 1; i++) {
    const a = base[i], b = base[i + 1]
    out.push(a)
    if (rnd() < 0.55) {
      const t = 0.3 + 0.4 * rnd()
      const x = a[0] + (b[0] - a[0]) * t, y = a[1] + (b[1] - a[1]) * t
      const depth = 1.2 + 2.2 * rnd()
      out.push([x + 0.6, y + 0.3], [x + 0.2 * rnd(), y + depth], [x - 0.6, y + 0.2])
    }
  }
  out.push(base[base.length - 1])
  return out
}

const poly = [...lead, ...trailing(7)]
const holes = [[16, 17.5, 0.9], [25, 19.6, 0.7]]
const veins = [
  [[4, 23.4], [9.5, 13.2]], [[11, 23.4], [16.5, 14.2]], [[19, 23.4], [24, 17.2]], [GEOM.world(R, "hand", [R.M[0] + 7, 23.4]), [29, 19.2]]
]
const veinDist = (p) => Math.min(...veins.map(([a, b]) => GEOM.segDist(p, a, b)))

const web = (glow) => (c) => {
  const sh = R.sheet
  for (const [u0, v0, dorsal] of [[sh.ventral[0], sh.ventral[1], false], [sh.dorsal[0], sh.dorsal[1], true]]) for (let u = 0; u < sh.w; u++) for (let v = 0; v < sh.h; v++) {
    const p = [sh.x + u + 0.5, sh.y - v - 0.5], x = u0 + u, y = v0 + v
    if (!GEOM.inside(poly, p)) continue
    if (holes.some(([hx, hy, hr]) => Math.hypot(p[0] - hx, p[1] - hy) < hr)) continue
    const vd = veinDist(p)
    const ed = GEOM.edgeDist(poly, p)
    if (glow) {
      if (vd < 0.45) c.px(x, y, dorsal ? "l" : "L")
      continue
    }
    let k
    if (vd < 0.45) k = dorsal ? "g" : "G"
    else if (ed < 0.8 && p[1] < 23) k = dorsal ? "h" : "H"
    else if (ed < 1.8 && p[1] < 23) k = dorsal ? "e" : "E"
    else k = dorsal ? (p[1] > 21 ? "M" : (Math.floor(p[0] * 0.5 + p[1]) % 4 === 0 ? "D" : "m")) : (p[1] > 21 ? "n" : "v")
    c.px(x, y, k)
  }
}

const bones = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") P.box(c, cube, "c", "B", "b")
    if (cube.kind === "knuckle") P.box(c, cube, "c", "b", "k")
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("phantom_wings", { w: R.texture[0], h: R.texture[1] }, web(false), bones)
sprite("phantom_glow", { w: R.texture[0], h: R.texture[1] }, web(true))
