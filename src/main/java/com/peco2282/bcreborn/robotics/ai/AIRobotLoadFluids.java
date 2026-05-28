/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.robotics.ai;

import com.peco2282.bcreborn.common.inventory.filters.IFluidFilter;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import com.peco2282.bcreborn.robotics.statements.ActionStationProvideFluids;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class AIRobotLoadFluids extends AIRobot {

	private int waitedCycles = 0;
	private IFluidFilter filter;

	public AIRobotLoadFluids(EntityRobotBase iRobot) {
		super(iRobot);
	}

	public AIRobotLoadFluids(EntityRobotBase iRobot, IFluidFilter iFilter) {
		this(iRobot);

		filter = iFilter;
		setSuccess(false);
	}

	@Override
	public void update() {
		if (filter == null) {
			terminate();
			return;
		}

		waitedCycles++;

		if (waitedCycles > 40) {
			if (load(robot, robot.getDockingStation(), filter, true) == 0) {
				terminate();
			} else {
				setSuccess(true);
				waitedCycles = 0;
			}
		}
	}

	public static int load(EntityRobotBase robot, DockingStation station, IFluidFilter filter,
						   boolean doLoad) {
		if (station == null) {
			return 0;
		}

		if (!ActionRobotFilter.canInteractWithFluid(station, filter,
				ActionStationProvideFluids.class)) {
			return 0;
		}

		IFluidHandler handler = station.getFluidInput();
		if (handler == null) {
			return 0;
		}

		Direction side = station.getFluidInputSide();

		FluidStack drainable = handler.drain(FluidType.BUCKET_VOLUME,
				IFluidHandler.FluidAction.SIMULATE);
		if (drainable == FluidStack.EMPTY || !filter.matches(drainable.getFluid())) {
			return 0;
		}

		drainable = drainable.copy();
		int filled = robot.fill(drainable, doLoad ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE);

		if (filled > 0 && doLoad) {
			drainable.setAmount(filled);
			handler.drain(drainable, IFluidHandler.FluidAction.EXECUTE);
		}
		return filled;
	}

	@Override
	public int getEnergyCost() {
		return 8;
	}

}
