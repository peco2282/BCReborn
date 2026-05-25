/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.transport;

import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IPipeTile extends IInjectable {
    enum PipeType {
        ITEM, FLUID, POWER, STRUCTURE
    }

    PipeType getPipeType();

    Level getWorld();

    BlockPos getPos();

    boolean isPipeConnected(Direction with);

    Block getNeighborBlock(Direction dir);

    BlockEntity getNeighborTile(Direction dir);

    IPipe getNeighborPipe(Direction dir);

    IPipe getPipe();

    DyeColor getPipeColor();

    PipePluggable getPipePluggable(Direction direction);

    boolean hasPipePluggable(Direction direction);

    boolean hasBlockingPluggable(Direction direction);

    void scheduleNeighborChange();

    void scheduleRenderUpdate();

    @Deprecated
    int injectItem(ItemStack stack, boolean doAdd, Direction from);
}
