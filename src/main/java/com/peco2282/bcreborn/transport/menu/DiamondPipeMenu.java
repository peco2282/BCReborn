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
package com.peco2282.bcreborn.transport.menu;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.transport.TransportMenuTypes;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class DiamondPipeMenu extends BuildCraftMenu<DiamondPipeMenu> {
  private final PipeBlockEntity pipe;

  public DiamondPipeMenu(int p_38852_, Inventory p_38853_, FriendlyByteBuf buf) {
    super(TransportMenuTypes.DIAMOND_PIPE_MENU.get(), p_38852_, p_38853_);
    this.pipe = getBlockEntity(p_38853_, buf);

    if (pipe != null) {
      for (int y = 0; y < 6; y++) {
        Direction dir = Direction.from3DDataValue(y);
        Container filterInv = pipe.getFilter(dir);
        for (int x = 0; x < 9; x++) {
          addSlot(new com.peco2282.bcreborn.common.gui.slots.SlotPhantom(filterInv, x, 8 + x * 18, 18 + y * 18));
        }
      }
    }

    for (int l = 0; l < 3; l++) {
      for (int k1 = 0; k1 < 9; k1++) {
        addSlot(new net.minecraft.world.inventory.Slot(p_38853_, k1 + l * 9 + 9, 8 + k1 * 18, 140 + l * 18));
      }
    }

    for (int i1 = 0; i1 < 9; i1++) {
      addSlot(new net.minecraft.world.inventory.Slot(p_38853_, i1, 8 + i1 * 18, 198));
    }
  }

  @Override
  public boolean stillValid(Player p_38874_) {
    return pipe != null && p_38874_.distanceToSqr((double) pipe.getBlockPos().getX() + 0.5D, (double) pipe.getBlockPos().getY() + 0.5D, (double) pipe.getBlockPos().getZ() + 0.5D) <= 64.0D;
  }
}
