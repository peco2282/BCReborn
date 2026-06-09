package com.peco2282.bcreborn.api.blueprints;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.IdMap;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

  public void addMissing() {
    register(null);
  }

  public void register(T value) {
    toObject.add(value);
    toId.put(value, toObject.size() - 1);
  }

  public List<T> values() {
    return toObject;
  }

  @Override
  public @NotNull Iterator<T> iterator() {
    return toObject.iterator();
  }
}