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
package com.peco2282.bcreborn.energy.screen;

import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.energy.menu.StoneEngineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StoneEngineScreen extends EngineScreen<StoneEngineMenu> {
  private static final ResourceLocation TEXTURE = ResourceBuilder.energy().addPath("steam_engine_gui.png").build(ResourceBuilder.ResourceType.GUI);
  // 簡易ゲージ領域（テクスチャに依存しない塗りつぶし描画）
  private static final int BURN_X = 80, BURN_Y = 24, BURN_W = 14, BURN_H = 14;
  private static final int EN_X = 152, EN_Y = 16, EN_W = 12, EN_H = 54;
  private static final int STAGE_X = 8, STAGE_Y = 8, STAGE_W = 10, STAGE_H = 10;

  public StoneEngineScreen(StoneEngineMenu p_97741_, Inventory p_97742_, Component p_97743_) {
    super(p_97741_, p_97742_, p_97743_);
  }

  @Override
  protected void initilaizeLedger(Inventory p_97742_) {
  }

  @Override
  protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
    p_283065_.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

    // 燃焼ゲージ（下から上に塗りつぶし）
    int burn = this.menu.getScaledBurn(); // 0..100
    int burnPix = (BURN_H * burn) / 100;
    int bx = this.leftPos + BURN_X;
    int by = this.topPos + BURN_Y + (BURN_H - burnPix);
    if (burnPix > 0) {
      p_283065_.fill(bx, by, bx + BURN_W, by + burnPix, 0xFFFFA000); // 橙色
    }

    // FE バー（縦）
    int energy = this.menu.getEnergy();
    int max = Math.max(1, this.menu.getMaxEnergy());
    int ePix = (int) Math.round((double) energy * EN_H / max);
    int ex = this.leftPos + EN_X;
    int ey = this.topPos + EN_Y + (EN_H - ePix);
    // 枠
    p_283065_.fill(ex - 1, this.topPos + EN_Y - 1, ex + EN_W + 1, this.topPos + EN_Y + EN_H + 1, 0xFF202020);
    if (ePix > 0) {
      p_283065_.fill(ex, ey, ex + EN_W, ey + ePix, 0xFF00C853); // 緑
    }

    // 段階インジケータ（色）
    int stage = this.menu.getStageOrdinal();
    int color = switch (stage) {
      case 0 -> 0xFF2196F3; // BLUE
      case 1 -> 0xFF4CAF50; // GREEN
      case 2 -> 0xFFFFC107; // YELLOW
      case 3 -> 0xFFF44336; // RED
      default -> 0xFFFF5722; // OVERHEAT
    };
    int sx = this.leftPos + STAGE_X, sy = this.topPos + STAGE_Y;
    p_283065_.fill(sx, sy, sx + STAGE_W, sy + STAGE_H, color);
  }

  @Override
  public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
    super.render(gui, mouseX, mouseY, partialTicks);

    // ツールチップ
    int ex = this.leftPos + EN_X;
    int ey = this.topPos + EN_Y;
    if (isHovering(ex, ey, EN_W, EN_H, mouseX, mouseY)) {
      int energy = this.menu.getEnergy();
      int max = Math.max(1, this.menu.getMaxEnergy());
      gui.renderTooltip(this.font, Component.translatable("tooltip.bcreborn.energy", energy, max), mouseX, mouseY);
    }

    int bx = this.leftPos + BURN_X;
    int by = this.topPos + BURN_Y;
    if (isHovering(bx, by, BURN_W, BURN_H, mouseX, mouseY)) {
      int burn = this.menu.getScaledBurn();
      gui.renderTooltip(this.font, Component.translatable("tooltip.bcreborn.burn", burn), mouseX, mouseY);
    }
  }
}
