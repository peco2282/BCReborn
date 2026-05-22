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
import net.minecraft.world.level.block.Blocks;

import java.util.LinkedList;

public class SchematicCactus extends SchematicBlockFloored {

	@Override
	public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
		requirements.add(new ItemStack(Blocks.CACTUS));
	}

	@Override
	public void storeRequirements(IBuilderContext context, int x, int y, int z) {
		// cancel requirements reading
	}

	@Override
	public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
		context.world().setBlock(new BlockPos(x, y, z), Blocks.CACTUS.defaultBlockState(), 3);
	}

	@Override
	public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
		return context.world().getBlockState(new BlockPos(x, y, z)).is(Blocks.CACTUS);
	}
}
