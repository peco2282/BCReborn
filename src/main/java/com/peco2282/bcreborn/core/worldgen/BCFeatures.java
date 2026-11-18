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
package com.peco2282.bcreborn.core.worldgen;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.energy.worldgen.OilWellFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/** The world generation features: water springs (core) and oil wells (energy). */
public final class BCFeatures {
  private static final DeferredRegister<Feature<?>> CORE = DeferredRegister.create(Registries.FEATURE, BCRebornCore.MODID);
  private static final DeferredRegister<Feature<?>> ENERGY = DeferredRegister.create(Registries.FEATURE, BCRebornEnergy.MODID);

  public static final RegistryObject<Feature<NoneFeatureConfiguration>> WATER_SPRING = CORE.register("water_spring", WaterSpringFeature::new);
  public static final RegistryObject<Feature<NoneFeatureConfiguration>> OIL_WELL = ENERGY.register("oil_well", OilWellFeature::new);

  private BCFeatures() {
  }

  public static void register(IEventBus modEventBus) {
    CORE.register(modEventBus);
    ENERGY.register(modEventBus);
  }
}
