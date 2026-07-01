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
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;

/**
 * A fluent builder for creating and populating NBT {@link CompoundTag} objects.
 * <p>
 * This class provides a convenient chain-based API for writing various data types
 * to NBT tags, supporting primitives, collections, Minecraft objects, and custom serializable objects.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * CompoundTag tag = NbtWriter.create()
 *     .putInt("count", 5)
 *     .putString("name", "example")
 *     .putBlockPos("position", new BlockPos(0, 64, 0))
 *     .build();
 * }</pre>
 * </p>
 */
public class NbtWriter {
  private final CompoundTag tag;

  private NbtWriter(CompoundTag tag) {
    this.tag = tag;
  }

  /**
   * Creates a new NbtWriter with an empty CompoundTag.
   *
   * @return A new NbtWriter instance.
   */
  public static NbtWriter create() {
    return new NbtWriter(new CompoundTag());
  }

  /**
   * Creates a new NbtWriter wrapping an existing CompoundTag.
   *
   * @param tag The CompoundTag to wrap.
   * @return A new NbtWriter instance.
   */
  public static NbtWriter of(CompoundTag tag) {
    return new NbtWriter(tag);
  }

  /**
   * Puts an integer value into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The integer value to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putInt(String key, int value) {
    tag.putInt(key, value);
    return this;
  }

  /**
   * Puts an integer value from a supplier into the tag.
   *
   * @param key      The key to store the value under.
   * @param supplier The supplier providing the integer value.
   * @return This writer for method chaining.
   */
  public NbtWriter putInt(String key, IntSupplier supplier) {
    tag.putInt(key, supplier.getAsInt());
    return this;
  }

  /**
   * Puts a long value into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The long value to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putLong(String key, long value) {
    tag.putLong(key, value);
    return this;
  }

  /**
   * Puts a long value from a supplier into the tag.
   *
   * @param key      The key to store the value under.
   * @param supplier The supplier providing the long value.
   * @return This writer for method chaining.
   */
  public NbtWriter putLong(String key, LongSupplier supplier) {
    tag.putLong(key, supplier.getAsLong());
    return this;
  }

  /**
   * Puts a float value into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The float value to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putFloat(String key, float value) {
    tag.putFloat(key, value);
    return this;
  }

  /**
   * Puts a float value from a supplier into the tag.
   *
   * @param key      The key to store the value under.
   * @param supplier The supplier providing the float value.
   * @return This writer for method chaining.
   */
  public NbtWriter putFloat(String key, FloatSupplier supplier) {
    tag.putFloat(key, supplier.getAsFloat());
    return this;
  }

  /**
   * Puts a double value into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The double value to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putDouble(String key, double value) {
    tag.putDouble(key, value);
    return this;
  }

  /**
   * Puts a double value from a supplier into the tag.
   *
   * @param key      The key to store the value under.
   * @param supplier The supplier providing the double value.
   * @return This writer for method chaining.
   */
  public NbtWriter putDouble(String key, DoubleSupplier supplier) {
    tag.putDouble(key, supplier.getAsDouble());
    return this;
  }

  /**
   * Puts a byte value into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The byte value to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putByte(String key, byte value) {
    tag.putByte(key, value);
    return this;
  }

  /**
   * Puts a short value into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The short value to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putShort(String key, short value) {
    tag.putShort(key, value);
    return this;
  }

  /**
   * Puts a boolean value into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The boolean value to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putBoolean(String key, boolean value) {
    tag.putBoolean(key, value);
    return this;
  }

  /**
   * Puts a boolean value from a supplier into the tag.
   *
   * @param key      The key to store the value under.
   * @param supplier The supplier providing the boolean value.
   * @return This writer for method chaining.
   */
  public NbtWriter putBoolean(String key, BooleanSupplier supplier) {
    tag.putBoolean(key, supplier.getAsBoolean());
    return this;
  }

  /**
   * Puts a string value into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The string value to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putString(String key, String value) {
    tag.putString(key, value);
    return this;
  }

  /**
   * Puts a string value from a supplier into the tag.
   *
   * @param key      The key to store the value under.
   * @param supplier The supplier providing the string value.
   * @return This writer for method chaining.
   */
  public NbtWriter putString(String key, Supplier<String> supplier) {
    tag.putString(key, supplier.get());
    return this;
  }

