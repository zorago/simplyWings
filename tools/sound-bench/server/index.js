import express from "express";
import fs from "node:fs";
import path from "node:path";
import { spawn } from "node:child_process";
import { fileURLToPath } from "node:url";
import { createServer as createVite } from "vite";
import { makeMeasurer, decode } from "./measure.js";

const HERE = path.dirname(fileURLToPath(import.meta.url));
const BENCH = path.resolve(HERE, "..");
const ROOT = path.resolve(BENCH, "../..");
const ASSETS = path.join(ROOT, "src/main/resources/assets/simplywings");
const SOUNDS = path.join(ASSETS, "sounds");
const CATALOG = path.join(ROOT, "tools/sounds/catalog.json");
const LOUDNESS = path.join(ROOT, "tools/sounds/loudness.json");
const CANDIDATES = path.join(ROOT, "tools/sounds/candidates");
const SOURCES_MD = path.join(ROOT, "art/sounds/SOURCES.md");
const WINGS = path.join(ROOT, "src/main/resources/data/simplywings/simplywings/wing");
const RIGS = path.join(ASSETS, "rigs");
const RIGBAKE = path.join(ROOT, "build/rigbake");
const SHOW = path.join(ASSETS, "gacha/altar_show.json.gz");
const SHOW_SOUNDS = path.join(ASSETS, "gacha/show_sounds.json");

const configFile = path.join(BENCH, "bench.config.json");
if (!fs.existsSync(configFile)) fs.copyFileSync(path.join(BENCH, "bench.config.example.json"), configFile);
const config = {
  ...JSON.parse(fs.readFileSync(path.join(BENCH, "bench.config.example.json"), "utf8")),
  ...(fs.existsSync(configFile) ? JSON.parse(fs.readFileSync(configFile, "utf8")) : {}),
};
const SOURCE_DIR = path.resolve(ROOT, config.soundSources);
const measure = makeMeasurer(config.ffmpeg, path.join(BENCH, ".cache/measure"));

function readJson(file) {
  return JSON.parse(fs.readFileSync(file, "utf8"));
}

function writeJson(file, data, indent = 1) {
  fs.writeFileSync(file, JSON.stringify(data, null, indent) + "\n", "utf8");
}

function inside(base, rel) {
  const p = path.resolve(base, rel);
  if (p !== base && !p.startsWith(base + path.sep)) throw Object.assign(new Error("path outside of " + base), { status: 400 });
  return p;
}

let vanilla = null;
function vanillaIndex() {
  if (vanilla) return vanilla;
  const id = config.assetIndex || readJson(config.minecraftVersionJson).assetIndex.id;
  const objects = readJson(path.join(config.minecraftAssets, "indexes", id + ".json")).objects;
  const object = (key) => {
    const o = objects[key];
    return o ? path.join(config.minecraftAssets, "objects", o.hash.slice(0, 2), o.hash) : null;
  };
  vanilla = { id, objects, object, sounds: readJson(object("minecraft/sounds.json")) };
  return vanilla;
}

function catalog() {
  return readJson(CATALOG);
}

function loudnessTargets() {
  return fs.existsSync(LOUDNESS) ? readJson(LOUDNESS) : {};
}

function split(id) {
  const i = id.indexOf(":");
  return i < 0 ? ["simplywings", id] : [id.slice(0, i), id.slice(i + 1)];
}

