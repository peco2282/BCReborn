/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
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
