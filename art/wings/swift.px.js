include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("swift.rig.js")

pal({
  K: "#141316", k: "#1d1c20", j: "#26252a", J: "#302e35", i: "#3c3a42", I: "#4a4751",
  g: "#2b292f", G: "#36343b", h: "#423f47", H: "#56525c",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const primary = (k) => (x, row, dorsal, w, h) => {
  const taper = x >= w - 4 ? 1 : 0
  if (x === w - 1 && row !== 0) return null
  if (x >= w - 3 && row === h - 1) return null
  if (dorsal) {
    if (row === 0) return k === 1 ? "I" : "i"
    if (row === h - 1) return "K"
    return taper ? "k" : (x % 7 === 3 ? "J" : "j")
  }
  if (row === 0) return "H"
  return row === h - 1 ? "g" : "G"
}

const scaly = (o) => (x, row, dorsal, w, h) => {
  const f = Math.floor(x / o.fw)
  const len = h - o.steps[f % o.steps.length]
  if (row >= len) return null
  const inF = x % o.fw
  if (row === len - 1 && o.fw > 2 && (inF === 0 || inF === o.fw - 1)) return null
  if (dorsal) {
    if (row === len - 1) return "I"
    if (inF === o.fw - 1) return "K"
    return row < 2 ? "J" : "j"
  }
  if (row === len - 1) return "H"
  return inF === o.fw - 1 ? "g" : "h"
}

const alternate = (cube, fn) => (x, row, dorsal, w, h) => fn(x + 3 * (cube.index % 2), row, dorsal, w, h)

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") { P.box(c, cube, "J", "j", "K"); continue }
    const fn = {
      primary: primary(cube.index),
      tertial_feather: alternate(cube, scaly({ fw: 3, steps: [0] })),
      secondary_feather: alternate(cube, scaly({ fw: 3, steps: [0] })),
      hand_feather: alternate(cube, scaly({ fw: 3, steps: [0] })),
      tertials: scaly({ fw: 3, steps: [0, 1] }),
      secondaries: scaly({ fw: 2, steps: [0, 1, 1, 0] }),
      hand_sheet: scaly({ fw: 2, steps: [1, 0] }),
      hand_coverts: scaly({ fw: 2, steps: [0, 1] }),
      greater_coverts: scaly({ fw: 3, steps: [1, 0] }),
      arm_coverts: scaly({ fw: 3, steps: [0, 1] }),
      marginal: scaly({ fw: 2, steps: [0, 0] })
    }[cube.kind]
    P.plate(c, cube, fn)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("swift_wings", { w: R.texture[0], h: R.texture[1] }, paint)
