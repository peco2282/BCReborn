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

import java.util.function.Supplier;

/**
 * Represents a type of docking station for robots.
 * <p>
 * This record encapsulates the registration information and factory for creating
 * docking station instances. Each type is uniquely identified by a ResourceLocation
 * and provides a factory method to instantiate new docking stations of this type.
 *
 * @param <T> the specific type of DockingStation this type represents
 */
public record DockingStationType<T extends DockingStation<T>>(
  /**
   * The unique identifier for this docking station type.
   */
  ResourceLocation id,
  /**
   * Factory supplier that creates new instances of the docking station.
   */
  Supplier<@NotNull T> factory
) {
  /**
   * Creates a new instance of the docking station.
   *
   * @return a new docking station instance of type T
   */
  public T create() {
    return factory.get();
  }
}
