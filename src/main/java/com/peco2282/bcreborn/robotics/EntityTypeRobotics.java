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
import com.peco2282.bcreborn.robotics.entity.EntityRobot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornRobotics.MODID)
public class EntityTypeRobotics {
    private static final BCRegistry REGISTRY = BCRebornRobotics.getRegistry();

    public static final RegistryObject<EntityType<EntityRobot>> ROBOT = REGISTRY.registerEntityType("robot", () -> EntityType.Builder.<EntityRobot>of(EntityRobot::new, MobCategory.MISC).fireImmune().sized(.25F, .25F).build("robot"));
}
