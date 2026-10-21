pal({
  k: "#2c3036", d: "#454a52", m: "#737a83", M: "#858c95", n: "#666d76", l: "#a9afb7", L: "#9aa0a8",
  h: "#c8cdd3", s: "#dfe3e7", t: "#50565e", b: "#6b5a48", B: "#8a7660", c: "#3a3128"
})

const plate = (x0, y0, w, h, dorsal, steps) => (c) => {
  const a = dorsal ? "m" : "l"
  const b = dorsal ? "n" : "L"
  const tip = dorsal ? "d" : "M"
  const shine = dorsal ? "M" : "h"
  for (let col = 0; col < w; col++) {
    const x = x0 + col
    const len = h - steps[col % steps.length]
    c.vline(x, y0, len, Math.floor(col / 1) % 2 === 0 ? a : b)
    c.px(x, y0 + len - 1, tip)
    for (let y = y0 + len; y < y0 + h; y++) c.px(x, y, "_")
  }
  c.hline(x0, y0, w, shine)
  c.hline(x0, y0 + 1, w, dorsal ? "M" : "s")
}

const coverts = (x0, y0, dorsal) => (c) => {
  c.rect(x0, y0, 3, 3, dorsal ? "M" : "h")
  c.hline(x0, y0 + 2, 3, dorsal ? "n" : "l")
  c.px(x0 + 1, y0, dorsal ? "l" : "s")
}

const primary = (x0, y0, len, dorsal, tipAtEnd) => (c) => {
  for (let i = 0; i < len; i++) {
    const x = tipAtEnd ? x0 + i : x0 + len - 1 - i
    const outer = i >= len - 3
    c.px(x, y0, outer ? "t" : dorsal ? "M" : "h")
    c.px(x, y0 + 1, outer ? (dorsal ? "k" : "n") : dorsal ? "m" : "l")
  }
  c.px(tipAtEnd ? x0 + len - 1 : x0, y0 + 1, "_")
}

sprite("basic_wings", { w: 64, h: 32 },
  plate(0, 0, 5, 6, false, [0, 1, 0, 1, 2]), plate(5, 0, 5, 6, true, [2, 1, 0, 1, 0]),
  plate(12, 0, 5, 8, false, [0, 1, 0, 1, 0]), plate(17, 0, 5, 8, true, [0, 1, 0, 1, 0]),
  coverts(24, 0, false), coverts(27, 0, true),
  primary(30, 0, 11, false, false), primary(41, 0, 11, true, true),
  primary(30, 3, 10, false, false), primary(40, 3, 10, true, true),
  primary(30, 6, 9, false, false), primary(39, 6, 9, true, true),
  rect(0, 16, 32, 8, "b"),
  hline(0, 16, 32, "B"), hline(0, 20, 32, "B"),
  hline(0, 23, 32, "c"))
