import * as THREE from "three";

export const ELEMENTS = ["fire", "dark", "earth", "water", "light", "air"];
export const TIERS = ["b", "a", "s", "ss", "sss", "mythic"];
export const TIER_COLORS = [0xdde4ea, 0x5aa9ff, 0xb46cff, 0xffc23a, 0xff4a5e, 0xf4f4f4];

const CORNERS = (x0, y0, z0, x1, y1, z1) => [
  [[x1, y1, z0], [x0, y1, z0], [x0, y0, z0], [x1, y0, z0]],
  [[x1, y1, z1], [x1, y1, z0], [x1, y0, z0], [x1, y0, z1]],
  [[x0, y1, z1], [x1, y1, z1], [x1, y0, z1], [x0, y0, z1]],
  [[x0, y1, z0], [x0, y1, z1], [x0, y0, z1], [x0, y0, z0]],
  [[x0, y1, z0], [x1, y1, z0], [x1, y1, z1], [x0, y1, z1]],
  [[x0, y0, z1], [x1, y0, z1], [x1, y0, z0], [x0, y0, z0]],
];
const NORMALS = [[0, 0, -1], [1, 0, 0], [0, 0, 1], [-1, 0, 0], [0, 1, 0], [0, -1, 0]];
const RAD = Math.PI / 180;

function cubeFaces(c, textures, out) {
  const [x0, y0, z0] = c.f;
  const [x1, y1, z1] = c.t;
  const o = c.o;
  const r = c.r;
  const m = new THREE.Matrix4().makeTranslation(o[0], o[1], o[2]);
  m.multiply(new THREE.Matrix4().makeRotationZ(r[2] * RAD));
  m.multiply(new THREE.Matrix4().makeRotationY(r[1] * RAD));
  m.multiply(new THREE.Matrix4().makeRotationX(r[0] * RAD));
  m.multiply(new THREE.Matrix4().makeTranslation(-o[0], -o[1], -o[2]));
  const nm = new THREE.Matrix3().getNormalMatrix(m);
  const corners = CORNERS(x0, y0, z0, x1, y1, z1);
  for (let i = 0; i < 6; i++) {
    const fa = c.faces[i];
    if (!fa) continue;
    const tex = fa[4];
    const tx = textures[tex];
    const u0 = fa[0] / tx.uw;
    const v0 = fa[1] / tx.uh;
    const u1 = fa[2] / tx.uw;
    const v1 = fa[3] / tx.uh;
    let uv = [u0, v0, u1, v0, u1, v1, u0, v1];
    const steps = ((Math.trunc(fa[5] / 90) % 4) + 4) % 4;
    for (let s = 0; s < steps; s++) uv = [uv[6], uv[7], uv[0], uv[1], uv[2], uv[3], uv[4], uv[5]];
    const pos = [];
    const v = new THREE.Vector3();
    for (let k = 0; k < 4; k++) {
      v.set(...corners[i][k]).applyMatrix4(m);
      pos.push(v.x, v.y, v.z);
    }
    const n = new THREE.Vector3(...NORMALS[i]).applyMatrix3(nm).normalize();
    out.push({ pos, uv, n: [n.x, n.y, n.z], tex });
  }
}

export function parseShow(json) {
  const textures = json.textures;
  const bones = json.bones.map((b) => {
    const faces = [];
    for (const c of b.cubes) cubeFaces(c, textures, faces);
    return { name: b.name, parent: b.parent, px: b.pivot[0], py: b.pivot[1], pz: b.pivot[2], rx: b.rot[0], ry: b.rot[1], rz: b.rot[2], lit: b.lit, faces };
  });
  const boneIndex = new Map(bones.map((b, i) => [b.name, i]));
  const root = new Int32Array(bones.length);
  for (let i = 0; i < bones.length; i++) root[i] = bones[i].parent < 0 ? i : root[bones[i].parent];
  const anims = {};
  for (const [name, a] of Object.entries(json.anims)) {
    const tracks = bones.map(() => [null, null, null]);
    for (const t of a.tracks) tracks[t.bone][t.ch] = Float32Array.from(t.keys);
    anims[name] = { name, length: a.length, loop: a.loop, meta: a.meta, tracks };
  }
  return { textures, bones, boneIndex, root, anims, bone: (n) => boneIndex.get(n) ?? -1 };
}

