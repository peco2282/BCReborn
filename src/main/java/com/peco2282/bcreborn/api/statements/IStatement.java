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
