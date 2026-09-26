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
package com.peco2282.bcreborn.factory.block.entity;

import com.peco2282.bcreborn.api.power.IRedstoneEngineReceiver;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import com.peco2282.bcreborn.factory.menu.HopperMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class HopperBlockEntity extends BuildCraftBlockEntity implements Container, IRedstoneEngineReceiver, IEnergyStorage, MenuProvider {

  private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
  private boolean isEmpty = true;
  private LazyOptional<IItemHandler> itemCapability =
    LazyOptional.of(() -> new InvWrapper(this));

  @Override
  public <T> LazyOptional<T> getCapability(
      Capability<T> cap, Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER) return itemCapability.cast();
    return super.getCapability(cap, side);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    itemCapability.invalidate();
  }

  @Override
  public void reviveCaps() {
    super.reviveCaps();
    itemCapability = LazyOptional.of(() -> new InvWrapper(this));
  }

  public HopperBlockEntity(BlockPos pos, BlockState state) {
    super(FactoryBlockEntityTypes.HOPPER.get(), pos, state);
    this.setBattery(new EnergyStorage(10, 10, 0));
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return getBattery().receiveEnergy(maxReceive, simulate);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return getBattery().extractEnergy(maxExtract, simulate);
  }

  @Override
  public int getEnergyStored() {
    return getBattery().getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return getBattery().getMaxEnergyStored();
  }

  @Override
  public boolean canExtract() {
    return getBattery().canExtract();
  }

  @Override
  public boolean canReceive() {
    return getBattery().canReceive();
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    ContainerHelper.loadAllItems(nbt, this.inventory);
    updateIsEmpty();
    setChanged();
  }

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    ContainerHelper.saveAllItems(nbt, this.inventory);
  }

  @Override
  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide || isEmpty || level.getGameTime() % 2 != 0) {
      return;
    }

    if (!level.hasChunkAt(pos.below())) return;
    BlockEntity outputTile = level.getBlockEntity(pos.below());
    if (outputTile == null) return;
    IItemHandler handler = outputTile.getCapability(
      ForgeCapabilities.ITEM_HANDLER, Direction.UP).orElse(null);
    if (handler == null && outputTile instanceof WorldlyContainer sided) {
      handler = new SidedInvWrapper(sided, Direction.UP);
    } else if (handler == null && outputTile instanceof Container container) {
      handler = new InvWrapper(container);
    }
    if (handler == null) return;
    for (int slot = 0; slot < getContainerSize(); slot++) {
      if (getItem(slot).isEmpty()) continue;
      ItemStack offered = getItem(slot).copyWithCount(1);
      ItemStack remaining = ItemHandlerHelper.insertItemStacked(handler, offered, false);
      if (remaining.isEmpty()) {
        removeItem(slot, 1);
        outputTile.setChanged();
        return;
      }
    }
  }
  private void updateIsEmpty() {
    isEmpty = true;
    for (ItemStack stack : inventory) {
      if (!stack.isEmpty()) {
        isEmpty = false;
        break;
      }
    }
  }

  @Override
  public int getContainerSize() {
    return inventory.size();
  }

  @Override
  public boolean isEmpty() {
    return isEmpty;
  }

  @Override
  public ItemStack getItem(int slot) {
    return inventory.get(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int count) {
    ItemStack stack = ContainerHelper.removeItem(inventory, slot, count);
    updateIsEmpty();
    setChanged();
    return stack;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    ItemStack stack = ContainerHelper.takeItem(inventory, slot);
    updateIsEmpty();
    setChanged();
    return stack;
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    inventory.set(slot, stack);
    updateIsEmpty();
    setChanged();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornfactory.hopper");
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
    return new HopperMenu(id, playerInv, this);
  }

  @Override
  public boolean stillValid(Player player) {
    return super.stillValid(player);
  }

  @Override
  public void clearContent() {
    inventory.clear();
    isEmpty = true;
  }

  @Override
  public boolean canConnectRedstoneEngine(Direction side) {
    return side != Direction.UP && side != Direction.DOWN;
  }
}
