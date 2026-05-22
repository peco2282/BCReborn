/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui.buttons;


import com.peco2282.bcreborn.common.gui.tooltips.ToolTip;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class GuiMultiButton extends GuiBetterButton {

	private final MultiButtonController<?> control;

	public GuiMultiButton(int id, int x, int y, int width, MultiButtonController<?> control) {
		super(id, x, y, width, StandardButtonTextureSets.LARGE_BUTTON, "");
		this.control = control;
	}

	@Override
	public int getHeight() {
		return texture.getHeight();
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (!isActive()) {
			return;
		}

		Font fontrenderer = Minecraft.getInstance().font;
		IMultiButtonState state = control.getButtonState();
		IButtonTextureSet tex = state.getTextureSet();
		int xOffset = tex.getX();
		int yOffset = tex.getY();
		int h = tex.getHeight();
		int w = tex.getWidth();
		boolean flag = isMouseOver(mouseX, mouseY);

		guiGraphics.blit(tex.getTexture(), getX(), getY(), xOffset, yOffset, width / 2, h);
		guiGraphics.blit(tex.getTexture(), getX() + width / 2, getY(), xOffset + w - width / 2, yOffset, width / 2, h);

		String label = state.getLabel();
		if (label != null && !label.isEmpty()) {
			guiGraphics.drawCenteredString(fontrenderer, Component.literal(label), getX() + width / 2, getY() + (h - 8) / 2, getTextColor(flag));
		}
	}

	@Override
	public void onClick(double mouseX, double mouseY) {
		if (isActive()) {
			control.incrementState();
		}
	}

	public MultiButtonController<?> getController() {
		return control;
	}

	@Override
	public ToolTip getToolTip() {
		ToolTip tip = this.control.getButtonState().getToolTip();
		if (tip != null) {
			return tip;
		}
		return super.getToolTip();
	}
}
