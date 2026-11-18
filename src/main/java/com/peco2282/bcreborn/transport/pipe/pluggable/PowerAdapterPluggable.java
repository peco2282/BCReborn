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
package com.peco2282.bcreborn.transport.pipe.pluggable;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.transport.IPipeBlockEntity;
import com.peco2282.bcreborn.api.transport.pluggable.IPipePluggableRenderer;
import com.peco2282.bcreborn.api.transport.pluggable.PipePluggable;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Original {@code PowerAdapterPluggable}: closes its side of the pipe and lets power in there, at most 40 RF per call,
 * straight into the pipe's own battery (a wooden or stripes pipe). A power pipe next to it connects to it like to a
 * machine. It stores nothing itself.
 */
public class PowerAdapterPluggable extends PipePluggable<PowerAdapterPluggable> {
  /** Original {@code MAX_POWER}. */
  public static final int MAX_POWER = 40;

  public PowerAdapterPluggable() {
    super(BCRebornTransport.POWER_ADAPTER);
  }

  /** The energy handler the adapter shows on its side of {@code pipe}. */
  public static IEnergyStorage storage(PipeBlockEntity pipe) {
    return new IEnergyStorage() {
      @Override
      public int receiveEnergy(int maxReceive, boolean simulate) {
        EnergyStorage battery = pipe.getBattery();
        return battery == null ? 0 : battery.receiveEnergy(Math.min(MAX_POWER, maxReceive), simulate);
      }

      @Override
      public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
      }

      @Override
      public int getEnergyStored() {
        EnergyStorage battery = pipe.getBattery();
        return battery == null ? 0 : battery.getEnergyStored();
      }

      @Override
      public int getMaxEnergyStored() {
        EnergyStorage battery = pipe.getBattery();
        return battery == null ? 0 : battery.getMaxEnergyStored();
      }

      @Override
      public boolean canExtract() {
        return false;
      }

      @Override
      public boolean canReceive() {
        return pipe.getBattery() != null;
      }
    };
  }

  @Override
  public void writeTag(CompoundTag nbt) {
  }

  @Override
  public void readTag(CompoundTag nbt) {
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
  }

  @Override
  public void readData(FriendlyByteBuf data) {
  }

  @Override
  public ItemStack[] getDropItems(IPipeBlockEntity pipe) {
    return new ItemStack[]{new ItemStack(TransportItems.POWER_ADAPTER.get())};
  }

  @Override
  public boolean isBlocking(IPipeBlockEntity pipe, Direction direction) {
    return true;
  }

  @Override
  public AABB getBoundingBox(Direction side) {
    // Original: 0.1875..0.8125 wide, 0..0.251 from the face.
    return PluggableBoxes.box(side, 0.1875f, 0.8125f, 0.0f, 0.251f);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public IPipePluggableRenderer getRenderer() {
    return PowerAdapterPluggableRenderer.INSTANCE;
  }
}
