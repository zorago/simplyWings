include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("peacock.rig.js")

pal({
  a: "#10323a", A: "#17504f", c: "#1f6f66", C: "#2c8f7c", d: "#45ad8c", D: "#7ccf9e",
  u: "#173a7a", U: "#2456a8",
  r: "#5e2f17", R: "#7f421f", o: "#a05a2a", O: "#c07a3c",
  e: "#3b3424", E: "#5a5236", s: "#d8cfae", S: "#efe6c8",
  g: "#556b25", G: "#728a30", y: "#b39a35", Y: "#dcc04a", Z: "#f0dc78", x: "#0d1622",
  v: "#9a8c70", V: "#b5a888",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const primary = (k) => (x, row, dorsal, w, h) => {
  if (x === w - 1 && (row === 0 || row === h - 1)) return null
  if (x === w - 2 && row === h - 1) return null
  if (dorsal) {
    if (row === 0) return x >= w - 3 ? "R" : "O"
    if (row === h - 1) return "r"
    return x >= w - 3 ? "r" : "o"
  }
  return row === 0 ? "V" : row === h - 1 ? "e" : "v"
}

const sheet = (o) => (x, row, dorsal, w, h) => {
  const f = Math.floor(x / o.fw)
  const len = h - o.steps[f % o.steps.length]
  if (row >= len) return null
  const inF = x % o.fw
  if (row === len - 1 && o.fw > 2 && (inF === 0 || inF === o.fw - 1)) return null
  if (!dorsal) return row === len - 1 ? "e" : inF === o.fw - 1 ? "v" : "V"
  if (o.barred) {
    if (row === len - 1) return "e"
    return Math.floor(row / 2) % 2 ? (inF === o.fw - 1 ? "e" : "E") : (inF === o.fw - 1 ? "E" : "s")
  }
  if (row === len - 1) return "a"
  if (row < 2) return "C"
  return inF === o.fw - 1 ? "a" : (f % 2 ? "A" : "c")
}

const scales = (o) => (x, row, dorsal, w, h) => {
  const f = Math.floor(x / o.fw)
  const len = h - ((f + o.shift) % 2)
  if (row >= len) return null
  if (!dorsal) return "V"
  if (row === len - 1) return o.deep ? "u" : "A"
  if ((x + row * 2) % 5 === 0) return "D"
  return (Math.floor(x / 2) + row) % 2 ? (o.deep ? "U" : "C") : (o.deep ? "u" : "d")
}

const train = (k) => (x, row, dorsal, w, h) => {
  const cy = 2, cx = w - 3
  const dx = (x - cx) / 1.25, dy = row - cy
  const r = Math.sqrt(dx * dx + dy * dy)
  if (x >= w - 1 && Math.abs(dy) > 0) return null
  const mid = Math.round(w * 0.55)
  if (Math.abs(x - mid) <= 1 && Math.abs(dy) <= 1 && k % 2 === 0) return Math.abs(dy) === 0 && x === mid ? "u" : "C"
  if (x >= cx - 3) {
    if (r <= 0.6) return "x"
    if (r <= 1.2) return "u"
    if (r <= 1.9) return "C"
    if (r <= 2.5) return "y"
    if (r <= 3.0) return x >= w - 2 ? null : "G"
    return null
  }
  if (row === cy) return dorsal ? "Y" : "y"
  const barb = (x + Math.abs(dy)) % 3 === 0
  if (Math.abs(dy) === 2) return (x + k) % 2 ? null : (barb ? "g" : "G")
  return barb ? "y" : "G"
}

const alternate = (cube, fn) => (x, row, dorsal, w, h) => fn(x + 3 * (cube.index % 2), row, dorsal, w, h)

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") { P.box(c, cube, "C", "c", "A"); continue }
    const fn = {
      primary: primary(cube.index),
      tertials: sheet({ fw: 3, steps: [0, 1, 0, 1], barred: true }),
      secondaries: sheet({ fw: 2, steps: [0, 1, 0, 1, 1, 0] }),
      hand_sheet: sheet({ fw: 2, steps: [1, 0], barred: true }),
      hand_coverts: scales({ fw: 2, shift: 0, deep: true }),
      greater_coverts: scales({ fw: 3, shift: 1 }),
      arm_coverts: scales({ fw: 3, shift: 0, deep: true }),
      marginal: scales({ fw: 2, shift: 0, deep: true }),
      train: train(cube.index),
      tertial_feather: alternate(cube, sheet({ fw: 3, steps: [0], barred: true })),
      secondary_feather: alternate(cube, sheet({ fw: 3, steps: [0] })),
      hand_feather: alternate(cube, sheet({ fw: 3, steps: [0], barred: true }))
    }[cube.kind]
    P.plate(c, cube, fn)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("peacock_wings", { w: R.texture[0], h: R.texture[1] }, paint)
