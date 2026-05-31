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
import com.peco2282.bcreborn.api.blueprints.MappingRegistry;
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.block.state.BlockState;

public class SchematicMetadataMask extends SchematicBlock {
	private final int mask;

	public SchematicMetadataMask(int mask) {
		this.mask = mask;
	}

	@Override
	public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
		super.initializeFromObjectAt(context, x, y, z);
	}

	@Override
	public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {
		super.readSchematicFromNBT(nbt, registry);
	}

	@Override
	public void storeRequirements(IBuilderContext context, int x, int y, int z) {
		if (state != null) {
			storedRequirements = new ItemStack[1];
			storedRequirements[0] = new ItemStack(state.getBlock());
		}
	}

	@Override
	public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
		if (state != null) {
			BlockState worldState = context.world().getBlockState(new BlockPos(x, y, z));
			return state.getBlock() == worldState.getBlock();
		}
		return false;
	}
}
