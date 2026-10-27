import { ELEMENTS, TIERS, meta } from "./show.js";

export const ANCHORS = ["altar", "crystal", "hover", "top", "flame", "star", "multi_star_{i}", "cloud_{i}"];
export const EACH = ["ignite", "beats", "spawn", "flights"];

function tierOf(v, fallback) {
  if (v === undefined || v === null) return fallback;
  if (typeof v === "number") return v | 0;
  const i = TIERS.indexOf(String(v).toLowerCase());
  return i < 0 ? fallback : i;
}

function floats(v) {
  if (v === undefined || v === null) return [0];
  return Array.isArray(v) ? v.map(Number) : [Number(v)];
}

const num = (o, k, d) => (o[k] === undefined ? d : Number(o[k]));

export function parseCue(o, extraTier) {
  const sounds = Array.isArray(o.sound) ? o.sound : [o.sound];
  const fallbackEnd = o.or === "end";
  return {
    raw: o,
    sounds,
    from: o.from ?? "start",
    fallback: o.or === undefined || fallbackEnd ? 0 : Number(o.or),
    fallbackEnd,
    each: o.each ?? null,
    t: floats(o.t),
    window: num(o, "window", -1),
    volume: num(o, "volume", 1),
    pitch: num(o, "pitch", 1),
    volumeStep: num(o, "volume_step", 0),
    pitchStep: num(o, "pitch_step", 0),
    byTier: (o.step_by ?? "index") === "tier",
    pitchRandom: num(o, "pitch_random", 0),
    volumePower: num(o, "volume_power", 0),
    at: o.at ?? "altar",
    offset: o.offset ? floats(o.offset) : [0, 0, 0],
    sample: num(o, "sample", 0),
    index: o.index === undefined ? -1 : Number(o.index) | 0,
    edge: o.edge === undefined ? -1 : Number(o.edge) | 0,
    minRank: num(o, "min_rank", -Infinity),
    tier: tierOf(extraTier ?? o.tier, -1),
    minTier: tierOf(o.min_tier, -Infinity),
    maxTier: tierOf(o.max_tier, Infinity),
    meta: o.meta ?? null,
  };
}

export function sound(c, i) {
  return c.sounds[Math.max(0, Math.min(c.sounds.length - 1, i))];
}

export function tierMatches(c, v) {
  return (c.tier < 0 || v === c.tier) && v >= c.minTier && v <= c.maxTier;
}

export function listFor(json, animName) {
  return json[animName] ?? json.default ?? null;
}

export function power(anim) {
  const rank = meta(anim, "rank", 0);
  const myth = anim.meta.mythic;
  return 0.35 + (0.65 * rank) / 4 + (typeof myth === "string" && myth !== "" ? 0.25 : 0);
}

function metaPositive(anim, key) {
  const v = meta(anim, key, 0);
  return v > 0;
}

function points(c, anim) {
  if (c.each === null) {
    const base = c.from === "start" ? 0 : meta(anim, c.from, c.fallbackEnd ? anim.length : c.fallback);
    return [{ base, i: 0, edge: 0 }];
  }
  const arr = anim.meta[c.each];
  if (!Array.isArray(arr)) return [];
  const out = [];
  let n = 0;
  for (const p of arr) {
    if (Array.isArray(p)) {
      out.push({ base: Number(p[0]), i: n++, edge: 0 });
      out.push({ base: Number(p[0]) + Number(p[1]), i: n++, edge: 1 });
    } else out.push({ base: Number(p), i: n++, edge: 0 });
  }
  return out;
}

function admitted(c, anim) {
  const rank = meta(anim, "rank", 0);
  return !((c.meta !== null && !metaPositive(anim, c.meta)) || rank < c.minRank);
}

function pointOk(c, p, tiers) {
  if ((c.index >= 0 && p.i !== c.index) || (c.edge >= 0 && p.edge !== c.edge)) return null;
  const tier = p.i < tiers.length ? tiers[p.i] : 0;
  if (!tierMatches(c, tier)) return null;
  if (c.at === "flame" && p.i >= ELEMENTS.length) return null;
  return tier;
}

export function resolve(cues, anim, tiers) {
  const out = [];
  cues.forEach((c, ci) => {
    if (!admitted(c, anim)) return;
    for (const p of points(c, anim)) {
      const tier = pointOk(c, p, tiers);
      if (tier === null) continue;
      c.t.forEach((t, k) => out.push({ c, ci, k, i: p.i, edge: p.edge, base: p.base, tier, time: p.base + t, sound: sound(c, p.i) }));
    }
  });
  return out;
}

