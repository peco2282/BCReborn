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
import com.peco2282.bcreborn.api.power.IRedstoneEngineReceiver;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.internal.ILEDProvider;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class PumpBlockEntity extends BuildCraftBlockEntity implements IHasWork, IFluidHandler, IRedstoneEngineReceiver, ILEDProvider, IEnergyStorage {

  public static final int REBUID_DELAY = 512;
  public static int MAX_LIQUID = 16000;
  public FluidTank tank = new FluidTank(MAX_LIQUID);

  private double tubeY = Double.NaN;
  private int aimY = 0;

  private int tick = 0;
  private final int tickPumped = -20;
  private boolean powered = false;

  private int ledState;
  private final SafeTimeTracker updateTracker = new SafeTimeTracker(16);

  public PumpBlockEntity(BlockPos pos, BlockState state) {
    super(FactoryBlockEntityTypes.PUMP.get(), pos, state);
    this.setBattery(new EnergyStorage(1000, 150, 0));
  }

  @Override
  public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      return;
    }

    if (updateTracker.markTimeIfDelay(level)) {
      // sendNetworkUpdate();
    }

    // Simplified pumping logic for now
    tick++;
    if (tick % 16 == 0) {
      // Pumping logic here
    }
  }

  @Override
  public void load(CompoundTag data) {
    super.load(data);
    tank.readFromNBT(data.getCompound("tank"));
    powered = data.getBoolean("powered");
    aimY = data.getInt("aimY");
    tubeY = data.getFloat("tubeY");
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);
    CompoundTag tankNbt = new CompoundTag();
    tank.writeToNBT(tankNbt);
    data.put("tank", tankNbt);
    data.putBoolean("powered", powered);
    data.putInt("aimY", aimY);
    data.putFloat("tubeY", (float) tubeY);
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
  public boolean hasWork() {
    return true; // Simplified
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
    return true;
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return 0; // Pump doesn't accept fluid
  }

  @Override
  public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
    return tank.drain(resource, action);
  }

  @Override
  public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
    return tank.drain(maxDrain, action);
  }

  @Override
  public boolean canConnectRedstoneEngine(Direction side) {
    return true;
  }

  @Override
  public int getLEDLevel(int led) {
    if (led == 0) { // Red LED
      return ledState & 15;
    } else { // Green LED
      return (ledState >> 4) > 0 ? 15 : 0;
    }
  }
}
