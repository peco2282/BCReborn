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
import com.peco2282.bcreborn.common.inventory.filters.IFluidFilter;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import com.peco2282.bcreborn.robotics.statements.ActionStationProvideFluids;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class AIRobotLoadFluids extends AIRobot {

  private int waitedCycles = 0;
  private IFluidFilter filter;

  public AIRobotLoadFluids(RobotEntityBase iRobot) {
    super(iRobot);
  }

  public AIRobotLoadFluids(RobotEntityBase iRobot, IFluidFilter iFilter) {
    this(iRobot);

    filter = iFilter;
    setSuccess(false);
  }

  public static int load(RobotEntityBase robot, DockingStation station, IFluidFilter filter,
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

  @Override
  public int getEnergyCost() {
    return 8;
  }

}
