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
package com.peco2282.bcreborn.transport;

import com.peco2282.bcreborn.BCRebornTransport;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.transport.menu.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornTransport.MODID)
public class TransportMenuTypes {
  private static final BCRegistry REGISTRY = BCRebornTransport.getRegistry();

  private static <M extends BuildCraftMenu<M>> RegistryObject<MenuType<M>> register(String name, IContainerFactory<M> factory) {
    return REGISTRY.registerMenuType(name, () -> IForgeMenuType.create(factory));
  }

  public static final RegistryObject<MenuType<DiamondPipeMenu>> DIAMOND_PIPE_MENU = register("diamond_pipe_menu", DiamondPipeMenu::new);
  public static final RegistryObject<MenuType<EmeraldPipeMenu>> EMERALD_PIPE_MENU = register("emerald_pipe_menu", EmeraldPipeMenu::new);
  public static final RegistryObject<MenuType<EmeraldFluidPipeMenu>> EMERALD_FLUID_PIPE_MENU = register("emerald_fluid_pipe_menu", EmeraldFluidPipeMenu::new);
  public static final RegistryObject<MenuType<EmzuliPipeMenu>> EMZULI_PIPE_MENU = register("emzuli_pipe_menu", EmzuliPipeMenu::new);
  public static final RegistryObject<MenuType<FilteredBufferMenu>> FILTERED_BUFFER_MENU = register("filtered_buffer_menu", FilteredBufferMenu::new);
  public static final RegistryObject<MenuType<GateInterfaceMenu>> GATE_INTERFACE_MENU = register("gate_interface_menu", GateInterfaceMenu::new);


}
