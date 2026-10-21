include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("crow.rig.js")

pal({
  K: "#09090d", k: "#121219", j: "#1a1b27", J: "#242a3f", i: "#2f3d63", I: "#383260",
  g: "#18181d", G: "#212127", h: "#2a2a31", H: "#35353e",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const primary = (k) => (x, row, dorsal, w, h) => {
  const emarg = k >= 2 && k <= 6 ? 0.45 - 0.04 * k : 0
  const narrowFrom = emarg ? Math.round(w * (1 - emarg)) : w
  const narrow = x >= narrowFrom
  if (narrow && row >= h - 2) return null
  if (narrow && x === narrowFrom && row === h - 1) return null
  if (x === w - 1 && (row === 0 || row >= (narrow ? h - 3 : h - 1))) return null
  if (x === w - 2 && row === h - 1) return null
  const tip = x >= w - 3
  if (dorsal) {
    if (row === 0) return tip ? "j" : (x % 5 === 2 ? "i" : "J")
    if (row === 1) return tip ? "k" : "j"
    if (row === h - 1 || (narrow && row === h - 3)) return "K"
    return tip ? "K" : "k"
  }
  if (row === 0) return "H"
  if (row === 1) return tip ? "G" : "h"
  if (row === h - 1 || (narrow && row === h - 3)) return "g"
  return "G"
}

const sheet = (o) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - o.steps[f % o.steps.length]
  if (row >= len) return null
  const inF = x % fw
  if (row === len - 1 && fw > 2 && (inF === 0 || inF === fw - 1)) return null
  const alt = f % 2 === 0
  if (dorsal) {
    if (row === len - 1) return "K"
    if (row < o.sheen) return (x + row) % 4 === 0 ? "i" : "J"
    if (inF === fw - 1) return "K"
    return alt ? "k" : "j"
  }
  if (row === len - 1) return "g"
  if (inF === fw - 1) return "g"
  return alt ? "G" : "h"
}

const coverts = (o) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - ((f + o.shift) % 2)
  if (row >= len) return null
  const inF = x % fw
  if (row === len - 1 && (inF === 0 || inF === fw - 1) && fw > 2) return null
  if (dorsal) {
    if (row === len - 1) return "k"
    if (row === 0) return "j"
    if (inF === 1 && row < len - 2) return o.purple ? "I" : "i"
    return "J"
  }
  if (row === len - 1) return "G"
  return row === 0 ? "G" : "h"
}

const marginal = (x, row, dorsal) => {
  if (!dorsal) return row === 0 ? "h" : "H"
  if (row === 0) return "j"
  return (x + row) % 3 === 0 ? "I" : (x + row) % 3 === 1 ? "i" : "J"
}

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") { P.box(c, cube, "j", "k", "K"); continue }
    const fn = {
      primary: primary(cube.index),
      tertials: sheet({ fw: 3, steps: [0, 1, 0, 2], sheen: 4 }),
      secondaries: sheet({ fw: 2, steps: [0, 1, 0, 1, 2, 1], sheen: 3 }),
      hand_sheet: sheet({ fw: 2, steps: [1, 0], sheen: 2 }),
      hand_coverts: coverts({ fw: 2, shift: 0, purple: true }),
      greater_coverts: coverts({ fw: 3, shift: 1 }),
      arm_coverts: coverts({ fw: 3, shift: 0, purple: true }),
      marginal,
      alula: primary(9)
    }[cube.kind]
    P.plate(c, cube, fn)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("crow_wings", { w: R.texture[0], h: R.texture[1] }, paint)
