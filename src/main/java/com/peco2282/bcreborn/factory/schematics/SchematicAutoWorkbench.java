/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.factory.schematics;


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicTile;
import com.peco2282.bcreborn.api.core.IInvSlot;
import com.peco2282.bcreborn.api.core.JavaTools;
import com.peco2282.bcreborn.common.inventory.InventoryIterator;
import com.peco2282.bcreborn.factory.block.entity.AutoWorkbenchBlockEntity;
import com.peco2282.bcreborn.factory.FactoryBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.LinkedList;

public class SchematicAutoWorkbench extends SchematicTile {

	@Override
	public void storeRequirements(IBuilderContext context, int x, int y, int z) {
		AutoWorkbenchBlockEntity autoWb = getTile(context, new BlockPos(x, y, z));
		if (autoWb != null) {
			ArrayList<ItemStack> rqs = new ArrayList<ItemStack>();
			rqs.add(new ItemStack(FactoryBlocks.AUTO_WORKBENCH.get()));

			for (IInvSlot slot : InventoryIterator.getIterable(autoWb.craftMatrix, Direction.UP)) {
				ItemStack stack = slot.getStackInSlot();
				if (stack != null) {
					stack = stack.copy();
					stack.setCount(1);
					rqs.add(stack);
				}
			}

			storedRequirements = JavaTools.concat(storedRequirements, rqs
					.toArray(new ItemStack[rqs.size()]));
		}
	}

	@Override
	public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
		super.initializeFromObjectAt(context, x, y, z);

		tileNBT.remove("Items");
	}

	@Override
	public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
		super.placeInWorld(context, x, y, z, stacks);

		AutoWorkbenchBlockEntity autoWb = getTile(context, new BlockPos(x, y, z));
		if (autoWb != null) {
			for (IInvSlot slot : InventoryIterator.getIterable(autoWb.craftMatrix, Direction.UP)) {
				ItemStack stack = slot.getStackInSlot();
				if (stack != null) {
					stack.setCount(1);
				}
			}
		}
	}

	@Override
	public BuildingStage getBuildStage() {
		return BuildingStage.STANDALONE;
	}

	private AutoWorkbenchBlockEntity getTile(IBuilderContext context, BlockPos pos) {
		BlockEntity tile = context.world().getBlockEntity(pos);
		if (tile != null && tile instanceof AutoWorkbenchBlockEntity autoWork) {
			return autoWork;
		}
		return null;
	}
}