export function fire(c, p, tier, pw) {
  const step = c.byTier ? tier : p.i;
  return {
    sound: sound(c, p.i),
    volume: c.volume + c.volumeStep * step + c.volumePower * pw,
    pitch: c.pitch + c.pitchStep * step + (c.pitchRandom > 0 ? Math.random() * c.pitchRandom : 0),
    at: c.at,
    i: p.i,
    sampleTime: p.base + c.sample,
    offset: c.offset,
  };
}

export function tick(cues, anim, tiers, lastTime, now) {
  const pw = power(anim);
  const fired = [];
  for (const c of cues) {
    if (!admitted(c, anim)) continue;
    for (const p of points(c, anim)) {
      const tier = pointOk(c, p, tiers);
      if (tier === null) continue;
      let go = false;
      for (const t of c.t) {
        const at = p.base + t;
        if (lastTime < at && now >= at && (c.window < 0 || now < at + c.window)) go = true;
      }
      if (go) fired.push({ ...fire(c, p, tier, pw), c });
    }
  }
  return fired;
}

export function parseCards(json) {
  const out = {};
  for (const [key, v] of Object.entries(json.cards || {})) {
    if (Array.isArray(v)) out[key] = v.map((o) => parseCue(o));
    else out[key] = Object.entries(v).flatMap(([tier, list]) => list.map((o) => parseCue(o, tier)));
  }
  return out;
}

export function cardSounds(cards, key, tier) {
  return (cards[key] || [])
    .filter((c) => tierMatches(c, tier))
    .map((c) => ({ c, sound: sound(c, 0), pitch: c.pitch + c.pitchStep * tier + (c.pitchRandom > 0 ? Math.random() * c.pitchRandom : 0), volume: c.volume }));
}

const PRE = [0, 0, 0.16, 0.32, 0.48, 0.6];
const FLIP = [0.2, 0.28, 0.42, 0.62, 0.8, 0.95];
const GAP = [0.1, 0.14, 0.3, 0.45, 0.6, 0.7];

export function deck(tiers) {
  const n = tiers.length;
  let f = 0.07 * n + 0.35;
  const cards = tiers.map((r, i) => {
    const c = { tier: r, deal: 0.07 * i, antic: f, flipStart: f + PRE[r] };
    c.flipEnd = c.flipStart + FLIP[r];
    f = c.flipStart + FLIP[r] * 0.6 + GAP[r];
    return c;
  });
  return { cards, end: n ? cards[n - 1].flipEnd : 0 };
}

export function deckState(d) {
  return { d, offset: 0, flags: d.cards.map(() => ({ dealt: false, tensed: false, flipped: false, opened: false })) };
}

export function deckDone(s, t) {
  return !s.d.cards.length || t + s.offset > s.d.end + 0.25;
}

export function deckFinish(s, t) {
  if (s.d.cards.length) s.offset = Math.max(s.offset, s.d.end + 0.3 - t);
}

export function deckStep(s, time, emit) {
  const t = time + s.offset;
  s.d.cards.forEach((c, i) => {
    const f = s.flags[i];
    if (!f.dealt && t >= c.deal) {
      f.dealt = true;
      if (t - c.deal < 0.15) emit("deal", c.tier, i);
    }
    if (!f.tensed && c.tier >= 2 && t >= c.antic) {
      f.tensed = true;
      if (t < c.flipStart) emit("shuffle", c.tier, i);
    }
    if (!f.flipped && t >= c.flipStart) {
      f.flipped = true;
      if (t < c.flipEnd) emit("flip", c.tier, i);
    }
    if (!f.opened && t >= c.flipEnd) {
      f.opened = true;
      emit("open", c.tier, i);
    }
  });
  return t;
}

export function deckEvents(d) {
  const out = [];
  d.cards.forEach((c, i) => {
    out.push({ key: "deal", time: c.deal, tier: c.tier, i });
    if (c.tier >= 2) out.push({ key: "shuffle", time: c.antic, tier: c.tier, i });
    out.push({ key: "flip", time: c.flipStart, tier: c.tier, i });
    out.push({ key: "open", time: c.flipEnd, tier: c.tier, i });
  });
  return out;
}
