package com.peco2282.bcreborn.common.block;

import net.minecraft.world.level.block.state.properties.Property;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockStateBuilder {
  private BlockStateBuilder() {}
  public static BlockStateBuilder builder() {
    return new BlockStateBuilder();
  }

  private final Map<Property<?>, Comparable<?>> PROPERTIES = new ConcurrentHashMap<>();

  public <T extends Comparable<T>> BlockStateBuilder put(Property<T> property, T value) {
    PROPERTIES.put(property, value);
    return this;
  }

  public void bind(BuildCraftBlock block) {
    block.registerDefaultState(block.getStateDefinition().any());
  }
}
