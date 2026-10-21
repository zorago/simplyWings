include("lib/rig_pack.js")
include("lib/bird.js")
include("lib/feathers.js")
include("copper.rig.js")

pal({
  a: "#4a2616", b: "#6e3a22", c: "#9a4e2c", d: "#c26a3a", e: "#e08a52", f: "#f6b07a",
  g: "#2e6a58", G: "#3e8a74", h: "#5aa88a", H: "#7ccaa8",
  k: "#3a2a20", K: "#5a4a3a", y: "#ffd27a", Y: "#fff0b8", o: "#c47a2a",
  l: "#ffe9a0e0", L: "#fff6d0f0",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG
const P = FEATHERS

const patina = (seed) => {
  const spots = []
  let r = seed
  const rnd = () => { r = (r * 9301 + 49297) % 233280; return r / 233280 }
  for (let i = 0; i < 3; i++) spots.push([rnd(), rnd(), 0.07 + 0.07 * rnd()])
  return (cube, x, row, w, h) => {
    const ox = (cube.from[0] * 7 + cube.from[1] * 3) % 1
    return spots.some(([sx, sy, sr]) => {
      const dx = (x / w + ox) % 1 - sx, dy = row / h - sy
      return dx * dx + dy * dy < sr * sr
    })
  }
}

const metal = (cube, spot) => (x, row, dorsal, w, h) => {
  const primary = cube.kind === "primary"
  if (primary) {
    if (x === w - 1 && (row === 0 || row === h - 1)) return null
    if (x === w - 2 && row === h - 1) return null
  } else {
    const fw = cube.kind === "secondaries" || cube.kind === "hand_sheet" ? 3 : 4
    const f = Math.floor(x / fw)
    const len = h - (f % 2)
    if (row >= len) return null
    if (row === len - 1 && (x % fw === 0)) return null
    if (x % fw === fw - 1) return dorsal ? "b" : "c"
    if (row === len - 1) return dorsal ? "a" : "b"
    if ((x % fw === 1) && row === 1) return dorsal ? "f" : "e"
  }
  if (primary && row === 0) return dorsal ? "e" : "d"
  if (primary && row === h - 1) return dorsal ? "a" : "b"
  if (primary && row === 1 && x % 4 === 1 && x < w - 2) return dorsal ? "f" : "e"
  const green = spot(cube, x, row, w, h)
  if (green) return dorsal ? ((x + row) % 3 === 0 ? "H" : "h") : "G"
  return dorsal ? (row < 2 ? "e" : "d") : "c"
}

const paintSide = (side) => (c) => {
  const spot = patina(side === "right" ? 11 : 47)
  for (const cube of R.cubes.filter(c => c.leftUv)) {
    const uv = side === "right" ? cube.uv : cube.leftUv
    P.plate(c, { ...cube, uv }, metal(cube, spot))
  }
}

const parts = (glow) => (c) => {
  for (const cube of R.cubes) {
    if (glow) {
      if (cube.kind === "bulb") {
        const [u, v] = cube.uv
        for (let i = 0; i < 8; i++) for (let j = 0; j < 4; j++) c.px(u + i, v + j, (i + j) % 2 ? "l" : "L")
      }
      continue
    }
    if (cube.kind === "bone") P.box(c, cube, "e", "c", "a")
    else if (cube.kind === "bulb") {
      P.box(c, cube, "Y", "y", "o")
      const [u, v] = cube.uv
      for (let i = 0; i < 8; i += 2) c.px(u + i, v + 2, "o")
    } else if (cube.kind === "cap") P.box(c, cube, "e", "b", "a")
    else if (cube.kind === "rod") P.box(c, cube, "K", "k", "k")
    else if (cube.kind === "gear") {
      P.box(c, cube, "f", "d", "a")
      const [u, v] = cube.uv
      for (let i = 0; i < 10; i += 2) c.px(u + i, v + 1, "a")
    }
  }
  if (!glow) {
    c.rect(0, R.texture[1] - 16, 32, 16, "q")
    for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
  }
}

sprite("copper_wings", { w: R.texture[0], h: R.texture[1] }, paintSide("right"), paintSide("left"), parts(false))
sprite("copper_glow", { w: R.texture[0], h: R.texture[1] }, parts(true))
