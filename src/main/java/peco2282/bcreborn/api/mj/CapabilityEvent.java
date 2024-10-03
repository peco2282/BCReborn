package peco2282.bcreborn.api.mj;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.lib.block.entity.TileContainerNeptune;
import peco2282.bcreborn.lib.block.entity.TileNeptune;

public class CapabilityEvent {
  private static final Logger log = LoggerFactory.getLogger(CapabilityEvent.class);

  @SubscribeEvent
  public static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
    if (event.getObject() instanceof TileNeptune || event.getObject() instanceof TileContainerNeptune) {
      log.trace(event.getObject().toString());
      event.addCapability(BCReborn.location("mj"), new MJCapabilityHelper(new MJConnectorImpl(), (() -> 0), (l, s) -> 0));
    }
  }
}
