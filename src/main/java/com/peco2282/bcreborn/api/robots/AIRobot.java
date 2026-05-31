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
package com.peco2282.bcreborn.api.robots;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class AIRobot {
  public EntityRobotBase robot;

  private AIRobot delegateAI;
  private AIRobot parentAI;
  private boolean success;

  public AIRobot(EntityRobotBase iRobot) {
    robot = iRobot;
    success = true;
  }

  public static AIRobot loadAI(CompoundTag nbt, EntityRobotBase robot) {
    AIRobot ai = null;

    try {
      Class<?> aiRobotClass;
      if (nbt.contains("class")) {
        aiRobotClass = RobotManager.getAIRobotByLegacyClassName(nbt.getString("class"));
      } else {
        aiRobotClass = RobotManager.getAIRobotByName(nbt.getString("aiName"));
      }
      if (aiRobotClass != null) {
        ai = (AIRobot) aiRobotClass.getConstructor(EntityRobotBase.class).newInstance(robot);
        ai.loadFromNBT(nbt);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return ai;
  }

  public void start() {
  }

  public void preempt(AIRobot ai) {
  }

  public void update() {
    terminate();
  }

  public void end() {
  }

  public void delegateAIEnded(AIRobot ai) {
  }

  public void delegateAIAborted(AIRobot ai) {
  }

  public void writeSelfToNBT(CompoundTag nbt) {
  }

  public void loadSelfFromNBT(CompoundTag nbt) {
  }

  public boolean success() {
    return success;
  }

  protected void setSuccess(boolean iSuccess) {
    success = iSuccess;
  }

  public int getEnergyCost() {
    return 1;
  }

  public boolean canLoadFromNBT() {
    return false;
  }

  public ItemStack receiveItem(ItemStack stack) {
    return stack;
  }

  public final void terminate() {
    abortDelegateAI();
    end();

    if (parentAI != null) {
      parentAI.delegateAI = null;
      parentAI.delegateAIEnded(this);
    }
  }

  public final void abort() {
    abortDelegateAI();

    try {
      end();

      if (parentAI != null) {
        parentAI.delegateAI = null;
        parentAI.delegateAIAborted(this);
      }
    } catch (Throwable e) {
      e.printStackTrace();
      delegateAI = null;

      if (parentAI != null) {
        parentAI.delegateAI = null;
      }
    }
  }

  public final void cycle() {
    try {
      preempt(delegateAI);

      if (delegateAI != null) {
        delegateAI.cycle();
      } else {
        robot.getBattery().extractEnergy(getEnergyCost(), false);
        update();
      }
    } catch (Throwable e) {
      e.printStackTrace();
      abort();
    }
  }

  public final void startDelegateAI(AIRobot ai) {
    abortDelegateAI();
    delegateAI = ai;
    ai.parentAI = this;
    delegateAI.start();
  }

  public final void abortDelegateAI() {
    if (delegateAI != null) {
      delegateAI.abort();
    }
  }

  public final AIRobot getActiveAI() {
    if (delegateAI != null) {
      return delegateAI.getActiveAI();
    } else {
      return this;
    }
  }

  public final AIRobot getDelegateAI() {
    return delegateAI;
  }

  public final void writeToNBT(CompoundTag nbt) {
    nbt.putString("aiName", RobotManager.getAIRobotName(getClass()));

    CompoundTag data = new CompoundTag();
    writeSelfToNBT(data);
    nbt.put("data", data);

    if (delegateAI != null && delegateAI.canLoadFromNBT()) {
      CompoundTag sub = new CompoundTag();
      delegateAI.writeToNBT(sub);
      nbt.put("delegateAI", sub);
    }
  }

  public final void loadFromNBT(CompoundTag nbt) {
    loadSelfFromNBT(nbt.getCompound("data"));

    if (nbt.contains("delegateAI")) {
      CompoundTag sub = nbt.getCompound("delegateAI");

      try {
        Class<?> aiRobotClass;
        if (sub.contains("class")) {
          aiRobotClass = RobotManager.getAIRobotByLegacyClassName(sub.getString("class"));
        } else {
          aiRobotClass = RobotManager.getAIRobotByName(sub.getString("aiName"));
        }
        if (aiRobotClass != null) {
          delegateAI = (AIRobot) aiRobotClass.getConstructor(EntityRobotBase.class).newInstance(robot);
          delegateAI.parentAI = this;

          if (delegateAI.canLoadFromNBT()) {
            delegateAI.loadFromNBT(sub);
          }
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }
}
