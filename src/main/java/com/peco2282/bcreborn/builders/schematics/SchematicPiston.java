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
import com.peco2282.bcreborn.common.builder.schematics.SchematicRotateMeta;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;

public class SchematicPiston extends SchematicRotateMeta {

	public SchematicPiston() {
	}

	@Override
	public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
		if (state != null) {
			context.world().setBlock(new BlockPos(x, y, z), state, 3);
		} else {
			super.placeInWorld(context, x, y, z, stacks);
		}
	}

}
