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

/**
 * Port of the original {@code GuiStoneEngine}: the GUI texture with the burn flame (texture region 176, 0),
 * plus the energy ledger from {@link EngineScreen}.
 */
public class StoneEngineScreen extends EngineScreen<StoneEngineMenu> {
  private static final ResourceLocation TEXTURE = ResourceBuilder.energy().addPath("steam_engine_gui.png").build(ResourceBuilder.ResourceType.GUI);

  public StoneEngineScreen(StoneEngineMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    // Original: engine.getScaledBurnTime(12) -> flame of up to 12 + 2 pixels.
    int l = menu.getScaledBurn() * 12 / 100;
    if (l > 0) {
      graphics.blit(TEXTURE, leftPos + 80, topPos + 24 + 12 - l, 176, 12 - l, 14, l + 2);
    }
  }
}
