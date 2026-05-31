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
package com.peco2282.bcreborn.core;

import com.peco2282.bcreborn.BCRebornCore;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.core.list.ListNewMenu;
import com.peco2282.bcreborn.core.list.ListOldMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornCore.MODID, priority = 1)
public class MenuTypesCore {
    private static final BCRegistry REGISTRY = BCRebornCore.getRegistry();

    public static final RegistryObject<MenuType<ListNewMenu>> LIST_NEW = register("list_new", IForgeMenuType.create((windowId, inv, data) -> new ListNewMenu(windowId, inv.player)));
    public static final RegistryObject<MenuType<ListOldMenu>> LIST_OLD = register("list_old", IForgeMenuType.create((windowId, inv, data) -> new ListOldMenu(windowId, inv.player)));

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String name, MenuType<T> type) {
        return REGISTRY.registerMenuType(name, () -> type);
    }
}
