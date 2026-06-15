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
package com.peco2282.bcreborn.api.boards;

import com.peco2282.bcreborn.api.robots.AIRobot;
import com.peco2282.bcreborn.api.robots.AIRobotType;
import com.peco2282.bcreborn.api.robots.RobotEntityBase;

/**
 * Abstract base class for redstone-controlled robot boards.
 * <p>
 * This class extends {@link AIRobot} and implements {@link IRedstoneBoard} to provide
 * a foundation for creating AI behaviors that can be controlled via redstone signals.
 * Subclasses must implement {@link #getNBTHandler()} to provide NBT serialization support.
 */
public abstract class RedstoneBoardRobot<T extends RedstoneBoardRobot<T>> extends AIRobot<T> implements IRedstoneBoard<RobotEntityBase> {

  /**
   * The robot entity that this board controls.
   */
  protected final RobotEntityBase robot;

  /**
   * Creates a new redstone board for the specified robot.
   *
   * @param robot The robot entity that this board will control.
   */
  public RedstoneBoardRobot(AIRobotType<T> type, RobotEntityBase robot) {
    super(type, robot);
    this.robot = robot;
  }

  /**
   * Gets the NBT handler for this redstone board.
   * <p>
   * The NBT handler is responsible for serializing and deserializing the board's state.
   *
   * @return The NBT handler instance for this board.
   */
  @Override
  public abstract RedstoneBoardRobotNBT getNBTHandler();

  /**
   * Updates the board's state based on the robot container.
   * <p>
   * This method is called periodically to allow the board to update its behavior.
   * The current implementation is empty and final, preventing subclasses from overriding it.
   *
   * @param container The robot entity container to update from.
   */
  @Override
  public final void updateBoard(RobotEntityBase container) {

  }

  /**
   * Determines whether this board can be loaded from NBT data.
   *
   * @return True, indicating that this board supports loading from NBT.
   */
  @Override
  public boolean canLoadFromNBT() {
    return true;
  }
}
