package com.peco2282.bcreborn.common.builder.schematics;

import com.peco2282.bcreborn.api.blueprints.IBuilderContext;
import com.peco2282.bcreborn.api.blueprints.SchematicTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class SchematicRotateMeta extends SchematicTile {
    int[] rot;
    boolean rotateForward;
    int infoMask = 0;

    public SchematicRotateMeta() {
        this(new int[] {0, 4, 8, 12}, true);
    }

    public SchematicRotateMeta(int[] rot, boolean rotateForward) {
        this.rot = rot;

        for (int element: rot) {
            if (element < 4) {
                infoMask = Math.max(infoMask, 3);
            } else if (element < 8) {
                infoMask = Math.max(infoMask, 7);
            } else if (element < 16) {
                infoMask = Math.max(infoMask, 15);
            }
        }

        this.rotateForward = rotateForward;
    }

    @Override
    public boolean isAlreadyBuilt(IBuilderContext context, int x, int y, int z) {
        return block == context.world().getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    @Override
    public void rotateLeft(IBuilderContext context) {
        int pos = facing.get2DDataValue() & infoMask;
        int others = facing.get2DDataValue() - pos;

        if (rotateForward) {
            if (pos == rot[0]) {
                pos = rot[1];
            } else if (pos == rot[1]) {
                pos = rot[2];
            } else if (pos == rot[2]) {
                pos = rot[3];
            } else if (pos == rot[3]) {
                pos = rot[0];
            }
        } else {
            if (pos == rot[0]) {
                pos = rot[3];
            } else if (pos == rot[1]) {
                pos = rot[2];
            } else if (pos == rot[2]) {
                pos = rot[0];
            } else if (pos == rot[3]) {
                pos = rot[1];
            }
        }
        facing = Direction.from2DDataValue(pos + others);
    }
}
