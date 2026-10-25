package com.zorago.simplywings.player;

import com.zorago.simplywings.SimplyWings;
import com.zorago.simplywings.gacha.GachaData;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SimplyWings.MOD_ID);

    public static final Supplier<AttachmentType<WingsData>> WINGS = ATTACHMENTS.register("wings",
            () -> AttachmentType.builder(() -> new WingsData()).serialize(WingsData.CODEC).copyOnDeath().build());

    public static final Supplier<AttachmentType<GachaData>> GACHA = ATTACHMENTS.register("gacha",
            () -> AttachmentType.builder(() -> new GachaData()).serialize(GachaData.CODEC).copyOnDeath().build());

    public static void register(IEventBus modBus) {
        ATTACHMENTS.register(modBus);
    }

    public static GachaData gacha(Player player) {
        return player.getData(GACHA);
    }

    public static WingsData get(Player player) {
        return player.getData(WINGS);
    }

    private ModAttachments() {
    }
}
