pal({
  k: "#000000", w: "#ffffff", f: "#c6c6c6", s: "#555555", S: "#373737", m: "#8b8b8b", t: "#a0a0a0",
  n: "#1b1028", N: "#2a1a3d", g: "#e3a12f", G: "#fde27a", o: "#8a4f12",
  c: "#bfe6f7", C: "#e8f8ff", q: "#9fd0ea", Q: "#6fa9c9", x: "#7a7a7a", y: "#b0b0b0"
})

const W = 176, H = 206

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

const bigSlot = (x, y) => (c) => {
  c.rect(x - 4, y - 4, 24, 24, "m")
  c.hline(x - 5, y - 5, 25, "S"); c.vline(x - 5, y - 5, 25, "S")
  c.hline(x - 4, y + 20, 25, "w"); c.vline(x + 20, y - 4, 25, "w")
}

const inventory = (c) => {
  for (let r = 0; r < 3; r++) for (let i = 0; i < 9; i++) slot(8 + i * 18, 124 + r * 18)(c)
  for (let i = 0; i < 9; i++) slot(8 + i * 18, 182)(c)
}

const flaskGhost = (x, y) => (c) => {
  c.rect(x + 6, y + 1, 4, 2, "x")
  c.rect(x + 6, y + 3, 4, 3, "y")
  c.rect(x + 4, y + 6, 8, 2, "y")
  c.rect(x + 3, y + 8, 10, 5, "y")
  c.rect(x + 4, y + 13, 8, 2, "y")
}

const tank = (c) => {
  c.rect(10, 18, 12, 70, "n")
  c.hline(9, 17, 14, "S"); c.vline(9, 17, 72, "S")
  c.hline(10, 88, 13, "w"); c.vline(22, 18, 71, "w")
}

const arrow = (x, y, k) => (c) => {
  c.rect(x, y + 5, 12, 5, k)
  for (let i = 0; i < 8; i++) c.vline(x + 12 + i, y + i, 15 - 2 * i, k)
}

const common = [panel, inventory, tank, slot(8, 92), flaskGhost(8, 92)]

sprite("assembly_table_components", { w: 256, h: 256 },
  ...common,
  (c) => {
    for (let r = 0; r < 5; r++) for (let i = 0; i < 5; i++) {
      if ((r === 0 || r === 4) && (i === 0 || i === 4)) continue
      slot(34 + i * 18, 18 + r * 18)(c)
    }
  },
  arrow(125, 54, "m"),
  bigSlot(150, 54))

sprite("assembly_table_assembly", { w: 256, h: 256 },
  ...common,
  (c) => {
    c.rect(87, 37, 2, 6, "m"); c.rect(73, 51, 6, 2, "m"); c.rect(97, 51, 6, 2, "m")
    c.px(87, 36, "g"); c.px(88, 36, "g"); c.px(72, 51, "g"); c.px(72, 52, "g"); c.px(103, 51, "g"); c.px(103, 52, "g")
  },
  slot(80, 20), slot(56, 44), slot(80, 44), slot(104, 44),
  arrow(125, 45, "m"),
  bigSlot(150, 44))

sprite("assembly_table_energy", { w: 256, h: 256 },
  ...common,
  (c) => {
    c.rect(66, 30, 44, 44, "m")
    c.hline(65, 29, 45, "S"); c.vline(65, 29, 45, "S")
    c.hline(66, 74, 45, "w"); c.vline(110, 30, 45, "w")
    for (let i = 0; i < 4; i++) {
      c.px(68 + i, 32 + i, "g"); c.px(107 - i, 32 + i, "g")
      c.px(68 + i, 71 - i, "g"); c.px(107 - i, 71 - i, "g")
    }
    c.px(87, 31, "G"); c.px(88, 31, "G"); c.px(87, 72, "G"); c.px(88, 72, "G")
  },
  slot(80, 44))

sprite("tab_on", { w: 28, h: 32 },
  rect(1, 1, 26, 31, "f"),
  hline(2, 0, 24, "k"), vline(0, 2, 30, "k"), vline(27, 2, 30, "k"),
  px(1, 1, "k"), px(26, 1, "k"),
  hline(2, 1, 24, "w"), vline(1, 2, 30, "w"), vline(2, 2, 30, "w"),
  vline(26, 2, 30, "s"), vline(25, 3, 29, "s"))

sprite("tab_off", { w: 28, h: 28 },
  rect(1, 1, 26, 27, "t"),
  hline(2, 0, 24, "k"), vline(0, 2, 26, "k"), vline(27, 2, 26, "k"),
  px(1, 1, "k"), px(26, 1, "k"),
  hline(2, 1, 24, "f"), vline(1, 2, 26, "f"),
  vline(26, 2, 26, "s"))

sprite("arrow_progress", { w: 20, h: 15 }, arrow(0, 0, "g"),
  (c) => { c.hline(0, 5, 12, "G"); for (let i = 0; i < 4; i++) c.px(12 + i, i, "G") })

sprite("tank_fill", { w: 12, h: 70 },
  grad(0, 0, 12, 70, "CcqQ", "v", 0.5),
  (c) => {
    for (let y = 3; y < 70; y += 9) { c.hline(2, y, 4, "C"); c.hline(7, y + 4, 3, "w") }
    c.vline(1, 0, 70, "C")
  })

sprite("tank_ticks", { w: 12, h: 70 },
  (c) => { for (let i = 1; i < 10; i++) { c.hline(8, i * 7, 4, "N"); c.hline(10, i * 7 - 1, 2, "Q") } })

sprite("cost_icon", { w: 9, h: 7 }, `
  ...ww....
  .wwCCw.w.
  wCCCCCwCw
  wCcccCCCw
  wccccccQw
  .wQQQQQw.
  ..wwwww..`)
