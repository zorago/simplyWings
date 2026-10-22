include("lib/rig_pack.js")
include("lib/rig_geom.js")
include("lib/feathers.js")
include("dragonfly.rig.js")

pal({
  a: "#5a2c0cf0", b: "#8c4a14e0", c: "#c07a26c8", s: "#3e1c06f0", d: "#c86a18c0",
  m: "#e8901cb8", n: "#f0a42cb0", o: "#f6b840a8", p: "#fbca56a0", r: "#ffdc7698",
  w: "#fff2c8b0", W: "#fffae0c8",
  g: "#e8b04a34", G: "#f4cc7024",
  h: "#2e1c0c", H: "#4a2e14", i: "#6e4820",
  e: "#6a4a1c", E: "#9c7430", f: "#c8a048", F: "#ecd07a",
  A: "#a84e10", B: "#d0741c", C: "#eca23a", D: "#fbd27a", k: "#3a1a06",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const columns = (shape, w, h) => {
  const out = []
  for (let x = 0; x < w; x++) {
    let top = null, bottom = null
    for (let row = 0; row < h; row++) {
      if (GEOM.inside(shape, [x + 0.5, row + 0.5])) {
        if (top === null) top = row
        bottom = row
      }
    }
    out.push([top, bottom])
  }
  return out
}

const membrane = (cube, o) => {
  const w = Math.round(cube.to[0] - cube.from[0]), h = Math.round(cube.to[1] - cube.from[1])
  const col = columns(cube.shape, w, h)
  const tones = ["m", "n", "o", "p", "r"]
  return (x, row, dorsal) => {
    const p = [x + 0.5, row + 0.5]
    if (!GEOM.inside(cube.shape, p)) return null
    const [top, bottom] = col[x]
    const edge = GEOM.edgeDist(cube.shape, p)
    const t = x / w
    if (row <= top) return "a"
    const out = (dx, dy) => !GEOM.inside(cube.shape, [x + 0.5 + dx, row + 0.5 + dy])
    if (out(1, 0) || out(-1, 0) || out(0, 1) || out(0, -1)) return row <= top + 1 ? "a" : (dorsal ? "b" : "c")
    if (x >= o.stigma[0] && x <= o.stigma[1] && row <= top + 1) return "s"
    if (x === o.nodus && row <= top + Math.ceil((bottom - top) * 0.7)) return "a"
    const span = Math.max(1, bottom - top)
    for (const v of o.veins) {
      const yv = top + v * span
      if (Math.abs(row + 0.5 - yv) < 0.5 && x > 0) return v < 0.5 ? "a" : "b"
    }
    const bands = o.veins.map(v => top + v * span)
    const zone = bands.filter(y => row + 0.5 > y).length
    const step = zone >= 2 ? 3 : 5
    if (x > 1 && (x + zone) % step === 0 && edge > 1.2) return dorsal ? "c" : "d"
    for (const [sx, sy] of o.bubbles) if (x === sx && row === sy) return dorsal ? "w" : "W"
    for (const [cx, cy, rr] of o.spots) if (Math.hypot(x + 0.5 - cx, row + 0.5 - cy) < rr) return "d"
    const k = Math.min(tones.length - 1, Math.floor(t * tones.length + (dorsal ? 0 : 0.6)))
    return tones[k]
  }
}

const ghost = (cube) => (x, row) => {
  if (!GEOM.inside(cube.shape || R.cubes.find(c => c.kind === cube.kind.replace("_ghost", "")).shape, [x + 0.5, row + 0.5])) return null
  return (x * 3 + row) % 5 === 0 ? "G" : "g"
}

const amber = (cube) => (c) => {
  P.box(c, cube, "D", "C", "A")
  const [u, v] = cube.uv
  const d = cube.to[2] - cube.from[2]
  const fu = u + Math.ceil(d), fv = v + Math.ceil(d)
  c.rect(fu, fv, 3, 4, "B")
  c.px(fu + 1, fv + 1, "k")
  c.px(fu + 1, fv + 2, "k")
  c.px(fu, fv + 1, "A")
  c.px(fu + 2, fv + 1, "A")
  c.px(fu + 2, fv, "D")
  c.px(fu + 1, fv + 3, "k")
}

const paint = (c) => {
  const fore = R.cubes.find(x => x.kind === "fore")
  const hind = R.cubes.find(x => x.kind === "hind")
  for (const cube of R.cubes) {
    switch (cube.kind) {
      case "fore":
        P.plate(c, cube, membrane(cube, { veins: [0.3, 0.64], nodus: 11, stigma: [19, 21],
          bubbles: [[6, 3], [15, 4], [21, 2]], spots: [[11.5, 2.4, 0.9]] }))
        break
      case "hind":
        P.plate(c, cube, membrane(cube, { veins: [0.24, 0.55], nodus: 9, stigma: [17, 19],
          bubbles: [[4, 5], [12, 3], [18, 4]], spots: [[9.5, 2.4, 0.9], [4.5, 7.2, 1.2]] }))
        break
      case "fore_ghost":
        P.plate(c, cube, (x, row) => GEOM.inside(fore.shape, [x + 0.5, row + 0.5]) ? ((x * 3 + row) % 5 === 0 ? "G" : "g") : null)
        break
      case "hind_ghost":
        P.plate(c, cube, (x, row) => GEOM.inside(hind.shape, [x + 0.5, row + 0.5]) ? ((x * 3 + row) % 5 === 0 ? "G" : "g") : null)
        break
      case "costa": P.box(c, cube, "i", "H", "h"); break
      case "stigma": P.box(c, cube, "H", "h", "h"); break
      case "hinge": P.box(c, cube, "i", "H", "h"); break
      case "setting": P.box(c, cube, "F", "f", "e"); break
      case "setting_tip": P.box(c, cube, "F", "E", "e"); break
      case "amber": amber(cube)(c); break
    }
  }
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("dragonfly_wings", { w: R.texture[0], h: R.texture[1] }, paint)
