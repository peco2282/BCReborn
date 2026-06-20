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
package com.peco2282.bcreborn.common.energy;

import com.peco2282.bcreborn.api.core.IBufferSerializable;
import com.peco2282.bcreborn.api.core.INBTSerializable;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.energy.IEnergyStorage;

public class EngineEnergyStorage<E extends EngineBlockEntity<?>> implements IEnergyStorage, IBufferSerializable, INBTSerializable {
  private final E engine;

  private int energy, maxEnergy, maxExtract;

  public EngineEnergyStorage(E engine) {
    this.engine = engine;
  }

  public E getEngine() {
    return engine;
  }

  public EngineEnergyStorage<E> setMaxEnergy(int maxEnergy) {
    this.maxEnergy = Math.max(0, maxEnergy);
    if (this.energy > this.maxEnergy) this.energy = this.maxEnergy;
    return this;
  }

  public int getMaxExtract() {
    return this.maxExtract;
  }

  public EngineEnergyStorage<E> setMaxExtract(int maxExtract) {
    this.maxExtract = Math.max(0, maxExtract);
    return this;
  }

  /*
    Sets the amount of energy stored in the storage.
  */

  /**
   * Adds energy to the storage. Returns quantity of energy that was accepted.
   *
   * @param maxReceive Maximum amount of energy to be inserted.
   * @param simulate   If TRUE, the insertion will only be simulated.
   * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
   */
  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return 0;
  }

  public void generateEnergy(int maxGenerate, boolean simulate) {
    maxGenerate = Math.min(maxGenerate, this.maxEnergy - this.energy);
    if (!simulate) {
      this.energy += maxGenerate;
      this.engine.setChanged();
    }
  }

  /**
   * Removes energy from the storage. Returns quantity of energy that was removed.
   *
   * @param maxExtract Maximum amount of energy to be extracted.
   * @param simulate   If TRUE, the extraction will only be simulated.
   * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
   */
  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    // 実際に取り出せる量は、要求量・現在量・最大抽出量の最小
    int toExtract = Math.min(maxExtract, Math.min(this.energy, this.maxExtract));
    if (toExtract <= 0) return 0;
    if (!simulate) {
      this.energy -= toExtract;
      this.engine.setChanged();
    }
    return toExtract;
  }

  /**
   * Returns the amount of energy currently stored.
   */
  @Override
  public int getEnergyStored() {
    return energy;
  }

  /**
   * Returns the maximum amount of energy that can be stored.
   */
  @Override
  public int getMaxEnergyStored() {
    return maxEnergy;
  }

  /**
   * Returns if this storage can have energy extracted.
   * If this is false, then any calls to extractEnergy will return 0.
   */
  @Override
  public boolean canExtract() {
    return true;
  }

  /**
   * Used to determine if this storage can receive energy.
   * If this is false, then any calls to receiveEnergy will return 0.
   */
  @Override
  public boolean canReceive() {
    return false;
  }

  /**
   * Serializes the state to the stream
   *
   * @param data The buffer to write the data to.
   */
  @Override
  public void writeData(FriendlyByteBuf data) {
    CompoundTag tag = new CompoundTag();
    writeTag(tag);
    data.writeNbt(tag);
  }

  /**
   * Deserializes the state from the stream
   *
   * @param data The buffer to read the data from.
   */
  @Override
  public void readData(FriendlyByteBuf data) {
    CompoundTag tag = data.readNbt();
    if (tag == null) {
      return;
    }
    readTag(tag);
  }

  @Override
  public void readTag(CompoundTag nbt) {
    this.maxEnergy = Math.max(0, nbt.getInt("MaxEnergy"));
    this.maxExtract = Math.max(0, nbt.getInt("MaxExtract"));
    // 保存された値を0..maxEnergyにクランプ
    int saved = nbt.getInt("Energy");
    if (saved < 0) saved = 0;
    if (saved > this.maxEnergy) saved = this.maxEnergy;
    this.energy = saved;
  }

  @Override
  public void writeTag(CompoundTag nbt) {
    nbt.putInt("MaxEnergy", this.maxEnergy);
    nbt.putInt("MaxExtract", this.maxExtract);
    nbt.putInt("Energy", this.energy);
  }
}
