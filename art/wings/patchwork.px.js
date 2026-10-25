include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("patchwork.rig.js")

pal({
  a: "#3e2616", b: "#5a3820", c: "#7a4e2c", d: "#966238",
  e: "#b8a888", f: "#d4c6a4", g: "#8a7a5a",
  r: "#7a2a22", R: "#a03a2c", u: "#2a4a6e", U: "#3e6690",
  m: "#5e6a3a", M: "#7a8a4a",
  s: "#e8dcb8", k: "#2a2018", w: "#eeeae0", W: "#cfc9bc", n: "#8a8a8a", N: "#a8a8a8",
  h: "#262428", H: "#3a3840", y: "#c8a050",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const quilt = (seed) => {
  let r = seed
  const rnd = () => { r = (r * 9301 + 49297) % 233280; return r / 233280 }
  const seeds = []
  for (let i = 0; i < 11; i++) seeds.push([2 + rnd() * 26, 8 + rnd() * 16, Math.floor(rnd() * 6)])
  const near = (p) => {
    let best = Infinity, second = Infinity, id = 0
    for (const [x, y, m] of seeds) {
      const d = Math.hypot(p[0] - x, (p[1] - y) * 1.2)
      if (d < best) { second = best; best = d; id = m } else if (d < second) second = d
    }
    return { id, seam: second - best < 0.5 }
  }
  return near
}

const material = (id, p, dorsal) => {
  const x = Math.floor(p[0]), y = Math.floor(p[1])
  switch (id) {
    case 0: return dorsal ? ((x + y) % 5 === 0 ? "b" : "c") : "d"
    case 1: return dorsal ? (y % 3 === 0 ? "e" : "f") : "f"
    case 2: return dorsal ? ((x + y) % 2 ? "r" : "R") : "R"
    case 3: return (Math.floor(x / 2) + Math.floor(y / 2)) % 2 ? "u" : "U"
    case 4: return dorsal ? (x % 2 ? "m" : "M") : "M"
    default: return dorsal ? "a" : "b"
  }
}

const paintSide = (side) => (c) => {
  const near = quilt(side === "right" ? 5 : 77)
  for (const cube of R.cubes.filter(c => c.leftUv && c.kind !== "primary")) {
    const uv = side === "right" ? cube.uv : cube.leftUv
    const sheet = cube.kind === "secondaries" || cube.kind === "tertials" || cube.kind === "hand_sheet" || cube.kind.endsWith("_feather")
    GEOM.pixels(R, { ...cube, uv }, (x, y, p, dorsal, col, row) => {
      const h = Math.round(cube.to[1] - cube.from[1])
      if (sheet) {
        const f = Math.floor(col / 3)
        const len = h - ((f * 7 + (side === "right" ? 1 : 2)) % 3)
        if (row >= len) return
      }
      const q = near(p)
      if (q.seam) { c.px(x, y, (col + row) % 3 === 0 ? "s" : "k"); return }
      c.px(x, y, material(q.id, p, dorsal))
    })
  }
  const kinds = side === "right" ? [0, 1, 2, 3, 0, 4] : [1, 0, 3, 2, 4, 1]
  for (const cube of R.cubes.filter(c => c.kind === "primary" && c.leftUv)) {
    const uv = side === "right" ? cube.uv : cube.leftUv
    P.plate(c, { ...cube, uv }, feather(kinds[(cube.index - 1) % kinds.length], cube.index))
  }
}

const feather = (type, k) => (x, row, dorsal, w, h) => {
  if (x === w - 1 && (row === 0 || row === h - 1)) return null
  if (x === w - 2 && row === h - 1) return null
  if (x >= 1 && x <= 2) return row % 2 ? "s" : "k"
  if (type === 0) return row === 0 ? "N" : row === h - 1 ? "h" : "n"
  if (type === 1) return row === 0 ? "H" : "h"
  if (type === 2) return row === 0 ? "w" : row === h - 1 ? "W" : "w"
  if (type === 3) return row === 0 ? "y" : (x % 3 ? "c" : "b")
  return row === 0 ? "N" : (x % 4 === 0 ? "H" : "n")
}

const parts = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") P.box(c, cube, "d", "c", "a")
    else if (cube.kind === "stick") P.box(c, cube, "f", "e", "g")
    else if (cube.kind === "bandage") {
      P.box(c, cube, "w", "W", "e")
      const [u, v] = cube.uv
      for (let i = 0; i < 8; i += 3) c.px(u + i, v + 2, "R")
    } else if (cube.kind === "string") P.box(c, cube, "f", "g", "k")
    else if (cube.kind === "flap") P.plate(c, cube, (x, row, dorsal) => row === 0 ? "s" : ((x + row) % 2 ? "U" : "u"))
    else if (cube.kind === "primary" && cube.only) P.plate(c, cube, feather(2, 7))
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("patchwork_wings", { w: R.texture[0], h: R.texture[1] }, paintSide("right"), paintSide("left"), parts)
