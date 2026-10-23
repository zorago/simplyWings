package com.zorago.simplywings.block;

import com.zorago.simplywings.config.ServerConfig;
import com.zorago.simplywings.essence.EssenceContainerItem;
import com.zorago.simplywings.essence.EssenceType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CloudCompressorBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CLOUD = 0;
    public static final int STEAM = 1;
    public static final int ETHEREAL = 2;
    public static final int OUTPUT = 3;
    public static final int SIZE = 4;
    public static final EssenceType[] INPUTS = {EssenceType.CLOUD, EssenceType.STEAM, EssenceType.ETHEREAL};

    private final Items items = new Items();
    private final List<CloudCompressorMenu> menus = new ArrayList<>();
    private int progress;
    private boolean running;
    private float spin;
    private float prevSpin;
    private float speed;

    public CloudCompressorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CLOUD_COMPRESSOR_ENTITY.get(), pos, state);
    }

    public SimpleContainer items() {
        return items;
    }

    public int progress() {
        return progress;
    }

    public boolean running() {
        return running;
    }

    public float spin(float partialTick) {
        return prevSpin + (spin - prevSpin) * partialTick;
    }

    public float speed() {
        return speed;
    }

    public void addMenu(CloudCompressorMenu menu) {
        menus.add(menu);
    }

    public void removeMenu(CloudCompressorMenu menu) {
        menus.remove(menu);
    }

    public static boolean accepts(int slot, ItemStack stack) {
        if (!EssenceContainerItem.isContainer(stack)) {
            return false;
        }
        EssenceType type = EssenceContainerItem.type(stack);
        if (slot == OUTPUT) {
            return type == null || type == EssenceType.INTENSIFIED;
        }
        return type == INPUTS[slot];
    }

    public static int cycleTicks() {
        return ServerConfig.COMPRESSOR_CYCLE_TICKS.get();
    }

    private boolean canRun() {
        int units = ServerConfig.COMPRESSOR_UNITS.get();
        for (int slot = CLOUD; slot <= ETHEREAL; slot++) {
            ItemStack input = items.getItem(slot);
            if (EssenceContainerItem.type(input) != INPUTS[slot] || EssenceContainerItem.amount(input) < units) {
                return false;
            }
        }
        ItemStack output = items.getItem(OUTPUT);
        return output.getCount() == 1 && EssenceContainerItem.isContainer(output)
                && (EssenceContainerItem.type(output) == null || EssenceContainerItem.type(output) == EssenceType.INTENSIFIED)
                && EssenceContainerItem.capacity(output) - EssenceContainerItem.amount(output) >= units * INPUTS.length;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CloudCompressorBlockEntity compressor) {
        boolean run = compressor.canRun();
        if (run) {
            if (++compressor.progress >= cycleTicks()) {
                compressor.progress = 0;
                compressor.compress();
            }
        } else if (compressor.progress > 0) {
            compressor.progress = 0;
        }
        if (run != compressor.running) {
            compressor.running = run;
            compressor.sync();
        }
    }

    private void compress() {
        int units = ServerConfig.COMPRESSOR_UNITS.get();
        for (int slot = CLOUD; slot <= ETHEREAL; slot++) {
            ItemStack input = items.getItem(slot).copy();
            EssenceContainerItem.drain(input, units);
            items.setItem(slot, input);
        }
        ItemStack output = items.getItem(OUTPUT).copy();
        EssenceContainerItem.fill(output, EssenceType.INTENSIFIED, units * INPUTS.length);
        items.setItem(OUTPUT, output);
        setChanged();
        if (level != null && level.random.nextInt(4) == 0) {
            level.playSound(null, worldPosition.above(2), SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.5F, 0.7F);
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CloudCompressorBlockEntity compressor) {
        compressor.speed += ((compressor.running ? 1.0F : 0.0F) - compressor.speed) * 0.05F;
        compressor.prevSpin = compressor.spin;
        compressor.spin += compressor.speed * 6.0F;
        CloudCompressorEffects.tick(level, pos, compressor);
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    public void dropContents() {
        if (level == null) {
            return;
        }
        for (int slot = 0; slot < SIZE; slot++) {
            Block.popResource(level, worldPosition, items.removeItemNoUpdate(slot));
        }
    }

    public AABB renderBox() {
        return new AABB(worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() - 1,
                worldPosition.getX() + 2, worldPosition.getY() + CloudCompressorBlock.HEIGHT + 1, worldPosition.getZ() + 2);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        NonNullList<ItemStack> stacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        for (int slot = 0; slot < SIZE; slot++) {
            stacks.set(slot, items.getItem(slot));
        }
        ContainerHelper.saveAllItems(tag, stacks, registries);
        tag.putInt("Progress", progress);
        tag.putBoolean("Running", running);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Items")) {
            NonNullList<ItemStack> stacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, stacks, registries);
            for (int slot = 0; slot < SIZE; slot++) {
                items.setItemQuietly(slot, stacks.get(slot));
            }
        }
        progress = tag.getInt("Progress");
        running = tag.getBoolean("Running");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Running", running);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.simplywings.cloud_compressor");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CloudCompressorMenu(containerId, inventory, this);
    }

    private final class Items extends SimpleContainer {
        private boolean quiet;

        Items() {
            super(SIZE);
        }

        void setItemQuietly(int slot, ItemStack stack) {
            quiet = true;
            setItem(slot, stack);
            quiet = false;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return accepts(slot, stack);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            if (quiet) {
                return;
            }
            CloudCompressorBlockEntity.this.setChanged();
            for (CloudCompressorMenu menu : List.copyOf(menus)) {
                menu.slotsChanged(this);
            }
        }

        @Override
        public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(CloudCompressorBlockEntity.this, player);
        }
    }
}
