package com.zorago.simplywings.block;

import com.zorago.simplywings.block.recipe.AssemblyInput;
import com.zorago.simplywings.block.recipe.AssemblyRecipe;
import com.zorago.simplywings.block.recipe.ModRecipes;
import com.zorago.simplywings.item.CloudFlaskItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblyTableBlockEntity extends BlockEntity implements MenuProvider {
    public static final int FLASK = 0;
    public static final int HARNESS = 1;
    public static final int CORE = 2;
    public static final int LEFT_WING = 3;
    public static final int RIGHT_WING = 4;
    public static final int OUTPUT = 5;
    public static final int ENERGY = 6;
    public static final int GRID = 7;
    public static final int GRID_CELLS = 21;
    public static final int SIZE = GRID + GRID_CELLS;
    public static final int TANK_CAPACITY = 1000;
    public static final int ASSEMBLY_TICKS = 80;
    public static final int EVENT_COMPONENT = 1;
    public static final int EVENT_WINGS = 2;

    private final Items items = new Items();
    private final List<AssemblyTableMenu> menus = new ArrayList<>();
    private int tank;
    private int assemblyTicks = -1;
    private ItemStack pending = ItemStack.EMPTY;
    private int animation;
    private long animationStart;

    public AssemblyTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ASSEMBLY_TABLE_ENTITY.get(), pos, state);
    }

    public SimpleContainer items() {
        return items;
    }

    public int tank() {
        return tank;
    }

    public int assemblyTicks() {
        return assemblyTicks;
    }

    public int animation() {
        return animation;
    }

    public long animationStart() {
        return animationStart;
    }

    public void addMenu(AssemblyTableMenu menu) {
        menus.add(menu);
    }

    public void removeMenu(AssemblyTableMenu menu) {
        menus.remove(menu);
    }

    public void drain(int amount) {
        tank = Math.max(0, tank - amount);
        sync();
    }

    public AssemblyInput assemblyInput() {
        return new AssemblyInput(items.getItem(HARNESS), items.getItem(CORE), items.getItem(LEFT_WING), items.getItem(RIGHT_WING));
    }

    public Optional<RecipeHolder<AssemblyRecipe>> assemblyRecipe() {
        if (level == null) {
            return Optional.empty();
        }
        return level.getRecipeManager().getRecipeFor(ModRecipes.ASSEMBLY.get(), assemblyInput(), level);
    }

    public boolean startAssembly() {
        if (level == null || assemblyTicks >= 0 || !items.getItem(OUTPUT).isEmpty()) {
            return false;
        }
        Optional<RecipeHolder<AssemblyRecipe>> recipe = assemblyRecipe();
        if (recipe.isEmpty() || tank < recipe.get().value().essence()) {
            return false;
        }
        pending = recipe.get().value().assemble(assemblyInput(), level.registryAccess());
        tank -= recipe.get().value().essence();
        for (int slot = HARNESS; slot <= RIGHT_WING; slot++) {
            items.removeItem(slot, 1);
        }
        assemblyTicks = 0;
        level.blockEvent(worldPosition, getBlockState().getBlock(), EVENT_WINGS, 0);
        sync();
        return true;
    }

    public void playComponentCraft() {
        if (level != null) {
            level.blockEvent(worldPosition, getBlockState().getBlock(), EVENT_COMPONENT, 0);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AssemblyTableBlockEntity table) {
        table.pourFlask();
        if (table.assemblyTicks >= 0 && ++table.assemblyTicks >= ASSEMBLY_TICKS) {
            table.items.setItem(OUTPUT, table.pending);
            table.pending = ItemStack.EMPTY;
            table.assemblyTicks = -1;
            table.sync();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AssemblyTableBlockEntity table) {
        AssemblyTableEffects.tick(level, pos, state, table);
    }

    private void pourFlask() {
        ItemStack flask = items.getItem(FLASK);
        int essence = CloudFlaskItem.essence(flask);
        if (essence <= 0 || tank >= TANK_CAPACITY) {
            return;
        }
        int poured = Math.min(essence, TANK_CAPACITY - tank);
        tank += poured;
        ItemStack rest = flask.copy();
        CloudFlaskItem.setEssence(rest, essence - poured);
        items.setItem(FLASK, rest);
        sync();
    }

    private void sync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == EVENT_COMPONENT || id == EVENT_WINGS) {
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
        for (int slot = 0; slot < SIZE; slot++) {
            Block.popResource(level, worldPosition, items.removeItemNoUpdate(slot));
        }
        Block.popResource(level, worldPosition, pending);
        pending = ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        NonNullList<ItemStack> stacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        for (int slot = 0; slot < SIZE; slot++) {
            stacks.set(slot, items.getItem(slot));
        }
        ContainerHelper.saveAllItems(tag, stacks, registries);
        tag.putInt("Tank", tank);
        tag.putInt("AssemblyTicks", assemblyTicks);
        if (!pending.isEmpty()) {
            tag.put("Pending", pending.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        NonNullList<ItemStack> stacks = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, stacks, registries);
        for (int slot = 0; slot < SIZE; slot++) {
            items.setItemQuietly(slot, stacks.get(slot));
        }
        tank = tag.getInt("Tank");
        assemblyTicks = tag.contains("AssemblyTicks") ? tag.getInt("AssemblyTicks") : -1;
        pending = tag.contains("Pending") ? ItemStack.parseOptional(registries, tag.getCompound("Pending")) : ItemStack.EMPTY;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Tank", tank);
        tag.putInt("AssemblyTicks", assemblyTicks);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        tank = tag.getInt("Tank");
        assemblyTicks = tag.getInt("AssemblyTicks");
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.simplywings.assembly_table");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AssemblyTableMenu(containerId, inventory, this);
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
        public void setChanged() {
            super.setChanged();
            if (quiet) {
                return;
            }
            AssemblyTableBlockEntity.this.setChanged();
            for (AssemblyTableMenu menu : List.copyOf(menus)) {
                menu.slotsChanged(this);
            }
        }

        @Override
        public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(AssemblyTableBlockEntity.this, player);
        }
    }
}
