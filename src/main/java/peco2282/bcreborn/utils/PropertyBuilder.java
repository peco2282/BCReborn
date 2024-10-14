package peco2282.bcreborn.utils;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.Map;

public class PropertyBuilder {
  private final Map<Property<?>, Comparable<?>> properties = new HashMap<>();

  private PropertyBuilder() {}
  public static PropertyBuilder builder() {
    return new PropertyBuilder();
  }

  public <C extends Comparable<C>, V extends C> PropertyBuilder add(Property<C> property, V value) {
    properties.put(property, value);
    return this;
  }

  public BlockState set(BlockState state) {
    for (Map.Entry<Property<?>, Comparable<?>> entry : properties.entrySet())
      //noinspection unchecked,rawtypes
      state = state.setValue((Property) entry.getKey(), (Comparable) entry.getValue());
    return state;
  }
}
