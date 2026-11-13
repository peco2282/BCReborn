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
package com.peco2282.bcreborn.transport.recipes;

import com.peco2282.bcreborn.api.gates.GateExpansions;
import com.peco2282.bcreborn.api.gates.IGateExpansion;
import com.peco2282.bcreborn.core.recipes.IntegrationRecipeBC;
import com.peco2282.bcreborn.silicon.SiliconItems;
import com.peco2282.bcreborn.transport.gates.GateDefinition;
import com.peco2282.bcreborn.transport.gates.ItemGate;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Adds expansions to a gate, or swaps its logic with a Redstone Chipset (original {@code GateExpansionRecipe}).
 * As in the original, the Redstone Chipset used for the logic swap is not consumed.
 */
public class GateExpansionRecipe extends IntegrationRecipeBC {
  public GateExpansionRecipe() {
    super(25000);
  }

  private static Map<IGateExpansion, ItemStack> recipes() {
    return GateExpansions.getRecipesForPostInit();
  }

  private static boolean isLogicSwap(ItemStack stack) {
    return stack.is(SiliconItems.REDSTONE_CHIPSET.get());
  }

  @Override
  public boolean isValidInput(ItemStack input) {
    return input.getItem() instanceof ItemGate;
  }

  @Override
  public boolean isValidExpansion(ItemStack input, ItemStack expansion) {
    if (isLogicSwap(expansion)) {
      return true;
    }
    for (ItemStack s : recipes().values()) {
      if (ItemStack.isSameItem(s, expansion)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public List<ItemStack> generateExampleInput() {
    return Collections.unmodifiableList(ItemGate.getAllGates());
  }

  @Override
  public List<ItemStack> generateExampleOutput() {
    ArrayList<ItemStack> list = new ArrayList<>();
    ArrayList<IGateExpansion> exps = new ArrayList<>(recipes().keySet());
    int combinations = exps.size();
    for (int i = 0; i < (1 << combinations); i++) {
      for (GateDefinition.GateLogic l : GateDefinition.GateLogic.values()) {
        for (GateDefinition.GateMaterial m : GateDefinition.GateMaterial.values()) {
          ItemStack s = ItemGate.makeGateItem(m, l);
          for (int j = 0; j < combinations; j++) {
            if (((i >> j) & 1) != 0) {
              ItemGate.addGateExpansion(s, exps.get(j));
            }
          }
          list.add(s);
        }
      }
    }
    return list;
  }

  @Override
  public List<List<ItemStack>> generateExampleExpansions() {
    ArrayList<List<ItemStack>> list = new ArrayList<>();
    list.add(new ArrayList<>(recipes().values()));
    return list;
  }

  @Override
  public @Nullable ItemStack craft(ItemStack input, List<ItemStack> expansions, boolean preview) {
    ItemStack output = input.copyWithCount(1);
    int expansionsAdded = 0;

    for (ItemStack chipset : expansions) {
      if (isLogicSwap(chipset)) {
        ItemGate.setLogic(output, ItemGate.getLogic(output) == GateDefinition.GateLogic.AND
          ? GateDefinition.GateLogic.OR : GateDefinition.GateLogic.AND);
        expansionsAdded++;
        continue;
      }
      for (Map.Entry<IGateExpansion, ItemStack> entry : recipes().entrySet()) {
        if (ItemStack.isSameItem(chipset, entry.getValue()) && !ItemGate.hasGateExpansion(output, entry.getKey())) {
          if (!preview) {
            chipset.shrink(1);
          }
          ItemGate.addGateExpansion(output, entry.getKey());
          expansionsAdded++;
          break;
        }
      }
    }

    return expansionsAdded > 0 ? output : null;
  }
}
