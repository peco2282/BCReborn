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
package com.peco2282.bcreborn.factory.block.entity;

import com.peco2282.bcreborn.api.power.IRedstoneEngine;
import com.peco2282.bcreborn.api.power.IRedstoneEngineReceiver;
import com.peco2282.bcreborn.api.tiles.IDebuggable;
import com.peco2282.bcreborn.api.tiles.IHasWork;
import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.BuildCraftBlockEntity;
import com.peco2282.bcreborn.common.inventory.InvUtils;
import com.peco2282.bcreborn.common.inventory.InventoryConcatenator;
import com.peco2282.bcreborn.common.item.EnergyStorage;
import com.peco2282.bcreborn.common.utils.BCFakePlayer;
import com.peco2282.bcreborn.common.utils.Utils;
import com.peco2282.bcreborn.factory.FactoryBlockEntityTypes;
import com.peco2282.bcreborn.factory.menu.AutoWorkbenchMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

public class AutoWorkbenchBlockEntity extends BuildCraftBlockEntity implements WorldlyContainer, IHasWork, IRedstoneEngineReceiver, IDebuggable, IEnergyStorage, MenuProvider {

  public static final int SLOT_RESULT = 9;
  public static final int CRAFT_TIME = 256;
  public static final int UPDATE_TIME = 16;
  private static final int[] SLOTS = Utils.createSlotArray(0, 10);
  public final LocalInventoryCrafting craftMatrix = new LocalInventoryCrafting();
  private final SimpleInventory resultInv = new SimpleInventory(1, "Auto Workbench", 64);
  private final SimpleInventory inputInv = new SimpleInventory(9, "Auto Workbench", 64) {
    @Override
    public void setItem(int i, ItemStack itemStack) {
      super.setItem(i, itemStack);
      if (craftMatrix.isInputMissing && !getItem(i).isEmpty()) {
        craftMatrix.isInputMissing = false;
      }
    }

    @Override
    public void setChanged() {
      super.setChanged();
      craftMatrix.isInputMissing = false;
    }
  };
  private final Container inv = InventoryConcatenator.make().add(inputInv).add(resultInv).add(craftMatrix);
  private final ResultContainer craftResult = new ResultContainer();
  private final int[] bindings = new int[9];
  private final int[] bindingCounts = new int[9];
  public int progress = 0;
  private int update = Utils.RANDOM.nextInt();

  private boolean hasWork = false;
  private boolean scheduledCacheRebuild = false;

  public AutoWorkbenchBlockEntity(BlockPos pos, BlockState state) {
    super(FactoryBlockEntityTypes.AUTO_WORKBENCH.get(), pos, state);
    this.setBattery(new EnergyStorage(16, 16, 0));
  }

  @Override
  public boolean hasWork() {
    return hasWork;
  }

  @Override
  public boolean canConnectRedstoneEngine(Direction side) {
    return true;
  }

  public boolean canConnectEnergy(Direction side) {
    BlockEntity tile = level.getBlockEntity(getBlockPos().relative(side));
    return tile instanceof IRedstoneEngine;
  }

  public ResultContainer getCraftResult() {
    return craftResult;
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornfactory.auto_workbench");
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
    return new AutoWorkbenchMenu(id, playerInv, this, new SimpleContainerData(1));
  }

  @Override
  public void getDebugInfo(List<String> info, Direction side, ItemStack debugger, Player player) {
    info.add("isInputMissing = " + craftMatrix.isInputMissing);
    info.add("isOutputJammed = " + craftMatrix.isOutputJammed);
  }

  public WeakReference<? extends Player> getInternalPlayer() {
    if (level instanceof ServerLevel serverLevel) {
      return BCFakePlayer.getBuildCraftPlayer(serverLevel);
    }
    return new WeakReference<>(null);
  }

  @Override
  public void setChanged() {
    super.setChanged();
    inv.setChanged();
  }

  @Override
  public int getContainerSize() {
    return 10;
  }

  @Override
  public boolean isEmpty() {
    return inv.isEmpty();
  }

