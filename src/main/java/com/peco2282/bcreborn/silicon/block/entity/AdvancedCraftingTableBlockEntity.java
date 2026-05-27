package com.peco2282.bcreborn.silicon.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;

public class AdvancedCraftingTableBlockEntity extends LaserTableBaseBlockEntity {
    public AdvancedCraftingTableBlockEntity(BlockPos pos, BlockState state) {
        super(SiliconBlockEntityTypes.ADVANCED_CRAFTING_TABLE.get(), pos, state);
    }

    @Override
    public int getRequiredEnergy() {
        return 0; // TODO: Implement advanced crafting logic
    }

    @Override
    public boolean canCraft() {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 40; // 1.7.10 had 40 slots
    }

    @Override
    public boolean hasWork() {
        return false;
    }
}
