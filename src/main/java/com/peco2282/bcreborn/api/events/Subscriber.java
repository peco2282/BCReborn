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
package com.peco2282.bcreborn.api.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods as event subscribers in the BC Reborn event system.
 * <p>
 * Methods annotated with {@code @Subscriber} will be registered to receive events.
 * The method must have a single parameter representing the event type to subscribe to.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * @Subscriber(priority = Priority.HIGH)
 * public void onCustomEvent(CustomEvent event) {
 *     // Handle event
 * }
 * }
 * </pre>
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscriber {
  /**
   * Specifies the priority of this event subscriber.
   * <p>
   * Higher priority subscribers are invoked before lower priority ones.
   * Default priority is {@link Priority#NORMAL}.
   * </p>
   *
   * @return the priority level for this subscriber
   */
  Priority priority() default Priority.NORMAL;

  /**
   * Defines the priority levels for event subscribers.
   * <p>
   * Priority determines the order in which subscribers are invoked when an event is fired.
   * Subscribers with higher priority values are called first.
   * </p>
   */
  enum Priority {
    /**
     * Lowest priority level (value: -2).
     * Subscribers with this priority are invoked last.
     */
    LOWEST,   // -2

    /**
     * Low priority level (value: -1).
     * Subscribers with this priority are invoked after NORMAL priority subscribers.
     */
    LOW,      // -1

    /**
     * Normal priority level (value: 0).
     * This is the default priority for subscribers.
     */
    NORMAL,   // 0

    /**
     * High priority level (value: 1).
     * Subscribers with this priority are invoked before NORMAL priority subscribers.
     */
    HIGH,     // 1

    /**
     * Highest priority level (value: 2).
     * Subscribers with this priority are invoked first.
     */
    HIGHEST;  // 2


    /**
     * Returns the numeric priority value for this priority level.
     * <p>
     * The value ranges from -2 (LOWEST) to 2 (HIGHEST).
     * </p>
     *
     * @return the numeric priority value
     */
    public int getPriority() {
      return -2 + ordinal();
    }
  }
}