  @Override
  public ItemStack getItem(int slot) {
    return inv.getItem(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int count) {
    return inv.removeItem(slot, count);
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return inv.removeItemNoUpdate(slot);
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    inv.setItem(slot, stack);
  }

  @Override
  public void load(CompoundTag data) {
    super.load(data);
    resultInv.readTag(data);
    if (data.contains("input")) {
      InvUtils.readInvFromNBT(inputInv, "input", data);
      InvUtils.readInvFromNBT(craftMatrix, "matrix", data);
    } else {
      InvUtils.readInvFromNBT(inputInv, "matrix", data);
      for (int i = 0; i < 9; i++) {
        ItemStack inputStack = inputInv.getItem(i);
        if (!inputStack.isEmpty()) {
          ItemStack matrixStack = inputStack.copy();
          matrixStack.setCount(1);
          craftMatrix.setItem(i, matrixStack);
        }
      }
    }

    craftMatrix.rebuildCache();
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);
    resultInv.writeTag(data);
    InvUtils.writeInvToNBT(inputInv, "input", data);
    InvUtils.writeInvToNBT(craftMatrix, "matrix", data);
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    return getBattery().receiveEnergy(maxReceive, simulate);
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    return getBattery().extractEnergy(maxExtract, simulate);
  }

  @Override
  public int getEnergyStored() {
    return getBattery().getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return getBattery().getMaxEnergyStored();
  }

  @Override
  public boolean canExtract() {
    return getBattery().canExtract();
  }

  @Override
  public boolean canReceive() {
    return getBattery().canReceive();
  }

