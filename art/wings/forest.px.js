include("lib/rig_pack.js")
include("lib/feathers.js")
include("forest.rig.js")

pal({
  a: "#22461a", b: "#2e5a1e", c: "#3e7a26", d: "#56963a", e: "#74b04a", f: "#98c860", g: "#b8dc78",
  m: "#1c3a14",
  k: "#3a2816", K: "#4e3620", l: "#6a4a2c", L: "#8a6440", n: "#4e7a2a",
  p: "#d870a8", P: "#f0a0c8", r: "#f8c8e0", y: "#f8e070",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const leaf = (cube) => (x, row, dorsal, w, h) => {
  const t = (x + 0.5) / w
  const saw = (x % 2 === 0 ? 0.35 : 0)
  const half = h / 2 * Math.pow(Math.sin(Math.PI * Math.min(1, t * 1.05)), 0.75) - saw
  const d = Math.abs(row + 0.5 - h / 2)
  if (d > half) return null
  const light = (cube.index * 7 + (cube.wing === "branch" ? 0 : 3)) % 5
  const ramp = dorsal ? ["b", "c", "d", "e"] : ["d", "e", "f", "g"]
  if (d < 0.5 && t < 0.92) return dorsal ? "m" : "c"
  if (Math.abs(d - (x * 0.5) % 2) < 0.3 && t > 0.15 && t < 0.85) return dorsal ? "c" : "e"
  if (d > half - 0.7) return dorsal ? "a" : "c"
  const k = Math.min(3, Math.floor((1 - d / Math.max(0.5, half)) * 2) + (light > 2 ? 1 : 0))
  return ramp[k]
}

const paint = (c) => {
  for (const cube of R.cubes) {
    switch (cube.kind) {
      case "leaf": P.plate(c, cube, leaf(cube)); break
      case "wood": {
        P.box(c, cube, "n", "l", "k")
        const [u, v] = cube.uv
        const fw = Math.ceil(2 * ((cube.to[0] - cube.from[0]) + (cube.to[2] - cube.from[2])))
        for (let i = 1; i < fw; i += 3) c.px(u + i, v + 1, "K")
        for (let i = 2; i < fw; i += 5) c.px(u + i, v + 2, "L")
        break
      }
      case "stem": P.box(c, cube, "d", "c", "b"); break
      case "petals": {
        P.box(c, cube, "r", "P", "p")
        const [u, v] = cube.uv
        c.px(u + 1, v + 1, "r")
        c.px(u + 3, v + 1, "p")
        break
      }
      case "pollen": P.box(c, cube, "y", "y", "L"); break
      case "burl": P.box(c, cube, "n", "l", "k"); break
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("forest_wings", { w: R.texture[0], h: R.texture[1] }, paint)
