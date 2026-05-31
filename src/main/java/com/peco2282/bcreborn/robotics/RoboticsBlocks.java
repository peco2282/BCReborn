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
package com.peco2282.bcreborn.robotics;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.robotics.block.RequesterBlock;
import com.peco2282.bcreborn.robotics.block.ZonePlanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornRobotics.MODID)
public class RoboticsBlocks {
  private static final BCRegistry REGISTRY = BCRebornRobotics.getRegistry();
  public static final RegistryObject<RequesterBlock> REQUESTER = register("requester", () ->
    new RequesterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F))
  );
  public static final RegistryObject<ZonePlanBlock> ZONE_PLAN = register("zone_plan", () ->
    new ZonePlanBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(5.0F, 6.0F))
  );

  private static <B extends Block> RegistryObject<B> register(String name, Supplier<B> supplier) {
    return REGISTRY.registerBlock(name, supplier);
  }
}
