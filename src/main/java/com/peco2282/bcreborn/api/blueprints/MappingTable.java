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
package com.peco2282.bcreborn.api.blueprints;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.IdMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A table that maps objects to integer IDs and vice versa.
 * Implements {@link IdMap}.
 *
 * @param <T> The type of objects in the table.
 */
public final class MappingTable<T> implements IdMap<T> {

  private final Object2IntMap<T> toId = new Object2IntOpenHashMap<>();
  private final List<T> toObject = new ArrayList<>();

  @Override
  public int getId(@NotNull T value) {
    if (!toId.containsKey(value)) {
      register(value);
    }
    return toId.getInt(value);
  }

  @Override
  public T byId(int p_122651_) {
    try {
      return get(p_122651_);
    } catch (MappingNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public int size() {
    return toObject.size();
  }

  /**
   * Gets the object associated with the specified ID.
   *
   * @param id The ID.
   * @return The object.
   * @throws MappingNotFoundException if no mapping exists for the ID.
   */
  public T get(int id) throws MappingNotFoundException {
    if (id < 0 || id >= toObject.size()) {
      throw new MappingNotFoundException(
        "No mapping at position " + id
      );
    }

    T value = toObject.get(id);

    if (value == null) {
      throw new MappingNotFoundException(
        "No mapping at position " + id
      );
    }

    return value;
  }

  /**
   * Adds a missing entry (null) to the mapping table.
   */
  public void addMissing() {
    register(null);
  }

  /**
   * Registers an object in the table and assigns it a new ID.
   *
   * @param value The object to register.
   */
  public void register(T value) {
    toObject.add(value);
    toId.put(value, toObject.size() - 1);
  }

  /**
   * Gets all objects in the mapping table.
   *
   * @return A list of objects.
   */
  public List<T> values() {
    return toObject;
  }

  @Override
  public @NotNull Iterator<T> iterator() {
    return toObject.iterator();
  }
}