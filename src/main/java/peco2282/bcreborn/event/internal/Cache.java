/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.event.internal;

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
