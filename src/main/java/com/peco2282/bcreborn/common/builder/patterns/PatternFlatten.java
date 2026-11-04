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

import com.peco2282.bcreborn.common.blueprint.BptBuilderTemplate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.peco2282.bcreborn.api.blueprints.SchematicMask;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import com.peco2282.bcreborn.common.Box;
import com.peco2282.bcreborn.common.blueprint.Template;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class PatternFlatten extends FillerPattern {
  public static final PatternFlatten INSTANCE = new PatternFlatten();
  public static final Codec<PatternFlatten> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    IStatementParameter.CODEC.listOf().fieldOf("parameters").forGetter(p -> Arrays.asList(p.parameters))
  ).apply(instance, PatternFlatten::new));

  private IStatementParameter[] parameters = new IStatementParameter[0];

  public PatternFlatten(List<IStatementParameter> parameters) {
    this();
    this.parameters = parameters.toArray(new IStatementParameter[0]);
  }

  public PatternFlatten() {
    super("flatten");
  }

  @Override
  public Codec<? extends FillerPattern> getCodec() {
    return CODEC;
  }

  /**
   * Original: the template reaches one layer below the box, which is filled so the area ends up flat.
   * The original assumed the world bottom was y = 0; this uses the level's minimum build height.
   */
  private static int bottomLayer(Box box, Level world) {
    return box.yMin > world.getMinBuildHeight() ? box.yMin - 1 : world.getMinBuildHeight();
  }

  @Override
  public Template getTemplate(Box box, Level world, IStatementParameter[] parameters) {
    int xMin = box.xMin;
    int yMin = bottomLayer(box, world);
    int zMin = box.zMin;

    int xMax = box.xMax;
    int yMax = box.yMax;
    int zMax = box.zMax;

    Template bpt = new Template(box.sizeX(), yMax - yMin + 1, box.sizeZ());

    if (box.yMin > world.getMinBuildHeight()) {
      for (int x = xMin; x <= xMax; ++x) {
        for (int z = zMin; z <= zMax; ++z) {
          bpt.put(x - xMin, 0, z - zMin, new SchematicMask(true));
        }
      }
    }

    return bpt;
  }

  @Override
  public BptBuilderTemplate getTemplateBuilder(Box box, Level world, IStatementParameter[] parameters) {
    return new BptBuilderTemplate(getTemplate(box, world, parameters), world, box.xMin, bottomLayer(box, world), box.zMin);
  }
}
