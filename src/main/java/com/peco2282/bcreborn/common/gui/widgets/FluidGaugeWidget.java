/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui.widgets;


import com.peco2282.bcreborn.common.gui.tooltips.ToolTipLine;
import com.peco2282.bcreborn.common.gui.tooltips.ToolTip;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.energy.fluids.Tank;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class FluidGaugeWidget extends Widget {

	public final Tank tank;

	public FluidGaugeWidget(Tank tank, int x, int y, int u, int v, int w, int h) {
		super(x, y, u, v, w, h);
		this.tank = tank;
	}

	@Override
	public ToolTip getToolTip() {
		return new ToolTip(new ToolTipLine(tank.getToolTip().getString()));
	}

	@Override
	public void draw(net.minecraft.client.gui.GuiGraphics guiGraphics, BuildCraftScreen<?> gui, int guiX, int guiY, int mouseX, int mouseY) {
		if (tank == null) {
			return;
		}
		FluidStack fluidStack = tank.getFluid();
		if (fluidStack == null || fluidStack.getAmount() <= 0 || fluidStack.getFluid() == null) {
			return;
		}

		// Simplified for now, complex fluid rendering needs specialized helper
		int scale = (int) (h * (Math.min(fluidStack.getAmount(), tank.getCapacity()) / (float) tank.getCapacity()));
        // gui.drawFluid(guiGraphics, guiX + x, guiY + y + h - scale, fluidStack, w, scale);
        
		gui.drawTexturedModalRect(guiGraphics, guiX + x, guiY + y, u, v, w, h);
	}

}
