/*
 * Copyright (c) 2025 peco2282
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package peco2282.bcreborn.core.block.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import peco2282.bcreborn.BCReborn;
import peco2282.bcreborn.core.block.menu.EngineIronMenu;
import peco2282.bcreborn.lib.block.screen.BCContainerScreen;

import java.awt.*;

@SuppressWarnings("FieldCanBeLocal")
public class EngineIronScreen extends BCContainerScreen<EngineIronMenu> {
  private static final ResourceLocation TEXTURE =
      BCReborn.location("textures/gui/combustion_engine_gui.png");
  private final int HEIGHT = 5;

  public EngineIronScreen(EngineIronMenu p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
    imageHeight = 176;
    inventoryLabelY = imageHeight - 95;
  }

  @Override
  protected @NotNull ResourceLocation getTexture() {
    return TEXTURE;
  }

  @Override
  protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
    p_283065_.blit(
        getTexture(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    water(p_283065_, Color.BLUE.getRGB(), 0);
    oil(p_283065_, Color.BLACK.getRGB(), 0);
    gold(p_283065_, Color.YELLOW.getRGB(), 0);
  }

  public void water(GuiGraphics graphics, int color, int stage) {
    int leftBase = 26 + leftPos;
    int bottomBase = 78 + topPos; // 18 + 8 * 10
    for (int i = stage - 1; i >= 0; i--) {
      graphics.fill(leftBase, bottomBase, leftBase + 16, (bottomBase -= HEIGHT), color);
      if ((stage - i) == 5) bottomBase--;
      else graphics.fill(leftBase + 7, bottomBase, leftBase + 16, --bottomBase, color);
    }
  }

  public void oil(GuiGraphics graphics, int color, int stage) {
    int leftBase = 80 + leftPos;
    int bottomBase = 78 + topPos; // 18 + 8 * 10
    for (int i = stage - 1; i >= 0; i--) {
      graphics.fill(leftBase, bottomBase, leftBase + 16, (bottomBase -= HEIGHT), color);
      if ((stage - i) == 5) bottomBase--;
      else graphics.fill(leftBase + 7, bottomBase, leftBase + 16, --bottomBase, color);
    }
  }

  public void gold(GuiGraphics graphics, int color, int stage) {
    int leftBase = 134 + leftPos;
    int bottomBase = 78 + topPos; // 18 + 8 * 10
    for (int i = stage - 1; i >= 0; i--) {
      graphics.fill(leftBase, bottomBase, leftBase + 16, (bottomBase -= HEIGHT), color);
      if ((stage - i) == 5) bottomBase--;
      else graphics.fill(leftBase + 7, bottomBase, leftBase + 16, --bottomBase, color);
    }
  }
}
