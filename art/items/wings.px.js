const T = {
  feather: ["o.......",
    "1o......",
    "11oo....",
    "o111oo..",
    "o21111oo",
    "o221111h",
    "o2221oo.",
    "o3222o..",
    "o3o22o..",
    "o3o3o...",
    "o3o3o...",
    ".o3o....",
    ".o3o....",
    "..o....."],
  membrane: ["a.......",
    "aa......",
    "o1aa....",
    "o11a1a..",
    "o12a11aa",
    "o22a21ah",
    "o2a22ao.",
    "o2a2ao..",
    "a2o2o...",
    "ao.a....",
    "a......."],
  insect: ["........",
    "ooo.....",
    "o11oo...",
    "o1a11oo.",
    "o11a111o",
    ".oo11a1h",
    "...ooooh",
    "..oo22oh",
    ".o2a22o.",
    "o22a2o..",
    "o2a2o...",
    ".oo....."],
  mech: ["a.......","aa......","a1aa....","a1a1aa..","a1a1a1aa","a2a2a2ah","a2a2a2a.","a3a3a3a.","a3a3aa..","a3aa....","aa......"],
  ethereal: ["a..a....",
    "1a.1a...",
    "a1a11a..",
    "a211a1a.",
    "a2211aaa",
    "a32211ah",
    "a3322aa.",
    "a332a...",
    ".a3a....",
    ".a.a....",
    "a......."],
  fin: ["........",
    "o.o.....",
    "1o1o....",
    "1a1a1o..",
    "o1a1a1oo",
    ".o1a1a1h",
    "..o1a1o.",
    "...ooo.."],
  seraph: ["o.......",
    "1o......",
    "o1o.....",
    ".o11o...",
    "..o111o.",
    "ooo..o1h",
    "o222221h",
    "ooo..o2h",
    "..o333o.",
    ".o33o...",
    "o3o.....",
    "3o......",
    "o......."]
}

const both = (c, x, y, color) => { c.px(x, y, color); c.px(15 - x, y, color) }

const pair = (name, shape, colors, ...extra) => sprite(name + "_wings", { w: 16, h: 16 },
  (c) => {
    const rows = T[shape]
    for (let y = 0; y < rows.length; y++) for (let x = 0; x < 8; x++) {
      const k = rows[y][x]
      if (k !== "." && colors[k]) both(c, x, y + 1, colors[k])
    }
  },
  ...extra)

const dots = (color, points) => (c) => { for (const [x, y] of points) if (c.is(x, y, "*")) both(c, x, y, color) }

const leather = "#6b4a2b"

pair("common", "feather", { o: "#3b3f45", 1: "#c9ced4", 2: "#9aa1a9", 3: "#6e757e", h: leather })
pair("bat", "membrane", { o: "#1f1a24", 1: "#5b4a66", 2: "#463852", a: "#8a7a96", h: leather })
pair("crow", "feather", { o: "#0b0b10", 1: "#3e3e52", 2: "#29293a", 3: "#16161f", h: leather },
  dots("#5a5a7a", [[1, 4], [2, 5]]))
pair("owl", "feather", { o: "#3d2a17", 1: "#d9b98a", 2: "#b08a5a", 3: "#7a5a36", h: leather },
  dots("#6a4a28", [[2, 6], [3, 6], [4, 7], [2, 8], [3, 9], [5, 8]]))
pair("gull", "feather", { o: "#4a5058", 1: "#ffffff", 2: "#c9d1d9", 3: "#1c1c1c", h: leather })
pair("hummingbird", "feather", { o: "#0f3a2e", 1: "#7ee0a0", 2: "#2fae6e", 3: "#1d6e5a", h: leather },
  dots("#b07ae0", [[3, 6], [4, 6]]))
pair("eagle", "feather", { o: "#2a180a", 1: "#b07a43", 2: "#7a4e26", 3: "#4a2e16", h: leather },
  dots("#e8d8b8", [[1, 4], [2, 4]]))
pair("swift", "feather", { o: "#0e0e12", 1: "#4a4a52", 2: "#33333a", 3: "#1e1e24", h: leather },
  dots("#d8d8d8", [[6, 7]]))
pair("pigeon", "feather", { o: "#2e3238", 1: "#b8bec6", 2: "#8f96a0", 3: "#5a6068", h: leather },
  dots("#1e2024", [[1, 7], [2, 7], [3, 7], [4, 8], [5, 8], [1, 9], [2, 9]]))
pair("peacock", "feather", { o: "#0b2f3a", 1: "#3fb3a8", 2: "#1f7f8a", 3: "#155a6e", h: leather },
  dots("#e3b341", [[2, 9], [4, 9], [1, 11]]), dots("#1a2a6a", [[3, 10]]))
pair("parrot", "feather", { o: "#3a1010", 1: "#ffd23f", 2: "#e03a3a", 3: "#2f6fe0", h: leather })
pair("bee", "insect", { o: "#6a5030", 1: "#dce9f2", 2: "#c8d8e4", a: "#a8865a", h: "#e8b830" },
  dots("#2a2010", [[7, 6], [7, 8]]))
pair("moth", "insect", { o: "#4a3a28", 1: "#d8c8a0", 2: "#a88a60", a: "#8a7050", h: "#7a6040" },
  dots("#3a2a1a", [[2, 4], [3, 9]]))
pair("beetle", "insect", { o: "#12301e", 1: "#3fae5f", 2: "#9a7a5a", a: "#1f6a3a", h: "#1f3a2a" },
  dots("#9fe8a8", [[1, 3], [2, 3]]))
