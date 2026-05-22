/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.core.Direction;

public interface ITileBufferHolder {

	void blockRemoved(Direction from);

	void blockCreated(Direction from, Block block, BlockEntity tile);

	Block getBlock(Direction to);

	BlockEntity getTile(Direction to);

}
