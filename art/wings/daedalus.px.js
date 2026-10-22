include("lib/rig_pack.js")
include("lib/feathers.js")
include("daedalus.rig.js")

pal({
  a: "#4e3412", b: "#6a4a1c", c: "#8a6428", d: "#b08838", e: "#d4ac52", f: "#f0d07a",
  m: "#2a3038", n: "#3a4048", o: "#525a64", p: "#6e7884", r: "#8c96a2", s: "#aab4c0",
  k: "#1a1c20", K: "#dfe6ee",
  w: "#f4efe4", W: "#ffffff", x: "#d8cdb8", y: "#c88a2a", Y: "#e8b04a",
  j: "#fffbe8", J: "#bfe8ff", g: "#ffd060", G: "#ff8a20", h: "#e8401c",
  u: "#f8f4ea", U: "#c02818",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const size = (cube) => {
  const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
  return [Math.ceil(2 * (w + d) - 1e-6), Math.ceil(h + d - 1e-6), Math.max(1, Math.round(d)), Math.max(1, Math.round(w))]
}

const tube = (c, cube) => {
  const [fw, fh] = size(cube)
  const [u, v] = cube.uv
  c.rect(u, v, fw, fh, "d")
  c.hline(u, v, fw, "f")
  c.hline(u, v + 1, fw, "e")
  c.hline(u, v + fh - 1, fw, "b")
  for (let i = 3; i < fw; i += 5) c.vline(u + i, v, fh, "c")
}

const brassBox = (c, cube) => {
  const [fw, fh] = size(cube)
  const [u, v] = cube.uv
  c.rect(u, v, fw, fh, "d")
  c.rect(u, v, fw, 1, "f")
  c.rect(u, v + fh - 1, fw, 1, "b")
  for (let i = 1; i < fw - 1; i += 3) {
    c.px(u + i, v + 1, "a")
    c.px(u + i, v + fh - 2, "a")
  }
}

const steel = (cube) => (x, row, dorsal, w, h) => {
  if (row === 0) return "s"
  if (row === h - 1 || x === 0 || x === w - 1) return dorsal ? "n" : "o"
  if ((x % 4 === 2) && (row === 1 || row === h - 2)) return dorsal ? "K" : "s"
  if (x % 8 === 0) return dorsal ? "o" : "p"
  return dorsal ? ((row + (cube.index || 0)) % 4 === 0 ? "o" : "p") : "r"
}

const steelBox = (c, cube) => {
  const [fw, fh] = size(cube)
  const [u, v] = cube.uv
  c.rect(u, v, fw, fh, "o")
  c.hline(u, v, fw, "s")
  c.hline(u, v + fh - 1, fw, "m")
  for (let i = 1; i < fw; i += 3) c.px(u + i, v + 1, "K")
}

const gear = (c, cube) => {
  const [fw, fh, d, w] = size(cube)
  const [u, v] = cube.uv
  c.rect(u, v, fw, fh, "c")
  const fu = u + d, fv = v + d
  for (let yy = 0; yy < w; yy++) for (let xx = 0; xx < w; xx++) {
    const dx = xx + 0.5 - w / 2, dy = yy + 0.5 - w / 2, r = Math.hypot(dx, dy)
    const a = Math.atan2(dy, dx)
    const tooth = Math.cos(a * 8) > 0.3
    const col = r > w / 2 - 0.2 && !tooth ? null : r < 0.8 ? "k" : r < 1.4 ? "f" : tooth && r > w / 2 - 1 ? "e" : "d"
    if (col) c.px(fu + xx, fv + yy, col)
    if (col) c.px(fu + w + d + xx, fv + yy, col)
  }
}

const feather = (x, row, dorsal, w, h) => {
  if (x === w - 1 && row !== 1) return null
  if (row === 1) return x < w - 1 ? "x" : "w"
  return x > w - 3 ? "W" : (dorsal ? "w" : "u")
}

const jet = (glow) => (c, cube) => {
  const [fw, fh] = size(cube)
  const [u, v] = cube.uv
  for (let row = 0; row < fh; row++) {
    const t = row / Math.max(1, fh - 1)
    for (let x = 0; x < fw; x++) {
      const edge = Math.abs(x + 0.5 - fw / 2) / (fw / 2)
      if (edge > 1 - 0.5 * t) continue
      c.px(u + x, v + row, t < 0.25 ? "j" : t < 0.45 ? "J" : t < 0.7 ? "g" : t < 0.88 ? "G" : "h")
    }
  }
}

const dial = (c, cube) => {
  const [fw, fh] = size(cube)
  const [u, v] = cube.uv
  c.rect(u, v, fw, fh, "u")
  c.px(u + 1, v + 1, "U")
  c.px(u + 2, v + 1, "k")
}

const base = (c) => {
  for (const cube of R.cubes) {
    switch (cube.kind) {
      case "brass": tube(c, cube); break
      case "brass_box": brassBox(c, cube); break
      case "steel": P.plate(c, cube, steel(cube)); break
      case "steel_box": steelBox(c, cube); break
      case "gear": gear(c, cube); break
      case "feather": P.plate(c, cube, feather); break
      case "wax": P.box(c, cube, "Y", "y", "c"); break
      case "jet": case "jet_side": jet(false)(c, cube); break
      case "dial": dial(c, cube); break
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

const glow = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "jet" || cube.kind === "jet_side") jet(true)(c, cube)
    if (cube.kind === "dial") {
      const [u, v] = cube.uv
      c.px(u + 1, v + 1, "U")
    }
  }
}

sprite("daedalus_wings", { w: R.texture[0], h: R.texture[1] }, base)
sprite("daedalus_glow", { w: R.texture[0], h: R.texture[1] }, glow)
