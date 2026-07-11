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
package com.peco2282.bcreborn.api.fuels;

import com.peco2282.bcreborn.api.core.StackKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ICoolantManager {
  ICoolant addCoolant(ICoolant coolant);

  ICoolant addCoolant(Fluid fluid, float degreesCoolingPerMB);

  ICoolant addCoolant(FluidType fluid, float degreesCoolingPerMB);

  ISolidCoolant addSolidCoolant(ISolidCoolant solidCoolant);

  ISolidCoolant addSolidCoolant(StackKey solid, StackKey liquid, float multiplier);

  Collection<ICoolant> getCoolants();

  Collection<ISolidCoolant> getSolidCoolants();

  @Nullable
  ICoolant getCoolant(Fluid fluid);

  @Nullable
  ICoolant getCoolant(FluidType fluid);

  @Nullable
  ISolidCoolant getSolidCoolant(StackKey solid);
}
