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
import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.builders.block.entity.ConstructionMarkerBlockEntity;
import com.peco2282.bcreborn.common.builder.BuildingItem;
import com.peco2282.bcreborn.common.builder.BuildingSlot;
import com.peco2282.bcreborn.common.inventory.filters.ArrayStackFilter;
import com.peco2282.bcreborn.robotics.ai.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.LinkedList;

public class BoardRobotBuilder extends RedstoneBoardRobot {

  private static final int MAX_RANGE_SQ = 3 * 64 * 64;

  private ConstructionMarkerBlockEntity markerToBuild;
  private BuildingSlot currentBuildingSlot;
  private LinkedList<ItemStack> requirementsToLookFor;
  private int launchingDelay = 0;

  public BoardRobotBuilder(EntityRobotBase iRobot) {
    super(iRobot);
  }

  @Override
  public RedstoneBoardRobotNBT getNBTHandler() {
    return BCBoardNBT.REGISTRY.get("builder");
  }

  @Override
  public void update() {
    if (launchingDelay > 0) {
      launchingDelay--;
      return;
    }

    if (markerToBuild == null) {
      markerToBuild = findClosestMarker();

      if (markerToBuild == null) {
        if (robot.containsItems()) {
          startDelegateAI(new AIRobotDisposeItems(robot));
        } else {
          startDelegateAI(new AIRobotGotoSleep(robot));
        }
        return;
      }
    }

    if (!markerToBuild.needsToBuild()) {
      markerToBuild = null;
      currentBuildingSlot = null;
      return;
    }

    if (currentBuildingSlot == null) {
      currentBuildingSlot = markerToBuild.bluePrintBuilder.reserveNextSlot(robot.level());

      if (currentBuildingSlot == null) {
        // No slots available yet
        launchingDelay = 40;
        return;
      }

    }

    if (requirementsToLookFor == null) {
      if (robot.containsItems()) {
        startDelegateAI(new AIRobotDisposeItems(robot));
      }
      MinecraftServer server = robot.level().getServer();
      GameType gameType = server.getForcedGameType() == null ? server.getDefaultGameType() : server.getForcedGameType();

      if (gameType.isCreative()) {
        requirementsToLookFor = currentBuildingSlot.getRequirements(markerToBuild
          .getContext());
      } else {
        requirementsToLookFor = new LinkedList<>();
      }

      if (requirementsToLookFor == null) {
        launchingDelay = 40;
        return;
      }

      if (requirementsToLookFor.size() > 4) {
        currentBuildingSlot.built = true;
        currentBuildingSlot = null;
        requirementsToLookFor = null;
        return;
      }
    }

    if (!requirementsToLookFor.isEmpty()) {
      startDelegateAI(new AIRobotGotoStationAndLoad(robot, new ArrayStackFilter(
        requirementsToLookFor.getFirst()), requirementsToLookFor.getFirst().getCount()));
      return;
    }

    if (requirementsToLookFor.isEmpty()) {
      if (currentBuildingSlot.stackConsumed == null) {
        // Once all the element are in, if not already, use them to
        // prepare the slot.
        markerToBuild.bluePrintBuilder.useRequirements(robot, currentBuildingSlot);
      }

      if (!hasEnoughEnergy()) {
        startDelegateAI(new AIRobotRecharge(robot));
      } else {
        startDelegateAI(new AIRobotGotoBlock(robot,
          (int) currentBuildingSlot.getDestination().x,
          (int) currentBuildingSlot.getDestination().y,
          (int) currentBuildingSlot.getDestination().z,
          8));
      }
      // TODO: take into account cases where the robot can't reach the
      // destination - go to work on another block
    }
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotGotoStationAndLoad) {
      if (ai.success()) {
        requirementsToLookFor.removeFirst();
      } else {
        startDelegateAI(new AIRobotGotoSleep(robot));
      }
    } else if (ai instanceof AIRobotGotoBlock) {
      if (markerToBuild == null || markerToBuild.bluePrintBuilder == null) {
        // defensive code, in case of a wrong load from NBT
        return;
      }

      if (!hasEnoughEnergy()) {
        startDelegateAI(new AIRobotRecharge(robot));
        return;
      }

      robot.getBattery().extractEnergy(currentBuildingSlot.getEnergyRequirement(), false);
      launchingDelay = currentBuildingSlot.getStacksToDisplay().size()
        * BuildingItem.ITEMS_SPACE;
      markerToBuild.bluePrintBuilder.buildSlot(robot.level(), markerToBuild,
        currentBuildingSlot, robot.getX() + 0.125F, robot.getY() + 0.125F,
        robot.getZ() + 0.125F);
      currentBuildingSlot = null;
      requirementsToLookFor = null;
    }
  }

  @Override
  public void writeSelfToNBT(CompoundTag nbt) {
    super.writeSelfToNBT(nbt);

    nbt.putInt("launchingDelay", launchingDelay);
  }

  @Override
  public void loadSelfFromNBT(CompoundTag nbt) {
    super.loadSelfFromNBT(nbt);

    launchingDelay = nbt.getInt("launchingDelay");
  }

  private ConstructionMarkerBlockEntity findClosestMarker() {
    double minDistance = Double.MAX_VALUE;
    ConstructionMarkerBlockEntity minMarker = null;

    IZone zone = robot.getZoneToWork();

    for (ConstructionMarkerBlockEntity marker : ConstructionMarkerBlockEntity.currentMarkers) {
      if (marker.getLevel() != robot.level()) {
        continue;
      }
      if (!marker.needsToBuild()) {
        continue;
      }
      if (zone != null && !zone.contains(marker.getBlockPos())) {
        continue;
      }

      double dx = robot.getX() - marker.getBlockPos().getX();
      double dy = robot.getY() - marker.getBlockPos().getY();
      double dz = robot.getZ() - marker.getBlockPos().getZ();
      double distance = dx * dx + dy * dy + dz * dz;

      if (distance < minDistance) {
        minMarker = marker;
        minDistance = distance;
      }
    }

    if (minMarker != null && minDistance < MAX_RANGE_SQ) {
      return minMarker;
    } else {
      return null;
    }
  }

  private boolean hasEnoughEnergy() {
    return robot.getEnergy() - currentBuildingSlot.getEnergyRequirement() > EntityRobotBase.SAFETY_ENERGY;
  }

}
