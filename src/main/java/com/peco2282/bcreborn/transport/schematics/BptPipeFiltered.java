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
package com.peco2282.bcreborn.transport.schematics;


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicBlockEntity;
import com.peco2282.bcreborn.common.SimpleInventory;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;

public class BptPipeFiltered extends BptPipeExtension {

  public BptPipeFiltered(Item i) {
    super(i);
  }

  @Override
  public void rotateLeft(SchematicBlockEntity slot, IBuilderContext context) {
    SimpleInventory inv = new SimpleInventory(54, "Filters", 1);
    SimpleInventory newInv = new SimpleInventory(54, "Filters", 1);
    inv.readTag(slot.tileNBT);

    for (int dir = 0; dir <= 5; ++dir) {
      Direction r = Direction.values()[dir];

      for (int s = 0; s < 9; ++s) {
        newInv.setItem(r.ordinal() * 9 + s, inv.getItem(dir * 9 + s));
      }
    }

    newInv.writeTag(slot.tileNBT);
  }
}
