include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("kite.rig.js")

pal({
  r: "#b8261e", R: "#d83a2a", t: "#8e1c16", w: "#efe8da", W: "#fbf7ee", v: "#d8d0c0",
  s: "#8a6a3a", S: "#b8935a", u: "#d8b878", k: "#5a4a32", n: "#e8e0c8",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS
const tip = GEOM.world(R, "outer", [R.J[0] + 9, 22.5])
const strutEnd = GEOM.world(R, "strut", [R.S[0] + 17, 22.5])
const lead = [[R.S[0], 22.9], [tip[0], tip[1] + 0.3]]
const trail = [tip, ...GEOM.sag(tip, strutEnd, 1.2, [12, 20], 6), strutEnd, ...GEOM.sag(strutEnd, [R.S[0], 6.5], 1.5, [12, 20], 6), [R.S[0], 6.5]]
const sail = [...lead, ...trail]

const paintSail = (c) => {
  for (const cube of R.cubes.filter(c => c.kind === "sail")) {
    GEOM.pixels(R, cube, (x, y, p, dorsal) => {
      if (!GEOM.inside(sail, p)) return
      const ed = GEOM.edgeDist(sail, p)
      const stripe = Math.floor((p[0] - R.S[0]) / 3) % 2 === 0
      let k
      if (ed < 0.7) k = "t"
      else k = stripe ? (dorsal ? "r" : "R") : (dorsal ? "w" : "W")
      if (ed >= 0.7 && ed < 1.4 && !stripe) k = "v"
      c.px(x, y, k)
    })
  }
}

const ribbon = (x, row, dorsal, w, h) => (row + x) % 4 < 2 ? (dorsal ? "R" : "r") : (dorsal ? "W" : "w")
const bow = (x, row, dorsal, w, h) => {
  const cx = (w - 1) / 2
  if (Math.abs(x - cx) < 0.6) return "t"
  return row === 0 && Math.abs(x - cx) > 1 ? null : (dorsal ? "R" : "r")
}

const paint = (c) => {
  paintSail(c)
  for (const cube of R.cubes) {
    if (cube.kind === "stick") P.box(c, cube, "u", "S", "s")
    else if (cube.kind === "string") P.box(c, cube, "n", "k", "k")
    else if (cube.kind === "ribbon") P.plate(c, cube, ribbon)
    else if (cube.kind === "bow") P.plate(c, cube, bow)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("kite_wings", { w: R.texture[0], h: R.texture[1] }, paint)
