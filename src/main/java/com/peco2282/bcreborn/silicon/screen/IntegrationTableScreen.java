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
package com.peco2282.bcreborn.silicon.screen;

import com.peco2282.bcreborn.silicon.block.entity.IntegrationTableBlockEntity;
import com.peco2282.bcreborn.silicon.menu.IntegrationTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class IntegrationTableScreen extends LaserTableScreen<IntegrationTableMenu> {

	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("bcrebornsilicon", "textures/gui/integration_table.png");

	public IntegrationTableScreen(IntegrationTableMenu container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title, (IntegrationTableBlockEntity) container.getSlot(0).container);
		this.imageWidth = 176;
		this.imageHeight = 186;
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
			int h = table.getProgressScaled(69);
			guiGraphics.blit(TEXTURE, x + 164, y + 18 + 74 - h, 176, 18, 4, h);
		}
		// 1.7.10 の拡張制限描画ロジックの移植は BlockEntity 側の実装を待つ
	}
}
