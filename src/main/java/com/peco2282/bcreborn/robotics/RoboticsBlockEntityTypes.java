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
import com.peco2282.bcreborn.robotics.block.entity.RequesterBlockEntity;
import com.peco2282.bcreborn.robotics.block.entity.ZonePlanBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornRobotics.MODID)
public class RoboticsBlockEntityTypes {
  private static final BCRegistry REGISTRY = BCRebornRobotics.getRegistry();

  private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, Supplier<BlockEntityType<T>> type) {
    return REGISTRY.registerBlockEntityType(name, type);
  }

  public static final RegistryObject<BlockEntityType<RequesterBlockEntity>> REQUESTER = REGISTRY.registerBlockEntityType("requester", () ->
    BlockEntityType.Builder.of(RequesterBlockEntity::new, RoboticsBlocks.REQUESTER.get()).build(null)
  );

  public static final RegistryObject<BlockEntityType<ZonePlanBlockEntity>> ZONE_PLAN = REGISTRY.registerBlockEntityType("zone_plan", () ->
    BlockEntityType.Builder.of(ZonePlanBlockEntity::new, RoboticsBlocks.ZONE_PLAN.get()).build(null)
  );
}
