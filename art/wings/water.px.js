include("lib/rig_geom.js")
include("lib/rig_pack.js")
include("water.rig.js")

pal({
  a: "#0e3478d8", b: "#164a9cc8", c: "#2266bcbc", d: "#3184d4b0", e: "#4aa2e4a8", f: "#74c0f0a4",
  A: "#1a4c9cc8", B: "#2462b4c0", C: "#3280cab4", D: "#4c9cdcac", E: "#6ab8eaa8", F: "#94d2f4a4",
  h: "#bfe6fcc0", w: "#ecf8ffd8", W: "#ffffffe8",
  t: "#5ab0ecd0", T: "#a6dcf8e0", s: "#d8f2ffe8",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const y = R.S[1]
const at = (k, q) => GEOM.world(R, "seg_" + k, q)
const top = [], bottom = []
let x = R.S[0]
R.segs.forEach((g, i) => {
  const k = i + 1
  const lift = Math.round(2.6 - 0.5 * i)
  top.push(at(k, [x, y + lift]))
  bottom.push(at(k, [x, y - g.h]))
  x += g.len
})
const last = R.segs.length
const tip = at(last, [x + 2, y - 0.5])
top.push(at(last, [x - 1, y + 0.6]))
const trail = [tip]
const bot = [...bottom].reverse()
let prev = tip
for (const b of bot) {
  trail.push(...GEOM.sag(prev, b, 1.6, [prev[0] * 0.5 + b[0] * 0.5, y + 4], 5), b)
  prev = b
}
const poly = [...top, ...trail, [R.S[0] - 0.5, bottom[0][1]], [R.S[0] - 0.5, y + 2.6]]
const crestLine = [...top, tip]

const lineDist = (p, pts) => {
  let d = Infinity
  for (let i = 0; i + 1 < pts.length; i++) d = Math.min(d, GEOM.segDist(p, pts[i], pts[i + 1]))
  return d
}

const tone = (p, dorsal, phase) => {
  const dc = lineDist(p, crestLine)
  const dt = lineDist(p, trail)
  if (dt < 0.8) return dorsal ? "w" : "W"
  if (dt < 1.7) return "h"
  if (dc < 0.9) return dorsal ? "f" : "F"
  const ripple = Math.sin(p[0] * 0.62 + p[1] * 0.95 - phase) + Math.sin(p[0] * 0.27 - p[1] * 0.6 - 2 * phase)
  const swell = Math.sin(p[0] * 0.35 - phase)
  if (dc > 1.6 && dc < 2.6 + 0.8 * swell && dt > 1.7) return dorsal ? "d" : "E"
  if (ripple > 1.55) return dorsal ? "f" : "h"
  if (ripple > 1.2) return dorsal ? "e" : "F"
  const depth = Math.min(5, Math.floor(dc / 2.3))
  const ramp = dorsal ? ["e", "d", "c", "b", "a", "a"] : ["F", "E", "D", "C", "B", "A"]
  return ramp[depth]
}

const paint = (c) => {
  const sh = R.sheet
  for (let f = 0; f < sh.frames; f++) {
    const phase = (f / sh.frames) * Math.PI * 2
    for (const [u0, v0, dorsal] of [[sh.ventral[0], sh.ventral[1], false], [sh.dorsal[0], sh.dorsal[1], true]]) {
      for (let u = 0; u < sh.w; u++) {
        for (let v = 0; v < sh.h; v++) {
          const p = [sh.x + u + 0.5, sh.y - v - 0.5]
          if (GEOM.inside(poly, p)) c.px(u0 + u + f * sh.step[0], v0 + v + f * sh.step[1], tone(p, dorsal, phase))
        }
      }
    }
  }
  for (const cube of R.cubes) {
    if (cube.kind === "water") continue
    const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
    const fw = Math.ceil(2 * (w + d) - 1e-6), fh = Math.ceil(h + d - 1e-6)
    const [u, v] = cube.uv
    const [top, mid, low] = cube.kind === "crest" ? ["s", "T", "t"] : cube.kind === "spring" ? ["W", "s", "T"] : ["W", "T", "t"]
    c.rect(u, v, fw, fh, mid)
    c.hline(u, v, fw, top)
    c.hline(u, v + fh - 1, fw, low)
    if (cube.kind === "crest") for (let i = 2; i < fw; i += 5) c.px(u + i, v + 1, "W")
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let i = 0; i < 32; i += 4) c.vline(i, R.texture[1] - 16, 16, "Q")
}

sprite("water_wings", { w: R.texture[0], h: R.texture[1] }, paint)
