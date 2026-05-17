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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedList;

public class SchematicTile extends SchematicBlock {

	/**
	 * This tree contains additional data to be stored in the blueprint. By
	 * default, it will be initialized from Schematic.readFromWord with the
	 * standard readNBT function of the corresponding tile (if any) and will be
	 * loaded from BptBlock.writeToWorld using the standard writeNBT function.
	 */
	public CompoundTag tileNBT = new CompoundTag();

	@Override
	public void idsToBlueprint(MappingRegistry registry) {
		registry.scanAndTranslateStacksToRegistry(tileNBT);
	}

	@Override
	public void idsToWorld(MappingRegistry registry) {
		try {
			registry.scanAndTranslateStacksToWorld(tileNBT);
		} catch (MappingNotFoundException e) {
			tileNBT = new CompoundTag();
		}
	}

	public void onNBTLoaded() {

	}

	/**
	 * Places the block in the world, at the location specified in the slot.
	 */
	@Override
	public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
		super.placeInWorld(context, x, y, z, stacks);

		BlockPos pos = new BlockPos(x, y, z);
		BlockEntity be = context.world().getBlockEntity(pos);
		if (be != null) {
			tileNBT.putInt("x", x);
			tileNBT.putInt("y", y);
			tileNBT.putInt("z", z);
			be.load(tileNBT);
		}
	}

	@Override
	public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
		super.initializeFromObjectAt(context, x, y, z);

		BlockEntity tile = context.world().getBlockEntity(new BlockPos(x, y, z));
		if (tile != null) {
			tileNBT = tile.saveWithFullMetadata();
			onNBTLoaded();
		}
	}

	@Override
	public void writeSchematicToNBT(CompoundTag nbt, MappingRegistry registry) {
		super.writeSchematicToNBT(nbt, registry);

		nbt.put("blockCpt", tileNBT);
	}

	@Override
	public void readSchematicFromNBT(CompoundTag nbt, MappingRegistry registry) {
		super.readSchematicFromNBT(nbt, registry);

		tileNBT = nbt.getCompound("blockCpt");
		onNBTLoaded();
	}

	@Override
	public int buildTime() {
		return 5;
	}
}
