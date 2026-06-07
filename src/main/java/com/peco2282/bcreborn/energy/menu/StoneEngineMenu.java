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
import com.peco2282.bcreborn.energy.MenuTypesEnergy;
import com.peco2282.bcreborn.energy.block.entity.StoneEngineBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class StoneEngineMenu extends BuildCraftMenu<StoneEngineMenu> {
  private StoneEngineBlockEntity engine;
  private final ContainerData data = new ContainerData() {
    @Override
    public int get(int index) {
      if (engine == null) return 0;
      return switch (index) {
        case 0 -> engine.getLeftBurnTime(100); // scaled burn time
        case 1 -> engine.getEnergyStored();
        case 2 -> (int) engine.heat;
        case 3 -> engine.energyStage.ordinal();
        case 4 -> engine.getMaxEnergyStored();
        default -> 0;
      };
    }

    @Override
    public void set(int index, int value) {
      // クライアント側のみ想定、サーバー書き換えなし
    }

    @Override
    public int getCount() {
      return 5;
    }
  };

  public StoneEngineMenu(int id, Inventory inventory) {
    this(id, inventory, new SimpleContainer(1));
  }

  public StoneEngineMenu(int id, Inventory inventory, Container engine) {
    super(MenuTypesEnergy.STONE_ENGINE.get(), id, inventory);
    if (engine instanceof StoneEngineBlockEntity) {
      this.engine = (StoneEngineBlockEntity) engine;
    }

    addSlot(new Slot(engine, 0, 80, 41));

    for (int i = 0; i < 3; i++) {
      for (int k = 0; k < 9; k++) {
        addSlot(new Slot(inventory, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
      }
    }

    for (int j = 0; j < 9; j++) {
      addSlot(new Slot(inventory, j, 8 + j * 18, 142));
    }

    addDataSlots(data);
  }

  // ---- client accessors (Screen から参照) ----
  public int getScaledBurn() {
    return data.get(0);
  }

  public int getEnergy() {
    return data.get(1);
  }

  public int getHeat() {
    return data.get(2);
  }

  public int getStageOrdinal() {
    return data.get(3);
  }

  public int getMaxEnergy() {
    return data.get(4);
  }

  @Override
  public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
    return new ItemStack(Blocks.ACACIA_DOOR);
  }

  @Override
  public boolean stillValid(Player p_38874_) {
    return this.engine.stillValid(p_38874_);
  }
}
