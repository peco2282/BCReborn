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

import com.peco2282.bcreborn.api.recipes.AssemblyRecipe;
import com.peco2282.bcreborn.api.recipes.BuildcraftRecipeRegistry;
import com.peco2282.bcreborn.api.recipes.IAssemblyRecipeManager;
import com.peco2282.bcreborn.silicon.SiliconBlockEntityTypes;
import com.peco2282.bcreborn.silicon.menu.AssemblyTableMenu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * Port of the original {@code buildcraft.silicon.TileAssemblyTable}.
 * <p>
 * The player plans recipes in the GUI ({@link #planOutput} / {@link #cancelPlanOutput}). Only planned recipes are
 * crafted; the table works on one at a time ({@link #getCurrentRecipe()}), crafts it as soon as enough laser energy
 * is stored, then moves on to the next planned recipe that can be crafted ({@link #setNextCurrentRecipe()}).
 * <p>
 * Assembly recipes only exist on the server (datapack reload), so the server also sends the list the GUI shows
 * ({@link #getDisplayedRecipes()}).
 */
public class AssemblyTableBlockEntity extends LaserTableBaseBlockEntity {
  /** Original GUI: 3 x 4 recipe slots. */
  public static final int DISPLAY_SLOTS = 12;

  private final Set<ResourceLocation> plannedOutput = new LinkedHashSet<>();
  @Nullable
  private ResourceLocation currentRecipeId;
  /** Client copy of the GUI list (filled from the update tag). */
  private List<DisplayedRecipe> clientDisplay = Collections.emptyList();

  private LazyOptional<IItemHandler> itemCapability =
    LazyOptional.of(() -> new InvWrapper(this));

  /**
   * One recipe slot of the GUI: craftable recipes first, then planned ones that cannot be crafted right now
   * (original {@code GuiAssemblyTable.updateRecipes()}).
   */
  public record DisplayedRecipe(ResourceLocation id, ItemStack result, boolean craftable) {
  }

  public AssemblyTableBlockEntity(BlockPos pos, BlockState state) {
    super(SiliconBlockEntityTypes.ASSEMBLY_TABLE.get(), pos, state);
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
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

  // -----------------------------------------------------------------------
  // Recipes
  // -----------------------------------------------------------------------

  @Nullable
  private static AssemblyRecipe recipe(@Nullable ResourceLocation id) {
    IAssemblyRecipeManager manager = BuildcraftRecipeRegistry.assembly();
    return manager == null || id == null ? null : manager.getRecipe(id);
  }

  private static List<AssemblyRecipe> allRecipes() {
    IAssemblyRecipeManager manager = BuildcraftRecipeRegistry.assembly();
    if (manager == null) return Collections.emptyList();
    List<AssemblyRecipe> list = new ArrayList<>(manager.getRecipes());
    list.sort(Comparator.comparing(r -> r.id().toString()));
    return list;
  }

  /** Original {@code IFlexibleRecipe.canBeCrafted()}: every ingredient can be taken from a separate item. */
  public boolean canBeCrafted(@Nullable AssemblyRecipe recipe) {
    return recipe != null && recipe.energy() >= 0 && !recipe.result().isEmpty() && consumption(recipe) != null;
  }

  @Nullable
  private int[] consumption(AssemblyRecipe recipe) {
    int[] used = new int[getContainerSize()];
    return matchIngredients(recipe.ingredients(), 0, used) ? used : null;
  }

  private boolean matchIngredients(List<Ingredient> ingredients, int index, int[] used) {
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

  /** Original {@code getPotentialOutputs()}: every recipe that can be crafted from the current contents. */
  public List<AssemblyRecipe> getPotentialOutputs() {
    List<AssemblyRecipe> result = new ArrayList<>();
    for (AssemblyRecipe recipe : allRecipes()) {
      if (canBeCrafted(recipe)) result.add(recipe);
    }
    return result;
  }

  @Nullable
  public AssemblyRecipe getCurrentRecipe() {
    return recipe(currentRecipeId);
  }

  public boolean isPlanned(ResourceLocation id) {
    return plannedOutput.contains(id);
  }

  public boolean isAssembling(ResourceLocation id) {
    return id.equals(currentRecipeId);
  }

  private void setCurrentRecipe(@Nullable AssemblyRecipe recipe) {
    ResourceLocation id = recipe == null ? null : recipe.id();
    if (!java.util.Objects.equals(id, currentRecipeId)) {
      currentRecipeId = id;
      setChanged();
    }
  }

  /** Original {@code planOutput()}. */
  public void planOutput(ResourceLocation id) {
    AssemblyRecipe recipe = recipe(id);
    if (recipe == null || isPlanned(id)) return;
    plannedOutput.add(id);
    AssemblyRecipe current = getCurrentRecipe();
    if (current == null || !isPlanned(current.id())) {
      setCurrentRecipe(recipe);
    }
    setChanged();
  }

  /** Original {@code cancelPlanOutput()}. */
  public void cancelPlanOutput(ResourceLocation id) {
    if (isAssembling(id)) {
      setCurrentRecipe(null);
    }
    plannedOutput.remove(id);
    if (!plannedOutput.isEmpty()) {
      setCurrentRecipe(recipe(plannedOutput.iterator().next()));
    }
    setChanged();
  }

  /** Original {@code setNextCurrentRecipe()}: the next craftable planned recipe after the current one, wrapping. */
  public void setNextCurrentRecipe() {
    boolean takeNext = false;
    for (ResourceLocation id : plannedOutput) {
      AssemblyRecipe recipe = recipe(id);
      if (recipe == null) continue;
      if (id.equals(currentRecipeId)) {
        takeNext = true;
      } else if (takeNext && canBeCrafted(recipe)) {
        setCurrentRecipe(recipe);
        return;
      }
    }
    for (ResourceLocation id : plannedOutput) {
      AssemblyRecipe recipe = recipe(id);
      if (recipe != null && canBeCrafted(recipe)) {
        setCurrentRecipe(recipe);
        return;
      }
    }
    setCurrentRecipe(null);
  }

  // -----------------------------------------------------------------------
  // Tick
  // -----------------------------------------------------------------------

  /**
   * Original {@code updateEntity()}: crafts the current recipe as soon as its energy is stored. (The previous port
   * crafted the alphabetically first craftable recipe without any planning, plus an extra crafting delay.)
   */
  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    super.tick(level, pos, state);
    if (level.isClientSide) return;

    AssemblyRecipe current = getCurrentRecipe();
    if (current == null) {
      clientRequiredEnergy = 0;
      return;
    }
    if (!canBeCrafted(current)) {
      setNextCurrentRecipe();
      current = getCurrentRecipe();
      if (current == null) {
        clientRequiredEnergy = 0;
        return;
      }
    }
    clientRequiredEnergy = current.energy();

    if (getEnergy() >= current.energy()) {
      int[] used = consumption(current);
      if (used == null) return;
      for (int slot = 0; slot < used.length; slot++) {
        if (used[slot] > 0) removeItem(slot, used[slot]);
      }
      setEnergy(Math.max(0, getEnergy() - current.energy()));
      outputStack(current.result().copy(), true);
      setNextCurrentRecipe();
      setChanged();
    }
  }

  /** Original: a newly inserted item may make a planned recipe craftable. */
  @Override
  public void setItem(int slot, ItemStack stack) {
    super.setItem(slot, stack);
    if (level != null && !level.isClientSide && currentRecipeId == null) {
      setNextCurrentRecipe();
    }
  }

  // -----------------------------------------------------------------------
  // GUI list
  // -----------------------------------------------------------------------

  /**
   * The recipes shown in the 12 recipe slots. On the server this is computed from the inventory and the plan; on
   * the client it is the copy sent in the update tag.
   */
  public List<DisplayedRecipe> getDisplayedRecipes() {
    if (level != null && level.isClientSide) {
      return clientDisplay;
    }
    List<DisplayedRecipe> list = new ArrayList<>();
    Set<ResourceLocation> added = new HashSet<>();
    for (AssemblyRecipe recipe : getPotentialOutputs()) {
      if (list.size() >= DISPLAY_SLOTS) break;
      list.add(new DisplayedRecipe(recipe.id(), recipe.result().copy(), true));
      added.add(recipe.id());
    }
    for (ResourceLocation id : plannedOutput) {
      if (list.size() >= DISPLAY_SLOTS) break;
      AssemblyRecipe recipe = recipe(id);
      if (recipe != null && added.add(id)) {
        list.add(new DisplayedRecipe(id, recipe.result().copy(), false));
      }
    }
    return list;
  }

  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag tag = super.getUpdateTag();
    ListTag display = new ListTag();
    for (DisplayedRecipe entry : getDisplayedRecipes()) {
      CompoundTag e = new CompoundTag();
      e.putString("id", entry.id().toString());
      e.put("result", entry.result().save(new CompoundTag()));
      e.putBoolean("craftable", entry.craftable());
      display.add(e);
    }
    tag.put("display", display);
    return tag;
  }

  // -----------------------------------------------------------------------
  // NBT
  // -----------------------------------------------------------------------

  @Override
  public void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    ListTag planned = new ListTag();
    for (ResourceLocation id : plannedOutput) {
      planned.add(StringTag.valueOf(id.toString()));
    }
    tag.put("plannedIds", planned);
    if (currentRecipeId != null) {
      tag.putString("recipeId", currentRecipeId.toString());
    }
    tag.putInt("requiredEnergy", clientRequiredEnergy);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    plannedOutput.clear();
    ListTag planned = tag.getList("plannedIds", Tag.TAG_STRING);
    for (int i = 0; i < planned.size(); i++) {
      ResourceLocation id = ResourceLocation.tryParse(planned.getString(i));
      if (id != null) plannedOutput.add(id);
    }
    currentRecipeId = tag.contains("recipeId", Tag.TAG_STRING) ? ResourceLocation.tryParse(tag.getString("recipeId")) : null;
    clientRequiredEnergy = tag.getInt("requiredEnergy");
    if (tag.contains("display", Tag.TAG_LIST)) {
      List<DisplayedRecipe> list = new ArrayList<>();
      ListTag display = tag.getList("display", Tag.TAG_COMPOUND);
      for (int i = 0; i < display.size(); i++) {
        CompoundTag e = display.getCompound(i);
        ResourceLocation id = ResourceLocation.tryParse(e.getString("id"));
        if (id != null) list.add(new DisplayedRecipe(id, ItemStack.of(e.getCompound("result")), e.getBoolean("craftable")));
      }
      clientDisplay = list;
    }
  }

  @Override
  public int getRequiredEnergy() {
    AssemblyRecipe current = getCurrentRecipe();
    return current == null ? 0 : current.energy();
  }

  /** Original {@code canCraft()} / {@code hasWork()}: there is a current (planned) recipe. */
  @Override
  public boolean canCraft() {
    return currentRecipeId != null;
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
