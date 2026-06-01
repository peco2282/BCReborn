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
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.api.robots.ResourceIdBlock;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

public class AIRobotGotoStation extends AIRobot {

  private BlockIndex stationIndex;
  private Direction stationSide;

  public AIRobotGotoStation(RobotEntityBase iRobot) {
    super(iRobot);
  }

  public AIRobotGotoStation(RobotEntityBase iRobot, DockingStation station) {
    this(iRobot);

    stationIndex = station.index();
    stationSide = station.side();
    setSuccess(false);
  }

  @Override
  public void start() {
    DockingStation station = robot.getRegistry().getStation(stationIndex.toBlockPos(), stationSide);

    if (station == null) {
      terminate();
    } else if (station == robot.getDockingStation()) {
      setSuccess(true);
      terminate();
    } else {
      ResourceIdBlock resourceId = new ResourceIdBlock(station.index().toBlockPos());
      resourceId.side = station.side();
      if (robot.getRegistry().take(resourceId, robot)) {
        startDelegateAI(new AIRobotGotoBlock(robot,
          station.x() + station.side().getStepX(),
          station.y() + station.side().getStepY(),
          station.z() + station.side().getStepZ()));
      } else {
        terminate();
      }
    }
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    DockingStation station = robot.getRegistry().getStation(stationIndex.toBlockPos(), stationSide);

    if (station == null) {
      terminate();
    } else if (ai instanceof AIRobotGotoBlock) {
      if (ai.success()) {
        startDelegateAI(new AIRobotStraightMoveTo(robot,
          stationIndex.x + 0.5F + stationSide.getStepX() * 0.5F,
          stationIndex.y + 0.5F + stationSide.getStepY() * 0.5F,
          stationIndex.z + 0.5F + stationSide.getStepZ() * 0.5F));
      } else {
        terminate();
      }
    } else {
      setSuccess(true);
      if (stationSide.getStepY() == 0) {
        robot.aimItemAt(stationIndex.x + 2 * stationSide.getStepX(), stationIndex.y,
          stationIndex.z + 2 * stationSide.getStepZ());
      } else {
        robot.aimItemAt(Mth.floor(robot.getAimYaw() / 90f) * 90f + 180f, robot.getAimPitch());
      }
      robot.dock(station);
      terminate();
    }
  }

  @Override
  public boolean canLoadFromNBT() {
    return true;
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    CompoundTag indexNBT = new CompoundTag();
    stationIndex.writeTo(indexNBT);
    nbt.put("stationIndex", indexNBT);
    nbt.putByte("stationSide", (byte) stationSide.ordinal());
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    stationIndex = new BlockIndex(nbt.getCompound("stationIndex"));
    stationSide = Direction.values()[nbt.getByte("stationSide")];
  }
}
