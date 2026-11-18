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

import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.block.SpringBlock;
import com.peco2282.bcreborn.energy.EnergyConfig;
import com.peco2282.bcreborn.energy.EnergyFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

/**
 * Oil generation of the original {@code OilPopulate.generateOil()}, run once per chunk as a world generation feature:
 * rare large and medium wells (a spherical deposit 20-29 blocks up, a spout to the surface and an oil lake around it,
 * and for a quarter of the large ones an oil spring on bedrock) and, in sandy and taiga biomes, surface oil lakes.
 *
 * <p>A feature may only change the chunks next to the one it is placed in, so the surface lake around a well is
 * limited to what fits there (the original lake of a large well reached 25 to 44 blocks).
 */
public class OilWellFeature extends Feature<NoneFeatureConfiguration> {
  public static final int LARGE_WELL_HEIGHT = 16;
  public static final int MEDIUM_WELL_HEIGHT = 6;

  public enum GenType {
    LARGE, MEDIUM, LAKE, NONE
  }

  public OilWellFeature() {
    super(NoneFeatureConfiguration.CODEC);
  }

  private static BlockState oil() {
    return EnergyFluids.OIL_BLOCK.get().defaultBlockState();
  }

  /** Original: biomes with increased oil ({@code increasedOilIDs}: SANDY and taiga, and the oil fields). */
  public static boolean isOilBiome(Holder<Biome> biome) {
    return biome.is(Tags.Biomes.IS_SANDY) || biome.is(BiomeTags.IS_TAIGA) || biome.is(OilBiomes.EXCESSIVE_OIL);
  }

  /** The original chances, checked in this order: large 0.04%, medium 0.1%, surface lake 2% (oil biomes only). */
  public static GenType pickType(RandomSource rand, boolean oilBiome, double scalar) {
    return pickType(rand, oilBiome, false, scalar);
  }

  /** As above; the oil fields ({@code excessiveBiomes}) multiply the chances by 30 more. */
  public static GenType pickType(RandomSource rand, boolean oilBiome, boolean excessive, double scalar) {
    double bonus = (oilBiome ? 3.0 : 1.0) * scalar;
    if (excessive) bonus *= 30.0;
    if (rand.nextDouble() <= 0.0004 * bonus) return GenType.LARGE;
    if (rand.nextDouble() <= 0.001 * bonus) return GenType.MEDIUM;
    if (oilBiome && rand.nextDouble() <= 0.02 * bonus) return GenType.LAKE;
    return GenType.NONE;
  }

