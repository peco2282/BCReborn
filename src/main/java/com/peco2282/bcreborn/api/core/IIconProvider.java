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
package com.peco2282.bcreborn.api.core;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public interface IIconProvider {

	/**
	 * @param iconIndex
	 */
	@OnlyIn(Dist.CLIENT)
	TextureAtlasSprite getIcon(int iconIndex);

	/**
	 * A call for the provider to register its Icons. This may be called multiple times but should only be executed once per provider
	 * @param textureGetter
	 */
	@OnlyIn(Dist.CLIENT)
	void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter);

}
