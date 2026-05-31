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
package com.peco2282.bcreborn.builders.schematics;

import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.StairBlock;

import java.util.LinkedList;

public class SchematicStairs extends SchematicBlock {

	@Override
	public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
		if (state != null) {
			requirements.add(new ItemStack(state.getBlock()));
		} else if (block != null) {
			requirements.add(new ItemStack(block));
		}
	}

	@Override
	public void storeRequirements(IBuilderContext context, int x, int y, int z) {

	}

	@Override
	public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
		return state == context.world().getBlockState(new BlockPos(x, y, z));
	}

	@Override
	public void rotateLeft(IBuilderContext context) {
		if (state != null) {
			state = state.rotate(net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90);
		}
	}

	@Override
	public BuildingStage getBuildStage() {
		return BuildingStage.STANDALONE;
	}
}
