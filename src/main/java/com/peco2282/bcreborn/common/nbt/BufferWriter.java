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

import com.peco2282.bcreborn.api.core.IBufferSerializable;
import com.peco2282.bcreborn.api.core.INBTSerializable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.*;

/**
 * A fluent utility class for writing data to a {@link FriendlyByteBuf}.
 * Provides a chainable API for serializing various data types to network buffers,
 * similar to {@link NbtWriter} for NBT serialization.
 *
 * <p>This class supports writing primitives, Minecraft types (BlockPos, ItemStack, etc.),
 * collections, maps, and custom serializable objects. All write methods return the
 * BufferWriter instance to allow method chaining.
 *
 * <p>Example usage:
 * <pre>{@code
 * BufferWriter.of(buffer)
 *     .writeInt(42)
 *     .writeString("example")
 *     .writeBoolean(true)
 *     .done();
 * }</pre>
 *
 * @see IBufferSerializable
 * @see FriendlyByteBuf
 */
public class BufferWriter {
  private final FriendlyByteBuf buf;

  private BufferWriter(FriendlyByteBuf buf) {
    this.buf = buf;
  }

  /**
   * Creates a new BufferWriter instance wrapping the given buffer.
   *
   * @param buf The buffer to write to.
   * @return A new BufferWriter instance.
   */
  public static BufferWriter of(FriendlyByteBuf buf) {
    return new BufferWriter(buf);
  }

  /**
   * Writes an integer value to the buffer.
   *
   * @param value The integer value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeInt(int value) {
    buf.writeInt(value);
    return this;
  }

  /**
   * Writes an integer value from a supplier to the buffer.
   *
   * @param supplier The supplier providing the integer value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeInt(IntSupplier supplier) {
    buf.writeInt(supplier.getAsInt());
    return this;
  }

  /**
   * Writes a long value to the buffer.
   *
   * @param value The long value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeLong(long value) {
    buf.writeLong(value);
    return this;
  }

  /**
   * Writes a long value from a supplier to the buffer.
   *
   * @param supplier The supplier providing the long value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeLong(LongSupplier supplier) {
    buf.writeLong(supplier.getAsLong());
    return this;
  }

  /**
   * Writes a string value to the buffer using UTF-8 encoding.
   *
   * @param value The string value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeString(String value) {
    buf.writeUtf(value);
    return this;
  }

  /**
   * Writes a string value from a supplier to the buffer using UTF-8 encoding.
   *
   * @param supplier The supplier providing the string value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeString(Supplier<String> supplier) {
    buf.writeUtf(supplier.get());
    return this;
  }

  /**
   * Writes a boolean value to the buffer.
   *
   * @param value The boolean value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeBoolean(boolean value) {
    buf.writeBoolean(value);
    return this;
  }

  /**
   * Writes a boolean value from a supplier to the buffer.
   *
   * @param supplier The supplier providing the boolean value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeBoolean(BooleanSupplier supplier) {
    buf.writeBoolean(supplier.getAsBoolean());
    return this;
  }

  /**
   * Writes a byte value to the buffer.
   *
   * @param value The byte value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeByte(byte value) {
    buf.writeByte(value);
    return this;
  }

  /**
   * Writes a short value to the buffer.
   *
   * @param value The short value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeShort(short value) {
    buf.writeShort(value);
    return this;
  }

  /**
   * Writes a float value to the buffer.
   *
   * @param value The float value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeFloat(float value) {
    buf.writeFloat(value);
    return this;
  }

  /**
   * Writes a float value from a supplier to the buffer.
   *
   * @param supplier The supplier providing the float value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeFloat(FloatSupplier supplier) {
    buf.writeFloat(supplier.getAsFloat());
    return this;
  }

  /**
   * Writes a double value to the buffer.
   *
   * @param value The double value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeDouble(double value) {
    buf.writeDouble(value);
    return this;
  }

  /**
   * Writes a double value from a supplier to the buffer.
   *
   * @param supplier The supplier providing the double value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeDouble(DoubleSupplier supplier) {
    buf.writeDouble(supplier.getAsDouble());
    return this;
  }

  /**
   * Writes a byte array to the buffer, prefixed with its length as a VarInt.
   *
   * @param value The byte array to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeByteArray(byte[] value) {
    buf.writeByteArray(value);
    return this;
  }

  /**
   * Writes an NBT compound tag to the buffer.
   *
   * @param tag The NBT tag to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeTag(CompoundTag tag) {
    buf.writeNbt(tag);
    return this;
  }

  /**
   * Writes a UUID to the buffer.
   *
   * @param value The UUID to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeUUID(UUID value) {
    buf.writeUUID(value);
    return this;
  }

  /**
   * Writes a block position to the buffer.
   *
   * @param value The BlockPos to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeBlockPos(BlockPos value) {
    buf.writeBlockPos(value);
    return this;
  }

  /**
   * Writes a direction enum to the buffer.
   *
   * @param value The Direction to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeDirection(Direction value) {
    buf.writeEnum(value);
    return this;
  }

  /**
   * Writes a block state to the buffer by writing its block's resource location.
   *
   * @param state The BlockState to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeBlockState(BlockState state) {
    var loc = ForgeRegistries.BLOCKS.getKey(state.getBlock());
    if (loc != null) {
      buf.writeResourceLocation(loc);
    }
    return this;
  }

  /**
   * Writes a resource location to the buffer.
   *
   * @param value The ResourceLocation to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeResourceLocation(ResourceLocation value) {
    buf.writeResourceLocation(value);
    return this;
  }

  /**
   * Writes an item stack to the buffer.
   *
   * @param value The ItemStack to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeItemStack(ItemStack value) {
    buf.writeItem(value);
    return this;
  }

  /**
   * Writes a fluid stack to the buffer.
   *
   * @param value The FluidStack to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeFluidStack(FluidStack value) {
    buf.writeFluidStack(value);
    return this;
  }

  /**
   * Writes a custom serializable object to the buffer by calling its writeData method.
   *
   * @param value The IBufferSerializable object to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeSerializable(IBufferSerializable value) {
    value.writeData(buf);
    return this;
  }


  /**
   * Writes an NBT-serializable object to the buffer by serializing it to NBT
   * and writing the resulting CompoundTag.
   *
   * @param serializable The INBTSerializable object to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeNbt(INBTSerializable serializable) {
    buf.writeNbt(serializable.serializeNBT());
    return this;
  }

  /**
   * Writes an enum value to the buffer using its ordinal.
   *
   * @param value The enum value to write.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeEnum(Enum<?> value) {
    buf.writeEnum(value);
    return this;
  }

  /**
   * Conditionally writes data to the buffer. Writes the condition as a boolean,
   * then executes the action if the condition is true.
   *
   * @param condition The condition to evaluate.
   * @param action    The action to perform if the condition is true.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter writeIf(boolean condition, Consumer<BufferWriter> action) {
    buf.writeBoolean(condition);
    if (condition) {
      action.accept(this);
    }
    return this;
  }

  /**
   * Conditionally writes a value to the buffer based on a predicate. Writes whether
   * the condition matches as a boolean, then writes the value using the provided
   * writer if the condition is satisfied.
   *
   * @param <T>       The type of the value to write.
   * @param value     The value to test and potentially write.
   * @param condition The predicate to test the value against.
   * @param writer    The writer function to use if the condition matches.
   * @return This BufferWriter instance for chaining.
   */
  public <T> BufferWriter writeIf(T value, Predicate<T> condition, BiConsumer<BufferWriter, T> writer) {
    boolean match = condition.test(value);
    buf.writeBoolean(match);
    if (match) {
      writer.accept(this, value);
    }
    return this;
  }

