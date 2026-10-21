include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("squirrel.rig.js")

pal({
  a: "#3e3a35", b: "#55504a", c: "#6c665e", d: "#837c72", e: "#9a9286", f: "#b0a796",
  w: "#e6dece", W: "#d4cab6", x: "#bfb39c", k: "#2a2622", p: "#c9a8a0",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS
const lineDist = (p, pts) => {
  let d = Infinity
  for (let i = 0; i + 1 < pts.length; i++) d = Math.min(d, GEOM.segDist(p, pts[i], pts[i + 1]))
  return d
}

const membrane = (c) => {
  for (const cube of GEOM.plates(R)) {
    if (cube.kind !== "membrane") continue
    GEOM.pixels(R, cube, (x, y, p, dorsal) => {
      if (!GEOM.inside(R.panel, p)) return
      const ed = lineDist(p, R.edge)
      const bone = Math.abs(p[1] - 22.5) < 1.3 && p[0] <= R.W[0]
      let k
      if (ed < 1.3) k = "w"
      else if (ed < 2.1) k = dorsal ? "b" : "W"
      else if (bone) k = dorsal ? "c" : "x"
      else {
        const streak = Math.floor(p[0] * 0.6 + p[1] * 0.3) % 5 === 0
        k = dorsal ? (streak ? "d" : "e") : (streak ? "W" : "w")
      }
      c.px(x, y, k)
    })
  }
}

const tail = (wide) => (x, row, dorsal, w, h) => {
  const cx = (w - 1) / 2
  const half = wide ? Math.min(w / 2, 1.5 + row * 0.5) : (w / 2) * (1 - Math.pow(row / h, 3) * 0.6)
  if (Math.abs(x - cx) > half) return null
  if (Math.abs(x - cx) > half - 1) return dorsal ? "f" : "W"
  if (Math.abs(x - cx) < 0.8) return dorsal ? "b" : "x"
  return dorsal ? ((x + row) % 3 ? "d" : "c") : "w"
}

const paint = (c) => {
  membrane(c)
  for (const cube of R.cubes) {
    if (cube.kind === "fur") P.box(c, cube, "e", "d", "c")
    else if (cube.kind === "spur") P.box(c, cube, "f", "e", "x")
    else if (cube.kind === "paw") P.box(c, cube, "p", "c", "b")
    else if (cube.kind === "tail") P.plate(c, cube, tail(true))
    else if (cube.kind === "tail_tip") P.plate(c, cube, tail(false))
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("squirrel_wings", { w: R.texture[0], h: R.texture[1] }, paint)
