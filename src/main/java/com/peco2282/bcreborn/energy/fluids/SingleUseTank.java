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
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class SingleUseTank extends Tank {

  private Fluid acceptedFluid;

  public SingleUseTank(String name, int capacity) {
    super(name, capacity);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    if (resource.isEmpty()) {
      return 0;
    }

    if (action.execute() && acceptedFluid == null) {
      acceptedFluid = resource.getFluid();
    }

    if (acceptedFluid == null || acceptedFluid == resource.getFluid()) {
      return super.fill(resource, action);
    }

    return 0;
  }

  public void reset() {
    acceptedFluid = null;
  }

  public void setAcceptedFluid(Fluid fluid) {
    this.acceptedFluid = fluid;
  }

  public Fluid getAcceptedFluid() {
    return acceptedFluid;
  }

  @Override
  public void writeTankToNBT(CompoundTag nbt) {
    super.writeTankToNBT(nbt);
    if (acceptedFluid != null) {
      int id = Fluid.FLUID_STATE_REGISTRY.getId(acceptedFluid.defaultFluidState());
      nbt.putInt("acceptedFluid", id);
    }
  }

  @Override
  public void readTankFromNBT(CompoundTag nbt) {
    super.readTankFromNBT(nbt);
    var fld = Fluid.FLUID_STATE_REGISTRY.byId(nbt.getInt("acceptedFluid"));
    if (fld != null) {
      acceptedFluid = fld.getType();
    }
  }
}
