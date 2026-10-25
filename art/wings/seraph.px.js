include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("seraph.rig.js")

pal({
  K: "#b88a2c", k: "#d0a440", j: "#e4bc58", J: "#f0d47c", i: "#f8e8a8", I: "#fff8dc",
  s: "#c89430", S: "#fff0c0",
  g: "#d8b060", G: "#e8c878", h: "#f2dc98", H: "#fcf0c8",
  o: "#c89a38", O: "#e6c060", p: "#ffe8a0", P: "#fff6d8",
  w: "#fffbe8", W: "#fff2c0",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const primary = (k, glow) => (x, row, dorsal, w, h) => {
  if (x === w - 1 && (row === 0 || row >= h - 1)) return null
  if (x === w - 2 && row === h - 1) return null
  if (x >= w - 3 && row === h - 1 && k > 5) return null
  const tip = x >= w - 4
  if (glow) return dorsal && (tip || row === 0) ? (x >= w - 2 ? "W" : "w") : null
  if (dorsal) {
    if (row === 1 && x < w - 4 && x % 2 === 0) return "S"
    if (row === 0) return tip ? "I" : "i"
    if (tip) return x >= w - 2 ? "I" : "i"
    return (x + k) % 7 === 0 ? "j" : (row === h - 1 ? "k" : "J")
  }
  if (row === 1) return "s"
  return tip ? "H" : (row === h - 1 ? "g" : "h")
}

const sheet = (o, glow) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - o.steps[f % o.steps.length]
  if (row >= len) return null
  const inF = x % fw
  if (row === len - 1 && (inF === 0 || inF === fw - 1)) return null
  if (glow) return dorsal && row >= len - 2 ? "w" : null
  if (dorsal) {
    if (row >= len - 2) return "i"
    if (inF === fw - 1) return "k"
    if (inF === 1 && row > 1 && row % 3 === 0) return "S"
    if (row < o.light) return f % 2 ? "i" : "J"
    return f % 2 ? "J" : "i"
  }
  if (row >= len - 2) return "H"
  return inF === fw - 1 ? "G" : "h"
}

const coverts = (o, glow) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - ((f + o.shift) % 2)
  if (row >= len) return null
  const inF = x % fw
  if (row === len - 1 && (inF === 0 || inF === fw - 1)) return null
  if (glow) return null
  if (dorsal) {
    if (row === len - 1) return "i"
    if (inF === fw - 1) return "j"
    return row < 2 ? o.top : o.mid
  }
  return row === len - 1 ? "H" : "h"
}

const marginal = (glow) => (x, row, dorsal) => {
  if (glow) return null
  if (!dorsal) return "H"
  if (row === 0) return "I"
  return (Math.floor(x / 2) + row) % 2 ? "i" : "J"
}

const scapulars = (glow) => (x, row, dorsal, w, h) => {
  const len = h - (x % 2)
  if (row >= len) return null
  if (glow) return null
  return dorsal ? (row >= len - 1 ? "i" : (x % 2 ? "J" : "j")) : "h"
}

const alternate = (cube, fn) => (x, row, dorsal, w, h) => fn(x + 3 * (cube.index % 2), row, dorsal, w, h)

const fns = (cube, glow) => ({
  primary: primary(cube.index, glow),
  tertials: sheet({ fw: 3, steps: [0, 1, 0, 2], light: 4 }, glow),
  secondaries: sheet({ fw: 3, steps: [0, 1, 1, 0, 2], light: 2 }, glow),
  hand_sheet: sheet({ fw: 2, steps: [1, 0], light: 0 }, glow),
  hand_coverts: coverts({ fw: 3, shift: 0, top: "i", mid: "J" }, glow),
  greater_coverts: coverts({ fw: 3, shift: 1, top: "I", mid: "i" }, glow),
  arm_coverts: coverts({ fw: 4, shift: 0, top: "I", mid: "i" }, glow),
  marginal: marginal(glow),
  alula: primary(11, glow),
  scapulars: scapulars(glow),
  tertial_feather: alternate(cube, sheet({ fw: 3, steps: [0], light: 4 }, glow)),
  secondary_feather: alternate(cube, sheet({ fw: 3, steps: [0], light: 2 }, glow)),
  hand_feather: alternate(cube, sheet({ fw: 3, steps: [0], light: 0 }, glow))
}[cube.kind])

const base = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") { P.box(c, cube, "I", "i", "J"); continue }
    P.plate(c, cube, fns(cube, false))
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

const glow = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") continue
    const fn = fns(cube, true)
    if (fn) P.plate(c, cube, fn)
  }
}

sprite("seraph_wings", { w: R.texture[0], h: R.texture[1] }, base)
sprite("seraph_glow", { w: R.texture[0], h: R.texture[1] }, glow)
