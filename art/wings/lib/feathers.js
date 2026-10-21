var FEATHERS = (() => {
  const size = (cube) => [Math.round(cube.to[0] - cube.from[0]), Math.round(cube.to[1] - cube.from[1])]

  const plate = (c, cube, fn) => {
    const [w, h] = size(cube)
    const [u, v] = cube.uv
    for (let row = 0; row < h; row++) {
      for (let x = 0; x < w; x++) {
        const s = fn(x, row, true, w, h)
        if (s) c.px(u + w + x, v + row, s)
        const n = fn(x, row, false, w, h)
        if (n) c.px(u + w - 1 - x, v + row, n)
      }
    }
  }

  const box = (c, cube, top, mid, low) => {
    const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
    const fw = Math.ceil(2 * (w + d) - 1e-6), fh = Math.ceil(h + d - 1e-6)
    const [u, v] = cube.uv
    c.rect(u, v, fw, fh, mid)
    c.hline(u, v, fw, top)
    c.hline(u, v + fh - 1, fw, low)
  }

  const primary = (t, o) => (x, row, dorsal, w, h) => {
    const p = dorsal ? t.d : t.v
    const L = w
    const emargAt = o.emarg ? Math.round(L * (1 - o.emarg)) : L
    const narrow = x >= emargAt
    const last = h - 1
    if (narrow && row >= h - (o.cut || 1)) return null
    if (x === L - 1 && (row === 0 || row >= (narrow ? h - (o.cut || 1) - 1 : last))) return null
    if (x === L - 2 && row === last && !narrow) return null
    const tipZone = x >= L - (o.tip || 3)
    if (row === 0) return tipZone ? p[4] : p[3]
    if (row === 1) return tipZone ? p[0] : p[2]
    if (o.bar && (x + (o.barOffset || 0)) % o.bar < (o.barWidth || 1)) return p[0]
    return tipZone ? p[0] : (row % 2 === 0 ? p[1] : p[1])
  }

  const sheet = (t, o) => (x, row, dorsal, w, h) => {
    const p = dorsal ? t.d : t.v
    const fw = o.fw || 2
    const k = Math.floor(x / fw)
    const len = h - (o.steps ? o.steps[k % o.steps.length] : 0)
    if (row >= len) return null
    const edge = x % fw === fw - 1
    if (row === len - 1) return (x % fw === 0 || edge) && fw > 1 && o.round ? null : p[4]
    if (row === len - 2 && o.tipBand) return p[0]
    if (o.bar && (row + (o.barOffset || 0)) % o.bar < (o.barWidth || 1) && row > 1) return p[0]
    if (row < (o.cap || 0)) return p[3]
    return k % 2 === 0 ? (edge ? p[2] : p[1]) : (edge ? p[1] : p[2])
  }

  return { size, plate, box, primary, sheet }
})()
