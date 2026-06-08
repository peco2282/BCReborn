package com.peco2282.bcreborn.api.blueprints;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.List;

public final class MappingTable<T> {

  private final Object2IntMap<T> toId = new Object2IntOpenHashMap<>();
  private final List<T> toObject = new ArrayList<>();

  public int getId(T value) {
    if (!toId.containsKey(value)) {
      register(value);
    }
    return toId.getInt(value);
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
    toObject.add(null);
  }

  public void register(T value) {
    toObject.add(value);
    toId.put(value, toObject.size() - 1);
  }

  public List<T> values() {
    return toObject;
  }
}