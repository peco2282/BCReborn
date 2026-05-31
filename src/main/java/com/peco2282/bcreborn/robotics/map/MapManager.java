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

import java.io.File;
import java.util.Date;

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


public class MapManager implements Runnable {
	private static final int UPDATE_DELAY = 60000;
	private final HashBiMap<Level, MapWorld> worldMap = HashBiMap.create();
	private final File location;
	private boolean stop = false;
	private long lastSaveTime;

	public MapManager(File location) {
		this.location = location;
	}

	public void stop() {
		stop = true;
		saveAllWorlds();
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
	public void blockPlaced(BlockEvent.EntityPlaceEvent placeEvent) {
		ChunkAccess chunk = placeEvent.getLevel().getChunk(placeEvent.getPos().getX(), placeEvent.getPos().getZ());
		MapWorld world = getWorld((Level) placeEvent.getLevel());
		if (world != null && doUpdate(world, chunk)) {
			int hv = placeEvent.getLevel().getHeight(Heightmap.Types.WORLD_SURFACE, placeEvent.getPos().getX(), placeEvent.getPos().getZ());
			if (placeEvent.getPos().getY() >= (hv - 3)) {
				world.updateChunk(chunk);
			}
		}
	}

	@SubscribeEvent
	public void blockBroken(BlockEvent.BreakEvent placeEvent) {
		ChunkAccess chunk = placeEvent.getLevel().getChunk(placeEvent.getPos().getX(), placeEvent.getPos().getZ());
		MapWorld world = getWorld((Level) placeEvent.getLevel());
		if (world != null && doUpdate(world, chunk)) {
			int hv = placeEvent.getLevel().getHeight(Heightmap.Types.WORLD_SURFACE, placeEvent.getPos().getX(), placeEvent.getPos().getZ());
			if (placeEvent.getPos().getY() >= (hv - 3)) {
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

	@Override
	public void run() {
		lastSaveTime = (new Date()).getTime();

		while (!stop) {
			long now = (new Date()).getTime();

			if (now - lastSaveTime > 120000) {
				saveAllWorlds();
				lastSaveTime = now;
			}

			try {
				Thread.sleep(4000);
			} catch (Exception e) {

			}
		}
	}

	public void initialize() {
//		for (WorldServer ws : DimensionManager.getWorlds()) {
//			MapWorld mw = getWorld(ws);
//			IChunkProvider provider = ws.getChunkProvider();
//			if (provider instanceof ChunkProviderServer) {
//				for (Object o : ((ChunkProviderServer) provider).func_152380_a()) {
//					if (o != null && o instanceof Chunk) {
//						Chunk c = (Chunk) o;
//						if (!mw.hasChunk(c.xPosition, c.zPosition)) {
//							mw.updateChunkDelayed(c, (byte) (40 + Utils.RANDOM.nextInt(20)));
//						}
//					}
//				}
//			}
//		}
	}
}
