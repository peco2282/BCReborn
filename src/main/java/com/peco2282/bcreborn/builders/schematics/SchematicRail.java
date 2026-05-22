/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.builders.schematics;

import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.common.builder.schematics.SchematicBlockFloored;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedList;

public class SchematicRail extends SchematicBlockFloored {
	@Override
	public void rotateLeft(IBuilderContext context) {
		if (state != null) {
			state = state.rotate(Rotation.COUNTERCLOCKWISE_90);
		}
	}

	@Override
	public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
		super.placeInWorld(context, x, y, z, stacks);
	}

	@Override
	public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
		BlockState worldState = context.world().getBlockState(new BlockPos(x, y, z));
		if (state != null) {
			return worldState.getBlock() == state.getBlock();
		}
		return worldState.getBlock() == block;
	}

	@Override
	public void postProcessing(IBuilderContext context, int x, int y, int z) {
		if (state != null) {
			context.world().setBlock(new BlockPos(x, y, z), state, 3);
		}
	}
}
