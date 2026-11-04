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
package com.peco2282.bcreborn.energy.menu;

import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import java.util.function.IntSupplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

/**
 * Menu data shared by the engine GUIs.
 * <p>
 * On the server it reads the engine; on the client it is plain storage the vanilla data packets write into (the
 * client menu has no engine). Data slots are sent as 16-bit values, so energy is split into two halves.
 */
public final class EngineMenuData {
  public static final int BURN = 0;
  public static final int ENERGY_LO = 1;
  public static final int ENERGY_HI = 2;
  public static final int HEAT = 3;
  public static final int STAGE = 4;
  public static final int MAX_LO = 5;
  public static final int MAX_HI = 6;
  public static final int OUTPUT = 7;
  public static final int BASE_COUNT = 8;

  private EngineMenuData() {
  }

  /**
   * @param engine the engine on the server, null on the client
   * @param burn   scaled burn time 0..100
   * @param extra  additional values appended after {@link #BASE_COUNT}
   */
  public static ContainerData create(@Nullable EngineBlockEntity<?> engine, IntSupplier burn, IntSupplier... extra) {
    int count = BASE_COUNT + extra.length;
    if (engine == null) {
      return new SimpleContainerData(count);
    }
    return new ContainerData() {
      @Override
      public int get(int index) {
        return switch (index) {
          case BURN -> burn.getAsInt();
          case ENERGY_LO -> engine.getEnergyStored() & 0xFFFF;
          case ENERGY_HI -> engine.getEnergyStored() >>> 16;
          case HEAT -> Math.round(engine.heat);
          case STAGE -> engine.energyStage.ordinal();
          case MAX_LO -> engine.getMaxEnergyStored() & 0xFFFF;
          case MAX_HI -> engine.getMaxEnergyStored() >>> 16;
          case OUTPUT -> engine.currentOutput;
          default -> index - BASE_COUNT < extra.length ? extra[index - BASE_COUNT].getAsInt() : 0;
        };
      }

      @Override
      public void set(int index, int value) {
      }

      @Override
      public int getCount() {
        return count;
      }
    };
  }

  public static int combine(ContainerData data, int lo, int hi) {
    return ((data.get(hi) & 0xFFFF) << 16) | (data.get(lo) & 0xFFFF);
  }

  public static int fluidId(FluidStack stack) {
    return stack.isEmpty() ? -1 : BuiltInRegistries.FLUID.getId(stack.getFluid());
  }

  public static FluidStack fluid(int id, int amount) {
    if (id < 0 || amount <= 0) return FluidStack.EMPTY;
    var fluid = BuiltInRegistries.FLUID.byId(id);
    return new FluidStack(fluid, amount);
  }
}
