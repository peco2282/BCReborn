/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui;


import com.peco2282.bcreborn.common.gui.buttons.GuiBetterButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;

import java.util.List;
import java.util.function.Supplier;

public final class GuiTools {

	/**
	 * Deactivate constructor
	 */
	private GuiTools() {
	}

	public static final Button.OnPress EMPTY_PRESS = (button) -> {
	};

	public static final Button.CreateNarration EMPTY_NARRATION = Supplier::get;

	public static void drawCenteredString(Font fr, String s, int y) {
		drawCenteredString(fr, s, y, 176);
	}

	public static void drawCenteredString(Font fr, String s, int y, int guiWidth) {
		drawCenteredString(fr, s, y, guiWidth, 0x404040, false);
	}

	public static void drawCenteredString(Font fr, String s, int y, int guiWidth, int color, boolean shadow) {
		// Use standard drawInBatch requires more params in 1.20.1, simplified
	}

	public static void newButtonRowAuto(List<GuiBetterButton> buttonList, int xStart, int xSize, List<? extends GuiBetterButton> buttons) {
		int buttonWidth = 0;
		for (GuiBetterButton b : buttons) {
			buttonWidth += b.getWidth();
		}
		int remaining = xSize - buttonWidth;
		int spacing = remaining / (buttons.size() + 1);
		int pointer = 0;
		for (GuiBetterButton b : buttons) {
			pointer += spacing;
			b.setX(xStart + pointer);
			pointer += b.getWidth();
			buttonList.add(b);
		}
	}

	public static void newButtonRow(List<GuiBetterButton> buttonList, int xStart, int spacing, List<? extends GuiBetterButton> buttons) {
		int pointer = 0;
		for (GuiBetterButton b : buttons) {
			b.setX(xStart + pointer);
			pointer += b.getWidth() + spacing;
			buttonList.add(b);
		}
	}
}
