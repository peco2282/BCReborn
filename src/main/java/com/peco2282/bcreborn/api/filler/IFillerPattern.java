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
package com.peco2282.bcreborn.api.filler;

import com.peco2282.bcreborn.api.statements.IStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * Interface representing a pattern for the BuildCraft Filler.
 * Extends {@link IStatement}.
 */
public interface IFillerPattern extends IStatement {

  /**
   * Gets the texture sprite to be used as an overlay for blocks when this pattern is active.
   *
   * @return The {@link TextureAtlasSprite}.
   */
  TextureAtlasSprite getBlockOverlay();
}
