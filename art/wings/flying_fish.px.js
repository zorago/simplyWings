include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("flying_fish.rig.js")

pal({
  m: "#8cc6e690", M: "#a6d8f080", n: "#6aa6d0a0", e: "#d2ecf8b0", r: "#2e5c82e8", R: "#4a7ca6d0",
  b: "#24486ab0", B: "#33608aa0",
  s: "#5e7f9c", S: "#8fb0c8", t: "#c6dceb", T: "#e8f4fa", k: "#2c3e52",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const fin = (sh, band) => (c) => {
  const rays = sh.rays
  const reach = (p) => {
    const a = Math.atan2(p[1] - sh.O[1], p[0] - sh.O[0])
    let best = null
    for (let i = 0; i + 1 < rays.length; i++) {
      const a0 = Math.atan2(rays[i][1][1] - sh.O[1], rays[i][1][0] - sh.O[0])
      const a1 = Math.atan2(rays[i + 1][1][1] - sh.O[1], rays[i + 1][1][0] - sh.O[0])
      if (a <= a0 + 0.02 && a >= a1 - 0.02) {
        const t = (a0 - a) / ((a0 - a1) || 1)
        const l0 = Math.hypot(rays[i][1][0] - sh.O[0], rays[i][1][1] - sh.O[1])
        const l1 = Math.hypot(rays[i + 1][1][0] - sh.O[0], rays[i + 1][1][1] - sh.O[1])
        best = l0 + (l1 - l0) * t - 0.8 * Math.sin(Math.PI * t)
      }
    }
    return best
  }
  for (const [u0, v0, dorsal] of [[sh.ventral[0], sh.ventral[1], false], [sh.dorsal[0], sh.dorsal[1], true]]) for (let u = 0; u < sh.w; u++) for (let v = 0; v < sh.h; v++) {
    const p = [sh.x + u + 0.5, sh.y - v - 0.5], x = u0 + u, y = v0 + v
    const L = reach(p)
    if (L === null) continue
    const d = Math.hypot(p[0] - sh.O[0], p[1] - sh.O[1])
    if (d > L) continue
    const rd = Math.min(...rays.map(([a, b]) => GEOM.segDist(p, a, b)))
    const f = d / L
    let k
    if (rd < 0.5) k = d < 3 ? "r" : "R"
    else if (L - d < 0.8) k = band && f > 0.6 ? "B" : "e"
    else if (band && f > 0.62 && f < 0.8) k = "b"
    else k = d < 4 ? "n" : (Math.floor(d * 1.3 + u) % 5 === 0 ? "M" : "m")
    c.px(x, y, k)
  }
}

const scales = (cube) => (c) => {
  P.box(c, cube, "T", "S", "s")
  const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
  const fw = Math.ceil(2 * (w + d) - 1e-6), fh = Math.ceil(h + d - 1e-6)
  const [u, v] = cube.uv
  for (let y = 1; y < fh - 1; y++) for (let x = 0; x < fw; x++) if ((x + y * 2) % 4 === 0) c.px(u + x, v + y, "t")
}

const paint = (c) => {
  fin(R.sheets.pectoral, true)(c)
  fin(R.sheets.pelvic, false)(c)
  for (const cube of R.cubes) {
    if (cube.kind === "root") scales(cube)(c)
    else if (cube.kind === "spine") P.box(c, cube, "R", "r", "r")
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("flying_fish_wings", { w: R.texture[0], h: R.texture[1] }, paint)
