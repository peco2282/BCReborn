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

import com.peco2282.bcreborn.BCReborn;
import com.peco2282.bcreborn.api.events.BCEvent;
import com.peco2282.bcreborn.api.events.BCEventBus;
import com.peco2282.bcreborn.api.events.EventDispatcher;
import com.peco2282.bcreborn.api.events.Subscriber;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class BCRebornEventBus implements BCEventBus {
  private static final BCEventBus EVENT_BUS = new BCRebornEventBus();
  private static final Logger LOGGER = BCReborn.commonLogger();
  private boolean isStarted = false;
  private boolean isShutdown = false;

  private BCRebornEventBus() {
  }

  public static BCEventBus getEventBus() {
    return EVENT_BUS;
  }

  private static Class<?> getEventClass(Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length != 1) {
      throw new IllegalArgumentException("method must have exactly 1 parameter");
    }
    Class<?> event = parameterTypes[0];
    if (!BCEvent.class.isAssignableFrom(event)) {
      throw new IllegalArgumentException("method must extends BCEvent");
    }
    if (event == BCEvent.class) {
      throw new IllegalArgumentException("method param[0] must not BCEvent");
    }
    return event;
  }

  private static boolean isValidMethod(Method method) {
    return
      Modifier.isPublic(method.getModifiers()) &&
        method.getParameterCount() == 1 &&
        method.getReturnType() == void.class &&
        BCEvent.class.isAssignableFrom(method.getParameterTypes()[0]) &&
        method.isAnnotationPresent(Subscriber.class);
  }

  @Override
  public void register(Object target) {
    checkState();
    Class<?> clazz;
    Object instance;
    if (target instanceof Class<?> c) {
      clazz = c;
      instance = null;
    } else {
      clazz = target.getClass();
      instance = target;
    }
    for (Method method : clazz.getDeclaredMethods()) {
      if (isValidMethod(method)) {
        @SuppressWarnings("unchecked")
        Class<BCEvent> eventClass = (Class<BCEvent>) getEventClass(method);
        register(clazz, eventClass, method, instance);
      }
    }
  }

  private void register(
    Class<?> declaringClass, Class<BCEvent> eventClass, Method method, @Nullable Object instance) {
    try {
      EventListenerContainer.registerListener(eventClass, new BCEventHolder(method, instance));
      LOGGER.info("Registered listener for {}#{}", declaringClass.getName(), method.getName());
    } catch (ReflectiveOperationException e) {
      LOGGER.error("At {} class", declaringClass.getName(), e);
    }
  }

  @Override
  public void unregister(Object listener) {
    checkState();
    Class<?> clazz = (listener instanceof Class<?> c) ? c : listener.getClass();
    EventListenerContainer.unregisterListener(clazz);
  }

  @Override
  public boolean post(BCEvent event) {
    checkState();
    var list = EventListenerContainer.getListeners(event.getClass());
    if (list == null || list.isEmpty()) return false;
    list.forEach(holder -> holder.call(event));
    LOGGER.info("{} events dispatched.", list.size());
    return true;
  }

  @Override
  public boolean post(BCEvent event, EventDispatcher dispatcher) {
    checkState();
    var list = EventListenerContainer.getListeners(event.getClass());
    if (list == null || list.isEmpty()) return false;
    list.forEach(listener -> dispatcher.dispatch(event));
    LOGGER.info("{}: {} events dispatched.", event.getClass().getSimpleName(), list.size());
    return true;
  }

  private void checkState() {
    if (isShutdown) {
      throw new IllegalStateException("EventBus is shutdown");
    }
    if (!isStarted) {
      throw new IllegalStateException("EventBus is not started");
    }
  }

  @Override
  public void shutdown() {
    this.isShutdown = true;
  }

  @Override
  public void start() {
    this.isStarted = true;
  }
}