  /**
   * Puts a UUID value into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The UUID value to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putUUID(String key, UUID value) {
    tag.putUUID(key, value);
    return this;
  }

  /**
   * Puts a UUID value from a supplier into the tag.
   *
   * @param key      The key to store the value under.
   * @param supplier The supplier providing the UUID value.
   * @return This writer for method chaining.
   */
  public NbtWriter putUUID(String key, Supplier<UUID> supplier) {
    tag.putUUID(key, supplier.get());
    return this;
  }

  /**
   * Puts a byte array into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The byte array to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putByteArray(String key, byte[] value) {
    tag.putByteArray(key, value);
    return this;
  }

  /**
   * Puts a byte collection into the tag as a byte array.
   *
   * @param key   The key to store the value under.
   * @param value The byte collection to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putByteArray(String key, ByteCollection value) {
    tag.putByteArray(key, value.toByteArray());
    return this;
  }

  /**
   * Puts a list of bytes into the tag as a byte array.
   *
   * @param key   The key to store the value under.
   * @param value The list of bytes to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putByteArray(String key, List<Byte> value) {
    tag.putByteArray(key, value);
    return this;
  }

  /**
   * Puts a byte array from a supplier into the tag.
   *
   * @param key      The key to store the value under.
   * @param supplier The supplier providing the byte array.
   * @return This writer for method chaining.
   */
  public NbtWriter putByteArray(String key, Supplier<byte[]> supplier) {
    tag.putByteArray(key, supplier.get());
    return this;
  }

  /**
   * Puts an integer array into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The integer array to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putIntArray(String key, int[] value) {
    tag.putIntArray(key, value);
    return this;
  }

  /**
   * Puts an integer collection into the tag as an integer array.
   *
   * @param key   The key to store the value under.
   * @param value The integer collection to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putIntArray(String key, IntCollection value) {
    tag.putIntArray(key, value.toIntArray());
    return this;
  }

  /**
   * Puts a list of integers into the tag as an integer array.
   *
   * @param key   The key to store the value under.
   * @param value The list of integers to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putIntArray(String key, List<Integer> value) {
    tag.putIntArray(key, value);
    return this;
  }

  /**
   * Puts an integer array from a supplier into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The supplier providing the integer array.
   * @return This writer for method chaining.
   */
  public NbtWriter putIntArray(String key, Supplier<int[]> value) {
    tag.putIntArray(key, value.get());
    return this;
  }

  /**
   * Puts a long array into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The long array to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putLongArray(String key, long[] value) {
    tag.putLongArray(key, value);
    return this;
  }

  /**
   * Puts a long collection into the tag as a long array.
   *
   * @param key   The key to store the value under.
   * @param value The long collection to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putLongArray(String key, LongCollection value) {
    tag.putLongArray(key, value.toLongArray());
    return this;
  }

  /**
   * Puts a list of longs into the tag as a long array.
   *
   * @param key   The key to store the value under.
   * @param value The list of longs to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putLongArray(String key, List<Long> value) {
    tag.putLongArray(key, value);
    return this;
  }

  /**
   * Puts a long array from a supplier into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The supplier providing the long array.
   * @return This writer for method chaining.
   */
  public NbtWriter putLongArray(String key, Supplier<long[]> value) {
    tag.putLongArray(key, value.get());
    return this;
  }

  /**
   * Puts a Direction into the tag.
   *
   * @param key       The key to store the value under.
   * @param direction The Direction to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putDirection(String key, Direction direction) {
    return putEnum(key, direction);
  }

  /**
   * Puts a BlockPos into the tag as a long.
   *
   * @param key The key to store the value under.
   * @param pos The BlockPos to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putBlockPos(String key, BlockPos pos) {
    tag.putLong(key, pos.asLong());
    return this;
  }

  /**
   * Puts a BlockState into the tag by storing its registry name.
   *
   * @param key   The key to store the value under.
   * @param state The BlockState to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putBlockState(String key, BlockState state) {
    tag.putString(key, Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(state.getBlock())).toString());
    return this;
  }

  /**
   * Puts a ResourceLocation into the tag as a string.
   *
   * @param key   The key to store the value under.
   * @param value The ResourceLocation to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putResourceLocation(String key, ResourceLocation value) {
    tag.putString(key, value.toString());
    return this;
  }

  /**
   * Puts an enum value into the tag using its serialized name.
   *
   * @param key   The key to store the value under.
   * @param value The enum value to store.
   * @param <E>   The enum type that implements StringRepresentable.
   * @return This writer for method chaining.
   */
  public <E extends Enum<E> & StringRepresentable> NbtWriter putEnum(String key, E value) {
    tag.putString(key, value.getSerializedName());
    return this;
  }

