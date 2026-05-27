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
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.ai.AIRobotFetchAndEquipItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;

public class BoardRobotMiner extends BoardRobotGenericBreakBlock {
	private static final int MAX_HARVEST_LEVEL = 3;
	private int harvestLevel = 0;

	public BoardRobotMiner(EntityRobotBase iRobot) {
		super(iRobot);
		detectHarvestLevel();
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		super.delegateAIEnded(ai);

		if (ai instanceof AIRobotFetchAndEquipItemStack) {
			if (ai.success()) {
				detectHarvestLevel();
			}
		}
	}

	private void detectHarvestLevel() {
		ItemStack stack = robot.getMainHandItem();

		if (!stack.isEmpty() && stack.is(ItemTags.PICKAXES)) {
			if (stack.getItem() instanceof TieredItem tieredItem) {
				Tier tier = tieredItem.getTier();
				harvestLevel = tier.getLevel();
			}
		}
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return BCBoardNBT.REGISTRY.get("miner");
	}

	@Override
	public boolean isExpectedTool(ItemStack stack) {
		return !stack.isEmpty() && stack.is(ItemTags.PICKAXES);
	}

	@Override
	public boolean isExpectedBlock(Level world, int x, int y, int z) {
		return BuildCraftAPI.getWorldProperty("ore@hardness=" + Math.min(MAX_HARVEST_LEVEL, harvestLevel))
				.get(world, new BlockPos(x, y, z));
	}
}
