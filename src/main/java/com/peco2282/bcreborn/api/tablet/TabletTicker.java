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
package com.peco2282.bcreborn.api.tablet;

/**
 * Utility class for capturing tablet ticks every X seconds.
 */
public class TabletTicker {
  private final float tickTime;
  private float time = 0.0F;
  private int ticked = 0;

  public TabletTicker(float tickTime) {
    this.tickTime = tickTime;
  }

  public void add(float time) {
    this.time += time;

    while (this.time >= tickTime) {
      this.time -= tickTime;
      ticked++;
    }
  }

  public int getTicks() {
    return ticked;
  }

  public boolean tick() {
    boolean oldTicked = ticked > 0;
    ticked = 0;
    return oldTicked;
  }

  public void reset() {
    ticked = 0;
    time = 0;
  }
}
