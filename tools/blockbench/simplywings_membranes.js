(function () {
  const LAYER = 0.01
  const BILLOW_SPAN = 0.12
  let holder = null
  let built = null

  const spec = () => {
    if (typeof Project === 'undefined' || !Project || !Project.save_path) return null
    const fs = require('fs')
    const path = Project.save_path.replace(/\.bbmodel$/, '.membranes.json')
    try {
      return fs.existsSync(path) ? JSON.parse(fs.readFileSync(path, 'utf8')) : null
    } catch (e) {
      console.warn('SimplyWings membranes', e)
      return null
    }
  }

  const findGroup = name => Group.all.find(g => g.name === name)

  const restMatrix = group => {
    const chain = []
    for (let g = group; g instanceof Group; g = g.parent) chain.unshift(g)
    const m = new THREE.Matrix4()
    let parentOrigin = [0, 0, 0]
    for (const g of chain) {
      const t = new THREE.Matrix4().makeTranslation(g.origin[0] - parentOrigin[0], g.origin[1] - parentOrigin[1], g.origin[2] - parentOrigin[2])
      const r = new THREE.Matrix4().makeRotationFromEuler(new THREE.Euler(
        THREE.MathUtils.degToRad(g.rotation[0]), THREE.MathUtils.degToRad(g.rotation[1]), THREE.MathUtils.degToRad(g.rotation[2]), 'ZYX'))
      m.multiply(t).multiply(r)
      parentOrigin = g.origin
    }
    return m
  }

  const anchorOf = (point, side, rootGroup) => {
    const bone = point[0]
    const group = bone === 'root' ? rootGroup : findGroup((side > 0 ? 'right_' : 'left_') + bone)
    if (bone !== 'root' && !group) return null
    const p = new THREE.Vector3(side * point[1], point[2], point[3])
    const rest = group ? restMatrix(group) : new THREE.Matrix4()
    return { group, local: p.applyMatrix4(rest.clone().invert()) }
  }

  const build = () => {
    clear()
    const s = spec()
    if (!s || !Texture.all.length) return
    const shoulder = findGroup('right_shoulder')
    const rootGroup = shoulder && shoulder.parent instanceof Group ? shoulder.parent : null
    const panels = []
    for (const panel of s.panels) {
      for (const side of [1, -1]) {
        const a = panel.a.map(p => anchorOf(p, side, rootGroup))
        const b = panel.b.map(p => anchorOf(p, side, rootGroup))
        if (a.includes(null) || b.includes(null)) continue
        panels.push({
          a, b, rows: panel.rows || 1,
          ua: panel.a.map(p => [p[1] - s.sheet.x, s.sheet.y - p[2]]),
          ub: panel.b.map(p => [p[1] - s.sheet.x, s.sheet.y - p[2]])
        })
      }
    }
    const tex = new THREE.Texture(Texture.all[0].img)
    tex.magFilter = THREE.NearestFilter
    tex.minFilter = THREE.NearestFilter
    tex.needsUpdate = true
    const material = new THREE.MeshLambertMaterial({ map: tex, transparent: true, alphaTest: 0.5, side: THREE.DoubleSide })
    holder = new THREE.Group()
    holder.name = 'simplywings_membranes'
    const meshes = panels.map(panel => {
      const quads = (panel.a.length - 1) * panel.rows * 2
      const geometry = new THREE.BufferGeometry()
      geometry.setAttribute('position', new THREE.BufferAttribute(new Float32Array(quads * 6 * 3), 3))
      geometry.setAttribute('uv', new THREE.BufferAttribute(new Float32Array(quads * 6 * 2), 2))
      const mesh = new THREE.Mesh(geometry, material)
      mesh.frustumCulled = false
      holder.add(mesh)
      return { panel, mesh }
    })
    scene.add(holder)
    built = { spec: s, meshes, project: Project }
    update()
  }

  const clear = () => {
    if (holder) {
      scene.remove(holder)
      holder.traverse(o => o.geometry && o.geometry.dispose())
    }
    holder = null
    built = null
  }

  const billowValue = () => {
    const g = findGroup('scene_membrane')
    return g && g.mesh ? g.mesh.position.x / 10 : 0.3
  }

  const update = () => {
    if (!built) return
    scene.updateMatrixWorld(true)
    const billow = billowValue()
    const s = built.spec
    const tw = Project.texture_width, th = Project.texture_height
    for (const { panel, mesh } of built.meshes) {
      const n = panel.a.length, rows = panel.rows
      const world = (anchor) => anchor.group ? anchor.group.mesh.localToWorld(anchor.local.clone())
        : anchor.local.clone().applyMatrix4(Project.model_3d ? Project.model_3d.matrixWorld : new THREE.Matrix4())
      const grid = []
      for (let i = 0; i < n; i++) {
        const pa = world(panel.a[i]), pb = world(panel.b[i])
        const bg = panel.b[i].group
        const up = bg ? new THREE.Vector3(0, 0, 1).transformDirection(bg.mesh.matrixWorld) : new THREE.Vector3(0, 0, 1)
        const bulge = billow * BILLOW_SPAN * pa.distanceTo(pb)
        const col = []
        for (let r = 0; r <= rows; r++) {
          const t = r / rows
          const p = pa.clone().lerp(pb, t).addScaledVector(up, bulge * Math.sin(Math.PI * t))
          const u = panel.ua[i][0] + (panel.ub[i][0] - panel.ua[i][0]) * t
          const v = panel.ua[i][1] + (panel.ub[i][1] - panel.ua[i][1]) * t
          col.push({ p, u, v, up })
        }
        grid.push(col)
      }
      const pos = mesh.geometry.attributes.position.array
      const uv = mesh.geometry.attributes.uv.array
      let k = 0, q = 0
      const put = (c, offset, sign, normal) => {
        pos[k++] = c.p.x + sign * LAYER * normal.x
        pos[k++] = c.p.y + sign * LAYER * normal.y
        pos[k++] = c.p.z + sign * LAYER * normal.z
        uv[q++] = (offset[0] + c.u) / tw
        uv[q++] = 1 - (offset[1] + c.v) / th
      }
      for (let i = 0; i + 1 < n; i++) {
        for (let r = 0; r < rows; r++) {
          const c0 = grid[i][r], c1 = grid[i + 1][r], c2 = grid[i + 1][r + 1], c3 = grid[i][r + 1]
          const normal = c2.p.clone().sub(c0.p).cross(c3.p.clone().sub(c1.p))
          if (normal.lengthSq() < 1e-9) normal.copy(c0.up)
          normal.normalize()
          if (normal.dot(c0.up) < 0) normal.negate()
          for (const [offset, sign] of [[s.sheet.dorsal, 1], [s.sheet.ventral, -1]]) {
            for (const c of [c0, c1, c2, c0, c2, c3]) put(c, offset, sign, normal)
          }
        }
      }
      mesh.geometry.attributes.position.needsUpdate = true
      mesh.geometry.attributes.uv.needsUpdate = true
      mesh.geometry.computeVertexNormals()
      mesh.geometry.computeBoundingSphere()
    }
  }

  const valid = () => built && built.project === Project && built.meshes.every(({ panel }) =>
    [...panel.a, ...panel.b].every(a => !a.group || (a.group.mesh && Group.all.includes(a.group))))
  const onFrame = () => {
    if (!built) return
    if (!valid()) {
      clear()
      return
    }
    try {
      update()
    } catch (e) {
      console.warn('SimplyWings membranes', e)
      clear()
    }
  }
  const onProject = () => {
    clear()
    setTimeout(() => { try { build() } catch (e) { console.warn('SimplyWings membranes', e) } }, 500)
  }

  Plugin.register('simplywings_membranes', {
    title: 'SimplyWings Membranes',
    author: 'zorago',
    description: 'Draws the skinned wing membranes of SimplyWings rigs (simplywings_membranes) and follows the bones.',
    icon: 'texture',
    version: '1.0.0',
    variant: 'both',
    onload() {
      Blockbench.on('display_animation_frame', onFrame)
      Blockbench.on('update_view', onFrame)
      Blockbench.on('load_project', onProject)
      Blockbench.on('select_project', onProject)
      window.swMembranes = { build, update, clear }
      build()
    },
    onunload() {
      Blockbench.removeListener('display_animation_frame', onFrame)
      Blockbench.removeListener('update_view', onFrame)
      Blockbench.removeListener('load_project', onProject)
      Blockbench.removeListener('select_project', onProject)
      clear()
      delete window.swMembranes
    }
  })
})()
