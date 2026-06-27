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
package com.peco2282.bcreborn.common.config;

import com.peco2282.bcreborn.common.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;

public class BCRebornConfigTab extends GridLayoutTab {
  public static final BCRebornConfigTab[] TABS = new BCRebornConfigTab[]{
    new BCRebornConfigTab(ConfigModule.GENERAL),
    new BCRebornConfigTab(ConfigModule.CORE),
    new BCRebornConfigTab(ConfigModule.BUILDER),
    new BCRebornConfigTab(ConfigModule.ENERGY),
    new BCRebornConfigTab(ConfigModule.FACTORY),
    new BCRebornConfigTab(ConfigModule.ROBOTICS),
    new BCRebornConfigTab(ConfigModule.SILICON),
    new BCRebornConfigTab(ConfigModule.TRANSPORT),
  };

  private final ConfigModule module;
  private ConfigSelectionList list;

  public BCRebornConfigTab(ConfigModule module) {
    super(module.module().withStyle(style -> style.withFont(Fonts.MICHROMA)));
    this.module = module;
  }

  public ConfigSelectionList getList(Minecraft minecraft, int width, int height, int y0, int y1) {
    if (this.list == null) {
      this.list = new ConfigSelectionList(minecraft, width, height, y0, y1);
      for (ConfigSection section : module.sections()) {
        this.list.addSection(section);
      }
    } else {
      this.list.updateSize(width, height, y0, y1);
    }
    return this.list;
  }
}
