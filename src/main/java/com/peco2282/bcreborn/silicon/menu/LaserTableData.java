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
package com.peco2282.bcreborn.silicon.menu;

import com.peco2282.bcreborn.silicon.block.entity.LaserTableBaseBlockEntity;
import net.minecraft.world.inventory.ContainerData;

/** Vanilla menu data uses signed shorts; split the two FE counters for lossless sync. */
final class LaserTableData implements ContainerData {
  private final LaserTableBaseBlockEntity table;
  LaserTableData(LaserTableBaseBlockEntity table) { this.table = table; }
  @Override public int getCount() { return 4; }
  @Override public int get(int index) {
    int value = index < 2 ? table.getEnergy() : table.clientRequiredEnergy;
    return index % 2 == 0 ? value & 0xffff : value >>> 16;
  }
  @Override public void set(int index, int value) {
    int old = index < 2 ? table.getEnergy() : table.clientRequiredEnergy;
    int next = index % 2 == 0 ? (old & 0xffff0000) | (value & 0xffff)
      : (old & 0xffff) | ((value & 0xffff) << 16);
    if (index < 2) table.setEnergy(next); else table.clientRequiredEnergy = next;
  }
}

