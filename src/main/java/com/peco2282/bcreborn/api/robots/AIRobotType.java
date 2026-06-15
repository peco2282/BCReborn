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

public record AIRobotType<T extends AIRobot<T>>(
  ResourceLocation id,
  Function<RobotEntityBase, @NotNull T> factory
) {
  public static <T extends AIRobot<T>> AIRobotType<T> of(ResourceLocation id, Function<RobotEntityBase, T> factory) {
    return new AIRobotType<>(id, factory);
  }

  @NotNull
  public T create(RobotEntityBase entity) {
    return factory.apply(entity);
  }
}
