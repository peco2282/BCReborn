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
package com.peco2282.bcreborn.transport.pipe.extraction;

import com.peco2282.bcreborn.common.SimpleInventory;
import com.peco2282.bcreborn.common.block.entity.EngineBlockEntity;
import com.peco2282.bcreborn.transport.block.entity.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

/**
 * エメラルドパイプ用のフィルター付きアイテム抽出モジュール。
 * フィルターに設定されたアイテムのみを抽出する。ラウンドロビン方式でフィルタースロットを巡回する。
 */
public class FilteredItemExtractionModule implements ExtractionModule {

  public static final FilteredItemExtractionModule INSTANCE = new FilteredItemExtractionModule();

  private FilteredItemExtractionModule() {
  }

  @Override
  public void extract(PipeBlockEntity pipe) {
    int energyAvailable = pipe.getExtractionEnergy();
    if (energyAvailable <= 0) return;

    int extractAmount = 1;
    int cooldown = 10;

    for (Direction dir : Direction.values()) {
      BlockEntity be = pipe.getLevel().getBlockEntity(pipe.getBlockPos().relative(dir));
      if (be instanceof EngineBlockEntity<?> engine && engine.orientation == dir.getOpposite() && engine.isActive()) {
        EngineBlockEntity.EnergyStage stage = engine.getEnergyStage();
        switch (stage) {
          case BLUE -> cooldown = 20;
          case GREEN -> {
          }
          case YELLOW -> extractAmount = 8;
          case RED -> {
            extractAmount = 64;
            cooldown = 5;
          }
        }
        break;
      }
    }

    if (pipe.getTicksSincePull() < cooldown) return;

    Direction extractDir = pipe.getExtractionSide();
    BlockPos inventoryPos = pipe.getBlockPos().relative(extractDir);
    BlockEntity invBe = pipe.getLevel().getBlockEntity(inventoryPos);

    if (invBe != null && !(invBe instanceof PipeBlockEntity)) {
      LazyOptional<IItemHandler> cap = invBe.getCapability(ForgeCapabilities.ITEM_HANDLER, extractDir.getOpposite());
      if (cap.isPresent()) {
        IItemHandler handler = cap.orElseThrow(IllegalStateException::new);
        SimpleInventory filter = pipe.getFilter(extractDir);
        int filterSize = filter.getContainerSize();
        PipeBlockEntity.ExtractFilterMode mode = pipe.getExtractFilterMode();

        for (int i = 0; i < handler.getSlots(); i++) {
          ItemStack stack = handler.getStackInSlot(i);
          if (stack.isEmpty()) continue;

          boolean matches = false;
          if (mode == PipeBlockEntity.ExtractFilterMode.ROUND_ROBIN) {
            // ラウンドロビン: フィルタースロットを順に確認し、一致するものがあれば抽出
            for (int attempt = 0; attempt < filterSize; attempt++) {
              int nextIdx = (pipe.getFilterSlotIndex() + 1) % filterSize;
              pipe.setFilterSlotIndex(nextIdx);
              ItemStack filterStack = filter.getItem(nextIdx);
              if (!filterStack.isEmpty() && ItemStack.isSameItemSameTags(stack, filterStack)) {
                matches = true;
                break;
              }
            }
          } else {
            // ホワイトリスト / ブラックリスト: フィルター全体を確認
            boolean inFilter = false;
            for (int slot = 0; slot < filterSize; slot++) {
              ItemStack filterStack = filter.getItem(slot);
              if (!filterStack.isEmpty() && ItemStack.isSameItemSameTags(stack, filterStack)) {
                inFilter = true;
                break;
              }
            }
            matches = (mode == PipeBlockEntity.ExtractFilterMode.WHITE_LIST) == inFilter;
          }

          if (matches) {
            ItemStack extracted = handler.extractItem(i, extractAmount, false);
            if (!extracted.isEmpty()) {
              pipe.injectItem(extracted, extractDir);
              pipe.consumeExtractionEnergy(10);
              pipe.resetTicksSincePull();
              return;
            }
          }
        }
      }
    }
  }
}
