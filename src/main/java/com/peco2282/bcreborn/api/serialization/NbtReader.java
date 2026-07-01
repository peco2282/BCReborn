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
package com.peco2282.bcreborn.api.serialization;

import com.peco2282.bcreborn.api.core.INBTSerializable;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.*;


/**
 * A utility class for reading NBT (Named Binary Tag) data from CompoundTag instances.
 * This class provides type-safe methods for extracting various data types from NBT,
 * including primitives, arrays, Minecraft types, and custom serializable objects.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * CompoundTag nbt = ...;
 * NbtReader reader = NbtReader.of(nbt);
 * int value = reader.getInt("key");
 * BlockPos pos = reader.getBlockPos("position");
 * }</pre>
 *
 * @see NbtWriter
 * @see CompoundTag
 */
public class NbtReader {
  private final CompoundTag nbt;

  /**
   * Private constructor to enforce factory method usage.
   *
   * @param nbt the CompoundTag to wrap and read from
   */
  private NbtReader(CompoundTag nbt) {
    this.nbt = nbt;
  }

  /**
   * Creates a new NbtReader instance wrapping the given CompoundTag.
   *
   * @param nbt the CompoundTag to read from
   * @return a new NbtReader instance
   */
  public static NbtReader of(CompoundTag nbt) {
    return new NbtReader(nbt);
  }

  /**
   * Reads an integer value from the NBT data.
   *
   * @param key the key to read from
   * @return the integer value, or 0 if the key doesn't exist
   */
  public int getInt(String key) {
    return nbt.getInt(key);
  }

  /**
   * Reads an integer value from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist
   * @return the integer value, or defaultValue if the key doesn't exist
   */
  public int getIntOrDefault(String key, int defaultValue) {
    return nbt.contains(key) ? nbt.getInt(key) : defaultValue;
  }

  /**
   * Reads an integer value and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the integer value
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyInt(String key, IntConsumer consumer) {
    consumer.accept(nbt.getInt(key));
    return this;
  }

  /**
   * Reads a long value from the NBT data.
   *
   * @param key the key to read from
   * @return the long value, or 0L if the key doesn't exist
   */
  public long getLong(String key) {
    return nbt.getLong(key);
  }

  /**
   * Reads a long value from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist
   * @return the long value, or defaultValue if the key doesn't exist
   */
  public long getLongOrDefault(String key, long defaultValue) {
    return nbt.contains(key) ? nbt.getLong(key) : defaultValue;
  }

  /**
   * Reads a long value and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the long value
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyLong(String key, LongConsumer consumer) {
    consumer.accept(nbt.getLong(key));
    return this;
  }

  /**
   * Reads a float value from the NBT data.
   *
   * @param key the key to read from
   * @return the float value, or 0.0f if the key doesn't exist
   */
  public float getFloat(String key) {
    return nbt.getFloat(key);
  }

  /**
   * Reads a float value from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist
   * @return the float value, or defaultValue if the key doesn't exist
   */
  public float getFloatOrDefault(String key, float defaultValue) {
    return nbt.contains(key) ? nbt.getFloat(key) : defaultValue;
  }

  /**
   * Reads a float value and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the float value
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyFloat(String key, FloatConsumer consumer) {
    consumer.accept(nbt.getFloat(key));
    return this;
  }

  /**
   * Reads a double value from the NBT data.
   *
   * @param key the key to read from
   * @return the double value, or 0.0 if the key doesn't exist
   */
  public double getDouble(String key) {
    return nbt.getDouble(key);
  }

  /**
   * Reads a double value from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist
   * @return the double value, or defaultValue if the key doesn't exist
   */
  public double getDoubleOrDefault(String key, double defaultValue) {
    return nbt.contains(key) ? nbt.getDouble(key) : defaultValue;
  }

  /**
   * Reads a double value and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the double value
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyDouble(String key, DoubleConsumer consumer) {
    consumer.accept(nbt.getDouble(key));
    return this;
  }

  /**
   * Reads a byte value from the NBT data.
   *
   * @param key the key to read from
   * @return the byte value, or 0 if the key doesn't exist
   */
  public byte getByte(String key) {
    return nbt.getByte(key);
  }

  /**
   * Reads a byte value from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist
   * @return the byte value, or defaultValue if the key doesn't exist
   */
  public byte getByteOrDefault(String key, byte defaultValue) {
    return nbt.contains(key) ? nbt.getByte(key) : defaultValue;
  }

