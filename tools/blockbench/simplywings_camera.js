(function () {
    let toggle;
    let fov = null;

    function bone(name) {
        return Group.all.find(g => g.name === name);
    }

    function follow() {
        if (!toggle || !toggle.value) return;
        const cam = bone('camera');
        const target = bone('camera_target');
        const preview = Preview.selected;
        if (!cam || !target || !preview) return;
        const p = new THREE.Vector3();
        const q = new THREE.Vector3();
        cam.mesh.getWorldPosition(p);
        target.mesh.getWorldPosition(q);
        cam.mesh.visible = false;
        target.mesh.visible = false;
        preview.camera.position.copy(p);
        preview.controls.target.copy(q);
        preview.camera.lookAt(q);
        if (preview.camera.isPerspectiveCamera && preview.camera.fov !== 50) {
            if (fov === null) fov = preview.camera.fov;
            preview.camera.fov = 50;
            preview.camera.updateProjectionMatrix();
        }
    }

    function restore() {
        const cam = bone('camera');
        const target = bone('camera_target');
        if (cam) cam.mesh.visible = true;
        if (target) target.mesh.visible = true;
        const preview = Preview.selected;
        if (preview && fov !== null) {
            preview.camera.fov = fov;
            preview.camera.updateProjectionMatrix();
            fov = null;
        }
    }

    BBPlugin.register('simplywings_camera', {
        title: 'SimplyWings camera view',
        author: 'SimplyWings',
        description: 'Looks through the animated camera bone (camera, aimed at camera_target) while an animation plays or is scrubbed.',
        icon: 'videocam',
        version: '1.0.0',
        variant: 'both',
        onload() {
            toggle = new Toggle('simplywings_camera_view', {
                name: 'Camera view (SimplyWings)',
                icon: 'videocam',
                category: 'view',
                keybind: new Keybind({key: 'k'}),
                default: false,
                onChange(value) {
                    if (value) follow();
                    else restore();
                }
            });
            MenuBar.addAction(toggle, 'view');
            Blockbench.on('display_animation_frame', follow);
        },
        onunload() {
            Blockbench.removeListener('display_animation_frame', follow);
            restore();
            toggle.delete();
        }
    });
})();
