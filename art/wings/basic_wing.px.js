pal({
  k: "#2c3036", d: "#454a52", m: "#737a83", M: "#858c95", n: "#666d76", l: "#a9afb7", L: "#9aa0a8",
  h: "#c8cdd3", s: "#dfe3e7", t: "#50565e", T: "#5d636b", b: "#6b5a48", B: "#8a7660", c: "#3a3128"
})

const plate = (x0, y0, w, h, dorsal, steps, dark) => (c) => {
  const a = dorsal ? (dark ? "n" : "m") : "l"
  const b = dorsal ? (dark ? "T" : "n") : "L"
  const tip = dorsal ? "d" : "M"
  const shine = dorsal ? "M" : "h"
  for (let col = 0; col < w; col++) {
    const feather = Math.floor(col / 2)
    const x = x0 + col
    const len = h - steps[feather % steps.length]
    c.vline(x, y0, len, feather % 2 === 0 ? a : b)
    if (col % 2 === 0) c.vline(x, y0 + 2, len - 4, dorsal ? "M" : "h")
    c.px(x, y0 + len - 1, tip)
    c.px(x, y0 + len - 2, dorsal ? "t" : "L")
    for (let y = y0 + len; y < y0 + h; y++) c.px(x, y, "_")
  }
  c.hline(x0, y0, w, shine)
  c.hline(x0, y0 + 1, w, dorsal ? "M" : "s")
}

const coverts = (x0, y0, dorsal) => (c) => {
  c.rect(x0, y0, 4, 4, dorsal ? "M" : "h")
  c.hline(x0, y0 + 3, 4, dorsal ? "n" : "l")
  c.px(x0 + 1, y0, dorsal ? "l" : "s")
  c.px(x0 + 2, y0 + 1, dorsal ? "l" : "s")
}

const primary = (x0, y0, len, dorsal, tipAtEnd) => (c) => {
  for (let i = 0; i < len; i++) {
    const x = tipAtEnd ? x0 + i : x0 + len - 1 - i
    const outer = i >= len - 4
    c.px(x, y0, outer ? "t" : dorsal ? "M" : "h")
    c.px(x, y0 + 1, outer ? "T" : dorsal ? "l" : "s")
    c.px(x, y0 + 2, outer ? "k" : dorsal ? "m" : "l")
    c.px(x, y0 + 3, outer ? "k" : dorsal ? "n" : "L")
  }
  const end = tipAtEnd ? x0 + len - 1 : x0
  c.px(end, y0, "_")
  c.px(end, y0 + 3, "_")
}

const feather = (x0, y0, len, dorsal, alt) => (c) => {
  const vane = dorsal ? (alt ? "n" : "m") : (alt ? "L" : "l")
  const edge = dorsal ? "T" : "L"
  const shaft = dorsal ? "M" : "h"
  for (let y = 0; y < len; y++) {
    const tip = y >= len - 2
    c.px(x0, y0 + y, tip ? (dorsal ? "d" : "M") : vane)
    c.px(x0 + 1, y0 + y, tip ? (dorsal ? "t" : "L") : shaft)
    c.px(x0 + 2, y0 + y, tip ? (dorsal ? "d" : "M") : edge)
  }
  c.px(x0, y0, dorsal ? "M" : "s")
  c.px(x0 + 1, y0, dorsal ? "l" : "s")
  c.px(x0 + 2, y0, dorsal ? "M" : "s")
  c.px(x0, y0 + len - 1, "_")
  c.px(x0 + 2, y0 + len - 1, "_")
}

const row = (u, v, lengths) => lengths.flatMap((len, k) => [feather(u + 6 * k, v, len, false, k % 2 === 1), feather(u + 6 * k + 3, v, len, true, k % 2 === 1)])

const pair = (u, v, len) => [primary(u, v, len, false, false), primary(u + len, v, len, true, true)]

sprite("basic_wings", { w: 128, h: 64 },
  ...row(0, 13, [8, 9, 9, 10]),
  ...row(0, 46, [11, 12, 12, 12, 12]),
  plate(30, 0, 4, 12, false, [0, 1], true), plate(34, 0, 4, 12, true, [1, 0], true),
  coverts(38, 0, false), coverts(42, 0, true),
  ...pair(48, 0, 16), ...pair(48, 5, 15), ...pair(48, 10, 14), ...pair(48, 15, 13), ...pair(48, 20, 12),
  rect(0, 32, 48, 12, "b"),
  hline(0, 32, 48, "B"), hline(0, 38, 48, "B"),
  hline(0, 37, 48, "c"), hline(0, 43, 48, "c"))
