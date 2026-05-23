package com.peco2282.bcreborn.common.event;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.api.blueprints.Schematic;
import com.peco2282.bcreborn.api.core.IWorldProperty;
import com.peco2282.bcreborn.api.crops.ICropHandler;
import com.peco2282.bcreborn.api.facades.IFacadeItem;
import com.peco2282.bcreborn.api.filler.IFillerPattern;
import com.peco2282.bcreborn.api.fuels.ICoolant;
import com.peco2282.bcreborn.api.fuels.IFuel;
import com.peco2282.bcreborn.api.fuels.ISolidCoolant;
import com.peco2282.bcreborn.api.library.LibraryTypeHandler;
import com.peco2282.bcreborn.api.registry.BCRegistryKeys;
import com.peco2282.bcreborn.api.statements.IStatement;
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
    event.create(new RegistryBuilder<IFillerPattern>().setName(BCRegistryKeys.FILLER_PATTERNS.location()));
    event.create(new RegistryBuilder<IStatement>().setName(BCRegistryKeys.STATEMENT.location()));
  }
}
