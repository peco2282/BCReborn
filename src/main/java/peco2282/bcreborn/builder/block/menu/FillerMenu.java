package peco2282.bcreborn.builder.block.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import peco2282.bcreborn.api.enums.EnumFillerType;
import peco2282.bcreborn.lib.block.menu.BCMenu;

public class FillerMenu extends BCMenu {
  private final Container input = new SimpleContainer(9);
  private final Container result = new SimpleContainer(9);
  private final Container resource = new SimpleContainer(27);
  private EnumFillerType current;
  public FillerMenu(int id, Inventory inventory, @Nullable FriendlyByteBuf buffer) {
    super(BCBuilderMenuTypes.FILLER.get(), id, inventory, buffer);
    addFullPlayerInventory(8, 153);

    for (int sy = 0; sy < 3; sy++) {
      for (int sx = 0; sx < 9; sx++) {
        addSlot(new Slot(resource, sx + sy * 9, sx * 18 + 8, sy * 18 + 40));
      }
    }
  }

  @Override
  public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player p_38874_) {
    return this.inventory.stillValid(p_38874_);
  }

  private void slotCheck() {
    EnumFillerType.check(resource).ifPresent(type -> {
      this.current = type;
      result.setItem(0, type.getPanel().getDefaultInstance());
    });
  }
}
