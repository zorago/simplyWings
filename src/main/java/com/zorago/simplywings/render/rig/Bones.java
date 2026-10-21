package com.zorago.simplywings.render.rig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;

public final class Bones {
    private final ModelPart root;
    private final Map<String, Bone> bones = new HashMap<>();
    private final List<Bone> all = new ArrayList<>();

    public Bones(ModelPart root, Map<String, List<String>> paths) {
        this.root = root;
        for (Map.Entry<String, List<String>> entry : paths.entrySet()) {
            ModelPart part = root;
            boolean found = true;
            for (String name : entry.getValue()) {
                if (!part.hasChild(name)) {
                    found = false;
                    break;
                }
                part = part.getChild(name);
            }
            if (found) {
                Bone bone = new Bone(part);
                bones.put(entry.getKey(), bone);
                all.add(bone);
            }
        }
    }

    public ModelPart root() {
        return root;
    }

    public Bone get(String name) {
        return bones.getOrDefault(name, Bone.NONE);
    }

    public boolean has(String name) {
        return bones.containsKey(name);
    }

    public int count() {
        return all.size();
    }

    public void reset() {
        for (Bone bone : all) {
            bone.reset();
        }
    }
}
