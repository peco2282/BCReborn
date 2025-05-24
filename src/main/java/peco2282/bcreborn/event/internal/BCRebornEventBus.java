/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.event.internal;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import peco2282.bcreborn.InternalLogger;
import peco2282.bcreborn.api.event.BCEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class BCRebornEventBus implements EventBus {

  private static final EventBus EVENT_BUS = new BCRebornEventBus();
  private static final Logger log = InternalLogger.create();
  private final Map<Class<BCEvent> /*BCEvent extended.*/, List<EventListener>> listeners =
      new ConcurrentHashMap<>();

  private BCRebornEventBus() {}

  public static EventBus getEventBus() {
    return EVENT_BUS;
  }

  private static @NotNull Class<?> getEventClass(Method method) {
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

  @Override
  public void register(Object target) {}

  public void registerAnnotated(Object target) {
    if (target == Class.class) throw new IllegalArgumentException("must not Class<?>");
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
        final Class<?> event = getEventClass(method);
        // noinspection unchecked
        register(target, (Class<BCEvent>) event, method, target);
      }
    }
  }

  private <E extends BCEvent> void register(
      Object target, Class<BCEvent> clazz, Method method, Object instance) {
    try {
      EventListener listener = new BCEventListener(method, instance);
      var list = ListenerContainer.registerListener(clazz, listener);
      listeners
          .computeIfAbsent(clazz, k -> Collections.synchronizedList(new ArrayList<>()))
          .addAll(list);
      log.info("Registered listener for {}#{}", target.getClass(), method.getName());
    } catch (ReflectiveOperationException e) {
      log.error("At {} class", target.getClass().getName(), e);
    }
  }

  private UnsupportedOperationException uoe(String method) {
    return new UnsupportedOperationException(method + " not supported. must static and private");
  }

  /**
   * Unregister the supplied listener from this EventBus.
   *
   * <p>Removes all listeners from events.
   *
   * <p>NOTE: Consumers can be stored in a variable if unregistration is required for the Consumer.
   *
   * @param object The object, {@link Class} or {@link Consumer} to unsubscribe.
   */
  @Override
  public void unregister(Object object) {}

  @Override
  public boolean post(BCEvent event) {
    return post(event, EventListener::invoke);
  }

  @Override
  public boolean post(BCEvent event, EventDispatcher dispatcher) {
    var list = ListenerContainer.getListeners(event.getClass());
    if (list == null) return false;
    list.forEach(listener -> dispatcher.dispatch(listener, event));
    log.info("{} events dispatched.", list.size());
    return true;
  }

  /**
   * Shuts down this event internal.
   *
   * <p>No future events will be fired on this event internal, so any call to {@link #post(BCEvent)}
   * will be a no op after this method has been invoked
   */
  @Override
  public void shutdown() {}

  @Override
  public void start() {}
}
