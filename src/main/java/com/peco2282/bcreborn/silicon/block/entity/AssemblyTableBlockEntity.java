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

import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import com.peco2282.bcreborn.silicon.menu.AssemblyTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import com.peco2282.bcreborn.api.recipes.AssemblyRecipe;
import com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class AssemblyTableBlockEntity extends LaserTableBaseBlockEntity {
  private int craftingTicks;
  private String recipeId = "";
  private LazyOptional<IItemHandler> itemCapability =
    LazyOptional.of(() -> new InvWrapper(this));

  @Override
  public <T> LazyOptional<T> getCapability(
    Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER) return itemCapability.cast();
    return super.getCapability(cap, side);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    itemCapability.invalidate();
  }

  @Override
  public void reviveCaps() {
    super.reviveCaps();
    itemCapability = LazyOptional.of(() -> new InvWrapper(this));
  }

  private AssemblyRecipe findRecipe() {
    var manager = BuildcraftRecipeRegistry.assembly();
    if (manager == null) return null;
    return manager.getRecipes().stream()
      .filter(r -> r.energy() >= 0 && !r.result().isEmpty() && consumption(r) != null)
      .sorted(Comparator.comparing(r -> r.id().toString())).findFirst().orElse(null);
  }

  private int[] consumption(AssemblyRecipe recipe) {
    int[] used = new int[getContainerSize()];
    return matchIngredients(recipe.ingredients(), 0, used) ? used : null;
  }

  private boolean matchIngredients(List<Ingredient> ingredients,
                                   int index, int[] used) {
    if (index == ingredients.size()) return true;
    for (int slot = 0; slot < used.length; slot++) {
      if (used[slot] < getItem(slot).getCount() && ingredients.get(index).test(getItem(slot))) {
        used[slot]++;
        if (matchIngredients(ingredients, index + 1, used)) return true;
        used[slot]--;
      }
    }
    return false;
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    super.tick(level, pos, state);
    if (level.isClientSide) return;
    var recipe = findRecipe();
    clientRequiredEnergy = recipe == null ? 0 : recipe.energy();
    if (recipe == null) {
      if (craftingTicks != 0 || !recipeId.isEmpty()) {
        craftingTicks = 0;
        recipeId = "";
        setChanged();
      }
      return;
    }
    if (!recipeId.equals(recipe.id().toString())) {
      recipeId = recipe.id().toString();
      craftingTicks = 0;
    }
    if (getEnergy() < recipe.energy()) return;
    if (++craftingTicks >= Math.max(1, recipe.craftingTime())) {
      int[] used = consumption(recipe);
      if (used == null) return;
      for (int slot = 0; slot < used.length; slot++) {
        if (used[slot] > 0) removeItem(slot, used[slot]);
      }
      subtractEnergy(recipe.energy());
      outputStack(recipe.result().copy(), true);
      craftingTicks = 0;
    }
    setChanged();
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    tag.putInt("craftingTicks", craftingTicks);
    tag.putString("recipeId", recipeId);
    tag.putInt("requiredEnergy", clientRequiredEnergy);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    craftingTicks = tag.getInt("craftingTicks");
    recipeId = tag.getString("recipeId");
    clientRequiredEnergy = tag.getInt("requiredEnergy");
  }
  public AssemblyTableBlockEntity(BlockPos pos, BlockState state) {
    super(SiliconBlockEntityTypes.ASSEMBLY_TABLE.get(), pos, state);
  }

  @Override
  public int getRequiredEnergy() {
    var recipe = findRecipe();
    return recipe == null ? 0 : recipe.energy();
  }

  @Override
  public boolean canCraft() {
    return findRecipe() != null;
  }

  @Override
  public int getContainerSize() {
    return 12; // 1.7.10 had 12 slots for assembly table
  }

  @Override
  public boolean hasWork() {
    return canCraft();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornsilicon.assembly_table");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
    return new AssemblyTableMenu(p_39954_, p_39955_, this);
  }
}
