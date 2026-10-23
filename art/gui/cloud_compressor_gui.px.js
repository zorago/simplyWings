pal({
  k: "#000000", w: "#ffffff", f: "#c6c6c6", s: "#555555", S: "#373737", m: "#8b8b8b", t: "#a0a0a0",
  n: "#1b1028", N: "#2a1a3d", o: "#8a4f12", O: "#be6c40", p: "#e2925f", b: "#d4a846", B: "#f6d678",
  c: "#ddf2ff", v: "#ff9c5a", e: "#c9a8f0", g: "#ffe68a", G: "#f4c542",
  W: "#ffffff", V: "#e6e6e6", U: "#cccccc", T: "#a8a8a8"
})

const W = 176, H = 180

const panel = (c) => {
  c.rect(2, 1, W - 4, H - 2, "f")
  c.rect(1, 2, W - 2, H - 4, "f")
  c.hline(2, 0, W - 4, "k"); c.hline(2, H - 1, W - 4, "k")
  c.vline(0, 2, H - 4, "k"); c.vline(W - 1, 2, H - 4, "k")
  c.px(1, 1, "k"); c.px(W - 2, 1, "k"); c.px(1, H - 2, "k"); c.px(W - 2, H - 2, "k")
  c.hline(2, 1, W - 5, "w"); c.hline(2, 2, W - 6, "w")
  c.vline(1, 2, H - 5, "w"); c.vline(2, 2, H - 6, "w")
  c.hline(4, H - 2, W - 5, "s"); c.hline(3, H - 3, W - 6, "s")
  c.vline(W - 2, 4, H - 5, "s"); c.vline(W - 3, 3, H - 6, "s")
}

const slot = (x, y) => (c) => {
  c.rect(x, y, 16, 16, "m")
  c.hline(x - 1, y - 1, 17, "S"); c.vline(x - 1, y - 1, 17, "S")
  c.hline(x, y + 16, 17, "w"); c.vline(x + 16, y, 17, "w")
}

const gauge = (x, y) => (c) => {
  c.rect(x, y, 4, 16, "n")
  c.hline(x - 1, y - 1, 5, "S"); c.vline(x - 1, y - 1, 17, "S")
  c.hline(x, y + 16, 5, "w"); c.vline(x + 4, y, 17, "w")
}

const drop = (x, y, k) => (c) => {
  c.px(x + 2, y, k)
  c.hline(x + 1, y + 1, 3, k)
  c.rect(x, y + 2, 5, 3, k)
  c.hline(x + 1, y + 5, 3, k)
  c.px(x + 1, y + 2, "w")
}

const pipe = (x, y, len) => (c) => {
  c.hline(x, y - 1, len, "o")
  c.hline(x, y, len, "O")
  c.hline(x, y + 1, len, "p")
  c.hline(x, y + 2, len, "o")
}

const vat = (c) => {
  c.rect(66, 18, 44, 56, "n")
  c.hline(65, 17, 46, "S"); c.vline(65, 17, 58, "S")
  c.hline(66, 74, 45, "w"); c.vline(110, 18, 57, "w")
  for (let y = 26; y < 74; y += 12) { c.hline(66, y, 3, "N"); c.hline(107, y, 3, "N") }
  c.rect(64, 15, 48, 2, "b"); c.hline(64, 15, 48, "B")
  c.rect(64, 75, 48, 2, "b"); c.hline(64, 76, 48, "o")
}

const inventory = (c) => {
  for (let r = 0; r < 3; r++) for (let i = 0; i < 9; i++) slot(8 + i * 18, 98 + r * 18)(c)
  for (let i = 0; i < 9; i++) slot(8 + i * 18, 156)(c)
}

const arrowTo = (x, y) => (c) => {
  for (let i = 0; i < 5; i++) c.vline(x + i, y - 4 + i, 9 - 2 * i, "G")
}

sprite("cloud_compressor", { w: 256, h: 256 },
  panel, inventory, vat,
  slot(26, 17), slot(26, 39), slot(26, 61), slot(134, 39),
  gauge(45, 17), gauge(45, 39), gauge(45, 61), gauge(153, 39),
  drop(14, 21, "c"), drop(14, 43, "v"), drop(14, 65, "e"), drop(162, 26, "g"),
  pipe(50, 25, 15), pipe(50, 47, 15), pipe(50, 69, 15),
  pipe(111, 47, 17), arrowTo(128, 48))

sprite("compressor_gauge", { w: 4, h: 16 },
  (c) => { for (let y = 0; y < 16; y++) { c.px(0, y, "W"); c.px(1, y, "V"); c.px(2, y, "U"); c.px(3, y, "T") } })

sprite("compressor_vat", { w: 44, h: 56 },
  (c) => {
    for (let x = 0; x < 44; x++) {
      const k = x < 3 ? "W" : x < 12 ? "V" : x < 34 ? "U" : "T"
      c.vline(x, 0, 56, k)
    }
    for (let y = 4; y < 56; y += 11) c.hline(6, y, 4, "W")
  })

sprite("compressor_bubbles", { w: 44, h: 96 },
  (c) => {
    const spots = [[6, 4, 2], [18, 12, 1], [30, 2, 2], [38, 20, 1], [10, 24, 1], [24, 28, 2], [34, 10, 1], [14, 16, 1]]
    for (let rep = 0; rep < 3; rep++) {
      for (const [x, y, r] of spots) {
        const yy = y + rep * 32
        if (r === 2) { c.rect(x, yy, 2, 2, "w"); c.px(x - 1, yy, "V"); c.px(x + 2, yy + 1, "V") } else c.px(x, yy, "w")
      }
    }
  })
