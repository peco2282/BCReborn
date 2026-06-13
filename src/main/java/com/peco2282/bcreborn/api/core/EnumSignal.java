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

import java.util.Locale;

/**
 * Represents different colors of pipe wire signals.
 */
public enum EnumSignal {
  RED,
  BLUE,
  GREEN,
  YELLOW;

  /**
   * Cache of all possible signal values.
   */
  public static final EnumSignal[] VALUES = values();

  /**
   * Gets a signal from its ordinal ID.
   *
   * @param id The ordinal ID.
   * @return The signal, or {@link #RED} if the ID is out of bounds.
   */
  public static EnumSignal fromId(int id) {
    if (id < 0 || id >= VALUES.length) {
      return RED;
    }
    return VALUES[id];
  }

  /**
   * @return The lowercase name of the signal, suitable for tags.
   */
  public String getTag() {
    return name().toLowerCase(Locale.ENGLISH);
  }

  /**
   * @return The capitalized name of the signal (e.g., "Red").
   */
  public String getName() {
    String name = name().toLowerCase(Locale.ENGLISH);
    return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
  }

  /**
   * @return The next signal in sequence.
   */
  public EnumSignal getNext() {
    return VALUES[(ordinal() + 1) % VALUES.length];
  }

  /**
   * @return The previous signal in sequence.
   */
  public EnumSignal getPrevious() {
    return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
  }
}
