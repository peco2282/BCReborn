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
