package peco2282.bcreborn.event;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peco2282.bcreborn.api.capability.mj.MJConnector;

public class CapabilityEvent {
  private static final Logger log = LoggerFactory.getLogger(CapabilityEvent.class);

  @SubscribeEvent
  public static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
    BlockEntity entity = event.getObject();
    if (entity instanceof MJConnector) {
      log.trace("CapEvent {}", entity);
//    event.addCapability(BCReborn.location(""), );
    }
  }
}
