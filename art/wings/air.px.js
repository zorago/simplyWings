include("lib/rig_pack.js")
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

const ribbon = (cube) => (x, row, dorsal, w, h) => {
  const gx = x + cube.seg * 8
  const fray = cube.last ? x / w : 0
  const thread = (row + Math.floor(gx / 3)) % 2
  if (cube.last && fray > 0.35 && hash(x, row, cube.ribbon) < (fray - 0.35) * 1.5 + (thread ? 0.25 : 0)) return null
  if (cube.last && x === w - 1 && row % 2 === 0) return null
  const streak = Math.sin(gx * 0.45 + row * 2.1 + cube.ribbon * 1.7)
  if (row === 0 && cube.ribbon === 0) return "d"
  if (streak > 0.86) return "e"
  if (streak > 0.55) return "d"
  if (streak < -0.7) return "f"
  return (gx + row) % 3 === 0 ? "c" : (dorsal ? "a" : "b")
}

const edge = (x, row, dorsal, w, h) => {
  if (x > w - 4 && hash(x, row, 9) < (x - (w - 4)) / 4) return null
  return row === 0 ? "e" : (x % 4 === 0 ? "d" : "c")
}

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "ribbon") P.plate(c, cube, ribbon(cube))
    else if (cube.kind === "edge") P.plate(c, cube, edge)
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
