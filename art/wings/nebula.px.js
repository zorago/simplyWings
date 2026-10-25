include("lib/rig_pack.js")
include("lib/feathers.js")
include("nebula.rig.js")

pal({
  a: "#080a22e0", b: "#10164ad8", c: "#1c226ad0", d: "#2e1e76c8", e: "#4a2286c0", f: "#6a2c94b4",
  g: "#924aa8a8", h: "#3a6ab8b0", i: "#5a9ad0a0", j: "#c070c098",
  w: "#ffffff", W: "#dfe8ff", y: "#ffe8a8",
  r: "#2a2e6a", R: "#3e4494",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS
const hash = (x, y, s) => {
  const v = Math.sin(x * 12.9898 + y * 78.233 + s * 37.719) * 43758.5453
  return v - Math.floor(v)
}
const noise = (x, y, s) => {
  const xi = Math.floor(x), yi = Math.floor(y), xf = x - xi, yf = y - yi
  const l = (a, b, t) => a + (b - a) * (t * t * (3 - 2 * t))
  return l(l(hash(xi, yi, s), hash(xi + 1, yi, s), xf), l(hash(xi, yi + 1, s), hash(xi + 1, yi + 1, s), xf), yf)
}
const fbm = (x, y, s) => 0.6 * noise(x / 4, y / 4, s) + 0.3 * noise(x / 2, y / 2, s + 1) + 0.1 * noise(x, y, s + 2)

const nebula = (gx, gy, depth, dorsal, glow) => {
  const n = fbm(gx, gy, 3)
  const m = fbm(gx + 40, gy, 7)
  const star = hash(gx, gy, 11)
  if (star > 0.965) return glow ? (star > 0.99 ? "w" : "W") : "W"
  if (glow) return null
  if (n > 0.68) return m > 0.55 ? "j" : "g"
  if (n > 0.56) return m > 0.6 ? "i" : "f"
  if (m > 0.66) return "h"
  const ramp = dorsal ? ["e", "d", "c", "b", "a"] : ["f", "e", "d", "c", "b"]
  return ramp[Math.min(4, Math.floor(depth * 4.5 + (n < 0.35 ? 1 : 0)))]
}

const cloud = (cube, glow) => (x, row, dorsal, w, h) => {
  const bottom = h - 1 - 3 * Math.abs(Math.sin(x * 0.55 + cube.part * 1.7)) - fbm(x, 0, cube.part) * 2
  if (row > bottom) return null
  if (row === 0 && hash(x, 0, 4) > 0.6) return null
  return nebula(x + cube.part * 9, row, row / Math.max(1, bottom), dorsal, glow)
}

const railAt = (x) => {
  const r = R.cloudRail
  if (x <= r[0][0]) return r[0][1]
  for (let i = 0; i + 1 < r.length; i++) if (x <= r[i + 1][0]) {
    const t = (x - r[i][0]) / Math.max(1e-6, r[i + 1][0] - r[i][0])
    return r[i][1] + (r[i + 1][1] - r[i][1]) * t
  }
  return r[r.length - 1][1]
}

const veil = (glow) => (c) => {
  const sh = R.sheet
  for (const [u0, v0, dorsal] of [[sh.ventral[0], sh.ventral[1], false], [sh.dorsal[0], sh.dorsal[1], true]]) for (let u = 0; u < sh.w; u++) for (let v = 0; v < sh.h; v++) {
    const px = sh.x + u + 0.5, py = sh.y - v - 0.5
    const bottom = railAt(px) - 1.5 - 1.2 * Math.abs(Math.sin(px * 0.55)) - fbm(px, 0, 2)
    if (py > R.cloudTop - 0.2 || py < bottom) continue
    if (py > R.cloudTop - 1.2 && hash(Math.floor(px), 0, 4) > 0.6) continue
    const k = nebula(Math.floor(px), Math.floor(R.cloudTop - py), (R.cloudTop - py) / (R.cloudTop - bottom), dorsal, glow)
    if (k) c.px(u0 + u, v0 + v, k)
  }
}

const lobeShape = (along, across, L, Wd) => {
  const t = along / L
  const r = Wd / 2 * (t < 0.75 ? 1 : Math.sqrt(Math.max(0, 1 - ((t - 0.75) / 0.25) ** 2))) + 0.2
  return Math.abs(across + 0.5 - Wd / 2) <= r * (0.85 + 0.15 * Math.sin(t * 9))
}

const lobe = (cube, vertical, glow) => (x, row, dorsal, w, h) => {
  const along = vertical ? row + 0.5 : x + 0.5
  const across = vertical ? x : row
  const L = vertical ? h : w, Wd = vertical ? w : h
  if (!lobeShape(along, across, L, Wd)) return null
  return nebula(x + cube.index * 13 + 50, row + cube.index * 5, along / L * 0.8, dorsal, glow)
}

const galaxy = (glow) => (x, row, dorsal, w, h) => {
  const dx = x + 0.5 - w / 2, dy = row + 0.5 - h / 2
  const r = Math.hypot(dx, dy)
  if (r > w / 2) return null
  const a = Math.atan2(dy, dx)
  const arm = Math.sin(a * 2 - r * 1.6)
  if (r < 0.9) return glow ? "y" : "w"
  if (arm > 0.4) return glow ? (r < 2 ? "W" : null) : (r < 2 ? "W" : "i")
  return glow ? null : (r < 2 ? "j" : "e")
}

const star = (glow) => (x, row, dorsal, w, h) => {
  const cx = Math.abs(x - 1), cy = Math.abs(row - 1)
  if (cx + cy > 1) return null
  return cx + cy === 0 ? "w" : (glow ? "W" : "W")
}

const base = (c) => {
  veil(false)(c)
  for (const cube of R.cubes) {
    switch (cube.kind) {
      case "cloud": P.plate(c, cube, cloud(cube, false)); break
      case "lobe_h": P.plate(c, cube, lobe(cube, false, false)); break
      case "lobe_v": P.plate(c, cube, lobe(cube, true, false)); break
      case "galaxy": P.plate(c, cube, galaxy(false)); break
      case "star": P.plate(c, cube, star(false)); break
      case "ridge": P.box(c, cube, "R", "r", "b"); break
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

const glowLayer = (c) => {
  veil(true)(c)
  for (const cube of R.cubes) {
    switch (cube.kind) {
      case "cloud": P.plate(c, cube, cloud(cube, true)); break
      case "lobe_h": P.plate(c, cube, lobe(cube, false, true)); break
      case "lobe_v": P.plate(c, cube, lobe(cube, true, true)); break
      case "galaxy": P.plate(c, cube, galaxy(true)); break
      case "star": P.plate(c, cube, star(true)); break
    }
  }
}

sprite("nebula_wings", { w: R.texture[0], h: R.texture[1] }, base)
sprite("nebula_glow", { w: R.texture[0], h: R.texture[1] }, glowLayer)
