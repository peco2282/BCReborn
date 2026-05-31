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
import com.peco2282.bcreborn.api.blueprints.SchematicTile;
import com.peco2282.bcreborn.api.core.JavaTools;
import net.minecraft.world.item.ItemStack;

public class SchematicJukebox extends SchematicTile {
	public SchematicJukebox() {
	}

	@Override
	public void storeRequirements(IBuilderContext context, int x, int y, int z) {
		super.storeRequirements(context, x, y, z);
		if (tileNBT != null && tileNBT.contains("RecordItem")) {
			ItemStack recordStack = ItemStack.of(tileNBT.getCompound("RecordItem"));
			storedRequirements = JavaTools.concat(storedRequirements, new ItemStack[] { recordStack });
		}
	}
}