package peco2282.bcreborn.core.block.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.lib.block.menu.BCMenu;

public class EngineIronMenu extends BCMenu {
  public EngineIronMenu(int id, Inventory inventory, @Nullable FriendlyByteBuf buf) {
    super(BCMenues.IRON_ENGINE.get(), id, inventory, buf);
    addFullPlayerInventory(8, 95);
  }

  @Override
  public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player p_38874_) {
    return this.inventory.stillValid(p_38874_);
  }
}
