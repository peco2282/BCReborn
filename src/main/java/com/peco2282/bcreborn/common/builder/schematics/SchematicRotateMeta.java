package com.peco2282.bcreborn.common.builder.schematics;

import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.level.block.Rotation;

public class SchematicRotateMeta extends SchematicTile {
    public SchematicRotateMeta() {
    }

    @Override
    public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
        if (state != null) {
            return block == context.world().getBlockState(new BlockPos(x, y, z)).getBlock();
        }
        return block == context.world().getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    @Override
    public void rotateLeft(IBuilderContext context) {
        if (state != null) {
            state = state.rotate(Rotation.COUNTERCLOCKWISE_90);
        }
    }
}
