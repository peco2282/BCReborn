package com.peco2282.bcreborn.common.builder.schematics;

import com.google.common.collect.Sets;
import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.core.BlockIndex;
import net.minecraft.core.Direction;

import java.util.Set;

public class SchematicRotateMetaSupported extends SchematicRotateMeta {

    public SchematicRotateMetaSupported() {
        super();
    }

    public SchematicRotateMetaSupported(int[] rot, boolean rotateForward) {
        super(rot, rotateForward);
    }

    @Override
    public Set<BlockIndex> getPrerequisiteBlocks(IBuilderContext context) {
        int pos = facing.get2DDataValue() & infoMask;
        if (pos == rot[0]) {
            return Sets.newHashSet(RELATIVE_INDEXES[Direction.NORTH.ordinal()]);
        } else if (pos == rot[1]) {
            return Sets.newHashSet(RELATIVE_INDEXES[Direction.EAST.ordinal()]);
        } else if (pos == rot[2]) {
            return Sets.newHashSet(RELATIVE_INDEXES[Direction.SOUTH.ordinal()]);
        } else if (pos == rot[3]) {
            return Sets.newHashSet(RELATIVE_INDEXES[Direction.WEST.ordinal()]);
        }
        return Sets.newHashSet();
    }
}
