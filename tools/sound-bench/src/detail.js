import WaveSurfer from "wavesurfer.js";
import RegionsPlugin from "wavesurfer.js/dist/plugins/regions.esm.js";
import { buffer, playBuffer, playEvent, previewBuffer, playPreview, stopAll, leaves } from "./audio.js";
import { api, state, save, build, log, target, reload } from "./main.js";

const VERDICTS = ["keep", "reject", "revise", "validated", "in_game", "todo"];
const STATUSES = ["custom", "vanilla", "alias", "silent", "todo"];
const FIELDS = ["start", "end", "fade_in", "fade_out", "pitch", "lp", "gain_db", "peak", "tempo", "loop", "echo"];
const waves = [];

function h(tag, attrs = {}, ...children) {
  const el = document.createElement(tag);
  for (const [k, v] of Object.entries(attrs)) {
    if (k.startsWith("on")) el.addEventListener(k.slice(2), v);
    else if (k === "class") el.className = v;
    else if (v !== undefined && v !== null && v !== false) el.setAttribute(k, v === true ? "" : v);
  }
  for (const c of children.flat()) if (c != null && c !== false) el.append(c instanceof Node ? c : document.createTextNode(String(c)));
  return el;
}

function fmt(v, d = 1) {
  return v == null || !isFinite(v) ? "—" : Number(v).toFixed(d);
}

function pairOf(e) {
  return state.pairs.find((p) => p.id === e.group);
}

export function gameCall(e) {
  if (e.category === "ui") return { volume: 1, pitch: 1, positional: false, jitter: false };
  if (e.category === "wing" && /flap/.test(e.id)) return { volume: 0.7, pitch: 1, positional: true, jitter: true };
  if (e.category === "wing" && /layer/.test(e.id)) return { volume: 1, pitch: 1, positional: true, jitter: false };
  if (e.id === "flight.landing_soft") return { volume: 0.6, pitch: 1, positional: true, jitter: false, range: [0.9, 1.1] };
  if (e.id === "flight.landing_hard") return { volume: 0.8, pitch: 1, positional: true, jitter: false, range: [0.9, 1.1] };
  return { volume: 1, pitch: 1, positional: true, jitter: false };
}

function layersOf(e) {
  return (e.layers || []).map((l) => (typeof l === "string" ? { id: l } : { id: l.event, volume: l.volume, pitch: l.pitch }));
}

export async function playGame(e, opts = {}) {
  const call = { ...gameCall(e), ...opts };
  let pitch = call.pitch;
  if (call.jitter) pitch *= 0.94 + Math.random() * 0.12;
  if (call.range) pitch *= call.range[0] + Math.random() * (call.range[1] - call.range[0]);
  const id = "simplywings:" + e.id;
  const handles = [await playEvent(id, { volume: call.volume, pitch, distance: state.distance, positional: call.positional, when: call.when || 0 })];
  for (const l of layersOf(e)) {
    handles.push(await playEvent(l.id.includes(":") ? l.id : "simplywings:" + l.id, {
      volume: (l.volume ?? 1) * (call.volume / 0.7),
      pitch: l.pitch ?? 1,
      distance: state.distance,
      positional: call.positional,
      when: call.when || 0,
    }));
  }
  return handles;
}

export async function playReference(e) {
  if (!e.reference) return;
  const call = gameCall(e);
  return playEvent(e.reference, { volume: call.volume, distance: state.distance, positional: call.positional });
}

export async function playVariant(e, i) {
  const owner = e.variants_from ? state.catalog.sounds.find((x) => x.id === e.variants_from) : e;
  const v = owner?.variants?.[i];
  if (!v) return;
  const buf = await buffer(`/api/audio/mod/${v.file}.ogg`);
  const call = gameCall(e);
  return playBuffer(buf, { volume: call.volume * (e.volume ?? 1), pitch: e.pitch ?? 1, distance: state.distance, attenuation: e.attenuation || 16, positional: call.positional });
}

async function burst(e) {
  const pair = pairOf(e);
  const rate = pair?.flap_rate || 2;
  const interval = Math.max(0.05, Math.max(1, Math.round(20 / rate)) / 20);
  for (let k = 0; k < 8; k++) playGame(e, { when: k * interval });
}

