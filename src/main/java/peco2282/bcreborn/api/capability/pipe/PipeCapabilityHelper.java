package peco2282.bcreborn.api.capability.pipe;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.capability.BCCapabilities;
import peco2282.bcreborn.transport.block.pipe.Entity;

public class PipeCapabilityHelper<E extends Entity> implements ICapabilityProvider {
  private final IPipe<E> pipe;

  public PipeCapabilityHelper(IPipe<E> pipe) {
    this.pipe = pipe;
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
    if (cap == BCCapabilities.PIPE) return BCCapabilities.PIPE.orEmpty(cap, LazyOptional.of(() -> pipe)).cast();
    return LazyOptional.empty();
  }
}
