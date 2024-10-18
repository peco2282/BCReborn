package peco2282.bcreborn.api.mj;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MJCapailityHelper implements ICapabilityProvider {
  private final MJConnector connector;
  private final MJReceiver receiver;
  private final MJGenerator generator;
  public MJCapailityHelper(
      @Nullable MJConnector connector,
      @Nullable MJReceiver receiver,
      @Nullable MJGenerator generator
  ) {
    this.connector = connector;
    this.receiver = receiver;
    this.generator = generator;
  }
  /**
   * Retrieves the Optional handler for the capability requested on the specific side.
   * The return value <strong>CAN</strong> be the same for multiple faces.
   * Modders are encouraged to cache this value, using the listener capabilities of the Optional to
   * be notified if the requested capability get lost.
   *
   * @param cap  The capability to check
   * @param side The Side to check from,
   *             <strong>CAN BE NULL</strong>. Null is defined to represent 'internal' or 'self'
   * @return The requested an optional holding the requested capability.
   */
  @Override
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (cap == MJCapabilities.CONNECTOR) return MJCapabilities.CONNECTOR.orEmpty(cap, lazy(connector)).cast();
    if (cap == MJCapabilities.RECEIVER) return MJCapabilities.RECEIVER.orEmpty(cap, lazy(receiver)).cast();
    if (cap == MJCapabilities.GENERATOR) return MJCapabilities.GENERATOR.orEmpty(cap, lazy(generator)).cast();
    return lazy(null).cast();
  }
  static <T> LazyOptional<T> lazy(T cap) {
    return LazyOptional.of(() -> cap);
  }
}
