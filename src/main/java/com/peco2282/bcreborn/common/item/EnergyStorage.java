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
package com.peco2282.bcreborn.common.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorage implements IEnergyStorage {
  private int energy, maxEnergy, maxReceive, maxExtract;

  public EnergyStorage(int maxEnergy, int maxReceive, int maxExtract) {
    this.maxEnergy = maxEnergy;
    this.maxReceive = maxReceive;
    this.maxExtract = maxExtract;
  }

  public void read(CompoundTag tag) {
    if (tag.contains("energy") && tag.contains("maxEnergy") && tag.contains("maxReceive") && tag.contains("maxExtract")
        && tag.getInt("maxEnergy") > 0) {
      this.energy = tag.getInt("energy");
      this.maxEnergy = tag.getInt("maxEnergy");
      this.maxReceive = tag.getInt("maxReceive");
      this.maxExtract = tag.getInt("maxExtract");
    }
  }

  public void write(CompoundTag tag) {
    tag.putInt("energy", this.energy);
    tag.putInt("maxEnergy", this.maxEnergy);
    tag.putInt("maxReceive", this.maxReceive);
    tag.putInt("maxExtract", this.maxExtract);
  }

  public int addEnergy(int minReceive, int maxReceive, boolean simulate) {
    int amountReceived = Math.min(maxReceive, maxEnergy - energy);

    if (amountReceived < minReceive) {
      return 0;
    }

    if (!simulate) {
      energy += amountReceived;
    }

    return amountReceived;
  }

  public int useEnergy(int minExtract, int maxExtract, boolean simulate) {
    int amountExtracted = Math.min(maxExtract, energy);

    if (amountExtracted < minExtract) {
      return 0;
    }

    if (!simulate) {
      energy -= amountExtracted;
    }

    return amountExtracted;
  }

  public int receiveEnergy(int maxReceive, boolean simulate) {
    return addEnergy(0, Math.min(maxReceive, this.maxReceive), simulate);
  }

  public int extractEnergy(int maxExtract, boolean simulate) {
    return useEnergy(0, Math.min(maxExtract, this.maxExtract), simulate);
  }

  public int getEnergyStored() {
    return energy;
  }

  public int getMaxEnergyStored() {
    return maxEnergy;
  }

  @Override
  public boolean canExtract() {
    return energy > 0;
  }

  @Override
  public boolean canReceive() {
    return energy < maxEnergy;
  }

  public int getMaxEnergyReceive() {
    return maxReceive;
  }

  public int getMaxEnergyExtract() {
    return maxExtract;
  }

  public void setEnergy(int iEnergy) {
    energy = iEnergy;

    if (energy < 0) {
      energy = 0;
    } else if (energy > maxEnergy) {
      energy = maxEnergy;
    }
  }
}
