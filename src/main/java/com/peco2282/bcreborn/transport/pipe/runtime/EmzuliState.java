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
package com.peco2282.bcreborn.transport.pipe.runtime;

import com.peco2282.bcreborn.common.SimpleInventory;
import java.util.BitSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Runtime state of an Emzuli pipe (original {@code PipeItemsEmzuli} fields), owned by the pipe block entity.
 * <ul>
 *   <li>{@link #getFilters()}: four filter slots (one per extraction preset: red, blue, green, yellow)</li>
 *   <li>slot colours: the paint applied to items extracted through each slot, or none</li>
 *   <li>the current slot, advanced every tick to the next slot that has a filter and an active preset</li>
 *   <li>active presets: set by the gate's extraction preset actions; not saved (as in the original)</li>
 * </ul>
 */
public final class EmzuliState {
  public static final int SLOTS = 4;
  /** Original preset order: slot 0 = red, 1 = blue, 2 = green, 3 = yellow. */
  public static final DyeColor[] PRESET_COLORS = {DyeColor.RED, DyeColor.BLUE, DyeColor.GREEN, DyeColor.YELLOW};

  private final SimpleInventory filters;
  /** 0 = no paint, otherwise {@code DyeColor.getId() + 1} (the original stored {@code EnumColor.ordinal() + 1}). */
  private final byte[] slotColors = new byte[SLOTS];
  private final BitSet activeFlags = new BitSet(SLOTS);
  private int currentFilter;

  public EmzuliState(Runnable onChanged) {
    this.filters = new SimpleInventory(SLOTS, "Filters", 1) {
      @Override
      public void setChanged() {
        onChanged.run();
      }
    };
  }

  public SimpleInventory getFilters() {
    return filters;
  }

  public int currentSlot() {
    return currentFilter % SLOTS;
  }

  public ItemStack getCurrentFilter() {
    return filters.getItem(currentSlot());
  }

  /** Original {@code checkExtract()}: extraction runs only for a slot that has a filter and an active preset. */
  public boolean canExtractCurrent() {
    return !activeFlags.isEmpty() && !getCurrentFilter().isEmpty() && activeFlags.get(currentSlot());
  }

  /** The paint for items extracted through the current slot (original {@code makeItem()}). */
  @Nullable
  public DyeColor currentPaint() {
    return getSlotColor(currentSlot());
  }

  @Nullable
  public DyeColor getSlotColor(int slot) {
    return decode(slotColors[slot]);
  }

  public void setSlotColor(int slot, @Nullable DyeColor color) {
    slotColors[slot] = (byte) encode(color);
  }

  /** Raw value for menu sync: 0 = none, otherwise colour id + 1. */
  public int getSlotColorData(int slot) {
    return slotColors[slot];
  }

  public static int encode(@Nullable DyeColor color) {
    return color == null ? 0 : color.getId() + 1;
  }

  @Nullable
  public static DyeColor decode(int data) {
    return data <= 0 || data > 16 ? null : DyeColor.byId(data - 1);
  }

  /**
   * The original GUI cycles through {@code [none, BLACK, RED, GREEN, ..., WHITE]} (EnumColor order, i.e. DyeColor
   * ids 15 down to 0). Left click moves forward, right click backward.
   */
  @Nullable
  public static DyeColor cycle(@Nullable DyeColor color, boolean forward) {
    // Position in the revolving list: 0 = none, 1 = BLACK (id 15) ... 16 = WHITE (id 0).
    int index = color == null ? 0 : 16 - color.getId();
    index = Math.floorMod(index + (forward ? 1 : -1), 17);
    return index == 0 ? null : DyeColor.byId(16 - index);
  }

  public boolean isPresetActive(int slot) {
    return activeFlags.get(slot);
  }

  public void clearPresets() {
    activeFlags.clear();
  }

  /** Original {@code setActivePreset()}: only red, blue, green and yellow map to a slot. */
  public void activatePreset(DyeColor color) {
    for (int i = 0; i < PRESET_COLORS.length; i++) {
      if (PRESET_COLORS[i] == color) {
        activeFlags.set(i);
      }
    }
  }

  /** Original {@code incrementFilter()}: moves to the next slot with a filter and an active preset. */
  public void incrementFilter() {
    int count = 0;
    currentFilter++;
    while (!(!filters.getItem(currentSlot()).isEmpty() && activeFlags.get(currentSlot())) && count < SLOTS) {
      currentFilter++;
      count++;
    }
    currentFilter = currentSlot();
  }

  public void load(CompoundTag tag) {
    filters.readTag(tag);
    currentFilter = Math.max(0, tag.getInt("currentFilter"));
    for (int slot = 0; slot < SLOTS; slot++) {
      slotColors[slot] = tag.getByte("slotColors[" + slot + "]");
    }
  }

  public void save(CompoundTag tag) {
    filters.writeTag(tag);
    tag.putInt("currentFilter", currentFilter);
    for (int slot = 0; slot < SLOTS; slot++) {
      tag.putByte("slotColors[" + slot + "]", slotColors[slot]);
    }
  }
}
