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
package com.peco2282.bcreborn.energy.block.entity;

import com.peco2282.bcreborn.common.ResourceBuilder;
import com.peco2282.bcreborn.common.data.tag.CommonItemTags;
import com.peco2282.bcreborn.energy.EnergyBlockEntityTypes;
import com.peco2282.bcreborn.energy.menu.StoneEngineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraft.world.item.Items;
import net.minecraft.util.Mth;

public class StoneEngineBlockEntity extends EngineBlockEntityContainer<StoneEngineBlockEntity> {
  public static final float MAX_OUTPUT = 10;
  public static final float MIN_OUTPUT = MAX_OUTPUT / 3;
  public static final float TARGET_OUTPUT = .375f;
  private final float kp = 1f;
  private final float ki = 0.05f;
  private final double eLimit = (MAX_OUTPUT - MIN_OUTPUT) / ki;
  private int burnTime = 0;
  private int totalBurnTime = 0;
  private ItemStack burnItem;
  private double esum;

  public StoneEngineBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
    super(EnergyBlockEntityTypes.STONE_ENGINE.get(), p_155229_, p_155230_, 1);
    // 中容量・中出力
    configureEnergy(10000, 10);
  }

  @Override
  protected ResourceBuilder getEngineResource() {
    return ResourceBuilder.energy("stone_engine");
  }

  @Override
  public boolean isFuelable(ItemStack stack) {
    return stack.is(CommonItemTags.ENGINE_ENERGY) || ForgeHooks.getBurnTime(stack, null) > 0;
  }

  @Override
  public boolean isBurning() {
    return burnTime > 0;
  }

  @Override
  public void updateProgress() {
    // TileEngineStone only consumes the next item while redstone-powered.
    if (burnTime <= 0 && isRedstonePowered && !isOverheated()) {
      ItemStack stack = getItem(0);
      if (!stack.isEmpty() && isFuelable(stack)) {
        int time = ForgeHooks.getBurnTime(stack, null);
        if (time > 0) {
          totalBurnTime = burnTime = time;
          burnItem = stack.copy();
          // 1個消費
          stack.shrink(1);
          if (stack.isEmpty()) setItem(0, ItemStack.EMPTY);
          setActive(true);
        }
      }
    }
  }

  @Override
  public void overheat() {
    super.overheat();
    burnTime = 0;
  }

  @Override
  public void explode() {
    level.explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 3, Level.ExplosionInteraction.NONE);
    level.setBlock(getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
  }

  @Override
  public void burning() {
    if (burnTime > 0) {
      burnTime--;
      if (isRedstonePowered && this.energyStorage != null) {
        this.energyStorage.generateEnergy(getIdealOutput(), false);
      }
      setPumping(true); // 石エンジンは常にピストンを動かそうとする
    } else {
      burnItem = null;
      burnTime = 0;
      setActive(false);
      setPumping(false);
    }
  }

  private int getIdealOutput() {
    if (burnItem != null && burnItem.is(Items.PAPER)) return 1;
    double error = TARGET_OUTPUT * getMaxEnergyStored() - getEnergyStored();
    esum = Mth.clamp(esum + error, -eLimit, eLimit);
    return (int) Math.round(Mth.clamp(error * kp + esum * ki, MIN_OUTPUT, MAX_OUTPUT));
  }

  @Override
  protected int getCurrentOutputLimit() {
    return (int) Math.floor(getIdealOutput() * heat / IDEAL_HEAT);
  }

  @Override
  public void onActivated() {
    super.onActivated();
    getItem(0);
    burnItem = getItem(0).copy();
    if (burnItem.isEmpty()) {
      setItem(0, ItemStack.EMPTY);
    }
  }

  public int getLeftBurnTime(int pixel) {
    // Avoid divide-by-zero when GUI opens before any fuel has started burning
    if (totalBurnTime <= 0) {
      return 0;
    }
    int bt = Math.max(0, burnTime);
    return bt * pixel / totalBurnTime;
  }

  /* SAVING & LOADING */
  @Override
  public void load(CompoundTag data) {
    super.load(data);
    configureEnergy(10000, 10);
    burnTime = data.getInt("burnTime");
    totalBurnTime = data.getInt("totalBurnTime");
    esum = data.getDouble("outputIntegral");
  }

  @Override
  public void saveAdditional(CompoundTag data) {
    super.saveAdditional(data);
    data.putInt("burnTime", burnTime);
    data.putInt("totalBurnTime", totalBurnTime);
    data.putDouble("outputIntegral", esum);
  }

  @Override
  public StoneEngineMenu createMenu(int p_58627_, Inventory p_58628_) {
    return new StoneEngineMenu(p_58627_, p_58628_, this);
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("menu.bcrebornenergy.stone_engine");
  }
}
