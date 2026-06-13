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

import com.peco2282.bcreborn.api.robots.RobotEntityBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

/**
 * Abstract base class for creating redstone boards for robots using NBT data.
 * <p>
 * This class extends {@link RedstoneBoardNBT} and provides robot-specific functionality,
 * including texture resource management and board creation.
 */
public abstract class RedstoneBoardRobotNBT extends RedstoneBoardNBT<RobotEntityBase> {

  /**
   * Creates a redstone board for the given robot using NBT data.
   * <p>
   * This implementation delegates to {@link #create(RobotEntityBase)},
   * ignoring the NBT parameter.
   *
   * @param nbt   The NBT data (currently unused).
   * @param robot The robot entity for which to create the board.
   * @return A new {@link RedstoneBoardRobot} instance.
   */
  @Override
  public RedstoneBoardRobot create(CompoundTag nbt, RobotEntityBase robot) {
    return create(robot);
  }

  /**
   * Creates a redstone board for the given robot.
   *
   * @param robot The robot entity for which to create the board.
   * @return A new {@link RedstoneBoardRobot} instance.
   */
  public abstract RedstoneBoardRobot create(RobotEntityBase robot);

  /**
   * Gets the texture resource location for the robot.
   *
   * @return The {@link ResourceLocation} pointing to the robot's texture.
   */
  public abstract ResourceLocation getRobotTexture();
}
