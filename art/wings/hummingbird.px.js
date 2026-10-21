include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("hummingbird.rig.js")

pal({
  K: "#1d1a22", k: "#2a2630", j: "#38333f", J: "#474150", i: "#585161",
  e: "#173d26", E: "#225430", f: "#2f6d3b", F: "#3f8a48", G: "#5aa858", L: "#8fd16e", r: "#b8e88a",
  v: "#6e6874", V: "#7f7985", w: "#958f9a", W: "#d9d4cc",
  x: "#4a44544a", X: "#5a546430", y: "#6a647224",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const primary = (k) => (x, row, dorsal, w, h) => {
  if (x === w - 1 && row !== 1) return null
  if (x === w - 2 && row === h - 1) return null
  if (dorsal) {
    if (row === 0) return x < 3 ? "f" : "i"
    return row === 1 ? "J" : (x % 3 === 0 ? "j" : "k")
  }
  return row === 0 ? "V" : row === h - 1 ? "v" : "w"
}

const sheet = (o) => (x, row, dorsal, w, h) => {
  const f = Math.floor(x / o.fw)
  const len = h - o.steps[f % o.steps.length]
  if (row >= len) return null
  if (dorsal) {
    if (row < o.green) return (x + row) % 3 === 0 ? "G" : "F"
    return row === len - 1 ? "K" : f % 2 ? "j" : "k"
  }
  return row === len - 1 ? "v" : "V"
}

const green = (o) => (x, row, dorsal, w, h) => {
  const f = Math.floor(x / o.fw)
  const len = h - ((f + o.shift) % 2)
  if (row >= len) return null
  if (!dorsal) return "W"
  if (row === len - 1) return "e"
  if ((x * 5 + row * 3) % 7 === 0) return "r"
  return (x + row) % 3 === 0 ? "L" : (x + row) % 3 === 1 ? "G" : "F"
}

const covered = {}
for (const cube of GEOM.plates(R)) {
  if (cube.kind === "ghost") continue
  const w = Math.round(cube.to[0] - cube.from[0]), h = Math.round(cube.to[1] - cube.from[1])
  for (let row = 0; row < h; row++) for (let col = 0; col < w; col++) {
    for (const [dx, dy] of [[0.25, 0.25], [0.75, 0.25], [0.25, 0.75], [0.75, 0.75]]) {
      const p = GEOM.world(R, cube.group, [cube.from[0] + col + dx, cube.to[1] - row - dy])
      covered[Math.floor(p[0]) + "," + Math.floor(p[1])] = true
    }
  }
}
const ghost = R.cubes.find(c => c.kind === "ghost")
const ghostFn = (x, row, dorsal, w, h) => {
  const wx = Math.floor(ghost.from[0] + x), wy = Math.floor(ghost.to[1] - row - 1)
  const at = (a, b) => covered[(wx + a) + "," + (wy + b)]
  if (!at(0, 0)) return null
  const edge = !at(1, 0) || !at(-1, 0) || !at(0, 1) || !at(0, -1)
  return edge ? "y" : (x + row) % 5 === 0 ? "X" : "x"
}

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "bone") { P.box(c, cube, "F", "E", "e"); continue }
    const fn = {
      primary: primary(cube.index),
      tertials: sheet({ fw: 2, steps: [0, 1], green: 2 }),
      secondaries: sheet({ fw: 2, steps: [0, 1, 0], green: 1 }),
      hand_sheet: sheet({ fw: 2, steps: [1, 0], green: 0 }),
      hand_coverts: green({ fw: 2, shift: 0 }),
      greater_coverts: green({ fw: 2, shift: 1 }),
      arm_coverts: green({ fw: 2, shift: 0 }),
      marginal: green({ fw: 1, shift: 0 }),
      ghost: ghostFn
    }[cube.kind]
    P.plate(c, cube, fn)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("hummingbird_wings", { w: R.texture[0], h: R.texture[1] }, paint)
