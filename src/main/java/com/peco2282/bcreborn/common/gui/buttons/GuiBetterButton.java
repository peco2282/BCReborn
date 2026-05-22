/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui.buttons;

import com.peco2282.bcreborn.common.gui.tooltips.IToolTipProvider;
import com.peco2282.bcreborn.common.gui.tooltips.ToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class GuiBetterButton extends Button implements IToolTipProvider {
	protected final IButtonTextureSet texture;
	private ToolTip toolTip;

	public GuiBetterButton(int id, int x, int y, String label) {
		this(id, x, y, 200, StandardButtonTextureSets.LARGE_BUTTON, label);
	}

	public GuiBetterButton(int id, int x, int y, int width, String label) {
		this(id, x, y, width, StandardButtonTextureSets.LARGE_BUTTON, label);
	}

	public GuiBetterButton(int id, int x, int y, int width, IButtonTextureSet texture, String label) {
		super(x, y, width, texture.getHeight(), Component.literal(label), (b) -> {}, Button.DEFAULT_NARRATION);
		this.texture = texture;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return texture.getHeight();
	}

	public int getTextColor(boolean mouseOver) {
		if (!visible) {
			return 0xffa0a0a0;
		} else if (mouseOver) {
			return 0xffffa0;
		} else {
			return 0xe0e0e0;
		}
	}

	public boolean isMouseOverButton(int mouseX, int mouseY) {
		return mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + getHeight();
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (!visible) {
			return;
		}

		Font fontrenderer = Minecraft.getInstance().font;
		guiGraphics.blit(texture.getTexture(), getX(), getY(), texture.getX(), texture.getY(), width / 2, texture.getHeight());
		guiGraphics.blit(texture.getTexture(), getX() + width / 2, getY(), texture.getX() + texture.getWidth() - width / 2, texture.getY(), width / 2, texture.getHeight());

		guiGraphics.drawCenteredString(fontrenderer, getMessage(), getX() + width / 2, getY() + (texture.getHeight() - 8) / 2, getTextColor(isMouseOver(mouseX, mouseY)));
	}

	@Override
	public ToolTip getToolTip() {
		return toolTip;
	}

	public GuiBetterButton setToolTip(ToolTip tips) {
		this.toolTip = tips;
		return this;
	}

	@Override
	public boolean isToolTipVisible() {
		return visible;
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		return isMouseOverButton(mouseX, mouseY);
	}
}
