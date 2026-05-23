package com.peco2282.bcreborn.builders.event;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.BuildersMenuTypes;
import com.peco2282.bcreborn.builders.screen.ArchitectScreen;
import com.peco2282.bcreborn.builders.screen.BlueprintLibraryScreen;
import com.peco2282.bcreborn.builders.screen.BuilderScreen;
import com.peco2282.bcreborn.builders.screen.FillerScreen;
import com.peco2282.bcreborn.common.builder.patterns.FillerPattern;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BCRebornBuilders.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BCRebornBuildersEvent {
  @SubscribeEvent
  public static void onTextureStitchPost(TextureStitchEvent.Post event) {
    if (!event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) return;
    for (FillerPattern pattern : FillerPattern.patterns.values()) {
      pattern.registerIcons(loc -> event.getAtlas().getSprite(loc));
    }
  }

  @SubscribeEvent
  public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      MenuScreens.register(BuildersMenuTypes.BUILDER.get(), BuilderScreen::new);
      MenuScreens.register(BuildersMenuTypes.ARCHITECT.get(), ArchitectScreen::new);
      MenuScreens.register(BuildersMenuTypes.FILLER.get(), FillerScreen::new);
      MenuScreens.register(BuildersMenuTypes.BLUEPRINT_LIBRARY.get(), BlueprintLibraryScreen::new);
    });
  }
}
