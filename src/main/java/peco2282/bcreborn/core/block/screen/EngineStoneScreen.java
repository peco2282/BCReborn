package peco2282.bcreborn.core.block.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.block.menu.EngineStoneMenu;
import peco2282.bcreborn.lib.block.screen.BCContainerScreen;

public class EngineStoneScreen extends BCContainerScreen<EngineStoneMenu> {
  private static final ResourceLocation TEXTURE = BCReborn.location("textures/gui/steam_engine_gui.png");
  public EngineStoneScreen(EngineStoneMenu p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
  }

  @Override
  protected @NotNull ResourceLocation getTexture() {
    return TEXTURE;
  }

  @Override
  protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
    p_283065_.blit(getTexture(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
  }
}
