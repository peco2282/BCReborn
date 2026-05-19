package com.peco2282.bcreborn.common.builder.schematics;

import com.google.common.collect.Sets;
import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import com.peco2282.bcreborn.api.core.BlockIndex;
import net.minecraft.core.Direction;

import java.util.Set;

public class SchematicWallSide extends SchematicBlock {
    @Override
    public Set<BlockIndex> getPrerequisiteBlocks(IBuilderContext context) {
        final int yPos = 0;
        final int yNeg = 5;
        final int xPos = 2;
        final int xNeg = 1;
        final int zPos = 4;
        final int zNeg = 3;

        switch (facing.get3DDataValue() & 7) {
            case xPos -> {
                return Sets.newHashSet(
                        RELATIVE_INDEXES[Direction.EAST.get3DDataValue()]
                );
            }

            case xNeg -> {
                return Sets.newHashSet(
                        RELATIVE_INDEXES[Direction.WEST.get3DDataValue()]
                );
            }
            case 7, yPos -> {
                return Sets.newHashSet(
                        RELATIVE_INDEXES[Direction.UP.get3DDataValue()]
                );
            }
            case 6, yNeg -> {
                return Sets.newHashSet(
                        RELATIVE_INDEXES[Direction.DOWN.get3DDataValue()]
                );
            }
            case zPos -> {
                return Sets.newHashSet(
                        RELATIVE_INDEXES[Direction.SOUTH.get3DDataValue()]
                );
            }
            case zNeg -> {
                return Sets.newHashSet(
                        RELATIVE_INDEXES[Direction.NORTH.get3DDataValue()]
                );
            }

        }
        return Sets.newHashSet();
    }
}
