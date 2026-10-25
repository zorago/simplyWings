include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("yin_yang.rig.js")

pal({
  a: "#07080f", b: "#0e1222", c: "#161d36", d: "#1f2a4c", e: "#2e3e6c", f: "#4a5c94",
  g: "#141828", h: "#1e2438", i: "#2a3250",
  A: "#bdb9b0", B: "#d2cfc6", C: "#e4e2da", D: "#f0eee8", E: "#f9f8f4", F: "#ffffff",
  G: "#e2e6ee", H: "#eef0f4", I: "#f8f2f2",
  w: "#f6f6f2", W: "#dcdcd6", k: "#0c0c10", K: "#24242c",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS
const YIN = { dark: "a", mid: "c", light: "d", sheen: "e", hi: "f", tip: "b", v1: "g", v2: "h", v3: "i", eye: "w", eyeRim: "W" }
const YANG = { dark: "A", mid: "D", light: "E", sheen: "G", hi: "F", tip: "C", v1: "B", v2: "C", v3: "H", eye: "k", eyeRim: "K" }

const sCurve = (t) => Math.round(2.6 + 2.6 * Math.sin(Math.PI * (t * 1.6 - 0.3)))

const primary = (T, k) => (x, row, dorsal, w, h) => {
  if (x === w - 1 && (row === 0 || row >= h - 1)) return null
  if (x === w - 2 && row === h - 1) return null
  const tip = x >= w - 4
  if (dorsal) {
    if (row === 0) return tip ? T.sheen : T.light
    if (row === 1 && x % 3 === 0) return T.sheen
    if (tip) return T.tip
    return (x + k) % 6 === 0 ? T.light : T.mid
  }
  return tip ? T.v3 : row === h - 1 ? T.v1 : T.v2
}

const sheet = (T, o) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - (o.curve ? sCurve(f * fw / w) : o.steps[f % o.steps.length])
  if (row >= len) return null
  const inF = x % fw
  if (row === len - 1 && (inF === 0 || inF === fw - 1)) return null
  if (dorsal) {
    if (row >= len - 2) return T.tip
    if (inF === fw - 1) return T.dark
    if (row < o.light) return f % 2 ? T.light : T.sheen
    return f % 2 ? T.mid : T.light
  }
  return row >= len - 2 ? T.v3 : (inF === fw - 1 ? T.v1 : T.v2)
}

const coverts = (T, o) => (x, row, dorsal, w, h) => {
  const fw = o.fw
  const f = Math.floor(x / fw)
  const len = h - ((f + o.shift) % 2)
  if (row >= len) return null
  if (o.eye) {
    const r = Math.hypot(x + 0.5 - o.eye[0], row + 0.5 - o.eye[1])
    if (r < 1.7) return T.eye
    if (r < 2.4) return T.eyeRim
  }
  const inF = x % fw
  if (row === len - 1 && (inF === 0 || inF === fw - 1)) return null
  if (dorsal) {
    if (row === len - 1) return T.tip
    if (inF === fw - 1) return T.dark
    return row < 2 ? T.hi : T.light
  }
  return row === len - 1 ? T.v3 : T.v2
}

const marginal = (T) => (x, row, dorsal) => {
  if (!dorsal) return T.v2
  if (row === 0) return T.hi
  return (Math.floor(x / 2) + row) % 2 ? T.light : T.sheen
}

const rowCount = (kind) => R.cubes.filter(c => c.kind === kind).length

const feather = (T, cube, o) => (x, row, dorsal, w, h) => {
  const n = rowCount(cube.kind)
  const t = n > 1 ? cube.index / (n - 1) : 0
  const len = h - (o.curve ? sCurve(t) : (cube.index % 2))
  if (row >= len) return null
  if (row === len - 1 && (x === 0 || x === w - 1)) return null
  const odd = cube.index % 2
  if (dorsal) {
    if (row >= len - 2) return T.tip
    if (x === w - 1) return T.dark
    if (x === 1 && row > 1 && row % 3 === 0) return T.hi
    if (row < o.light) return odd ? T.light : T.sheen
    return odd ? T.mid : T.light
  }
  if (row >= len - 2) return T.v3
  return x === w - 1 ? T.v1 : (odd ? T.v2 : T.v3)
}

const fns = (T, cube) => ({
  tertial_feather: feather(T, cube, { light: 4 }),
  secondary_feather: feather(T, cube, { curve: true, light: 2 }),
  hand_feather: feather(T, cube, { light: 0 }),
  primary: primary(T, cube.index),
  tertials: sheet(T, { fw: 3, steps: [0, 1, 0, 2], light: 4 }),
  secondaries: sheet(T, { fw: 3, curve: true, light: 2 }),
  hand_sheet: sheet(T, { fw: 2, steps: [1, 0], light: 0 }),
  hand_coverts: coverts(T, { fw: 3, shift: 0, eye: [2.6, 3.4] }),
  greater_coverts: coverts(T, { fw: 3, shift: 1 }),
  arm_coverts: coverts(T, { fw: 4, shift: 0 }),
  marginal: marginal(T),
  alula: primary(T, 10)
}[cube.kind])

const taiji = (x, row, dorsal, w, h) => {
  const cx = w / 2, cy = h / 2, r = w / 2
  const px = x + 0.5 - cx, py = row + 0.5 - cy
  const d = Math.hypot(px, py)
  if (d > r) return null
  if (d > r - 0.6) return "K"
  const upper = Math.hypot(px, py + r / 2), lower = Math.hypot(px, py - r / 2)
  if (upper < r / 6 + 0.2) return "k"
  if (lower < r / 6 + 0.2) return "w"
  const yang = upper < r / 2 ? true : lower < r / 2 ? false : px > 0
  return yang ? "E" : "b"
}

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "taiji") { P.plate(c, cube, taiji); continue }
    if (cube.kind === "rim") { P.box(c, cube, "K", "k", "k"); continue }
    if (cube.kind === "bone") {
      P.box(c, cube, "E", "D", "C")
      P.box(c, { ...cube, uv: cube.leftUv }, "d", "c", "b")
      continue
    }
    P.plate(c, cube, fns(YANG, cube))
    P.plate(c, { ...cube, uv: cube.leftUv }, fns(YIN, cube))
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("yin_yang_wings", { w: R.texture[0], h: R.texture[1] }, paint)
