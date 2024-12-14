package peco2282.bcreborn.utils;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.Map;


/**
 * A builder utility class for constructing and managing property values that can be applied to a {@link BlockState}.
 * It allows for flexible addition and application of property settings.
 *
 * @author peco2282
 */
public class PropertyBuilder {
  private final Map<Property<?>, Comparable<?>> properties = new HashMap<>();

  private PropertyBuilder() {}
  /**
   * Initializes and returns a new instance of the {@link PropertyBuilder}.
   *
   * @return a new {@link PropertyBuilder}.
   */
  public static PropertyBuilder builder() {
    return new PropertyBuilder();
  }

  /**
   * Adds a property and its corresponding value to the builder for later application.
   *
   * @param <C> the type of the property, which must extend {@link Comparable}.
   * @param <V> the type of the value to be assigned to the property.
   * @param property the {@link Property} to add to the builder.
   * @param value the value to assign to the specified property.
   * @return the {@link PropertyBuilder} instance for method chaining.
   */
  public <C extends Comparable<C>, V extends C> PropertyBuilder add(Property<C> property, V value) {
    properties.put(property, value);
    return this;
  }

  /**
   * Applies all stored properties and their values to the specified {@link BlockState}.
   *
   * @param state the {@link BlockState} to which properties will be applied.
   * @return the modified {@link BlockState} with the applied properties.
   */
  public BlockState set(BlockState state) {
    for (Map.Entry<Property<?>, Comparable<?>> entry : properties.entrySet())
      //noinspection unchecked,rawtypes
      state = state.setValue((Property) entry.getKey(), (Comparable) entry.getValue());
    return state;
  }
}