  /**
   * Puts a StringRepresentable value into the tag using its serialized name.
   *
   * @param key   The key to store the value under.
   * @param value The StringRepresentable value to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putStringRepresentable(String key, StringRepresentable value) {
    tag.putString(key, value.getSerializedName());
    return this;
  }

  /**
   * Puts an ItemStack into the tag, if it's not empty.
   *
   * @param key   The key to store the value under.
   * @param value The ItemStack to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putItemStack(String key, ItemStack value) {
    if (!value.isEmpty()) {
      tag.put(key, value.save(new CompoundTag()));
    }
    return this;
  }

  /**
   * Puts an ItemStack from a supplier into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The supplier providing the ItemStack.
   * @return This writer for method chaining.
   */
  public NbtWriter putItemStack(String key, Supplier<ItemStack> value) {
    tag.put(key, value.get().save(new CompoundTag()));
    return this;
  }

  /**
   * Puts a FluidStack into the tag, if it's not empty.
   *
   * @param key   The key to store the value under.
   * @param value The FluidStack to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putFluidStack(String key, FluidStack value) {
    if (!value.isEmpty()) {
      tag.put(key, value.writeToNBT(new CompoundTag()));
    }
    return this;
  }

  /**
   * Puts a FluidStack from a supplier into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The supplier providing the FluidStack.
   * @return This writer for method chaining.
   */
  public NbtWriter putFluidStack(String key, Supplier<FluidStack> value) {
    tag.put(key, value.get().writeToNBT(new CompoundTag()));
    return this;
  }

  /**
   * Puts a CompoundTag into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The CompoundTag to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putTag(String key, CompoundTag value) {
    tag.put(key, value);
    return this;
  }

  /**
   * Puts a ListTag into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The ListTag to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putList(String key, ListTag value) {
    tag.put(key, value);
    return this;
  }

  public NbtWriter putList(String key, Consumer<ListTag> value) {
    value.accept(new ListTag());
    return this;
  }

  /**
   * Puts an INBTSerializable object into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The INBTSerializable object to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putSerializable(String key, INBTSerializable value) {
    tag.put(key, value.serializeNBT());
    return this;
  }

  /**
   * Puts a collection into the tag as a ListTag, with each element serialized using the provided function.
   *
   * @param key               The key to store the value under.
   * @param collection        The collection to store.
   * @param elementSerializer The function that serializes each element to a Tag.
   * @param <T>               The type of elements in the collection.
   * @return This writer for method chaining.
   */
  public <T> NbtWriter putCollection(String key, Collection<T> collection, Function<T, Tag> elementSerializer) {
    ListTag list = new ListTag();
    for (T element : collection) {
      list.add(elementSerializer.apply(element));
    }
    tag.put(key, list);
    return this;
  }

  /**
   * Puts a collection of strings into the tag as a ListTag of StringTags.
   *
   * @param key     The key to store the value under.
   * @param strings The collection of strings to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putStrings(String key, Collection<String> strings) {
    return putCollection(key, strings, StringTag::valueOf);
  }

  /**
   * Puts an array of strings into the tag as a ListTag of StringTags.
   *
   * @param key     The key to store the value under.
   * @param strings The array of strings to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putStrings(String key, String[] strings) {
    return putStrings(key, Arrays.asList(strings));
  }

  /**
   * Puts a collection into the tag as a ListTag, with each element serialized using the provided writer.
   *
   * @param key           The key to store the value under.
   * @param collection    The collection to store.
   * @param elementWriter The consumer that writes each element.
   * @param <T>           The type of elements in the collection.
   * @return This writer for method chaining.
   */
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

  /**
   * Conditionally puts a value into the tag if the condition is met.
   *
   * @param key       The key to store the value under.
   * @param value     The value to potentially store.
   * @param condition The predicate to test the condition.
   * @param writer    The consumer that writes the value if the condition is true.
   * @param <T>       The type of the value.
   * @return This writer for method chaining.
   */
  public <T> NbtWriter putIf(String key, T value, Predicate<T> condition, BiConsumer<NbtWriter, T> writer) {
    if (condition.test(value)) {
      writer.accept(this, value);
    }
    return this;
  }

