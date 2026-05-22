/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.transport.schematics;


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicTile;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;

public class BptPipeRotatable extends BptPipeExtension {

	public BptPipeRotatable(Item i) {
		super(i);
	}

	@Override
	public void rotateLeft(SchematicTile slot, IBuilderContext context) {
		int orientation = (int) slot.meta & 7;
		int others = (int) (slot.meta - orientation);

		slot.meta = (byte) (Direction.values()[orientation].getClockWise(Direction.Axis.Y).ordinal() + others);
	}

}
