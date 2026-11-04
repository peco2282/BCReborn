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

import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Exposes the Diamond Pipe's 6 sides x 9 slots of filters as a single 54-slot inventory,
 * matching the original {@code PipeItemsDiamond.SimpleFilterInventory}.
 * <p>
 * Slot index is {@code direction.get3DDataValue() * 9 + slot} (the original's {@code x + y * 9}).
 * Any change marks the owning {@link PipeBlockEntity} dirty so it is saved to NBT.
 */
public final class DiamondPipeFilterContainer implements Container {
  public static final int SLOTS_PER_SIDE = 9;
  public static final int SIZE = SLOTS_PER_SIDE * 6;

  private final PipeBlockEntity pipe;

  public DiamondPipeFilterContainer(PipeBlockEntity pipe) {
    this.pipe = pipe;
  }

  private static Direction sideOf(int index) {
    return Direction.from3DDataValue(index / SLOTS_PER_SIDE);
  }

  private Container filterOf(int index) {
    return pipe.getFilter(sideOf(index));
  }

  private boolean inRange(int index) {
    return index >= 0 && index < SIZE;
  }

  @Override
  public int getContainerSize() {
    return SIZE;
  }

  @Override
  public boolean isEmpty() {
    for (Direction dir : Direction.values()) {
      if (!pipe.getFilter(dir).isEmpty()) return false;
    }
    return true;
  }

  @Override
  public ItemStack getItem(int index) {
    if (!inRange(index)) return ItemStack.EMPTY;
    return filterOf(index).getItem(index % SLOTS_PER_SIDE);
  }

  @Override
  public ItemStack removeItem(int index, int count) {
    if (!inRange(index)) return ItemStack.EMPTY;
    ItemStack removed = filterOf(index).removeItem(index % SLOTS_PER_SIDE, count);
    if (!removed.isEmpty()) setChanged();
    return removed;
  }

  @Override
  public ItemStack removeItemNoUpdate(int index) {
    if (!inRange(index)) return ItemStack.EMPTY;
    return filterOf(index).removeItemNoUpdate(index % SLOTS_PER_SIDE);
  }

  @Override
  public void setItem(int index, ItemStack stack) {
    if (!inRange(index)) return;
    filterOf(index).setItem(index % SLOTS_PER_SIDE, stack);
    setChanged();
  }

  @Override
  public int getMaxStackSize() {
    // Original: new SimpleFilterInventory(54, "Filters", 1)
    return 1;
  }

  @Override
  public void setChanged() {
    pipe.setChanged();
  }

  @Override
  public boolean stillValid(Player player) {
    return Container.stillValidBlockEntity(pipe, player);
  }

  @Override
  public void clearContent() {
    for (Direction dir : Direction.values()) {
      pipe.getFilter(dir).clearContent();
    }
    setChanged();
  }
}
