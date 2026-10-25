include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("air.rig.js")

pal({
  g: "#a6dcf85a", a: "#bfe8ff66", b: "#d2f0ff80", c: "#e6f7ff9c", d: "#f6fdffb8", e: "#ffffffd4", w: "#ffffffee",
  G: "#8ccbf066", A: "#a4d8f672", B: "#b8e2fa8c", C: "#cdecfca8", D: "#e2f5ffc4", E: "#f4fcffdc",
  r: "#f2fbff", R: "#cbeafb", s: "#9ed6f2", S: "#78c0e6",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG

const lineDist = (p, pts) => {
  let d = Infinity
  for (let i = 0; i + 1 < pts.length; i++) d = Math.min(d, GEOM.segDist(p, pts[i], pts[i + 1]))
  return d
}

const STYLE = {
  drift: { base: ["g", "G"], body: ["a", "A"], streak: ["c", "C"], edge: ["d", "D"], lanes: 5, speed: 1 },
  breeze: { base: ["a", "A"], body: ["b", "B"], streak: ["d", "D"], edge: ["e", "E"], lanes: 4, speed: 1 },
  gust: { base: ["b", "B"], body: ["c", "C"], streak: ["e", "E"], edge: ["w", "E"], lanes: 3, speed: 1 }
}

const layer = (c, L) => {
  const sh = L.sheet
  const top = L.a.map(q => [q[1], q[2]])
  const bottom = L.b.map(q => [q[1], q[2]])
  const poly = [...top, ...bottom.slice().reverse()]
  const st = STYLE[L.name]
  for (let f = 0; f < sh.frames; f++) {
    const phase = (f / sh.frames) * Math.PI * 2
    for (const [u0, v0, dorsal] of [[sh.ventral[0], sh.ventral[1], false], [sh.dorsal[0], sh.dorsal[1], true]]) {
      const k = dorsal ? 0 : 1
      for (let u = 0; u < sh.w; u++) {
        for (let v = 0; v < sh.h; v++) {
          const p = [sh.x + u + 0.5, sh.y - v - 0.5]
          if (!GEOM.inside(poly, p)) continue
          const dt = lineDist(p, top)
          const db = lineDist(p, bottom)
          const t = dt / (dt + db + 1e-6)
          const along = (p[0] - R.S[0]) / (R.end - R.S[0])
          if (db < 2.2) {
            const wisp = Math.sin(p[0] * 1.15 - phase * 2 + Math.floor(t * 9) * 1.7)
            if (wisp < -0.15 - db * 0.3) continue
          }
          if (along > 0.86 && Math.sin(p[1] * 2.3 + phase) > 0.55 && dt > 0.8) continue
          let col = t < 0.5 ? st.body[k] : st.base[k]
          if (dt < 0.9) col = st.edge[k]
          const lane = t * st.lanes + 0.35 * Math.sin(p[0] * 0.35 + t * 4)
          const inLane = Math.abs(lane - Math.round(lane)) < 0.16 && t > 0.08
          const dash = Math.sin(p[0] * 0.55 - phase * st.speed + Math.round(lane) * 2.1)
          if (inLane && dash > 0.1) col = st.streak[k]
          if (inLane && dash > 0.82) col = st.edge[k]
          c.px(u0 + u + f * sh.step[0], v0 + v + f * sh.step[1], col)
        }
      }
    }
  }
}

const paint = (c) => {
  for (const L of R.layers) layer(c, L)
  for (const cube of R.cubes) {
    const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
    const fw = Math.ceil(2 * (w + d) - 1e-6), fh = Math.ceil(h + d - 1e-6)
    const [u, v] = cube.uv
    if (cube.kind === "vortex") {
      for (let i = 0; i < fw; i++) for (let j = 0; j < fh; j++) c.px(u + i, v + j, (i + j) % 3 === 0 ? "r" : (i + 2 * j) % 4 === 0 ? "s" : "R")
    } else if (cube.kind === "core") {
      c.rect(u, v, fw, fh, "r")
    } else {
      c.rect(u, v, fw, fh, "e")
      c.hline(u, v, fw, "w")
      for (let i = 1; i < fw; i += 4) c.px(u + i, v + fh - 1, "d")
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let i = 0; i < 32; i += 4) c.vline(i, R.texture[1] - 16, 16, "Q")
}

sprite("air_wings", { w: R.texture[0], h: R.texture[1] }, paint)
