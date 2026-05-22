package com.peco2282.bcreborn.builders.block.entity;

import com.peco2282.bcreborn.builders.BlockEntityTypesBuilders;
import com.peco2282.bcreborn.common.builder.TileAbstractBuilder;
import net.minecraft.world.Container;
import com.peco2282.bcreborn.common.SimpleInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class QuarryBlockEntity extends TileAbstractBuilder {
    private final SimpleInventory inv = new SimpleInventory(0, "Quarry", 64);

    public QuarryBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypesBuilders.QUARRY.get(), pos, state);
    }

    @Override
    protected void tick(Level level, BlockPos pos, BlockState state) {
        // TODO: Implement quarry logic
    }

    public Container getInventory() {
        return inv;
    }

    @Override
    public List<ItemStack> getInventoryList() {
        return new ArrayList<>();
    }
}