function measureLine(m, e) {
  if (!m) return h("span", {}, "mesure…");
  const t = target(e);
  const rmsClass = t?.rms_max_db == null ? "" : Math.abs(m.rms_max_db - t.rms_max_db) > 2 ? "bad" : "ok";
  const attackClass = e.slow_attack ? "" : m.attack_ms > 10 ? "bad" : "ok";
  const tp = m.true_peak_db != null && m.true_peak_db > -1 ? "bad" : "ok";
  const seam = e.loop ? h("span", { class: m.loop_seam > 0.005 ? "bad" : "ok" }, ` · raccord ${fmt(m.loop_seam, 3)}`) : null;
  return h(
    "span",
    {},
    `${fmt(m.duration, 2)} s · `,
    h("span", { class: rmsClass, title: t ? `cible ${t.rms_max_db} dB` : "pas de cible" }, `RMS max ${fmt(m.rms_max_db)} dB`),
    ` · ${m.lufs == null || m.lufs < -69 ? "LUFS —" : `${fmt(m.lufs)} LUFS`} · `,
    h("span", { class: tp }, `pic vrai ${fmt(m.true_peak_db)} dBTP`),
    " · ",
    h("span", { class: attackClass }, `attaque ${fmt(m.attack_ms, 0)} ms`),
    ` · centre ${fmt(m.centroid_hz, 0)} Hz · ${m.sample_rate || "?"} Hz ${m.channels || ""}`,
    seam,
  );
}

async function measure(kind, path) {
  const key = kind === "mod" ? path.replace(/\.ogg$/, "") : `${kind}:${path}`;
  if (!state.measures.has(key)) state.measures.set(key, await api.get(`/api/measure?kind=${kind}&path=${encodeURIComponent(path)}`));
  return state.measures.get(key);
}

function wave(container, url, height = 40) {
  const ws = WaveSurfer.create({ container, url, height, waveColor: "#5a6b88", progressColor: "#6aa5ff", cursorWidth: 0, interact: false, normalize: false });
  waves.push(ws);
  return ws;
}

function seamCanvas(buf) {
  const c = h("canvas", { class: "seam", width: 600, height: 60 });
  const g = c.getContext("2d");
  const d = buf.getChannelData(0);
  const n = Math.min(Math.floor(buf.sampleRate * 0.05), Math.floor(d.length / 2));
  const joined = new Float32Array(2 * n);
  joined.set(d.subarray(d.length - n), 0);
  joined.set(d.subarray(0, n), n);
  g.strokeStyle = "#6aa5ff";
  g.beginPath();
  for (let i = 0; i < joined.length; i++) {
    const x = (i / joined.length) * 600;
    const y = 30 - joined[i] * 28;
    i ? g.lineTo(x, y) : g.moveTo(x, y);
  }
  g.stroke();
  g.strokeStyle = "#ef6464";
  g.beginPath();
  g.moveTo(300, 0);
  g.lineTo(300, 60);
  g.stroke();
  return c;
}

