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
package com.peco2282.bcreborn.common.blueprint;


import com.peco2282.bcreborn.common.builder.AbstractBuilderBlockEntity;
import com.peco2282.bcreborn.common.builder.BuilderItemMetaPair;
import com.peco2282.bcreborn.common.builder.BuildingSlotBlock;
import com.peco2282.bcreborn.energy.fluids.Tank;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

import java.util.*;

public class BuildingSlotMapIterator {
  public static int MAX_PER_ITEM = 512;
  private final BptBuilderBlueprint builderBlueprint;
  private final Map<BuilderItemMetaPair, List<BuildingSlotBlock>> slotMap;
  private final Set<BuilderItemMetaPair> availablePairs = new HashSet<>();
  private final boolean isCreative;
  private Iterator<BuilderItemMetaPair> keyIterator;
  private BuilderItemMetaPair currentKey;
  private List<BuildingSlotBlock> slots;
  private int slotPos, slotFound;

  public BuildingSlotMapIterator(BptBuilderBlueprint builderBlueprint, AbstractBuilderBlockEntity builder) {
    this.builderBlueprint = builderBlueprint;
    this.slotMap = builderBlueprint.buildList;
    int[] buildStageOccurences = builderBlueprint.buildStageOccurences;
    this.isCreative = builder.getLevel().getServer().getDefaultGameType() == GameType.CREATIVE;

    reset();
  }

  public void refresh(AbstractBuilderBlockEntity builder) {
    if (!isCreative) {
      availablePairs.clear();
      availablePairs.add(new BuilderItemMetaPair(null));

      if (builder != null) {
        for (ItemStack stack : builder.getInventoryList()) {
          if (stack != null && !stack.isEmpty()) {
            availablePairs.add(new BuilderItemMetaPair(stack));
          }
        }
        for (Tank t : builder.getFluidTanks()) {
          if (!t.getFluid().isEmpty() && t.getFluid().getFluid() != null) {
            availablePairs.add(new BuilderItemMetaPair(new ItemStack(Items.BUCKET)));
          }
        }
      }
    }
  }

  public void skipKey() {
    findNextKey();
  }

  private void findNextKey() {
    slotPos = -1;
    slotFound = 0;
    slots = null;
    while (keyIterator.hasNext()) {
      currentKey = keyIterator.next();
      if (isCreative || availablePairs.contains(currentKey)) {
        slots = slotMap.get(currentKey);
        slotPos = currentKey.position - 1;
        return;
      }
    }
    this.currentKey = null;
    this.keyIterator = slotMap.keySet().iterator();
  }

  public BuildingSlotBlock next() {
    if (slots == null) {
      findNextKey();
    }
    while (slots != null) {
      slotPos++;
      while (slotFound < MAX_PER_ITEM && slotPos < slots.size()) {
        BuildingSlotBlock b = slots.get(slotPos);
        if (b != null) {
          slotFound++;
          currentKey.position = slotPos + 1;
          return b;
        }
        slotPos++;
      }
      if (slotFound >= MAX_PER_ITEM) {
        currentKey.position = slotPos;
      } else {
        currentKey.position = 0;
      }
      findNextKey();
    }
    return null;
  }

  public void remove() {
    BuildingSlotBlock slot = slots.get(slotPos);
    slots.set(slotPos, null);

    builderBlueprint.onRemoveBuildingSlotBlock(slot);
  }

  public void reset() {
    this.keyIterator = slotMap.keySet().iterator();
    this.currentKey = null;
    this.slots = null;
    findNextKey();
  }
}
