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
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.*;

/**
 * A fluent API for reading data from a {@link FriendlyByteBuf}.
 * <p>
 * This class provides a chainable interface for reading various data types from network packets
 * or other byte buffer sources. It supports both direct value reading and consumer-based operations
 * for more complex deserialization scenarios.
 * </p>
 * <p>
 * Example usage:
 * <pre>{@code
 * BufferReader.of(buffer)
 *     .applyInt(this::setId)
 *     .applyString(this::setName)
 *     .applyCollection(BufferReader::readInt, this::setValues)
 *     .done();
 * }</pre>
 * </p>
 *
 * @see BufferWriter
 * @see FriendlyByteBuf
 */
public class BufferReader {
  private final FriendlyByteBuf buf;

  /**
   * Private constructor to enforce factory method usage.
   *
   * @param buf The underlying byte buffer to read from
   */
  private BufferReader(FriendlyByteBuf buf) {
    this.buf = buf;
  }

  /**
   * Creates a new BufferReader instance wrapping the given buffer.
   *
   * @param buf The byte buffer to read from
   * @return A new BufferReader instance
   */
  public static BufferReader of(FriendlyByteBuf buf) {
    return new BufferReader(buf);
  }

  /**
   * Reads an integer value from the buffer.
   *
   * @return The integer value read
   */
  public int readInt() {
    return buf.readInt();
  }

  /**
   * Reads an integer value and passes it to the consumer.
   *
   * @param consumer The consumer to accept the integer value
   * @return This BufferReader for method chaining
   */
  public BufferReader applyInt(IntConsumer consumer) {
    consumer.accept(buf.readInt());
    return this;
  }

  /**
   * Reads a long value from the buffer.
   *
   * @return The long value read
   */
  public long readLong() {
    return buf.readLong();
  }

  /**
   * Reads a long value and passes it to the consumer.
   *
   * @param consumer The consumer to accept the long value
   * @return This BufferReader for method chaining
   */
  public BufferReader applyLong(LongConsumer consumer) {
    consumer.accept(buf.readLong());
    return this;
  }

  /**
   * Reads a UTF-8 encoded string from the buffer.
   *
   * @return The string value read
   */
  public String readString() {
    return buf.readUtf();
  }

  /**
   * Reads a UTF-8 encoded string and passes it to the consumer.
   *
   * @param consumer The consumer to accept the string value
   * @return This BufferReader for method chaining
   */
  public BufferReader applyString(Consumer<String> consumer) {
    consumer.accept(buf.readUtf());
    return this;
  }

  /**
   * Reads a boolean value from the buffer.
   *
   * @return The boolean value read
   */
  public boolean readBoolean() {
    return buf.readBoolean();
  }

  /**
   * Reads a boolean value and passes it to the consumer.
   *
   * @param consumer The consumer to accept the boolean value
   * @return This BufferReader for method chaining
   */
  public BufferReader applyBoolean(BooleanConsumer consumer) {
    consumer.accept(buf.readBoolean());
    return this;
  }

  /**
   * Reads a byte value from the buffer.
   *
   * @return The byte value read
   */
  public byte readByte() {
    return buf.readByte();
  }

  /**
   * Reads a byte value and passes it to the consumer.
   *
   * @param consumer The consumer to accept the byte value
   * @return This BufferReader for method chaining
   */
  public BufferReader applyByte(ByteConsumer consumer) {
    consumer.accept(buf.readByte());
    return this;
  }

  /**
   * Reads a short value from the buffer.
   *
   * @return The short value read
   */
  public short readShort() {
    return buf.readShort();
  }

  /**
   * Reads a short value and passes it to the consumer.
   *
   * @param consumer The consumer to accept the short value
   * @return This BufferReader for method chaining
   */
  public BufferReader applyShort(ShortConsumer consumer) {
    consumer.accept(buf.readShort());
    return this;
  }

  /**
   * Reads a float value from the buffer.
   *
   * @return The float value read
   */
  public float readFloat() {
    return buf.readFloat();
  }

  /**
   * Reads a float value and passes it to the consumer.
   *
   * @param consumer The consumer to accept the float value
   * @return This BufferReader for method chaining
   */
  public BufferReader applyFloat(FloatConsumer consumer) {
    consumer.accept(buf.readFloat());
    return this;
  }

  /**
   * Reads a double value from the buffer.
   *
   * @return The double value read
   */
  public double readDouble() {
    return buf.readDouble();
  }

