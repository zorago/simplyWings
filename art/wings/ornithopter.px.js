include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("ornithopter.rig.js")

pal({
  a: "#3e2a17", b: "#5a3d22", c: "#7a5530", d: "#9a6e3e", e: "#b88a52",
  k: "#8a7652", K: "#a8946c", l: "#c4b28a", L: "#d8c8a4", m: "#e8dcc0", M: "#f2e9d4",
  r: "#6e5e40", R: "#9a8a62", y: "#8a6a24", Y: "#c49a3a", Z: "#e8c868",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const tip = GEOM.world(R, "hand", [R.W[0] + 11, 22.5])
const ribEnd = ([g, x, b, t]) => GEOM.world(R, g, GEOM.rotate([x, b], [x, 22.5], t))
const ribPts = R.ribs.map(ribEnd)
const trail = [[tip[0], tip[1] - 0.2], ...ribPts.slice().reverse(), [R.S[0], 8.4]]
const curved = []
for (let i = 0; i + 1 < trail.length; i++) {
  curved.push(trail[i], ...GEOM.sag(trail[i], trail[i + 1], 1.4, [12, 23], 5))
}
curved.push(trail[trail.length - 1])
const panel = [[R.S[0], 22.6], [tip[0], tip[1] + 0.1], ...curved]
const ribLines = R.ribs.map((r) => [GEOM.world(R, r[0], [r[1], 22.5]), ribEnd(r)])
const nearRib = (p) => Math.min(...ribLines.map(([a, b]) => GEOM.segDist(p, a, b)))

const canvas = (c) => {
  for (const cube of R.cubes.filter(c => c.kind === "canvas")) {
    GEOM.pixels(R, cube, (x, y, p, dorsal) => {
      if (!GEOM.inside(panel, p)) return
      const ed = GEOM.edgeDist(panel, p)
      const rd = nearRib(p)
      let k
      if (ed < 0.8 && p[1] < 22) k = dorsal ? "k" : "K"
      else if (rd < 0.9) k = (Math.round(p[1]) % 2 === 0) ? (dorsal ? "k" : "K") : (dorsal ? "K" : "l")
      else if (ed < 1.8 && p[1] < 22) k = dorsal ? "K" : "L"
      else k = dorsal ? (Math.floor(p[1]) % 3 === 0 ? "l" : "L") : (Math.floor(p[1]) % 3 === 0 ? "L" : "m")
      c.px(x, y, k)
    })
  }
}

const wood = (cube) => (c) => {
  const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
  const fw = Math.ceil(2 * (w + d) - 1e-6), fh = Math.ceil(h + d - 1e-6)
  const [u, v] = cube.uv
  for (let j = 0; j < fh; j++) for (let i = 0; i < fw; i++) {
    let k = (i + j * 3) % 7 === 0 ? "b" : ((i + j) % 5 === 0 ? "e" : "d")
    if (j === fh - 1) k = "b"
    if (j === 0) k = "e"
    c.px(u + i, v + j, k)
  }
}

const paint = (c) => {
  canvas(c)
  for (const cube of R.cubes) {
    if (cube.kind === "wood") wood(cube)(c)
    else if (cube.kind === "brass") P.box(c, cube, "Z", "Y", "y")
    else if (cube.kind === "rope") P.box(c, cube, "R", "r", "a")
    else if (cube.kind === "pulley") {
      P.box(c, cube, "Y", "c", "a")
      const [u, v] = cube.uv
      c.px(u + 2, v + 2, "Z"); c.px(u + 6, v + 2, "Z")
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("ornithopter_wings", { w: R.texture[0], h: R.texture[1] }, paint)
