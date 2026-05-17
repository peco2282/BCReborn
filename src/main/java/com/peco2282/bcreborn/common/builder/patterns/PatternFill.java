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

public final class PatternFill extends FillerPattern {

    public static final PatternFill INSTANCE = new PatternFill();

    private PatternFill() {
        super("fill");
    }

    @Override
    public Template getTemplate(Box box, Level world, IStatementParameter[] parameters) {
        Template bpt = new Template(box.sizeX(), box.sizeY(), box.sizeZ());
        fill(0, 0, 0, box.sizeX() - 1, box.sizeY() - 1, box.sizeZ() - 1, bpt);
        return bpt;
    }
}
