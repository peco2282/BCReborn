package peco2282.bcreborn.builder.block.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.builder.block.menu.FillerMenu;
import peco2282.bcreborn.lib.block.container.BCContainerScreen;

public class FillerScreen extends BCContainerScreen<FillerMenu> {
  private static final ResourceLocation TEXTURE = BCReborn.location("textures/gui/fillers.png");
  public FillerScreen(FillerMenu p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
//    imageWidth = 176;
    imageHeight = 241;
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
