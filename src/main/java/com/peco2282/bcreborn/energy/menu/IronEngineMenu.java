package com.peco2282.bcreborn.energy.menu;

import com.peco2282.bcreborn.common.menu.BuildCraftMenu;
import com.peco2282.bcreborn.energy.MenuTypesEnergy;
import com.peco2282.bcreborn.energy.block.entity.IronEngineBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class IronEngineMenu extends BuildCraftMenu<IronEngineMenu> {
  public IronEngineMenu(int p_38852_, Inventory p_38853_) {
    this(p_38852_, p_38853_, new SimpleContainer(1));
  }

  private final Container container;
  private IronEngineBlockEntity engine;
  private final ContainerData data = new ContainerData() {
    @Override
    public int get(int index) {
      if (engine == null) return 0;
      return switch (index) {
        case 0 -> engine.getTotalBurnTime() == 0 ? 0 : (engine.getBurnTime() * 100 / engine.getTotalBurnTime());
        case 1 -> engine.getEnergyStored();
        case 2 -> (int) engine.heat;
        case 3 -> engine.energyStage.ordinal();
        case 4 -> engine.getMaxEnergyStored();
        default -> 0;
      };
    }

    @Override
    public void set(int index, int value) { }

    @Override
    public int getCount() { return 5; }
  };

  public IronEngineMenu(int p_38852_, Inventory p_38853_, Container engine) {
    super(MenuTypesEnergy.IRON_ENGINE.get(), p_38852_, p_38853_);
    this.container = engine;
    if (engine instanceof IronEngineBlockEntity) {
      this.engine = (IronEngineBlockEntity) engine;
    }
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
  public int getScaledBurn() { return data.get(0); }
  public int getEnergy() { return data.get(1); }
  public int getHeat() { return data.get(2); }
  public int getStageOrdinal() { return data.get(3); }
  public int getMaxEnergy() { return data.get(4); }

  @Override
  public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
    return null;
  }

  @Override
  public boolean stillValid(Player p_38874_) {
    return engine.stillValid(p_38874_);
  }
}
