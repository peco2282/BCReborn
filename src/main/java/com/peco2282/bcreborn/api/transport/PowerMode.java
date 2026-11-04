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
package com.peco2282.bcreborn.api.transport;

import net.minecraft.util.StringRepresentable;

/**
 * Represents different power levels for transport systems.
 */
public enum PowerMode implements StringRepresentable {
  // Original buildcraft.core.PowerMode: the iron kinesis pipe limits, 2..128 MJ/t = 20..1280 RF/t.
  M2(20),
  M4(40),
  M8(80),
  M16(160),
  M32(320),
  M64(640),
  M128(1280);

  public static final PowerMode[] VALUES = values();

  /**
   * The maximum power value associated with this mode.
   */
  public final int maxPower;

  PowerMode(int maxPower) {
    this.maxPower = maxPower;
  }

  public PowerMode getNext() {
    return VALUES[(ordinal() + 1) % VALUES.length];
  }

  public PowerMode getPrevious() {
    return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
  }

  /** @return the mode whose limit is {@code maxPower}, or {@link #M128} */
  public static PowerMode fromMaxPower(int maxPower) {
    for (PowerMode mode : VALUES) {
      if (mode.maxPower == maxPower) return mode;
    }
    return M128;
  }

  @Override
  public String getSerializedName() {
    return name().toLowerCase();
  }
}