  /**
   * Reads a byte value and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the byte value
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyByte(String key, ByteConsumer consumer) {
    consumer.accept(nbt.getByte(key));
    return this;
  }

  /**
   * Reads a short value from the NBT data.
   *
   * @param key the key to read from
   * @return the short value, or 0 if the key doesn't exist
   */
  public short getShort(String key) {
    return nbt.getShort(key);
  }

  /**
   * Reads a short value from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist
   * @return the short value, or defaultValue if the key doesn't exist
   */
  public short getShortOrDefault(String key, short defaultValue) {
    return nbt.contains(key) ? nbt.getShort(key) : defaultValue;
  }

  /**
   * Reads a short value and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the short value
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyShort(String key, ShortConsumer consumer) {
    consumer.accept(nbt.getShort(key));
    return this;
  }

  /**
   * Reads a boolean value from the NBT data.
   *
   * @param key the key to read from
   * @return the boolean value, or false if the key doesn't exist
   */
  public boolean getBoolean(String key) {
    return nbt.getBoolean(key);
  }

  /**
   * Reads a boolean value from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist
   * @return the boolean value, or defaultValue if the key doesn't exist
   */
  public boolean getBooleanOrDefault(String key, boolean defaultValue) {
    return nbt.contains(key) ? nbt.getBoolean(key) : defaultValue;
  }

  /**
   * Reads a boolean value and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the boolean value
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyBoolean(String key, BooleanConsumer consumer) {
    consumer.accept(nbt.getBoolean(key));
    return this;
  }

  /**
   * Reads a string value from the NBT data.
   *
   * @param key the key to read from
   * @return the string value, or an empty string if the key doesn't exist
   */
  public String getString(String key) {
    return nbt.getString(key);
  }

  /**
   * Reads a string value from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist
   * @return the string value, or defaultValue if the key doesn't exist
   */
  public String getStringOrDefault(String key, String defaultValue) {
    return nbt.contains(key) ? nbt.getString(key) : defaultValue;
  }

  /**
   * Reads a string value and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the string value
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyString(String key, Consumer<String> consumer) {
    consumer.accept(nbt.getString(key));
    return this;
  }

  /**
   * Reads a UUID value from the NBT data.
   * Returns null if the key doesn't exist or doesn't contain a valid UUID.
   *
   * @param key the key to read from
   * @return the UUID value, or null if the key doesn't exist or is invalid
   */
  @Nullable
  public UUID getUUID(String key) {
    return nbt.hasUUID(key) ? nbt.getUUID(key) : null;
  }

  /**
   * Reads a UUID value and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the UUID value
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyUUID(String key, Consumer<UUID> consumer) {
    consumer.accept(nbt.getUUID(key));
    return this;
  }

  /**
   * Reads a byte array from the NBT data.
   *
   * @param key the key to read from
   * @return the byte array, or an empty array if the key doesn't exist
   */
  public byte[] getByteArray(String key) {
    return nbt.getByteArray(key);
  }

  /**
   * Reads a byte array and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the byte array
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyByteArray(String key, Consumer<byte[]> consumer) {
    consumer.accept(nbt.getByteArray(key));
    return this;
  }

  /**
   * Reads an int array from the NBT data.
   *
   * @param key the key to read from
   * @return the int array, or an empty array if the key doesn't exist
   */
  public int[] getIntArray(String key) {
    return nbt.getIntArray(key);
  }

  /**
   * Reads an int array and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the int array
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyIntArray(String key, Consumer<int[]> consumer) {
    consumer.accept(nbt.getIntArray(key));
    return this;
  }

  /**
   * Reads a long array from the NBT data.
   *
   * @param key the key to read from
   * @return the long array, or an empty array if the key doesn't exist
   */
  public long[] getLongArray(String key) {
    return nbt.getLongArray(key);
  }

  /**
   * Reads a long array and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the long array
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyLongArray(String key, Consumer<long[]> consumer) {
    consumer.accept(nbt.getLongArray(key));
    return this;
  }

  /**
   * Reads a BlockPos from the NBT data stored as a long value.
   *
   * @param key the key to read from
   * @return the BlockPos decoded from the long value
   */
  public BlockPos getBlockPos(String key) {
    return BlockPos.of(nbt.getLong(key));
  }

