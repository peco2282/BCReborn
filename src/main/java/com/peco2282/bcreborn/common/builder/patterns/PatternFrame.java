/*
 * BC Reborn
 *
 * Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
 * Copyright (c) 2025-2026 peco2282
 *
 * Contains original work and code derived from BuildCraft.
 *
 * Licensed under the Minecraft Mod Public License 1.0 (MMPL).
 * See LICENSE for details.
 */
package com.peco2282.bcreborn.common.builder.patterns;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.peco2282.bcreborn.api.blueprints.SchematicMask;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.blueprint.Template;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class PatternFrame extends FillerPattern {
  public static final PatternFrame INSTANCE = new PatternFrame();
  public static final Codec<PatternFrame> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    IStatementParameter.CODEC.listOf().fieldOf("parameters").forGetter(p -> Arrays.asList(p.parameters))
  ).apply(instance, PatternFrame::new));

  private IStatementParameter[] parameters = new IStatementParameter[0];

  public PatternFrame(List<IStatementParameter> parameters) {
    this();
    this.parameters = parameters.toArray(new IStatementParameter[0]);
  }

  public PatternFrame() {
    super("frame");
  }

  @Override
  public Codec<? extends FillerPattern> getCodec() {
    return CODEC;
  }

  @Override
  public Template getTemplate(Box box, Level world, IStatementParameter[] parameters) {
    Template template = new Template(box.sizeX(), box.sizeY(), box.sizeZ());

    int xMax = box.sizeX() - 1;
    int zMax = box.sizeZ() - 1;

    for (int it = 0; it < 2; it++) {
      int y = it * (box.sizeY() - 1);
      for (int i = 0; i < template.sizeX; ++i) {
        template.put(i, y, 0, new SchematicMask(true));
        template.put(i, y, zMax, new SchematicMask(true));
      }
      for (int k = 0; k < template.sizeZ; ++k) {
        template.put(0, y, k, new SchematicMask(true));
        template.put(xMax, y, k, new SchematicMask(true));
      }
    }

    for (int h = 1; h < box.sizeY(); ++h) {
      template.put(0, h, 0, new SchematicMask(true));
      template.put(0, h, zMax, new SchematicMask(true));
      template.put(xMax, h, 0, new SchematicMask(true));
      template.put(xMax, h, zMax, new SchematicMask(true));
    }

    return template;
  }
}
