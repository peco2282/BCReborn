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
package com.peco2282.bcreborn.transport.block.entity;

import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.transport.TransportBlockEntityTypes;
import com.peco2282.bcreborn.transport.menu.FilteredBufferMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Original {@code TileFilteredBuffer}: nine storage slots, each holding only the item set in the filter slot above
 * it (any damage for damageable items). Every side reaches all nine slots.
 */
public class FilteredBufferBlockEntity extends BuildCraftBlockEntity implements Container, MenuProvider {
  public static final int SLOTS = 9;

  private final SimpleInventory filters = new SimpleInventory(SLOTS, "FilteredBufferFilters", 1);
  private final SimpleInventory storage = new SimpleInventory(SLOTS, "FilteredBufferStorage", 64);
  private final LazyOptional<InvWrapper> itemHandler = LazyOptional.of(() -> new InvWrapper(this));

  public FilteredBufferBlockEntity(BlockPos pos, BlockState state) {
    super(TransportBlockEntityTypes.FILTERED_BUFFER.get(), pos, state);
  }

  public SimpleInventory getFilters() {
    return filters;
  }

  /** Original {@code isItemValidForSlot()}. */
  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    ItemStack filter = filters.getItem(slot);
    if (filter.isEmpty() || filter.getItem() != stack.getItem()) return false;
    return stack.isDamageableItem() || ItemStack.isSameItemSameTags(filter, stack);
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
  }

  @Override
  public int getContainerSize() {
    return storage.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return storage.isEmpty();
  }

  @Override
  public ItemStack getItem(int slot) {
    return storage.getItem(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int count) {
    ItemStack removed = storage.removeItem(slot, count);
    setChanged();
    return removed;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return storage.removeItemNoUpdate(slot);
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    storage.setItem(slot, stack);
    setChanged();
  }

  @Override
  public int getMaxStackSize() {
    return storage.getMaxStackSize();
  }

  @Override
  public boolean stillValid(Player player) {
    return super.stillValid(player);
  }

  @Override
  public void clearContent() {
    storage.clearContent();
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    CompoundTag storageTag = new CompoundTag();
    storage.writeTag(storageTag);
    tag.put("inventoryStorage", storageTag);
    CompoundTag filterTag = new CompoundTag();
    filters.writeTag(filterTag);
    tag.put("inventoryFilters", filterTag);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    storage.readTag(tag.getCompound("inventoryStorage"));
    filters.readTag(tag.getCompound("inventoryFilters"));
  }

  @Override
  public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER) return itemHandler.cast();
    return super.getCapability(cap, side);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    itemHandler.invalidate();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcreborntransport.filtered_buffer_menu");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
    return new FilteredBufferMenu(id, inventory, this);
  }
}
