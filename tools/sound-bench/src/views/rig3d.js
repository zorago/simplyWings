import * as THREE from "three";

const ROOT_Y = 24;
const DEPTH_STEP = 0.012;
const DEPTH_LAYERS = 5;
const DEG = Math.PI / 180;
const MEMBRANE_LAYER = 0.04;
const BILLOW_SPAN = 0.12;

export function part(name) {
  const o = new THREE.Object3D();
  o.name = name;
  o.rotation.order = "ZYX";
  return o;
}

export function setPose(o, a) {
  o.rotation.set(a[0], a[1], a[2]);
  o.position.set(a[3], a[4], a[5]);
  const hidden = a[6] === 0 && a[7] === 0 && a[8] === 0;
  o.visible = !hidden;
  if (hidden) o.scale.set(1, 1, 1);
  else o.scale.set(a[6], a[7], a[8]);
}

function cubeFaces(buf, u, v, x0, y0, z0, dx, dy, dz, grow, mirror, tw, th) {
  let x1 = x0 + dx;
  const y1 = y0 + dy + grow;
  const z1 = z0 + dz + grow;
  x0 -= grow;
  y0 -= grow;
  z0 -= grow;
  x1 += grow;
  if (mirror) [x0, x1] = [x1, x0];
  const p7 = [x0, y0, z0];
  const p0 = [x1, y0, z0];
  const p1 = [x1, y1, z0];
  const p2 = [x0, y1, z0];
  const p3 = [x0, y0, z1];
  const p4 = [x1, y0, z1];
  const p5 = [x1, y1, z1];
  const p6 = [x0, y1, z1];
  const f4 = u;
  const f5 = u + dz;
  const f6 = u + dz + dx;
  const f7 = u + dz + dx + dx;
  const f8 = u + dz + dx + dz;
  const f9 = u + dz + dx + dz + dx;
  const f10 = v;
  const f11 = v + dz;
  const f12 = v + dz + dy;
  const polys = [
    [[p4, p3, p7, p0], f5, f10, f6, f11],
    [[p1, p2, p6, p5], f6, f11, f7, f10],
    [[p7, p3, p6, p2], f4, f11, f5, f12],
    [[p0, p7, p2, p1], f5, f11, f6, f12],
    [[p4, p0, p1, p5], f6, f11, f8, f12],
    [[p3, p4, p5, p6], f8, f11, f9, f12],
  ];
  for (const [verts, u1, v1, u2, v2] of polys) {
    const uv = [
      [u2 / tw, v1 / th],
      [u1 / tw, v1 / th],
      [u1 / tw, v2 / th],
      [u2 / tw, v2 / th],
    ];
    const order = mirror ? [3, 2, 1, 0] : [0, 1, 2, 3];
    const q = order.map((k) => [verts[k], uv[k]]);
    for (const k of [0, 1, 2, 0, 2, 3]) {
      buf.pos.push(...q[k][0]);
      buf.uv.push(...q[k][1]);
    }
  }
}

function geometry(buf) {
  if (!buf.pos.length) return null;
  const g = new THREE.BufferGeometry();
  g.setAttribute("position", new THREE.Float32BufferAttribute(buf.pos, 3));
  g.setAttribute("uv", new THREE.Float32BufferAttribute(buf.uv, 2));
  g.computeVertexNormals();
  return g;
}

function vec(o, key, n) {
  const a = Array.isArray(o?.[key]) ? o[key] : [];
  return Array.from({ length: n }, (_, i) => Number(a[i]) || 0);
}

function pose(o, origin, parentOrigin, rotation) {
  const p = parentOrigin || [0, 0, 0];
  o.position.set(-(origin[0] - p[0]), -(origin[1] - p[1]) + (parentOrigin ? 0 : ROOT_Y), origin[2] - p[2]);
  o.rotation.set(-rotation[0] * DEG, -rotation[1] * DEG, rotation[2] * DEG);
}

