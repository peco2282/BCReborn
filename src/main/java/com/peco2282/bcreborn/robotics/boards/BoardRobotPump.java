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
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndUnloadFluids;
import com.peco2282.bcreborn.robotics.ai.AIRobotPumpBlock;
import com.peco2282.bcreborn.robotics.ai.AIRobotSearchAndGotoBlock;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;

public class BoardRobotPump extends RedstoneBoardRobot {

	private BlockIndex blockFound;

	public BoardRobotPump(EntityRobotBase iRobot) {
		super(iRobot);
	}

	@Override
	public RedstoneBoardRobotNBT getNBTHandler() {
		return BCBoardNBT.REGISTRY.get("pump");
	}

	@Override
	public void update() {
		final IWorldProperty isFluidSource = BuildCraftAPI.getWorldProperty("fluidSource");
		// Simplified check for fluid in tank
		boolean hasFluid = false; // TODO: Implement proper check

		if (hasFluid) {
			startDelegateAI(new AIRobotGotoStationAndUnloadFluids(robot));
		} else {
			startDelegateAI(new AIRobotSearchAndGotoBlock(robot, false, (world, pos) -> {
				if (isFluidSource.get(world, pos)
						&& !robot.getRegistry().isTaken(new ResourceIdBlock(pos))) {
					return matchesGateFilter(world, pos);
				} else {
					return false;
				}
			}));
		}
	}

	@Override
	public void delegateAIEnded(AIRobot ai) {
		if (ai instanceof AIRobotSearchAndGotoBlock searchAI) {
			if (ai.success()) {
				blockFound = searchAI.getBlockFound();
				startDelegateAI(new AIRobotPumpBlock(robot, blockFound));
			} else {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		} else if (ai instanceof AIRobotPumpBlock) {
			releaseBlockFound();
		} else if (ai instanceof AIRobotGotoStationAndUnloadFluids) {
			if (!ai.success()) {
				startDelegateAI(new AIRobotGotoSleep(robot));
			}
		}
	}

	private void releaseBlockFound() {
		if (blockFound != null) {
			robot.getRegistry().release(new ResourceIdBlock(blockFound.toBlockPos()));
			blockFound = null;
		}
	}

	private boolean matchesGateFilter(Level world, BlockPos pos) {
		return true; // Simplified for now
	}

	@Override
	public boolean canLoadFromNBT() {
		return true;
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
