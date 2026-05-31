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
package com.peco2282.bcreborn.common.data;

import com.peco2282.bcreborn.*;
import com.peco2282.bcreborn.energy.worldgen.BiomeModifiersEnergy;
import com.peco2282.bcreborn.energy.worldgen.ConfiguredFeaturesEnergy;
import com.peco2282.bcreborn.energy.worldgen.PlacedFeaturesEnergy;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ResourceDataGenerator extends DatapackBuiltinEntriesProvider {
  private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
      .add(Registries.CONFIGURED_FEATURE, ConfiguredFeaturesEnergy::bootstrap)
      .add(Registries.PLACED_FEATURE, PlacedFeaturesEnergy::bootstrap)
      .add(ForgeRegistries.Keys.BIOME_MODIFIERS, BiomeModifiersEnergy::bootstrap);

  /**
   * Constructs a new datapack provider which generates all registry objects
   * from the provided mods using the holder.
   *
   * @param output     the target directory of the data generator
   * @param registries a future of a lookup for registries and their objects
   */
  public ResourceDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, BUILDER, Set.of(
        BCRebornCore.MODID,
        BCRebornBuilders.MODID,
        BCRebornEnergy.MODID,
        BCRebornTransport.MODID,
        BCRebornFactory.MODID,
        BCRebornSilicon.MODID,
        BCRebornRobotics.MODID
    ));
  }
}
