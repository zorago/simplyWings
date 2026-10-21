pal({
  k: "#000000", w: "#ffffff", f: "#c6c6c6", s: "#555555", S: "#373737", d: "#1e2226", D: "#16191c", t: "#2a3036"
})

const W = 320, H = 188

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

const inset = (x, y, w, h) => (c) => {
  c.rect(x, y, w, h, "d")
  c.hline(x - 1, y - 1, w + 1, "S"); c.vline(x - 1, y - 1, h + 1, "S")
  c.hline(x, y + h, w + 1, "w"); c.vline(x + w, y, h + 1, "w")
}

sprite("wings_screen", { w: W, h: H },
  panel,
  inset(8, 19, 114, 160),
  (c) => { c.rect(116, 19, 6, 160, "D") },
  inset(128, 19, 78, 160),
  (c) => { c.rect(128, 151, 78, 28, "t"); c.hline(128, 151, 78, "D") },
  inset(212, 19, 100, 160))
