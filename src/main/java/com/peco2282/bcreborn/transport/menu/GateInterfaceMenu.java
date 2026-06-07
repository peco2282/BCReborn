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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class GateInterfaceMenu extends BuildCraftMenu<GateInterfaceMenu> {
  private final PipeBlockEntity pipe;

  public GateInterfaceMenu(int p_38852_, Inventory p_38853_, FriendlyByteBuf buf) {
    super(TransportMenuTypes.GATE_INTERFACE_MENU.get(), p_38852_, p_38853_);
    this.pipe = getBlockEntity(p_38853_, buf);

    int guiHeight = 166; // Default height

    for (int y = 0; y < 3; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new net.minecraft.world.inventory.Slot(p_38853_, x + y * 9 + 9, 8 + x * 18, guiHeight - 84 + y * 18));
      }
    }

    for (int x = 0; x < 9; x++) {
      addSlot(new net.minecraft.world.inventory.Slot(p_38853_, x, 8 + x * 18, guiHeight - 26));
    }
  }

  @Override
  public boolean stillValid(Player p_38874_) {
    return pipe != null && p_38874_.distanceToSqr((double) pipe.getBlockPos().getX() + 0.5D, (double) pipe.getBlockPos().getY() + 0.5D, (double) pipe.getBlockPos().getZ() + 0.5D) <= 64.0D;
  }
}
