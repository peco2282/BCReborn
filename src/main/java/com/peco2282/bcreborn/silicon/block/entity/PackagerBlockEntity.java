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
package com.peco2282.bcreborn.silicon.block.entity;

import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.inventory.MachineItemHandler;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.silicon.item.PackageItem;
import com.peco2282.bcreborn.silicon.menu.PackagerMenu;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import com.peco2282.bcreborn.silicon.SiliconItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class PackagerBlockEntity extends BuildCraftBlockEntity implements MenuProvider, WorldlyContainer {
  protected SimpleInventory inv = new SimpleInventory(12, "inv", 64);
  public final SimpleInventory pattern = new SimpleInventory(9, "pattern", 1) {
    @Override public void setChanged() { PackagerBlockEntity.this.setChanged(); }
  };
  private LazyOptional<IItemHandler> itemCapability =
    LazyOptional.of(() -> new MachineItemHandler(
      this, slot -> slot >= 0 && slot <= 9, slot -> slot == 11));

  @Override
  public <T> LazyOptional<T> getCapability(
      Capability<T> cap, Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER) return itemCapability.cast();
    return super.getCapability(cap, side);
  }

  @Override public void invalidateCaps() { super.invalidateCaps(); itemCapability.invalidate(); }
  @Override public void reviveCaps() {
    super.reviveCaps();
    itemCapability = LazyOptional.of(() ->
      new MachineItemHandler(this, slot -> slot >= 0 && slot <= 9, slot -> slot == 11));
  }

  public PackagerBlockEntity(BlockPos pos, BlockState state) {
    super(SiliconBlockEntityTypes.PACKAGER.get(), pos, state);
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide || level.getGameTime() % 5 != 0 || !getItem(11).isEmpty()) return;
    ItemStack wrapper = getItem(9);
    boolean extending = wrapper.getItem() instanceof PackageItem;
    if (!extending && !wrapper.is(Items.PAPER)) return;
    ItemStack result = extending ? wrapper.copyWithCount(1)
      : new ItemStack(SiliconItems.PACKAGE_ITEM.get());
    var next = MachineInventory.copy(this);
    boolean filled = false;
    for (int i = 0; i < 9; i++) {
      ItemStack wanted = pattern.getItem(i);
      if (wanted.isEmpty()) continue;
      if (result.getOrCreateTag().contains("item" + i)) return;
      int found = -1;
      for (int slot = 0; slot < 9; slot++) {
        if (!next[slot].isEmpty() && ItemStack.isSameItemSameTags(wanted, next[slot])) { found = slot; break; }
      }
      if (found < 0) return;
      result.getOrCreateTag().put("item" + i, next[found].copyWithCount(1).save(new CompoundTag()));
      next[found].shrink(1);
      filled = true;
    }
    if (!filled) return;
    next[9].shrink(1);
    next[11] = result;
    MachineInventory.commit(this, next);
  }

  @Override
  public int[] getSlotsForFace(Direction side) {
    return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11};
  }

  @Override
  public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
    return canPlaceItem(slot, stack);
  }

  @Override
  public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
    return slot == 11;
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    return slot >= 0 && slot < 9 || slot == 9 && (stack.is(Items.PAPER)
      || stack.getItem() instanceof PackageItem);
  }

  @Override
  public int getContainerSize() {
    return inv.getContainerSize();
  }

  @Override
  public boolean isEmpty() {
    return inv.isEmpty();
  }

  @Override
  public ItemStack getItem(int slot) {
    return inv.getItem(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    ItemStack result = inv.removeItem(slot, amount);
    if (!result.isEmpty()) setChanged();
    return result;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return inv.removeItemNoUpdate(slot);
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    inv.setItem(slot, stack);
    setChanged();
  }

  @Override
  public boolean stillValid(Player player) {
    return super.stillValid(player);
  }

  @Override
  public void clearContent() {
    inv.clearContent();
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    inv.readTag(nbt);
    pattern.readTag(nbt.getCompound("pattern"));
  }

  @Override
  public void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    inv.writeTag(nbt);
    CompoundTag savedPattern = new CompoundTag();
    pattern.writeTag(savedPattern);
    nbt.put("pattern", savedPattern);
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornsilicon.packager");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
    return new PackagerMenu(p_39954_, p_39955_, this);
  }
}
