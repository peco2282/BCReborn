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
package com.peco2282.bcreborn.factory.screen;

import com.peco2282.bcreborn.BCRebornFactory;
import com.peco2282.bcreborn.common.screen.BuildCraftScreen;
import com.peco2282.bcreborn.factory.menu.HopperMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class HopperScreen extends BuildCraftScreen<HopperMenu> {
    public static final ResourceLocation TEXTURE = BCRebornFactory.location("textures/gui/hopper.png");

    public HopperScreen(HopperMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 153;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void initilaizeLedger(Inventory p_97742_) {
    }

    @Override
    protected ResourceLocation getMenuTexture() {
        return TEXTURE;
    }

    @Override
    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {
        p_281635_.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        p_281635_.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics p_283065_, float p_97788_, int p_97789_, int p_97790_) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        p_283065_.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }
}
