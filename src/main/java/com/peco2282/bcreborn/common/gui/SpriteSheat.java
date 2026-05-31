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

import com.peco2282.bcreborn.BCRebornCore;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public record SpriteSheat(ResourceLocation texture, int width, int height, int u, int v) {
  public static final ResourceLocation BUTTONS = BCRebornCore.location("textures/gui/buttons.png");

  public static final SpriteSheat BUTTON_LARGE_NONE = new SpriteSheat(BUTTONS, 200, 20, 0, 0);
  public static final SpriteSheat BUTTON_LARGE_OFF = new SpriteSheat(BUTTONS, 200, 20, 0, 20);
  public static final SpriteSheat BUTTON_LARGE_NORMAL = new SpriteSheat(BUTTONS, 200, 20, 0, 40);
  public static final SpriteSheat BUTTON_LARGE_SELECTED = new SpriteSheat(BUTTONS, 200, 20, 0, 60);

  public static final SpriteSheat BUTTON_SMALL_NONE = new SpriteSheat(BUTTONS, 200, 15, 0, 80);
  public static final SpriteSheat BUTTON_SMALL_OFF = new SpriteSheat(BUTTONS, 200, 15, 0, 95);
  public static final SpriteSheat BUTTON_SMALL_NORMAL = new SpriteSheat(BUTTONS, 200, 15, 0, 110);
  public static final SpriteSheat BUTTON_SMALL_SELECTED = new SpriteSheat(BUTTONS, 200, 15, 0, 125);

  public static final SpriteSheat BUTTON_LEFT_OFF = new SpriteSheat(BUTTONS, 10, 16, 204, 0);
  public static final SpriteSheat BUTTON_LEFT_NORMAL = new SpriteSheat(BUTTONS, 10, 16, 204, 16);
  public static final SpriteSheat BUTTON_LEFT_SELECTED = new SpriteSheat(BUTTONS, 10, 16, 204, 32);

  public static final SpriteSheat BUTTON_RIGHT_OFF = new SpriteSheat(BUTTONS, 10, 16, 214, 0);
  public static final SpriteSheat BUTTON_RIGHT_NORMAL = new SpriteSheat(BUTTONS, 10, 16, 214, 16);
  public static final SpriteSheat BUTTON_RIGHT_SELECTED = new SpriteSheat(BUTTONS, 10, 16, 214, 32);

  public static final SpriteSheat BUTTON_UNLOCK_OFF = new SpriteSheat(BUTTONS, 16, 16, 224, 0);
  public static final SpriteSheat BUTTON_UNLOCK_NORMAL = new SpriteSheat(BUTTONS, 16, 16, 224, 16);
  public static final SpriteSheat BUTTON_UNLOCK_SELECTED = new SpriteSheat(BUTTONS, 16, 16, 224, 32);

  public static final SpriteSheat BUTTON_LOCK_OFF = new SpriteSheat(BUTTONS, 16, 16, 240, 0);
  public static final SpriteSheat BUTTON_LOCK_NORMAL = new SpriteSheat(BUTTONS, 16, 16, 240, 16);
  public static final SpriteSheat BUTTON_LOCK_SELECTED = new SpriteSheat(BUTTONS, 16, 16, 240, 32);

  public static final SpriteSheat BUTTON_EMPTY_OFF = new SpriteSheat(BUTTONS, 10, 10, 226, 48);
  public static final SpriteSheat BUTTON_EMPTY_NORMAL = new SpriteSheat(BUTTONS, 10, 10, 236, 48);
  public static final SpriteSheat BUTTON_EMPTY_SELECTED = new SpriteSheat(BUTTONS, 10, 10, 246, 48);

  public SpriteSheat(ResourceLocation texture, int width, int height) {
    this(texture, width, height, 0, 0);
  }

  public void blit(GuiGraphics graphics, int x, int y) {
    graphics.blit(texture, x, y, u, v, width, height);
  }
}
