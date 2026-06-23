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

public record ResourceIdType<T extends ResourceId<T>>(ResourceLocation id, Supplier<@NotNull T> factory) {
  private static <T extends ResourceId<T>> ResourceIdType<T> of(ResourceLocation id, Supplier<T> factory) {
    return RobotManager.registerResourceIdType(new ResourceIdType<>(id, factory));
  }

  public T create() {
    return factory.get();
  }

  public static final ResourceIdType<ResourceIdBlock> BLOCK = of(ResourceLocation.fromNamespaceAndPath("bcreborncore", "resource_id_block"), ResourceIdBlock::new);
  public static final ResourceIdType<ResourceIdRequest> REQUEST = of(ResourceLocation.fromNamespaceAndPath("bcreborncore", "resource_id_request"), ResourceIdRequest::new);
}
