package peco2282.bcreborn.lib.block.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BCMenu extends AbstractContainerMenu {
  protected final Inventory inventory;
  protected BCMenu(@NotNull MenuType<?> menuType, int id, Inventory inventory, @Nullable FriendlyByteBuf buffer) {
    super(menuType, id);
    this.inventory = inventory;
  }

  protected void addFullPlayerInventory(int startX, int startY) {
    for (int sy = 0; sy < 3; sy++) {
      for (int sx = 0; sx < 9; sx++) {
        addSlot(new Slot(inventory, sx + sy * 9 + 9, startX + sx * 18, startY + sy * 18));
      }
    }

    for (int sx = 0; sx < 9; sx++) {
      addSlot(new Slot(inventory, sx, startX + sx * 18, startY + 58));
    }
  }
}
