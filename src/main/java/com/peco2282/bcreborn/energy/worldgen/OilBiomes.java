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
package com.peco2282.bcreborn.energy.worldgen;

import com.peco2282.bcreborn.BCRebornEnergy;
import com.peco2282.bcreborn.energy.EnergyConfig;
import java.util.Random;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import org.jetbrains.annotations.Nullable;

/**
 * The Desert / Ocean Oil Field biomes (original BiomeGenOilDesert / BiomeGenOilOcean). As the original
 * {@code GenLayerBiomeReplacer} did, they are not placed by climate: parts of the vanilla desert and ocean are
 * repainted where a large simplex noise field (shifted by the world seed) is strong enough. The swap itself happens in
 * {@code MultiNoiseBiomeSourceMixin}; Forge 1.20.1 has no API to add overworld biomes
 * ({@code BiomeManager.addAdditionalOverworldBiomes} is not wired to world generation).
 */
public final class OilBiomes {
  public static final ResourceKey<Biome> OIL_DESERT = ResourceKey.create(Registries.BIOME, BCRebornEnergy.location("oil_desert"));
  public static final ResourceKey<Biome> OIL_OCEAN = ResourceKey.create(Registries.BIOME, BCRebornEnergy.location("oil_ocean"));
  /** Original {@code excessiveBiomes}: oil wells are 30 times as likely there. */
  public static final TagKey<Biome> EXCESSIVE_OIL = TagKey.create(Registries.BIOME, BCRebornEnergy.location("excessive_oil"));

  /** Original GenLayerAddOilDesert / GenLayerAddOilOcean and GenLayerBiomeReplacer.OFFSET_RANGE. */
  public static final double DESERT_SCALE = 0.001;
  public static final double DESERT_THRESHOLD = 0.7;
  public static final double OCEAN_SCALE = 0.0005;
  public static final double OCEAN_THRESHOLD = 0.9;
  public static final int OFFSET_RANGE = 500000;

  /** The field for the running server, set when it starts. */
  @Nullable
  private static volatile Field field;

  /** The noise field and biomes of one world. */
  public record Field(SimplexNoise noise, int xOffset, int zOffset, Holder<Biome> desert, Holder<Biome> ocean,
                      Holder<Biome> oilDesert, Holder<Biome> oilOcean) {
    public static Field create(long seed, Registry<Biome> biomes) {
      // Original: the offsets come from java.util.Random(worldSeed), first x then z.
      Random rand = new Random(seed);
      int xOffset = rand.nextInt(OFFSET_RANGE) - OFFSET_RANGE / 2;
      int zOffset = rand.nextInt(OFFSET_RANGE) - OFFSET_RANGE / 2;
      return new Field(new SimplexNoise(new LegacyRandomSource(seed)), xOffset, zOffset,
        biomes.getHolderOrThrow(Biomes.DESERT), biomes.getHolderOrThrow(Biomes.OCEAN),
        biomes.getHolderOrThrow(OIL_DESERT), biomes.getHolderOrThrow(OIL_OCEAN));
    }

    public boolean isOilDesert(int blockX, int blockZ) {
      return strength(blockX, blockZ, DESERT_SCALE) > DESERT_THRESHOLD;
    }

    public boolean isOilOcean(int blockX, int blockZ) {
      return strength(blockX, blockZ, OCEAN_SCALE) > OCEAN_THRESHOLD;
    }

    private double strength(int blockX, int blockZ, double scale) {
      return noise.getValue((blockX + xOffset) * scale, (blockZ + zOffset) * scale);
    }

    /** The biome a column shows: {@code biome}, or its oil field where the field is strong enough. */
    public Holder<Biome> replace(Holder<Biome> biome, int blockX, int blockZ) {
      if (biome == desert && isOilDesert(blockX, blockZ)) return oilDesert;
      if (biome == ocean && isOilOcean(blockX, blockZ)) return oilOcean;
      return biome;
    }
  }

  private OilBiomes() {
  }

  @Nullable
  public static Field field() {
    return field;
  }

  /** Called when a server starts (and before its levels exist). */
  public static void onServerStarting(MinecraftServer server) {
    if (!EnergyConfig.isWorldgenEnable()) {
      field = null;
      return;
    }
    Registry<Biome> biomes = server.registryAccess().registryOrThrow(Registries.BIOME);
    field = biomes.containsKey(OIL_DESERT) && biomes.containsKey(OIL_OCEAN)
      ? Field.create(server.getWorldData().worldGenOptions().seed(), biomes) : null;
  }

  public static void onServerStopped() {
    field = null;
  }

  /** The biome definitions: copies of the vanilla desert and ocean (the originals extend BiomeGenDesert / Ocean). */
  public static void bootstrap(BootstapContext<Biome> context) {
    var placed = context.lookup(Registries.PLACED_FEATURE);
    var carvers = context.lookup(Registries.CONFIGURED_CARVER);
    context.register(OIL_DESERT, OverworldBiomes.desert(placed, carvers));
    context.register(OIL_OCEAN, OverworldBiomes.ocean(placed, carvers, false));
  }
}
