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

import com.peco2282.bcreborn.api.events.BCEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventListenerContainer {
  private static final Map<Class<? extends BCEvent>, List<BCEventHolder>> listeners = new ConcurrentHashMap<>();

  public static <T extends BCEvent> void registerListener(
    Class<T> clazz, BCEventHolder listener) {
    var list = listeners.computeIfAbsent(clazz, ignore -> new CopyOnWriteArrayList<>());
    list.add(listener);
    list.sort(Comparator.comparingInt(BCEventHolder::getPriority).reversed());
  }

  public static <T extends BCEvent> @Nullable List<BCEventHolder> getListeners(Class<T> clazz) {
    return listeners.get(clazz);
  }

  public static void unregisterListener(Class<?> declaringClass) {
    listeners.values().forEach(list ->
      list.removeIf(holder -> holder.getDeclaringClass() == declaringClass));
  }
}
