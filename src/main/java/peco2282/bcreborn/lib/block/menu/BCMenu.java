package peco2282.bcreborn.lib.block.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Represents the base class for custom menu implementations.
 * This class is used as a foundation for creating custom menus in the game.
 * It manages the player's inventory slots and facilitates inventory interactions.
 *
 * <p>
 * To use this class, extend it and provide a specific {@link MenuType}.
 * Use {@link #addFullPlayerInventory(int, int)} to add standard player inventory slots.
 * </p>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>
 * public class ExampleMenu extends BCMenu {
 *   public ExampleMenu(int id, Inventory inventory, @Nullable FriendlyByteBuf buffer) {
 *     super(ExampleMenuType.INSTANCE.get(), id, inventory, buffer);
 *     addFullPlayerInventory(8, 95);
 *   }
 *
 *   @Override
 *   public boolean stillValid(Player player) {
 *     return this.inventory.stillValid(player);
 *   }
 * }
 * </pre>
 *
 * @author peco2282
 */
public abstract class BCMenu extends AbstractContainerMenu {
  /**
   * The player's inventory associated with this menu.
   */
  protected final Inventory inventory;

  /**
   * Constructs a new {@code BCMenu}.
   *
   * @param menuType  the {@link MenuType} of this menu
   * @param id        the unique ID for this menu instance
   * @param inventory the player's inventory
   * @param buffer    an optional {@link FriendlyByteBuf} for additional data
   */
  protected BCMenu(@NotNull MenuType<?> menuType, int id, Inventory inventory, @Nullable FriendlyByteBuf buffer) {
    super(menuType, id);
    this.inventory = inventory;
  }

  /**
   * Adds the full set of player inventory slots to this menu.
   * This includes both the main inventory (3 rows of 9 slots) and the hotbar (1 row of 9 slots).
   *
   * @param startX the starting X position for the inventory slots
   * @param startY the starting Y position for the inventory slots
   */
  protected void addFullPlayerInventory(int startX, int startY) {
    // Add the main inventory slots (3 rows, 9 slots each).
    for (int sy = 0; sy < 3; sy++) {
      for (int sx = 0; sx < 9; sx++) {
        addSlot(new Slot(inventory, sx + sy * 9 + 9, startX + sx * 18, startY + sy * 18));
      }
    }

    // Add the hotbar slots (1 row, 9 slots).
    for (int sx = 0; sx < 9; sx++) {
      addSlot(new Slot(inventory, sx, startX + sx * 18, startY + 58));
    }
  }
}