  /**
   * Reads a BlockPos from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist
   * @return the BlockPos, or defaultValue if the key doesn't exist
   */
  public BlockPos getBlockPosOrDefault(String key, BlockPos defaultValue) {
    return nbt.contains(key) ? BlockPos.of(nbt.getLong(key)) : defaultValue;
  }

  /**
   * Reads a BlockPos and passes it to the given consumer if the key exists.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the BlockPos
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyBlockPos(String key, Consumer<BlockPos> consumer) {
    if (nbt.contains(key)) {
      consumer.accept(getBlockPos(key));
    }
    return this;
  }

  /**
   * Reads a BlockState from the NBT data by parsing the stored ResourceLocation.
   * Returns the default block state of the found block.
   *
   * @param key the key to read from
   * @return the BlockState, or null if the key doesn't exist or the block is not found
   */
  @Nullable
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

  /**
   * Reads a BlockState from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist or the block is not found
   * @return the BlockState, or defaultValue if not found
   */
  public BlockState getBlockStateOrDefault(String key, BlockState defaultValue) {
    BlockState state = getBlockState(key);
    return state != null ? state : defaultValue;
  }

  /**
   * Reads a BlockState and passes it to the given consumer if found.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the BlockState
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyBlockState(String key, Consumer<@NotNull BlockState> consumer) {
    BlockState state = getBlockState(key);
    if (state != null) {
      consumer.accept(state);
    }
    return this;
  }

  /**
   * Reads a CompoundTag from the NBT data.
   *
   * @param key the key to read from
   * @return the CompoundTag, or a new empty CompoundTag if the key doesn't exist
   */
  public CompoundTag getCompound(String key) {
    return getCompoundOrDefault(key, new CompoundTag());
  }

  /**
   * Reads a CompoundTag from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if the key doesn't exist
   * @return the CompoundTag, or defaultValue if the key doesn't exist
   */
  public CompoundTag getCompoundOrDefault(String key, CompoundTag defaultValue) {
    return nbt.contains(key) ? nbt.getCompound(key) : defaultValue;
  }

  /**
   * Reads a CompoundTag and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the CompoundTag
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyCompound(String key, Consumer<CompoundTag> consumer) {
    consumer.accept(nbt.getCompound(key));
    return this;
  }

  /**
   * Reads a ResourceLocation from the NBT data by parsing the stored string.
   * Uses safe parsing that returns null on invalid input.
   *
   * @param key the key to read from
   * @return the ResourceLocation, or null if parsing fails or the key doesn't exist
   */
  @Nullable
  public ResourceLocation getResource(String key) {
    return ResourceLocation.tryParse(getString(key));
  }

  /**
   * Reads a ResourceLocation from the NBT data with a fallback default.
   *
   * @param key          the key to read from
   * @param defaultValue the value to return if parsing fails or the key doesn't exist
   * @return the ResourceLocation, or defaultValue if parsing fails
   */
  public ResourceLocation getResourceOrDefault(String key, ResourceLocation defaultValue) {
    ResourceLocation res = ResourceLocation.tryParse(getString(key));
    return res != null ? res : defaultValue;
  }

  /**
   * Reads a ResourceLocation from the NBT data using strict parsing.
   * Throws an exception if the string is not a valid ResourceLocation.
   *
   * @param key the key to read from
   * @return the ResourceLocation
   * @throws ResourceLocationException if the string is not a valid ResourceLocation
   */
  public ResourceLocation getNotNullResource(String key) {
    return ResourceLocation.parse(getString(key));
  }

  /**
   * Reads a ResourceLocation using strict parsing and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the ResourceLocation
   * @return the NbtReader instance for method chaining
   * @throws ResourceLocationException if the string is not a valid ResourceLocation
   */
  public NbtReader applyResource(String key, Consumer<ResourceLocation> consumer) {
    consumer.accept(getNotNullResource(key));
    return this;
  }

  /**
   * Reads an enum value from the NBT data using its serialized name.
   * Falls back to the first enum constant if the key doesn't exist or no match is found.
   *
   * @param key       the key to read from
   * @param enumClass the enum class
   * @param <E>       the enum type that implements StringRepresentable
   * @return the enum value, or the first enum constant as fallback
   */
  public <E extends Enum<E> & StringRepresentable> E getEnum(String key, Class<E> enumClass) {
    return getEnum(key, enumClass, enumClass.getEnumConstants()[0]);
  }

