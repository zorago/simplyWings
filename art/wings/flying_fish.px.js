include("lib/rig_pack.js")
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

const ray = (band) => (x, row, dorsal, w, h) => {
  const mid = Math.floor(h / 2) - (h % 2 ? 0 : 1)
  if (x === w - 1 && row !== mid) return null
  if (x >= w - 2 && (row === 0 || row === h - 1)) return null
  if (row === mid) return x < 3 ? "r" : "R"
  const tipBand = band && x >= w * 0.62 && x <= w * 0.8
  if (row === 0 || row === h - 1) return tipBand ? "B" : "e"
  if (tipBand) return "b"
  return x < 4 ? "n" : (x + row) % 5 === 0 ? "M" : "m"
}

const scales = (cube) => (c) => {
  P.box(c, cube, "T", "S", "s")
  const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
  const fw = Math.ceil(2 * (w + d)), fh = Math.ceil(h + d)
  const [u, v] = cube.uv
  for (let y = 1; y < fh - 1; y++) for (let x = 0; x < fw; x++) if ((x + y * 2) % 4 === 0) c.px(u + x, v + y, "t")
}

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "root") { scales(cube)(c); continue }
    P.plate(c, cube, ray(cube.kind === "ray"))
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("flying_fish_wings", { w: R.texture[0], h: R.texture[1] }, paint)
