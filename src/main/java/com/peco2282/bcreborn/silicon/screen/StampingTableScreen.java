/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.silicon.screen;

import com.peco2282.bcreborn.silicon.block.entity.StampingTableBlockEntity;
import com.peco2282.bcreborn.silicon.menu.StampingTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StampingTableScreen extends LaserTableScreen<StampingTableMenu> {

	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("bcrebornsilicon", "textures/gui/stamper.png");

	public StampingTableScreen(StampingTableMenu container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title, (StampingTableBlockEntity) container.getSlot(0).container);
		this.imageWidth = 176;
		this.imageHeight = 151;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected ResourceLocation getMenuTexture() {
		return TEXTURE;
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
		if (table.getEnergy() > 0) {
			int progress = table.getProgressScaled(98);
			// フラッシュアニメーションのロジックは簡略化
			guiGraphics.blit(TEXTURE, x + 36, y + 14, 0, 221, progress, 24);
		}
	}
}
