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

import net.minecraft.util.StringRepresentable;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.function.Supplier;

public class RegistryEnumObject<E extends Enum<E> & StringRepresentable, V> extends EnumMap<E, RegistryObject<V>> implements Supplier<RegistryObject<V>> {
  public static <E extends Enum<E> & StringRepresentable, V> RegistryEnumObject<E, V> create(Class<E> keyType) {
    return new RegistryEnumObject<>(keyType);
  }
  private final Class<E> keyType;
  @Override
  public RegistryObject<V> get() {
    return get(keyType.getEnumConstants()[0]);
  }

  public RegistryObject<V> get(E key) {
    return super.get(key);
  }

  private RegistryEnumObject(Class<E> keyType) {
    super(keyType);
    if (keyType == null) {
      throw new IllegalArgumentException("Key type cannot be null");
    }
    if (!keyType.isEnum()) {
      throw new IllegalArgumentException("Key type must be an enum");
    }
    if (keyType.getEnumConstants().length == 0) {
      throw new IllegalArgumentException("Key type must have at least one enum constant");
    }
    this.keyType = keyType;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "{" +
      "keyType=" + keyType.getSimpleName() + ", " +
      "size=" + size() + "}";
  }
}
