package com.zorago.simplywings.block;

import net.minecraft.sounds.SoundSource;
import com.zorago.simplywings.sound.ModSounds;
import com.zorago.simplywings.essence.EssenceContainerItem;
import com.zorago.simplywings.essence.EssenceCost;
import com.zorago.simplywings.essence.EssenceTanks;
import com.zorago.simplywings.essence.EssenceType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class EssenceBenchBlockEntity extends BlockEntity implements MenuProvider {
    public static final int FLASK = 0;

    private final Items items;
    private final List<EssenceBenchMenu> menus = new ArrayList<>();
    private final EssenceTanks tanks = new EssenceTanks();
    private int animation;
    private long animationStart;

    protected EssenceBenchBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int size) {
        super(type, pos, state);
        this.items = new Items(size);
    }

    public SimpleContainer items() {
        return items;
    }

    public EssenceTanks tanks() {
        return tanks;
    }

    public int tank(EssenceType type) {
        return tanks.get(type);
    }

    public float fill() {
        int total = 0;
        for (EssenceType type : EssenceType.values()) {
            total += tanks.get(type);
        }
        return total / (float) (EssenceTanks.CAPACITY * EssenceType.COUNT);
    }

    public int animation() {
        return animation;
    }

    public long animationStart() {
        return animationStart;
    }

    public void addMenu(EssenceBenchMenu menu) {
        menus.add(menu);
    }

    public void removeMenu(EssenceBenchMenu menu) {
        menus.remove(menu);
    }

    public boolean canPay(EssenceCost cost) {
        return tanks.canPay(cost);
    }

    public void pay(EssenceCost cost) {
        tanks.pay(cost);
        sync();
    }

    protected void playEvent(int id) {
        if (level != null) {
            level.blockEvent(worldPosition, getBlockState().getBlock(), id, 0);
        }
    }

    private long lastPourSound = -100;

    protected void tickServer() {
        ItemStack flask = items.getItem(FLASK);
        if (!EssenceContainerItem.isContainer(flask) || EssenceContainerItem.type(flask) == null) {
            return;
        }
        ItemStack rest = flask.copy();
        if (tanks.pour(rest)) {
            if (level != null && level.getGameTime() - lastPourSound >= 20) {
                lastPourSound = level.getGameTime();
                level.playSound(null, worldPosition, ModSounds.ESSENCE_POUR.get(), SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
            }
            items.setItem(FLASK, rest);
            sync();
        }
    }

    protected void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id > 0) {
            if (level != null && level.isClientSide) {
                animation = id;
                animationStart = level.getGameTime();
            }
            return true;
        }
        return super.triggerEvent(id, type);
    }

    public void dropContents() {
        if (level == null) {
            return;
        }
        for (int slot = 0; slot < items.getContainerSize(); slot++) {
            Block.popResource(level, worldPosition, items.removeItemNoUpdate(slot));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        NonNullList<ItemStack> stacks = NonNullList.withSize(items.getContainerSize(), ItemStack.EMPTY);
        for (int slot = 0; slot < items.getContainerSize(); slot++) {
            stacks.set(slot, items.getItem(slot));
        }
        ContainerHelper.saveAllItems(tag, stacks, registries);
        tanks.save(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        NonNullList<ItemStack> stacks = NonNullList.withSize(items.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, stacks, registries);
        for (int slot = 0; slot < items.getContainerSize(); slot++) {
            items.setItemQuietly(slot, stacks.get(slot));
        }
        tanks.load(tag);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tanks.save(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        tanks.load(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private final class Items extends SimpleContainer {
        private boolean quiet;

        Items(int size) {
            super(size);
        }

        void setItemQuietly(int slot, ItemStack stack) {
            quiet = true;
            setItem(slot, stack);
            quiet = false;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (quiet) {
                return;
            }
            EssenceBenchBlockEntity.this.setChanged();
            for (EssenceBenchMenu menu : List.copyOf(menus)) {
                menu.slotsChanged(this);
            }
        }

        @Override
        public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(EssenceBenchBlockEntity.this, player);
        }
    }
}
