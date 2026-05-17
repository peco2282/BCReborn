/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.gates;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Function;

public interface IGateExpansion {
    String getUniqueIdentifier();

    String getDisplayName();

    GateExpansionController makeController(BlockEntity pipeTile);

    void registerBlockOverlay(Function<ResourceLocation, TextureAtlasSprite> textureGetter);

    void registerItemOverlay(Function<ResourceLocation, TextureAtlasSprite> textureGetter);

    TextureAtlasSprite getOverlayBlock();

    TextureAtlasSprite getOverlayItem();
}
