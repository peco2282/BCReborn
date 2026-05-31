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
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.core.INBT;
import com.peco2282.bcreborn.api.core.ISerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.ArrayUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LibraryId implements INBT, Comparable<LibraryId>, ISerializable {
  public static final char BPT_SEP_CHARACTER = '-';

  public byte[] uniqueId;
  public String name = "";
  public String extension = "tpl";

  public String completeId;

  public LibraryId() {
  }

  public void generateUniqueId(byte[] data) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] id = digest.digest(data);

      uniqueId = id;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void write(CompoundTag nbt) {
    nbt.putByteArray("uniqueBptId", uniqueId);
    nbt.putString("name", name);
    nbt.putString("extension", extension);
  }

  @Override
  public void read(CompoundTag nbt) {
    uniqueId = nbt.getByteArray("uniqueBptId");
    name = nbt.getString("name");
    if (nbt.contains("kind")) {
      extension = nbt.getByte("kind") > 0 ? "bpt" : "tpl";
    } else {
      extension = nbt.getString("extension");
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof LibraryId) {
      return Arrays.equals(uniqueId, ((LibraryId) obj).uniqueId);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(ArrayUtils.addAll(uniqueId, name.getBytes()));
  }

  public String getCompleteId() {
    if (completeId == null) {
      if (uniqueId.length > 0) {
        completeId = name + BPT_SEP_CHARACTER
            + toString(uniqueId);
      } else {
        completeId = name;
      }
    }

    return completeId;
  }

  @Override
  public String toString() {
    return getCompleteId();
  }

  private static char toHex(int i) {
    if (i < 10) {
      return (char) ('0' + i);
    } else {
      return (char) ('a' - 10 + i);
    }
  }

  private static int fromHex(char c) {
    if (c >= '0' && c <= '9') {
      return c - '0';
    } else {
      return c - ('a' - 10);
    }
  }

  @Override
  public int compareTo(LibraryId o) {
    return getCompleteId().compareTo(o.getCompleteId());
  }

  public static String toString(byte[] bytes) {
    char[] ret = new char[bytes.length * 2];

    for (int i = 0; i < bytes.length; i++) {
      int val = bytes[i] + 128;

      ret[i * 2] = toHex(val >> 4);
      ret[i * 2 + 1] = toHex(val & 0xf);
    }

    return new String(ret);
  }

  public static byte[] toBytes(String suffix) {
    byte[] result = new byte[suffix.length() / 2];

    for (int i = 0; i < result.length; ++i) {
      result[i] = (byte) ((byte) (fromHex(suffix.charAt(i * 2 + 1)))
          + (byte) (fromHex(suffix.charAt(i * 2)) << 4));

      result[i] -= 128;
    }

    return result;
  }

  @Override
  public void readData(FriendlyByteBuf stream) {
    uniqueId = stream.readByteArray();
    name = stream.readUtf();
    extension = stream.readUtf();
  }

  public static LibraryId decode(FriendlyByteBuf stream) {
    LibraryId libraryId = new LibraryId();
    libraryId.readData(stream);
    return libraryId;
  }

  @Override
  public void writeData(FriendlyByteBuf stream) {
    stream.writeByteArray(uniqueId);
    stream.writeUtf(name);
    stream.writeUtf(extension);
  }
}
