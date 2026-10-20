package com.zorago.simplywings.client;

import com.zorago.simplywings.anim.TailAnimator;
import com.zorago.simplywings.anim.WingAnimator;
import com.zorago.simplywings.player.FlightMode;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class WingVisuals {
    private static final Int2ObjectMap<Visual> REMOTE = new Int2ObjectOpenHashMap<>();
    private static final Visual LOCAL = new Visual();
    private static Optional<ResourceLocation> preview;

    public static final class Visual {
        public Optional<ResourceLocation> equipped = Optional.empty();
        public FlightMode mode = FlightMode.NONE;
        public int flags;
        public final WingAnimator animator = new WingAnimator();
        public final TailAnimator tail = new TailAnimator();

        public boolean has(int flag) {
            return (flags & flag) != 0;
        }
    }

    public static Visual of(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (player == mc.player) {
            LOCAL.equipped = preview != null ? preview : ClientWings.equipped();
            LOCAL.mode = ClientFlight.mode();
            LOCAL.flags = ClientFlight.flags();
            return LOCAL;
        }
        return REMOTE.get(player.getId());
    }

    static void update(int entityId, Optional<ResourceLocation> equipped, FlightMode mode, int flags) {
        if (equipped.isEmpty()) {
            REMOTE.remove(entityId);
            return;
        }
        Visual visual = REMOTE.computeIfAbsent(entityId, id -> new Visual());
        visual.equipped = equipped;
        visual.mode = mode;
        visual.flags = flags;
    }

    public static void setPreview(Optional<ResourceLocation> wing) {
        preview = wing;
    }

    public static Visual local() {
        return LOCAL;
    }

    static void clear() {
        REMOTE.clear();
    }

    private WingVisuals() {
    }
}
