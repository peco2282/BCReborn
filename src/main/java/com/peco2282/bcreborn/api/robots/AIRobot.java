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

/**
 * Base class for all robot AI behaviors.
 * <p>
 * This class defines the lifecycle and execution logic for robot AIs. AIs can be stacked,
 * allowing a parent AI to delegate tasks to a sub-AI (delegate).
 */
public class AIRobot {
  /**
   * The robot entity this AI is controlling.
   */
  public RobotEntityBase robot;

  private AIRobot delegateAI;
  private AIRobot parentAI;
  private boolean success;

  /**
   * Constructs a new AIRobot instance for the given robot.
   *
   * @param iRobot The robot entity this AI will control.
   */
  public AIRobot(RobotEntityBase iRobot) {
    robot = iRobot;
    success = true;
  }

  /**
   * Loads an AI instance from NBT data.
   *
   * @param nbt   The NBT data containing AI information.
   * @param robot The robot entity the loaded AI will control.
   * @return A new AIRobot instance, or {@code null} if loading failed.
   */
  public static AIRobot loadAI(CompoundTag nbt, RobotEntityBase robot) {
    AIRobot ai = null;

    try {
      Class<?> aiRobotClass;
      if (nbt.contains("class")) {
        aiRobotClass = RobotManager.getAIRobotByLegacyClassName(nbt.getString("class"));
      } else {
        aiRobotClass = RobotManager.getAIRobotByName(nbt.getString("aiName"));
      }
      if (aiRobotClass != null) {
        ai = (AIRobot) aiRobotClass.getConstructor(RobotEntityBase.class).newInstance(robot);
        ai.loadFromNBT(nbt);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return ai;
  }

  /**
   * Called when the AI starts execution.
   */
  public void start() {
  }

  /**
   * Called before each cycle to allow preemption of the current delegate AI.
   *
   * @param ai The current delegate AI.
   */
  public void preempt(AIRobot ai) {
  }

  /**
   * Main update logic for this AI. Called every cycle if no delegate AI is active.
   */
  public void update() {
    terminate();
  }

  /**
   * Called when the AI ends normally or is aborted.
   */
  public void end() {
  }

  /**
   * Called when a delegate AI has ended normally.
   *
   * @param ai The delegate AI that ended.
   */
  public void delegateAIEnded(AIRobot ai) {
  }

  /**
   * Called when a delegate AI has been aborted.
   *
   * @param ai The delegate AI that was aborted.
   */
  public void delegateAIAborted(AIRobot ai) {
  }

  /**
   * Writes AI-specific data to NBT.
   *
   * @param nbt The NBT tag to write to.
   */
  public void writeSelfToNBT(CompoundTag nbt) {
  }

  /**
   * Loads AI-specific data from NBT.
   *
   * @param nbt The NBT tag to load from.
   */
  public void loadSelfFromNBT(CompoundTag nbt) {
  }

  /**
   * Checks if the AI completed successfully.
   *
   * @return {@code true} if successful, {@code false} otherwise.
   */
  public boolean success() {
    return success;
  }

  /**
   * Sets the success state of this AI.
   *
   * @param iSuccess The success state.
   */
  protected void setSuccess(boolean iSuccess) {
    success = iSuccess;
  }

  /**
   * Returns the energy cost per cycle for this AI.
   *
   * @return The energy cost.
   */
  public int getEnergyCost() {
    return 1;
  }

  /**
   * Checks if this AI can be persisted to NBT.
   *
   * @return {@code true} if persistent, {@code false} otherwise.
   */
  public boolean canLoadFromNBT() {
    return false;
  }

  /**
   * Called when the robot receives an item while this AI is active.
   *
   * @param stack The item stack received.
   * @return The remaining item stack after the AI has processed it.
   */
  public ItemStack receiveItem(ItemStack stack) {
    return stack;
  }

  /**
   * Terminates this AI and notifies the parent AI.
   */
  public final void terminate() {
    abortDelegateAI();
    end();

    if (parentAI != null) {
      parentAI.delegateAI = null;
      parentAI.delegateAIEnded(this);
    }
  }

  /**
   * Aborts this AI and notifies the parent AI.
   */
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

  /**
   * Executes one cycle of this AI or its delegate AI.
   */
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

  /**
   * Starts a new delegate AI.
   *
   * @param ai The AI to delegate to.
   */
  public final void startDelegateAI(AIRobot ai) {
    abortDelegateAI();
    delegateAI = ai;
    ai.parentAI = this;
    delegateAI.start();
  }

  /**
   * Aborts the current delegate AI.
   */
  public final void abortDelegateAI() {
    if (delegateAI != null) {
      delegateAI.abort();
    }
  }

  /**
   * Returns the currently active AI in the delegation stack.
   *
   * @return The active AIRobot instance.
   */
  public final AIRobot getActiveAI() {
    if (delegateAI != null) {
      return delegateAI.getActiveAI();
    } else {
      return this;
    }
  }

  /**
   * Returns the current delegate AI.
   *
   * @return The delegate AIRobot instance, or {@code null} if none.
   */
  public final AIRobot getDelegateAI() {
    return delegateAI;
  }

  /**
   * Writes this AI and its delegate AI (if persistent) to NBT.
   *
   * @param nbt The NBT tag to write to.
   */
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

  /**
   * Loads this AI and its delegate AI from NBT.
   *
   * @param nbt The NBT tag to load from.
   */
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
          delegateAI = (AIRobot) aiRobotClass.getConstructor(RobotEntityBase.class).newInstance(robot);
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