function resolve(id, cat, depth = 0) {
  if (depth > 6) return [];
  const [ns, name] = split(id);
  if (ns === "minecraft") {
    const ev = vanillaIndex().sounds[name];
    if (!ev) return [];
    return weighted(ev.sounds.map((s) => (typeof s === "string" ? { name: s } : s)), (s) => {
      const vol = s.volume ?? 1;
      const pitch = s.pitch ?? 1;
      if (s.type === "event") return resolve(s.name, cat, depth + 1).map((l) => ({ ...l, volume: l.volume * vol, pitch: l.pitch * pitch }));
      const file = s.name.includes(":") ? s.name.split(":")[1] : s.name;
      return [{ url: `/api/vanilla/file/${file}.ogg`, label: `minecraft:${file}`, volume: vol, pitch, attenuation: s.attenuation_distance ?? 16, p: 1 }];
    });
  }
  const e = cat.sounds.find((x) => x.id === name);
  if (!e) return [];
  const att = e.attenuation || 16;
  if (e.status === "alias") {
    return resolve(e.alias.target, cat, depth + 1).map((l) => ({ ...l, volume: l.volume * (e.alias.volume ?? 1), pitch: l.pitch * (e.alias.pitch ?? 1) }));
  }
  if (e.status === "vanilla") {
    if (!e.vanilla) return [];
    return resolve(e.vanilla.event, cat, depth + 1).map((l) => ({ ...l, volume: l.volume * (e.vanilla.volume ?? 1), pitch: l.pitch * (e.vanilla.pitch ?? 1) }));
  }
  const owner = e.variants_from ? cat.sounds.find((x) => x.id === e.variants_from) : e;
  const variants = owner?.variants || [];
  return variants.map((v) => ({
    url: `/api/audio/mod/${v.file}.ogg`,
    label: `simplywings:${v.file}`,
    volume: e.volume ?? 1,
    pitch: e.pitch ?? 1,
    attenuation: att,
    p: 1 / variants.length,
  }));
}

function weighted(list, expand) {
  const total = list.reduce((a, s) => a + (s.weight ?? 1), 0) || 1;
  return list.flatMap((s) => {
    const leaves = expand(s);
    const share = (s.weight ?? 1) / total;
    const sum = leaves.reduce((a, l) => a + l.p, 0) || 1;
    return leaves.map((l) => ({ ...l, p: (l.p / sum) * share }));
  });
}

function runProcess(cmd, args, opts = {}) {
  return new Promise((resolvePromise) => {
    const p = spawn(cmd, args, { cwd: ROOT, windowsHide: true, shell: process.platform === "win32" && cmd.endsWith(".bat"), ...opts });
    let out = "";
    p.stdout.on("data", (d) => (out += d));
    p.stderr.on("data", (d) => (out += d));
    p.on("error", (e) => resolvePromise({ code: -1, out: out + String(e) }));
    p.on("close", (code) => resolvePromise({ code, out }));
  });
}

const gradlew = process.platform === "win32" ? path.join(ROOT, "gradlew.bat") : path.join(ROOT, "gradlew");
let baking = null;
function bake() {
  if (!baking) {
    baking = runProcess(gradlew, ["--no-configuration-cache", "-q", "bakeBench"]).finally(() => (baking = null));
  }
  return baking;
}

function newest(dir) {
  let t = 0;
  if (!fs.existsSync(dir)) return t;
  for (const f of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, f.name);
    t = Math.max(t, f.isDirectory() ? newest(p) : fs.statSync(p).mtimeMs);
  }
  return t;
}

function bakeIfStale() {
  const index = path.join(RIGBAKE, "index.json");
  const sources = Math.max(newest(path.join(ROOT, "src/main/java/com/zorago/simplywings/render")), newest(path.join(ROOT, "src/main/java/com/zorago/simplywings/anim")), newest(RIGS));
  if (!fs.existsSync(index) || fs.statSync(index).mtimeMs < sources) {
    console.log("[bench] rig bake is stale, running bakeBench in the background");
    bake().then((r) => console.log(`[bench] bakeBench finished (${r.code})${r.code ? "\n" + r.out : ""}`));
  }
}

function pairs() {
  return fs
    .readdirSync(WINGS)
    .filter((f) => f.endsWith(".json"))
    .map((f) => {
      const j = readJson(path.join(WINGS, f));
      return { id: f.slice(0, -5), model: j.model.split(":")[1], flap_rate: j.flap_rate, hover_rate: j.hover_rate, rarity: j.rarity ?? null };
    });
}

const app = express();
app.use(express.json({ limit: "20mb" }));

