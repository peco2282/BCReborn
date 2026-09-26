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

import com.peco2282.bcreborn.silicon.menu.AdvancedCraftingTableMenu;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class AdvancedCraftingTableBlockEntity extends LaserTableBaseBlockEntity {
  public static final int ENERGY_PER_CRAFT = 5000;
  public static final int PATTERN_START = 24;
  public static final int PREVIEW = 33;

  @Override
  protected boolean isMachineInput(int slot) { return slot >= 0 && slot < 15; }

  @Override
  protected boolean isMachineOutput(int slot) { return slot >= 15 && slot < 24; }

  private CraftingContainer grid(boolean actual, int[] used) {
    var grid = new TransientCraftingContainer(
      new AbstractContainerMenu(null, -1) {
        @Override public boolean stillValid(Player player) { return false; }
        @Override public ItemStack quickMoveStack(Player player, int slot) {
          return ItemStack.EMPTY;
        }
      }, 3, 3);
    for (int i = 0; i < 9; i++) {
      var pattern = getItem(PATTERN_START + i);
      if (pattern.isEmpty()) continue;
      if (!actual) { grid.setItem(i, pattern.copyWithCount(1)); continue; }
      int found = -1;
      for (int slot = 0; slot < 15; slot++) {
        if (used[slot] < getItem(slot).getCount()
          && ItemStack.isSameItemSameTags(pattern, getItem(slot))) {
          found = slot;
          break;
        }
      }
      if (found < 0) return null;
      used[found]++;
      grid.setItem(i, getItem(found).copyWithCount(1));
    }
    return grid;
  }

  private ItemStack[] plan() {
    if (level == null) return null;
    int[] used = new int[15];
    var grid = grid(true, used);
    if (grid == null) return null;
    var recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, level).orElse(null);
    if (recipe == null) return null;
    var result = recipe.assemble(grid, level.registryAccess());
    if (result.isEmpty()) return null;
    var next = MachineInventory.copy(this);
    for (int i = 0; i < used.length; i++) next[i].shrink(used[i]);
    if (!MachineInventory.insert(next, 15, 24, result)) return null;
    for (var remainder : recipe.getRemainingItems(grid)) {
      if (!remainder.isEmpty() && !MachineInventory.insert(next, 15, 24, remainder)) return null;
    }
    return next;
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    super.tick(level, pos, state);
    if (level.isClientSide) return;
    var grid = grid(false, null);
    var recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, grid, level).orElse(null);
    var preview = recipe == null ? ItemStack.EMPTY : recipe.assemble(grid, level.registryAccess());
    if (!ItemStack.matches(getItem(PREVIEW), preview)) setItem(PREVIEW, preview);
    clientRequiredEnergy = preview.isEmpty() ? 0 : ENERGY_PER_CRAFT;
    if (getEnergy() < ENERGY_PER_CRAFT) return;
    var next = plan();
    if (next == null) return;
    MachineInventory.commit(this, next);
    subtractEnergy(ENERGY_PER_CRAFT);
    setChanged();
  }
  public AdvancedCraftingTableBlockEntity(BlockPos pos, BlockState state) {
    super(SiliconBlockEntityTypes.ADVANCED_CRAFTING_TABLE.get(), pos, state);
  }

  @Override
  public int getRequiredEnergy() {
    return canCraft() ? ENERGY_PER_CRAFT : 0;
  }

  @Override
  public boolean canCraft() {
    return plan() != null;
  }

  @Override
  public int getContainerSize() {
    return 40; // 1.7.10 had 40 slots
  }

  @Override
  public boolean hasWork() {
    return canCraft();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornsilicon.advanced_crafting_table");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
    return new AdvancedCraftingTableMenu(p_39954_, p_39955_, this);
  }
}
