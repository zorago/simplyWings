include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("parrot.rig.js")

pal({
  r: "#8f1a14", R: "#b8261b", t: "#d93a22", T: "#ef5a33",
  y: "#c99418", Y: "#e8b828", z: "#f8d84a",
  g: "#3d8a2a", G: "#5aa834",
  b: "#152f7a", B: "#1f45a3", c: "#2e62c9", C: "#4c84e0", n: "#0d1c45",
  o: "#6e1f17", O: "#8e3020", p: "#b0702a",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const primary = (k) => (x, row, dorsal, w, h) => {
  if (x === w - 1 && (row === 0 || row === h - 1)) return null
  if (x === w - 2 && row === h - 1) return null
  const tip = x >= w - 3
  if (dorsal) {
    if (row === 0) return tip ? "B" : "C"
    if (row === h - 1) return "n"
    return tip ? "b" : (row === 1 ? "c" : "B")
  }
  if (row === 0) return "p"
  return row === h - 1 ? "o" : "O"
}

const sheet = (o) => (x, row, dorsal, w, h) => {
  const f = Math.floor(x / o.fw)
  const len = h - o.steps[f % o.steps.length]
  if (row >= len) return null
  const inF = x % o.fw
  if (row === len - 1 && o.fw > 2 && (inF === 0 || inF === o.fw - 1)) return null
  if (!dorsal) return row === len - 1 ? "o" : inF === o.fw - 1 ? "O" : "p"
  const [top, mid, edge, tip] = o.colors
  if (row === len - 1) return tip
  if (inF === o.fw - 1) return edge
  return row < 2 ? top : mid
}

const marginal = (x, row, dorsal) => {
  if (!dorsal) return "R"
  if (row === 0) return "T"
  return (Math.floor(x / 2) + row) % 2 ? "t" : "R"
}

const alternate = (cube, fn) => (x, row, dorsal, w, h) => fn(x + 3 * (cube.index % 2), row, dorsal, w, h)

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") { P.box(c, cube, "T", "R", "r"); continue }
    const fn = {
      primary: primary(cube.index),
      tertial_feather: alternate(cube, sheet({ fw: 3, steps: [0], colors: ["z", "Y", "y", "G"] })),
      secondary_feather: alternate(cube, sheet({ fw: 3, steps: [0], colors: ["C", "B", "b", "n"] })),
      hand_feather: alternate(cube, sheet({ fw: 3, steps: [0], colors: ["c", "B", "b", "n"] })),
      tertials: sheet({ fw: 3, steps: [0, 1, 0, 1], colors: ["z", "Y", "y", "G"] }),
      secondaries: sheet({ fw: 2, steps: [0, 1, 0, 1, 1, 0], colors: ["C", "B", "b", "n"] }),
      hand_sheet: sheet({ fw: 2, steps: [1, 0], colors: ["c", "B", "b", "n"] }),
      hand_coverts: sheet({ fw: 2, steps: [0, 1], colors: ["C", "c", "B", "b"] }),
      greater_coverts: sheet({ fw: 3, steps: [1, 0], colors: ["z", "Y", "y", "g"] }),
      arm_coverts: sheet({ fw: 3, steps: [0, 1], colors: ["T", "t", "R", "r"] }),
      marginal,
      alula: primary(10)
    }[cube.kind]
    P.plate(c, cube, fn)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("parrot_wings", { w: R.texture[0], h: R.texture[1] }, paint)