  @Override
  protected void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
      return;
    }

    if (scheduledCacheRebuild) {
      craftMatrix.rebuildCache();
      scheduledCacheRebuild = false;
    }

    if (craftMatrix.isOutputJammed || craftMatrix.isInputMissing || craftMatrix.currentRecipe == null) {
      progress = 0;
      return;
    }

    if (!hasWork) {
      return;
    }

    int updateNext = update + getBattery().getEnergyStored() + 1;
    int updateThreshold = (update & ~15) + 16;
    update = Math.min(updateThreshold, updateNext);
    if ((update % UPDATE_TIME) == 0) {
      updateCrafting();
    }
    getBattery().setEnergy(0);
  }

  public int getProgressScaled(int i) {
    return (progress * i) / CRAFT_TIME;
  }

  /**
   * Increment craft job, find recipes, produce output
   */
  private void updateCrafting() {
    progress += UPDATE_TIME;

    for (int i = 0; i < 9; i++) {
      bindingCounts[i] = 0;
    }
    for (int i = 0; i < 9; i++) {
      ItemStack comparedStack = craftMatrix.getItem(i);
      if (comparedStack.isEmpty()) {
        bindings[i] = -1;
        continue;
      }

      if (bindings[i] == -1 || !ItemStack.isSameItemSameTags(inputInv.getItem(bindings[i]), comparedStack)) {
        boolean found = false;
        for (int j = 0; j < 9; j++) {
          if (j == bindings[i]) {
            continue;
          }

          ItemStack inputInvStack = inputInv.getItem(j);

          if (ItemStack.isSameItemSameTags(inputInvStack, comparedStack)
            && inputInvStack.getCount() > bindingCounts[j]) {
            found = true;
            bindings[i] = j;
            bindingCounts[j]++;
            break;
          }
        }
        if (!found) {
          craftMatrix.isInputMissing = true;
          progress = 0;
          return;
        }
      } else {
        bindingCounts[bindings[i]]++;
      }
    }

    for (int i = 0; i < 9; i++) {
      if (bindingCounts[i] > 0) {
        ItemStack stack = inputInv.getItem(i);
        if (!stack.isEmpty() && stack.getCount() < bindingCounts[i]) {
          // Do not break progress yet, instead give it a chance to rebuild
          // It will quit when trying to find a valid binding to "fit in"
          for (int j = 0; j < 9; j++) {
            if (bindings[j] == i) {
              bindings[j] = -1;
            }
          }
          return;
        }
      }
    }

    if (progress < CRAFT_TIME) {
      return;
    }

    progress = 0;

    craftMatrix.setUseBindings(true);
    ItemStack result = craftMatrix.getRecipeOutput();

    if (!result.isEmpty()) {
      ItemStack resultInto = resultInv.getItem(0);

      // In 1.20.1 we should probably use a better way to handle remaining items
      // For now, let's just consume the items
      for (int i = 0; i < 9; i++) {
        if (bindings[i] >= 0) {
          inputInv.removeItem(bindings[i], 1);
        }
      }

      if (resultInto.isEmpty()) {
        resultInv.setItem(0, result);
      } else {
        resultInto.grow(result.getCount());
      }
    }

    craftMatrix.setUseBindings(false);
    craftMatrix.rebuildCache();
  }

  @Override
  public void clearContent() {
    inv.clearContent();
  }

  @Override
  public int[] getSlotsForFace(Direction side) {
    return SLOTS;
  }

  @Override
  public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
    if (slot >= 9) {
      return false;
    }
    ItemStack slotStack = inv.getItem(slot);
    if (ItemStack.isSameItemSameTags(stack, slotStack)) {
      return true;
    }
    for (int i = 0; i < 9; i++) {
      ItemStack inputStack = craftMatrix.getItem(i);
      if (!inputStack.isEmpty() && ItemStack.isSameItemSameTags(inputStack, stack)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
    return slot == SLOT_RESULT;
  }

  public class LocalInventoryCrafting implements CraftingContainer, StackedContentsCompatible {
    public CraftingRecipe currentRecipe;
    public boolean useBindings, isOutputJammed, isInputMissing;

    @Override
    public int getWidth() {
      return 3;
    }

    @Override
    public int getHeight() {
      return 3;
    }

    @Override
    public List<ItemStack> getItems() {
      NonNullList<ItemStack> list = NonNullList.withSize(9, ItemStack.EMPTY);
      for (int i = 0; i < 9; i++) {
        list.set(i, getItem(i));
      }
      return list;
    }

    @Override
    public int getContainerSize() {
      return 9;
    }

    @Override
    public boolean isEmpty() {
      for (int i = 0; i < 9; i++) {
        if (!getItem(i).isEmpty()) return false;
      }
      return true;
    }

    @Override
    public ItemStack getItem(int slot) {
      if (useBindings) {
        if (slot >= 0 && slot < 9 && bindings[slot] >= 0) {
          return inputInv.getItem(bindings[slot]);
        } else {
          return ItemStack.EMPTY;
        }
      } else {
        // We don't have a backing inventory for the matrix, it's just a phantom inventory
        // In 1.7.10 it was extending InventoryCrafting which had a field.
        // Let's use a SimpleInventory internally or just mirror the 1.7.10 behavior.
        // For now let's assume it was using DummyMenu which is empty.
        return ItemStack.EMPTY;
      }
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_) {
      return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
      return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
      scheduledCacheRebuild = true;
    }

    @Override
    public boolean stillValid(Player p_18946_) {
      return true;
    }

    @Override
    public void clearContent() {
    }

    @Override
    public void fillStackedContents(StackedContents p_39386_) {
      for (int i = 0; i < 9; i++) {
        p_39386_.accountSimpleStack(getItem(i));
      }
    }

    public ItemStack getRecipeOutput() {
      currentRecipe = findRecipe();
      if (currentRecipe == null) {
        return ItemStack.EMPTY;
      }
      return currentRecipe.assemble(this, level.registryAccess());
    }

    private CraftingRecipe findRecipe() {
      if (level == null) return null;
      return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, this, level).orElse(null);
    }

    public void rebuildCache() {
      currentRecipe = findRecipe();
      hasWork = currentRecipe != null;

      ItemStack result = getRecipeOutput();
      ItemStack resultInto = resultInv.getItem(0);

      isOutputJammed = !resultInto.isEmpty() && (
        !ItemStack.isSameItemSameTags(resultInto, result)
          || resultInto.getCount() + result.getCount() > resultInto.getMaxStackSize());
    }

    @Override
    public void setChanged() {
      scheduledCacheRebuild = true;
    }

    public void setUseBindings(boolean use) {
      useBindings = use;
    }
  }
}
