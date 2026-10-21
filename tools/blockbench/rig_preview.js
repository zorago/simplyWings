window.swDeg = 180 / Math.PI;
window.swRest = window.swRest || {};
window.swSnapshot = function () {
  window.swRest = {};
  for (const g of Group.all) window.swRest[g.name] = g.rotation.slice();
  return Object.keys(window.swRest).length;
};
window.swReset = function () {
  for (const g of Group.all) if (window.swRest[g.name]) g.rotation = window.swRest[g.name].slice();
  Canvas.updateAll();
  return true;
};
window.swRot = function (name, xRot, yRot, zRot) {
  const b = Group.all.find(x => x.name === name);
  if (!b || !window.swRest[name]) return false;
  const r0 = window.swRest[name];
  const D = window.swDeg;
  b.rotation = [r0[0] - xRot * D, r0[1] - yRot * D, r0[2] + zRot * D];
  return true;
};
window.swFeather = function (prefix, s, fold, p) {
  const d = Object.assign({fold: 0, stroke: 0, sweep: 0, twist: 0, elbow: 0, wrist: 0, bendElbow: 0, bendWrist: 0, bendTips: 0, fan: 1, handTwist: 0}, p);
  const [F1, F2, F3] = fold;
  const fanEff = d.fan * (1 - d.fold);
  const set = (n, x, y, z) => window.swRot(prefix + n, x, y, z);
  set('shoulder', d.twist, s * d.stroke, s * d.sweep);
  set('arm', 0, 0, s * -F1 * d.fold);
  set('tertials', 0, 0, s * F1 * d.fold * 0.85);
  set('forearm', 0, s * d.bendElbow, s * (F2 * d.fold - d.elbow));
  set('secondaries', 0, 0, s * (-(F2 - F1) * d.fold * 0.95 + 0.4 * d.elbow));
  set('hand', d.handTwist, s * d.bendWrist, s * (-F3 * d.fold - d.wrist));
  set('hand_feathers', 0, 0, s * (F1 - F2 + F3) * 0.9 * d.fold);
  set('hand_coverts', 0, 0, s * ((F2 - F1 - F3) * -0.6 * d.fold));
  for (let k = 1; k <= 16; k++) {
    const name = prefix + 'primary_' + k;
    const b = Group.all.find(x => x.name === name);
    if (!b) break;
    const r0 = window.swRest[name];
    b.rotation = [r0[0], r0[1] - s * d.bendTips * Math.max(0.4, 1.2 - 0.1 * k) * window.swDeg, r0[2] * fanEff];
  }
  return true;
};
window.swPair = function (fold, p, prefixes) {
  window.swReset();
  for (const [prefix, s] of (prefixes || [['right_', 1], ['left_', -1]])) window.swFeather(prefix, s, fold, p);
  Canvas.updateAll();
  return true;
};
