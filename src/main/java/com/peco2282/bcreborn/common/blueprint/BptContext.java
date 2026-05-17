/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.blueprint;

import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.MappingRegistry;
import com.peco2282.bcreborn.api.core.IBox;
import com.peco2282.bcreborn.api.core.Position;
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
