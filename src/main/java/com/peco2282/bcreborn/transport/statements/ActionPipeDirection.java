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
package com.peco2282.bcreborn.transport.statements;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.api.statements.IActionInternal;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementContainer;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.core.statements.BCStatement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.function.Function;

public class ActionPipeDirection extends BCStatement implements IActionInternal {

  public final Direction direction;

  public ActionPipeDirection(Direction direction) {
    super("buildcraft:pipe.dir." + direction.name().toLowerCase(Locale.ENGLISH));

    this.direction = direction;
  }

  @Override
  public String getDescription() {
    return direction.name().charAt(0) + direction.name().substring(1).toLowerCase(Locale.ENGLISH) + " Pipe Direction";
  }

  @Override
  public void registerIcons(Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
    icon = textureGetter.apply(BCRebornTransport.location("triggers/trigger_dir_" + direction.name().toLowerCase(Locale.ENGLISH)));
  }

  @Override
  public IStatement rotateLeft() {
    return this; // TODO: Implement rotation properly if needed
  }

  @Override
  public void actionActivate(IStatementContainer source,
                             IStatementParameter[] parameters) {

  }
}
