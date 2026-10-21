var PACK = (rig, width, start) => {
  let x = 0, y = start || 0, row = 0
  for (const c of rig.cubes) {
    if (c.uv) continue
    const w = c.to[0] - c.from[0], h = c.to[1] - c.from[1], d = c.to[2] - c.from[2]
    const fw = Math.ceil(2 * (w + d) - 1e-6), fh = Math.ceil(h + d - 1e-6)
    if (x + fw > width) {
      x = 0
      y += row
      row = 0
    }
    c.uv = [x, y]
    x += fw
    row = Math.max(row, fh)
  }
  return y + row
}
