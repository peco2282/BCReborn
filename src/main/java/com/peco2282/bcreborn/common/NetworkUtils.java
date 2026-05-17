package com.peco2282.bcreborn.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.nio.charset.StandardCharsets;

public interface NetworkUtils {
  static void writeUTF(FriendlyByteBuf data, String str) {
    data.writeUtf(str);
//    if (str == null) {
//      data.writeInt(0);
//      return;
//    }
//    byte[] b = str.getBytes(StandardCharsets.UTF_8);
//    data.writeInt(b.length);
//    data.writeBytes(b);
  }

  static String readUTF(FriendlyByteBuf data) {
    return data.readUtf();
//    int len = data.readInt();
//    if (len == 0) {
//      return "";
//    }
//    byte[] b = new byte[len];
//    data.readBytes(b);
//    return new String(b, StandardCharsets.UTF_8);
  }

  static void writeNBT(FriendlyByteBuf data, CompoundTag nbt) {
    data.writeNbt(nbt);
//    try {
//      byte[] compressed = CompressedStreamTools.compress(nbt);
//      data.writeInt(compressed.length);
//      data.writeBytes(compressed);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
  }

  static CompoundTag readNBT(FriendlyByteBuf data) {
    return data.readNbt();
//    try {
//      int length = data.readInt();
//      byte[] compressed = new byte[length];
//      data.readBytes(compressed);
//      DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(compressed))));
//      return NbtIo.read(dataInputStream);
//    } catch (IOException e) {
//      e.printStackTrace();
//      return null;
//    }
  }

  static void writeStack(FriendlyByteBuf data, ItemStack stack) {
    data.writeItemStack(stack, true);
//    if (stack == null || stack.getItem() == null || stack.getCount() < 0) {
//      data.writeByte(0);
//    } else {
//      // ItemStacks generally shouldn't have a stackSize above 64,
//      // so we use this "trick" to save bandwidth by storing it in the first byte.
//      data.writeByte((Mth.clamp(stack.getCount() + 1, 0, 64) & 0x7F) | (stack.hasTag() ? 128 : 0));
//      data.writeShort(BuiltInRegistries.ITEM.getId(stack.getItem()));
//      data.writeShort(stack.getDamageValue());
//      if (stack.hasTag()) {
//        writeNBT(data, stack.getTag());
//      }
//    }
  }

  static ItemStack readStack(FriendlyByteBuf data) {
    return data.readItem();
//    int flags = data.readUnsignedByte();
//    if (flags == 0) {
//      return null;
//    } else {
//      boolean hasCompound = (flags & 0x80) != 0;
//      int stackSize = (flags & 0x7F) - 1;
//      int itemId = data.readUnsignedShort();
//      int itemDamage = data.readShort();
//      ItemStack stack = new ItemStack(Item.getItemById(itemId), stackSize, itemDamage);
//      if (hasCompound) {
//        stack.setTagCompound(readNBT(data));
//      }
//      return stack;
//    }
  }

  static void writeByteArray(FriendlyByteBuf stream, byte[] data) {
    stream.writeByteArray(data);
//    stream.writeInt(data.length);
//    stream.writeBytes(data);
  }

  static byte[] readByteArray(FriendlyByteBuf stream) {
    return stream.readByteArray();
//    byte[] data = new byte[stream.readInt()];
//    stream.readBytes(data, 0, data.length);
//    return data;
  }
}
