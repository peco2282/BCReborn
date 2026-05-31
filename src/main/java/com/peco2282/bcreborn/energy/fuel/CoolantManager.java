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
import com.peco2282.bcreborn.api.core.StackKey;
import com.peco2282.bcreborn.api.fuels.ICoolant;
import com.peco2282.bcreborn.api.fuels.ICoolantManager;
import com.peco2282.bcreborn.api.fuels.ISolidCoolant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CoolantManager implements ICoolantManager {
  public static final ICoolantManager INSTANCE = new CoolantManager();
  private final Map<FluidType, ICoolant> coolants = Maps.newLinkedHashMap();
  private final Map<StackKey, ISolidCoolant> solidCoolants = Maps.newLinkedHashMap();

  private CoolantManager() {
  }

  @Override
  public ICoolant addCoolant(ICoolant coolant) {
    coolants.put(coolant.getFluid(), coolant);
    return coolant;
  }

  @Override
  public ICoolant addCoolant(Fluid fluid, float degreesCoolingPerMB) {
    coolants.put(fluid.getFluidType(), new Coolant(fluid, degreesCoolingPerMB));
    return null;
  }

  @Override
  public ICoolant addCoolant(FluidType fluid, float degreesCoolingPerMB) {
    coolants.put(fluid, new Coolant(fluid, degreesCoolingPerMB));
    return null;
  }

  @Override
  public ISolidCoolant addSolidCoolant(ISolidCoolant solidCoolant) {
    solidCoolants.put(solidCoolant.getSolid(), solidCoolant);
    return solidCoolant;
  }

  @Override
  public ISolidCoolant addSolidCoolant(StackKey solid, StackKey liquid, float multiplier) {
    ISolidCoolant solidCoolant = new SolidCoolant(solid, liquid, multiplier);
    solidCoolants.put(solid, solidCoolant);
    return solidCoolant;
  }

  @Override
  public Collection<ICoolant> getCoolants() {
    return Collections.unmodifiableCollection(coolants.values());
  }

  @Override
  public Collection<ISolidCoolant> getSolidCoolants() {
    return Collections.unmodifiableCollection(solidCoolants.values());
  }

  @Override
  public ICoolant getCoolant(Fluid fluid) {
    return coolants.get(fluid.getFluidType());
  }

  @Override
  public ICoolant getCoolant(FluidType fluid) {
    return coolants.get(fluid);
  }

  @Override
  public ISolidCoolant getSolidCoolant(StackKey solid) {
    return solidCoolants.get(solid);
  }


  @SuppressWarnings("ClassCanBeRecord")
  private static class Coolant implements ICoolant {
    private final FluidType fluid;
    private final float degreesCoolingPerMB;

    public Coolant(Fluid fluid, float degreesCoolingPerMB) {
      this(fluid.getFluidType(), degreesCoolingPerMB);
    }

    public Coolant(FluidType fluid, float degreesCoolingPerMB) {
      this.fluid = fluid;
      this.degreesCoolingPerMB = degreesCoolingPerMB;
    }

    @Override
    public FluidType getFluid() {
      return this.fluid;
    }

    @Override
    public float getDegreesCoolingPerMB(float heat) {
      return this.degreesCoolingPerMB;
    }
  }

  @SuppressWarnings("ClassCanBeRecord")
  private static class SolidCoolant implements ISolidCoolant {
    private final StackKey solid;
    private final StackKey liquid;
    private final float multiplier;

    public SolidCoolant(StackKey solid, StackKey liquid, float multiplier) {
      this.solid = solid;
      this.liquid = liquid;
      this.multiplier = multiplier;
    }

    @Override
    public StackKey getSolid() {
      return this.solid;
    }

    @Override
    public FluidStack getFluidFromSolidCoolant(ItemStack stack) {
      if (stack.isEmpty() || !ItemStack.isSameItemSameTags(stack, this.solid.stack())) {
        return null;
      }
      int liquidAmount = (int) (stack.getCount() * liquid.fluidStack().getAmount() * multiplier / solid.stack().getCount());

      return new FluidStack(this.liquid.fluidStack().getFluid(), liquidAmount);
    }
  }
}
