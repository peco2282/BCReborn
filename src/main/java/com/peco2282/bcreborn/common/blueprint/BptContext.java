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

import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.MappingRegistry;
import com.peco2282.bcreborn.api.core.IBox;
import com.peco2282.bcreborn.api.core.Position;
import com.peco2282.bcreborn.common.Box;
import net.minecraft.world.level.Level;

public class BptContext implements IBuilderContext {

    public BlueprintReadConfiguration readConfiguration;
    public Box box;
    public Level world;
    private final MappingRegistry mappingRegistry;

    public BptContext(Level world, Box box, MappingRegistry registry) {
        this.world = world;
        this.box = box;
        this.mappingRegistry = registry;
    }

    @Override
    public Position rotatePositionLeft(Position pos) {
        return new Position((box.sizeZ() - 1) - pos.z, pos.y, pos.x);
    }

    @Override
    public IBox surroundingBox() {
        return box;
    }

    @Override
    public Level world() {
        return world;
    }

    public void rotateLeft() {
        box = box.rotateLeft();
    }

    @Override
    public MappingRegistry getMappingRegistry() {
        return mappingRegistry;
    }
}
