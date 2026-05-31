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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.BedPart;

import java.util.LinkedList;

public class SchematicBed extends SchematicBlock {

	@Override
	public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
		if (state != null && state.getValue(BedBlock.PART) == BedPart.FOOT) {
			requirements.add(new ItemStack(Items.WHITE_BED));
		}
	}

	@Override
	public void storeRequirements(IBuilderContext context, int x, int y, int z) {
		// cancel requirements reading
	}

	@Override
	public void rotateLeft(IBuilderContext context) {
		if (state != null) {
			state = state.rotate(Rotation.COUNTERCLOCKWISE_90);
		}
	}

	@Override
	public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
		if (state != null) {
			context.world().setBlock(new BlockPos(x, y, z), state, 3);
		}
	}

	@Override
	public boolean doNotBuild() {
		return state != null && state.getValue(BedBlock.PART) == BedPart.HEAD;
	}
}
