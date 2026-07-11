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
package com.peco2282.bcreborn.energy.fuel;

import com.google.common.collect.Maps;
import com.peco2282.bcreborn.api.fuels.IFuel;
import com.peco2282.bcreborn.api.fuels.IFuelManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class FuelManager implements IFuelManager {
  public static final FuelManager INSTANCE = new FuelManager();
  private final Map<FluidType, IFuel> fuels = Maps.newLinkedHashMap();

  private FuelManager() {
  }

  @Override
  public IFuel addFuel(IFuel fuel) {
    if (fuels.containsKey(fuel.getFluid())) {
      throw new IllegalArgumentException("Fuel for fluid " + fuel.getFluid() + " already exists");
    }
    fuels.put(fuel.getFluid(), fuel);
    return fuel;
  }

  @Override
  public void addFuel(Fluid fluid, int powerPerCycle, int totalBurningTime) {
    addFuel(new Fuel(fluid, powerPerCycle, totalBurningTime));
  }

  @Override
  public IFuel addFuel(FluidType fluid, int powerPerCycle, int totalBurningTime) {
    return addFuel(new Fuel(fluid, powerPerCycle, totalBurningTime));
  }

  @Override
  public Collection<IFuel> getFuels() {
    return Collections.unmodifiableCollection(fuels.values());
  }

  @Override
  @Nullable
  public IFuel getFuel(FluidType fluid) {
    return fuels.get(fluid);
  }

  @Override
  @Nullable
  public IFuel getFuel(Fluid fluid) {
    return getFuel(fluid.getFluidType());
  }


  @SuppressWarnings("ClassCanBeRecord")
  private static class Fuel implements IFuel {
    private final FluidType fluid;
    private final int powerPerCycle;
    private final int totalBurningTime;

    public Fuel(Fluid fluid, int powerPerCycle, int totalBurningTime) {
      this(fluid.getFluidType(), powerPerCycle, totalBurningTime);
    }

    public Fuel(FluidType fluid, int powerPerCycle, int totalBurningTime) {
      this.fluid = fluid;
      this.powerPerCycle = powerPerCycle;
      this.totalBurningTime = totalBurningTime;
    }


    @Override
    public FluidType getFluid() {
      return this.fluid;
    }

    @Override
    public int getTotalBurningTime() {
      return this.totalBurningTime;
    }

    @Override
    public int getPowerPerCycle() {
      return this.powerPerCycle;
    }
  }

}
