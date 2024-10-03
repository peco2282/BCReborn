package peco2282.bcreborn.event;


import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.block.BCCoreBlocks;
import peco2282.bcreborn.core.block.container.EngineIronScreen;
import peco2282.bcreborn.core.block.container.EngineStoneScreen;
import peco2282.bcreborn.core.block.menu.BCCoreMenuTypes;

// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@Mod.EventBusSubscriber(modid = BCReborn.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
  private static final Logger log = LoggerFactory.getLogger(ClientModEvents.class);

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    log.trace("onClientSetup");
    log.trace("Register MenuScreens");
    MenuScreens.register(BCCoreMenuTypes.STONE_ENGINE.get(), EngineStoneScreen::new);
    MenuScreens.register(BCCoreMenuTypes.IRON_ENGINE.get(), EngineIronScreen::new);
    log.trace("Done");
    log.trace("Register ItemBlockRenderTypes");
    ItemBlockRenderTypes.setRenderLayer(BCCoreBlocks.STONE_ENGINE.get(), RenderType.cutout());
    ItemBlockRenderTypes.setRenderLayer(BCCoreBlocks.IRON_ENGINE.get(), RenderType.cutout());
    log.trace("Done");
    // Some client setup code
//    LOGGER.info("HELLO FROM CLIENT SETUP");
//    LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
  }
}