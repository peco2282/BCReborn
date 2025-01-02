package peco2282.bcreborn.api.capability.mj;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface MJConnector extends MJCapability {
  boolean canConnection();
}
