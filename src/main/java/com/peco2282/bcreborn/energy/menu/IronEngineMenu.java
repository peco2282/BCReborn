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
package com.peco2282.bcreborn.energy.menu;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.energy.EnergyMenuTypes;
import com.peco2282.bcreborn.energy.block.entity.IronEngineBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class IronEngineMenu extends BuildCraftMenu<IronEngineMenu> implements EngineMenuInfo {
  private IronEngineBlockEntity engine;
  private final ContainerData data;

  public IronEngineMenu(int p_38852_, Inventory p_38853_) {
    this(p_38852_, p_38853_, new SimpleContainer(1));
  }

  public IronEngineMenu(int p_38852_, Inventory p_38853_, Container engine) {
    super(EnergyMenuTypes.IRON_ENGINE.get(), p_38852_, p_38853_);
    if (engine instanceof IronEngineBlockEntity) {
      this.engine = (IronEngineBlockEntity) engine;
    }
    IronEngineBlockEntity e = this.engine;
    this.data = EngineMenuData.create(e,
      () -> e.getTotalBurnTime() == 0 ? 0 : e.getBurnTime() * 100 / e.getTotalBurnTime(),
      () -> EngineMenuData.fluidId(e.getFluidInTank(IronEngineBlockEntity.TANK_FUEL)),
      () -> e.getFluidInTank(IronEngineBlockEntity.TANK_FUEL).getAmount(),
      () -> EngineMenuData.fluidId(e.getFluidInTank(IronEngineBlockEntity.TANK_COOLANT)),
      () -> e.getFluidInTank(IronEngineBlockEntity.TANK_COOLANT).getAmount());
    addSlot(new Slot(engine, 0, 52, 41));


    for (int i = 0; i < 3; i++) {
      for (int k = 0; k < 9; k++) {
        addSlot(new Slot(p_38853_, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
      }
    }

    for (int j = 0; j < 9; j++) {
      addSlot(new Slot(p_38853_, j, 8 + j * 18, 142));
    }
    addDataSlots(data);
  }

  // ---- client accessors (Screen から参照) ----
  public int getScaledBurn() {
    return data.get(EngineMenuData.BURN);
  }

  @Override
  public int getEnergy() {
    return EngineMenuData.combine(data, EngineMenuData.ENERGY_LO, EngineMenuData.ENERGY_HI);
  }

  @Override
  public int getHeat() {
    return data.get(EngineMenuData.HEAT);
  }

  public int getStageOrdinal() {
    return data.get(EngineMenuData.STAGE);
  }

  public int getMaxEnergy() {
    return EngineMenuData.combine(data, EngineMenuData.MAX_LO, EngineMenuData.MAX_HI);
  }

  @Override
  public int getCurrentOutput() {
    return data.get(EngineMenuData.OUTPUT);
  }

  public FluidStack getFuel() {
    return EngineMenuData.fluid(data.get(EngineMenuData.BASE_COUNT), data.get(EngineMenuData.BASE_COUNT + 1));
  }

  public FluidStack getCoolant() {
    return EngineMenuData.fluid(data.get(EngineMenuData.BASE_COUNT + 2), data.get(EngineMenuData.BASE_COUNT + 3));
  }

  // Shift-click uses BuildCraftMenu's machine / player inventory handling.

  @Override
  public boolean stillValid(Player p_38874_) {
    return engine != null && engine.stillValid(p_38874_);
  }
}
