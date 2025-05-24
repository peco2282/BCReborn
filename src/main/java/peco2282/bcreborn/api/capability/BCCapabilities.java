/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.api.capability.mj.MJConnector;
import peco2282.bcreborn.api.capability.mj.MJGenerator;
import peco2282.bcreborn.api.capability.mj.MJReceiver;
import peco2282.bcreborn.api.capability.pipe.IPipe;

public class BCCapabilities {
  public static final Capability<MJConnector> CONNECTOR;
  public static final Capability<MJReceiver> RECEIVER;
  public static final Capability<MJGenerator> GENERATOR;

  public static final String CONNECTOR_KEY = "connector";
  public static final String RECEIVER_KEY = "receiver";
  public static final String GENERATOR_KEY = "generator";
  public static final Capability<IPipe<?>> PIPE;
  public static final String PIPE_KEY = "pipe";

  static {
    CONNECTOR = CapabilityManager.get(new CapabilityToken<>() {}, BCReborn.location(CONNECTOR_KEY));
    RECEIVER = CapabilityManager.get(new CapabilityToken<>() {}, BCReborn.location(RECEIVER_KEY));
    GENERATOR = CapabilityManager.get(new CapabilityToken<>() {}, BCReborn.location(GENERATOR_KEY));
    PIPE = CapabilityManager.get(new CapabilityToken<>() {}, BCReborn.location(PIPE_KEY));
  }
}
