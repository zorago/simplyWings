include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("lib/fury.js")
include("lib/fury_px.js")
include("night.rig.js")

pal({
  a: "#07080b", b: "#0d0f14", c: "#13161d", d: "#1a1e27", e: "#222733", f: "#2c3240",
  g: "#151820", h: "#1c2029", i: "#252a36", j: "#30364a",
  k: "#3a4256", K: "#0a0b0e",
  r: "#a8201c", R: "#d03028", W: "#f2f0ea",
  l: "#9ad8ff", L: "#e8f8ff",
  q: "#b9c0c8", Q: "#a3abb4"
})

const T = {
  edge: "a", edgeV: "g", nearBone: "c", nearBoneV: "i", scale: "b", scaleV: "h", rim: "b", rimV: "h",
  deep: "d", mid: "c", deepV: "j", midV: "i",
  skinTop: "e", skin: "c", skinLow: "a", skinSpeck: "d",
  claw: "k", clawTop: "W", spine: "b", spineTop: "e", spineLow: "a",
  fin: "c", finV: "i", red: "r", redEdge: "R", mark: "W",
  glow: "l", glowHi: "L", sag: 3
}
const F = FURY_PAINT(RIG, T)
sprite("night_wings", { w: RIG.texture[0], h: RIG.texture[1] }, F.base)
sprite("night_glow", { w: RIG.texture[0], h: RIG.texture[1] }, F.glow)
