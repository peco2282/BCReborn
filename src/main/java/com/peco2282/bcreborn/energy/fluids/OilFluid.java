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

import com.peco2282.bcreborn.energy.EnergyFluids;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class OilFluid extends ForgeFlowingFluid {

  protected OilFluid(Properties properties) {
    super(properties);
  }

  @Override
  public Fluid getFlowing() {
    return EnergyFluids.OIL_FLOWING.get();
  }

  @Override
  public Fluid getSource() {
    return EnergyFluids.OIL_SOURCE.get();
  }

  @Override
  public boolean isSame(Fluid fluid) {
    return fluid == EnergyFluids.OIL_SOURCE.get() || fluid == EnergyFluids.OIL_FLOWING.get();
  }

  public static class Source extends OilFluid {
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

  public static class Flowing extends OilFluid {
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
