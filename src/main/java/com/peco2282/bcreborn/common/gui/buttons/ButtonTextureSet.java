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
package com.peco2282.bcreborn.common.gui.buttons;


import net.minecraft.resources.ResourceLocation;

public class ButtonTextureSet implements IButtonTextureSet {
  private final ResourceLocation texture;
  private final int x, y, height, width;

  public ButtonTextureSet(int x, int y, int height, int width) {
    this(x, y, height, width, StandardButtonTextureSets.BUTTON_TEXTURES);
  }

  public ButtonTextureSet(int x, int y, int height, int width, ResourceLocation texture) {
    this.x = x;
    this.y = y;
    this.height = height;
    this.width = width;
    this.texture = texture;
  }

  @Override
  public int getX() {
    return x;
  }

  @Override
  public int getY() {
    return y;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public ResourceLocation getTexture() {
    return texture;
  }
}
