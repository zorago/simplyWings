var RIG = (() => {
  const A = [5, 22, 0]
  const T = [0, 11.5, 2.6]
  const groups = [
    { name: 'arm', origin: A },
    { name: 'tail', origin: T, single: true },
    { name: 'tail_tip', parent: 'tail', origin: [T[0], T[1] - 7, T[2]], single: true }
  ]
  const cubes = [
    { name: 'paw', group: 'arm', from: [3.6, 12, -0.6], to: [4.6, 13.5, 0.6], kind: 'paw' },
    { name: 'tail_plate', group: 'tail', from: [T[0] - 3, T[1] - 7, T[2] + 0.6], to: [T[0] + 3, T[1], T[2] + 0.62], kind: 'tail' },
    { name: 'tail_tip_plate', group: 'tail_tip', from: [T[0] - 3.5, T[1] - 15, T[2] + 0.64], to: [T[0] + 3.5, T[1] - 7, T[2] + 0.66], kind: 'tail_tip' }
  ]
  const rig = { model: 'squirrel', texture: [64, 64], A, T, groups, cubes }
  rig.used = PACK(rig, 64)
  const n = 5, top = 22, bottom = 12, flankX = 4
  const ys = [...Array(n)].map((_, i) => top - (top - bottom) * i / (n - 1))
  const spread = (y) => [A[0] + (A[1] - y), A[1] - 1]
  const flank = ys.map(y => [flankX, y])
  const hand = ys.map(spread)
  rig.panel = [...hand, ...flank.slice().reverse()]
  rig.edge = [hand[n - 1], flank[n - 1]]
  rig.armLine = [hand[0], hand[n - 1]]
  const pts = [...flank, ...hand]
  const x0 = Math.floor(Math.min(...pts.map(q => q[0])) - 1), x1 = Math.ceil(Math.max(...pts.map(q => q[0])) + 1)
  const y0 = Math.ceil(Math.max(...pts.map(q => q[1])) + 1), y1 = Math.floor(Math.min(...pts.map(q => q[1])) - 1)
  rig.sheet = { x: x0, y: y0, w: x1 - x0, h: y0 - y1, ventral: [0, rig.used], dorsal: [x1 - x0, rig.used] }
  const uv = (p) => [p[0] - x0, y0 - p[1]]
  const a = ys.map((y, i) => ['root', flankX, y, 0, ...uv(flank[i])])
  const b = ys.map((y, i) => [i === 0 ? 'root' : 'arm', flankX, y, 0, ...uv(hand[i])])
  rig.membranes = { sheet: { x: x0, y: y0, ventral: rig.sheet.ventral, dorsal: rig.sheet.dorsal }, panels: [{ name: 'patagium', rows: 3, a, b }] }
  return rig
})()
