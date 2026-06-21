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
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TankBlockEntity extends BuildCraftBlockEntity implements IFluidHandler {
  public final FluidTank tank = new FluidTank(16000);
  private final int prevLightValue = 0;
  public boolean hasUpdate = false;
  public boolean hasNetworkUpdate = false;
  public SafeTimeTracker tracker = new SafeTimeTracker(20); // Placeholder
  private int cachedComparatorOverride = 0;

  public TankBlockEntity(BlockPos pos, BlockState state) {
    super(FactoryBlockEntityTypes.TANK.get(), pos, state);
  }

  public static TankBlockEntity getTankBelow(TankBlockEntity tile) {
    BlockEntity below = tile.getLevel().getBlockEntity(tile.getBlockPos().below());
    if (below instanceof TankBlockEntity) {
      return (TankBlockEntity) below;
    }
    return null;
  }

  public static TankBlockEntity getTankAbove(TankBlockEntity tile) {
    BlockEntity above = tile.getLevel().getBlockEntity(tile.getBlockPos().above());
    if (above instanceof TankBlockEntity) {
      return (TankBlockEntity) above;
    }
    return null;
  }

  @Override
  public void initialize() {
    super.initialize();
    updateComparators();
  }

  protected void updateComparators() {
    int co = calculateComparatorInputOverride();
    TankBlockEntity uTank = getBottomTank();
    while (uTank != null) {
      uTank.cachedComparatorOverride = co;
      uTank.hasUpdate = true;
      uTank = getTankAbove(uTank);
    }
  }

  @Override
  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      return;
    }

    if (tank.getFluidAmount() > 0) {
      moveFluidBelow();
    }

    if (hasUpdate) {
      level.updateNeighborsAt(pos, state.getBlock());
      hasUpdate = false;
    }

    if (hasNetworkUpdate && tracker.markTimeIfDelay(level)) {
      level.sendBlockUpdated(pos, state, state, 3);
      hasNetworkUpdate = false;
    }
  }

  @Override
  public void load(CompoundTag nbt) {
    super.load(nbt);
    tank.readFromNBT(nbt.getCompound("tank"));
  }

  @Override
  protected void saveAdditional(CompoundTag nbt) {
    super.saveAdditional(nbt);
    CompoundTag tankNbt = new CompoundTag();
    tank.writeToNBT(tankNbt);
    nbt.put("tank", tankNbt);
  }

  public TankBlockEntity getBottomTank() {
    TankBlockEntity lastTank = this;
    while (true) {
      TankBlockEntity below = getTankBelow(lastTank);
      if (below != null) {
        lastTank = below;
      } else {
        break;
      }
    }
    return lastTank;
  }

  public TankBlockEntity getTopTank() {
    TankBlockEntity lastTank = this;
    while (true) {
      TankBlockEntity above = getTankAbove(lastTank);
      if (above != null) {
        lastTank = above;
      } else {
        break;
      }
    }
    return lastTank;
  }

  public void moveFluidBelow() {
    TankBlockEntity below = getTankBelow(this);
    if (below == null) return;

    int oldComparator = getComparatorInputOverride();
    int used = below.tank.fill(tank.getFluid(), FluidAction.EXECUTE);

    if (used > 0) {
      hasNetworkUpdate = true;
      below.hasNetworkUpdate = true;
      tank.drain(used, FluidAction.EXECUTE);
      if (oldComparator != calculateComparatorInputOverride()) {
        updateComparators();
      }
    }
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public @NotNull FluidStack getFluidInTank(int tank) {
    return this.tank.getFluid();
  }

  @Override
  public int getTankCapacity(int tank) {
    return this.tank.getCapacity();
  }

  @Override
  public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
    return this.tank.isFluidValid(stack);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (resource.isEmpty()) return 0;
    TankBlockEntity bottom = getBottomTank();
    int totalFilled = 0;
    FluidStack toFill = resource.copy();
    TankBlockEntity current = bottom;
    while (current != null && !toFill.isEmpty()) {
      int filled = current.tank.fill(toFill, action);
      toFill.shrink(filled);
      totalFilled += filled;
      if (filled > 0 && action.execute()) current.hasNetworkUpdate = true;
      current = getTankAbove(current);
    }
    return totalFilled;
  }

  @Override
  public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
    TankBlockEntity bottom = getBottomTank();
    return bottom.tank.drain(resource, action);
  }

  @Override
  public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
    TankBlockEntity bottom = getBottomTank();
    return bottom.tank.drain(maxDrain, action);
  }

  public int calculateComparatorInputOverride() {
    return tank.getFluidAmount() * 15 / tank.getCapacity();
  }

  public int getComparatorInputOverride() {
    return cachedComparatorOverride;
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.FLUID_HANDLER) {
      return LazyOptional.of(() -> tank).cast();
    }
    return super.getCapability(cap, side);
  }
}
