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
package com.peco2282.bcreborn.transport.test;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.block.SpringBlock;
import com.peco2282.bcreborn.core.worldgen.WaterSpringFeature;
import com.peco2282.bcreborn.energy.worldgen.OilWellFeature;
import com.peco2282.bcreborn.energy.worldgen.PlacedFeaturesEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

/** World generation of the original OilPopulate and SpringPopulate, as features. */
@GameTestHolder(BCRebornTransport.MODID)
public class WorldgenGameTests {
  private static void check(GameTestHelper helper, boolean condition, String message) {
    if (!condition) helper.fail(message);
  }

  private static boolean hasFeature(GameTestHelper helper, ResourceKey<Biome> biome, ResourceKey<PlacedFeature> feature) {
    Holder<Biome> holder = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(biome);
    return holder.value().getGenerationSettings().features().stream()
      .anyMatch(step -> step.stream().anyMatch(placed -> placed.is(feature)));
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void wellsAndSpringsGenerateInTheOverworldOnly(GameTestHelper helper) {
    for (ResourceKey<Biome> biome : new ResourceKey[]{Biomes.PLAINS, Biomes.DESERT, Biomes.OCEAN, Biomes.TAIGA}) {
      check(helper, hasFeature(helper, biome, PlacedFeaturesEnergy.OIL_WELL), "Oil wells must generate in " + biome.location());
      check(helper, hasFeature(helper, biome, PlacedFeaturesEnergy.WATER_SPRING), "Water springs must generate in " + biome.location());
    }
    for (ResourceKey<Biome> biome : new ResourceKey[]{Biomes.NETHER_WASTES, Biomes.THE_END}) {
      check(helper, !hasFeature(helper, biome, PlacedFeaturesEnergy.OIL_WELL) && !hasFeature(helper, biome, PlacedFeaturesEnergy.WATER_SPRING),
        "Nothing may generate in " + biome.location());
    }
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void oilWellChancesFollowTheOriginal(GameTestHelper helper) {
    RandomSource rand = RandomSource.create(42);
    int trials = 200000;
    int[] plain = new int[4];
    int[] oil = new int[4];
    for (int i = 0; i < trials; i++) {
      plain[OilWellFeature.pickType(rand, false, 1.0).ordinal()]++;
      oil[OilWellFeature.pickType(rand, true, 1.0).ordinal()]++;
    }
    int large = OilWellFeature.GenType.LARGE.ordinal();
    int medium = OilWellFeature.GenType.MEDIUM.ordinal();
    int lake = OilWellFeature.GenType.LAKE.ordinal();
    // Expected: large 0.04% / medium 0.1%, three times that in oil biomes, and 6% surface lakes there only.
    check(helper, plain[lake] == 0, "Surface lakes only come in oil biomes, got " + plain[lake]);
    check(helper, plain[large] > 40 && plain[large] < 130, "About 80 large wells expected, got " + plain[large]);
    check(helper, plain[medium] > 130 && plain[medium] < 280, "About 200 medium wells expected, got " + plain[medium]);
    check(helper, oil[large] > 170 && oil[large] < 310, "About 240 large wells expected in oil biomes, got " + oil[large]);
    check(helper, oil[lake] > 10500 && oil[lake] < 13000, "About 11800 surface lakes expected in oil biomes, got " + oil[lake]);
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void waterSpringReplacesBedrockAndFloodsUp(GameTestHelper helper) {
    helper.setBlock(new BlockPos(1, 0, 1), Blocks.BEDROCK);
    helper.setBlock(new BlockPos(1, 1, 1), Blocks.STONE);
    helper.setBlock(new BlockPos(1, 2, 1), Blocks.STONE);
    BlockPos bottom = helper.absolutePos(new BlockPos(1, 0, 1));
    check(helper, helper.getLevel().getBlockState(bottom.above(3)).isAir(), "The test needs air above the column");
    check(helper, WaterSpringFeature.placeSpring(helper.getLevel(), bottom.getX(), bottom.getZ(), bottom.getY()), "A spring must be placed on bedrock");
    check(helper, helper.getBlockState(new BlockPos(1, 0, 1)).is(CoreBlocks.SPRING.get())
      && helper.getBlockState(new BlockPos(1, 0, 1)).getValue(SpringBlock.TYPE) == SpringBlock.EnumSpring.WATER, "The bottom bedrock becomes a water spring");
    check(helper, helper.getBlockState(new BlockPos(1, 1, 1)).is(Blocks.WATER) && helper.getBlockState(new BlockPos(1, 2, 1)).is(Blocks.WATER),
      "The rock above the spring turns into water up to the first air");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void waterSpringSitsOnFlatBedrock(GameTestHelper helper) {
    helper.setBlock(new BlockPos(1, 0, 1), Blocks.STONE);
    helper.setBlock(new BlockPos(1, 1, 1), Blocks.BEDROCK);
    helper.setBlock(new BlockPos(1, 2, 1), Blocks.STONE);
    BlockPos bottom = helper.absolutePos(new BlockPos(1, 0, 1));
    check(helper, WaterSpringFeature.placeSpring(helper.getLevel(), bottom.getX(), bottom.getZ(), bottom.getY()), "A spring must be placed on bedrock");
    check(helper, helper.getBlockState(new BlockPos(1, 1, 1)).is(Blocks.BEDROCK), "A bedrock layer above the bottom stays");
    check(helper, helper.getBlockState(new BlockPos(1, 2, 1)).is(CoreBlocks.SPRING.get()), "The spring sits on top of it");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void noSpringWithoutBedrock(GameTestHelper helper) {
    for (int y = 0; y < 3; y++) helper.setBlock(new BlockPos(1, y, 1), Blocks.STONE);
    BlockPos bottom = helper.absolutePos(new BlockPos(1, 0, 1));
    check(helper, !WaterSpringFeature.placeSpring(helper.getLevel(), bottom.getX(), bottom.getZ(), bottom.getY() - 2),
      "Without bedrock in the bottom five layers there is no spring");
    check(helper, helper.getBlockState(new BlockPos(1, 2, 1)).is(Blocks.STONE), "Nothing may change");
    helper.succeed();
  }

  private static net.minecraft.core.Registry<Biome> biomes(GameTestHelper helper) {
    return helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void oilFieldBiomesAreRegisteredAndTagged(GameTestHelper helper) {
    var desert = biomes(helper).getHolderOrThrow(com.peco2282.bcreborn.energy.worldgen.OilBiomes.OIL_DESERT);
    var ocean = biomes(helper).getHolderOrThrow(com.peco2282.bcreborn.energy.worldgen.OilBiomes.OIL_OCEAN);
    check(helper, desert.is(net.minecraft.tags.BiomeTags.IS_OVERWORLD) && desert.is(net.minecraftforge.common.Tags.Biomes.IS_SANDY)
      && desert.is(com.peco2282.bcreborn.energy.worldgen.OilBiomes.EXCESSIVE_OIL), "The desert oil field is an overworld sandy biome with excessive oil");
    check(helper, ocean.is(net.minecraft.tags.BiomeTags.IS_OCEAN) && ocean.is(com.peco2282.bcreborn.energy.worldgen.OilBiomes.EXCESSIVE_OIL),
      "The ocean oil field is an ocean with excessive oil");
    check(helper, OilWellFeature.isOilBiome(desert) && OilWellFeature.isOilBiome(ocean), "Both have surface oil deposits");
    check(helper, hasFeature(helper, com.peco2282.bcreborn.energy.worldgen.OilBiomes.OIL_DESERT, PlacedFeaturesEnergy.OIL_WELL),
      "Oil wells generate in the desert oil field");
    RandomSource rand = RandomSource.create(7);
    int large = 0;
    for (int i = 0; i < 20000; i++) {
      if (OilWellFeature.pickType(rand, true, true, 1.0) == OilWellFeature.GenType.LARGE) large++;
    }
    // 0.0004 * 3 * 30 = 3.6% large wells in an oil field (original excessiveBiomes bonus).
    check(helper, large > 600 && large < 840, "About 720 large wells expected in an oil field, got " + large);
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID)
  public void oilFieldNoiseMakesLargePatches(GameTestHelper helper) {
    var field = com.peco2282.bcreborn.energy.worldgen.OilBiomes.Field.create(12345L, biomes(helper));
    int desert = 0;
    int ocean = 0;
    int samples = 0;
    int sameNextBlock = 0;
    for (int x = -400000; x < 400000; x += 4000) {
      for (int z = -400000; z < 400000; z += 4000) {
        samples++;
        boolean oil = field.isOilDesert(x, z);
        if (oil) desert++;
        if (field.isOilOcean(x, z)) ocean++;
        if (oil == field.isOilDesert(x + 16, z)) sameNextBlock++;
      }
    }
    check(helper, desert > samples / 200 && desert < samples / 4, "Desert oil fields cover a small share, got " + desert + " of " + samples);
    check(helper, ocean < desert, "Ocean oil fields are rarer (threshold 0.9), got " + ocean + " vs " + desert);
    check(helper, sameNextBlock > samples * 98 / 100, "Fields are large patches, not noise, got " + sameNextBlock + " of " + samples);
    var other = com.peco2282.bcreborn.energy.worldgen.OilBiomes.Field.create(54321L, biomes(helper));
    int differs = 0;
    for (int x = -400000; x < 400000; x += 4000) {
      if (field.isOilDesert(x, 0) != other.isOilDesert(x, 0)) differs++;
    }
    check(helper, differs > 0, "Another world seed moves the fields");
    helper.succeed();
  }

  @PrefixGameTestTemplate(false)
  @GameTest(template = "empty_3x3", templateNamespace = BCRebornTransport.MODID, timeoutTicks = 400)
  public void overworldRepaintsDesertAndOceanAsOilFields(GameTestHelper helper) {
    var field = com.peco2282.bcreborn.energy.worldgen.OilBiomes.field();
    check(helper, field != null, "The oil field noise must be set up when the server starts");
    var level = helper.getLevel();
    var source = level.getChunkSource().getGenerator().getBiomeSource();
    check(helper, source instanceof net.minecraft.world.level.biome.MultiNoiseBiomeSource, "The overworld uses the multi noise biome source");
    var multiNoise = (net.minecraft.world.level.biome.MultiNoiseBiomeSource) source;
    check(helper, source.possibleBiomes().contains(field.oilDesert()) && source.possibleBiomes().contains(field.oilOcean()),
      "The oil fields must be possible biomes of the overworld");
    var sampler = level.getChunkSource().randomState().sampler();
    int swapped = 0;
    for (int x = -300000; x < 300000; x += 1500) {
      for (int z = -300000; z < 300000; z += 30000) {
        int qx = net.minecraft.core.QuartPos.fromBlock(x);
        int qy = net.minecraft.core.QuartPos.fromBlock(64);
        int qz = net.minecraft.core.QuartPos.fromBlock(z);
        var climate = multiNoise.getNoiseBiome(sampler.sample(qx, qy, qz));
        var actual = multiNoise.getNoiseBiome(qx, qy, qz, sampler);
        var expected = field.replace(climate, net.minecraft.core.QuartPos.toBlock(qx), net.minecraft.core.QuartPos.toBlock(qz));
        check(helper, actual == expected, "At " + x + ", " + z + " the biome must be " + expected.unwrapKey() + ", is " + actual.unwrapKey());
        if (actual != climate) swapped++;
      }
    }
    check(helper, swapped > 0, "Some desert or ocean must have been repainted in the sampled area");
    helper.succeed();
  }
}
