/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.api.core.IWorldProperty;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.api.robots.ResourceIdBlock;
import com.peco2282.bcreborn.robotics.ai.AIRobotFetchAndEquipItemStack;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotSearchAndGotoBlock;
import com.peco2282.bcreborn.robotics.ai.AIRobotUseToolOnBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.Level;

public class BoardRobotFarmer extends RedstoneBoardRobot {

	private BlockIndex blockFound;

	public BoardRobotFarmer(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return BCBoardNBT.REGISTRY.get("farmer");
	}

	@Override
	public void update() {
		final IWorldProperty isDirt = BuildCraftAPI.getWorldProperty("dirt");
		if (robot.getMainHandItem().isEmpty()) {
			startDelegateAI(new AIRobotFetchAndEquipItemStack(robot, stack -> {
				return !stack.isEmpty() && stack.is(ItemTags.HOES);
			}));
		} else {
			startDelegateAI(new AIRobotSearchAndGotoBlock(robot, false, (world, pos) -> {
				return isDirt.get(world, pos)
						&& !robot.getRegistry().isTaken(new ResourceIdBlock(pos))
						&& world.isEmptyBlock(pos.above());
			}));
		}
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotSearchAndGotoBlock) {
			if (ai.success()) {
				blockFound = ((AIRobotSearchAndGotoBlock) ai).getBlockFound();
				startDelegateAI(new AIRobotUseToolOnBlock(robot, blockFound));
			} else {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		} else if (ai instanceof AIRobotFetchAndEquipItemStack) {
			if (!ai.success()) {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		} else if (ai instanceof AIRobotUseToolOnBlock) {
			releaseBlockFound();
		}
	}

	private void releaseBlockFound() {
		if (blockFound != null) {
			robot.getRegistry().release(new ResourceIdBlock(blockFound.toBlockPos()));
			blockFound = null;
		}
	}

	@Override
	public void end() {
		releaseBlockFound();
	}

	@Override
	public void writeSelfToNBT(CompoundTag nbt) {
		super.writeSelfToNBT(nbt);

		if (blockFound != null) {
			CompoundTag sub = new CompoundTag();
			blockFound.writeTo(sub);
			nbt.put("blockFound", sub);
		}
	}

	@Override
	public void loadSelfFromNBT(CompoundTag nbt) {
		super.loadSelfFromNBT(nbt);

		if (nbt.contains("blockFound")) {
			blockFound = new BlockIndex(nbt.getCompound("blockFound"));
		}
	}
}
