package com.zorago.simplywings.gacha;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class GachaData {
    public static final int HISTORY = 100;

    public record Entry(int tier, ResourceLocation item, int count) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("tier").forGetter(Entry::tier),
                ResourceLocation.CODEC.fieldOf("item").forGetter(Entry::item),
                Codec.INT.optionalFieldOf("count", 1).forGetter(Entry::count)
        ).apply(i, Entry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Entry::tier,
                ResourceLocation.STREAM_CODEC, Entry::item,
                ByteBufCodecs.VAR_INT, Entry::count,
                Entry::new);

        public GachaTier rank() {
            return GachaTier.of(tier);
        }
    }

    public static final Codec<GachaData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("pity_s", 0).forGetter(d -> d.pityS),
            Codec.INT.optionalFieldOf("pity_ss", 0).forGetter(d -> d.pitySS),
            Codec.INT.optionalFieldOf("pity_sss", 0).forGetter(d -> d.pitySSS),
            Codec.INT.optionalFieldOf("total", 0).forGetter(d -> d.total),
            Codec.DOUBLE.optionalFieldOf("reserve", 0.0).forGetter(d -> d.reserve),
            Entry.CODEC.listOf().optionalFieldOf("history", List.of()).forGetter(d -> List.copyOf(d.history))
    ).apply(i, GachaData::new));

    private int pityS;
    private int pitySS;
    private int pitySSS;
    private int total;
    private double reserve;
    private final Deque<Entry> history;

    public GachaData() {
        this(0, 0, 0, 0, 0.0, List.of());
    }

    private GachaData(int pityS, int pitySS, int pitySSS, int total, double reserve, List<Entry> history) {
        this.pityS = pityS;
        this.pitySS = pitySS;
        this.pitySSS = pitySSS;
        this.total = total;
        this.reserve = reserve;
        this.history = new ArrayDeque<>(history);
    }

    public int pity(GachaTier tier) {
        return switch (tier) {
            case S -> pityS;
            case SS -> pitySS;
            case SSS, MYTHIC -> pitySSS;
            default -> 0;
        };
    }

    public void tickPity() {
        pityS++;
        pitySS++;
        pitySSS++;
        total++;
    }

    public void reset(GachaTier got) {
        if (got.atLeast(GachaTier.S)) {
            pityS = 0;
        }
        if (got.atLeast(GachaTier.SS)) {
            pitySS = 0;
        }
        if (got.atLeast(GachaTier.SSS)) {
            pitySSS = 0;
        }
    }

    public int total() {
        return total;
    }

    public double reserve() {
        return reserve;
    }

    public void setReserve(double reserve) {
        this.reserve = reserve;
    }

    public void record(Entry entry) {
        history.addFirst(entry);
        while (history.size() > HISTORY) {
            history.removeLast();
        }
    }

    public List<Entry> history() {
        return new ArrayList<>(history);
    }

    public void clear() {
        pityS = 0;
        pitySS = 0;
        pitySSS = 0;
        total = 0;
        history.clear();
    }

    public void setPity(int s, int ss, int sss) {
        pityS = s;
        pitySS = ss;
        pitySSS = sss;
    }
}
