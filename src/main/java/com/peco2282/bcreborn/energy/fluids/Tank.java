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


import com.peco2282.bcreborn.api.core.IBufferSerializable;
import com.peco2282.bcreborn.api.core.INBTSerializable;
import com.peco2282.bcreborn.api.serialization.NbtReader;
import com.peco2282.bcreborn.api.serialization.NbtWriter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Locale;

public class Tank extends FluidTank implements IBufferSerializable, INBTSerializable {
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
  public void writeTag(CompoundTag nbt) {
    NbtWriter.of(nbt).withWriter(name, w -> {
      super.writeToNBT(w.getTag());
      writeTankToNBT(w.getTag());
    });
  }

  @Override
  public final CompoundTag writeToNBT(CompoundTag nbt) {
    writeTag(nbt);
    return nbt;
  }

  @Override
  public void readTag(CompoundTag nbt) {
    NbtReader.of(nbt).apply(name, r -> {
      setFluid(FluidStack.EMPTY);
      super.readFromNBT(r.getTag());
      readTankFromNBT(r.getTag());
    });
  }

  @Override
  public final FluidTank readFromNBT(CompoundTag nbt) {
    readTag(nbt);
    return this;
  }

  public void writeTankToNBT(CompoundTag nbt) {
  }

  public void readTankFromNBT(CompoundTag nbt) {
  }

  public Component getToolTip() {
    return refreshTooltip();
  }

  @Override
  public void writeData(FriendlyByteBuf data) {
    FluidStack fluidStack = getFluid();
    if (fluidStack.getFluid() != null) {
      fluidStack.writeToPacket(data);
      data.writeInt(colorRenderCache);
    } else {
      data.writeShort(-1);
    }
  }

  @Override
  public void readData(FriendlyByteBuf data) {
    int fluidId = data.readShort();
    FluidStack fs = FluidStack.readFromPacket(data);
    if (fs != null && fs != FluidStack.EMPTY) {
      setFluid(fs);
      colorRenderCache = data.readInt();
    } else {
      setFluid(FluidStack.EMPTY);
      colorRenderCache = 0xFFFFFF;
    }
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
