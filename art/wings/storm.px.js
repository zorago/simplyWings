include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("storm.rig.js")

pal({
  w: "#ffffff", W: "#e4f6ff", c: "#a8e2ff", C: "#78c4ff",
  a: "#b4e0ffd8", b: "#7cb4ffc8", d: "#6a7cf0bc", e: "#6a4ad8b0", f: "#4c2aa8a0", g: "#34187888",
  v: "#f4fbffe8",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS
const hash = (x, y, s) => {
  const v = Math.sin(x * 12.9898 + y * 78.233 + s * 37.719) * 43758.5453
  return v - Math.floor(v)
}
const tri = (t) => Math.abs((t % 2 + 2) % 2 - 1)

const bolts = (w, h, seed, count) => {
  const set = new Set()
  const walk = (x, row, len, s, depth) => {
    for (let i = 0; i < len && row < h; i++) {
      set.add(Math.round(x) + "," + row)
      const r = hash(i, row, s)
      x += r < 0.33 ? -1 : r > 0.66 ? 1 : 0
      row += 1
      if (depth < 1 && hash(row, i, s + 9) > 0.93) walk(x, row, Math.floor(len * 0.5), s + 3.3, depth + 1)
    }
  }
  for (let k = 0; k < count; k++) walk(2 + (k + 0.5) * (w - 4) / count + hash(k, 1, seed) * 2, 1, h, seed + k * 5.1, 0)
  return set
}

const plasma = (row, x, depth, edge, seed, veins) => {
  if (veins && veins.has(x + "," + row) && edge > 0.8 && depth > 0.18) return (x + row) % 3 === 0 ? "v" : "c"
  if (edge < 0.9) return "f"
  const t = depth
  return t < 0.12 ? "a" : t < 0.32 ? "b" : t < 0.55 ? "d" : t < 0.78 ? "e" : "f"
}

const plume = (cube) => {
  const w = Math.round(cube.to[0] - cube.from[0]), h = Math.round(cube.to[1] - cube.from[1])
  const seed = 1.3 + cube.index * 2.1
  const veins = cube.index < 4 ? new Set() : bolts(w, h, seed, 1)
  return (x, row, dorsal) => {
    const t = row / h
    const half = w / 2 * (t < 0.7 ? 1 : Math.max(0.2, 1 - (t - 0.7) * 2.6))
    if (Math.abs(x + 0.5 - w / 2) > half) return null
    const bottom = h - 1 - 1.5 * hash(x, 0, seed)
    if (row > bottom) return null
    if (row === 0) return "a"
    return plasma(row, x, t, Math.min(bottom - row, half - Math.abs(x + 0.5 - w / 2) + 1), seed, veins)
  }
}

const paint = (c) => {
  for (const cube of R.cubes) {
    switch (cube.kind) {
      case "plume": P.plate(c, cube, plume(cube)); break
      case "bone": P.box(c, cube, "w", "W", "c"); break
      case "bolt": P.box(c, cube, "w", "W", "C"); break
      case "arc": P.box(c, cube, "w", "w", "W"); break
      case "core": P.box(c, cube, "w", "w", "W"); break
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("storm_wings", { w: R.texture[0], h: R.texture[1] }, paint)
