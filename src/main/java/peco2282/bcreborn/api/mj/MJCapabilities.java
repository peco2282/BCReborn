package peco2282.bcreborn.api.mj;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import peco2282.bcreborn.BCReborn;

public class MJCapabilities {
  public static final Capability<MJConnector> CONNECTOR;
  public static final Capability<MJReceiver> RECEIVER;
  public static final Capability<MJGenerator> GENERATOR;

  public static final String CONNECTOR_KEY = "connector";
  public static final String RECEIVER_KEY = "receiver";
  public static final String GENERATOR_KEY = "generator";

  static {
    CONNECTOR = CapabilityManager.get(new CapabilityToken<>() {
    }, BCReborn.location(CONNECTOR_KEY));
    RECEIVER = CapabilityManager.get(new CapabilityToken<>() {
    }, BCReborn.location(RECEIVER_KEY));
    GENERATOR = CapabilityManager.get(new CapabilityToken<>() {
    }, BCReborn.location(GENERATOR_KEY));
  }
}
