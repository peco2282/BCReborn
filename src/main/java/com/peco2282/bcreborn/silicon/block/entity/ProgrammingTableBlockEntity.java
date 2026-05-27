package com.peco2282.bcreborn.silicon.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;

public class ProgrammingTableBlockEntity extends LaserTableBaseBlockEntity {
    public ProgrammingTableBlockEntity(BlockPos pos, BlockState state) {
        super(SiliconBlockEntityTypes.PROGRAMMING_TABLE.get(), pos, state);
    }

    @Override
    public int getRequiredEnergy() {
        return 0;
    }

    @Override
    public boolean canCraft() {
        return false;
    }

    @Override
    public int getContainerSize() {
        return 2; // 1.7.10 had 2 slots
    }

    @Override
    public boolean hasWork() {
        return false;
    }
}
