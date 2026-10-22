include("lib/rig_pack.js")
include("earth.rig.js")

pal({
  k: "#26262c", K: "#36363e", g: "#4a4a52", G: "#5c5c64", h: "#70707a", H: "#86868e", i: "#9c9ca2",
  s: "#2c3240", S: "#3a4252", t: "#4a5466", T: "#5c6878", u: "#72808e",
  m: "#2e5222", M: "#3e6a2a", n: "#5a8a34", N: "#7aa848", o: "#9cc45a",
  a: "#3a1e5e", A: "#5a2e8a", b: "#8a4cc8", B: "#b880f0", c: "#e0c0ff", C: "#fbf0ff",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const hash = (x, y, s) => {
  const v = Math.sin(x * 12.9898 + y * 78.233 + s * 37.719) * 43758.5453
  return v - Math.floor(v)
}

const faces = (cube, fn) => {
  const w = Math.max(1, Math.round(cube.to[0] - cube.from[0]))
  const h = Math.max(1, Math.round(cube.to[1] - cube.from[1]))
  const d = Math.max(1, Math.round(cube.to[2] - cube.from[2]))
  const [u, v] = cube.uv
  const list = [
    ["top", u + d, v, w, d], ["bottom", u + d + w, v, w, d],
    ["east", u, v + d, d, h], ["north", u + d, v + d, w, h],
    ["west", u + d + w, v + d, d, h], ["south", u + 2 * d + w, v + d, w, h]
  ]
  for (const [face, fu, fv, fw, fh] of list) {
    for (let yy = 0; yy < fh; yy++) for (let xx = 0; xx < fw; xx++) fn(face, fu + xx, fv + yy, xx, yy, fw, fh)
  }
}

const rock = (cube, seed) => (c) => faces(cube, (face, px, py, x, y, fw, fh) => {
  const r = hash(x, y, seed + face.length)
  if (cube.moss && (face === "top" || (face !== "bottom" && y < 1 + (r > 0.55 ? 1 : 0)))) {
    c.px(px, py, r > 0.8 ? "o" : r > 0.5 ? "N" : r > 0.2 ? "n" : "M")
    return
  }
  const crack = Math.abs(((x * 0.7 + y * 1.3 + seed) % 5) - 2.5) < 0.35 && r > 0.35
  if (crack) { c.px(px, py, "k"); return }
  const shade = face === "top" ? 2 : face === "bottom" ? -2 : face === "south" || face === "north" ? 0 : -1
  const ramp = ["k", "K", "g", "G", "h", "H", "i"]
  const k = Math.max(0, Math.min(6, 3 + shade + (r > 0.75 ? 1 : r < 0.2 ? -1 : 0) + (y === 0 ? 1 : 0)))
  c.px(px, py, ramp[k])
})

const slate = (cube, seed, along) => (c) => faces(cube, (face, px, py, x, y, fw, fh) => {
  const r = hash(x, y, seed)
  const pos = along === "x" ? x : y
  const len = along === "x" ? fw : fh
  const layer = (along === "x" ? y : x) % 2
  const tipZone = pos > len - 3
  const shade = face === "top" ? 1 : face === "bottom" ? -1 : 0
  const ramp = ["s", "S", "t", "T", "u"]
  let k = 2 + shade + layer - (tipZone ? 1 : 0) + (r > 0.85 ? 1 : 0)
  if (r < 0.08) k = 0
  c.px(px, py, ramp[Math.max(0, Math.min(4, k))])
})

const crystal = (cube, glow) => (c) => faces(cube, (face, px, py, x, y, fw, fh) => {
  const top = face === "top"
  const t = top ? 1 : 1 - y / Math.max(1, fh - 1)
  if (glow) {
    if (top || t > 0.55 || x === 0) c.px(px, py, t > 0.8 || top ? "C" : "c")
    return
  }
  const ramp = ["a", "A", "b", "B", "c"]
  c.px(px, py, top ? "c" : ramp[Math.min(4, Math.floor(t * 4.6) + (x === 0 ? 1 : 0))])
})

const base = (c) => {
  R.cubes.forEach((cube, i) => {
    if (cube.kind === "rock") rock(cube, i * 3.1)(c)
    else if (cube.kind === "slate") slate(cube, i * 1.7, "y")(c)
    else if (cube.kind === "slate_h") slate(cube, i * 1.7, "x")(c)
    else if (cube.kind === "crystal") crystal(cube, false)(c)
  })
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

const glow = (c) => {
  for (const cube of R.cubes) if (cube.kind === "crystal") crystal(cube, true)(c)
}

sprite("earth_wings", { w: R.texture[0], h: R.texture[1] }, base)
sprite("earth_glow", { w: R.texture[0], h: R.texture[1] }, glow)
