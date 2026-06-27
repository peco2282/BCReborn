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

/**
 * Event bus interface for managing and dispatching BC Reborn events.
 * <p>
 * This interface provides methods for registering event listeners, posting events,
 * and controlling the lifecycle of the event bus system.
 * </p>
 */
public interface BCEventBus {
  /**
   * Registers an event listener to receive events from this event bus.
   * <p>
   * The listener object should contain methods annotated with the appropriate
   * event handler annotation to receive events.
   * </p>
   *
   * @param listener the listener object to register
   */
  void register(Object listener);

  /**
   * Unregisters a previously registered event listener.
   * <p>
   * After unregistering, the listener will no longer receive events from this event bus.
   * </p>
   *
   * @param listener the listener object to unregister
   */
  void unregister(Object listener);

  /**
   * Posts an event to all registered listeners using the default dispatcher.
   *
   * @param event the event to post
   * @return true if the event was cancelled, false otherwise
   */
  boolean post(BCEvent event);

  /**
   * Posts an event to all registered listeners using a custom dispatcher.
   *
   * @param event      the event to post
   * @param dispatcher the custom event dispatcher to use
   * @return true if the event was cancelled, false otherwise
   */
  boolean post(BCEvent event, EventDispatcher dispatcher);

  /**
   * Shuts down the event bus and releases all resources.
   * <p>
   * All registered listeners will be unregistered and the event bus
   * will stop accepting new events.
   * </p>
   */
  void shutdown();

  /**
   * Initializes and starts the event bus.
   * <p>
   * This method should be called before registering listeners or posting events.
   * </p>
   */
  void start();

}
