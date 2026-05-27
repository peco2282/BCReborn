/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.lib.utils.BlockUtils;

public class AIRobotPumpBlock extends AIRobot {

	private BlockIndex blockToPump;
	private long waited = 0;
	private int pumped = 0;

	public AIRobotPumpBlock(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotPumpBlock(EntityRobotBase iRobot, BlockIndex iBlockToPump) {
		this(iRobot);

		blockToPump = iBlockToPump;
	}

	@Override
	public void start() {
		robot.aimItemAt(blockToPump.x, blockToPump.y, blockToPump.z);
	}

	@Override
	public void preempt(AIRobot ai) {
		super.preempt(ai);
	}

	@Override
	public void update() {
		if (waited < 40) {
			waited++;
		} else {
			FluidStack fluidStack = BlockUtils.drainBlock(robot.level(), blockToPump.x, blockToPump.y, blockToPump.z, false);
			if (fluidStack != null) {
				if (robot.fill(ForgeDirection.UNKNOWN, fluidStack, true) > 0) {
					BlockUtils.drainBlock(robot.level(), blockToPump.x, blockToPump.y,
							blockToPump.z, true);
				}
			}
			terminate();
		}

	}

	@Override
	public int getEnergyCost() {
		return 5;
	}

	@Override
	public boolean success() {
		return pumped > 0;
	}
}
