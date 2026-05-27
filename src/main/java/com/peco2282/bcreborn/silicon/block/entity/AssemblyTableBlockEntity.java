package com.peco2282.bcreborn.silicon.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;

public class AssemblyTableBlockEntity extends LaserTableBaseBlockEntity {
    public AssemblyTableBlockEntity(BlockPos pos, BlockState state) {
        super(SiliconBlockEntityTypes.ASSEMBLY_TABLE.get(), pos, state);
    }

    @Override
    public int getRequiredEnergy() {
        return 0; // TODO: Implement assembly recipe logic
    }

    @Override
    public boolean canCraft() {
        return false; // TODO: Implement assembly recipe logic
    }

    @Override
    public int getContainerSize() {
        return 12; // 1.7.10 had 12 slots for assembly table
    }

    @Override
    public boolean hasWork() {
        return false;
    }
}
