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
package com.peco2282.bcreborn.transport.pipe.extraction;

import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Emerald pipe extraction (original {@code PipeItemsEmerald.checkExtract()}): the wooden pipe's pull, restricted by
 * the filters. White and black list take the first allowed stack, as much of it as the energy pays for; round robin
 * takes one item of the current filter per pull and then moves on to the next non-empty filter.
 */
public class FilteredItemExtractionModule implements ExtractionModule {
  public static final FilteredItemExtractionModule INSTANCE = new FilteredItemExtractionModule();

  private FilteredItemExtractionModule() {
  }

  @Override
  public void extract(PipeBlockEntity pipe) {
    SimpleInventory filters = pipe.getEmeraldFilters();
    if (pipe.getExtractFilterMode() == PipeBlockEntity.ExtractFilterMode.ROUND_ROBIN) {
      int[] taken = {-1};
      boolean pulled = StandardItemExtractionModule.INSTANCE.pull(pipe, inventory -> {
        StandardItemExtractionModule.Pick pick = roundRobin(pipe, filters, inventory);
        taken[0] = pick == null ? -1 : pick.slot();
        return pick;
      }, null);
      // Original: the filter only moves on after an item of it was taken.
      if (pulled && taken[0] >= 0) incrementFilter(pipe, filters);
      return;
    }
    boolean whiteList = pipe.getExtractFilterMode() == PipeBlockEntity.ExtractFilterMode.WHITE_LIST;
    StandardItemExtractionModule.INSTANCE.pull(pipe,
      inventory -> StandardItemExtractionModule.firstMatching(inventory, stack -> isFiltered(filters, stack) == whiteList), null);
  }

  private static boolean isFiltered(SimpleInventory filters, ItemStack stack) {
    for (int i = 0; i < filters.getContainerSize(); i++) {
      ItemStack filter = filters.getItem(i);
      if (!filter.isEmpty() && ItemStack.isSameItemSameTags(stack, filter)) return true;
    }
    return false;
  }

  /** Original {@code checkExtractRoundRobin()}: one item matching the current filter. */
  @Nullable
  private static StandardItemExtractionModule.Pick roundRobin(PipeBlockEntity pipe, SimpleInventory filters, IItemHandler inventory) {
    ItemStack filter = currentFilter(pipe, filters);
    if (filter.isEmpty()) return null;
    StandardItemExtractionModule.Pick pick = StandardItemExtractionModule.firstMatching(inventory,
      stack -> ItemStack.isSameItemSameTags(stack, filter));
    return pick == null ? null : new StandardItemExtractionModule.Pick(pick.slot(), 1);
  }

  /** Original {@code getCurrentFilter()}: the current filter, moving on if it is empty. */
  private static ItemStack currentFilter(PipeBlockEntity pipe, SimpleInventory filters) {
    int size = filters.getContainerSize();
    if (size == 0) return ItemStack.EMPTY;
    int index = Math.floorMod(pipe.getFilterSlotIndex(), size);
    if (filters.getItem(index).isEmpty()) {
      incrementFilter(pipe, filters);
      index = Math.floorMod(pipe.getFilterSlotIndex(), size);
    }
    return filters.getItem(index);
  }

  /** Original {@code incrementFilter()}: the next non-empty filter slot. */
  private static void incrementFilter(PipeBlockEntity pipe, SimpleInventory filters) {
    int size = filters.getContainerSize();
    int index = (Math.floorMod(pipe.getFilterSlotIndex(), size) + 1) % size;
    int count = 0;
    while (filters.getItem(index).isEmpty() && count < size) {
      index = (index + 1) % size;
      count++;
    }
    pipe.setFilterSlotIndex(index);
  }
}
