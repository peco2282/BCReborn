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
package com.peco2282.bcreborn.transport.item;

import com.peco2282.bcreborn.api.transport.PipeWire;
import net.minecraft.world.item.Item;

/**
 * Port of the original {@code ItemPipeWire}. The original used one item with a damage value per colour;
 * here each {@link PipeWire} colour has its own item. Installing and removing is handled by {@code PipeBlock}.
 */
public class PipeWireItem extends Item {
  private final PipeWire wire;

  public PipeWireItem(PipeWire wire) {
    super(new Item.Properties());
    this.wire = wire;
  }

  public PipeWire getWire() {
    return wire;
  }
}
