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


import com.peco2282.bcreborn.BCRebornCore;
import net.minecraft.resources.ResourceLocation;

public enum StandardButtonTextureSets implements IButtonTextureSet {
	LARGE_BUTTON(0, 0, 20, 200),
	SMALL_BUTTON(0, 80, 15, 200),
	LEFT_BUTTON(204, 0, 16, 10),
	RIGHT_BUTTON(214, 0, 16, 10);
	public static final ResourceLocation BUTTON_TEXTURES = BCRebornCore.location("textures/gui/buttons.png");
	private final int x, y, height, width;

	StandardButtonTextureSets(int x, int y, int height, int width) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
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
		return BUTTON_TEXTURES;
	}
}
