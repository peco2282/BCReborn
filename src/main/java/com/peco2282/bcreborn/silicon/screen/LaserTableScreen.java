/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.silicon.screen;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.silicon.block.entity.LaserTableBaseBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public abstract class LaserTableScreen<M extends BuildCraftMenu<M>> extends BuildCraftScreen<M> {

	protected final LaserTableBaseBlockEntity table;

	public LaserTableScreen(M container, Inventory playerInventory, Component title, LaserTableBaseBlockEntity table) {
		super(container, playerInventory, title);
		this.table = table;
	}

	@Override
	protected void initilaizeLedger(Inventory playerInventory) {
		// 1.20.1 では Ledger システムが BuildCraftScreen にある
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
		guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
	}
}
