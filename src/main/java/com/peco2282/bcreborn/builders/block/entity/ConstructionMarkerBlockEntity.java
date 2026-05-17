package com.peco2282.bcreborn.builders.block.entity;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.builder.BuildingItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ConstructionMarkerBlockEntity extends BlockEntity implements MenuProvider {
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public static Set<ConstructionMarkerBlockEntity> currentMarkers = new HashSet<>();

    public Direction direction = Direction.NORTH;
    public ItemStack itemBlueprint;

    private ArrayList<BuildingItem> buildersInAction = new ArrayList<>();
    private CompoundTag initNBT;

    public ConstructionMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypesBuilders.CONSTRUCTION_MARKER.get(), pos, state);
    }

    public ItemStack getBlueprint() {
        return items.get(0);
    }

    public void setBlueprint(ItemStack stack) {
        items.set(0, stack);
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean hasBlueprint() {
        return !items.get(0).isEmpty();
    }

    public ItemStack removeBlueprint() {
        ItemStack stack = items.get(0);
        items.set(0, ItemStack.EMPTY);
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return stack;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.bcrebornbuilders.construction_marker");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
        return null;
    }
}
