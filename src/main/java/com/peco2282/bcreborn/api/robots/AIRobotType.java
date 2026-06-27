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

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Represents a type of AI robot with a unique identifier and factory for creating instances.
 * <p>
 * This record encapsulates the registration information for a robot AI type, including
 * its resource location identifier and a factory function to create instances of the AI.
 *
 * @param <T>     the type of AI robot this type represents
 * @param id      the unique resource location identifier for this robot AI type
 * @param factory a function that creates instances of the AI from a robot entity
 */
public record AIRobotType<T extends AIRobot<T>>(
  ResourceLocation id,
  Function<RobotEntityBase, @NotNull T> factory
) {
  /**
   * Creates a new AI robot type with the specified identifier and factory function.
   *
   * @param <T>     the type of AI robot
   * @param id      the unique resource location identifier
   * @param factory the factory function to create AI instances
   * @return a new AIRobotType instance
   */
  public static <T extends AIRobot<T>> AIRobotType<T> of(ResourceLocation id, Function<RobotEntityBase, T> factory) {
    return new AIRobotType<>(id, factory);
  }

  /**
   * Creates a new AI instance for the given robot entity.
   *
   * @param entity the robot entity to create the AI for
   * @return a new AI instance
   */
  public T create(RobotEntityBase entity) {
    return factory.apply(entity);
  }
}
