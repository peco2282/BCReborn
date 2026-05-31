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
package com.peco2282.bcreborn.silicon;

import com.peco2282.bcreborn.BCRebornSilicon;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.silicon.menu.AdvancedCraftingTableMenu;
import com.peco2282.bcreborn.silicon.menu.AssemblyTableMenu;
import com.peco2282.bcreborn.silicon.menu.ChargingTableMenu;
import com.peco2282.bcreborn.silicon.menu.IntegrationTableMenu;
import com.peco2282.bcreborn.silicon.menu.PackagerMenu;
import com.peco2282.bcreborn.silicon.menu.ProgrammingTableMenu;
import com.peco2282.bcreborn.silicon.menu.StampingTableMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@InitRegister(modId = BCRebornSilicon.MODID)
public class SiliconMenuTypes {
    private static final BCRegistry REGISTRY = BCRebornSilicon.getRegistry();

    public static final RegistryObject<MenuType<AssemblyTableMenu>> ASSEMBLY_TABLE = register("assembly_table", AssemblyTableMenu::new);
    public static final RegistryObject<MenuType<AdvancedCraftingTableMenu>> ADVANCED_CRAFTING_TABLE = register("advanced_crafting_table", AdvancedCraftingTableMenu::new);
    public static final RegistryObject<MenuType<IntegrationTableMenu>> INTEGRATION_TABLE = register("integration_table", IntegrationTableMenu::new);
    public static final RegistryObject<MenuType<ChargingTableMenu>> CHARGING_TABLE = register("charging_table", ChargingTableMenu::new);
    public static final RegistryObject<MenuType<ProgrammingTableMenu>> PROGRAMMING_TABLE = register("programming_table", ProgrammingTableMenu::new);
    public static final RegistryObject<MenuType<StampingTableMenu>> STAMPING_TABLE = register("stamping_table", StampingTableMenu::new);
    public static final RegistryObject<MenuType<PackagerMenu>> PACKAGER = register("packager", PackagerMenu::new);

    private static <M extends BuildCraftMenu<M>> RegistryObject<MenuType<M>> register(String name, IContainerFactory<M> supplier) {
        return REGISTRY.registerMenuType(name, () -> IForgeMenuType.create(supplier));
    }

}
