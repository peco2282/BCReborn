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

import com.peco2282.bcreborn.energy.FluidsEnergy;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class FuelFluid extends ForgeFlowingFluid {

  protected FuelFluid(Properties properties) {
    super(properties);
  }

  @Override
  public Fluid getFlowing() {
    return FluidsEnergy.FUEL_FLOWING.get();
  }

  @Override
  public Fluid getSource() {
    return FluidsEnergy.FUEL_SOURCE.get();
  }

  @Override
  public boolean isSame(Fluid fluid) {
    return fluid == FluidsEnergy.FUEL_SOURCE.get() || fluid == FluidsEnergy.FUEL_FLOWING.get();
  }

  public static class Source extends FuelFluid {
    public Source(Properties properties) {
      super(properties);
    }

    @Override
    public int getAmount(FluidState state) {
      return 8;
    }

    @Override
    public boolean isSource(FluidState state) {
      return true;
    }
  }

  public static class Flowing extends FuelFluid {
    public Flowing(Properties properties) {
      super(properties);
    }

    @Override
    protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
      super.createFluidStateDefinition(builder);
      builder.add(LEVEL);
    }

    @Override
    public int getAmount(FluidState state) {
      return state.getValue(LEVEL);
    }

    @Override
    public boolean isSource(FluidState state) {
      return false;
    }
  }
}