pair("squirrel", "membrane", { o: "#4a4a4a", 1: "#a8a8a8", 2: "#8a8a8a", a: "#d8d8d8", h: "#7a5a3a" })
pair("flying_fish", "fin", { o: "#3a6a8a", 1: "#bfe0f5", 2: "#8fbfdf", a: "#ffffff", h: "#5a8aaa" })
pair("phantom", "membrane", { o: "#1a2233", 1: "#4a5a7a", 2: "#3a4866", a: "#7fd17a", h: "#3a4866" })
pair("amethyst", "feather", { o: "#3a1f5e", 1: "#d8a8ff", 2: "#a66be0", 3: "#6e3fa8", h: leather },
  dots("#ffffff", [[1, 4], [3, 9]]))
pair("ornithopter", "mech", { o: "#4a3018", 1: "#e8dcc0", 2: "#d0c2a0", 3: "#b0a080", a: "#7a5230", h: "#7a5230" })
pair("kite", "mech", { o: "#5a3a20", 1: "#ffffff", 2: "#e03a3a", 3: "#ffffff", a: "#8a6a40", h: "#8a6a40" })
pair("copper", "mech", { o: "#4a2614", 1: "#e0905a", 2: "#c06a3a", 3: "#8a4a28", a: "#5a3018", h: "#8a4a28" },
  dots("#4fa88a", [[1, 6], [5, 9]]))
pair("patchwork", "mech", { o: "#3a2818", 1: "#a07850", 2: "#7a8aa0", 3: "#6a4a30", a: "#e0d0a0", h: leather },
  dots("#c06a5a", [[1, 5], [2, 5], [5, 7]]))
pair("ancient", "insect", { o: "#5a3010", 1: "#f0c060", 2: "#d09030", a: "#7a4a20", h: "#4a3a20" },
  dots("#8a5a20", [[2, 3], [4, 5], [3, 9]]))
pair("ptero", "membrane", { o: "#3a2410", 1: "#d0a060", 2: "#b07a40", a: "#f0d0a0", h: "#6a4020" })
pair("fire", "ethereal", { 1: "#ffe066", 2: "#ff9a2e", 3: "#e0401e", a: "#ff6a1e", h: "#ffe066" })
pair("water", "ethereal", { 1: "#bfe8ff", 2: "#4fa8e0", 3: "#2a6ab0", a: "#ffffff", h: "#bfe8ff" })
pair("earth", "feather", { o: "#3a3a3a", 1: "#6abf4a", 2: "#8a8a8a", 3: "#5a5a5a", h: "#5a5a5a" },
  dots("#b07ae0", [[2, 9], [5, 8]]))
pair("air", "ethereal", { 1: "#ffffff", 2: "#e8f4fa", 3: "#c8e0ec", a: "#a8d4ea", h: "#ffffff" })
pair("storm", "ethereal", { 1: "#bfe8ff", 2: "#6a9aff", 3: "#6a3ab0", a: "#d8d0ff", h: "#bfe8ff" },
  dots("#ffffff", [[2, 5], [3, 6], [2, 7], [3, 8]]))
pair("aurora", "ethereal", { 1: "#8affc0", 2: "#4ad0a0", 3: "#8a5ae0", a: "#c8ffe0", h: "#8affc0" })
pair("shadow", "ethereal", { 1: "#4a3a5a", 2: "#2e2238", 3: "#1a1222", a: "#3a2a4a", h: "#2e2238" },
  dots("#d080ff", [[3, 7], [5, 6]]))
pair("nebula", "ethereal", { 1: "#6a5ab0", 2: "#3a2a7a", 3: "#1a1440", a: "#8a6ad0", h: "#6a5ab0" },
  dots("#ffffff", [[2, 6], [4, 8], [1, 9], [5, 6]]))
pair("forest", "insect", { o: "#2a4a1a", 1: "#7acf5a", 2: "#4a9a3a", a: "#6a4a2a", h: "#6a4a2a" },
  dots("#ff9ac8", [[2, 3], [4, 5], [3, 9]]))
pair("angel", "feather", { o: "#8a8070", 1: "#fffaf0", 2: "#ece4d4", 3: "#cfc4ae", h: "#e3b341" })
pair("demon", "membrane", { o: "#2a0808", 1: "#a02a2a", 2: "#6a1a1a", a: "#3a0e0e", h: "#1a0a0a" },
  dots("#ff6a3a", [[1, 10], [3, 11], [5, 10]]))
pair("psyche", "insect", { o: "#2a8a9a", 1: "#c8ffff", 2: "#6ae0e8", a: "#ffffff", h: "#c8ffff" })
pair("seraph", "seraph", { o: "#6a4a10", 1: "#ffe9a0", 2: "#f0c040", 3: "#c08a20", h: "#fff4c8" })
pair("daedalus", "mech", { o: "#2a2e34", 1: "#c8ccd2", 2: "#9aa0a8", 3: "#6a7078", a: "#3a3e44", h: "#3a3e44" },
  dots("#ff8a3a", [[3, 11], [4, 11]]))
pair("night", "membrane", { o: "#050508", 1: "#2a2a32", 2: "#1a1a20", a: "#4a4a58", h: "#0e0e12" },
  (c) => { c.px(1, 10, "#c02020"); c.px(2, 10, "#c02020") })
pair("light", "membrane", { o: "#6a7480", 1: "#ffffff", 2: "#dfe6ee", a: "#aab4c0", h: "#aab4c0" },
  dots("#8ab8ff", [[1, 10], [5, 10]]))

sheet("all_wings", ["*_wings"], { cols: 10, pad: 2 })
