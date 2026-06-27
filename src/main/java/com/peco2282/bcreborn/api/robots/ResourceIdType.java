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
 * Registry type for resource identifiers used by the robot system.
 * Each ResourceIdType represents a specific category of resources that robots can interact with,
 * providing a factory for creating instances of the corresponding ResourceId implementation.
 *
 * @param <T>     the type of ResourceId this type produces
 * @param id      the unique ResourceLocation identifier for this type
 * @param factory the supplier that creates new instances of the ResourceId
 */
public record ResourceIdType<T extends ResourceId<T>>(ResourceLocation id, Supplier<@NotNull T> factory) {
  /**
   * Creates and registers a new ResourceIdType with the RobotManager.
   *
   * @param id the unique identifier for this resource type
   * @param factory the factory method for creating instances
   * @param <T> the type of ResourceId
   * @return the registered ResourceIdType instance
   */
  private static <T extends ResourceId<T>> ResourceIdType<T> of(ResourceLocation id, Supplier<T> factory) {
    return RobotManager.registerResourceIdType(new ResourceIdType<>(id, factory));
  }

  /**
   * Creates a new instance of the ResourceId using the configured factory.
   *
   * @return a new ResourceId instance
   */
  public T create() {
    return factory.get();
  }

  /**
   * Resource ID type for block-based resources.
   * Used when robots need to identify and interact with specific blocks in the world.
   */
  public static final ResourceIdType<ResourceIdBlock> BLOCK = of(ResourceLocation.fromNamespaceAndPath("bcreborncore", "resource_id_block"), ResourceIdBlock::new);

  /**
   * Resource ID type for resource requests.
   * Used when robots need to track and fulfill specific resource requirements or deliveries.
   */
  public static final ResourceIdType<ResourceIdRequest> REQUEST = of(ResourceLocation.fromNamespaceAndPath("bcreborncore", "resource_id_request"), ResourceIdRequest::new);
}
