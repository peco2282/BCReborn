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
package com.peco2282.bcreborn.api.transport.pluggable;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public record PluggableType<T extends PipePluggable<T>>(
  ResourceLocation id,
  Supplier<T> factory
) {
  public static <T extends PipePluggable<T>> PluggableType<T> of(ResourceLocation id, Supplier<T> factory) {
    return new PluggableType<>(id, factory);
  }

  public T create() {
    return factory.get();
  }
}
