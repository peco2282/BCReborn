/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.builders.blueprints;


import net.minecraft.world.Container;

public interface IBlueprintBuilderAgent {

	boolean breakBlock(int x, int y, int z);

	Container getInventory();

	boolean buildBlock(int x, int y, int z);

}