  @Override
  public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
    if (!EnergyConfig.isWorldgenEnable()) return false;
    WorldGenLevel level = context.level();
    RandomSource rand = context.random();
    BlockPos origin = context.origin();
    int x = origin.getX();
    int z = origin.getZ();
    Holder<Biome> biome = level.getBiome(new BlockPos(x, 64, z));
    GenType type = pickType(rand, isOilBiome(biome), biome.is(OilBiomes.EXCESSIVE_OIL), EnergyConfig.getOilWellGenerationRate());
    return type != GenType.NONE && generate(level, rand, x, z, type);
  }

  /** Original {@code generateOil()} after the type was chosen. */
  public static boolean generate(WorldGenLevel level, RandomSource rand, int x, int z, GenType type) {
    Area area = new Area(new ChunkPos(new BlockPos(x, 0, z)));
    int groundLevel = getTopBlock(level, x, z);
    if (groundLevel < level.getMinBuildHeight() + 5) return false;
    if (surfaceDeviation(level, x, groundLevel, z, 8) > 0.45) return false;

    if (type == GenType.LAKE) {
      // Original: a surface lake only where the ground is the biome's top block; here, any solid ground.
      if (level.getBlockState(new BlockPos(x, groundLevel, z)).isAir()) return false;
      generateSurfaceDeposit(level, area, rand, x, groundLevel, z, area.fit(x, z, 5 + rand.nextInt(10)));
      return true;
    }

    int wellHeight = type == GenType.LARGE ? LARGE_WELL_HEIGHT : MEDIUM_WELL_HEIGHT;
    int maxHeight = groundLevel + wellHeight;
    if (maxHeight >= level.getMaxBuildHeight() - 1) return false;
    int wellY = 20 + rand.nextInt(10);
    if (wellY > groundLevel) return false;

    // Spherical deposit.
    int radius = type == GenType.LARGE ? 8 + rand.nextInt(9) : 4 + rand.nextInt(4);
    int radiusSq = radius * radius;
    for (int dx = -radius; dx <= radius; dx++) {
      for (int dy = -radius; dy <= radius; dy++) {
        for (int dz = -radius; dz <= radius; dz++) {
          int distance = dx * dx + dy * dy + dz * dz;
          if (distance <= radiusSq) {
            area.set(level, new BlockPos(x + dx, wellY + dy, z + dz), oil(), distance == radiusSq ? 3 : 2);
          }
        }
      }
    }

    // Lake around the spout.
    int lakeRadius = type == GenType.LARGE ? 25 + rand.nextInt(20) : 5 + rand.nextInt(10);
    generateSurfaceDeposit(level, area, rand, x, groundLevel, z, area.fit(x, z, lakeRadius));

    // Spout, from an oil spring on bedrock for a quarter of the large wells.
    boolean makeSpring = type == GenType.LARGE && EnergyConfig.isSpawnOilSprings() && rand.nextDouble() <= 0.25;
    int baseY = makeSpring ? level.getMinBuildHeight() : wellY;
    BlockPos springPos = new BlockPos(x, baseY, z);
    if (makeSpring && level.getBlockState(springPos).is(Blocks.BEDROCK)) {
      BlockState spring = CoreBlocks.SPRING.get().defaultBlockState().setValue(SpringBlock.TYPE, SpringBlock.EnumSpring.OIL);
      level.setBlock(springPos, spring, 3);
      level.scheduleTick(springPos, spring.getBlock(), SpringBlock.EnumSpring.OIL.getTickRate());
    }
    for (int y = baseY + 1; y <= maxHeight; ++y) {
      area.set(level, new BlockPos(x, y, z), oil(), 3);
    }
    if (type == GenType.LARGE) {
      for (int y = wellY; y <= maxHeight - wellHeight / 2; ++y) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
          area.set(level, new BlockPos(x, y, z).relative(side), oil(), 3);
        }
      }
    }
    return true;
  }

  /** The chunks a feature placed in {@code center} may change: the center and the eight around it. */
  record Area(int minX, int minZ, int maxX, int maxZ) {
    Area(ChunkPos center) {
      this((center.x - 1) << 4, (center.z - 1) << 4, ((center.x + 2) << 4) - 1, ((center.z + 2) << 4) - 1);
    }

    boolean contains(int x, int z) {
      return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    /** {@code radius}, made small enough for a lake around (x, z) to stay inside. */
    int fit(int x, int z, int radius) {
      int margin = Math.min(Math.min(x - minX, maxX - x), Math.min(z - minZ, maxZ - z)) - 1;
      return Math.max(1, Math.min(radius, margin));
    }

    void set(WorldGenLevel level, BlockPos pos, BlockState state, int flags) {
      if (contains(pos.getX(), pos.getZ()) && !level.isOutsideBuildHeight(pos)) level.setBlock(pos, state, flags);
    }
  }

  private static void generateSurfaceDeposit(WorldGenLevel level, Area area, RandomSource rand, int x, int y, int z, int radius) {
    int depth = rand.nextDouble() < 0.5 ? 1 : 2;
    setOilColumnForLake(level, area, x, y, z, depth, 2);

    for (int w = 1; w <= radius; ++w) {
      float proba = (float) (radius - w + 4) / (float) (radius + 4);
      setOilWithProba(level, area, rand, proba, x, y, z + w, depth);
      setOilWithProba(level, area, rand, proba, x, y, z - w, depth);
      setOilWithProba(level, area, rand, proba, x + w, y, z, depth);
      setOilWithProba(level, area, rand, proba, x - w, y, z, depth);
      for (int i = 1; i <= w; ++i) {
        setOilWithProba(level, area, rand, proba, x + i, y, z + w, depth);
        setOilWithProba(level, area, rand, proba, x + i, y, z - w, depth);
        setOilWithProba(level, area, rand, proba, x + w, y, z + i, depth);
        setOilWithProba(level, area, rand, proba, x - w, y, z + i, depth);
        setOilWithProba(level, area, rand, proba, x - i, y, z + w, depth);
        setOilWithProba(level, area, rand, proba, x - i, y, z - w, depth);
        setOilWithProba(level, area, rand, proba, x + w, y, z - i, depth);
        setOilWithProba(level, area, rand, proba, x - w, y, z - i, depth);
      }
    }

    // Fill the holes surrounded by oil.
    for (int dx = x - radius; dx <= x + radius; ++dx) {
      for (int dz = z - radius; dz <= z + radius; ++dz) {
        if (!isOil(level, dx, y, dz) && isOilSurrounded(level, dx, y, dz)) {
          setOilColumnForLake(level, area, dx, y, dz, depth, 2);
        }
      }
    }
  }

  private static boolean isReplaceableFluid(WorldGenLevel level, int x, int y, int z) {
    BlockState state = level.getBlockState(new BlockPos(x, y, z));
    FluidState fluid = state.getFluidState();
    if (!fluid.isEmpty() && !fluid.getType().isSame(Fluids.LAVA)) return true;
    return state.getBlock() instanceof LiquidBlock && state.getBlock() != Blocks.LAVA;
  }

  private static boolean isOil(WorldGenLevel level, int x, int y, int z) {
    return level.getBlockState(new BlockPos(x, y, z)).is(EnergyFluids.OIL_BLOCK.get());
  }

  private static boolean isReplaceableForLake(WorldGenLevel level, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    BlockState state = level.getBlockState(pos);
    if (state.isAir() || !state.blocksMotion() || state.getBlock() instanceof BushBlock) return true;
    return !state.isSolidRender(level, pos);
  }

  private static boolean isOilAdjacent(WorldGenLevel level, int x, int y, int z) {
    return isOil(level, x + 1, y, z) || isOil(level, x - 1, y, z) || isOil(level, x, y, z + 1) || isOil(level, x, y, z - 1);
  }

  private static boolean isOilSurrounded(WorldGenLevel level, int x, int y, int z) {
    return isOil(level, x + 1, y, z) && isOil(level, x - 1, y, z) && isOil(level, x, y, z + 1) && isOil(level, x, y, z - 1);
  }

  private static void setOilWithProba(WorldGenLevel level, Area area, RandomSource rand, float proba, int x, int y, int z, int depth) {
    if (rand.nextFloat() <= proba && !level.isEmptyBlock(new BlockPos(x, y - depth - 1, z)) && isOilAdjacent(level, x, y, z)) {
      setOilColumnForLake(level, area, x, y, z, depth, 3);
    }
  }

  private static void setOilColumnForLake(WorldGenLevel level, Area area, int x, int y, int z, int depth, int update) {
    if (!area.contains(x, z) || !isReplaceableForLake(level, x, y + 1, z)) return;
    if (!level.isEmptyBlock(new BlockPos(x, y + 2, z))) return;
    BlockPos pos = new BlockPos(x, y, z);
    BlockPos support = pos.below();
    if (!isReplaceableFluid(level, x, y, z) && !level.getBlockState(support).isFaceSturdy(level, support, Direction.UP)) return;
    level.setBlock(pos, oil(), update);
    BlockPos above = pos.above();
    if (!level.isEmptyBlock(above)) level.setBlock(above, Blocks.AIR.defaultBlockState(), update);
    for (int d = 1; d <= depth - 1; d++) {
      BlockPos below = new BlockPos(x, y - d, z);
      BlockPos belowSupport = below.below();
      if (isReplaceableFluid(level, x, y - d, z) || !level.getBlockState(belowSupport).isFaceSturdy(level, belowSupport, Direction.UP)) return;
      level.setBlock(below, oil(), 2);
    }
  }

  /** Original {@code getTopBlock()}: the surface, skipping plants and non-solid blocks; fluid counts as surface. */
  static int getTopBlock(WorldGenLevel level, int x, int z) {
    int y = Math.min(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z), level.getMaxBuildHeight() - 1);
    for (; y > level.getMinBuildHeight(); --y) {
      BlockState state = level.getBlockState(new BlockPos(x, y, z));
      if (state.isAir()) continue;
      if (!state.getFluidState().isEmpty()) return y;
      if (!state.blocksMotion() || state.getBlock() instanceof BushBlock) continue;
      return y - 1;
    }
    return level.getMinBuildHeight() - 1;
  }

  private static double surfaceDeviation(WorldGenLevel level, int x, int y, int z, int radius) {
    int diameter = radius * 2;
    double deviation = 0;
    for (int i = 0; i < diameter; i++) {
      for (int k = 0; k < diameter; k++) {
        deviation += getTopBlock(level, x - radius + i, z - radius + k) - (double) y;
      }
    }
    // The original divides by the height (about 64); keep that scale, but never divide by zero or less.
    return Math.abs(deviation / (double) Math.max(1, y));
  }
}
