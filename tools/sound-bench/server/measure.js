import { spawn } from "node:child_process";
import fs from "node:fs";
import path from "node:path";
import crypto from "node:crypto";

const SR = 44100;

function run(cmd, args, input) {
  return new Promise((resolve, reject) => {
    const p = spawn(cmd, args, { windowsHide: true });
    const out = [];
    const err = [];
    p.stdout.on("data", (d) => out.push(d));
    p.stderr.on("data", (d) => err.push(d));
    p.on("error", reject);
    p.on("close", (code) => resolve({ code, stdout: Buffer.concat(out), stderr: Buffer.concat(err).toString() }));
    if (input) p.stdin.end(input);
  });
}

export async function decode(ffmpeg, file) {
  const { stdout, code, stderr } = await run(ffmpeg, ["-v", "error", "-i", file, "-ac", "1", "-ar", String(SR), "-f", "f32le", "-"]);
  if (code !== 0) throw new Error(stderr || `ffmpeg failed on ${file}`);
  return new Float32Array(stdout.buffer, stdout.byteOffset, Math.floor(stdout.length / 4));
}

function db(v) {
  return v > 0 ? 20 * Math.log10(v) : -120;
}

function fft(re, im) {
  const n = re.length;
  for (let i = 1, j = 0; i < n; i++) {
    let bit = n >> 1;
    for (; j & bit; bit >>= 1) j ^= bit;
    j ^= bit;
    if (i < j) {
      [re[i], re[j]] = [re[j], re[i]];
      [im[i], im[j]] = [im[j], im[i]];
    }
  }
  for (let len = 2; len <= n; len <<= 1) {
    const ang = (-2 * Math.PI) / len;
    const wr = Math.cos(ang);
    const wi = Math.sin(ang);
    for (let i = 0; i < n; i += len) {
      let cr = 1;
      let ci = 0;
      for (let j = 0; j < len / 2; j++) {
        const ar = re[i + j + len / 2] * cr - im[i + j + len / 2] * ci;
        const ai = re[i + j + len / 2] * ci + im[i + j + len / 2] * cr;
        re[i + j + len / 2] = re[i + j] - ar;
        im[i + j + len / 2] = im[i + j] - ai;
        re[i + j] += ar;
        im[i + j] += ai;
        const t = cr * wr - ci * wi;
        ci = cr * wi + ci * wr;
        cr = t;
      }
    }
  }
}

export function analyse(x) {
  let peak = 0;
  for (let i = 0; i < x.length; i++) peak = Math.max(peak, Math.abs(x[i]));
  const win = Math.round(0.1 * SR);
  const hop = Math.round(0.01 * SR);
  let rmsMax = 0;
  for (let s = 0; s + win <= Math.max(x.length, win); s += hop) {
    let acc = 0;
    const end = Math.min(x.length, s + win);
    for (let i = s; i < end; i++) acc += x[i] * x[i];
    rmsMax = Math.max(rmsMax, Math.sqrt(acc / win));
  }
  let attack = null;
  const threshold = peak * 0.1;
  for (let i = 0; i < x.length; i++) {
    if (Math.abs(x[i]) >= threshold) {
      attack = i / SR;
      break;
    }
  }
  let onset30 = 0;
  const t30 = peak * 10 ** (-30 / 20);
  for (let i = 0; i < x.length; i++) {
    if (Math.abs(x[i]) >= t30) {
      onset30 = i / SR;
      break;
    }
  }
  const N = 2048;
  let num = 0;
  let den = 0;
  for (let s = 0; s + N <= x.length; s += N) {
    const re = new Float64Array(N);
    const im = new Float64Array(N);
    for (let i = 0; i < N; i++) re[i] = x[s + i] * (0.5 - 0.5 * Math.cos((2 * Math.PI * i) / (N - 1)));
    fft(re, im);
    for (let k = 1; k < N / 2; k++) {
      const m = Math.hypot(re[k], im[k]);
      num += m * ((k * SR) / N);
      den += m;
    }
  }
  const seam = x.length > 1 ? Math.abs(x[x.length - 1] - x[0]) : 0;
  return {
    duration: x.length / SR,
    peak_db: db(peak),
    rms_max_db: db(rmsMax),
    attack_ms: attack === null ? null : attack * 1000,
    onset30_s: onset30,
    centroid_hz: den > 0 ? num / den : null,
    loop_seam: seam,
  };
}

