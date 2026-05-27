/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;

public class BoardRobotLeaveCutter extends BoardRobotGenericBreakBlock {

	public BoardRobotLeaveCutter(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return BCBoardNBT.REGISTRY.get("leaveCutter");
	}

	@Override
	public boolean isExpectedTool(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ShearsItem;
	}

	@Override
	public boolean isExpectedBlock(Level world, int x, int y, int z) {
		return BuildCraftAPI.getWorldProperty("leaves").get(world, new BlockPos(x, y, z));
	}
}
