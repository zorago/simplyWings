import * as THREE from "three";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";
import { gain } from "../audio.js";
import { parseShow, createScene, position, meta, tintFor, ELEMENTS, TIERS, TIER_COLORS } from "./gacha/show.js";
import { parseCue, parseCards, listFor, resolve, fire, tick, power, cardSounds, deck, deckState, deckStep, deckDone, deckFinish, deckEvents, ANCHORS, EACH } from "./gacha/cues.js";

const ANIM_ORDER = ["summon_b", "summon_a", "summon_s", "summon_ss", "summon_sss", "summon_x10", "summon_mythic_night", "summon_mythic_light"];
const SINGLE_TIER = { summon_b: 0, summon_a: 1, summon_s: 2, summon_ss: 3, summon_sss: 4, summon_mythic_night: 5, summon_mythic_light: 5 };
const CARD_KEYS = ["deal", "shuffle", "flip", "open"];
const GUIDES = ["pre", "birth", "desc", "hover", "burst", "reveal", "settle", "end"];
const hex = (c) => "#" + c.toString(16).padStart(6, "0");
const r2 = (x) => Math.round(x * 100) / 100;
const smooth = (t) => {
  t = Math.max(0, Math.min(1, t));
  return t * t * (3 - 2 * t);
};

function short(id) {
  const [ns, name] = id.includes(":") ? id.split(":") : ["simplywings", id];
  const parts = name.split(".");
  return ns === "minecraft" ? parts.slice(-2).join(".") : parts[parts.length - 1];
}

const CSS = `
.gv-body{position:relative;height:340px;flex:none;background:#0b0d16;overflow:hidden}
.gv-body canvas{display:block}
.gv-hud{position:absolute;left:8px;top:6px;font:12px monospace;color:#cfd6e4;text-shadow:0 1px 2px #000;pointer-events:none;white-space:pre}
.gv-sec{border-top:1px solid var(--line);padding:4px 10px;display:flex;gap:6px;align-items:center;flex-wrap:wrap}
.gv-tl{position:relative;flex:none;user-select:none}
.gv-row{display:flex;height:16px;align-items:center;border-bottom:1px solid #1a1d24}
.gv-row.off{opacity:.35}
.gv-row.sel{background:#1c2638}
.gv-lab{width:190px;flex:none;font-size:11px;padding:0 6px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;color:var(--dim);cursor:pointer}
.gv-track{position:relative;flex:1;height:100%}
.gv-guides{height:18px;background:#121419;cursor:pointer}
.gv-guide{position:absolute;top:0;bottom:0;width:1px;background:#2c3446;pointer-events:none}
.gv-gl{position:absolute;top:1px;font-size:9px;color:#7d8699;transform:translateX(2px);pointer-events:none;white-space:nowrap}
.gv-m{position:absolute;top:2px;height:11px;min-width:7px;padding:0 3px;transform:translateX(-3px);border-radius:3px;background:#3b5c94;color:#e6eefc;font-size:9px;line-height:11px;cursor:grab;white-space:nowrap;z-index:1}
.gv-m:hover{background:#5179c0;z-index:3}
.gv-m.hl{background:#d08a1c;color:#111}
.gv-m.sel{outline:2px solid #fff;z-index:2}
.gv-m.drag{cursor:grabbing;background:#7ba7ff;z-index:4}
.gv-ph{position:absolute;top:0;bottom:0;width:2px;background:#ff5b5b;pointer-events:none;z-index:5}
.gv-scroll{max-height:300px;overflow:auto;position:relative}
.gv-insp textarea{width:100%;height:84px;font:11px monospace;background:#111;color:var(--text);border:1px solid var(--line)}
.gv-cards canvas{display:block;width:100%;height:150px;background:#120c1c}
.gv-log{font:11px monospace;max-height:140px;overflow:auto;padding:4px 10px;color:var(--dim)}
.gv-log .burst{color:#ff8a65}
.gv-tiers select{width:58px}
`;