  public <T extends INBTSerializable> NbtWriter putIf(String key, T value, boolean condition) {
    if (condition) {
      putSerializable(key, value);
    }
    return this;
  }

  public NbtWriter rawTagAction(Consumer<CompoundTag> action) {
    action.accept(tag);
    return this;
  }

  /**
   * Puts a value into the tag if it is not null.
   *
   * @param key    The key to store the value under.
   * @param value  The value to store (can be null).
   * @param writer The consumer that writes the value if it's not null.
   * @param <T>    The type of the value.
   * @return This writer for method chaining.
   */
  public <T> NbtWriter putNullable(String key, @Nullable T value, BiConsumer<NbtWriter, T> writer) {
    if (value != null) {
      writer.accept(this, value);
    }
    return this;
  }

  /**
   * Puts a value into the tag if the Optional is present.
   *
   * @param key    The key to store the value under.
   * @param value  The Optional containing the potential value.
   * @param writer The consumer that writes the value if present.
   * @param <T>    The type of the value.
   * @return This writer for method chaining.
   */
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public <T> NbtWriter putIfPresent(String key, Optional<T> value, BiConsumer<NbtWriter, T> writer) {
    value.ifPresent(v -> writer.accept(this, v));
    return this;
  }

  /**
   * Puts a map into the tag as a ListTag of CompoundTags, each containing a key and a value.
   *
   * @param key         The key to store the map under.
   * @param map         The map to store.
   * @param keyWriter   The consumer that writes each key.
   * @param valueWriter The consumer that writes each value.
   * @param <K>         The type of keys in the map.
   * @param <V>         The type of values in the map.
   * @return This writer for method chaining.
   */
  public <K, V> NbtWriter putMap(String key, Map<K, V> map, BiConsumer<NbtWriter, K> keyWriter, BiConsumer<NbtWriter, V> valueWriter) {
    ListTag list = new ListTag();
    map.forEach((k, v) -> {
      NbtWriter entryWriter = create();
      keyWriter.accept(entryWriter, k);
      valueWriter.accept(entryWriter, v);
      list.add(entryWriter.build());
    });
    tag.put(key, list);
    return this;
  }

  /**
   * Puts another NbtWriter's tag into this tag.
   *
   * @param key   The key to store the value under.
   * @param value The NbtWriter whose tag to store.
   * @return This writer for method chaining.
   */
  public NbtWriter putWriter(String key, NbtWriter value) {
    tag.put(key, value.build());
    return this;
  }

  /**
   * Creates a nested NbtWriter and writes it into this tag using the provided consumer.
   *
   * @param key   The key to store the value under.
   * @param value The consumer that configures the nested writer.
   * @return This writer for method chaining.
   */
  public NbtWriter withWriter(String key, Consumer<NbtWriter> value) {
    var writer = create();
    value.accept(writer);
    return putWriter(key, writer);
  }

  /**
   * Puts a generic Tag into the tag.
   *
   * @param key   The key to store the value under.
   * @param value The Tag to store.
   * @return This writer for method chaining.
   */
  public NbtWriter put(String key, Tag value) {
    tag.put(key, value);
    return this;
  }

  /**
   * Runs the provided consumer if the key is not present in the tag.
   *
   * @param key    The key to check.
   * @param action The action to run if the key is absent.
   * @return This writer for method chaining.
   */
  public NbtWriter ifAbsent(String key, Consumer<NbtWriter> action) {
    if (!tag.contains(key)) {
      action.accept(this);
    }
    return this;
  }

  /**
   * Builds and returns the CompoundTag.
   *
   * @return The built CompoundTag.
   */
  public CompoundTag build() {
    return tag;
  }

  /**
   * Gets the underlying CompoundTag.
   *
   * @return The CompoundTag being built.
   */
  public CompoundTag getTag() {
    return tag;
  }

  /**
   * Terminal operation that does nothing but consumes the builder chain.
   * <p>
   * This method is intended to be called at the end of a builder chain to suppress
   * "method return value not used" warnings when the final {@link CompoundTag} is
   * not needed to be stored or returned.
   * </p>
   * <p>
   * Example usage:
   * <pre>{@code
   * NbtWriter.create()
   *     .putInt("count", 5)
   *     .putString("name", "example")
   *     .done(); // Suppress warning
   * }</pre>
   * </p>
   */
  public void done() {
    // No-op - intentionally empty to suppress unused return value warnings
  }
}
