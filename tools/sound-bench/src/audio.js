export const ctx = new AudioContext();
const master = ctx.createGain();
master.connect(ctx.destination);
const buffers = new Map();
const resolved = new Map();
const playing = new Set();

export function setMaster(v) {
  master.gain.value = v;
}

export async function buffer(url) {
  if (!buffers.has(url)) {
    buffers.set(
      url,
      fetch(url)
        .then((r) => {
          if (!r.ok) throw new Error(`${r.status} ${url}`);
          return r.arrayBuffer();
        })
        .then((b) => ctx.decodeAudioData(b)),
    );
  }
  try {
    return await buffers.get(url);
  } catch (e) {
    buffers.delete(url);
    throw e;
  }
}

export function forget(prefix = "") {
  for (const k of [...buffers.keys()]) if (k.startsWith(prefix)) buffers.delete(k);
  resolved.clear();
}

export function gain(volume, distance, attenuation, positional = true) {
  const v = Math.min(1, Math.max(0, volume));
  if (!positional) return v;
  const range = Math.max(1, volume) * (attenuation || 16);
  return distance >= range ? 0 : v * (1 - distance / range);
}

export function clampPitch(p) {
  return Math.min(2, Math.max(0.5, p));
}

export function stopAll() {
  for (const s of [...playing]) s.stop();
}

export function playBuffer(buf, { volume = 1, pitch = 1, distance = 0, attenuation = 16, positional = true, when = 0, loop = false, offset = 0, duration } = {}) {
  if (ctx.state === "suspended") ctx.resume();
  const src = ctx.createBufferSource();
  src.buffer = buf;
  src.playbackRate.value = clampPitch(pitch);
  src.loop = loop;
  const g = ctx.createGain();
  g.gain.value = gain(volume, distance, attenuation, positional);
  src.connect(g).connect(master);
  const handle = {
    src,
    gainNode: g,
    stop() {
      try {
        src.stop();
      } catch {}
      playing.delete(handle);
    },
  };
  src.onended = () => playing.delete(handle);
  playing.add(handle);
  src.start(ctx.currentTime + when, offset, duration);
  return handle;
}

export async function leaves(id) {
  if (!resolved.has(id)) {
    resolved.set(
      id,
      fetch(`/api/resolve/${encodeURIComponent(id)}`).then((r) => r.json()),
    );
  }
  return resolved.get(id);
}

function pick(list) {
  let r = Math.random();
  for (const l of list) {
    r -= l.p;
    if (r <= 0) return l;
  }
  return list[list.length - 1];
}

export async function playEvent(id, { volume = 1, pitch = 1, distance = 0, positional = true, when = 0, loop = false } = {}) {
  const list = await leaves(id);
  if (!list.length) return null;
  const leaf = pick(list);
  const buf = await buffer(leaf.url);
  const handle = playBuffer(buf, {
    volume: volume * leaf.volume,
    pitch: pitch * leaf.pitch,
    distance,
    attenuation: leaf.attenuation,
    positional,
    when,
    loop,
  });
  handle.leaf = leaf;
  return handle;
}

function bursts(x, pattern, rate) {
  const out = new Float32Array(Math.floor((Math.max(...pattern.map(([t, d]) => t + d)) + 0.01) * rate));
  let pos = 0;
  for (const [t, d] of pattern) {
    const n = Math.floor(d * rate);
    if (pos + n > x.length) pos = 0;
    const seg = x.slice(pos, pos + n);
    pos += n;
    const a = Math.min(seg.length >> 1, Math.floor(0.003 * rate));
    const z = Math.min(seg.length >> 1, Math.floor(Math.min(0.04, d / 3) * rate));
    for (let i = 0; i < a; i++) seg[i] *= i / a;
    for (let i = 0; i < z; i++) seg[seg.length - 1 - i] *= (i / z) ** 2;
    const at = Math.floor(t * rate);
    for (let i = 0; i < seg.length && at + i < out.length; i++) out[at + i] += seg[i];
  }
  return out;
}

export function previewBuffer(source, v, target) {
  const sr = source.sampleRate;
  const tempo = v.tempo || 1;
  const pitch = v.pitch || 1;
  const data = source.getChannelData(0);
  const mono = new Float32Array(data.length);
  for (let c = 0; c < source.numberOfChannels; c++) {
    const d = source.getChannelData(c);
    for (let i = 0; i < d.length; i++) mono[i] += d[i] / source.numberOfChannels;
  }
  const scale = pitch;
  const a = Math.max(0, Math.floor((v.start || 0) / tempo * scale * sr));
  const b = v.end == null ? mono.length : Math.min(mono.length, Math.floor((v.end / tempo) * scale * sr));
  let x = mono.slice(a, Math.max(a + 1, b));
  let mean = 0;
  for (const s of x) mean += s;
  mean /= x.length;
  for (let i = 0; i < x.length; i++) x[i] -= mean;
  if (v.bursts?.length) x = bursts(x, v.bursts, sr * scale);
  if (!v.loop) {
    const nIn = Math.max(1, Math.floor((v.fade_in || 0.004) * sr * scale));
    const nOut = Math.min(Math.floor(x.length / 2), Math.max(1, Math.floor((v.fade_out ?? 0.25) * sr * scale)));
    for (let i = 0; i < nIn && i < x.length; i++) x[i] *= i / nIn;
    for (let i = 0; i < nOut; i++) x[x.length - 1 - i] *= (i / nOut) ** 2;
  }
  let peak = 0;
  for (const s of x) peak = Math.max(peak, Math.abs(s));
  let g;
  if (target != null) {
    const win = Math.round(0.1 * sr * scale);
    const hop = Math.max(1, Math.round(0.01 * sr * scale));
    let rmsMax = 0;
    for (let s = 0; s + win <= Math.max(x.length, win); s += hop) {
      let acc = 0;
      for (let i = s; i < Math.min(x.length, s + win); i++) acc += x[i] * x[i];
      rmsMax = Math.max(rmsMax, Math.sqrt(acc / win));
    }
    g = 10 ** ((target + (v.gain_db || 0)) / 20) / Math.max(1e-6, rmsMax);
    g = Math.min(g, 10 ** (-1 / 20) / Math.max(1e-6, peak));
  } else {
    g = 10 ** (((v.peak ?? -3) + (v.gain_db || 0)) / 20) / Math.max(1e-6, peak);
  }
  for (let i = 0; i < x.length; i++) x[i] *= g;
  const out = ctx.createBuffer(1, x.length, sr);
  out.getChannelData(0).set(x);
  return out;
}

export function playPreview(buf, v, opts = {}) {
  const handle = playBuffer(buf, { ...opts, pitch: (opts.pitch || 1) * (v.pitch || 1), loop: !!v.loop });
  if (v.lp) {
    handle.src.disconnect();
    let node = handle.src;
    for (let k = 0; k < 2; k++) {
      const f = ctx.createBiquadFilter();
      f.type = "lowpass";
      f.frequency.value = v.lp;
      f.Q.value = 0.5;
      node.connect(f);
      node = f;
    }
    node.connect(handle.gainNode);
  }
  return handle;
}
