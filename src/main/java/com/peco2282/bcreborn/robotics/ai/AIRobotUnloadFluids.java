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
package com.peco2282.bcreborn.robotics.ai;

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.inventory.filters.SimpleFluidFilter;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import com.peco2282.bcreborn.robotics.statements.ActionStationAcceptFluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class AIRobotUnloadFluids extends AIRobot {

  private int waitedCycles = 0;

  public AIRobotUnloadFluids(RobotEntityBase iRobot) {
    super(iRobot);
    setSuccess(false);
  }

  public static int unload(RobotEntityBase robot, DockingStation station, boolean doUnload) {
    if (station == null) {
      return 0;
    }

    if (!ActionRobotFilter.canInteractWithFluid(station,
      new SimpleFluidFilter(robot.getFluidInTank(100)),
      ActionStationAcceptFluids.class)) {
      return 0;
    }

    IFluidHandler fluidHandler = station.getFluidOutput();
    if (fluidHandler == null) {
      return 0;
    }

    FluidStack drainable = robot.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
    if (drainable == FluidStack.EMPTY) {
      return 0;
    }

    drainable = drainable.copy();
    int filled = fluidHandler.fill(drainable, doUnload ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE);

    if (filled > 0 && doUnload) {
      drainable.setAmount(filled);
      robot.drain(drainable, IFluidHandler.FluidAction.EXECUTE);
    }
    return filled;
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

  @Override
  public int getEnergyCost() {
    return 10;
  }
}
