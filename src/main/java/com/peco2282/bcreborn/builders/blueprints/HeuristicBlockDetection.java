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
package com.peco2282.bcreborn.builders.blueprints;

import com.peco2282.bcreborn.api.blueprints.SchematicBlock;
import com.peco2282.bcreborn.api.blueprints.SchematicFluid;
import com.peco2282.bcreborn.builders.schematics.SchematicStairs;
import com.peco2282.bcreborn.common.blueprint.SchematicRegistry;
import com.peco2282.bcreborn.common.builder.schematics.SchematicBlockEntityCreative;
import com.peco2282.bcreborn.common.builder.schematics.SchematicBlockFloored;
import com.peco2282.bcreborn.common.builder.schematics.SchematicWallSide;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Port of the original {@code HeuristicBlockDetection}: every block without a dedicated schematic gets a generic one,
 * so blueprints can record and build it.
 * <ul>
 *   <li>blocks with a block entity: creative only (to prevent duplication exploits)</li>
 *   <li>liquids: fluid schematic</li>
 *   <li>plants and pressure plates: floored; levers, torches and buttons: wall side; stairs: stairs</li>
 *   <li>everything else: the plain {@link SchematicBlock}</li>
 * </ul>
 * The original went through all 16 metadata values; the 1.20 registry falls back to the block's default state, so one
 * entry per block is enough.
 */
public final class HeuristicBlockDetection {
  private HeuristicBlockDetection() {
  }

  public static void start() {
    SchematicRegistry registry = SchematicRegistry.INSTANCE;
    for (Block block : ForgeRegistries.BLOCKS) {
      if (block == null || block == Blocks.AIR || registry.isSupported(block.defaultBlockState())) {
        continue;
      }
      try {
        if (block instanceof EntityBlock) {
          registry.registerSchematicBlock(block, SchematicBlockEntityCreative::new);
        } else if (block instanceof LiquidBlock) {
          registry.registerSchematicBlock(block, () -> new SchematicFluid(block));
        } else if (block instanceof BushBlock || block instanceof IPlantable || block instanceof BonemealableBlock
          || block instanceof BasePressurePlateBlock) {
          registry.registerSchematicBlock(block, SchematicBlockFloored::new);
        } else if (block instanceof LeverBlock || block instanceof TorchBlock || block instanceof ButtonBlock) {
          registry.registerSchematicBlock(block, SchematicWallSide::new);
        } else if (block instanceof StairBlock) {
          registry.registerSchematicBlock(block, SchematicStairs::new);
        } else {
          registry.registerSchematicBlock(block, SchematicBlock::new);
        }
      } catch (RuntimeException e) {
        com.peco2282.bcreborn.api.core.BCLog.logger.warn("Builder: no schematic for " + ForgeRegistries.BLOCKS.getKey(block), e);
      }
    }
  }
}
