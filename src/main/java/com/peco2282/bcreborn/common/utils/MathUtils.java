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
package com.peco2282.bcreborn.common.utils;

public final class MathUtils {

  /**
   * Deactivate constructor
   */
  private MathUtils() {
  }

  public static int clamp(int value, int min, int max) {
    return value < min ? min : (value > max ? max : value);
  }

  public static float clamp(float value, float min, float max) {
    return value < min ? min : (value > max ? max : value);
  }

  public static double clamp(double value, double min, double max) {
    return value < min ? min : (value > max ? max : value);
  }
}