  /**
   * Reads a double value and passes it to the consumer.
   *
   * @param consumer The consumer to accept the double value
   * @return This BufferReader for method chaining
   */
  public BufferReader applyDouble(DoubleConsumer consumer) {
    consumer.accept(buf.readDouble());
    return this;
  }

  /**
   * Reads a byte array from the buffer.
   *
   * @return The byte array read
   */
  public byte[] readByteArray() {
    return buf.readByteArray();
  }

  /**
   * Reads a byte array and passes it to the consumer.
   *
   * @param consumer The consumer to accept the byte array
   * @return This BufferReader for method chaining
   */
  public BufferReader applyByteArray(Consumer<byte[]> consumer) {
    consumer.accept(buf.readByteArray());
    return this;
  }

  /**
   * Reads an NBT compound tag from the buffer.
   * If the tag is null, returns an empty CompoundTag.
   *
   * @return The CompoundTag read, or empty tag if null
   */
  public CompoundTag readTag() {
    var nbt = buf.readNbt();
    return nbt != null ? nbt : new CompoundTag();
  }

  /**
   * Reads an NBT compound tag and passes it to the consumer.
   *
   * @param consumer The consumer to accept the CompoundTag
   * @return This BufferReader for method chaining
   */
  public BufferReader applyTag(Consumer<CompoundTag> consumer) {
    consumer.accept(readTag());
    return this;
  }

  /**
   * Reads a UUID from the buffer.
   *
   * @return The UUID read
   */
  public UUID readUUID() {
    return buf.readUUID();
  }

  /**
   * Reads a UUID and passes it to the consumer.
   *
   * @param consumer The consumer to accept the UUID
   * @return This BufferReader for method chaining
   */
  public BufferReader applyUUID(Consumer<UUID> consumer) {
    consumer.accept(buf.readUUID());
    return this;
  }

  /**
   * Reads a Direction enum value from the buffer.
   *
   * @return The Direction value read
   */
  public Direction readDirection() {
    return buf.readEnum(Direction.class);
  }

  /**
   * Reads a Direction enum value and passes it to the consumer.
   *
   * @param consumer The consumer to accept the Direction
   * @return This BufferReader for method chaining
   */
  public BufferReader applyDirection(Consumer<Direction> consumer) {
    consumer.accept(readDirection());
    return this;
  }

  /**
   * Reads a BlockState from the buffer by reading its resource location and resolving the block.
   *
   * @return The BlockState read, or null if the block is not found
   */
  public BlockState readBlockState() {
    ResourceLocation rl = buf.readResourceLocation();
    Block block = ForgeRegistries.BLOCKS.getValue(rl);
    return block != null ? block.defaultBlockState() : null;
  }

  /**
   * Reads a BlockState and passes it to the consumer if not null.
   *
   * @param consumer The consumer to accept the BlockState
   * @return This BufferReader for method chaining
   */
  public BufferReader applyBlockState(Consumer<BlockState> consumer) {
    BlockState state = readBlockState();
    if (state != null) {
      consumer.accept(state);
    }
    return this;
  }

  /**
   * Reads a BlockPos from the buffer.
   *
   * @return The BlockPos read
   */
  public BlockPos readBlockPos() {
    return buf.readBlockPos();
  }

  /**
   * Reads a BlockPos and passes it to the consumer.
   *
   * @param consumer The consumer to accept the BlockPos
   * @return This BufferReader for method chaining
   */
  public BufferReader applyBlockPos(Consumer<BlockPos> consumer) {
    consumer.accept(buf.readBlockPos());
    return this;
  }

  /**
   * Reads a ResourceLocation from the buffer.
   *
   * @return The ResourceLocation read
   */
  public ResourceLocation readResourceLocation() {
    return buf.readResourceLocation();
  }

  /**
   * Reads a ResourceLocation and passes it to the consumer.
   *
   * @param consumer The consumer to accept the ResourceLocation
   * @return This BufferReader for method chaining
   */
  public BufferReader applyResourceLocation(Consumer<ResourceLocation> consumer) {
    consumer.accept(buf.readResourceLocation());
    return this;
  }

  /**
   * Reads an ItemStack from the buffer.
   *
   * @return The ItemStack read
   */
  public ItemStack readItemStack() {
    return buf.readItem();
  }

  /**
   * Reads an ItemStack and passes it to the consumer.
   *
   * @param consumer The consumer to accept the ItemStack
   * @return This BufferReader for method chaining
   */
  public BufferReader applyItemStack(Consumer<ItemStack> consumer) {
    consumer.accept(buf.readItem());
    return this;
  }

