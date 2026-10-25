include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("air.rig.js")

pal({
  a: "#ffffff30", b: "#eef8ff40", c: "#dff2ff52", d: "#ffffff74", e: "#ffffffa8", f: "#c8ecff66",
  r: "#f2fbff", R: "#cbeafb", s: "#9ed6f2", S: "#78c0e6",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS
const hash = (x, y, s) => {
  const v = Math.sin(x * 12.9898 + y * 78.233 + s * 37.719) * 43758.5453
  return v - Math.floor(v)
}

const ribbon = (rb) => (x, row, dorsal) => {
  const lastLen = rb.len[rb.len.length - 1]
  const start = rb.total - lastLen
  const fray = x >= start ? (x - start) / lastLen : 0
  const thread = (row + Math.floor(x / 3)) % 2
  if (fray > 0.35 && hash(x, row, rb.index + (rb.sail === "sail" ? 0 : 7)) < (fray - 0.35) * 1.5 + (thread ? 0.25 : 0)) return null
  if (x >= rb.total - 1 && row % 2 === 0) return null
  const streak = Math.sin(x * 0.45 + row * 2.1 + rb.index * 1.7)
  if (row === 0 && rb.index === 0) return "d"
  if (streak > 0.86) return "e"
  if (streak > 0.55) return "d"
  if (streak < -0.7) return "f"
  return (x + row) % 3 === 0 ? "c" : (dorsal ? "a" : "b")
}

const edge = (x, row, dorsal, w, h) => {
  if (x > w - 4 && hash(x, row, 9) < (x - (w - 4)) / 4) return null
  return row === 0 ? "e" : (x % 4 === 0 ? "d" : "c")
}

const paint = (c) => {
  for (const rb of R.ribbons) {
    const fn = ribbon(rb)
    for (const [u0, v0, dorsal] of [[rb.ventral[0], rb.ventral[1], false], [rb.dorsal[0], rb.dorsal[1], true]]) {
      for (let x = 0; x < rb.w; x++) for (let row = 0; row < Math.ceil(rb.h); row++) {
        const col = fn(x, row, dorsal)
        if (col) c.px(u0 + x, v0 + row, col)
      }
    }
  }
  for (const cube of R.cubes) {
    if (cube.kind === "edge") P.plate(c, cube, edge)
    else if (cube.kind === "root") {
      P.box(c, cube, "r", "R", "s")
      const [u, v] = cube.uv
      c.px(u + 2, v + 2, "S")
      c.px(u + 4, v + 1, "s")
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("air_wings", { w: R.texture[0], h: R.texture[1] }, paint)
