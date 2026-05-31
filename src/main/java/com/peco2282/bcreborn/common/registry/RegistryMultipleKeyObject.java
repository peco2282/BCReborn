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

import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class RegistryMultipleKeyObject<V> implements Supplier<RegistryObject<V>> {
  // @formatter:off
  public static <V, K1, K2> TwoKeys<V, K1, K2> two(
    Collection<K1> key1s,                                           // Allow Key1 to be a collection
    Collection<K2> key2s,                                           // Allow Key2 to be a collection
    BiFunction<K1, K2, String> nameMaker,                           // Create registry name from key1 and key2
    BiFunction<String, Supplier<V>, RegistryObject<V>> registerer,  // Registerer the object
    BiFunction<K1, K2, V> constructor                               // Constructor for the object
  ) {
    return new TwoKeys<>(key1s, key2s, nameMaker, registerer, constructor);
  }

  public static <V, K1, K2, K3> ThreeKeys<V, K1, K2, K3> three(
    Collection<K1> key1s,                                           // Allow Key1 to be a collection
    Collection<K2> key2s,                                           // Allow Key2 to be a collection
    Collection<K3> key3s,                                           // Allow Key3 to be a collection
    TriFunction<K1, K2, K3, String> nameMaker,                      // Create registry name from key1, key2, and key3
    BiFunction<String, Supplier<V>, RegistryObject<V>> registerer,  // Registerer the object
    TriFunction<K1, K2, K3, V> constructor                          // Constructor for the object
  ) {
    return new ThreeKeys<>(key1s, key2s, key3s, nameMaker, registerer, constructor);
  }

  // @formatter:on
  public static class TwoKeys<V, K1, K2> extends RegistryMultipleKeyObject<V> {
    private final Collection<K1> key1s;
    private final Collection<K2> key2s;
    private final Map<K1, Map<K2, RegistryObject<V>>> map = new ConcurrentHashMap<>();

    public TwoKeys(
      Collection<K1> key1s,
      Collection<K2> key2s,
      BiFunction<K1, K2, String> nameMaker,
      BiFunction<String, Supplier<V>, RegistryObject<V>> registerer,
      BiFunction<K1, K2, V> constructor
    ) {
      this.key1s = key1s;
      this.key2s = key2s;
      for (K1 k1 : key1s) {
        var map2 = new ConcurrentHashMap<K2, RegistryObject<V>>();
        for (K2 k2 : key2s) {
          String name = nameMaker.apply(k1, k2);
          Supplier<V> supplier = () -> constructor.apply(k1, k2);
          map2.put(k2, registerer.apply(name, supplier));
        }
        map.put(k1, map2);
      }
    }

    @Override
    public RegistryObject<V> get() {
      return map.get(key1s.iterator().next()).get(key2s.iterator().next());
    }

    public RegistryObject<V> get(K1 key1, K2 key2) {
      return map.get(key1).get(key2);
    }
  }

  public static class ThreeKeys<V, K1, K2, K3> extends RegistryMultipleKeyObject<V> {
    private final Collection<K1> key1s;
    private final Collection<K2> key2s;
    private final Collection<K3> key3s;
    private final Map<K1, Map<K2, Map<K3, RegistryObject<V>>>> map = new ConcurrentHashMap<>();

    public ThreeKeys(
      Collection<K1> key1s,
      Collection<K2> key2s,
      Collection<K3> key3s,
      TriFunction<K1, K2, K3, String> nameMaker,
      BiFunction<String, Supplier<V>, RegistryObject<V>> registerer,
      TriFunction<K1, K2, K3, V> constructor
    ) {
      this.key1s = key1s;
      this.key2s = key2s;
      this.key3s = key3s;
      for (K1 k1 : key1s) {
        var map2 = new ConcurrentHashMap<K2, Map<K3, RegistryObject<V>>>();
        for (K2 k2 : key2s) {
          var map3 = new ConcurrentHashMap<K3, RegistryObject<V>>();
          for (K3 k3 : key3s) {
            String name = nameMaker.apply(k1, k2, k3);
            Supplier<V> supplier = () -> constructor.apply(k1, k2, k3);
            map3.put(k3, registerer.apply(name, supplier));
          }
          map2.put(k2, map3);
        }
        map.put(k1, map2);
      }
    }

    @Override
    public RegistryObject<V> get() {
      return map.get(key1s.iterator().next()).get(key2s.iterator().next()).get(key3s.iterator().next());
    }

    public RegistryObject<V> get(K1 key1, K2 key2, K3 key3) {
      return map.get(key1).get(key2).get(key3);
    }
  }
}
