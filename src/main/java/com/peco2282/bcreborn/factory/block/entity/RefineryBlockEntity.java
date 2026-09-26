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

import com.peco2282.bcreborn.api.core.SafeTimeTracker;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import com.peco2282.bcreborn.factory.menu.RefineryMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;
import com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry;
import com.peco2282.bcreborn.api.recipes.RefineryRecipe;
import java.util.Comparator;
import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

public class RefineryBlockEntity extends BuildCraftBlockEntity implements IFluidHandler, IHasWork, IEnergyStorage, MenuProvider {
  private int progress;
  private String recipeId = "";
  private LazyOptional<IFluidHandler> fluidCapability =
    LazyOptional.of(() -> this);

  public static int LIQUID_PER_SLOT = 4000;
  private final SafeTimeTracker updateNetworkTime = new SafeTimeTracker(20);
  public FluidTank[] tanks = {new FluidTank(LIQUID_PER_SLOT), new FluidTank(LIQUID_PER_SLOT)};
  public FluidTank result = new FluidTank(LIQUID_PER_SLOT);
  public float animationSpeed = 1;
  public short animationStage = 0;
  public boolean isActive;

  public RefineryBlockEntity(BlockPos pos, BlockState state) {
    super(FactoryBlockEntityTypes.REFINERY.get(), pos, state);
    this.setBattery(new EnergyStorage(10000, 1500, 0));
  }

  @Override
  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      if (isActive) {
        animationStage += (short) animationSpeed;
        if (animationStage > 300) {
          animationStage -= 300;
        }
      } else {
        animationStage = 0;
      }
      return;
    }

    if (updateNetworkTime.markTimeIfDelay(level)) {
      level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
    }

    int previousProgress = progress;
    String previousRecipe = recipeId;
    var manager = BuildcraftRecipeRegistry.refinery();
    var recipe = manager == null ? null : manager.getRecipes().stream()
      .filter(r -> inputOrder(r) >= 0 && r.energy() >= 0 && !r.result().isEmpty())
      .sorted(Comparator.comparing(r -> r.id().toString())).findFirst().orElse(null);
    boolean wasActive = isActive;
    isActive = false;
    if (recipe == null) {
      progress = 0;
      recipeId = "";
    } else {
      if (!recipe.id().toString().equals(recipeId)) {
        progress = 0;
        recipeId = recipe.id().toString();
      }
      if (result.fill(recipe.result(), FluidAction.SIMULATE) == recipe.result().getAmount()
        && getBattery().getEnergyStored() >= recipe.energy()) {
        isActive = true;
        if (++progress >= Math.max(1, recipe.delay())) {
          int first = inputOrder(recipe);
          getBattery().useEnergy(recipe.energy(), recipe.energy(), false);
          tanks[first].drain(recipe.primaryAmount(), FluidAction.EXECUTE);
          if (recipe.secondary().isPresent()) tanks[1 - first].drain(recipe.secondaryAmount(), FluidAction.EXECUTE);
          result.fill(recipe.result().copy(), FluidAction.EXECUTE);
          progress = 0;
        }
      }
    }
    if (wasActive != isActive || isActive || previousProgress != progress || !previousRecipe.equals(recipeId)) setChanged();
  }

  private int inputOrder(RefineryRecipe recipe) {
    for (int first = 0; first < 2; first++) {
      if (matches(tanks[first], recipe.primary(), recipe.primaryAmount())
        && (recipe.secondary().isEmpty() || matches(tanks[1 - first], recipe.secondary().get(), recipe.secondaryAmount()))) return first;
    }
    return -1;
  }

  private boolean matches(FluidTank tank, Ingredient ingredient, int amount) {
    // 既存の Ingredient API は液体のバケツを識別子として使い、消費量はmBで指定する。
    return amount > 0 && tank.getFluidAmount() >= amount
      && ingredient.test(new ItemStack(tank.getFluid().getFluid().getBucket()));
  }

  @Override
  public <T> LazyOptional<T> getCapability(
    Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.FLUID_HANDLER) return fluidCapability.cast();
    return super.getCapability(cap, side);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    fluidCapability.invalidate();
  }

  @Override
  public void reviveCaps() {
    super.reviveCaps();
    fluidCapability = LazyOptional.of(() -> this);
  }

  @Override
  public void load(CompoundTag data) {
    super.load(data);
    tanks[0].readFromNBT(data.getCompound("tank1"));
    tanks[1].readFromNBT(data.getCompound("tank2"));
    result.readFromNBT(data.getCompound("result"));
    animationStage = data.getShort("animationStage");
    animationSpeed = data.getFloat("animationSpeed");
    isActive = data.getBoolean("isActive");
    progress = data.getInt("progress");
    recipeId = data.getString("recipeId");
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);
    CompoundTag t1 = new CompoundTag();
    tanks[0].writeToNBT(t1);
    data.put("tank1", t1);
    CompoundTag t2 = new CompoundTag();
    tanks[1].writeToNBT(t2);
    data.put("tank2", t2);
    CompoundTag tr = new CompoundTag();
    result.writeToNBT(tr);
    data.put("result", tr);
    data.putShort("animationStage", animationStage);
    data.putFloat("animationSpeed", animationSpeed);
    data.putBoolean("isActive", isActive);
    data.putInt("progress", progress);
    data.putString("recipeId", recipeId);
  }

  @Override
  public boolean hasWork() {
    return isActive;
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornfactory.refinery");
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
    return new RefineryMenu(id, playerInv, this);
  }

  @Override
  public boolean stillValid(Player player) {
    return super.stillValid(player);
  }

  @Override
  public int getTanks() {
    return 3;
  }

  @Override
  public FluidStack getFluidInTank(int tank) {
    if (tank < 2) return tanks[tank].getFluid();
    return result.getFluid();
  }

  @Override
  public int getTankCapacity(int tank) {
    return LIQUID_PER_SLOT;
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return tank < 2;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    // Simplified fill logic
    int filled = tanks[0].fill(resource, action);
    if (filled == 0) filled = tanks[1].fill(resource, action);
    if (filled > 0 && action.execute()) setChanged();
    return filled;
  }

  @Override
  public FluidStack drain(FluidStack resource, FluidAction action) {
    FluidStack drained = result.drain(resource, action);
    if (!drained.isEmpty() && action.execute()) setChanged();
    return drained;
  }

  @Override
  public FluidStack drain(int maxDrain, FluidAction action) {
    FluidStack drained = result.drain(maxDrain, action);
    if (!drained.isEmpty() && action.execute()) setChanged();
    return drained;
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
}
