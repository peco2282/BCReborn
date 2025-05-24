/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.lib.damage.LiquidDamageType;
import peco2282.bcreborn.registry.levelgen.BCFeatures;
import peco2282.bcreborn.registry.levelgen.BCPlacements;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * The BCRegistryProvider is responsible for creating and registering custom game elements such as
 * menu textures, configured features, placed features, and custom damage types for the BC Reborn
 * mod. It utilizes the data generator framework provided by Minecraft Forge to automate generating
 * necessary datapack entries for the mod.
 *
 * @author peco2282
 */
public class BCRegistryProvider extends DatapackBuiltinEntriesProvider {
  private static final RegistrySetBuilder BUILDER =
      new RegistrySetBuilder()
          .add(Registries.CONFIGURED_FEATURE, BCFeatures::bootstrap)
          .add(Registries.PLACED_FEATURE, BCPlacements::bootstrap)
          .add(Registries.DAMAGE_TYPE, LiquidDamageType::bootstrap);

  /**
   * Creates a new instance of BCRegistryProvider. This class manages the generation of registry
   * objects and bootstrap procedures defined in the {@link RegistrySetBuilder} for the BC Reborn
   * mod. All registry entries are tied to the {@link BCReborn#MODID} namespace.
   *
   * @param output The target output directory for the data generator, where datapack files will be
   *     created.
   * @param registries A {@link CompletableFuture} containing a lookup provider for registries to
   *     interact with during the bootstrap process.
   */
  public BCRegistryProvider(
      PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, BUILDER, Set.of(BCReborn.MODID));
  }
}