export function meta(anim, key, fallback) {
  const v = anim.meta[key];
  if (v === undefined || v === null) return fallback;
  if (typeof v === "number") return v;
  if (Array.isArray(v) && v.length === 1 && typeof v[0] === "number") return v[0];
  const n = Number(v);
  return Number.isFinite(n) && v !== "" ? n : fallback;
}

function copy(k, i, out) {
  out[0] = k[i * 5 + 1];
  out[1] = k[i * 5 + 2];
  out[2] = k[i * 5 + 3];
}

export function sample(anim, bone, ch, time, out, fallback) {
  const k = anim.tracks[bone][ch];
  if (!k) {
    out[0] = out[1] = out[2] = fallback;
    return;
  }
  const n = k.length / 5;
  if (time <= k[0] || n === 1) return copy(k, 0, out);
  if (time >= k[(n - 1) * 5]) return copy(k, n - 1, out);
  let lo = 0;
  let hi = n - 1;
  while (hi - lo > 1) {
    const mid = (lo + hi) >>> 1;
    if (k[mid * 5] <= time) lo = mid;
    else hi = mid;
  }
  const i = lo;
  const t0 = k[i * 5];
  const t1 = k[(i + 1) * 5];
  const u = (time - t0) / Math.max(1e-6, t1 - t0);
  const ia = k[i * 5 + 4] | 0;
  const ib = k[(i + 1) * 5 + 4] | 0;
  if (ia === 2) return copy(k, i, out);
  if (ia === 1 || ib === 1) {
    const a = Math.max(0, i - 1);
    const d = Math.min(n - 1, i + 2);
    const ta = k[a * 5];
    const td = k[d * 5];
    const h = t1 - t0;
    const u2 = u * u;
    const u3 = u2 * u;
    const h00 = 2 * u3 - 3 * u2 + 1;
    const h10 = u3 - 2 * u2 + u;
    const h01 = -2 * u3 + 3 * u2;
    const h11 = u3 - u2;
    for (let c = 0; c < 3; c++) {
      const p0 = k[a * 5 + 1 + c];
      const p1 = k[i * 5 + 1 + c];
      const p2 = k[(i + 1) * 5 + 1 + c];
      const p3 = k[d * 5 + 1 + c];
      const m1 = (p2 - p0) / Math.max(1e-4, t1 - ta);
      const m2 = (p3 - p1) / Math.max(1e-4, td - t0);
      out[c] = h00 * p1 + h10 * h * m1 + h01 * p2 + h11 * h * m2;
    }
    return;
  }
  for (let c = 0; c < 3; c++) out[c] = k[i * 5 + 1 + c] + (k[(i + 1) * 5 + 1 + c] - k[i * 5 + 1 + c]) * u;
}

const POS = [0, 0, 0];
const ROT = [0, 0, 0];
const SCL = [1, 1, 1];
const E = new THREE.Euler(0, 0, 0, "ZYX");
const Q = new THREE.Quaternion();
const VP = new THREE.Vector3();
const VS = new THREE.Vector3();
const TB = new THREE.Matrix4();

function zero() {
  return Math.abs(SCL[0]) < 1e-4 || Math.abs(SCL[1]) < 1e-4 || Math.abs(SCL[2]) < 1e-4;
}

