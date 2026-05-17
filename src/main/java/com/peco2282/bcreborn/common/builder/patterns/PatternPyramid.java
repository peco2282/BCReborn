/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.builder.patterns;

import com.peco2282.bcreborn.api.blueprints.SchematicMask;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.blueprint.Box;
import com.peco2282.bcreborn.common.blueprint.Template;
import net.minecraft.world.level.Level;

public class PatternPyramid extends FillerPattern {

    private static final int[] MODIFIERS = {
            0x0101, 0x1101, 0x1001,
            0x0111, 0x1111, 0x1011,
            0x0110, 0x1110, 0x1010
    };

    public PatternPyramid() {
        super("pyramid");
    }

    @Override
    public int maxParameters() {
        return 2;
    }

    @Override
    public int minParameters() {
        return 2;
    }

    @Override
    public IStatementParameter createParameter(int index) {
        return index == 1 ? new PatternParameterCenter(4) : new PatternParameterYDir(true);
    }

    @Override
    public Template getTemplate(Box box, Level world, IStatementParameter[] parameters) {
        int xMin = (int) box.pMin().x;
        int yMin = (int) box.pMin().y;
        int zMin = (int) box.pMin().z;

        int xMax = (int) box.pMax().x;
        int yMax = (int) box.pMax().y;
        int zMax = (int) box.pMax().z;

        Template bpt = new Template(xMax - xMin + 1, yMax - yMin + 1, zMax - zMin + 1);

        int[] modifiers = new int[4];
        int height;
        int stepY;

        if (parameters.length >= 1 && parameters[0] != null && !(((PatternParameterYDir) parameters[0]).up)) {
            stepY = -1;
        } else {
            stepY = 1;
        }

        int center = 4;
        if (parameters.length >= 2 && parameters[1] != null) {
            center = ((PatternParameterCenter) parameters[1]).getDirection();
        }

        modifiers[0] = (MODIFIERS[center] >> 12) & 1;
        modifiers[1] = (MODIFIERS[center] >> 8) & 1;
        modifiers[2] = (MODIFIERS[center] >> 4) & 1;
        modifiers[3] = (MODIFIERS[center]) & 1;

        if (stepY == 1) {
            height = yMin;
        } else {
            height = yMax;
        }

        int x1 = xMin, x2 = xMax, z1 = zMin, z2 = zMax;

        while (height >= yMin && height <= yMax) {
            for (int x = x1; x <= x2; ++x) {
                for (int z = z1; z <= z2; ++z) {
                    bpt.put(x - xMin, height - yMin, z - zMin, new SchematicMask(true));
                }
            }

            x1 += modifiers[0];
            x2 -= modifiers[1];
            z1 += modifiers[2];
            z2 -= modifiers[3];
            height += stepY;

            if (x1 > x2 || z1 > z2) {
                break;
            }
        }

        return bpt;
    }
}
