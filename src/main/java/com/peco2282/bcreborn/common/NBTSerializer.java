package com.peco2282.bcreborn.common;

import net.minecraft.nbt.CompoundTag;


/**
 * Interface for serializing and deserializing objects to/from NBT (Named Binary Tag) format.
 * <p>
 * This interface provides a standard way to save and load object state to Minecraft's NBT storage system,
 * which is commonly used for persistent data storage in block entities, items, and world data.
 * </p>
 *
 * @param <T> the type of object to serialize/deserialize
 */
public interface NBTSerializer<T> {
  /**
   * Serializes the given object into the provided NBT tag.
   * <p>
   * Implementations should write all necessary data from the object to the tag
   * so that it can be fully reconstructed later via {@link #deserialize(Object, CompoundTag)}.
   * </p>
   *
   * @param obj the object to serialize (must not be null)
   * @param tag the NBT tag to write data into (must not be null)
   */
  void serialize(T obj, CompoundTag tag);

  /**
   * Deserializes data from the provided NBT tag into the given object.
   * <p>
   * Implementations should read all necessary data from the tag and apply it to the object,
   * restoring its state to match what was previously saved via {@link #serialize(Object, CompoundTag)}.
   * </p>
   *
   * @param obj the object to populate with deserialized data (must not be null)
   * @param tag the NBT tag to read data from (must not be null)
   */
  void deserialize(T obj, CompoundTag tag);
}
