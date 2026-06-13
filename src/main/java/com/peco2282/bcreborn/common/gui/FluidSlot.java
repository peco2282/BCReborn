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
package com.peco2282.bcreborn.common.gui;

import com.peco2282.bcreborn.common.screen.AdvancedInterfaceScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

/**
 * For the refinery, a kind of phantom slot for fluid.
 */
public class FluidSlot extends AdvancedSlot {
  public Fluid fluid;
  public int colorRenderCache;

  public FluidSlot(AdvancedInterfaceScreen<?> gui, int x, int y) {
    super(gui, x, y);
  }

  @Override
  public void drawSprite(GuiGraphics guiGraphics, int cornerX, int cornerY) {
    if (fluid != null) {
      // colorRenderCache はサブクラスや描画側で適用する
    }
    super.drawSprite(guiGraphics, cornerX, cornerY);
  }

  @Override
  public TextureAtlasSprite getIcon() {
    if (fluid == null) {
      return null;
    }
    IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
    ResourceLocation stillTexture = extensions.getStillTexture();
    return Minecraft.getInstance()
      .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
      .apply(stillTexture);
  }

  @Override
  public ResourceLocation getTexture() {
    return TextureAtlas.LOCATION_BLOCKS;
  }
}
