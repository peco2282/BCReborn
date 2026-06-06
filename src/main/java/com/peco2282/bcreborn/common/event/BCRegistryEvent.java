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
package com.peco2282.bcreborn.common.event;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.blueprints.Schematic;
import com.peco2282.bcreborn.api.boards.RedstoneBoardNBT;
import com.peco2282.bcreborn.api.core.IWorldProperty;
import com.peco2282.bcreborn.api.crops.ICropHandler;
import com.peco2282.bcreborn.api.facades.IFacadeItem;
import com.peco2282.bcreborn.api.registry.BCRegistryKeys;
import com.peco2282.bcreborn.api.statements.IStatement;
import com.peco2282.bcreborn.api.statements.IStatementParameter;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = BCRebornCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCRegistryEvent {
  @SubscribeEvent
  public static void onRegistryEvent(NewRegistryEvent event) {
    event.create(new RegistryBuilder<Schematic>().setName(BCRegistryKeys.SCHEMATIC.location()));
    event.create(new RegistryBuilder<IWorldProperty>().setName(BCRegistryKeys.WORLD_PROPERTY.location()));
    event.create(new RegistryBuilder<ICropHandler>().setName(BCRegistryKeys.CROP_HANDLER.location()));
    event.create(new RegistryBuilder<IFacadeItem>().setName(BCRegistryKeys.FACADE_ITEM.location()));
    event.create(new RegistryBuilder<IStatement>().setName(BCRegistryKeys.STATEMENT.location()));
    event.create(new RegistryBuilder<IStatementParameter>().setName(BCRegistryKeys.STATEMENT_PARAMETER.location()));
    event.create(new RegistryBuilder<RedstoneBoardNBT<?>>().setName(BCRegistryKeys.REDSTONE_BOARD.location()));
  }
}
