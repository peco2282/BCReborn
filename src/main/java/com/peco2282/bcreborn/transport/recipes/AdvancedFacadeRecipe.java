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

import com.peco2282.bcreborn.api.facades.FacadeType;
import com.peco2282.bcreborn.api.transport.PipeWire;
import com.peco2282.bcreborn.core.recipes.IntegrationRecipeBC;
import com.peco2282.bcreborn.transport.TransportItems;
import com.peco2282.bcreborn.transport.item.FacadeItem;
import com.peco2282.bcreborn.transport.item.PipeWireItem;
import com.peco2282.bcreborn.transport.item.PlugItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Makes a phased facade: a facade plus a pipe wire and a second basic facade gives a facade that shows the second
 * block while that wire is active (original {@code AdvancedFacadeRecipe}). A pipe plug in place of the second
 * facade gives a transparent state: the facade disappears while the wire is active.
 */
public class AdvancedFacadeRecipe extends IntegrationRecipeBC {
  public AdvancedFacadeRecipe() {
    super(25000, 2);
  }

  private static boolean isBasicFacade(ItemStack stack) {
    return stack.getItem() instanceof FacadeItem && FacadeItem.getType(stack) == FacadeType.Basic;
  }

  @Override
  public List<ItemStack> generateExampleInput() {
    return List.of(new ItemStack(TransportItems.FACADE.get()));
  }

  @Override
  public List<ItemStack> generateExampleOutput() {
    return List.of(new ItemStack(TransportItems.FACADE.get()));
  }

  @Override
  public List<List<ItemStack>> generateExampleExpansions() {
    List<List<ItemStack>> list = new ArrayList<>();
    list.add(List.of(new ItemStack(TransportItems.FACADE.get()), new ItemStack(TransportItems.PIPE_PLUG.get())));
    List<ItemStack> pipeWires = new ArrayList<>();
    for (PipeWire wire : PipeWire.values()) {
      pipeWires.add(wire.getStack());
    }
    list.add(pipeWires);
    return list;
  }

  @Override
  public boolean isValidInput(ItemStack input) {
    return input.getItem() instanceof FacadeItem;
  }

  @Override
  public boolean isValidExpansion(ItemStack input, ItemStack expansion) {
    return isBasicFacade(expansion) || expansion.getItem() instanceof PlugItem || expansion.getItem() instanceof PipeWireItem;
  }

  @Override
  public @Nullable ItemStack craft(ItemStack input, List<ItemStack> expansions, boolean preview) {
    PipeWire wire = null;
    ItemStack facade = null;

    for (ItemStack stack : expansions) {
      if (wire == null && stack.getItem() instanceof PipeWireItem wireItem) {
        wire = wireItem.getWire();
        if (!preview) {
          stack.shrink(1);
        }
      } else if (facade == null && (isBasicFacade(stack) || stack.getItem() instanceof PlugItem)) {
        // Copy before shrinking: the states are read from this stack below.
        facade = stack.copy();
        if (!preview) {
          stack.shrink(1);
        }
      }
    }

    if (wire == null || facade == null) {
      return null;
    }

    FacadeItem.FacadeState[] states = FacadeItem.getStates(input);
    if (states.length == 0) {
      return null;
    }
    FacadeItem.FacadeState additionalState;
    if (facade.getItem() instanceof PlugItem) {
      // Original FacadeState.createTransparent(wire).
      additionalState = new FacadeItem.FacadeState(null, wire, false);
    } else {
      FacadeItem.FacadeState[] added = FacadeItem.getStates(facade);
      if (added.length == 0) {
        return null;
      }
      additionalState = new FacadeItem.FacadeState(added[0].state(), wire, added[0].hollow());
    }

    FacadeItem.FacadeState[] result = null;
    // If a state with the same wire already exists, override it.
    for (int i = 0; i < states.length; i++) {
      if (states[i].wire() == wire) {
        result = states.clone();
        result[i] = additionalState;
        break;
      }
    }
    if (result == null) {
      result = new FacadeItem.FacadeState[states.length + 1];
      System.arraycopy(states, 0, result, 0, states.length);
      result[states.length] = additionalState;
    }

    ItemStack output = new ItemStack(TransportItems.FACADE.get());
    TransportItems.FACADE.get().setFacadeStates(output, result);
    return output;
  }
}