const wrap = (fn) => (req, res) =>
  Promise.resolve(fn(req, res)).catch((e) => res.status(e.status || 500).json({ error: String(e.message || e) }));

app.get("/api/config", (req, res) => res.json({ ...config, root: ROOT }));
app.get("/api/catalog", (req, res) => res.json(catalog()));
app.put(
  "/api/catalog/:id",
  wrap((req, res) => {
    const cat = catalog();
    const i = cat.sounds.findIndex((e) => e.id === req.params.id);
    if (i < 0) return res.status(404).json({ error: "unknown id" });
    cat.sounds[i] = { ...cat.sounds[i], ...req.body, id: cat.sounds[i].id };
    writeJson(CATALOG, cat);
    res.json(cat.sounds[i]);
  }),
);
app.get("/api/loudness", (req, res) => res.json(loudnessTargets()));
app.put(
  "/api/loudness",
  wrap((req, res) => {
    writeJson(LOUDNESS, req.body, 2);
    res.json(req.body);
  }),
);
app.get("/api/pairs", (req, res) => res.json(pairs()));
app.get(
  "/api/resolve/:id",
  wrap((req, res) => res.json(resolve(req.params.id, catalog()))),
);
app.get(
  "/api/vanilla/event/:id",
  wrap((req, res) => res.json(resolve(req.params.id.includes(":") ? req.params.id : "minecraft:" + req.params.id, catalog()))),
);
app.get(
  "/api/vanilla/events",
  wrap((req, res) => res.json(Object.keys(vanillaIndex().sounds).sort())),
);
app.get(
  /^\/api\/vanilla\/file\/(.+)$/,
  wrap((req, res) => {
    const file = vanillaIndex().object(`minecraft/sounds/${req.params[0]}`);
    if (!file || !fs.existsSync(file)) return res.status(404).end();
    res.type("audio/ogg").sendFile(file);
  }),
);
app.get(/^\/api\/audio\/mod\/(.+)$/, (req, res) => {
  const file = inside(SOUNDS, req.params[0]);
  if (!fs.existsSync(file)) return res.status(404).end();
  res.set("Cache-Control", "no-store").type("audio/ogg").sendFile(file);
});
app.get(/^\/api\/source\/(.+)$/, (req, res) => {
  const file = inside(SOURCE_DIR, decodeURIComponent(req.params[0]));
  if (!fs.existsSync(file)) return res.status(404).end();
  res.sendFile(file);
});

function locate(kind, rel) {
  if (kind === "mod") return inside(SOUNDS, rel);
  if (kind === "source") return inside(SOURCE_DIR, rel);
  if (kind === "candidate") return inside(CANDIDATES, rel);
  if (kind === "vanilla") return vanillaIndex().object(`minecraft/sounds/${rel}`);
  throw Object.assign(new Error("bad kind"), { status: 400 });
}

app.get(
  "/api/measure",
  wrap(async (req, res) => {
    const file = locate(req.query.kind, req.query.path);
    if (!file || !fs.existsSync(file)) return res.status(404).json({ error: "missing file" });
    res.json(await measure(file));
  }),
);

app.post(
  "/api/trim",
  wrap(async (req, res) => {
    const v = req.body;
    const file = inside(SOURCE_DIR, v.source);
    const x = await decode(config.ffmpeg, file);
    const sr = 44100;
    const rate = (v.pitch || 1) * 1;
    const a = Math.floor(((v.start || 0) / (v.tempo || 1)) * sr);
    const b = v.end == null ? x.length : Math.min(x.length, Math.floor((v.end / (v.tempo || 1)) * sr));
    let peak = 0;
    for (let i = a; i < b; i++) peak = Math.max(peak, Math.abs(x[i]));
    const t = peak * 10 ** (-30 / 20);
    let onset = a;
    for (let i = a; i < b; i++) {
      if (Math.abs(x[i]) >= t) {
        onset = i;
        break;
      }
    }
    const start = Math.max(0, (onset / sr - 0.003) * (v.tempo || 1));
    res.json({ start: Math.round(start * 1000) / 1000, moved_ms: Math.round((start - (v.start || 0)) * 1000), rate });
  }),
);

