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

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import com.peco2282.bcreborn.silicon.menu.ChargingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ChargingTableBlockEntity extends LaserTableBaseBlockEntity {
  public ChargingTableBlockEntity(BlockPos pos, BlockState state) {
    super(SiliconBlockEntityTypes.CHARGING_TABLE.get(), pos, state);
  }

  @Nullable
  private IEnergyStorage itemEnergy() {
    ItemStack stack = getItem(0);
    return stack.isEmpty() ? null : stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
  }

  /** Original updateEntity(): laser energy goes into the item; without anything to charge it drains away. */
  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    super.tick(level, pos, state);
    if (level.isClientSide) {
      return;
    }

    if (getEnergy() > 0) {
      IEnergyStorage storage = itemEnergy();
      if (storage != null && getRequiredEnergy() > 0) {
        subtractEnergy(storage.receiveEnergy(getEnergy(), false));
        setChanged();
      } else {
        subtractEnergy(Math.min(getEnergy(), 10));
      }
    }
  }

  @Override
  public int getRequiredEnergy() {
    IEnergyStorage storage = itemEnergy();
    return storage != null ? Math.max(0, storage.getMaxEnergyStored() - storage.getEnergyStored()) : 0;
  }

  @Override
  public boolean canCraft() {
    return hasWork();
  }

  @Override
  public int getContainerSize() {
    return 1;
  }

  @Override
  public boolean hasWork() {
    return getRequiredEnergy() > 0;
  }

  /** Original isItemValidForSlot(): only items that can hold energy. */
  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    return slot == 0 && !stack.isEmpty() && stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornsilicon.charging_table");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
    return new ChargingTableMenu(p_39954_, p_39955_, this);
  }
}
