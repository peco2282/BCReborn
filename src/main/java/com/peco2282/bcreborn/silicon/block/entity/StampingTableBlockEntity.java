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
package com.peco2282.bcreborn.silicon.block.entity;

import net.minecraft.core.NonNullList;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.server.level.ServerLevel;
import com.peco2282.bcreborn.silicon.item.PackageItem;
import com.peco2282.bcreborn.common.utils.Utils;
import com.peco2282.bcreborn.common.utils.BCFakePlayer;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import com.peco2282.bcreborn.silicon.menu.StampingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code TileStampingTable}: runs one item at a time through the crafting recipes with laser
 * energy.
 * <ul>
 *   <li>a Package (made by the Packager) is crafted as the 3 x 3 grid it holds;</li>
 *   <li>any other item is tried as a single item in the grid (a log gives planks, a block of iron nine ingots);</li>
 *   <li>an item without a recipe is passed on to the output unchanged.</li>
 * </ul>
 * Slot 0 is the input, 1 the output, 2..4 take what the recipe leaves behind (and overflow of the output). Each
 * item costs 400 RF, a Package 400 RF per stack in it.
 */
public class StampingTableBlockEntity extends LaserTableBaseBlockEntity {
  /** Original {@code getRequiredEnergy()}: 400 per item. */
  public static final int ENERGY_PER_ITEM = 400;
  public static final int SLOT_INPUT = 0;
  public static final int SLOT_OUTPUT = 1;
  public static final int SLOT_LEFTOVER_FIRST = 2;
  public static final int SLOT_LEFTOVER_LAST = 4;

  public StampingTableBlockEntity(BlockPos pos, BlockState state) {
    super(SiliconBlockEntityTypes.STAMPING_TABLE.get(), pos, state);
  }

  @Override
  protected boolean isMachineInput(int slot) {
    return slot == SLOT_INPUT;
  }

  @Override
  protected boolean isMachineOutput(int slot) {
    return slot >= SLOT_OUTPUT && slot <= SLOT_LEFTOVER_LAST;
  }

  private static int packageStacks(ItemStack stack) {
    CompoundTag tag = stack.getTag();
    if (tag == null) return 0;
    int count = 0;
    for (int i = 0; i < 9; i++) {
      if (tag.contains("item" + i)) count++;
    }
    return count;
  }

  @Override
  public int getRequiredEnergy() {
    ItemStack stack = getItem(SLOT_INPUT);
    ItemStack output = getItem(SLOT_OUTPUT);
    if (!output.isEmpty() && output.getCount() >= output.getMaxStackSize()) {
      return 0;
    }
    if (stack.isEmpty()) {
      return 0;
    }
    return stack.getItem() instanceof PackageItem ? ENERGY_PER_ITEM * packageStacks(stack) : ENERGY_PER_ITEM;
  }

  @Override
  public boolean canCraft() {
    return hasWork();
  }

  @Override
  public boolean hasWork() {
    return getRequiredEnergy() > 0;
  }

  /** The grid for the input: the Package's contents, or the item alone in the first slot. Null if unreadable. */
  @Nullable
  private CraftingContainer craftingGrid(ItemStack input) {
    CraftingContainer grid = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
      @Override
      public boolean stillValid(Player player) {
        return false;
      }

      @Override
      public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
      }
    }, 3, 3);
    if (input.getItem() instanceof PackageItem) {
      for (int i = 0; i < 9; i++) {
        if (input.getTag() != null && input.getTag().contains("item" + i)) {
          ItemStack stack = PackageItem.getStack(input, i);
          if (stack.isEmpty()) return null;
          grid.setItem(i, stack);
        }
      }
    } else {
      grid.setItem(0, input.copyWithCount(1));
    }
    return grid;
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    super.tick(level, pos, state);
    if (level.isClientSide) return;

    int required = getRequiredEnergy();
    clientRequiredEnergy = required;
    if (required <= 0 || getEnergy() < required) return;

    ItemStack input = getItem(SLOT_INPUT);
    CraftingContainer grid = craftingGrid(input);
    if (grid == null) return;

    CraftingRecipe recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, level).orElse(null);
    ItemStack result = recipe == null ? ItemStack.EMPTY : recipe.assemble(grid, level.registryAccess());

    subtractEnergy(required);

    if (!result.isEmpty()) {
      FakePlayer player = BCFakePlayer.getBuildCraftPlayer((ServerLevel) level, pos.getX(), pos.getY() + 1, pos.getZ()).get();
      // Original craftSlot.onPickupFromSlot(): crafting statistics and the crafted event.
      result.onCraftedBy(level, player, result.getCount());
      ForgeEventFactory.firePlayerCraftingEvent(player, result, grid);
      handleLeftoverItems(recipe.getRemainingItems(grid));
      if (player != null) {
        handleLeftoverItems(player.getInventory().items);
        player.getInventory().items.replaceAll(stack -> ItemStack.EMPTY);
      }

      for (int i = SLOT_OUTPUT; i <= SLOT_LEFTOVER_LAST && !result.isEmpty(); i++) {
        ItemStack inside = getItem(i);
        if (inside.isEmpty()) {
          inv.setItem(i, result.copy());
          result.setCount(0);
        } else if (ItemStack.isSameItemSameTags(inside, result)) {
          int moved = Math.min(result.getCount(), inside.getMaxStackSize() - inside.getCount());
          inside.grow(moved);
          result.shrink(moved);
        }
      }
      if (!result.isEmpty()) {
        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, result.copy()));
      }
      input.shrink(1);
      inv.setItem(SLOT_INPUT, input.isEmpty() ? ItemStack.EMPTY : input);
    } else if (getItem(SLOT_OUTPUT).isEmpty()) {
      // Original: an item without a recipe is passed through whole (the energy is spent anyway).
      inv.setItem(SLOT_OUTPUT, input);
      inv.setItem(SLOT_INPUT, ItemStack.EMPTY);
    }
    setChanged();
  }

  /**
   * Original {@code handleLeftoverItems()}: into slots 2..4, then into a neighbouring inventory, then dropped on
   * top of the table.
   */
  private void handleLeftoverItems(NonNullList<ItemStack> items) {
    for (ItemStack leftover : items) {
      if (leftover.isEmpty()) continue;
      ItemStack output = leftover.copy();
      for (int j = SLOT_LEFTOVER_FIRST; j <= SLOT_LEFTOVER_LAST && !output.isEmpty(); j++) {
        ItemStack target = getItem(j);
        if (target.isEmpty()) {
          inv.setItem(j, output);
          output = ItemStack.EMPTY;
        } else if (ItemStack.isSameItemSameTags(target, output)) {
          int moved = Math.min(output.getCount(), target.getMaxStackSize() - target.getCount());
          target.grow(moved);
          output.shrink(moved);
        }
      }
      if (!output.isEmpty()) {
        output.shrink(Utils.addToRandomInventoryAround(level, worldPosition, output));
      }
      if (!output.isEmpty()) {
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), output);
      }
    }
  }

  @Override
  public int getContainerSize() {
    // Original getSizeInventory(): input, output and three leftover slots.
    return 5;
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornsilicon.stamping_table");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
    return new StampingTableMenu(p_39954_, p_39955_, this);
  }
}
