import { setMaster, stopAll, forget } from "./audio.js";
import { renderDetail, gameCall, playGame, playReference, playVariant } from "./detail.js";
import { createViewHost } from "./views/host.js";

const $ = (s) => document.querySelector(s);
export const api = {
  async get(url) {
    const r = await fetch(url);
    if (!r.ok) throw new Error(`${r.status} ${url}: ${await r.text()}`);
    return r.json();
  },
  async send(method, url, body) {
    const r = await fetch(url, { method, headers: { "Content-Type": "application/json" }, body: body === undefined ? undefined : JSON.stringify(body) });
    const j = await r.json().catch(() => ({}));
    if (!r.ok) throw new Error(j.error || j.out || `${r.status} ${url}`);
    return j;
  },
};

const STATUSES = ["custom", "vanilla", "alias", "silent", "todo"];
const VERDICTS = ["todo", "keep", "reject", "revise", "validated", "in_game"];
const DONE = new Set(["keep", "validated", "in_game"]);

export const state = {
  catalog: null,
  pairs: [],
  loudness: {},
  measures: new Map(),
  selected: null,
  distance: 4,
  visible: [],
};

function status(text) {
  $("#status").textContent = text;
}

export function log(title, text) {
  const d = $("#log");
  d.querySelector("pre").textContent = `${title}\n\n${text}`;
  d.showModal();
}

function passIndex(e) {
  const families = ["feather", "membrane", "leather", "insect", "strong", "buzz"];
  if (e.category === "wing") return families.includes(e.group) && !e.family ? 1 : 2;
  return { flight: 3, ui: 4, gacha: 5, block: 6, essence: 6, fury: 7 }[e.category] ?? 8;
}

export function target(e) {
  return e?.loudness_target ? state.loudness[e.loudness_target] : undefined;
}

export function outOfTarget(e) {
  const t = target(e);
  if (!t || t.rms_max_db == null) return false;
  return (e.variants || []).some((v) => {
    const m = state.measures.get(v.file);
    return m && Math.abs(m.rms_max_db - t.rms_max_db) > 2;
  });
}

function matches(e) {
  const q = $("#search").value.trim().toLowerCase();
  if (q && ![e.id, e.description, e.trigger, ...(e.keywords || [])].join(" ").toLowerCase().includes(q)) return false;
  const st = $("#f-status").value;
  if (st && e.status !== st) return false;
  if (!st && !$("#f-silent").checked && e.status === "silent") return false;
  const v = $("#f-verdict").value;
  if (v && e.verdict?.state !== v) return false;
  if ($("#f-out").checked && !outOfTarget(e)) return false;
  return true;
}

function renderTree() {
  const tree = $("#tree");
  const open = new Set([...tree.querySelectorAll("details[open]")].map((d) => d.dataset.key));
  tree.innerHTML = "";
  const list = state.catalog.sounds.filter(matches);
  const order = $("#f-order").value;
  list.sort((a, b) => (order === "passes" ? passIndex(a) - passIndex(b) : 0) || a.id.localeCompare(b.id));
  state.visible = list;
  const cats = new Map();
  for (const e of list) {
    if (!cats.has(e.category)) cats.set(e.category, new Map());
    const g = cats.get(e.category);
    if (!g.has(e.group)) g.set(e.group, []);
    g.get(e.group).push(e);
  }
  const fresh = open.size === 0;
  for (const [cat, groups] of cats) {
    const all = [...groups.values()].flat();
    const dc = document.createElement("details");
    dc.dataset.key = cat;
    dc.open = fresh || open.has(cat) || $("#search").value !== "";
    dc.innerHTML = `<summary>${cat} <span class="count">${all.filter((e) => DONE.has(e.verdict?.state)).length}/${all.length}</span></summary>`;
    for (const [group, items] of groups) {
      const dg = document.createElement("details");
      dg.dataset.key = cat + "/" + group;
      dg.open = open.has(dg.dataset.key) || $("#search").value !== "" || items.some((e) => e.id === state.selected?.id);
      dg.innerHTML = `<summary>${group} <span class="count">${items.filter((e) => DONE.has(e.verdict?.state)).length}/${items.length} validés</span></summary>`;
      for (const e of items) {
        const div = document.createElement("div");
        div.className = "item" + (e.id === state.selected?.id ? " sel" : "");
        div.dataset.id = e.id;
        div.innerHTML = `${e.id}<span class="badge">${e.status}</span><span class="badge v-${e.verdict?.state}">${e.verdict?.state || "todo"}</span>`;
        div.onclick = () => select(e.id);
        dg.appendChild(div);
      }
      dc.appendChild(dg);
    }
    tree.appendChild(dc);
  }
}

export async function reload() {
  state.catalog = await api.get("/api/catalog");
  if (state.selected) state.selected = state.catalog.sounds.find((e) => e.id === state.selected.id) || null;
  forget("/api/audio/mod/");
  renderTree();
}

export async function save(id, patch) {
  const updated = await api.send("PUT", `/api/catalog/${encodeURIComponent(id)}`, patch);
  const i = state.catalog.sounds.findIndex((e) => e.id === id);
  state.catalog.sounds[i] = updated;
  if (state.selected?.id === id) state.selected = updated;
  forget();
  renderTree();
  return updated;
}

