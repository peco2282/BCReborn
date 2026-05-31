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

public enum PowerMode implements StringRepresentable {
    OFF(0),
    LOW(10),
    MEDIUM(100),
    HIGH(1000),
    MAX(10000);

    public final int maxPower;

    PowerMode(int maxPower) {
        this.maxPower = maxPower;
    }

  @Override
  public String getSerializedName() {
    return name().toLowerCase();
  }
}
