/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui.widgets;


import net.minecraft.client.gui.GuiGraphics;
import com.peco2282.bcreborn.common.gui.tooltips.ToolTip;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IndicatorWidget extends Widget {

	public final IIndicatorController controller;

	public IndicatorWidget(IIndicatorController controller, int x, int y, int u, int v, int w, int h) {
		super(x, y, u, v, w, h);
		this.controller = controller;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void draw(GuiGraphics guiGraphics, BuildCraftScreen<?> gui, int guiX, int guiY, int mouseX, int mouseY) {
		int scale = controller.getScaledLevel(h);
		gui.drawTexturedModalRect(guiGraphics, guiX + x, guiY + y + h - scale, u, v + h - scale, w, scale);
	}

	@Override
	public ToolTip getToolTip() {
		return controller.getToolTip();
	}

}
