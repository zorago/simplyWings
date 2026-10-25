include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("eagle.rig.js")

pal({
  K: "#1e140d", k: "#2d1f15", j: "#3e2b1d", J: "#523a26", i: "#6a4b30",
  t: "#8a6338", T: "#a87b45", y: "#c99a58", Y: "#e0b66e",
  g: "#3a3029", G: "#4a3e35", h: "#5c4f44", H: "#6f6155", b: "#2a221d",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const primary = (k) => (x, row, dorsal, w, h) => {
  const emarg = k <= 6 ? 0.42 - 0.03 * k : 0
  const narrowFrom = emarg ? Math.round(w * (1 - emarg)) : w
  const narrow = x >= narrowFrom
  if (narrow && row >= h - 3) return null
  if (narrow && x === narrowFrom && row === h - 4) return null
  if (x === w - 1 && (row === 0 || row >= (narrow ? h - 4 : h - 1))) return null
  if (x === w - 2 && row === h - 1) return null
  const tip = x >= w - 4
  if (dorsal) {
    if (row === 0) return tip ? "j" : "J"
    if (row === 1) return tip ? "k" : (x % 6 === 3 ? "i" : "j")
    return tip ? "K" : (row === h - 1 ? "K" : "k")
  }
  if (tip) return "b"
  if ((x + k) % 5 === 0) return "g"
  return row === 0 ? "H" : row === h - 1 ? "g" : "h"
}

const sheet = (o) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - o.steps[f % o.steps.length]
  if (row >= len) return null
  const inF = x % fw
  if (row === len - 1 && (inF === 0 || inF === fw - 1)) return null
  if (dorsal) {
    if (row === len - 1 || row === len - 2) return "K"
    if (inF === fw - 1) return "k"
    if (row < o.light) return f % 2 ? "i" : "J"
    return f % 2 ? "j" : "J"
  }
  if (row >= len - 2) return "b"
  if (row % 4 === 3) return "g"
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
    if (row === len - 1) return "J"
    if (inF === fw - 1) return "i"
    return row < 2 ? o.top : o.mid
  }
  return row === len - 1 ? "G" : "H"
}

const marginal = (x, row, dorsal) => {
  if (!dorsal) return "H"
  if (row === 0) return "Y"
  return (Math.floor(x / 2) + row) % 2 ? "y" : "T"
}

const alternate = (cube, fn) => (x, row, dorsal, w, h) => fn(x + 3 * (cube.index % 2), row, dorsal, w, h)

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") { P.box(c, cube, "T", "t", "J"); continue }
    const fn = {
      primary: primary(cube.index),
      tertials: sheet({ fw: 3, steps: [0, 1, 0, 2], light: 4 }),
      secondaries: sheet({ fw: 3, steps: [0, 1, 1, 0, 2], light: 2 }),
      hand_sheet: sheet({ fw: 2, steps: [1, 0], light: 0 }),
      hand_coverts: coverts({ fw: 3, shift: 0, top: "J", mid: "j" }),
      greater_coverts: coverts({ fw: 3, shift: 1, top: "T", mid: "t" }),
      arm_coverts: coverts({ fw: 4, shift: 0, top: "y", mid: "T" }),
      marginal,
      tertial_feather: alternate(cube, sheet({ fw: 3, steps: [0], light: 4 })),
      secondary_feather: alternate(cube, sheet({ fw: 3, steps: [0], light: 2 })),
      hand_feather: alternate(cube, sheet({ fw: 3, steps: [0], light: 0 })),
      alula: primary(10)
    }[cube.kind]
    P.plate(c, cube, fn)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("eagle_wings", { w: R.texture[0], h: R.texture[1] }, paint)
