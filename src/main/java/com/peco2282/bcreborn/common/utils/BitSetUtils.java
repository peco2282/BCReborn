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
package com.peco2282.bcreborn.common.utils;

import java.util.BitSet;

public final class BitSetUtils {
	private BitSetUtils() {

	}

	public static BitSet fromByteArray(byte[] bytes) {
		BitSet bits = new BitSet(bytes.length * 8);
		for (int i = 0; i < bytes.length * 8; i++) {
			if ((bytes[i / 8] & (1 << (i % 8))) != 0) {
				bits.set(i);
			}
		}
		return bits;
	}

	public static byte[] toByteArray(BitSet bits) {
		return toByteArray(bits, (bits.size() + 7) >> 3);
	}

	public static byte[] toByteArray(BitSet bits, int sizeInBytes) {
		byte[] bytes = new byte[sizeInBytes];
		for (int i = 0; i < bits.length(); i++) {
			if (bits.get(i)) {
				bytes[i / 8] |= 1 << (i % 8);
			}
		}
		return bytes;
	}
}
