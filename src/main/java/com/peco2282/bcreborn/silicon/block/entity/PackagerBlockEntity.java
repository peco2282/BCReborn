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

import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PackagerBlockEntity extends BuildCraftBlockEntity implements WorldlyContainer {
  protected SimpleInventory inv = new SimpleInventory(10, "inv", 64);

  public PackagerBlockEntity(BlockPos pos, BlockState state) {
    super(SiliconBlockEntityTypes.PACKAGER.get(), pos, state);
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    // TODO: Implement packager logic
  }

  @Override
  public int[] getSlotsForFace(net.minecraft.core.Direction side) {
    return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
  }

  @Override
  public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable net.minecraft.core.Direction side) {
    return true;
  }

  @Override
  public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction side) {
    return true;
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
    return inv.removeItem(slot, amount);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return inv.removeItemNoUpdate(slot);
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    inv.setItem(slot, stack);
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
    inv.readFromNBT(nbt, "inv");
  }

  @Override
  public void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    inv.writeToNBT(nbt, "inv");
  }
}
