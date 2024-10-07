package peco2282.bcreborn.api.mj;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.mj.impl.MJConnectorSimpleImpl;
import peco2282.bcreborn.api.mj.impl.MJGeneratorSimpleImpl;
import peco2282.bcreborn.api.mj.impl.MJReceiverSimpleImpl;

@SuppressWarnings("UnstableApiUsage")
public class MJManagement extends CapabilityProvider<MJManagement> {
  private MJManagement() {
    super(MJManagement.class);
  }

  public static MJManagement getInstance() {
    return new MJManagement();
  }

  public static MJGenerator getGenerator(long per) {
    return new MJGeneratorSimpleImpl(per);
  }

  public static MJConnector getConnector() {
    return new MJConnectorSimpleImpl();
  }

  public static MJReceiver getReceiver(long require) {
    return new MJReceiverSimpleImpl(require);
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (cap == MJCapabilities.GENERATOR) return LazyOptional.of(() -> (T) getGenerator(10));
    if (cap == MJCapabilities.CONNECTOR) return LazyOptional.of(() -> (T) getConnector());
    if (cap == MJCapabilities.RECEIVER) return LazyOptional.of(() -> (T) getReceiver(10));
    return super.getCapability(cap, side);
  }
}
