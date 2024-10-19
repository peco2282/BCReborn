package peco2282.bcreborn.builder.block.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.builder.block.menu.FillerMenu;
import peco2282.bcreborn.builder.block.screen.widget.ModeChangeButton;
import peco2282.bcreborn.lib.block.screen.BCContainerScreen;

public class FillerScreen extends BCContainerScreen<FillerMenu> {
  private static final ResourceLocation TEXTURE = BCReborn.location("textures/gui/filler.png");
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
  protected void init() {
    super.init();
    addRenderableWidget(new ModeChangeButton(4, this.height - 12, 10, 12, Component.literal("Mode")));
  }

  @Override
  public void render(GuiGraphics p_283479_, int p_283661_, int p_281248_, float p_281886_) {
    super.render(p_283479_, p_283661_, p_281248_, p_281886_);
  }

  @Override
  protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
    p_283065_.blit(getTexture(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
//    ButtonTextureProvider.leftButton(p_283065_, ButtonTextureProvider.Mode.SELECTED, leftPos + 2, topPos + 40, true, isFocused());
//    ButtonTextureProvider.rightButton(p_283065_, ButtonTextureProvider.Mode.SELECTED, leftPos + 4-2, topPos + 40);
  }
}