app.post(
  "/api/build",
  wrap(async (req, res) => {
    const ids = Array.isArray(req.body?.ids) ? req.body.ids : [];
    const r = await runProcess(config.python, ["-I", "tools/sounds/build_sounds.py", ...ids]);
    res.status(r.code === 0 ? 200 : 500).json(r);
  }),
);
app.post(
  "/api/audit",
  wrap(async (req, res) => {
    const r = await runProcess(config.python, ["-I", "tools/sounds/audit.py"]);
    res.json(r);
  }),
);

app.post(
  "/api/copy-to-instance",
  wrap(async (req, res) => {
    const target = config.testInstance;
    if (!target) return res.status(400).json({ error: "set testInstance in tools/sound-bench/bench.config.json (a local test instance, never the CurseForge pack)" });
    if (/PleasePlayWithMe/i.test(target)) return res.status(400).json({ error: "refusing to write into the public CurseForge pack" });
    const r = await runProcess(gradlew, ["--no-configuration-cache", "-q", "build"]);
    if (r.code !== 0) return res.status(500).json(r);
    const libs = path.join(ROOT, "build/libs");
    const jar = fs.readdirSync(libs).filter((f) => f.endsWith(".jar") && !/sources|javadoc/.test(f)).sort((a, b) => fs.statSync(path.join(libs, b)).mtimeMs - fs.statSync(path.join(libs, a)).mtimeMs)[0];
    const mods = path.join(target, "mods");
    fs.mkdirSync(mods, { recursive: true });
    for (const f of fs.readdirSync(mods)) if (/^simplywings.*\.jar$/i.test(f)) fs.rmSync(path.join(mods, f));
    fs.copyFileSync(path.join(libs, jar), path.join(mods, jar));
    res.json({ code: 0, out: `copied ${jar} to ${mods}` });
  }),
);

app.get(
  "/api/candidates/:id",
  wrap((req, res) => {
    const dir = inside(CANDIDATES, req.params.id);
    res.json(fs.existsSync(dir) ? fs.readdirSync(dir).filter((f) => !f.endsWith(".json")) : []);
  }),
);
app.get(
  "/api/candidates/:id/:name",
  wrap((req, res) => {
    const file = inside(CANDIDATES, path.join(req.params.id, req.params.name));
    if (!fs.existsSync(file)) return res.status(404).end();
    res.sendFile(file);
  }),
);
app.post(
  "/api/candidates/:id",
  express.raw({ type: "*/*", limit: "200mb" }),
  wrap((req, res) => {
    const name = path.basename(String(req.query.name || "candidate")).replace(/[^\w.\-]+/g, "_");
    if (!/\.(mp3|wav|flac|ogg)$/i.test(name)) return res.status(400).json({ error: "mp3, wav, flac or ogg only" });
    const dir = inside(CANDIDATES, req.params.id);
    fs.mkdirSync(dir, { recursive: true });
    fs.writeFileSync(path.join(dir, name), req.body);
    res.json({ name });
  }),
);
app.delete(
  "/api/candidates/:id/:name",
  wrap((req, res) => {
    fs.rmSync(inside(CANDIDATES, path.join(req.params.id, req.params.name)), { force: true });
    res.json({ ok: true });
  }),
);
app.post(
  "/api/candidates/:id/promote",
  wrap((req, res) => {
    const { name, license, page = "", author = "", justification = "", settings = {} } = req.body;
    if (!license) return res.status(400).json({ error: "license is required" });
    if (license !== "CC0" && !justification) return res.status(400).json({ error: "a non-CC0 license needs a justification" });
    const from = inside(CANDIDATES, path.join(req.params.id, name));
    const local = `bench_${name}`;
    fs.copyFileSync(from, inside(SOURCE_DIR, local));
    const cat = catalog();
    const e = cat.sounds.find((x) => x.id === req.params.id);
    if (!e) return res.status(404).json({ error: "unknown id" });
    e.variants = e.variants || [];
    const base = e.variants.length ? e.variants[0].file.replace(/_\d+$/, "") : e.id.replace(/\./g, "/");
    let n = e.variants.length + 1;
    while (e.variants.some((v) => v.file === `${base}_${n}`)) n++;
    e.variants.push({
      file: `${base}_${n}`,
      source: local,
      start: 0,
      end: null,
      peak: -3,
      fade_in: 0.004,
      fade_out: 0.1,
      echo: 0,
      loop: 0,
      tempo: 1,
      pitch: 1,
      lp: 0,
      gain_db: 0,
      ...settings,
    });
    if (e.status === "todo" || e.status === "silent") e.status = "custom";
    writeJson(CATALOG, cat);
    const md = fs.readFileSync(SOURCES_MD, "utf8");
    const header = "\n## Ajouts du banc de son\n\n| Fichier local | Page | Auteur | Licence | Utilisé pour |\n|---|---|---|---|---|\n";
    const row = `| \`${local}\` | ${page} | ${author} | ${license}${justification ? ` (${justification})` : ""} | \`${base}_${n}\` |\n`;
    fs.writeFileSync(SOURCES_MD, (md.includes("## Ajouts du banc de son") ? md : md.trimEnd() + "\n" + header) + row, "utf8");
    fs.rmSync(from, { force: true });
    res.json(e);
  }),
);

