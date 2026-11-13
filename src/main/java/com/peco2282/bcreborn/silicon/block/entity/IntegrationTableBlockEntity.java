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

import com.peco2282.bcreborn.api.recipes.IIntegrationRecipe;
import java.util.ArrayList;
import com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry;
import com.peco2282.bcreborn.silicon.menu.IntegrationTableMenu;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class IntegrationTableBlockEntity extends LaserTableBaseBlockEntity {
  public static final int ROBOT_INTEGRATION_ENERGY = 50000;
  private record Plan(ItemStack[] items, int energy) {}

  @Override protected boolean isMachineInput(int slot) { return slot >= 0 && slot < 9; }
  @Override protected boolean isMachineOutput(int slot) { return slot == 9; }

  private boolean match(List<Ingredient> ingredients, int index, int[] used) {
    if (index == ingredients.size()) return true;
    for (int slot = 1; slot < 9; slot++) {
      if (used[slot] < getItem(slot).getCount() && ingredients.get(index).test(getItem(slot))) {
        used[slot]++;
        if (match(ingredients, index + 1, used)) return true;
        used[slot]--;
      }
    }
    return false;
  }

  /** Original setNewActiveRecipe(): the first dynamic recipe that accepts the input is the active one. */
  @Nullable
  private IIntegrationRecipe activeRecipe() {
    var manager = BuildcraftRecipeRegistry.integration();
    ItemStack input = getItem(0);
    if (manager == null || input.isEmpty()) return null;
    for (IIntegrationRecipe recipe : manager.getIntegrationRecipes()) {
      if (recipe.isValidInput(input)) return recipe;
    }
    return null;
  }

  /** Original getMaximumExpansionCount(): 0 without a dynamic recipe, -1 for no limit. */
  public int getMaxExpansionCount() {
    IIntegrationRecipe recipe = activeRecipe();
    return recipe != null ? recipe.getMaximumExpansionCount(getItem(0)) : 0;
  }

  /**
   * Original isItemValidForSlot(): anything may be the input; expansions must fit the active recipe and its
   * expansion count. Data-driven recipes accept the ingredients of the recipes matching the input.
   */
  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    if (slot == 0) return true;
    if (slot < 0 || slot >= 9) return false;
    IIntegrationRecipe active = activeRecipe();
    if (active != null) {
      int expansionCount = active.getMaximumExpansionCount(getItem(0));
      if (expansionCount > 0 && slot > expansionCount) return false;
      return active.isValidExpansion(getItem(0), stack);
    }
    var manager = BuildcraftRecipeRegistry.integration();
    if (manager == null) return false;
    for (var recipe : manager.getRecipes()) {
      if (!recipe.input().test(getItem(0))) continue;
      for (Ingredient expansion : recipe.expansions()) {
        if (expansion.test(stack)) return true;
      }
    }
    return false;
  }

  /** Plans a dynamic recipe on a copy of the inventory, so that nothing is consumed unless the output fits. */
  @Nullable
  private Plan planDynamic(IIntegrationRecipe recipe) {
    ItemStack[] next = MachineInventory.copy(this);
    List<ItemStack> expansions = new ArrayList<>();
    for (int slot = 1; slot < 9; slot++) {
      if (!next[slot].isEmpty()) expansions.add(next[slot]);
    }
    if (expansions.isEmpty()) return null;
    ItemStack output = recipe.craft(next[0].copy(), expansions, false);
    if (output == null || output.isEmpty()) return null;
    // Original: the input shrinks by the size of the output.
    next[0].shrink(output.getCount());
    if (!MachineInventory.insert(next, 9, 10, output)) return null;
    return new Plan(next, recipe.getEnergyCost());
  }

  private Plan plan() {
    IIntegrationRecipe active = activeRecipe();
    if (active != null) return planDynamic(active);
    var manager = BuildcraftRecipeRegistry.integration();
    if (manager != null) {
      for (var recipe : manager.getRecipes().stream().sorted(Comparator.comparing(r -> r.id().toString())).toList()) {
        if (recipe.energy() < 0 || recipe.result().isEmpty() || !recipe.input().test(getItem(0))
          || recipe.expansions().size() > recipe.maxExpansionCount()) continue;
        int[] used = new int[9];
        if (!match(recipe.expansions(), 0, used)) continue;
        used[0] = 1;
        var next = MachineInventory.copy(this);
        if (!MachineInventory.insert(next, 9, 10, recipe.result())) continue;
        for (int slot = 0; slot < 9; slot++) next[slot].shrink(used[slot]);
        return new Plan(next, recipe.energy());
      }
    }
    return null;
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    super.tick(level, pos, state);
    if (level.isClientSide) return;
    Plan plan = plan();
    clientRequiredEnergy = plan == null ? 0 : plan.energy();
    if (plan == null || getEnergy() < plan.energy()) return;
    MachineInventory.commit(this, plan.items());
    subtractEnergy(plan.energy());
    setChanged();
  }
  public IntegrationTableBlockEntity(BlockPos pos, BlockState state) {
    super(SiliconBlockEntityTypes.INTEGRATION_TABLE.get(), pos, state);
  }

  @Override
  public int getRequiredEnergy() {
    Plan plan = plan();
    return plan == null ? 0 : plan.energy();
  }

  @Override
  public boolean canCraft() {
    return plan() != null;
  }

  @Override
  public int getContainerSize() {
    return 12; // 1.7.10 had 12 slots
  }

  @Override
  public boolean hasWork() {
    return canCraft();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornsilicon.integration_table");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
    return new IntegrationTableMenu(p_39954_, p_39955_, this);
  }
}
