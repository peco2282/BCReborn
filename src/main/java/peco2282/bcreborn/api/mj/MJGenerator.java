package peco2282.bcreborn.api.mj;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface MJGenerator {
  long capacity();
  long perTick();
}