app.get(
  "/api/rigs/index",
  wrap((req, res) => {
    const index = path.join(RIGBAKE, "index.json");
    if (!fs.existsSync(index)) return res.status(404).json({ error: "no bake yet", baking: !!baking });
    res.json({ ...readJson(index), baking: !!baking });
  }),
);
app.post(
  "/api/rigs/bake",
  wrap(async (req, res) => res.json(await bake())),
);
app.get(
  "/api/rigs/:model/bench",
  wrap((req, res) => {
    const file = inside(RIGBAKE, `${req.params.model}.bench.json`);
    if (!fs.existsSync(file)) return res.status(404).json({ error: "not baked" });
    res.sendFile(file);
  }),
);
app.get(
  "/api/rigs/:model/bbmodel",
  wrap((req, res) => res.sendFile(inside(RIGS, `${req.params.model}.bbmodel`))),
);
app.get(
  "/api/rigs/:model/membranes",
  wrap((req, res) => {
    const file = inside(RIGS, `${req.params.model}.membranes.json`);
    if (!fs.existsSync(file)) return res.json(null);
    res.sendFile(file);
  }),
);
app.get(/^\/api\/assets\/(.+)$/, (req, res) => {
  const file = inside(ASSETS, req.params[0]);
  if (!fs.existsSync(file)) return res.status(404).end();
  res.sendFile(file);
});
app.get("/api/gacha/show", (req, res) => {
  res.set({ "Content-Type": "application/json", "Content-Encoding": "gzip" });
  fs.createReadStream(SHOW).pipe(res);
});
app.get("/api/gacha/sounds", (req, res) => {
  if (!fs.existsSync(SHOW_SOUNDS)) return res.status(404).json({ error: "show_sounds.json missing" });
  res.sendFile(SHOW_SOUNDS);
});
app.put(
  "/api/gacha/sounds",
  wrap((req, res) => {
    writeJson(SHOW_SOUNDS, req.body, 2);
    res.json({ ok: true });
  }),
);

const vite = await createVite({ root: BENCH, server: { middlewareMode: true }, appType: "spa", logLevel: "warn" });
app.use(vite.middlewares);

app.listen(config.port, "127.0.0.1", () => {
  console.log(`[bench] Simply Wings sound bench on http://localhost:${config.port}`);
  try {
    console.log(`[bench] vanilla asset index ${vanillaIndex().id}, ${Object.keys(vanilla.sounds).length} vanilla events`);
  } catch (e) {
    console.log(`[bench] vanilla assets unavailable: ${e.message}`);
  }
  bakeIfStale();
});
