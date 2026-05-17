/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.statements;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

public interface IStatement {
    String getUniqueTag();

    @OnlyIn(Dist.CLIENT)
    TextureAtlasSprite getIcon();

    @OnlyIn(Dist.CLIENT)
    void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter);

    int maxParameters();

    int minParameters();

    String getDescription();

    IStatementParameter createParameter(int index);

    IStatement rotateLeft();
}
