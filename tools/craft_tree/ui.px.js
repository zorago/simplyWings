pal({
  k: "#000000", w: "#ffffff", l: "#dbdbdb", p: "#c6c6c6", s: "#8b8b8b", d: "#555555", D: "#373737",
  r: "#c0392b", R: "#7a1f16", o: "#e8a33c", b: "#8a6a45", B: "#5a4228", c: "#3f8fd1", C: "#245a8a",
  g: "#5fae3b", G: "#356b20", y: "#f2d54b", Y: "#b08a1a", m: "#b04bd1", M: "#6a2384", n: "#2b2547",
  q: "#f0f0ff", h: "#ffd9a0", e: "#d8a8ff"
})
ramps("kDdsplw", "Rr", "Bbo", "Cc", "Gg", "Yy", "Mm")

sprite("card", { w: 24, h: 24 },
  rect(1, 1, 22, 22, "p"),
  hline(2, 0, 20, "k"), hline(2, 23, 20, "k"), vline(0, 2, 20, "k"), vline(23, 2, 20, "k"),
  px(1, 1, "k"), px(22, 1, "k"), px(1, 22, "k"), px(22, 22, "k"),
  hline(2, 1, 19, "w"), vline(1, 2, 19, "w"), hline(2, 2, 18, "w"), vline(2, 3, 17, "w"),
  hline(3, 22, 19, "d"), vline(22, 3, 19, "d"), hline(4, 21, 17, "d"), vline(21, 4, 17, "d"),
  px(2, 21, "p"), px(21, 2, "p"))

const band = (name, hi, mid, lo) => sprite(name, { w: 24, h: 11 },
  rect(0, 0, 24, 11, mid), hline(0, 0, 24, hi), hline(0, 1, 24, hi), hline(0, 10, 24, lo), hline(0, 9, 24, lo),
  vline(0, 0, 11, lo), vline(23, 0, 11, lo))
band("band_kill", "#e05a4a", "r", "R")
band("band_break", "#a8865c", "b", "B")
band("band_fish", "#62aee8", "c", "C")
band("band_vanilla", "#a8a8a8", "s", "d")
band("band_rare", "#7a8cff", "#4a5ee0", "#2a3590")
band("band_epic", "#d070f0", "m", "M")
band("band_legendary", "#ffd060", "o", "#a06010")
band("band_mythic", "#ff7a6a", "r", "R")
band("band_level", "#7a7a7a", "d", "D")

sprite("icon_kill", { outline: "k" }, `
  ........ww
  .......wlw
  ......wls.
  .....wls..
  .b..wls...
  ..bwls....
  ..bbs.....
  .bBb......
  bB..b.....
  BB........`)

sprite("icon_break", { outline: "k" }, `
  ..lllll...
  .ls...sl..
  ls..b..sl.
  l...b...l.
  ....b.....
  ....b.....
  ....B.....
  ....B.....
  ....B.....
  ....B.....`)

sprite("icon_fish", { outline: "k" }, `
  .........b
  ........bw
  .......b.w
  ......b..w
  .....b...w
  ....b....w
  ...b.....w
  ..B....w.w
  .B.....ww.
  B.........`)

sprite("icon_vanilla", { outline: "k" }, `
  bbbbbbbbbb
  bBoooooBob
  bBbbbbbBbb
  bggggggggb
  bgGgggGggb
  bbbbbbbbbb
  bBbBbbBbBb
  bbBbbbbBbb
  bBbBbbBbBb
  bbbbbbbbbb`)

const cond = (name, grid) => sprite(name, { outline: "k" }, grid)
cond("cond_night", `
  ..yyy..
  .yy....
  yy.....
  yy.....
  yy.....
  .yy....
  ..yyy..`)
cond("cond_storm", `
  .sssss.
  sllllls
  sssssss
  ...yy..
  ..yy...
  .yyyy..
  ...y...`)
cond("cond_sneak", `
  ..ll...
  ..ll...
  .cccc..
  cccccc.
  .CCCC..
  .C..C..
  CC..CC.`)
cond("cond_sprint", `
  ....ll.
  ...cll.
  ..ccc..
  wc.cc..
  ...CCw.
  ..C..C.
  .C.....`)
cond("cond_up", `
  ...g...
  ..ggg..
  .ggggg.
  ...g...
  ...g...
  ...g...
  ...g...`)
cond("cond_down", `
  ...b...
  ...b...
  ...b...
  ...b...
  .bbbbb.
  ..bbb..
  ...b...`)
cond("cond_biome", `
  ..ggg..
  .gGggg.
  gggGggg
  .ggggg.
  ...b...
  ...b...
  ..bbb..`)
cond("cond_village", `
  ...r...
  ..rrr..
  .rrrrr.
  rrrrrrr
  .ollo..
  .olllo.
  .oollo.`)
cond("cond_dark", `
  .nnnnn.
  nnnnnnn
  nnmnmnn
  nnnnnnn
  nnnnnnn
  .nnnnn.
  ..nnn..`)
cond("cond_full", `
  ...o...
  ..ooo..
  .ooyoo.
  .oyyyo.
  .oyyyo.
  .ooooo.
  ..ooo..`)
cond("cond_chance", `
  .ww.ww.
  wwwwwww
  ww.w.ww
  wwwwwww
  ww.w.ww
  wwwwwww
  .ww.ww.`)
cond("cond_cloud", `
  .......
  ..qqq..
  .qqqqq.
  qqqqqqq
  qqqqqqq
  .lllll.
  .......`)

sprite("unknown", { w: 16, h: 16 },
  rect(1, 1, 14, 14, "m"), box(0, 1, 16, 14, "M"), box(1, 0, 14, 16, "M"),
  rect(2, 2, 12, 12, "e"), dither(2, 2, 12, 12, "e", "m", 0.5),
  `
  ................
  ................
  ................
  .....kkkkkk.....
  ....kwwwwwwk....
  ...kwwkkkkwwk...
  ...kwwk..kwwk...
  ....kk..kwwk....
  .......kwwk.....
  ......kwwk......
  ......kwwk......
  .......kk.......
  ......kwwk......
  ......kwwk......
  .......kk.......
  ................`)

sprite("arrow", { outline: "k" }, `
  ........l...
  ........ll..
  llllllllwll.
  wwwwwwwwwwwl
  llllllllwll.
  ........ll..
  ........l...`)
