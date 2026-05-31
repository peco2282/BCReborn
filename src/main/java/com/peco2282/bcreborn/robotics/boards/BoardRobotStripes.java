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
package com.peco2282.bcreborn.robotics.boards;

import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.api.robots.ResourceIdBlock;
import com.peco2282.bcreborn.robotics.ai.AIRobotFetchAndEquipItemStack;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotSearchAndGotoBlock;
import com.peco2282.bcreborn.robotics.ai.AIRobotStripesHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class BoardRobotStripes extends RedstoneBoardRobot {

	private BlockIndex blockFound;

	public BoardRobotStripes(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return BCBoardNBT.REGISTRY.get("stripes");
	}

	@Override
	public void update() {
		if (robot.getMainHandItem().isEmpty()) {
			startDelegateAI(new AIRobotFetchAndEquipItemStack(robot, stack -> !stack.isEmpty()));
		} else {
			startDelegateAI(new AIRobotSearchAndGotoBlock(robot, true, (world, pos) -> {
				return world.isEmptyBlock(pos)
						&& !robot.getRegistry().isTaken(new ResourceIdBlock(pos));
			}));
		}
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotSearchAndGotoBlock searchAI) {
			if (ai.success()) {
				blockFound = searchAI.getBlockFound();
				startDelegateAI(new AIRobotStripesHandler(robot, blockFound));
			} else {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		} else if (ai instanceof AIRobotFetchAndEquipItemStack) {
			if (!ai.success()) {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		} else if (ai instanceof AIRobotStripesHandler) {
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