function editor(e, v, index) {
  const box = h("div", { class: "editor" });
  const draft = { ...v };
  const fields = h("div", { class: "fields" });
  for (const f of FIELDS) {
    const input = h("input", { type: "number", step: f === "lp" ? 100 : f === "pitch" || f === "tempo" ? 0.01 : 0.001, value: draft[f] ?? "" });
    input.oninput = () => {
      draft[f] = input.value === "" ? null : parseFloat(input.value);
      syncRegion();
    };
    input.dataset.field = f;
    fields.append(h("label", {}, f, input));
  }
  const waveBox = h("div", { class: "wave" });
  let region = null;
  let ws = null;
  let source = null;
  const syncFields = () => {
    for (const input of fields.querySelectorAll("input")) input.value = draft[input.dataset.field] ?? "";
  };
  const syncRegion = () => {
    if (region && ws) region.setOptions({ start: (draft.start || 0) * (draft.pitch || 1) / (draft.tempo || 1) * 0 + (draft.start || 0), end: draft.end ?? ws.getDuration() });
  };
  const isSynth = String(v.source).startsWith("synth:");
  if (!isSynth) {
    const url = `/api/source/${encodeURIComponent(v.source)}`;
    ws = wave(waveBox, url, 70);
    const regions = ws.registerPlugin(RegionsPlugin.create());
    ws.on("decode", () => {
      region = regions.addRegion({ start: draft.start || 0, end: draft.end ?? ws.getDuration(), color: "rgba(106,165,255,0.18)", drag: true, resize: true });
      region.on("update-end", () => {
        draft.start = Math.round(region.start * 1000) / 1000;
        draft.end = Math.round(region.end * 1000) / 1000;
        syncFields();
      });
    });
  } else {
    waveBox.append(h("p", { class: "hint" }, `Son synthétisé (${v.source}) : réglages dans build_sounds.py, fonction synth.`));
  }
  const getSource = async () => (source ||= await buffer(`/api/source/${encodeURIComponent(v.source)}`));
  const preview = async (loop = false) => {
    stopAll();
    const src = await getSource();
    const t = target(e);
    const out = previewBuffer(src, { ...draft, loop: loop || draft.loop }, e.loudness_target && t?.rms_max_db != null ? t.rms_max_db : null);
    playPreview(out, { ...draft, loop: loop || !!draft.loop }, { volume: gameCall(e).volume * (e.volume ?? 1), distance: state.distance, attenuation: e.attenuation || 16, positional: gameCall(e).positional });
    if (e.loop) {
      box.querySelector("canvas.seam")?.remove();
      box.append(seamCanvas(out));
    }
  };
  const apply = async (andBuild) => {
    const owner = state.catalog.sounds.find((x) => x.id === e.id);
    const variants = owner.variants.map((x, i) => (i === index ? { ...draft } : x));
    await save(e.id, { variants });
    if (andBuild) {
      await build([e.id]);
      await reload();
    }
    renderDetail(document.querySelector("#detail"), state.selected);
  };
  box.append(
    waveBox,
    fields,
    h(
      "div",
      { class: "row" },
      !isSynth && h("button", { onclick: () => preview(false) }, "▶ Aperçu"),
      !isSynth && e.loop && h("button", { onclick: () => preview(true) }, "▶ Boucle sans fin"),
      h("button", { onclick: () => stopAll() }, "■"),
      !isSynth &&
        h(
          "button",
          {
            onclick: async () => {
              const r = await api.send("POST", "/api/trim", draft);
              draft.start = r.start;
              syncFields();
              syncRegion();
            },
            title: "Début 3 ms avant le premier passage à −30 dB du pic",
          },
          "Rogner l'attaque",
        ),
      h("button", { onclick: () => apply(false) }, "Appliquer"),
      h("button", { class: "primary", onclick: () => apply(true) }, "Appliquer + construire"),
    ),
    h("p", { class: "hint" }, "Aperçu instantané en Web Audio depuis la source (tempo ≠ 1 non simulé). « Appliquer » écrit dans le catalogue ; « construire » réencode l'OGG."),
  );
  return box;
}

function variantRow(e, v, i, owned) {
  const row = h("div", { class: "variant" });
  const waveBox = h("div", { class: "wave" });
  const meas = h("div", { class: "measures" }, "…");
  const play = h("button", { title: `variation ${i + 1}`, onclick: () => playVariant(e, i) }, `▶${i + 1}`);
  row.append(play, h("div", {}, h("div", { class: "meta" }, `${v.file}  ←  ${v.source} [${fmt(v.start, 2)} → ${v.end == null ? "fin" : fmt(v.end, 2)}]`), waveBox), meas);
  wave(waveBox, `/api/audio/mod/${v.file}.ogg`);
  measure("mod", v.file + ".ogg")
    .then((m) => meas.replaceChildren(measureLine(m, e)))
    .catch(() => meas.replaceChildren("fichier absent : construire"));
  if (owned) {
    let open = null;
    const toggle = h("button", {}, "Éditer");
    toggle.onclick = () => {
      if (open) {
        open.remove();
        open = null;
      } else {
        open = editor(e, v, i);
        row.append(open);
      }
    };
    const remove = h("button", {
      title: "Retirer cette variation du catalogue",
      onclick: async () => {
        const owner = state.catalog.sounds.find((x) => x.id === e.id);
        await save(e.id, { variants: owner.variants.filter((_, k) => k !== i) });
        renderDetail(document.querySelector("#detail"), state.selected);
      },
    }, "✕");
    meas.after(h("div", { class: "measures row" }, toggle, remove));
  }
  return row;
}

