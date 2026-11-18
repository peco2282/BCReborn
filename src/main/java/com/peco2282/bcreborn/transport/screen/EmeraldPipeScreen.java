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
package com.peco2282.bcreborn.transport.screen;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.transport.menu.EmeraldPipeMenu;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.AbstractButton;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class EmeraldPipeScreen extends BuildCraftScreen<EmeraldPipeMenu> {
  private static final ResourceLocation TEXTURE = BCRebornTransport.location("textures/gui/pipe_emerald.png");
  private static final ResourceLocation TEXTURE_BUTTON = BCRebornTransport.location("textures/gui/pipe_emerald_button.png");

  private static final int WHITE_LIST_BUTTON_ID = 1;
  private static final int BLACK_LIST_BUTTON_ID = 2;
  private static final int ROUND_ROBIN_BUTTON_ID = 3;

  public EmeraldPipeScreen(EmeraldPipeMenu p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
    this.imageWidth = 175;
    this.imageHeight = 161;
    this.inventoryLabelY = this.imageHeight - 93;
  }

  @Override
  protected void initilaizeLedger(Inventory p_97742_) {
  }

  /**
   * Original {@code GuiEmeraldPipe}: three 18x18 image buttons at (7, 41) for white list, black list and round robin;
   * the one of the current mode is shown pressed.
   */
  @Override
  protected void init() {
    super.init();
    PipeBlockEntity.ExtractFilterMode[] modes = PipeBlockEntity.ExtractFilterMode.values();
    for (int i = 0; i < modes.length; i++) {
      addRenderableWidget(new ModeButton(leftPos + 7 + i * 18, topPos + 41, modes[i]));
    }
  }

  private final class ModeButton extends AbstractButton {
    private final PipeBlockEntity.ExtractFilterMode mode;

    ModeButton(int x, int y, PipeBlockEntity.ExtractFilterMode mode) {
      super(x, y, 18, 18, Component.translatable("gui.bcreborntransport.emerald." + mode.name().toLowerCase(java.util.Locale.ROOT)));
      this.mode = mode;
      setTooltip(Tooltip.create(getMessage()));
    }

    @Override
    public void onPress() {
      if (minecraft != null && minecraft.gameMode != null) {
        minecraft.gameMode.handleInventoryButtonClick(menu.containerId, mode.ordinal());
      }
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      boolean selected = menu.getMode() == mode;
      int u = selected ? 54 : isHoveredOrFocused() ? 36 : 18;
      graphics.blit(TEXTURE_BUTTON, getX(), getY(), u, 0, 18, 18);
      graphics.blit(TEXTURE_BUTTON, getX() + 1, getY() + 1, 19 + mode.ordinal() * 18, 19, 16, 16);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
      defaultButtonNarrationText(output);
    }
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    int x = (this.width - this.imageWidth) / 2;
    int y = (this.height - this.imageHeight) / 2;
    guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
  }

  @Override
  protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    guiGraphics.drawString(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);
    guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0x404040, false);
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(guiGraphics, mouseX, mouseY);
  }
}
