include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("gull.rig.js")

pal({
  K: "#141518", k: "#1f2125", j: "#2c2f35", w: "#f4f6f7", W: "#dfe4e8",
  m: "#7c8894", M: "#8e9aa6", n: "#a0abb6", N: "#b3bdc6",
  v: "#e9edf0", V: "#d3d9de", u: "#bcc4cb", b: "#5e6872",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const primary = (k) => (x, row, dorsal, w, h) => {
  if (x === w - 1 && row !== 1) return null
  const black = x >= w * (k <= 2 ? 0.35 : k <= 4 ? 0.5 : k <= 6 ? 0.68 : 0.85)
  const apical = x >= w - 3 && x <= w - 2 && k <= 5
  const mirror = (k === 1 || k === 2) && x >= w - 7 && x <= w - 5 && row >= 1
  if (dorsal) {
    if (apical) return row === 1 ? "w" : "W"
    if (mirror) return "w"
    if (black) return row === 0 ? "j" : row === h - 1 ? "K" : "k"
    return row === 0 ? "N" : row === h - 1 ? "m" : "M"
  }
  if (apical || mirror) return "v"
  if (black) return row === 0 ? "b" : "j"
  return row === h - 1 ? "V" : "v"
}

const sheet = (o) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - o.steps[f % o.steps.length]
  if (row >= len) return null
  const inF = x % fw
  const white = row >= len - o.white
  if (dorsal) {
    if (white) return row === len - 1 && inF === fw - 1 ? "W" : "w"
    if (inF === fw - 1) return "m"
    return row < 2 ? "N" : "M"
  }
  if (inF === fw - 1 && !white) return "V"
  return "v"
}

const coverts = (o) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - ((f + o.shift) % 2)
  if (row >= len) return null
  const inF = x % fw
  if (dorsal) {
    if (row === len - 1) return "m"
    return inF === fw - 1 ? "M" : row === 0 ? "N" : "n"
  }
  return row === len - 1 ? "V" : "v"
}

const marginal = (x, row, dorsal) => dorsal ? (row === 0 ? "w" : x % 2 ? "N" : "n") : "w"

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") { P.box(c, cube, "N", "M", "m"); continue }
    const fn = {
      primary: primary(cube.index),
      tertials: sheet({ fw: 3, steps: [0, 1, 0, 1], white: 3 }),
      secondaries: sheet({ fw: 2, steps: [0, 1, 0, 1, 1, 0], white: 2 }),
      hand_sheet: sheet({ fw: 2, steps: [1, 0], white: 2 }),
      hand_coverts: coverts({ fw: 2, shift: 0 }),
      greater_coverts: coverts({ fw: 3, shift: 1 }),
      arm_coverts: coverts({ fw: 3, shift: 0 }),
      marginal
    }[cube.kind]
    P.plate(c, cube, fn)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("gull_wings", { w: R.texture[0], h: R.texture[1] }, paint)
