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
  private final Container type = new TypeConatiner();
  private final Container resource = new SimpleContainer(27);
  private EnumFillerType current;
  public FillerMenu(int id, Inventory inventory, @Nullable FriendlyByteBuf buffer) {
    super(BCBuilderMenuTypes.FILLER.get(), id, inventory, buffer);
    addFullPlayerInventory(8, 153);

    for (int sy = 0; sy < 3; sy++) {
      for (int sx = 0; sx < 9; sx++) {
        addSlot(new Slot(resource, sx + sy * 9, sx * 18 + 8, sy * 18 + 60));
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

  private static class TypeConatiner implements Container {
    @Override
    public int getContainerSize() {
      return 1;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public ItemStack getItem(int p_18941_) {
      return null;
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_) {
      return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
      return null;
    }

    @Override
    public void setItem(int p_18944_, ItemStack p_18945_) {

    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player p_18946_) {
      return true;
    }

    @Override
    public void clearContent() {
    }
  }
}