  /**
   * Reads a FluidStack from the buffer.
   *
   * @return The FluidStack read
   */
  public FluidStack readFluidStack() {
    return buf.readFluidStack();
  }

  /**
   * Reads a custom serializable object from the buffer.
   * The factory supplier creates a new instance which then reads its data from the buffer.
   *
   * @param factory A supplier that creates a new instance of the serializable object
   * @param <T>     The type of the serializable object
   * @return The deserialized object
   */
  public <T extends IBufferSerializable> T readSerializable(Supplier<T> factory) {
    T value = factory.get();
    value.readData(buf);
    return value;
  }

  /**
   * Reads a FluidStack and passes it to the consumer.
   *
   * @param consumer The consumer to accept the FluidStack
   * @return This BufferReader for method chaining
   */
  public BufferReader applyFluidStack(Consumer<FluidStack> consumer) {
    consumer.accept(buf.readFluidStack());
    return this;
  }

  /**
   * Reads an enum value from the buffer.
   *
   * @param enumClass The class of the enum to read
   * @param <E>       The enum type
   * @return The enum value read
   */
  public <E extends Enum<E>> E readEnum(Class<E> enumClass) {
    return buf.readEnum(enumClass);
  }

  /**
   * Reads an enum value and passes it to the consumer.
   *
   * @param enumClass The class of the enum to read
   * @param consumer  The consumer to accept the enum value
   * @param <E>       The enum type
   * @return This BufferReader for method chaining
   */
  public <E extends Enum<E> & StringRepresentable> BufferReader applyEnum(Class<E> enumClass, Consumer<E> consumer) {
    consumer.accept(buf.readEnum(enumClass));
    return this;
  }

  /**
   * Reads a collection from the buffer using an ArrayList as the default collection type.
   *
   * @param elementReader A function to read each element
   * @param <T>           The type of elements in the collection
   * @return The collection read
   */
  public <T> List<T> readCollection(Function<BufferReader, T> elementReader) {
    return readCollectionWithFactory(ArrayList::new, elementReader);
  }

  /**
   * Reads a collection from the buffer with a custom collection factory.
   *
   * @param collectionFactory A function to create the collection with the given size
   * @param elementReader     A function to read each element
   * @param <T>               The type of elements in the collection
   * @param <C>               The collection type
   * @return The collection read
   */
  public <T, C extends Collection<T>> C readCollectionWithFactory(IntFunction<C> collectionFactory, Function<BufferReader, T> elementReader) {
    int size = buf.readVarInt();
    C collection = collectionFactory.apply(size);
    for (int i = 0; i < size; i++) {
      collection.add(elementReader.apply(this));
    }
    return collection;
  }

  /**
   * Reads a collection as a List and passes it to the consumer.
   *
   * @param elementReader A function to read each element
   * @param consumer      The consumer to accept the collection
   * @param <T>           The type of elements in the collection
   * @return This BufferReader for method chaining
   */
  public <T> BufferReader applyCollection(Function<BufferReader, T> elementReader, Consumer<List<T>> consumer) {
    consumer.accept(readCollection(elementReader));
    return this;
  }

  /**
   * Reads a collection with a custom factory and passes it to the consumer.
   *
   * @param collectionFactory A function to create the collection with the given size
   * @param elementReader     A function to read each element
   * @param consumer          The consumer to accept the collection
   * @param <T>               The type of elements in the collection
   * @param <C>               The collection type
   * @return This BufferReader for method chaining
   */
  public <T, C extends Collection<T>> BufferReader applyCollectionWithFactory(IntFunction<C> collectionFactory, Function<BufferReader, T> elementReader, Consumer<C> consumer) {
    consumer.accept(readCollectionWithFactory(collectionFactory, elementReader));
    return this;
  }

  public <T extends IBufferSerializable> BufferReader applySerializableCollection(Supplier<T> value, Consumer<List<T>> consumer) {
    consumer.accept(readCollection(it -> {
      var data = value.get();
      data.readData(buf);
      return data;
    }));
    return this;
  }

  public <T extends IBufferSerializable, C extends Collection<T>> BufferReader applyCollectionWithFactory(IntFunction<C> collectionFactory, Supplier<T> value, Consumer<C> consumer) {
    consumer.accept(readCollectionWithFactory(collectionFactory, it -> {
      var data = value.get();
      data.readData(buf);
      return data;
    }));
    return this;
  }

