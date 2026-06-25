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
import com.peco2282.bcreborn.api.events.EventListener;
import com.peco2282.bcreborn.api.events.Subscriber;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class BCEventHolder {
  private static final EventFactory factory = EventFactory.getFactory();

  private final int priority;
  private final EventListener listener;
  private final Class<?> declaringClass;

  public BCEventHolder(Method method, @Nullable Object instance) throws ReflectiveOperationException {
    this.listener = factory.create(method, instance);
    this.priority = method.getAnnotation(Subscriber.class).priority().getPriority();
    this.declaringClass = method.getDeclaringClass();
  }

  public void call(BCEvent event) {
    listener.invoke(event);
  }

  public int getPriority() {
    return priority;
  }

  public Class<?> getDeclaringClass() {
    return declaringClass;
  }
}
