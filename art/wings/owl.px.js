include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("owl.rig.js")

pal({
  K: "#3a2617", k: "#563a24", j: "#71502f", J: "#8c673f", i: "#a98253", I: "#c6a274", s: "#e6d5b3",
  g: "#a8916c", G: "#c2ac85", h: "#d8c7a3", H: "#e9dcc0", b: "#9a8260",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const primary = (k) => (x, row, dorsal, w, h) => {
  if (x === w - 1 && (row === 0 || row === h - 1)) return null
  if (x === w - 2 && row === h - 1 && (k % 2 === 0)) return null
  if (k === 1 && row === 0 && x % 2 === 1 && x > 2) return null
  const bar = (x + k) % 5 < 2
  const tip = x >= w - 2
  if (dorsal) {
    if (row === 0) return k === 1 ? "I" : "i"
    if (row === h - 1) return tip ? "k" : "K"
    if (tip) return "j"
    if (bar) return row === 1 ? "k" : "K"
    return row === 1 ? "i" : row % 2 ? "J" : "j"
  }
  if (row === 0) return "H"
  if (row === h - 1) return "g"
  if (bar) return "b"
  return row === 1 ? "H" : "h"
}

const sheet = (o) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - o.steps[f % o.steps.length]
  if (row >= len) return null
  const inF = x % fw
  if (row === len - 1 && (inF === 0 || inF === fw - 1)) return null
  const bar = row % o.bar === o.bar - 1 && row > 1 && row < len - 2
  if (dorsal) {
    if (row === len - 1) return "i"
    if (row === len - 2) return "s"
    if (bar) return inF === fw - 1 ? "K" : "k"
    if (inF === fw - 1) return "j"
    return row % o.bar === 0 ? "i" : "J"
  }
  if (row >= len - 2) return "H"
  if (bar) return "g"
  return inF === fw - 1 ? "G" : "h"
}

const coverts = (o) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - ((f + o.shift) % 2)
  if (row >= len) return null
  const inF = x % fw
  if (row === len - 1 && (inF === 0 || inF === fw - 1)) return null
  if (dorsal) {
    if (row === len - 2 && inF === 1) return "s"
    if (row === len - 1) return "K"
    if (inF === fw - 1) return "k"
    return row < 2 ? "J" : (f % 2 ? "j" : "J")
  }
  return row === len - 1 ? "G" : "H"
}

const marginal = (x, row, dorsal) => {
  if (!dorsal) return "H"
  if ((x * 3 + row * 2) % 7 === 0) return "s"
  return (x + row) % 2 ? "j" : "J"
}

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") { P.box(c, cube, "J", "j", "k"); continue }
    const fn = {
      primary: primary(cube.index),
      tertials: sheet({ fw: 4, steps: [0, 1, 1, 0], bar: 5 }),
      secondaries: sheet({ fw: 3, steps: [0, 1, 0, 1, 1], bar: 5 }),
      hand_sheet: sheet({ fw: 2, steps: [1, 0], bar: 5 }),
      hand_coverts: coverts({ fw: 3, shift: 0 }),
      greater_coverts: coverts({ fw: 3, shift: 1 }),
      arm_coverts: coverts({ fw: 4, shift: 0 }),
      marginal,
      alula: primary(10)
    }[cube.kind]
    P.plate(c, cube, fn)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("owl_wings", { w: R.texture[0], h: R.texture[1] }, paint)
