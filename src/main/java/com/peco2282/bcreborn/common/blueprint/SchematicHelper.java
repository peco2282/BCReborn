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
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.blueprints.ISchematicHelper;
import com.peco2282.bcreborn.common.inventory.StackHelper;
import net.minecraft.world.item.ItemStack;

public final class SchematicHelper implements ISchematicHelper {
  public static final SchematicHelper INSTANCE = new SchematicHelper();

  private SchematicHelper() {

  }

  @Override
  public boolean isEqualItem(ItemStack a, ItemStack b) {
    return StackHelper.isEqualItem(a, b);
  }
}
