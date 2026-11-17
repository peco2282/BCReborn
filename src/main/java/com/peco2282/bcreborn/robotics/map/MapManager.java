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
package com.peco2282.bcreborn.robotics.map;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import com.google.common.collect.HashBiMap;
import com.peco2282.bcreborn.common.utils.Utils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.io.File;
import java.util.Date;


/**
 * Port of the original {@code MapManager}: keeps a colour per block column of every chunk that was loaded, for the
 * Zone Planner's map. It is created when the server starts ({@link #start}) and saved under the world's
 * {@code buildcraft/zonemap} folder.
 * <p>
 * The original saved from a thread of its own. The regions are also written by the server thread, so here they
 * are saved on the server thread: every {@link #SAVE_INTERVAL} ticks, when a world unloads and when the server
 * stops.
 */
public class MapManager {
  private static final int UPDATE_DELAY = 60000;
  /** Original: every two minutes. */
  private static final int SAVE_INTERVAL = 2400;
  @Nullable
  private static MapManager instance;
  private final HashBiMap<Level, MapWorld> worldMap = HashBiMap.create();
  private final File location;
  private int ticksSinceSave;

  public MapManager(File location) {
    this.location = location;
  }

  /** Starts the map for this server and listens to chunk and block changes. */
  public static void start(MinecraftServer server) {
    stop();
    instance = new MapManager(server.getWorldPath(LevelResource.ROOT).resolve("buildcraft").resolve("zonemap").toFile());
    MinecraftForge.EVENT_BUS.register(instance);
  }

  /** Saves everything and stops listening. */
  public static void stop() {
    if (instance != null) {
      MinecraftForge.EVENT_BUS.unregister(instance);
      instance.saveAllWorlds();
      instance = null;
    }
  }

  @Nullable
  public static MapManager get() {
    return instance;
  }

  /**
   * The map of {@code level}, with the given chunk columns brought up to date if they are loaded but not mapped
   * yet (the original filled chunks that were loaded before the manager started in {@code initialize()}).
   */
  @Nullable
  public static MapWorld getLoaded(Level level, int chunkXMin, int chunkZMin, int chunkXMax, int chunkZMax) {
    if (instance == null || !(level instanceof ServerLevel serverLevel)) return null;
    MapWorld world = instance.getWorld(level);
    if (world == null) return null;
    for (int cx = chunkXMin; cx <= chunkXMax; cx++) {
      for (int cz = chunkZMin; cz <= chunkZMax; cz++) {
        if (!world.hasChunk(cx, cz)) {
          LevelChunk chunk = serverLevel.getChunkSource().getChunkNow(cx, cz);
          if (chunk != null) world.updateChunk(chunk);
        }
      }
    }
    return world;
  }

  public MapWorld getWorld(Level world) {
    if (world == null || world.isClientSide) {
      return null;
    }

    if (!worldMap.containsKey(world)) {
      synchronized (worldMap) {
        worldMap.put(world, new MapWorld(world, location));
      }
    }
    return worldMap.get(world);
  }

  private boolean doUpdate(MapWorld world, ChunkAccess chunk) {
    int x = chunk.getPos().x;
    int z = chunk.getPos().z;
    long updateTime = (new Date()).getTime() - UPDATE_DELAY;
    return world.getUpdateTime(x, z) < updateTime || !world.hasChunk(x, z);
  }

  private void updateChunk(Level rworld, ChunkAccess chunk, boolean force) {
    MapWorld world = getWorld(rworld);
    if (world != null && (force || doUpdate(world, chunk))) {
      world.updateChunk(chunk);
    }
  }

  private void updateChunkDelayed(Level rworld, ChunkAccess chunk, boolean force, byte time) {
    MapWorld world = getWorld(rworld);
    if (world != null && (force || doUpdate(world, chunk))) {
      world.updateChunkDelayed(chunk, time);
    }
  }

  @SubscribeEvent
  public void tickDelayedWorlds(TickEvent.LevelTickEvent event) {
    if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER) {
      MapWorld w = worldMap.get(event.level);
      if (w != null) {
        w.tick();
      }
    }
  }

  @SubscribeEvent
  public void tickSave(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END && ++ticksSinceSave >= SAVE_INTERVAL) {
      ticksSinceSave = 0;
      saveAllWorlds();
    }
  }

  @SubscribeEvent
  public void worldUnloaded(LevelEvent.Unload event) {
    if (worldMap.containsKey(event.getLevel())) {
      worldMap.get(event.getLevel()).save();
      synchronized (worldMap) {
        worldMap.remove(event.getLevel());
      }
    }
  }

  @SubscribeEvent
  public void chunkLoaded(ChunkEvent.Load event) {
    if (event.getLevel() instanceof Level level) {
      updateChunkDelayed(level, event.getChunk(), false, (byte) (40 + Utils.RANDOM.nextInt(20)));
    }
  }

  @SubscribeEvent
  public void chunkUnloaded(ChunkEvent.Unload event) {
    if (event.getLevel() instanceof Level level) {
      updateChunk(level, event.getChunk(), false);
    }
  }


  @SubscribeEvent
  public void blockPlaced(BlockEvent.EntityPlaceEvent event) {
    blockChanged(event.getLevel(), event.getPos());
  }

  @SubscribeEvent
  public void blockBroken(BlockEvent.BreakEvent event) {
    blockChanged(event.getLevel(), event.getPos());
  }

  /** Original: a change near the surface remaps the chunk (the chunk was looked up by block coordinates). */
  private void blockChanged(LevelAccessor accessor, BlockPos pos) {
    if (!(accessor instanceof Level level) || level.isClientSide) return;
    ChunkAccess chunk = level.getChunk(pos);
    MapWorld world = getWorld(level);
    if (world != null && doUpdate(world, chunk)) {
      int hv = level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
      if (pos.getY() >= (hv - 3)) {
        world.updateChunk(chunk);
      }
    }
  }

  public void saveAllWorlds() {
    synchronized (worldMap) {
      for (MapWorld world : worldMap.values()) {
        world.save();
      }
    }
  }
}
