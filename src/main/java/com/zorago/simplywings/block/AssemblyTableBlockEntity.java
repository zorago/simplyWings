package com.zorago.simplywings.block;

import com.zorago.simplywings.block.recipe.AssemblyInput;
import com.zorago.simplywings.block.recipe.AssemblyRecipe;
import com.zorago.simplywings.block.recipe.ModRecipes;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblyTableBlockEntity extends EssenceBenchBlockEntity {
    public static final int HARNESS = 1;
    public static final int CORE = 2;
    public static final int LEFT_WING = 3;
    public static final int RIGHT_WING = 4;
    public static final int OUTPUT = 5;
    public static final int SIZE = 6;
    public static final int ASSEMBLY_TICKS = 80;
    public static final int EVENT_WINGS = 2;

    private int assemblyTicks = -1;
    private ItemStack pending = ItemStack.EMPTY;

    public AssemblyTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ASSEMBLY_TABLE_ENTITY.get(), pos, state, SIZE);
    }

    public int assemblyTicks() {
        return assemblyTicks;
    }

    public AssemblyInput assemblyInput() {
        return new AssemblyInput(items().getItem(HARNESS), items().getItem(CORE), items().getItem(LEFT_WING), items().getItem(RIGHT_WING));
    }

    public Optional<RecipeHolder<AssemblyRecipe>> assemblyRecipe() {
        if (level == null) {
            return Optional.empty();
        }
        return level.getRecipeManager().getRecipeFor(ModRecipes.ASSEMBLY.get(), assemblyInput(), level);
    }

    public boolean startAssembly() {
        if (level == null || assemblyTicks >= 0 || !items().getItem(OUTPUT).isEmpty()) {
            return false;
        }
        Optional<RecipeHolder<AssemblyRecipe>> recipe = assemblyRecipe();
        if (recipe.isEmpty() || !canPay(recipe.get().value().essence())) {
            return false;
        }
        pending = recipe.get().value().assemble(assemblyInput(), level.registryAccess());
        tanks().pay(recipe.get().value().essence());
        for (int slot = HARNESS; slot <= RIGHT_WING; slot++) {
            items().removeItem(slot, 1);
        }
        assemblyTicks = 0;
        playEvent(EVENT_WINGS);
        sync();
        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AssemblyTableBlockEntity table) {
        table.tickServer();
        if (table.assemblyTicks >= 0 && ++table.assemblyTicks >= ASSEMBLY_TICKS) {
            table.items().setItem(OUTPUT, table.pending);
            table.pending = ItemStack.EMPTY;
            table.assemblyTicks = -1;
            table.sync();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AssemblyTableBlockEntity table) {
        AssemblyTableEffects.tick(level, pos, state, table);
    }

    @Override
    public void dropContents() {
        super.dropContents();
        if (level != null) {
            Block.popResource(level, worldPosition, pending);
        }
        pending = ItemStack.EMPTY;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("AssemblyTicks", assemblyTicks);
        if (!pending.isEmpty()) {
            tag.put("Pending", pending.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        assemblyTicks = tag.contains("AssemblyTicks") ? tag.getInt("AssemblyTicks") : -1;
        pending = tag.contains("Pending") ? ItemStack.parseOptional(registries, tag.getCompound("Pending")) : ItemStack.EMPTY;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("AssemblyTicks", assemblyTicks);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        assemblyTicks = tag.getInt("AssemblyTicks");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.simplywings.assembly_table");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AssemblyTableMenu(containerId, inventory, this);
    }
}
