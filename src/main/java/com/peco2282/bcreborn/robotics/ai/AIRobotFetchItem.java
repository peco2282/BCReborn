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

import com.peco2282.bcreborn.api.core.IZone;
import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.EntityRobotBase;
import com.peco2282.bcreborn.common.inventory.filters.IStackFilter;
import com.peco2282.bcreborn.robotics.boards.BoardRobotPicker;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AIRobotFetchItem extends AIRobot {

  private ItemEntity target;

  private float maxRange;
  private IStackFilter stackFilter;
  private int pickTime = -1;
  private IZone zone;

  public AIRobotFetchItem(EntityRobotBase iRobot) {
    super(iRobot);
  }

  public AIRobotFetchItem(EntityRobotBase iRobot, float iMaxRange, IStackFilter iStackFilter, IZone iZone) {
    this(iRobot);

    maxRange = iMaxRange;
    stackFilter = iStackFilter;
    zone = iZone;
  }

  @Override
  public void preempt(AIRobot ai) {
    if (target != null && target.isRemoved()) {
      terminate();
    }
  }

  @Override
  public void update() {
    if (target == null) {
      scanForItem();
    } else {
      pickTime++;

      if (pickTime > 5) {
        ItemStack stack = target.getItem();
        ItemStack remaining = robot.receiveItem(null, stack);
        target.setItem(remaining);

        if (remaining.isEmpty()) {
          target.discard();
        }

        terminate();
      }
    }
  }

  @Override
  public void delegateAIEnded(AIRobot ai) {
    if (ai instanceof AIRobotGotoBlock) {
      if (target == null) {
        // This would happen after a load. As we reached the item
        // location already, just consider that the item is not there
        // anymore and allow user to try to find another one.
        setSuccess(false);
        terminate();
        return;
      }

      if (!ai.success()) {
        robot.unreachableEntityDetected(target);
        setSuccess(false);
        terminate();
      }
    }
  }

  @Override
  public void end() {
    if (target != null) {
      BoardRobotPicker.targettedItems.remove(target.getId());
    }
  }

  private void scanForItem() {
    double previousDistance = Double.MAX_VALUE;

    AABB area = robot.getBoundingBox().inflate(maxRange);
    List<ItemEntity> items = robot.level().getEntitiesOfClass(ItemEntity.class, area);

    for (ItemEntity e : items) {
      if (!e.isRemoved()
        && !BoardRobotPicker.targettedItems.contains(e.getId())
        && !robot.isKnownUnreachable(e)
        && (zone == null || zone.contains(e.getX(), e.getY(), e.getZ()))) {
        double dx = e.getX() - robot.getX();
        double dy = e.getY() - robot.getY();
        double dz = e.getZ() - robot.getZ();

        double sqrDistance = dx * dx + dy * dy + dz * dz;
        double maxDistance = maxRange * maxRange;

        if (sqrDistance >= maxDistance) {
          continue;
        } else if (stackFilter != null && !stackFilter.matches(e.getItem())) {
          continue;
        } else {
          if (robot.hasFreeSlot()) {
            if (target == null) {
              previousDistance = sqrDistance;
              target = e;
            } else {
              if (sqrDistance < previousDistance) {
                previousDistance = sqrDistance;
                target = e;
              }
            }
          }
        }
      }
    }

    if (target != null) {
      BoardRobotPicker.targettedItems.add(target.getId());
      if (Mth.floor(target.getX()) != Mth.floor(robot.getX()) || Mth.floor(target.getY()) != Mth.floor(robot.getY())
        || Mth.floor(target.getZ()) != Mth.floor(robot.getZ())) {
        startDelegateAI(new AIRobotGotoBlock(robot, Mth.floor(target.getX()),
          Mth.floor(target.getY()), Mth.floor(target.getZ())));
      }
    } else {
      // No item was found, terminate this AI
      setSuccess(false);
      terminate();
    }
  }

  @Override
  public int getEnergyCost() {
    return 15;
  }
}
