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

import com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry;
import com.peco2282.bcreborn.api.recipes.IProgrammingRecipe;
import com.peco2282.bcreborn.silicon.menu.ProgrammingTableMenu;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ProgrammingTableBlockEntity extends LaserTableBaseBlockEntity {
  private String selectedRecipe = "";
  private ItemStack selectedOption = ItemStack.EMPTY;
  private record Choice(IProgrammingRecipe recipe, ItemStack option) {}
  private record Plan(ItemStack[] items, int energy) {}

  @Override protected boolean isMachineInput(int slot) { return slot == 0; }
  @Override protected boolean isMachineOutput(int slot) { return slot == 1; }

  private List<Choice> choices() {
    var choices = new ArrayList<Choice>();
    var manager = BuildcraftRecipeRegistry.programming();
    if (manager == null || getItem(0).isEmpty()) return choices;
    for (var recipe : manager.getRecipes().stream().sorted(Comparator.comparing(r -> r.getId().toString())).toList()) {
      if (!recipe.canCraft(getItem(0).copy())) continue;
      for (var option : recipe.getOptions(8, 4)) {
        if (!option.isEmpty()) choices.add(new Choice(recipe, option.copy()));
      }
    }
    return choices;
  }

  public void cycleOption(int direction) {
    var choices = choices();
    if (choices.isEmpty()) return;
    int index = -1;
    for (int i = 0; i < choices.size(); i++) {
      if (selectedRecipe.equals(choices.get(i).recipe().getId().toString())
        && ItemStack.isSameItemSameTags(selectedOption, choices.get(i).option())) { index = i; break; }
    }
    Choice choice = choices.get(index < 0 ? 0 : Math.floorMod(index + direction, choices.size()));
    selectedRecipe = choice.recipe().getId().toString();
    selectedOption = choice.option().copy();
    setItem(2, selectedOption.copy());
    setChanged();
  }

  private Plan plan() {
    for (Choice choice : choices()) {
      if (!selectedRecipe.equals(choice.recipe().getId().toString())
        || !ItemStack.isSameItemSameTags(selectedOption, choice.option())) continue;
      int cost = choice.recipe().getEnergyCost(choice.option().copy());
      var result = choice.recipe().craft(getItem(0).copyWithCount(1), choice.option().copy());
      if (cost < 0 || result.isEmpty()) return null;
      var next = MachineInventory.copy(this);
      if (!MachineInventory.insert(next, 1, 2, result)) return null;
      next[0].shrink(1);
      return new Plan(next, cost);
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

  @Override public void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    tag.putString("selectedRecipe", selectedRecipe);
    tag.put("selectedOption", selectedOption.save(new CompoundTag()));
  }

  @Override public void load(CompoundTag tag) {
    super.load(tag);
    selectedRecipe = tag.getString("selectedRecipe");
    selectedOption = ItemStack.of(tag.getCompound("selectedOption"));
    inv.setItem(2, selectedOption.copy());
  }
  public ProgrammingTableBlockEntity(BlockPos pos, BlockState state) {
    super(SiliconBlockEntityTypes.PROGRAMMING_TABLE.get(), pos, state);
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
    return 3; // Input, output, and a non-consumable programming preview.
  }

  @Override
  public boolean hasWork() {
    return canCraft();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornsilicon.programming_table");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
    return new ProgrammingTableMenu(p_39954_, p_39955_, this);
  }
}