function addBox(buf, e, pivot, tw, th) {
  const sx = e.to[0] - e.from[0];
  const sy = e.to[1] - e.from[1];
  const sz = e.to[2] - e.from[2];
  cubeFaces(buf, e.u, e.v, pivot[0] - e.to[0], -e.from[1] - sy + pivot[1], e.from[2] - pivot[2], sx, sy, sz, e.inflate, e.mirror, tw, th);
}

export function boxMesh(material, x, y, z, dx, dy, dz) {
  const buf = { pos: [], uv: [] };
  cubeFaces(buf, 0, 0, x, y, z, dx, dy, dz, 0, false, 64, 64);
  return new THREE.Mesh(geometry(buf), material);
}

export async function loadTexture(urls) {
  for (const url of urls) {
    if (!url) continue;
    try {
      const img = await new THREE.ImageLoader().loadAsync(url);
      const t = new THREE.Texture(img);
      t.magFilter = THREE.NearestFilter;
      t.minFilter = THREE.NearestFilter;
      t.generateMipmaps = false;
      t.flipY = false;
      t.colorSpace = THREE.SRGBColorSpace;
      t.needsUpdate = true;
      return t;
    } catch {}
  }
  return null;
}

export function buildRig(bb, membranes, material) {
  const tw = bb.resolution.width;
  const th = bb.resolution.height;
  const groupData = new Map((bb.groups || []).map((g) => [g.uuid, g]));
  const elements = new Map();
  let index = 0;
  for (const o of bb.elements || []) {
    if ((o.type ?? "cube") !== "cube") continue;
    const uv = vec(o, "uv_offset", 2);
    elements.set(o.uuid, {
      from: vec(o, "from", 3),
      to: vec(o, "to", 3),
      origin: vec(o, "origin", 3),
      rotation: vec(o, "rotation", 3),
      u: Math.trunc(uv[0]),
      v: Math.trunc(uv[1]),
      inflate: (o.inflate || 0) + DEPTH_STEP * (index++ % DEPTH_LAYERS),
      mirror: !!o.mirror_uv,
      export: o.export !== false,
    });
  }
  const group = (node) => {
    const data = groupData.get(node.uuid) || node;
    return {
      name: data.name,
      origin: vec(data, "origin", 3),
      rotation: vec(data, "rotation", 3),
      export: data.export !== false,
      children: (node.children || []).map((c) => (typeof c === "object" ? group(c) : c)),
    };
  };
  const root = part("root");
  const bones = new Map();
  const build = (parent, g, parentGroup) => {
    if (!g.export) return;
    const buf = { pos: [], uv: [] };
    const rotated = [];
    for (const c of g.children) {
      if (typeof c !== "string") continue;
      const e = elements.get(c);
      if (!e || !e.export) continue;
      if (e.rotation.some((r) => r !== 0)) rotated.push(e);
      else addBox(buf, e, g.origin, tw, th);
    }
    const p = part(g.name);
    pose(p, g.origin, parentGroup?.origin, g.rotation);
    const geo = geometry(buf);
    if (geo) p.add(new THREE.Mesh(geo, material));
    const old = parent.children.find((c) => c.name === g.name);
    if (old) parent.remove(old);
    parent.add(p);
    if (!bones.has(g.name)) bones.set(g.name, p);
    rotated.forEach((e, i) => {
      const b = { pos: [], uv: [] };
      addBox(b, e, e.origin, tw, th);
      const r = part(`${g.name}_r${i}`);
      pose(r, e.origin, g.origin, e.rotation);
      r.add(new THREE.Mesh(geometry(b), material));
      p.add(r);
    });
    for (const c of g.children) if (typeof c === "object") build(p, c, g);
  };
  for (const node of bb.outliner || []) if (node && typeof node === "object") build(root, group(node), null);
  const membrane = membranes ? new Membrane(membranes, root, bones, tw, th, material) : null;
  if (membrane && !membrane.panels.length) return { root, bones, membrane: null, width: tw, height: th };
  if (membrane) root.add(membrane.mesh);
  return { root, bones, membrane, width: tw, height: th };
}

