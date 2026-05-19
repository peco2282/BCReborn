package com.peco2282.bcreborn.common.builder.schematics;


import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;

public class SchematicIgnoreMeta extends SchematicBlock {
    @Override
    public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
        requirements.add(new ItemStack(block, 1));
    }

    @Override
    public void storeRequirements(IBuilderContext context, int x, int y, int z) {
    }

    @Override
    public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
        return block == context.world().getBlockState(new BlockPos(x, y, z)).getBlock();
    }
}
