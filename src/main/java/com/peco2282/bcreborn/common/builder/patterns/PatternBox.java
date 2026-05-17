/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.builder.patterns;

import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.blueprint.Box;
import com.peco2282.bcreborn.common.blueprint.Template;
import net.minecraft.world.level.Level;

public class PatternBox extends FillerPattern {

    public PatternBox() {
        super("box");
    }

    @Override
    public Template getTemplate(Box box, Level world, IStatementParameter[] parameters) {
        Template result = new Template(box.sizeX(), box.sizeY(), box.sizeZ());

        int xMin = 0;
        int yMin = 0;
        int zMin = 0;

        int xMax = box.sizeX() - 1;
        int yMax = box.sizeY() - 1;
        int zMax = box.sizeZ() - 1;

        fill(xMin, yMin, zMin, xMax, yMin, zMax, result);
        fill(xMin, yMin, zMin, xMin, yMax, zMax, result);
        fill(xMin, yMin, zMin, xMax, yMax, zMin, result);
        fill(xMax, yMin, zMin, xMax, yMax, zMax, result);
        fill(xMin, yMin, zMax, xMax, yMax, zMax, result);
        fill(xMin, yMax, zMin, xMax, yMax, zMax, result);

        return result;
    }
}
