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
package com.peco2282.bcreborn.builders.schematics;

import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.common.builder.schematics.SchematicBlockFloored;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;

import java.util.LinkedList;

public class SchematicRedstoneDiode extends SchematicBlockFloored {
  private final Item baseItem;

  public SchematicRedstoneDiode(Item baseItem) {
    this.baseItem = baseItem;
  }

  @Override
  public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
    requirements.add(new ItemStack(baseItem));
  }

  @Override
  public void storeRequirements(IBuilderContext context, int x, int y, int z) {

  }

  @Override
  public void rotateLeft(IBuilderContext context) {
    state = state.rotate(Rotation.COUNTERCLOCKWISE_90);
  }
}