function aliasEditor(e) {
  const key = e.status === "alias" ? "alias" : "vanilla";
  const data = { ...(e[key] || {}) };
  const targetField = key === "alias" ? "target" : "event";
  const box = h("div", { class: "box" }, h("div", { class: "title" }, key === "alias" ? "Alias" : "Événement vanilla"));
  const inputs = {};
  for (const f of [targetField, "volume", "pitch"]) {
    inputs[f] = h("input", { value: data[f] ?? (f === targetField ? "" : 1), type: f === targetField ? "text" : "number", step: 0.01, size: f === targetField ? 42 : undefined, list: f === "event" ? "vanilla-events" : undefined });
  }
  box.append(
    h("div", { class: "fields" }, ...Object.entries(inputs).map(([f, i]) => h("label", {}, f, i))),
    h(
      "div",
      { class: "row" },
      h("button", {
        onclick: async () => {
          const out = { [targetField]: inputs[targetField].value.trim(), volume: parseFloat(inputs.volume.value), pitch: parseFloat(inputs.pitch.value) };
          await save(e.id, { [key]: out });
          renderDetail(document.querySelector("#detail"), state.selected);
        },
      }, "Appliquer"),
      h("button", { onclick: () => playEvent(inputs[targetField].value.trim(), { volume: parseFloat(inputs.volume.value), pitch: parseFloat(inputs.pitch.value), distance: state.distance }) }, "▶ cible"),
    ),
  );
  vanillaList();
  return box;
}

let vanillaLoaded = false;
async function vanillaList() {
  if (vanillaLoaded) return;
  vanillaLoaded = true;
  const events = await api.get("/api/vanilla/events");
  const dl = h("datalist", { id: "vanilla-events" }, ...events.map((x) => h("option", { value: "minecraft:" + x })));
  document.body.append(dl);
}

function layersBox(e) {
  const box = h("div", { class: "box" }, h("div", { class: "title" }, "Couches jouées en même temps"));
  const list = layersOf(e);
  if (!list.length) box.append(h("p", { class: "hint" }, "Aucune couche."));
  list.forEach((l, i) => {
    const sub = state.catalog.sounds.find((x) => x.id === l.id);
    box.append(
      h(
        "div",
        { class: "row" },
        h("button", { onclick: () => playEvent(l.id.includes(":") ? l.id : "simplywings:" + l.id, { volume: (l.volume ?? 1), pitch: l.pitch ?? 1, distance: state.distance }) }, "▶"),
        sub ? h("a", { href: "#" + l.id }, l.id) : l.id,
        l.volume != null ? ` vol ${l.volume} · hauteur ${l.pitch}` : sub?.vanilla ? ` → ${sub.vanilla.event} vol ${sub.vanilla.volume} · hauteur ${sub.vanilla.pitch}` : "",
        typeof (e.layers || [])[i] !== "string" &&
          h("button", {
            onclick: async () => {
              await save(e.id, { layers: e.layers.filter((_, k) => k !== i) });
              renderDetail(document.querySelector("#detail"), state.selected);
            },
          }, "✕"),
      ),
    );
  });
  return box;
}

