var GEOM = (() => {
  const rad = Math.PI / 180

  const rotate = (p, o, deg) => {
    const a = deg * rad, c = Math.cos(a), s = Math.sin(a)
    const dx = p[0] - o[0], dy = p[1] - o[1]
    return [o[0] + dx * c - dy * s, o[1] + dx * s + dy * c]
  }

  const world = (rig, groupName, p) => {
    let q = p.slice()
    let g = rig.groups.find(x => x.name === groupName)
    while (g) {
      const r = g.rot || [0, 0, 0]
      if (r[2]) q = rotate(q, g.origin, r[2])
      g = g.parent ? rig.groups.find(x => x.name === g.parent) : null
    }
    return q
  }

  const inside = (poly, p) => {
    let hit = false
    for (let i = 0, j = poly.length - 1; i < poly.length; j = i++) {
      const [xi, yi] = poly[i], [xj, yj] = poly[j]
      if ((yi > p[1]) !== (yj > p[1]) && p[0] < (xj - xi) * (p[1] - yi) / (yj - yi) + xi) hit = !hit
    }
    return hit
  }

  const segDist = (p, a, b) => {
    const dx = b[0] - a[0], dy = b[1] - a[1]
    const t = Math.max(0, Math.min(1, ((p[0] - a[0]) * dx + (p[1] - a[1]) * dy) / (dx * dx + dy * dy || 1)))
    return Math.hypot(p[0] - a[0] - t * dx, p[1] - a[1] - t * dy)
  }

  const edgeDist = (poly, p) => {
    let d = Infinity
    for (let i = 0; i < poly.length; i++) d = Math.min(d, segDist(p, poly[i], poly[(i + 1) % poly.length]))
    return d
  }

  const sag = (a, b, depth, toward, steps) => {
    const out = []
    const n = steps || 6
    for (let i = 1; i < n; i++) {
      const t = i / n
      const x = a[0] + (b[0] - a[0]) * t, y = a[1] + (b[1] - a[1]) * t
      const k = depth * Math.sin(Math.PI * t)
      const dx = toward[0] - x, dy = toward[1] - y, l = Math.hypot(dx, dy) || 1
      out.push([x + dx / l * k, y + dy / l * k])
    }
    return out
  }

  const plates = (rig) => rig.cubes.filter(c => c.to[2] - c.from[2] < 0.05)

  const pixels = (rig, cube, fn) => {
    const w = Math.round(cube.to[0] - cube.from[0]), h = Math.round(cube.to[1] - cube.from[1])
    const [u, v] = cube.uv
    for (let row = 0; row < h; row++) {
      for (let col = 0; col < w; col++) {
        const y = cube.to[1] - row - 0.5
        const south = world(rig, cube.group, [cube.from[0] + col + 0.5, y])
        const north = world(rig, cube.group, [cube.to[0] - col - 0.5, y])
        fn(u + w + col, v + row, south, true, col, row)
        fn(u + col, v + row, north, false, w - 1 - col, row)
      }
    }
  }

  return { rad, rotate, world, inside, segDist, edgeDist, sag, plates, pixels }
})()