export async function build(ids) {
  status(`construction ${ids ? ids.join(", ") : "de tout"}…`);
  try {
    const r = await api.send("POST", "/api/build", { ids: ids || [] });
    status(r.out.trim().split("\n").pop());
    for (const k of [...state.measures.keys()]) state.measures.delete(k);
    forget();
    return r;
  } catch (e) {
    status("échec de la construction");
    log("build_sounds.py", e.message);
    throw e;
  }
}

const view = createViewHost($("#view"), { api, state, playGame: (e, o) => playGame(e, o), save });

export function select(id) {
  const e = state.catalog.sounds.find((x) => x.id === id);
  if (!e) return;
  stopAll();
  state.selected = e;
  for (const el of document.querySelectorAll("#tree .item")) el.classList.toggle("sel", el.dataset.id === id);
  const el = document.querySelector(`#tree .item[data-id="${CSS.escape(id)}"]`);
  if (el) {
    el.closest("details details")?.setAttribute("open", "");
    el.closest("details")?.parentElement?.closest("details")?.setAttribute("open", "");
    el.scrollIntoView({ block: "nearest" });
  }
  renderDetail($("#detail"), e);
  view.show(e);
}

function step(dir) {
  const ids = [...document.querySelectorAll("#tree .item")].map((x) => x.dataset.id);
  if (!ids.length) return;
  const i = ids.indexOf(state.selected?.id);
  select(ids[(i + dir + ids.length) % ids.length]);
}

async function verdict(stateName) {
  const e = state.selected;
  if (!e) return;
  await save(e.id, { verdict: { ...e.verdict, state: stateName, date: new Date().toISOString().slice(0, 10) } });
  renderDetail($("#detail"), state.selected);
}

document.addEventListener("keydown", (ev) => {
  if (ev.target.closest("input, textarea, select") || ev.ctrlKey || ev.metaKey || ev.altKey) return;
  const e = state.selected;
  if (ev.key === "ArrowDown") step(1);
  else if (ev.key === "ArrowUp") step(-1);
  else if (!e) return;
  else if (ev.key === " ") playGame(e);
  else if (ev.key === "r" || ev.key === "R") playReference(e);
  else if (/^[1-9]$/.test(ev.key)) playVariant(e, parseInt(ev.key, 10) - 1);
  else if (ev.key === "k" || ev.key === "K") verdict("keep");
  else if (ev.key === "x" || ev.key === "X") verdict("reject");
  else if (ev.key === "v" || ev.key === "V") verdict("revise");
  else if (ev.key === "Escape") stopAll();
  else return;
  ev.preventDefault();
});

async function measureAll() {
  const files = state.catalog.sounds.flatMap((e) => (e.variants || []).map((v) => v.file));
  let n = 0;
  for (const f of files) {
    status(`mesure ${++n}/${files.length} ${f}`);
    try {
      state.measures.set(f, await api.get(`/api/measure?kind=mod&path=${encodeURIComponent(f + ".ogg")}`));
    } catch {}
  }
  status(`${files.length} fichiers mesurés`);
  renderTree();
  if (state.selected) renderDetail($("#detail"), state.selected);
}

async function init() {
  for (const s of STATUSES) $("#f-status").insertAdjacentHTML("beforeend", `<option>${s}</option>`);
  for (const s of VERDICTS) $("#f-verdict").insertAdjacentHTML("beforeend", `<option>${s}</option>`);
  for (const id of ["#search", "#f-status", "#f-verdict", "#f-out", "#f-silent", "#f-order"]) $(id).addEventListener("input", renderTree);
  $("#master").oninput = (ev) => setMaster(parseFloat(ev.target.value));
  setMaster(0.8);
  $("#measure-all").onclick = measureAll;
  $("#build-all").onclick = () => build().then((r) => log("Construction", r.out));
  $("#audit").onclick = async () => {
    status("audit…");
    const r = await api.send("POST", "/api/audit");
    status(`audit terminé (code ${r.code})`);
    log("audit.py", r.out);
  };
  $("#copy").onclick = async () => {
    status("gradlew build + copie…");
    try {
      const r = await api.send("POST", "/api/copy-to-instance");
      status(r.out);
    } catch (e) {
      status("copie impossible");
      log("Copie vers l'instance", e.message);
    }
  };
  $("#help").onclick = () =>
    log(
      "Raccourcis",
      "Espace : ▶ Jeu (variation au hasard, hasard de hauteur du code, couches)\nR : ▶ Référence vanilla\n1 à 9 : variation n\nK / X / V : keep / reject / revise\n↑ ↓ : son précédent / suivant\nÉchap : tout couper\n\nLe curseur de distance simule l'atténuation linéaire de Minecraft (portée = max(volume, 1) × attenuation).",
    );
  [state.pairs, state.loudness] = await Promise.all([api.get("/api/pairs"), api.get("/api/loudness")]);
  await reload();
  status(`${state.catalog.sounds.length} sons au catalogue`);
  const first = location.hash.slice(1);
  if (first) select(decodeURIComponent(first));
}

window.addEventListener("hashchange", () => select(decodeURIComponent(location.hash.slice(1))));
init().catch((e) => log("Erreur au démarrage", e.stack || String(e)));

export { gameCall };
