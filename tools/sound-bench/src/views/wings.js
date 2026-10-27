import * as THREE from "three";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls.js";
import { gain } from "../audio.js";
import { buildRig, buildSteve, loadTexture, setPose, part } from "./rig3d.js";

const FAMILY_PAIR = { feather: "common", membrane: "demon", leather: "bat", insect: "bee", buzz: "bee" };
const FLAP_TYPES = new Set(["strike", "strike_strong", "mode_travel", "mode_hover", "air_jump"]);
const STRONG_TYPES = new Set(["strike_strong", "air_jump", "mode_travel"]);
const SOUND_TYPES = new Set([...FLAP_TYPES, "launch", "land_soft", "land_hard", "buzz_start", "buzz_stop"]);
const LABELLED = new Set(["launch", "land_soft", "land_hard", "mode_travel", "mode_hover", "air_jump", "strike_strong", "tuck", "brake", "stall", "rouse", "buzz_start", "buzz_stop"]);
const TICK = 0.05;
const PLAYER_SCALE = 0.9375;

function lerp(a, b, f) {
  return a + (b - a) * f;
}

function clamp01(x) {
  return Math.min(1, Math.max(0, x));
}

export function create(root, ctx) {
  root.innerHTML = `
    <div class="view-head">
      <strong>Ailes 3D</strong>
      <label>paire <select data-a="pair"></select></label>
      <label>clip <select data-a="clip"></select></label>
      <button data-a="rear">vue joueur</button>
      <button data-a="side">vue voisin</button>
      <label title="Boucle de vent (FlightLoops.Wind) pilotée par la vitesse du clip"><input type="checkbox" data-a="wind"> vent</label>
      <button data-a="rebake" title="gradlew bakeBench">re-bake</button>
      <span class="hint" data-a="info"></span>
    </div>
    <div class="view-body" data-a="body">
      <div data-a="msg" class="hint" style="position:absolute;inset:0;display:none;padding:14px;background:#16181dcc;z-index:2"></div>
    </div>
    <div class="row" style="padding:4px 10px;border-top:1px solid var(--line)">
      <button data-a="play">▶</button>
      <label><input type="checkbox" data-a="slow"> ×0,25</label>
      <label><input type="checkbox" data-a="loop"> boucle</label>
      <span class="hint" data-a="time"></span>
    </div>
    <div class="timeline" data-a="timeline" style="overflow:hidden"><div class="cursor" data-a="cursor"></div></div>`;
  const $ = (a) => root.querySelector(`[data-a="${a}"]`);
  const { api, state } = ctx;

  const renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.setPixelRatio(window.devicePixelRatio);
  const body = $("body");
  body.appendChild(renderer.domElement);
  const scene = new THREE.Scene();
  scene.background = new THREE.Color(0x30343c);
  scene.add(new THREE.AmbientLight(0xffffff, 1.4));
  const sun = new THREE.DirectionalLight(0xffffff, 1.6);
  sun.position.set(0.3, 1, 0.7);
  scene.add(sun);
  const fill = new THREE.DirectionalLight(0xffffff, 0.7);
  fill.position.set(-0.3, 0.6, -0.8);
  scene.add(fill);
  const grid = new THREE.GridHelper(24, 24, 0x6a7080, 0x454a55);
  scene.add(grid);
  const camera = new THREE.PerspectiveCamera(45, 1, 0.05, 300);
  const controls = new OrbitControls(camera, renderer.domElement);
  controls.target.set(0, 1.1, 0);
  const view = (name) => {
    if (name === "side") camera.position.set(10, 1.6, 0);
    else camera.position.set(0, 2.1, -4.2);
    controls.target.set(0, 1.1, 0);
    controls.update();
  };
  view("rear");

  const pivotUp = new THREE.Group();
  pivotUp.position.y = 0.9;
  const tilt = new THREE.Group();
  tilt.rotation.order = "XYZ";
  const pivotDown = new THREE.Group();
  pivotDown.position.y = -0.9;
  const entity = new THREE.Group();
  entity.rotation.y = Math.PI;
  entity.scale.set(-PLAYER_SCALE / 16, -PLAYER_SCALE / 16, PLAYER_SCALE / 16);
  const model = part("model");
  model.position.y = -1.501 * 16;
  scene.add(pivotUp);
  pivotUp.add(tilt);
  tilt.add(pivotDown);
  pivotDown.add(entity);
  entity.add(model);
  const steve = buildSteve();
  model.add(steve.group);
  const wingMount = new THREE.Group();
  steve.parts.get("body").add(wingMount);

  const resize = () => {
    const w = body.clientWidth;
    const h = body.clientHeight;
    if (!w || !h) return;
    renderer.setSize(w, h);
    camera.aspect = w / h;
    camera.updateProjectionMatrix();
  };
  new ResizeObserver(resize).observe(body);

  const benches = new Map();
  const rigs = new Map();
  let index = null;
  let entry = null;
  let pairId = null;
  let manualPair = false;
  let manualClip = false;
  let bench = null;
  let rig = null;
  let clip = null;
  let time = 0;
  let playing = false;
  let visible = false;
  let last = 0;
  let tickAcc = 0;
  let loadToken = 0;
  const wind = { handle: null, starting: false, volume: 0.01, pitch: 1 };
  const buzz = { handle: null, starting: false, volume: 0.01, pitch: 1, base: 1, on: false };

  const message = (html) => {
    const m = $("msg");
    m.style.display = html ? "block" : "none";
    m.innerHTML = html || "";
  };

  async function loadIndex(force) {
    if (index && !force) return index;
    const r = await fetch("/api/rigs/index");
    if (r.status === 404) {
      const j = await r.json().catch(() => ({}));
      index = null;
      return { missing: true, baking: j.baking };
    }
    if (!r.ok) throw new Error(`${r.status} /api/rigs/index`);
    index = await r.json();
    return index;
  }

  async function bake() {
    message("Bake en cours (gradlew bakeBench, une à deux minutes)…");
    try {
      await api.send("POST", "/api/rigs/bake");
      benches.clear();
      index = null;
      await show(entry);
    } catch (e) {
      message(`Échec du bake : <pre>${String(e.message).replace(/</g, "&lt;")}</pre>`);
    }
  }

  function modelOf(id) {
    for (const m of index?.models || []) if (m.pairs.some((p) => p.id === id)) return m;
    const p = state.pairs.find((x) => x.id === id);
    return p ? (index?.models || []).find((m) => m.model === p.model) || { model: p.model, missing: true, pairs: [] } : null;
  }

  function pairInfo(id) {
    return modelOf(id)?.pairs?.find((p) => p.id === id) || state.pairs.find((p) => p.id === id) || {};
  }

  function pairFor(e) {
    const ids = new Set(state.pairs.map((p) => p.id));
    if (e.category === "wing" && ids.has(e.group)) return e.group;
    if (e.category === "fury") return /light/.test(e.group + e.id) ? "light" : "night";
    if (e.category === "wing") {
      const rep = FAMILY_PAIR[e.group];
      if (rep && ids.has(rep)) return rep;
      const flap = state.catalog.sounds.find((x) => /^wing\.[^.]+\.flap$/.test(x.id) && x.family === e.group && ids.has(x.group));
      if (flap) return flap.group;
    }
    const rigName = e.animations?.[0]?.rig;
    const byModel = state.pairs.filter((p) => p.model === rigName);
    const hit = byModel.find((p) => p.id === rigName) || byModel[0];
    return hit?.id || "common";
  }

  function clipFor(e) {
    const names = new Set(bench.clips.map((c) => c.name));
    const s = e.animations?.[0]?.scenario;
    if (s && names.has(s)) return s;
    const id = e.id;
    const c = /buzz/.test(id)
      ? "hover"
      : /flap_strong|launch|takeoff|unfold/.test(id)
        ? "takeoff"
        : /landing_hard/.test(id)
          ? "landing_hard"
          : /landing|\.fold/.test(id)
            ? "landing"
            : /wind/.test(id)
              ? "full_flight"
              : /brake/.test(id)
                ? "brake"
                : /stall/.test(id)
                  ? "stall"
                  : /tuck/.test(id)
                    ? "tuck"
                    : /dive/.test(id)
                      ? "dive_deep"
                      : /glide/.test(id)
                        ? "cruise_glide"
                        : /hover/.test(id)
                          ? "hover"
                          : "cruise_flap";
    return names.has(c) ? c : bench.clips[0].name;
  }

  function fillPairs() {
    const sel = $("pair");
    const list = [...state.pairs].sort((a, b) => a.id.localeCompare(b.id));
    sel.innerHTML = list.map((p) => `<option value="${p.id}">${p.id}${p.model !== p.id ? ` (${p.model})` : ""}</option>`).join("");
    sel.value = pairId;
  }

  function fillClips() {
    const sel = $("clip");
    sel.innerHTML = bench.clips.map((c) => `<option value="${c.name}">${c.name} (${c.length.toFixed(2)} s)</option>`).join("");
    sel.value = clip.name;
  }

  async function benchOf(name) {
    if (!benches.has(name)) {
      benches.set(
        name,
        api.get(`/api/rigs/${name}/bench`).catch((e) => {
          benches.delete(name);
          throw e;
        }),
      );
    }
    return benches.get(name);
  }

  async function rigOf(name) {
    if (!rigs.has(name)) {
      rigs.set(
        name,
        (async () => {
          const [bb, membranes] = await Promise.all([api.get(`/api/rigs/${name}/bbmodel`), api.get(`/api/rigs/${name}/membranes`)]);
          const texture = await loadTexture([`/api/assets/textures/entity/${name}_wings.png`, bb.textures?.[0]?.source]);
          const material = new THREE.MeshLambertMaterial({ map: texture, side: THREE.DoubleSide, alphaTest: 0.1, transparent: true });
          return buildRig(bb, membranes, material);
        })().catch((e) => {
          rigs.delete(name);
          throw e;
        }),
      );
    }
    return rigs.get(name);
  }

  function stopLoops() {
    wind.handle?.stop();
    wind.handle = null;
    wind.volume = 0.01;
    buzz.handle?.stop();
    buzz.handle = null;
    buzz.volume = 0.01;
  }

  function windWanted() {
    return $("wind").checked;
  }

  function continuous() {
    return !!(bench?.continuous ?? pairInfo(pairId).continuous);
  }

  function buzzInitial() {
    return continuous() && (clip.name === "hover" || clip.name === "idle_hover");
  }

  function buzzAt(t) {
    let on = buzzInitial();
    for (const ev of clip.events) {
      if (ev.t > t) break;
      if (ev.type === "buzz_start" || (ev.type === "mode_hover" && continuous())) on = true;
      if (ev.type === "buzz_stop") on = false;
    }
    return on;
  }

  async function selectPair(id) {
    pairId = id;
    $("pair").value = id;
    const token = ++loadToken;
    const m = modelOf(id);
    if (!m) return message(`Paire inconnue : ${id}`);
    if (m.missing) {
      message(`Le modèle <b>${m.model}</b> n'est pas dans le bake. <button data-a="bake2">Lancer le bake</button>`);
      $("bake2").onclick = bake;
      return;
    }
    message(`Chargement de ${m.model}…`);
    let b;
    let r;
    try {
      [b, r] = await Promise.all([benchOf(m.model), rigOf(m.model)]);
    } catch (e) {
      if (token === loadToken) message(`Impossible de charger ${m.model} : ${String(e.message).replace(/</g, "&lt;")}`);
      return;
    }
    if (token !== loadToken) return;
    message("");
    bench = b;
    stopLoops();
    if (rig) wingMount.remove(rig.root);
    rig = r;
    wingMount.add(rig.root);
    const keep = manualClip && clip && bench.clips.find((c) => c.name === clip.name);
    setClip(keep ? clip.name : clipFor(entry));
    const baked = m.baked_pair && m.baked_pair !== id ? ` · bake fait avec ${m.baked_pair}` : "";
    $("info").textContent = `${m.model} · flap ${bench.flap_rate}/s · hover ${bench.hover_rate}/s${bench.continuous ? " · continu" : ""}${rig.membrane ? " · membranes" : ""}${baked}`;
  }

  function setClip(name) {
    clip = bench.clips.find((c) => c.name === name) || bench.clips[0];
    fillClips();
    $("loop").checked = clip.loop === "loop";
    $("wind").checked = entry?.id === "flight.wind" || (clip.speed || 0) > 0 || clip.frames.some((f) => (f.speed || 0) > 0);
    stopLoops();
    seek(0);
    renderMarks();
    playing = true;
    $("play").textContent = "❚❚";
  }

  function frameAt(t) {
    const fps = bench.fps || 40;
    const frames = clip.frames;
    const x = Math.min(frames.length - 1, Math.max(0, t * fps));
    const i = Math.min(frames.length - 1, Math.floor(x));
    const j = Math.min(frames.length - 1, i + 1);
    return [frames[i], frames[j], x - i];
  }

  function apply(t) {
    const [a, b, f] = frameAt(t);
    const mix = (va, vb) => (vb ? va.map((v, k) => (k >= 6 && (va[6] === 0 || vb[6] === 0) ? v : lerp(v, vb[k], f))) : va);
    for (const [name, va] of Object.entries(a.bones)) {
      const o = rig.bones.get(name);
      if (o) setPose(o, mix(va, b.bones[name]));
    }
    for (const [name, va] of Object.entries(a.player || {})) {
      const o = steve.parts.get(name);
      if (o) setPose(o, mix(va, b.player?.[name]));
    }
    tilt.rotation.x = lerp(a.pitch || 0, b.pitch || 0, f) * (Math.PI / 180);
    tilt.rotation.y = lerp(a.roll || 0, b.roll || 0, f) * (Math.PI / 180);
    if (rig.membrane) rig.membrane.update(lerp(a.billow || 0, b.billow || 0, f), t * 20);
    $("cursor").style.left = `${(t / clip.length) * 100}%`;
    $("time").textContent = `${t.toFixed(2)} / ${clip.length.toFixed(2)} s · ${(a.speed ?? 0).toFixed(0)} m/s`;
  }

  function seek(t) {
    time = Math.min(clip.length, Math.max(0, t));
    buzz.on = buzzAt(time);
    apply(time);
  }

  function flapEntry(strong) {
    const sounds = state.catalog.sounds;
    return sounds.find((e) => e.id === `wing.${pairId}.${strong ? "flap_strong" : "flap"}`) || (strong ? sounds.find((e) => e.id === `wing.${pairId}.flap`) : null);
  }

  function entryOf(id) {
    return state.catalog.sounds.find((e) => e.id === id);
  }

  async function fire(ev, manual) {
    const type = ev.type;
    if (FLAP_TYPES.has(type)) {
      const e = flapEntry(STRONG_TYPES.has(type) || ev.strong);
      if (e) ctx.playGame(e, { volume: ev.volume ?? 0.7, pitch: ev.pitch ?? 1 });
      if (type === "mode_hover" && continuous()) buzz.on = true;
    } else if (type === "launch") {
      const e = entryOf("flight.takeoff_launch");
      if (e) ctx.playGame(e, { volume: ev.volume ?? 1, pitch: ev.pitch ?? 1 });
    } else if (type === "land_soft" || type === "land_hard") {
      ctx.playEvent(`simplywings:flight.landing_${type === "land_soft" ? "soft" : "hard"}`, {
        volume: ev.volume ?? (type === "land_soft" ? 0.6 : 0.8),
        pitch: 0.9 + Math.random() * 0.2,
        distance: state.distance,
      });
      const e = flapEntry(false);
      if (e && ev.flap_volume) ctx.playGame(e, { volume: ev.flap_volume, pitch: ev.flap_pitch ?? 1 });
    } else if (type === "buzz_start") {
      if (manual) ctx.playEvent(`simplywings:wing.${pairId}.buzz`, { distance: state.distance });
      else buzz.on = true;
    } else if (type === "buzz_stop") {
      buzz.on = false;
    }
  }

  function fireRange(a, b) {
    for (const ev of clip.events) {
      if (ev.silent) continue;
      if (ev.t > a && ev.t <= b) fire(ev, false);
    }
  }

  async function startLoop(slot, id, opts) {
    if (slot.handle || slot.starting) return;
    slot.starting = true;
    try {
      const h = await ctx.playEvent(id, { ...opts, loop: true });
      if (!h) return;
      if (!visible || !playing) h.stop();
      else slot.handle = h;
    } finally {
      slot.starting = false;
    }
  }

  function tickLoops(speed) {
    const windTarget = windWanted() ? clamp01((speed - 8) / 52) * 0.75 : 0;
    if (windWanted()) wind.pitch = 0.8 + 0.45 * clamp01(speed / 60);
    wind.volume += (windTarget - wind.volume) * 0.12;
    if (windTarget > 0 && !wind.handle) startLoop(wind, "simplywings:flight.wind", { volume: 0.01, positional: false });
    if (windTarget === 0 && wind.volume < 0.01 && wind.handle) {
      wind.handle.stop();
      wind.handle = null;
    }
    const on = buzz.on && continuous();
    const buzzTarget = on ? 0.45 + 0.35 * clamp01(speed / 40) : 0;
    buzz.pitch = buzz.base * (1 + 0.25 * clamp01(speed / 40));
    buzz.volume += (buzzTarget - buzz.volume) * (on ? 0.2 : 0.1);
    if (on && !buzz.handle) {
      buzz.base = 0.97 + Math.random() * 0.06;
      startLoop(buzz, `simplywings:wing.${pairId}.buzz`, { volume: 0.01, distance: state.distance });
    }
    if (!on && buzz.volume < 0.01 && buzz.handle) {
      buzz.handle.stop();
      buzz.handle = null;
    }
  }

  function modulate() {
    for (const [slot, positional] of [[wind, false], [buzz, true]]) {
      const h = slot.handle;
      if (!h) continue;
      const leaf = h.leaf || {};
      h.gainNode.gain.value = gain(slot.volume * (leaf.volume ?? 1), state.distance, leaf.attenuation, positional);
      h.src.playbackRate.value = Math.min(2, Math.max(0.5, slot.pitch * (leaf.pitch ?? 1)));
    }
  }

  function step(now) {
    if (!visible) return;
    requestAnimationFrame(step);
    const dt = Math.min(0.1, (now - (last || now)) / 1000);
    last = now;
    if (bench && rig && clip) {
      if (playing) {
        const rate = $("slow").checked ? 0.25 : 1;
        let next = time + dt * rate;
        if (next >= clip.length) {
          if ($("loop").checked) {
            fireRange(time, clip.length + 1e-6);
            next -= clip.length;
            buzz.on = buzzInitial();
            fireRange(-1, next);
          } else {
            fireRange(time, clip.length + 1e-6);
            next = clip.length;
            playing = false;
            $("play").textContent = "▶";
          }
        } else {
          fireRange(time, next);
        }
        tickAcc += next >= time ? next - time : next + clip.length - time;
        time = next;
        apply(time);
        const speed = frameAt(time)[0].speed ?? 0;
        while (tickAcc >= TICK) {
          tickAcc -= TICK;
          tickLoops(speed);
        }
        if (!playing) stopLoops();
      }
      modulate();
    }
    controls.update();
    renderer.render(scene, camera);
  }

  function renderMarks() {
    const tl = $("timeline");
    for (const el of [...tl.querySelectorAll(".mark, .label")]) el.remove();
    for (const ev of clip.events) {
      const left = `${(ev.t / clip.length) * 100}%`;
      const sound = SOUND_TYPES.has(ev.type) && !ev.silent;
      const m = document.createElement("div");
      m.className = "mark" + (sound ? "" : " silent");
      m.style.left = left;
      if (ev.type === "beat") {
        m.style.height = "6px";
        m.style.width = "3px";
        m.style.marginLeft = "-1px";
      }
      if (ev.type === "strike_strong" || ev.type === "launch") m.style.background = "var(--bad)";
      if (ev.type.startsWith("land")) m.style.background = "var(--ok)";
      if (ev.type.startsWith("buzz")) m.style.background = "var(--accent)";
      const extra = ["volume", "pitch", "flap_volume", "flap_pitch", "variant"].filter((k) => ev[k] != null).map((k) => `${k} ${ev[k]}`);
      m.title = `${ev.type} @ ${ev.t.toFixed(3)} s${ev.silent ? " (silencieux)" : ""}${extra.length ? " · " + extra.join(", ") : ""}`;
      m.onmousedown = (x) => x.stopPropagation();
      m.onclick = (x) => {
        x.stopPropagation();
        seek(ev.t);
        if (sound) fire(ev, true);
      };
      tl.appendChild(m);
      if (LABELLED.has(ev.type)) {
        const l = document.createElement("div");
        l.className = "label";
        l.style.left = left;
        l.textContent = ev.type;
        tl.appendChild(l);
      }
    }
  }

  const scrub = (x) => {
    const r = $("timeline").getBoundingClientRect();
    seek(((x.clientX - r.left) / r.width) * clip.length);
  };
  $("timeline").onmousedown = (x) => {
    if (!clip) return;
    scrub(x);
    const move = (y) => scrub(y);
    const up = () => {
      window.removeEventListener("mousemove", move);
      window.removeEventListener("mouseup", up);
    };
    window.addEventListener("mousemove", move);
    window.addEventListener("mouseup", up);
  };
  $("play").onclick = () => {
    if (!clip) return;
    if (!playing && time >= clip.length - 1e-6) seek(0);
    playing = !playing;
    $("play").textContent = playing ? "❚❚" : "▶";
    if (!playing) stopLoops();
  };
  $("pair").onchange = (x) => {
    manualPair = true;
    selectPair(x.target.value);
  };
  $("clip").onchange = (x) => {
    manualClip = true;
    setClip(x.target.value);
  };
  $("wind").onchange = () => {
    if (!windWanted()) wind.volume = Math.min(wind.volume, 0.009);
  };
  $("rear").onclick = () => view("rear");
  $("side").onclick = () => view("side");
  $("rebake").onclick = bake;

  async function show(e) {
    if (!e) return;
    const fresh = entry?.id !== e.id;
    entry = e;
    if (!visible) {
      visible = true;
      last = 0;
      requestAnimationFrame(step);
    }
    resize();
    let idx;
    try {
      idx = await loadIndex(false);
    } catch (err) {
      return message(`Erreur : ${err.message}`);
    }
    if (idx.missing) {
      message(
        idx.baking
          ? "Bake en cours (gradlew bakeBench)… recharge la vue quand il est terminé."
          : `Pas de bake des rigs (build/rigbake/index.json absent). <button data-a="bake1">Lancer le bake</button>`,
      );
      if (!idx.baking) $("bake1").onclick = bake;
      return;
    }
    $("rebake").title = idx.baking ? "bake en cours" : "gradlew bakeBench";
    const specific = e.category === "wing" && state.pairs.some((p) => p.id === e.group);
    if (specific || e.category === "fury") manualPair = false;
    if (fresh && !manualClip) clip = null;
    if (fresh) manualClip = false;
    fillPairs();
    const id = manualPair && pairId ? pairId : pairFor(e);
    if (id === pairId && bench && rig) {
      setClip(clipFor(e));
      return;
    }
    await selectPair(id);
  }

  return {
    show,
    hide() {
      visible = false;
      playing = false;
      $("play").textContent = "▶";
      stopLoops();
    },
  };
}
