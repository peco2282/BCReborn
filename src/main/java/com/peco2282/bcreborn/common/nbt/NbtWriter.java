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
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.*;

public class NbtWriter {
  private final CompoundTag tag;

  private NbtWriter(CompoundTag tag) {
    this.tag = tag;
  }

  public static NbtWriter create() {
    return new NbtWriter(new CompoundTag());
  }

  public static NbtWriter of(CompoundTag tag) {
    return new NbtWriter(tag);
  }

  public NbtWriter putInt(String key, int value) {
    tag.putInt(key, value);
    return this;
  }

  public NbtWriter putInt(String key, IntSupplier supplier) {
    tag.putInt(key, supplier.getAsInt());
    return this;
  }

  public NbtWriter putLong(String key, long value) {
    tag.putLong(key, value);
    return this;
  }

  public NbtWriter putLong(String key, LongSupplier supplier) {
    tag.putLong(key, supplier.getAsLong());
    return this;
  }

  public NbtWriter putFloat(String key, float value) {
    tag.putFloat(key, value);
    return this;
  }

  public NbtWriter putFloat(String key, DoubleSupplier supplier) {
    tag.putFloat(key, (float) supplier.getAsDouble());
    return this;
  }

  public NbtWriter putDouble(String key, double value) {
    tag.putDouble(key, value);
    return this;
  }

  public NbtWriter putDouble(String key, DoubleSupplier supplier) {
    tag.putDouble(key, supplier.getAsDouble());
    return this;
  }

  public NbtWriter putByte(String key, byte value) {
    tag.putByte(key, value);
    return this;
  }

  public NbtWriter putShort(String key, short value) {
    tag.putShort(key, value);
    return this;
  }

  public NbtWriter putBoolean(String key, boolean value) {
    tag.putBoolean(key, value);
    return this;
  }

  public NbtWriter putBoolean(String key, BooleanSupplier supplier) {
    tag.putBoolean(key, supplier.getAsBoolean());
    return this;
  }

  public NbtWriter putString(String key, String value) {
    tag.putString(key, value);
    return this;
  }

  public NbtWriter putString(String key, Supplier<String> supplier) {
    tag.putString(key, supplier.get());
    return this;
  }

  public NbtWriter putUUID(String key, UUID value) {
    tag.putUUID(key, value);
    return this;
  }

  public NbtWriter putUUID(String key, Supplier<UUID> supplier) {
    tag.putUUID(key, supplier.get());
    return this;
  }

  public NbtWriter putByteArray(String key, byte[] value) {
    tag.putByteArray(key, value);
    return this;
  }

  public NbtWriter putByteArray(String key, ByteCollection value) {
    tag.putByteArray(key, value.toByteArray());
    return this;
  }

  public NbtWriter putByteArray(String key, List<Byte> value) {
    tag.putByteArray(key, value);
    return this;
  }

  public NbtWriter putByteArray(String key, Supplier<byte[]> supplier) {
    tag.putByteArray(key, supplier.get());
    return this;
  }

  public NbtWriter putIntArray(String key, int[] value) {
    tag.putIntArray(key, value);
    return this;
  }

  public NbtWriter putIntArray(String key, IntCollection value) {
    tag.putIntArray(key, value.toIntArray());
    return this;
  }

  public NbtWriter putIntArray(String key, List<Integer> value) {
    tag.putIntArray(key, value);
    return this;
  }

  public NbtWriter putIntArray(String key, Supplier<int[]> value) {
    tag.putIntArray(key, value.get());
    return this;
  }

  public NbtWriter putLongArray(String key, long[] value) {
    tag.putLongArray(key, value);
    return this;
  }

  public NbtWriter putLongArray(String key, LongCollection value) {
    tag.putLongArray(key, value.toLongArray());
    return this;
  }

  public NbtWriter putLongArray(String key, List<Long> value) {
    tag.putLongArray(key, value);
    return this;
  }

  public NbtWriter putLongArray(String key, Supplier<long[]> value) {
    tag.putLongArray(key, value.get());
    return this;
  }

  public NbtWriter putDirection(String key, Direction direction) {
    return putEnum(key, direction);
  }

  public NbtWriter putBlockPos(String key, BlockPos pos) {
    tag.putLong(key, pos.asLong());
    return this;
  }

  public NbtWriter putBlockState(String key, BlockState state) {
    tag.putString(key, Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(state.getBlock())).toString());
    return this;
  }

  public NbtWriter putResourceLocation(String key, ResourceLocation value) {
    tag.putString(key, value.toString());
    return this;
  }

  public <E extends Enum<E> & StringRepresentable> NbtWriter putEnum(String key, E value) {
    tag.putString(key, value.getSerializedName());
    return this;
  }

  public NbtWriter putStringRepresentable(String key, StringRepresentable value) {
    tag.putString(key, value.getSerializedName());
    return this;
  }

  public NbtWriter putItemStack(String key, ItemStack value) {
    tag.put(key, value.save(new CompoundTag()));
    return this;
  }

  public NbtWriter putItemStack(String key, Supplier<ItemStack> value) {
    tag.put(key, value.get().save(new CompoundTag()));
    return this;
  }

  public NbtWriter putFluidStack(String key, FluidStack value) {
    tag.put(key, value.writeToNBT(new CompoundTag()));
    return this;
  }

  public NbtWriter putFluidStack(String key, Supplier<FluidStack> value) {
    tag.put(key, value.get().writeToNBT(new CompoundTag()));
    return this;
  }

  public NbtWriter putTag(String key, CompoundTag value) {
    tag.put(key, value);
    return this;
  }

  public NbtWriter putList(String key, ListTag value) {
    tag.put(key, value);
    return this;
  }

  public NbtWriter putSerializable(String key, INBTSerializable value) {
    tag.put(key, value.serializeNBT());
    return this;
  }

  public <T> NbtWriter putCollection(String key, Collection<T> collection, BiConsumer<NbtWriter, T> elementWriter) {
    ListTag list = new ListTag();
    for (T element : collection) {
      NbtWriter writer = create();
      elementWriter.accept(writer, element);
      list.add(writer.build());
    }
    tag.put(key, list);
    return this;
  }

  public <T> NbtWriter putIf(String key, T value, Predicate<T> condition, BiConsumer<NbtWriter, T> writer) {
    if (condition.test(value)) {
      writer.accept(this, value);
    }
    return this;
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public <T> NbtWriter putIfPresent(String key, Optional<T> value, BiConsumer<NbtWriter, T> writer) {
    value.ifPresent(v -> writer.accept(this, v));
    return this;
  }

  public NbtWriter putWriter(String key, NbtWriter value) {
    tag.put(key, value.build());
    return this;
  }

  public NbtWriter withWriter(String key, Consumer<NbtWriter> value) {
    var writer = create();
    value.accept(writer);
    return putWriter(key, writer);
  }

  public NbtWriter put(String key, Tag value) {
    tag.put(key, value);
    return this;
  }

  public CompoundTag build() {
    return tag;
  }

  public CompoundTag getTag() {
    return tag;
  }
}