function relative(root, obj, inv) {
  if (obj === root) return new THREE.Matrix4();
  return new THREE.Matrix4().multiplyMatrices(inv, obj.matrixWorld);
}

class Membrane {
  constructor(json, root, bones, tw, th, material) {
    this.root = root;
    this.tw = tw;
    this.th = th;
    this.panels = [];
    root.updateMatrixWorld(true);
    const inv = root.matrixWorld.clone().invert();
    const def = json.sheet;
    for (const panel of json.panels || []) {
      const rows = Math.max(1, panel.rows ?? 1);
      const sheet = panel.sheet || def;
      for (const side of [1, -1]) {
        const anchors = (points) => {
          const out = [];
          for (const p of points) {
            const name = p[0] === "root" ? "root" : (side > 0 ? "right_" : "left_") + p[0];
            const obj = name === "root" ? root : bones.get(name);
            if (!obj) return null;
            const [x, y, z] = [p[1], p[2], p[3]];
            const model = new THREE.Vector3(-side * x, 24 - y, z);
            const local = model.applyMatrix4(relative(root, obj, inv).invert());
            const u = p.length >= 6 ? p[4] : x - sheet.x;
            const v = p.length >= 6 ? p[5] : sheet.y - y;
            out.push({ obj, local, u, v });
          }
          return out;
        };
        const a = anchors(panel.a);
        const b = anchors(panel.b);
        if (a && b && a.length === b.length && a.length > 1) {
          this.panels.push({ a, b, rows, ventral: sheet.ventral, dorsal: sheet.dorsal, frames: sheet.frames ?? 1, fps: sheet.fps ?? 0, step: sheet.step ?? [0, 0] });
        }
      }
    }
    let quads = 0;
    for (const p of this.panels) quads += (p.a.length - 1) * p.rows * 2;
    this.positions = new Float32Array(quads * 6 * 3);
    this.uvs = new Float32Array(quads * 6 * 2);
    const g = new THREE.BufferGeometry();
    g.setAttribute("position", new THREE.BufferAttribute(this.positions, 3));
    g.setAttribute("uv", new THREE.BufferAttribute(this.uvs, 2));
    this.geometry = g;
    this.mesh = new THREE.Mesh(g, material);
    this.mesh.frustumCulled = false;
  }

