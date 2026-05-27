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
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.lib.inventory.filters.SimpleFluidFilter;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import com.peco2282.bcreborn.robotics.statements.ActionStationAcceptFluids;

public class AIRobotUnloadFluids extends AIRobot {

	private int waitedCycles = 0;

	public AIRobotUnloadFluids(EntityRobotBase iRobot) {
		super(iRobot);
		setSuccess(false);
	}

	@Override
	public void update() {
		waitedCycles++;

		if (waitedCycles > 40) {
			if (unload(robot, robot.getDockingStation(), true) == 0) {
				terminate();
			} else {
				setSuccess(true);
			}
		}
	}

	public static int unload(EntityRobotBase robot, DockingStation station, boolean doUnload) {
		if (station == null) {
			return 0;
		}

		if (!ActionRobotFilter.canInteractWithFluid(station,
				new SimpleFluidFilter(robot.getTankInfo(ForgeDirection.UNKNOWN)[0].fluid),
				ActionStationAcceptFluids.class)) {
			return 0;
		}

		IFluidHandler fluidHandler = station.getFluidOutput();
		if (fluidHandler == null) {
			return 0;
		}

		FluidStack drainable = robot.drain(ForgeDirection.UNKNOWN,
				FluidContainerRegistry.BUCKET_VOLUME, false);
		if (drainable == null) {
			return 0;
		}

		drainable = drainable.copy();
		int filled = fluidHandler.fill(station.getFluidOutputSide(), drainable, doUnload);

		if (filled > 0 && doUnload) {
			drainable.amount = filled;
			robot.drain(ForgeDirection.UNKNOWN, drainable, true);
		}
		return filled;
	}

	@Override
	public int getEnergyCost() {
		return 10;
	}
}
