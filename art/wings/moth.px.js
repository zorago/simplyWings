include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("moth.rig.js")

pal({
  a: "#4a3826", b: "#6a5236", c: "#8a6e4a", d: "#a88c62", e: "#c4aa7c", f: "#dcc69a", g: "#efe0bc",
  k: "#20170f", r: "#7a3a1e", y: "#d8a640", Y: "#f0cc6a", w: "#f6eedc", p: "#c79a8a",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const shaped = (shape, paint) => (x, row, dorsal, w, h) => {
  const p = [x + 0.5, row + 0.5]
  if (!GEOM.inside(shape, p)) return null
  const edge = GEOM.edgeDist(shape, p)
  return paint(p, edge, dorsal)
}

const fore = shaped(R.cubes[0].shape, (p, edge, dorsal) => {
  const [x, y] = p
  if (edge < 0.8) return dorsal ? "a" : "c"
  if (!dorsal) return y < 2.5 ? "e" : (x + y) % 5 < 1 ? "e" : "f"
  if (Math.hypot(x - 13, y - 6) < 1.4) return "k"
  if (Math.hypot(x - 13, y - 6) < 2.3) return "y"
  const zig = Math.abs(((x * 0.9 + 100) % 3) - 1.5)
  if (Math.abs(y - (3 + x * 0.25 + zig)) < 0.55 && x > 3) return "a"
  if (Math.abs(y - (6.5 + x * 0.05 - zig * 0.6)) < 0.5 && x > 4 && x < 15) return "b"
  if (x > 17) return edge < 2 ? "b" : "c"
  if (y < 2.6) return "e"
  return x < 4 ? "c" : (Math.floor(x + y * 0.5) % 4 === 0 ? "d" : "e")
})

const hind = shaped(R.cubes[1].shape, (p, edge, dorsal) => {
  const [x, y] = p
  const r = Math.hypot(x - 7.5, y - 7)
  if (edge < 0.8) return dorsal ? "a" : "c"
  if (!dorsal) return r < 2 ? "e" : "f"
  if (r < 0.9) return "w"
  if (r < 2) return "k"
  if (r < 2.9) return "Y"
  if (r < 3.6) return "r"
  if (edge < 1.8) return "p"
  return x < 3 ? "c" : (Math.floor(x * 0.7 + y) % 3 === 0 ? "d" : "e")
})

const fur = (cube) => (c) => {
  P.box(c, cube, "g", "f", "e")
  const [u, v] = cube.uv
  for (let i = 0; i < 12; i++) c.px(u + (i * 7) % 18, v + (i * 3) % 6, i % 2 ? "e" : "g")
}

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "root") { P.box(c, cube, "e", "d", "c"); continue }
    if (cube.kind === "tuft") { fur(cube)(c); continue }
    P.plate(c, cube, cube.kind === "fore" ? fore : hind)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("moth_wings", { w: R.texture[0], h: R.texture[1] }, paint)
