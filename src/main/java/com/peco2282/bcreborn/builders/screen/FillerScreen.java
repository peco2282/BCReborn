/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.builders.screen;

import com.peco2282.bcreborn.BCRebornBuilders;
import com.peco2282.bcreborn.builders.block.entity.FillerBlockEntity;
import com.peco2282.bcreborn.builders.menu.FillerMenu;
import com.peco2282.bcreborn.common.gui.SpriteSheat;
import com.peco2282.bcreborn.common.packet.BCNetworkManager;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class FillerScreen extends BuildCraftScreen<FillerMenu> {
  private static final ResourceLocation TEXTURE = BCRebornBuilders.location("textures/gui/filler.png");
  private final FillerBlockEntity filler;

  public FillerScreen(FillerMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
    this.filler = menu.getFiller();
    this.imageWidth = 175;
    this.imageHeight = 240;
  }

  @Override
  protected void init() {
    super.init();

    addRenderableWidget(new FillerButton(leftPos + 20, topPos + 30, 10, 16, SpriteSheat.BUTTON_LEFT_NORMAL, SpriteSheat.BUTTON_LEFT_SELECTED, b -> changePattern(-1)));

    addRenderableWidget(new FillerButton(leftPos + 62, topPos + 30, 10, 16, SpriteSheat.BUTTON_RIGHT_NORMAL, SpriteSheat.BUTTON_RIGHT_SELECTED, b -> changePattern(1)));
  }

  private void changePattern(int delta) {
    BCNetworkManager.sendSetFillerPattern(filler.getBlockPos(), delta);
  }

  @Override
  protected void initilaizeLedger(Inventory inventory) {
  }

  @Override
  protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    drawWidgets(graphics, mouseX, mouseY);
    // Render current delta icon
    int pattern = menu.getCurrentPattern();
    graphics.blit(TEXTURE, leftPos + 133, topPos + 18, 176 + (pattern % 4) * 16, 16 + (pattern / 4) * 16, 16, 16);

    // Tooltip for delta
    if (isHovering(133, 18, 16, 16, mouseX, mouseY)) {
      graphics.renderTooltip(this.font, Component.translatable("gui.filler.delta." + pattern), mouseX, mouseY);
    }
  }

  @Override
  protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
    graphics.drawString(this.font, this.title, imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);
    graphics.drawString(this.font, Component.translatable("gui.inventory"), 8, imageHeight - 94, 0x404040, false);
  }

  private static class FillerButton extends Button {
    private final SpriteSheat noActive;
    private final SpriteSheat hover;

    public FillerButton(int x, int y, int width, int height, SpriteSheat texture, SpriteSheat hover, Button.OnPress onPress) {
      super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
      this.noActive = texture;
      this.hover = hover;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (this.isHoveredOrFocused()) {
        this.hover.blit(graphics, getX(), getY());
      } else {
        this.noActive.blit(graphics, getX(), getY());
      }
    }
  }
}
