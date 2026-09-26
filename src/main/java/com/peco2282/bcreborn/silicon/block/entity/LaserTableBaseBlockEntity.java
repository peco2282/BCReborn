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

import com.peco2282.bcreborn.api.power.ILaserTarget;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.inventory.MachineItemHandler;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.utils.AverageInt;
import com.peco2282.bcreborn.common.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public abstract class LaserTableBaseBlockEntity extends BuildCraftBlockEntity implements MenuProvider, ILaserTarget, Container, IHasWork {
  private final AverageInt recentEnergyAverageUtil = new AverageInt(20);
  public int clientRequiredEnergy = 0;
  protected SimpleInventory inv = new SimpleInventory(getContainerSize(), "inv", 64);
  private int energy = 0;
  private int recentEnergyAverage;
  private LazyOptional<IItemHandler> itemCapability =
    LazyOptional.of(() -> new MachineItemHandler(
      this, this::isMachineInput, this::isMachineOutput));

  protected boolean isMachineInput(int slot) { return true; }
  protected boolean isMachineOutput(int slot) { return true; }

  public void dropRealInventory() {
    if (level == null || level.isClientSide) return;
    for (int slot = 0; slot < getContainerSize(); slot++) {
      if (isMachineInput(slot) || isMachineOutput(slot)) {
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(),
          worldPosition.getZ(), getItem(slot).copy());
      }
    }
    clearContent();
    setChanged();
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) { return isMachineInput(slot); }

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
    itemCapability = LazyOptional.of(() ->
      new MachineItemHandler(this, this::isMachineInput, this::isMachineOutput));
  }

  public LaserTableBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    recentEnergyAverageUtil.tick();
    recentEnergyAverage = (int) Math.round(recentEnergyAverageUtil.getAverage());
  }

  public int getEnergy() {
    return energy;
  }

  public void setEnergy(int energy) {
    this.energy = energy;
  }

  public void addEnergy(int energy) {
    this.energy += energy;
  }

  public void subtractEnergy(int energy) {
    this.energy -= energy;
  }

  public abstract int getRequiredEnergy();

  public int getProgressScaled(int ratio) {
    if (clientRequiredEnergy == 0) {
      return 0;
    } else if (energy >= clientRequiredEnergy) {
      return ratio;
    } else {
      return (int) ((double) energy / (double) clientRequiredEnergy * ratio);
    }
  }

  public int getRecentEnergyAverage() {
    return recentEnergyAverage;
  }

  public abstract boolean canCraft();

  @Override
  public boolean requiresLaserEnergy() {
    return canCraft() && energy < getRequiredEnergy() * 5F;
  }

  @Override
  public void receiveLaserEnergy(int energy) {
    if (level == null || level.isClientSide || energy <= 0) return;
    this.energy = (int) Math.min(Integer.MAX_VALUE, (long) this.energy + energy);
    recentEnergyAverageUtil.push(energy);
    setChanged();
  }

  @Override
  public boolean isInvalidTarget() {
    return isRemoved();
  }

  @Override
  public double getXCoord() {
    return worldPosition.getX();
  }

  @Override
  public double getYCoord() {
    return worldPosition.getY();
  }

  @Override
  public double getZCoord() {
    return worldPosition.getZ();
  }

  @Override
  public int getContainerSize() {
    return 0;
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
    ItemStack removed = inv.removeItem(slot, amount);
    if (!removed.isEmpty()) setChanged();
    return removed;
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
  public int getMaxStackSize() {
    return inv.getMaxStackSize();
  }

  @Override
  public void setChanged() {
    super.setChanged();
    inv.setChanged();
  }

  @Override
  public void clearContent() {
    inv.clearContent();
  }

  @Override
  public void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    inv.writeTag(nbt);
    nbt.putInt("energy", energy);
    nbt.putInt("recentEnergyAverage", recentEnergyAverage);
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    inv.readTag(nbt);
    energy = nbt.getInt("energy");
    recentEnergyAverage = nbt.getInt("recentEnergyAverage");
  }

  protected void outputStack(ItemStack remaining, boolean autoEject) {
    outputStack(remaining, null, 0, autoEject);
  }

  protected void outputStack(ItemStack remaining, Container inv, int slot, boolean autoEject) {
    if (autoEject) {
      if (!remaining.isEmpty()) {
        int added = Utils.addToRandomInventoryAround(level, worldPosition, remaining);
        remaining.shrink(added);
      }

      if (!remaining.isEmpty()) {
        int added = Utils.addToRandomInjectableAround(level, worldPosition, Direction.UP, remaining);
        remaining.shrink(added);
      }
    }

    if (inv != null && !remaining.isEmpty()) {
      ItemStack inside = inv.getItem(slot);

      if (inside.isEmpty()) {
        inv.setItem(slot, remaining.copy());
        remaining.setCount(0);
        return;
      } else if (ItemStack.isSameItemSameTags(inside, remaining)) {
        int move = Math.min(remaining.getCount(), inside.getMaxStackSize() - inside.getCount());
        inside.grow(move);
        remaining.shrink(move);
      }
    }

    if (!remaining.isEmpty()) {
      ItemEntity entityitem = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.7, worldPosition.getZ() + 0.5, remaining);
      level.addFreshEntity(entityitem);
    }
  }
}
