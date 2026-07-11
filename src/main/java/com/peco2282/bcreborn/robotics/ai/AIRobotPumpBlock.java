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
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.common.utils.BlockUtils;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class AIRobotPumpBlock extends AIRobot<AIRobotPumpBlock> {

  private BlockPos blockToPump;
  private long waited = 0;

  public AIRobotPumpBlock(RobotEntityBase iRobot) {
    super(RoboticsAIType.PUMP_BLOCK, iRobot);
  }

  public AIRobotPumpBlock(RobotEntityBase iRobot, BlockPos iBlockToPump) {
    this(iRobot);

    blockToPump = iBlockToPump;
  }

  @Override
  public void start() {
    robot.aimItemAt(blockToPump.getX(), blockToPump.getY(), blockToPump.getZ());
  }

  @Override
  public void preempt(AIRobot<?> ai) {
    super.preempt(ai);
  }

  @Override
  public void update() {
    if (waited < 40) {
      waited++;
    } else {
      FluidStack fluidStack = BlockUtils.drainBlock(robot.level(), blockToPump, false);
      if (!fluidStack.isEmpty()) {
        if (robot.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE) > 0) {
          BlockUtils.drainBlock(robot.level(), blockToPump, true);
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
    return false;
  }
}
