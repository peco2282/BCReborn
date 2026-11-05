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
import com.peco2282.bcreborn.transport.pipe.runtime.EmzuliState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code ContainerEmzuliPipe}: four phantom filter slots, four paint buttons and the player
 * inventory.
 * <p>
 * The slot colours are synced through {@link ContainerData} (the original used progress bar updates). A paint
 * button click is sent with the vanilla menu button packet ({@link #clickMenuButton}); the original sent a
 * {@code PacketGuiReturn}.
 */
public class EmzuliPipeMenu extends BuildCraftMenu<EmzuliPipeMenu> {
  /** Paint button positions (original {@code PaintWidget}s), in slot order. */
  public static final int[][] PAINT_BUTTONS = {{51, 19}, {104, 19}, {51, 47}, {104, 47}};
  public static final int BUTTON_LEFT = 0;
  public static final int BUTTON_RIGHT = 1;
  public static final int BUTTON_MIDDLE = 2;

  @Nullable
  private final PipeBlockEntity pipe;
  private final ContainerData slotColors;

  /** Client side: resolves the block entity from the BlockPos written by {@code NetworkHooks.openScreen}. */
  public EmzuliPipeMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
    this(containerId, playerInventory, resolvePipe(playerInventory, buf), new SimpleContainerData(EmzuliState.SLOTS));
  }

  /** Server side. */
  public EmzuliPipeMenu(int containerId, Inventory playerInventory, PipeBlockEntity pipe) {
    this(containerId, playerInventory, pipe, serverData(pipe));
  }

  private EmzuliPipeMenu(int containerId, Inventory playerInventory, @Nullable PipeBlockEntity pipe, ContainerData slotColors) {
    super(TransportMenuTypes.EMZULI_PIPE_MENU.get(), containerId, playerInventory);
    this.pipe = pipe;
    this.slotColors = slotColors;
    EmzuliState state = pipe != null ? pipe.getEmzuliState() : null;
    // Fall back to a dummy container so the slot count still matches the server.
    Container filterInv = state != null ? state.getFilters() : new SimpleContainer(EmzuliState.SLOTS);

    addSlot(new SlotPhantom(filterInv, 0, 25, 21));
    addSlot(new SlotPhantom(filterInv, 1, 134, 21));
    addSlot(new SlotPhantom(filterInv, 2, 25, 49));
    addSlot(new SlotPhantom(filterInv, 3, 134, 49));

    for (int l = 0; l < 3; l++) {
      for (int k1 = 0; k1 < 9; k1++) {
        addSlot(new Slot(playerInventory, k1 + l * 9 + 9, 8 + k1 * 18, 84 + l * 18));
      }
    }
    for (int i1 = 0; i1 < 9; i1++) {
      addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
    }
    addDataSlots(slotColors);
  }

  private static ContainerData serverData(PipeBlockEntity pipe) {
    return new ContainerData() {
      @Override
      public int get(int index) {
        EmzuliState state = pipe.getEmzuliState();
        return state != null ? state.getSlotColorData(index) : 0;
      }

      @Override
      public void set(int index, int value) {
        EmzuliState state = pipe.getEmzuliState();
        if (state != null) state.setSlotColor(index, EmzuliState.decode(value));
      }

      @Override
      public int getCount() {
        return EmzuliState.SLOTS;
      }
    };
  }

  @Nullable
  private static PipeBlockEntity resolvePipe(Inventory playerInventory, @Nullable FriendlyByteBuf buf) {
    if (buf == null) return null;
    BlockEntity be = playerInventory.player.level().getBlockEntity(buf.readBlockPos());
    return be instanceof PipeBlockEntity pipeBE ? pipeBE : null;
  }

  /** Menu button id for a paint button click: {@code slot * 3 + mouseButton}. */
  public static int buttonId(int slot, int mouseButton) {
    return slot * 3 + mouseButton;
  }

  /** The colour shown on a paint button, or null for "don't paint". */
  @Nullable
  public DyeColor getSlotColor(int slot) {
    return EmzuliState.decode(slotColors.get(slot));
  }

  /**
   * Original {@code PaintWidget.onRelease()} + {@code readGuiData()}: left click next colour, right click previous,
   * middle click none.
   */
  @Override
  public boolean clickMenuButton(Player player, int id) {
    int slot = id / 3;
    int button = id % 3;
    if (pipe == null || slot < 0 || slot >= EmzuliState.SLOTS) return false;
    EmzuliState state = pipe.getEmzuliState();
    if (state == null) return false;
    DyeColor color = button == BUTTON_MIDDLE ? null : EmzuliState.cycle(state.getSlotColor(slot), button == BUTTON_LEFT);
    state.setSlotColor(slot, color);
    pipe.setChanged();
    return true;
  }

  @Nullable
  public PipeBlockEntity getPipe() {
    return pipe;
  }

  @Override
  public boolean stillValid(Player player) {
    return pipe != null && !pipe.isRemoved() && player.distanceToSqr(pipe.getBlockPos().getX() + 0.5D,
      pipe.getBlockPos().getY() + 0.5D, pipe.getBlockPos().getZ() + 0.5D) <= 64.0D;
  }
}