function candidatesBox(e) {
  const box = h("div", { class: "box" }, h("div", { class: "title" }, "Candidats", h("span", { class: "hint" }, "tools/sounds/candidates/" + e.id)));
  const drop = h("div", { class: "drop" }, "Glisser un mp3, wav, flac ou ogg ici");
  drop.ondragover = (ev) => {
    ev.preventDefault();
    drop.classList.add("over");
  };
  drop.ondragleave = () => drop.classList.remove("over");
  drop.ondrop = async (ev) => {
    ev.preventDefault();
    drop.classList.remove("over");
    for (const f of ev.dataTransfer.files) {
      await fetch(`/api/candidates/${encodeURIComponent(e.id)}?name=${encodeURIComponent(f.name)}`, { method: "POST", body: f });
    }
    renderDetail(document.querySelector("#detail"), state.selected);
  };
  const list = h("div");
  box.append(drop, list);
  api.get(`/api/candidates/${encodeURIComponent(e.id)}`).then((names) => {
    for (const name of names) {
      const url = `/api/candidates/${encodeURIComponent(e.id)}/${encodeURIComponent(name)}`;
      const waveBox = h("div", { class: "wave" });
      const meas = h("div", { class: "measures" }, "…");
      const form = h("div", { class: "fields", style: "display:none" });
      const license = h("select", {}, h("option", {}, "CC0"), h("option", {}, "CC-BY 4.0"), h("option", {}, "autre"));
      const page = h("input", { placeholder: "page (URL)", size: 30 });
      const author = h("input", { placeholder: "auteur" });
      const why = h("input", { placeholder: "justification si pas CC0", size: 30 });
      form.append(
        h("label", {}, "licence", license),
        page,
        author,
        why,
        h("button", {
          class: "primary",
          onclick: async () => {
            try {
              await api.send("POST", `/api/candidates/${encodeURIComponent(e.id)}/promote`, { name, license: license.value, page: page.value, author: author.value, justification: why.value });
              await reload();
              await build([e.id]);
              renderDetail(document.querySelector("#detail"), state.selected);
            } catch (err) {
              log("Promotion", err.message);
            }
          },
        }, "Confirmer"),
      );
      list.append(
        h(
          "div",
          { class: "variant" },
          h("button", {
            onclick: async () => {
              stopAll();
              const src = await buffer(url);
              const t = target(e);
              const out = previewBuffer(src, { start: 0, end: null, fade_out: 0.05 }, t?.rms_max_db ?? -14);
              playPreview(out, {}, { volume: gameCall(e).volume, distance: state.distance, positional: gameCall(e).positional });
            },
            title: "normalisé à la cible",
          }, "▶"),
          h("div", {}, h("div", { class: "meta" }, name), waveBox),
          meas,
          h(
            "div",
            { class: "measures row" },
            h("button", { onclick: () => (form.style.display = form.style.display ? "" : "none") }, "Promouvoir"),
            h("button", {
              onclick: async () => {
                await fetch(url, { method: "DELETE" });
                renderDetail(document.querySelector("#detail"), state.selected);
              },
            }, "✕"),
          ),
          h("div", { class: "editor" }, form),
        ),
      );
      wave(waveBox, url);
      measure("candidate", `${e.id}/${name}`).then((m) => meas.replaceChildren(measureLine(m, e))).catch(() => meas.replaceChildren("mesure impossible"));
    }
  });
  return box;
}

