/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.silicon.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;

public class StampingTableBlockEntity extends LaserTableBaseBlockEntity {
    public StampingTableBlockEntity(BlockPos pos, BlockState state) {
        super(SiliconBlockEntityTypes.STAMPING_TABLE.get(), pos, state);
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
        return 16; // 1.7.10 had 16 slots
    }

    @Override
    public boolean hasWork() {
        return false;
    }
}
