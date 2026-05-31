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
package com.peco2282.bcreborn.common.gui.widgets;


import net.minecraft.client.gui.GuiGraphics;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;

public class ButtonWidget extends Widget {

	private boolean pressed;
	private int buttonPressed;

	public ButtonWidget(int x, int y, int u, int v, int w, int h) {
		super(x, y, u, v, w, h);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, BuildCraftScreen<?> gui, int guiX, int guiY, int mouseX, int mouseY) {
		int vv = pressed ? v + h : v;
		gui.drawTexturedModalRect(guiGraphics, guiX + x, guiY + y, u, vv, w, h);
	}

	@Override
	public final boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		pressed = true;
		buttonPressed = mouseButton;
		onPress(buttonPressed);
		return true;
	}

	@Override
	public final void handleMouseRelease(int mouseX, int mouseY, int eventType) {
		if (pressed) {
			pressed = false;
			onRelease(buttonPressed);
		}
	}

	@Override
	public final void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
		if (pressed && !isMouseOver(mouseX, mouseY)) {
			pressed = false;
			onRelease(buttonPressed);
		}
	}

	public void onPress(int mouseButton) {
	}

	public void onRelease(int mouseButton) {
	}
}
