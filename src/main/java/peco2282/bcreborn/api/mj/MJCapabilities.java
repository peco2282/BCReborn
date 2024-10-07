package peco2282.bcreborn.api.mj;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import peco2282.bcreborn.BCReborn;

public class MJCapabilities {
  public static final Capability<MJConnector> CONNECTOR;
  public static final Capability<MJReceiver> RECEIVER;
  public static final Capability<MJGenerator> GENERATOR;

  static {
    CONNECTOR = CapabilityManager.get(new CapabilityToken<>() {
    }, BCReborn.location("conector"));
    RECEIVER = CapabilityManager.get(new CapabilityToken<>() {
    }, BCReborn.location("receiver"));
    GENERATOR = CapabilityManager.get(new CapabilityToken<>() {
    }, BCReborn.location("generator"));
  }
}
