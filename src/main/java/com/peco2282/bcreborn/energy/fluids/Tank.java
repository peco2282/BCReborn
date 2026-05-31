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
package com.peco2282.bcreborn.energy.fluids;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Locale;

public class Tank extends FluidTank {
  private final String name;
  public int colorRenderCache = 0xFFFFFF;
  protected Component toolTip;

  public Tank(String name, int capacity) {
    super(capacity);
    this.name = name;
    refreshTooltip();
  }

  public boolean isEmpty() {
    return getFluid().getAmount() <= 0;
  }

  public boolean isFull() {
    return getFluid().getAmount() >= getCapacity();
  }

  public Fluid getFluidType() {
    return getFluid().getFluid();
  }

  @Override
  public final CompoundTag writeToNBT(CompoundTag nbt) {
    CompoundTag tankData = new CompoundTag();
    super.writeToNBT(tankData);
    writeTankToNBT(tankData);
    nbt.put(name, tankData);
    return nbt;
  }

  @Override
  public final FluidTank readFromNBT(CompoundTag nbt) {
    if (nbt.contains(name)) {
      // allow to read empty tanks
      setFluid(null);

      CompoundTag tankData = nbt.getCompound(name);
      super.readFromNBT(tankData);
      readTankFromNBT(tankData);
    }
    return this;
  }

  public void writeTankToNBT(CompoundTag nbt) {
  }

  public void readTankFromNBT(CompoundTag nbt) {
  }

  public Component getToolTip() {
    return refreshTooltip();
  }

  protected Component refreshTooltip() {
    int amount = 0;
    MutableComponent tip = Component.literal("");
    if (getFluid().getAmount() > 0) {
      tip
        .append(getFluid().getFluid().getFluidType().getDescription(getFluid()))
        .append(CommonComponents.space())
        .append(CommonComponents.space());
      amount = getFluid().getAmount();
    }
    tip.append(String.format(Locale.ENGLISH, "%,d / %,d", amount, getCapacity()));
    toolTip = tip;
    return toolTip;
  }
}
