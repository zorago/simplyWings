window.swb = window.swb || {};
window.swb.root = 'D:\\Bureau\\General\\SimplyWings';
window.swb.load = function (model) {
  const fs = require('fs');
  const dir = window.swb.root + '\\art\\wings\\lib\\';
  const lib = ['rig_pack.js', 'bird.js', 'rig_geom.js', 'fury.js'].map(f => fs.readFileSync(dir + f, 'utf8')).join('\n');
  const code = lib + '\n' + fs.readFileSync(window.swb.root + '\\art\\wings\\' + model + '.rig.js', 'utf8');
  window.swb.rig = new Function(code + '\nreturn RIG;')();
  return window.swb.rig;
};
window.swb.side = function (name, side) {
  return side === 'left' ? 'left_' + name : 'right_' + name;
};
window.swb.start = function (model) {
  const rig = window.swb.load(model);
  newProject(Formats.modded_entity);
  Project.name = model;
  Project.box_uv = true;
  Project.texture_width = rig.texture[0];
  Project.texture_height = rig.texture[1];
  const ref = new Group({name: 'ref', origin: [0, 0, 0], export: false}).init();
  const parts = [['ref_body', [-4, 12, -2], [4, 24, 2]], ['ref_head', [-4, 24, -4], [4, 32, 4]],
    ['ref_legs', [-4, 0, -2], [4, 12, 2]], ['ref_arm_r', [4, 12, -2], [8, 24, 2]], ['ref_arm_l', [-8, 12, -2], [-4, 24, 2]]];
  for (const [n, f, t] of parts) new Cube({name: n, from: f, to: t, export: false, color: 7, box_uv: true, uv_offset: [0, rig.texture[1] - 16]}).init().addTo(ref);
  window.swb.groups = {};
  window.swb.tex = null;
  window.swb.built = [];
  Canvas.updateAll();
  return {groups: rig.groups.length, cubes: rig.cubes.length};
};
window.swb.mirrorVec = function (v) {
  return [-v[0], v[1], v[2]];
};
window.swb.groupFor = function (g, side) {
  const key = (g.single ? '' : side + ':') + g.name;
  if (window.swb.groups[key]) return window.swb.groups[key];
  const mirror = side === 'left' && !g.single;
  const origin = mirror ? window.swb.mirrorVec(g.origin) : g.origin.slice();
  const r = g.rot || [0, 0, 0];
  const rotation = mirror ? [r[0], -r[1], -r[2]] : r.slice();
  const name = g.single ? g.name : window.swb.side(g.name, side);
  const parent = g.parent ? window.swb.groupFor(window.swb.rig.groups.find(x => x.name === g.parent), side) : null;
  const grp = new Group({name, origin, rotation}).init();
  if (parent) grp.addTo(parent);
  window.swb.groups[key] = grp;
  return grp;
};
window.swb.cube = function (c, side) {
  const rig = window.swb.rig;
  const g = rig.groups.find(x => x.name === c.group);
  const mirror = side === 'left' && !g.single;
  const grp = window.swb.groupFor(g, side);
  let from = c.from.slice();
  let to = c.to.slice();
  if (mirror) {
    from = [-c.to[0], c.from[1], c.from[2]];
    to = [-c.from[0], c.to[1], c.to[2]];
  }
  const r = c.rot || [0, 0, 0];
  const o = c.origin || g.origin;
  const cube = new Cube({
    name: g.single ? c.name : window.swb.side(c.name, side), from, to, box_uv: true,
    uv_offset: (mirror && c.leftUv ? c.leftUv : c.uv).slice(), mirror_uv: mirror, inflate: c.inflate || 0,
    origin: mirror ? window.swb.mirrorVec(o) : o.slice(), rotation: mirror ? [r[0], -r[1], -r[2]] : r.slice(),
    color: c.color || 0
  }).init().addTo(grp);
  if (window.swb.tex) cube.applyTexture(window.swb.tex, true);
  window.swb.built.push(cube);
  return cube;
};
window.swb.step = function (groupNames, sides) {
  const rig = window.swb.rig;
  const names = Array.isArray(groupNames) ? groupNames : [groupNames];
  Undo.initEdit({outliner: true, elements: [], selection: true});
  const made = [];
  for (const side of sides || ['right']) {
    for (const n of names) {
      const g = rig.groups.find(x => x.name === n);
      if (g.single && side === 'left') continue;
      window.swb.groupFor(g, side);
      for (const c of rig.cubes.filter(x => x.group === n && (!x.only || x.only === side))) made.push(window.swb.cube(c, side));
    }
  }
  Undo.finishEdit('build ' + names.join(','), {outliner: true, elements: made, selection: true});
  Canvas.updateAll();
  return made.length;
};
window.swb.mirrorAll = function () {
  const rig = window.swb.rig;
  return window.swb.step(rig.groups.filter(g => !g.single).map(g => g.name), ['left']);
};
window.swb.texture = function () {
  const rig = window.swb.rig;
  for (const t of Texture.all.slice()) t.remove(true);
  const path = window.swb.root + '\\src\\main\\resources\\assets\\simplywings\\textures\\entity\\' + rig.model + '_wings.png';
  const tex = new Texture({name: rig.model + '_wings.png'}).fromPath(path).add(false);
  tex.uv_width = rig.texture[0];
  tex.uv_height = rig.texture[1];
  window.swb.tex = tex;
  for (const c of Cube.all) {
    c.applyTexture(tex, true);
  }
  Canvas.updateAll();
  return tex.name;
};
window.swb.reloadTexture = function () {
  for (const t of Texture.all) t.reloadTexture();
  Canvas.updateAll();
  return Texture.all.length;
};
window.swb.save = function () {
  const rig = window.swb.rig;
  const fs = require('fs');
  const path = window.swb.root + '\\src\\main\\resources\\assets\\simplywings\\rigs\\' + rig.model + '.bbmodel';
  fs.writeFileSync(path, Codecs.project.compile({raw: false}));
  Project.save_path = path;
  Project.saved = true;
  return path;
};
window.swb.rest = function () {
  window.swb.restRot = {};
  for (const g of Group.all) window.swb.restRot[g.name] = g.rotation.slice();
  return Object.keys(window.swb.restRot).length;
};
window.swb.pose = function (deg) {
  const D = window.swb.restRot;
  for (const g of Group.all) if (D[g.name]) g.rotation = D[g.name].slice();
  for (const key in deg) {
    for (const [prefix, s] of [['right_', 1], ['left_', -1]]) {
      const g = Group.all.find(x => x.name === prefix + key);
      if (!g) continue;
      const r = deg[key];
      g.rotation = [D[g.name][0] + r[0], D[g.name][1] + s * r[1], D[g.name][2] + s * r[2]];
    }
  }
  Canvas.updateAll();
  return true;
};
window.swb.fresh = function (model) {
  const previous = window.swb.rig ? window.swb.rig.model : null;
  for (const p of ModelProject.all.filter(p => p.name === model || p.name === previous)) {
    p.saved = true;
    p.close(true);
  }
  const r = window.swb.start(model);
  window.swb.texture();
  return r;
};
window.swb.all = function () {
  return window.swb.rig.groups.filter(g => !g.single).map(g => g.name);
};
