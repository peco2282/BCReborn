package com.peco2282.bcreborn.robotics;

import com.peco2282.bcreborn.BCRebornRobotics;
import com.peco2282.bcreborn.common.BCRegistry;
import com.peco2282.bcreborn.common.bean.InitRegister;
import com.peco2282.bcreborn.robotics.menu.RequesterMenu;
import com.peco2282.bcreborn.robotics.menu.ZonePlanMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

@InitRegister(modId = BCRebornRobotics.MODID)
public class MenuTypesRobotics {
    private static final BCRegistry REGISTRY = BCRebornRobotics.getRegistry();

    public static final RegistryObject<MenuType<RequesterMenu>> REQUESTER = register("requester", IForgeMenuType.create((windowId, inv, data) -> new RequesterMenu(windowId, inv, data)));
    public static final RegistryObject<MenuType<ZonePlanMenu>> ZONE_PLAN = register("zone_plan", IForgeMenuType.create((windowId, inv, data) -> new ZonePlanMenu(windowId, inv, data)));

    private static <M extends AbstractContainerMenu> RegistryObject<MenuType<M>> register(String name, MenuType<M> menuType) {
        return REGISTRY.registerMenuType(name, () -> menuType);
    }
}