  /**
   * Reads an enum value from the NBT data using its serialized name with a custom fallback.
   *
   * @param key       the key to read from
   * @param enumClass the enum class
   * @param fallback  the fallback value if the key doesn't exist or no match is found
   * @param <E>       the enum type that implements StringRepresentable
   * @return the enum value, or the fallback if not found
   */
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

  /**
   * Reads an enum value and passes it to the given consumer.
   * Uses the first enum constant as fallback.
   *
   * @param key       the key to read from
   * @param enumClass the enum class
   * @param consumer  the consumer to accept the enum value
   * @param <E>       the enum type that implements StringRepresentable
   * @return the NbtReader instance for method chaining
   */
  public <E extends Enum<E> & StringRepresentable> NbtReader applyEnum(String key, Class<E> enumClass, Consumer<E> consumer) {
    consumer.accept(getEnum(key, enumClass, enumClass.getEnumConstants()[0]));
    return this;
  }

  public Direction getDirection(String key, Direction fallback) {
    return getEnum(key, Direction.class, fallback);
  }

  @Nullable
  public Direction getDirection(String key) {
    //noinspection DataFlowIssue
    return getEnum(key, Direction.class, null);
  }

  public NbtReader applyDirection(String key, Consumer<@NotNull Direction> consumer) {
    var dir = getDirection(key);
    if (dir != null) {
      consumer.accept(dir);
    }
    return this;
  }

  public NbtReader applyDirection(String key, Consumer<@NotNull Direction> consumer, Direction fallback) {
    consumer.accept(getDirection(key, fallback));
    return this;
  }

  /**
   * Reads an ItemStack from the NBT data.
   *
   * @param key the key to read from
   * @return the ItemStack, or ItemStack.EMPTY if the key doesn't exist
   */
  public ItemStack getItemStack(String key) {
    return nbt.contains(key) ? ItemStack.of(nbt.getCompound(key)) : ItemStack.EMPTY;
  }

  /**
   * Reads an ItemStack and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the ItemStack
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyItemStack(String key, Consumer<ItemStack> consumer) {
    consumer.accept(getItemStack(key));
    return this;
  }

  /**
   * Reads a FluidStack from the NBT data.
   *
   * @param key the key to read from
   * @return the FluidStack, or FluidStack.EMPTY if the key doesn't exist
   */
  public FluidStack getFluidStack(String key) {
    return nbt.contains(key) ? FluidStack.loadFluidStackFromNBT(nbt.getCompound(key)) : FluidStack.EMPTY;
  }

  /**
   * Reads a FluidStack and passes it to the given consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the FluidStack
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyFluidStack(String key, Consumer<FluidStack> consumer) {
    consumer.accept(getFluidStack(key));
    return this;
  }

  /**
   * Reads a list of strings from the NBT data.
   *
   * @param key the key to read from
   * @return a list of strings, or an empty list if the key doesn't exist or is not a list of strings
   */
  public List<String> getStrings(String key) {
    ListTag list = nbt.getList(key, Tag.TAG_STRING);
    List<String> result = new ArrayList<>(list.size());
    for (int i = 0; i < list.size(); i++) {
      result.add(list.getString(i));
    }
    return result;
  }

  /**
   * Reads a list of strings from the NBT data and applies it to a consumer.
   *
   * @param key      the key to read from
   * @param consumer the consumer to apply the list to
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applyStrings(String key, Consumer<List<String>> consumer) {
    consumer.accept(getStrings(key));
    return this;
  }

  /**
   * Reads a nested CompoundTag and passes it to the given consumer as an NbtReader.
   *
   * @param key      the key to read from
   * @param consumer the consumer to accept the NbtReader for the nested tag
   * @return the NbtReader instance for method chaining
   */
  public NbtReader apply(String key, Consumer<NbtReader> consumer) {
    if (nbt.contains(key)) {
      consumer.accept(getCompoundReader(key));
    }
    return this;
  }

  /**
   * Reads a value from the tag if it exists.
   *
   * @param key          the key to read from
   * @param reader       a function that converts an NbtReader to an element of type T
   * @param defaultValue the value to return if the key doesn't exist
   * @param <T>          the type of the value
   * @return the read value or defaultValue
   */
  @Nullable
  public <T> T getNullable(String key, Function<NbtReader, @Nullable T> reader, @Nullable T defaultValue) {
    if (nbt.contains(key)) {
      return reader.apply(getCompoundReader(key));
    }
    return defaultValue;
  }

