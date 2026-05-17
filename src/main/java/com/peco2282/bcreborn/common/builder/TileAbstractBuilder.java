package com.peco2282.bcreborn.common.builder;

import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;
import java.util.List;

public abstract class TileAbstractBuilder extends BuildCraftBlockEntity {

    public LinkedList<ItemStack> requiredToBuild;

    public TileAbstractBuilder(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract List<ItemStack> getInventory();

    public boolean isBuildingMaterial(int i) {
        return true;
    }

    public boolean consumeEnergyToDoWork() {
        return true;
    }

    @Override
    protected void tick(Level level, BlockPos pos, BlockState state) {
    }
}
