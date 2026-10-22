include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("lib/fury.js")
include("lib/fury_px.js")
include("light.rig.js")

pal({
  a: "#b8c4d4", b: "#d6dee8", c: "#e6ecf2", d: "#f2f5f8", e: "#fbfcfd", f: "#ffffff",
  g: "#dfe6f0", h: "#eaeff6", i: "#f4f2fa", j: "#f8eef4",
  k: "#c8d0dc", K: "#9aa6b8",
  p: "#cfe0ff", P: "#f4d8ec", W: "#ffffff",
  l: "#bfe8ff", L: "#f4fcff",
  q: "#b9c0c8", Q: "#a3abb4"
})

const T = {
  edge: "a", edgeV: "k", nearBone: "c", nearBoneV: "h", scale: "p", scaleV: "P", rim: "b", rimV: "g",
  deep: "d", mid: "c", deepV: "i", midV: "j",
  skinTop: "f", skin: "d", skinLow: "b", skinSpeck: "p",
  claw: "K", clawTop: "f", spine: "c", spineTop: "f", spineLow: "a",
  fin: "d", finV: "i", red: "d", redEdge: "a", mark: "f",
  glow: "l", glowHi: "L", glowEdge: "L", sag: 3.4
}
const F = FURY_PAINT(RIG, T)
sprite("light_wings", { w: RIG.texture[0], h: RIG.texture[1] }, F.base)
sprite("light_glow", { w: RIG.texture[0], h: RIG.texture[1] }, F.glow)
