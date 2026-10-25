var RIG = (() => {
  const f = FURY({
    skin: true,
    main: {
      prefix: '', S: [2.4, 22.6, 2.5], arm: 8, forearm: 12, bone: 1.15, armDepth: 15, front: 3, rot: 6,
      fingers: [
        { angle: 10, p: 11, d: 11, droop: -6 }, { angle: -18, p: 10, d: 10, droop: -6 },
        { angle: -46, p: 9, d: 9, droop: -5 }, { angle: -78, p: 8, d: 7, droop: 4 }
      ]
    },
    hind: {
      prefix: 'hind_', S: [2.2, 15.5, 2.8], arm: 4, forearm: 6, bone: 0.75, armDepth: 7, front: 1.5, rot: -18,
      fingers: [
        { angle: 4, p: 6, d: 5, droop: -5 }, { angle: -30, p: 5, d: 5, droop: -4 }, { angle: -70, p: 4, d: 4, droop: 3 }
      ]
    },
    tail: {
      origin: [0, 13.5, 2.6], mid: 2, tip: 5, midFin: [3, 3.5], tipFin: [5.5, 5], red: true,
      segs: [{ len: 5.5, w: 3.4, d: 3 }, { len: 5, w: 2.9, d: 2.6 }, { len: 4.5, w: 2.4, d: 2.2 }, { len: 4.5, w: 2, d: 1.8 }, { len: 4, w: 1.6, d: 1.5 }, { len: 3.5, w: 1.3, d: 1.2 }]
    },
    spines: [
      { y: 23, z: 2.2, h: 1.6, l: 1.6 }, { y: 20.5, z: 2.2, h: 1.8, l: 1.9 }, { y: 18, z: 2.2, h: 1.8, l: 1.9 },
      { y: 15.5, z: 2.2, h: 1.6, l: 1.7 },
      { parent: 'tail_1', y: 11, z: 3.9, h: 1.4, l: 1.5 }, { parent: 'tail_2', y: 6, z: 3.7, h: 1.2, l: 1.3 },
      { parent: 'tail_3', y: 1.5, z: 3.5, h: 1, l: 1.1 }
    ],
    glowSpines: true
  })
  const rig = { model: 'night', texture: [256, 128], ...f }
  rig.used = PACK(rig, 256)
  return rig
})()
