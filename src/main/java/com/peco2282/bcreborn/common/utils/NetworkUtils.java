package com.peco2282.bcreborn.common.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public final class NetworkUtils {
	private NetworkUtils() {

	}

	public static void writeUTF(FriendlyByteBuf data, String str) {
        data.writeUtf(str);
    }

	public static String readUTF(FriendlyByteBuf data) {
		return data.readUtf();
	}

	public static void writeNBT(FriendlyByteBuf data, CompoundTag nbt) {
		data.writeNbt(nbt);
	}

	public static CompoundTag readNBT(FriendlyByteBuf data) {
		return data.readNbt();
	}

	public static void writeStack(FriendlyByteBuf data, ItemStack stack) {
		data.writeItem(stack);
	}

	public static ItemStack readStack(FriendlyByteBuf data) {
		return data.readItem();
	}

	public static void writeByteArray(FriendlyByteBuf stream, byte[] data) {
		stream.writeByteArray(data);
	}

	public static byte[] readByteArray(FriendlyByteBuf stream) {
		return stream.readByteArray();
	}
}
