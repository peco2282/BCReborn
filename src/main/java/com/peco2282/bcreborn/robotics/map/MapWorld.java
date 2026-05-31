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

import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class MapWorld {
	private final Long2ObjectOpenHashMap<MapRegion> regionMap;
	private final HashMap<ChunkAccess, Integer> timeToUpdate = new HashMap<ChunkAccess, Integer>();
	private final Long2LongOpenHashMap regionUpdateTime;
	private final LongOpenHashSet updatedChunks;
	private final File location;

	public MapWorld(Level world, File location) {
		regionMap = new Long2ObjectOpenHashMap<>();
		regionUpdateTime = new Long2LongOpenHashMap();
		updatedChunks = new LongOpenHashSet();

		ResourceLocation dimension = world.dimension().location();
		String saveFolder = dimension.getNamespace() + "/" + dimension.getPath();
        this.location = new File(location, saveFolder);
		try {
			this.location.mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MapRegion getRegion(int x, int z) {
		long id = MapUtils.getIDFromCoords(x, z);
		MapRegion region = regionMap.get(id);
		if (region == null) {
			region = new MapRegion(x, z);

			// Check in the location first
			File target = new File(location, "r" + x + "," + z + ".nbt");
			if (target.exists()) {
				try {
					CompoundTag nbt = NbtIo.readCompressed(target);
					if (nbt != null) {
						region.readFromNBT(nbt);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			regionMap.put(id, region);
		}
		return region;
	}

	private MapChunk getChunk(int x, int z) {
		MapRegion region = getRegion(x >> 4, z >> 4);
		return region.getChunk(x & 15, z & 15);
	}

	public boolean hasChunk(int x, int z) {
		MapRegion region = getRegion(x >> 4, z >> 4);
		return region.hasChunk(x & 15, z & 15);
	}

	public void save() {
		long[] chunkList;
		synchronized (updatedChunks) {
			chunkList = updatedChunks.toLongArray();
			updatedChunks.clear();
		}

		for (long id : chunkList) {
			MapRegion region = regionMap.get(id);
			if (region == null) {
				continue;
			}

			CompoundTag output = new CompoundTag();
			region.writeToNBT(output);
			File file = new File(location, "r" + MapUtils.getXFromID(id) + "," + MapUtils.getZFromID(id) + ".nbt");

			try {
				NbtIo.writeCompressed(output, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int getColor(int x, int z) {
		MapChunk chunk = getChunk(x >> 4, z >> 4);
		return chunk.getColor(x & 15, z & 15);
	}

	public void tick() {
		if (timeToUpdate.size() > 0) {
			synchronized (timeToUpdate) {
                Set<ChunkAccess> chunks = new HashSet<>(timeToUpdate.keySet());
				for (ChunkAccess c : chunks) {
					int v = timeToUpdate.get(c);
					if (v > 1) {
						timeToUpdate.put(c, v - 1);
					} else {
						try {
							updateChunk(c);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void updateChunk(ChunkAccess rchunk) {
		long id = MapUtils.getIDFromCoords(rchunk);
		MapChunk chunk = getChunk(rchunk.getPos().x, rchunk.getPos().z);
		chunk.update(rchunk);
		synchronized (updatedChunks) {
			updatedChunks.add(id);
		}
		synchronized (timeToUpdate) {
			timeToUpdate.remove(rchunk);
		}
		regionUpdateTime.put(id, (new Date()).getTime());
	}

	public long getUpdateTime(int x, int z) {
		return regionUpdateTime.get(MapUtils.getIDFromCoords(x, z));
	}

	public void updateChunkDelayed(ChunkAccess chunk, byte time) {
		synchronized (timeToUpdate) {
			timeToUpdate.put(chunk, (int) time);
		}
	}
}
