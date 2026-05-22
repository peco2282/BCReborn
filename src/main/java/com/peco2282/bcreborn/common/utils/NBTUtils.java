/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.utils;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.item.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public final class NBTUtils {

	/**
	 * Deactivate constructor
	 */
	private NBTUtils() {

	}


	public static CompoundTag load(byte[] data) {
		try {
			CompoundTag nbt = NbtIo.readCompressed(new ByteArrayInputStream(data));
			return nbt;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static CompoundTag getItemData(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return null;
		}
		return stack.getOrCreateTag();
	}

	public static void writeUUID(CompoundTag data, String tag, UUID uuid) {
		if (uuid == null || data == null) {
			return;
		}
		data.putUUID(tag, uuid);
	}

	public static UUID readUUID(CompoundTag data, String tag) {
		if (data != null && data.hasUUID(tag)) {
			return data.getUUID(tag);
		}
		return null;
	}

	public static byte[] save(CompoundTag compound) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			NbtIo.writeCompressed(compound, baos);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
}
