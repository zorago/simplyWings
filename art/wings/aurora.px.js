include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("aurora.rig.js")

pal({
  a: "#d8ffe8d0", b: "#7cf8b4c4", c: "#3ce89cb4", d: "#2cc8b0a4", e: "#3c9ce094", f: "#6a6af084",
  g: "#9a5ae870", h: "#b45ae058", i: "#c060d840",
  r: "#f2fff8", R: "#b0f8d4", k: "#e8f0ff", K: "#9ab8f0",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS
const RAMP = ["a", "b", "c", "d", "e", "f", "g", "h", "i"]
const hash = (x, y, s) => {
  const v = Math.sin(x * 12.9898 + y * 78.233 + s * 37.719) * 43758.5453
  return v - Math.floor(v)
}

const light = (cube) => (x, row, dorsal, w, h) => {
  const gx = x + cube.index * 6 + (cube.curtain === "veil" ? 0 : 3)
  const ray = Math.sin(gx * 1.15) + 0.6 * Math.sin(gx * 2.7 + 1)
  const reach = h - 1 - (2.5 + 2 * Math.sin(gx * 0.7) + (ray > 0.8 ? 2 : 0) + hash(gx, 0, 3) * 1.5)
  if (row > reach) return null
  const t = row / Math.max(1, reach)
  let k = Math.floor(t * (RAMP.length - 0.01))
  if (ray > 1.1 && k > 0) k -= 1
  if (ray < -1.1 && k < RAMP.length - 1) k += 1
  if (row === 0) return "a"
  if (!dorsal && k > 0) k -= 1
  return RAMP[Math.max(0, Math.min(RAMP.length - 1, k))]
}

const strips = (c) => {
  for (const st of R.strips) {
    for (const [u0, v0, dorsal] of [[st.ventral[0], st.ventral[1], false], [st.dorsal[0], st.dorsal[1], true]]) {
      let start = 0
      st.segs.forEach((sg, i) => {
        const fn = light({ index: i, curtain: st.curtain })
        const w = i === st.segs.length - 1 ? sg.len + 1 : sg.len
        for (let x = 0; x < w; x++) {
          for (let row = 0; row < sg.h; row++) {
            const col = fn(x, row, dorsal, sg.len + 0.3, sg.h + 0.6)
            if (col) c.px(u0 + start + x, v0 + row, col)
          }
        }
        start += sg.len
      })
    }
  }
}

const paint = (c) => {
  strips(c)
  for (const cube of R.cubes) {
    if (cube.kind === "light") P.plate(c, cube, light(cube))
    else if (cube.kind === "ray") P.box(c, cube, "r", "R", "b")
    else if (cube.kind === "clasp") P.box(c, cube, "k", "k", "K")
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("aurora_wings", { w: R.texture[0], h: R.texture[1] }, paint)
