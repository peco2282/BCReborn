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

import com.peco2282.bcreborn.core.CoreBlocks;
import com.peco2282.bcreborn.core.block.SpringBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.level.ChunkEvent;

public class SpringPopulate {

  // A spring will be generated approximately every 40th chunk (0.025f chance).
  private static final float SPRING_CHANCE = 0.025f;

  // TODO: Fix this
//    @SubscribeEvent
  public void onChunkLoad(ChunkEvent.Load event) {
    LevelAccessor levelAccessor = event.getLevel();

    // Only run on server side, only for newly generated chunks
    if (levelAccessor.isClientSide()) {
      return;
    }
    if (!event.isNewChunk()) {
      return;
    }
    if (!(levelAccessor instanceof Level level)) {
      return;
    }

    // No water springs in the Nether or End
    if (!level.dimensionType().natural()) {
      return;
    }
    // Exclude Nether and End by dimension key
    var dimKey = level.dimension();
    if (dimKey.equals(Level.NETHER) || dimKey.equals(Level.END)) {
      return;
    }

    if (!SpringBlock.EnumSpring.WATER.canGen()) {
      return;
    }

    ChunkAccess chunk = event.getChunk();
    ChunkPos chunkPos = chunk.getPos();

    if (level.getRandom().nextFloat() > SPRING_CHANCE) {
      return;
    }

    int worldX = chunkPos.getMinBlockX() + level.getRandom().nextInt(16);
    int worldZ = chunkPos.getMinBlockZ() + level.getRandom().nextInt(16);

    doPopulate(level, worldX, worldZ);
  }

  private void doPopulate(Level level, int x, int z) {
    // Search for bedrock in the bottom 5 layers
    for (int i = 0; i < 5; i++) {
      BlockPos candidate = new BlockPos(x, i, z);
      BlockState state = level.getBlockState(candidate);

      if (!state.is(Blocks.BEDROCK)) {
        continue;
      }

      // Handle flat bedrock maps: place spring just above bedrock
      int y = i > 0 ? i : i - 1;
      BlockPos springPos = new BlockPos(x, y + 1, z);

      level.setBlock(springPos, CoreBlocks.SPRING.get().defaultBlockState()
        .setValue(SpringBlock.TYPE, SpringBlock.EnumSpring.WATER), 3);

      // Fill non-air blocks above the spring with water up to the surface
      for (int j = y + 2; j < level.getHeight(); j++) {
        BlockPos above = new BlockPos(x, j, z);
        if (level.isEmptyBlock(above)) {
          break;
        } else {
          level.setBlock(above, Blocks.WATER.defaultBlockState(), 3);
        }
      }

      break;
    }
  }
}
