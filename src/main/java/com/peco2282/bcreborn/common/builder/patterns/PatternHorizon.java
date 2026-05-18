/**
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package com.peco2282.bcreborn.common.builder.patterns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.peco2282.bcreborn.api.blueprints.SchematicMask;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.blueprint.Box;
import com.peco2282.bcreborn.common.blueprint.Template;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class PatternHorizon extends FillerPattern {
    public static final PatternHorizon INSTANCE = new PatternHorizon();
    public static final Codec<PatternHorizon> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IStatementParameter.CODEC.listOf().fieldOf("parameters").forGetter(p -> Arrays.asList(p.parameters))
    ).apply(instance, PatternHorizon::new));

    private IStatementParameter[] parameters = new IStatementParameter[0];

    public PatternHorizon(List<IStatementParameter> parameters) {
        this();
        this.parameters = parameters.toArray(new IStatementParameter[0]);
    }

    public PatternHorizon() {
        super("horizon");
    }

    @Override
    public Codec<? extends FillerPattern> getCodec() {
        return CODEC;
    }

    @Override
    public Template getTemplate(Box box, Level world, IStatementParameter[] parameters) {
        int xMin = (int) box.pMin().x;
        int yMin = box.pMin().y > 0 ? (int) box.pMin().y - 1 : 0;
        int zMin = (int) box.pMin().z;

        int xMax = (int) box.pMax().x;
        int yMax = world.getMaxBuildHeight();
        int zMax = (int) box.pMax().z;

        Template bpt = new Template(box.sizeX(), yMax - yMin + 1, box.sizeZ());

        if (box.sizeY() > 0) {
            for (int x = xMin; x <= xMax; ++x) {
                for (int z = zMin; z <= zMax; ++z) {
                    bpt.put(x - xMin, 0, z - zMin, new SchematicMask(true));
                }
            }
        }

        return bpt;
    }
}