export function create(root, ctx) {
  if (!document.getElementById("gv-style")) document.head.insertAdjacentHTML("beforeend", `<style id="gv-style">${CSS}</style>`);
  root.style.overflow = "auto";
  root.innerHTML = `
    <div class="view-head">
      <strong>Autel</strong>
      <select data-a="anim"></select>
      <button data-a="play" title="Lecture / pause">▶</button>
      <button data-a="restart" title="Rejouer depuis 0">⟲</button>
      <select data-a="speed"><option value="1">×1</option><option value="0.5">×0.5</option><option value="0.25">×0.25</option></select>
      <button data-a="skip" title="Clic pendant le show : montre les cartes ; pendant les cartes : les retourne toutes, puis ferme">Passer</button>
      <button data-a="esc" title="Échap : ferme les cartes et relâche le show">Échap</button>
      <label title="Caméra libre (l'écoute reste à la caméra du jeu)"><input data-a="orbit" type="checkbox"> orbite</label>
      <span class="spacer"></span>
      <button data-a="save" disabled>Enregistrer</button>
      <span class="hint" data-a="status"></span>
    </div>
    <div class="gv-sec gv-tiers" data-a="tiers" style="display:none"></div>
    <div class="gv-body" data-a="body"><div class="gv-hud" data-a="hud">chargement de altar_show.json.gz…</div></div>
    <div class="gv-sec"><input data-a="scrub" type="range" min="0" max="1" step="0.001" value="0" style="flex:1"><span data-a="time" style="font:12px monospace;width:120px;text-align:right"></span></div>
    <div class="gv-tl" data-a="tl"></div>
    <div class="gv-sec gv-insp" data-a="insp" style="display:block"></div>
    <div class="gv-sec" data-a="add"></div>
    <div class="gv-sec"><strong>Cartes</strong><span class="hint" data-a="cardinfo"></span></div>
    <div class="gv-cards"><canvas data-a="cards"></canvas></div>
    <div class="gv-tl" data-a="ctl"></div>
    <div class="gv-sec"><strong>Journal</strong><span class="hint">sons déclenchés (en orange : plus de 4 dans le même tick, rafale)</span><button data-a="clearlog">effacer</button></div>
    <div class="gv-log" data-a="log"></div>`;
  const $ = (a) => root.querySelector(`[data-a="${a}"]`);
  const status = (t) => ($("status").textContent = t);

  let model = null;
  let scene = null;
  let orbit = null;
  let json = null;
  let cues = [];
  let cards = {};
  let markers = [];
  let anim = null;
  let tiers = [0];
  let tint = null;
  let entry = null;
  let selected = null;
  let dirty = false;
  let visible = false;
  let raf = 0;
  let last = 0;
  const active = [];
  const listener = new THREE.Vector3();
  const sim = { t: 0, playing: false, lastTime: -0.05, k: 0, cards: null, holding: false, exitAt: -1 };
  const ready = load();

  async function load() {
    const [show, sounds] = await Promise.all([ctx.api.get("/api/gacha/show"), ctx.api.get("/api/gacha/sounds")]);
    model = parseShow(show);
    json = sounds;
    scene = createScene(model, $("body"));
    orbit = new OrbitControls(scene.camera, scene.renderer.domElement);
    orbit.enabled = false;
    orbit.target.set(0, 3, 0);
    const names = [...ANIM_ORDER.filter((n) => model.anims[n]), ...Object.keys(model.anims).filter((n) => !ANIM_ORDER.includes(n) && n !== "idle")];
    $("anim").innerHTML = names.map((n) => `<option>${n}</option>`).join("");
    new ResizeObserver(() => {
      const b = $("body");
      scene.resize(b.clientWidth, b.clientHeight);
      drawCards();
    }).observe($("body"));
    const datalist = document.createElement("datalist");
    datalist.id = "gv-sounds";
    datalist.innerHTML = (ctx.state.catalog?.sounds || []).map((e) => `<option value="simplywings:${e.id}">`).join("");
    root.appendChild(datalist);
  }

  function setAnim(name) {
    anim = model.anims[name];
    $("anim").value = name;
    if (name in SINGLE_TIER) tiers = [SINGLE_TIER[name]];
    else {
      const pulls = Array.isArray(anim.meta.pulls) ? anim.meta.pulls : [];
      const n = Math.max(pulls.length, (anim.meta.spawn || []).length);
      tiers = Array.from({ length: n }, (_, i) => Math.max(0, TIERS.indexOf(pulls[i] ?? "b"))).sort((a, b) => a - b);
    }
    renderTiers();
    rebuild();
    restart(false);
  }

  function renderTiers() {
    const el = $("tiers");
    if (anim.name in SINGLE_TIER || !tiers.length) {
      el.style.display = "none";
      return;
    }
    el.style.display = "flex";
    el.innerHTML = `<span class="hint">paliers des étoiles (le serveur les trie) :</span>` + tiers.map((t, i) => `<select data-i="${i}" style="color:${hex(TIER_COLORS[t])}">${TIERS.map((n, k) => `<option value="${k}" ${k === t ? "selected" : ""}>${i}: ${n}</option>`).join("")}</select>`).join("");
    for (const s of el.querySelectorAll("select")) {
      s.onchange = () => {
        tiers[+s.dataset.i] = +s.value;
        tiers.sort((a, b) => a - b);
        renderTiers();
        rebuild();
      };
    }
  }

  function effectiveKey() {
    return json[anim.name] ? anim.name : "default";
  }

  function rebuild() {
    const list = listFor(json, anim.name) || [];
    cues = list.map((o) => parseCue(o));
    cards = parseCards(json);
    markers = resolve(cues, anim, tiers);
    tint = tintFor(model, tiers);
    renderTimeline();
    renderCardTimeline();
    renderInspector();
    renderAdd();
    $("save").disabled = !dirty;
  }

  function cardsAt() {
    return meta(anim, "reveal", anim.length - 6) + 0.9;
  }

  function holdTime() {
    return anim.length - meta(anim, "post", 0);
  }

  function highlightSet() {
    const s = new Set();
    if (!entry) return s;
    s.add("simplywings:" + entry.id);
    if (entry.status === "vanilla" && entry.vanilla?.event) s.add(entry.vanilla.event);
    if (entry.status === "alias" && entry.alias?.target) s.add(entry.alias.target);
    return s;
  }

  function trackHtml(items, length, hl, kind) {
    return items
      .map((m, n) => {
        const cls = ["gv-m", hl.has(m.sound) ? "hl" : "", selected && m.c.raw === selected.raw ? "sel" : ""].join(" ");
        return `<div class="${cls}" data-kind="${kind}" data-n="${n}" style="left:${(m.time / length) * 100}%" title="${m.sound} @ ${m.time.toFixed(2)} s${m.i ? " · i=" + m.i : ""}">${short(m.sound)}</div>`;
      })
      .join("");
  }

  function guidesHtml(length) {
    const g = GUIDES.filter((k) => typeof anim.meta[k] === "number").map((k) => [k, anim.meta[k]]);
    g.push(["cartes", cardsAt()], ["hold", holdTime()]);
    return g.map(([k, t]) => `<div class="gv-guide" style="left:${(t / length) * 100}%"></div><div class="gv-gl" style="left:${(t / length) * 100}%">${k}</div>`).join("");
  }

  function laneLabel(c) {
    const s = c.sounds.length > 1 ? `${short(c.sounds[0])}…×${c.sounds.length}` : short(c.sounds[0]);
    const anchor = c.each ? `[${c.each}]` : c.from !== "start" ? `←${c.from}` : "";
    return `${s} ${anchor} @${c.at}`;
  }

  function renderTimeline() {
    const length = anim.length;
    const hl = highlightSet();
    const rows = cues
      .map((c, ci) => {
        const mine = markers.filter((m) => m.ci === ci);
        const sel = selected && c.raw === selected.raw;
        return `<div class="gv-row ${mine.length ? "" : "off"} ${sel ? "sel" : ""}"><div class="gv-lab" data-ci="${ci}" title="${JSON.stringify(c.raw).replace(/"/g, "&quot;")}">${laneLabel(c)}</div><div class="gv-track">${trackHtml(mine, length, hl, "show")}</div></div>`;
      })
      .join("");
    $("tl").innerHTML = `
      <div class="gv-row gv-guides"><div class="gv-lab">liste « ${effectiveKey()} » · ${markers.length} sons</div><div class="gv-track" data-a="gtrack">${guidesHtml(length)}<div class="gv-ph" data-a="ph"></div></div></div>
      <div class="gv-scroll"><div style="position:relative">${rows}</div></div>`;
    const byCue = new Map();
    markers.forEach((m) => {
      if (!byCue.has(m.ci)) byCue.set(m.ci, []);
      byCue.get(m.ci).push(m);
    });
    for (const el of $("tl").querySelectorAll(".gv-m")) {
      const track = el.parentElement;
      const ci = +el.closest(".gv-row").querySelector(".gv-lab").dataset.ci;
      wireMarker(el, byCue.get(ci)[+el.dataset.n], track, length);
    }
    for (const el of $("tl").querySelectorAll(".gv-lab[data-ci]")) el.onclick = () => select(cues[+el.dataset.ci].raw);
    const g = $("tl").querySelector('[data-a="gtrack"]');
    g.onpointerdown = (ev) => {
      const rect = g.getBoundingClientRect();
      seek(((ev.clientX - rect.left) / rect.width) * length);
    };
  }

  function wireMarker(el, m, track, length) {
    el.onpointerdown = (ev) => {
      ev.preventDefault();
      ev.stopPropagation();
      el.setPointerCapture(ev.pointerId);
      const x0 = ev.clientX;
      let moved = false;
      let T = m.time;
      el.onpointermove = (e) => {
        if (Math.abs(e.clientX - x0) > 3) moved = true;
        if (!moved) return;
        el.classList.add("drag");
        const rect = track.getBoundingClientRect();
        T = Math.max(0, Math.min(length, ((e.clientX - rect.left) / rect.width) * length));
        if (e.shiftKey) T = Math.round(T * 20) / 20;
        el.style.left = (T / length) * 100 + "%";
        el.textContent = `${short(m.sound)} ${T.toFixed(2)}`;
      };
      el.onpointerup = () => {
        el.onpointermove = el.onpointerup = null;
        if (moved) drag(m, T);
        else {
          select(m.c.raw);
          playMarker(m);
        }
      };
    };
  }

  function drag(m, T) {
    const o = m.c.raw;
    let note;
    if (!m.c.each) {
      if (Array.isArray(o.t)) o.t[m.k] = r2(T - m.base);
      else o.t = r2(T - m.base);
      note = `t = ${Array.isArray(o.t) ? o.t[m.k] : o.t} après « ${m.c.from} »`;
    } else {
      const d = T - m.time;
      if (Array.isArray(o.t)) o.t[m.k] = r2(o.t[m.k] + d);
      else o.t = r2((o.t ?? 0) + d);
      note = `cue « each: ${m.c.each} » : t décalé de ${d >= 0 ? "+" : ""}${d.toFixed(2)} s pour toute la famille (${markers.filter((x) => x.ci === m.ci).length} sons)`;
    }
    dirty = true;
    selected = { raw: o };
    rebuild();
    status(note);
  }

  function findList(raw) {
    for (const [k, v] of Object.entries(json)) {
      if (k === "cards") {
        for (const [ck, cv] of Object.entries(v)) {
          if (Array.isArray(cv) && cv.includes(raw)) return { list: cv, path: `cards.${ck}` };
          if (!Array.isArray(cv)) for (const [tk, tl] of Object.entries(cv)) if (tl.includes(raw)) return { list: tl, path: `cards.${ck}.${tk}` };
        }
      } else if (Array.isArray(v) && v.includes(raw)) return { list: v, path: k };
    }
    return null;
  }

  function select(raw) {
    selected = { raw };
    rebuild();
  }

  function renderInspector() {
    const el = $("insp");
    if (!selected) {
      el.innerHTML = `<span class="hint">Clic sur un marqueur : écoute et sélection. Glisser : change son temps (Maj = pas de 0,05 s, un tick). Clic sur la règle : déplace la tête de lecture.</span>`;
      return;
    }
    const where = findList(selected.raw);
    el.innerHTML = `<div class="row" style="margin-bottom:4px"><strong>Cue</strong><span class="hint">${where ? where.path : "?"}</span><button data-b="apply">Appliquer</button><button data-b="del">Supprimer</button><button data-b="none">Désélectionner</button></div><textarea spellcheck="false">${JSON.stringify(selected.raw)}</textarea>`;
    el.querySelector('[data-b="apply"]').onclick = () => {
      try {
        const v = JSON.parse(el.querySelector("textarea").value);
        for (const k of Object.keys(selected.raw)) delete selected.raw[k];
        Object.assign(selected.raw, v);
        dirty = true;
        rebuild();
        status("cue modifiée (pense à Enregistrer)");
      } catch (e) {
        status("JSON invalide : " + e.message);
      }
    };
    el.querySelector('[data-b="del"]').onclick = () => {
      const w = findList(selected.raw);
      if (!w) return;
      w.list.splice(w.list.indexOf(selected.raw), 1);
      const fam = selected.raw.each ? ` (toute la famille « ${selected.raw.each} »)` : "";
      selected = null;
      dirty = true;
      rebuild();
      status("cue supprimée" + fam);
    };
    el.querySelector('[data-b="none"]').onclick = () => {
      selected = null;
      rebuild();
    };
  }

  function renderAdd() {
    const nums = Object.keys(anim.meta).filter((k) => typeof anim.meta[k] === "number");
    const key = effectiveKey();
    $("add").innerHTML = `
      <strong>Ajouter</strong>
      <input data-b="sound" list="gv-sounds" placeholder="simplywings:gacha.bell" style="width:200px">
      <select data-b="from"><option value="start">depuis start</option>${nums.map((k) => `<option value="${k}">depuis ${k}</option>`).join("")}${EACH.map((k) => `<option value="each:${k}">each ${k}</option>`).join("")}</select>
      <input data-b="at" list="gv-anchors" value="altar" style="width:110px"><datalist id="gv-anchors">${ANCHORS.map((a) => `<option value="${a}">`).join("")}</datalist>
      vol <input data-b="vol" type="number" step="0.5" value="2" style="width:56px">
      <button data-b="go">au curseur</button>
      <span class="spacer"></span>
      ${key === "default" ? `<button data-b="own" title="Copie la liste default dans une liste propre à ${anim.name}">liste propre à ${anim.name}</button>` : `<button data-b="drop" title="Supprime la liste propre, l'anim reprend default">revenir à default</button>`}`;
    const q = (b) => $("add").querySelector(`[data-b="${b}"]`);
    q("go").onclick = () => {
      const s = q("sound").value.trim();
      if (!s) return status("choisis un son");
      const from = q("from").value;
      const o = { sound: s.includes(":") ? s : "simplywings:" + s };
      if (from.startsWith("each:")) {
        o.each = from.slice(5);
        o.t = 0;
      } else {
        if (from !== "start") o.from = from;
        const base = from === "start" ? 0 : meta(anim, from, 0);
        o.t = r2(sim.t - base);
      }
      o.volume = Number(q("vol").value) || 1;
      o.pitch = 1;
      o.at = q("at").value || "altar";
      (json[key] = json[key] || []).push(o);
      selected = { raw: o };
      dirty = true;
      rebuild();
      status(`cue ajoutée à « ${key} »`);
    };
    if (q("own"))
      q("own").onclick = () => {
        json[anim.name] = JSON.parse(JSON.stringify(json.default || []));
        dirty = true;
        selected = null;
        rebuild();
        status(`liste propre à ${anim.name} créée (copie de default)`);
      };
    if (q("drop"))
      q("drop").onclick = () => {
        if (!confirm(`Supprimer la liste propre à ${anim.name} ?`)) return;
        delete json[anim.name];
        dirty = true;
        selected = null;
        rebuild();
      };
  }

  function cardMarkers() {
    const d = deck(tiers);
    const out = [];
    for (const ev of deckEvents(d)) {
      for (const s of cardSounds(cards, ev.key, ev.tier)) out.push({ ...ev, c: s.c, sound: s.sound, ci: CARD_KEYS.indexOf(ev.key) });
    }
    return { d, out };
  }

  function renderCardTimeline() {
    const { d, out } = cardMarkers();
    const length = d.end + 0.4;
    const hl = highlightSet();
    $("cardinfo").textContent = `départ à ${cardsAt().toFixed(2)} s du show (reveal + 0,9) · ${d.cards.length} carte(s) · fin ${d.end.toFixed(2)} s · sons UI (non positionnels), temps fixés par CardDeck`;
    $("ctl").innerHTML =
      `<div class="gv-row gv-guides"><div class="gv-lab">temps des cartes</div><div class="gv-track" data-a="ctrack"><div class="gv-ph" data-a="cph"></div></div></div>` +
      CARD_KEYS.map((k, ci) => {
        const mine = out.filter((m) => m.ci === ci);
        return `<div class="gv-row ${mine.length ? "" : "off"}"><div class="gv-lab">${k}</div><div class="gv-track">${trackHtml(mine, length, hl, "card")}</div></div>`;
      }).join("");
    $("ctl")._length = length;
    const rows = [...$("ctl").querySelectorAll(".gv-row")].slice(1);
    rows.forEach((row, ci) => {
      const mine = out.filter((m) => m.ci === ci);
      row.querySelectorAll(".gv-m").forEach((el) => {
        const m = mine[+el.dataset.n];
        el.style.cursor = "pointer";
        el.title += ` · carte ${m.i} (${TIERS[m.tier]})`;
        el.onclick = () => {
          selected = { raw: m.c.raw };
          rebuild();
          playCard(m.key, m.tier, m.c);
        };
      });
    });
  }

  function anchorPos(f, time) {
    const v = new THREE.Vector3();
    switch (f.at) {
      case "altar":
        v.set(0, 1, 0);
        break;
      case "crystal":
        v.set(0, 50 / 16, 0);
        break;
      case "hover":
        v.set(0, 6, 0);
        break;
      case "top":
        v.set(0, meta(anim, "top", 384) / 16, 0);
        break;
      case "flame": {
        const b = model.bones[Math.max(0, model.bone("flame_" + ELEMENTS[f.i]))];
        v.set(b.px / 16, b.py / 16 + 1.6, b.pz / 16);
        break;
      }
      default: {
        const b = model.bone(f.at.replace("{i}", String(f.i)));
        if (b >= 0) position(model, anim, b, time, v).divideScalar(16);
      }
    }
    return v.add(new THREE.Vector3(f.offset[0] || 0, f.offset[1] || 0, f.offset[2] || 0));
  }

  function cameraAt(time) {
    const c = model.bone("camera");
    const t = model.bone("camera_target");
    if (c < 0 || t < 0) return [new THREE.Vector3(0, 4, 14), new THREE.Vector3(0, 2, 0)];
    return [position(model, anim, c, time).divideScalar(16), position(model, anim, t, time).divideScalar(16)];
  }

  function spin(p, a) {
    const c = Math.cos(a);
    const s = Math.sin(a);
    return new THREE.Vector3(p.x * c - p.z * s, p.y, p.x * s + p.z * c);
  }

  function frozen(raw) {
    return (sim.holding || sim.exitAt >= 0) && raw > holdTime();
  }

  function gameCamera(raw) {
    if (frozen(raw)) {
      const hold = holdTime();
      const base = cameraAt(hold);
      const a = 0.16 * (raw - hold);
      let pos = spin(base[0], a);
      let target = spin(base[1], a);
      if (sim.exitAt >= 0) {
        const post = meta(anim, "post", 2.4);
        const u = smooth((raw - sim.exitAt) / post);
        const end = cameraAt(anim.length);
        pos = pos.lerp(end[0], u);
        target = target.lerp(end[1], u);
      }
      return [pos, target];
    }
    return cameraAt(Math.min(raw, anim.length));
  }

  async function playAt(f, pos, listenerPos) {
    const dist = pos.distanceTo(listenerPos);
    const h = await ctx.playEvent(f.sound, { volume: f.volume, pitch: f.pitch, distance: dist, positional: true });
    if (!h) return { dist, missing: true };
    const a = { h, pos, volume: f.volume };
    active.push(a);
    h.src.addEventListener("ended", () => {
      const k = active.indexOf(a);
      if (k >= 0) active.splice(k, 1);
    });
    return { dist };
  }

  function playMarker(m) {
    const f = fire(m.c, { i: m.i, base: m.base }, m.tier, power(anim));
    const pos = anchorPos(f, f.sampleTime);
    const lis = cameraAt(Math.min(m.time, anim.length))[0];
    playAt(f, pos, lis).then((r) => status(`${f.sound} · vol ${f.volume.toFixed(2)} · hauteur ${f.pitch.toFixed(2)} · ${r.dist.toFixed(1)} blocs de la caméra${r.missing ? " · introuvable" : ""}`));
  }

  function playCard(key, tier, only) {
    const list = cardSounds(cards, key, tier).filter((s) => !only || s.c === only);
    for (const s of list) ctx.playEvent(s.sound, { volume: s.volume, pitch: s.pitch, positional: false });
    return list;
  }

  const logRows = [];
  function log(time, text, burst) {
    logRows.unshift(`<div class="${burst ? "burst" : ""}">${time.toFixed(2).padStart(6)} s  ${text}</div>`);
    logRows.length = Math.min(logRows.length, 80);
    $("log").innerHTML = logRows.join("");
  }

  function processTick(now) {
    const fin = sim.exitAt >= 0 ? now > sim.exitAt + meta(anim, "post", 2.4) + 0.1 : !sim.holding && now > anim.length + 0.5;
    if (fin) {
      sim.playing = false;
      $("play").textContent = "▶";
      return;
    }
    if (now >= 0) {
      const fired = tick(cues, anim, tiers, sim.lastTime, now);
      const lis = gameCamera(now)[0];
      const burst = fired.length > 4;
      for (const f of fired) {
        const pos = anchorPos(f, f.sampleTime);
        log(now, `${f.sound}  vol ${f.volume.toFixed(2)}  h ${f.pitch.toFixed(2)}  ${pos.distanceTo(lis).toFixed(1)} b`, burst);
        playAt(f, pos, lis);
      }
    }
    sim.lastTime = now;
    if (!sim.cards && now >= cardsAt()) startCards(now);
  }

  function startCards(at) {
    sim.cards = { start: at, s: deckState(deck(tiers)), closed: false };
    sim.holding = true;
  }

  function cardTime() {
    return sim.cards ? sim.t - sim.cards.start : -1;
  }

  function stepCards() {
    const c = sim.cards;
    if (!c || c.closed) return;
    const fired = [];
    deckStep(c.s, cardTime(), (key, tier, i) => fired.push([key, tier, i]));
    for (const [key, tier, i] of fired) {
      const list = playCard(key, tier);
      log(sim.t, `carte ${i} ${key} (${TIERS[tier]}) : ${list.map((s) => short(s.sound)).join(", ") || "—"}`, fired.length > 4);
    }
  }

  function release() {
    if (sim.holding && sim.t > holdTime()) sim.exitAt = sim.t;
    sim.holding = false;
  }

  function exitCards() {
    release();
    if (sim.cards) sim.cards.closed = true;
    else sim.cards = { start: sim.t, s: deckState(deck([])), closed: true };
  }

  function skip() {
    if (!sim.cards) {
      startCards(sim.t);
      log(sim.t, "Passer : cartes affichées, le show continue derrière (ses sons aussi)");
    } else if (!sim.cards.closed) {
      const t = cardTime();
      if (deckDone(sim.cards.s, t)) {
        exitCards();
        log(sim.t, "Passer : cartes fermées, le show est relâché");
      } else {
        deckFinish(sim.cards.s, t);
        log(sim.t, "Passer : toutes les cartes sont retournées d'un coup");
      }
    }
  }

  function stopSounds() {
    ctx.stopAll();
    active.length = 0;
  }

  function restart(play) {
    stopSounds();
    sim.t = 0;
    sim.lastTime = -0.05;
    sim.k = 0;
    sim.cards = null;
    sim.holding = false;
    sim.exitAt = -1;
    sim.playing = play;
    $("play").textContent = play ? "❚❚" : "▶";
  }

  function seek(t) {
    stopSounds();
    sim.t = Math.max(0, t);
    sim.k = Math.floor(sim.t * 20) + 1;
    sim.lastTime = (sim.k - 1) / 20;
    sim.holding = false;
    sim.exitAt = -1;
    sim.cards = null;
    if (sim.t >= cardsAt()) {
      startCards(cardsAt());
      deckStep(sim.cards.s, cardTime(), () => {});
    }
  }

  function updateGains() {
    for (const a of active) {
      const leaf = a.h.leaf;
      a.h.gainNode.gain.value = gain(a.volume * (leaf?.volume ?? 1), a.pos.distanceTo(listener), leaf?.attenuation ?? 16);
    }
  }

  function frame(ts) {
    raf = requestAnimationFrame(frame);
    const dt = Math.min(0.1, (ts - (last || ts)) / 1000);
    last = ts;
    if (!model || !anim) return;
    if (sim.playing) {
      sim.t += dt * parseFloat($("speed").value);
      while (sim.playing && sim.k / 20 <= sim.t) processTick(sim.k++ / 20);
      stepCards();
    }
    const raw = sim.t;
    const fz = frozen(raw);
    const time = fz ? holdTime() : Math.min(raw, anim.length);
    const [cp, ct] = gameCamera(raw);
    listener.copy(cp);
    updateGains();
    if (!orbit.enabled) {
      scene.camera.position.copy(cp);
      scene.camera.lookAt(ct);
    } else orbit.update();
    const idle = model.anims.idle;
    scene.draw(anim, time, fz ? raw : time, tint, fz && idle ? { anim: idle, time: raw % idle.length } : null);
    const L = anim.length;
    const ph = $("tl").querySelector('[data-a="ph"]');
    if (ph) ph.style.left = (Math.min(raw, L) / L) * 100 + "%";
    $("scrub").value = Math.min(1, raw / L);
    $("time").textContent = `${raw.toFixed(2)} / ${L.toFixed(2)} s`;
    const ctime = cardTime();
    const cph = $("ctl").querySelector('[data-a="cph"]');
    if (cph) cph.style.left = (Math.max(0, Math.min(ctime + (sim.cards?.s.offset || 0), $("ctl")._length)) / $("ctl")._length) * 100 + "%";
    $("hud").textContent = `${anim.name}  t=${raw.toFixed(2)}${fz ? "  (figé, cartes ouvertes)" : ""}${sim.exitAt >= 0 ? "  sortie" : ""}\nécoute : caméra du jeu (${cp.x.toFixed(1)}, ${cp.y.toFixed(1)}, ${cp.z.toFixed(1)})  distance autel ${cp.distanceTo(new THREE.Vector3(0, 1, 0)).toFixed(1)} b`;
    drawCards();
  }

  function drawCards() {
    const cv = $("cards");
    const w = cv.clientWidth;
    const h = cv.clientHeight;
    if (!w) return;
    if (cv.width !== w) cv.width = w;
    if (cv.height !== h) cv.height = h;
    const g = cv.getContext("2d");
    g.clearRect(0, 0, w, h);
    const c = sim.cards;
    if (!c || c.closed) {
      g.fillStyle = "#7d8699";
      g.font = "12px system-ui";
      g.fillText(c?.closed ? "cartes fermées" : `les cartes arrivent à ${cardsAt().toFixed(2)} s (ou sur Passer)`, 12, 20);
      return;
    }
    const t = cardTime() + c.s.offset;
    const n = c.s.d.cards.length;
    const scale = Math.min(0.62, (h - 16) / (n > 5 ? 2 * 96 + 12 : 96));
    const W = 70 * scale;
    const H = 96 * scale;
    const cols = Math.min(5, n);
    const gap = 12 * scale;
    const x0 = (w - (cols * W + (cols - 1) * gap)) / 2 + W / 2;
    const rows = Math.ceil(n / cols);
    const y0 = (h - (rows * H + (rows - 1) * gap)) / 2 + H / 2;
    c.s.d.cards.forEach((card, i) => {
      if (t < card.deal) return;
      const sx = x0 + (i % cols) * (W + gap);
      const sy = y0 + Math.floor(i / cols) * (H + gap);
      const p = Math.min(1, (t - card.deal) / 0.28);
      const e = 1 + 2.7 * (p - 1) ** 3 + 1.7 * (p - 1) ** 2;
      let x = w / 2 + (sx - w / 2) * e;
      const y = h + H + (sy - h - H) * e;
      let lift = 0;
      if (t >= card.antic && t < card.flipEnd && card.tier >= 2) {
        const k = Math.min(1, Math.max(0, (t - card.antic) / Math.max(0.01, card.flipStart - card.antic)));
        x += Math.sin(t * 70) * 1.6 * k * (card.tier / 3);
        lift = -7 * k * scale;
      }
      let front = t >= card.flipEnd;
      let squash = 1;
      if (t >= card.flipStart && t < card.flipEnd) {
        const u = (t - card.flipStart) / (card.flipEnd - card.flipStart);
        const turns = card.tier >= 3 ? 3 : 1;
        const angle = Math.PI * turns * (0.5 - 0.5 * Math.cos(Math.PI * u));
        front = Math.floor((angle + Math.PI / 2) / Math.PI) % 2 === 1;
        squash = Math.max(0.04, Math.abs(Math.cos(angle)));
        lift -= 4 * Math.sin(Math.PI * u) * scale;
      }
      const col = hex(TIER_COLORS[card.tier]);
      g.save();
      g.translate(x, y + lift);
      g.scale(squash, 1);
      g.fillStyle = front ? "#1a1226" : "#2c1a46";
      g.fillRect(-W / 2, -H / 2, W, H);
      g.lineWidth = 2;
      g.strokeStyle = front ? col : "#9b5cff";
      g.strokeRect(-W / 2, -H / 2, W, H);
      if (front) {
        g.fillStyle = col;
        g.font = `bold ${Math.round(14 * scale + 6)}px system-ui`;
        g.textAlign = "center";
        g.fillText(TIERS[card.tier].toUpperCase(), 0, 5);
      }
      g.restore();
    });
  }

  $("anim").onchange = () => setAnim($("anim").value);
  $("play").onclick = () => {
    if (!anim) return;
    if (!sim.playing && sim.t >= anim.length && !sim.holding) restart(true);
    else {
      sim.playing = !sim.playing;
      if (!sim.playing) stopSounds();
    }
    $("play").textContent = sim.playing ? "❚❚" : "▶";
  };
  $("restart").onclick = () => restart(true);
  $("skip").onclick = () => anim && skip();
  $("esc").onclick = () => {
    if (!anim) return;
    exitCards();
    log(sim.t, "Échap : cartes fermées, show relâché");
  };
  $("orbit").onchange = () => {
    orbit.enabled = $("orbit").checked;
    if (orbit.enabled) orbit.target.set(0, 3, 0);
  };
  $("scrub").oninput = () => anim && seek(parseFloat($("scrub").value) * anim.length);
  $("clearlog").onclick = () => {
    logRows.length = 0;
    $("log").innerHTML = "";
  };
  $("save").onclick = async () => {
    try {
      await ctx.api.send("PUT", "/api/gacha/sounds", json);
      dirty = false;
      $("save").disabled = true;
      status("show_sounds.json enregistré : F3+T en jeu pour recharger");
    } catch (e) {
      status("échec : " + e.message);
    }
  };

  function firesIn(name, ids) {
    const a = model.anims[name];
    const list = (listFor(json, name) || []).map((o) => parseCue(o));
    const t = name in SINGLE_TIER ? [SINGLE_TIER[name]] : (a.meta.pulls || []).map((p) => Math.max(0, TIERS.indexOf(p))).sort((x, y) => x - y);
    if (resolve(list, a, t).some((m) => ids.has(m.sound))) return true;
    const cs = parseCards(json);
    return deckEvents(deck(t)).some((ev) => cardSounds(cs, ev.key, ev.tier).some((s) => ids.has(s.sound)));
  }

  return {
    async show(e) {
      entry = e;
      visible = true;
      await ready;
      if (!visible) return;
      const ids = highlightSet();
      if (!anim || !firesIn(anim.name, ids)) {
        const name = ANIM_ORDER.find((n) => model.anims[n] && firesIn(n, ids)) || anim?.name || "summon_b";
        if (name !== anim?.name) setAnim(name);
        else rebuild();
      } else rebuild();
      if (!raf) raf = requestAnimationFrame(frame);
    },
    hide() {
      visible = false;
      sim.playing = false;
      $("play").textContent = "▶";
      stopSounds();
      cancelAnimationFrame(raf);
      raf = 0;
      last = 0;
    },
  };
}
