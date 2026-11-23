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
package com.peco2282.bcreborn.transport.pipe.extraction;

import com.peco2282.bcreborn.common.item.EnergyStorage;
import net.minecraft.nbt.CompoundTag;

/**
 * Original {@code PipeFluidsWood.liquidToExtract}: the wooden and emerald fluid pipes turn the energy they receive
 * into an amount of fluid they may pull, 50 mB per RF ({@code 5 * pipeFluidsBaseFlowRate}) up to 1000 mB
 * ({@code 100 * pipeFluidsBaseFlowRate}).
 * <p>
 * The stored value is that amount in mB; to engines the battery reports RF (20 RF when full).
 */
public class FluidExtractionBattery extends EnergyStorage {
  /** Original getEnergyMultiplier(): mB per RF. */
  public static final int MB_PER_RF = 50;
  /** Original getMaxExtractionFluid(). */
  public static final int MAX_LIQUID = 1000;

  public FluidExtractionBattery(int liquid) {
    super(MAX_LIQUID, MAX_LIQUID, 0, Math.max(0, Math.min(liquid, MAX_LIQUID)));
  }

  /** Original receiveEnergy(): only whole RF that still fit into the buffer are taken. */
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    int maxToReceive = (MAX_LIQUID - getLiquid()) / MB_PER_RF;
    int received = Math.max(0, Math.min(maxReceive, maxToReceive));
    if (!simulate && received > 0) {
      addEnergy(0, received * MB_PER_RF, false);
    }
    return received;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public int getEnergyStored() {
    return getLiquid() / MB_PER_RF;
  }

  @Override
  public int getMaxEnergyStored() {
    return MAX_LIQUID / MB_PER_RF;
  }

  @Override
  public boolean canReceive() {
    return getLiquid() + MB_PER_RF <= MAX_LIQUID;
  }

  /** The fluid the pipe may still pull, in mB. */
  public int getLiquid() {
    return super.getEnergyStored();
  }

  /** Original: the buffer always goes down by the flow rate, pulled or not. */
  public void spend(int liquid) {
    useEnergy(0, liquid, false);
  }

  /** Only the amount is read; older saves had a 1024 RF battery with other limits. */
  @Override
  public void read(CompoundTag tag) {
    setEnergy(Math.min(tag.getInt("energy"), MAX_LIQUID));
  }
}
