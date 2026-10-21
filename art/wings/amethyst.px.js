include("lib/rig_pack.js")
include("amethyst.rig.js")

pal({
  a: "#2a1546", b: "#3e2066", c: "#56308c", d: "#7046b4", e: "#8e62d6", f: "#b088f0", g: "#d6b6ff", h: "#f2e4ff",
  x: "#c79cffc0", X: "#ecd8ffe8",
  q: "#b9c0c8", Q: "#a3abb4"
})

const R = RIG

const faces = (cube) => {
  const w = cube.to[0] - cube.from[0], h = cube.to[1] - cube.from[1], d = cube.to[2] - cube.from[2]
  const [u, v] = cube.uv
  const W = Math.ceil(w - 1e-6), Hh = Math.ceil(h - 1e-6), D = Math.ceil(d - 1e-6)
  return {
    up: [u + D, v, W, D], down: [u + D + W, v, W, D],
    east: [u, v + D, D, Hh], north: [u + D, v + D, W, Hh], west: [u + D + W, v + D, D, Hh], south: [u + 2 * D + W, v + D, W, Hh],
    all: [u, v, Math.ceil(2 * (w + d) - 1e-6), Math.ceil(h + d - 1e-6)]
  }
}

const crystal = (cube, glowOnly) => (c) => {
  const f = faces(cube)
  if (glowOnly) {
    if (!cube.glow) return
    const [x, y, w, h] = f.south
    for (let i = 0; i < w; i++) for (let j = 0; j < h; j++) if ((i + j) % 3 !== 0 && j > 0 && j < h - 1 && i > 0) c.px(x + i, y + j, (i + j) % 2 ? "x" : "X")
    const [nx, ny, nw, nh] = f.north
    for (let i = 1; i < nw; i++) for (let j = 1; j < nh - 1; j++) if ((i + j) % 3 === 1) c.px(nx + i, ny + j, "x")
    return
  }
  const tone = (name) => ({ up: ["g", "f"], down: ["b", "a"], east: ["c", "b"], west: ["c", "b"], north: ["d", "c"], south: ["e", "d"] })[name]
  const tip = cube.kind === "tip", bar = cube.kind === "bar"
  for (const name of ["up", "down", "east", "west", "north", "south"]) {
    const [x, y, w, h] = f[name]
    const [light, dark] = tone(name)
    for (let i = 0; i < w; i++) {
      for (let j = 0; j < h; j++) {
        let k = (i + j) % 4 === 0 ? dark : light
        if (bar) k = (i % 3 === 0) ? dark : light
        if (!tip && !bar && Math.abs(i - j * (w / Math.max(1, h)) * 0.5) < 0.6) k = name === "south" ? "g" : light
        if (tip && name === "south") k = i > w / 2 ? "h" : "f"
        if (j === 0 && (name === "south" || name === "north")) k = name === "south" ? "f" : "e"
        c.px(x + i, y + j, k)
      }
    }
  }
  if (cube.glow && !tip) {
    const [x, y, w, h] = f.south
    for (let i = 1; i < w - 1; i += 2) c.px(x + i, y + Math.floor(h / 2), "h")
  }
}

const mannequin = (c) => {
  c.rect(0, R.texture[1] - 16, 32, 16, "q")
  for (let x = 0; x < 32; x += 4) c.vline(x, R.texture[1] - 16, 16, "Q")
}

sprite("amethyst_wings", { w: R.texture[0], h: R.texture[1] }, ...R.cubes.map(cube => crystal(cube, false)), mannequin)
sprite("amethyst_glow", { w: R.texture[0], h: R.texture[1] }, ...R.cubes.map(cube => crystal(cube, true)))
