package com.peco2282.bcreborn.common.registry;

import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

public class RegistryBooleanObject<V> extends HashMap<Boolean, RegistryObject<V>> {
  private final RegistryObject<V> onTrue;
  private final RegistryObject<V> onFalse;

  private RegistryBooleanObject(RegistryObject<V> onTrue, RegistryObject<V> onFalse) {
    this.onTrue = onTrue;
    this.onFalse = onFalse;
  }
  public static <V> RegistryBooleanObject<V> create(RegistryObject<V> onTrue, RegistryObject<V> onFalse) {
    return new RegistryBooleanObject<>(onTrue, onFalse);
  }
  public RegistryObject<V> get(boolean key) {
    return key ? onTrue : onFalse;
  }

  public RegistryObject<V> getOnTrue() {
    return onTrue;
  }

  public RegistryObject<V> getOnFalse() {
    return onFalse;
  }

  @Override
  public String toString() {
    return "RegistryBooleanObject{" +
        "onTrue=" + onTrue +
        ", onFalse=" + onFalse +
        '}';
  }
}
