/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

/**
 * For the refinery, a kind of phantom slot for fluid.
 */
public class FluidSlot extends AdvancedSlot {
	public Fluid fluid;
	public int colorRenderCache;

	public FluidSlot(GuiAdvancedInterface<?> gui, int x, int y) {
		super(gui, x, y);
	}

	@Override
	public void drawSprite(GuiGraphics guiGraphics, int cornerX, int cornerY) {
		if (fluid != null) {
			// colorRenderCache はサブクラスや描画側で適用する
		}
		super.drawSprite(guiGraphics, cornerX, cornerY);
	}

	@Override
	public TextureAtlasSprite getIcon() {
		if (fluid == null) {
			return null;
		}
		IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
		ResourceLocation stillTexture = extensions.getStillTexture();
		return net.minecraft.client.Minecraft.getInstance()
				.getTextureAtlas(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS)
				.apply(stillTexture);
	}

	@Override
	public ResourceLocation getTexture() {
		return net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;
	}
}
