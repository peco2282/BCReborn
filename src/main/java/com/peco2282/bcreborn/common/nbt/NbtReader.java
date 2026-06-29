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
package com.peco2282.bcreborn.common.nbt;

import com.peco2282.bcreborn.api.core.INBTSerializable;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.function.*;

public class NbtReader {
  private final CompoundTag nbt;

  private NbtReader(CompoundTag nbt) {
    this.nbt = nbt;
  }

  public static NbtReader of(CompoundTag nbt) {
    return new NbtReader(nbt);
  }

  public int getInt(String key) {
    return nbt.getInt(key);
  }

  public int getIntOrDefault(String key, int defaultValue) {
    return nbt.contains(key) ? nbt.getInt(key) : defaultValue;
  }

  public void applyInt(String key, IntConsumer consumer) {
    consumer.accept(nbt.getInt(key));
  }

  public long getLong(String key) {
    return nbt.getLong(key);
  }

  public long getLongOrDefault(String key, long defaultValue) {
    return nbt.contains(key) ? nbt.getLong(key) : defaultValue;
  }

  public void applyLong(String key, LongConsumer consumer) {
    consumer.accept(nbt.getLong(key));
  }

  public float getFloat(String key) {
    return nbt.getFloat(key);
  }

  public float getFloatOrDefault(String key, float defaultValue) {
    return nbt.contains(key) ? nbt.getFloat(key) : defaultValue;
  }

  public void applyFloat(String key, FloatConsumer consumer) {
    consumer.accept(nbt.getFloat(key));
  }

  public double getDouble(String key) {
    return nbt.getDouble(key);
  }

  public double getDoubleOrDefault(String key, double defaultValue) {
    return nbt.contains(key) ? nbt.getDouble(key) : defaultValue;
  }

  public void applyDouble(String key, DoubleConsumer consumer) {
    consumer.accept(nbt.getDouble(key));
  }

  public byte getByte(String key) {
    return nbt.getByte(key);
  }

  public byte getByteOrDefault(String key, byte defaultValue) {
    return nbt.contains(key) ? nbt.getByte(key) : defaultValue;
  }

  public void applyByte(String key, ByteConsumer consumer) {
    consumer.accept(nbt.getByte(key));
  }

  public short getShort(String key) {
    return nbt.getShort(key);
  }

  public short getShortOrDefault(String key, short defaultValue) {
    return nbt.contains(key) ? nbt.getShort(key) : defaultValue;
  }

  public void applyShort(String key, ShortConsumer consumer) {
    consumer.accept(nbt.getShort(key));
  }

  public boolean getBoolean(String key) {
    return nbt.getBoolean(key);
  }

  public boolean getBooleanOrDefault(String key, boolean defaultValue) {
    return nbt.contains(key) ? nbt.getBoolean(key) : defaultValue;
  }

  public void applyBoolean(String key, BooleanConsumer consumer) {
    consumer.accept(nbt.getBoolean(key));
  }

  public String getString(String key) {
    return nbt.getString(key);
  }

  public String getStringOrDefault(String key, String defaultValue) {
    return nbt.contains(key) ? nbt.getString(key) : defaultValue;
  }

  public void applyString(String key, Consumer<String> consumer) {
    consumer.accept(nbt.getString(key));
  }

  @Nullable
  public UUID getUUID(String key) {
    return nbt.hasUUID(key) ? nbt.getUUID(key) : null;
  }

  public void applyUUID(String key, Consumer<UUID> consumer) {
    consumer.accept(nbt.getUUID(key));
  }

  public byte[] getByteArray(String key) {
    return nbt.getByteArray(key);
  }

  public void applyByteArray(String key, Consumer<byte[]> consumer) {
    consumer.accept(nbt.getByteArray(key));
  }

  public int[] getIntArray(String key) {
    return nbt.getIntArray(key);
  }

  public void applyIntArray(String key, Consumer<int[]> consumer) {
    consumer.accept(nbt.getIntArray(key));
  }

  public long[] getLongArray(String key) {
    return nbt.getLongArray(key);
  }

  public void applyLongArray(String key, Consumer<long[]> consumer) {
    consumer.accept(nbt.getLongArray(key));
  }

  public BlockPos getBlockPos(String key) {
    return BlockPos.of(nbt.getLong(key));
  }

