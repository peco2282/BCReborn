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
package com.peco2282.bcreborn.common.builder.schematics;

import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;

public class SchematicIgnore extends SchematicBlock {
    @Override
    public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
    }

    @Override
    public void rotateLeft(IBuilderContext context) {
    }

    @Override
    public void initializeFromObjectAt(IBuilderContext context, int x, int y, int z) {
    }

    @Override
    public void placeInWorld(IBuilderContext context, int x, int y, int z, LinkedList<ItemStack> stacks) {
    }

    @Override
    public void storeRequirements(IBuilderContext context, int x, int y, int z) {
    }

    @Override
    public boolean doNotBuild() {
        return true;
    }
}
