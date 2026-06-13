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
package com.peco2282.bcreborn.common.gui;

import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.common.screen.AdvancedInterfaceScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

/**
 * Created by asie on 1/24/15.
 */
public abstract class StatementSlot extends AdvancedSlot {
  public int slot;
  public ArrayList<StatementParameterSlot> parameters = new ArrayList<>();

  public StatementSlot(AdvancedInterfaceScreen<?> gui, int x, int y, int slot) {
    super(gui, x, y);

    this.slot = slot;
  }

  @Override
  public String getDescription() {
    IStatement stmt = getStatement();

    if (stmt != null) {
      return stmt.getDescription();
    } else {
      return "";
    }
  }

  @OnlyIn(Dist.CLIENT)
  @Override
  public TextureAtlasSprite getIcon() {
    IStatement stmt = getStatement();

    if (stmt != null) {
      return stmt.getIcon();
    } else {
      return null;
    }
  }

  @Override
  public boolean isDefined() {
    return getStatement() != null;
  }

  public abstract IStatement getStatement();

  @Override
  public boolean shouldDrawHighlight() {
    return false;
  }
}