function biquad(x, b0, b1, b2, a1, a2) {
  const y = new Float64Array(x.length);
  let x1 = 0, x2 = 0, y1 = 0, y2 = 0;
  for (let i = 0; i < x.length; i++) {
    const v = b0 * x[i] + b1 * x1 + b2 * x2 - a1 * y1 - a2 * y2;
    x2 = x1;
    x1 = x[i];
    y2 = y1;
    y1 = v;
    y[i] = v;
  }
  return y;
}

function kWeight(x) {
  const f0 = 1681.974450955533, G = 3.999843853973347, Q = 0.7071752369554196;
  const K = Math.tan((Math.PI * f0) / SR), Vh = 10 ** (G / 20), Vb = Vh ** 0.4996667741545416;
  const a0 = 1 + K / Q + K * K;
  const s1 = biquad(x, (Vh + (Vb * K) / Q + K * K) / a0, (2 * (K * K - Vh)) / a0, (Vh - (Vb * K) / Q + K * K) / a0, (2 * (K * K - 1)) / a0, (1 - K / Q + K * K) / a0);
  const f1 = 38.13547087602444, Q1 = 0.5003270373238773, K1 = Math.tan((Math.PI * f1) / SR);
  const d = 1 + K1 / Q1 + K1 * K1;
  return biquad(s1, 1, -2, 1, (2 * (K1 * K1 - 1)) / d, (1 - K1 / Q1 + K1 * K1) / d);
}

export function integratedLufs(x) {
  const y = kWeight(x);
  const block = Math.round(0.4 * SR);
  const hop = Math.round(0.1 * SR);
  const powers = [];
  for (let s = 0; s + block <= y.length; s += hop) {
    let acc = 0;
    for (let i = s; i < s + block; i++) acc += y[i] * y[i];
    powers.push(acc / block);
  }
  const lk = (p) => -0.691 + 10 * Math.log10(p);
  const abs = powers.filter((p) => lk(p) > -70);
  if (!abs.length) return null;
  const rel = lk(abs.reduce((a, b) => a + b, 0) / abs.length) - 10;
  const gated = abs.filter((p) => lk(p) > rel);
  return gated.length ? lk(gated.reduce((a, b) => a + b, 0) / gated.length) : null;
}

export function truePeakDb(x) {
  const taps = 12;
  let peak = 0;
  for (let i = 0; i < x.length; i++) {
    peak = Math.max(peak, Math.abs(x[i]));
    for (let k = 1; k < 4; k++) {
      const frac = k / 4;
      let acc = 0;
      for (let j = -taps + 1; j <= taps; j++) {
        const n = i + j;
        if (n < 0 || n >= x.length) continue;
        const t = j - frac;
        const w = 0.5 + 0.5 * Math.cos((Math.PI * t) / taps);
        acc += x[n] * (Math.sin(Math.PI * t) / (Math.PI * t)) * w;
      }
      peak = Math.max(peak, Math.abs(acc));
    }
  }
  return db(peak);
}

export async function streamInfo(ffmpeg, file) {
  const { stderr } = await run(ffmpeg, ["-i", file]);
  const m = stderr.match(/Audio: [^,]+, (\d+) Hz, ([^,\n]+)/);
  return { sample_rate: m ? parseInt(m[1], 10) : null, channels: m ? m[2].trim() : null };
}

export function makeMeasurer(ffmpeg, cacheDir) {
  fs.mkdirSync(cacheDir, { recursive: true });
  return async function measure(file) {
    const st = fs.statSync(file);
    const key = crypto.createHash("sha1").update(`${path.resolve(file)}|${st.mtimeMs}|${st.size}`).digest("hex");
    const cached = path.join(cacheDir, key + ".json");
    if (fs.existsSync(cached)) return JSON.parse(fs.readFileSync(cached, "utf8"));
    const x = await decode(ffmpeg, file);
    const result = { ...analyse(x), lufs: integratedLufs(x), true_peak_db: truePeakDb(x), ...(await streamInfo(ffmpeg, file)) };
    fs.writeFileSync(cached, JSON.stringify(result));
    return result;
  };
}
