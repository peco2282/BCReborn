/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui.widgets;


import com.peco2282.bcreborn.common.gui.tooltips.IToolTipProvider;
import com.peco2282.bcreborn.common.gui.tooltips.ToolTip;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.DataInputStream;
import java.io.IOException;

public class Widget implements IToolTipProvider {

	public final int x;
	public final int y;
	public final int u;
	public final int v;
	public final int w;
	public final int h;
	public boolean hidden;
	protected BuildCraftMenu<?> container;

	public Widget(int x, int y, int u, int v, int w, int h) {
		this.x = x;
		this.y = y;
		this.u = u;
		this.v = v;
		this.w = w;
		this.h = h;
	}

	public void addToContainer(BuildCraftMenu<?> container) {
		this.container = container;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public final boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= x - 1 && mouseX < x + w + 1 && mouseY >= y - 1 && mouseY < y + h + 1;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleMouseRelease(int mouseX, int mouseY, int eventType) {
	}

	@OnlyIn(Dist.CLIENT)
	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClientPacketData(DataInputStream data) throws IOException {
	}

	@OnlyIn(Dist.CLIENT)
	public void draw(GuiGraphics guiGraphics, BuildCraftScreen<?> gui, int guiX, int guiY, int mouseX, int mouseY) {
		gui.drawTexturedModalRect(guiGraphics, guiX + x, guiY + y, u, v, w, h);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public ToolTip getToolTip() {
		return null;
	}

	@Override
	public boolean isToolTipVisible() {
		return true;
	}

	public void initWidget(ContainerListener player) {
	}

	public void updateWidget(ContainerListener player) {
	}
}
