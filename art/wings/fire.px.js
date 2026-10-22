include("lib/rig_pack.js")
include("lib/feathers.js")
include("fire.rig.js")

pal({
  w: "#fffbe0", W: "#fff2a8", y: "#ffd84a", Y: "#ffbe2e", o: "#ff9a1e", O: "#ff7414",
  r: "#f04a12", R: "#c42a0e", d: "#8a1608d8", D: "#5a0e06a8",
  e: "#ffe070", E: "#ff8c20",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS
const RAMP = ["w", "W", "y", "Y", "o", "O", "r", "R", "d", "D"]

const flame = (t, r, seed) => {
  if (t < 0.3 && r < 0.45) return t < 0.15 ? "w" : "W"
  const k = Math.min(RAMP.length - 1, Math.max(1, Math.floor(1.2 + t * 7.4 + r * 1.6 + 0.4 * Math.sin(seed))))
  return RAMP[k]
}

const lick = (along, across, L, Wd, seed) => {
  const t = along / L
  const c = Wd / 2 + 0.55 * Math.sin(along * 0.55 + seed) * t * Wd / 4
  const hw = Wd / 2 * Math.pow(Math.max(0, 1 - t), 0.6) + 0.25
  const d = Math.abs(across + 0.5 - c)
  if (d > hw) return null
  return flame(t, d / hw, seed + along)
}

const tongueH = (cube) => (x, row, dorsal, w, h) => lick(x + 0.5, row, w, h, cube.index * 1.7)
const tongueV = (cube) => (x, row, dorsal, w, h) => lick(row + 0.5, x, h, w, cube.index * 2.3 + 1)

const core = (cube) => (x, row, dorsal, w, h) => {
  const wave = 1.4 + 1.1 * Math.sin(x * 0.9 + (cube.role === "arm" ? 0 : cube.role === "forearm" ? 2 : 4))
  if (row >= h - wave) return null
  const t = row / h
  if (row === 0) return "Y"
  return t < 0.25 ? "W" : t < 0.5 ? "y" : t < 0.75 ? "Y" : "o"
}

const crest = (x, row, dorsal, w, h) => {
  const k = x % 4
  const height = [1.2, 2.6, 3.4, 2]
  const fromBottom = h - 1 - row
  if (fromBottom >= height[k] + (Math.floor(x / 4) % 2)) return null
  return fromBottom < 1 ? "Y" : fromBottom < 2 ? "O" : "r"
}

const paint = (c) => {
  for (const cube of R.cubes) {
    switch (cube.kind) {
      case "tongue_h": P.plate(c, cube, tongueH(cube)); break
      case "tongue_v": P.plate(c, cube, tongueV(cube)); break
      case "core": P.plate(c, cube, core(cube)); break
      case "crest": P.plate(c, cube, crest); break
      case "rod": P.box(c, cube, "w", "W", "y"); break
      case "heart": P.box(c, cube, "w", "w", "W"); break
      case "ember": P.box(c, cube, "e", "E", "O"); break
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("fire_wings", { w: R.texture[0], h: R.texture[1] }, paint)
