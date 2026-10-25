include("lib/rig_pack.js")
include("lib/feathers.js")
include("shadow.rig.js")

pal({
  a: "#0c0612e0", b: "#170c22d4", c: "#24123ac4", d: "#341c4eb4", e: "#4a2a66a0", f: "#62388088", g: "#7a4a9a60",
  p: "#c060ff", P: "#f0c8ff", k: "#08040c",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS
const RAMP = ["a", "b", "c", "d", "e", "f", "g"]
const hash = (x, y, s) => {
  const v = Math.sin(x * 12.9898 + y * 78.233 + s * 37.719) * 43758.5453
  return v - Math.floor(v)
}

const swirl = (x, y, seed) => {
  const cx = 4 + (seed * 3.7) % 6, cy = 4 + (seed * 2.3) % 4
  const dx = x - cx, dy = y - cy
  const r = Math.hypot(dx, dy), a = Math.atan2(dy, dx)
  return Math.sin(r * 1.25 - a * 2 + seed)
}

const smoke = (t, x, y, seed, dorsal) => {
  const s = swirl(x, y, seed)
  let k = Math.floor(t * 5.2) + (s > 0.7 ? -1 : s < -0.8 ? 1 : 0)
  if (!dorsal) k += 1
  return RAMP[Math.max(0, Math.min(RAMP.length - 1, k))]
}

const curl = (along, across, L, Wd, seed, dorsal) => {
  const t = along / L
  const bend = Math.sin(t * Math.PI * 1.4 + seed) * Wd * 0.3 * t + (t > 0.7 ? (t - 0.7) * Wd * 1.2 : 0)
  const c = Wd / 2 + bend
  const hw = Wd / 2 * Math.pow(Math.max(0, 1 - t), 0.85) + 0.25
  const d = Math.abs(across + 0.5 - c)
  if (d > hw) return null
  if (t > 0.6 && hash(Math.floor(along), Math.floor(across), seed) < (t - 0.6) * 1.2) return null
  return smoke(Math.min(1, t * 0.8 + d / hw * 0.35), along, across, seed, dorsal)
}

const plume = (cube) => (x, row, dorsal, w, h) => {
  const seed = cube.index * 1.7 + 0.4
  const t = (row + 0.5) / h
  const sway = 0.8 * Math.sin(t * Math.PI * 1.3 + seed) * t
  const half = w / 2 * (t < 0.55 ? 1 : Math.max(0.15, 1 - (t - 0.55) * 1.9)) + 0.2
  const d = Math.abs(x + 0.5 - w / 2 - sway)
  if (d > half) return null
  if (t > 0.65 && hash(x, row, seed) < (t - 0.65) * 1.4) return null
  return smoke(Math.min(1, t * 0.75 + d / half * 0.3), x + cube.index * 5, row, seed, dorsal)
}

const eye = (glow) => (x, row, dorsal, w, h) => {
  if (!dorsal) return glow ? null : "a"
  if (glow) return x === 1 ? "P" : "p"
  return "k"
}

const base = (c) => {
  for (const cube of R.cubes) {
    switch (cube.kind) {
      case "curl_h": P.plate(c, cube, (x, row, dorsal, w, h) => curl(x + 0.5, row, w, h, cube.index * 1.9, dorsal)); break
      case "plume": P.plate(c, cube, plume(cube)); break
      case "eye": P.plate(c, cube, eye(false)); break
      case "heart": P.box(c, cube, "c", "b", "a"); break
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

const glow = (c) => {
  for (const cube of R.cubes) if (cube.kind === "eye") P.plate(c, cube, eye(true))
  const heart = R.cubes.find(q => q.kind === "heart")
  const [u, v] = heart.uv
  c.px(u + 3, v + 4, "p")
  c.px(u + 4, v + 4, "p")
}

sprite("shadow_wings", { w: R.texture[0], h: R.texture[1] }, base)
sprite("shadow_glow", { w: R.texture[0], h: R.texture[1] }, glow)
