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
package com.peco2282.bcreborn.builders.quarry;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.BuildersConfig;
import com.peco2282.bcreborn.builders.block.entity.QuarryBlockEntity;
import com.peco2282.bcreborn.common.Box;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.common.world.ForgeChunkManager;

/**
 * Quarry chunk loading (original {@code TileQuarry.forceChunkLoading()} and the {@code QuarryChunkloadCallback}).
 * <p>
 * The original requested a Forge ticket per quarry. Forge 1.20 keeps forced chunks per owner block position and
 * restores them when the world loads, so the quarry only adds / removes its chunks, and the loading callback drops
 * the tickets of quarries that no longer exist (or all of them when chunk loading is disabled).
 */
public final class QuarryChunkLoader {
  /** Forge 1.7.10 default {@code maximumChunksPerTicket}; larger areas fall back to the default area. */
  public static final int MAX_CHUNKS = 25;

  private QuarryChunkLoader() {
  }

  /** Registers the validation callback; call during mod setup. */
  public static void register() {
    ForgeChunkManager.setForcedChunkLoadingCallback(BCRebornBuilders.MODID, (level, helper) -> {
      for (BlockPos owner : helper.getBlockTickets().keySet().toArray(BlockPos[]::new)) {
        if (!BuildersConfig.isQuarryDoChunkLoading() || !(level.getBlockEntity(owner) instanceof QuarryBlockEntity)) {
          helper.removeAllTickets(owner);
        }
      }
    });
  }

  /** Original area check: {@code (xSize * zSize) >> 8 >= maxChunkListDepth}. */
  public static boolean isTooLarge(int xSize, int zSize) {
    return BuildersConfig.isQuarryDoChunkLoading() && ((xSize * zSize) >> 8) >= MAX_CHUNKS;
  }

  /** The quarry's own chunk plus every chunk of its box. */
  public static LongSet chunksFor(BlockPos quarry, Box box) {
    LongSet chunks = new LongOpenHashSet();
    chunks.add(ChunkPos.asLong(quarry.getX() >> 4, quarry.getZ() >> 4));
    if (box.isInitialized()) {
      for (int cx = box.xMin >> 4; cx <= box.xMax >> 4; cx++) {
        for (int cz = box.zMin >> 4; cz <= box.zMax >> 4; cz++) {
          chunks.add(ChunkPos.asLong(cx, cz));
        }
      }
    }
    return chunks;
  }

  /** Forces {@code chunks} (ticking, as forced chunks were in 1.7.10). */
  public static void force(ServerLevel level, BlockPos owner, LongSet chunks) {
    for (long chunk : chunks) {
      ForgeChunkManager.forceChunk(level, BCRebornBuilders.MODID, owner, ChunkPos.getX(chunk), ChunkPos.getZ(chunk), true, true);
    }
  }

  /** Original {@code ForgeChunkManager.releaseTicket()}. */
  public static void release(ServerLevel level, BlockPos owner, LongSet chunks) {
    for (long chunk : chunks) {
      ForgeChunkManager.forceChunk(level, BCRebornBuilders.MODID, owner, ChunkPos.getX(chunk), ChunkPos.getZ(chunk), false, true);
    }
  }
}