export function local(b, anim, i, time, out) {
  sample(anim, i, 0, time, POS, 0);
  sample(anim, i, 1, time, ROT, 0);
  sample(anim, i, 2, time, SCL, 1);
  E.set((b.rx - ROT[0]) * RAD, (b.ry - ROT[1]) * RAD, (b.rz + ROT[2]) * RAD, "ZYX");
  Q.setFromEuler(E);
  VP.set(b.px - POS[0], b.py + POS[1], b.pz + POS[2]);
  VS.set(SCL[0], SCL[1], SCL[2]);
  out.compose(VP, Q, VS);
  out.multiply(TB.makeTranslation(-b.px, -b.py, -b.pz));
  return out;
}

export function solve(model, anim, time, world, hidden, only) {
  const L = new THREE.Matrix4();
  for (let i = 0; i < model.bones.length; i++) {
    if (only && !only(model.root[i])) continue;
    const b = model.bones[i];
    if (b.parent >= 0 && hidden[b.parent]) {
      hidden[i] = true;
      continue;
    }
    local(b, anim, i, time, L);
    hidden[i] = zero();
    if (b.parent >= 0) world[i].multiplyMatrices(world[b.parent], L);
    else world[i].copy(L);
  }
}

export function position(model, anim, bone, time, out = new THREE.Vector3()) {
  const m = new THREE.Matrix4();
  const l = new THREE.Matrix4();
  const chain = [];
  for (let i = bone; i >= 0 && chain.length < 64; i = model.bones[i].parent) chain.push(i);
  for (let d = chain.length - 1; d >= 0; d--) {
    const i = chain[d];
    local(model.bones[i], anim, i, time, l);
    m.multiply(l);
  }
  const b = model.bones[bone];
  return out.set(b.px, b.py, b.pz).applyMatrix4(m);
}

export function visible(model, anim, bone, time) {
  for (let i = bone; i >= 0; i = model.bones[i].parent) {
    sample(anim, i, 2, time, SCL, 1);
    if (zero()) return false;
  }
  return true;
}

export function tintFor(model, tiers) {
  const tint = new Uint32Array(model.bones.length).fill(0xffffff);
  for (let i = 0; i < tiers.length && i < 10; i++) {
    const r = model.bone("multi_star_" + i);
    if (r < 0) continue;
    const color = TIER_COLORS[Math.max(0, Math.min(5, tiers[i]))];
    for (let b = 0; b < model.bones.length; b++) {
      for (let q = b; q >= 0; q = model.bones[q].parent) {
        if (q === r) {
          tint[b] = color;
          break;
        }
      }
    }
  }
  return tint;
}

