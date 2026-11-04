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

import com.mojang.blaze3d.systems.RenderSystem;
import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.energy.menu.IronEngineMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

/**
 * Port of the original {@code GuiCombustionEngine}: fuel (104, 19) and coolant (122, 19) tanks drawn with the
 * fluid texture under the gauge overlay (texture region 176, 0), fluid name tooltips, and the energy ledger.
 */
public class IronEngineScreen extends EngineScreen<IronEngineMenu> {
  private static final ResourceLocation TEXTURE = ResourceBuilder.energy("combustion_engine_gui.png").build(ResourceBuilder.ResourceType.GUI);
  /** Original {@code TileEngineIron.MAX_LIQUID}. */
  private static final int TANK_CAPACITY = 10000;
  private static final int FUEL_X = 104;
  private static final int COOLANT_X = 122;
  private static final int TANK_Y = 19;
  private static final int TANK_W = 16;
  private static final int TANK_H = 58;

  public IronEngineScreen(IronEngineMenu menu, Inventory inventory, Component title) {
    super(menu, inventory, title);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    drawFluid(graphics, menu.getFuel(), leftPos + FUEL_X, topPos + TANK_Y);
    drawFluid(graphics, menu.getCoolant(), leftPos + COOLANT_X, topPos + TANK_Y);
    graphics.blit(TEXTURE, leftPos + FUEL_X, topPos + TANK_Y, 176, 0, 16, 60);
    graphics.blit(TEXTURE, leftPos + COOLANT_X, topPos + TANK_Y, 176, 0, 16, 60);
  }

  /** Original {@code GuiBuildCraft.drawFluid()}: the still texture, tinted, filled from the bottom. */
  private static void drawFluid(GuiGraphics graphics, FluidStack stack, int x, int y) {
    if (stack.isEmpty()) return;
    int height = Math.min(TANK_H, Math.max(1, stack.getAmount() * TANK_H / TANK_CAPACITY));
    IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(stack.getFluid());
    TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
      .apply(ext.getStillTexture(stack));
    int color = ext.getTintColor(stack);
    RenderSystem.setShaderColor(((color >> 16) & 0xff) / 255F, ((color >> 8) & 0xff) / 255F, (color & 0xff) / 255F, 1F);
    int drawn = 0;
    while (drawn < height) {
      int part = Math.min(16, height - drawn);
      graphics.blit(x, y + TANK_H - drawn - part, 0, TANK_W, part, sprite);
      drawn += part;
    }
    RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
  }

  /** Original: the fluid name when hovering a tank. */
  @Override
  protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderTooltip(graphics, mouseX, mouseY);
    FluidStack stack = FluidStack.EMPTY;
    if (mouseY >= topPos + TANK_Y && mouseY < topPos + TANK_Y + 60) {
      if (mouseX >= leftPos + FUEL_X && mouseX < leftPos + FUEL_X + 16) {
        stack = menu.getFuel();
      } else if (mouseX >= leftPos + COOLANT_X && mouseX < leftPos + COOLANT_X + 16) {
        stack = menu.getCoolant();
      }
    }
    if (!stack.isEmpty()) {
      graphics.renderTooltip(font, Component.literal("").append(stack.getDisplayName())
        .append(" (" + stack.getAmount() + " mB)"), mouseX, mouseY);
    }
  }
}
