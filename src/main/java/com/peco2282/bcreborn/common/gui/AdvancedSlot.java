/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public abstract class AdvancedSlot {
	private static final ResourceLocation TEXTURE_SLOT = BCRebornCore.location(
			"textures/gui/slot.png");
	public int x, y;
	public BuildCraftScreen<?> gui;
	public boolean drawBackround = false;

	public AdvancedSlot(GuiAdvancedInterface gui, int x, int y) {
		this.x = x;
		this.y = y;
		this.gui = gui;
	}

	public String getDescription() {
		return null;
	}

	public final void drawTooltip(GuiAdvancedInterface gui, GuiGraphics guiGraphics, int x, int y) {
		String desc = getDescription();
		if (desc != null) {
			gui.drawTooltip(guiGraphics, desc, x, y);
		} else {
			ItemStack stack = getItemStack();
			if (stack != null && !stack.isEmpty()) {
				int cornerX = (gui.width - gui.getXSize()) / 2;
				int cornerY = (gui.height - gui.getYSize()) / 2;
				int xS = x - cornerX;
				int yS = y - cornerY;
				gui.renderTooltip(guiGraphics, stack, xS, yS);
			}
		}
	}

	public TextureAtlasSprite getIcon() {
		return null;
	}

	public ResourceLocation getTexture() {
		return null;
	}

	public ItemStack getItemStack() {
		return null;
	}

	public boolean isDefined() {
		return true;
	}

	public void drawSprite(GuiGraphics guiGraphics, int cornerX, int cornerY) {
		if (drawBackround) {
			guiGraphics.blit(TEXTURE_SLOT, cornerX + x - 1, cornerY + y - 1, 0, 0, 18, 18);
		}
		if (!isDefined()) {
			return;
		}
		ItemStack stack = getItemStack();
		if (stack != null && !stack.isEmpty()) {
			drawStack(guiGraphics, stack);
		} else if (getIcon() != null) {
			ResourceLocation texture = getTexture();
			if (texture != null) {
				RenderSystem.setShaderTexture(0, texture);
			}
			guiGraphics.blit(cornerX + x, cornerY + y, 0, 16, 16, getIcon());
		}
	}

	public void drawStack(GuiGraphics guiGraphics, ItemStack item) {
		int cornerX = (gui.width - gui.getXSize()) / 2;
		int cornerY = (gui.height - gui.getYSize()) / 2;
		((GuiAdvancedInterface<?>) gui).drawStack(guiGraphics, item, cornerX + x, cornerY + y);
	}

	public boolean shouldDrawHighlight() {
		return true;
	}
}
