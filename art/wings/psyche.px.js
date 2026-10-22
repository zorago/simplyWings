include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("psyche.rig.js")

pal({
  a: "#f4feffe8", b: "#d4f6fce0", c: "#a8eaf6d8", d: "#72d6eed0", e: "#40b4e0cc", f: "#2a86c4c8", g: "#1c5c9ac8",
  n: "#0e2a52e0", N: "#16406ed8", w: "#ffffffee",
  p: "#f8f0ffe0", P: "#e4d8f8d8", r: "#c8b8ecd0",
  s: "#effeff", S: "#b8f8ff", t: "#6ae8fa", T: "#2ec4ec", u: "#2a8ac8b0",
  k: "#1a2238", K: "#2c3a5a", l: "#c89a48", L: "#f0d080", y: "#fff2b0",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const wing = (cube, kind) => (x, row, dorsal) => {
  const p = [x + 0.5, row + 0.5]
  if (!GEOM.inside(cube.shape, p)) return null
  const edge = GEOM.edgeDist(cube.shape, p)
  const r = Math.hypot(p[0], p[1])
  const ang = Math.atan2(p[1], p[0])
  const veins = Math.abs(Math.sin(ang * (kind === "fore" ? 7 : 6))) < 0.12 && r > 3
  if (edge < 0.8) return "n"
  if (edge < 2.4) {
    const spot = kind === "fore" ? (Math.floor(r / 2.2) % 2 === 0 && Math.abs(Math.sin(ang * 9)) > 0.8) : (Math.abs(Math.sin(ang * 8)) > 0.85)
    return spot ? "w" : "N"
  }
  if (veins) return dorsal ? "g" : "r"
  if (kind === "hind") {
    const e = Math.hypot(p[0] - 9.5, p[1] - 12.5)
    if (e < 1.2) return "w"
    if (e < 2.2) return "n"
    if (e < 3.2) return dorsal ? "t" : "T"
  } else {
    const e = Math.hypot(p[0] - 17, p[1] - 8.5)
    if (!dorsal && e < 1.3) return "w"
    if (!dorsal && e < 2.4) return "f"
    if (dorsal && p[0] > 19 && p[1] < 6 && edge > 2.4) return "a"
  }
  if (!dorsal) {
    const k = Math.min(2, Math.floor(r / 9))
    return ["p", "P", "r"][k]
  }
  const k = Math.min(6, Math.floor(r / 4.2 + (edge < 4 ? 1 : 0)))
  return ["a", "b", "c", "d", "e", "f", "g"][k]
}

const lick = (x, row, dorsal, w, h) => {
  const t = (x + 0.5) / w
  const c = h / 2 + 0.5 * Math.sin(x * 1.3) * t
  const hw = h / 2 * Math.pow(Math.max(0, 1 - t), 0.6) + 0.2
  const d = Math.abs(row + 0.5 - c)
  if (d > hw) return null
  return t < 0.3 && d < hw * 0.5 ? "s" : t < 0.55 ? "S" : t < 0.8 ? "t" : "T"
}

const tailFlame = (x, row, dorsal, w, h) => {
  const t = (row + 0.5) / h
  const c = w / 2 + 0.6 * Math.sin(row * 0.9) * t
  const hw = w / 2 * Math.pow(Math.max(0, 1 - t), 0.6) + 0.2
  if (Math.abs(x + 0.5 - c) > hw) return null
  return t < 0.3 ? "s" : t < 0.6 ? "S" : "t"
}

const tail = (x, row, dorsal, w, h) => {
  const t = row / h
  const hw = w / 2 * (1 - 0.4 * t)
  if (Math.abs(x + 0.5 - w / 2) > hw) return null
  return x === 0 || x === w - 1 ? "n" : (dorsal ? "f" : "P")
}

const paint = (c) => {
  for (const cube of R.cubes) {
    switch (cube.kind) {
      case "fore": P.plate(c, cube, wing(cube, "fore")); break
      case "hind": P.plate(c, cube, wing(cube, "hind")); break
      case "soulfire": P.plate(c, cube, lick); break
      case "tail": P.plate(c, cube, tail); break
      case "tail_flame": P.plate(c, cube, tailFlame); break
      case "root": P.box(c, cube, "K", "k", "k"); break
      case "chain": P.box(c, cube, "L", "l", "l"); break
      case "lamp": P.box(c, cube, "L", "l", "k"); break
      case "lamp_flame": P.plate(c, cube, (x, row, d, w, h) => row < 1 ? "S" : row < 2 ? "s" : "y"); break
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("psyche_wings", { w: R.texture[0], h: R.texture[1] }, paint)
