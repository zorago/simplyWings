include("lib/rig_pack.js")
include("lib/feathers.js")
include("bee.rig.js")

pal({
  m: "#f4f1e48c", M: "#fbf8ee70", e: "#d9cfb8b0", v: "#6e4a28d0", V: "#9a704090",
  k: "#2a1c12", K: "#3c2a1a", y: "#e8b828", Y: "#f6d24a",
  g: "#e6e2d438", G: "#f0ece022",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const oval = (w, h, x, row) => {
  const cx = (w - 1) / 2 + 0.6, cy = (h - 1) / 2
  const dx = (x - cx) / (w / 2 + 0.3), dy = (row - cy) / (h / 2 + 0.2)
  const base = x < 2 ? Math.abs(row - cy) <= 1 : true
  return dx * dx + dy * dy <= 1 && base
}

const wing = (cells) => (x, row, dorsal, w, h) => {
  if (!oval(w, h, x, row)) return null
  const edge = !oval(w, h, x + 1, row) || !oval(w, h, x, row - 1) || !oval(w, h, x, row + 1)
  if (edge) return row <= 1 && x < w * 0.7 ? "v" : "e"
  if (row === cells.mid && x <= cells.midEnd) return x < 3 ? "v" : "V"
  for (const [x0, y0, x1, y1] of cells.boxes) {
    const onX = (x === x0 || x === x1) && row >= y0 && row <= y1
    const onY = (row === y0 || row === y1) && x >= x0 && x <= x1
    if (onX || onY) return "V"
  }
  return (x + row) % 4 === 0 ? "M" : "m"
}

const foreVeins = { mid: 3, midEnd: 5, boxes: [[5, 1, 8, 3], [8, 2, 10, 4]] }
const hindVeins = { mid: 2, midEnd: 4, boxes: [[4, 1, 6, 2]] }

const ghost = (x, row, dorsal, w, h) => {
  if (!oval(w, h, x, row)) return null
  return (x + row) % 3 === 0 ? "G" : "g"
}

const paint = (c) => {
  for (const cube of R.cubes) {
    if (cube.kind === "hinge") { P.box(c, cube, "Y", "K", "k"); continue }
    const fn = {
      fore: wing(foreVeins),
      hind: wing(hindVeins),
      fore_ghost: ghost,
      hind_ghost: ghost
    }[cube.kind]
    P.plate(c, cube, fn)
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("bee_wings", { w: R.texture[0], h: R.texture[1] }, paint)