  public BlockPos getBlockPosOrDefault(String key, BlockPos defaultValue) {
    return nbt.contains(key) ? BlockPos.of(nbt.getLong(key)) : defaultValue;
  }

  public void applyBlockPos(String key, Consumer<BlockPos> consumer) {
    if (nbt.contains(key)) {
      consumer.accept(getBlockPos(key));
    }
  }

  public BlockState getBlockState(String key) {
    ResourceLocation rl = getResource(key);
    if (rl != null) {
      Block block = ForgeRegistries.BLOCKS.getValue(rl);
      if (block != null) {
        return block.defaultBlockState();
      }
    }
    return null;
  }

  public BlockState getBlockStateOrDefault(String key, BlockState defaultValue) {
    BlockState state = getBlockState(key);
    return state != null ? state : defaultValue;
  }

  public void applyBlockState(String key, Consumer<BlockState> consumer) {
    BlockState state = getBlockState(key);
    if (state != null) {
      consumer.accept(state);
    }
  }

  public CompoundTag getCompound(String key) {
    return getCompoundOrDefault(key, new CompoundTag());
  }

  public CompoundTag getCompoundOrDefault(String key, CompoundTag defaultValue) {
    return nbt.contains(key) ? nbt.getCompound(key) : defaultValue;
  }

  public void applyCompound(String key, Consumer<CompoundTag> consumer) {
    consumer.accept(nbt.getCompound(key));
  }

  @Nullable
  public ResourceLocation getResource(String key) {
    return ResourceLocation.tryParse(getString(key));
  }

  public ResourceLocation getResourceOrDefault(String key, ResourceLocation defaultValue) {
    ResourceLocation res = ResourceLocation.tryParse(getString(key));
    return res != null ? res : defaultValue;
  }

  public ResourceLocation getNotNullResource(String key) {
    return ResourceLocation.parse(getString(key));
  }

  public void applyResource(String key, Consumer<ResourceLocation> consumer) {
    consumer.accept(getNotNullResource(key));
  }

  public <E extends Enum<E> & StringRepresentable> E getEnum(String key, Class<E> enumClass) {
    return getEnum(key, enumClass, enumClass.getEnumConstants()[0]);
  }

  public <E extends Enum<E> & StringRepresentable> E getEnum(String key, Class<E> enumClass, E fallback) {
    if (!nbt.contains(key)) {
      return fallback;
    }

    String value = nbt.getString(key);
    for (E e : enumClass.getEnumConstants()) {
      if (e.getSerializedName().equals(value)) {
        return e;
      }
    }
    return fallback;
  }

  public <E extends Enum<E> & StringRepresentable> void applyEnum(String key, Class<E> enumClass, Consumer<E> consumer) {
    consumer.accept(getEnum(key, enumClass, enumClass.getEnumConstants()[0]));
  }

  public ItemStack getItemStack(String key) {
    return nbt.contains(key) ? ItemStack.of(nbt.getCompound(key)) : ItemStack.EMPTY;
  }

  public void applyItemStack(String key, Consumer<ItemStack> consumer) {
    consumer.accept(getItemStack(key));
  }

  public FluidStack getFluidStack(String key) {
    return nbt.contains(key) ? FluidStack.loadFluidStackFromNBT(nbt.getCompound(key)) : FluidStack.EMPTY;
  }

  public void applyFluidStack(String key, Consumer<FluidStack> consumer) {
    consumer.accept(getFluidStack(key));
  }

  public ListTag getList(String key, int type) {
    return nbt.getList(key, type);
  }

  public void getSerializable(String key, INBTSerializable target) {
    if (nbt.contains(key)) {
      target.deserializeNBT(nbt.getCompound(key));
    }
  }

  public <T> void readCollection(String key, Collection<T> collection, Function<NbtReader, T> elementReader) {
    ListTag list = nbt.getList(key, Tag.TAG_COMPOUND);
    for (int i = 0; i < list.size(); i++) {
      collection.add(elementReader.apply(of(list.getCompound(i))));
    }
  }

  public NbtReader getCompoundReader(String key) {
    return of(getCompound(key));
  }

  public boolean contains(String key) {
    return nbt.contains(key);
  }

  public boolean isEmpty() {
    return nbt.isEmpty();
  }

  public CompoundTag getTag() {
    return nbt;
  }

  public NbtReader copy() {
    return NbtReader.of(nbt.copy());
  }
}
