/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.event;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import peco2282.bcreborn.InternalLogger;
import peco2282.bcreborn.api.capability.mj.MJConnector;

public class CapabilityEvent {
  private static final Logger log = InternalLogger.create();

  @SubscribeEvent
  public static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
    BlockEntity entity = event.getObject();
    if (entity instanceof MJConnector) {
      log.trace("CapEvent {}", entity);
      //    event.addCapability(BCReborn.location(""), );
    }
  }
}
