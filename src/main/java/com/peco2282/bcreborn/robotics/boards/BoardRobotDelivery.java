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

import com.peco2282.bcreborn.api.StackHelper;
import com.peco2282.bcreborn.api.robots.DockingStation;
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import com.peco2282.bcreborn.common.inventory.filters.PassThroughStackFilter;
import com.peco2282.bcreborn.robotics.ai.AIRobotDeliverRequested;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoStationAndLoad;
import com.peco2282.bcreborn.robotics.ai.AIRobotSearchStackRequest;
import com.peco2282.bcreborn.robotics.statements.ActionRobotFilter;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobot;
import com.peco2282.bcreborn.api.boards.RedstoneBoardRobotNBT;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import com.peco2282.bcreborn.robotics.RoboticsAIType;
import com.peco2282.bcreborn.robotics.RoboticsRedstoneRobots;
import com.peco2282.bcreborn.robotics.ai.AIRobotDisposeItems;
import com.peco2282.bcreborn.robotics.ai.AIRobotGotoSleep;
import com.peco2282.bcreborn.robotics.station.StackRequest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class BoardRobotDelivery extends RedstoneBoardRobot<BoardRobotDelivery> {

  private final ArrayList<ItemStack> deliveryBlacklist = new ArrayList<>();

  private StackRequest currentRequest = null;

  public BoardRobotDelivery(RobotEntityBase iRobot) {
    super(RoboticsAIType.DELIVERY, iRobot);
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return RoboticsRedstoneRobots.ROBOT_DELIVERY.get();
  }

  @Override
  public void update() {
    if (robot.containsItems()) {
      startDelegateAI(new AIRobotDisposeItems(robot));
      return;
    }

    if (currentRequest == null) {
      IStackFilter gateFilter = robot.getLinkedStation() == null ? new PassThroughStackFilter()
        : ActionRobotFilter.getGateFilter(robot.getLinkedStation());
      startDelegateAI(new AIRobotSearchStackRequest(robot, gateFilter, deliveryBlacklist));
    } else {
      startDelegateAI(new AIRobotGotoStationAndLoad(robot,
        stack -> currentRequest != null && StackHelper.isMatchingItemOrList(stack, currentRequest.getStack()),
        currentRequest.getStack().getCount()));
    }
  }

  @Override
  public void delegateAIEnded(AIRobot<?> ai) {
    if (ai instanceof AIRobotSearchStackRequest search) {
      if (!ai.success()) {
        deliveryBlacklist.clear();
        startDelegateAI(new AIRobotGotoSleep(robot));
      } else {
        currentRequest = search.request;
        DockingStation<?> station = currentRequest.getStation(robot.level());
        if (station == null || !station.take(robot)) {
          releaseCurrentRequest();
        }
      }
    } else if (ai instanceof AIRobotGotoStationAndLoad) {
      if (!ai.success()) {
        if (currentRequest != null) {
          deliveryBlacklist.add(currentRequest.getStack());
        }
        releaseCurrentRequest();
      } else {
        startDelegateAI(new AIRobotDeliverRequested(robot, currentRequest));
      }
    } else if (ai instanceof AIRobotDeliverRequested) {
      releaseCurrentRequest();
    }
  }

  private void releaseCurrentRequest() {
    if (currentRequest != null) {
      robot.getRegistry().release(currentRequest.getResourceId(robot.level()));
      DockingStation<?> station = currentRequest.getStation(robot.level());
      if (station != null) {
        station.release(robot);
      }
      currentRequest = null;
    }
  }

  @Override
  public boolean canLoadFromNBT() {
    return true;
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);
    if (currentRequest != null) {
      CompoundTag requestNBT = new CompoundTag();
      currentRequest.writeToNBT(requestNBT);
      nbt.put("currentRequest", requestNBT);
    }
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);
    if (nbt.contains("currentRequest")) {
      currentRequest = StackRequest.loadFromNBT(nbt.getCompound("currentRequest"));
    }
  }
}
