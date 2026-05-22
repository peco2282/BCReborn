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
import com.peco2282.bcreborn.common.SimpleInventory;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;

public class BptPipeFiltered extends BptPipeExtension {

	public BptPipeFiltered(Item i) {
		super(i);
	}

	@Override
	public void rotateLeft(SchematicTile slot, IBuilderContext context) {
		SimpleInventory inv = new SimpleInventory(54, "Filters", 1);
		SimpleInventory newInv = new SimpleInventory(54, "Filters", 1);
		inv.read(slot.tileNBT);

		for (int dir = 0; dir <= 5; ++dir) {
			Direction r = Direction.values()[dir];

			for (int s = 0; s < 9; ++s) {
				newInv.setItem(r.ordinal() * 9 + s, inv.getItem(dir * 9 + s));
			}
		}

		newInv.write(slot.tileNBT);
	}
}
