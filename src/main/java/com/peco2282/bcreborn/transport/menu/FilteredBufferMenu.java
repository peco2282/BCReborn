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
import com.peco2282.bcreborn.common.gui.slots.SlotValidated;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.transport.TransportMenuTypes;
import com.peco2282.bcreborn.transport.block.entity.FilteredBufferBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

/** Original {@code ContainerFilteredBuffer}: a row of filters over a row of storage slots. */
public class FilteredBufferMenu extends BuildCraftMenu<FilteredBufferMenu> {
  private final FilteredBufferBlockEntity buffer;

  /** Original {@code SlotPhantomLockable}: a filter can only change while its storage slot is empty. */
  private static final class LockableFilterSlot extends SlotPhantom {
    private final Container storage;

    LockableFilterSlot(Container filters, Container storage, int index, int x, int y) {
      super(filters, index, x, y);
      this.storage = storage;
    }

    @Override
    public boolean canAdjust() {
      return storage.getItem(getSlotIndex()).isEmpty();
    }
  }

  public FilteredBufferMenu(int id, Inventory playerInventory, FilteredBufferBlockEntity buffer) {
    super(TransportMenuTypes.FILTERED_BUFFER_MENU.get(), id, playerInventory);
    this.buffer = buffer;
    for (int col = 0; col < FilteredBufferBlockEntity.SLOTS; col++) {
      addSlot(new LockableFilterSlot(buffer.getFilters(), buffer, col, 8 + col * 18, 27));
      addSlot(new SlotValidated(buffer, col, 8 + col * 18, 61));
    }
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 86 + row * 18));
      }
    }
    for (int col = 0; col < 9; col++) {
      addSlot(new Slot(playerInventory, col, 8 + col * 18, 144));
    }
  }

  public FilteredBufferMenu(int id, Inventory playerInventory, FriendlyByteBuf buf) {
    this(id, playerInventory, BuildCraftMenu.<FilteredBufferBlockEntity>getBlockEntity(playerInventory, buf));
  }

  public FilteredBufferBlockEntity getBuffer() {
    return buffer;
  }

  @Override
  public boolean stillValid(Player player) {
    return buffer.stillValid(player);
  }
}
