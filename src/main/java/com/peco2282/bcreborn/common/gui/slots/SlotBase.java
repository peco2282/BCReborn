/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui.slots;


import com.peco2282.bcreborn.common.gui.tooltips.IToolTipProvider;
import com.peco2282.bcreborn.common.gui.tooltips.ToolTip;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class SlotBase extends Slot implements IToolTipProvider {

	private ToolTip toolTips;

	public SlotBase(Container iinventory, int slotIndex, int posX, int posY) {
		super(iinventory, slotIndex, posX, posY);
	}

	public boolean canShift() {
		return true;
	}

	/**
	 * @return the toolTips
	 */
	@Override
	public ToolTip getToolTip() {
		return toolTips;
	}

	/**
	 * @param toolTips the tooltips to set
	 */
	public void setToolTips(ToolTip toolTips) {
		this.toolTips = toolTips;
	}

	@Override
	public boolean isToolTipVisible() {
		return getItem().isEmpty();
	}

	@Override
	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + 16 && mouseY >= y && mouseY <= y + 16;
	}
}