  update(billow, ticks) {
    const root = this.root;
    root.updateMatrixWorld(true);
    const inv = root.matrixWorld.clone().invert();
    const cache = new Map();
    const mat = (obj) => {
      if (!cache.has(obj)) cache.set(obj, relative(root, obj, inv));
      return cache.get(obj);
    };
    let pi = 0;
    let ui = 0;
    const normal = new THREE.Vector3();
    const e1 = new THREE.Vector3();
    const e2 = new THREE.Vector3();
    for (const panel of this.panels) {
      const n = panel.a.length;
      const rows = panel.rows;
      const pos = [];
      const up = [];
      const u = [];
      const v = [];
      for (let i = 0; i < n; i++) {
        const A = panel.a[i];
        const B = panel.b[i];
        const ma = mat(A.obj);
        const mb = mat(B.obj);
        const pa = A.local.clone().applyMatrix4(ma);
        const pb = B.local.clone().applyMatrix4(mb);
        up[i] = new THREE.Vector3(0, 0, 1).transformDirection(mb);
        const bulge = billow * BILLOW_SPAN * pa.distanceTo(pb);
        pos[i] = [];
        u[i] = [];
        v[i] = [];
        for (let r = 0; r <= rows; r++) {
          const t = r / rows;
          pos[i][r] = pa.clone().lerp(pb, t).addScaledVector(up[i], bulge * Math.sin(Math.PI * t));
          u[i][r] = A.u + (B.u - A.u) * t;
          v[i][r] = A.v + (B.v - A.v) * t;
        }
      }
      const frame = panel.frames > 1 ? (((Math.floor((ticks / 20) * panel.fps)) % panel.frames) + panel.frames) % panel.frames : 0;
      for (let i = 0; i + 1 < n; i++) {
        for (let r = 0; r < rows; r++) {
          const corners = [[i, r], [i + 1, r], [i + 1, r + 1], [i, r + 1]];
          e1.subVectors(pos[i + 1][r + 1], pos[i][r]);
          e2.subVectors(pos[i][r + 1], pos[i + 1][r]);
          normal.crossVectors(e1, e2);
          if (normal.lengthSq() < 1e-12) normal.copy(up[i]);
          normal.normalize();
          if (normal.dot(up[i]) < 0) normal.negate();
          for (let layer = 0; layer < 2; layer++) {
            const side = layer === 0 ? 1 : -1;
            const base = layer === 0 ? panel.dorsal : panel.ventral;
            const off = [base[0] + frame * panel.step[0], base[1] + frame * panel.step[1]];
            const q = [0, 1, 2, 3].map((k) => corners[layer === 0 ? k : 3 - k]);
            for (const k of [0, 1, 2, 0, 2, 3]) {
              const [ci, cr] = q[k];
              const p = pos[ci][cr];
              this.positions[pi++] = p.x + side * MEMBRANE_LAYER * normal.x;
              this.positions[pi++] = p.y + side * MEMBRANE_LAYER * normal.y;
              this.positions[pi++] = p.z + side * MEMBRANE_LAYER * normal.z;
              this.uvs[ui++] = (off[0] + u[ci][cr]) / this.tw;
              this.uvs[ui++] = (off[1] + v[ci][cr]) / this.th;
            }
          }
        }
      }
    }
    this.geometry.attributes.position.needsUpdate = true;
    this.geometry.attributes.uv.needsUpdate = true;
    this.geometry.computeVertexNormals();
    this.geometry.computeBoundingSphere();
  }
}

const STEVE = [
  ["head", [0, 0, 0], [-4, -8, -4, 8, 8, 8], 0xc69c7a],
  ["body", [0, 0, 0], [-4, 0, -2, 8, 12, 4], 0x2fa3a8],
  ["right_arm", [-5, 2, 0], [-3, -2, -2, 4, 12, 4], 0xc69c7a],
  ["left_arm", [5, 2, 0], [-1, -2, -2, 4, 12, 4], 0xc69c7a],
  ["right_leg", [-1.9, 12, 0], [-2, 0, -2, 4, 12, 4], 0x3a3f99],
  ["left_leg", [1.9, 12, 0], [-2, 0, -2, 4, 12, 4], 0x3a3f99],
];

export function buildSteve() {
  const parts = new Map();
  const group = new THREE.Group();
  for (const [name, pivot, box, color] of STEVE) {
    const p = part(name);
    p.position.set(...pivot);
    p.add(boxMesh(new THREE.MeshLambertMaterial({ color, side: THREE.DoubleSide }), ...box));
    group.add(p);
    parts.set(name, p);
  }
  const head = parts.get("head");
  const hair = new THREE.MeshLambertMaterial({ color: 0x3b2a1e, side: THREE.DoubleSide });
  head.add(boxMesh(hair, -4.05, -8.05, -4.05, 8.1, 2.1, 8.1));
  const eye = new THREE.MeshLambertMaterial({ color: 0x4a3aa8, side: THREE.DoubleSide });
  head.add(boxMesh(eye, -3, -4, -4.1, 2, 1, 0.2));
  head.add(boxMesh(eye, 1, -4, -4.1, 2, 1, 0.2));
  const sleeve = new THREE.MeshLambertMaterial({ color: 0x2fa3a8, side: THREE.DoubleSide });
  parts.get("right_arm").add(boxMesh(sleeve, -3.05, -2.05, -2.05, 4.1, 4.1, 4.1));
  parts.get("left_arm").add(boxMesh(sleeve, -1.05, -2.05, -2.05, 4.1, 4.1, 4.1));
  return { group, parts };
}
