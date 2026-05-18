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
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.blueprint.Box;
import com.peco2282.bcreborn.common.blueprint.Template;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class PatternClear extends FillerPattern {
    public static final PatternClear INSTANCE = new PatternClear();
    public static final Codec<PatternClear> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IStatementParameter.CODEC.listOf().fieldOf("parameters").forGetter(p -> Arrays.asList(p.parameters))
    ).apply(instance, PatternClear::new));

    private IStatementParameter[] parameters = new IStatementParameter[0];

    public PatternClear(List<IStatementParameter> parameters) {
        this();
        this.parameters = parameters.toArray(new IStatementParameter[0]);
    }

    public PatternClear() {
        super("clear");
    }

    @Override
    public Codec<? extends FillerPattern> getCodec() {
        return CODEC;
    }

    @Override
    public Template getTemplate(Box box, Level world, IStatementParameter[] parameters) {
        int xMin = (int) box.pMin().x;
        int yMin = (int) box.pMin().y;
        int zMin = (int) box.pMin().z;

        int xMax = (int) box.pMax().x;
        int yMax = (int) box.pMax().y;
        int zMax = (int) box.pMax().z;

        return new Template(xMax - xMin + 1, yMax - yMin + 1, zMax - zMin + 1);
    }
}
