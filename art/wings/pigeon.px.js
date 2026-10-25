include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("pigeon.rig.js")

pal({
  K: "#202226", k: "#2c2f35", s: "#4f5560", S: "#626975", t: "#767e8a",
  m: "#8d96a2", M: "#9ea7b2", n: "#afb7c1", N: "#c0c7cf",
  v: "#cfd4da", V: "#dde1e5", u: "#b5bcc4", b: "#7a828c",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const primary = (k) => (x, row, dorsal, w, h) => {
  if (x === w - 1 && (row === 0 || row === h - 1)) return null
  if (x === w - 2 && row === h - 1) return null
  const dark = x >= w - (k <= 4 ? 6 : 4)
  if (dorsal) {
    if (dark) return row === 0 ? "s" : row === h - 1 ? "K" : "k"
    if (row === 0) return "N"
    return row === h - 1 ? "s" : row === 1 ? "M" : (k <= 3 ? "S" : "t")
  }
  if (dark) return row === h - 1 ? "s" : "b"
  return row === h - 1 ? "u" : "v"
}

const sheet = (o) => (x, row, dorsal, w, h) => {
  const f = Math.floor(x / o.fw)
  const len = h - o.steps[f % o.steps.length]
  if (row >= len) return null
  const inF = x % o.fw
  if (row === len - 1 && o.fw > 2 && (inF === 0 || inF === o.fw - 1)) return null
  const bar = o.bar && row >= o.bar[0] && row < o.bar[1]
  if (dorsal) {
    if (bar) return inF === o.fw - 1 ? "K" : "k"
    if (row === len - 1) return o.tip || "m"
    if (inF === o.fw - 1) return "m"
    return row < 2 ? "N" : (f % 2 ? "n" : "M")
  }
  if (bar) return "u"
  return inF === o.fw - 1 ? "V" : "v"
}

const marginal = (x, row, dorsal) => dorsal ? (row === 0 ? "N" : (x + row) % 3 ? "n" : "M") : "V"

const alternate = (cube, fn) => (x, row, dorsal, w, h) => fn(x + 3 * (cube.index % 2), row, dorsal, w, h)

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") { P.box(c, cube, "n", "M", "m"); continue }
    const fn = {
      primary: primary(cube.index),
      tertial_feather: alternate(cube, sheet({ fw: 3, steps: [0], bar: [6, 8] })),
      secondary_feather: alternate(cube, sheet({ fw: 3, steps: [0], bar: [6, 8], tip: "s" })),
      hand_feather: alternate(cube, sheet({ fw: 3, steps: [0], tip: "s" })),
      tertials: sheet({ fw: 3, steps: [0, 1, 0, 1], bar: [6, 8] }),
      secondaries: sheet({ fw: 2, steps: [0, 1, 0, 1, 1, 0], bar: [6, 8], tip: "s" }),
      hand_sheet: sheet({ fw: 2, steps: [1, 0], tip: "s" }),
      hand_coverts: sheet({ fw: 2, steps: [0, 1] }),
      greater_coverts: sheet({ fw: 3, steps: [1, 0], bar: [3, 5] }),
      arm_coverts: sheet({ fw: 3, steps: [0, 1], bar: [3, 5] }),
      marginal,
      alula: primary(10)
    }[cube.kind]
    P.plate(c, cube, fn)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("pigeon_wings", { w: R.texture[0], h: R.texture[1] }, paint)
