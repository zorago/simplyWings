include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("beetle.rig.js")

pal({
  a: "#0b2a18", b: "#124024", c: "#1a5e32", d: "#24803f", e: "#3aa452", f: "#72d07a", F: "#b8f0a0",
  n: "#0f1a12", o: "#3a2a1a",
  m: "#5a3e22b8", M: "#6e5030a0", v: "#2e1e10e0", V: "#40301ccc", x: "#4a3a2a30", X: "#5a4a3a20",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const metal = (cube, dark) => (c) => {
  const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
  const fw = Math.ceil(2 * (w + d) - 1e-6), fh = Math.ceil(h + d - 1e-6)
  const [u, v] = cube.uv
  const tones = dark ? ["a", "b", "c", "b"] : ["b", "c", "d", "e", "d", "c"]
  for (let y = 0; y < fh; y++) {
    for (let x = 0; x < fw; x++) {
      let k = tones[x % tones.length]
      if (!dark && x % 3 === 1) k = "c"
      if (!dark && (x + Math.floor(y / 3)) % 11 === 4) k = "f"
      if (!dark && (x * 7 + y * 3) % 29 === 0) k = "F"
      if (y === fh - 1) k = "a"
      c.px(u + x, v + y, k)
    }
  }
}

const membrane = (offset, ghost) => (x, row, dorsal, w, h) => {
  const p = [x + offset + 0.5, row + 0.5]
  const shape = R.cubes.find(c => c.kind === "membrane").shape
  if (!GEOM.inside(shape, p)) return null
  if (ghost) return (x + row) % 3 ? "x" : "X"
  const edge = GEOM.edgeDist(shape, p)
  const [px, py] = p
  if (py < 1.8 && px < 15) return "v"
  if (edge < 0.7) return "V"
  if (Math.abs(py - (2.5 + px * 0.2)) < 0.5 && px > 2 && px < 14) return "V"
  if (Math.abs(py - (4.5 + px * 0.12)) < 0.45 && px > 4 && px < 13) return "V"
  if (Math.abs(px - 8) < 0.5 && py > 1.8) return "V"
  return (x + row) % 4 === 0 ? "M" : "m"
}

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "shell") { metal(cube, false)(c); continue }
    if (cube.kind === "shell_dark") { metal(cube, true)(c); continue }
    if (cube.kind === "suture") { P.box(c, cube, "e", "a", "n"); continue }
    if (cube.kind === "root") { P.box(c, cube, "c", "o", "n"); continue }
    P.plate(c, cube, membrane(cube.offset, cube.kind === "ghost"))
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("beetle_wings", { w: R.texture[0], h: R.texture[1] }, paint)