  /**
   * Writes a nullable value to the buffer. Writes a boolean indicating whether
   * the value is non-null, then writes the value using the provided writer if
   * it is not null.
   *
   * @param <T>    The type of the value to write.
   * @param value  The potentially null value to write.
   * @param writer The writer function to use if the value is not null.
   * @return This BufferWriter instance for chaining.
   */
  public <T> BufferWriter writeNullable(@Nullable T value, BiConsumer<BufferWriter, @NotNull T> writer) {
    buf.writeBoolean(value != null);
    if (value != null) {
      writer.accept(this, value);
    }
    return this;
  }

  /**
   * Writes an optional value to the buffer. Writes a boolean indicating whether
   * the optional is present, then writes the value using the provided writer if
   * it is present.
   *
   * @param <T>    The type of the optional value to write.
   * @param value  The optional value to write.
   * @param writer The writer function to use if the value is present.
   * @return This BufferWriter instance for chaining.
   */
  public <T> BufferWriter writeOptional(Optional<T> value, BiConsumer<BufferWriter, T> writer) {
    buf.writeBoolean(value.isPresent());
    value.ifPresent(t -> writer.accept(this, t));
    return this;
  }

  /**
   * Applies a custom consumer function to this BufferWriter, allowing for
   * custom write operations within a method chain.
   *
   * @param consumer The consumer to apply to this BufferWriter.
   * @return This BufferWriter instance for chaining.
   */
  public BufferWriter apply(Consumer<BufferWriter> consumer) {
    consumer.accept(this);
    return this;
  }

  /**
   * Gets the underlying FriendlyByteBuf being written to.
   *
   * @return The underlying buffer.
   */
  public FriendlyByteBuf getBuffer() {
    return buf;
  }

  /**
   * Writes a collection to the buffer. First writes the collection size as a VarInt,
   * then writes each element using the provided element writer.
   *
   * @param <T>           The type of elements in the collection.
   * @param collection    The collection to write.
   * @param elementWriter The writer function for each element.
   * @return This BufferWriter instance for chaining.
   */
  public <T> BufferWriter writeCollection(Collection<T> collection, BiConsumer<BufferWriter, T> elementWriter) {
    buf.writeVarInt(collection.size());
    for (T element : collection) {
      elementWriter.accept(this, element);
    }
    return this;
  }

  public <T extends IBufferSerializable> BufferWriter writeCollection(Collection<T> collection) {
    return writeCollection(collection, BufferWriter::writeSerializable);
  }

  /**
   * Writes a map to the buffer. First writes the map size as a VarInt, then writes
   * each key-value pair using the provided key and value writers.
   *
   * @param <K>         The type of keys in the map.
   * @param <V>         The type of values in the map.
   * @param map         The map to write.
   * @param keyWriter   The writer function for each key.
   * @param valueWriter The writer function for each value.
   * @return This BufferWriter instance for chaining.
   */
  public <K, V> BufferWriter writeMap(Map<K, V> map, BiConsumer<BufferWriter, K> keyWriter, BiConsumer<BufferWriter, V> valueWriter) {
    buf.writeVarInt(map.size());
    map.forEach((key, value) -> {
      keyWriter.accept(this, key);
      valueWriter.accept(this, value);
    });
    return this;
  }

  /**
   * Marks the end of the write operations. This is a no-op method provided
   * for semantic clarity in method chains.
   */
  public void done() {
    // No-op
  }
}
