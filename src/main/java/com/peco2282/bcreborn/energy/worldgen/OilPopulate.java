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

import com.peco2282.bcreborn.energy.ConfigEnergy;
import com.peco2282.bcreborn.energy.FluidsEnergy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public final class OilPopulate {

  public static final OilPopulate INSTANCE = new OilPopulate();

  private static final int LARGE_WELL_HEIGHT = 16;
  private static final int MEDIUM_WELL_HEIGHT = 6;

  public final Set<Integer> excessiveBiomes = new HashSet<>();
  public final Set<Integer> surfaceDepositBiomes = new HashSet<>();
  public final Set<Integer> excludedBiomes = new HashSet<>();

  private OilPopulate() {
  }

  private static BlockState getOilBlockState() {
    return FluidsEnergy.OIL_BLOCK.get().defaultBlockState();
  }

  @SubscribeEvent
  public void onChunkLoad(ChunkEvent.Load event) {
    LevelAccessor levelAccessor = event.getLevel();

    if (levelAccessor.isClientSide()) {
      return;
    }
    if (!event.isNewChunk()) {
      return;
    }
    if (!(levelAccessor instanceof Level level)) {
      return;
    }
    if (!ConfigEnergy.isWorldgenEnable()) {
      return;
    }

    var dimKey = level.dimension();
    if (dimKey.equals(Level.NETHER) || dimKey.equals(Level.END)) {
      return;
    }

    ChunkAccess chunk = event.getChunk();
    ChunkPos chunkPos = chunk.getPos();

    int chunkX = chunkPos.x;
    int chunkZ = chunkPos.z;

    long seed = (level instanceof ServerLevel sl) ? sl.getSeed() : level.getRandom().nextLong();
    Random rand = new Random(seed ^ ((long) chunkX << 32 | (chunkZ & 0xFFFFFFFFL)));
    generateOil(level, rand, chunkX, chunkZ);
  }

  public void generateOil(Level level, Random rand, int chunkX, int chunkZ) {
    int x = chunkX * 16 + 8 + rand.nextInt(16);
    int z = chunkZ * 16 + 8 + rand.nextInt(16);

    BlockPos pos = new BlockPos(x, 64, z);
    Biome biome = level.getBiome(pos).value();

    double bonus = 1.0;
    bonus *= ConfigEnergy.getOilWellGenerationRate();

    GenType type = GenType.NONE;
    if (rand.nextDouble() <= 0.0004 * bonus) {
      type = GenType.LARGE;
    } else if (rand.nextDouble() <= 0.001 * bonus) {
      type = GenType.MEDIUM;
    } else if (rand.nextDouble() <= 0.02 * bonus) {
      type = GenType.LAKE;
    }

    if (type == GenType.NONE) {
      return;
    }

    int groundLevel = getTopBlock(level, x, z);
    if (groundLevel < 5) {
      return;
    }

    double deviation = surfaceDeviation(level, x, groundLevel, z, 8);
    if (deviation > 0.45) {
      return;
    }

    if (type == GenType.LARGE || type == GenType.MEDIUM) {

      int wellHeight = MEDIUM_WELL_HEIGHT;
      if (type == GenType.LARGE) {
        wellHeight = LARGE_WELL_HEIGHT;
      }
      int maxHeight = groundLevel + wellHeight;
      if (maxHeight >= level.getHeight() - 1) {
        return;
      }

      int wellY = 20 + rand.nextInt(10);
      if (wellY > groundLevel) {
        return;
      }

      int radius;
      if (type == GenType.LARGE) {
        radius = 8 + rand.nextInt(9);
      } else {
        radius = 4 + rand.nextInt(4);
      }

      int radiusSq = radius * radius;

      for (int poolX = -radius; poolX <= radius; poolX++) {
        for (int poolY = -radius; poolY <= radius; poolY++) {
          for (int poolZ = -radius; poolZ <= radius; poolZ++) {
            int distance = poolX * poolX + poolY * poolY + poolZ * poolZ;
            if (distance <= radiusSq) {
              BlockPos oilPos = new BlockPos(poolX + x, poolY + wellY, poolZ + z);
              int flags = distance == radiusSq ? 3 : 2;
              level.setBlock(oilPos, getOilBlockState(), flags);
            }
          }
        }
      }

      int lakeRadius;
      if (type == GenType.LARGE) {
        lakeRadius = 25 + rand.nextInt(20);
      } else {
        lakeRadius = 5 + rand.nextInt(10);
      }
      generateSurfaceDeposit(level, rand, x, groundLevel, z, lakeRadius);

      boolean makeSpring = type == GenType.LARGE && ConfigEnergy.isSpawnOilSprings();

      int baseY = makeSpring ? level.getMinBuildHeight() : wellY;

      for (int y = baseY + 1; y <= maxHeight; ++y) {
        level.setBlock(new BlockPos(x, y, z), getOilBlockState(), 3);
      }

      if (type == GenType.LARGE) {
        for (int y = wellY; y <= maxHeight - wellHeight / 2; ++y) {
          level.setBlock(new BlockPos(x + 1, y, z), getOilBlockState(), 3);
          level.setBlock(new BlockPos(x - 1, y, z), getOilBlockState(), 3);
          level.setBlock(new BlockPos(x, y, z + 1), getOilBlockState(), 3);
          level.setBlock(new BlockPos(x, y, z - 1), getOilBlockState(), 3);
        }
      }

    } else if (type == GenType.LAKE) {

      BlockPos lakePos = new BlockPos(x, groundLevel, z);
      BlockState blockState = level.getBlockState(lakePos);
      // Place lake if on solid ground
      if (!blockState.isAir()) {
        generateSurfaceDeposit(level, rand, x, groundLevel, z, 5 + rand.nextInt(10));
      }
    }
  }

  public void generateSurfaceDeposit(Level level, Random rand, int x, int y, int z, int radius) {
    int depth = rand.nextDouble() < 0.5 ? 1 : 2;

    setOilColumnForLake(level, x, y, z, depth, 2);

    for (int w = 1; w <= radius; ++w) {
      float proba = (float) (radius - w + 4) / (float) (radius + 4);

      setOilWithProba(level, rand, proba, x, y, z + w, depth);
      setOilWithProba(level, rand, proba, x, y, z - w, depth);
      setOilWithProba(level, rand, proba, x + w, y, z, depth);
      setOilWithProba(level, rand, proba, x - w, y, z, depth);

      for (int i = 1; i <= w; ++i) {
        setOilWithProba(level, rand, proba, x + i, y, z + w, depth);
        setOilWithProba(level, rand, proba, x + i, y, z - w, depth);
        setOilWithProba(level, rand, proba, x + w, y, z + i, depth);
        setOilWithProba(level, rand, proba, x - w, y, z + i, depth);

        setOilWithProba(level, rand, proba, x - i, y, z + w, depth);
        setOilWithProba(level, rand, proba, x - i, y, z - w, depth);
        setOilWithProba(level, rand, proba, x + w, y, z - i, depth);
        setOilWithProba(level, rand, proba, x - w, y, z - i, depth);
      }
    }

    for (int dx = x - radius; dx <= x + radius; ++dx) {
      for (int dz = z - radius; dz <= z + radius; ++dz) {
        if (isOil(level, dx, y, dz)) {
          continue;
        }
        if (isOilSurrounded(level, dx, y, dz)) {
          setOilColumnForLake(level, dx, y, dz, depth, 2);
        }
      }
    }
  }

  private boolean isReplaceableFluid(Level level, int x, int y, int z) {
    BlockState state = level.getBlockState(new BlockPos(x, y, z));
    FluidState fluidState = state.getFluidState();
    if (!fluidState.isEmpty() && !fluidState.getType().isSame(Fluids.LAVA)
      && !fluidState.getType().isSame(Fluids.FLOWING_LAVA)) {
      return true;
    }
    Block block = state.getBlock();
    return block instanceof LiquidBlock && block != Blocks.LAVA;
  }

  private boolean isOil(Level level, int x, int y, int z) {
    BlockState state = level.getBlockState(new BlockPos(x, y, z));
    return state.getBlock() == FluidsEnergy.OIL_BLOCK.get();
  }

  private boolean isReplaceableForLake(Level level, int x, int y, int z) {
    BlockPos pos = new BlockPos(x, y, z);
    BlockState state = level.getBlockState(pos);

    if (state.isAir()) {
      return true;
    }

    Block block = state.getBlock();

    if (!state.blocksMotion()) {
      return true;
    }

    if (block instanceof BushBlock) {
      return true;
    }

    return !state.isSolidRender(level, pos);
  }

  private boolean isOilAdjacent(Level level, int x, int y, int z) {
    return isOil(level, x + 1, y, z)
      || isOil(level, x - 1, y, z)
      || isOil(level, x, y, z + 1)
      || isOil(level, x, y, z - 1);
  }

  private boolean isOilSurrounded(Level level, int x, int y, int z) {
    return isOil(level, x + 1, y, z)
      && isOil(level, x - 1, y, z)
      && isOil(level, x, y, z + 1)
      && isOil(level, x, y, z - 1);
  }

  private void setOilWithProba(Level level, Random rand, float proba, int x, int y, int z, int depth) {
    if (rand.nextFloat() <= proba && !level.isEmptyBlock(new BlockPos(x, y - depth - 1, z))) {
      if (isOilAdjacent(level, x, y, z)) {
        setOilColumnForLake(level, x, y, z, depth, 3);
      }
    }
  }

  private void setOilColumnForLake(Level level, int x, int y, int z, int depth, int update) {
    if (isReplaceableForLake(level, x, y + 1, z)) {
      if (!level.isEmptyBlock(new BlockPos(x, y + 2, z))) {
        return;
      }
      BlockPos pos = new BlockPos(x, y, z);
      if (isReplaceableFluid(level, x, y, z) || level.getBlockState(new BlockPos(x, y - 1, z)).isFaceSturdy(level, new BlockPos(x, y - 1, z), Direction.UP)) {
        level.setBlock(pos, getOilBlockState(), update);
      } else {
        return;
      }
      BlockPos above = new BlockPos(x, y + 1, z);
      if (!level.isEmptyBlock(above)) {
        level.setBlock(above, Blocks.AIR.defaultBlockState(), update);
      }

      for (int d = 1; d <= depth - 1; d++) {
        BlockPos below = new BlockPos(x, y - d, z);
        BlockPos belowSupport = new BlockPos(x, y - d - 1, z);
        if (isReplaceableFluid(level, x, y - d, z) || !level.getBlockState(belowSupport).isFaceSturdy(level, belowSupport, Direction.UP)) {
          return;
        }
        level.setBlock(below, getOilBlockState(), 2);
      }
    }
  }

  private int getTopBlock(Level level, int x, int z) {
    int y = level.getHeight() - 1;

    for (; y > level.getMinBuildHeight(); --y) {
      BlockPos pos = new BlockPos(x, y, z);
      BlockState state = level.getBlockState(pos);

      if (state.isAir()) {
        continue;
      }

      Block block = state.getBlock();

      if (!state.getFluidState().isEmpty()) {
        return y;
      }

      if (!state.blocksMotion()) {
        continue;
      }

      if (block instanceof BushBlock) {
        continue;
      }

      return y - 1;
    }

    return -1;
  }

  private double surfaceDeviation(Level level, int x, int y, int z, int radius) {
    int diameter = radius * 2;
    double deviation = 0;
    for (int i = 0; i < diameter; i++) {
      for (int k = 0; k < diameter; k++) {
        deviation += getTopBlock(level, x - radius + i, z - radius + k) - (double) y;
      }
    }
    return Math.abs(deviation / (double) y);
  }

  private enum GenType {
    LARGE, MEDIUM, LAKE, NONE
  }
}
