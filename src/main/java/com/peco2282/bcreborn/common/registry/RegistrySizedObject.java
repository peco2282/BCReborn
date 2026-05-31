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
package com.peco2282.bcreborn.common.registry;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RegistrySizedObject<V> extends Int2ObjectArrayMap<RegistryObject<V>> implements Supplier<RegistryObject<V>> {
  public static <V> RegistrySizedObject<V> create(int size) {
    return new RegistrySizedObject<>(size);
  }

  private RegistrySizedObject(int size) {
    super(size);
  }

  @Override
  public RegistryObject<V> get() {
    return get(0);
  }

  public RegistryObject<V> get(int index) {
    return super.get(index);
  }
}
