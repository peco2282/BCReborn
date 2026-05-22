/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.blueprints;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;

import java.util.LinkedList;

public class SchematicFluid extends SchematicBlock {

	private final ItemStack fluidItem;

	public SchematicFluid(Block fluidBlock) {
		this.block = fluidBlock;
		this.fluidItem = new ItemStack(fluidBlock);
	}

	@Override
	public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
		if (state != null && state.hasProperty(LiquidBlock.LEVEL) && state.getValue(LiquidBlock.LEVEL) == 0) {
			requirements.add(fluidItem.copy());
		}
	}

	@Override
	public void storeRequirements(IBuilderContext context, int x, int y, int z) {
		// cancel requirements reading
	}

	@Override
	public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		return block == context.world().getBlockState(pos).getBlock();
	}

	@Override
	public void rotateLeft(IBuilderContext context) {

	}

	@Override
	public boolean doNotBuild() {
		if (state != null && state.hasProperty(LiquidBlock.LEVEL)) {
			return state.getValue(LiquidBlock.LEVEL) != 0;
		}
		return false;
	}

	@Override
	public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
		if (!doNotBuild()) {
			context.world().setBlock(new BlockPos(x, y, z), state != null ? state : block.defaultBlockState(), 3);
		}
	}

	@Override
	public void postProcessing(IBuilderContext context, int x, int y, int z) {
		if (doNotBuild()) {
			context.world().setBlock(new BlockPos(x, y, z), state != null ? state : block.defaultBlockState(), 3);
		}
	}

	@Override
	public LinkedList<ItemStack> getStacksToDisplay(LinkedList<ItemStack> stackConsumed) {
		LinkedList<ItemStack> result = new LinkedList<ItemStack>();
		result.add(fluidItem.copy());
		return result;
	}

	@Override
	public int getEnergyRequirement(LinkedList<ItemStack> stacksUsed) {
		return 1 * BuilderAPI.BUILD_ENERGY;
	}
}
