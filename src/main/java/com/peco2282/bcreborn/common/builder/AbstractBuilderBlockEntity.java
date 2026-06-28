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
package com.peco2282.bcreborn.common.builder;

import com.peco2282.bcreborn.api.tiles.IDebuggable;
import com.peco2282.bcreborn.common.IBlockEntityContainer;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.blueprint.RequirementItemStack;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.energy.fluids.Tank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class AbstractBuilderBlockEntity extends BuildCraftBlockEntity implements IBuildingItemsProvider, IBlockEntityContainer, IDebuggable {

  public List<RequirementItemStack> requiredToBuild = new ArrayList<>();

  public AbstractBuilderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public abstract List<ItemStack> getInventoryList();

  public EnergyStorage getEnergyStorage() {
    return null; // Should be overridden if needed
  }

  public List<Tank> getFluidTanks() {
    return Collections.emptyList();
  }

  public boolean drainBuild(FluidStack stack, boolean doDrain) {
    return false;
  }

  public int energyAvailable() {
    return 0;
  }

  public boolean isBuildingMaterial(int i) {
    return true;
  }

  public boolean consumeEnergyToDoWork() {
    return true;
  }

  @Override
  public Collection<BuildingItem> getBuilders() {
    return Collections.emptyList();
  }

  public boolean consumeEnergy(int energy) {
    return false;
  }

  public boolean isBuildingMaterialSlot(int index) {
    return isBuildingMaterial(index);
  }

  @Override
  public AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
    return null;
  }

  @Override
  public Component getName() {
    return Component.literal("Builder");
  }

  @Override
  public int getContainerSize() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public ItemStack getItem(int p_18942_) {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeItem(int p_18942_, int p_18943_) {
    return ItemStack.EMPTY;
  }

  @Override
  public ItemStack removeItemNoUpdate(int p_18951_) {
    return ItemStack.EMPTY;
  }

  @Override
  public void setItem(int p_18944_, ItemStack p_18945_) {
  }

  @Override
  public void clearContent() {
  }

  @Override
  public void addAndLaunchBuildingItem(BuildingItem item) {
  }

  @Override
  public void getDebugInfo(List<String> info, Direction side, ItemStack debugger, Player player) {
    info.add("Machine Info:");
    EnergyStorage storage = getEnergyStorage();
    if (storage != null) {
      long time = level != null ? level.getGameTime() : 0;
      info.add(String.format("  Storage  : %d / %d RF", storage.getEnergyStored(), storage.getMaxEnergyStored()));
      info.add(String.format("  Received : %d RF/tick (%d RF/s)", storage.getLastTickReceived(time), storage.getLastTickReceived(time) * 20));
      info.add(String.format("  Used     : %d RF/tick (%d RF/s)", storage.getLastTickExtracted(time), storage.getLastTickExtracted(time) * 20));
    } else {
      info.add("  No energy storage");
    }
  }
}