export function renderDetail(root, e) {
  while (waves.length) waves.pop().destroy();
  root.innerHTML = "";
  const owner = e.variants_from ? state.catalog.sounds.find((x) => x.id === e.variants_from) : e;
  const variants = owner?.variants || [];
  const t = target(e);
  const keywords = (e.keywords || []).join(" ");
  const statusSel = h("select", {}, ...STATUSES.map((s) => h("option", { selected: s === e.status }, s)));
  statusSel.onchange = async () => {
    await save(e.id, { status: statusSel.value });
    renderDetail(root, state.selected);
  };
  const distance = h("input", { type: "range", min: 1, max: 48, step: 1, value: state.distance });
  const distLabel = h("span", {}, `${state.distance} blocs`);
  distance.oninput = () => {
    state.distance = parseInt(distance.value, 10);
    distLabel.textContent = `${state.distance} blocs`;
  };
  const note = h("textarea", { rows: 2, style: "width:100%", placeholder: "note" });
  note.value = e.verdict?.note || "";
  note.onchange = () => save(e.id, { verdict: { ...e.verdict, note: note.value } });
  const kwInput = h("input", { value: keywords, size: 50 });
  kwInput.onchange = () => save(e.id, { keywords: kwInput.value.split(/\s*,\s*/).filter(Boolean) });
  kwInput.value = (e.keywords || []).join(", ");
  root.append(
    h("h2", {}, e.id, " ", statusSel),
    h("div", { class: "meta" }, `${e.category} › ${e.group}`, e.family ? ` · famille ${e.family}` : "", e.loudness_target ? ` · cible ${e.loudness_target}${t ? ` (${t.rms_max_db} dB)` : ""}` : "", e.attenuation ? ` · atténuation ${e.attenuation}` : " · atténuation 16 (défaut)", e.preload ? " · preload" : "", e.loop ? " · boucle" : "", e.license_blocker ? " · ⚠ sample non libre, bloquant pour la publication" : ""),
    e.trigger && h("div", { class: "meta" }, "Déclencheur : ", e.trigger),
    (e.code || []).length > 0 && h("div", { class: "meta" }, "Code : ", e.code.join(", ")),
    e.description && h("p", {}, e.description),
    h(
      "div",
      { class: "meta" },
      "Mots-clés : ",
      ...(e.keywords || []).map((k) => h("span", { class: "kw", title: "copier", onclick: () => navigator.clipboard.writeText(k) }, `[${k}]`)),
      (e.keywords || []).length > 0 && h("a", { href: `https://freesound.org/search/?q=${encodeURIComponent(keywords)}&f=license:%22Creative+Commons+0%22`, target: "_blank" }, " Freesound CC0 ↗"),
    ),
    h("div", { class: "meta" }, "Éditer les mots-clés (séparés par des virgules) : ", kwInput),
    h(
      "div",
      { class: "box" },
      h(
        "div",
        { class: "row" },
        h("button", { class: "primary", onclick: () => playGame(e), title: "Espace" }, "▶ Jeu"),
        h("button", { onclick: () => playReference(e), disabled: !e.reference, title: e.reference ? `R : ${e.reference}` : "pas de référence" }, "▶ Référence"),
        h("button", { onclick: () => burst(e), title: "8 déclenchements au rythme de la paire" }, "▶ Rafale"),
        h("button", { onclick: () => stopAll() }, "■"),
        h("label", {}, "distance ", distance, distLabel),
      ),
      h(
        "div",
        { class: "row verdicts" },
        "Verdict : ",
        ...VERDICTS.map((v) =>
          h("button", {
            class: e.verdict?.state === v ? `on v-${v}` : `v-${v}`,
            onclick: async () => {
              await save(e.id, { verdict: { ...e.verdict, state: v, date: new Date().toISOString().slice(0, 10) } });
              renderDetail(root, state.selected);
            },
          }, v),
        ),
        e.verdict?.date ? h("span", { class: "hint" }, e.verdict.date) : null,
      ),
      note,
    ),
  );
  if (e.status === "alias" || e.status === "vanilla") root.append(aliasEditor(e));
  if (e.status !== "silent") root.append(layersBox(e));
  if (variants.length || e.status === "custom" || e.status === "todo") {
    const box = h(
      "div",
      { class: "box" },
      h(
        "div",
        { class: "title" },
        `Variations (${variants.length})`,
        e.variants_from ? h("span", { class: "hint" }, `fichiers de ${e.variants_from}`) : null,
        h("span", { class: "spacer" }),
        h("button", { onclick: () => build([owner.id]).then(() => renderDetail(root, state.selected)) }, "Construire"),
        e.loudness_target &&
          h("button", {
            title: "Remet le gain de chaque variation à 0 : la construction aligne tout le groupe sur la cible de sonie",
            onclick: async () => {
              for (const x of state.catalog.sounds.filter((s) => s.group === e.group && s.category === e.category && (s.variants || []).length)) {
                await save(x.id, { variants: x.variants.map((v) => ({ ...v, gain_db: 0 })) });
              }
              await build(state.catalog.sounds.filter((s) => s.group === e.group && s.category === e.category).map((s) => s.id));
              renderDetail(root, state.selected);
            },
          }, "Normaliser le groupe"),
      ),
    );
    variants.forEach((v, i) => box.append(variantRow(e, v, i, !e.variants_from)));
    root.append(box);
  }
  if (e.status !== "alias" || e.category !== "wing") root.append(candidatesBox(e));
  leaves("simplywings:" + e.id).then((l) => {
    if (!l.length && e.status !== "silent" && e.declared !== false) root.querySelector(".box")?.prepend(h("p", { class: "hint" }, "Rien à jouer pour cet id (pas encore construit ?)."));
  });
}
