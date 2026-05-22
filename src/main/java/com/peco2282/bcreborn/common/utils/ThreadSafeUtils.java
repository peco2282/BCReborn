package com.peco2282.bcreborn.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;

public final class ThreadSafeUtils {
	private static final ThreadLocal<LevelChunk> lastChunk = new ThreadLocal<>();

	private ThreadSafeUtils() {

	}

	public static LevelChunk getChunk(Level world, int x, int z) {
		LevelChunk chunk;
		chunk = lastChunk.get();

		if (chunk != null) {
			if (chunk.isInLevel()) {
				if (chunk.getLevel() == world && chunk.getPos().x == x && chunk.getPos().z == z) {
					return chunk;
				}
			} else {
                lastChunk.remove();
			}
		}

		ChunkSource provider = world.getChunkSource();
		// These probably won't guarantee full thread safety, but it's our best bet.
		if (!Utils.CAULDRON_DETECTED && provider instanceof ServerChunkCache) {
			// Slight optimization
			chunk = provider.getChunkNow(x, z);
		} else {
			chunk = provider.hasChunk(x, z) ? provider.getChunkNow(x, z) : null;
		}

		if (chunk != null) {
			lastChunk.set(chunk);
		}
		return chunk;
	}
}
