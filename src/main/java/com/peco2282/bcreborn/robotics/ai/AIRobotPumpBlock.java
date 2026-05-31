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

import com.peco2282.bcreborn.api.core.BlockIndex;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class AIRobotPumpBlock extends AIRobot {

  private BlockIndex blockToPump;
  private long waited = 0;

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
      FluidStack fluidStack = BlockUtils.drainBlock(robot.level(), blockToPump.toBlockPos(), false);
      if (fluidStack != null) {
        if (robot.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE) > 0) {
          BlockUtils.drainBlock(robot.level(), blockToPump.toBlockPos(), true);
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
    int pumped = 0;
    return pumped > 0;
  }
}