  /**
   * Reads a map from the buffer using a HashMap as the default map type.
   *
   * @param keyReader   A function to read each key
   * @param valueReader A function to read each value
   * @param <K>         The type of keys in the map
   * @param <V>         The type of values in the map
   * @return The map read
   */
  public <K, V> Map<K, V> readMap(Function<BufferReader, K> keyReader, Function<BufferReader, V> valueReader) {
    return readMap(HashMap::new, keyReader, valueReader);
  }

  /**
   * Reads a map from the buffer with a custom map factory.
   *
   * @param mapFactory  A function to create the map with the given size
   * @param keyReader   A function to read each key
   * @param valueReader A function to read each value
   * @param <K>         The type of keys in the map
   * @param <V>         The type of values in the map
   * @param <M>         The map type
   * @return The map read
   */
  public <K, V, M extends Map<K, V>> M readMap(IntFunction<M> mapFactory, Function<BufferReader, K> keyReader, Function<BufferReader, V> valueReader) {
    int size = buf.readVarInt();
    M map = mapFactory.apply(size);
    for (int i = 0; i < size; i++) {
      map.put(keyReader.apply(this), valueReader.apply(this));
    }
    return map;
  }

  /**
   * Reads a map as a HashMap and passes it to the consumer.
   *
   * @param keyReader   A function to read each key
   * @param valueReader A function to read each value
   * @param consumer    The consumer to accept the map
   * @param <K>         The type of keys in the map
   * @param <V>         The type of values in the map
   * @return This BufferReader for method chaining
   */
  public <K, V> BufferReader applyMap(Function<BufferReader, K> keyReader, Function<BufferReader, V> valueReader, Consumer<Map<K, V>> consumer) {
    consumer.accept(readMap(keyReader, valueReader));
    return this;
  }

  /**
   * Reads a map with a custom factory and passes it to the consumer.
   *
   * @param mapFactory  A function to create the map with the given size
   * @param keyReader   A function to read each key
   * @param valueReader A function to read each value
   * @param consumer    The consumer to accept the map
   * @param <K>         The type of keys in the map
   * @param <V>         The type of values in the map
   * @param <M>         The map type
   * @return This BufferReader for method chaining
   */
  public <K, V, M extends Map<K, V>> BufferReader applyMap(IntFunction<M> mapFactory, Function<BufferReader, K> keyReader, Function<BufferReader, V> valueReader, Consumer<M> consumer) {
    consumer.accept(readMap(mapFactory, keyReader, valueReader));
    return this;
  }

  public BufferReader applySerializable(IBufferSerializable serializable) {
    serializable.readData(buf);
    return this;
  }

  public BufferReader applyNBTSerializable(INBTSerializable serializable) {
    var nbt = buf.readNbt();
    if (nbt != null) {
      serializable.deserializeNBT(nbt);
    }
    return this;
  }

  /**
   * Reads a boolean condition and passes it to the consumer.
   * Useful for conditional deserialization logic.
   *
   * @param action The consumer to accept the boolean condition
   * @return This BufferReader for method chaining
   */
  public BufferReader readIf(BooleanConsumer action) {
    boolean condition = buf.readBoolean();
    action.accept(condition);
    return this;
  }

  /**
   * Reads a boolean flag and executes the action only if the flag is true.
   *
   * @param action The action to execute if the flag is true
   * @return This BufferReader for method chaining
   */
  public BufferReader ifPresent(Consumer<BufferReader> action) {
    if (buf.readBoolean()) {
      action.accept(this);
    }
    return this;
  }

  /**
   * Reads a nullable value from the buffer.
   * First reads a boolean indicating presence, then reads and passes the value to the consumer if present.
   *
   * @param reader   A function to read the value if present
   * @param consumer The consumer to accept the value if present
   * @param <T>      The type of the nullable value
   * @return This BufferReader for method chaining
   */
  public <T> BufferReader readNullable(Function<BufferReader, T> reader, Consumer<T> consumer) {
    if (buf.readBoolean()) {
      consumer.accept(reader.apply(this));
    }
    return this;
  }

  /**
   * Applies a custom consumer operation to this BufferReader.
   * Useful for complex deserialization logic or custom extensions.
   *
   * @param consumer The consumer to accept this BufferReader
   * @return This BufferReader for method chaining
   */
  public BufferReader apply(Consumer<BufferReader> consumer) {
    consumer.accept(this);
    return this;
  }

  /**
   * Gets the underlying FriendlyByteBuf for direct access if needed.
   *
   * @return The underlying byte buffer
   */
  public FriendlyByteBuf getBuffer() {
    return buf;
  }

  /**
   * Marks the end of a read operation chain.
   * This is a no-op method that serves as a semantic indicator for code readability.
   */
  public void done() {
    // No-op
  }
}
