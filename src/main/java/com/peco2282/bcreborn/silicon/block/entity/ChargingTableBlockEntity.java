package com.peco2282.bcreborn.silicon.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;

public class ChargingTableBlockEntity extends LaserTableBaseBlockEntity {
    public ChargingTableBlockEntity(BlockPos pos, BlockState state) {
        super(SiliconBlockEntityTypes.CHARGING_TABLE.get(), pos, state);
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
        return 1;
    }

    @Override
    public boolean hasWork() {
        return false;
    }
}
