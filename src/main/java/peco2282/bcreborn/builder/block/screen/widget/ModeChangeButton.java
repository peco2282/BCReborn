package peco2282.bcreborn.builder.block.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import peco2282.bcreborn.misc.ButtonTextureProvider;

public class ModeChangeButton extends Button {
  public ModeChangeButton(int x, int y, int width, int height, Component message) {
    super(x, y, width, height, message, b -> {
    }, DEFAULT_NARRATION);
  }

  @Override
  protected void renderWidget(GuiGraphics p_281670_, int p_282682_, int p_281714_, float p_282542_) {
  }
}
