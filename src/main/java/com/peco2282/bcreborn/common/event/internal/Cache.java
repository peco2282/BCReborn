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
package com.peco2282.bcreborn.common.event.internal;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class Cache {
  private final Object lock = new Object();
  private final Map<Method, Class<?>> map = new ConcurrentHashMap<>(32);

  public Class<?> get(Method method) {
    return map.get(method);
  }

  public <I> Class<?> computeIfAbsent(
      Method key, Supplier<I> factory, Function<I, Class<?>> finalizer) {
    var ret = get(key);

    if (ret != null) return ret;

    var intermediate = factory.get();

    synchronized (lock) {
      ret = map.get(key);
      if (ret == null) {
        ret = finalizer.apply(intermediate);
        map.put(key, ret);
      }
      return ret;
    }
  }
}
