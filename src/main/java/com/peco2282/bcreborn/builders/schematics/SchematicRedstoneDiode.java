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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

import java.util.LinkedList;

public class SchematicRedstoneDiode extends SchematicBlockFloored {
	private Item baseItem;

	public SchematicRedstoneDiode(Item baseItem) {
		this.baseItem = baseItem;
	}

	@Override
	public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
		requirements.add(new ItemStack(baseItem));
	}

	@Override
	public void storeRequirements(IBuilderContext context, int x, int y, int z) {

	}

	@Override
	public void rotateLeft(IBuilderContext context) {
		if (state != null) {
			state = state.rotate(Rotation.COUNTERCLOCKWISE_90);
		}
	}
}