export function createScene(model, canvasHost) {
  const renderer = new THREE.WebGLRenderer({ antialias: true });
  renderer.outputColorSpace = THREE.SRGBColorSpace;
  renderer.setPixelRatio(Math.min(2, window.devicePixelRatio || 1));
  canvasHost.appendChild(renderer.domElement);
  const scene = new THREE.Scene();
  scene.background = new THREE.Color(0x0b0d16);
  const camera = new THREE.PerspectiveCamera(70, 1, 0.05, 3000);
  scene.add(new THREE.AmbientLight(0xffffff, 1.6));
  const sun = new THREE.DirectionalLight(0xffffff, 1.4);
  sun.position.set(0.2, 1, -0.7);
  scene.add(sun);
  const back = new THREE.DirectionalLight(0xffffff, 0.6);
  back.position.set(-0.2, 1, 0.7);
  scene.add(back);
  const grid = new THREE.GridHelper(64, 64, 0x2a3040, 0x1a1f2b);
  grid.position.y = 2;
  scene.add(grid);
  const root = new THREE.Group();
  root.scale.setScalar(1 / 16);
  scene.add(root);

  const loader = new THREE.TextureLoader();
  const maps = model.textures.map((t) => {
    const [, p] = t.path.split(":");
    const tex = loader.load(`/api/assets/${p}`);
    tex.flipY = false;
    tex.magFilter = THREE.NearestFilter;
    tex.minFilter = THREE.NearestFilter;
    tex.generateMipmaps = false;
    tex.colorSpace = THREE.SRGBColorSpace;
    if (t.frames > 1) tex.repeat.set(1, 1 / t.frames);
    return tex;
  });
  const materials = new Map();
  const material = (tex, lit, color) => {
    const key = `${tex}/${lit ? 1 : 0}/${color}`;
    if (!materials.has(key)) {
      const opts = { map: maps[tex], color, transparent: true, alphaTest: 0.01, side: THREE.DoubleSide };
      materials.set(key, lit ? new THREE.MeshLambertMaterial(opts) : new THREE.MeshBasicMaterial(opts));
    }
    return materials.get(key);
  };

  const meshes = model.bones.map((b) => {
    if (!b.faces.length) return [];
    const byTex = new Map();
    for (const f of b.faces) {
      if (!byTex.has(f.tex)) byTex.set(f.tex, []);
      byTex.get(f.tex).push(f);
    }
    return [...byTex].map(([tex, faces]) => {
      const pos = new Float32Array(faces.length * 12);
      const nor = new Float32Array(faces.length * 12);
      const uv = new Float32Array(faces.length * 8);
      const idx = [];
      faces.forEach((f, k) => {
        pos.set(f.pos, k * 12);
        for (let v = 0; v < 4; v++) nor.set(f.n, k * 12 + v * 3);
        uv.set(f.uv, k * 8);
        const o = k * 4;
        idx.push(o, o + 1, o + 2, o, o + 2, o + 3);
      });
      const g = new THREE.BufferGeometry();
      g.setAttribute("position", new THREE.BufferAttribute(pos, 3));
      g.setAttribute("normal", new THREE.BufferAttribute(nor, 3));
      g.setAttribute("uv", new THREE.BufferAttribute(uv, 2));
      g.setIndex(idx);
      g.computeBoundingSphere();
      const mesh = new THREE.Mesh(g, material(tex, b.lit, 0xffffff));
      mesh.matrixAutoUpdate = false;
      mesh.userData.tex = tex;
      root.add(mesh);
      return mesh;
    });
  });

  const n = model.bones.length;
  const worldA = Array.from({ length: n }, () => new THREE.Matrix4());
  const hiddenA = new Uint8Array(n);
  const flameRoots = new Set(ELEMENTS.map((e) => model.bone("flame_" + e)).filter((b) => b >= 0));

  function apply(i, tint) {
    const list = meshes[i];
    if (!list.length) return;
    const show = !hiddenA[i];
    for (const m of list) {
      m.visible = show;
      if (!show) continue;
      m.matrix.copy(worldA[i]);
      m.matrixWorldNeedsUpdate = true;
      const want = material(m.userData.tex, model.bones[i].lit, tint ? tint[i] : 0xffffff);
      if (m.material !== want) m.material = want;
    }
  }

  return {
    camera,
    renderer,
    resize(w, h) {
      renderer.setSize(w, h, false);
      renderer.domElement.style.width = w + "px";
      renderer.domElement.style.height = h + "px";
      camera.aspect = w / Math.max(1, h);
      camera.updateProjectionMatrix();
    },
    draw(anim, time, clock, tint, frozenIdle) {
      model.textures.forEach((t, k) => {
        if (t.frames > 1) {
          const frame = Math.floor((clock * 20) / Math.max(1, t.frameTime)) % t.frames;
          maps[k].offset.set(0, frame / t.frames);
        }
      });
      if (frozenIdle) {
        solve(model, anim, time, worldA, hiddenA, (r) => !flameRoots.has(r));
        for (let i = 0; i < n; i++) if (!flameRoots.has(model.root[i])) apply(i, tint);
        solve(model, frozenIdle.anim, frozenIdle.time, worldA, hiddenA, (r) => flameRoots.has(r));
        for (let i = 0; i < n; i++) if (flameRoots.has(model.root[i])) apply(i, null);
      } else {
        solve(model, anim, time, worldA, hiddenA);
        for (let i = 0; i < n; i++) apply(i, tint);
      }
      renderer.render(scene, camera);
    },
    dispose() {
      renderer.dispose();
      renderer.domElement.remove();
    },
  };
}
