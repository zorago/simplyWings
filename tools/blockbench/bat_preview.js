window.swb.batPose = function (d) {
  const D = 180 / Math.PI;
  const p = Object.assign({fold: 0, stroke: 0, sweep: 0, twist: 0, elbow: 0, wrist: 0, bendElbow: 0, bendWrist: 0, bendTips: 0, fan: 1, handTwist: 0, asym: 0, billow: 0, flutter: 0}, d);
  const R = window.swb.restRot;
  const scales = [];
  for (const [prefix, s] of [['right_', 1], ['left_', -1]]) {
    const bone = (n) => Group.all.find(x => x.name === prefix + n);
    const rot = (n, x, y, z) => {
      const g = bone(n);
      if (!g) return;
      const r = R[g.name];
      g.rotation = [r[0] - x * D, r[1] - y * D, r[2] + z * D];
    };
    const fold = p.fold;
    const fan = Math.min(1, Math.max(0, p.fan)) * (1 - fold);
    const spread = 1 - 0.62 * fold;
    rot('shoulder', p.twist, s * p.stroke - p.asym, s * p.sweep);
    rot('arm', 0, 0, s * -1.26 * fold);
    rot('forearm', 0, s * p.bendElbow, s * (2.74 * fold - p.elbow));
    rot('hand', p.handTwist, s * p.bendWrist, s * (-2.79 * fold - p.wrist));
    rot('thumb', 0, 0, s * 0.5 * fold);
    rot('arm_membrane', p.billow * 0.3, 0, s * 0.35 * fold);
    rot('forearm_membrane', p.billow * 0.3, 0, s * -0.2 * fold);
    scales.push([prefix + 'arm_membrane', spread], [prefix + 'forearm_membrane', spread]);
    for (let k = 0; k < 4; k++) {
      const f = bone('finger_' + (k + 1));
      const r = R[f.name];
      f.rotation = [r[0], r[1], r[2] * fan + s * -0.06 * (1 - fan) * D];
      rot('finger_' + (k + 1) + '_tip', 0, s * p.bendTips * (1 - 0.18 * k), s * -0.25 * fold);
      rot('finger_' + (k + 1) + '_web', p.billow * 0.2, 0, 0);
      rot('finger_' + (k + 1) + '_tip_web', p.billow * 0.25 + p.flutter * (0.6 + 0.3 * k), 0, 0);
      scales.push([prefix + 'finger_' + (k + 1) + '_web', spread], [prefix + 'finger_' + (k + 1) + '_tip_web', spread]);
    }
  }
  Canvas.updateAll();
  for (const [n, sc] of scales) {
    const g = Group.all.find(x => x.name === n);
    if (g && g.mesh) g.mesh.scale.set(1, sc, 1);
  }
  return true;
};
