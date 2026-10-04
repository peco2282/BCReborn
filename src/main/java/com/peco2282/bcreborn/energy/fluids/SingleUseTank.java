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
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

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

    if (acceptedFluid == null || acceptedFluid == resource.getFluid()) {
      int filled = super.fill(resource, action);
      if (action.execute() && filled > 0 && acceptedFluid == null) {
        acceptedFluid = resource.getFluid();
      }
      return filled;
    }

    return 0;
  }

  public void reset() {
    acceptedFluid = null;
  }

  public Fluid getAcceptedFluid() {
    return acceptedFluid;
  }

  public void setAcceptedFluid(Fluid fluid) {
    this.acceptedFluid = fluid;
  }

  @Override
  public void writeTankToNBT(CompoundTag nbt) {
    super.writeTankToNBT(nbt);
    if (acceptedFluid != null) {
      nbt.putString("acceptedFluid", ForgeRegistries.FLUIDS.getKey(acceptedFluid).toString());
    } else {
      nbt.remove("acceptedFluid");
    }
  }

  @Override
  public void readTankFromNBT(CompoundTag nbt) {
    super.readTankFromNBT(nbt);
    acceptedFluid = null;
    // Stored contents have a stable registry name and take precedence over legacy IDs.
    if (!isEmpty()) {
      acceptedFluid = getFluidType();
    } else if (nbt.contains("acceptedFluid", Tag.TAG_STRING)) {
      ResourceLocation id = ResourceLocation.tryParse(nbt.getString("acceptedFluid"));
      // Unknown saved fluids must not silently unlock the tank for another fluid.
      acceptedFluid = id != null && ForgeRegistries.FLUIDS.containsKey(id)
        ? ForgeRegistries.FLUIDS.getValue(id) : Fluids.EMPTY;
    } else if (nbt.contains("acceptedFluid", Tag.TAG_INT)) {
      var state = Fluid.FLUID_STATE_REGISTRY.byId(nbt.getInt("acceptedFluid"));
      acceptedFluid = state == null ? Fluids.EMPTY : state.getType();
      // Old saves could accidentally persist EMPTY when the key was originally absent.
      if (state != null && acceptedFluid == Fluids.EMPTY) acceptedFluid = null;
    }
  }

  @Override
  public void readTag(CompoundTag nbt) {
    acceptedFluid = null;
    setFluid(FluidStack.EMPTY);
    super.readTag(nbt);
  }
}
