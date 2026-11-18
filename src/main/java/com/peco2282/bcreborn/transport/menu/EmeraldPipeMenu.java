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

import com.peco2282.bcreborn.common.gui.slots.SlotPhantom;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.transport.TransportMenuTypes;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;

/**
 * Original {@code ContainerEmeraldPipe}: nine filters, and the white list / black list / round robin buttons, which
 * reach the server through the vanilla container button click.
 */
public class EmeraldPipeMenu extends BuildCraftMenu<EmeraldPipeMenu> {
  private final PipeBlockEntity pipe;
  private final DataSlot mode;

  public EmeraldPipeMenu(int id, Inventory playerInventory, PipeBlockEntity pipe) {
    super(TransportMenuTypes.EMERALD_PIPE_MENU.get(), id, playerInventory);
    this.pipe = pipe;
    for (int i = 0; i < 9; i++) {
      addSlot(new SlotPhantom(pipe.getEmeraldFilters(), i, 8 + i * 18, 18));
    }
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 79 + row * 18));
      }
    }
    for (int col = 0; col < 9; col++) {
      addSlot(new Slot(playerInventory, col, 8 + col * 18, 137));
    }
    mode = addDataSlot(new DataSlot() {
      @Override
      public int get() {
        return pipe.getExtractFilterMode().ordinal();
      }

      @Override
      public void set(int value) {
        // The client copy; the server keeps the mode in the pipe.
        if (pipe.getLevel() != null && pipe.getLevel().isClientSide) {
          pipe.setExtractFilterMode(PipeBlockEntity.ExtractFilterMode.values()[Math.floorMod(value, PipeBlockEntity.ExtractFilterMode.values().length)]);
        }
      }
    });
  }

  public EmeraldPipeMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
    this(id, playerInventory, BuildCraftMenu.<PipeBlockEntity>getBlockEntity(playerInventory, buf));
  }

  public PipeBlockEntity.ExtractFilterMode getMode() {
    return PipeBlockEntity.ExtractFilterMode.values()[Math.floorMod(mode.get(), PipeBlockEntity.ExtractFilterMode.values().length)];
  }

  /** The mode buttons: the button id is the mode's ordinal. */
  @Override
  public boolean clickMenuButton(Player player, int id) {
    if (id < 0 || id >= PipeBlockEntity.ExtractFilterMode.values().length) return false;
    pipe.setExtractFilterMode(PipeBlockEntity.ExtractFilterMode.values()[id]);
    return true;
  }

  @Override
  public boolean stillValid(Player player) {
    return !pipe.isRemoved() && player.distanceToSqr(pipe.getBlockPos().getCenter()) <= 64.0D;
  }
}
