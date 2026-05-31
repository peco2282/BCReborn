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
