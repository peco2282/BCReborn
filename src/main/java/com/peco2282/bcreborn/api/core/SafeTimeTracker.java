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
package com.peco2282.bcreborn.api.core;


import net.minecraft.world.level.Level;

/**
 * A utility class for tracking time delays in a Minecraft world.
 * <p>
 * This tracker helps manage timed events by checking if a certain amount of game time
 * has passed since the last marked time. It supports optional randomization to prevent
 * all instances from synchronizing their actions.
 * </p>
 * <p>
 * The tracker uses world game time ({@link Level#getGameTime()}) and handles cases
 * where the world time might reset or go backwards.
 * </p>
 */
public class SafeTimeTracker {

  /**
   * The last marked game time in ticks. Initialized to {@link Long#MIN_VALUE}.
   */
  private long lastMark = Long.MIN_VALUE;

  /**
   * The duration in ticks of the last completed delay period. -1 if no delay has completed yet.
   */
  private long duration = -1;

  /**
   * The range for random delay variation in ticks. A value of 0 means no randomization.
   */
  private long randomRange = 0;

  /**
   * The actual random delay added in the last delay period, between 0 and randomRange.
   */
  private long lastRandomDelay = 0;

  /**
   * The base delay duration in ticks that must pass before the next event.
   */
  private long internalDelay = 1;

  /**
   * Default constructor with a delay of 1 tick and no randomization.
   *
   * @deprecated Should use constructors with parameters instead for explicit delay configuration.
   */
  @Deprecated
  public SafeTimeTracker() {

  }

  /**
   * Creates a time tracker with a specific delay and no randomization.
   *
   * @param delay The delay in ticks that must pass before marking time again.
   */
  public SafeTimeTracker(long delay) {
    internalDelay = delay;
  }

  /**
   * Creates a time tracker with a specific delay and randomization range.
   * <p>
   * In many situations, it is a bad idea to have all objects of the same
   * kind to be waiting for the exact same amount of time, as that can lead
   * to some situation where they're all synchronized and got to work all
   * at the same time. When created with a random range, the mark that is set
   * when reaching the expected delay will be added with a random number
   * between [0, range[, meaning that the event will take between 0 and range
   * more ticks to run.
   * </p>
   *
   * @param delay  The base delay in ticks that must pass before marking time again.
   * @param random The range in ticks for random delay variation (0 to random-1).
   */
  public SafeTimeTracker(long delay, long random) {
    internalDelay = delay;
    randomRange = random;
  }

  /**
   * Checks if the configured delay has passed and marks the current time if it has.
   * <p>
   * This method handles world time resets by detecting when the current time is less
   * than the last marked time. It also applies random delay variation if configured.
   * </p>
   *
   * @param world The world to get the current game time from. Must not be null.
   * @return {@code true} if the delay has passed and time was marked, {@code false} otherwise.
   */
  public boolean markTimeIfDelay(Level world) {
    if (world == null) {
      return false;
    }

    long currentTime = world.getGameTime();

    if (currentTime < lastMark) {
      lastMark = currentTime;
      return false;
    } else if (lastMark + internalDelay + lastRandomDelay <= currentTime) {
      duration = currentTime - lastMark;
      lastMark = currentTime;
      lastRandomDelay = (int) (Math.random() * randomRange);

      return true;
    } else {
      return false;
    }
  }

  /**
   * Gets the duration in ticks of the last completed delay period.
   *
   * @return The duration in ticks, or 0 if no delay has completed yet.
   */
  public long durationOfLastDelay() {
    return duration > 0 ? duration : 0;
  }

  /**
   * Manually marks the current world time without checking the delay.
   * <p>
   * This can be used to reset the tracker or synchronize it with a specific event.
   * </p>
   *
   * @param world The world to get the current game time from.
   */
  public void markTime(Level world) {
    lastMark = world.getGameTime();
  }
}