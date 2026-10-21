pal({
  k: "#1b1028", d: "#2a1a3d", D: "#3d2757", p: "#5c3d7a", P: "#8a62a8", v: "#b48fd0",
  s: "#8f8a5c", S: "#b3ad74", e: "#d6d396", E: "#ecebb8",
  o: "#8a4f12", O: "#c27c1e", g: "#f0b43a", G: "#fde27a",
  w: "#ffffff", W: "#e6edf7", b: "#c2cfe3", B: "#8c9cba",
  c: "#bfe6f7", C: "#e8f8ff", q: "#d8f0ff", Q: "#9fd0ea"
})
ramps("sSeE", "kdDpPv", "oOgG", "BbWw", "QcCw")

sprite("assembly_table_stone", { w: 16, h: 16 },
  rect(0, 0, 16, 16, "e"),
  hline(0, 0, 16, "E"), hline(0, 15, 16, "s"),
  hline(0, 7, 16, "S"), hline(0, 8, 16, "E"),
  vline(5, 1, 6, "S"), vline(12, 9, 6, "S"),
  vline(6, 1, 6, "E"), vline(13, 9, 6, "E"),
  px(2, 3, "S"), px(9, 4, "S"), px(3, 12, "S"), px(14, 2, "E"), px(8, 11, "S"))

sprite("assembly_table_pillar", { w: 16, h: 16 },
  rect(0, 0, 16, 16, "d"),
  vline(0, 0, 16, "D"), vline(15, 0, 16, "k"),
  hline(0, 0, 16, "D"), hline(0, 15, 16, "k"),
  rect(3, 2, 10, 12, "k"),
  box(3, 2, 10, 12, "p"),
  vline(8, 4, 8, "P"), vline(7, 4, 8, "P"),
  line(5, 6, 7, 4, "v"), line(10, 6, 8, 4, "v"),
  px(5, 7, "P"), px(10, 7, "P"), px(7, 11, "v"), px(8, 11, "v"))

sprite("assembly_table_top", { w: 16, h: 16 },
  rect(0, 0, 16, 16, "D"),
  box(0, 0, 16, 16, "S"),
  box(1, 1, 14, 14, "e"),
  rect(2, 2, 12, 12, "d"),
  box(4, 4, 8, 8, "o"),
  line(4, 4, 11, 11, "O"), line(11, 4, 4, 11, "O"),
  px(7, 7, "g"), px(8, 8, "g"), px(7, 8, "G"), px(8, 7, "G"),
  px(2, 2, "p"), px(13, 2, "p"), px(2, 13, "p"), px(13, 13, "p"))

sprite("assembly_table_wing", { w: 16, h: 16 },
  rect(0, 0, 16, 16, "W"),
  grad(0, 0, 16, 16, "wWb", "v", 0.4),
  hline(0, 0, 16, "w"),
  vline(3, 1, 15, "b"), vline(7, 1, 15, "b"), vline(11, 1, 15, "b"), vline(15, 1, 15, "B"),
  hline(0, 14, 16, "g"), hline(0, 15, 16, "O"))

sprite("assembly_table_metal", { w: 16, h: 16 },
  rect(0, 0, 16, 16, "O"),
  hline(0, 0, 16, "G"), hline(0, 1, 16, "g"), hline(0, 15, 16, "o"),
  vline(0, 0, 16, "g"), vline(15, 0, 16, "o"),
  px(4, 4, "g"), px(11, 4, "g"), px(4, 11, "g"), px(11, 11, "g"),
  px(5, 5, "o"), px(12, 5, "o"), px(5, 12, "o"), px(12, 12, "o"))

sprite("assembly_table_glass", { w: 16, h: 16 },
  box(0, 0, 16, 16, "Q"),
  hline(1, 1, 14, "q"), vline(1, 2, 13, "q"),
  line(3, 6, 6, 3, "C"), line(3, 8, 8, 3, "q"),
  px(12, 12, "C"))

sprite("assembly_table_cloud", { w: 16, h: 16 },
  rect(0, 0, 16, 16, "c"),
  circ(4, 5, 3, "C"), circ(10, 4, 4, "C"), circ(12, 11, 3, "C"), circ(5, 12, 3, "C"),
  circ(9, 4, 2, "w"), circ(4, 4, 1, "w"), circ(12, 10, 1, "w"),
  px(7, 9, "Q"), px(2, 9, "Q"), px(14, 6, "Q"), px(9, 14, "Q"))
