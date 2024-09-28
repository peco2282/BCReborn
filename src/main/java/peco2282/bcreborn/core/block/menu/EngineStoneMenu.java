package peco2282.bcreborn.core.block.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.lib.block.menu.BCMenu;

public class EngineStoneMenu extends BCMenu {
  private final Container container = new SimpleContainer(1);
  public EngineStoneMenu(int id, Inventory inventory, @Nullable FriendlyByteBuf buf) {
    super(BCMenues.STONE_ENGINE.get(), id, inventory, buf);
    addFullPlayerInventory(8, 95);
    addSlot(new Slot(container, 0, 56, 17));
  }

  @Override
  public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player p_38874_) {
    return inventory.stillValid(p_38874_);
  }
}
