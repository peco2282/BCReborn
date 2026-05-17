/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * The BuildCraft API is distributed under the terms of the MIT License.
 * Please check the contents of the license, which should be located
 * as "LICENSE.API" in the BuildCraft source code distribution.
 */
package com.peco2282.bcreborn.api.filler;

import com.peco2282.bcreborn.api.statements.IStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface IFillerPattern extends IStatement {
    TextureAtlasSprite getBlockOverlay();
}
