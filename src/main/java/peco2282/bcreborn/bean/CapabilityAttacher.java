package peco2282.bcreborn.bean;

import net.minecraft.util.StringRepresentable;
import peco2282.bcreborn.api.mj.MJCapabilities;

/**
 * The {@code CapabilityAttacher} annotation is used to specify the type of capability 
 * that should be attached to a Minecraft object. This helps in managing various capabilities
 * like connectors, receivers, and generators in the modded environment.
 */
public @interface CapabilityAttacher {
  /**
   * Specifies the {@code Type} of capability to attach.
   */
  Type value();

  /**
   * The {@code Type} enum defines different types of capabilities that
   * can be attached. These include connectors, receivers, and generators.
   */
  enum Type implements StringRepresentable {
    /**
     * Represents a connector capability that handles connection pipe.
     */
    CONNECTOR(MJCapabilities.CONNECTOR_KEY),
    /**
     * Represents a receiver capability that handles receiving pipe.
     */
    RECEIVER(MJCapabilities.RECEIVER_KEY),
    /**
     * Represents a generator capability that handles power generation pipe.
     */
    GENERATOR(MJCapabilities.GENERATOR_KEY);
    private final String name;

    Type(String name) {
      this.name = name;
    }

    /**
     * Returns the serialized name of the capability type as a string.
     *
     * @return the name of the capability type
     */
    @Override
    public String getSerializedName() {
      return this.name;
    }
  }
}
