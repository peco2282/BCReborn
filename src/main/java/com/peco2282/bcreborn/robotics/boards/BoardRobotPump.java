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
import com.peco2282.bcreborn.api.core.BuildCraftAPI;
import com.peco2282.bcreborn.api.core.IWorldProperty;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.ResourceIdBlock;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndUnloadFluids;
import com.peco2282.bcreborn.robotics.ai.AIRobotPumpBlock;
import com.peco2282.bcreborn.robotics.ai.AIRobotSearchAndGotoBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class BoardRobotPump extends RedstoneBoardRobot<BoardRobotPump> {

  private BlockPos blockFound;

  public BoardRobotPump(RobotEntityBase iRobot) {
    super(RoboticsAIType.PUMP, iRobot);
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return RoboticsRedstoneRobots.ROBOT_PUMP.get();
  }

  @Override
  public void update() {
    final IWorldProperty isFluidSource = BuildCraftAPI.getWorldProperty("fluidSource");
    // Simplified check for fluid in tank
    boolean hasFluid = false; // TODO: Implement proper check

    //noinspection ConstantValue
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
  public void delegateAIEnded(AIRobot<?> ai) {
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
      robot.getRegistry().release(new ResourceIdBlock(blockFound));
      blockFound = null;
    }
  }

  private boolean matchesGateFilter(Level world, BlockPos pos) {
    return true; // Simplified for now
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);
    if (blockFound != null) {
      nbt.putLong("blockFound", blockFound.asLong());
    }
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    if (nbt.contains("blockFound")) {
      blockFound = BlockPos.of(nbt.getLong("blockFound"));
    }
  }
}
