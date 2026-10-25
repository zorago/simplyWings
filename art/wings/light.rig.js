var RIG = (() => {
  const f = FURY({
    backPlate: true,
    skin: true,
    main: {
      prefix: '', S: [2.4, 22.6, 2.5], arm: 8, forearm: 12, bone: 1.15, armDepth: 15, front: 3, rot: 6,
      fingers: [
        { angle: 12, p: 11, d: 11, droop: -5 }, { angle: -16, p: 10, d: 10, droop: -5 },
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
      origin: [0, 13.5, 2.6], mid: 2, tip: 5, midFin: [3, 3.5], tipFin: [5.5, 5], red: false,
      segs: [{ len: 5.5, w: 3.4, d: 3 }, { len: 5, w: 2.9, d: 2.6 }, { len: 4.5, w: 2.4, d: 2.2 }, { len: 4.5, w: 2, d: 1.8 }, { len: 4, w: 1.6, d: 1.5 }, { len: 3.5, w: 1.3, d: 1.2 }]
    },
    spines: [],

    glowSpines: false
  })
  const rig = { model: 'light', texture: [256, 128], ...f }
  rig.used = PACK(rig, 256)
  return rig
})()
