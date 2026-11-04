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
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code ContainerDiamondPipe}.
 * Lays out 6 rows (DOWN, UP, NORTH, SOUTH, WEST, EAST) x 9 columns of phantom slots,
 * followed by the player inventory.
 */
public class DiamondPipeMenu extends BuildCraftMenu<DiamondPipeMenu> {
  @Nullable
  private final PipeBlockEntity pipe;
  private final Container filterInv;

  /**
   * Client side: resolves the block entity from the BlockPos written by {@code NetworkHooks.openScreen}.
   */
  public DiamondPipeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
    this(containerId, playerInventory, resolvePipe(playerInventory, buf));
  }

  /**
   * Server side: receives the pipe block entity directly.
   */
  public DiamondPipeMenu(int containerId, Inventory playerInventory, @Nullable PipeBlockEntity pipe) {
    super(TransportMenuTypes.DIAMOND_PIPE_MENU.get(), containerId, playerInventory);
    this.pipe = pipe;
    // Fall back to a dummy container so the slot count still matches the server if the BE is not loaded on the client
    this.filterInv = pipe != null ? new DiamondPipeFilterContainer(pipe) : new SimpleContainer(DiamondPipeFilterContainer.SIZE);

    for (int y = 0; y < 6; y++) {
      for (int x = 0; x < 9; x++) {
        addSlot(new SlotPhantom(filterInv, x + y * 9, 8 + x * 18, 18 + y * 18));
      }
    }

    for (int l = 0; l < 3; l++) {
      for (int k1 = 0; k1 < 9; k1++) {
        addSlot(new Slot(playerInventory, k1 + l * 9 + 9, 8 + k1 * 18, 140 + l * 18));
      }
    }

    for (int i1 = 0; i1 < 9; i1++) {
      addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 198));
    }
  }

  @Nullable
  private static PipeBlockEntity resolvePipe(Inventory playerInventory, @Nullable FriendlyByteBuf buf) {
    if (buf == null) return null;
    BlockEntity be = playerInventory.player.level().getBlockEntity(buf.readBlockPos());
    return be instanceof PipeBlockEntity pipeBE ? pipeBE : null;
  }

  @Nullable
  public PipeBlockEntity getPipe() {
    return pipe;
  }

  public Container getFilterInventory() {
    return filterInv;
  }

  @Override
  public boolean stillValid(Player player) {
    return pipe != null && !pipe.isRemoved() && filterInv.stillValid(player);
  }
}