  /**
   * Reads a ListTag from the NBT data with a specific element type.
   *
   * @param key  the key to read from
   * @param type the NBT type ID of the list elements (e.g., Tag.TAG_COMPOUND)
   * @return the ListTag, or an empty ListTag if the key doesn't exist
   */
  public ListTag getList(String key, int type) {
    return nbt.getList(key, type);
  }

  /**
   * Reads NBT data into an INBTSerializable object by calling its deserializeNBT method.
   * Only performs deserialization if the key exists in the NBT data.
   *
   * @param key    the key to read from
   * @param target the target object to deserialize into
   * @return the NbtReader instance for method chaining
   */
  public NbtReader applySerializable(String key, INBTSerializable target) {
    if (nbt.contains(key)) {
      target.deserializeNBT(nbt.getCompound(key));
    }
    return this;
  }

  public NbtReader rawTagAction(Consumer<CompoundTag> action) {
    action.accept(nbt);
    return this;
  }

  /**
   * Reads a collection of objects from a ListTag in the NBT data.
   * Each element in the list is read as a CompoundTag and converted using the provided reader function.
   *
   * @param key           the key to read from
   * @param collection    the collection to populate with elements
   * @param elementReader a function that converts an NbtReader to an element of type T
   * @param <T>           the type of elements in the collection
   * @return the NbtReader instance for method chaining
   */
  public <T> NbtReader readCollection(String key, Collection<T> collection, Function<NbtReader, T> elementReader) {
    ListTag list = nbt.getList(key, Tag.TAG_COMPOUND);
    for (int i = 0; i < list.size(); i++) {
      collection.add(elementReader.apply(of(list.getCompound(i))));
    }
    return this;
  }

  /**
   * Creates a new NbtReader for a nested CompoundTag.
   *
   * @param key the key to read from
   * @return a new NbtReader wrapping the nested CompoundTag
   */
  public NbtReader getCompoundReader(String key) {
    return of(getCompound(key));
  }

  /**
   * Reads a map of objects from a ListTag in the NBT data.
   *
   * @param key         the key to read from
   * @param map         the map to populate
   * @param keyReader   a function that converts an NbtReader to a key of type K
   * @param valueReader a function that converts an NbtReader to a value of type V
   * @param <K>         the type of keys in the map
   * @param <V>         the type of values in the map
   * @return the NbtReader instance for method chaining
   */
  public <K, V> NbtReader readMap(String key, Map<K, V> map, Function<NbtReader, K> keyReader, Function<NbtReader, V> valueReader) {
    ListTag list = nbt.getList(key, Tag.TAG_COMPOUND);
    for (int i = 0; i < list.size(); i++) {
      NbtReader entryReader = of(list.getCompound(i));
      K k = keyReader.apply(entryReader);
      V v = valueReader.apply(entryReader);
      map.put(k, v);
    }
    return this;
  }

  /**
   * Checks if the NBT data contains a value for the given key.
   *
   * @param key the key to check
   * @return true if the key exists in the NBT data
   */
  public boolean contains(String key) {
    return nbt.contains(key);
  }

  /**
   * Checks if the underlying NBT data is empty.
   *
   * @return true if the NBT data contains no entries
   */
  public boolean isEmpty() {
    return nbt.isEmpty();
  }

  /**
   * Returns the underlying CompoundTag being read from.
   *
   * @return the wrapped CompoundTag
   */
  public CompoundTag getTag() {
    return nbt;
  }

  /**
   * Creates a deep copy of this NbtReader with a copied CompoundTag.
   *
   * @return a new NbtReader with a copy of the underlying NBT data
   */
  public NbtReader copy() {
    return NbtReader.of(nbt.copy());
  }

  /**
   * Marks the end of a method chain.
   * This method performs no operation and rarely needs to be called.
   * It can be used to improve the readability of method chains.
   */
  public void done() {
    // No-op
  }

  public NbtReader applyIf(String key, INBTSerializable serializable) {
    if (nbt.contains(key)) {
      serializable.deserializeNBT(nbt.getCompound(key));
    }
    return this;
  }
}
